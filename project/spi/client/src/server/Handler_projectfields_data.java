/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_projectfields_data.java,v 1.11.2.10 2006/03/22 20:27:15 nancy Exp $ */
package server;

//import client.MessageMap;
import common.Log;
import common.msg.MessageReader;
import common.ProjectFieldsData;

import java.sql.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for project_fields_data message that inserts or updates
 * the <code>projectfields</code> table.
 * @see common.ProjectFieldsData
 * @see client.TaskSendProjectFieldsData
 */
final public class Handler_projectfields_data extends Handler {
    ServerTask task;
    PreparedStatement pst;
    Connection con;
    Statement st;

    ProjectFieldsData data;

    /**
     * This class cannot be instantiated.
     */
    public Handler_projectfields_data() {
    }

    /**
     * Determine from the data in ProjectFieldsData, the action to
     * be taken and dispatch to the correct method.
     * 
     * data.moveIndicator > 0 means move the row up
     * data.moveIndicator < 0 means move the row down
     *
     * When data.moveIndicator == 0, the row is being inserted or updated
     * and a sequence of 0 means it is being inserted.
     */
    public void run (ServerTask task, Element action) {
        Log.print("Handler_projectfields_data");
        this.task = task;
        Element givenValueList = action;
        
        //Note.  "child" may be ignored white space, if not validating parser
        //if (givenValueList.hasChildNodes()) {
        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild != null) {
            // fill in the int and String fields of ProjectFieldsData
            data = new ProjectFieldsData();
            try {
                MessageReader.decode(givenValueList, data);
            } catch (Throwable t) {
                Log.quit(t);
            }

            if (data != null) {
                // update or insert the project_fields row contained in data
                Log.print("(Handler_projectfields_data.run) projectfields_id=" + data.projectfieldsId);
                
                con = task.getConnection();
                st = task.getStatement();
                
                if (data.moveIndicator > 0) {
                    moveRowUp();
                } else if (data.moveIndicator < 0) {
                    moveRowDown();
                } else if (data.sequence == 0) {
                    addRow();
                } else {
                    updateRow();
                }
            }
        }
    }

    /**
     * Move the current row, identified by data.projectfieldsId, up one position by
     * setting it's sequence to the sequence of the row following it and
     * setting the sequence of the following row to the current row's sequence.
     */
    private void moveRowUp() {
        try {
            ResultSet rs = st.executeQuery(
                        "select sequence, project_id"
                        +" from projectfields"
                        +"   where projectfields_id ="+data.projectfieldsId);
            if (rs.next()) {
                // save the sequence and project_id being updated
                int sequence = rs.getInt(1);
                int project_id = rs.getInt(2);
                rs.close();
                // get the projectfields_id of the next row
                rs = st.executeQuery(
                        "select projectfields_id, sequence"
                        +" from projectfields"
                        +"   where project_id ="+project_id
                        +"     and sequence <"+sequence
                        +"   order by sequence desc"
                        +"   limit 1");
                if (rs.next()) {
                    int projectfields_id = rs.getInt(1);
                    // set the sequence of the first row to the sequence of the second
                    //pst = con.prepareStatement(
                    task.executeUpdate(
                            "update projectfields set sequence = "+rs.getInt(2)
                            +" where projectfields_id = "+data.projectfieldsId);
                    //Tables.projectfields.executeUpdate(task,data.projectfieldsId,pst);
                    //pst.close();
                    // set the sequence of the second row to the sequence of the first
                    //pst = con.prepareStatement(
                    task.executeUpdate(
                            "update projectfields set sequence = "+sequence
                            +" where projectfields_id = "+projectfields_id);
                    //Tables.projectfields.executeUpdate(task,projectfields_id,pst);
                    //pst.close();
                }
                rs.close();
                correctSequence(data.projectId);
            }
        } catch (Throwable t) {
            Log.quit(t);
        }
    }

    /**
     * Move the current row, identified by data.projectfieldsId, down one position by
     * setting it's sequence to the sequence of the row following it and
     * setting the sequence of the following row to the current row's sequence.
     */
    private void moveRowDown() {
        try {
            ResultSet rs = st.executeQuery(
                        "select sequence, project_id"
                        +" from projectfields"
                        +"   where projectfields_id ="+data.projectfieldsId);
            if (rs.next()) {
                // save the sequence and project_id being updated
                int sequence = rs.getInt(1);
                int project_id = rs.getInt(2);
                rs.close();
                // get the projectfields_id of the preceding row
                rs = st.executeQuery(
                        "select projectfields_id, sequence"
                        +" from projectfields"
                        +"   where project_id ="+project_id
                        +"     and sequence >"+sequence
                        +"   order by sequence"
                        +"   limit 1");
                if (rs.next()) {
                    int projectfields_id = rs.getInt(1);
                    // set the sequence of the first row to the sequence of the second
                    //pst = con.prepareStatement(
                    task.executeUpdate(
                            "update projectfields set sequence = "+rs.getInt(2)
                            +" where projectfields_id = "+data.projectfieldsId);
                    //Tables.projectfields.executeUpdate(task,data.projectfieldsId,pst);
                    //pst.close();
                    // set the sequence of the second row to the sequence of the first
                    //pst = con.prepareStatement(
                    task.executeUpdate(
                            "update projectfields set sequence = "+sequence
                            +" where projectfields_id = "+projectfields_id);
                    // Tables.projectfields.executeUpdate(task,projectfields_id,pst);
                    //pst.close();
                }
                rs.close();
                correctSequence(data.projectId);
            }
        } catch (Throwable t) {
            Log.quit(t);
        }
    }

    private void updateRow() {
        try {
            pst = task.prepareStatement(
                    "update projectfields set field_name = ?,"
                    +" field_size = ?, minimum_size = ?, field_type = ?, required = ?,"
                    +" repeated = ?, unitize = ?, default_value = ?,"
                    +" min_value = ?, max_value = ?, tablespec_id = ?,"
                    +" table_mandatory = ?, mask = ?, valid_chars = ?, invalid_chars = ?,"
                    +" charset = ?, type_field = ?, type_value = ?, spell_check = ?,"
                    +" field_level = ?, field_group = ?, tag_name = ?"
                    +" where projectfields_id = ?");
            pst.setString(1, data.fieldName);
            pst.setInt(2, data.fieldSize);
            pst.setInt(3, data.minimumSize);
            pst.setString(4, data.fieldType);
            pst.setString(5, data.required);
            pst.setString(6, data.repeated);
            pst.setString(7, data.unitize);
            pst.setString(8, data.defaultValue);
            pst.setString(9, data.minValue);
            pst.setString(10, data.maxValue);
            //pst.setString(10, data.tableName);
            pst.setInt(11, data.tablespecId);
            pst.setString(12, data.tableMandatory);
            pst.setString(13, data.mask);
            pst.setString(14, data.validChars);
            pst.setString(15, data.invalidChars);
            pst.setString(16, data.charset);
            pst.setString(17, data.typeField);
            pst.setString(18, data.typeValue);
            pst.setString(19, data.spellCheck);
            pst.setInt(20, data.fieldLevel.equals("*") ? 0 : Integer.parseInt(data.fieldLevel));
            pst.setInt(21, data.fieldGroup);
            pst.setString(22, data.tagName);
            pst.setInt(23, data.projectfieldsId); // where
            pst.executeUpdate();
            pst.close();

            rowAux();
        } catch (Throwable t) {
            Log.quit(t);
        }
    }

    private void addRow() {
        try {
            int sequence = 1;
            // get the highest sequence for this project
            ResultSet rs = st.executeQuery(
                        "select max(sequence)"
                        +" from projectfields"
                        +"   where project_id ="+data.projectId);
            if (rs.next()) {
                sequence = rs.getInt(1) + 1;
            }

            // add new user
            pst = task.prepareStatement(
                "insert into projectfields (project_id, sequence, field_name,"
                    +" field_size, minimum_size, field_type, required,"
                    +" repeated, unitize, default_value,"
                    +" min_value, max_value, tablespec_id,"
                    +" table_mandatory, mask, valid_chars, invalid_chars,"
                    +" charset, type_field, type_value, spell_check, field_level,"
                    +" field_group, tag_name)"
                +" values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            pst.setInt(1, data.projectId);
            pst.setInt(2, sequence);
            pst.setString(3, data.fieldName);
            pst.setInt(4, data.fieldSize);
            pst.setInt(5, data.minimumSize);
            pst.setString(6, data.fieldType);
            pst.setString(7, data.required);
            pst.setString(8, data.repeated);
            pst.setString(9, data.unitize);
            pst.setString(10, data.defaultValue);
            pst.setString(11, data.minValue);
            pst.setString(12, data.maxValue);
            //pst.setString(12, data.tableName);
            pst.setInt(13, data.tablespecId);
            pst.setString(14, data.tableMandatory);
            pst.setString(15, data.mask);
            pst.setString(16, data.validChars);
            pst.setString(17, data.invalidChars);
            pst.setString(18, data.charset);
            pst.setString(19, data.typeField);
            pst.setString(20, data.typeValue);
            pst.setString(21, data.spellCheck);
            pst.setInt(22, data.fieldLevel.equals("*") ? 0 : Integer.parseInt(data.fieldLevel));
            pst.setInt(23, data.fieldGroup);
            pst.setString(24, data.tagName);
            pst.executeUpdate();
            pst.close();

            rowAux();
        } catch (Throwable t) {
            Log.quit(t);
        }
    }

    private void rowAux() {
        try {
            updateLevelFieldName();
            updateUnitprice();

            // If one projectfields.field_group is greater than one,
            //   all must be.
            // If one projectfields.field_group is greater than one,
            //   all batch.active_groups must be > 0.
            if (data.fieldGroup > 0) {
                // TBD: There is no place for the user to update
                //      batch.active_group and the viewer will not detect
                //      groups if active_group is 0.
                int i = task.executeUpdate(
                        "update batch B, volume V set active_group = 1"
                        +" where V.project_id = "+data.projectId
                        +"  and B.volume_id = V.volume_id"
                        +"  and B.active_group = 0");
                Log.print("(Handler_projectfields_data.updateRow) "
                          +"Set active_group = 1 in " + i + " batches "
                          +"in project_id " + data.projectId + ".");

                // TBD: Should we not do this automatic update and just give
                //      a warning during projectfields update?
                i = task.executeUpdate(
                        "update projectfields set field_group = 1"
                        +" where project_id = "+data.projectId
                        +"  and field_group = 0");
                Log.print("(Handler_projectfields_data.updateRow) "
                          +"Set field_group = 1 in " + i + " projectfields "
                          +"in project_id " + data.projectId + ".");
            }

            correctSequence(data.projectId);
        } catch (Throwable t) {
            Log.quit(t);
        }
    }

    /**
     * Because of the need to sequence by projectfields.field_group first,
     * a field can be added that is out of order, so here we are checking
     * that sequences ascend when sorted by field_group, sequence.
     */
    private void correctSequence(int projectId) {
        try {
            int seq = 1;
            ResultSet rs = st.executeQuery(
                    "select projectfields_id, sequence"
                    +" from projectfields"
                    +"   where project_id ="+projectId
                    +"   order by field_group, sequence");
            while (rs.next()) {
                if (rs.getInt(2) != seq) {
                    // resequence from here
                    rs.previous();
                    while (rs.next()) {
                        Log.print("(Handler_PF.correctSequence) " + seq +
                                          rs.getInt(1));
                        task.executeUpdate(
                                "update projectfields set sequence = "+seq
                                +" where projectfields_id = "+rs.getInt(1));
                        seq++;
                    }
                    break;
                } else {
                    seq++;
                }
            }
            rs.close();
        } catch (Throwable t) {
            Log.quit(t);
        }
    }

    private void updateLevelFieldName() {
        String name = null;
        try{
            ResultSet rs = st.executeQuery(
                        "select level_field_name"
                        +" from project"
                        +"   where project_id ="+data.projectId);
            if (rs.next()) {
                name = rs.getString(1);
                Log.print("(Handler_projectfields_data.updateLevelFieldName) "
                          + data.projectId + "/" + data.fieldName
                          + "/" + name + "/" + data.fieldLevel);
                if (data.fieldLevel.equals("*")) {
                    if (! data.fieldName.equals(name)) {
                        // This is the level_field_name and it has changed
                        // for this project.
                        pst = task.prepareStatement(
                                "update project set level_field_name = ?"
                                +" where project_id = " + data.projectId);
                        pst.setString(1, data.fieldName);
                        pst.executeUpdate();
                        pst.close();
                    }
                } else if (name != null
                           && name.equals(data.fieldName)) {
                    // This is not the level_field_name, but it's name
                    // is in the project as such.  Remove it.
                    pst = task.prepareStatement(
                            "update project set level_field_name = null"
                            +" where project_id = " + data.projectId);
                    pst.executeUpdate();
                    pst.close();
                }
            }
        } catch (Throwable t) {
            Log.quit(t);
        }
    }

    /**
     * Add a row of zero prices to the unitprice table for each volume and
     * the default volume for every new field_level.
     * 
     * TBD:  When do we worry about deleting unused unitprice rows?
     */
    private void updateUnitprice() {
        if (data.fieldLevel.equals("*")
            || data.fieldLevel.equals("0")) {
            // a row already exists for 0
            return;
        }
        try{
            // see if there is a volume default unitprice row for this field_level
            ResultSet rs = st.executeQuery(
                        "select unitprice_id"
                        +" from unitprice"
                        +"   where project_id ="+data.projectId
                        +"     and volume_id = 0"
                        +"     and field_level ="
                        + Integer.parseInt(data.fieldLevel));
            if (! rs.next()) {
                // the row does not exist; add it

                float u_page = 0;
                float u_doc = 0;
                float uqc_page = 0;
                float uqc_doc = 0;
                float c_page = 0;
                float c_doc = 0;
                float cqc_page = 0;
                float cqc_doc = 0;

                // get the default prices for level 0
                rs = st.executeQuery("select unitize_page_price, unitize_doc_price,"
                                     +" uqc_page_price, uqc_doc_price,"
                                     +" coding_page_price, coding_doc_price,"
                                     +" codingqc_page_price, codingqc_doc_price"
                                     +" from unitprice"
                                     +" where project_id ="+data.projectId
                                     +"  and volume_id = 0 and field_level = 0");
                if (rs.next()) {
                    u_page = rs.getFloat(1);
                    u_doc  = rs.getFloat(2);
                    uqc_page = rs.getFloat(3);
                    uqc_doc  = rs.getFloat(4);
                    c_page = rs.getFloat(5);
                    c_doc  = rs.getFloat(6);
                    cqc_page = rs.getFloat(7);
                    cqc_doc  = rs.getFloat(8);
                }

                pst = task.prepareStatement(
                    "insert into unitprice"
                    +" (project_id, volume_id, field_level"
                    +", unitize_page_price, unitize_doc_price"
                    +", uqc_page_price, uqc_doc_price"
                    +", coding_page_price, coding_doc_price"
                    +", codingqc_page_price, codingqc_doc_price)"
                    +" values (?,0,?,?,?,?,?,?,?,?,?)");
                pst.setInt(1, data.projectId);
                pst.setInt(2, Integer.parseInt(data.fieldLevel));
                pst.setFloat(3, u_page);
                pst.setFloat(4, u_doc);
                pst.setFloat(5, uqc_page);
                pst.setFloat(6, uqc_doc);
                pst.setFloat(7, c_page);
                pst.setFloat(8, c_doc);
                pst.setFloat(9, cqc_page);
                pst.setFloat(10, cqc_doc);
                pst.executeUpdate();
                pst.close();
                
                // add a row with this level for each volume in the project
                rs = st.executeQuery(" select distinct V.volume_id"
                                     +"  from project P"
                                     +"  left join volume V using (project_id)"
                                     +"  where P.project_id ="+data.projectId
                                     +"    and P.active and V.sequence > 0");
                pst = task.prepareStatement(
                     "insert into unitprice"
                     +" (project_id, volume_id, field_level"
                     +", unitize_page_price, unitize_doc_price"
                     +", uqc_page_price, uqc_doc_price"
                     +", coding_page_price, coding_doc_price"
                     +", codingqc_page_price, codingqc_doc_price)"
                     +" values (?,?,?,?,?,?,?,?,?,?,?)");
                while (rs.next()) {
                    pst.setInt(1, data.projectId);
                    pst.setInt(2, rs.getInt(1));
                    pst.setInt(3, Integer.parseInt(data.fieldLevel));
                    pst.setFloat(4, u_page);
                    pst.setFloat(5, u_doc);
                    pst.setFloat(6, uqc_page);
                    pst.setFloat(7, uqc_doc);
                    pst.setFloat(8, c_page);
                    pst.setFloat(9, c_doc);
                    pst.setFloat(10, cqc_page);
                    pst.setFloat(11, cqc_doc);
                    pst.executeUpdate();
                }
                pst.close();
                rs.close();
            }
        } catch (Throwable t) {
            Log.quit(t);
        }
    }
}
