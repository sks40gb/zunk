/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.lexpar.util.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Element;

/**
 * ClientTask to get progress status while importing new project. 
 * @author anurag
 */
public class TaskRequestImportProgressStatus extends ClientTask {

    /** Server connection */
    private ServerConnection scon = Global.theServerConnection;

    /**     
     * Write the message with attributes and set the result.
     * Message should be XML format.
     */
    public void run() throws IOException {
        try {
            MessageWriter writer = scon.startMessage(T_REQUEST_IMPORT_PROGRESS_STATUS);
            writer.endElement();
            writer.close();
            Element reply = scon.receiveMessage();
            //Progress status of importing project.
            int status = Integer.parseInt(reply.getAttribute(A_IMPORT_PROGRESS_STATUS));
            //Get error if there is any problem while importing the project, otherwise it will be null.
            String errorMessage = reply.getAttribute(A_IMPORT_ERROR_MESSAGE);
            //put the progress status and error message in ArrayList <code>rec<code>.
            List rec = new ArrayList();
            rec.add(status);
            rec.add(errorMessage);
            setResult(rec);
        } catch (Exception e) {
            Log.print(e.toString());
        }
    }
}
