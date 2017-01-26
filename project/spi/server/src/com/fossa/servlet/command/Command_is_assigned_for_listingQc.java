/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class returns user id of the users assigned for ListingQC OR TallyQC.
 * @author bmurali
 */
public class Command_is_assigned_for_listingQc implements Command
{

   private Connection connection;
   private PreparedStatement getUserIdPrepStmt = null;
   private ResultSet getUserIdResultSet = null;

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
         String userName = action.getAttribute(A_USER_NAME);
         String status = action.getAttribute(A_STATUS);
         connection = dbTask.getConnection();
         
         //Get UserId
         if ("ListingQC".equals(status)) {
            getUserIdPrepStmt = connection.prepareStatement(SQLQueries.SEL_USR_LISTQC);
         }
         else if ("TallyQC".equals(status)) {
            getUserIdPrepStmt = connection.prepareStatement(SQLQueries.SEL_USR_TALLYQC);
         }
         getUserIdPrepStmt.setString(1, userName);
         getUserIdPrepStmt.setString(2, userName);
         getUserIdPrepStmt.executeQuery();
         getUserIdResultSet = getUserIdPrepStmt.getResultSet();
         if (getUserIdResultSet.next()) {
            int user_id = getUserIdResultSet.getInt(1);
            //Start writing the XML
            String userSessionId = user.getFossaSessionId();
            writer.startElement(T_REPLY_IS_ASSIGNED_LISTING_QC);
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.writeAttribute(A_USERS_ID, user_id);
            writer.endElement();
         }
         else {
            throw new ServerFailException("No fields  Assigned For :  " + status);
         }
         getUserIdPrepStmt.close();
         getUserIdResultSet.close();
      } catch (IOException ex) {
         CommonLogger.printExceptions(this, "IOException while getting the assigned user for listing qc.", ex);
      } catch (ServerFailException exc) {
         CommonLogger.printExceptions(this, "ServerFailException while getting the assigned user for listing qc.", exc);
         return exc.getMessage();
      } catch (SQLException ex) {
         CommonLogger.printExceptions(this, "SQLException while getting the assigned user for listing qc.", ex);
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
