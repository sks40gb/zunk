/* $Header: /home/common/cvsarea/ibase/dia/src/client/ServerConnection.java,v 1.37.6.3 2006/03/14 15:08:46 nancy Exp $ */
package client;

import common.CommonProperties;
import common.Log;
import common.msg.MessageConstants;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import common.msg.MultiInputStream;
import common.msg.MultiOutputStream;
import common.msg.XmlUtil;
import java.awt.Component;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URLConnection;
import javax.swing.ProgressMonitorInputStream;
import tools.DiaProperties;
import tools.JniSystem;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import server.ServerFailException;

/**
 * A singleton instance of ServerConnection is stored in <code>client.Global</code>
 * to provide communication between the client and the server.
 * @see client.Global
 */
public class ServerConnection implements MessageConstants {

    private MultiInputStream rawInStream;
    private MultiOutputStream rawOutStream;
    private GZIPOutputStream zipStream;
    private String userName;
    private String minVersion;
    private String maxVersion;
    private String sessionKey;
    private String timeZoneID;
    private boolean admin;
    private boolean permissionUnitize = false;
    private boolean permissionUqc = false;
    private boolean permissionCoding = false;
    private boolean permissionCodingqc = false;
    private boolean permissionQa = false;
    private boolean permissionTeamLeader = false;
    private boolean permissionAdmin = false;
    private boolean permissionAdminUsers = false;
    private boolean permissionAdminProject = false;
    private boolean permissionAdminBatch = false;
    private boolean permissionAdminEdit = false;
    private boolean permissionAdminImport = false;
    private boolean permissionAdminExport = false;
    private boolean permissionAdminProfit = false;
    private boolean permissionListing = false;
    private boolean permissionListingQC = false;
    private boolean permissionTally = false;
    private MessageWriter writer;
    private NodeList updateList;
    private InputStream inputStream;
    private ClientServerCommunication urlConnection;
    private URLConnection url;
    private String fossaSessionId;
    private String tagName;
    final public static byte[] ERROR_IMAGE = {(byte) 'E', (byte) 'E', (byte) ' '};

    /**
     * Login on server.  Raises an exception if unable to connect.
     */
    public void connect(String userName, char[] password, boolean admin,
            boolean restart, String newPassword)
            throws IOException, ClassNotFoundException, FailException {
        try {
            urlConnection = new ClientServerCommunication();
            url = urlConnection.getServletConnection();
        } catch (Exception e) {
            System.out.println(e);
        }

        Log.print("Server connection admin=" + admin + " restart=" + restart);
        this.userName = userName;
        this.admin = admin;
        String programVersion = DiaProperties.getProperty("program_version");
        MessageWriter messageWriter;
        messageWriter = startMessage(T_HELLO);
        messageWriter.writeAttribute(A_NAME, userName);
        messageWriter.writeAttribute(A_VERSION, programVersion);

        if (admin) {
            messageWriter.writeAttribute(A_ADMIN, "YES");
        }
        if (restart) {
            messageWriter.writeAttribute(A_SESSION_KEY, new String(password));
        } else {
            if (newPassword != null && newPassword.length() > 0) {
                // new password is MD5 - can't occur with session key
                messageWriter.writeAttribute(A_NEW_PASSWORD, newPassword);
            }
            messageWriter.writePassword(password);
        }
        messageWriter.endElement();
        messageWriter.close();
        url.connect();

        rawInStream = new MultiInputStream(url.getInputStream());
        //inputStream = url.getInputStream();
        Element reply = urlConnection.receiveMessage(rawInStream.newStream());
        tagName = reply.getTagName();
        if (T_ACCEPT.equals(tagName)) {
            fossaSessionId = reply.getAttribute(A_FOSSAID);
            setFossaSessionId(fossaSessionId);
            // save min and max versions
            minVersion = reply.getAttribute(A_MIN_VERSION);
            maxVersion = reply.getAttribute(A_MAX_VERSION);
            sessionKey = reply.getAttribute(A_SESSION_KEY);
            timeZoneID = reply.getAttribute(A_TIME_ZONE);

            //if user is not admin
            if (!admin) {
                permissionUnitize = "YES".equals(reply.getAttribute(A_OK_UNITIZE));
                permissionUqc = "YES".equals(reply.getAttribute(A_OK_UQC));
                permissionCoding = "YES".equals(reply.getAttribute(A_OK_CODING));
                permissionCodingqc = "YES".equals(reply.getAttribute(A_OK_CODINGQC));
                permissionQa = "YES".equals(reply.getAttribute(A_OK_QA));
                permissionListing = "YES".equals(reply.getAttribute(A_OK_LISTING));
                permissionListingQC = "YES".equals(reply.getAttribute(A_OK_LISTING_QC));
                permissionTally = "YES".equals(reply.getAttribute(A_OK_TALLY));
                permissionTeamLeader = "YES".equals(reply.getAttribute(A_OK_TEAM_LEADER));

            } else {
                permissionTeamLeader = "YES".equals(reply.getAttribute(A_OK_TEAM_LEADER));
                permissionAdmin = "YES".equals(reply.getAttribute(A_OK_ADMIN));
                permissionAdminUsers = "YES".equals(reply.getAttribute(A_OK_ADMIN_USERS));
                permissionAdminProject = "YES".equals(reply.getAttribute(A_OK_ADMIN_PROJECT));
                permissionAdminBatch = "YES".equals(reply.getAttribute(A_OK_ADMIN_BATCH));
                permissionAdminEdit = "YES".equals(reply.getAttribute(A_OK_ADMIN_EDIT));
                permissionAdminImport = "YES".equals(reply.getAttribute(A_OK_ADMIN_IMPORT));
                permissionAdminExport = "YES".equals(reply.getAttribute(A_OK_ADMIN_EXPORT));
                permissionAdminProfit = "YES".equals(reply.getAttribute(A_OK_ADMIN_PROFIT));
                permissionAdminProfit = "YES".equals(reply.getAttribute(A_OK_ADMIN_PROFIT));
            }
        } else {
            Log.quit("Invalid message from login: " + reply);
        }
    }

