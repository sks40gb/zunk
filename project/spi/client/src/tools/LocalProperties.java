/* $Header: /home/common/cvsarea/ibase/dia/src/tools/LocalProperties.java,v 1.7 2004/08/16 00:53:07 weaston Exp $ */

package tools;

//import common.Log;
import common.CommonProperties;
import common.FileProperties;


/* Utility class to allow saving of properties that are local to this
 * installation.  Note that files\dia.properties cannot be used, as it
 * gets overwritten when downloading an upgrade with rsync.
 */
public class LocalProperties {

    // class may not be instantiated
    private LocalProperties() {}

    final private static String FILE_NAME = CommonProperties.LOCAL_PROPERTIES;
    final private static String TITLE = "DIA local properties";
    private static String localDirName = null;
    private static FileProperties theProperties = null;

    /* Obtain a property from the local property file.  If there is no
     * local property file, an empty one is assumed as the default.
     * @param key   the property key
     * @return  the property value or null    
     */
    public static synchronized String getProperty(String key) {
        return getProperty(key, null);
    }

    /** Discard local properties.
     *  Intended for use by the installer when the local.properties
     *  might be changed outside of this Java program.  Note that
     *  the local directory name is preserved.
     */
    public static synchronized void close() {
        theProperties = null;
    }

    /* Obtain a property from the local property file.  If there is no
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

    /* Save a property in the local property file.  If there is no
     * local property file, an empty one is assumed as the default.
     * The modified property file is written immediately.
     * @param key   the property key
     * @param value the property value
     */
    public static synchronized void setProperty(String key, String value) {
        //if (key.startsWith("null")) {
        //    Log.quit("bad key="+key);
        //}
        if (theProperties == null) {
            openProperties();
        }
        theProperties.setProperty(key, value);
    }

    /* Obtain an integer property from the local property file.  If there is no
     * local property file, an empty one is assumed as the default.
     * @param key   the property key
     * @return  the property value or null    
     */
    public static synchronized int getProperty(String key, int dflt) {
        if (theProperties == null) {
            openProperties();
        }
        return Integer.parseInt(getProperty(key, Integer.toString(dflt)));
    }

    /* Save an integer property in the local property file.  If there is no
     * local property file, an empty one is assumed as the default.
     * The modified property file is written immediately.
     * @param key   the property key
     * @param value the property value
     */
    public static synchronized void setProperty(String key, int value) {
        if (theProperties == null) {
            openProperties();
        }
        theProperties.setProperty(key, Integer.toString(value));
    }

    /** Allow the directory containing the local properties to
     *  be specified.  This is intended only for use by the installer.
     *  Properties file must be closed
     */
    public static void setLocalDirectory(String dir) {
        assert theProperties == null;
        localDirName = dir;
    }

    private static void openProperties() {
        theProperties = new FileProperties(FILE_NAME, TITLE, localDirName);
    }

    /**
     * Rewrite the local property file.
     */
    public static void storeFile() {
        if (theProperties != null) {
            theProperties.storeFile();
        }
    }
}
