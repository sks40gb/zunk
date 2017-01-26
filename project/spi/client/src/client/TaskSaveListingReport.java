/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to save the Listing report.
 * @author bmurali
 */
public class TaskSaveListingReport extends ClientTask {

    /** Field name */
    private String fieldName;
    /** Project Id */
    private int projectId;
    /** Volume Id */
    private int volumeId;
    /** Field value of the project */
    private String fieldValue;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instance and create for this class and remember the parameters.
     * @param projectId   Project Id ;  project.project_id from project.
     * @param volumeId    Volume Id : volume.volume_id from volume.
     * @param fieldName   Field name.
     * @param fieldValue  Field value to be saved.
     */
    public TaskSaveListingReport(int projectId, int volumeId, String fieldName, String fieldValue) {
        this.fieldName = fieldName;
        this.projectId = projectId;
        this.volumeId = volumeId;
        this.fieldValue = fieldValue;
    }

    /**
     * Write the message with attributes.
     * Message should be in XML format. 
     */
    public void run() throws IOException {

        MessageWriter writer;
        writer = scon.startMessage(T_SAVE_LISTINGREPORT);
        writer.writeAttribute(A_FIELD_NAME, fieldName);
        writer.writeAttribute(A_PROJECT_ID, projectId);
        writer.writeAttribute(A_VOLUME_ID, volumeId);

        //TODO sunil --- check whether this field value is not going to be used.
        //writer.writeAttribute(A_FIELD_VALUE, fieldValue);    

        writer.endElement();
        writer.close();
        //reply is not required.
        Element reply = scon.receiveMessage();

    }
}
