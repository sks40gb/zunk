/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask for creating ISO sampling
 * @author anurag
 */
public class TaskISOSampling extends ClientTask {

    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Volume Id */
    private int volume_id;
    /** Project Id */
    private int project_id;
    /** Sample document size */
    private int sample_document_size;
    /** user who coded the for the docuemnt */
    private String coders;
    /** Number of fields */
    private String fieldsCounted;

    /**
     * Instantiate the object with  following parameters
     * @param volume_id             Volume Id
     * @param project_id            Project Id
     * @param sample_document_size  Document size
     * @param coders                Coder for the sampling
     * @param fieldsCounted         Number of fields
     */
    public TaskISOSampling(int volume_id, int project_id, int sample_document_size, String coders, String fieldsCounted) {
        this.volume_id = volume_id;
        this.project_id = project_id;
        this.sample_document_size = sample_document_size;
        this.coders = coders;
        this.fieldsCounted = fieldsCounted;
    }

    /**
     * Write the message with parameters and set the result.
     * Message should be in XML format.
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_ISO_SAMPLING);
        writer.writeAttribute(A_VOLUME_ID, volume_id);
        writer.writeAttribute(A_PROJECT_ID, project_id);
        writer.writeAttribute(A_SAMPLE_SIZE, sample_document_size);
        writer.writeAttribute(A_CODERS, coders);
        writer.writeAttribute(A_FIELD_COUNTED, fieldsCounted);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        setResult(reply.getAttribute(A_COUNT));
    }
}
