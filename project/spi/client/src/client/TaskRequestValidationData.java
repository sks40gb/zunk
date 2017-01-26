/*
 * TaskRequestValidationData.java
 *
 * Created on December 28, 2007, 6:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

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
public class TaskRequestValidationData extends ClientTask {

    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Volume Id */
    private int volumeId;

    /**
     * Create an instance of this class and remember the parameter.
     * @param volumeId 0 to request the locked volume; volume.volume_id
     * of the volume to retreive
     */
    public TaskRequestValidationData(int volumeId) {
        this.volumeId = volumeId;
    }
     
    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
       
        MessageWriter writer;
        writer = scon.startMessage(T_REQUEST_PROJECTFIELDS);
        if (volumeId != 0) {
            writer.writeAttribute(A_VOLUME_ID, volumeId);
        }
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        Log.print("received "+reply.getNodeName());
           
        if (T_PROJECTFIELDS_DATA.equals(reply.getNodeName())) {
            Map data = new HashMap();
            // if there were values, fill in the HashMap
            NodeList valueList = reply.getElementsByTagName(T_VALUE_LIST);
            if (valueList.getLength() > 0) {
                int i = 0;
                data = MessageMap.decodeList((Element) (valueList.item(0)));
                i++;
            }
            // store the result so the callback can get it
            setResult(data);
        } else {
            Log.quit("Project unexpected message type: "+reply.getNodeName());
        }
    }
}