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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;

/**
 * Command Class to Get Project Field name, field value, 
 * Correction data and Correction type for a given Project and Volume
 *
 * @author Prakasha
 */
public class Command_Get_QA_FieldValue implements Command {
     
    PreparedStatement getl1childIdStatement = null;
    ResultSet l1ChildIdResultSet = null;
    PreparedStatement getl1ValueStatement = null;
    PreparedStatement getStartBateStatement = null;
    ResultSet l1ValueResultSet = null;
    ResultSet getStartBateResultSet = null;
    String startBates = "";
    int l1ChildId = 0;

    
    // Method to get Project Field name, field value, 
    // Correction data and Correction type for a given Project and Volume
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        
        PreparedStatement getProjectsFieldsStatement = null;
        PreparedStatement getFieldsOfTypeNameStatement = null;
        PreparedStatement getFieldsOfTypeTextStatement = null;
        PreparedStatement getCorrectionDataStatement = null;
        
        Connection connection = null;
        ResultSet projectFieldsResultSet = null;
        ResultSet fieldsOfTypeNameResultSet = null;
        ResultSet fieldsOfTypeTextResultSet = null;
        ResultSet correctionDataResultSet = null;
       
        int fieldId = 0;
        
        String fieldName = "";
        String fieldType = "";
        String fieldValue = "";
        int correctionId = 0;
        String correctionData = "";
        String correctionType = "";
        String tagName = ""; 
        String l1Information = "";
        String fieldLevel = "";
        
        // Getting Attribute value from input xml
        int projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
        int childId = Integer.parseInt(action.getAttribute(A_CHILD_ID));
        int samplingId = Integer.parseInt(action.getAttribute(A_SAMPLING_ID));
        String samplingType = action.getAttribute(A_SAMPLING_TYPE);
        
