/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.MarkingData;
import common.msg.MessageWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ClientTask to get Fields detail
 * @author bmurali
 */
public class TaskRequestFieldvalueDetails extends ClientTask {

    /** Field name */
    private String fieldName;
    /** Field value */
    private String fieldValue;
    /** Project Id */
    private int projectId;
    /** Volume Id */
    private int volumeId;
    /** Status */
    private String status;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** put the result to map */
    HashMap map = new HashMap();

    /**
     * Instantiate this ClientTask and remember the parameters.
     * @param fieldName   Field name
     * @param fieldValue  Field value
     * @param projectId   Project Id
     * @param volumeId    Volume Id
     * @param status      Status
     */
    public TaskRequestFieldvalueDetails(String fieldName, String fieldValue, int projectId, int volumeId, String status) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.projectId = projectId;
        this.volumeId = volumeId;
        this.status = status;
    }

    /**
     * Write the message with attributes and set the result with the
     * data decoded into a <code>Map</code>.
     * 
     * @throws java.io.IOException if i/o error ocurred while sending the request to server.  
     */
    public void run() throws IOException {

        Node child = null;
        Element childElement = null;
        ArrayList value = null;

        MessageWriter writer;
        writer = scon.startMessage(T_REQUEST_FIELDVALUEDETAILS);
        writer.writeAttribute(A_FIELD_NAME, fieldName);
        writer.writeAttribute(A_FIELD_VALUE, fieldValue);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_STATUS, status);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        if (T_RESULT_SET.equals(reply.getNodeName())) {
            NodeList valueList = reply.getElementsByTagName("row");
            
            // 1. Get the childs from the XML reply from the sever.
            // 2. Put the child to an ArrayList <code> value </code>
            // 3. Get each element from <code> value </code> set the fields of
            //    <code>marking<marking> @see <code>MarkingData</code> 
            // 4. Put this object <code>marking</code> to the map.
            // 5. Set the map as result.
            
            for (int i = 0; i < valueList.getLength(); i++) {
                child = valueList.item(i).getFirstChild();
                value = new ArrayList();
                MarkingData marking = new MarkingData();
                int j = 0;
                while (child != null) {
                    childElement = (Element) child;
                    value.add(childElement.getTextContent());
                    j++;
                    child = child.getNextSibling();
                }
                if (value != null) {
                    marking.setFirstBatesOfRange((String) value.get(0));
                    marking.setLastBatesOfRange((String) value.get(1));
                    marking.setFieldvalue((String) value.get(2));
                    marking.setSequence((String) value.get(3));
                    marking.setView_marking((String) value.get(4));
                }
                map.put(i, marking);
            }

        }
        setResult(map);
    }
}
