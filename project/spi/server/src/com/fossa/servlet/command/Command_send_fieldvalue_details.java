/*
 * Command_send_fieldvalue_details.java
 *
 * Created on February 17, 2008, 11:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.BateNumbers;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Element;

/**
 * This class handles the requests for field value details in Listing or Tally
 * @author murali
 */
public class Command_send_fieldvalue_details implements Command {

    private Connection connection;
    private PreparedStatement pstmt_getFieldType;
    private PreparedStatement pstmt_selectFromPage;
    private PreparedStatement pstmt_insertIntoNameValue;
    private PreparedStatement pstmt_updateListingOccurrence;
    private PreparedStatement pstmt_seletFromListingOccurrence;
    private PreparedStatement pstmt_selectFromValue;
    private ResultSet rs_getFieldType;
    private ResultSet rs_getChildId;
    private ResultSet rs_fromListingOccurrence;
    private ResultSet rs_fromValue;    
    private Statement stmt;
    public String field_type = "";
    public String field_value = "";
    public int childId = 0;
    private HashMap map = new HashMap();        
    private String insertQuery;    
    private int volumeId = 0;
    private String volumeName = "";
    private String bateNo = "";
    private int projectId = 0;
    private int fieldId = 0;
    private String status = "";
    private String errorType = "";
    private int listing_occurrence_id = 0;
    private int value_sequence = 0;
    private String view_marking = "";

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        
        connection = dbTask.getConnection();
        stmt = dbTask.getStatement();
        int userId = user.getUsersId();
        String fieldName = action.getAttribute(A_FIELD_NAME);
        String fieldValue = action.getAttribute(A_FIELD_VALUE);
        String newfieldValue = action.getAttribute(A_NEW_FIELD_VALUE);
        String condition = action.getAttribute(A_CONDITION);
        status = action.getAttribute(A_STATUS);
        bateNo = action.getAttribute(A_BATE);
        volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
        projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
        listing_occurrence_id = Integer.parseInt(action.getAttribute(A_ID));
        if ("Tally".equals(status) || "TallyQC".equals(status)) {
            errorType = action.getAttribute(A_ERRORTYPE);
        }
        try {
            //Get the field type for the given field
            pstmt_getFieldType = connection.prepareStatement("SELECT field_type ,projectfields_id FROM projectfields" +
                                                              " WHERE " + "field_name = ? AND project_id = ?");
            pstmt_getFieldType.setString(1, fieldName);
            pstmt_getFieldType.setInt(2, projectId);
            pstmt_getFieldType.executeQuery();
            rs_getFieldType = pstmt_getFieldType.getResultSet();
            while (rs_getFieldType.next()) {
                field_type = rs_getFieldType.getString(1);
                fieldId = rs_getFieldType.getInt(2);
            }
            if ("sweep".equals(condition)) {
                Statement st = connection.createStatement();                 
                if ("Listing".equals(status) || "ListingQC".equals(status)) {
                    rs_getChildId = st.executeQuery("SELECT child_id , sequence from listing_occurrence " +
                                                    "where listing_occurrence_id = " + listing_occurrence_id);
                } else if ("Tally".equals(status) || "TallyQC".equals(status)) {
                    rs_getChildId = st.executeQuery("SELECT sequence from tally_occurrence where tally_occurrence_id = "
                                                      + listing_occurrence_id);
                }
      
            } else if ("sweepAll".equals(condition)) {
                if ("Listing".equals(status) || "ListingQC".equals(status)) {
                    pstmt_selectFromPage = connection.prepareStatement("SELECT child_id,sequence from listing_occurrence " +
                                                                         "where field_id = ? and field_value =? ");
                } else if ("Tally".equals(status) || "TallyQC".equals(status)) {
                    pstmt_selectFromPage = connection.prepareStatement("SELECT child_id,sequence from tally_occurrence " +
                                                                        "where field_id = ? and field_value =? AND tally_occurrence_id = ?");
                }
                pstmt_selectFromPage.setInt(1, fieldId);
                pstmt_selectFromPage.setString(2, fieldValue);                
                pstmt_selectFromPage.executeQuery();
                rs_getChildId = pstmt_selectFromPage.getResultSet();
            }
            while (rs_getChildId.next()) {
                childId = rs_getChildId.getInt(1);
                 value_sequence = rs_getChildId.getInt(2);
                if (value_sequence == 0) {
                    value_sequence = rs_getChildId.getInt(2);
                }
                if ("Listing".equals(status) || "ListingQC".equals(status)) {
                    if ("name".equals(field_type)) {
                        String value = null;
                        Statement st = connection.createStatement();
                        ResultSet rsObj = st.executeQuery("select value from namevalue where child_id ='" + childId + "'and field_name ='" + fieldName + "'");
                        while (rsObj.next()) {
                            value = rsObj.getString(1);
                            if (value.contains(";")) {
                                String[] arr = value.split(";");
                                arr[value_sequence - 1] = newfieldValue;
                                value = "";
                                for (int i = 0; i < arr.length; i++) {
                                    if (i == 0) {
                                        value += arr[i];
                                    } else {
                                        value += ";" + arr[i];
                                    }
                                }
                            } else {
                                value = newfieldValue;
                            }
                        }
                        user.executeUpdate(dbTask, "delete from namevalue where child_id ='" + childId + "'and field_name ='" + fieldName + "'");

                        insertQuery = "Insert into namevalue (child_id, field_name, sequence, last_name, first_name," + "middle_name, organization, value) values(?,?,?,?,?,?,?,?)";
                        String data[] = new String[]{value};
                        pstmt_insertIntoNameValue = user.prepareStatement(dbTask, insertQuery);
                        boolean itemSaved = false;
                        for (int i = 0; i < data.length; i++) {
                            String nameData = data[i].trim();
                            String firstName = "";
                            String lastName = "";
                            String middleName = "";
                            String organization = "";

                            int slashPos = nameData.lastIndexOf('/');
                            // If no name, discard the slash - use org as name
                            if (slashPos == 0) {
                                nameData = nameData.substring(1).trim();
                                slashPos = -1;
                            }
                            if (slashPos >= 0) {
                                if (slashPos < nameData.length()) {
                                    organization = nameData.substring(slashPos + 1).trim();
                                }
                                nameData = nameData.substring(0, slashPos).trim();
                            }
                            int commaPos = nameData.indexOf(",");
                            if (commaPos == 0) {
                                nameData = nameData.substring(1).trim();
                                commaPos = -1;
                            }
                            if (commaPos < 0) {
                                lastName = nameData;
                            } else {
                                lastName = nameData.substring(0, commaPos).trim();
                                nameData = nameData.substring(commaPos + 1).trim();
                                int spacePos = nameData.lastIndexOf(' ');
                                if (spacePos < 0) {
                                    firstName = nameData.trim();
                                } else {
                                    firstName = nameData.substring(0, spacePos).trim();
                                    middleName = nameData.substring(spacePos + 1).trim();
                                }
                            }
                            if (lastName.length() > 0) {
                                itemSaved = true;
                                pstmt_insertIntoNameValue.setInt(1, childId);
                                pstmt_insertIntoNameValue.setString(2, fieldName);
                                pstmt_insertIntoNameValue.setInt(3, i);
                                pstmt_insertIntoNameValue.setString(4, lastName);
                                pstmt_insertIntoNameValue.setString(5, firstName);
                                pstmt_insertIntoNameValue.setString(6, middleName);
                                pstmt_insertIntoNameValue.setString(7, organization);                                
                                pstmt_insertIntoNameValue.setString(8, data[i].trim());
                                pstmt_insertIntoNameValue.executeUpdate();
                            }
                            if (!itemSaved) {
                                pstmt_insertIntoNameValue.setInt(1, childId);
                                pstmt_insertIntoNameValue.setString(2, fieldName);
                                pstmt_insertIntoNameValue.setInt(3, 0);
                                pstmt_insertIntoNameValue.setString(4, "");
                                pstmt_insertIntoNameValue.setString(5, "");
                                pstmt_insertIntoNameValue.setString(6, "");
                                pstmt_insertIntoNameValue.setString(7, "");
                                pstmt_insertIntoNameValue.setString(8, "");
                                pstmt_insertIntoNameValue.executeUpdate();
                            }
                            pstmt_insertIntoNameValue.close();
                        }
                    } else if ("text".equals(field_type) || "date".equals(field_type)) {

                        pstmt_selectFromValue = connection.prepareStatement("SELECT value from value where child_id=? AND field_name =?");
                        pstmt_selectFromValue.setInt(1, childId);
                        pstmt_selectFromValue.setString(2, fieldName);
                        pstmt_selectFromValue.executeQuery();
                        rs_fromValue = pstmt_selectFromValue.getResultSet();
                        while (rs_fromValue.next()) {
                            field_value = rs_fromValue.getString(1);
                            if (field_value.contains(";")) {
                                String[] arr = field_value.split(";");
                                arr[value_sequence - 1] = newfieldValue;
                                field_value = "";
                                for (int i = 0; i < arr.length; i++) {
                                    if (i == 0) {
                                        field_value += arr[i];
                                    } else {
                                        field_value += ";" + arr[i];
                                    }
                                }
                            } else {
                                field_value = newfieldValue;
                            }
                        }
                        //delete the value
                        user.executeUpdate(dbTask, "delete from value where child_id ='" + childId + "'and field_name ='" + fieldName + "'");

                        insertQuery = "Insert into value (child_id, field_name, sequence, value) values (?,?,?,?)";
                                             
                        pstmt_insertIntoNameValue = user.prepareStatement(dbTask, insertQuery);
                        boolean itemSaved = false;
                        itemSaved = true;
                        pstmt_insertIntoNameValue.setInt(1, childId);
                        pstmt_insertIntoNameValue.setString(2, fieldName);
                        pstmt_insertIntoNameValue.setInt(3, 0);
                        pstmt_insertIntoNameValue.setString(4, field_value);
                        pstmt_insertIntoNameValue.executeUpdate();

                        // We save blank, because it could be a non-required field with default
                        if (!itemSaved) {
                            pstmt_insertIntoNameValue.setInt(1, childId);
                            pstmt_insertIntoNameValue.setString(2, fieldName);
                            pstmt_insertIntoNameValue.setInt(3, 0);
                            pstmt_insertIntoNameValue.setString(4, "");
                            pstmt_insertIntoNameValue.executeUpdate();
                        }
                        pstmt_insertIntoNameValue.close();
                    }
                }
                if ("Listing".equals(status) || "ListingQC".equals(status)) {
                    pstmt_updateListingOccurrence = connection.prepareStatement("UPDATE listing_occurrence  SET field_value = ? " +
                                                                                "where " + "child_id = ? AND field_id = ? AND field_value = ? ");
                    pstmt_updateListingOccurrence.setString(1, newfieldValue);
                    pstmt_updateListingOccurrence.setInt(2, childId);
                    pstmt_updateListingOccurrence.setInt(3, fieldId);
                    pstmt_updateListingOccurrence.setString(4, fieldValue);                    
                    pstmt_updateListingOccurrence.executeUpdate();
                    pstmt_updateListingOccurrence.close();

                } else if ("Tally".equals(status) || "TallyQC".equals(status)) {
                    Statement st = connection.createStatement();
                    st.executeUpdate("UPDATE tally_occurrence  SET field_value = '" + newfieldValue + "',errorType ='" + errorType +
                            "' where child_id = " + childId + " AND field_id = " + fieldId + " AND field_value = '" + fieldValue 
                            + "'AND tally_occurrence_id = " + listing_occurrence_id);
                }
                //To make an entry in the codinghistory table
                int project_id = 0;
                int batch_id = 0;
                String field_name = "";
                String fieldType = "";                
                int volumeID = 0;

                ResultSet getBatchIdResultSet = stmt.executeQuery("select batch_id from child where child_id = " + childId);
                while (getBatchIdResultSet.next()) {
                    batch_id = getBatchIdResultSet.getInt(1);
                }

                ResultSet getVolumeIdResultSet = stmt.executeQuery("select volume_id from batch where batch_id = " + batch_id);
                while (getVolumeIdResultSet.next()) {
                    volumeID = getVolumeIdResultSet.getInt(1);
                }

                ResultSet getProjIdResultSet = stmt.executeQuery("select project_id from volume where volume_id = " + volumeID);
                while (getProjIdResultSet.next()) {
                    project_id = getProjIdResultSet.getInt(1);
                }

                ResultSet getFieldResultSet = stmt.executeQuery("select field_name,field_type from projectfields where projectfields_id = " + fieldId);
                while (getFieldResultSet.next()) {
                    field_name = getFieldResultSet.getString(1);
                    fieldType = getFieldResultSet.getString(2);
                }

                //insert in coding history
                storeInCodingHistory(project_id, volumeID, batch_id, field_name, fieldType, newfieldValue, childId, userId);
            }
            if ("Listing".equals(status) || "ListingQC".equals(status)) {
                pstmt_seletFromListingOccurrence = connection.prepareStatement("SELECT child_id,field_value,view_marking," +
                                                                                 "start_bates,end_bates,listing_occurrence_id " +
                                                                                 "FROM listing_occurrence " +
                                                                                 " WHERE " + "project_id = ? AND field_id = ? AND field_value = ? ");
            } else if ("Tally".equals(status) || "TallyQC".equals(status)) {
                pstmt_seletFromListingOccurrence = connection.prepareStatement("SELECT child_id,field_value  FROM tally_occurrence" +
                        " WHERE " + "project_id = ? AND field_id = ? AND field_value = ? AND tally_occurrence_id = ?");
            }
            pstmt_seletFromListingOccurrence.setInt(1, projectId);
            pstmt_seletFromListingOccurrence.setInt(2, fieldId);
            pstmt_seletFromListingOccurrence.setString(3, newfieldValue);                     
            pstmt_seletFromListingOccurrence.executeQuery();
            rs_fromListingOccurrence = pstmt_seletFromListingOccurrence.getResultSet();            
            while (rs_fromListingOccurrence.next()) {

                ArrayList alist = new ArrayList();                
                alist.add(rs_fromListingOccurrence.getInt(1));                
                alist.add(rs_fromListingOccurrence.getString(2));                
                alist.add(rs_fromListingOccurrence.getString(3));
                //startbate
                alist.add(rs_fromListingOccurrence.getString(4));
                //endbate
                alist.add(rs_fromListingOccurrence.getString(5));
                //listing_occurrence_id
                alist.add(rs_fromListingOccurrence.getInt(6));                

                BateNumbers bates = createBates(alist, newfieldValue, view_marking);
                map.put(rs_fromListingOccurrence.getInt(1), bates);
            }
            writeXmlFromResult(user, map, writer, false);
        } catch (IOException ex) {
            CommonLogger.printExceptions(this, "IOException while updating the listing/tally occurence." , ex);
        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "SQLException while updating the listing/tally occurence." , ex);
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

