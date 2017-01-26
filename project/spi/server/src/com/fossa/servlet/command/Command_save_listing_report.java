/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.BateNumbers;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Element;

/**
 * Command Class to Save Listing Report
 * @author bmurali
 */
public class Command_save_listing_report implements Command {

    private Connection connection;
    private PreparedStatement getFieldTypePrepStmt = null;
    private PreparedStatement getBatchIDPrepStmt = null;
    private PreparedStatement getChildIdPrepStmt = null;
    private PreparedStatement getValuePrepStmt = null;
    private PreparedStatement insListingOccPrepStmt = null;
    private PreparedStatement getListingDetailsPrepStmt = null;
    private PreparedStatement getBatesPrepStmt = null;
    private PreparedStatement getVolumePrepStmt = null;
    private PreparedStatement insListingReportPrepStmt = null;
    private ResultSet getFieldTypeResultSet = null;
    private ResultSet getBatchIdResultSet = null;
    private ResultSet getChildIdResultSet = null;
    private ResultSet getValueResultSet = null;    
    private ResultSet getListingDetailsResultSet = null;
    private ResultSet getBatesResultSet = null;
    private ResultSet getVolumeResultSet = null;    
    private String fieldName = "";
    private String fieldValue = "";
    private String volumeName = "";
    private int projectId = 0;
    private int volumeId = 0;
    private int fieldId = 0;
    private int batchId = 0;
    private int childId = 0;    
    private String field_type = "";
    private String selectQuery = "";
    private String insertQuery = "";
    private HashMap map = new HashMap();    
    private HashMap mapForValueDetail = new HashMap();

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            connection = dbTask.getConnection();
            fieldName = action.getAttribute(A_FIELD_NAME);
            fieldValue = action.getAttribute(A_FIELD_VALUE);
            projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
            volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            
            //Get FieldType and ProjectFieldId
            getFieldTypePrepStmt = connection.prepareStatement("SELECT field_type,projectfields_id  FROM projectfields" +
                                                " WHERE field_name = ? AND project_id =?");
            getFieldTypePrepStmt.setString(1, fieldName);
            getFieldTypePrepStmt.setInt(2, projectId);
            getFieldTypePrepStmt.executeQuery();
            getFieldTypeResultSet = getFieldTypePrepStmt.getResultSet();
            while (getFieldTypeResultSet.next()) {
                field_type = getFieldTypeResultSet.getString(1);
                fieldId = getFieldTypeResultSet.getInt(2);
            }
            
            //Get BatchId
            getBatchIDPrepStmt = connection.prepareStatement("SELECT batch_id  FROM batch WHERE volume_id = ? AND status = ?");
            getBatchIDPrepStmt.setInt(1, volumeId);
            getBatchIDPrepStmt.setString(2, "QCComplete");
            getBatchIDPrepStmt.executeQuery();
            getBatchIdResultSet = getBatchIDPrepStmt.getResultSet();
            ArrayList clidIdList = new ArrayList();
            while (getBatchIdResultSet.next()) {
                batchId = getBatchIdResultSet.getInt(1);
                getChildIdPrepStmt = connection.prepareStatement("SELECT child_id  FROM child WHERE volume_id = ? AND batch_id = ?");
                getChildIdPrepStmt.setInt(1, volumeId);
                getChildIdPrepStmt.setInt(2, batchId);
                getChildIdPrepStmt.executeQuery();
                getChildIdResultSet = getChildIdPrepStmt.getResultSet();
                while (getChildIdResultSet.next()) {
                    childId = getChildIdResultSet.getInt(1);
                    clidIdList.add(childId);
                }
                map.put(batchId, clidIdList);
            }

