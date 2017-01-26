/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskTeamsvolume.java,v 1.4.6.1 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called from <code>ui.BatchingPage</code> to remove a volume
 * from all users and teams queues.
 * @see ui.BatchingPage
 * @see server.Handler_teamsvolume
 */
public class TaskTeamsvolume extends ClientTask {

    private int volumeId = 0;
    private int teamsId = 0;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameter.
     */
    public TaskTeamsvolume(int volumeId) {
        this(volumeId, 0);
    }

    /**
     * Create an instance of this class and remember the parameters.
     * @param volumeId required - volume to remove from all queues
     * @param teamsId 0 for remove from all queues; teams.teams_id of 
     * a single team from which to remove the volume
     */
    public TaskTeamsvolume(int volumeId, int teamsId) {
        this.volumeId = volumeId;
        this.teamsId = teamsId;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_TEAMSVOLUME);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.writeAttribute(A_TEAMS_ID, teamsId);
        writer.endElement();
        writer.close();
        Log.print("(TaskTeamsvolume.run) volume/teams " + volumeId + "/" + teamsId);

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        //Log.print("T_TEAMSVOLUME reply " + ok);
        setResult((Object) reply);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("Teamsvolume unexpected message type: " + ok);
        }
    }
}