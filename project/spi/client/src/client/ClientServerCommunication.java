/*
 * ClientServerCommunication.java
 *
 * Created on October 29, 2007, 8:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package client;

import common.CommonProperties;
import common.Log;
import common.msg.MessageConstants;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import common.msg.XmlUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import tools.LocalProperties;

/**
 * Establish the connection between the client and server.
 * @author bmurali
 */
public class ClientServerCommunication implements MessageConstants {

    /** server context path */
    public static final String PROPS_SERVER_CONTEXT = LocalProperties.getProperty("serverContext");
    /** Server port number */
    public static final String PROPS_SERVER_PORT = LocalProperties.getProperty("server_port");
    /** Server IP address */
    public static final String PROPS_SERVER_IP = LocalProperties.getProperty("server_ip");
    /** Image context path */
    public static final String PROPS_IMAGE_CONTEXT = LocalProperties.getProperty("imageContext");
    /** Image port number */
    public static final String PROPS_IMAGE_PORT = LocalProperties.getProperty("image_port");
    /** Image IP address */
    public static final String PROPS_IMAGE_IP = LocalProperties.getProperty("image_ip");
    /** Server URL */
    public static final String SERVER_URL = "http://" + PROPS_SERVER_IP + ":" +
            PROPS_SERVER_PORT + PROPS_SERVER_CONTEXT;
    /** Image URL */
    public static final String IMAGE_URL = "http://" + PROPS_SERVER_IP + ":" +
            PROPS_IMAGE_PORT + PROPS_IMAGE_CONTEXT;
    private static final String DISCONNECTED = "Disconnected";
    private static final String RECONNECT = "Reconnect";
    private static final String EXIT = "Exit";
    private OutputStream rawOutStream;
    private URLConnection url;
    private NodeList updateList;

    /**
     * Opens the server connection for the client.
     * @return  Server URL
     * @throws  MalformedURLException  If the string specifies an
     *               unknown protocol.
     * @throws java.io.IOException   if an I/O exception occurs.        
     * @throws java.lang.ClassNotFoundException - If the Class does not exists.
     */
    public URLConnection getServletConnection()
            throws MalformedURLException, IOException, ClassNotFoundException {
        url = new URL(SERVER_URL).openConnection();
        url.setDoOutput(true);
        // Waiting time for making the connection with server.
        url.setConnectTimeout(10000);
        return url;
    }

    /**
     * Opens the server connection for the Image.
     * @return  Server URL
     * @throws  MalformedURLException  If the string specifies an
     *               unknown protocol.
     * @throws java.io.IOException   if an I/O exception occurs.        
     * @throws java.lang.ClassNotFoundException - If the Class does not exists.
     */
    public URLConnection getConnectionForImageServlet()
            throws MalformedURLException, IOException, ClassNotFoundException {
        url = new URL(IMAGE_URL).openConnection();
        url.setDoOutput(true);
        //Waiting time for connection.
        url.setConnectTimeout(10000);
        return url;
    }

    /**
     * Start to write the message from here.
     * 
     * @param element Action name.
     * @param rawOutStream OutputStream to write the message.
     * @return MessageWriter
     * @throws java.io.IOException   if an I/O exception occurs.
     */
    public MessageWriter startMessage(String element, OutputStream rawOutStream) throws IOException {
        MessageWriter writer = new MessageWriter(rawOutStream, CommonProperties.MESSAGE_DTD);
        writer.startElement(element);
        return writer;
    }

    /**
     * Send a new message.
     * Convenience method for message without content.
     * @param element 
     * @throws java.io.IOException
     */
    public void sendMessage(String element)
            throws IOException {
        rawOutStream = url.getOutputStream();

        MessageWriter writer = new MessageWriter(rawOutStream, CommonProperties.MESSAGE_DTD);
        writer.startElement(element);
        writer.endElement();
        writer.close();
    }

    /**
     * Recieve message from the server.
     * Message is in XML format.
     * @param rawInStream For reading the incoming message from server.
     * @return Action name
     * @throws java.io.IOException   if an I/O exception occurs.
     */
    public Element receiveMessage(InputStream rawInStream) throws IOException {
        synchronized (this) {
            //Check the list of messages coming from the server.
            if (updateList != null) {
                Log.print("**** updateList IS NOT NULL **** ");
                for (int i = 0; i < updateList.getLength(); i++) {
                    Log.print("... " + updateList.item(i));
                }
            }
            updateList = null;
        }
        Element messageNode = null;
        try {
            MessageReader reader = new MessageReader(rawInStream, CommonProperties.getMessageDTDFilePath());
            messageNode = reader.read().getDocumentElement();
        } catch (Exception ioe) {
            disconnect("Disconnected: " + ioe.toString());
        }
        Node action = messageNode.getFirstChild();
        while (action != null) {
            if (action.getNodeType() == Node.ELEMENT_NODE) {
                final Element actionElement = (Element) action;
                String actionName = actionElement.getTagName();
                // check for disconnect action (anywhere in the message)
                NodeList disconnects = messageNode.getElementsByTagName(T_DISCONNECT);
                if (disconnects.getLength() > 0) {
                    // Disconnect from the server and tell user
                    String message = "Disconnect: " + XmlUtil.getTextFromNode(disconnects.item(0));
                    Log.print(message);
                    disconnect(message);
                }

                // see if there are any updates and remember them
                Node action2 = action;
                while ((action2 = action2.getNextSibling()) != null) {
                    if (action2.getNodeType() == Node.ELEMENT_NODE) {
                        updateList = messageNode.getElementsByTagName(
                                T_UPDATE_MANAGED_MODEL);
                        break;
                    }
                }
                // Check for fail message
                if (actionName.equals(T_FAIL)) {
                    String message = XmlUtil.getTextFromNode(action);
                    if (actionElement.getAttribute(A_SQLSTATE).length() != 0) {
                        String sqlState = actionElement.getAttribute(A_SQLSTATE);
                        String codeString = actionElement.getAttribute(A_SQLCODE);
                        throw new SQLFailException(message,
                                sqlState,
                                Integer.parseInt(codeString));
                    } else {
                        throw new FailException(message);
                    }
                }

                return actionElement;
            }
            action = action.getNextSibling();
        }
        Log.quit("No action in message: " + messageNode);
        return null;
    }

    public synchronized void disconnect(final String message) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                public void run() {
                    int answer = JOptionPane.showOptionDialog(
                            client.Global.mainWindow, // parentComponent
                            message, // message
                            DISCONNECTED, // title
                            JOptionPane.YES_NO_OPTION, // optionType
                            JOptionPane.ERROR_MESSAGE, // messageType
                            null, // icon
                            new Object[]{RECONNECT, EXIT}, // options
                            RECONNECT);               // initial value);

                    if (answer == JOptionPane.YES_OPTION) {
                        // restart();
                        // never return
                    }
                    System.exit(1);
                }
            });
        } catch (Exception e) {
            Log.quit(e);
        }
    }
}
