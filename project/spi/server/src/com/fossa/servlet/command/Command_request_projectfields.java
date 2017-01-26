/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.ProjectWriter;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class handles the requests for the project fields
 * @author ashish
 */
class Command_request_projectfields implements Command{
    
    private static final String INPUT_VALIDATION = "Input Validation";
    private PreparedStatement pstmt=null;    
    private Connection connection=null;    
    private ResultSet rs=null;    
    private ResultSet documentValidationsResultSet = null;    
    private ResultSetMetaData rsmd=null;    
    private int volumeId = 0;
    private int columnCount = 0;
    
    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {      
        try{
             int projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
             
            loadProjectDescription(task,dbTask,projectId);            
            
            String userSessionId = task.getFossaSessionId();
            writer.startElement(T_PROJECTFIELDS_DATA);
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.startElement(T_VALUE_LIST);
            volumeId = task.getVolumeId();
            if(volumeId == 0){
               volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            }                       
            ProjectWriter.write(task, dbTask, writer, volumeId);
            writeXmlFromResult(task, rs,rsmd,writer);
            writer.endElement();  // end message           
       } catch (IOException exc) {
            CommonLogger.printExceptions(this, "IOException while writing in XML during requesting for project fields.", exc);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while writing in XML during requesting for project fields.", exc);
            return null;
        }     
        return null;
    }
   
    /**
     * Method to Load Project Description for Given Project
     * 
     * @param task
     * @param dbTask
     * @param projectId
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
   private void loadProjectDescription(UserTask task, DBTask dbTask,int projectId) 
    throws SQLException, IOException
    {
      try {            
           connection = dbTask.getConnection();              
           String query  = "SELECT DISTINCT PF.projectfields_id,FM.validation_functions_master_id, FM.function_name" +
                   ",FM.function_body,MD.parameter,MD.error_message,PF.field_name, PF.field_type " +
                   "FROM  validation_mapping_details MD INNER JOIN validation_mapping_master MM " +
                   "ON MD.validation_mapping_master_id = MM.validation_mapping_master_id INNER JOIN " +
                   "validation_functions_master FM ON MM.validation_functions_master_id = FM.validation_functions_master_id  " +
                   "INNER JOIN projectfields PF ON PF.projectfields_id=MD.projectfields_id WHERE PF.project_id = ? " +
                   "AND MD.status = 'true' ORDER BY function_name";
           pstmt = connection.prepareStatement(query);
           pstmt.setInt(1, projectId);        
           pstmt.executeQuery();                   
           rs=pstmt.getResultSet();
           rsmd = rs.getMetaData();
           
           System.out.println("================>>>>>>>>>>>>>>");
           
           query = "SELECT function_name, function_body, error_message, parameter, type " +
                    "FROM validation_functions_master WHERE project_id  = ? " +
                    "AND status = 'true' AND scope = ? AND ( type = '" + INPUT_VALIDATION +"' ) ";
           
           pstmt = connection.prepareStatement(query);
           pstmt.setInt(1, projectId);  
           pstmt.setString(2, "Document");
           pstmt.executeQuery();                   
           documentValidationsResultSet = pstmt.getResultSet();
           
         } catch (SQLException e) {
            CommonLogger.printExceptions(this, "Exception while getting project fields details." , e);
            Log.quit(e);
         }
    }
    
    public boolean isReadOnly() {
        return false;
    }
     
    /**
     * Method to Write XML as result
     * 
     * @param task  //UserTask
     * @param rs    //ResultSet
     * @param rsmd  //ResultSetMetaData
     * @param writer //MessageWriter
     */
    private void writeXmlFromResult(UserTask task, ResultSet rs, ResultSetMetaData rsmd, MessageWriter writer) {
     
       //1. Read the row.
      //2. Get the field_name and assign this to current
      //3. Assign        
      String previous = null;
      String current = null;
      String functionName = null;
      String functionBody = null;
      String parameter = null;
      String errorMessage = null;
      String fieldName = null;
      String fieldType = null;
      try {
         columnCount = rsmd.getColumnCount();
         boolean second = true;
         writer.startElement(T_VALIDATIONS);
         writer.writeAttribute(A_COUNT, "2");
          //read the first row
          if (rs.next()) {
              functionName = rs.getString(3);
              functionBody = rs.getString(4);
              parameter = rs.getString(5);
              errorMessage = rs.getString(6);
              fieldName = rs.getString(7);
              fieldType = rs.getString(8);
              current = fieldName;
              previous = current;
              writeData(fieldName, true, writer);
              writeData(functionName, functionBody, parameter, errorMessage, fieldType, writer);
          } else if(documentValidationsResultSet.next()) {
              functionName = documentValidationsResultSet.getString(1);
              functionBody = documentValidationsResultSet.getString(2);
              errorMessage = documentValidationsResultSet.getString(3);
              parameter = documentValidationsResultSet.getString(4);
              fieldName = "$DOCUMENT VALIDATION FUNCTION$";
              fieldType = "$DOCUMENT VALIDATION FUNCTION$";
              current = fieldName;
              previous = current;
              writeData(fieldName, true, writer);
              writeData(functionName, functionBody, parameter, errorMessage, fieldType, writer);
          }

          if (null != current) {
              while (rs.next()) {
                  functionName = rs.getString(3);
                  functionBody = rs.getString(4);
               parameter = rs.getString(5);
               errorMessage = rs.getString(6);
               fieldName = rs.getString(7);
               fieldType = rs.getString(8);
               current = fieldName;
               // If the previous and current Field is same then write the validation function within the same
               // Field
               if (current.equals(previous)) {
                  writeData(functionName, functionBody, parameter, errorMessage, fieldType, writer);
               }
               else {
                  writeData(previous, false, writer);
                  writeData(current, true, writer);
                  writeData(functionName, functionBody, parameter, errorMessage, fieldType, writer);
                  previous = current;
               }
            }
            
            while(documentValidationsResultSet.next()){
                
               //1.function_name, 
               //2.function_body, 
               //3.error_message, 
               //4.parameter, 
               //5.type                        
                        
               functionName = documentValidationsResultSet.getString(1);
               functionBody = documentValidationsResultSet.getString(2);
               errorMessage = documentValidationsResultSet.getString(3);
               parameter = documentValidationsResultSet.getString(4);
               fieldName = "$DOCUMENT VALIDATION FUNCTION$";
               fieldType = "$DOCUMENT VALIDATION FUNCTION$";
               
                System.out.println("===========>>>  0");
               
               current = fieldName;
               // If the previous and current Field is same then write the validation function within the same
               // Field
               if (current.equals(previous)) {
                  writeData(functionName, functionBody, parameter, errorMessage, fieldType, writer);
               }
               else {
                  writeData(previous, false, writer);
                  writeData(current, true, writer);
                  writeData(functionName, functionBody, parameter, errorMessage, fieldType, writer);
                  previous = current;
               }
            }
            
            writeData(current, false, writer); //close the last field_name tag
         }
         //end the ELements
         writer.endElement();
      } catch (SQLException sql) {
         CommonLogger.printExceptions(this, "SQLException while writing in XML during requesting for project fields.", sql);
      } catch (IOException ex) {
         CommonLogger.printExceptions(this, "IOException while writing in XML during requesting for project fields.", ex);
      }
   }
   
