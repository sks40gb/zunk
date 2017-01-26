/*
 * Command_tanlespec.java
 *
 * Created on November 21, 2007, 2:09 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.TablespecData;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class handles the request for table spec
 * @author bmurali
 */
public class Command_tablespec implements Command {

    private PreparedStatement pst;
    private Connection con;
    private Statement st;   

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
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
                //MessageReader.decode(givenValueList, data);]
                XmlReader reader = new XmlReader();
                reader.decode(givenValueList, data);
            } catch (Throwable t) {
                logger.error("Exception while reading the XMLReader." + t);
                StringWriter sw = new StringWriter();
                t.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            }
            if (data != null) {
                // update or insert the tablespec row contained in data
                //Log.print("(Handler_tablespec.run) users_id=" + data.tablespec_id);
                con = dbTask.getConnection();
                st = dbTask.getStatement();
                if (data.project_id > 0) {
                    ResultSet getProjectResultSet = null;
                    try {
                        getProjectResultSet = st.executeQuery("SELECT project_id  FROM project  WHERE project_id =" + data.project_id + "and active = 1");
                        if (!getProjectResultSet.next()) {
                            // project does not exist
                            return null;  // TBD:  set error message?
                        }
                    } catch (SQLException ex) {
                         CommonLogger.printExceptions(this, "Exception while fetching the project name." , ex);
                    }
                }
                if (data.tablespec_id > 0 && data.table_type.equals("")) {
                    ResultSet rs = null;
                    try {
                        rs = st.executeQuery("SELECT TS.table_name, PF.projectfields_id  FROM tablespec TS, projectfields PF " +
                                " WHERE TS.tablespec_id = " + data.tablespec_id + " and TS.tablespec_id = PF.tablespec_id");
                        if (rs.next()) {
                            try {
                                writer.startElement(T_ERROR);
                                String userSessionId = user.getFossaSessionId();
                                writer.writeAttribute(A_FOSSAID, userSessionId);

                                writer.writeAttribute(A_DATA, "Table is in use, so cannot be deleted." + "\n\nRemove table from project fields before deleting.");
                                writer.endElement();
                            } catch (Exception t) {
                               // Log.quit(t);
                                CommonLogger.printExceptions(this, "Exception while getting the table spec name." , t);
                            }
                        } else {
                            //keep the record of table into history table (history_tablespec) before deleting.     
                            data.insertIntoHistoryTable(con, user.getUsersId(), Mode.DELETE);

                            pst = user.prepareStatement(dbTask, SQLQueries.DEL_TSPEC_TSPEC);
                            pst.setInt(1, data.tablespec_id);
                            pst.executeUpdate();
                            pst.close();
                            pst = user.prepareStatement(dbTask, SQLQueries.DEL_TSPEC_TVALUE);
                            pst.setInt(1, data.tablespec_id);
                            pst.executeUpdate();
                            pst.close();
                        }
                        rs.close();
                    } catch (SQLException ex) {
                        CommonLogger.printExceptions(this, "Exception while getting the table spec name." , ex);
                    }
                } else if (data.tablespec_id > 0) {
                    try {
                        // TBD:  edits not implemented
                        // How do we handle changes to existing data?
                        // change existing tablespec                	

                        pst = user.prepareStatement(dbTask, "UPDATE tablespec  SET table_name =? ,table_type = ?, project_id =? " +
                                       ",requirement =? , updateable =? ,model_tablespec_id = ? WHERE tablespec_id =?");
                        pst.setString(1, data.table_name);
                        pst.setString(2, data.table_type);
                        pst.setInt(3, data.project_id);
                        pst.setString(4, data.requirement);
                        pst.setString(5, data.updateable);
                        pst.setInt(6, data.model_tablespec_id);
                        pst.setInt(7, data.tablespec_id);
                        pst.executeUpdate();
                        pst.close();

                        //insert the record into the history table (history_tablespec)
                        data.insertIntoHistoryTable(con, user.getUsersId(), Mode.EDIT);

                    } catch (SQLException ex) {
                        CommonLogger.printExceptions(this, "Exception while updating a table spec." , ex);
                    }
                } else {
                    ResultSet getTableSpecResultSet;
                    try {
                        getTableSpecResultSet = st.executeQuery("select * from tablespec where table_name = '" + data.table_name + "'");
                        if (getTableSpecResultSet.next()) {
                            try {                                
                                writer.startElement(T_ERROR);
                                String userSessionId = user.getFossaSessionId();
                                writer.writeAttribute(A_FOSSAID, userSessionId);
                                writer.writeAttribute(A_DATA,
                                        "Duplicate Entry - Table name already exists");
                                writer.endElement();
                            } catch (Throwable t) {
                                logger.error("Exception while getting the table spec details." + t);
                                StringWriter sw = new StringWriter();
                                t.printStackTrace(new PrintWriter(sw));
                                logger.error(sw.toString());
                                //Log.quit(t);
                            }
                        } else { 
                            // add new user
                            pst = user.prepareStatement(dbTask, "INSERT INTO tablespec(table_name, table_type, project_id, requirement,updateable,model_tablespec_id) VALUES (?,?,?,?,?,?)");

                            pst.setString(1, data.table_name);
                            pst.setString(2, data.table_type);
                            pst.setInt(3, data.project_id);
                            pst.setString(4, data.requirement);
                            pst.setString(5, data.updateable);
                            pst.setInt(6, data.model_tablespec_id);
                            pst.executeUpdate();
                            pst.close();

                            pst = con.prepareStatement("SELECT max(tablespec_id) from tablespec");
                            ResultSet getTableSpecIdResultSet = pst.executeQuery();
                            if (getTableSpecIdResultSet.next()) {
                                data.tablespec_id = getTableSpecIdResultSet.getInt(1);
                            }
                            //insert the record into the history table (history_tablespec)
                            data.insertIntoHistoryTable(con, user.getUsersId(), Mode.ADD);
                        }
                        getTableSpecResultSet.close();
                    } catch (SQLException ex) {
                        CommonLogger.printExceptions(this, "Exception while saving a table spec." , ex);
                    }
                }
            }
        }


        return null;
    }

    public boolean isReadOnly() {
        return false;
    }
}
