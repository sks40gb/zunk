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
 * ClientTask to get sampling type for the user.
 * @author Prakasha
 */
public class TaskQAAssignUser extends ClientTask {

    /** server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** group number */
    private int groupNumber;
    /** User name */
    private String userName;
    private int volumeId;

    /**
     * Get the intance of this ClientTask and remember following parameters
     * @param userName    User name
     * @param groupNumber Group number
     */
    public TaskQAAssignUser(String userName, int groupNumber, int volumeId) {
        this.userName = userName;
        this.groupNumber = groupNumber;
        this.volumeId = volumeId;
    }

    /**
     * Write the message with attributes and set the result.
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_QA_PR_ASSIGN_USER);
        writer.writeAttribute(A_USER_NAME, userName);
        writer.writeAttribute(A_GROUP_NUMBER, groupNumber);
        writer.writeAttribute(A_VOLUME_ID, volumeId);
        writer.endElement();
        writer.close();
        List result = new ArrayList();

        Element reply = scon.receiveMessage();
        result.add(reply.getAttribute(A_ERROR_MESSAGE));
        result.add(reply.getAttribute(A_SAMPLING_TYPE));
        setResult(result);
    }
}
