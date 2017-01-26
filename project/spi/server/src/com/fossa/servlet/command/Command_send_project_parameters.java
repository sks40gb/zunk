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
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This class handles the requests for project parameters required in QA sampling.
 * @author anurag
 */
public class Command_send_project_parameters implements Command
{
   private int volume_id = 0;
   private int project_id = 0;
   private int lot_size = 0;
   private int sample_size = 0;
   private String inspectonType = null;
   private String samplingType = null;
   private String samplingMethod = null;
   private String accuracy = null;
   private String lot_size_value = null;
   private String aql = null;
   private String code_letter = null;
   private String table_name = null;
   private int accept_number = 0;
   private int reject_number = 0;
   private int error_count = 0;  
   private Connection con;
   private Statement st;
   private Statement getChildErrorStatement;   
   ResultSet getQALevelResultSet = null;
   PreparedStatement getQALevelPStatement = null;
   PreparedStatement getLotBatchSizePStatement = null;
   PreparedStatement getcodeLetterPStatement = null;
   PreparedStatement getSampleSizePStatement = null;
   PreparedStatement getChildIdPStatement = null;
   ResultSet getLotBatchSizeResultSet = null;
   ResultSet getcodeLetterResultSet = null;
   ResultSet getSampleSizeResultSet = null;
   ResultSet getChildIdResultSet = null;
      
   public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer)
   {
      try {
         
           con = dbTask.getConnection();
           st = dbTask.getStatement();           
           st = con.createStatement();
           Date date = new Date();
           long time = date.getTime();
           Timestamp timestamp = new Timestamp(time);
           
           volume_id = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
           project_id = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
           lot_size = Integer.parseInt(action.getAttribute(A_LOT_SIZE));
           inspectonType = action.getAttribute(A_INSPECTION_TYPE);
           aql = action.getAttribute(A_AQL_VALUE);
           samplingType = action.getAttribute(A_SAMPLING_TYPE);
           samplingMethod = action.getAttribute(A_SAMPLING_METHOD);
           accuracy = action.getAttribute(A_ACCURACY);          
           if(accuracy.equals("")){
              accuracy = "0";
           }
          
           
           getQALevelPStatement = con.prepareStatement(SQLQueries.SEL_PARAM_QALEVEL);
           getQALevelPStatement.setInt(1, project_id);
           getQALevelPStatement.setInt(2, volume_id);
           getQALevelResultSet = getQALevelPStatement.executeQuery();           

           int qa_level_value  = 0;
           if(getQALevelResultSet.next()){                 
                String qa_level = getQALevelResultSet.getString(1);
                qa_level_value = Integer.parseInt(qa_level.substring(2)) + 1;                
           }else{              
              qa_level_value = 1;
           }          
           
           if(inspectonType.equalsIgnoreCase("Normal")){
              table_name = "single_normal_sampling_master";
           }
           else if(inspectonType.equalsIgnoreCase("Tightened")){
              table_name = "single_tightened_sampling_master";
           }
           else if(inspectonType.equalsIgnoreCase("Reduced")){
              table_name = "single_reduced_sampling_master";
           }
           st.close();                                
           
           //Fetch all lot_batch_size from sampling_codeletters table
           st = con.createStatement();
           
           getLotBatchSizePStatement = con.prepareStatement(SQLQueries.SEL_LOT_BATCH_SIZE);
           getLotBatchSizeResultSet = getLotBatchSizePStatement.executeQuery();
           
           while(getLotBatchSizeResultSet.next()){
              lot_size_value = getLotBatchSizeResultSet.getString(1);
              if(lot_size_value.contains("and")){
                 String[] lot_size_last_value = lot_size_value.split("and");
                 if(lot_size >= Integer.parseInt(lot_size_last_value[0].trim())){
                     break;
                 }
              }else{
                 String[] lot_size_array = lot_size_value.split("to");
                 if(lot_size >= Integer.parseInt(lot_size_array[0].trim()) && lot_size <= Integer.parseInt(lot_size_array[1].trim())){
                     break;
                 }     
              }
           }
           st.close();           
                      
           st = con.createStatement();

           //Geting last inserted QALevel 
           getcodeLetterPStatement = con.prepareStatement(SQLQueries.SEL_CODE_LETTER);
           getcodeLetterPStatement.setString(1, lot_size_value);
           getcodeLetterResultSet = getcodeLetterPStatement.executeQuery();
          
           if(getcodeLetterResultSet.next()){
             code_letter = getcodeLetterResultSet.getString(1); 

           }
           st.close();
           
           st = con.createStatement();
           //Get the sample size,accept and reject number
           String selectQuery = "select sample_size,Accept,Reject from "+ table_name +" " +
                                 "where CodeLetters = '" + code_letter + "'and AQL = '"+ aql+"'";             
           ResultSet getSampleSizeResultSet = st.executeQuery(selectQuery);
           if(getSampleSizeResultSet.next()){
               sample_size = getSampleSizeResultSet.getInt(1);
               accept_number = getSampleSizeResultSet.getInt(2);
               reject_number = getSampleSizeResultSet.getInt(3);              
           }
           
           st.close();
           
           //insert a new row into sampling table                     
           user.executeUpdate(dbTask, "insert into sampling(project_id,volume_id,samplingMethod,samplingType," +
                                       "inspectionLevel,AQL,accuracyRequired,start_time,QALevel,accept_number,reject_number) " +
                                       "values ("+project_id+","+volume_id+",'"+samplingMethod+"','"+samplingType+"','"+
                                       inspectonType+"','"+aql+"',"+Double.parseDouble(accuracy)+",'"+timestamp+"','" +
                                       "QA"+qa_level_value+"',"+accept_number+","+reject_number+")");
          
           //calculate the total no. of errors for the given volume           
           st = con.createStatement();
           getChildErrorStatement = con.createStatement();
           Map child_error_map = new HashMap(); //Holds the childId and error count
           
           ResultSet getChildErrorResultSet = getChildErrorStatement.executeQuery(SQLQueries.SEL_DISTINCT_CHILD_ID);
           while(getChildErrorResultSet.next()){
                 child_error_map.put(getChildErrorResultSet.getInt(1), getChildErrorResultSet.getInt(2));                 
           }           
           getChildErrorStatement.close();
           
           
           getChildIdPStatement = con.prepareStatement(SQLQueries.SEL_DIST_CHILD_ID);
           getChildIdPStatement.setInt(1, volume_id);
           getChildIdResultSet = getChildIdPStatement.executeQuery();
           while(getChildIdResultSet.next()){
              int childId = getChildIdResultSet.getInt(1);
              if(child_error_map.containsKey(childId)){
                 error_count += (Integer)child_error_map.get(childId);
              }
           }
           st.close();
           //Start writing the XML
           String userSessionId = user.getFossaSessionId();
           writer.startElement(T_SEND_PROJECT_PARAMETERS);
           writer.writeAttribute(A_FOSSAID, userSessionId);
           writer.writeAttribute(A_SAMPLE_SIZE, sample_size);
           writer.writeAttribute(A_ACCEPT_NUMBER, accept_number);
           writer.writeAttribute(A_REJECT_NUMBER, reject_number);
           writer.writeAttribute(A_ERROR_COUNT, error_count);           
           writer.endElement();
           
      } catch (SQLException sqlexc) {
         CommonLogger.printExceptions(this, "SQLException while sending the project parameters." , sqlexc);
      }catch (IOException exc) {
         CommonLogger.printExceptions(this, "Exception while sending the project parameters." , exc);
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
