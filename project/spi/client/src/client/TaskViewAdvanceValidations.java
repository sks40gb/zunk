/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.lexpar.util.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;
import java.sql.ResultSet;

/**
 * ClientTask for displaying the validaton data for the field.
 * @see ui.AdvanceValidationPage
 * @see ui.PostValidationPage
 * @see ui.AdvanceValidationAddFromAll
 * @author sunil
 */
public class TaskViewAdvanceValidations extends ClientTask {

    /** Field Id */
    private int projectFieldId;
    /** Server connection */
    private ServerConnection scon = Global.theServerConnection;
    private String scope;
    private int projectId;

    /**
     * Create the instance of this class with the parameter
     * @param projectFieldId Field Id get the record for.
     */
    public TaskViewAdvanceValidations(int projectFieldId) {

        this.projectFieldId = projectFieldId;
        this.projectId = -1;
        this.scope = "All";
    }

    public TaskViewAdvanceValidations(int projectId, int projectFieldId, String scope) {

        this.projectFieldId = projectFieldId;
        this.scope = scope;
        this.projectId = projectId;
    }

    /**
     * Write the message with attributes and set the result.     
     */
    @Override
    public void run() throws IOException {
        try {
            MessageWriter writer = scon.startMessage(T_VIEW_ADVANCE_VALIDATIONS);
            writer.writeAttribute(A_PROJECT_ID, Integer.toString(projectId));
            writer.writeAttribute(A_FIELD_ID, Integer.toString(projectFieldId));
            writer.writeAttribute(A_FUNCTION_SCOPE, scope);
            writer.endElement();
            writer.close();

            Element reply = scon.receiveMessage();
            final ResultSet rs = Sql.resultFromXML(reply);

            if (T_RESULT_SET.equals(reply.getNodeName())) {
                synchronized (this) {
                    // force cache flush for rs
                }
            } else if (T_FAIL.equals(reply.getNodeName())) {
                Log.quit("Sql.TaskRequestFieldvalue: SQL error: " + reply);
            } else {
                Log.quit("TaskRequestFieldvalue: unexpected message type: " + reply.getNodeName());
            }
            setResult(rs);
        } catch (Exception exc) {
            Log.print("Validate batch failure: " + exc);
            System.err.println("Exception in TaskViewAdvanceValidations. " + exc);
        }
    }
}
