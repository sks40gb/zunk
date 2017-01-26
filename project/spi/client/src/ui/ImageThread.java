/* $Header: /home/common/cvsarea/ibase/dia/src/ui/ImageThread.java,v 1.19 2005/03/14 13:10:32 weaston Exp $ */

/**
 * ImageThread.java
 *
 * Created on May 16, 2003, 2:27 PM
 */

package ui;

import beans.IbaseTextField;
import client.Global;
import client.ImageConnection;
import common.Log;
import common.msg.MessageConstants;
import beans.ViewTIFFPanel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.w3c.dom.Element;

/**
 *
 * @author  Bill
 */
public class ImageThread extends Thread implements MessageConstants {

    final private static int TIMEOUT = 120000;  // ping server every 2 min
    
    private static ImageThread     theImageThread = null;

    private static ArrayList requestList = new ArrayList();
    
    // information for image being retrieved
    private ViewTIFFPanel   viewer = null;
    private String          filename = null; // fully qualified
    private int             resolution = RES_HIGH;
    private int             offset = 0;
    private String serverIP_port;
    
    /** Creates a new instance of ImageThread */
    public ImageThread() {
        super();
        this.setDaemon(true);
    }
    
    public void run() {

        // make sure there's an image connection
        try {
            makeImageConnection();
        } catch (IOException e) {
            Log.quit(e);
        }
        
        for (;;) {
            synchronized (this) {
                if (! requestList.isEmpty()) {
                    // take the first element in the queue                  
                    RenderingRequest request
                            = (RenderingRequest) requestList.remove(0);
                    viewer = request.viewer;
                    filename = request.filename;
                    offset = request.offset;
                    resolution = request.resolution;
                    serverIP_port = request.serverIP_port;
                     SplitPaneViewer viewer = SplitPaneViewer.getInstance();
                     viewer.setImagePath(filename);
                    Log.print(">>>about to read image: "+filename+" resolution="+resolution);
                } else {                   
                    filename = null;
                    try {
                        //Log.print(">>>ImageThread about to wait");
                        this.wait(TIMEOUT);
                    } catch (InterruptedException e) {
                    }
                }
            }

            if (filename != null) {                
                //Log.print("calling obtainImage - "+filename);   
                
                final byte[] imageData
                        = client.Global.theImageConnection.obtainImage(
                                                viewer, filename, offset, resolution ,serverIP_port);
              
                //Log.print(">>>back from obtainImage " + imageData);
                SwingUtilities.invokeLater(
                    new Runnable() {
                        public void run() {
                            //Log.print(">>>calling ViewFromSource"); 
                            long startTime = System.currentTimeMillis();
                            boolean ok;
                            if (imageData != null) {
                                ok = viewer.ViewFromSource(null, null,
                                                           imageData, imageData.length);
                            } else {
                                String message = "EEImage not found.";
                                ok = viewer.ViewFromSource(null, null,
                                            ImageConnection.ERROR_IMAGE,
                                            ImageConnection.ERROR_IMAGE.length);
                                SwingUtilities.getWindowAncestor(viewer).toFront();
                                JOptionPane.showMessageDialog(viewer,
                                                              "Requested image not found",
                                                              "Image not found",
                                                              JOptionPane.ERROR_MESSAGE);
                            }
                            //Log.print(">>>back from ViewFromSource - ok="+ok
                            //          +" time="+(System.currentTimeMillis() - startTime));                    
                        }
                    });
            } else if (requestList.isEmpty()) {
                try {                  
                    // must have timed out, ping the server
                    client.Global.theImageConnection.sendMessage(T_PING);
                    Element reply = client.Global.theImageConnection.receiveMessage();
                    if (! T_OK.equals(reply.getNodeName())) {
                        Log.quit("Invalid reply pinging image server: "+reply.getNodeName());
                    }
                } catch (IOException e) {
                    Log.quit(e);
                }
            }
        }
    }
    
    /**
     * Request fetching and rendering the given image.
     */
    public static void renderImage(ViewTIFFPanel viewer,
                                   String filename,
                                   int offset,
                                   int resolution,String serverIP_port)
    {
        
        SplitPaneViewer viewer1 = SplitPaneViewer.getInstance();
        viewer1.setImagePath(filename);
        //Log.print("renderImage "+filename+" res="+resolution);
        // Make sure there's an image thread running
        if (theImageThread == null) {
            theImageThread = new ImageThread();
            theImageThread.start();
            Log.print("Image Thread: theImageThread started");
        }

        // Save the requested info for the image thread
        // and wake up the image thread
        RenderingRequest request = new RenderingRequest(
                                viewer, filename, offset, resolution,serverIP_port);
        synchronized (theImageThread) {                       
            for (int i = 0; i < requestList.size(); i++) {                
                RenderingRequest queuedRequest
                                = (RenderingRequest) requestList.get(i);
                if (viewer == queuedRequest.viewer) {
                    requestList.set(i, request);
                    return;
                }
            }
            requestList.add(request);
            theImageThread.notifyAll();
        }
    }

    /**
     * Determine if there is an image waiting to be rendered for this viewer.
     * (Used by "movie" to keep in sync.)
     */
    public static boolean isBusy(ViewTIFFPanel viewer) {
        synchronized(theImageThread) {
            for (int i = 0; i < requestList.size(); i++) {
                RenderingRequest queuedRequest = (RenderingRequest) requestList.get(i);
                if (viewer == queuedRequest.viewer) {
                    return true;
                }
            }
            return false;
        }
    }

    // a class to hold a request for rendering
    private static class RenderingRequest {
        ViewTIFFPanel viewer;
        String filename;
        int offset;
        int resolution;
        String serverIP_port;
        
        RenderingRequest (ViewTIFFPanel viewer,
                          String filename, int offset, int resolution,String serverIP_port) {
            this.viewer = viewer;
            this.filename = filename;
            this.offset = offset;
            this.resolution = resolution; 
            this.serverIP_port = serverIP_port;
            
        }
    }



    public static synchronized void makeImageConnection() throws IOException {        
        //Try to create an image connection
        // TODO: login for image server -- maybe capability from coding server
//commented bala
//        if (Global.theImageConnection != null) {
//            // already connected
//            return;
//        }
         
         
        Global.theImageConnection = new ImageConnection();  
       // ClientServerCommunication clientServerCommunication = new ClientServerCommunication();
        //ServerConnection serverConnection = new ServerConnection();
        try {            
                Global.theImageConnection.connect();                            
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException e) {
            e.printStackTrace();
            // failed to connect - clean up and tell user
            throw new IOException("Image Server: " + e);
        }
    }

    /**
     * Clear the image in the given viewer
     */
    public static synchronized void clearImage(ViewTIFFPanel viewer) {
        viewer.ViewFromSource(null, null,
                              ImageConnection.ERROR_IMAGE,
                              ImageConnection.ERROR_IMAGE.length);
    }
    
    
}
