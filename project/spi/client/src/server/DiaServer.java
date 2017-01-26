/* $Header: /home/common/cvsarea/ibase/dia/src/server/DiaServer.java,v 1.14 2004/08/05 21:34:51 weaston Exp $ */

package server;

import common.CommonProperties;
import common.FileProperties;
import com.lexpar.util.Log;

import java.io.*;
import java.util.*;

/**
 * Main program for the DIA server.
 */
public class DiaServer {

    private static String propertyFileName = CommonProperties.SERVER_PROPERTIES;
    
    // Class may not be instantiated
    private DiaServer() {}

    /**
     * Main program for the DIA server.
     * @param args One optional parameter - the name of the server property file.
     */
    final public static void main (String[] args) {
        
        Log.setQuitException(FatalException.class);
        Log.setLogFileName("server");
        
        if (args.length > 0) {
            propertyFileName = args[0];
        }
        ServerProperties.setPropertyFileName(propertyFileName);

        System.setProperty("javax.net.ssl.keyStore",
                           FileProperties.getBaseDirectory()+"/"+"keystore");
        System.setProperty("javax.net.ssl.keyStorePassword", "password");
        
        try {
            DiaListener.start();   
            Log.quit("Listener loop terminated.");
        } catch (FatalException e) {
            System.out.println("Main thread terminated by fatal exception");
            System.exit(1);
        } catch (Throwable e) {
            System.out.println("Main thread terminated by uncaught exception");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
