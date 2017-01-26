/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;
/**
 * ClientTask to get sampling parameters
 * @author anurag
 */
public class TaskGetSamplingParameters extends ClientTask {
    
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** array size upto which the result going to be stored. */
    private int RESULT_SIZE = 20;
    /** Volume Id */
    private int volume_id;
    /** Project id */
    private int project_id;    
   
    /**
     * Instantiate with following parameters 
     * @param volume_id  Volume Id
     * @param project_id Project Id
     */
   public TaskGetSamplingParameters(int volume_id,int project_id){
      this.volume_id = volume_id;
      this.project_id = project_id;
   }

   /**
    * Send the request in xml format to get sampling parameters.
    * Get the reply from the server.
    * Convert the reply to array.
    * Put the array to result.
    * @throws java.io.IOException
    */
   @Override
   public void run() throws IOException
   {     
      MessageWriter writer;
      writer = scon.startMessage(TASK_GET_SAMPLING_PARAMETERS);
      writer.writeAttribute(A_VOLUME_ID, volume_id);      
      writer.writeAttribute(A_PROJECT_ID, project_id);      
      writer.endElement();
      writer.close();
      //get the reply from the server.
      Element reply = scon.receiveMessage();
      //create the array of size <code>RESULT_SIZE</coce>
      String[] resultArray = new String[RESULT_SIZE];
      resultArray[0] = reply.getAttribute(A_PROJECT_NAME);
      resultArray[1] = reply.getAttribute(A_VOLUME_NAME);
      resultArray[2] = reply.getAttribute(A_SAMPLING_METHOD);
      resultArray[3] = reply.getAttribute(A_SAMPLING_TYPE);
      resultArray[4] = reply.getAttribute(A_INSPECTION_TYPE);
      resultArray[5] = reply.getAttribute(A_AQL_VALUE);
      resultArray[6] = reply.getAttribute(A_REJECT_NUMBER);
      resultArray[7] = reply.getAttribute(A_ERROR_COUNT);
      resultArray[8] = reply.getAttribute(A_TOTAL_DOCS);
      resultArray[9] = reply.getAttribute(A_TOTAL_FIELDS);
      resultArray[10] = reply.getAttribute(A_SAMPLING_DOCS);
      resultArray[11] = reply.getAttribute(A_SAMPLING_FIELDS);
      resultArray[12] = reply.getAttribute(A_SAMPLING_RESULT);
      resultArray[13] = reply.getAttribute(A_QA_LEVEL);
      resultArray[14] = reply.getAttribute(A_SAMPLING_ID);
      resultArray[15] = reply.getAttribute(A_IS_SAMPLING_DONE);
      resultArray[16] = reply.getAttribute(A_TAGS_COUNT);
      resultArray[17] = reply.getAttribute(A_MISCODED);
      resultArray[18] = reply.getAttribute(A_UNCODED);
      resultArray[19] = reply.getAttribute(A_ADDED);
      setResult(resultArray);
   }   
}