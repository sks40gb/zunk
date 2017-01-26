/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class calls BatchIO.batchBoundary to add / remove  a batch based on a batch or document.
 * @author Bala
 */
public class Command_batch_boundary implements Command
{

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
         int givenBatchId = getInt(action, A_BATCH_ID);
         int givenChildId = getInt(action, A_ID);
         int givenDelta = getInt(action, A_DELTA);

         Log.print("Handler_batch_boundary: " + givenBatchId + "/" + givenChildId + "/" + givenDelta);
         //The method call below adds or removes a batch based on the values of batch_id and child_id:
            //moves a document to the next or the previous batch.
         BatchIO.batchBoundary(user, dbTask, givenBatchId, givenChildId, givenDelta);

      } catch (SQLException sql) {
         CommonLogger.printExceptions(this, "Exception while handling batch boundary.", sql);
      }
      return null;
   }

   // get integer attribute, or zero if empty string

   private int getInt(Element action, String attribute)
   {
      String attributeString = action.getAttribute(attribute);
      return (attributeString.length() == 0 ? 0 : Integer.parseInt(attributeString));
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
