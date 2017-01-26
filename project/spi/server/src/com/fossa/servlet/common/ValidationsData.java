/*
 * ValidationsData.java
 *
 * Created on January 15, 2008, 4:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

import com.fossa.servlet.command.Mode;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Bala
 */
/**
 * Used to make entries in the history table
 */
public class ValidationsData {
     
   
     public int standard_field_validations_mapping_id = 0;    
     
      /** validation function name*/
     public String functionName = "";  
     
      /** validation function body*/
     public String methodBody = "";
     
      /** validation function description*/
     public String description = "";   
     
      /** validation function status(True/False)*/
     public String status = "";  
     
      /** validation function Error message*/
     public String errorMessage = "";     
     
      /** validation function user input*/
     public String userInput = "";
     
      /** validation function is common to all project*/
     public String isGeneric = "false";
     
      /** Mode */
     public String editOrDisplay= "";
      
     /** Mode */
     public String type= "";

     /** Scope of the function
      * 1. Field
      * 2. Document
      * 3. Volume
      */
     public String scope= "";
     
     public int fieldId = 0;
     public int projectId =0;
     public int validation_mapping_details_id = 0;
     public int validation_functions_master_id = 0;
     public int std_group_id = 0;   
     private static Logger logger = Logger.getLogger("com.fossa.servlet.common");
     
     /** Method used to save the advance validation data into corresponding history table
      * @param con database connection
      * @param userId 
      * @param mode shows the corresponding process(Add,Delete)
      */
    public void insertIntoHistoryTable(Connection con, int userId, String mode) {
        try {
            
            if(mode.equals(Mode.DELETE)){
                getRecord(con);
            }
            
            PreparedStatement ps = con.prepareStatement("INSERT INTO history_advance_validation (" +
                    "validation_functions_master_id," +
                    "function_name, " +
                    "description," +
                    "function_body, " +
                    "error_message, " +
                    "parameters," +
                    "status," +
                    "isGeneric, " +
                    "projectfields_id, " +
                    "users_id, " +
                    "mode, " +
                    "date) " +
                    "values(?,?,?,?,?,?,?,?,?,?,?,?)");
            ps.setInt(1, validation_functions_master_id);
            ps.setString(2, functionName);
            ps.setString(3, description);
            ps.setString(4, methodBody);
            ps.setString(5, errorMessage);
            ps.setString(6, userInput);
            ps.setString(7, status);
            ps.setString(8, isGeneric);
            ps.setInt(9, fieldId);
            ps.setInt(10, userId);
            ps.setString(11, mode);
            ps.setTimestamp(12, new Timestamp(new Date().getTime()));
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            logger.error("Exception while saving history for advance validations." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }
    
    /** Used to get the validation details
     *@param con database connection
     */
    public void getRecord(Connection con) {
        try{
            PreparedStatement pst = con.prepareStatement("SELECT FM.function_name,FM.description,MD.error_message, " +
                    " MD.parameter, coalesce(MD.status, 'False') as \"ifnull(MD.status, 'False')\", FM.function_body " +
                    " FROM validation_functions_master FM INNER JOIN validation_mapping_master MM " +
                    " ON FM.validation_functions_master_id = MM.validation_functions_master_id " +
                    " LEFT OUTER JOIN validation_mapping_details MD " +
                    " ON MM.validation_mapping_master_id = MD.validation_mapping_master_id " +
                    " WHERE MD.validation_mapping_details_id = ? AND MM.validation_functions_master_id = ? ");
            
            pst.setInt(1, validation_mapping_details_id);
            pst.setInt(2, validation_functions_master_id);
            pst.executeQuery();
            ResultSet rs = pst.getResultSet();
            if (rs.next()) {
                functionName = rs.getString(1);
                description = rs.getString(2);
                errorMessage = rs.getString(3);
                userInput = rs.getString(4);
                status = rs.getString(5);
                methodBody = rs.getString(6);
            }
            
        }catch(Exception e){
            logger.error("Exception while fetching records for advance validations." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }
  
     
}

