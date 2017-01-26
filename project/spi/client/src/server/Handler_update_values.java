/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_update_values.java,v 1.12.6.3 2006/03/22 20:27:15 nancy Exp $ */
package server;

import client.MessageMap;
import common.Log;
import common.msg.MessageConstants;
import common.msg.MessageWriter;
import common.msg.XmlUtil;

import java.sql.*;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Handler for update_values message to normalize or delete coded data from
 * <code>value</code> or <code>namevalue</code>.
 * @see client.TaskUpdateValues
 */
final public class Handler_update_values extends Handler {
    String old_fname = "";
    String old_mname = "";
    String old_lname = "";
    String old_org = "";

    String fname = "";
    String mname = "";
    String lname = "";
    String org = "";

    String data = "";
    String old_data = "";

    /**
     * This class cannot be instantiated.
     */
    public Handler_update_values() {}

    public void run (ServerTask task, Element action)
         throws java.io.IOException, java.sql.SQLException {
        Connection con = task.getConnection();
        Statement st = task.getStatement();
        PreparedStatement pst = null;
        ResultSet rs = null;

        int updateCount = 0;
        int count = 0;

        int projectId = Integer.parseInt(action.getAttribute(A_ID));
        int tablespecId = Integer.parseInt(action.getAttribute(A_TABLESPEC_ID));
        String tableType = action.getAttribute(A_TYPE);

        NodeList nl = action.getElementsByTagName(A_DATA);
        if (nl.getLength() > 0) {
            data = XmlUtil.getTextFromNode(nl.item(0));
        }
        nl = action.getElementsByTagName(A_OLD_DATA);
        if (nl.getLength() > 0) {
            old_data = XmlUtil.getTextFromNode(nl.item(0));
        }
        //String data = action.getAttribute(A_DATA);
        //old_data = action.getAttribute(A_OLD_DATA);
        
        Log.print("(Handler_update_values) projectId/table/data/olddata " + projectId 
                  + "/" + tablespecId + "/" + tableType + "/" + data + "/" + old_data);
        if (tableType.equals("name")) {
            parseName(data);
            parseOldName(old_data);
        }
        PreparedStatement pst1;
        if (tableType.equals("text")) {
            pst1 = con.prepareStatement(
                "select C.child_id"
                +"   FROM tablespec TS"
                +"   inner join volume V using (project_id)"
                +"   inner join child C ON (C.volume_id = V.volume_id)"
                +"   inner join projectfields PF ON (PF.project_id = V.project_id and PF.tablespec_id = TS.tablespec_id)"
                +"   inner join value NVa ON (NVa.child_id = C.child_id  and NVa.field_name = PF.field_name)"
                +"   where TS.tablespec_id = ?"
                +"     and TS.project_id in (0, ?)"
                +"     and NVa.value = ?"
                );
            pst1.setString(3, old_data);
        } else {
            pst1 = con.prepareStatement(
                //"select C.child_id from project P, volume V, child C, namevalue NVa,"
                "select C.child_id"
                +"   FROM tablespec TS"
                +"   inner join volume V using (project_id)"
                +"   inner join child C ON (C.volume_id = V.volume_id)"
                +"   inner join projectfields PF ON (PF.project_id = V.project_id and PF.tablespec_id = TS.tablespec_id)"
                +"   inner join namevalue NVa ON (NVa.child_id = C.child_id  and NVa.field_name = PF.field_name)"
                +"   where TS.tablespec_id = ?"
                +"     and TS.project_id in (0, ?)"
                +"     and NVa.first_name = ?"
                +"     and NVa.middle_name = ?"
                +"     and NVa.last_name = ?"
                +"     and NVa.organization = ?"
                );
            pst1.setString(3, old_fname);
            pst1.setString(4, old_mname);
            pst1.setString(5, old_lname);
            pst1.setString(6, old_org);
        }
        pst1.setInt(1, tablespecId);
        pst1.setInt(2, projectId);
        rs = pst1.executeQuery();
        if ( (old_data != null
              || old_data.length() > 0)
            && (data == null
                || data.length() < 1) ) {
            // delete value, longvalue or namevalue for given
            // project, or all projects if projectId is 0
            Log.print("(Hander_update_values) delete " + projectId + "/" + old_data);
            if (tableType.equals("text")) {
                pst = con.prepareStatement(
                    "delete from value"
                    +"  where child_id = ?"
                    +"    and value = ?");
                while (rs.next()) {
                    pst.setInt(1, rs.getInt(1));
                    pst.setString(2, old_data);
                    updateCount = updateCount + pst.executeUpdate();
                }
            } else {
                pst = con.prepareStatement(
                    "delete from namevalue"
                    +"  where child_id = ?"
                    +"   and first_name = ?"
                    +"   and middle_name = ?"
                    +"   and last_name = ?"
                    +"   and organization = ?");
                while (rs.next()) {
                    pst.setInt(1, rs.getInt(1));
                    pst.setString(2, old_fname);
                    pst.setString(3, old_mname);
                    pst.setString(4, old_lname);
                    pst.setString(5, old_org);
                    updateCount = updateCount + pst.executeUpdate();
                }
            }
            pst.close();
        } else if (data != null
                   && data.length() > 0) {
            // update value or namevalue
            Log.print("(Hander_update_values) update " + projectId + "/" + data);
            if (tableType.equals("text")) {
                pst = con.prepareStatement(
                    "update value"
                    +" set value = ?"
                    +" where child_id = ?"
                    +"   and value = ?");
                while (rs.next()) {
                    Log.print("(Hander_update_values) update project/child "
                              + projectId + "/" + rs.getInt(1));
                    pst.setString(1, data);
                    pst.setInt(2, rs.getInt(1));
                    pst.setString(3, old_data);
                    updateCount = updateCount + pst.executeUpdate();
                }
                pst.close();
            } else {
                // name
                pst = con.prepareStatement(
                    "update namevalue"
                    +" set first_name = ?,"
                    +"   middle_name = ?,"
                    +"   last_name = ?,"
                    +"   organization = ?,"
                    +"   value = ?"
                    +" where child_id = ?"
                    +"   and first_name = ?"
                    +"   and middle_name = ?"
                    +"   and last_name = ?"
                    +"   and organization = ?"
                    +"   and value = ?"
                    );
                while (rs.next()) {
                    Log.print("(Hander_update_values) update name "
                              + projectId + "/" + data);
                    pst.setString(1, fname);
                    pst.setString(2, mname);
                    pst.setString(3, lname);
                    pst.setString(4, org);
                    pst.setString(5, data);
                    pst.setInt(6, rs.getInt(1));
                    pst.setString(7, old_fname);
                    pst.setString(8, old_mname);
                    pst.setString(9, old_lname);
                    pst.setString(10, old_org);
                    pst.setString(11, old_data);
                    updateCount = updateCount + pst.executeUpdate();
                }
                pst.close();
            }
        }

        task.commitTransaction();
        // return the update count to the client
        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_UPDATE_COUNT);
        writer.writeAttribute(A_COUNT, updateCount);
        writer.endElement();
    }

    private void parseName(String nameData) {
        int slashPos = nameData.lastIndexOf('/');
        // If no name, discard the slash - use org as name
        if (slashPos == 0) {
            nameData = nameData.substring(1).trim();
            slashPos = -1;
        }
        if (slashPos >= 0) {
            if (slashPos < nameData.length()) {
                org = nameData.substring(slashPos + 1).trim();
            }
            nameData = nameData.substring(0,slashPos).trim();
        }
        int commaPos = nameData.indexOf(",");
        if (commaPos == 0) {
            nameData = nameData.substring(1).trim();
            commaPos = -1;
        }
        if (commaPos < 0) {
            lname = nameData;
        } else {
            lname = nameData.substring(0, commaPos).trim();
            nameData = nameData.substring(commaPos + 1).trim();
            int spacePos = nameData.lastIndexOf(' ');
            if (spacePos < 0) {
                fname = nameData.trim();
            } else {
                fname = nameData.substring(0,spacePos).trim();
                mname = nameData.substring(spacePos + 1).trim();
            }
        }
    }

    private void parseOldName(String nameData) {
        int slashPos = nameData.lastIndexOf('/');
        // If no name, discard the slash - use org as name
        if (slashPos == 0) {
            nameData = nameData.substring(1).trim();
            slashPos = -1;
        }
        if (slashPos >= 0) {
            if (slashPos < nameData.length()) {
                old_org = nameData.substring(slashPos + 1).trim();
            }
            nameData = nameData.substring(0,slashPos).trim();
        }
        int commaPos = nameData.indexOf(",");
        if (commaPos == 0) {
            nameData = nameData.substring(1).trim();
            commaPos = -1;
        }
        if (commaPos < 0) {
            old_lname = nameData;
        } else {
            old_lname = nameData.substring(0, commaPos).trim();
            nameData = nameData.substring(commaPos + 1).trim();
            int spacePos = nameData.lastIndexOf(' ');
            if (spacePos < 0) {
                old_fname = nameData.trim();
            } else {
                old_fname = nameData.substring(0,spacePos).trim();
                old_mname = nameData.substring(spacePos + 1).trim();
            }
        }
    }
}
