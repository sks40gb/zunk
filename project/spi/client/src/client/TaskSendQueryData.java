/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.QueryData;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to send the query data.
 * @see QueryData
 * @author bmurali
 */
public class TaskSendQueryData extends ClientTask {

    /** fields of <code>queryData</code> cantains all query data. */
    private QueryData queryData;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create the instance of this class and remember the parameter
     * @param queryData - query records
     */
    public TaskSendQueryData(QueryData queryData) {
        this.queryData = queryData;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_QUERY_DATA);
        writer.encode(QueryData.class, queryData);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        setResult((Object) ok);
    }
}