/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import org.w3c.dom.Element;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.session.UserTask;

/**
 * This class is used for replying the import progress status to the client.
 * @author anurag
 */
public class Command_request_import_progress_status implements Command
{

   public static int importStatus = 0;

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
         String userSessionId = user.getFossaSessionId();
         writer.startElement(T_REQUEST_IMPORT_PROGRESS_STATUS);   //row
         writer.writeAttribute(A_FOSSAID, userSessionId);   
         writer.writeAttribute(A_IMPORT_PROGRESS_STATUS, Command_import_data.progressCount); //import_progress_status
         writer.writeAttribute(A_IMPORT_ERROR_MESSAGE, (Command_import_data.errorMessage == null ? "" : Command_import_data.errorMessage)); //errorMessage
         writer.endElement(); //row ends
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while getting the import progress status.", exc);
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}

