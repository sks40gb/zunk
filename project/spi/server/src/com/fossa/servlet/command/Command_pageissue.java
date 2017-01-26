/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * This class is handler of page issues operations.
 * @author ashish
 */
class Command_pageissue implements Command{

    private PreparedStatement pst;
    private Connection con;
    private Statement getPageSeqStatement;
    private UserTask task;    
    
    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {      
        this.task = task;
        ResultSet getPageSeqResultSet = null;
        try {
            con = dbTask.getConnection();
            getPageSeqStatement = dbTask.getStatement();
           
            /** page_id of the pageissue to update or insert */
            String page = action.getAttribute(A_PAGE_ID);
            /** 0 if insert, otherwise the sequence of the pageissue to update */
            String sequence = action.getAttribute(A_SEQUENCE);
            /** text to insert or update in the pageissue row */
            String issue = action.getAttribute(A_ISSUE);
            /** -1 if move up; 1 if move down */
            String direction = "";

            if (sequence == null
                || sequence.equals("")) {                
                int max_sequence = 1;
                getPageSeqResultSet = getPageSeqStatement.executeQuery(SQLQueries.SEL_PGISSUE_MAX);
                if (getPageSeqResultSet.next()) {
                    max_sequence = getPageSeqResultSet.getInt(1) + 1;
                }
                //make a new page issue
                pst = con.prepareStatement(SQLQueries.INS_PGISSUE_PGID);
                pst.setString(1, page);
                pst.setInt(2, max_sequence);
                pst.setString(3, issue);
                pst.executeUpdate();
                pst.close();
            } else {
                // delete, edit or move up/down existing page issue
                direction = action.getAttribute(A_DELTA);
                if (direction == null
                    || direction.equals("")) {
                    if (issue.equals("")) {
                        // delete the page issue                        
                        pst = con.prepareStatement(SQLQueries.DEL_PGISSUE_PGID);
                        pst.setString(1, page);
                        pst.setString(2, sequence);
                        pst.executeUpdate();
                        pst.close();
                    } else {
                        // edit the page issue                        
                        pst = con.prepareStatement(SQLQueries.UPD_PGISSUE_NAME);
                        pst.setString(1, issue);
                        pst.setString(2, page);
                        pst.setString(3, sequence);
                        pst.executeUpdate();
                        pst.close();
                    }
                } else {
                    // move up/down a page issue
                    if (direction.equals("-1")) {
                        moveRowUp(page, sequence);
                    } else if (direction.equals("1")) {
                        moveRowDown(page, sequence);
                    }
                }
            }
        } catch (Throwable t) {
            logger.error("Exception while updating the page issue." + t);
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(t);
        }
        return null;
    }

     /**
     * Move the current row, identified by pageissue_id, up one position by
     * setting it's sequence to the sequence of the row following it and
     * setting the sequence of the following row to the current row's sequence.
     */
    private void moveRowUp(String page, String sequence) {
        try {
            // get the page_id of the next row
            PreparedStatement select_pgissue_top =  con.prepareStatement(SQLQueries.SEL_PGISSUE_TOP);
            select_pgissue_top.setString(1, page);
            select_pgissue_top.setString(2, sequence);
            ResultSet rs = select_pgissue_top.executeQuery();
            
            if (rs.next()) {
                int next_sequence = rs.getInt(1);
                String next_issue_name = rs.getString(2);
                // remove the next pageissue row to avoid key duplicate
                PreparedStatement delete_pgissue_seq =  con.prepareStatement(SQLQueries.DEL_PGISSUE_SEQ);
                delete_pgissue_seq.setString(1, page);
                delete_pgissue_seq.setLong(2, next_sequence);
                delete_pgissue_seq.executeUpdate();
                
                // set the sequence of the first row to the sequence of the next
                PreparedStatement update_pgissue_pgid =  con.prepareStatement(SQLQueries.UPD_PGISSUE_PGID);
                update_pgissue_pgid.setInt(1, next_sequence);
                update_pgissue_pgid.setString(2, page);
                update_pgissue_pgid.setString(3, sequence);
                update_pgissue_pgid.executeUpdate();
                
                // insert the previously-removed row with its new sequence
                PreparedStatement insPageIssuePrepStmt = con.prepareStatement(SQLQueries.INS_PGISSUE_SEQ);
                insPageIssuePrepStmt.setString(1, page);
                insPageIssuePrepStmt.setString(2, sequence);
                insPageIssuePrepStmt.setString(3, next_issue_name);
                insPageIssuePrepStmt.executeUpdate();
                insPageIssuePrepStmt.close();
            }
            rs.close();
        } catch (Throwable t) {
            logger.error("Exception while moving the page issue up." + t);
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(t);
        }
    }

    /**
     * Move the current row, identified by pageissue_id, down one position by
     * setting it's sequence to the sequence of the row preceding it and
     * setting the sequence of the preceding row to the current row's sequence.
     */
    private void moveRowDown(String page, String sequence) {  
        try {
            // get the page_id of the preceding row
            PreparedStatement sel_pgissue_seq =  con.prepareStatement(SQLQueries.SEL_PGISSUE_SEQ);
            sel_pgissue_seq.setString(1, page);
            sel_pgissue_seq.setString(2, sequence);
            ResultSet rs = sel_pgissue_seq.executeQuery();
            
            if (rs.next()) {
                int next_sequence = rs.getInt(1);
                String next_issue_name = rs.getString(2);
                // remove the previous pageissue row to avoid key duplicate
                PreparedStatement del_pgissue_pid =  con.prepareStatement(SQLQueries.DEL_PGISSUE_PID);
                del_pgissue_pid.setString(1, page);
                del_pgissue_pid.setInt(2, next_sequence);
                del_pgissue_pid.executeUpdate();
                
                // set the sequence of the first row to the sequence of the second
                PreparedStatement update_pgissue_setseq =  con.prepareStatement(SQLQueries.UPD_PGISSUE_SETSEQ);
                update_pgissue_setseq.setInt(1, next_sequence);
                update_pgissue_setseq.setString(2, page);
                update_pgissue_setseq.setString(3, sequence);
                update_pgissue_setseq.executeUpdate();
                
                // insert the previously-removed row with its new sequence
                PreparedStatement pst = con.prepareStatement(SQLQueries.INS_PGISSUE_ISSUE);
                pst.setString(1, page);
                pst.setString(2, sequence);
                pst.setString(3, next_issue_name);
                pst.executeUpdate();
                pst.close();
            }
            rs.close();
        } catch (Throwable t) {
            logger.error("Exception while moving the page issue down." + t);
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(t);
        }  
    }
    
    public boolean isReadOnly() {
        return true;
    }

}
