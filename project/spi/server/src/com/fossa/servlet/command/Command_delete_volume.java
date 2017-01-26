/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.exception.UserErrorMessage;
import com.fossa.servlet.session.UserTask;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 *  This class handles deleting the volumes and other parameters.
 * @author ashish
 */
class Command_delete_volume implements Command
{

   public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer)
   {
      int volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
      Log.print("Command_delete_volume volumeId=" + volumeId);
      Statement st = null;
      ResultSet getAssgnedBatchesResultSet = null;
      try {
         st = dbTask.getStatement();
         PreparedStatement select_assign_a = task.prepareStatement(dbTask, SQLQueries.SEL_ASSIGN_A);
         select_assign_a.setInt(1, volumeId);
         select_assign_a.setInt(2, volumeId);
         getAssgnedBatchesResultSet = select_assign_a.executeQuery();
         if (getAssgnedBatchesResultSet.next()) {
            throw new ServerFailException(UserErrorMessage.batchInUse);
         }
         //Delete the corresponding page issue
         st.executeUpdate(SQLQueries.DEL_PG_ISSUE + volumeId);
         //Delete the corresponding page
         st.executeUpdate(SQLQueries.DEL_PG + volumeId);

         PreparedStatement deleteChidCodedPrepStmt = task.prepareStatement(dbTask, SQLQueries.DEL_COM_CC);
         deleteChidCodedPrepStmt.setInt(1, volumeId);
         deleteChidCodedPrepStmt.executeUpdate();

         PreparedStatement deleteFieldChangePrepStmt = task.prepareStatement(dbTask, SQLQueries.DEL_COM_FC);
         deleteFieldChangePrepStmt.setInt(1, volumeId);
         deleteFieldChangePrepStmt.executeUpdate();

         PreparedStatement deleteValuePrepStmt = task.prepareStatement(dbTask, SQLQueries.DEL_COM_VAL);
         deleteValuePrepStmt.setInt(1, volumeId);
         deleteValuePrepStmt.executeUpdate();

         PreparedStatement deleteNameValuePrepStmt = task.prepareStatement(dbTask, SQLQueries.DEL_COM_NV);
         deleteNameValuePrepStmt.setInt(1, volumeId);
         deleteNameValuePrepStmt.executeUpdate();

         PreparedStatement deleteLongValuePrepStmt = task.prepareStatement(dbTask, SQLQueries.DEL_COM_LV);
         deleteLongValuePrepStmt.setInt(1, volumeId);
         deleteLongValuePrepStmt.executeUpdate();

         PreparedStatement deleteChildPrepStmt = task.prepareStatement(dbTask, SQLQueries.DEL_COM_CHILD);
         deleteChildPrepStmt.setInt(1, volumeId);
         deleteChildPrepStmt.executeUpdate();

         PreparedStatement deleteBatchErrorPrepStmt = task.prepareStatement(dbTask, SQLQueries.DEL_COM_BE);
         deleteBatchErrorPrepStmt.setInt(1, volumeId);
         deleteBatchErrorPrepStmt.executeUpdate();

         PreparedStatement deleteBatchUserPrepStmt = task.prepareStatement(dbTask, SQLQueries.DEL_COM_BU);
         deleteBatchUserPrepStmt.setInt(1, volumeId);
         deleteBatchUserPrepStmt.executeUpdate();

         PreparedStatement deleteTeamsQueuePrepStmt = task.prepareStatement(dbTask, SQLQueries.DEL_COM_TQ);
         deleteTeamsQueuePrepStmt.setInt(1, volumeId);
         deleteTeamsQueuePrepStmt.executeUpdate();

         PreparedStatement deleteUsersQueuePrepStmt = task.prepareStatement(dbTask, SQLQueries.DEL_COM_UQ);
         deleteUsersQueuePrepStmt.setInt(1, volumeId);
         deleteUsersQueuePrepStmt.executeUpdate();

         //Delete correspoding batch
         task.executeUpdate(dbTask, SQLQueries.DEL_FROM_BATCH + volumeId);
         //Delete correspoding range
         st.executeUpdate(SQLQueries.DEL_FROM_RANGE + volumeId);
         //Delete correspoding teamvolume
         task.executeUpdate(dbTask, SQLQueries.DEL_FROM_TEAMSVOL + volumeId);
         //Delete correspoding volumeerror
         st.executeUpdate(SQLQueries.DEL_VOL_ERROR + volumeId);
         //Delete correspoding volume
         task.executeUpdate(dbTask, SQLQueries.DEL_FROM_VOLUME + volumeId);

      } catch (ServerFailException sql) {
         CommonLogger.printExceptions(this, "ServerFailException while deleting the volume.", sql);
         return sql.getMessage();
      } catch (SQLException sql) {
         CommonLogger.printExceptions(this, "SQLException while deleting the volume.", sql);
         return null;
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while deleting the volume.", exc);
         return null;
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
