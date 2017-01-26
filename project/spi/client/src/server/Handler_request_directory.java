/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_request_directory.java,v 1.6.8.1 2006/03/14 15:08:46 nancy Exp $ */
package server;

//import common.edit.TableWriter;
//import common.Log;
import common.msg.MessageWriter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.w3c.dom.Element;

/**
 * Handler for request_directory message
 */
final public class Handler_request_directory extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_request_directory() {}

    /** 
     * This handler is read-only.
     * @return true
     */
    public boolean isReadOnly() {
        return true;
    }

    /**            
     * Root (relative to working directory) of download directory.
     * (also used in MessageRequestFile)
     */
    final static String DOWNLOAD_ROOT = "download";

    public void run(ServerTask task, Element action) throws IOException {

        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_DIRECTORY);

        File dirFile = new File(DOWNLOAD_ROOT);

        // walk the directory tree and build the reply message
        walkDirectoryTree(null, dirFile, writer);

        writer.endElement();  // end message
    }

    /**
     * walk one level of directory tree and add files to directory message.
     */
    private void walkDirectoryTree
            (String subPath, File dirFile, MessageWriter writer)
    throws IOException {
        File subDirFile = (subPath == null ? dirFile : new File(dirFile, subPath));
        assert (subDirFile.isDirectory());                                                                       

        // obtain list of files and directories
        String[] fileNames = subDirFile.list();
        // Note: sort not required, but it gives predictable order
        Arrays.sort(fileNames);

        // walk the files at this level
        for (int i = 0; i < fileNames.length; i++) {
            File memberFile = new File(subDirFile, fileNames[i]);
            if (! memberFile.isDirectory()) {
                assert (memberFile.length() <= Integer.MAX_VALUE);
                writer.startElement(T_FILENAME);
                writer.writeAttribute(A_NAME, 
                                    (subPath == null
                                     ? fileNames[i]
                                     : subPath + "/" + fileNames[i]));
                writer.writeAttribute(A_TIME, Long.toString(memberFile.lastModified()));
                writer.writeAttribute(A_LENGTH, Long.toString(memberFile.length()));
                writer.endElement();
            }
        }

        // walk the subdirectories at this level
        for (int i = 0; i < fileNames.length; i++) {
            File memberFile = new File(subDirFile, fileNames[i]);
            if (memberFile.isDirectory()) {
                walkDirectoryTree(subPath == null
                                  ? fileNames[i]
                                  : subPath + "/" + fileNames[i],
                                dirFile,
                                writer);
            }
        }
    }
}
