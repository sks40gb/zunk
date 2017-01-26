/*
 * Command_tablevalue.java
 *
 * Created on November 21, 2007, 1:02 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.TablevalueData;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * This class handles the table value
 * @author bmurali
 */
public class Command_tablevalue implements Command {

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        Connection con = dbTask.getConnection();        
        PreparedStatement pst;

        TablevalueData data = new TablevalueData();
        Element givenValueList = action;
        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild != null) {
            // fill in the int and String fields of DelimiterData            
            XmlReader xmlreader = new XmlReader();
            try {
                xmlreader.decode(givenValueList, data);
            } catch (IOException ex) {
                CommonLogger.printExceptions(this, "", ex);
            }
            if (data == null) {
                return null;
            }
        } else {
            return null;
        }
        if (!data.value.equals("") && data.old_value.equals("")) {            
            try {
                // add a new table value
                pst = user.prepareStatement(dbTask, SQLQueries.INS_TBVALUE_TBVAL);
                pst.setInt(1, data.tablespec_id);
                pst.setString(2, data.value);
                pst.setInt(3, data.level);
                pst.setString(4, data.model_value);
                pst.executeUpdate();
                pst.close();
            } catch (SQLException e) {                                            
            }
        } else if (!data.old_value.equals("") && !data.value.equals("")) {            
            try {
                System.out.println("Inside Update");
                System.out.println("data.value  :" +data.value);
                System.out.println("data.model_value  :" +data.model_value);
                System.out.println("data.tablespec_id :" +data.tablespec_id);
                System.out.println("data.old_value  :" +data.old_value);
                //update existing row
                pst = user.prepareStatement(dbTask, SQLQueries.UPD_TBVALUE_TBVAL);
                pst.setString(1, data.value);
                pst.setInt(2, data.level);
                pst.setString(3, data.model_value);
                pst.setInt(4, data.tablespec_id);
                pst.setString(5, data.old_value);
                pst.executeUpdate();
                pst.close();
            } catch (SQLException e) {                
                String sqlState = e.getSQLState();
                int errorCode = e.getErrorCode();
            }
        } else if (!data.old_value.equals("")) {            
            String tableType = "";
            try {
                pst = con.prepareStatement(
                        "select table_type from tablespec" + " where tablespec_id = ?");
                pst.setInt(1, data.tablespec_id);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    tableType = rs.getString(1);
                }
                pst.close();
                pst = user.prepareStatement(dbTask,
                        "delete from tablevalue" + " where tablespec_id = ?" + "   and value = ?");
                pst.setInt(1, data.tablespec_id);
                pst.setString(2, data.old_value);
                pst.executeUpdate();
                pst.close();

            } catch (SQLException ex) {
                CommonLogger.printExceptions(this, "", ex);
            }
        }
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }
}