    private BateNumbers createBates(List batesList, String newfieldValue, String view_marking) {
        BateNumbers bates = null;
        String startBate = "";
        String endBate = "";
        if (batesList != null) {
            if (batesList.size() != 0) {
                bates = new BateNumbers();
                bates.setStartBate((String) batesList.get(3));
                bates.setEndBate((String) batesList.get(4));
                bates.setFieldValue((String) batesList.get(1));
                bates.setListing_occurrence_id((Integer) batesList.get(5));
                bates.setChildId((Integer) batesList.get(0));
                bates.setView_marking((String) batesList.get(2));
            } else {
                throw new ServerFailException("Volume Contains Incomplete Coding Batch :   ");
            }
        }

        return bates;
    }

    private void writeXmlFromResult(UserTask user, HashMap map, MessageWriter writer, boolean b)
            throws SQLException, IOException {

        String userSessionId = user.getFossaSessionId();
        int columnCount = 4;
        writer.startElement(T_RESULT_SET);
        writer.writeAttribute(A_FOSSAID, userSessionId);
        writer.writeAttribute(A_COUNT, Integer.toString(columnCount));

        writer.startElement(T_HEADING);
        //writer.endElement();
        writer.startElement(T_COLUMN);
        writer.writeContent("start bate");
        writer.endElement();

        writer.startElement(T_COLUMN);
        writer.writeContent("end bate");
        writer.endElement();

        writer.startElement(T_COLUMN);
        writer.writeContent("Field value");
        writer.endElement();

        writer.startElement(T_COLUMN);
        writer.writeContent("listing_occurrence_id");
        writer.endElement();

        writer.startElement(T_COLUMN);
        writer.writeContent("Mark");
        writer.endElement();

        writer.endElement();

        if (null != map) {
            Set keys = map.keySet();
            for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
                writer.startElement(T_ROW);
                Integer key = (Integer) iterator.next();
                BateNumbers bateNumber = (BateNumbers) map.get(key);
                writer.startElement(T_COLUMN);
                writer.writeContent(bateNumber.getStartBate()); //start_bate
                writer.endElement();
                writer.startElement(T_COLUMN);
                writer.writeContent(bateNumber.getEndBate()); //end_bate
                writer.endElement();
                writer.startElement(T_COLUMN);
                writer.writeContent(bateNumber.getFieldValue()); //field value
                writer.endElement();
                writer.startElement(T_COLUMN);
                writer.writeContent(bateNumber.getListing_occurrence_id()); //listing_occurrence_id
                writer.endElement();

                writer.startElement(T_COLUMN);
                writer.writeContent(bateNumber.getView_marking()); //listing_occurrence_id
                writer.endElement();
                writer.endElement(); //row
            }
            writer.endElement();//result set
        }


    }

    private void storeInCodingHistory(int project_id, int volumeID, int batch_id, String field_name, String fieldType, String field_Value, int childId, int userId) {
        try {

            Date date = new Date();
            long time = date.getTime();
            Timestamp timestamp = new Timestamp(time);
            
            PreparedStatement getEventPrepStmt = connection.prepareStatement("select event,codinghistory_id from codinghistory " +
                                                                  "where child_id = ? and field_name=?");
            getEventPrepStmt.setInt(1, childId);
            getEventPrepStmt.setString(2, field_name);
            ResultSet getEventResultSet = getEventPrepStmt.executeQuery();

            String[] valueSpaceSplitString = field_Value.split(" ");
            int lengthSpaceSplitted = valueSpaceSplitString.length;
            String[] valueSemiColonSplitString = field_Value.split(";");
            int lengthSemiColonSplitted = valueSemiColonSplitString.length;

            //if event is already there THEN update the values
            if (getEventResultSet.next() && status.equals(getEventResultSet.getString(1))) {
                PreparedStatement updateCodingHistPrepStmt = connection.prepareStatement("update codinghistory set value = ?  , logged_time = ? ," +
                        "words = ?,characters = ? ,tags = ?" + " where codinghistory_id =? ");
                updateCodingHistPrepStmt.setString(1, field_Value);
                updateCodingHistPrepStmt.setTimestamp(2, timestamp);
                updateCodingHistPrepStmt.setInt(3, lengthSpaceSplitted);
                updateCodingHistPrepStmt.setInt(4, field_Value.length());
                updateCodingHistPrepStmt.setInt(5, lengthSemiColonSplitted);
                updateCodingHistPrepStmt.setInt(6, getEventResultSet.getInt(2));
                updateCodingHistPrepStmt.executeUpdate();
                updateCodingHistPrepStmt.close();
            } else {
                //else add a new row
                PreparedStatement insCodingHistPrepStmt = connection.prepareStatement("insert into codinghistory(child_id,project_id" +
                        ",volume_id,batch_id,field_name,field_type,value,event,user_id,logged_time,words,characters,tags)" +
                        " values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
                insCodingHistPrepStmt.setInt(1, childId);
                insCodingHistPrepStmt.setInt(2, project_id);
                insCodingHistPrepStmt.setInt(3, volumeID);
                insCodingHistPrepStmt.setInt(4, batch_id);
                insCodingHistPrepStmt.setString(5, field_name);
                insCodingHistPrepStmt.setString(6, fieldType);
                insCodingHistPrepStmt.setString(7, field_Value);
                insCodingHistPrepStmt.setString(8, status);
                insCodingHistPrepStmt.setInt(9, userId);
                insCodingHistPrepStmt.setTimestamp(10, timestamp);
                insCodingHistPrepStmt.setInt(11, lengthSpaceSplitted);
                insCodingHistPrepStmt.setInt(12, field_Value.length());
                insCodingHistPrepStmt.setInt(13, lengthSemiColonSplitted);
                insCodingHistPrepStmt.executeUpdate();
                insCodingHistPrepStmt.close();
            }
        } catch (SQLException ex) {
            CommonLogger.printExceptions(this,"Exceptions raised while saving coding history", ex);
        }
    }
}
