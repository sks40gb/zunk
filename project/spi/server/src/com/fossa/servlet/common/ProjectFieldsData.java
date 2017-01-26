/* $Header: /home/common/cvsarea/ibase/dia/src/common/ProjectFieldsData.java,v 1.7.6.4 2005/11/11 15:14:29 nancy Exp $ */
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
 * A container for data required for update and retrieval of projectfields.
 */
public class ProjectFieldsData {

    public ProjectFieldsData() {
//        projectfieldsId = 0;
//        projectId = 0;
//        sequence = 0;
//        fieldName = "";
//        l1_information = "";
//        tagName = "";
//        fieldType = "";
//        fieldSize = 0;
//        minimumSize = 0;
//        repeated = "";   
//        unitize = "";
//        spellCheck = "";
//        required = "";
//        defaultValue = "";
//        fieldLevel = "";
//        fieldGroup = 0;
//        minValue = "0";
//        maxValue =  "1";
//        tablespecId = 0;
//        tableMandatory = "";
//        mask = "";
    }
    

    /** Id of the projectfields row */
    public int projectfieldsId;

    /** Id of the project */
    public int projectId;

    /** Sequence of this component on the screen */
    public int sequence;

    /** The name of the field */
    public String fieldName;
    
    /** L1 information field */
    public String l1_information;

    /** The short name of the field (used for BRS) */
    public String tagName;

    /** Type of field: text, date, signed, unsigned, name */
    public String fieldType;

    /** Size of the field */
    public int fieldSize;

    /** Minimum size of the field */
    public int minimumSize;

    /** One value or can the value be repeated */
    public String repeated;

    /** Should this field show on the unitize screen */
    public String unitize;

    /** Should this field have a spell check */
    public String spellCheck;

    /** Is the field required */
    public String required;

    /** If no value entered by user, use this value */
    public String defaultValue;

    /** document level indicator */
    public String fieldLevel;

    /** group this field belongs to */
    public int fieldGroup;

    /** Minimum value of the data entered by the user */
    public String minValue;

    /** Maximum value of the data entered by the user */
    public String maxValue;

    /** If the data can be selected from a list, the id of the table in tablespec */
    public int tablespecId;

    /** Is the data required to be selected from a list */
    public String tableMandatory;

    /** Field mask */
    public String mask;

    /** Characters allowed in the field */
    public String validChars;

    /** Characters not allowed in the field */
    public String invalidChars;

    /** Character set */
    public String charset;

    public String typeField;
    public String typeValue;

    /** +1 indicates move up, -1 indicates move down */
    public int moveIndicator;

    /** Description of the Field */
    public String description;
    
    /** Standard field validation  */
    public String standardFieldValidations;
    
    /** standard field validation group Id */
    public int std_group_id = -1;
    
    /** shows the mode of action*/
    public String mode;
    
    /** Logger */ 
    private static Logger logger = Logger.getLogger("com.fossa.servlet.common");
    
