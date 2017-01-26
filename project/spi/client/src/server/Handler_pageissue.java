/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_pageissue.java,v 1.5.6.2 2006/03/22 20:27:15 nancy Exp $ */
package server;

import common.Log;

import java.sql.*;
import org.w3c.dom.Element;

/**
 * Handler for pageissue message - updates pageissue to insert
 * a row, update a row or resequence an existing pageissue row.
 * @see client.TaskSendPageIssue
 */
final public class Handler_pageissue extends Handler {
    
    PreparedStatement pst;
    Connection con;
    Statement st;
    ServerTask task;

    /**
     * This class cannot be instantiated.
     */
    public Handler_pageissue() {
    }

    public void run (ServerTask task, Element action) {
        this.task = task;
        try {
            con = task.getConnection();
            st = task.getStatement();
            
            Element givenValueList = action;
            int old_teams_id = 0;
            
            /** page_id of the pageissue to update or insert */
            String page = action.getAttribute(A_PAGE_ID);
            /** 0 if insert, otherwise the sequence of the pageissue to update */
            String sequence = action.getAttribute(A_SEQUENCE);
            /** text to insert or update in the pageissue row */
            String issue = action.getAttribute(A_ISSUE);
            /** -1 if move up; 1 if move down */
            String direction = "";

            //Log.print("Handler_pageissue " + page);
            
            if (sequence == null
                || sequence.equals("")) {
                // insert
                int max_sequence = 1;
                ResultSet rs = st.executeQuery(
                            "select MAX(sequence)"
                            +" from pageissue");
                if (rs.next()) {
                    max_sequence = rs.getInt(1) + 1;
                }
                pst = con.prepareStatement(
                    "insert into pageissue"
                    +" (page_id, sequence, issue_name)"
                    +" values (?,?,?)");
                pst.setString(1, page);
                pst.setInt(2, max_sequence);
                pst.setString(3, issue);
                pst.executeUpdate();
                pst.close();
            } else {
                // delete, edit or move up/down
                direction = action.getAttribute(A_DELTA);

                if (direction == null
                    || direction.equals("")) {
                    if (issue.equals("")) {
                        // delete
                        //Log.print("Handler_pageissue update " + page + "/" + sequence + "/" + issue);
                        pst = con.prepareStatement(
                            "delete from pageissue"
                            +" where page_id=?"
                            +"   and sequence=?");
                        pst.setString(1, page);
                        pst.setString(2, sequence);
                        pst.executeUpdate();
                        pst.close();
                    } else {
                        // edit
                        //Log.print("Handler_pageissue update " + page + "/" + sequence + "/" + issue);
                        pst = con.prepareStatement(
                            "update pageissue"
                            +" set issue_name=?"
                            +" where page_id=?"
                            +"   and sequence=?");
                        pst.setString(1, issue);
                        pst.setString(2, page);
                        pst.setString(3, sequence);
                        pst.executeUpdate();
                        pst.close();
                    }
                } else {
                    // move up/down
                    if (direction.equals("-1")) {
                        moveRowUp(page, sequence);
                    } else if (direction.equals("1")) {
                        moveRowDown(page, sequence);
                    }
                }
            }
        } catch (Throwable t) {
            Log.quit(t);
        }
    }

    /**
     * Move the current row, identified by pageissue_id, up one position by
     * setting it's sequence to the sequence of the row following it and
     * setting the sequence of the following row to the current row's sequence.
     */
    private void moveRowUp(String page, String sequence) {
        try {
            // get the page_id of the next row
            ResultSet rs = st.executeQuery(
                    "select sequence, issue_name"
                    +" from pageissue"
                    +"   where page_id ="+page
                    +"     and sequence <"+sequence
                    +"   order by sequence desc"
                    +"   limit 1");
            if (rs.next()) {
                int next_sequence = rs.getInt(1);
                String next_issue_name = rs.getString(2);
                // remove the next pageissue row to avoid key duplicate
                st.executeUpdate(
                    "delete from pageissue"
                    +" where page_id = "+page
                    +"   and sequence = "+next_sequence);
                // set the sequence of the first row to the sequence of the next
                st.executeUpdate(
                        "update pageissue"
                        +" set sequence = "+next_sequence
                        +" where page_id = "+page
                        +"   and sequence = "+sequence);
                // insert the previously-removed row with its new sequence
                PreparedStatement pst = con.prepareStatement(
                    "insert into pageissue"
                    +" (page_id, sequence, issue_name)"
                    +" values (?,?,?)");
                pst.setString(1, page);
                pst.setString(2, sequence);
                pst.setString(3, next_issue_name);
                pst.executeUpdate();
                pst.close();
            }
            rs.close();
        } catch (Throwable t) {
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
            ResultSet rs = st.executeQuery(
                    "select sequence, issue_name"
                    +" from pageissue"
                    +"   where page_id ="+page
                    +"     and sequence >"+sequence
                    +"   order by sequence"
                    +"   limit 1");
            if (rs.next()) {
                int next_sequence = rs.getInt(1);
                String next_issue_name = rs.getString(2);
                // remove the previous pageissue row to avoid key duplicate
                st.executeUpdate(
                    "delete from pageissue"
                    +" where page_id = "+page
                    +"   and sequence = "+next_sequence);
                // set the sequence of the first row to the sequence of the second
                st.executeUpdate(
                        "update pageissue set sequence = "+next_sequence
                        +" where page_id = "+page
                        +"   and sequence = "+sequence);
                // insert the previously-removed row with its new sequence
                PreparedStatement pst = con.prepareStatement(
                    "insert into pageissue"
                    +" (page_id, sequence, issue_name)"
                    +" values (?,?,?)");
                pst.setString(1, page);
                pst.setString(2, sequence);
                pst.setString(3, next_issue_name);
                pst.executeUpdate();
                pst.close();
            }
            rs.close();
        } catch (Throwable t) {
            Log.quit(t);
        }
    }
}
