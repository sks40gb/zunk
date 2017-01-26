/* $Header: /home/common/cvsarea/ibase/dia/src/server/ImageServerTask.java,v 1.13 2004/08/05 21:34:51 weaston Exp $ */

package server;

import common.CommonProperties;
import com.lexpar.util.Log;
import common.msg.LoggedInputStream;
import common.msg.MessageConstants;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import common.msg.MultiInputStream;
import common.msg.MultiOutputStream;

import java.net.*;                                                         
import java.io.*;
import java.sql.Connection;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

final public class ImageServerTask extends Thread implements MessageConstants {

    private Socket          incoming;
    private int             imageTaskNumber;
    private Connection      con = null;
    String dbName;
    int dbPort;

    MultiInputStream rawin;
    MultiOutputStream rawout;
    MessageWriter writer = null;

    ImageServerTask (Socket incoming, int imageTaskNumber) {
        this.incoming = incoming; 
        this.imageTaskNumber = imageTaskNumber; 
        dbName = ServerProperties.getProperty("database");
        dbPort   = Integer.parseInt(ServerProperties.getProperty("dbport"));
    }

    public void run() {
            
        try {
            rawin = new MultiInputStream(incoming.getInputStream());
            rawout = new MultiOutputStream(incoming.getOutputStream());
            Log.print(" enter (ImageServerTask.run)");
             
            for (;;) {
                Node messageAction = readMessage();
                do {
                    if (messageAction.getNodeType() == Node.ELEMENT_NODE) {
                        String action = messageAction.getNodeName();

                        if (action.equals(T_IMAGE_REQUEST)) {
                            ImageSender.send(this, (Element) messageAction, rawout);
                        } else if (action.equals(T_GOODBYE)) {
                            Log.print("goodbye received");
                            throw new GoodbyeException();
                        } else if (action.equals(T_PING)) {
                            //Log.print("image ping");
                            MessageWriter writer = new MessageWriter(
                                    new GZIPOutputStream(rawout.newStream()),
                                    CommonProperties.MESSAGE_DTD);
                            writer.startElement(T_OK);
                            writer.endElement();
                            writer.close();
                        } else {
                            Log.quit("Invalid message for image server: "+messageAction);
                        }
                    }
                } while ((messageAction = messageAction.getNextSibling()) != null  );
            }

        } catch (GoodbyeException e) {
            Log.print(imageTaskNumber + " End task");
        } catch (FatalException e) {
            // suppress message -- already done
            e.printStackTrace();
        } catch (SocketException e) {
            //e.printStackTrace();
            Log.print("Abort task " + imageTaskNumber + ": " + e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.print("Abort task " + imageTaskNumber + ": " + e);
        } catch (Throwable e) {
            e.printStackTrace();
            Log.print("Abort task " + imageTaskNumber + ": " + e);
        } finally {
            // clean up before ending task
        }
    }

    public int getImageTaskNumber() {
        return imageTaskNumber;
    }
    
    private Element readMessage() {
        try {
            MessageReader reader = new MessageReader(
                    LoggedInputStream.makeInstance(
                        new GZIPInputStream(rawin.newStream())),
                    //(rawin.newStream()),
                   CommonProperties.getMessageDTDFilePath());
            Document doc = reader.read();
            Element result = (Element) doc.getDocumentElement().getFirstChild();
            //Log.print("Message read: "+result);
            return result;
        } catch (SocketException e) {
            Log.quit("readMessage: ",e);
            return null;
        } catch (IOException e) {
            Log.quit(e);
            return null;
        }
    }

    /**
     * Return a JDBC connection for this task.  Multiple calls to
     * this method will return the same instance of Connection.
     */
    public Connection getConnection() {
        if (con == null) {
            con = DiaDatabaseOpener.open(dbPort, dbName);
        }
        return con;
    }
}
