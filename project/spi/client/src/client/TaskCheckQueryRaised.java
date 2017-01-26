/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask for checking whether query is raised for the field of a project.
 * @author bmurali
 */
public class TaskCheckQueryRaised extends ClientTask {

    /** Project Id */
    private int projectId;
    /** Field name */
    private String fieldName;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate the object with the following parameters
     * @param projectId  Project Id
     * @param fieldName  Field name
     */
    public TaskCheckQueryRaised(int projectId, String fieldName) {

        this.projectId = projectId;
        this.fieldName = fieldName;
    }

    /**
     * Write the message with attributes and set the result.
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;

        writer = scon.startMessage(T_CHECK_QUERY_RAISED);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.writeAttribute(A_FIELD_NAME, fieldName);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        setResult(reply);
    }
}
