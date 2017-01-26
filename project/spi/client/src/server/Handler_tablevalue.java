/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_tablevalue.java,v 1.17.2.3 2006/03/22 20:27:15 nancy Exp $ */
package server;

import client.MessageMap;
import common.Log;
import common.TablevalueData;
import common.msg.MessageReader;

import java.io.IOException;
import java.sql.*;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for tablevalue message to add, update or delete a single\
 * <code>tablevalue</code> row.
 * @see common.TablevalueData
 * @see client.TaskSendTablevalue
 */
final public class Handler_tablevalue extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_tablevalue() {}

    public void run (ServerTask task, Element action) throws SQLException, IOException {
        Connection con = task.getConnection();
        Statement st = task.getStatement();
        PreparedStatement pst;

        // TBD: THis isn't right, yet.
        // Regular user should be able to add, but not change (including
        //   capitalization)
        // Admin or QA can delete or modify.  QC can probably change
        //   or delete for non-mandatory tables, but not do 
        //   multiple replace.
        // Maybe we need a mechanism--a new kind of TableMap--which
        //   allows changes to tables to be deferred until the user
        //   saves.
        TablevalueData data = new TablevalueData();
        Element givenValueList = action;
        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild != null) {
            // fill in the int and String fields of DelimiterData
            MessageReader.decode(givenValueList, data);
            if (data == null) {
                return;
            }
        } else {
            return;
        }
        
        //String tableName = action.getAttribute(A_NAME);
        //String data = action.getAttribute(A_DATA);
        //int level = Integer.parseInt(action.getAttribute(A_LEVEL));
        //String old_data = action.getAttribute(A_OLD_DATA);
        //Log.print("(Handler_tablevalue) " + tableName + "/" + data
        //          + "/" + old_data);
        if (! data.value.equals("")
            && data.old_value.equals("")) {
            //Log.print("(Hander_tablevalue) insert");
            try {
                pst = task.prepareStatement(
                    "insert into tablevalue"
                    +" (tablespec_id, value, field_level, model_value)"
                    +" values (?, ?, ?, ?)");
                pst.setInt(1, data.tablespec_id);
                pst.setString(2, data.value);
                pst.setInt(3, data.level);
                pst.setString(4, data.model_value);
                pst.executeUpdate();
                pst.close();
            } catch (SQLException e) {
                String sqlState = e.getSQLState();
                int errorCode = e.getErrorCode();
                // Suppress the print -- we get lots of these, and they are OK -- wbe 2005-04-28
                //Log.print(">>>"+e+" sqlState="+sqlState+" errorCode="+errorCode);
                if (errorCode == ServerTask.ER_DUP_ENTRY ) {
                    // it's a dup, ignore it
                    Log.print("(Handler_tablevalue.run) duplicate key");
                } else {
                    throw e;
                }
            }
        } else if (! data.old_value.equals("")
            && ! data.value.equals("")) {
            //Log.print("(Hander_tablevalue) update");
            try {
                pst = task.prepareStatement(
                    "update tablevalue set value = ?, field_level = " + data.level
                    +" , model_value = ?"
                    +" where tablespec_id = ?"
                    +"   and value = ?");
                pst.setString(1, data.value);
                pst.setString(2, data.model_value);
                pst.setInt(3, data.tablespec_id);
                pst.setString(4, data.old_value);
                pst.executeUpdate();
                pst.close();
            } catch (SQLException e) {
                // ignore dups for now
                String sqlState = e.getSQLState();
                int errorCode = e.getErrorCode();
                Log.print(">>>"+e+" sqlState="+sqlState+" errorCode="+errorCode);
                if (errorCode == ServerTask.ER_DUP_ENTRY ) {
                    // it's a dup, ignore it
                    Log.print("(Handler_tablevalue.run#2)  key");
                } else {
                    throw e;
                }
            }
        } else if (! data.old_value.equals("")) {
            //Log.print("(Hander_tablevalue) delete");
            // Delete removes the value from tablevalue, then removes all
            // occurrences of the value from the data for each project.
            //String yes_no = action.getAttribute(A_CHANGE_DATA);
            //boolean change_data = yes_no.equals("YES") ? true : false;
            String tableType = "";
            pst = con.prepareStatement(
                "select table_type from tablespec"
                +" where tablespec_id = ?");
            pst.setInt(1, data.tablespec_id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                tableType = rs.getString(1);
            }
            pst.close();
            rs.close();

            pst = task.prepareStatement(
                "delete from tablevalue"
                +" where tablespec_id = ?"
                +"   and value = ?");
            pst.setInt(1, data.tablespec_id);
            pst.setString(2, data.old_value);
            pst.executeUpdate();
            pst.close();
        }
    }
}
