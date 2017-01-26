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
public class VolumeData {

    private int volumeId;
    private Map<Integer, ChildData> childMap;
    ProjectData projectmap;
    PreparedStatement pstmt = null;
    ResultSet rs = null;
    private Connection connection;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.server.valueobjects");
    /**     
     * @param volumeId - Volume Id 
     * @param projectData - Project Data
     */
    public VolumeData(int volumeId, ProjectData projectData) {
        this.volumeId = volumeId;
        this.projectmap = projectData;
        childMap = new HashMap<Integer, ChildData>();
        connection = PostValidation.getConnection();
        // fill the initial data for the volume object
        fillInitialData();
    }

    /**
     * get the required initial record for the volume data 
     */
    private void fillInitialData() {
        try {
            int childId = -1;
            int batchId = -1;
            pstmt = connection.prepareStatement(SQLQueries.SEL_CHILD_BATCH_IDS_PVR);
            pstmt.setInt(1, volumeId);
            pstmt.executeQuery();
            rs = pstmt.getResultSet();
            while (rs.next()) {
                childId = rs.getInt(1);
                batchId = rs.getInt(2);
                ChildData childmap = new ChildData(childId, batchId, projectmap);
                childMap.put(childId, childmap);
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            logger.error("Exception while filling the initial data in VolumeData." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }

    /**
     * Get the Childs record for this Volume
     * @return - Child Map
     */
    public Map<Integer, ChildData> getChildMap() {
        return childMap;
    }

    /**
     * Set the Child Map (child record for volume).
     * @param childMap - Child Map
     */
    public void setChildMap(Map<Integer, ChildData> childMap) {
        this.childMap = childMap;
    }

    /**
     * Get Volume Id
     * @return - Volume Id
     */
    public int getVolumeId() {
        return volumeId;
    }

    /**
     * Get Volume Id
     * @param volumeId -Volume Id
     */
    public void setVolumeId(int volumeId) {
        this.volumeId = volumeId;
    }
}