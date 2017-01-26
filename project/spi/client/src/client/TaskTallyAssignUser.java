/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to get sampling type for the user.
 * @author Prakasha
 */
public class TaskTallyAssignUser extends ClientTask {

    /** server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** Insert Process */
    private boolean isInsertProcess;
    private int userId;
    private int tally_dictionary_group_id;
    private int tally_assignment_id;
    private String errorStatus = null;
    private String tallyAssignmentId = null;

    /**
     * Get the intance of this ClientTask and remember following parameters
     * @param userName    User name
     * @param groupNumber Group number
     */
    public TaskTallyAssignUser(boolean isInsertProcess,int userId, int tally_dictionary_group_id,int tally_assignment_id) {
        this.isInsertProcess = isInsertProcess;
        this.userId = userId;
        this.tally_dictionary_group_id = tally_dictionary_group_id;
        this.tally_assignment_id = tally_assignment_id;
    }

    /**
     * Write the message with attributes and set the result.
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_TALLY_ASSIGN_USER);
        writer.writeAttribute(A_INSERT_PROCESS ,""+isInsertProcess);
        writer.writeAttribute(A_USERS_ID, userId);
        writer.writeAttribute(A_TALLY_GROUP_ID, tally_dictionary_group_id);
        writer.writeAttribute(A_TALLY_ASSIGNMENT_ID, tally_assignment_id);
        writer.endElement();
        writer.close();
        
        Element reply = scon.receiveMessage();
        errorStatus = reply.getAttribute(A_ERROR_MESSAGE);
        tallyAssignmentId = reply.getAttribute(A_TALLY_ASSIGNMENT_ID);
        String serverReply[] =  new String[]{errorStatus,tallyAssignmentId};
        setResult(serverReply);
        
    }
}
