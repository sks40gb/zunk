/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class deletes records from listing_qc table.
 * @author bmurali
 */
public class Command_delete_listing_qc implements Command
{

   private Connection connection;

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
         connection = dbTask.getConnection();
         int listing_qc_id = Integer.parseInt(action.getAttribute(A_LISTING_ID));
         //Delete the listing_qc record
         user.executeUpdate(dbTask, SQLQueries.DEL_LISTQC + listing_qc_id);
      } catch (SQLException ex) {
         CommonLogger.printExceptions(this, "Exception while deleting record from listing qc.", ex);
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
