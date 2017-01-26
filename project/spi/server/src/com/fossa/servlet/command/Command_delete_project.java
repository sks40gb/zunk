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
import com.fossa.servlet.server.valueobjects.ProjectHistoryData;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class handles deleting the projects and other parameters.
 * @author ashish
 */
class Command_delete_project implements Command{

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {    
        int projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
        Log.print("Command_delete_project projectId="+projectId);
        Statement getAssgnBatchesStatement = dbTask.getStatement();
        Connection con = dbTask.getConnection();
        ResultSet getAssignedBatchesResultSet = null;
        try{
            getAssignedBatchesResultSet = getAssgnBatchesStatement.executeQuery("SELECT TOP 1 0 as '0' FROM assignment A" +
                                                                                " inner join batch B on " +
                                                                                "B.batch_id = A.batch_id inner join " +
                                                                                "volume V on V.volume_id = B.volume_id " +
                                                                                "WHERE project_id="+projectId+" " +
                                                                                "UNION ALL SELECT 0 as '0' FROM session S " +
                                                                                "inner join volume V on V.volume_id = " +
                                                                                "S.volume_id WHERE project_id="+projectId);
            if (getAssignedBatchesResultSet!=null && getAssignedBatchesResultSet.next()) {
                throw new ServerFailException("Project has batches assigned or in use.");
            }
            //
            PreparedStatement deletePageIssuePrepStmt =  con.prepareStatement(SQLQueries.DEL_PAGEISSUE);
            deletePageIssuePrepStmt.setInt(1, projectId);
            deletePageIssuePrepStmt.executeUpdate();
            
            PreparedStatement deleteBinderImagePrepStmt =  con.prepareStatement(SQLQueries.DEL_BM);
            deleteBinderImagePrepStmt.setInt(1, projectId);
            deleteBinderImagePrepStmt.executeUpdate();

            PreparedStatement deletePagePrepStmt =  con.prepareStatement(SQLQueries.DEL_P_PAGE);
            deletePagePrepStmt.setInt(1, projectId);
            deletePagePrepStmt.executeUpdate();

            PreparedStatement deleteChildCodedPrepStmt =  con.prepareStatement(SQLQueries.DEL_CC);
            deleteChildCodedPrepStmt.setInt(1, projectId);
            deleteChildCodedPrepStmt.executeUpdate();

            PreparedStatement deleteFieldChangePrepStmt =  con.prepareStatement(SQLQueries.DEL_FC);
            deleteFieldChangePrepStmt.setInt(1, projectId);
            deleteFieldChangePrepStmt.executeUpdate();

            PreparedStatement deleteValuePrepStmt =  con.prepareStatement(SQLQueries.DEL_V_VALUE);
            deleteValuePrepStmt.setInt(1, projectId);
            deleteValuePrepStmt.executeUpdate();

            PreparedStatement deleteNameValuePrepStmt =  con.prepareStatement(SQLQueries.DEL_NV);
            deleteNameValuePrepStmt.setInt(1, projectId);
            deleteNameValuePrepStmt.executeUpdate();

            PreparedStatement deleteLongValuePrepStmt =  con.prepareStatement(SQLQueries.DEL_LV);
            deleteLongValuePrepStmt.setInt(1, projectId);
            deleteLongValuePrepStmt.executeUpdate();

            PreparedStatement deleteChildErrorPrepStmt =  con.prepareStatement(SQLQueries.DEL_CHILD_ERR);
            deleteChildErrorPrepStmt.setInt(1, projectId);
            deleteChildErrorPrepStmt.executeUpdate();
            
            PreparedStatement deleteChildPrepStmt =  task.prepareStatement(dbTask,SQLQueries.DEL_C_CHILD);
            deleteChildPrepStmt.setInt(1, projectId);
            deleteChildPrepStmt.executeUpdate();
            
            PreparedStatement updateBatchCreditPrepStmt =  con.prepareStatement(SQLQueries.UPD_BATCHCREDIT);
            updateBatchCreditPrepStmt.setInt(1, projectId);
            updateBatchCreditPrepStmt.executeUpdate();
            
            PreparedStatement deleteBatchErrorPrepStmt =  con.prepareStatement(SQLQueries.DEL_BE);
            deleteBatchErrorPrepStmt.setInt(1, projectId);
            deleteBatchErrorPrepStmt.executeUpdate();

            PreparedStatement deleteBatchUserPrepStmt =  con.prepareStatement(SQLQueries.DEL_BU);
            deleteBatchUserPrepStmt.setInt(1, projectId);
            deleteBatchUserPrepStmt.executeUpdate();
            
            PreparedStatement deleteTeamsQueuePrepStmt =  task.prepareStatement(dbTask,SQLQueries.DEL_TQ);
            deleteTeamsQueuePrepStmt.setInt(1, projectId);
            deleteTeamsQueuePrepStmt.executeUpdate();
            
            PreparedStatement deleteUsersQueuePrepStmt =  task.prepareStatement(dbTask,SQLQueries.DEL_UQ);
            deleteUsersQueuePrepStmt.setInt(1, projectId);
            deleteUsersQueuePrepStmt.executeUpdate();
            
            PreparedStatement deleteBatchPrepStmt =  task.prepareStatement(dbTask,SQLQueries.DEL_B);
            deleteBatchPrepStmt.setInt(1, projectId);
            deleteBatchPrepStmt.executeUpdate();
            
            PreparedStatement deleteRangePrepStmt =  con.prepareStatement(SQLQueries.DEL_R);
            deleteRangePrepStmt.setInt(1, projectId);
            deleteRangePrepStmt.executeUpdate();
            
            PreparedStatement deleteTeamsVolumePrepStmt =  task.prepareStatement(dbTask,SQLQueries.DEL_TV);
            deleteTeamsVolumePrepStmt.setInt(1, projectId);
            deleteTeamsVolumePrepStmt.executeUpdate();
            
            PreparedStatement deleteVolumeErrorPrepStmt =  con.prepareStatement(SQLQueries.DEL_VE);
            deleteVolumeErrorPrepStmt.setInt(1, projectId);
            deleteVolumeErrorPrepStmt.executeUpdate();
            
            PreparedStatement deleteVolumePrepStmt =  task.prepareStatement(dbTask,SQLQueries.DEL_VOL);
            deleteVolumePrepStmt.setInt(1, projectId);
            deleteVolumePrepStmt.executeUpdate();
            
            PreparedStatement deleteProjFieldsPrepStmt =  task.prepareStatement(dbTask,SQLQueries.DEL_PROJ_FIELDS);
            deleteProjFieldsPrepStmt.setInt(1, projectId);
            deleteProjFieldsPrepStmt.executeUpdate();
            
            PreparedStatement deleteTeamsValuePrepStmt =  task.prepareStatement(dbTask,SQLQueries.DEL_TEAMSVALUE);
            deleteTeamsValuePrepStmt.setInt(1, projectId);
            deleteTeamsValuePrepStmt.executeUpdate();
            
            PreparedStatement deleteTableSpecPrepStmt =  task.prepareStatement(dbTask,SQLQueries.DEL_TABLESPEC);
            deleteTableSpecPrepStmt.setInt(1, projectId);
            deleteTableSpecPrepStmt.executeUpdate();
            
            PreparedStatement updateProjectPrepStmt =  task.prepareStatement(dbTask,SQLQueries.UPD_PROJ_ACTIVE);
            updateProjectPrepStmt.setInt(1, projectId);
            updateProjectPrepStmt.executeUpdate();
            
            //take project history
            ProjectHistoryData projectData = new ProjectHistoryData(con, projectId);
            projectData.insertIntoHistoryTable(con,task.getUsersId(),Mode.DELETE);
            
        } catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while deleting the project.", sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while deleting the project.", exc);
            return null;
        }
       return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
