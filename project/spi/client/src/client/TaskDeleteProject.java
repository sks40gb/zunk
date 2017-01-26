/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskDeleteProject.java,v 1.1.6.1 2006/03/14 15:08:46 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to mark the given project as deleted and remove
 * the project components from the database.
 * @see server.Handler_delete_project
 */
public class TaskDeleteProject extends ClientTask {

    /** Project Id */
    private int projectId;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this ClientTask and remember the parameter.
     * @param projectId the project.project_id of the project to mark as
     * deleted
     */
    public TaskDeleteProject(int projectId) {
        this.projectId = projectId;
    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_DELETE_PROJECT);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();

        String ok = reply.getNodeName();
        Log.print("T_DELETE_PROJECT reply " + ok);
        setResult(ok);

        if (! T_OK.equals(ok)
        && ! T_FAIL.equals(ok)) {
            Log.quit("SendBoundary unexpected message type: "+ok);
        }
    }
}
