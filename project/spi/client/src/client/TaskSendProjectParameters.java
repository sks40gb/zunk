/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to send project parameters.
 * @author anurag
 */
public class TaskSendProjectParameters extends ClientTask {

    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;
    private int volume_id;
    private int project_id;
    private int lotsize;
    private String inspectionType;
    private String aql;
    private String samplingMethod;
    private String samplingType;
    private String accuracy;
    
    /**
     * Instantiate this class with following parameters.
     * 
     * @param volume_id          //Volume
     * @param lotsize            //lotsize of volume
     * @param inspectionType     //InspectionType
     * @param aql                //AQL Value
     * @param project_id         //Project
     * @param samplingMethod     //Sampling Method
     * @param samplingType       //Type of Sampling
     * @param accuracy           //Required Accuracy
     */
    public TaskSendProjectParameters(int volume_id, int lotsize, String inspectionType, String aql, int project_id, String samplingMethod, String samplingType, String accuracy) {
        this.volume_id = volume_id;
        this.lotsize = lotsize;
        this.inspectionType = inspectionType;
        this.aql = aql;
        this.project_id = project_id;
        this.samplingMethod = samplingMethod;
        this.samplingType = samplingType;
        this.accuracy = accuracy;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_SEND_PROJECT_PARAMETERS);
        writer.writeAttribute(A_PROJECT_ID, project_id);
        writer.writeAttribute(A_VOLUME_ID, volume_id);
        writer.writeAttribute(A_LOT_SIZE, lotsize);
        writer.writeAttribute(A_INSPECTION_TYPE, inspectionType);
        writer.writeAttribute(A_AQL_VALUE, aql);
        writer.writeAttribute(A_SAMPLING_METHOD, samplingMethod);
        writer.writeAttribute(A_SAMPLING_TYPE, samplingType);
        writer.writeAttribute(A_ACCURACY, accuracy);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String[] resultArray = new String[4];
        for (int i = 0; i < resultArray.length; i++) {
            System.out.println("resultArray: " + resultArray[i]);
        }
        resultArray[0] = reply.getAttribute(A_SAMPLE_SIZE);
        resultArray[1] = reply.getAttribute(A_ACCEPT_NUMBER);
        resultArray[2] = reply.getAttribute(A_REJECT_NUMBER);
        resultArray[3] = reply.getAttribute(A_ERROR_COUNT);
        setResult(resultArray);
    }
}
