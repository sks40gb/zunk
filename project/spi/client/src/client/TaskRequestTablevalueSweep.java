/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskRequestTablevalueSweep.java,v 1.1.6.2 2006/03/21 16:42:41 nancy Exp $ */
/* $Heading$ */
package client;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import java.sql.ResultSet;
import org.w3c.dom.Element;

/**
 * ClientTask to 'sweep' the tablespec and tablevalues tables, then
 * the coded data looking for matching values.
 * @see server.Handler_request_tablevalue_sweep
 */
public class TaskRequestTablevalueSweep extends ClientTask {

    /** Table name */
    private String tablename;
    /** tablespceId can be obtained from tablespec.tablespecId . */
    private int tablespecId;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameters.
     * @param tablename The name of the table to search for coding values; values,
     *          longvalues or namevalues.
     * @param tablespecId The tablespec.tablespec_id to retrieve.
     */
    public TaskRequestTablevalueSweep(String tablename, int tablespecId) {
        this.tablename = tablename;
        this.tablespecId = tablespecId;
    }

    /**
     * Write the message with attributes and set the result.
     * @throws java.io.IOException
     */
    public void run() throws IOException {

        MessageWriter writer;
        writer = scon.startMessage(T_REQUEST_TABLEVALUE_SWEEP);
        writer.writeAttribute(A_NAME, tablename);
        writer.writeAttribute(A_ID, Integer.toString(tablespecId));
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();

        // Convet to ResultSet
        final ResultSet rs = Sql.resultFromXML(reply);
        if (T_RESULT_SET.equals(reply.getNodeName())) {
            synchronized (this) {
                // force cache flush for rs
            }
        } else if (T_FAIL.equals(reply.getNodeName())) {
            Log.quit("Sql.TaskRequestTablevalueSweep: SQL error: " + reply);
        } else {
            Log.quit("TaskRequestTablevalueSweep: unexpected message type: " + reply.getNodeName());
        }
        setResult(rs);
    }
}
