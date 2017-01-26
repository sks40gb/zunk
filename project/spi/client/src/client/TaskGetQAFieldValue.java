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
 * Task to Get QA FieldValue for Given Document
 * @author Prakasha
 */
public class TaskGetQAFieldValue extends ClientTask {
    private int projectId;
    private int volumeId;
    private int childId;
    private int samplingId;
    private String samplingType;
    private Map fieldValueList = null;
    final private ServerConnection scon = Global.theServerConnection;
    
    /**
     * Instantiate with following parameters 
     *
     * @param projectId  //Project
     * @param volumeId   //Volume
     * @param childId    //Document
     * @param samplingId //Sampling Id
     * @param samplingType //Sampling Type
     */
    public TaskGetQAFieldValue(int projectId, int volumeId, int childId, int samplingId,
            String samplingType) {
        
        this.projectId = projectId;
        this.volumeId = volumeId;
        this.childId = childId;
        this.samplingId = samplingId;
        this.samplingType = samplingType;
    }

    public void run() throws IOException {
      MessageWriter writer;
      writer = scon.startMessage(T_QA_PR_GET_FIELDVALUES);  
      writer.writeAttribute(A_PROJECT_ID, projectId);
      writer.writeAttribute(A_VOLUME_ID, volumeId);
      writer.writeAttribute(A_CHILD_ID, childId);
      writer.writeAttribute(A_SAMPLING_ID, samplingId);
      writer.writeAttribute(A_SAMPLING_TYPE, samplingType);
      writer.endElement();
      writer.close();
      
      //Get reply from server
      Element reply = scon.receiveMessage();
        
        if (T_QA_PR_GET_FIELDVALUES.equals(reply.getNodeName())) {
            fieldValueList = new HashMap();
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

                 if(value != null){                   
                 fieldValueList.put(i,value);                 
                 }

            }

         }
      setResult(fieldValueList);
    }
}
