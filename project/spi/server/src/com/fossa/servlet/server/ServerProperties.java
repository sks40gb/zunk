/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.server;

import com.fossa.servlet.common.FileProperties;
import com.fossa.servlet.common.Log;
import java.io.*;
import java.util.*;

/**
 *
 * @author ashish
 */

/**
 * Utility class to allow reading of properties that are constant for 
 * all installations of DIA.
 * Note: Cloned from Local.Properties; writing not implemented
 */

public class ServerProperties {

    // class may not be instantiated
    private ServerProperties() {}

    private static String propertyFileName = "server.properties";
    final private static String TITLE = null; // title not used -- never written
    private static FileProperties theProperties = null;

    /**
     * Set the file name for the property file.  This allows property
     * file to be chosen at runtime.
     */
    public static synchronized void setPropertyFileName(String fileName) {
        if (theProperties != null && ! fileName.equals(propertyFileName)) {
            close();
        }
        propertyFileName = fileName;
    }

    /**
     * Obtain a property from the local property file.  If there is no
     * local property file, an empty one is assumed as the default.
     * @param key   the property key
     * @return  the property value or null    
     */
    public static synchronized String getProperty(String key) {
        return getProperty(key, null);
    }

    /**
     * Discard local properties.
     *  Intended for use by the installer when the local.properties
     *  might be changed outside of this Java program.  Note that
     *  the local directory name is preserved.
     */
    public static synchronized void close() {
        theProperties = null;
    }

    /**
     * Obtain a property from the local property file.  If there is no
     * local property file, an empty one is assumed as the default.
     * Fails if no property is found.
     * @param key   the property key
     * @return  the property value    
     */
    public static synchronized String getProperty(String key, String dflt) {
        if (theProperties == null) {
            openProperties();
        }
        String result = theProperties.getProperty(key, dflt);
        if (result == null) {
            Log.quit("Server property not defined: "+key);
        }
        return result;
    }

    ///* Save a property in the local property file.  If there is no
    // * local property file, an empty one is assumed as the default.
    // * The modified property file is written immediately.
    // * @param key   the property key
    // * @param value the property value
    // */
    //public static synchronized void setProperty(String key, String value) {
    //    if (theProperties == null) {
    //        openProperties();
    //    }
    //    theProperties.setProperty(key, value);
    //    writeProperties();
    //}
    //
    ///** Allow the directory containing the local properties to
    // *  be specified.  This is intended only for use by the installer.
    // *  Properties file must be closed
    // */
    //public static void setLocalDirectory(String dir) {
    //    assert theProperties == null;
    //    localDirName = dir;
    //}

    private static void openProperties() {
        theProperties = new FileProperties(propertyFileName, TITLE);
    }

    public static synchronized void reopenProperties() {
        if (theProperties != null) {
            close();
        }
        openProperties();
    }

    //private static void writeProperties() {
    //    theProperties.storeFile();
    //}
}
