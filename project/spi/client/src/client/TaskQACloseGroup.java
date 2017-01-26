/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * Task to Close Selected QA Group
 * @author Prakasha
 */
public class TaskQACloseGroup extends ClientTask{

    private int groupNumber;
    private int volumeId;
    final private ServerConnection scon = Global.theServerConnection;
    
    /**
     * Create Instance of this class and remember the groupNumber and VolumeId
     *
     * @param groupNumber
     * @param volumeId
     */
    public TaskQACloseGroup(int groupNumber,int volumeId) {
        this.groupNumber = groupNumber;
        this.volumeId = volumeId;
    }

    public void run() throws IOException {
      MessageWriter writer;
      writer = scon.startMessage(T_QA_PR_CLOSE_GROUP);  
      writer.writeAttribute(A_GROUP_NUMBER, groupNumber);
      writer.writeAttribute(A_VOLUME_ID, volumeId);
      writer.endElement();
      writer.close();
      
      Element reply = scon.receiveMessage();
        
    
    }
}
