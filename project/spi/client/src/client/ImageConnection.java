/* $Header: /home/common/cvsarea/ibase/dia/src/client/ImageConnection.java,v 1.22.8.1 2006/03/09 12:09:16 nancy Exp $ */
package client;

import common.CommonProperties;
import com.lexpar.util.Log;
import common.msg.MessageConstants;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import common.msg.MultiInputStream;
import common.msg.MultiOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.awt.Component;
import java.io.File;
import java.io.FileInputStream;
import java.io.InterruptedIOException;
import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import javax.swing.ProgressMonitorInputStream;

/**
 * A singleton instance of ImageConnection is stored in <code>client.Global</code>
 * to provide a connection to the ImageServer via <code>ui.ImageThread</code>.
 * @see client.Global
 * @see server.ImageServer
 * @see ui.ImageThread
 */
public class ImageConnection implements MessageConstants {

    final public static byte[] ERROR_IMAGE = {(byte) 'E', (byte) 'E', (byte) ' '};
    private MultiInputStream rawInStream;
    private MultiOutputStream rawOutStream;
    private MessageWriter writer;
    private ClientServerCommunication urlConnection;
    private URLConnection url;
    private InputStream inputStream;
    private final int TIME_OUT = 10000;

    /** 
     * Login on image server.  Raises an exception if unable to connect.
     */
    public void connect()
            throws IOException {

        try {
            urlConnection = new ClientServerCommunication();
            url = urlConnection.getConnectionForImageServlet();

        } catch (Exception e) {
            System.out.println(e);
        }
    }

