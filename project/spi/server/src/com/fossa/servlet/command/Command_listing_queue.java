/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.valueobjects.BatchProcessHistroyData;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Element;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This class puts the user in Listing Queue.
 * @author bmurali
 */
public class Command_listing_queue implements Command
{

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      Statement getBatchIdStatement = null;
      Connection con = null;
      try {
         getBatchIdStatement = dbTask.getStatement();
         con = dbTask.getConnection();
         int batch_id = 0;
         Date date = new Date();
         long time = date.getTime();
         Timestamp timestamp = new Timestamp(time);
         int volume_id = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
         int users_id = Integer.parseInt(action.getAttribute(A_USERS_ID));
         if (volume_id > 0) {
            //Get the batch id for given volume id and batch status = Listing 
            ResultSet getBatchIdResultSet = getBatchIdStatement.executeQuery("select batch_id FROM batch WITH(HOLDLOCK)" +
                    " WHERE volume_id='" + volume_id + "'AND status = 'Listing'");
            while (getBatchIdResultSet.next()) {
               batch_id = getBatchIdResultSet.getInt(1);
               if (users_id > 0) {
                  // queue the batch to the user
                  user.executeUpdate(dbTask, "insert into usersqueue(batch_id,users_id,timestamp) " +
                          "values(" + batch_id + "," + users_id + ",'" + timestamp + "')");

                  //insert the batch history
                  BatchProcessHistroyData data = new BatchProcessHistroyData();
                  data.setBatch_id(batch_id);
                  data.setVolume_id(volume_id);
                  data.setProcess("Listing");
                  data.setIs_ready("Yes");
                  data.setIn_queue("Yes");
                  data.setQueued_time(BatchIO.getCurrentDateTime());
                  data.setQueued_to(users_id);
                  data.insertIntoHistoryTable(con);
               }
            }
         }
      } catch (Exception exe) {
         CommonLogger.printExceptions(this, "Exception while queueing the users for listing.", exe);
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