            if (null != map) {
                //delete a row
                user.executeUpdate(dbTask,
                        "delete from listing_occurrence where project_id='" + projectId + "' AND field_id ='" + fieldId + "'");
                Set keys = map.keySet();                
                for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
                    Integer key = (Integer) iterator.next();
                    List batesList = (ArrayList) map.get(key);
                    for (Iterator itr = batesList.iterator(); itr.hasNext();) {
                        Integer value = (Integer) itr.next();
                        if ("name".equals(field_type)) {
                            selectQuery = "SELECT VALUE FROM namevalue where FIELD_NAME = ? AND child_id = ?";
                        } else if ("text".equals(field_type)) {
                            selectQuery = "SELECT VALUE FROM value where FIELD_NAME = ? AND child_id = ?";
                        }
                        getValuePrepStmt = connection.prepareStatement(selectQuery);
                        getValuePrepStmt.setString(1, fieldName);
                        getValuePrepStmt.setInt(2, value);
                        getValuePrepStmt.executeQuery();
                        getValueResultSet = getValuePrepStmt.getResultSet();
                        if (getValueResultSet.next()) {
                            fieldValue = getValueResultSet.getString(1);
                            insertQuery = "INSERT INTO listing_occurrence(project_id,child_id,field_id," +
                                                            "field_type,field_value) VALUES (?,?,?,?,?)";
                            insListingOccPrepStmt = connection.prepareStatement(insertQuery);
                            insListingOccPrepStmt.setInt(1, projectId);
                            insListingOccPrepStmt.setInt(2, value);
                            insListingOccPrepStmt.setInt(3, fieldId);
                            insListingOccPrepStmt.setString(4, field_type);
                            insListingOccPrepStmt.setString(5, fieldValue);
                            insListingOccPrepStmt.executeUpdate();                        
                        }
                    }                
                }
            }
            //Get Listing Details for given volume
            getListingDetailsPrepStmt = connection.prepareStatement("SELECT child_id,field_id,field_value,listing_occurrence_id " +
                                                " FROM listing_occurrence WHERE project_id = ? ");
            getListingDetailsPrepStmt.setInt(1, projectId);
            getListingDetailsPrepStmt.executeQuery();
            getListingDetailsResultSet = getListingDetailsPrepStmt.getResultSet();
            while (getListingDetailsResultSet.next()) {
                childId = getListingDetailsResultSet.getInt(1);
                fieldId = getListingDetailsResultSet.getInt(2);
                fieldValue = getListingDetailsResultSet.getString(3);
                int listing_id = getListingDetailsResultSet.getInt(4);
                getBatesPrepStmt = connection.prepareStatement("SELECT bates_number from page where child_id=?");
                getBatesPrepStmt.setInt(1, childId);
                getBatesPrepStmt.executeQuery();
                getBatesResultSet = getBatesPrepStmt.getResultSet();
                ArrayList alist = new ArrayList();                
                while (getBatesResultSet.next()) {
                    String bate = getBatesResultSet.getString(1);                    
                    alist.add(bate);
                }
                getVolumePrepStmt = connection.prepareStatement("SELECT volume_name from volume where volume_id=?");
                getVolumePrepStmt.setInt(1, volumeId);
                getVolumePrepStmt.executeQuery();
                getVolumeResultSet = getVolumePrepStmt.getResultSet();
                while (getVolumeResultSet.next()) {
                    volumeName = getVolumeResultSet.getString(1);
                }
                BateNumbers bates = createBates(alist, fieldValue, volumeName, projectId, fieldId, childId);
                mapForValueDetail.put(String.valueOf(listing_id), bates);
            }
            if (null != mapForValueDetail) {
                user.executeUpdate(dbTask,
                        "delete from listing_reports where project_id='" + projectId + "'");
                Set keys = mapForValueDetail.keySet();
                for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
                    String key = (String) iterator.next();
                    BateNumbers bateNumber = (BateNumbers) mapForValueDetail.get(key);

                    insertQuery = "INSERT INTO listing_reports(project_id,volumeName,child_id,field_id,field_value,startBate,endBate) " +
                                       "VALUES (?,?,?,?,?,?,?)";
                    insListingReportPrepStmt = connection.prepareStatement(insertQuery);
                    insListingReportPrepStmt.setInt(1, bateNumber.getProjectId());
                    insListingReportPrepStmt.setString(2, bateNumber.getVolumeName());
                    insListingReportPrepStmt.setInt(3, bateNumber.getChildId());
                    insListingReportPrepStmt.setInt(4, bateNumber.getFieldId());
                    insListingReportPrepStmt.setString(5, bateNumber.getFieldValue());
                    insListingReportPrepStmt.setString(6, bateNumber.getStartBate());
                    insListingReportPrepStmt.setString(7, bateNumber.getEndBate());
                    insListingReportPrepStmt.executeUpdate();
                }
            }

        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "SQLException while saving the listing report." , ex);
            return ex.getMessage();
        }
        return null;
    }
    
    /**
     * Method to Create BatesNumber
     * 
     * @param batesList  //Holds List of BatesNumber
     * @param fieldValue //Project FieldValue
     * @param volumeName //VolumeName
     * @param projectId
     * @param fieldId    //Project FieldId
     * @param childId    //ChildId - Document
     * @return
     */
    private BateNumbers createBates(List batesList, String fieldValue, String volumeName, int projectId, int fieldId, int childId) {
        BateNumbers bates = null;
        if (batesList != null) {
            String startBate = (String) batesList.get(0);
            String endBate = (String) batesList.get(batesList.size() - 1);
            bates = new BateNumbers();
            bates.setStartBate(startBate);
            bates.setEndBate(endBate);
            bates.setFieldValue(fieldValue);
            bates.setVolumeName(volumeName);
            bates.setChildId(childId);
            bates.setFieldId(fieldId);
            bates.setProjectId(projectId);
        }
        return bates;
    }

    public boolean isReadOnly() {
        return true;
    }
}
