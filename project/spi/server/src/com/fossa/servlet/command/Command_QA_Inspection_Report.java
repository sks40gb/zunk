/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonConstants;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.QAInspectionReportData;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import org.w3c.dom.Element;

/**
 * Command Class to Get QAInspectionReport Details for a given Project and Volume
 * @author Prakasha
 */
public class Command_QA_Inspection_Report implements Command {

    ResultSet samplingDetailsResultSet = null;
    PreparedStatement samplingDetailsStatement = null;
    Date date = new Date();
    long time = date.getTime();
    Timestamp timestamp = new Timestamp(time);
    Connection connection = null;
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        
        //TODO:Number Format Exception should be catched for below two lines
        PreparedStatement updateBatchQAStatusStatement = null;
        PreparedStatement updateBatchStatusStatement = null;
        int projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
        int volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        connection = dbTask.getConnection();
        int rejectionNumber = 0;
        String samplingMethod = "";
        String samplingType = "";
        float accuracyRequired = 0;
        String QALevel = "";
        float AQL = 0l;
        int correctionDataCount = 0;
        String projectName = "";
        String volumeName = "";
        int documentCount = 0;
        int fieldCount = 0;
        int tagCount = 0;
        int sampledDocumentCount = 0;
        int sampledFieldCount = 0;
        int sampledTagCount = 0;
        int numberOfDocWithError = 0;
        int numberOfFieldWithError = 0;
        int numberOfTagWithError = 0;
        int numberOfMisCodedData = 0;
        int numberOfUnCodedData = 0;
        int numberOfAddedData = 0;
        String samplingResult = "";
        int samplingId = 0;
        int totalCharCount = 0;
        String sampledCharCount = "";
        
