/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendDelimiterData.java,v 1.2.6.1 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.DelimiterData;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called by <code>ui.DelimiterDialog</code> and
 * <code>ui.DelimiterPanel</code> to send delimiters and parameters
 * to the server for storage.  Delimiter data is used for import and
 * export.
 * @see common.DelimiterData
 * @see ui.DelimiterDialog
 * @see ui.DelimiterPanel
 * @see server.Handler_delimiter_data
 */
public class TaskSendDelimiterData extends ClientTask {

    private DelimiterData data;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameter.
     * @param data an instance of DelimiterData containing the user-updated
     * delimiters
     */
    public TaskSendDelimiterData(DelimiterData data) {
        this.data = data;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_DELIMITER_DATA);
        writer.encode(DelimiterData.class, data);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        setResult((Object) ok);
    }
}