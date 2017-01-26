/*
 * TaskCodingManule.java 
 * Created on January 22, 2008, 2:02 PM
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask for opening the coding manual file like pdf, txt etc  
 * related to the project.
 * @author bmurali
 */
public class TaskCodingManual extends ClientTask {

    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Project Id */
    private int projectId;

    /** Creates a new instance of TaskCodingManule with parameter     
     * @param projectId  Project Id
     */
    public TaskCodingManual(int projectId) {
        this.projectId = projectId;
    }

    @Override
    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_CODING_MANUAL);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        setResult((String) reply.getAttribute(A_PATH));
    }
}
