/**
 * Copyright (C) 2001 Yasna.com. All rights reserved.
 *
 * ===================================================================
 * The Apache Software License, Version 1.1
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by
 *        Yasna.com (http://www.yasna.com)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Yazd" and "Yasna.com" must not be used to
 *    endorse or promote products derived from this software without
 *    prior written permission. For written permission, please
 *    contact yazd@yasna.com.
 *
 * 5. Products derived from this software may not be called "Yazd",
 *    nor may "Yazd" appear in their name, without prior written
 *    permission of Yasna.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL YASNA.COM OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of Yasna.com. For more information
 * on Yasna.com, please see <http://www.yasna.com>.
 */


package com.mooo.mycoz.db.pool;
import java.sql.*;

import javax.sql.DataSource;
import javax.naming.InitialContext;

import com.mooo.mycoz.db.DbConfig;

/**
 * Central manager of database connections. All methods are static so that they
 * can be easily accessed throughout the classes in the database package.
 */
public class DbConnectionManager {

    private static DbConnectionProvider connectionProvider;
    private static Object providerLock = new Object();
    private static boolean AppServerPooler = false;
    private static boolean checkedPooler = false;
    private static DataSource appServerSource;
    public static final String CONTEXT_JDBC_NAME=DbConfig.getProperty("JNDI.dataprovider");

    /**
     * Returns a database connection from the currently active connection
     * provider.
     */
    public synchronized static Connection getConnection() {
        Connection con=null;
        if (appServerSource == null && !checkedPooler){
            checkedPooler=true;
            try{
                InitialContext ctxt = new InitialContext ();
                appServerSource = (DataSource)ctxt.lookup(CONTEXT_JDBC_NAME);
                AppServerPooler=true;
                System.err.println("Yazd got a connection provider from app server ("+CONTEXT_JDBC_NAME+")");
            }catch(Exception e){
                System.err.println("Failed to find an application datasource ("+CONTEXT_JDBC_NAME+"): "+e.getMessage());
            }
        }
        if (connectionProvider == null && !AppServerPooler) {
            synchronized (providerLock) {
                if (connectionProvider == null) {
                    //Attempt to load the connection provider classname as
                    //a Yazd property.
                    String className =
                        DbConfig.getProperty("connectionProvider.className");
                    if (className != null) {
                        //Attempt to load the class.
                        try {
                            Class<?> conClass = Class.forName(className);
                            connectionProvider = (DbConnectionProvider)conClass.newInstance();
                        }
                        catch(Exception e) {
                            System.err.println("Warning: failed to create the " +
                                "connection provider specified by connection" +
                                "Provider.className. Using the default pool.");
                            connectionProvider = new DbConnectionDefaultPool();
                        }
                    }
                    else {
                        connectionProvider = new DbConnectionDefaultPool();
                    }
                    connectionProvider.start();
                }
            }
        }
        if(AppServerPooler){
            try{
                con = appServerSource.getConnection ();
            }catch(Exception e){
                System.err.println("There was a problem obtaining a connection from application server :"+e.getMessage());
            }
        }else{
            con = connectionProvider.getConnection();
        }
        if (con == null) {
            System.err.println("WARNING: DbConnectionManager.getConnection() " +
                "failed to obtain a connection.");
        }
        return con;
    }

    /**
     * Returns the current connection provider. The only case in which this
     * method should be called is if more information about the current
     * connection provider is needed. Database connections should always be
     * obtained by calling the getConnection method of this class.
     */
    public static DbConnectionProvider getDbConnectionProvider() {
        return connectionProvider;
    }

    /**
     * Sets the connection provider. The old provider (if it exists) is shut
     * down before the new one is started. A connection provider <b>should
     * not</b> be started before being passed to the connection manager
     * because the manager will call the start() method automatically.
     */
    public static void setDbConnectionProvider(DbConnectionProvider provider) {
        synchronized (providerLock) {
            if (connectionProvider != null) {
                connectionProvider.destroy();
                connectionProvider = null;
            }
            connectionProvider = provider;
            provider.start();
        }
    }


}
