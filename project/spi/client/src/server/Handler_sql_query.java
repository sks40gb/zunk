/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_sql_query.java,v 1.21.6.1 2006/03/14 15:08:47 nancy Exp $ */
package server;

import common.Log;
import common.msg.MessageWriter;
import common.msg.XmlUtil;

import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Execute a stored SQL query and add the result to the XML message.
 * A sequence of queries may be executed in one transaction, ordered
 * by sql_text.sequence; each query except the last should return a
 * single row, whose values are taken as parameters for the next
 * query.
 * TBD: If needed, we could extend this to allow the first
 * query in the sequence to be a CREATE TEMPORARY TABLE.
 * @see client.TaskExecuteQuery
 * @see client.Sql
 *
 */
final public class Handler_sql_query extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_sql_query() {}

    /** 
     * This handler is read-only.
     * @return true
     */
    public boolean isReadOnly() {
        return true;
    }

    public void run (ServerTask task, Element action)
    throws ServerFailException, SQLException, IOException{
        Connection con = task.getConnection();

        String name = action.getAttribute(A_NAME);
        boolean requestedMetaData = "YES".equals(action.getAttribute(A_REQUEST_METADATA));
   
        PreparedStatement ps1 = con.prepareStatement(
            "select role, text from sql_text"
            +" where name=?"
            +" order by sequence");
        ps1.setString(1, name);
        ResultSet rs1 = ps1.executeQuery();
        if (! rs1.next()) {
            throw new ServerFailException("SQL not defined on server: "+name);
        }

        //int role = rs1.getInt(1); // for now, not checking role

        // Obtain the sql string and do substitutions
        String sql = SqlUtil.substitute(task, rs1.getString(2));

        NodeList nodes = action.getElementsByTagName(T_PARAMETER);
        PreparedStatement ps2 = con.prepareStatement(sql);
        //Log.print("Handler_sql_query.run: "+nodes.getLength()+" "+sql);
        //Log.print("ps2="+ps2);
        int ps2indx = 0;
        for (int i = 0; i < nodes.getLength(); i++) {
            ps2indx++;
            //Log.print("...["+ps2indx+"]: "+XmlUtil.getTextFromNode(nodes.item(i)));
            ps2.setString(ps2indx, XmlUtil.getTextFromNode(nodes.item(i)));
        }

        ResultSet rs2;
        try {
            rs2 = ps2.executeQuery() ;
        } catch (SQLException e) {
            throw new ServerSQLFailException(e);
        }

        while (rs1.next()) {

            // get the results from the prior query
            ResultSetMetaData rsmd = rs2.getMetaData();
            int columnCount = rsmd.getColumnCount();
            String[] results = new String[columnCount];
            if (rs1.next()) {
                for (int i = 0; i < columnCount; i++) {
                    results[i] = rs2.getString(i+1);
                }
            } else {
                Arrays.fill(results,"0");
            }

            // prepare the next query and fill in parameters
            // Note: we collect parameters in an array above,
            // because we reuse PreparedStatement ps2.
            sql = SqlUtil.substitute(task, rs1.getString(2));
            ps2 = con.prepareStatement(sql);
            for (int i = 0; i < columnCount; i++) {
                ps2.setString(i+1, results[i]);
            }

            // execute the next query
            try {
                rs2 = ps2.executeQuery() ;
            } catch (SQLException e) {
                throw new ServerSQLFailException(e);
            }
        }

        ps1.close();

        writeXmlFromResult(task, rs2, requestedMetaData);
        ps2.close();
    }

    /**
     * Add the XML representation of a result set to the output message.
     * Column name metadata is not included.
     * @param task The server task.
     * @param rs The result set.
     */
    public static void writeXmlFromResult(ServerTask task, ResultSet rs)
    throws SQLException, IOException {
        writeXmlFromResult(task,rs,false);
    }

    /**
    * Add the XML representation of a result set to the output message.
     * <p>Note: this may be called from other classes, as well as this one.
     * @param task The server task.
     * @param rs The result set.
     * @param requestedMetaData If true, column names are included for metadata.
     */
    public static void writeXmlFromResult(ServerTask task, ResultSet rs, boolean requestedMetaData)
    throws SQLException, IOException {

        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_RESULT_SET);
        writer.writeAttribute(A_COUNT, Integer.toString(columnCount));
        if (requestedMetaData) {
            writer.startElement(T_HEADING);
            for (int j = 1; j <= columnCount; j++) {
                writer.startElement(T_COLUMN);
                writer.writeContent(rsmd.getColumnName(j));
                writer.endElement();
            }
            writer.endElement();
        }
        while (rs.next()) {
            writer.startElement(T_ROW);
            for (int j = 1; j <= columnCount; j++) {
                writer.startElement(T_COLUMN);
                String value = rs.getString(j);
                if (rs.wasNull()) {
                    writer.writeAttribute(A_IS_NULL, "YES");
                } else {
                    writer.writeContent(value);
                }
                writer.endElement();
            }
            writer.endElement();
        }
        writer.endElement();
    }
}
