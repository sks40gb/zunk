/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.ProjectFieldsData;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.valueobjects.ProjectHistoryData;
import com.fossa.servlet.session.UserTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class is handler for adding,editing,deleting and changing sequence of project fields.
 * @author ashish
 */
class Command_projectfields_data implements Command {

    private PreparedStatement pst;   
    private Connection con;
    private Statement st;
    private UserTask task;
    private DBTask dbTask;
    private ProjectFieldsData data;    

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {
        Log.print("Command_projectfields_data");
        this.task = task;
        this.dbTask = dbTask;
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
                XmlReader xmlReader = new XmlReader();
                xmlReader.decode(givenValueList, data);
            } catch (Throwable t) {
               logger.error("Exception while reading the XMLReader." + t);
               StringWriter sw = new StringWriter();
               t.printStackTrace(new PrintWriter(sw));
               logger.error(sw.toString());
               Log.quit(t);
            }

            if (data != null) {
                // update or insert the project_fields row contained in data
                Log.print("(Command_projectfields_data.run) projectfields_id=" + data.projectfieldsId);

                con = dbTask.getConnection();
                st = dbTask.getStatement();
                //if the mode is delete then delete the record else
                // perform the operation on the basis of moveIndicator
                if (data.mode.equals(Mode.DELETE)) {
                    deleteRow();
                } else if (data.moveIndicator > 0) {
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
        return null;
    }

    //Deltes the selected project field
    private void deleteRow() {
        //before deleting the projectfield insert the record into the table history.
        data.insertIntoHistoryTable(con, task.getUsersId(), Mode.DELETE);
        //delete the record
        String deleteFromTallyMaping ="DELETE from tally_mapping where project_field_id = ?";
        
        
        
        String deleteQuery = "DELETE projectfields  FROM projectfields PF INNER JOIN project on project.project_id = PF.project_id" +
                     " where PF.projectfields_id = ? and (level_field_name is null or level_field_name != field_name)";
        try {
            //Delete from tallymapping          
            PreparedStatement deleteRowFromTallyMaping = con.prepareStatement(deleteFromTallyMaping);
            deleteRowFromTallyMaping.setInt(1, data.projectfieldsId);
            deleteRowFromTallyMaping.executeUpdate();
            
            //Delete from projectfields
            PreparedStatement delProjFieldPrepStmt = task.prepareStatement(dbTask, deleteQuery);
            delProjFieldPrepStmt.setInt(1, data.projectfieldsId);
            delProjFieldPrepStmt.executeUpdate();
        } catch (Exception e) {
            CommonLogger.printExceptions(this, "Exception while deleting the project fields.", e);
        }
    }

    /**
     * Move the current row, identified by data.projectfieldsId, up one position by
     * setting it's sequence to the sequence of the row following it and
     * setting the sequence of the following row to the current row's sequence.
     */
    private void moveRowUp() {
        ResultSet rs = null;
        try {
            //Get the current sequence of the field
            rs = st.executeQuery(SQLQueries.SEL_PRF_SEQ + data.projectfieldsId);
            if (rs.next()) {
                // save the sequence and project_id being updated
                int sequence = rs.getInt(1);
                int project_id = rs.getInt(2);
                rs.close();
                // get the projectfields_id of the next row       
                PreparedStatement getTopProjFieldPrepStmt = con.prepareStatement(SQLQueries.SEL_PRF_TOP);
                getTopProjFieldPrepStmt.setInt(1, project_id);
                getTopProjFieldPrepStmt.setInt(2, sequence);
                rs = getTopProjFieldPrepStmt.executeQuery();

                if (rs.next()) {
                    int projectfields_id = rs.getInt(1);
                    PreparedStatement update_prf_seq = task.prepareStatement(dbTask, SQLQueries.UPD_PRF_SEQ);
                    update_prf_seq.setInt(1, rs.getInt(2));
                    update_prf_seq.setInt(2, data.projectfieldsId);
                    update_prf_seq.executeUpdate();

                    PreparedStatement update_prf_id = task.prepareStatement(dbTask, SQLQueries.UPD_PRF_ID);
                    update_prf_id.setInt(1, sequence);
                    update_prf_id.setInt(2, projectfields_id);
                    update_prf_id.executeUpdate();
                }
                rs.close();
                correctSequence(data.projectId);
            }
        } catch (Throwable t) {
            logger.error("Exception while moving the project fields up." + t);
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
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
            PreparedStatement del_pgissue_pid = con.prepareStatement(SQLQueries.SEL_PRF_SEQUENCE);
            del_pgissue_pid.setInt(1, data.projectfieldsId);
            ResultSet rs = del_pgissue_pid.executeQuery();

            if (rs.next()) {
                // save the sequence and project_id being updated
                int sequence = rs.getInt(1);
                int project_id = rs.getInt(2);
                rs.close();
                // get the projectfields_id of the preceding row          
                PreparedStatement getTopProjFieldIdPrepStmt = con.prepareStatement(SQLQueries.SEL_PRF_PRFID);
                getTopProjFieldIdPrepStmt.setInt(1, project_id);
                getTopProjFieldIdPrepStmt.setInt(2, sequence);
                rs = getTopProjFieldIdPrepStmt.executeQuery();

                if (rs.next()) {
                    int projectfields_id = rs.getInt(1);
                    PreparedStatement update_prf_prfields = task.prepareStatement(dbTask, SQLQueries.UPD_PRF_PRFIELDS);
                    update_prf_prfields.setInt(1, rs.getInt(2));
                    update_prf_prfields.setInt(2, data.projectfieldsId);
                    update_prf_prfields.executeUpdate();

                    PreparedStatement update_prf_prfseq = task.prepareStatement(dbTask, SQLQueries.UPD_PRF_PRFSEQ);
                    update_prf_prfseq.setInt(1, sequence);
                    update_prf_prfseq.setInt(2, projectfields_id);
                    update_prf_prfseq.executeUpdate();
                }
                rs.close();
                correctSequence(data.projectId);
            }
        } catch (Throwable t) {
            logger.error("Exception while moving the project fields down." + t);
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(t);
        }
    }

    //Updates the project field data
    private void updateRow() {
        try {

            String selectQuery = "select validation_functions_group_id from validation_functions_group where group_name =?";
            PreparedStatement getValidationGroupIdPrepStmt = con.prepareStatement(selectQuery);
            getValidationGroupIdPrepStmt.setString(1, data.standardFieldValidations.trim().toUpperCase());
            ResultSet getValidationGroupIdResultSet = getValidationGroupIdPrepStmt.executeQuery();
            while (getValidationGroupIdResultSet.next()) {
                data.std_group_id = getValidationGroupIdResultSet.getInt(1);
            }
            getValidationGroupIdResultSet.close();
            getValidationGroupIdPrepStmt.close();

            //Update the project field data
            pst = task.prepareStatement(dbTask, SQLQueries.UPD_PRF_PRFNAME);
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
            pst.setString(23, data.description);
            pst.setInt(24, data.std_group_id);
            pst.setString(25, data.l1_information);
            pst.setInt(26, data.projectfieldsId);
            pst.executeUpdate();
            pst.close();

            //insert the data into history table also.
            data.insertIntoHistoryTable(con, task.getUsersId(), Mode.EDIT);
            rowAux();
        } catch (Throwable t) {
           logger.error("Exception while updating the project fields." + t);
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(t);
        }
    }
    
    //Adds a new project field
    private void addRow() {
        try {
            String selectQuery = "select validation_functions_group_id from validation_functions_group where group_name =?";
            PreparedStatement getValidationGroupIdPrepStmt = con.prepareStatement(selectQuery);
            getValidationGroupIdPrepStmt.setString(1, data.standardFieldValidations.trim().toUpperCase());
            ResultSet getValidationGroupIdResultSet = getValidationGroupIdPrepStmt.executeQuery();
            while (getValidationGroupIdResultSet.next()) {
                data.std_group_id = getValidationGroupIdResultSet.getInt(1);
            }
            getValidationGroupIdResultSet.close();
            getValidationGroupIdPrepStmt.close();

            int sequence = 1;
            // get the highest sequence for this project
            ResultSet rs = st.executeQuery(SQLQueries.SEL_PRF_MAX + data.projectId);
            if (rs.next()) {
                sequence = rs.getInt(1) + 1;
            }
            rs.close();
            //Adds a new project field
            pst = task.prepareStatement(dbTask, SQLQueries.INS_PRF_PRFIELDS);
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
            //for description
            pst.setString(25, data.description);
            //for standard field validations group            
            pst.setInt(26, data.std_group_id);
            //for standard validation_functions_group_id 
            pst.setString(27, data.l1_information);
            pst.executeUpdate();
            pst.close();

            int field_id = 0;
            ResultSet resultSet = st.executeQuery(SQLQueries.SEL_PRF_PRFIELDS + data.projectId);

            if (resultSet.next()) {
                field_id = resultSet.getInt(1);
                data.projectfieldsId = field_id;
            }
            //insert the data to histoy table
            data.insertIntoHistoryTable(con, task.getUsersId(), Mode.ADD);
            rowAux();
        } catch (Throwable t) {
            logger.error("Exception while adding a new project field." + t);
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(t);
        }
    }

    //Updates the field group
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

               //Update batch set the project field group as active
                PreparedStatement update_prf_batch = task.prepareStatement(dbTask, SQLQueries.UPD_PRF_BATCH);
                update_prf_batch.setInt(1, data.projectId);
                int i = update_prf_batch.executeUpdate();

                Log.print("(Command_projectfields_data.updateRow) " + "Set active_group = 1 in " + i + " batches " + "in project_id " + data.projectId + ".");

                // TBD: Should we not do this automatic update and just give
                //      a warning during projectfields update?
                
                //Update the field group of the selected field
                PreparedStatement update_prf_fgrp = task.prepareStatement(dbTask, SQLQueries.UPD_PRF_FGRP);
                update_prf_fgrp.setInt(1, data.projectId);
                i = update_prf_fgrp.executeUpdate();

                Log.print("(Command_projectfields_data.updateRow) " + "Set field_group = 1 in " + i + " projectfields " + "in project_id " + data.projectId + ".");
            }
            correctSequence(data.projectId);
        } catch (Throwable t) {
            logger.error("Exception while updating the project fields group." + t);
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
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

            //Get the correct sequence for the select project
            ResultSet getFieldSeqResultSet = st.executeQuery("select projectfields_id, sequence" + " from projectfields" + 
                                           " where project_id =" + projectId + "   order by field_group, sequence");
            while (getFieldSeqResultSet.next()) {
                if (getFieldSeqResultSet.getInt(2) != seq) {
                    while (getFieldSeqResultSet.next()) {
                        Log.print("(Command_PF.correctSequence) " + seq + getFieldSeqResultSet.getInt(1));
                        task.executeUpdate(dbTask,
                                "update projectfields set sequence = " + seq + " where projectfields_id = " + getFieldSeqResultSet.getInt(1));
                        seq++;
                    }
                    break;
                } else {
                    seq++;
                }
            }
            getFieldSeqResultSet.close();
        } catch (Throwable t) {
            logger.error("Exception while updating project fields sequence." + t);
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(t);
        }
    }

    //Update the field treatment level i.e. 0 or 1.
    private void updateLevelFieldName() {
        String name = null;
        try {
            ResultSet getFieldLevel = st.executeQuery(
                    "select level_field_name" + " from project" + "   where project_id =" + data.projectId);
            if (getFieldLevel.next()) {
                name = getFieldLevel.getString(1);
                Log.print("(Command_projectfields_data.updateLevelFieldName) " + data.projectId + "/" + data.fieldName + "/" + name + "/" + data.fieldLevel);
                if (data.fieldLevel.equals("*")) {
                    if (!data.fieldName.equals(name)) {
                        // This is the level_field_name and it has changed
                        // for this project.
                        //Update the field level
                        pst = task.prepareStatement(dbTask,
                                "update project set level_field_name = ?" + " where project_id = " + data.projectId);
                        pst.setString(1, data.fieldName);
                        pst.executeUpdate();
                        pst.close();
                    }
                } else if (name != null && name.equals(data.fieldName)) {
                    //Update the field level
                    pst = task.prepareStatement(dbTask,
                            "update project set level_field_name = null" + " where project_id = " + data.projectId);
                    pst.executeUpdate();
                    pst.close();
                }
                //add project history
                ProjectHistoryData projectData = new ProjectHistoryData(con, data.projectId);
                projectData.insertIntoHistoryTable(con, task.getUsersId(), Mode.EDIT);
            }
        } catch (Throwable t) {
            logger.error("Exception while updating the project level_field_name." + t);
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
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
        if (data.fieldLevel.equals("*") || data.fieldLevel.equals("0")) {
            // a row already exists for 0
            return;
        }
        try {
            // see if there is a volume default unitprice row for this field_level            
            PreparedStatement select_prf_uprice = con.prepareStatement(SQLQueries.SEL_PRF_UPRICE);
            select_prf_uprice.setInt(1, data.projectId);
            select_prf_uprice.setString(2, data.fieldLevel);
            ResultSet rs = select_prf_uprice.executeQuery();

            if (!rs.next()) {
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

                PreparedStatement select_prf_unitprice = con.prepareStatement(SQLQueries.SEL_PRF_UNITPRICE);
                select_prf_unitprice.setInt(1, data.projectId);
                rs = select_prf_unitprice.executeQuery();

                if (rs.next()) {
                    u_page = rs.getFloat(1);
                    u_doc = rs.getFloat(2);
                    uqc_page = rs.getFloat(3);
                    uqc_doc = rs.getFloat(4);
                    c_page = rs.getFloat(5);
                    c_doc = rs.getFloat(6);
                    cqc_page = rs.getFloat(7);
                    cqc_doc = rs.getFloat(8);
                }

                pst = task.prepareStatement(dbTask, SQLQueries.INS_PRF_UNITPRICE);
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
                PreparedStatement select_prf_distinct = con.prepareStatement(SQLQueries.SEL_PRF_DISTINCT);
                select_prf_distinct.setInt(1, data.projectId);
                rs = select_prf_distinct.executeQuery();
                pst = task.prepareStatement(dbTask, SQLQueries.INS_PRF_UPRICE);

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
            logger.error("Exception while updating the project unit price." + t);
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
