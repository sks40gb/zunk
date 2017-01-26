/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendTablespec.java,v 1.4.6.1 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.TablespecData;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called from <code>ui.TableAdminPage</code> to delete a table
 * and from <code>beans.AddEditTablespec</code> to add or update a table.
 * @see common.TablespecData
 * @see beans.AddEditTablespec
 * @see ui.TableAdminPage
 * @see server.Handler_tablespec
 */
public class TaskSendTablespec extends ClientTask {

    final private ServerConnection scon = Global.theServerConnection;
    private TablespecData tablespecData;

    /**
     * Create an instance of this class and remember the parameter.
     * @param tablespecData an instance of <code>common.TablespecData</code>
     * containing the details for a table
     */
    public TaskSendTablespec(TablespecData tablespecData) {
        this.tablespecData = tablespecData;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_TABLESPEC);
        writer.encode(TablespecData.class, tablespecData);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        if (ok.equals(T_OK)) {
            setResult((Object) ok);
        } else {
            setResult((String) reply.getAttribute(A_DATA));
        }
    }
}
