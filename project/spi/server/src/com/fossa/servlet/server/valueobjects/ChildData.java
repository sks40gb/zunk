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
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author sunil
 */
/**class used to get all the field value from value and namevalue table*/

public class ChildData {
  private int childId;
    private int batchId;
    private Map<String, FieldData> fieldMap;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    private Connection connection;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.server.valueobjects");

    /**     
     * @param childId - Child Id
     * @param batchId - Batch Id
     * @param projectmap - Project Map
     */
    public ChildData(int childId, int batchId, ProjectData projectmap) {
        this.childId = childId;
        this.batchId = batchId;
        fieldMap = new HashMap<String, FieldData>();
        connection = PostValidation.getConnection();
        fillInitialData(projectmap);
    }

    /**
     * Fill initial data to Child Object
     * @param projectmap - Project Map
     */
    private void fillInitialData(ProjectData projectmap) {
        try {            
            pstmt = connection.prepareStatement(SQLQueries.SEL_VALUE_PVR);
            pstmt.setInt(1, childId);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            String fieldValue;
            String fieldName;
            while (rs.next()) {                
                fieldValue = rs.getString(1);
                fieldName = rs.getString(2);
                //get the fieldData according the fieldName
                FieldData fieldmap = projectmap.getFieldMap().get(fieldName);
                //set the fieldData value.        
                if (fieldmap != null) {
                    //set the value for the field for the particular child and 
                    //then put this values to the map.
                    fieldmap.setFieldValue(fieldValue);
                    fieldMap.put(fieldmap.getFieldName(), fieldmap);
                }
            }

            pstmt = connection.prepareStatement(SQLQueries.SEL_NAME_VALUE_PVR);
            pstmt.setInt(1, childId);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            while (rs.next()) {                
                fieldValue = rs.getString(1);
                fieldName = rs.getString(2);
                //get the fieldData according the fieldName

                FieldData fieldmap = projectmap.getFieldMap().get(fieldName);
                //set the fieldData value.                
                if (fieldmap != null) {
                    //set the value for the field for the particular child and 
                    //then put this values to the map.
                    fieldmap.setFieldValue(fieldValue);
                    fieldMap.put(fieldmap.getFieldName(), fieldmap);
                }
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            logger.error("Exception while filling the initial data in ChildData." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }

    /**
     * Get Batch Id
     * @return - Batch Id
     */
    public int getBatchId() {
        return batchId;
    }

    /**
     * Set Batch Id
     * @param batchId -Batch Id
     */
    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    /**
     * Get Child Id
     * @return - Child Id
     */
    public int getChildId() {
        return childId;
    }

    /**
     * Set Child Id
     * @param childId -Child Id
     */
    public void setChildId(int childId) {
        this.childId = childId;
    }

    /**
     * Get records of Field in a Map
     * @return - Field Map
     */
    public Map<String, FieldData> getFieldMap() {
        return fieldMap;
    }

    /**
     * Set Field Map 
     * @param fieldMap - Field Map
     */
    public void setFieldMap(Map<String, FieldData> fieldMap) {
        this.fieldMap = fieldMap;
    }
}


