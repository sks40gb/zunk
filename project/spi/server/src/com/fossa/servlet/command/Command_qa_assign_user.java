/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonConstants;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.EventLog;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This Class Assigns the Selected QASampledGroup to a Current User
 *
 * @author Prakasha
 */
public class Command_qa_assign_user implements Command{

    public synchronized String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        
        PreparedStatement getSamplingTypeStatement = null;
        PreparedStatement checkForQAGroupAvailabilityPStatement = null;
        PreparedStatement userIdPStatement = null;
        PreparedStatement sampledGroupIdPStatement = null;
        PreparedStatement getNumberOfErrorInVolumePStatement = null;
        ResultSet checkForQAGroupAvailabilityResultSet = null;
        ResultSet userIdResultSet = null;
        ResultSet sampledGroupIdResultSet = null;
        ResultSet getNumberOfErrorInVolumeResultSet = null;
        
        int usersId = 0;
        int sampleGroupId = 0;
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);
        int numberOfErrorInVolume = 0;
        Connection connection = null;
        ResultSet resultSet = null;
        String samplingType = null;
        String userName = action.getAttribute(A_USER_NAME);
        int volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        int samplingId = 0;
        int rejectNumber = 0;
        String volumeStatus = "";
        
        //TODO: Cyrus:
        //If there is a NumberFormatException then we need to send a error message back.       
        int groupNumber = Integer.parseInt(action.getAttribute(A_GROUP_NUMBER));
        connection = dbTask.getConnection();
        //Get Sampling Type for Given QASampledGroup
        try {
           
            getSamplingTypeStatement = connection.prepareStatement(SQLQueries.SEL_SAMPLING_TYPE);
            getSamplingTypeStatement.setInt(1, volumeId);
            resultSet = getSamplingTypeStatement.executeQuery();
            if(resultSet.next()) {
                samplingType = resultSet.getString(1);
                samplingId = resultSet.getInt(2);
                rejectNumber = resultSet.getInt(3);
            }
            
            //get total number of errors for a given volume
            getNumberOfErrorInVolumePStatement = connection.prepareStatement(SQLQueries.SEL_QA_PR_NUMBER_OF_FIELDS_WITH_ERROR);
            getNumberOfErrorInVolumePStatement.setInt(1, volumeId);
            getNumberOfErrorInVolumePStatement.setInt(2, samplingId);
            getNumberOfErrorInVolumeResultSet = getNumberOfErrorInVolumePStatement.executeQuery();

            if(null != getNumberOfErrorInVolumeResultSet && getNumberOfErrorInVolumeResultSet.next()) {
                numberOfErrorInVolume = getNumberOfErrorInVolumeResultSet.getInt(1);
            }
            
            //get the assigned user for given group
            checkForQAGroupAvailabilityPStatement = connection.prepareStatement(SQLQueries.SEL_QA_GROUP_ASSIGNMENT);
            checkForQAGroupAvailabilityPStatement.setInt(1, groupNumber);
            checkForQAGroupAvailabilityPStatement.setInt(2, volumeId);
            checkForQAGroupAvailabilityPStatement.setInt(3, samplingId);
            checkForQAGroupAvailabilityPStatement.setInt(4, samplingId);
            checkForQAGroupAvailabilityResultSet = checkForQAGroupAvailabilityPStatement.executeQuery();

            boolean assignUser = false;
            boolean writeToXML = true;
            
            //Check for rejection of volume
            if(numberOfErrorInVolume >= rejectNumber) {//volume rejected, if number of error in volume is greater >= rejectionNumber
                volumeStatus = CommonConstants.SAMPLING_RESULT_REJECT;
            }else {//volume accepted
                 volumeStatus = CommonConstants.SAMPLING_RESULT_ACCEPT;
            }   
            
            //Get valid and right user for group
            if(checkForQAGroupAvailabilityResultSet.next() && CommonConstants.SAMPLING_RESULT_ACCEPT.equals(volumeStatus)) {
                int userId = checkForQAGroupAvailabilityResultSet.getInt(1);
                if(userId == 0) {//group is not assigned, Assigned to current user
                    assignUser = true;
                }else if(userId != user.getUsersId()) {
                    writer.startElement(T_QA_PR_ASSIGN_USER);
                    String userSessionId = user.getFossaSessionId();
                    writer.writeAttribute(A_FOSSAID, userSessionId);
                    writer.writeAttribute(A_SAMPLING_TYPE, samplingType);
                    writer.writeAttribute(A_ERROR_MESSAGE, CommonConstants.STATUS_FAIL);
                    writer.endElement();
                    writeToXML = false;
                }
                
            }
            
            //Assign the group to selected user
            if(assignUser){
                userIdPStatement = connection.prepareStatement(SQLQueries.SEL_ASSIGNED_USER);
                userIdPStatement.setString(1, userName);
                userIdResultSet = userIdPStatement.executeQuery();
                
                while(userIdResultSet.next()){
                    usersId =  userIdResultSet.getInt(1);
                }
                
                sampledGroupIdPStatement = connection.prepareStatement(SQLQueries.SEL_DISTINCT_GROUPID);
                sampledGroupIdPStatement.setInt(1, groupNumber);
                sampledGroupIdPStatement.setInt(2, volumeId);
                sampledGroupIdPStatement.setInt(3, samplingId);
                sampledGroupIdResultSet = sampledGroupIdPStatement.executeQuery();
                while(sampledGroupIdResultSet.next()){
                    sampleGroupId = sampledGroupIdResultSet.getInt(1);
                }
                
                user.executeUpdate(dbTask,"update qa_assignment set users_id = "+usersId+", qa_status = 'In Use', start_time = '"+timestamp+"' where qa_sampled_group_id = "+sampleGroupId);
            }
            
            //Open volume for QA
            EventLog.open(user, dbTask, volumeId,CommonConstants.PROCESS_QA);
            if(writeToXML) {
            
               //Start writing the XML
                writer.startElement(T_QA_PR_ASSIGN_USER);
                String userSessionId = user.getFossaSessionId();
                writer.writeAttribute(A_FOSSAID, userSessionId);
                writer.writeAttribute(A_SAMPLING_TYPE, samplingType);
                writer.writeAttribute(A_ERROR_MESSAGE, volumeStatus);
                writer.endElement(); 
            }
            
        } catch (IOException io) {
            CommonLogger.printExceptions(this, "IOException caught during fetching sampling type", io);
        } catch (SQLException se) {
            CommonLogger.printExceptions(this, "SQLException caught during fetching sampling type", se);
        }
        
       return null;
    }

 public boolean isReadOnly() {
        return true;
 }
    
}
