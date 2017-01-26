/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.w3c.dom.Element;

/**
 * ClientTask to save the Listing report for occurance for a field of
 * the project.
 * 
 * @author balab
 */
public class TaskSaveListingReportListForOccurrence extends ClientTask {

    /** Report List for occurance */
    ArrayList reportList;
    /** Field name */
    String fieldName;
    /** Project Id */
    int projectId;
    /** Volume Id */
    int volumeId;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instance and create the object for this ClientTask class.
     * Remember the parameters.
     * @param reportList  Report list for Listing for a field.
     * @param fieldName   Field for which listing record going to be saved.
     * @param projectId   Project id; project.project_id from project.
     * @param volumeId    Volume Id; can retrieved from volume.volume_id form volume.
     */
    public TaskSaveListingReportListForOccurrence(ArrayList reportList, String fieldName, int projectId, int volumeId) {
        this.reportList = reportList;
        this.fieldName = fieldName;
        this.projectId = projectId;
        this.volumeId = volumeId;
    }

    /**
     * Write the message with attributes.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {

        MessageWriter writer;
        writer = scon.startMessage(T_SAVE_LISTING_OCCURRENCE_LIST);
        writer.writeAttribute(A_FIELD_NAME, fieldName);
        writer.writeAttribute(A_PROJECT_ID, projectId);

        writer.writeAttribute(A_VOLUME_ID, volumeId);

        for (int i = 0; reportList.size() > i; i++) {

            writer.startElement(T_LISTING_OCCURRENCE_LIST);
            writer.writeContent((String) reportList.get(i));
            writer.endElement();
        }
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
    }
}
