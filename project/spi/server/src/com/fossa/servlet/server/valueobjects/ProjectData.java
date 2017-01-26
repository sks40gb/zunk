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
public class ProjectData {

    private int projectId;
    private String projectName;
    private Map<String, FieldData> fieldMap;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    private Connection connection;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.server.valueobjects");

    /**
     * 1. Set the project id
     * 2. Get Field record for a field
     * 3. create a connectoin for transaction
     * 4. fill initial data
     * @param projectId - Project Id
     */
    public ProjectData(int projectId) {
        this.projectId = projectId;
        fieldMap = new HashMap<String, FieldData>();
        connection = PostValidation.getConnection();

        //get the project name
        fillInitialData();
    }

    /**
     * Set project name.
     */
    private void fillInitialData() {
        try {
            pstmt = connection.prepareStatement(SQLQueries.SEL_PROJECT_NAME_PVR);
            pstmt.setInt(1, projectId);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            if (rs.next()) {
                projectName = rs.getString(1);
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            logger.error("Exception while filling the initial data in ProjectData." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }

    /**
     * get the record for a field
     * @return - Field Map
     */
    public Map<String, FieldData> getFieldMap() {
        return fieldMap;
    }

    /**
     * Set the Field Map (keep record of Field in a map).
     * @param fieldMap - Field Map
     */
    public void setFieldMap(Map<String, FieldData> fieldMap) {
        this.fieldMap = fieldMap;
    }

    /**
     * Get the Project Id
     * @return - Project Id
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Set the Project Id
     * @param projectId - Project Id
     */
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    /**
     * Get the Project Name
     * @return - Project Name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Set the Project Name
     * @param projectName - Project Name
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * put a Field record in a Map collection of Fields.
     * @param key - Field Name. 
     * @param value - Field Object.
     */
    public void putInFieldMap(String key, FieldData value) {
        fieldMap.put(key, value);
    }
}