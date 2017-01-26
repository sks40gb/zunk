/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.server.valueobjects;

import com.fossa.servlet.command.*;
import com.fossa.servlet.common.SQLQueries;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.log4j.Logger;

/**
 *
 * @author sunil
 */
public class PostValidationReportsData {
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    private Connection connection;
    private int postValidatonId;
    private String postValidatonFilePath;
    private int projectFieldsId;
    private int batchId;
    private int childId;
    private int validationFuncitonsMasterId;
    private String status;
    private String FieldsName;
    private String functionName;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.server.valueobjects");

    public PostValidationReportsData() {
        connection = PostValidation.getConnection();
    }

    /**
     * Get Batch Number
     * @return - Batch Number
     */
    public int getBatchNumber() {
        int batchNumber = -1;
        try {
            pstmt = connection.prepareStatement(SQLQueries.SEL_BATCH_NUM_PVR);
            pstmt.setInt(1, batchId);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            while (rs.next()) {
                batchNumber = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("Exception while getting the batch number for PVR." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
        return batchNumber;
    }

    /**
     * Batch Id
     * @return Batch Id
     */
    public int getBatchId() {
        return batchId;
    }

    /**
     * Set Batch Id
     * @param batchId - Batch Id
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
     * @param childId - Child Id
     */
    public void setChildId(int childId) {
        this.childId = childId;
    }

    /**
     * Get Post Validation Id
     * @return - Post validation id
     */
    public int getPostValidatonId() {
        return postValidatonId;
    }

    /**
     * Get Fields Id
     * @return - Fields Id 
     */
    public int getProjectFieldsId() {
        return projectFieldsId;
    }

    /**
     * Set Field
     * @param projectFieldsId
     */
    public void setProjectFieldsId(int projectFieldsId) {
        this.projectFieldsId = projectFieldsId;
    }
    
    /**
     * Get Status
     * @return - Status
     */
    public String getStatus() {
        return status;
    }

    /**
     *  Set Status
     * @param status - Status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Get Functions Id
     * @return - validationFunctionsMasterId
     */
    public int getValidationFuncitonsMasterId() {
        return validationFuncitonsMasterId;
    }

    /**
     * Set Funtions Id
     * @param validationFuncitonsMasterId - Functions Id
     */
    public void setValidationFuncitonsMasterId(int validationFuncitonsMasterId) {
        this.validationFuncitonsMasterId = validationFuncitonsMasterId;
    }

    /**
     * Get Field Name
     * @return - Field Name
     */
    public String getFieldsName() {
        return FieldsName;
    }

    /**
     * Set Field Name
     * @param FieldsName - Field Name
     */
    public void setFieldsName(String FieldsName) {
        this.FieldsName = FieldsName;
    }

    /**
     * Get Function Name
     * @return - Function Name
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * Set Function Name
     * @param functionName - Function Name
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    /**
     * Get Post validation file path
     * @return - File Path
     */
    public String getPostValidatonFilePath() {
        return postValidatonFilePath;
    }
    
    /**
     * Get Post validation file path
     * @return - File Path
     */
    public void setPostValidatonFilePath(String filePath) {
        postValidatonFilePath = filePath;
    }

    /**
     * Get Document Number for a specific the Volume.
     * @param volumeId - Volume Id
     * @return - Document Number
     */
    public String getDocumentNumber(int volumeId) {
        String documentNumber = "";
        try {
            pstmt = connection.prepareStatement(SQLQueries.SEL_DOC_NUM_PVR);
            pstmt.setInt(1, volumeId);
            pstmt.setInt(2, childId);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();            
            if (rs.next()) {
                documentNumber = rs.getString(1);                
            }
        } catch (Exception e) {
            logger.error("Exception while getting the document number for PVR." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
        return (documentNumber == null ? "" : documentNumber);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PostValidationReportsData other = (PostValidationReportsData) obj;
        if (this.projectFieldsId != other.projectFieldsId) {
            return false;
        }
        if (this.batchId != other.batchId) {
            return false;
        }
        if (this.childId != other.childId) {
            return false;
        }
        if (this.validationFuncitonsMasterId != other.validationFuncitonsMasterId) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + this.projectFieldsId;
        hash = 89 * hash + this.batchId;
        hash = 89 * hash + this.childId;
        hash = 89 * hash + this.validationFuncitonsMasterId;
        return hash;
    }
}

