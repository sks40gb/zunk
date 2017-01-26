/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import org.w3c.dom.Element;

/**
 * This class create batches for Listing.
 * @author bmurali
 */
public class Command_create_listing_batches implements Command
{

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      int volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
      int userId = Integer.parseInt(action.getAttribute(A_USERS_ID));
      Log.print("create_listing_batches: volumeId=" + volumeId + " userId=" + userId);
      try {
         BatchIO.createListingBatches(user, dbTask, volumeId, userId, writer);
      } catch (ServerFailException exception) {
         CommonLogger.printExceptions(this, "SQLException while creating listing batches.", exception);
         return exception.getMessage();
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while creating listing batches.", exc);
         return null;
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
