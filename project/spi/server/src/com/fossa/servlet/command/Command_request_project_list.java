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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class handles the requests for project list
 * @author ashish
 */
class Command_request_project_list implements Command{
   
    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {  
        Connection con = null;
        Statement st = null;
        ResultSet getProjectResultSet = null;   
        try{
            st = dbTask.getStatement();
            String whichStatus = action.getAttribute(A_STATUS);            //Holds the batch status
            // We suppress writing the log for the temporary table
            // Note.  there's no index, so batches can be inserted twice
            //ToDO: commented as Bala has already done it            
            try {
                task.createTemporaryTable(SQLQueries.CREATE_PRLIST_TABLE,dbTask);
            } catch (SQLException ignore) {
               CommonLogger.printExceptions(this, "Exception while creating temporary table for project list.", ignore);
            }
            
            // We find all projects that are relevant and all batches
            // the user can open for the given status.  We put them in
            // a temporary table and count them.  Note:  We could have
            // replaced the inserts with a great big UNION ALL, but
            // it would be harder to understand, and we need the temporary
            // table, anyway.

            // A project is relevant if it has:
            //    a volume queued to the user's team,
            //    or a batch queued to the user or the user's team
            //    or a batch assigned to the user
            // If a relevant project has no available batches in this
            // status for this user, it will show up with a count of zero.

            // Find batches in usersqueue            
            st.executeUpdate("INSERT INTO #TEMP SELECT V.project_id,  CASE WHEN  B.status = '"+whichStatus+"' " +
                    "THEN Q.batch_id ELSE NULL END  as 'if(B.status = "+whichStatus+", Q.batch_id, NULL)' " +
                    "FROM usersqueue Q inner join batch B on B.batch_id = Q.batch_id inner join volume V " +
                    "on V.volume_id = B.volume_id WHERE Q.users_id="+task.getUsersId());
            
            // Find batches in teamqueue
            if (whichStatus.equals("Unitize") || whichStatus.equals("Coding")) {            	
                st.executeUpdate("INSERT INTO #TEMP SELECT V.project_id, CASE WHEN B.status ='"+whichStatus+"' " +
                        "and UQ.batch_id is null THEN Q.batch_id  ELSE   NULL END  FROM users U inner join teamsqueue Q " +
                        "on Q.teams_id = U.teams_id inner join batch B on B.batch_id = Q.batch_id inner join volume V " +
                        "on V.volume_id = B.volume_id left join usersqueue UQ on UQ.batch_id=Q.batch_id " +
                        "WHERE U.users_id="+task.getUsersId());
            } else { // since whichStatus in (UQC, CodingQC)
                // Note: project but not batch if this user was the original coder            	
                st.executeUpdate("INSERT INTO #TEMP SELECT V.project_id,CASE WHEN (BU.coder_id <> U.users_id and " +
                        "B.status = '"+whichStatus+"' and UQ.batch_id is null) THEN Q.batch_id ELSE NULL END  " +
                        "FROM users U inner join teamsqueue Q on Q.teams_id = U.teams_id     inner join batch B " +
                        "on B.batch_id = Q.batch_id inner join volume V on V.volume_id = B.volume_id left join batchuser BU " +
                        "on BU.batch_id = B.batch_id left join usersqueue UQ on UQ.batch_id=Q.batch_id " +
                        "WHERE U.users_id="+task.getUsersId());
            }
            // Find batches in teamsvolume
            // Done in two statements, because otherwise it gets complex
            int tvCount = st.executeUpdate(SQLQueries.INS_PRLIST_DISTINCT);            
            
            if (tvCount != 0
            && (whichStatus.equals("Unitize") || whichStatus.equals("Coding"))) {                
                PreparedStatement insert_prlist_batchid =  con.prepareStatement(SQLQueries.INS_PRLIST_BATCHID);                
                insert_prlist_batchid.setInt(1, task.getUsersId());
                insert_prlist_batchid.setString(2, whichStatus);
                insert_prlist_batchid.executeUpdate();
                
            }

            // add projects for user's currently-assigned batches, if any
            // batch_id is set to null.  This gives a line
            // (with zero available count) if user's active
            // batch has volume not queued for team
            // we add for all statuses            
            st.executeUpdate(SQLQueries.INS_PRLIST_ASSIGN +task.getUsersId());

            // Finished updating temp table, start logging again
            task.finishedWritingTemporaryTable(dbTask);

            // get projects and batch counts
            // Note.  count(distinct ... ) should give count of non-null batch_id's
            // so projects assigned to team with no batches should have 0
            if(whichStatus.equals("codingManual")){
                 getProjectResultSet = st.executeQuery(SQLQueries.SEL_PRLIST_ACTIVE);                 
            }
            else{
                 getProjectResultSet = st.executeQuery(SQLQueries.SEL_PRLIST_PNAME);            
            }
            CommandFactory factory = CommandFactory.getInstance();
            Command_sql_query sqlCommand = (Command_sql_query)factory.getCommand(T_SQL_QUERY);
            sqlCommand.writeXmlFromResult(task, getProjectResultSet, writer, false);            
            getProjectResultSet.close();
            st.executeUpdate("drop table #temp");            
        }catch (SQLException sql) {
            CommonLogger.printExceptions(this, "Exception while requesting for project list." , sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while requesting for project list." ,exc);
            return null;
        } finally {
            if(null != con) {
               //con.close
            }
        }
    return null;
    }

    public boolean isReadOnly() {
        return false;
    }

}
