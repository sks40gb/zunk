/*
 * Command_sample_qa.java
 *
 * Created on November 21, 2007, 2:55 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import org.w3c.dom.Element;

/**
 * This class handles the requests for various QA process.
 * @author bmurali
 */
public class Command_sample_qa implements Command {
    private Connection con = null;

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        int volumeId = user.getLockVolumeId();
        int projectId = 0;
        String project_name = null;
        String usersIdString = null;
        String teamsIdString = null;
        String sampling_for = null;
        String accuracyRequiredString = null;
        PreparedStatement projectNamePStatement = null;
        PreparedStatement qalevelPStatement = null;
        PreparedStatement insertSamplingPStatement = null;
        PreparedStatement updateBatchPStatement = null;
        PreparedStatement selectUserNamePStatement = null;
        PreparedStatement selectFieldNamePStatement = null;
        ResultSet projectNameResultSet = null;
        ResultSet qalevelResultSet = null;
        ResultSet selectUserNameResultSet = null;
        ResultSet selectFieldNameResultSet = null;
        ResultSet rs = null;
        ResultSet rs1 = null;
        ResultSet rs2 = null;
        assert volumeId != 0;
        assert user.getLockBatchId() == 0;
        int users_id = 0;
        float accuracyRequired = 0;

        int percent = Integer.parseInt(action.getAttribute(A_PERCENT));
        accuracyRequiredString = action.getAttribute(A_ACCURACY_REQUIRED);
        if(accuracyRequiredString.length() > 0){
            accuracyRequired = Float.parseFloat(accuracyRequiredString);
        }
        usersIdString = action.getAttribute(A_USERS_ID);
        teamsIdString = action.getAttribute(A_TEAMS_ID);
        sampling_for = action.getAttribute(A_SAMPLING_FOR);
//        if(usersIdString.length()>0){
//            users_id = Integer.parseInt(usersIdString);
//        }
        String field_name = "";
        String user_name = "";
        boolean newSample = "YES".equals(action.getAttribute(A_NEW_SAMPLE));  //if yes means it is not opened yet otherwise its an old sample
        Log.print("in Command_sample_qa.run vol=" + volumeId + " pct=" + percent + " user: " + usersIdString + " team: " + teamsIdString + " new_sample: " + newSample);

        Statement st = null;
        int count = 0;
        st = dbTask.getStatement();
        con = dbTask.getConnection();
        
        try{
            projectNamePStatement = con.prepareStatement(SQLQueries.SEL_SAMPLE_PROJECT_NAME);
            projectNamePStatement.setInt(1, volumeId);
            projectNameResultSet = projectNamePStatement.executeQuery();
            
            while(projectNameResultSet.next()){
                projectId = projectNameResultSet.getInt(1);
                project_name = projectNameResultSet.getString(2);
            }
           
            qalevelPStatement = con.prepareStatement(SQLQueries.SEL_SAMPLE_QALEVEL);
            qalevelPStatement.setInt(1, projectId);
            qalevelPStatement.setInt(2, volumeId);
            qalevelResultSet = qalevelPStatement.executeQuery();
            
           
           int qa_level_value  = 0;
           if(qalevelResultSet.next()){ 
                String qa_level = qalevelResultSet.getString(1);
                qa_level_value = Integer.parseInt(qa_level.substring(2)) + 1;                
           }else{              
              qa_level_value = 1;
           }
           
            insertSamplingPStatement = con.prepareStatement(SQLQueries.INS_SAMPLE_SAMPLING);
            insertSamplingPStatement.setInt(1, projectId);
            insertSamplingPStatement.setInt(2, volumeId);
            insertSamplingPStatement.setFloat(3, accuracyRequired);
            insertSamplingPStatement.setString(4, "QA"+qa_level_value);
            insertSamplingPStatement.executeUpdate();
                
            // move TComplete batches to QA (managed)    
                
            updateBatchPStatement = con.prepareStatement(SQLQueries.UPD_SAMPLE_BATCH);
            updateBatchPStatement.setInt(1, volumeId);
            updateBatchPStatement.executeUpdate();
            
            
            String storedProcedureString = "{ call SProc_CreateQASamples_for_fixedPercentage(?,?,10,?,?,?,?,?) }";            
            
            /* This procedure picks up various child ids randomnly. Each child id represents a document 
             * and its picked up based on the project field(s) and the coder(s) who has coded 
             * the document. The number of records picked up depends on how much the sample size should be.
             * The proc doesnt have a return statement
             */ 
            CallableStatement cs = con.prepareCall( storedProcedureString );            
            
            cs.setInt(1, projectId);               //project_id
            cs.setInt(2, volumeId);                //volume_id
            cs.setInt(3, percent);         //projectfields
            cs.setString(4, sampling_for);     //sample_size
            cs.setString(5, usersIdString);                //coders
            cs.setString(6, teamsIdString);
            cs.setFloat(7, accuracyRequired);
            cs.execute();            
            cs.close();
            
            selectUserNamePStatement = con.prepareStatement(SQLQueries.SEL_SAMPLE_UNAME);
            selectUserNamePStatement.setInt(1, volumeId);
            selectUserNameResultSet = selectUserNamePStatement.executeQuery();
            
            while(selectUserNameResultSet.next()){
                user_name = selectUserNameResultSet.getString(1);
            }
            
            selectFieldNamePStatement = con.prepareStatement(SQLQueries.SEL_SAMPLE_FIELDNAME);
            selectFieldNamePStatement.setInt(1, projectId);
            selectFieldNameResultSet = selectFieldNamePStatement.executeQuery();
            
            while(selectFieldNameResultSet.next()){
                field_name = field_name+selectFieldNameResultSet.getString(1);
                field_name = field_name + ",";
            }
            
            if(field_name.charAt(field_name.length()-1) == ',') {
                field_name = field_name.substring(0,field_name.length()-1);
            }
            
        } catch (SQLException ex) {
                CommonLogger.printExceptions(this, "SQLException while updating the batch status in QA." , ex);
            }
        try {
            // send back info           
            String userSessionId = user.getFossaSessionId();
            writer.startElement(T_SAMPLE_FIXED_PERCENTAGE);
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.writeAttribute(A_PROJECT_NAME,project_name);
            writer.writeAttribute(A_USER_NAME,user_name);
            writer.writeAttribute(A_FIELD_NAME,field_name);
            writer.endElement();
        } catch (IOException ex) {
              CommonLogger.printExceptions(this, "IOException while writing the XML.", ex);              
        }
        return null;
    }


    public boolean isReadOnly() {
        return false;
    }
}
