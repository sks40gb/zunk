/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.w3c.dom.Element;

/**
 * This class returns a directory path for download purpose in client side.
 * @author ashish
 */
class Command_request_directory implements Command
{

   /**            
     * Root (relative to working directory) of download directory.
     * (also used in MessageRequestFile)
     */
   final static String DOWNLOAD_ROOT = "download";

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
         String userSessionId = user.getFossaSessionId();
         writer.startElement(T_DIRECTORY);
         writer.writeAttribute(A_FOSSAID, userSessionId);
         File dirFile = new File(DOWNLOAD_ROOT);

         // walk the directory tree and build the reply message
         walkDirectoryTree(user, null, dirFile, writer);
         writer.endElement();  // end message
      } catch (IOException exc) {
         CommonLogger.printExceptions(this, "IOException while getting the directory.", exc);
         return null;
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while getting the directory.", exc);
         return null;
      }
      return null;
   }

   /**
     * walk one level of directory tree and add files to directory message.
     */
   private void walkDirectoryTree(UserTask task, String subPath, File dirFile, MessageWriter writer)
           throws IOException
   {
      File subDirFile = (subPath == null ? dirFile : new File(dirFile, subPath));
      assert (subDirFile.isDirectory());

      // obtain list of files and directories
      String[] fileNames = subDirFile.list();
      // Note: sort not required, but it gives predictable order
      Arrays.sort(fileNames);

      // walk the files at this level
      for (int i = 0; i < fileNames.length; i++) {
         File memberFile = new File(subDirFile, fileNames[i]);
         if (!memberFile.isDirectory()) {
            assert (memberFile.length() <= Integer.MAX_VALUE);
            String userSessionId = task.getFossaSessionId();
            writer.startElement(T_FILENAME);
            writer.writeAttribute(A_FOSSAID, userSessionId);
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
            walkDirectoryTree(task, subPath == null
                    ? fileNames[i]
                    : subPath + "/" + fileNames[i],
                    dirFile,
                    writer);
         }
      }
   }

   public boolean isReadOnly()
   {
      return false;
   }

}
