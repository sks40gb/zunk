/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.server.valueobjects;

import com.fossa.servlet.command.PostValidation;
import com.fossa.servlet.common.SQLQueries;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author sunil
 */
/**class used to get project fields data*/
public class FieldData {

    private int fieldId;
    private int fieldSize;
    private String fieldName;
    private String fieldType;
    private String fieldValue;
    private com.fossa.servlet.writer.ValueData valueData;
    private Map<Integer, FunctionData> validationMap;
    private List<FunctionData> validationList;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    private Connection connection;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.server.valueobjects");

    public FieldData(){

    }

    public FieldData addName(String name){
        fieldName = name;
        return this;
    }
    public FieldData addType(String type){
        fieldType = type;
        return this;
    }
    public FieldData addValue(String value){
        fieldValue = value;
        return this;
    }

    public FieldData addPage(String value){
        fieldValue = value;
        return this;
    }

    public FieldData addValueData(com.fossa.servlet.writer.ValueData vd){
        valueData = vd;        
        return this;
    }

    /**
     * Set the Field Id for primary opertion.
     * Initialize the Validation Map
     * create connection for db opertion.
     * Fill initial required informatoin. 
     * @param fieldId - Field Id
     */
    public FieldData(int fieldId) {
        this.fieldId = fieldId;
        validationMap = new HashMap<Integer, FunctionData>();
        connection = PostValidation.getConnection();
        fillInitialData();
    }

    /**
     * set Field Name
     * Set Field Size
     * Set Field Type
     */
    private void fillInitialData() {
        try {
            pstmt = connection.prepareStatement(SQLQueries.SEL_PROJECT_FIELDS_PVR);
            pstmt.setInt(1, fieldId);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            if (rs.next()) {
                fieldName = rs.getString(1);
                fieldType = rs.getString(2);
                fieldSize = rs.getInt(3);
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            logger.error("Exception while filling the initial data in FieldData." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }

    /**
     * Get Field Id
     * @return - Field Id
     */
    public int getFieldId() {
        return fieldId;
    }

    /**
     * Set Field Id
     * @param fieldId - Field Id
     */
    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    /**
     * Set Field Name
     * @return - Field Name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Set Field Name
     * @param fieldName - Field Name
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Get Field Size
     * @return - Field Size
     */
    public int getFieldSize() {
        return fieldSize;
    }

    /**
     * Set Field size
     * @param fieldSize - Field size
     */
    public void setFieldSize(int fieldSize) {
        this.fieldSize = fieldSize;
    }

    /**
     * Get Field Type
     * @return - Field Type
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * Set Field Type
     * @param fieldType - Field type
     */
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * Get the validation map for the PVR
     * @return - Validation Map
     */
    public Map<Integer, FunctionData> getValidationMap() {
        return validationMap;
    }

    /**
     * Set validation map
     * @param validationMap - ValidationMap
     */
    public void setValidationMap(Map<Integer, FunctionData> validationMap) {
        this.validationMap = validationMap;
    }

    /**
     * Get Field Value
     * @return - Field value
     */
    public String getFieldValue() {
        return (fieldValue == null ? "" : fieldValue);
    }

    /**
     * Set Field Value
     * @param fieldValue - Field Value
     */
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
        if(valueData != null ){
            valueData.value = fieldValue;
        }
    }

    /**
     * Get the list of advance validatoin data for PVR
     * @return - Validation List.
     */
    public List<FunctionData> getValidationList() {
        validationList = new ArrayList<FunctionData>();
        for (FunctionData v : validationMap.values()) {
            validationList.add(v);
        }
        return validationList;
    }

    /**
     * Set validationList
     * @param validationList - List<Validation>
     */
    public void setValidationList(List<FunctionData> validationList) {
        this.validationList = validationList;
    }

    public void putInValidationMap(int key, FunctionData value) {
        validationMap.put(key, value);
    }
}