/* $Header: /home/common/cvsarea/ibase/dia/src/server/Attic/Handler_customerprice.java,v 1.1.2.3 2006/03/21 16:42:41 nancy Exp $ */
package server;

import common.Log;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import common.CustomerpriceData;
                  
import java.math.BigDecimal;
import java.sql.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for customerprice message.  Call MessageReader to decode the
 * XML message into <code>common.CustomerpriceData</code>, then update or insert
 * values into the customerprice table.
 * @see common.msg.MessageReader
 * @see common.CustomerpriceData
 * @see client.TaskSendCustomerprice
 * @see beans.AddEditCustomerprice
 * @see ui.CustomerPricePage
 */
final public class Handler_customerprice extends Handler implements common.msg.MessageConstants {
    
    PreparedStatement pst;
    Connection con;
    Statement st;

    /**
     * This class cannot be instantiated.
     */
    public Handler_customerprice() {
    }

    public void run (ServerTask task, Element action) throws SQLException {
        //Log.print("Handler_customerprice");
        Element givenValueList = action;

        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild != null) {
            CustomerpriceData data = new CustomerpriceData();
            // fill in the int and String fields of the CustomerpriceData
            try {
                MessageReader.decode(givenValueList, data);
            } catch (Throwable t) {
                Log.quit(t);
            }
            if (data != null) {
                // update or insert the customerprice row contained in data
                //Log.print("(Handler_customerprice.run) users_id=" + data.customerprice_id);
                con = task.getConnection();
                st = task.getStatement();
                if (data.customerprice_id > 0) {
                    // change existing customerprice
                    try {
                        pst = task.prepareStatement(
                            "update customerprice set "
                            +" unitize_page_price=?, unitize_doc_price=?,"
                            +" coding_page_price=?, coding_doc_price=?"
                            +" where customerprice_id = ?");
                        pst.setInt(1, getInt(data.unitize_page_price));
                        pst.setInt(2, getInt(data.unitize_doc_price));
                        pst.setInt(3, getInt(data.coding_page_price));
                        pst.setInt(4, getInt(data.coding_doc_price));
                        pst.setInt(5, data.customerprice_id);
                        pst.executeUpdate();
                        pst.close();
                    } catch (SQLException e) {
                        String sqlState = e.getSQLState();
                        int errorCode = e.getErrorCode();
                        Log.print(">>> Handler_customerprice update"+e+" sqlState="+sqlState+" errorCode="+errorCode);
                        if (errorCode == ServerTask.ER_DUP_ENTRY ) {
                            // it's a dup, ignore it
                            Log.print("(Handler_customerprice update) duplicate");
                        } else {
                            throw e;
                        }
                    }

                } else {
                    // add new price
                    try {
                        pst = task.prepareStatement(
                            "insert into customerprice"
                            +" (project_id, volume_id, field_level"
                            +", unitize_page_price, unitize_doc_price"
                            +", coding_page_price, coding_doc_price"
                            +" values (?,?,?,?,?,?,?)");
                        pst.setInt(1, data.project_id);
                        pst.setInt(2, data.volume_id);
                        pst.setInt(3, data.field_level);
                        pst.setInt(4, getInt(data.unitize_page_price));
                        pst.setInt(5, getInt(data.unitize_doc_price));
                        pst.setInt(6, getInt(data.coding_page_price));
                        pst.setInt(7, getInt(data.coding_doc_price));
                        pst.executeUpdate();
                        pst.close();
                    } catch (SQLException e) {
                        String sqlState = e.getSQLState();
                        int errorCode = e.getErrorCode();
                        Log.print(">>> Handler_customerprice insert"+e+" sqlState="+sqlState+" errorCode="+errorCode);
                        if (errorCode == ServerTask.ER_DUP_ENTRY ) {
                            // it's a dup, ignore it
                            Log.print("(Handler_customerprice insert) duplicate");
                        } else {
                            throw e;
                        }

                    }
                }
            }
        }
    }

    private int getInt(String str) {
        double d = Float.parseFloat(str) / .1;
        String s = Double.toString(d);
        return Integer.parseInt(s.substring(0,s.indexOf(".")));
    }
}
