/* $Header: /home/common/cvsarea/ibase/dia/src/tools/DiaProperties.java,v 1.3.6.1 2006/03/30 12:28:55 nancy Exp $ */

package tools;

import common.CommonProperties;
import common.FileProperties;



/**
 * Utility class to allow reading of properties that are constant for 
 * all installations of DIA.
 * Note: Cloned from Local.Properties; writing not implemented
 */
public class DiaProperties {

    // class may not be instantiated
    private DiaProperties() {}

    final private static String FILE_NAME = CommonProperties.getDiaPropertiesFilePath();
    final private static String TITLE = null; // title not used -- never written
    private static String localDirName = null;
    private static FileProperties theProperties = null;

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
     * @param key   the property key
     * @return  the property value or null    
     */
    public static synchronized String getProperty(String key, String dflt) {
        if (theProperties == null) {
            openProperties();
        }
        return theProperties.getProperty(key, dflt);
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
        theProperties = new FileProperties(FILE_NAME, TITLE, localDirName);
    }

    public static FileProperties getInstance() {
        if (theProperties == null) {
            openProperties();
        }
        return theProperties;
    }

    //private static void writeProperties() {
    //    theProperties.storeFile();
    //}
}
