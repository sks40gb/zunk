/*
 * TaskEditCodingManual.java
 *
 * Created on January 26, 2008, 3:42 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to edit the Coding Manual 
 * @author murali
 */
public class TaskEditCodingManual extends ClientTask {

    /** Project Id */
    private int projectId;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /** Creates a new instance of TaskEditCodingManual with parameter
     * 
     * @param projectId Project Id 
     */
    public TaskEditCodingManual(int projectId) {
        this.projectId = projectId;
    }

    /**
     * Write the message with attributes and set the result.
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_EDIT_CODING_MANUAL);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String fileName = reply.getAttribute(A_PATH);
        setResult(fileName);
    }
}