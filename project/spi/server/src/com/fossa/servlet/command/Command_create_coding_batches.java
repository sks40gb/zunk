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
 * This class create batches for Coding.
 * @author ashish
 */
class Command_create_coding_batches implements Command
{

   public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer)
   {
      int givenBatchId = Integer.parseInt(action.getAttribute(A_BATCH_ID));
      int givenDocumentCount = Integer.parseInt(action.getAttribute(A_COUNT));
      Log.print("create_coding_batches: batch id=" + givenBatchId + " count=" + givenDocumentCount);
      try {
         BatchIO.createCodingBatches(task, dbTask, givenBatchId, givenDocumentCount);
         Log.print("... updated batchId=" + givenBatchId);
      } catch (SQLException sql) {
         CommonLogger.printExceptions(this, "SQLException while creating coding batches.", sql);
         return null;
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while creating coding batches.", exc);
         return null;
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
