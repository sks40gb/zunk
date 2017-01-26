/*
 * Tally Process Task - Will Call the Tally Process Stored Procedure
 * Should Extend the ClientTask.
 */
package client;

import common.TallyProcessData;
import common.msg.MessageWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ClientTask for Tally Process. Get the data <code>tallyProcessData</code>required 
 * to perform the Tally. Put these data to a map and set to the result.
 * @see ConfirmTallyFields
 *  
 * @author sunil
 */
public class TaskTallyProcess extends ClientTask {

    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Data required to perform the Tally process */
    //private TallyProcessData tallyProcessData;
    /** Map for the fields name */
    private Map<String, List> fieldNames;
    /** Put the <code>tallyProcessData</code> to this map. */
    private Map tallyProcessDataMap;
    /** Project Id */
    private int projectId;
    /** Volume Id */
    private int volumeId;

    /**
     * Create an instance of this class with the following parameters
     * @param fieldNames  Fields name 
     * @param projectId   Project Id
     * @param volumeId    Volume Id
     */
    public TaskTallyProcess(Map<String, List> fieldNames, int projectId, int volumeId) {
        this.fieldNames = fieldNames;
        this.projectId = projectId;
        this.volumeId = volumeId;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_TALLY_PROCESS);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        List values = null;
        String key = "";
        String fieldValues = "";
        Set keys = fieldNames.keySet();

        // 1. Iterate each fields 
        // 2. Get the field name
        // 3. Append the field values
        // 4. Write to the message.
        for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
            key = (String) iterator.next();
            values = (List) fieldNames.get(key);
            fieldValues = key;
            for (int i = 0; values.size() > i; i++) {
                fieldValues += "-" + (String) values.get(i);
            }
            writer.startElement(E_FIELD_NAME);
            writer.writeContent(fieldValues); //fieldName

            writer.endElement();

        }
        writer.endElement();
        writer.close();

        // Recieve the reply from the server.
        Element reply = scon.receiveMessage();

        // if there is result from the server
        // 1. Get the nodes from the reply coming from server in xml  format.
        // 2. Iterate over each node and get tally process data.
        // 3. Put the <code>tallProcessData</code> to the map <code>tallyProcessDataMap</code>.
        // 4. Set the map to the result.

        if (T_TALLY_PROCESS.equals(reply.getNodeName())) {
            tallyProcessDataMap = new HashMap();
            NodeList valueList = reply.getElementsByTagName("row");
            for (int i = 0; i < valueList.getLength(); i++) {
                Node child = valueList.item(i).getFirstChild();
                ArrayList value = new ArrayList();
                TallyProcessData tallyProcessData = new TallyProcessData();
                int j = 0;
                while (child != null) {
                    Element childElement = (Element) child;
                    value.add(childElement.getTextContent());
                    j++;
                    child = child.getNextSibling();
                }

                if (value != null) {                    
                    tallyProcessData.setProjectFieldName(value.get(1).toString());
                    tallyProcessData.setTallyType(value.get(2).toString());
                    tallyProcessData.setGroupCount(Integer.parseInt(value.get(3).toString()));
                    tallyProcessDataMap.put(i, tallyProcessData);
                }

            }

        } else if (T_OK.equals(reply.getNodeName())) {
            System.out.println("server reply ok");
        }
        setResult(tallyProcessDataMap);
    }
}
