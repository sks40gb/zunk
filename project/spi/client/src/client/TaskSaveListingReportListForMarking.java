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
 * ClientTask to save the marked Listing report.
 * @author balab
 */
public class TaskSaveListingReportListForMarking extends ClientTask {

    /** Listing report list */
    ArrayList markingList;
    /** Field name */
    String fieldName;
    /** Project name */
    int projectId;
    /** Volume Id */
    int volumeId;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate this class with following parameters. 
     * @param markingList  Marking List
     * @param fieldName    Field name
     * @param projectId    Project Id; project.project_id from project 
     * @param volumeId     Volume Id; volume.volume_id from volume
     */
    public TaskSaveListingReportListForMarking(ArrayList markingList, String fieldName, int projectId, int volumeId) {
        this.markingList = markingList;
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
        writer = scon.startMessage(T_SAVE_LISTING_MARKING_LIST);
        writer.writeAttribute(A_FIELD_NAME, fieldName);
        writer.writeAttribute(A_PROJECT_ID, projectId);

        writer.writeAttribute(A_VOLUME_ID, volumeId);
    
        for (int i = 0; markingList.size() > i; i++) {

            writer.startElement(T_LISTING_MARKING_LIST);
            writer.writeContent((String) markingList.get(i));
            writer.endElement();
        }
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
    }
}
