/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;
/**
 * This class returns the treatment level for a batch. 
 * @author bmurali
 */

public class Command_check_level implements Command
{

   /**
     * Fetches the treatment level for a batch.
     *
     * @param action    XML Element object
     * @param user      UserTask object taken from the user's session object
     * @param dbTask    DBTask object taken from the user's session object
     * @param writer    MessageWriter object to write messages back to the client.
     * @return  String  A null String
     */
   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      int batchId = 0;
      String level = "";
      Statement getTreatmentLevelStatement = null;
      String query = "select treatment_level from batch where batch_id=";

      try {
         getTreatmentLevelStatement = dbTask.getStatement();
         batchId = Integer.parseInt(action.getAttribute(A_BATCH_ID));

         ResultSet getTreatmentLevelResultSet = getTreatmentLevelStatement.executeQuery(query + batchId);
         if (getTreatmentLevelResultSet.next()) {
            level = getTreatmentLevelResultSet.getString(1);
         }
         String userSessionId = user.getFossaSessionId();
         //Start writing the XML
         writer.startElement(T_BATCH_LEVEL);
         writer.writeAttribute(A_FOSSAID, userSessionId);
         writer.writeAttribute(A_LEVEL, level);
         writer.endElement();

      } catch (IOException ex) {
         CommonLogger.printExceptions(this, "IO Exception when checking Treatment Level. ", ex);
      } catch (SQLException ex) {
         CommonLogger.printExceptions(this, "SQL Exception when checking Treatment Level. ", ex);
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
