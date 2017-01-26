/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.OccurrenceData;
import common.msg.MessageWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Gets the values along with the occurances based on project, volume and the field name.
 *
 * @author Bala
 */
public class TaskRequestFieldvalue extends ClientTask {

    /** Field name */
    private String fieldName;
    /** Project Id */
    private int projectId;
    /** Volume Id */
    private int volumeId;
    /** Status (Tally, Listing, QA etc..)*/
    private String status;
    /** Put the result into this map*/
    HashMap map = new HashMap();
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate this class with following parameters
     * @param fieldName  Field name
     * @param projectId  Project Id
     * @param volumeId   Volume Id
     * @param status     Status
     */
    public TaskRequestFieldvalue(String fieldName, int projectId, int volumeId, String status) {
        this.fieldName = fieldName;
        this.projectId = projectId;
        this.volumeId = volumeId;
        this.status = status;
    }

    /**
     * Write the message with attributes and set the result with the
     * data decoded into a <code>Map</code>.
     */
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_REQUEST_FIELDVALUE);
        writer.writeAttribute(A_FIELD_NAME, fieldName);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_STATUS, status);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();

        if (T_RESULT_SET.equals(reply.getNodeName())) {
            NodeList valueList = reply.getElementsByTagName("row");

            for (int i = 0; i < valueList.getLength(); i++) {
                Node child = valueList.item(i).getFirstChild();
                ArrayList value = new ArrayList();
                OccurrenceData occurrence = new OccurrenceData();
                int j = 0;

                while (child != null) {
                    Element childElement = (Element) child;
                    value.add(childElement.getTextContent());
                    j++;
                    child = child.getNextSibling();
                }
                //Get the field value, occurance and whether the field is marked for Edit Checking
                if (value != null) {
                    occurrence.setFieldValue((String) value.get(0));
                    occurrence.setOccurrence((String) value.get(1));
                    occurrence.setMarking((String) value.get(2));
                }

                map.put(i, occurrence);
            }
        }

        setResult(map);
    }
}