        //Retriving all required data for QAInspection Report
        try {
            //Get Sampling Details
            samplingDetailsStatement = connection.prepareStatement(SQLQueries.SEL_QA_PR_SAMPLING_DETAILS);
            samplingDetailsStatement.setInt(1, volumeId);
            samplingDetailsResultSet = samplingDetailsStatement.executeQuery();
            
            if(samplingDetailsResultSet.next()) {
                samplingId = samplingDetailsResultSet.getInt(1);
                rejectionNumber = samplingDetailsResultSet.getInt(2);
                
                if(null != samplingDetailsResultSet.getString(3)) {
                    samplingMethod = samplingDetailsResultSet.getString(3);
                }else {//NO value reset to default
                    samplingMethod = "";
                }
                
                if(null != samplingDetailsResultSet.getString(4)) {
                    samplingType = samplingDetailsResultSet.getString(4);
                }else {//NO value reset to default
                    samplingType = "";
                }
                
                accuracyRequired = samplingDetailsResultSet.getFloat(5);
                
                if(null != samplingDetailsResultSet.getString(6)) {
                    QALevel = samplingDetailsResultSet.getString(6);
                }else {//NO value reset to default
                    QALevel = "";
                }
                
                AQL = samplingDetailsResultSet.getFloat(7);
            }
            samplingDetailsResultSet.close();
            
            //Get Project Details
            samplingDetailsResultSet = getQueryResult(SQLQueries.SEL_QA_PR_PROJECTS_DETAILS,projectId,volumeId);

            if(samplingDetailsResultSet.next()) {
                if(null != samplingDetailsResultSet.getString(1)) {
                    projectName = samplingDetailsResultSet.getString(1);
                }
                
                if(null != samplingDetailsResultSet.getString(2)) {
                    volumeName = samplingDetailsResultSet.getString(2);
                }
            }
            samplingDetailsResultSet.close();
            
            //Get Correction Data for a given Volume and Sampling
            correctionDataCount = getCountQueryResult(SQLQueries.SEL_QA_PR_CORRECTION_DATA_COUNT,volumeId,samplingId);
            
            //Get Total Number of Document for a given Volume
            documentCount = getCountQueryResult(SQLQueries.SEL_QA_PR_DOCUMENT_COUNT,volumeId);
            
            //Get Total Number of Field Count for a Given Project
            fieldCount = getCountQueryResult(SQLQueries.SEL_QA_PR_FIELD_COUNT,projectId);
            
            //Get Total Number of Tag Count for a Given Project
            tagCount = getCountQueryResult(SQLQueries.SEL_QA_PR_TAG_COUNT,volumeId);
            
            //Get Total Number of Sampled Document for a given Volume and Sampling
            sampledDocumentCount = getCountQueryResult(SQLQueries.SEL_QA_PR_SAMPLED_DOCUMENT_COUNT,volumeId,samplingId);
            
            //Get Total Number of Sampled Tag for a given Volume and Sampling
            sampledTagCount = getCountQueryResult(SQLQueries.SEL_QA_PR_SAMPLED_TAG_COUNT,volumeId,samplingId);
            
            //Get Total Number of Sampled Document with Error for a given Volume and Sampling
            numberOfDocWithError = getCountQueryResult(SQLQueries.SEL_QA_PR_NUMBER_OF_DOC_WITH_ERROR,volumeId,samplingId);
            
            //Get Total Number of Fields with Error for a given Volume and Sampling
            numberOfFieldWithError = getCountQueryResult(SQLQueries.SEL_QA_PR_NUMBER_OF_FIELDS_WITH_ERROR,volumeId,samplingId);
            
            //Get Total Number of Tag with Error for a given Volume and Sampling
            numberOfTagWithError = getCountQueryResult(SQLQueries.SEL_QA_PR_NUMBER_OF_TAG_WITH_ERROR,volumeId,samplingId);
            
            //Get Total Number of Miscoded
            numberOfMisCodedData = getCountQueryResult(SQLQueries.SEL_QA_PR_NUMBER_OF_MISCODED,
                    CommonConstants.ERROR_TYPE_MISCODED,volumeId,samplingId);
            
            //Get Total Number of UnCoded
            numberOfUnCodedData = getCountQueryResult(SQLQueries.SEL_QA_PR_NUMBER_OF_UNCODED,
                    CommonConstants.ERROR_TYPE_UNCODED,volumeId,samplingId);
            
            //Get Total Number of Added
            numberOfAddedData = getCountQueryResult(SQLQueries.SEL_QA_PR_NUMBER_OF_ADDED,
                    CommonConstants.ERROR_TYPE_ADDED, volumeId,samplingId);
            
            //Get Total Number of Sampled Field for a given Volume and Sampling
            sampledFieldCount = sampledDocumentCount * fieldCount;
            
            //Get Total character count 
            totalCharCount = getCountQueryResult(SQLQueries.SEL_CHAR_COUNT_FOR_VOLUME, volumeId);
            
            //Get Total Number of Sampled Characters for a given Volume and Sampling
            sampledCharCount =getSampledCharCount(SQLQueries.SEL_QA_SAMPLED_CHAR_COUNT,samplingId);
            
            //Setting up all retrieved report in a QAInspectionReportData Object
            QAInspectionReportData qaInspectionReportData = new QAInspectionReportData();
            qaInspectionReportData.rejectionNo = rejectionNumber;
            qaInspectionReportData.QALevel = QALevel;
            qaInspectionReportData.samplingMethod = samplingMethod;
            qaInspectionReportData.accuracyRequired = Float.toString(accuracyRequired);
            qaInspectionReportData.AQL_Value = Float.toString(AQL);
            qaInspectionReportData.samplingType = samplingType;
            qaInspectionReportData.documentCount = documentCount;
            qaInspectionReportData.fieldCount = fieldCount;
            if(samplingType.equals(CommonConstants.QA_SAMPLING_CHARACTER)){
                qaInspectionReportData.tagCount = totalCharCount;
                qaInspectionReportData.sampledTagCount = sampledCharCount;
            }else{
                qaInspectionReportData.tagCount = tagCount;
                qaInspectionReportData.sampledTagCount = Integer.toString(sampledTagCount);
            }            
            qaInspectionReportData.sampledDocumentCount = sampledDocumentCount;
            qaInspectionReportData.sampledFieldCount = sampledFieldCount;            
            qaInspectionReportData.numberOfAdded = numberOfAddedData;
            qaInspectionReportData.numberOfDocWithError = numberOfDocWithError;
            qaInspectionReportData.numberOfFieldWithError = numberOfFieldWithError;
            qaInspectionReportData.numberOfMisCoded = numberOfMisCodedData;
            qaInspectionReportData.numberOfTagWithError = numberOfTagWithError;
            qaInspectionReportData.numberOfUnCoded = numberOfUnCodedData;
            qaInspectionReportData.correctionDataCount = correctionDataCount;
            qaInspectionReportData.volumeName = volumeName;
            qaInspectionReportData.projectName = projectName;
            qaInspectionReportData.samplingId = samplingId;
            
            if(samplingId > 0) { //valid samplingId
               
                if(numberOfFieldWithError >= rejectionNumber) {
                    updateBatchStatusStatement = connection.prepareStatement(SQLQueries.UPD_QA_PR_BATCH_STATUS);
                    updateBatchStatusStatement.setString(1, CommonConstants.PROCESS_CODING_QC);
                    updateBatchStatusStatement.setInt(2, volumeId);
                    updateBatchStatusStatement.setString(3, "QA");
                    updateBatchStatusStatement.executeUpdate();
                    updateBatchStatusStatement.close();
                    samplingResult = CommonConstants.SAMPLING_RESULT_REJECT;
                }else {//Sampling is Accept
                    //update all the batch with status as 'QAComplete'
                    updateBatchStatusStatement = connection.prepareStatement(SQLQueries.UPD_QA_PR_BATCH_STATUS_COMPLETE);
                    updateBatchStatusStatement.setString(1, CommonConstants.PROCESS_QA_COMPLETE);
                    updateBatchStatusStatement.setInt(2, samplingId);
                    updateBatchStatusStatement.executeUpdate();
                    updateBatchStatusStatement.close();
                     //update batch with Error, with the status as 'QAReject'
                    updateBatchQAStatusStatement = connection.prepareStatement(SQLQueries.UPD_QA_PR_BATCH_QASTATUS);
                    updateBatchQAStatusStatement.setString(1,CommonConstants.PROCESS_QA_ERROR);
                    updateBatchQAStatusStatement.setInt(2, samplingId);
                    updateBatchQAStatusStatement.executeUpdate();
                    updateBatchQAStatusStatement.close();
                    samplingResult = CommonConstants.SAMPLING_RESULT_ACCEPT;
                }
            }else {
                //Invalid samplingId
                logger.info("Invalid Sampling Id");
            }
            
            //writing QAInspectionReportData in xml
            writer.startElement(T_QA_SAMPLING_REPORT);
            String userSessionId = user.getFossaSessionId();
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.encode(QAInspectionReportData.class, qaInspectionReportData);
            writer.endElement();
        }catch(SQLException exception) {
            String sqlState = exception.getSQLState();
            int errorCode = exception.getErrorCode();
            Log.print(">>>"+exception+" sqlState="+sqlState+" errorCode="+errorCode);
            CommonLogger.printExceptions(this,"SQLException caught while obtaining data for QAIR", exception);
            exception.printStackTrace();
        }catch(IOException exception) {
            CommonLogger.printExceptions(this,"IOException caught while obtaining data for QAIR", exception);
            exception.printStackTrace();
        }
        
