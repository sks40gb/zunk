/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 *
 * @author bala
 */
/**
 * An extension of java.util.Properties.  Reads properties from a file.
 *<p>
 * Note.  Thread safety: Properties extends Hashtable, which has all synchronized
 * methods.  Therefore, this class can be used on multiple threads.
 */
public class FileProperties extends Properties {

    // The name of the file holding the properties (set on reading)
    private String holdFileName = null;

    // Title to be written with properties file
    private String title;

    // directory where properties file is stored (may be explicitly set)
    private String directory;

    private static Logger logger = Logger.getLogger("com.fossa.servlet.common");
    /**
     * Create a new FileProperties in a the working directory.
     */
    public FileProperties (String fileName, String title) {
        this(fileName,title,null);
    }

    /**
     * Create a new FileProperties in a given directory.
     */
    public FileProperties (String fileName, String title, String directory) {
        this.title = title;
        this.directory = directory;
        loadFile(fileName);
    }

    /**
     * Load the properties from the disk file.  If there is no existing file,
     * treat as if empty file.
     */
    private synchronized void loadFile(String fileName) {
       
        try {
            if (directory == null) {
                directory = getBaseDirectory();
            }
            holdFileName = directory + "/" + fileName;
            InputStream stream = new BufferedInputStream(new FileInputStream(holdFileName));
            this.load(stream);
            stream.close();
        } catch (FileNotFoundException e) {
            // ignore not found -- treat as empty file
            logger.error("Exception while loading the file." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        } catch (IOException e) {
            logger.error("Exception while accessing the file." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(e);
        }
    }
    
    /**
     * Store the properties in the disk file.
     */
    public synchronized void storeFile() {
        try {
            OutputStream stream = new BufferedOutputStream(new FileOutputStream(holdFileName));
            this.store(stream, title);
            stream.close();
        } catch (IOException e) {
            logger.error("Exception while saving in the file." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(e);
        }
    }

    /**
     * Obtain fully qualified name for working directory.  (This allows testing within
     * the Sun Studio IDE or as standalone.  If working in Sun Studio, the proper working
     * directory is the one containing the bin subdirectory; otherwise it is the
     * actual working directory.
     */
    public static String getBaseDirectory() {
        URL baseURL = FileProperties.class.getResource("/bin");
        String result;
        if (baseURL != null) {
            String basePath = baseURL.toString();
            try {
                basePath = java.net.URLDecoder.decode(basePath, "UTF-8");
            } catch (java.io.UnsupportedEncodingException uee) {
               logger.error("Exception while getting the directory name." + uee);
               StringWriter sw = new StringWriter();
               uee.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            	Log.quit(uee);
            }
            // ends here
            assert basePath.startsWith("file:");
            result = basePath.substring(6, basePath.length() - 4);
        } else {
            StringBuffer buffer = new StringBuffer(System.getProperty("user.dir"));
            int pos;
            while ((pos = buffer.indexOf("\\")) >= 0) {
                buffer.setCharAt(pos, '/');
            }
            result = buffer.toString();
        }
        //Log.write("FileProperties.getBaseDIrectory() => '"+result+"'");
        return result;
    }
}
