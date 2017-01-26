/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.ListingData;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Element;

/**
 * This class returns the field values and their occurences for Listing OR Tally processes.
 * @author bmurali
 */
public class Command_request_fieldvalue implements Command{
    private Connection connection;    
    private PreparedStatement pstmt_getListingOccurrenceValues;
    private PreparedStatement pstmt_getCountListingOccurrence;  
    private Statement getProjFieldIdStatement;
    private ResultSet getListingOccrncesResultSet;
    private ResultSet rs_countListingOccurrence;
    
    private String fieldName ="";
    private String fieldValue ="";    
    private String status ="";    
    private int projectId = 0;
    private int volumeId = 0;
    private int fieldId = 0;      
    private int occurrence =0;    
    private String marking ="";        
    private HashMap mapForValue =new HashMap();   
    
    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        try {
            connection = dbTask.getConnection();
            getProjFieldIdStatement = connection.createStatement();
            fieldName = action.getAttribute(A_FIELD_NAME);
            fieldValue = action.getAttribute(A_FIELD_VALUE);
            projectId = Integer.parseInt(action.getAttribute(A_PROJECT_ID));
            volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            status = action.getAttribute(A_STATUS);
           
            CallableStatement cs = connection.prepareCall( "{ call sproc_Listing(?,?,?,?,?) }" );                        
            cs.setString(1, fieldName);              
            cs.setInt(2, projectId);                
            cs.setString(3, fieldValue);           
            cs.setInt(4, volumeId);               
            cs.setString(5, status);             
            cs.execute();            
            cs.close();
        
            ResultSet getProjFieldIdResultSet = getProjFieldIdStatement.executeQuery("SELECT projectfields_id " +
                                                                                     "from projectfields " +
                                                                                    "where project_id="+projectId+" " +
                                                                                    "AND field_name ='"+fieldName+"'");
            if(getProjFieldIdResultSet.next()){
             fieldId = getProjFieldIdResultSet.getInt(1);
            }
             String SELECT_MARKING_LISTING = "SELECT DISTINCT field_value ,marking FROM listing_occurrence where project_id = ? "
                                             +" AND field_id = ? AND volume_id = ?";
             String SELECT_MARKING_TALLY = "SELECT DISTINCT field_value FROM tally_occurrence where project_id = ? "
                                            +" AND field_id = ?";
             if("Listing".equals(status)|| "ListingQC".equals(status)){
                 pstmt_getListingOccurrenceValues = connection.prepareStatement(SELECT_MARKING_LISTING);              
             }else if("Tally".equals(status) || "TallyQC".equals(status)){
                 pstmt_getListingOccurrenceValues = connection.prepareStatement(SELECT_MARKING_TALLY);   
             }              
             pstmt_getListingOccurrenceValues.setInt(1, projectId); 
             pstmt_getListingOccurrenceValues.setInt(2, fieldId); 
             pstmt_getListingOccurrenceValues.setInt(3, volumeId); 
             pstmt_getListingOccurrenceValues.executeQuery(); 
             getListingOccrncesResultSet=pstmt_getListingOccurrenceValues.getResultSet();
             String SELECT_OCCURENCE_LISTING = "SELECT COUNT(*) FROM listing_occurrence " +
                                                "WHERE field_value = ? AND field_id =? AND volume_id = ?";
             String SELECT_OCCURENCE_TALLY = "SELECT COUNT(*) FROM tally_occurrence " +
                                             "WHERE field_value = ? AND field_id =?";
             
             while(getListingOccrncesResultSet.next()){   
                 //Contains the field values,occurences
                 ArrayList list = new ArrayList();
                 fieldValue = getListingOccrncesResultSet.getString(1);
                 marking = getListingOccrncesResultSet.getString(2);
                 list.add(marking);
                  if("Listing".equals(status)|| "ListingQC".equals(status)){
                 pstmt_getCountListingOccurrence = connection.prepareStatement(SELECT_OCCURENCE_LISTING);              
                 }else if("Tally".equals(status) || "TallyQC".equals(status)){
                     pstmt_getCountListingOccurrence = connection.prepareStatement(SELECT_OCCURENCE_TALLY);   
                 }                    
                 pstmt_getCountListingOccurrence.setString(1, fieldValue);
                 pstmt_getCountListingOccurrence.setInt(2, fieldId);
                 pstmt_getCountListingOccurrence.setInt(3, volumeId);
                 pstmt_getCountListingOccurrence.executeQuery(); 
                 rs_countListingOccurrence=pstmt_getCountListingOccurrence.getResultSet(); 
                 if(rs_countListingOccurrence.next()){
                      occurrence = rs_countListingOccurrence.getInt(1);   
                      list.add(occurrence);
                      ListingData listingData = new ListingData();
                      listingData.setMarking((String)list.get(0));
                      listingData.setOccurrence((Integer)list.get(1));
                      mapForValue.put(fieldValue,listingData);                          
                 }     
             } 
             
             writeXmlFromResult(user, mapForValue, writer, false);
        } catch(ServerFailException excp){        
             CommonLogger.printExceptions(this, "ServerFailException while getting the field values during Listing/Tally.", excp);
             return excp.getMessage();           
        } catch(Exception exc){
             CommonLogger.printExceptions(this, "Exception while getting the field values during Listing/Tally.", exc);
             return null;
        }
        return null;
    }
   
    //Write XML as result
    public  void writeXmlFromResult(UserTask task, Map mapForValue, MessageWriter writer, boolean requestedMetaData)
    throws SQLException, IOException {         
        int columnCount = 3;
        String userSessionId = task.getFossaSessionId();
        
        writer.startElement(T_RESULT_SET);
        writer.writeAttribute(A_FOSSAID, userSessionId);
        writer.writeAttribute(A_COUNT, Integer.toString(columnCount));
        writer.startElement(T_HEADING);

                writer.startElement(T_COLUMN);
                writer.writeContent("field_value");
                writer.endElement();
                
                writer.startElement(T_COLUMN);
                writer.writeContent("field_count");
                writer.endElement();
                
                writer.startElement(T_COLUMN);
                writer.writeContent("marking");
                writer.endElement();
                
        writer.endElement();            
        if(null != mapForValue) {
            Set keys = mapForValue.keySet();
            int i=0;
            for(Iterator iterator = keys.iterator(); iterator.hasNext();) {
                String key = (String)iterator.next();                 
                ListingData listingData = (ListingData)mapForValue.get(key); 
                
                writer.startElement(T_ROW);               
                writer.startElement(T_COLUMN);                 
                writer.writeContent(key); //field_value
                writer.endElement();

                writer.startElement(T_COLUMN);                
                writer.writeContent(listingData.getOccurrence()); //field_count
                writer.endElement();
                
                writer.startElement(T_COLUMN);                
                writer.writeContent(listingData.getMarking()); //marking
                writer.endElement();
                writer.endElement(); //row     
                i++;
            }
        }       
       writer.endElement();//result set
    }
    
    public boolean isReadOnly() {
        return true;
    }

}
