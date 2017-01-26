/* $Header: /home/common/cvsarea/ibase/dia/src/server/ImageServer.java,v 1.6.8.1 2006/03/22 20:27:15 nancy Exp $ */

package server;

import common.CommonProperties;
import common.FileProperties;
import com.lexpar.util.Log;


/**
 * Main program for the DIA Image server.
 */
public class ImageServer
{
    // Note.  ImageServer should NEVER rewrite the property file
    // It is updated only by the coding server
    private static String propertyFileName = CommonProperties.SERVER_PROPERTIES;
    
    // Class may not be instantiated
    private ImageServer() {}

    /**
     * Main program for the DIA image server.
     * @param args One optional parameter - the name of the server property file.
     */
    final public static void main (String[] args) {

        Log.setQuitException(FatalException.class);
        Log.setLogFileName("image");
        
        if (args.length > 0) {
            propertyFileName = args[0];
        }
        ServerProperties.setPropertyFileName(propertyFileName);

        System.setProperty("javax.net.ssl.keyStore",
                           FileProperties.getBaseDirectory()+"/"+"keystore");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
        
        try {
            ImageListener listener = new ImageListener();
            listener.start();   
            Log.quit("Listener loop terminated.");
        } catch (FatalException e) {
            System.out.println("Main thread terminated by fatal exception");
            e.printStackTrace();
            System.exit(1);
        } catch (Throwable e) {
            System.out.println("Main thread terminated by uncaught exception");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
}
