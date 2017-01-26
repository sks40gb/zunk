/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendProjectFieldsData.java,v 1.4.6.1 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.ProjectFieldsData;
import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called by <code>beans.AddEditProjecFields</code> to add 
 * and update the dynamic fields of a project (<code>projectfields</code>)
 * and by <code>ui.ProjectAdminPage</code> to resequence existing
 * fields within a project.
 * @see common.ProjectFieldsData
 * @see server.Handler_projectfields_data
 */
public class TaskSendProjectFieldsData extends ClientTask {

    private ProjectFieldsData projectFieldsData;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameter.
     * @param projectFieldsData an instance of <code>common.ProjectFieldsData</code>
     * containing the updates to send to the server
     */
    public TaskSendProjectFieldsData(ProjectFieldsData projectFieldsData) {
        this.projectFieldsData = projectFieldsData;
    }

    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_PROJECTFIELDS_DATA);
        writer.encode(ProjectFieldsData.class, projectFieldsData);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        //Log.print("T_PROJECTFIELDS_DATA reply " + ok);
        setResult((Object) ok);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("SendProjectFieldsData unexpected message type: " + ok);
        }
    }
}
