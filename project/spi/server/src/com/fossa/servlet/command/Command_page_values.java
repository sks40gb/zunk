/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.client.MessageMap;
import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.BoundaryMapper;
import com.fossa.servlet.server.EventLog;
import com.fossa.servlet.session.UserTask;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for page_values message.  Handles updating of the values in coding fields
 * by calling BoundaryMapper to store values.  This class also updates counts kept
 * for changes made by QCer and QAers.
 * @see BoundaryMapper
 * @see client.TaskSendCodingData
 * @see ui.SplitPaneViewer
 * @author ashish
 */
class Command_page_values implements Command{

    private boolean rework = false;
    private boolean childcoded = false;

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {    
        int volumeId = task.getVolumeId();
        int batchId = task.getBatchId();        
        int givenPageId = Integer.parseInt(action.getAttribute(A_PAGE_ID));
        String orginal_status = action.getAttribute(A_STATUS);        //Holds the batch status
         System.out.println("batchId========="+ batchId);
        System.out.println("orginal_status======="+ orginal_status);
        String processLevel = "";	//Holds the treatment level i.e L1 OR L2
	Connection con =  dbTask.getConnection();
        // for event entry:
        // child_count, page_count, rows_deleted, field_count       
        Statement st = null;
        ResultSet rs = null;        
        try{
            st = dbTask.getStatement();            
            if (action.hasAttribute(A_BOUNDARY_FLAG)) {
                // if boundary_flag attribute doesn't exist,
                // the user hasn't updated it.
                String givenBoundaryFlag = action.getAttribute(A_BOUNDARY_FLAG);
                BoundaryMapper.store(task, dbTask, volumeId, givenPageId, givenBoundaryFlag.trim(),batchId);
            }             
            ResultSet getTreatmentLevelResultSet = st.executeQuery("select batch_id,treatment_level from batch where batch_id="+batchId);
             if(getTreatmentLevelResultSet.next()){
               processLevel = getTreatmentLevelResultSet.getString(2);            
            }
           
            // find the corresponding child for this page
            if(orginal_status.equals("QA")){                 
                rs = st.executeQuery (
                "select child_id"
                +" from page"
                +" where page_id="+givenPageId);
            }
            else{
               if(processLevel.equals("L1")){
                rs = st.executeQuery (
                "select child_id"
                +" from project_l1"
                +" where project_l1_id="+givenPageId);
            }else{
                rs = st.executeQuery (
                "select child_id"
                +" from page"
                +" where page_id="+givenPageId);
            }
            }            
            if (! rs.next()) {
                Log.quit("Command_page_values: child not found");
            }
            int childId = rs.getInt(1);            
            rs.close();

            Node actionChild = action.getFirstChild();
            Element valueList = null;
            Element errorFlagList = null;
	    Element errorTypeList = null;
            while (actionChild != null) { 
                //Note.  actionChild may be ignored white space, if not validating parser
                if (actionChild.getNodeType() == Node.ELEMENT_NODE) {
                    if (T_VALUE_LIST.equals(actionChild.getNodeName())) {
                        valueList = (Element) actionChild;
                    } else if (T_ERROR_FLAG_LIST.equals(actionChild.getNodeName())) {
                        errorFlagList = (Element) actionChild;
                        break;
                    }
                }
                actionChild = actionChild.getNextSibling();
		}
        
             while (actionChild != null) { 
                //Note.  actionChild may be ignored white space, if not validating parser
                 if (T_ERROR_TYPE_LIST.equals(actionChild.getNodeName())) {
                   errorTypeList = (Element) actionChild;
                   break;
                 }
                actionChild = actionChild.getNextSibling(); 
            }
            
            String status = null;
            int pageCount = 0;
            //Get the batch status,page count and rework for the child
             if(orginal_status.equals("QA")){                  
                rs = st.executeQuery(
                    "select B.status, count(P.page_id), U.rework, U.qa_rework"
                    +" from child C"
                    +" left join page P on (P.child_id = C.child_id)"
                    +" left join batch B on (B.batch_id = C.batch_id)"
                    +" left join batchuser U on (U.batch_id = B.batch_id)"
                    +" where C.child_id = "+childId
                    +" group by B.batch_id, C.child_id, B.status, U.rework, U.qa_rework");
             }else{
                if(processLevel.equals("L1")){
                    rs = st.executeQuery(
                    "select B.status, count(P.project_l1_id), U.rework, U.qa_rework"
                    +" from child C"
                    +" left join project_l1 P on (P.child_id = C.child_id)"
                    +" left join batch B on (B.batch_id = C.batch_id)"
                    +" left join batchuser U on (U.batch_id = B.batch_id)"
                    +" where C.child_id = "+childId
                    +" group by B.batch_id, C.child_id, B.status, U.rework, U.qa_rework");
                }else{
                     rs = st.executeQuery(
                    "select B.status, count(P.page_id), U.rework, U.qa_rework"
                    +" from child C"
                    +" left join page P on (P.child_id = C.child_id)"
                    +" left join batch B on (B.batch_id = C.batch_id)"
                    +" left join batchuser U on (U.batch_id = B.batch_id)"
                    +" where C.child_id = "+childId
                    +" group by B.batch_id, C.child_id, B.status, U.rework, U.qa_rework");
                }
             }
            if (rs.next()) {
                status = rs.getString(1);
                pageCount = rs.getInt(2);
                rework = rs.getInt(3) == 1 || rs.getInt(4) == 1;
            }
            rs.close();
                
            if (valueList != null) {                
                Map valueMap = MessageMap.decode(valueList);                
                ValueMapper.store(task, dbTask, writer, volumeId, childId, valueMap
                                  ,status, pageCount, rework , processLevel);
            } else {
                Log.print("(Command_page_values.run) no valueList");                   
            }

            if (errorFlagList != null) {
                Map errorFlagMap = MessageMap.decode(errorFlagList);    //Holds the error flags
                Map errorTypeMap = MessageMap.decode(errorTypeList);    //Holds the error types
                ValueMapper.storeErrorFlags(task, dbTask, volumeId, childId, errorFlagMap,errorTypeMap);
            }

            // check to see if this child has been updated by this user before.
            if ("CodingQC".equals(status) || "QA".equals(status)) {
                rs = st.executeQuery(SQLQueries.SEL_VALUES_USERID +childId);
                if (rs.next()
                    && rs.getInt(1) == task.getUsersId()
                    && status.equals(rs.getString(2))) { // is this necessary?
                    childcoded = true;
                }
                rs.close();
            }

            if (task.isAdmin()) {
                // do not record saved child
            } else if (batchId == 0) {                
                // record saved child for QA
                // batchId should always be set, EXCEPT for QA
                // TBD: This is a bit of a kludge!!!

                // Insert users_id, etc, if this child has not
                // been saved for QA before.  (In which case,
                // there should be a childcoded record
                // with users_id = 0)
                // status = "QA";
                PreparedStatement update_values_child =  con.prepareStatement(SQLQueries.UPD_VALUES_CHILD);
                update_values_child.setInt(1, task.getUsersId());
                update_values_child.setLong(2, System.currentTimeMillis());
                update_values_child.setInt(3, childId);
                update_values_child.executeUpdate();                
            } else {
                // get the batch status
                // TBD: this should probably be cached or saved in session                
                if ("Coding".equals(status)
                || "CodingQC".equals(status) || "Masking".equals(status) || "ModifyErrors".equals(status)) {
                    // for coding, QC, QA remember who saved
                    ResultSet rs_childid = st.executeQuery("select count(*) from childcoded where child_id =" +childId);
                    rs_childid.next();
                    int child_id =  rs_childid.getInt(1);
                    
                    if(child_id == 0){
                        //make a new entry for the coded child
                        PreparedStatement insChildCodedPrepStmt =  con.prepareStatement(SQLQueries.INS_VALUES_DEC);
                        insChildCodedPrepStmt.setInt(1, childId);
                        insChildCodedPrepStmt.setString(2, status);
                        insChildCodedPrepStmt.setInt(3, task.getUsersId());
                        insChildCodedPrepStmt.setLong(4, System.currentTimeMillis());
                        insChildCodedPrepStmt.executeUpdate();
                    }
                    else{
                        //or update the exisitng child status
                        PreparedStatement updateChildCodedPrepStmt =  con.prepareStatement(SQLQueries.UPD_VALUES_DEC);
                        updateChildCodedPrepStmt.setString(1, status);
                        updateChildCodedPrepStmt.setInt(2, task.getUsersId());
                        updateChildCodedPrepStmt.setLong(3, System.currentTimeMillis());
                        updateChildCodedPrepStmt.setInt(4, childId);
                        updateChildCodedPrepStmt.executeUpdate();                    
                    }
                    
                } else if ("Unitize".equals(status) || "UQC".equals(status)) {
                    // for Unitize, UnitizeQC record highest saved
                    // First determine if new page higher than recorded                
                    ResultSet getPageResultSet;
                  if(orginal_status.equals("QA")){
                     getPageResultSet = st.executeQuery("SELECT 0 FROM page P0, batchuser U WITH(UPDLOCK) left join page P on " +
                                           "P.page_id = U.last_unitized_page_id WHERE P0.page_id="+givenPageId+" " +
                                           "and U.batch_id= "+batchId+" and (U.last_unitized_page_id = 0 or P0.seq > P.seq)");
                  }
                  else{
                    if(processLevel.equals("L1")){
			getPageResultSet = st.executeQuery("SELECT 0 FROM project_l1 P0, batchuser U WITH(UPDLOCK) " +
                                                           "left join project_l1 P on P.project_l1_id = U.last_unitized_page_id " +
                                                           "WHERE P0.project_l1_id="+givenPageId+" and U.batch_id= "+batchId+"" +
                                                           " and (U.last_unitized_page_id = 0 or P0.seq > P.seq)");
                    }else{
                    	getPageResultSet = st.executeQuery("SELECT 0 FROM page P0, batchuser U WITH(UPDLOCK) left join page P " +
                                                            "on P.page_id = U.last_unitized_page_id " +
                                                            "WHERE P0.page_id="+givenPageId+" and U.batch_id= "+batchId+" " +
                                                            "and (U.last_unitized_page_id = 0 or P0.seq > P.seq)");
                    }			
                  }                
                    // now update page_unitized if required
                    if (getPageResultSet.next()) {
                        st.executeUpdate(
                            "update batchuser"
                            +" set last_unitized_page_id="+givenPageId
                            +" where batch_id="+batchId);     
                    }                    
                }
            }
            if ("CodingQC".equals(status)
            || "QA".equals(status)) {
                Log.print("(Command_page_values.run) CodingQC or QA");
                // get the level
                // TBD: level and field count really should be cached]
                PreparedStatement getFieldLevelPrepStmt =  con.prepareStatement(SQLQueries.SEL_VALUES_TV);
                getFieldLevelPrepStmt.setInt(1, childId);
                getFieldLevelPrepStmt.setInt(2, volumeId);
                ResultSet resultset = getFieldLevelPrepStmt.executeQuery();
                
                int level = (resultset.next() ? resultset.getInt(1) : 0);
                // now get the field count for that level
                PreparedStatement getFieldCountPrepStmt=  con.prepareStatement(SQLQueries.SEL_VALUES_COUNTP);
                getFieldCountPrepStmt.setInt(1, volumeId);
                getFieldCountPrepStmt.setInt(2, level);
                resultset = getFieldCountPrepStmt.executeQuery();
                
                resultset.next();
                int fieldCount = resultset.getInt(1);
                // get the change and error counts for this child
                // TBD should we get this from the message instead of the DB?
                // we only count fields for the proper level
                if (level == 0) {
                    resultset = st.executeQuery(SQLQueries.SEL_VALUES_COUNTF +childId);
                } else {
                    // TBD check that this indexes on fieldchange first
                    PreparedStatement getErrorCountPrepStmt =  con.prepareStatement(SQLQueries.SEL_VALUES_COUNTFC);
                    getErrorCountPrepStmt.setInt(1, childId);
                    getErrorCountPrepStmt.setInt(2, volumeId);
                    getErrorCountPrepStmt.setInt(3, level);
                    resultset = getErrorCountPrepStmt.executeQuery();                    
                }
                resultset.next();
                int changeCount = resultset.getInt(1);
                int errorCount = resultset.getInt(2);
                resultset.close();                
                // TBD: for qc and qa, should we track actual updates or all fields?
                if (! childcoded && ! rework) {
                    // If there is no childcoded row and it is not rework for this childId,
                    // make an entry in event to record this CodingQC or QA action.
                    EventLog.add(task, dbTask,volumeId, batchId, status
                                 , /* child count */ 1, pageCount, fieldCount); 
                }

                Log.print("(Command_page_values.run) write childerror "
                          + fieldCount + "/" + changeCount + "/" + errorCount);
                // roll up the change and error counts
                PreparedStatement updateChildErrorPrepStmt =  con.prepareStatement(SQLQueries.UPDATE_CHILDERROR_VALUES);
                updateChildErrorPrepStmt.setInt(1, fieldCount);
                updateChildErrorPrepStmt.setInt(2, changeCount);
                updateChildErrorPrepStmt.setInt(3, errorCount);
                updateChildErrorPrepStmt.setInt(4, childId);
                updateChildErrorPrepStmt.executeUpdate();                
            }           
        } catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while updating the page values." , sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while updating the page values." , exc);
            return null;
        }  
    return null;
    }

    public boolean isReadOnly() {
        return true;
    }

}
