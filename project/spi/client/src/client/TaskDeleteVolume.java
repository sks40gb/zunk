package client;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to delete a volume.
 * @see server.Handler_delete_volume
 */
public class TaskDeleteVolume extends ClientTask {

    /** Volume Id */
    private int volumeId;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this ClientTask and remember the parameter.
     * @param volumeId the volume.volume_id to be deleted
     */
    public TaskDeleteVolume(int volumeId) {
        this.volumeId = volumeId;
    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_DELETE_VOLUME);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();

        String ok = reply.getNodeName();
        Log.print("T_DELETE_VOLUME reply " + ok);
        setResult(ok);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("SendBoundary unexpected message type: " + ok);
        }
    }
}
