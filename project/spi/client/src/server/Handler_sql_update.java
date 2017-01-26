/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_sql_update.java,v 1.21.8.2 2006/03/14 15:08:47 nancy Exp $ */
package server;

//import client.MessageMap;
import common.Log;
//import common.msg.MessageReader;
import common.msg.MessageWriter;
import common.msg.XmlUtil;

import java.io.IOException;
import java.sql.*;
//import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.Element;
//import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Execute a stored SQL update and add the result to the XML message.
 * Parses SQL statement and updates changes table if the affected
 * table is managed.
 */
final public class Handler_sql_update extends Handler {

    // lazily created patterns
    // Note.  We don't syncronize.  A thread will see either a null
    // or a valid compiled pattern.  In principle, it would be
    // possible for two threads to do the lazy creation at the
    // same time, but this should not cause a problem.
    private static Pattern deletePattern = null;
    private static Pattern insertPattern = null;
    private static Pattern updatePattern = null;

    /**
     * This class cannot be instantiated.
     */
    public Handler_sql_update() {}

    public void run (ServerTask task, Element action)
    throws ServerFailException, IOException, SQLException {
        Connection con = task.getConnection();

        String name = action.getAttribute(A_NAME);

        PreparedStatement ps1 = con.prepareStatement(
            "select role, text from sql_text"
            +" where name=?"
            +" lock in share mode");
            // ?? +" order by sequence");
        ps1.setString(1, name);
        ResultSet rs1 = ps1.executeQuery();
        if (! rs1.next()) {
            throw new ServerFailException("SQL not defined on server: "+name);
        }

        //int role = rs1.getInt(1); // for now, not checking role

        // Obtain the sql string and do substitutions
        String sql = SqlUtil.substitute(task, rs1.getString(2)).trim();
        ps1.close(); // for now, assume only one statement - maybe batches later

        NodeList parameterNodes = action.getElementsByTagName(T_PARAMETER);
        PreparedStatement ps2;
        // For now, don't treat replace as mangaged (kludge!!)
        // TBD: really need to indicate in sql_text whether or not stmt is managed
        if (sql.startsWith("replace")) {
            ps2 = con.prepareStatement(sql);
        } else {
            ps2 = task.prepareStatement(sql);
        }
        setParameters(ps2, parameterNodes);
        int updateCount = ps2.executeUpdate();
        ps2.close();
        Log.print("Handler_sql_update: '"+name+"' count='"+updateCount+"'");

        task.commitTransaction();

        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_UPDATE_COUNT);
        writer.writeAttribute(A_COUNT, Integer.toString(updateCount));
        writer.endElement();
    }


    private void setParameters(PreparedStatement ps2, NodeList parameterNodes)
    throws SQLException {
        int end = parameterNodes.getLength();
        Log.print("setParameters: end="+end);
        int ps2indx = 0;
        for (int i = 0; i < end; i++) {
            ps2indx++;
            //Log.print(i+"->"+ps2indx+":'"
            //          +XmlUtil.getTextFromNode(parameterNodes.item(i))+"'");
            ps2.setBytes(ps2indx,
                          XmlUtil.getTextFromNode(parameterNodes.item(i)).getBytes());
        }
    }
}
