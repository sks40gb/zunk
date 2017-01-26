/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import org.w3c.dom.Element;

/**
 * This class create ModifyErrors for Selected batch.
 * @author bmurali
 */
public class Command_create_modify_error_batch implements Command
{

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      int givenBatchId = Integer.parseInt(action.getAttribute(A_BATCH_ID));
      try {
         BatchIO.createModifyErrorBatches(user, dbTask, givenBatchId);
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while creating modify error batches.", exc);
         return null;
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
