/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskUpdateValues.java,v 1.7.6.1 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called from <code>beans.SweepTablevalue</code> to normalize coded
 * data and to delete coded data values.
 * @see beans.SweepTablevalue
 * @see server.Handler_update_values
 */
public class TaskUpdateValues extends ClientTask {

    final private ServerConnection scon = Global.theServerConnection;
    private int projectId;
    private int tablespecId;
    private String type;
    private String data;
    private String old_data = "";

    /**
     * Create an instance of this task and remember the parameters.
     * @param projectId the project.project_id of the project owner of the tablespec
     * @param tablespecId the tablespec.tablespec_id of the table being normalized
     * @param type name or text
     * @param old_data the data being replaced
     */
    public TaskUpdateValues(int projectId, int tablespecId, String type, String old_data) {
        this(projectId, tablespecId, type, old_data, "");
    }

    /**
     * Create an instance of this task and remember the parameters.
     * @param projectId the project.project_id of the project owner of the tablespec
     * @param tablespecId the tablespec.tablespec_id of the table being normalized
     * @param type name or text
     * @param old_data the data being replaced
     * @param data the new data to be used in the normalize
     */
    public TaskUpdateValues(int projectId, int tablespecId, String type, String old_data, String data) {
        this.projectId = projectId;
        this.tablespecId = tablespecId;
        this.type = type;
        this.old_data = old_data;
        this.data = data;
    }

    /**
     * Write the message with attributes and set the result.     
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_UPDATE_VALUES);
        writer.writeAttribute(A_ID, projectId);
        writer.writeAttribute(A_TABLESPEC_ID, tablespecId);
        writer.writeAttribute(A_TYPE, type);
        if (data.length() > 0) {
            writer.startElement(A_DATA);
            writer.writeContent(data);
            writer.endElement();
        }
        if (old_data.length() > 0) {
            writer.startElement(A_OLD_DATA);
            writer.writeContent(old_data);
            writer.endElement();
        }
        addStandardAttributes(writer);
        writer.endElement();
        writer.close();
        Element reply = scon.receiveMessage();
        if (T_UPDATE_COUNT.equals(reply.getNodeName())) {
            setResult((String) reply.getAttribute(A_COUNT));
        } else {
            Log.quit("TaskUpdateValues unexpected message type: " + reply.getNodeName());
        }
    }
}
