/*
 * TaskSendFieldValueDetails.java
 *
 * Created on February 17, 2008, 11:10 PM
 *
 * To change this template, choose Tools | Template Manager
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
 * ClientTask to save the Field's value details.
 * @see MarkingData
 * @author murali
 */
public class TaskSendFieldvalueDetails extends ClientTask {

    /** Field name */
    private String fieldName;
    /** Olf field value */
    private String fieldValue;
    /** New field value */
    private String newFieldValue;
    /** bates number */
    private String bateno;
    /** Volume Id */
    private int volumeId;
    /** Project Id */
    private int projectId;
    /** Condition */
    private String condition;
    /** Status */
    private String status;
    /** Error type */
    private String errorType;
    /** listing_occurrence_id _Id in listing_occurrence table */
    private int listing_occurrence_id;
    //Map for <code>MarkingData</code>
    HashMap markingMap = new HashMap();
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /** Creates a new instance of TaskSendFieldValueDetails 
     * Remember the parameters.
     * @param projectId     Project Id to which field belongs to.
     * @param fieldName     Field for which details are sent.
     * @param fieldValue    Old Field value.
     * @param newFieldValue New Field value.
     * @param bateno        Bates number
     * @param volumeId      Volume Id, can be retrieved from volume.volume_id
     * @param condition     Condition
     * @param status        Status may be Listing, Tally, QA, etc..
     * @param errorType     Type of error
     * @param listing_occurrence_id 
     */
    public TaskSendFieldvalueDetails(int projectId, String fieldName, String fieldValue, String newFieldValue, String bateno, int volumeId, String condition, String status, String errorType, int listing_occurrence_id) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.newFieldValue = newFieldValue;
        this.bateno = bateno;
        this.volumeId = volumeId;
        this.projectId = projectId;
        this.condition = condition;
        this.status = status;
        this.errorType = errorType;
        this.listing_occurrence_id = listing_occurrence_id;

    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {

        MessageWriter writer;
        writer = scon.startMessage(T_SEND_FIELDVALUEDETAILS);
        writer.writeAttribute(A_FIELD_NAME, fieldName);
        writer.writeAttribute(A_FIELD_VALUE, fieldValue);
        writer.writeAttribute(A_NEW_FIELD_VALUE, newFieldValue);
        writer.writeAttribute(A_BATE, bateno);
        writer.writeAttribute(A_CONDITION, condition);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.writeAttribute(A_STATUS, status);
        if (errorType != null) {
            writer.writeAttribute(A_ERRORTYPE, errorType);
        }
        writer.writeAttribute(A_ID, listing_occurrence_id);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        
        // 1. If results is not empty or null
        // 2, Add the result into the ArrayList <code>value</code>
        // 3. Get each record from <code>value</code> and put into <code>MarkingData</code>
        // 4. Put the <code>marking</code> to the <code>markingMap</map>
        // 5. Set <code>markingMap</code> as a result.
        if (T_RESULT_SET.equals(reply.getNodeName())) {
            NodeList valueList = reply.getElementsByTagName("row");
            for (int i = 0; i < valueList.getLength(); i++) {
                Node child = valueList.item(i).getFirstChild();
                ArrayList value = new ArrayList();
                MarkingData marking = new MarkingData();
                int j = 0;
                while (child != null) {
                    Element childElement = (Element) child;
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

                markingMap.put(i, marking);
            }
        }
        setResult(markingMap);
    }
}
