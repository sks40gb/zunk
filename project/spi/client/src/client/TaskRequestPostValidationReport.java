/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;
import java.sql.ResultSet;

/**
 * ClientTask to get the Post Validation Report.
 * @author sunil
 */
public class TaskRequestPostValidationReport extends ClientTask {

    /** Project Id */
    private int projectId;
    /** Volume Id */
    private int volumeId;
    /** Server connection */
    private ServerConnection scon = Global.theServerConnection;

    /**
     * Create and instance of this class and remember the following parameters 
     * @param projectId
     * @param volumeId
     */
    public TaskRequestPostValidationReport(int projectId, int volumeId) {
        this.projectId = projectId;
        this.volumeId = volumeId;
    }

    /**
     * Write the message with attributes and set the result with the
     * data decoded into <code>common.MailText</code>.
     */
    public void run() throws IOException {
        try {
            MessageWriter writer = scon.startMessage(T_REQUEST_POST_VALIDATIONS_REPORT);
            writer.writeAttribute(A_PROJECT_ID, Integer.toString(projectId));
            writer.writeAttribute(A_VOLUME_ID, Integer.toString(volumeId));
            writer.endElement();
            writer.close();
            Element reply = scon.receiveMessage();
            final ResultSet rs = Sql.resultFromXML(reply);
            setResult(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
