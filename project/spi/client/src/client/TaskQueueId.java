/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * Task to Get ServerTaskQueueId
 * @author balab
 */
public class TaskQueueId  extends ClientTask{
    
     /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    
     public TaskQueueId() {        
      }

     
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_SERVER_QUEUE);        
         writer.endElement();
         writer.close();
         
         Element reply = scon.receiveMessage();
           setResult((Object) reply);

//         if (T_SERVER_QUEUE_ID.equals(reply.getNodeName())) {
//          String serverQueueId = reply.getAttribute(A_SERVER_QUEUE_ID);
//          ExportPage obj = new ExportPage();
//          obj.setServerQueueId(Integer.parseInt(serverQueueId));
//         }
    }

}
