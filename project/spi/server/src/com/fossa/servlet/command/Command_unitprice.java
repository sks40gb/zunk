/*
 * Command_unitprice.java
 *
 * Created on November 21, 2007, 12:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.UnitpriceData;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import org.w3c.dom.*;

/**
 * This class handles the updation of unit price
 * @author bmurali
 */
public class Command_unitprice implements Command {

    private PreparedStatement pst;

    public String execute(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        Element givenValueList = action;

        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) {
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild != null) {
            UnitpriceData data = new UnitpriceData();
            // fill in the int and String fields of the UnitpriceData
            XmlReader reader = new XmlReader();
            try {
                reader.decode(givenValueList, data);
            } catch (Throwable t) {
                logger.error("Exception while reading the XML in unitprice." + t);
                StringWriter sw = new StringWriter();
                t.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
                Log.quit(t);
            }
            if (data != null) {
                // update or insert the unitprice row contained in data                               
                if (data.unitprice_id > 0) {
                    // update existing unitprice
                    try {
                        pst = user.prepareStatement(dbTask, SQLQueries.UPD_UPRICE_UPRCIE);
                        pst.setInt(1, getInt(data.unitize_page_price));
                        pst.setInt(2, getInt(data.unitize_doc_price));
                        pst.setInt(3, getInt(data.uqc_page_price));
                        pst.setInt(4, getInt(data.uqc_doc_price));
                        pst.setInt(5, getInt(data.coding_page_price));
                        pst.setInt(6, getInt(data.coding_doc_price));
                        pst.setInt(7, getInt(data.codingqc_page_price));
                        pst.setInt(8, getInt(data.codingqc_doc_price));
                        pst.setInt(9, data.unitprice_id);
                        pst.executeUpdate();
                        pst.close();
                    } catch (SQLException e) {                        
                        CommonLogger.printExceptions(this, "Exception while updating the unit price.", e);
                    }

                } else {
                    // add new price
                    try {
                        pst = user.prepareStatement(dbTask, SQLQueries.INS_UPRICE_PID);
                        pst.setInt(1, data.project_id);
                        pst.setInt(2, data.volume_id);
                        pst.setInt(3, data.field_level);
                        pst.setInt(4, getInt(data.unitize_page_price));
                        pst.setInt(5, getInt(data.unitize_doc_price));
                        pst.setInt(6, getInt(data.uqc_page_price));
                        pst.setInt(7, getInt(data.uqc_doc_price));
                        pst.setInt(8, getInt(data.coding_page_price));
                        pst.setInt(9, getInt(data.coding_doc_price));
                        pst.setInt(10, getInt(data.codingqc_page_price));
                        pst.setInt(11, getInt(data.codingqc_doc_price));
                        pst.executeUpdate();
                        pst.close();
                    } catch (SQLException e) {                        
                        CommonLogger.printExceptions(this, "Exception while saving the unit price.", e);
                    }
                }
            }
        }
        return null;
    }

    //Returns integer from a double value
    private int getInt(String str) {
        double d = Float.parseFloat(str) / .1;
        String s = Double.toString(d);
        return Integer.parseInt(s.substring(0, s.indexOf(".")));
    }

    public boolean isReadOnly() {
        return false;
    }
}