    /**
     * Create a new MessageReader, read the returned message.
     * @return the Element returned by the server
     */
    public Element receiveMessage() throws IOException {
        rawInStream = new MultiInputStream(url.getInputStream());
        inputStream = rawInStream.newStream();
        MessageReader reader = new MessageReader(inputStream, CommonProperties.getMessageDTDFilePath());
        Element messageNode = reader.read().getDocumentElement();
        Node action = messageNode.getFirstChild();
        while (action != null) {
            if (action.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) action;
            }
            action = action.getNextSibling();
        }
        Log.quit("No action in message: " + messageNode);
        return null;
    }

    /**
     * Start a new message.
     * @param element the text of the beginning of the message --
     * should be as task name from common.msg.MessageConstants
     * @return the MessageWriter instance containing the given text
     * @see common.msg.MessageConstants
     */
    public MessageWriter startMessage(String element) throws IOException {
        urlConnection = new ClientServerCommunication();
        try {
            url = urlConnection.getConnectionForImageServlet();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new IOException("unable to get server connection");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        rawOutStream = new MultiOutputStream(url.getOutputStream());
        MessageWriter messageWriter = new MessageWriter(rawOutStream.newStream(), CommonProperties.MESSAGE_DTD);
        messageWriter.startElement(element);
        return messageWriter;
    }

    /**
     * Start writing the message.
     * @param urlString URL for image connection.
     * @param element Element
     * @return <code>MessageWriter</code>
     * @throws java.io.IOException If i/o error occured while writing the message.
     */
    public MessageWriter startMessage(String urlString, String element) throws IOException {
        try {
            url = getImageServerURL(urlString);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        rawOutStream = new MultiOutputStream(url.getOutputStream());
        MessageWriter messageWriter = new MessageWriter(rawOutStream.newStream(), CommonProperties.MESSAGE_DTD);
        messageWriter.startElement(element);
        return messageWriter;
    }

    private URLConnection getImageServerURL(String urlString) throws MalformedURLException, IOException {
        System.out.println("get URL connection for STring ... " + urlString);
        URLConnection _urlConnection = new URL(urlString).openConnection();
        _urlConnection.setDoOutput(true);
        _urlConnection.setConnectTimeout(TIME_OUT);
        return _urlConnection;
    }

    /**
     * Request and receive an image.  Returns null if no image exists.
     */
    public byte[] obtainImage(Component parent, String filename, int pageOffset, int resolution, String serverIP_port) /* throws IOException */ {       
        if (null != serverIP_port && null != filename) {
            String tokens[] = serverIP_port.split(":");

            String type = "";
            String ip = "";
            String port = "";
            if (tokens.length > 1) {
                type = tokens[0];
                ip = tokens[1];
                port = tokens[2];
                serverIP_port = type + ":" + ip + ":" + port;
            } else {
                type = tokens[0];
            }

            //if requesting for image
            if (type.equalsIgnoreCase("image")) {
                String urlString = "http://" + ip + ":" + port + "/fossa/image";              
                return getImageFromImageServer(parent, urlString, filename, pageOffset, resolution);
            } else if (type.equalsIgnoreCase("unc") || null == type) {                
                try {
                    File f = new File(filename);
                    FileInputStream fis = new FileInputStream(f);
                    byte b[] = new byte[9999999];
                    int len = fis.read(b);
                    return b;
                } catch (IOException ioe) {
                     //Requested image path doesn't exists
                    //Global.theServerConnection.disconnect("Image Disconnected: " + ioe.toString());
                    return null; // never reached

                }
            } else {
                return null;
            }

        } else {
            return null;
        }
    }

    /**
     * Request and receive an image.  Returns null if no image exists.
     */
    public byte[] getImageFromImageServer(Component parent, String urlString, String filename, int pageOffset, int resolution) {
        System.out.println("urls STRing is : " + urlString);
        System.out.println("filename is : " + filename);

        ProgressMonitorInputStream stream = null;
        try {
            writer = startMessage(urlString, T_IMAGE_REQUEST);
            writer.writeAttribute(A_IMAGE_PATH, filename);
            if (pageOffset != 0) {
                writer.writeAttribute(A_OFFSET, Integer.toString(pageOffset));
            }
            if (resolution != RES_HIGH) {
                writer.writeAttribute(A_RESOLUTION, Integer.toString(resolution));
            }
            writer.endElement();
            writer.close();

            rawInStream = new MultiInputStream(url.getInputStream());
            inputStream = rawInStream.newStream();
            stream = new ProgressMonitorInputStream(parent, "Read Image", inputStream);
            stream.getProgressMonitor().setMaximum(10000);
            short magic = readShort(stream);
            if (magic != IMAGE_MAGIC) {
                throw new IOException("bad magic on image stream: " + Integer.toHexString(magic));
            }

            int length = readInt(stream);
            if (length == 0) {
                Log.print("...image not found");
                stream.close();
                return null;
            } else if (length < 0) {
                Log.print("...error reading image: length=" + length);
                stream.close();
                return null;
            } else {
                // set maximum for read (including the 10 bytes already read)
                stream.getProgressMonitor().setMaximum(length + 10);
                // read the rest of the stream into a byte array
                byte[] result = new byte[length];
                int count = 0;
                int bytes;
                do {
                    bytes = stream.read(result,
                            count,
                            length - count);
                    if (bytes <= 0) {
                        Log.quit("Unexpected eof reading image");
                    }
                    count += bytes;               
                } while (count < length);
                stream.close();
                return result;
            }
        } catch (SocketTimeoutException ste) {
            //Global.theServerConnection.disconnect("Image Disconnected: " + ste.toString());
            return null; // never reached

        } catch (InterruptedIOException e) {
            Log.print("ImageConnection.obtainImage: reading cancelled");
            try {
                stream.close();
            } catch (IOException e2) {
                Log.quit(e2);
            }
            Log.print("...stream closed");
            return ERROR_IMAGE;
        } catch (SocketException se) {
            //Global.theServerConnection.disconnect("Image Disconnected: " + se.toString());
            return null; // never reached

        } catch (IOException ioe) {
            //Global.theServerConnection.disconnect("Image Disconnected: " + ioe.toString());
            return null; // never reached

        }

    }

    private short readShort(InputStream stream) throws IOException {
        int byte1 = stream.read();
        int byte2 = stream.read();
        if (byte1 < 0 || byte2 < 0) {
            Log.quit("obtainImage: unexpected EOF");
        }
        return (short) ((byte1 << 8) + (byte2 & 0xFF));
    }

    private int readInt(InputStream stream) throws IOException {
        int short1 = readShort(stream);
        int short2 = readShort(stream);
        return (short1 << 16) + (short2 & 0xFFFF);
    }

    /**
     * Send a new message.
     * Convenience method for message without content.
     */
    public void sendMessage(String element)
            throws IOException {
        writer = startMessage(element);
        writer.endElement();
        writer.close();
        url.connect();
    }

    /**
     * close the connection
     * @throws java.io.IOException if i/o error ocuured while sending message
     * to disconnect.
     */
    public void shutdown() throws IOException {
        if (url != null) {
            sendMessage(T_GOODBYE);
        }
    }
}