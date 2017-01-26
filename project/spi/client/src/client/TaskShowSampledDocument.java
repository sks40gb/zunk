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
 * ClientTask to display the sample document
 * @see QAGroupAssignWindow
 * @author Prakasha
 */
public class TaskShowSampledDocument extends ClientTask {

    /** Server connection */
    /** Project Id */
    private int projectId;
    /** Volume Id */
    private int volumeId;
    /** Group ID */
    private int groupNO;
    /** List of sample document to be displayed */
    private Map sampledDocumentList = null;
    final private ServerConnection scon = Global.theServerConnection;
    
    /**
     * Create an instance of this class and remember the parameters.
     * @param projectId  Project id
     * @param volumeId   Volume Id
     * @param groupId    Group id
     */

    public TaskShowSampledDocument(int projectId, int volumeId, int groupNO) {
        this.projectId = projectId;
        this.volumeId = volumeId;
        this.groupNO = groupNO;
    }

    /**
     * Write the message with attributes and set the result.     
     */
    @Override
    public void run() throws IOException {
      MessageWriter writer;
      writer = scon.startMessage(T_QA_PR_SAMPLED_DOCUMENT);  
      writer.writeAttribute(A_PROJECT_ID, projectId);
      writer.writeAttribute(A_VOLUME_ID, volumeId);
      writer.writeAttribute(A_GROUP_NUMBER, groupNO);
      writer.endElement();
      writer.close();
      
      
      Element reply = scon.receiveMessage();
        
        if (T_QA_PR_SAMPLED_DOCUMENT.equals(reply.getNodeName())) {
            sampledDocumentList = new HashMap();
            NodeList valueList = reply.getElementsByTagName("row");
             for (int i=0;i<valueList.getLength();i++) {
                 Node child = valueList.item(i).getFirstChild(); 
                 ArrayList value = new ArrayList();
                 int j=0;
                 while(child != null){                     
                     Element childElement = (Element) child;
                     value.add(childElement.getTextContent());
                     j++;
                     child = child.getNextSibling();
                 } 
                if (value != null) {
                    sampledDocumentList.put(i, value);
                }

            }

        }
        setResult(sampledDocumentList);
    }
}
