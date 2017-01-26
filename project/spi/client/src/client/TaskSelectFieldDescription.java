/*
 * TaskSelectFieldDiscription.java
 *
 * Created on December 18, 2007, 4:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to get the field description.
 * @author bmurali
 */
public class TaskSelectFieldDescription extends ClientTask {

    /** Field name */
    private String fieldName;
    /** Project id */
    private int projectId;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instance and create the object for this class and remember the parameters.
     * @param fieldName  Field name of the project for which descrpition needs to be get.
     * @param projectId  Project Id can be retrieve from project.project_id from project.
     */
    public TaskSelectFieldDescription(String fieldName, int projectId) {
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