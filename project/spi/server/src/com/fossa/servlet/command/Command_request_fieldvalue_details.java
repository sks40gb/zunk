/*
 * To change this template, choose Tools | Templates
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
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Element;

/**
 * Command Class to Get the FieldValues For Given Volume 
 * @author bmurali
 */
public class Command_request_fieldvalue_details implements Command {

    private Connection connection;
    private PreparedStatement pstmt_getProjectfields = null;
    private PreparedStatement pstmt_getListingOccurrenceDetails = null;       
    private ResultSet rs_getProjectfields = null;
    private ResultSet rs_getListingOccurrence = null;      
    private String status = "";
    private int childId = 0;
    private HashMap map = new HashMap();    
    private BateNumbers obj;
    private int volumeId = 0;
    private int projectId = 0;
    private int fieldId = 0;    
    private int listing_occurrence_id = 0;
    private String view_marking="";        

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            connection = dbTask.getConnection();            
            String fieldName = action.getAttribute(A_FIELD_NAME);
            String fieldValue = action.getAttribute(A_FIELD_VALUE);
            projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
            volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            status = action.getAttribute(A_STATUS);
            pstmt_getProjectfields = connection.prepareStatement("SELECT projectfields_id  FROM projectfields " +
                                                                  "WHERE field_name = ? AND project_id =?");
            pstmt_getProjectfields.setString(1, fieldName);
            pstmt_getProjectfields.setInt(2, projectId);
            pstmt_getProjectfields.executeQuery();
            rs_getProjectfields = pstmt_getProjectfields.getResultSet();
            while (rs_getProjectfields.next()) {
                fieldId = rs_getProjectfields.getInt(1);
            }
            //Get the listing occurence details
            if ("Listing".equals(status) || "ListingQC".equals(status)) {
                pstmt_getListingOccurrenceDetails = connection.prepareStatement("SELECT child_id,field_value,listing_occurrence_id" +
                                                                         ",view_marking,start_bates,end_bates " +
                                                                         "FROM listing_occurrence WHERE project_id = ? " +
                                                                         "AND field_id = ? AND field_value = ?");
            } //OR get the Tally occurence details
            else if ("Tally".equals(status) || "TallyQC".equals(status)) {
                pstmt_getListingOccurrenceDetails = connection.prepareStatement("SELECT child_id,field_value,tally_occurrence_id  " +
                                                                         "FROM tally_occurrence WHERE project_id = ? " +
                                                                         "AND field_id = ? AND field_value = ?");
            }
            
            pstmt_getListingOccurrenceDetails.setInt(1, projectId);
            pstmt_getListingOccurrenceDetails.setInt(2, fieldId);
            pstmt_getListingOccurrenceDetails.setString(3, fieldValue);
            pstmt_getListingOccurrenceDetails.executeQuery();
            rs_getListingOccurrence = pstmt_getListingOccurrenceDetails.getResultSet();
           
            while (rs_getListingOccurrence.next()) {
                ArrayList alist = new ArrayList();       //Contains the childId,field value,start_bates,end_bates
                childId = rs_getListingOccurrence.getInt(1);
                fieldValue = rs_getListingOccurrence.getString(2);
                listing_occurrence_id = rs_getListingOccurrence.getInt(3);               
                view_marking = rs_getListingOccurrence.getString(4);               
                alist.add(rs_getListingOccurrence.getString(5));
                alist.add(rs_getListingOccurrence.getString(6));
                
                BateNumbers bates = createBates(alist, fieldValue,view_marking);
                map.put(listing_occurrence_id, bates);
            }
            writeXmlFromResult(user, map, writer, false);

        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "SQLException while viewing the details of field values during Listing/Tally.", ex);
            return ex.getMessage();
        } catch (IOException ex) {
            CommonLogger.printExceptions(this, "IOException while viewing the details of field values during Listing/Tally.", ex);
            return null;
        }
        return null;
    }
    
    /**
     * Method to Create BatesNumber
     * 
     * @param batesList //Holds list of BatesNumber
     * @param fieldValue //Project FieldValue
     * @param view_marking
     * @return
     */
    private BateNumbers createBates(List batesList, String fieldValue,String view_marking) {
        BateNumbers bates = null;
        String startBate = "";
        String endBate = "";
        if (batesList != null) {
            if (batesList.size() != 0) {
                startBate = (String) batesList.get(0);
                endBate = (String) batesList.get(1);
            } else {
                throw new ServerFailException("Volume Contains Incomplete Coding Batch :   ");
            }
            bates = new BateNumbers();
            bates.setStartBate(startBate);
            bates.setEndBate(endBate);
            bates.setFieldValue(fieldValue);
            bates.setListing_occurrence_id(listing_occurrence_id);
            bates.setChildId(childId);
            bates.setView_marking(view_marking);        
        }
        return bates;
    }

    /**
     * Method to write output XML to Client
     * 
     * @param user
     * @param map
     * @param writer
     * @param b     //Boolean
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     */
    private void writeXmlFromResult(UserTask user, HashMap map, MessageWriter writer, boolean b) throws SQLException, IOException {

        String userSessionId = user.getFossaSessionId();
        int columnCount = 5;
        writer.startElement(T_RESULT_SET);
        writer.writeAttribute(A_FOSSAID, userSessionId);
        writer.writeAttribute(A_COUNT, Integer.toString(columnCount));


        writer.startElement(T_HEADING);

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
                writer.writeContent(bateNumber.getView_marking()); //view_marking
                
                writer.endElement();                
                writer.endElement(); //row
            }
            writer.endElement();//result set
        }
    }
    
    public boolean isReadOnly() {
        return true;
    }
}