        // Getting Project Fields and its values for a given Project and Volume
        try {
            connection = dbTask.getConnection();
            getProjectsFieldsStatement = connection.prepareStatement(SQLQueries.SEL_PROJECT_DETAILS);
            // Get project fields and its details
            getProjectsFieldsStatement.setInt(1, projectId);
            projectFieldsResultSet = getProjectsFieldsStatement.executeQuery();
            
            // Start writing Project Field Values into xml
            writer.startElement(T_QA_PR_GET_FIELDVALUES); //<task_qa_pr_get_fieldvalues>
            String userSessionId = user.getFossaSessionId();
            writer.writeAttribute(A_FOSSAID, userSessionId);
            
            // Getting Field Values for given Project and Volume
            while(projectFieldsResultSet.next()) {
                fieldId = projectFieldsResultSet.getInt(1);
                fieldName = projectFieldsResultSet.getString(2);
                fieldType = projectFieldsResultSet.getString(3);
                tagName = projectFieldsResultSet.getString(4);
                l1Information = projectFieldsResultSet.getString(5);
                
                // Get the values if the project field type is name
                if(CommonConstants.FIELD_TYPE_NAME.equals(fieldType)) {
                    getFieldsOfTypeNameStatement = connection.prepareStatement(SQLQueries.SEL_FIELDVALUE_OF_TYPE_NAME);
                    getFieldsOfTypeNameStatement.setInt(1, childId);
                    getFieldsOfTypeNameStatement.setString(2, fieldName);
                    fieldsOfTypeNameResultSet = getFieldsOfTypeNameStatement.executeQuery();
                    
                    // Getting the field value from the query result and set the field level
                    // If the field value exists then set the field level as L2
                    // else set the field value as L1
                    if(fieldsOfTypeNameResultSet.next()) {
                        fieldValue = fieldsOfTypeNameResultSet.getString(1);
                        fieldLevel = CommonConstants.FIELD_LEVEL_L2;
                    }else {// Obtain the field value for the L1 field
                        fieldValue = getL1ValueForGivenChildId(childId, connection, fieldName,l1Information, fieldType);
                        fieldLevel = CommonConstants.FIELD_LEVEL_L1;
                    }
                }else {// Get the values if the project field type is not name
                    getFieldsOfTypeTextStatement = connection.prepareStatement(SQLQueries.SEL_FIELDVALUE_OF_TYPE_TEXT);
                    getFieldsOfTypeTextStatement.setInt(1, childId);
                    getFieldsOfTypeTextStatement.setString(2, fieldName);
                    fieldsOfTypeTextResultSet = getFieldsOfTypeTextStatement.executeQuery();

                    // For L2 field type
                    if(fieldsOfTypeTextResultSet.next()) {
                        fieldValue = fieldsOfTypeTextResultSet.getString(1);
                        fieldLevel = CommonConstants.FIELD_LEVEL_L2;
                    }else {// For L1 field type
                        fieldValue = getL1ValueForGivenChildId(childId, connection, fieldName, l1Information, fieldType);
                        fieldLevel = CommonConstants.FIELD_LEVEL_L1;
                    }
                }
                
                // If sampling type is field display - get a field value and write to XML
                if(CommonConstants.SAMPLINGTYPE_FIELD_DISPLAY.equals(samplingType)) {
                    
                        getCorrectionDataStatement = connection.prepareStatement(SQLQueries.SEL_QA_CORRECTION_DATA_FOR_FIELD_SAMPLING);
                        getCorrectionDataStatement.setInt(1, fieldId);
                        getCorrectionDataStatement.setInt(2, childId);
                        getCorrectionDataStatement.setInt(3, samplingId);
                        // Query to obtain Correction data and its details for a given project field
                        correctionDataResultSet = getCorrectionDataStatement.executeQuery();
                        
                        // Get the correction data and its details from the result set                
                        if(correctionDataResultSet.next()) {
                            correctionId = correctionDataResultSet.getInt(1);
                            correctionData = correctionDataResultSet.getString(2);
                            correctionType = correctionDataResultSet.getString(3);
                            
                        } else {// if no data then reset the correction data and its details
                            correctionId = 0;
                            correctionData = "";
                            correctionType = "";
                        }
                        
                        // Method call to write to the XML
                        writeXml(writer,fieldName,fieldValue,
                                fieldId,0,correctionId,correctionData, 
                                correctionType,tagName, fieldLevel);
                    
                    
                }// if sampling type is tag dispaly - get a field value and split it before writing to XML
                else if(CommonConstants.SAMPLINGTYPE_TAG_DISPLAY.equals(samplingType)) {
                    // Spliting the Field value in to Tags for the field seperator ';'
                    String[] splitedValue = fieldValue.split(";");
                
                    for(int i=0; i<splitedValue.length; i++) {
                        int index = i+1;
                        getCorrectionDataStatement = connection.prepareStatement(SQLQueries.SEL_QA_CORRECTION_DATA_FOR_FIELD_SAMPLING);
                        getCorrectionDataStatement.setInt(1, fieldId);
                        getCorrectionDataStatement.setInt(2, childId);
                        getCorrectionDataStatement.setInt(3, samplingId);
                        //Get Correction data
                        correctionDataResultSet = getCorrectionDataStatement.executeQuery();


                        // Get the correction data and its details from the result set
                        if(correctionDataResultSet.next()) {
                            correctionId = correctionDataResultSet.getInt(1);
                            correctionData = correctionDataResultSet.getString(2);
                            correctionType = correctionDataResultSet.getString(3);
                        }else {//if no data then reset the correction data 
                            correctionId = 0;
                            correctionData = "";
                            correctionType = "";
                        }
                        
                        // Method call to write to the XML
                        writeXml(writer,fieldName,splitedValue[i],
                                fieldId,index,correctionId,correctionData, 
                                correctionType,tagName, fieldLevel);

                    }
                }
            }
            writer.endElement(); //</task_qa_pr_get_fieldvalues>
            
        }catch(SQLException se) {//TODO: Prakash send a error message to the user
            se.printStackTrace();
            
            String sqlState = se.getSQLState();
            int errorCode = se.getErrorCode();
            Log.print(">>>"+se+" sqlState="+sqlState+" errorCode="+errorCode);
            CommonLogger.printExceptions(this,"SQLException caught while obtaining QA field value and details", se);
        }catch(IOException ie) {
            ie.printStackTrace();
            CommonLogger.printExceptions(this,"IOException caught while obtaining QA field value and details", ie);
        }
        return null;
    }
    
    /**
     * Method to Get L1 FieldValue For L2 Document(Child)
     *
     * @param childId - Child id of the sampled L2 document
     * @param connection - Connection variable to obtain sql connection
     * @param fieldName - Project field name of the L1 field
     * @param l1Information - L1 information of the project field, whether source, folder or document 
     * @param fieldType - Project field type (name or text)
     * @return  the L1 field value
     */
    private String getL1ValueForGivenChildId(int childId, Connection connection, String fieldName,
                                                String l1Information, String fieldType){
        
        String l1ChildValue = "";
        startBates = "";    // Reset the bates number
        l1ChildId = 0;
        
        // Get L1 childId for L2 ChildId
        try {
            
            // if the L1 information of the project field is not null
            if(null != l1Information ) {
                // if the L1 information of the project field is folder
                if(l1Information.equals(CommonConstants.L1_INFORMATION_FOLDER)) {
                    getl1childIdStatement = connection.prepareStatement(SQLQueries.SEL_CHILD_FOR_L1_INFORMATION_FOLDER);
                    getl1childIdStatement.setInt(1, childId);
                    getl1childIdStatement.setString(2, "");
                    l1ChildIdResultSet =  getl1childIdStatement.executeQuery();

                } 
                // if the L1 information of the project field is source
                else if(l1Information.equals(CommonConstants.L1_INFORMATION_SOURCE)) {
                    // Query to obatin the L1 child id for the selected L2 child id
                    getl1childIdStatement = connection.prepareStatement(SQLQueries.SEL_CHILD_FOR_L1_INFORMATION_SOURCE);
                    getl1childIdStatement.setInt(1, childId);
                    getl1childIdStatement.setString(2, "");
                    getl1childIdStatement.setString(3, "D");
                    l1ChildIdResultSet =  getl1childIdStatement.executeQuery();
                }
            }
            // Get the L1 child id from the result set
            while (l1ChildIdResultSet.next()){
                l1ChildId = l1ChildIdResultSet.getInt(1);
                break;
            }
            
            //get L1 Field Value 
            // if the field type is not null
            if(null != fieldType) {
                if(fieldType.equals(CommonConstants.FIELD_TYPE_NAME)) {  
                    getl1ValueStatement = connection.prepareStatement(SQLQueries.SEL_FIELDVALUE_OF_TYPE_NAME);
                    getl1ValueStatement.setInt(1, l1ChildId);
                    getl1ValueStatement.setString(2, fieldName);
                    l1ValueResultSet = getl1ValueStatement.executeQuery();

                } else if( (fieldType.equals(CommonConstants.FIELD_TYPE_TEXT) // if the field type is text
                                || fieldType.equalsIgnoreCase(CommonConstants.FIELD_TYPE_DATE))
                          ) {
                    // Query to obtain the L1 field value for the L1 child id        
                    getl1ValueStatement = connection.prepareStatement(SQLQueries.SEL_FIELDVALUE_OF_TYPE_TEXT);
                    getl1ValueStatement.setInt(1, l1ChildId);
                    getl1ValueStatement.setString(2, fieldName);
                    l1ValueResultSet = getl1ValueStatement.executeQuery();
                }            
            }            
            
            // Get the L1 field value from the result set    
            while(l1ValueResultSet.next()) {
                l1ChildValue = l1ValueResultSet.getString(1);
                break;
            }
            
            // get start bates for l1field
            getStartBateStatement = connection.prepareStatement(SQLQueries.SEL_BATES_NUMBER_FOR_L1_CHILD);
            getStartBateStatement.setInt(1, childId);
            getStartBateStatement.setString(2, "RANGE");
            getStartBateResultSet = getStartBateStatement.executeQuery();
            
            // Get the start bates from the result set
            while(getStartBateResultSet.next()) {
                startBates = getStartBateResultSet.getString(1);
                break;
            }
            
        } catch (SQLException ex) {
            String sqlState = ex.getSQLState();
            int errorCode = ex.getErrorCode();
            Log.print(">>>"+ex+" sqlState="+sqlState+" errorCode="+errorCode);
            Logger.getLogger(Command_Get_QA_FieldValue.class.getName()).log(Level.SEVERE, null, ex);
            CommonLogger.printExceptions(this,"SQLException caught while obtaining L1 field value", ex);
        }
        
        return l1ChildValue;
    }
    
    
    /**
     * Method to write the given input param value to XML
     *
     * @param writer - Message writer object 
     * @param fieldName - Project field names of the selected sampled document
     * @param splitedValue - Value of the project field after splitting, if the sampling is tag type
     * @param fieldId - project field id of the sampled document
     * @param index - tag sequence of the correction value from the qa_corrections table
     * @param correctionId - qa correction ID of the project field from the qa_corrections table
     * @param correctionData - Correction data of the project field from the qa_corrections table
     * @param correctionType - Correction type of the project field from the qa_corrections table
     * @param tagName - Tag name of the project field
     * @param fieldLevel - Project field level (whether L1 or L2)
     */
    private void writeXml(MessageWriter writer, String fieldName,String splitedValue,
            int fieldId, int index, int correctionId, String correctionData, 
            String correctionType, String tagName, String fieldLevel) {
        try {

            writer.startElement(T_ROW); //<row>
            writer.startElement(T_COLUMN); //<column>
            writer.writeContent(fieldName);
            writer.endElement(); //<column>
            writer.startElement(T_COLUMN); //<column>
            writer.writeContent(splitedValue);
            writer.endElement(); //</column>
            writer.startElement(T_COLUMN); //<column>
            writer.writeContent(fieldId);
            writer.endElement(); //</column>
            writer.startElement(T_COLUMN); //<column>
            writer.writeContent(index);
            writer.endElement(); //</column>
            writer.startElement(T_COLUMN); //<column>
            writer.writeContent(correctionId);
            writer.endElement(); //</column>
            writer.startElement(T_COLUMN); //<column>
            writer.writeContent(correctionData);
            writer.endElement(); //</column>
            writer.startElement(T_COLUMN); //<column>
            writer.writeContent(correctionType);
            writer.endElement(); //</column>
            writer.startElement(T_COLUMN); //<column>
            writer.writeContent(tagName);
            writer.endElement(); //</column>
            writer.startElement(T_COLUMN); //<column>
            writer.writeContent(l1ChildId);
            writer.endElement(); //</column>
            writer.startElement(T_COLUMN); //<column>
            writer.writeContent(startBates);
            writer.endElement(); //</column>
            writer.startElement(T_COLUMN); //<column>
            writer.writeContent(fieldLevel);
            writer.endElement(); //</column>
            writer.endElement(); //</row>
           
        } catch (IOException ex) {
            Logger.getLogger(Command_Get_QA_FieldValue.class.getName()).log(Level.SEVERE, null, ex);
            CommonLogger.printExceptions(this,"IOException caught while obtaining L1 field value", ex);
        }        
    }
    
    /**
     *
     * @return true 
     */
    public boolean isReadOnly() {
        return true;
    }

}