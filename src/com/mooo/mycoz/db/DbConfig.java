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

package com.mooo.mycoz.db;

import javax.naming.InitialContext;

import com.mooo.mycoz.db.pool.DbConnectionManager;

import java.util.*;
import java.io.*;

/**
 * Manages properties for the entire Yazd system. Properties are merely
 * pieces of information that need to be saved in between server restarts. The
 * class also reports the version of Yazd.
 * <p>
 * At the moment, properties are stored in a Java Properties file. In a version
 * of Yazd coming soon, the properties file format will move to XML. XML
 * properties will allow hierarchical property structures which may mean the
 * API of this class will have to change.
 * <p>
 * Yazd properties are only meant to be set and retrevied by core Yazd classes.
 * Therefore, skin writers should probably ignore this class.
 * <p>
 * This class is implemented as a singleton since many classloaders seem to
 * take issue with doing classpath resource loading from a static context.
 */
public class DbConfig {

    /**
     * The Major version number of Yazd. i.e. 1.x.x
     */
    private static final int MAJOR_VERSION = 3;

    /**
     * The Minor version number of Yazd. i.e. x.1.x.
     */
    private static final int MINOR_VERSION = 0;

    /**
     * The revision version number of Yazd. i.e. x.x.1.
     */
    private static final int REVISION_VERSION = 1;

    private static DbConfig manager = null;
    private static Object managerLock = new Object();
    private static String propsName = "/mypool.xml";

    /**
     * Returns a Yazd property.
     *
     * @param name the name of the property to return.
     * @return the property value specified by name.
     */
    public static String getProperty(String name) {
        if (manager == null) {
            synchronized(managerLock) {
                if (manager == null) {
                    manager = new DbConfig(propsName);
                }
            }
        }
        return manager.getProp(name);
    }
    public static void setPath(String path){
        if (manager == null) {
            synchronized(managerLock) {
                if (manager == null) {
                    manager = new DbConfig(propsName);
                }
            }
        }
        manager.setProp("path",path);
    }
    /**
     * Sets a Yazd property. If the property doesn't already exists, a new
     * one will be created.
     *
     * @param name the name of the property being set.
     * @param value the value of the property being set.
     */
    public static void setProperty(String name, String value) {
        if (manager == null) {
            synchronized(managerLock) {
                if (manager == null) {
                    manager = new DbConfig(propsName);
                }
            }
        }
        manager.setProp(name, value);
    }

    /**
     * Deletes a Yazd property. If the property doesn't exist, the method
     * does nothing.
     *
     * @param name the name of the property to delete.
     */
    public static void deleteProperty(String name) {
        if (manager == null) {
            synchronized(managerLock) {
                if (manager == null) {
                    manager = new DbConfig(propsName);
                }
            }
        }
        manager.deleteProp(name);
    }

    /**
     * Returns the names of the Yazd properties.
     *
     * @return an Enumeration of the Yazd property names.
     */
    public static Enumeration<?> propertyNames() {
        if (manager == null) {
            synchronized(managerLock) {
                if (manager == null) {
                    manager = new DbConfig(propsName);
                }
            }
        }
        return manager.propNames();
    }

    /**
     * Returns true if the properties are readable. This method is mainly
     * valuable at setup time to ensure that the properties file is setup
     * correctly.
     */
    public static boolean propertyFileIsReadable() {
        if (manager == null) {
            synchronized(managerLock) {
                if (manager == null) {
                    manager = new DbConfig(propsName);
                }
            }
        }
        return manager.propFileIsReadable();
    }

    /**
     * Returns true if the properties are writable. This method is mainly
     * valuable at setup time to ensure that the properties file is setup
     * correctly.
     */
    public static boolean propertyFileIsWritable() {
        if (manager == null) {
            synchronized(managerLock) {
                if (manager == null) {
                    manager = new DbConfig(propsName);
                }
            }
        }
        return manager.propFileIsWritable();
    }

    /**
     * Returns true if the yazd.properties file exists where the path property
     * purports that it does.
     */
    public static boolean propertyFileExists() {
        if (manager == null) {
            synchronized(managerLock) {
                if (manager == null) {
                    manager = new DbConfig(propsName);
                }
            }
        }
        return manager.propFileExists();
    }

    /**
     * Returns the version number of Yazd as a String. i.e. -- major.minor.revision
     */
    public static String getYazdVersion() {
        return MAJOR_VERSION + "." + MINOR_VERSION + "." + REVISION_VERSION;
    }

    /**
     * Returns the major version number of Yazd. i.e. -- 1.x.x
     */
    public static int getYazdVersionMajor() {
        return MAJOR_VERSION;
    }

    /**
     * Returns the minor version number of Yazd. i.e. -- x.1.x
     */
    public static int getYazdVersionMinor() {
        return MINOR_VERSION;
    }

