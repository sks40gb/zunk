/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskValidateBatch.java,v 1.12.6.2 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.CodingData;
import common.Log;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * ClientTask called from <code>ui.SplitPaneViewer</code> to check all fields
 * in the active group for all documents in the batch for errors.
 * @see server.Handler_validate_batch
 */
public class TaskValidateBatch extends ClientTask {

    private boolean unitize = false;
    private int activeGroup = 0;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameters.
     * @param unitize true if the locked batch is in unitize status because
     * the rules are different in unitize, e.g., required fields are not required
     * unless the status is in or beyond Coding.
     * @param activeGroup 0 if not using groups; the group currently being
     * coded in the locked batch
     */
    public TaskValidateBatch(boolean unitize, int activeGroup) {
        this.unitize = unitize;
        this.activeGroup = activeGroup;
    }

    /**
     * Write the message with attributes and set the result.     
     */
    @Override
    public void run() throws IOException {

        try {
            MessageWriter writer;
            writer = scon.startMessage(T_VALIDATE_BATCH);
            writer.writeAttribute(A_IS_UNITIZE, unitize ? "yes" : "no");
            writer.writeAttribute(A_GROUP, Integer.toString(activeGroup));
            addStandardAttributes(writer);
            writer.endElement();
            writer.close();

            Element reply = scon.receiveMessage();
            Log.print("TaskValidateBatch received " + reply.getNodeName());

            if (T_CODING_DATA.equals(reply.getNodeName())) {
                CodingData data = new CodingData();
                // fill in the int and String fields of the CodingData
                MessageReader.decode(reply, data);
                //Log.print("pageId="+data.pageId/*+" nextChildImageInRegion="+data.nextChildImageInRegion*/);
                // if there were values, fill in the HashMap
                NodeList valueList = reply.getElementsByTagName(T_VALUE_LIST);
                if (valueList.getLength() > 0) {
                    data.valueMap = MessageMap.decode((Element) (valueList.item(0)));
                }
                // store the result so the callback can get it
                setResult(data);
            } else if (T_OK.equals(reply.getNodeName())) {
                setResult(T_OK);
            } else {
                Log.quit("Validate batch unexpected message type: " + reply.getNodeName());
            }

        } catch (FailException e) {
            Log.print("Validate batch failure: " + e.getMessage());
            throw e;
        }
    }
}
