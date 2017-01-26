/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CodingData;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * This class handles the page requests for 'Coding'
 * @author ashish
 */
class Command_request_coding implements Command{
    
    private PreparedStatement getPagePrepStmt = null;    
    private PreparedStatement getBatchIdPrepStmt = null;
    private PreparedStatement getProjectL1PrepStmt = null;
    private ResultSet getPageResultSet = null;    
    private ResultSet getBatchIdResultSet = null;
    private ResultSet getProjectL1ResultSet = null;
    private Connection connection;
    private int volumeId = 0;
    private int pageId=0;
    private int childId = 0;
    private int  batchId=0;
     
    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {    
        int givenPageId = Integer.parseInt(action.getAttribute(A_PAGE_ID));
        int delta       = Integer.parseInt(action.getAttribute(A_DELTA));
        int boundary    = Integer.parseInt(action.getAttribute(A_BOUNDARY));        
        
        String status = action.getAttribute(A_STATUS);
        //added for Listing
         if("Listing".equals(status) || "TallyQC".equals(status)){
            int i = 0;
            String bate = action.getAttribute(A_BATE);
            volumeId = Integer.parseInt(action.getAttribute(A_VOLUME_ID));
            connection = dbTask.getConnection();            
            try {                                
                getPagePrepStmt = connection.prepareStatement("SELECT page_id,child_id FROM page WHERE bates_number = ? AND volume_id=?");
                getPagePrepStmt.setString(1, bate);
                getPagePrepStmt.setInt(2, volumeId);
                getPagePrepStmt.executeQuery();
                getPageResultSet = getPagePrepStmt.getResultSet();
                while (getPageResultSet.next()) {
                    i++;
                    pageId = getPageResultSet.getInt(1);
                    childId =getPageResultSet.getInt(2);                    
                }
                if(i == 0){
                   getProjectL1PrepStmt = connection.prepareStatement("SELECT page_id,child_id FROM project_l1" +
                                                                      " WHERE bates_number = ? AND volume_id=?");
                   getProjectL1PrepStmt.setString(1, bate);
                   getProjectL1PrepStmt.setInt(2, volumeId);
                   getProjectL1PrepStmt.executeQuery();
                   getProjectL1ResultSet = getProjectL1PrepStmt.getResultSet();
                   while (getProjectL1ResultSet.next()) {
                       i++;
                       pageId = getProjectL1ResultSet.getInt(1);
                       childId =getProjectL1ResultSet.getInt(2);                       
                   }
                }
                getBatchIdPrepStmt= connection.prepareStatement("SELECT batch_id  FROM child WHERE child_id = ?");
                getBatchIdPrepStmt.setInt(1, childId);
                getBatchIdPrepStmt.executeQuery();
                getBatchIdResultSet = getBatchIdPrepStmt.getResultSet();
                while (getBatchIdResultSet.next()) {
                    batchId = getBatchIdResultSet.getInt(1);                    
                }                
             } catch (SQLException ex) {
                CommonLogger.printExceptions(this, "Exception while getting the page id during listing." , ex);
            }
         }//ends
        
        int pos;  //Contains the page id
        try{
            MarshallPage m = MarshallPage.makeInstance(task, dbTask, action);
            Log.print("Request coding: "+givenPageId+" "+delta+" "+boundary+" volumeId="+volumeId);
            if("Listing".equals(status) || "TallyQC".equals(status)){
                pos=pageId;                               
                CodingData data = null;
            if (pos != 0) {                  
                data = m.collectCodingData(pos,volumeId,batchId);                  
                if ("QA".equals(status)) {
                    data.errorFlagMap = m.collectErrorFlagData(data.childId);
                    data.errorTypeMap = m.collectErrorTypeData(data.childId);
                }
            }             
            if (data != null) {
                ValueMapper valueMapper = new ValueMapper();
                String userSessionId = task.getFossaSessionId();
                writer.startElement(T_CODING_DATA);
                writer.writeAttribute(A_FOSSAID, userSessionId);
                writer.encode(CodingData.class, data);
                valueMapper.write(task, dbTask, writer, volumeId, data.childId);
                if (data.errorFlagMap != null) {
                    valueMapper.writeErrorFlags(task, dbTask, writer, data.errorFlagMap);
                }
                if(data.errorTypeMap != null){
                   valueMapper.writeErrorTypes(task, dbTask, writer, data.errorTypeMap);
                }               
                writer.endElement();                
            } else {
                String userSessionId = task.getFossaSessionId();
                writer.startElement(T_FAIL);
                writer.writeAttribute(A_FOSSAID, userSessionId);                              
                writer.writeContent("End of Batch");
                writer.endElement();
            }                 
            }else{
                int []  possition = new int [2];
                if (givenPageId == 0) {
                    if (delta == 0) {
                        assert boundary == 0;                        
                        pos = m.findUncoded();  // TBD: remove use of magic numbers 
                    } else {                                              
                        possition = m.findPositionInBatch(delta, boundary);                        
                    }
                } else {
                    possition = m.findRelativeInBatch(givenPageId, delta, boundary);
                }        
            CodingData data = null;
            if (possition[0] != 0) {
                data = m.collectCodingData(possition);
                if ("QA".equals(status)) {
                    data.errorFlagMap = m.collectErrorFlagData(data.childId);
                    data.errorTypeMap = m.collectErrorTypeData(data.childId);
                }
            }
             
            if (data != null) {
                ValueMapper valueMapper = new ValueMapper();
                String userSessionId = task.getFossaSessionId();
                writer.startElement(T_CODING_DATA);
                writer.writeAttribute(A_FOSSAID, userSessionId);
                writer.encode(CodingData.class, data);
                //Get the field values of both (L1 and L2)
                valueMapper.write(task, dbTask, writer, m.getVolumeId(), data.childId,possition[0],data.treatment_level,status,givenPageId);
                if (data.errorFlagMap != null) {
                    valueMapper.writeErrorFlags(task, dbTask, writer, data.errorFlagMap);
                }
                if(data.errorTypeMap != null){
                   valueMapper.writeErrorTypes(task, dbTask, writer, data.errorTypeMap);
                } 
                writer.endElement();
                
            } else {
                String userSessionId = task.getFossaSessionId();
                writer.startElement(T_FAIL);
                writer.writeAttribute(A_FOSSAID, userSessionId);  
                if("QA".equals(status)){
                  writer.writeContent("End of sampling document");
                }else{
                  writer.writeContent("Page not found");
                }
                writer.endElement();
            }
        }
        } catch (IOException exc) {
            CommonLogger.printExceptions(this, "IOException while getting the coding data." , exc);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while getting the coding data." , exc);
            return null;
        }   
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
