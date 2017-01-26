/*
 * Command_Coding_Manual_Tracking.java
 *
 * Created on January 28, 2008, 7:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import org.w3c.dom.Element;

/**
 * This class tracks the time taken by the end user spent on reading the Coding manual. 
 * @author Bala
 */
public class Command_Coding_Manual_Tracking implements Command
{

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {

      Connection connection;
      ResultSet getUserNameResultSet = null;
      PreparedStatement getUserNamePrepStmt = null;
      PreparedStatement insertTimeSpentPrepStmt = null;
      String userName = null;

      try {
         connection = dbTask.getConnection();
         int projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
         int userId = user.getUsersId();
         
         //Get UserName for given UserId
         getUserNamePrepStmt = connection.prepareStatement(SQLQueries.SEL_USER_NAME);
         getUserNamePrepStmt.setInt(1, userId);
         getUserNamePrepStmt.executeQuery();
         getUserNameResultSet = getUserNamePrepStmt.getResultSet();
         while (getUserNameResultSet.next()) {
            userName = getUserNameResultSet.getString(1);
         }

         long startdate = Long.parseLong(action.getAttribute(A_START_DATE));
         long endDate = Long.parseLong(action.getAttribute(A_END_DATE));

         Timestamp startTime = new Timestamp(startdate);
         Timestamp endTime = new Timestamp(endDate);
         long timeSpent = endDate - startdate;
         timeSpent = Math.round((timeSpent / 1000));
         insertTimeSpentPrepStmt = connection.prepareStatement(SQLQueries.INS_CODING_MANUAL);
         insertTimeSpentPrepStmt.setString(1, userName);
         insertTimeSpentPrepStmt.setInt(2, projectId);
         insertTimeSpentPrepStmt.setTimestamp(3, startTime);
         insertTimeSpentPrepStmt.setTimestamp(4, endTime);
         insertTimeSpentPrepStmt.setLong(5, timeSpent);
         insertTimeSpentPrepStmt.execute();
         getUserNameResultSet.close();
         insertTimeSpentPrepStmt.close();

      } catch (SQLException ex) {
         CommonLogger.printExceptions(this, "Exception while tracking coding manual.", ex);
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return false;
   }

}
