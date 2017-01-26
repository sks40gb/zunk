/* $Header: /home/common/cvsarea/ibase/dia/src/client/DownloadUpgrade.java,v 1.5.8.1 2006/03/09 12:09:16 nancy Exp $ */
package client;

import common.Log;
import common.SyncFile;
import common.msg.MessageWriter;
import common.msg.MessageConstants;
import ui.DownloadFrame;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Request a list of filenames from the server and download them.
 */
public class DownloadUpgrade implements MessageConstants {

    /**
     * For each filename received from the server, call <code>doDownload</code>
     * to download the file and rename it filename+~~.
     * @param frame
     */
    public static void run(DownloadFrame frame) {

        // clear any leftover download/backup files
        deleteBackupFiles(".");

        final ServerConnection scon = Global.theServerConnection;
        try {
            // obtain directory from server
            MessageWriter writer = scon.startMessage(T_REQUEST_DIRECTORY);
            writer.endElement();
            writer.close();

            Element reply = scon.receiveMessage();
            NodeList files = reply.getElementsByTagName(T_FILENAME);

            for (int i = 0; i < files.getLength(); i++) {
                Element fileElement = (Element) files.item(i);
                String name = fileElement.getAttribute(A_NAME);
                String dirPath = null;
                int lastSlash = name.lastIndexOf('/');
                if (lastSlash >= 0) {
                    dirPath = name.substring(0, lastSlash);
                    name = name.substring(lastSlash + 1);
                }
                long timestamp = Long.parseLong(fileElement.getAttribute(A_TIME));
                int length = Integer.parseInt(fileElement.getAttribute(A_LENGTH));
                System.out.println(dirPath + "; " + name + "; " + timestamp + "; " + length);

                // make sure there is a subdirectory
                File dirFile = new File(".");
                if (dirPath != null) {
                    dirFile = new File(dirFile, dirPath);
                    dirFile.mkdirs();
                    if (!dirFile.isDirectory()) {
                        Log.quit("Upgrade: not a directory: " + dirFile);
                    }
                }

                // Check if download needed
                boolean downloadNeeded = false;
                File targetFile = new File(dirFile, name);
                if (!targetFile.exists()) {                    
                    downloadNeeded = true;
                } else if (targetFile.length() != length) {                    
                    downloadNeeded = true;
                } else if (targetFile.lastModified() != timestamp) {                    
                    downloadNeeded = true;
                }

                if (downloadNeeded) {
                    doDownload(scon, frame, dirPath, name);
                }

            }
            System.out.println("==finished==");

        } catch (IOException e) {
            Log.quit(e);
        }
    }

    /**
     * Download a file
     * @param scon  ServerConnection
     * @param frame  Parent frame
     * @param dirPath  Directory path where to save the downloaded file.
     * @param name - File name
     * @throws java.io.IOException If any i/o error is occured while writing 
     * the file.
     */
    private static void doDownload(ServerConnection scon,
            DownloadFrame frame,
            String dirPath,
            String name)
            throws IOException {
        System.out.println("doDownload: " + dirPath + "; " + name);
        frame.setText("SPiCA is downloading an upgraded version of the SPiCA program." + "  This may take a few minutes." + "\n\nDownloading: " + (dirPath == null ? "" : dirPath + "/") + name);

        // read the existing file (assume empty if not exists)
        // if there is a version with ~~~, it was downloaded on a previous try
        SyncFile oldFile;
        if ((new File(name + "~~~")).exists()) {
            oldFile = new SyncFile(dirPath, name + "~~~");
        } else {
            oldFile = new SyncFile(dirPath, name);
        }

        // build file download request
        int signatureCount = (oldFile.getLength() + (SyncFile.BLOCK_LENGTH - 1)) / SyncFile.BLOCK_LENGTH;
        MessageWriter writer = scon.startMessage(T_REQUEST_FILE);
        writer.writeAttribute(A_NAME, (dirPath == null ? name : dirPath + "/" + name));
        writer.writeAttribute(A_COUNT, signatureCount);
        writer.endElement();
        writer.close();
        DataOutputStream dataStream = new DataOutputStream(scon.getOutputStream());

        for (int i = 0; i < signatureCount; i++) {
            int offset = SyncFile.BLOCK_LENGTH * i;
            dataStream.writeInt(oldFile.fastSum(offset));
            dataStream.write(oldFile.strongSum(offset));
        }
        dataStream.writeInt(0);
        dataStream.close();

        // obtain download
        Element reply = scon.receiveMessage();
        String deltaName = reply.getAttribute(A_NAME);
        long deltaTime = Long.parseLong(reply.getAttribute(A_TIME));
        int deltaLength = Integer.parseInt(reply.getAttribute(A_LENGTH));

        System.out.println("delta received: " + deltaName + " " + deltaTime + " " + deltaLength);

        // reconstruct the file
        SyncFile newFile = new SyncFile(deltaTime, deltaLength);

        DataInputStream inStream = scon.getDataStream();

        byte[] deltaStrongSum = new byte[16];
        inStream.readFully(deltaStrongSum);

        int count;
        int pos = 0;
        while ((count = inStream.readInt()) != 0 && pos < deltaLength) {
            System.out.println("==" + count + " " + pos);
            if (count < 0) {
                // - count matched bytes
                int offset = inStream.readInt();
                oldFile.copy(offset, newFile, pos, -count);
                pos -= count;
            } else { // since count > 0
                // + count unmatched bytes
                // TODO figure out how to avoid this allocation !!!

                byte[] buffer = new byte[count];
                inStream.readFully(buffer);
                newFile.store(buffer, pos);
                pos += count;
            }
        }
        inStream.close();       
        if (pos != deltaLength) {
            Log.quit("File length mismatch");
        }
        // Compare checksums of both files.
        // Highly unlikely that these will not match -- we'll worry about
        //   it when it happens.
        if (!Arrays.equals(deltaStrongSum, newFile.strongSum())) {
            Log.quit("File checksum mismatch");
        }

        newFile.write(dirPath, name + "~~");

        // delete prior download, if any
        (new File(dirPath, name + "~~~")).delete();
    }

    /**
     *  walk directory tree and delete or rename any files whose name ends in "~"
     *  File naming:
     *    nnn~   = backup file made while installing
     *    nnn~~  = downloaded file to be installed
     *    nnn~~~ = previously downloaded file, which may be used as pattern for retry
     */
    private static void deleteBackupFiles(String directory) {
        File dirFile = new File(directory);
        String[] members = dirFile.list();
        Arrays.sort(members);
        String memberName;
        for (int i = 0; i < members.length; i++) {
            memberName = members[i];
            if (memberName.endsWith("~") && !memberName.endsWith("~~~")) {
                File memberFile = new File(dirFile, memberName);
                if (memberName.endsWith("~~")) {
                    File saveFile = new File(dirFile, memberName + "~");
                    // previously download file nnn~~, save it as nnn~~~
                    saveFile.delete(); // delete any existing save file

                    boolean result = memberFile.renameTo(saveFile);
                    if (!result) {
                        //errorCount++;
                        Log.print("failed renaming: " + directory + "/" + memberName + " to " + memberName + "~");
                    }
                } else {
                    // leftover backup file nnn~
                    boolean result = memberFile.delete();
                    if (!result) {
                        //errorCount++;
                        Log.print("failed deleting: " + directory + "/" + memberName);
                    }
                }
            }
        }
        for (int i = 0; i < members.length; i++) {
            memberName = members[i];
            if ((new File(directory, memberName)).isDirectory()) {
                deleteBackupFiles(directory + '/' + memberName);
            }
        }
    }
}
