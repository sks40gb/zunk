/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class to check whether the logged in user is the same as the assigned user   
 *
 * @author Prakasha
 */

public class Command_tally_assign_user implements Command{

    public synchronized String execute(org.w3c.dom.Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            String isInsertProcess = null;
            String errorStatus = "pass";
            int assignedUserId = 0;
            int userId = 0;
            int tally_group_id = 0;
            int tally_assignment_id = 0;
            PreparedStatement getTallyUserIdStatement = null;
            PreparedStatement insertTallyAssignmentStatement = null;
            PreparedStatement updateTallyAssignmentStatement = null;
            ResultSet getTallyUserIdResultSet = null;
            Connection con = null;
            Statement st = null;
            ResultSet rs = null;

            con = dbTask.getConnection();
            st = dbTask.getStatement();
            isInsertProcess = action.getAttribute(A_INSERT_PROCESS);
            userId = Integer.parseInt(action.getAttribute(A_USERS_ID));
            tally_group_id = Integer.parseInt(action.getAttribute(A_TALLY_GROUP_ID));
            tally_assignment_id = Integer.parseInt(action.getAttribute(A_TALLY_ASSIGNMENT_ID));
            
            getTallyUserIdStatement= con.prepareStatement(SQLQueries.SEL_TALLY_USER_ID);  
            getTallyUserIdStatement.setInt(1, tally_group_id);
            getTallyUserIdResultSet = getTallyUserIdStatement.executeQuery();
            
            if(getTallyUserIdResultSet.next()){
                assignedUserId = getTallyUserIdResultSet.getInt(1);
            }
            
            if(assignedUserId == userId || assignedUserId == 0){
                errorStatus = "fail";
                if(isInsertProcess.equals("true")){
                    insertTallyAssignmentStatement= con.prepareStatement(SQLQueries.INS_TALLY_ASSIGNMENT);  
                    insertTallyAssignmentStatement.setInt(1, userId);
                    insertTallyAssignmentStatement.setInt(2, tally_group_id);
                    insertTallyAssignmentStatement.executeUpdate();
            
                }else{
                    updateTallyAssignmentStatement= con.prepareStatement(SQLQueries.UPD_TALLY_ASSIGNMENT);  
                    updateTallyAssignmentStatement.setInt(1, userId);
                    updateTallyAssignmentStatement.setInt(2, tally_assignment_id);
                    updateTallyAssignmentStatement.executeUpdate();
                    
                }
            
                writer.startElement(T_TALLY_ASSIGN_USER);
                String userSessionId = user.getFossaSessionId();
                writer.writeAttribute(A_FOSSAID, userSessionId);
                writer.writeAttribute(A_ERROR_MESSAGE, errorStatus);
                writer.endElement();
            }else{
                writer.startElement(T_TALLY_ASSIGN_USER);
                String userSessionId = user.getFossaSessionId();
                writer.writeAttribute(A_FOSSAID, userSessionId);
                writer.writeAttribute(A_ERROR_MESSAGE, errorStatus);
                writer.endElement();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Command_tally_assign_user.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ie){
            Logger.getLogger(Command_tally_assign_user.class.getName()).log(Level.SEVERE, null, ie);
        }
        return null;
    }
    public boolean isReadOnly() {
         return true;
    }
}
