/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendTeams.java,v 1.4.8.1 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called from <code>beans.AddEditTeams</code> to add or update
 * a team and from <code>ui.TeamAdminPage</code> to delete a team.
 * @see beans.AddEditTeams
 * @see ui.TeamAdminPage
 * @see server.Handler_teams_data
 */
public class TaskSendTeams extends ClientTask {

    /** Team Id */
    private int teamsId;
    /** Team leader Id */
    private int leaderUsersId;
    /** Name of team */
    private String teamName;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameters.
     * @param teamsId 0 to add a team; the <code>teams.teams_id</code> of a
     * team to be edited
     * @param leaderUsersId the users.users_id of the leader of this team
     * @param teamName the name of this team
     */
    public TaskSendTeams(int teamsId, int leaderUsersId, String teamName) {
        this.teamsId = teamsId;
        this.teamName = teamName;
        this.leaderUsersId = leaderUsersId;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_TEAMS_DATA);

        writer.writeAttribute(A_ID, teamsId);
        writer.writeAttribute(A_TEAM_NAME, teamName);
        writer.writeAttribute(A_USERS_ID, leaderUsersId);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();

        String ok = reply.getNodeName();
        //Log.print("T_SEND_TEAM reply " + ok);
        setResult((Object) ok);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("TaskSendTeams unexpected message type: " + ok);
        }
    }
}
