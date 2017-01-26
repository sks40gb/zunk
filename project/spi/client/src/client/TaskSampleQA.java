/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSampleQA.java,v 1.4.6.1 2006/03/21 16:42:41 nancy Exp $ */
package client;

import common.msg.MessageWriter;

import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import org.w3c.dom.Element;

/**
 * ClientTask to choose the given percentage of children in QA status
 *  from all teams from the volume open for QA by the current user.
 * Selecton is done on a team-by-team basis, so each team has at least the
 * given percentage.  A batch is assumed to belong to the team
 * indicated by the last coder to open it.  Selection is also made
 * for a "null" team, for users who have no team assigned.
 * @see server.Handler_sample_qa
 */
public class TaskSampleQA extends ClientTask {

    /** percentage of children to return in the sample */
    private int percent;
    /** Teams Id */
    private String teamsIdString;
    /** User Id */
    private String usersIdString;
    /** New sample */
    private boolean newSample;
    /** Accuracy Required */
    private float accuracyRequired;
    /** sampling_for */
    private String sampling_for;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameters.
     * @param percent the percentage of children to return in the sample
     * @param teamsIdString limit the sampling to children in this teams.teams_id
     * @param usersIdString limit the sampling to children in the users.users_id
     * @param newSample
     */
    public TaskSampleQA(int percent, String teamsIdString,
            String usersIdString, boolean newSample, float accuracyRequired, String sampling_for) {
        this.percent = percent;
        this.teamsIdString = teamsIdString;
        this.usersIdString = usersIdString;
        this.newSample = newSample;
        this.accuracyRequired = accuracyRequired;
        this.sampling_for = sampling_for;
    }

     /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_SAMPLE_QA);
        writer.writeAttribute(A_PERCENT, percent);
        if (teamsIdString != null) {
            writer.writeAttribute(A_TEAMS_ID, teamsIdString);
        } else if (usersIdString != null) {
            writer.writeAttribute(A_USERS_ID, usersIdString);
        }
        if (newSample) {
            writer.writeAttribute(A_NEW_SAMPLE, "YES");
        }
        writer.writeAttribute(A_ACCURACY_REQUIRED, Float.toString(accuracyRequired));
        writer.writeAttribute(A_SAMPLING_FOR, sampling_for);
        addStandardAttributes(writer); // adds the QCer's volume

        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();

        T_SAMPLE_FIXED_PERCENTAGE.equals(reply.getNodeName());
        String projectName = reply.getAttribute(A_PROJECT_NAME);
        String userName = reply.getAttribute(A_USER_NAME);
        String fieldName = reply.getAttribute(A_FIELD_NAME);
        
        ArrayList value = new ArrayList();
        
        value.add(projectName);
        value.add(userName);
        value.add(fieldName);
        
        setResult(value);
    }
}
