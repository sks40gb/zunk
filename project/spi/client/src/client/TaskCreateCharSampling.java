/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

/**
 * ClientTask for creating Character sampling
 * @author anurag
 */
public class TaskCreateCharSampling extends ClientTask {

    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Volume Id */
    private int volume_id;
    /** Project Id */
    private int project_id;
    /** Sample document size */
    private int sample_document_size;
    /** sampling type */
    private String samplingType;
    /** required accuracy **/
    private float requiredAccuracy;

    /**
     * Instantiate the object with  following parameters
     * @param volume_id             Volume Id
     * @param project_id            Project Id
     * @param sample_document_size  Document size
     * @param samplingType          Sampling Type
     */
    public TaskCreateCharSampling(int volume_id, int project_id, int sample_document_size,
            String samplingType, float requiredAccuracy) {
        this.volume_id = volume_id;
        this.project_id = project_id;
        this.sample_document_size = sample_document_size;
        this.samplingType = samplingType;
        this.requiredAccuracy = requiredAccuracy;
    }

    /**
     * Write the message with parameters and set the result.
     * Message should be in XML format.
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_CHARACTER_SAMPLING);
        writer.writeAttribute(A_VOLUME_ID, volume_id);
        writer.writeAttribute(A_PROJECT_ID, project_id);
        writer.writeAttribute(A_SAMPLE_SIZE, sample_document_size);
        writer.writeAttribute(A_SAMPLING_TYPE, samplingType);
        writer.writeAttribute(A_REQUIRED_ACCURACY, ""+requiredAccuracy);
        writer.endElement();
        writer.close();

        List result = new ArrayList();
        Element reply = scon.receiveMessage();
        result.add(reply.getAttribute(A_SELECTED_FIELD_NAME));
        result.add(reply.getAttribute(A_CODERS));
        setResult(result);
    }
}