    /**
     * Method to write xml having list of functions with all its 
     * details used for validation of the field.
     * 
     * @param functionName  //Name of function
     * @param functionBody  //Function body
     * @param parameter     //Function Parameter
     * @param errorMessage  //Function ErrorMessage
     * @param fieldType     //FieldType
     * @param writer        //MessageWriter
     */
     private void writeData(String functionName, String functionBody, String parameter, String errorMessage, String fieldType, MessageWriter writer) {
      try {
         String[] value = new String[5];
         value[0] = functionName;
         value[1] = functionBody;
         value[2] = parameter;
         value[3] = errorMessage;
         value[4] = fieldType;
         writer.startElement(T_ROW);
         for (int j = 0; j < 5; j++) {
            writer.startElement(T_COLUMN);
            String value1 = value[j];
            if (value1 == null) {
               writer.writeAttribute(A_IS_NULL, "YES");
            }
            else {
               writer.writeContent(value1);
            }
            writer.endElement();
         }
         writer.endElement();
      } catch (IOException ex) {
         CommonLogger.printExceptions(this, "IOException while writing in XML during requesting for project fields.", ex);
      }
   }
    
    /**
     * Method to Write output XML Data
     * @param fieldName //Project FieldName
     * @param isStart   //Boolean - true for current value
     * @param writer
     */
    private void writeData(String fieldName, boolean isStart, MessageWriter writer) {
      if (isStart) {
         try {
            //<fieldName>
            writer.startElement(T_FIELD_NAMES);
            writer.writeAttribute(A_NAME, fieldName);
         } catch (IOException ex) {
            CommonLogger.printExceptions(this, "IOException while writing in XML during requesting for project fields.", ex);
         }
      }
      else {
         //</fieldName>
         try {
            writer.endElement();
         } catch (IOException ex) {
            CommonLogger.printExceptions(this, "IOException while writing in XML during requesting for project fields.", ex);
         }
      }
   }
}