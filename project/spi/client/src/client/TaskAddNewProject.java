package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask for adding a new project.  
 * @author anurag
 */
public class TaskAddNewProject extends ClientTask {

    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;
    
    private String projectName = null;
    
    public TaskAddNewProject(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_ADD_NEW_PROJECT);
        writer.writeAttribute(A_PROJECT_NAME,projectName);                
        writer.endElement();
        writer.close();

        // Recieve the reply from the server.
        Element reply = scon.receiveMessage();
        setResult(reply);
    }
}
