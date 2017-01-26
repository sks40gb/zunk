/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to select the queries for the field of a project.
 * @author bmurali
 */
public class TaskSelectQueryTracker extends ClientTask {

    /** Field name */
    private String fieldName;
    /** Project Id */
    private int projectId;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instance and create the object for the this ClientTask class.
     * Remember the following parameters.
     * @param fieldName Field name
     * @param projectId Project Id
     */
    public TaskSelectQueryTracker(String fieldName, int projectId) {
        this.fieldName = fieldName;
        this.projectId = projectId;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_FIELD_DESCRIPTION);
        writer.writeAttribute(A_SELECTED_FIELD_NAME, fieldName);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.endElement();
        writer.close();
        Element reply = scon.receiveMessage();
        String description = reply.getAttribute(A_SELECTED_FIELD_NAME);
        setResult(description);
    }
}