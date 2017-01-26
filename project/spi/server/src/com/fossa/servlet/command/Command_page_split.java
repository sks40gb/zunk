/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;


import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.server.BoundaryMapper;
import com.fossa.servlet.server.valueobjects.VolumeHistoryData;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class handles in splitting up the documents
 * @author ashish
 */
class Command_page_split implements Command{

    public Command_page_split() {
    }

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {  
        int volumeId = task.getVolumeId();
        int batchId = task.getBatchId();
        int pageId = Integer.parseInt(action.getAttribute(A_PAGE_ID));
        Connection con = null;
        Statement st = null;
        ResultSet getPageIdResultSet = null;
        try{            
            con = dbTask.getConnection();
            st  = dbTask.getStatement();
            boolean unsplit = "YES".equalsIgnoreCase(action.getAttribute(A_UNSPLIT));
            boolean clone = "YES".equalsIgnoreCase(action.getAttribute(A_CLONE));
            Log.print("page split: unsplit="+unsplit+" clone="+clone+" pageId="+pageId);

            getPageIdResultSet = st.executeQuery(SQLQueries.SEL_PAGE_CHILDID+pageId);
            if (! getPageIdResultSet.next()) {
                throw new ServerFailException("Page not found");
            }
            int childId = getPageIdResultSet.getInt(1);
            boolean isSplit = getPageIdResultSet.getBoolean(2);
            String batesNumber = getPageIdResultSet.getString(3);
            int seq = getPageIdResultSet.getInt(4);
            int lft = getPageIdResultSet.getInt(5);
            getPageIdResultSet.close();
            int batesLength = batesNumber.length();

            int newPageId;
            if (unsplit) {
                Log.print("unsplit: childId="+childId+" isSplit="+isSplit+" batesNumber="+batesNumber);
                if (! isSplit) {
                    throw new ServerFailException("Not a subdocument");
                }
                // after above test, doc must be a child
                // get the prior page
                PreparedStatement select_page_bates =  task.prepareStatement(dbTask,SQLQueries.SEL_PAGE_BATES);
                select_page_bates.setInt(1, seq);
                select_page_bates.setInt(2, volumeId);
                ResultSet getBatesResultSet = select_page_bates.executeQuery();
                
                if (! getBatesResultSet.next()) {
                    throw new ServerFailException("Prior page not found");
                }
                String priorBatesNumber = getBatesResultSet.getString(1);
                int priorseq = getBatesResultSet.getInt(2);
                boolean priorIsSplit = getBatesResultSet.getBoolean(3);
                newPageId = getBatesResultSet.getInt(4);
                getBatesResultSet.close();

                if (priorIsSplit) {
                    priorBatesNumber = priorBatesNumber.substring(0, priorBatesNumber.length() - 3);
                }
                // make it not a child
                // Note: is_split goes away with the child row
                BoundaryMapper.store(task, dbTask, volumeId, pageId, "", true,batchId);
                
                if (priorBatesNumber.length() == (batesLength - 3) && batesNumber.startsWith(priorBatesNumber)) {
                    // this page is a clone, so delete it and adjust boundaries
                    st.executeUpdate(SQLQueries.DEL_PAGE+pageId);
                    adjustRgtValues(task, dbTask, volumeId, seq, priorseq);
                } else {
                    PreparedStatement update_page_bates =  task.prepareStatement(dbTask,SQLQueries.UPD_PAGE_BATES);
                    update_page_bates.setInt(1, (batesLength - 3));
                    update_page_bates.setInt(2, pageId);
                    update_page_bates.executeUpdate();                    
                    newPageId = pageId;
                }
            } else { // since request is to split
                //check that next child is not split
                PreparedStatement sel_is_split =  task.prepareStatement(dbTask,SQLQueries.SEL_PAGE_ISSPLIT);
                sel_is_split.setInt(1, seq);
                sel_is_split.setInt(2, volumeId);
                ResultSet checkIsSplitResultSet = sel_is_split.executeQuery();
                
                if (checkIsSplitResultSet.next() && checkIsSplitResultSet.getBoolean(1)) {
                    checkIsSplitResultSet.close();
                    throw new ServerFailException("Cannot split - already split");
                }
                checkIsSplitResultSet.close();

                //get the new Bates number with suffix
                String newBates;
                if (isSplit && seq==lft) {
                    char suffix = batesNumber.charAt(batesLength - 2);
                    if (suffix == 'Z') {
                        throw new ServerFailException("Cannot split - no suffix letter");
                    }
                    suffix++;
                    newBates = batesNumber.substring(0, batesLength - 2) + suffix + ']';
                } else {
                    newBates = batesNumber + "[A]";
                }

                if (clone || seq == lft) {
                    // clone the page
                    // first make sure we can insert new page
                    PreparedStatement select_page =  task.prepareStatement(dbTask,SQLQueries.SEL_PAGE);
                    select_page.setInt(1, volumeId);
                    select_page.setInt(2, (seq+1));
                    ResultSet getPageResultSet = select_page.executeQuery();
                    
                    if (getPageResultSet.next()) {
                        adjustSeq(task, dbTask, volumeId, seq+1, 100);
                    }
                    getPageResultSet.close();

                    // create a new page
                    PreparedStatement insPagePrepStmt = con.prepareStatement(SQLQueries.INS_PAGE);
                    insPagePrepStmt.setInt(1, seq+1);
                    insPagePrepStmt.setString(2, newBates);
                    insPagePrepStmt.setInt(3, pageId);
                    insPagePrepStmt.executeUpdate();
                    insPagePrepStmt.close();
                    //Get last inserted page id
                    ResultSet getTopPageIDResultSet = st.executeQuery(SQLQueries.SEL_PAGE_PAGEID);
                    getTopPageIDResultSet.next();
                    newPageId = getTopPageIDResultSet.getInt(1);
                    getTopPageIDResultSet.close();

                    adjustRgtValues(task, dbTask, volumeId, seq, seq+1);

                } else {
                    // we are not cloning
                    // update the bates number
                    PreparedStatement ps = con.prepareStatement(SQLQueries.UPD_PAGE_BNO);
                    ps.setString(1, newBates);
                    ps.setInt(2, pageId);
                    ps.executeUpdate();
                    ps.close();

                    newPageId = pageId;
                }

                // make the split page a child
                BoundaryMapper.store(task, dbTask, volumeId, newPageId, "C", true,batchId);

                // add the split flag
                PreparedStatement update_page_bates =  task.prepareStatement(dbTask,SQLQueries.UPD_CHILD_SPLIT);
                update_page_bates.setInt(1, newPageId);
                update_page_bates.executeUpdate();
                
            }
            //Start writing the XML
            String userSessionId = task.getFossaSessionId();
            writer.startElement(T_OK);
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.writeAttribute(A_PAGE_ID, newPageId);
            writer.endElement();
            return null;
        
        } catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while  splitting the page." , sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "SQLException while  splitting the page." , exc);
            return null;
        }
    }
        // open up a hole of size delta in the seq numbers
        // note.  lft used by managed tables for child and batch
        private void adjustSeq(UserTask task, DBTask dbTask, int volumeId, int first, int delta)
        throws SQLException {
            
            Statement st = null;
            ResultSet rs = null;
            try{            
                st = dbTask.getStatement();
                rs = st.executeQuery(SQLQueries.SEL_PAGE_PRID+volumeId);
                rs.next();
                int projectId = rs.getInt(1);
                rs.close();
                
                PreparedStatement update_page_seq =  task.prepareStatement(dbTask,SQLQueries.UPD_PAGE_SEQ);
                update_page_seq.setInt(1, delta);
                update_page_seq.setInt(2, projectId);
                update_page_seq.setInt(3, first);
                update_page_seq.executeUpdate();
                
                PreparedStatement update_page_rgt1 =  task.prepareStatement(dbTask,SQLQueries.UPD_PAGE_RGT1);
                update_page_rgt1.setInt(1, delta);
                update_page_rgt1.setInt(2, first);
                update_page_rgt1.setInt(3, delta);
                update_page_rgt1.setInt(4, projectId);
                update_page_rgt1.setInt(5, first);
                update_page_rgt1.executeUpdate();
                
                PreparedStatement update_page_rgt2 =  task.prepareStatement(dbTask,SQLQueries.UPD_PAGE_RGT2);
                update_page_rgt2.setInt(1, delta);
                update_page_rgt2.setInt(2, first);
                update_page_rgt2.setInt(3, delta);
                update_page_rgt2.setInt(4, projectId);
                update_page_rgt2.setInt(5, first);
                update_page_rgt2.executeUpdate();

                PreparedStatement update_page_rgt3 =  task.prepareStatement(dbTask,SQLQueries.UPD_PAGE_RGT3);
                update_page_rgt3.setInt(1, delta);
                update_page_rgt3.setInt(2, first);
                update_page_rgt3.setInt(3, delta);
                update_page_rgt3.setInt(4, projectId);
                update_page_rgt3.setInt(5, first);
                update_page_rgt3.executeUpdate();                

                PreparedStatement update_page_rgt4 =  task.prepareStatement(dbTask,SQLQueries.UPD_PAGE_RGT4);
                update_page_rgt4.setInt(1, delta);
                update_page_rgt4.setInt(2, first);
                update_page_rgt4.setInt(3, delta);
                update_page_rgt4.setInt(4, projectId);
                update_page_rgt4.setInt(5, first);
                update_page_rgt4.executeUpdate();
                
                PreparedStatement update_pg_projrgt =  task.prepareStatement(dbTask,SQLQueries.UPD_PAGE_PROJRGT);
                update_pg_projrgt.setInt(1, delta);
                update_pg_projrgt.setInt(2, projectId);
                update_pg_projrgt.setInt(3, first);
                update_pg_projrgt.executeUpdate();
                
        } catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while updating the page parameters.", sql);
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while updating the page parameters.", exc);
        }        
    }

    private void adjustRgtValues
            (UserTask task, DBTask dbTask, int volumeId, int oldSeq, int newSeq)
    throws SQLException {                              
            try{                
                PreparedStatement update_pg_childrgt =  task.prepareStatement(dbTask,SQLQueries.UPD_PAGE_CHILDRGT);
                update_pg_childrgt.setInt(1, newSeq);
                update_pg_childrgt.setInt(2, volumeId);
                update_pg_childrgt.setInt(3, oldSeq);
                int count = update_pg_childrgt.executeUpdate();
                
                if (count > 0) {
                    PreparedStatement update_pg_batchrgt =  task.prepareStatement(dbTask,SQLQueries.UPD_PAGE_BATCHRGT);
                    update_pg_batchrgt.setInt(1, newSeq);
                    update_pg_batchrgt.setInt(2, volumeId);
                    update_pg_batchrgt.setInt(3, oldSeq);
                    count = update_pg_batchrgt.executeUpdate();
                    
                    PreparedStatement update_pg_range =  task.prepareStatement(dbTask,SQLQueries.UPD_PAGE_RANGE);
                    update_pg_range.setInt(1, newSeq);
                    update_pg_range.setInt(2, volumeId);
                    update_pg_range.setInt(3, oldSeq);
                    count *= update_pg_range.executeUpdate();                    
                }
                if (count > 0) {
                    PreparedStatement update_page_volrgt =  task.prepareStatement(dbTask,SQLQueries.UPD_PAGE_VOLUMERGT);
                    update_page_volrgt.setInt(1, newSeq);
                    update_page_volrgt.setInt(2, volumeId);
                    update_page_volrgt.setInt(3, oldSeq);
                    count = update_page_volrgt.executeUpdate();
                    
                   VolumeHistoryData volumeData = new VolumeHistoryData(dbTask.getConnection(),volumeId);
                   volumeData.insertIntoHistoryTable(dbTask.getConnection(),task.getUsersId(),Mode.EDIT);                    
                }
                if (count > 0) {
                    PreparedStatement update_page_prgt =  task.prepareStatement(dbTask,SQLQueries.UPD_PAGE_PRGT);
                    update_page_prgt.setInt(1, newSeq);
                    update_page_prgt.setInt(2, volumeId);
                    update_page_prgt.setInt(3, oldSeq);
                    count = update_page_prgt.executeUpdate();                    
                }
         } catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while updating the page rgt value." , sql);
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while updating the page rgt value." , exc);
        }
    }
    
    public boolean isReadOnly() {
        return true;
    }

}