    /**
     * Returns the revision version number of Yazd. i.e. -- x.x.1
     */
    public static int getYazdVersionRevision() {
        return REVISION_VERSION;
    }

    private Properties properties = null;
    private Object propertiesLock = new Object();
    private String resourceURI;

    /**
     * Creates a new PropertyManager. Singleton access only.
     */
    private DbConfig(String resourceURI) {
        this.resourceURI = resourceURI;
    }

    /**
     * Gets a Yazd property. Yazd properties are stored in yazd.properties.
     * The properties file should be accesible from the classpath. Additionally,
     * it should have a path field that gives the full path to where the
     * file is located. Getting properties is a fast operation.
     *
     * @param name the name of the property to get.
     * @return the property specified by name.
     */
    protected String getProp(String name) {
        //If properties aren't loaded yet. We also need to make this thread
        //safe, so synchronize...
        if (properties == null) {
            synchronized(propertiesLock) {
                //Need an additional check
                if (properties == null) {
                    loadProps();
                }
            }
        }
        String property = properties.getProperty(name);
        if (property == null) {
            return null;
        }
        else {
            return property.trim();
        }
    }

    /**
     * Sets a Yazd property. Because the properties must be saved to disk
     * every time a property is set, property setting is relatively slow.
     */
    protected void setProp(String name, String value) {
        //Only one thread should be writing to the file system at once.
        synchronized (propertiesLock) {
            //Create the properties object if necessary.
            if (properties == null) {
                loadProps();
            }
            properties.setProperty(name, value);
            saveProps();
        }
    }

    protected void deleteProp(String name) {
        //Only one thread should be writing to the file system at once.
        synchronized (propertiesLock) {
            //Create the properties object if necessary.
            if (properties == null) {
                loadProps();
            }
            properties.remove(name);
            saveProps();
        }
    }

    protected Enumeration<?> propNames() {
        //If properties aren't loaded yet. We also need to make this thread
        //safe, so synchronize...
        if (properties == null) {
            synchronized(propertiesLock) {
                //Need an additional check
                if (properties == null) {
                    loadProps();
                }
            }
        }
        return properties.propertyNames();
    }

    private void loadProps() {
        properties = new Properties();
        InputStream in = null;
        try {
            in = getClass().getResourceAsStream(resourceURI);
            properties.loadFromXML(in);
            
            //properties.load(in);
        }
        catch (Exception e) {
            System.err.println("Error reading Yolanda properties in PropertyManager.loadProps() " + e);
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            } catch (Exception e) { }
        }
    }




    /**
     * Saves Yazd properties to disk.
     */
    private void saveProps() {
        //Now, save the properties to disk. In order for this to work, the user
        //needs to have set the path field in the properties file. Trim
        //the String to make sure there are no extra spaces.
        if(propFileIsWritable()){
            String path = properties.getProperty("path").trim();
            OutputStream out = null;
            try {
                out = new FileOutputStream(path);
                properties.store(out, "yazd.properties -- " + (new java.util.Date()));
            }
            catch (Exception ioe) {
                System.err.println("There was an error writing yazd.properties to " + path + ". " +
                        "Ensure that the path exists and that the Yazd process has permission " +
                        "to write to it -- " + ioe);
                ioe.printStackTrace();
            }
            finally {
                try {
                    out.close();
                } catch (Exception e) { }
            }
        }
 
        }
    /**
     * this method checks to see if the application is already setup or not.
     * If the application is setup, then you can load the properties from database.
     * Otherwise it has to be determined how the application is being setup. Are we using
     * the default connection pooler or a different db connection pooler.
     * @return value of the source
     */

    @SuppressWarnings("unused")
	private int getLoadSource(){
        int  loadsource=1; //assume that application is not setup.
        // check to see if we are loading from the database using a context provider
        try{
            InitialContext ctxt = new InitialContext ();
            ctxt.lookup(DbConnectionManager.CONTEXT_JDBC_NAME);
            loadsource=2;
        }catch(Exception e){

        }
        return loadsource;

    }

    /**
     * Returns true if the properties are readable. This method is mainly
     * valuable at setup time to ensure that the properties file is setup
     * correctly.
     */
    public boolean propFileIsReadable() {
        try {
            //InputStream in = 
            	getClass().getResourceAsStream(resourceURI);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the yazd.properties file exists where the path property
     * purports that it does.
     */
    public boolean propFileExists() {
        String path = getProp("path");
        if( path == null ) {
            return false;
        }
        File file = new File(path);
        if (file.isFile()) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Returns true if the properties are writable. This method is mainly
     * valuable at setup time to ensure that the properties file is setup
     * correctly.
     */
    public boolean propFileIsWritable() {
        String path = getProp("path");
        File file = new File(path);
        if (file.isFile()) {
            //See if we can write to the file
            if (file.canWrite()) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return false;
        }
    }
}
