/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import org.w3c.dom.Element;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.session.UserTask; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * This class handles the requests for Post Validations Reports
 * @author sunil
 */ 

public class Command_request_post_validation_report implements Command {

      private PreparedStatement getPVRPrepStmt;            
      private ResultSet getPVRResultSet = null;            
      
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
       try{
            int volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            int projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID)); 
            Connection con = dbTask.getConnection();            
            String sqlQuery = null;
            // Get record by project id and volume id
            if(projectId != -1 && volumeId != -1){
                sqlQuery= SQLQueries.SEL_PVR_BY_VOLUME;
                getPVRPrepStmt = con.prepareStatement(sqlQuery);
                getPVRPrepStmt.setInt(1,projectId);
                getPVRPrepStmt.setInt(2,volumeId);
            }
            // Get record by project id
            else if(projectId != -1){
                sqlQuery= SQLQueries.SEL_PVR_BY_PROJECT;
                getPVRPrepStmt = con.prepareStatement(sqlQuery);
                getPVRPrepStmt.setInt(1,projectId);
            }
            // Get all record
            else{
                sqlQuery= sqlQuery= SQLQueries.SEL_ALL_PVR;
                getPVRPrepStmt = con.prepareStatement(sqlQuery);
            }            
            getPVRPrepStmt.executeQuery();
            getPVRResultSet = getPVRPrepStmt.getResultSet();           
            Command_sql_query.writeXmlFromResult(user, getPVRResultSet,writer);
            getPVRResultSet.close();            
       }catch(Exception exc){
            CommonLogger.printExceptions(this, "Exception while requesting for the post validation reports." ,exc);            
       }
       return null;
    }
    
    public boolean isReadOnly() {
        return true;
    }

}

