/* $Header: /home/common/cvsarea/ibase/dia/src/server/ImageListener.java,v 1.5.8.1 2006/03/22 20:27:15 nancy Exp $ */

package server;

import com.lexpar.util.Log;
import server.ServerProperties;

import javax.net.*;
import javax.net.ssl.*;
import java.net.*;                                                         
import java.io.*;
import java.util.Date;

/**
 * Listen for SSL connections.
 * @see ImageServerTask
 */
public class ImageListener {

    public ImageListener() {
    }

    int taskCount = 0;

    private ServerSocketFactory factory;
    private String host;
    private int port;

    /**
     * Start the listener.
     */
    public void start() {

        String host      = ServerProperties.getProperty("imagehost");
        int    port     = Integer.parseInt(ServerProperties.getProperty("imageport"));
        String database = ServerProperties.getProperty("imagedatabase");
        int    dbport   = Integer.parseInt(ServerProperties.getProperty("imagedbport"));

        Log.print("Image server starting on "+host+"; port "+port);

        // listen for SSL connections.
        try {
            factory = SSLServerSocketFactory.getDefault();
            ServerSocket s = factory.createServerSocket(port, 0, InetAddress.getByName(host));
            for (;;) {
                Socket incoming = s.accept();
                taskCount++;
                Log.print(taskCount + " " + new Date() + " SSL socket accepted");
                new ImageServerTask(incoming, taskCount).start();
            }
        } catch (IOException e) {
            Log.quit(e);
        }

        // never returns
        Log.quit("sslLoop returned");
    }
}
