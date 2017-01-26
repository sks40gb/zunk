/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendTablevalue.java,v 1.9.6.1 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.TablevalueData;
import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called from <code>beans.AddEditTablespec</code> to
 * delete existing table values, from <code>beans.AddEditTextFieldDialog</code>
 * to add or update existing tablevalues, and from <code>client.SweepTablevalue</code>
 * to add or delete tablevalues.
 * @see common.TablevalueData
 * @see beans.AddEditTablespec
 * @see beans.AddEditTextFieldDialog
 * @see beans.SweepTablevalue
 * @see server.Handler_tablevalue
 */
public class TaskSendTablevalue extends ClientTask {

    final private ServerConnection scon = Global.theServerConnection;
    private TablevalueData data;

    /**
     * Create an instance of this class and remember the parameter.
     * @param data an instance of <code>common.TablevalueData</code> containing
     * the update to the server
     */
    public TaskSendTablevalue(TablevalueData data) {
        this.data = data;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_TABLEVALUE);
        writer.encode(TablevalueData.class, data);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        //Log.print("T_TABLEVALUE reply " + ok);
        setResult((Object) ok);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("TaskSendTablevalue unexpected message type: " + ok);
        }
    }
}
