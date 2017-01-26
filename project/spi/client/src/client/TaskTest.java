

package client;

import common.CommonProperties;
import common.msg.MessageConstants;
import common.msg.MessageWriter;
import common.msg.MultiOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URLConnection;

/**
 *ClientTask to create the new batches for the Listing.
 * @author bmurali
 */
public class TaskTest extends ClientTask {

    /** Volume Id */
    private int volumeId;
    /** User Id */
    private int userId;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate the object with the following parameters 
     * @param volumeId   Volume id
     * @param userId     Project Id
     */
    public TaskTest() {
        
    }
    public TaskTest(int volumeId, int userId) {
        this.volumeId = volumeId;
        this.userId = userId;
    }
    
    
 /**
     * Start a new message.
     */
    public MessageWriter start(String element) throws IOException {
        ClientServerCommunication urlConnection = new ClientServerCommunication();

        try {
           URLConnection url = urlConnection.getServletConnection();
           MultiOutputStream rawOutStream = new MultiOutputStream(url.getOutputStream());
           
           MessageWriter writer = new MessageWriter(rawOutStream.newStream(), CommonProperties.MESSAGE_DTD);           
           String fossaSessionId = scon.getFossaSessionId();
           writer.startElement(element);
           //String session = getSessionKey();
           if (null != fossaSessionId) {
               writer.writeAttribute(A_FOSSAID, fossaSessionId);
           } else {
               writer.writeAttribute(A_FOSSAID, "-1");
           }          
           return writer;
        }catch(ConnectException connExc){
           throw new ConnectException("Unable to get the server connection.");
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new IOException("unable to get server connection");
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IOException("Unable to get the server connection");
        }
        return null;
    }
    
    public void writeMethod() throws IOException {  
        try{
            System.out.println("==========================>>>>>");
        ClientServerCommunication urlConnection = new ClientServerCommunication();
        URLConnection url = urlConnection.getServletConnection();
        String fossaSessionId = scon.getFossaSessionId();
        MultiOutputStream rawOutStream = new MultiOutputStream(url.getOutputStream());
           
        StringBuffer cache = new StringBuffer();
        
        Writer writer = new BufferedWriter(
                     new OutputStreamWriter(rawOutStream.newStream(), MessageConstants.UTF_8));
        
        cache.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");        
        writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        cache.append("<!DOCTYPE message SYSTEM \"message.dtd\" >\n");
        writer.append("<!DOCTYPE message SYSTEM \"message.dtd\" >\n");
        
        writer.write("<message>\n"+
                        "<ping fossaSession_id=\"" + fossaSessionId + "\"/>\n"+
                    "</message>\n");
        cache.append("<message>\n"+
                        "<ping fossaSession_id=\"" + fossaSessionId + "\"/>\n"+
                    "</message>\n");
            System.out.println(cache.toString());
         writer.flush();   
         writer.close();   
            cache = null;
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        
        writeMethod();

//        MessageWriter writer = startMessage(T_CREATE_LISTING_BATCH);        
//        writer.writeAttribute(A_VOLUME_ID, 12);
//        writer.writeAttribute(A_USERS_ID, "40");
//        writer.endElement();
//        writer.close();
//      //  Element reply = scon.receiveMessage();
//      //  setResult(reply);
    }
}
