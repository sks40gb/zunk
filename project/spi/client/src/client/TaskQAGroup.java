/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * ClientTask to get the QA group for the volume 
 * @author anurag
 */
public class TaskQAGroup extends ClientTask {

    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Volume Id for which group is searched */
    private int volume_id;
    /** Put the results in  <code> resultMap </code> */
    private Map resultMap;
    /** row data */
    private ArrayList rowList;

    /**
     * Instantiate this ClientTask with following parameters 
     * @param volume_id Volume Id, the group is searched for.
     */
    public TaskQAGroup(int volume_id) {
        this.volume_id = volume_id;
    }

    /**
     * Write the message with attributes and set the result.
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_QA_GROUP);
        writer.writeAttribute(A_VOLUME_ID, volume_id);
        writer.endElement();
        writer.close();
        resultMap = new HashMap();
        Element reply = scon.receiveMessage();
        NodeList nodeList = reply.getElementsByTagName(T_ROW);
        //get the rows and put in the map <code> resultMap </code>
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i).getFirstChild();
            rowList = new ArrayList();
            while (null != child) {
                Element childElement = (Element) child;
                rowList.add(childElement.getTextContent());
                child = (Element) child.getNextSibling();
            }

            resultMap.put(i, rowList);
        }
        resultMap.put("VolumeStatus", reply.getAttribute(A_ERROR_MESSAGE));
        setResult(resultMap);
    }
}
