/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import valueobjects.Feedback;
import com.lexpar.util.Log;
import common.InvestigationFields;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;
import java.sql.ResultSet;

/**
 * ClientTask to get Investigarion Feedback data.
 * @see InvestigationFields
 * @see Feedback
 * 
 * @author sunil 
 */
public class TaskRequestInvestigationData extends ClientTask {

    /** Investingation data */
    private InvestigationFields ifields;
    /** Server connection */
    private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate this ClientTask object with the parameter
     * @param feedback  Investigation Feedback data
     */
    public TaskRequestInvestigationData(Feedback feedback) {
        ifields = new InvestigationFields();
        ifields.projecId = Feedback.projecId;
        ifields.batchId = feedback.getBatchId();

        try {
            // Sometimes while retriving record from the XML file the <code>.0</code> append with the batch Id.
            // The appended part should be removed otherwise it will give the wrong data.
            if (ifields.batchId.contains(".")) {
                ifields.batchId = ifields.batchId.substring(0, ifields.batchId.indexOf("."));
            }
        } catch (NumberFormatException e) {
            Log.print(this.getClass().getSimpleName() + " : \n" + e.toString());
        }

        // Set Investigation data and send the message by decoding this <code>ifields</code>.
        ifields.boxId = feedback.getBoxId();
        ifields.docNumber = feedback.getDocNumber();
        ifields.tagName = feedback.getTagName();
        ifields.tagValue = feedback.getTagValue();
        ifields.correctTagValue = feedback.getCorrectTagValue();
        ifields.auditProblem = feedback.getAuditProblem();
        ifields.procText = feedback.getProcText();
    }

    /**     
     * Write the message with attributes and set the result.
     * Message should be XML format.
     */
    public void run() throws IOException {
        try {
            MessageWriter writer = scon.startMessage(T_REQUEST_INVESTIGATION_DATA);
            writer.encode(InvestigationFields.class, ifields);
            writer.endElement();
            writer.close();
            Element reply = scon.receiveMessage();
            final ResultSet rs = Sql.resultFromXML(reply);

            if (T_RESULT_SET.equals(reply.getNodeName())) {
                synchronized (this) {
                    // force cache flush for rs
                    }
            } else {
                Log.quit("TaskRequestInvestigationData: unexpected message type: " + reply.getNodeName());
            }
            setResult(rs);

        } catch (Exception e) {
            Log.print(this.getClass().getSimpleName() + " : \n" + e.toString());
        }
    }
}
