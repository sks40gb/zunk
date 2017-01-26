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
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This class returns the sampling parameters.
 * @author anurag
 */
public class Command_get_sampling_parameters implements Command
{
   private int sampling_id = 0;
   private int volume_id = 0;
   private int project_id = 0;   
   private String projectName = null;
   private String volumeName = null;
   private String samplingResult = null;
   private String inspectonType = null;
   private String samplingType = null;
   private String samplingMethod = null;   
   private String aql = null;  
   private String qa_level = null;
   private int accept_number = 0;
   private int reject_number = 0;
   private int error_count = 0;
   private int sampling_document_count = 0;
   private int sampling_field_count = 0;
   private int total_document_count = 0;
   private int total_field_count = 0;
   private int sampling_tag_count = 0;
   private String error_type ="";
   private int uncoded_error =0;
   private int miscoded_error =0;
   private int added_error =0;
   private boolean isSamplingDone = false;
   private Date date = new Date();
   private long time = date.getTime();
   private Timestamp timestamp = new Timestamp(time);  
   private Connection con;
   private Statement st;
   private PreparedStatement pst;
   PreparedStatement getChildIdPStatement = null;
   PreparedStatement getChildIDStatement = null;
   PreparedStatement child_coded_count = null;
   PreparedStatement getFieldCountStatement = null;
   PreparedStatement getTagCountStatement = null;
   PreparedStatement getProjectNameStatement = null;
   PreparedStatement getDistinctChildIdStatement = null;
   PreparedStatement getDistinctFieldNameStatement = null;
   PreparedStatement getVolumeNameStatement = null;
   PreparedStatement getSamplingRecordsStatement = null;
   ResultSet getChildIdResultSet = null;
   ResultSet getChildIDResultSet = null;
   ResultSet getFieldCountResultSet = null;
   ResultSet getTagCountResultSet = null;
   ResultSet getProjectNameResultSet = null;
   ResultSet getVolumeNameResultSet = null;
   ResultSet getDistinctChildIdResultSet = null;
   ResultSet getDistinctFieldNameResultSet = null;
   ResultSet getSamplingRecordsResultSet = null;
      
   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {         
           con = dbTask.getConnection();           
           st = con.createStatement();
           
           volume_id = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
           project_id = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
           
           getChildIdPStatement = con.prepareStatement(SQLQueries.SEL_GET_PARAM_CHILD_ID);
           getChildIdPStatement.setInt(1, volume_id);
           getChildIdResultSet = getChildIdPStatement.executeQuery();  
           
           while(getChildIdResultSet.next()){
                  int sel_child_id = getChildIdResultSet.getInt(1); 
                  
                  getChildIDStatement = con.prepareStatement(SQLQueries.SEL_GET_PARAM_CHILDID);
                  getChildIDStatement.setInt(1, sel_child_id);
                  getChildIDResultSet = getChildIDStatement.executeQuery();
                  
                  while(getChildIDResultSet.next()){
                     int child_coded_count= getChildIDResultSet.getInt(1);
                     
                     getFieldCountStatement = con.prepareStatement(SQLQueries.SEL_FIELD_COUNT);
                     getFieldCountStatement.setInt(1, child_coded_count);
                     getFieldCountResultSet = getFieldCountStatement.executeQuery();
                     
                     while(getFieldCountResultSet.next()){
                        sampling_field_count += getFieldCountResultSet.getInt(1);
                        error_count += getFieldCountResultSet.getInt(2);                                                
                     }
                     
                     getTagCountStatement = con.prepareStatement(SQLQueries.SEL_TAG_COUNT);
                     getTagCountStatement.setInt(1, child_coded_count);
                     getTagCountResultSet = getTagCountStatement.executeQuery();
                     
                     while(getTagCountResultSet.next()){
                        sampling_tag_count += getTagCountResultSet.getInt(1);                           
                        error_type = getTagCountResultSet.getString(2);
                        if(error_type.equals("uncoded")){
                           uncoded_error++;
                        } else if(error_type.equals("miscoded")){
                           miscoded_error++;
                        } else if(error_type.equals("added")){
                           added_error++;
                        }
                     }                     
                     sampling_document_count++;                     
                  }
           }           
           st = con.createStatement();
           
           getSamplingRecordsStatement = con.prepareStatement(SQLQueries.SEL_PARAM_SAMPLING);
           getSamplingRecordsStatement.setInt(1, project_id);
           getSamplingRecordsStatement.setInt(1, volume_id);
           getSamplingRecordsResultSet = getTagCountStatement.executeQuery();
           
           if(getSamplingRecordsResultSet.next()){ 
              int samplingId = getSamplingRecordsResultSet.getInt(1);
              isSamplingDone = false;
              sampling_id = samplingId;
              samplingMethod = getSamplingRecordsResultSet.getString(4);
              samplingType = getSamplingRecordsResultSet.getString(5);
              inspectonType = getSamplingRecordsResultSet.getString(6);
              aql = getSamplingRecordsResultSet.getString(7);
              qa_level = getSamplingRecordsResultSet.getString(9);
              accept_number = getSamplingRecordsResultSet.getInt(14);
              reject_number = getSamplingRecordsResultSet.getInt(15);
                       
              if(error_count > accept_number){
                 samplingResult = CommonConstants.SAMPLING_RESULT_REJECT;
              }else{
                 samplingResult = CommonConstants.SAMPLING_RESULT_ACCEPT;
              }
                      
              //updating the sampling table
              pst = con.prepareStatement(SQLQueries.UPD_END_TIME);           
              pst.setTimestamp(1,timestamp);
              pst.setString(2,CommonConstants.SAMPLING_STATUS_FINISHED);           
              pst.setString(3,samplingResult); 
              pst.setInt(4, samplingId);               
              pst.executeUpdate();
              pst.close();                
           }else{                
              isSamplingDone = true;
           }
        
           getProjectNameStatement = con.prepareStatement(SQLQueries.SEL_PARAM_PROJECT_NAME);
           getProjectNameStatement.setInt(1, project_id);
           getProjectNameResultSet = getProjectNameStatement.executeQuery();
           
           while(getProjectNameResultSet.next()){
              projectName = getProjectNameResultSet.getString(1);
           }
           
           getVolumeNameStatement = con.prepareStatement(SQLQueries.SEL_PARAM_VOLUME_NAME);
           getVolumeNameStatement.setInt(1, volume_id);
           getVolumeNameResultSet = getVolumeNameStatement.executeQuery();
           
           while(getVolumeNameResultSet.next()){
              volumeName = getVolumeNameResultSet.getString(1);
           }
           
           getDistinctChildIdStatement = con.prepareStatement(SQLQueries.SEL_PARAM_DISTINCT_CHILD_ID);
           getDistinctChildIdStatement.setInt(1, volume_id);
           getDistinctChildIdResultSet = getDistinctChildIdStatement.executeQuery();
           
           while(getDistinctChildIdResultSet.next()){
               total_document_count++;
           }
           
           getDistinctFieldNameStatement = con.prepareStatement(SQLQueries.SEL_PARAM_DISTINCT_FIELD_NAME);
           getDistinctFieldNameStatement.setInt(1, project_id);
           getDistinctFieldNameResultSet = getDistinctFieldNameStatement.executeQuery();
           
           while(getDistinctFieldNameResultSet.next()){
               total_field_count++;
           }
           //Start writing the XML          
           String userSessionId = user.getFossaSessionId();
           writer.startElement(TASK_GET_SAMPLING_PARAMETERS);
           writer.writeAttribute(A_FOSSAID, userSessionId);
           writer.writeAttribute(A_PROJECT_NAME,projectName);
           writer.writeAttribute(A_VOLUME_NAME,volumeName);
           writer.writeAttribute(A_SAMPLING_METHOD,samplingMethod);
           writer.writeAttribute(A_SAMPLING_TYPE,samplingType);
           writer.writeAttribute(A_INSPECTION_TYPE,inspectonType);
           writer.writeAttribute(A_AQL_VALUE,aql);           
           writer.writeAttribute(A_REJECT_NUMBER, reject_number);           
           writer.writeAttribute(A_ERROR_COUNT, error_count);           
           writer.writeAttribute(A_TOTAL_DOCS, total_document_count);           
           writer.writeAttribute(A_TOTAL_FIELDS, total_field_count);           
           writer.writeAttribute(A_SAMPLING_DOCS, sampling_document_count);           
           writer.writeAttribute(A_SAMPLING_FIELDS, sampling_field_count);           
           writer.writeAttribute(A_SAMPLING_RESULT, samplingResult);           
           writer.writeAttribute(A_QA_LEVEL,qa_level);           
           writer.writeAttribute(A_SAMPLING_ID,sampling_id);
           writer.writeAttribute(A_IS_SAMPLING_DONE,Boolean.toString(isSamplingDone));
           writer.writeAttribute(A_TAGS_COUNT,sampling_tag_count);
           writer.writeAttribute(A_MISCODED,miscoded_error);
           writer.writeAttribute(A_UNCODED,uncoded_error);
           writer.writeAttribute(A_ADDED,added_error);
           
           writer.endElement();

      } catch (SQLException sqlexc) {
         CommonLogger.printExceptions(this, "SQLException while getting the sampling parameters." , sqlexc);
      }catch (IOException exc) {
         CommonLogger.printExceptions(this, "IOException while getting the sampling parameters." , exc);
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
