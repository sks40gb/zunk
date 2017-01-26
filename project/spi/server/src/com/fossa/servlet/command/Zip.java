package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.log4j.Logger;

/**
 * This class is used to zip the exported files in server
 * @author anurag
 */
public class Zip {

    private static Logger logger = Logger.getLogger("com.fossa.servlet.command");

    /**
     * Create the instance of Zip and remember the parameters.
     * @param location    Location where to put the zip file.
     * @param filesToZip  Files to zip
     * @param pathFiles   Path for the files to be pushed to a zip file.
     * @param project     Project 
     * @param volume      Volume
     */
    public Zip(String location, String[] filesToZip, String[] pathFiles, String project, String volume) {

        byte[] buffer = new byte[18024];

        // Specify zip file name
        String zipFileName = location + project + "_" + volume + ".zip";

        try {
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFileName));

            // Set the compression ratio
            out.setLevel(Deflater.DEFAULT_COMPRESSION);

            // iterate through the array of files, adding each to the zip file
            for (int i = 0; i < pathFiles.length; i++) {
                // Associate a file input stream for the current file
                FileInputStream in = new FileInputStream(pathFiles[i]);

                // Add ZIP entry to output stream.
                out.putNextEntry(new ZipEntry(filesToZip[i]));

                // Transfer bytes from the current file to the ZIP file
                //out.write(buffer, 0, in.read(buffer));

                int len;
                while ((len = in.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                // Close the current entry
                out.closeEntry();

                // Close the current file input stream
                in.close();

            }
            // Close the ZipOutPutStream
            out.close();
        } catch (IllegalArgumentException iae) {            
            CommonLogger.printExceptions(this, "IllegalArgumentException during zipping files for TSearch..", iae);
        } catch (FileNotFoundException fnfe) {            
            CommonLogger.printExceptions(this, "FileNotFoundException during zipping files for TSearch.", fnfe);
        } catch (IOException ioe) {            
            CommonLogger.printExceptions(this, "IOException during zipping files for TSearch.", ioe);
        }


    }
}