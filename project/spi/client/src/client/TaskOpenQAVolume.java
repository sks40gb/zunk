/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskOpenQAVolume.java,v 1.4.6.2 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to open a volume for QA.  Pass the volume_id to the Handler
 * for the volume to be opened.
 * @see server.Handler_open_qa_volume
 */
public class TaskOpenQAVolume extends ClientTask {
    
    /** Voluem id */
    private int volumeId;
    /** server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameter.
     * @param volumeId the volume.volume_id of the volume to open
     */
    public TaskOpenQAVolume(int volumeId) {
        this.volumeId = volumeId;
    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_OPEN_QA_VOLUME);
        writer.writeAttribute(A_VOLUME_ID, Integer.toString(volumeId));
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        setResult(reply);
    }
}