        return null;
    }
    
    public boolean isReadOnly() {
        return true;
    }
    /**
     * Method will execute given query and return the ResultSet in samplingDetailsResultSet
     *
     * @param query - SQL query
     * @param int - query param1
     * @param int - query param2
     * @return samplingDetailsResultSet
     */
    private ResultSet getQueryResult(String query,int param1, int param2) {
        try{
            samplingDetailsStatement = connection.prepareStatement(query);
            samplingDetailsStatement.setInt(1, param1);
            samplingDetailsStatement.setInt(2, param2);
            samplingDetailsResultSet = samplingDetailsStatement.executeQuery();
        }catch(SQLException exception) {
            exception.printStackTrace();
            String sqlState = exception.getSQLState();
            int errorCode = exception.getErrorCode();
            Log.print(">>>"+exception+" sqlState="+sqlState+" errorCode="+errorCode);
            CommonLogger.printExceptions(this,"SQLException caught while executing the query", exception);
        }
        
        return samplingDetailsResultSet;
    }
    
    /**
     * Method to get single int value form a given query,
     * This method is used only if the query returns single integer value
     *
     * @param query - SQL query
     * @param int - query param1
     * @param int - query param1
     * @return count
     */
    private int getCountQueryResult(String query,int param1, int param2) {
        int count = 0;
        try{
            samplingDetailsStatement = connection.prepareStatement(query);
            samplingDetailsStatement.setInt(1, param1);
            samplingDetailsStatement.setInt(2, param2);
            samplingDetailsResultSet = samplingDetailsStatement.executeQuery();
            if(samplingDetailsResultSet.next()) {
                count = samplingDetailsResultSet.getInt(1);
            }
                samplingDetailsResultSet.close();
        }catch(SQLException exception) {
            String sqlState = exception.getSQLState();
            int errorCode = exception.getErrorCode();
            Log.print(">>>"+exception+" sqlState="+sqlState+" errorCode="+errorCode);
            CommonLogger.printExceptions(this,"SQLException caught while obtaining the count", exception);
            exception.printStackTrace();
        }
        return count;
    }
    
    /**
     * Method to get single int value form a given query,
     * This method is used only if the query returns single integer value
     * @param query - SQL query
     * @param String - query param1
     * @param int - query param2
     * @param int - query param3
     * @return count
     */
    private int getCountQueryResult(String query,String param1, int param2, int param3) {
        int count = 0;
        try{
            samplingDetailsStatement = connection.prepareStatement(query);
            samplingDetailsStatement.setString(1, param1);
            samplingDetailsStatement.setInt(2, param2);
            samplingDetailsStatement.setInt(3, param3);
            samplingDetailsResultSet = samplingDetailsStatement.executeQuery();
            if(samplingDetailsResultSet.next()) {
                count = samplingDetailsResultSet.getInt(1);
            }
                samplingDetailsResultSet.close();
        }catch(SQLException exception) {
            String sqlState = exception.getSQLState();
            int errorCode = exception.getErrorCode();
            Log.print(">>>"+exception+" sqlState="+sqlState+" errorCode="+errorCode);
            CommonLogger.printExceptions(this,"SQLException caught while obtaining the count", exception);
            exception.printStackTrace();
        }
        return count;
    }
    
    /**
     * Method to get single int value form a given query,
     * This method is used only if the query returns single integer value
     * @param query - SQL query
     * @param int  - query param1
     * @return count
     */
    private int getCountQueryResult(String query,int param1) {
        int count = 0;
        try{
            samplingDetailsStatement = connection.prepareStatement(query);
            samplingDetailsStatement.setInt(1, param1);
            samplingDetailsResultSet = samplingDetailsStatement.executeQuery();
            if(samplingDetailsResultSet.next()) {
                count = samplingDetailsResultSet.getInt(1);
            }
                samplingDetailsResultSet.close();
        }catch(SQLException exception) {
            String sqlState = exception.getSQLState();
            int errorCode = exception.getErrorCode();
            Log.print(">>>"+exception+" sqlState="+sqlState+" errorCode="+errorCode);
            CommonLogger.printExceptions(this,"SQLException caught while obtaining the count", exception);
            exception.printStackTrace();
        }
        return count;
    }
    
    //Returns total number of sampled chararcters 
    private String getSampledCharCount(String query,int param1){
        String charCount = "";
        try{
            samplingDetailsStatement = connection.prepareStatement(query);
            samplingDetailsStatement.setInt(1, param1);
            samplingDetailsResultSet = samplingDetailsStatement.executeQuery();
            if(samplingDetailsResultSet.next()) {
                charCount = samplingDetailsResultSet.getString(1);
            }
                samplingDetailsResultSet.close();
        }catch(SQLException exception) {
            String sqlState = exception.getSQLState();
            int errorCode = exception.getErrorCode();
            Log.print(">>>"+exception+" sqlState="+sqlState+" errorCode="+errorCode);
            CommonLogger.printExceptions(this,"SQLException caught while obtaining the count", exception);
            exception.printStackTrace();
        }
        return charCount;
    }

}