    /** Insert a particular row of field data for a corresponding data*/
    public void insertIntoHistoryTable(Connection con, int userId, String mode) {
        try {         
            // if the mode is delete. It doesn't have record other than projectfields_id
            // so first get the record corresponding to the projectfields_id
            if(mode.equals(Mode.DELETE)){
                getRecord(con);
            }
            String sql  ="INSERT INTO history_projectfields (" +
                    "projectfields_id, " +    //1
                    "project_id, " +          //2          
                    "sequence, " +            //3        
                    "field_name, " +          //4
                    "tag_name, " +            //5        
                    "field_type, " +          //6 
                    "field_size, " +          //7          
                    "minimum_size, " +        //8
                    "field_level, " +         //9
                    "field_group, " +         //10           
                    "repeated, " +            //11   
                    "required, " +            //12
                    "default_value, " +       //13             
                    "min_value, " +           //14
                    "max_value, " +           //15         
                    "tablespec_id, " +        //16
                    "table_mandatory, " +     //17               
                    "mask, " +                //18
                    "valid_chars, " +         //19  
                    "invalid_chars, " +       //20             
                    "charset, " +             //21
                    "type_field, " +          //22
                    "type_value, " +          //23          
                    "unitize, " +             //24
                    "spell_check, " +         //25
                    "description, " +         //26
                    "validation_functions_group_id, " + //27
                    "l1_information, " +      //28              
                    "h_users_id," +            //29
                    "mode," +                  //30 
                    "date" +                  //31 
                    ") VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            
            PreparedStatement pst = con.prepareStatement(sql);
                               
            pst.setInt(1, projectfieldsId);
            pst.setInt(2, projectId);            
            pst.setInt(3, sequence);            
            pst.setString(4, fieldName);
            pst.setString(5, tagName);            
            pst.setString(6, fieldType);
            pst.setInt(7, fieldSize);
            pst.setInt(8, minimumSize);
            pst.setInt(9, fieldLevel.equals("*") ? 0 : Integer.parseInt(fieldLevel));
            pst.setInt(10, fieldGroup);
            pst.setString(11, repeated);
            pst.setString(12, required);
            pst.setString(13, defaultValue);
            pst.setString(14, minValue);            
            pst.setString(15, maxValue);
            pst.setInt(16, tablespecId);
            pst.setString(17, tableMandatory);
            pst.setString(18, mask);
            pst.setString(19, validChars);
            pst.setString(20, invalidChars);
            pst.setString(21, charset);
            pst.setString(22, typeField);
            pst.setString(23, typeValue);            
            pst.setString(24, unitize);            
            pst.setString(25, spellCheck);                        
            pst.setString(26, description);                       
            pst.setInt(27, std_group_id );            
            pst.setString(28, l1_information);
            pst.setInt(29, userId);
            pst.setString(30, mode);
            pst.setTimestamp(31, new Timestamp(new Date().getTime()));            
            pst.executeUpdate();
            pst.close();
        } catch (Exception e) {
            logger.error("Exception while saving history for project fields." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }    
    /** Get all the field records for a particular project */
    public void getRecord(Connection con) {
        try{
            String sql = "SELECT " +
                    "projectfields_id, " +    //1
                    "project_id, " +          //2          
                    "sequence, " +            //3        
                    "field_name, " +          //4
                    "tag_name, " +            //5        
                    "field_type, " +          //6 
                    "field_size, " +          //7          
                    "minimum_size, " +        //8
                    "field_level, " +         //9
                    "field_group, " +         //10           
                    "repeated, " +            //11   
                    "required, " +            //12
                    "default_value, " +       //13             
                    "min_value, " +           //14
                    "max_value, " +           //15         
                    "tablespec_id, " +        //16
                    "table_mandatory, " +     //17               
                    "mask, " +                //18
                    "valid_chars, " +         //19  
                    "invalid_chars, " +       //20             
                    "charset, " +             //21
                    "type_field, " +          //22
                    "type_value, " +          //23          
                    "unitize, " +             //24
                    "spell_check, " +         //25
                    "description, " +          //26
                    "validation_functions_group_id, " + //27
                    "l1_information " +      //28  
                    "FROM projectfields WHERE projectfields_id = ?";
            
            
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1,projectfieldsId);
            pst.executeQuery();
            ResultSet rs = pst.getResultSet();
            
            if (rs.next()) {
                projectfieldsId = rs.getInt(1);
                projectId = rs.getInt(2);
                sequence = rs.getInt(3);
                fieldName = rs.getString(4);
                tagName = rs.getString(5);
                fieldType = rs.getString(6);
                fieldSize = rs.getInt(7);
                minimumSize = rs.getInt(8);
                fieldLevel = rs.getString(9);
                fieldGroup = rs.getInt(10);
                repeated = rs.getString(11);
                required = rs.getString(12);
                defaultValue = rs.getString(13);
                minValue = rs.getString(14);
                maxValue = rs.getString(15);
                tablespecId = rs.getInt(16);
                tableMandatory = rs.getString(17);
                mask = rs.getString(18);
                validChars = rs.getString(19);
                invalidChars = rs.getString(20);
                charset = rs.getString(21);
                typeField = rs.getString(22);
                typeValue = rs.getString(23);
                unitize = rs.getString(24);
                spellCheck = rs.getString(25);
                description = rs.getString(26);
                std_group_id = rs.getInt(27);
                l1_information = rs.getString(28);
            }
            
        }catch(Exception e){
            logger.error("Exception while fetching records of project fields." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }
     
}
