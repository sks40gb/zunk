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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class handles the closing of volumes.
 * @author bmurali
 */
public class Command_close_volume implements Command
{

   private Connection connection;
   private PreparedStatement getBatchIdPrepStmt = null;
   private PreparedStatement updateBatchPrepStmt = null;
   private ResultSet getBatchIdResultSet = null;
   private int volumeId;
   private int projectId;
   private String status;
   private String batchStatus;

   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
         connection = dbTask.getConnection();
         volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
         status = action.getAttribute(A_STATUS);   //Holds the batch status
         projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
         
         //Get batchid for given volume
         if ("Listing".equals(status)) {
            getBatchIdPrepStmt = connection.prepareStatement(SQLQueries.SEL_BATCH_ID_BATCH);
            batchStatus = "Listing";
         }
         getBatchIdPrepStmt.setInt(1, volumeId);
         getBatchIdPrepStmt.setString(2, batchStatus);
         getBatchIdPrepStmt.executeQuery();
         getBatchIdResultSet = getBatchIdPrepStmt.getResultSet();
         while (getBatchIdResultSet.next()) {
            int batchId = getBatchIdResultSet.getInt(1);
            
            //Update all Listing batch status with 'LComplete' for a Given Volume
            if ("Listing".equals(status)) {
               updateBatchPrepStmt = user.prepareStatement(dbTask, SQLQueries.UPD_STATUS_BATCH);
               batchStatus = "LComplete";             
            }
            updateBatchPrepStmt.setString(1, batchStatus);
            updateBatchPrepStmt.setInt(2, batchId);
            updateBatchPrepStmt.executeUpdate();
            
            // will delete the listing queued batch
            user.executeUpdate(dbTask,SQLQueries.DEL_USR_QUEUE+batchId);
         }
         getBatchIdPrepStmt.close();
         updateBatchPrepStmt.close();
         getBatchIdResultSet.close();
      } catch (SQLException ex) {
         CommonLogger.printExceptions(this, "SQLException while closing the volume.", ex);
      } catch (ServerFailException excp) {
         CommonLogger.printExceptions(this, "ServerFailException while closing the volume.", excp);
         return excp.getMessage();
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
