/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlUtil;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.exception.UserErrorMessage;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class handles the closing of QA batches.
 * @author ashish
 */
class Command_close_qa implements Command
{

   public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer)
   {
      String teamsIdString = action.getAttribute(A_TEAMS_ID);
      String usersIdString = action.getAttribute(A_USERS_ID);
      boolean reject = "YES".equals(action.getAttribute(A_REJECT));
      String comments = XmlUtil.getTextFromNode(action);
      String newStatus = (reject ? "CodingQC" : "QAComplete");
      long now = System.currentTimeMillis();
      int maxGroup = 0;

      Connection con = null;
      Statement st = null;
      int volumeId = task.getLockVolumeId();
      Log.print("in Command_close_qa.run teams=" + teamsIdString + "/" + teamsIdString.length() + " users=" +
              usersIdString + "/" + usersIdString.length() + " reject=" + reject + " volume=" + volumeId);

      try {
         con = dbTask.getConnection();
         st = dbTask.getStatement();
         // see if we're dealing with groups
         ResultSet rs = st.executeQuery(SQLQueries.SEL_MAX_PF + volumeId);
         if (rs.next()) {
            maxGroup = rs.getInt(1);
         }
         Log.print("(Command_close_qa) maxGroup is " + maxGroup);
         rs.close();
         // mark batches to be closed
         String innerJoinBatchQuery = "";
         if (teamsIdString.length() == 0 && usersIdString.length() == 0) {
         }
         else {
            innerJoinBatchQuery = " from batch B inner join batchuser BU on BU.batch_id = B.batch_id";
         }
         String innerJoinUserQuery = "";
         String teamIdCondition = "";
         if (teamsIdString.length() == 0) {
         }
         else {
            innerJoinUserQuery = " from batchuser BU inner join users U on U.users_id = BU.coder_id";
            teamIdCondition = " and U.teams_id=" + teamsIdString;
         }

         String userIdCondition = "";
         if (usersIdString.length() == 0) {
         }
         else {
            userIdCondition = " and BU.coder_id=" + usersIdString;
         }

         String updateBatchQuery = "update batch" + " set status=''" + innerJoinBatchQuery + innerJoinUserQuery +
                 " where volume_id=" + volumeId + "   and status='QA'" + userIdCondition + teamIdCondition;
         int count = st.executeUpdate(updateBatchQuery);
         if (count > 0) {
            // there are some batches selected.  Close/reject them.
            PreparedStatement getMaxChildPrepStmt = con.prepareStatement(SQLQueries.SEL_MAX_CHILD);
            getMaxChildPrepStmt.setInt(1, volumeId);
            ResultSet getMaxChildResultSet = getMaxChildPrepStmt.executeQuery();

            if (getMaxChildResultSet.next()) {
               int round = 1 + getMaxChildResultSet.getInt(1);

               if (reject) {
                  // For reject, mark batches as qa rework
                  PreparedStatement updatebatchUserPrepStmt = con.prepareStatement(SQLQueries.UPD_QA_REWORK);
                  updatebatchUserPrepStmt.setInt(1, volumeId);
                  updatebatchUserPrepStmt.executeUpdate();

               }
               else {
                  // For accept, check that all selected children have been saved
                  PreparedStatement getTop1ChildCodedPrepStmt = con.prepareStatement(SQLQueries.SEL_TOP_1);
                  getTop1ChildCodedPrepStmt.setInt(1, volumeId);
                  getMaxChildResultSet = getTop1ChildCodedPrepStmt.executeQuery();
                  if (getMaxChildResultSet.next()) {
                     throw new ServerFailException(UserErrorMessage.unsavedDoc);
                  }
               }
               PreparedStatement updateCCPrepStmt = con.prepareStatement(SQLQueries.UPD_CC);
               updateCCPrepStmt.setInt(1, round);
               updateCCPrepStmt.setInt(2, volumeId);
               updateCCPrepStmt.executeUpdate();

               // Roll error statistics into volumeerror table
                    // Note: X.round=1 selects first Coding round,
                    //   so errors associated with original coder
               PreparedStatement insertVolErrorPrepStmt = con.prepareStatement(SQLQueries.INS_VOLUME_ERROR);
               insertVolErrorPrepStmt.setLong(1, now);
               insertVolErrorPrepStmt.setInt(2, round);
               insertVolErrorPrepStmt.setInt(3, volumeId);
               insertVolErrorPrepStmt.executeUpdate();

               // Clear error statistics for children
               PreparedStatement deleteChildError = con.prepareStatement(SQLQueries.DEL_CE);
               deleteChildError.setInt(1, volumeId);
               deleteChildError.executeUpdate();

               // add to batch comments
               if (comments.length() > 0) {
                  comments += "\n";
               }
               //fetch current date along with users id
               rs = st.executeQuery(SQLQueries.SEL_SUB_STRING + task.getUsersId());
               rs.next();
               comments += rs.getString(1) + " QA" + (reject ? " REJECTED " : " ") + rs.getString(2);
               rs.close();
               PreparedStatement updateBatcCommentsPrepStmt = con.prepareStatement(SQLQueries.UPD_BC);
               updateBatcCommentsPrepStmt.setString(1, "\n" + comments);
               updateBatcCommentsPrepStmt.setInt(2, volumeId);
               updateBatcCommentsPrepStmt.executeUpdate();
               updateBatcCommentsPrepStmt.close();
               // just in case there are batches without comments -- there shouldn't be
               ResultSet getBatchIdResultset = st.executeQuery("select batch_id from batch where status = '' " +
                       "and volume_id = " + volumeId);
               getBatchIdResultset.next();
               int batchId = getBatchIdResultset.getInt(1);
               PreparedStatement insBatchCommentsPrepStmt = con.prepareStatement("Declare @count_batch_id as int" +
                                                              " SET @count_batch_id = (SELECT Count(*) " +
                                                              "from batch_comments where batch_id = " + batchId + ")" +
                                                              " If Not(@count_batch_id >0)  " +
                                                              "INSERT into batch_comments(batch_id, comments)  " +
                                                              "SELECT batch_id,?  FROM batch " +
                                                              "WHERE volume_id=? and status=''");
               insBatchCommentsPrepStmt.setString(1, "\n" + comments);
               insBatchCommentsPrepStmt.setInt(2, volumeId);
               insBatchCommentsPrepStmt.executeUpdate();
               insBatchCommentsPrepStmt.close();
               // Set the final status                    
               if (maxGroup > 0 && newStatus.equals("QAComplete")) {
                  // If we're dealing with groups, set the status back to Coding
                        // for the next group.
                  PreparedStatement updateBatchPrepStmt = task.prepareStatement(dbTask, SQLQueries.UPD_BATCH_MAX);
                  updateBatchPrepStmt.setInt(1, maxGroup);
                  updateBatchPrepStmt.setString(2, newStatus);
                  updateBatchPrepStmt.setInt(3, maxGroup);
                  updateBatchPrepStmt.setInt(4, volumeId);
                  updateBatchPrepStmt.executeUpdate();
               }
               else {
                  PreparedStatement updateBatchStatusPrepStmt = task.prepareStatement(dbTask, SQLQueries.UPD_SET_STATUS);
                  updateBatchStatusPrepStmt.setString(1, newStatus);
                  updateBatchStatusPrepStmt.setInt(2, volumeId);
                  updateBatchStatusPrepStmt.executeUpdate();
               }
            }
         }
         String userSessionId = task.getFossaSessionId();
         //Start writing the XML
         // return the count of batches to the client
         writer.startElement(T_UPDATE_COUNT);
         writer.writeAttribute(A_FOSSAID, userSessionId);
         writer.writeAttribute(A_COUNT, count);
         writer.endElement();
      } catch (SQLException sql) {
         CommonLogger.printExceptions(this, "SQLException while closing the QA batch.", sql);
         return null;
      } catch (ServerFailException exc) {
         CommonLogger.printExceptions(this, "ServerFailException while closing the QA batch.", exc);
         return exc.getMessage();
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while closing the QA batch.", exc);
         return null;
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