    /**
     * Get minimum client version (from server's accept message)
     */
    public String getMinVersion() {
        return minVersion;
    }

    /**
     * Get maximum client version (from server's accept message)
     */
    public String getMaxVersion() {
        return maxVersion;
    }

    /**
     * Return the user name.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Return the assigned session key.
     */
    public String getSessionKey() {
        return sessionKey;
    }

    /**
     * Remember server's time zone.
     */
    public void setTimeZoneID(String id) {
        timeZoneID = id;
    }

    /**
     * Return the server's time zone.
     */
    public String getTimeZoneID() {
        return timeZoneID;
    }

    /**
     * Clear and eturn the list of pending updates.
     * Synchronized to allow list to be passed to GUI thread.
     */
    public synchronized NodeList retrieveUpdateList() {
        NodeList result = updateList;
        updateList = null;
        return result;
    }

    /**
     * Set the list of pending updates.
     * Synchronized to allow list to be passed to GUI thread.
     */
    public synchronized void setUpdateList(NodeList list) {
        updateList = list;
    }

    public Element receiveMessage() throws IOException {
        synchronized (this) {
            if (updateList != null) {
                Log.print("**** updateList IS NOT NULL **** ");
                for (int i = 0; i < updateList.getLength(); i++) {
                    Log.print("... " + updateList.item(i));
                }
            }
            updateList = null;
        }
        rawInStream = new MultiInputStream(url.getInputStream());
        inputStream = rawInStream.newStream();

        Element messageNode = null;
        MessageReader reader = new MessageReader(inputStream, CommonProperties.getMessageDTDFilePath());
        messageNode = reader.read().getDocumentElement();
        Node action = messageNode.getFirstChild();
        while (action != null) {
            if (action.getNodeType() == Node.ELEMENT_NODE) {
                final Element actionElement = (Element) action;
                String actionName = actionElement.getTagName();

                boolean hasAttributes = actionElement.hasAttributes();
                if (hasAttributes) {
                    fossaSessionId = actionElement.getAttribute("fossaSession_id");
                    setFossaSessionId(fossaSessionId);
                }

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
                        updateList = messageNode.getElementsByTagName(T_UPDATE_MANAGED_MODEL);
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
                        //JOptionPane.showMessageDialog(null, message);
                        throw new FailException(message);
                    }
                }

                return actionElement;
            }
            action = action.getNextSibling();
        }
        // Log.quit("No action in message: "+messageNode);
        return null;
    }

    /**
     * Disconnect this client, with a message.
     * Synchronized to avoid quit from another source while disconnecting
     */
    public synchronized void disconnect(final String message) {
        try {
            SwingUtilities.invokeAndWait(new Runnable() {

                @Override
                public void run() {
                    int answer = JOptionPane.showOptionDialog(Global.mainWindow, // parentComponent
                            message+"\nPlease Login Again.", // message
                            "Disconnected", // title
                            JOptionPane.YES_OPTION, // optionType
                            JOptionPane.ERROR_MESSAGE, // messageType
                            null, // icon
                            new Object[]{"Exit"}, // options
                            "Reconnect");               // initial value);

                    //if (answer == JOptionPane.YES_OPTION) {
                        //restart();    --- TODO : running exe file 
                        //Close the session for the user
                       
                    //}
                       System.exit(1);                       
                    }
            });
        } catch (Exception e) {
            Log.quit(e);
        }
    }

    /**
     * Start a new message.
     */
    public MessageWriter startMessage(String element) throws IOException {
        urlConnection = new ClientServerCommunication();

        try {
            url = urlConnection.getServletConnection();
           rawOutStream = new MultiOutputStream(url.getOutputStream());
           MessageWriter writer = new MessageWriter(rawOutStream.newStream(), CommonProperties.MESSAGE_DTD);
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

    /**
     * Send a new message.
     * Convenience method for two attributes and text content.
     */
    public void sendMessage(String element,
            String attributeName1,
            String attributeValue1,
            String attributeName2,
            String attributeValue2,
            String content)
            throws IOException {
        MessageWriter writer = new MessageWriter((zipStream = new GZIPOutputStream(rawOutStream.newStream())),
                //(rawOutStream.newStream()),
                CommonProperties.MESSAGE_DTD);
        writer.startElement(element);
        writer.writeAttribute(attributeName1, attributeValue1);
        writer.writeAttribute(attributeName2, attributeValue2);
        writer.writeContent(content);
        writer.endElement();
        writer.close();
    }

    /**
     * Send a new message.
     * Convenience method for two attributes.
     */
    public void sendMessage(String element,
            String attributeName1,
            String attributeValue1,
            String attributeName2,
            String attributeValue2)
            throws IOException {
        MessageWriter messageWriter = new MessageWriter((zipStream = new GZIPOutputStream(rawOutStream.newStream())),
                //(rawOutStream.newStream()),
                CommonProperties.MESSAGE_DTD);
        messageWriter.startElement(element);
        messageWriter.writeAttribute(attributeName1, attributeValue1);
        messageWriter.writeAttribute(attributeName2, attributeValue2);
        messageWriter.endElement();
        messageWriter.close();
    }

    /**
     * Send a new message.
     * Convenience method for one attribute and text content.
     */
    public void sendMessage(String element,
            String attributeName1,
            String attributeValue1,
            String content)
            throws IOException {
        MessageWriter messageWriter = new MessageWriter((zipStream = new GZIPOutputStream(rawOutStream.newStream())),
                //(rawOutStream.newStream()),
                CommonProperties.MESSAGE_DTD);
        messageWriter.startElement(element);
        messageWriter.writeAttribute(attributeName1, attributeValue1);
        messageWriter.writeContent(content);
        messageWriter.endElement();
        messageWriter.close();
    }

    /**
     * Send a new message.
     * Convenience method for One attribute.
     */
    public void sendMessage(String element,
            String attributeName1,
            String attributeValue1)
            throws IOException {
        MessageWriter messageWriter = new MessageWriter((zipStream = new GZIPOutputStream(rawOutStream.newStream())),
                //(rawOutStream.newStream()),
                CommonProperties.MESSAGE_DTD);
        messageWriter.startElement(element);
        messageWriter.writeAttribute(attributeName1, attributeValue1);
        messageWriter.endElement();
        messageWriter.close();
    }

    /**
     * Send a new message.
     * Convenience method for text content only.
     */
    public void sendMessage(String element,
            String content)
            throws IOException {
        MessageWriter messageWriter = new MessageWriter((zipStream = new GZIPOutputStream(rawOutStream.newStream())),
                //(rawOutStream.newStream()),
                CommonProperties.MESSAGE_DTD);
        messageWriter.startElement(element);
        messageWriter.writeContent(content);
        messageWriter.endElement();
        messageWriter.close();
    }

    /**
     * Send a new message.
     * Convenience method for message without content.
     */
    public void sendMessage(String element)
            throws IOException {
        urlConnection = new ClientServerCommunication();
        try {
            url = urlConnection.getServletConnection();
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
        messageWriter.writeAttribute(A_FOSSAID, fossaSessionId);
        messageWriter.endElement();
        messageWriter.close();
        connect();
    }

    /** Shutdown connection to server */
    public void shutdown() throws IOException {
        shutdown(false);
    }

    /** Shutdown connection to server.
     * This version called from tools.Update
     * @param restart If true, restart="YES" is included in the
     *      message.  The server then retains the session for restart.
     */
    public void shutdown(boolean restart) {
        Log.print("shutdown " + restart);
        if (url != null) {
            //System.out.println("inside if");
            try {
                if (restart) {
                    sendMessage(T_GOODBYE, A_RESTART, "YES");
                // startMessage(T_GOODBYE, A_RESTART, "YES");
                } else {
                    sendMessage(T_GOODBYE);
                }
            } catch (Throwable th) {
                Log.print("FAILED IN SHUTDOWN - IGNORED: " + th);
            }
        }
    }

    public DataInputStream getDataStream() throws IOException {
        return new DataInputStream(new BufferedInputStream(new GZIPInputStream(rawInStream.newStream())));
    }

    public OutputStream getOutputStream() throws IOException {
        return new BufferedOutputStream(new GZIPOutputStream(rawOutStream.newStream()));
    }

    /** Obtain permissionUnitize */
    public boolean getPermissionUnitize() {
        return permissionUnitize;
    }

    /** Obtain permissionUqc */
    public boolean getPermissionUqc() {
        return permissionUqc;
    }

    /** Obtain permissionCoding */
    public boolean getPermissionCoding() {
        return permissionCoding;
    }

    /** Obtain permissionCodingqc */
    public boolean getPermissionCodingqc() {
        return permissionCodingqc;
    }

    /** Obtain permissionQa */
    public boolean getPermissionQa() {
        return permissionQa;
    }

    /** Obtain permissionTeamLeader */
    public boolean getPermissionTeamLeader() {
        return permissionTeamLeader;
    }

    /** Obtain permissionAdmin */
    public boolean getPermissionAdmin() {
        return permissionAdmin;
    }

    /** Obtain permissionAdminUsers */
    public boolean getPermissionAdminUsers() {
        return permissionAdminUsers;
    }

    /** Obtain permissionAdminProject */
    public boolean getPermissionAdminProject() {
        return permissionAdminProject;
    }

    /** Obtain permissionAdminBatch */
    public boolean getPermissionAdminBatch() {
        return permissionAdminBatch;
    }

    /** Obtain permissionAdminEdit */
    public boolean getPermissionAdminEdit() {
        return permissionAdminEdit;
    }

    /** Obtain permissionAdminImport */
    public boolean getPermissionAdminImport() {
        return permissionAdminImport;
    }

    /** Obtain permissionAdminExport */
    public boolean getPermissionAdminExport() {
        return permissionAdminExport;
    }

    /** Obtain permissionAdminProfit */
    public boolean getPermissionAdminProfit() {
        return permissionAdminProfit;
    }

    /** Obtain permissionUnitize */
    public boolean getPermissionListing() {
        return permissionListing;
    }

    public boolean getPermissionTally() {
        return permissionTally;
    }

    public boolean getPermissionListingQC() {
        return permissionListingQC;
    }

    // Restart after connection loss
    private void restart() {
        // Call DIA to start new instance, but do NOT wait for process to return
        try {

            boolean isNT;
            String osName = System.getProperty("os.name");
            if ("Windows Me".equalsIgnoreCase(osName) || "Windows 98".equals(osName) || "Windows 95".equals(osName)) {
                isNT = false;
            } else {
                isNT = true;
            }

            StringBuffer buffer = new StringBuffer(80);
            buffer.append("i-BaseDIA.exe ");
            if (admin) {
                buffer.append("--admin ");
            }
            buffer.append("--restart ");
            buffer.append(userName);
            buffer.append(" ");
            buffer.append(sessionKey);
            String commandLine = buffer.toString();
            Log.print("restart command: " + commandLine);
            Log.close();
            if (isNT) {
                Process dummy = Runtime.getRuntime().exec(commandLine);
            } else {
                new JniSystem().call(commandLine);
            }
        } catch (Exception e) {
            // Note.  This should re-open the log.
            Log.print("Restarting DIA: " + e);
            disconnect("FAILED RESTARTING DIA" + "\n\nPlease restart manually" + "\n\n" + e);
        }
        System.exit(0);
    }

    /**
     * Request and receive an image.  Returns null if no image exists.
     */
    public byte[] obtainImage(Component parent, String filename, int pageOffset, int resolution) /* throws IOException */ {
        //Log.print("...opening stream");
        ProgressMonitorInputStream stream = null;
        try {
            //Log.print("Request image: "+filename+" pageOffset="+pageOffset+" res="+resolution);

            writer = startMessage(T_IMAGE_REQUEST);
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
            //set maximum temporarily, so it doesn't die immediately
            //InputStream inStream=new BufferedInputStream(stream);                         
            stream.getProgressMonitor().setMaximum(10000);
            //Log.print("...starting read");

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
                //Log.print("...reading ended");
                stream.close();
                //Log.print("...stream closed");
                return result;
            }
        } catch (SocketTimeoutException ste) {
            Global.theServerConnection.disconnect("Image Disconnected: " + ste.toString());
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
            Global.theServerConnection.disconnect("Image Disconnected: " + se.toString());
            return null; // never reached

        } catch (IOException ioe) {
            Global.theServerConnection.disconnect("Image Disconnected: " + ioe.toString());
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

    public void connect() throws IOException {
        if (url != null) {
            url.connect();
        }
    }

    public void setFossaSessionId(String fossaSessionId) {
        this.fossaSessionId = fossaSessionId;
    }

    public String getFossaSessionId() {
        return fossaSessionId;
    }
}
