/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendUnitprice.java,v 1.2.2.1 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.UnitpriceData;
import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called from <code>beans.AddEditUnitprice</code> to add or update
 * <code>unitprice</code> data.
 * @see beans.AddEditUnitprice
 * @see common.UnitpriceData
 * @see server.Handler_unitprice
 */
public class TaskSendUnitprice extends ClientTask {

    final private ServerConnection scon = Global.theServerConnection;
    private UnitpriceData data;

    /**
     * Create an instance of this class and remember the parameter.
     * @param data an instance of <code>common.UnitpriceData</code> containing
     * the <code>unitprice</code> data for the server
     */
    public TaskSendUnitprice(UnitpriceData data) {
        this.data = data;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_UNITPRICE);
        writer.encode(UnitpriceData.class, data);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();

        String ok = reply.getNodeName();
        setResult((Object) ok);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("TaskSendUnitprice unexpected message type: " + ok);
        }
    }
}
