/* $Header: /home/common/cvsarea/ibase/dia/src/server/ImageSender.java,v 1.13.8.1 2006/03/09 12:09:16 nancy Exp $ */

package server;

import com.lexpar.util.Log;
import common.msg.MessageConstants;
import common.msg.MultiOutputStream;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.w3c.dom.Element;

/**
 * Class to manage sending of images.
 **/
public class ImageSender implements MessageConstants {

    // scaling parameters for {HIGH, MEDIUM, LOW, DRAFT}_RES
    final private static int[]     scalePercent = {100,100,50,50};
    final private static boolean[] halveYRes = {false,true,false,true};

    /**
     *  Class may not be instantiated
     */
    private ImageSender() {}

    /**
     * Send an image to the client.
     * @param task    The image task serving the user.
     * @param action  Message fragment for the current image_request
     * @param rawout  The image server output stream for this task.
     *     TODO:  Why isn't this in the image task?
     */
    public static void send(ImageServerTask task, Element action, MultiOutputStream rawout) {

        String imagePath = action.getAttribute(A_IMAGE_PATH);
        assert imagePath != null;
        String resolutionString = action.getAttribute(A_RESOLUTION);
        int resolution = (resolutionString.length() == 0
                          ? 0
                          : Integer.parseInt(resolutionString));
        String offsetString = action.getAttribute(A_OFFSET);
        int offset = (offsetString.length() == 0
                          ? 0
                          : Integer.parseInt(offsetString));

        try {
            int length = -1;
            InputStream inputStream = null;
            ImageSource imageFile = null;
            try {
                imageFile = new ImageSource(
                    task, imagePath, offset, resolution);
                length = imageFile.getStreamLength();
                inputStream = imageFile.getStream();
            } catch (FileNotFoundException e) {
                // Not found -- return a zero-length file
                Log.print("File not found: "+imagePath);
                length = 0;
            }

            OutputStream stream = rawout.newStream();

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
            Log.quit(e);
        }

    }
    
     public static void send(String filename, int pageOffset, int resolution,  OutputStream rawout) {
         try {
            int length = -1;
            InputStream inputStream = null;
            ImageSource imageFile = null;
            try {
                imageFile = new ImageSource(
                     filename, pageOffset, resolution);
                length = imageFile.getStreamLength();
                inputStream = imageFile.getStream();
            } catch (FileNotFoundException e) {
                // Not found -- return a zero-length file
                Log.print("File not found: "+filename);
                length = 0;
            }
             
            
            
            OutputStream stream = rawout;

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
