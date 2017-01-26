/*
 * Command_update_values.java
 *
 * Created on November 20, 2007, 5:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlUtil;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class handles the add,update,delete of value/longvalue/namevalue table
 * @author bmurali
 */
public class Command_update_values implements Command {

    private String old_fname = "";
    private String old_mname = "";
    private String old_lname = "";
    private String old_org = "";
    private String fname = "";
    private String mname = "";
    private String lname = "";
    private String org = "";
    private String data = "";
    private String old_data = "";

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        Connection con = dbTask.getConnection();
        Statement st = dbTask.getStatement();
        PreparedStatement pst = null;
        ResultSet getChildIdResultSet = null;

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
        if (tableType.equals("name")) {
            parseName(data);
            parseOldName(old_data);
        }
        try {
            PreparedStatement getChildIdPrepStmt;
            if (tableType.equals("text")) {
                getChildIdPrepStmt = con.prepareStatement(SQLQueries.SEL_UPDVAL_CID);
                getChildIdPrepStmt.setString(3, old_data);
            } else {
                getChildIdPrepStmt = con.prepareStatement(SQLQueries.SEL_UPDVAL_CHILDID);
                getChildIdPrepStmt.setString(3, old_fname);
                getChildIdPrepStmt.setString(4, old_mname);
                getChildIdPrepStmt.setString(5, old_lname);
                getChildIdPrepStmt.setString(6, old_org);
            }
            getChildIdPrepStmt.setInt(1, tablespecId);
            getChildIdPrepStmt.setInt(2, projectId);
            getChildIdResultSet = getChildIdPrepStmt.executeQuery();
        } catch (SQLException ex) {
            CommonLogger.printExceptions(this, "Exception while getting the child id from table spec.", ex);
        }

        if ((old_data != null || old_data.length() > 0) && (data == null || data.length() < 1)) {
            // delete value, longvalue or namevalue for given
            // project, or all projects if projectId is 0            
            try {
                if (tableType.equals("text")) {
                    pst = con.prepareStatement(SQLQueries.DEL_UPDVAL_CHILDID);
                    while (getChildIdResultSet.next()) {
                        pst.setInt(1, getChildIdResultSet.getInt(1));
                        pst.setString(2, old_data);
                        updateCount = updateCount + pst.executeUpdate();
                    }
                } else {
                    pst = con.prepareStatement(SQLQueries.DEL_UPDVAL_NAMEVALUE);
                    while (getChildIdResultSet.next()) {
                        pst.setInt(1, getChildIdResultSet.getInt(1));
                        pst.setString(2, old_fname);
                        pst.setString(3, old_mname);
                        pst.setString(4, old_lname);
                        pst.setString(5, old_org);
                        updateCount = updateCount + pst.executeUpdate();
                    }
                }
                pst.close();
            } catch (SQLException ex) {                
                CommonLogger.printExceptions(this,"SQLException while deleting the value/namevalue table." , ex);
            }
        } else if (data != null && data.length() > 0) {
            // update value or namevalue            
            try {
                if (tableType.equals("text")) {
                    pst = con.prepareStatement(SQLQueries.UPD_UPDVAL_VAL);
                    while (getChildIdResultSet.next()) {                        
                        pst.setString(1, data);
                        pst.setInt(2, getChildIdResultSet.getInt(1));
                        pst.setString(3, old_data);
                        updateCount = updateCount + pst.executeUpdate();
                    }
                    pst.close();
                } else {
                    pst = con.prepareStatement(SQLQueries.UPD_UPDVAL_NAMEVALUE);
                    while (getChildIdResultSet.next()) {                        
                        pst.setString(1, fname);
                        pst.setString(2, mname);
                        pst.setString(3, lname);
                        pst.setString(4, org);
                        pst.setString(5, data);
                        pst.setInt(6, getChildIdResultSet.getInt(1));
                        pst.setString(7, old_fname);
                        pst.setString(8, old_mname);
                        pst.setString(9, old_lname);
                        pst.setString(10, old_org);
                        pst.setString(11, old_data);
                        updateCount = updateCount + pst.executeUpdate();
                    }
                    pst.close();
                }
            } catch (SQLException ex) {
                CommonLogger.printExceptions(this,"SQLException while updating the value/namevalue table." , ex);
            }
        }
        try {
            dbTask.commitTransaction(user);
        } catch (SQLException ex) {            
            CommonLogger.printExceptions(this, "SQLException while committing a transaction.", ex);
        }
        try {
            // return the update count to the client                  
            String userSessionId = user.getFossaSessionId();
            writer.startElement(T_UPDATE_COUNT);
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.writeAttribute(A_COUNT, updateCount);
            writer.endElement();
        } catch (IOException ex) {
            CommonLogger.printExceptions(this, "Exception while writing result in XML.", ex);
        }
        return null;
    }

    public boolean isReadOnly() {
        return false;
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
            nameData = nameData.substring(0, slashPos).trim();
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
                fname = nameData.substring(0, spacePos).trim();
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
            nameData = nameData.substring(0, slashPos).trim();
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
                old_fname = nameData.substring(0, spacePos).trim();
                old_mname = nameData.substring(spacePos + 1).trim();
            }
        }
    }
}
