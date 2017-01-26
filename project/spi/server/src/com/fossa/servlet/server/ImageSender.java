/*
 * ImageSender.java
 *
 * Created on November 30, 2007, 10:52 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.server;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.msg.MultiOutputStream;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 *
 * @author Bala
 */
/**
 * Class to manage sending of images.
 **/
public class ImageSender implements MessageConstants {

    final private static int[] scalePercent = {100, 100, 50, 50};
    final private static boolean[] halveYRes = {false, true, false, true};
    private static Logger logger = Logger.getLogger("com.fossa.servlet.server");
    /** Creates a new instance of ImageSender */

    public ImageSender() {
    }
   
    /**
     * Send an image to the client.
     * @param task    The image task serving the user.
     * @param dbTask   Database connection
     * @param messageAction  Message fragment for the current image_request
     * @param rawOutStream  The image server output stream for this task.
     *     TODO:  Why isn't this in the image task?
     */
    public static void send(UserTask task, DBTask dbTask, Element messageAction, MultiOutputStream rawOutStream) {
        String imagePath = messageAction.getAttribute(A_IMAGE_PATH);        
        //image path for server import              
        assert imagePath != null;
        String resolutionString = messageAction.getAttribute(A_RESOLUTION);
        int resolution = (resolutionString.length() == 0
                ? 0
                : Integer.parseInt(resolutionString));
        String offsetString = messageAction.getAttribute(A_OFFSET);
        int offset = (offsetString.length() == 0
                ? 0
                : Integer.parseInt(offsetString));

        try {
            int length = -1;
            InputStream inputStream = null;
            ImageSource imageFile = null;
            try {
                imageFile = new ImageSource(task,
                        dbTask, imagePath, offset, resolution);
                length = imageFile.getStreamLength();
                inputStream = imageFile.getStream();
            } catch (FileNotFoundException e) {
                // Not found -- return a zero-length file
                Log.print("File not found: " + imagePath);
                length = 0;
                logger.error("Exception during reading the file in ImageSender." + e);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            }

            OutputStream stream = rawOutStream.newStream();

            writeShort(stream, IMAGE_MAGIC);
            writeInt(stream, length);
            if (length > 0) {
                // We chop into chunks, so that progress bar on client works
                // TODO: Clean this up - big chunks on broadband, maybe suppress Nagle's algorithm
                //final int BUFFER_SIZE = 16384;
                final int BUFFER_SIZE = 4096;
                byte[] buffer = new byte[BUFFER_SIZE];
                int buffer_bytes = 1024 - 10;
                int count;
                while ((count = inputStream.read(buffer, 0, buffer_bytes)) > 0) {
                    stream.write(buffer, 0, count);
                    stream.flush();
                    if (buffer_bytes >= BUFFER_SIZE) {
                    // no action
                    } else if (buffer_bytes >= 1024) {
                        buffer_bytes *= 2;
                    } else {
                        buffer_bytes = 1024;
                    }
                }
                imageFile.close();
            }
            stream.close();
        } catch (IOException e) {
            logger.error("Exception during sending image." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(e);
        }
    }

    // Write a short integer (in big-endian order) to the given stream.

    private static void writeShort(OutputStream stream, int value) throws IOException {
        stream.write(value >> 8);
        stream.write(value);
    }
    // Write an integer (in big-endian order) to the given stream.

    private static void writeInt(OutputStream stream, int value) throws IOException {
        stream.write(value >> 24);
        stream.write(value >> 16);
        stream.write(value >> 8);
        stream.write(value);
    }
}
