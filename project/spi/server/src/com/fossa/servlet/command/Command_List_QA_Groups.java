/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonConstants;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class returns a list of QA groups for a given Volume.
 * @author Prakasha
 *
 */
public class Command_List_QA_Groups implements Command {

    // Method to return the list of QA groups for a given volume
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        
        PreparedStatement getSamplingDetailsStatement = null;
        PreparedStatement getQALevelStatement = null;
        PreparedStatement getUserNameStatement = null;
        PreparedStatement getSamplingIdStatement = null;
        PreparedStatement getNumberOfErrorInVolumePStatement = null;
        Connection connection = null;
        ResultSet getSamplingIdResultSet = null;
        ResultSet getNumberOfErrorInVolumeResultSet = null;
        int samplingId = 0;
        int rejectNumber = 0;
        int numberOfErrorInVolume = 0;
        int volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        
        //TODO: Cyrus:
        //If there is a NumberFormatException then we need to send a error message back.       

        if(logger.isInfoEnabled()) {
            logger.info("List QA Groups for volume : " + volumeId);
        }
        try {
            connection = dbTask.getConnection();
            getSamplingIdStatement = connection.prepareStatement(SQLQueries.SEL_SAMPLING_ID);
            getSamplingIdStatement.setInt(1, volumeId);
            getSamplingIdResultSet = getSamplingIdStatement.executeQuery();
            
            if(getSamplingIdResultSet.next()) {
                samplingId = getSamplingIdResultSet.getInt(1);
                rejectNumber = getSamplingIdResultSet.getInt(2);
            }
            
            //get total number of errors in given volume
            getNumberOfErrorInVolumePStatement = connection.prepareStatement(SQLQueries.SEL_QA_PR_NUMBER_OF_FIELDS_WITH_ERROR);
            getNumberOfErrorInVolumePStatement.setInt(1, volumeId);
            getNumberOfErrorInVolumePStatement.setInt(2, samplingId);
            getNumberOfErrorInVolumeResultSet = getNumberOfErrorInVolumePStatement.executeQuery();

            if(null != getNumberOfErrorInVolumeResultSet && getNumberOfErrorInVolumeResultSet.next()) {
                numberOfErrorInVolume = getNumberOfErrorInVolumeResultSet.getInt(1);
            }
                 
            //select QA groups for a given Volume
            getSamplingDetailsStatement  = connection.prepareStatement(SQLQueries.SEL_SAMPLING_DETAILS);
            getSamplingDetailsStatement.setInt(1, volumeId);
            getSamplingDetailsStatement.setInt(2, samplingId);
            getSamplingIdResultSet = getSamplingDetailsStatement.executeQuery();
           
            writer.startElement(T_QA_GROUP);
            String userSessionId = user.getFossaSessionId();
            writer.writeAttribute(A_FOSSAID, userSessionId);
            if(rejectNumber <= numberOfErrorInVolume) {
                 writer.writeAttribute(A_ERROR_MESSAGE, CommonConstants.SAMPLING_RESULT_REJECT);
            }else {
                writer.writeAttribute(A_ERROR_MESSAGE, CommonConstants.SAMPLING_RESULT_ACCEPT);
            }
            
            String qaLevel = "";
            String userName = "";
            String status = "";
            int groupId = 0;
            int userId = 0;
            int groupNumber = 0;
            while(getSamplingIdResultSet.next()) {
                groupId = getSamplingIdResultSet.getInt(1);
                groupNumber = getSamplingIdResultSet.getInt(2);
                status = getSamplingIdResultSet.getString(3);
                userId = getSamplingIdResultSet.getInt(4);
                getQALevelStatement  = connection.prepareStatement(SQLQueries.SEL_QA_LEVEL);
                getQALevelStatement.setInt(1, groupId);
                ResultSet getQALevelResultSet = getQALevelStatement.executeQuery();
                
                if(getQALevelResultSet.next()) {
                    qaLevel = getQALevelResultSet.getString(1);
                }else {
                    qaLevel = "";
                }     
                
                getUserNameStatement  = connection.prepareStatement(SQLQueries.SEL_USER_NAME);
                getUserNameStatement.setInt(1, userId);
                ResultSet getUserNameResultSet = getUserNameStatement.executeQuery();                
                if(getUserNameResultSet.next()) {
                   userName = getUserNameResultSet.getString(1);
                }else {
                    userName = "";
                }
                //Start writing the XML
                writer.startElement(T_ROW);
                writer.startElement(T_COLUMN);
                writer.writeContent(groupNumber);
                writer.endElement();
                writer.startElement(T_COLUMN);
                writer.writeContent(qaLevel);
                writer.endElement();
                
                writer.startElement(T_COLUMN);
                writer.writeContent(userName);
                writer.endElement();
                writer.startElement(T_COLUMN);
                writer.writeContent(status);
                writer.endElement();
                writer.startElement(T_COLUMN);
                writer.writeContent(groupId);
                writer.endElement();
                writer.endElement();
                
            }
            writer.endElement();
        } catch (IOException ex) {
            CommonLogger.printExceptions(this, "Exception while fetching the sampling details.", ex);
        } catch (SQLException ex) {
            String sqlState = ex.getSQLState();
            int errorCode = ex.getErrorCode();
            Log.print(">>>"+ex+" sqlState="+sqlState+" errorCode="+errorCode);
            CommonLogger.printExceptions(this, "Exception while fetching the sampling details.", ex);
        }
         return null;
    }


    public boolean isReadOnly() {
        return true;
    }   
}
