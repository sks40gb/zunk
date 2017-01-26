/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskRequestProjectFields.java,v 1.14.8.2 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * ClientTask to request the projectfields data for the given volume's
 * project to build the dynamic fields on the viewer/coder screen.
 * If volume is zero, the locked volume is used.  Admin app must
 * specify the volume because there is no locked volume.
 * @see server.Handler_request_projectfields
 */
public class TaskRequestProjectFields extends ClientTask {

    final private ServerConnection scon = Global.theServerConnection;
    private int volumeId;
    private int projectId;

    /**
     * Create an instance of this class and remember the parameter.
     * @param volumeId 0 to request the locked volume; volume.volume_id
     * of the volume to retreive
     */
    public TaskRequestProjectFields(int volumeId, int projectId) {
        this.volumeId = volumeId;
        this.projectId = projectId;
    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {

        System.out.println("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTt");
        
        MessageWriter writer;
        writer = scon.startMessage(T_REQUEST_PROJECTFIELDS);
        if (volumeId != 0) {
            writer.writeAttribute(A_VOLUME_ID, volumeId);

        }
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        Log.print("received " + reply.getNodeName());

        if (T_PROJECTFIELDS_DATA.equals(reply.getNodeName())) {
            Map data = new HashMap();
            Map validationData = new HashMap();
            // if there were values, fill in the HashMap
            NodeList valueList = reply.getElementsByTagName(T_VALUE_LIST);
            if (valueList.getLength() > 0) {
                int i = 0;
                data = MessageMap.decodeList((Element) (valueList.item(0)));
                i++;
            }

            NodeList validations = reply.getElementsByTagName(T_VALIDATIONS);
            if (validations.getLength() > 0) {
                int i = 0;
                validationData = MessageMap.validationsList((Element) (validations.item(0)));
                i++;
            }

            Map resultMap = new HashMap();
            resultMap.put(T_VALUE_LIST, data);
            resultMap.put(T_VALIDATIONS, validationData);
            setResult(resultMap);

        } else {
            Log.quit("Project unexpected message type: " + reply.getNodeName());
        }
    }
}