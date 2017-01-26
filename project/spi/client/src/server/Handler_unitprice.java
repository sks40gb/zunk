/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_unitprice.java,v 1.2.2.2 2006/03/22 20:27:15 nancy Exp $ */
package server;

import common.Log;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import common.UnitpriceData;
                  
import java.math.BigDecimal;
import java.sql.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for unitprice message to update the <code>unitprice</code>
 * data sent from the client in <code>common.UnitpriceData</code>.
 * @see common.UnitpriceData
 * @see client.TaskSendUnitprice
 */
final public class Handler_unitprice extends Handler implements common.msg.MessageConstants {
    
    PreparedStatement pst;
    Connection con;
    Statement st;

    /**
     * This class cannot be instantiated.
     */
    public Handler_unitprice() {
    }

    public void run (ServerTask task, Element action) throws SQLException {
        //Log.print("Handler_unitprice");
        Element givenValueList = action;

        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild != null) {
            UnitpriceData data = new UnitpriceData();
            // fill in the int and String fields of the UnitpriceData
            try {
                MessageReader.decode(givenValueList, data);
            } catch (Throwable t) {
                Log.quit(t);
            }
            if (data != null) {
                // update or insert the unitprice row contained in data
                //Log.print("(Handler_unitprice.run) users_id=" + data.unitprice_id);
                con = task.getConnection();
                st = task.getStatement();
                if (data.unitprice_id > 0) {
                    // change existing unitprice
                    try {
                        pst = task.prepareStatement(
                            "update unitprice set "
                            +" unitize_page_price=?, unitize_doc_price=?,"
                            +" uqc_page_price=?, uqc_doc_price=?,"
                            +" coding_page_price=?, coding_doc_price=?,"
                            +" codingqc_page_price=?, codingqc_doc_price=?"
                            +" where unitprice_id = ?");
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
                        String sqlState = e.getSQLState();
                        int errorCode = e.getErrorCode();
                        Log.print(">>> Handler_unitprice update"+e+" sqlState="+sqlState+" errorCode="+errorCode);
                        if (errorCode == ServerTask.ER_DUP_ENTRY ) {
                            // it's a dup, ignore it
                            Log.print("(Handler_unitprice update) duplicate");
                        } else {
                            throw e;
                        }
                    }

                } else {
                    // add new price
                    try {
                        pst = task.prepareStatement(
                            "insert into unitprice"
                            +" (project_id, volume_id, field_level"
                            +", unitize_page_price, unitize_doc_price"
                            +", uqc_page_price, uqc_doc_price"
                            +", coding_page_price, coding_doc_price"
                            +", codingqc_page_price, codingqc_doc_price)"
                            +" values (?,?,?,?,?,?,?,?,?,?,?)");
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
                        String sqlState = e.getSQLState();
                        int errorCode = e.getErrorCode();
                        Log.print(">>> Handler_unitprice insert"+e+" sqlState="+sqlState+" errorCode="+errorCode);
                        if (errorCode == ServerTask.ER_DUP_ENTRY ) {
                            // it's a dup, ignore it
                            Log.print("(Handler_unitprice insert) duplicate");
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
