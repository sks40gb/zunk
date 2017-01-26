/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendUsersData.java,v 1.7.6.1 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.UsersData;
import common.msg.MessageWriter;

import java.io.IOException;
import javax.swing.JOptionPane;
import org.w3c.dom.Element;

/**
 * ClientTask called from <code>beans.AddEditUsers</code> to update and add 
 * <code>users</code> data and from <code>ui.UserAdminPage</code> to
 * delete a user.
 * @see beans.AddEditUsers
 * @see ui.UserAdminPage
 * @see common.UsersData
 * @see server.Handler_users_data
 */
public class TaskSendUsersData extends ClientTask {

    private UsersData usersData;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameter.
     * @param usersData an instance of <code>common.UsersData</code>
     * containing the client updates for the server
     */
    public TaskSendUsersData(UsersData usersData) {
        this.usersData = usersData;
    }

    /**
     * Write the message with attributes and set the result.     
     */
    @Override
    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_USERS_DATA);
        writer.encode(UsersData.class, usersData);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        //Log.print("T_USERS_DATA reply " + ok);
        setResult((Object) ok);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            //    Log.quit("SendUsersData unexpected message type: "+ok);
            JOptionPane.showMessageDialog(null,
                    "Duplicate User Id." + "\n\nPlease re-enter user data.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
