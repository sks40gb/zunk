/*
 * TaskGoodbye.java
 *
 * Created on December 14, 2007, 6:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to disconnect from the server.
 * Server will close the session for user.
 * @author bmurali
 */
public class TaskGoodbye extends ClientTask {

    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /** Creates a new instance of TaskGoodbye */
    public TaskGoodbye() {
    }

    //Requsets sent to the server in xml format.     
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_GOODBYE);
        writer.endElement();
        writer.close();
        Element reply = scon.receiveMessage();
    }
}
