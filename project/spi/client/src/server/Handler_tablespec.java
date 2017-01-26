/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_tablespec.java,v 1.8.6.4 2006/03/22 20:27:15 nancy Exp $ */
package server;

import common.Log;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import common.TablespecData;

import java.sql.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for tablespec message; allows addition of a new tablespec, editing of existing
 * tables and deletion of a tablespec with its associated tablevalues.
 */
final public class Handler_tablespec extends Handler implements common.msg.MessageConstants {
    
    PreparedStatement pst;
    Connection con;
    Statement st;

    /**
     * This class cannot be instantiated.
     */
    public Handler_tablespec() {
    }

    public void run (ServerTask task, Element action) throws SQLException {
        //Log.print("Handler_tablespec");
        Element givenValueList = action;
        int old_teams_id = 0;

        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild != null) {
            TablespecData data = new TablespecData();
            // fill in the int and String fields of the TablespecData
            try {
                MessageReader.decode(givenValueList, data);
            } catch (Throwable t) {
                Log.quit(t);
            }
            if (data != null) {
                // update or insert the tablespec row contained in data
                //Log.print("(Handler_tablespec.run) users_id=" + data.tablespec_id);
                con = task.getConnection();
                st = task.getStatement();
                if (data.project_id > 0) {
                    // tablespec for a specific project -- see if the project exists
                    ResultSet rs = st.executeQuery(
                        "select project_id"
                        +" from project"
                        +" where project_id ="+data.project_id
                        +"   and active");
                    if (! rs.next()) {
                        // project does not exist
                        return;  // TBD:  set error message?
                    }
                }
                if (data.tablespec_id > 0
                    && data.table_type.equals("")) {
                    ResultSet rs = st.executeQuery(
                        "select TS.table_name, PF.projectfields_id"
                        +" from tablespec TS, projectfields PF"
                        +"  where TS.tablespec_id = "+data.tablespec_id
                        +"    and TS.tablespec_id = PF.tablespec_id");
                    if (rs.next()) {
                        try {
                            MessageWriter writer = task.getMessageWriter();
                            writer.startElement(T_ERROR);
                            writer.writeAttribute(A_DATA
                                  , "Table is in use, so cannot be deleted."
                                  +"\n\nRemove table from project fields before deleting.");
                            writer.endElement();
                        } catch (Throwable t) {
                            Log.quit(t);
                        }
                    } else {
                        pst = task.prepareStatement(
                            "delete from tablespec"
                            +" where tablespec_id = ?");
                        pst.setInt(1, data.tablespec_id);
                        pst.executeUpdate();
                        pst.close();
                        pst = task.prepareStatement(
                            "delete from tablevalue"
                            +" where tablespec_id = ?");
                        pst.setInt(1, data.tablespec_id);
                        pst.executeUpdate();
                        pst.close();
                    }
                    rs.close();
                } else if (data.tablespec_id > 0) {
                    // TBD:  edits not implemented
                    // How do we handle changes to existing data?
                    // change existing tablespec
                    pst = task.prepareStatement(
                        "update tablespec set table_name = ?,"
                        +" table_type = ?, project_id = ?,"
                        +" requirement = ?, updateable = ?,"
                        +" model_tablespec_id = ?"
                        +" where tablespec_id = ?");
                    pst.setString(1, data.table_name);
                    pst.setString(2, data.table_type);
                    pst.setInt(3, data.project_id);
                    pst.setString(4, data.requirement);
                    pst.setString(5, data.updateable);
                    pst.setInt(6, data.model_tablespec_id);
                    pst.setInt(7, data.tablespec_id);
                    pst.executeUpdate();
                    pst.close();
                } else {
                    ResultSet rs = st.executeQuery(
                            "select * from tablespec where table_name = '" 
                            + data.table_name + "'");
                    if (rs.next()) {
                        try {
                            MessageWriter writer = task.getMessageWriter();
                            writer.startElement(T_ERROR);
                            writer.writeAttribute(A_DATA, 
                                "Duplicate Entry - Table name already exists");
                            writer.endElement();
                        } catch (Throwable t) {
                            Log.quit(t);
                        }
                    } else { // ends here
                        // add new user
                        pst = task.prepareStatement(
                            "insert into tablespec"
                            +" (table_name, table_type, project_id, requirement,"
                            +" updateable, model_tablespec_id)"
                            +" values (?,?,?,?,?,?)");
                        pst.setString(1, data.table_name);
                        pst.setString(2, data.table_type);
                        pst.setInt(3, data.project_id);
                        pst.setString(4, data.requirement);
                        pst.setString(5, data.updateable);
                        pst.setInt(6, data.model_tablespec_id);
                        pst.executeUpdate();
                        pst.close();
                    }
                    rs.close(); // changed here
                } // changed here
            }
        }
    }
}
