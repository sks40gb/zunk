/*
 * Class used to send request to server
 * to get the QASampling report for a given volume
 */

package client;

import common.QAInspectionReportData;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 *Task Class to send and retrieve QAInspectionReport 
 * @author Prakasha
 */
public class TaskQAInspectionReport extends ClientTask {

    private int projectId;
    private int volumeId;
    private ServerConnection scon = Global.theServerConnection;
 
    public TaskQAInspectionReport(int projectId, int volumeId){
       this.projectId = projectId;
       this.volumeId = volumeId;
      
    }
        
    public void run() throws IOException {
        //Requsets sent to the server in xml format. 
        MessageWriter writer = scon.startMessage(T_QA_SAMPLING_REPORT);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.endElement();
        writer.close(); 
        
        //request form server in xml format
        Element reply = scon.receiveMessage();
        QAInspectionReportData qaInspectionReportData = null;
        if (T_QA_SAMPLING_REPORT.equals(reply.getNodeName())) {
            qaInspectionReportData = new QAInspectionReportData();
            MessageReader.decode(reply, qaInspectionReportData);
         }
      setResult(qaInspectionReportData);
                 
    }

}
