/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskBatchQueue.java,v 1.4.6.2 2006/03/14 15:08:46 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask for queuing batches and volumes to usersqueue and teamsqueue.
 * @see server.Handler_batch_queue
 */
public class TaskBatchQueue extends ClientTask {

    /** Batch Id */
    private int batchId;
    /** Volume Id */
    private int volumeId;
    /** User Id */
    private int usersId;
    /** Team Id */
    private int teamsId;
    /** Remove other task in queue ? */
    private boolean clearQueues = false;
    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of TaskBatchQueque that defaults to NOT clearing the queues.
     * @param batchId the batch.batch_id of the batch to be queued; 0 to queue a volume
     * @param volumeId required - volume to queue if batch_id is 0; else volume_id containing the batch
     * @param usersId 0 if queuing to a team; else users_id to use in usersqueue
     * @param teamsId 0 if queuing to a user; else teams_id to use in teamsqueue
     */
    public TaskBatchQueue (int batchId, int volumeId, int usersId, int teamsId) {
        this(batchId, volumeId, usersId, teamsId, false);
    }

    /**
     * Create an instance of this class and remember the given parameters.
     * @param batchId the batch.batch_id of the batch to be queued; 0 to queue a volume
     * @param volumeId required - volume to queue if batch_id is 0; else volume_id containing the batch
     * @param usersId 0 if queuing to a team; else users_id to use in usersqueue
     * @param teamsId 0 if queuing to a user; else teams_id to use in teamsqueue
     * @param clearQueues means a drop happened from the usersTree and all
     * occurrences of this batch should be removed from the queues
     * before the batch is added to the destination queue.
     */
    public TaskBatchQueue (int batchId, int volumeId, int usersId, int teamsId, boolean clearQueues) {
        this.batchId = batchId;
        this.volumeId = volumeId;
        this.usersId = usersId;
        this.teamsId = teamsId;
        this.clearQueues = clearQueues;
    }

    /**
     * Write the message with attributes and set the result.
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_BATCH_QUEUE);
        writer.writeAttribute(A_BATCH_ID, batchId);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_USERS_ID, usersId);
        writer.writeAttribute(A_TEAMS_ID, teamsId);
        writer.writeAttribute(A_DELETE, clearQueues ? "true" : "false");
        writer.endElement();
        writer.close();
        Log.print("(TaskBatchQueue.run) batch/volume/users/teams " + batchId + "/" + volumeId 
                  + "/" + usersId + "/" + teamsId);

        Element reply = scon.receiveMessage();        
        String ok = reply.getNodeName();
        setResult((Object)reply);

        if (! T_OK.equals(ok)
            && ! T_FAIL.equals(ok)) {
            Log.quit("BatchQueue unexpected message type: "+ok);
        }
    }
}
