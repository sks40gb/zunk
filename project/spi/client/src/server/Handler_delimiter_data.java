/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_delimiter_data.java,v 1.4.6.5 2006/03/30 12:28:55 nancy Exp $ */
package server;

import common.Log;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import common.DelimiterData;

import java.sql.*;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for delimiter_data message.  Call <code>common.msg.MessageReader</code>
 * to decode the XML message containing user-updated delimiters for import and export,
 * then write them to the <code>export</code> table.
 * @see common.msg.MessageReader
 * @see common.DelimiterData
 */
final public class Handler_delimiter_data extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_delimiter_data() {
    }

    public void run (ServerTask task, Element action) {
        try {
            //Log.print("Handler_delimiter_data");
            Element givenValueList = action;
            
            //Note.  "child" may be ignored white space, if not validating parser
            Node firstChild = givenValueList.getFirstChild();
            while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
                firstChild = firstChild.getNextSibling();
            }
            if (firstChild != null) {
                DelimiterData data = new DelimiterData();
                // fill in the int and String fields of DelimiterData
                MessageReader.decode(givenValueList, data, /* trim? */ false);
                if (data != null) {
                    // update or insert the export row contained in data
                    //Log.print("(Handler_delimiter_data.run) project is " + data.delimiter_set_name);
                    saveDelimiterData(task, data);
                }
            }
        } catch (Throwable t) {
            Log.quit(t);
        }
    }

    private void saveDelimiterData(ServerTask task, DelimiterData data)
    throws java.io.IOException {
        try {
            Log.print("(Handler_delimiter_data.save) '" + data.value_separator + "'");
            Connection con = task.getConnection();
            Statement st = task.getStatement();
            PreparedStatement pst = null;
            if (data.force.equals("")) {
                // delete
                pst = con.prepareStatement(
                    "delete from export"
                    +" where export_name = ?");
                pst.setString(1, data.delimiter_set_name);
                pst.executeUpdate();
            } else {
                // see if the export_name exists
                pst = con.prepareStatement(
                    "select export_name"
                    +" from export"
                    +" where export_name=?");
                pst.setString(1, data.delimiter_set_name);
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    //Log.print("(Handler_export_data.saveExportData) update " + data.delimiter_set_name);
                    // update
                    rs.close();
                    pst = con.prepareStatement(
                        "update export set"
                        +" force_export = ?,"
                        +" uppercase = ?,"
                        +" uppercase_names = ?,"
                        +" field_delimiter = ?,"
                        +" text_qualifier = ?,"
                        +" value_separator = ?,"
                        +" date_format = ?,"
                        +" missing_date = ?,"
                        +" missing_year = ?,"
                        +" missing_month = ?,"
                        +" missing_day = ?,"
                        +" missing_date_character = ?,"
                        +" name_mask1 = ?,"
                        +" name_mask2 = ?,"
                        +" name_mask3 = ?,"
                        +" name_mask4 = ?,"
                        +" brs_format = ?"
                        +" where export_name = ?");
                    pst.setString(1, data.force);
                    pst.setString(2, data.uppercase);
                    pst.setString(3, data.uppercase_names);
                    pst.setString(4, data.field_delimiter);
                    pst.setString(5, data.text_qualifier);
                    pst.setString(6, data.value_separator);
                    pst.setString(7, data.date_format);
                    pst.setString(8, data.missing_date);
                    pst.setString(9, data.missing_year);
                    pst.setString(10, data.missing_month);
                    pst.setString(11, data.missing_day);
                    pst.setString(12, data.missing_date_character);
                    pst.setString(13, data.name_mask1);
                    pst.setString(14, data.name_mask2);
                    pst.setString(15, data.name_mask3);
                    pst.setString(16, data.name_mask4);
                    pst.setString(17, data.brs_format);
                    pst.setString(18, data.delimiter_set_name);
                    pst.executeUpdate();
                    pst.close();
                } else {
                    // insert
                    //Log.print("(Handler_export_data.saveExportData) insert " + data.delimiter_set_name);
                    rs.close();
                    pst = con.prepareStatement(
                        "insert into export set"
                        +" force_export = ?,"
                        +" uppercase = ?,"
                        +" uppercase_names = ?,"
                        +" field_delimiter = ?,"
                        +" text_qualifier = ?,"
                        +" value_separator = ?,"
                        +" date_format = ?,"
                        +" missing_date = ?,"
                        +" missing_year = ?,"
                        +" missing_month = ?,"
                        +" missing_day = ?,"
                        +" missing_date_character = ?,"
                        +" export_name = ?,"
                        +" name_mask1 = ?,"
                        +" name_mask2 = ?,"
                        +" name_mask3 = ?,"
                        +" name_mask4 = ?,"
                        +" brs_format = ?");
                    pst.setString(1, data.force);
                    pst.setString(2, data.uppercase);
                    pst.setString(3, data.uppercase_names);
                    pst.setString(4, data.field_delimiter);
                    pst.setString(5, data.text_qualifier);
                    pst.setString(6, data.value_separator);
                    pst.setString(7, data.date_format);
                    pst.setString(8, data.missing_date);
                    pst.setString(9, data.missing_year);
                    pst.setString(10, data.missing_month);
                    pst.setString(11, data.missing_day);
                    pst.setString(12, data.missing_date_character);
                    pst.setString(13, data.delimiter_set_name);
                    pst.setString(14, data.name_mask1);
                    pst.setString(15, data.name_mask2);
                    pst.setString(16, data.name_mask3);
                    pst.setString(17, data.name_mask4);
                    pst.setString(18, data.brs_format);
                    pst.executeUpdate();
                    pst.close();
                }
            }
            pst.close();
        } catch (Throwable t) {
            Log.quit(t);
        }
        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_OK);
        writer.endElement();
    }
}
