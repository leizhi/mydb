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

/**
 * Copyright (C) 2000 CoolServlets.com. All rights reserved.
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
 *        CoolServlets.com (http://www.coolservlets.com)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Jive" and "CoolServlets.com" must not be used to
 *    endorse or promote products derived from this software without
 *    prior written permission. For written permission, please
 *    contact webmaster@coolservlets.com.
 *
 * 5. Products derived from this software may not be called "Jive",
 *    nor may "Jive" appear in their name, without prior written
 *    permission of CoolServlets.com.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL COOLSERVLETS.COM OR
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
 * individuals on behalf of CoolServlets.com. For more information
 * on CoolServlets.com, please see <http://www.coolservlets.com>.
 */

package com.mooo.mycoz.db.pool;

import java.sql.Connection;
import java.util.Enumeration;

/**
 * Abstract class that defines the connection provider framework. Other classes
 * extend this abstract class to make connection to actual data sources.
 */
public abstract class DbConnectionProvider {

    /** Dummy values. Override in subclasses. **/
    private static final String NAME = "";
    private static final String DESCRIPTION = "";
    private static final String AUTHOR = "";
    private static final int MAJOR_VERSION = 0;
    private static final int MINOR_VERSION = 0;
    private static final boolean POOLED = false;

    /**
     * Returns the name of the connection provider.
     *
     * @return the name of the connection provider.
     */
    public String getName() {
        return NAME;
    }

    /**
     * Returns a description of the connection provider.
     *
     * @return the description of the connection provider.
     */
    public String getDescription() {
        return DESCRIPTION;
    }

    /**
     * Returns the author of the connection provider.
     *
     * @return the name of the author of this connection provider.
     */
    public String getAuthor() {
        return AUTHOR;
    }

    /**
     * Returns the major version of the connection provider, i.e. 1.x.
     *
     * @return the major version number of the provider.
     */
    public int getMajorVersion() {
        return MAJOR_VERSION;
    }

    /**
     * Returns the minor version of the connection provider, i.e. x.1.
     *
     * @return the minor version number of the provider.
     */
    public int getMinorVersion() {
        return MINOR_VERSION;
    }

    /**
     * Returns true if this connection provider provides connections out
     * of a connection pool. Implementing and using connection providers that
     * are pooled is strongly recommended, as they greatly increase the speed
     * of Yazd.
     *
     * @return true if the Connection objects returned by this provider are
     *      pooled.
     */
    public boolean isPooled() {
        return POOLED;
    }

    /**
     * Returns a database connection. When a Yazd component is done with a
     * connection, it will call the close method of that connection. Therefore,
     * connection pools with special release methods are not directly
     * supported by the connection provider infrastructure. Instead, connections
     * from those pools should be wrapped such that calling the close method
     * on the wrapper class will release the connection from the pool.
     *
     * @return a Connection object.
     */
    public abstract Connection getConnection();

    /**
     * Starts the connection provider. For some connection providers, this
     * will be a no-op. However, connection provider users should always call
     * this method to make sure the connection provider is started.
     */
    protected abstract void start();

    /**
     * This method should be called whenever properties have been changed so
     * that the changes will take effect.
     */
    protected abstract void restart();

    /**
     * Tells the connection provider to destroy itself. For many connection
     * providers, this will essentially result in a no-op. However,
     * connection provider users should always call this method when changing
     * from one connection provider to another to ensure that there are no
     * dangling database connections.
     */
    protected abstract void destroy();

    /**
     * Returns the value of a property of the connection provider.
     *
     * @param name the name of the property.
     * @return the value of the property.
     */
    public abstract String getProperty(String name);

    /**
     * Returns the description of a property of the connection provider.
     *
     * @param name the name of the property.
     * @return the description of the property.
     */
    public abstract String getPropertyDescription(String name);

    /**
     * Returns an enumeration of the property names for the connection provider.
     *
     * @return an Enumeration of the property names.
     */
    public abstract Enumeration<?> propertyNames();

    /**
     * Sets a property of the connection provider. Each provider has a set number
     * of properties that are determined by the author. Trying to set a non-
     * existant property will result in an IllegalArgumentException.
     *
     * @param name the name of the property to set.
     * @param value the new value for the property.
     */
    public abstract void setProperty(String name, String value);

}
