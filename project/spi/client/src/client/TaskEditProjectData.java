package client;

import common.EditCodingManualData;
import common.msg.MessageWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.w3c.dom.Element;

/**
 * ClientTask to update the project and volume
 * It updates the following record for the volume
 *                        1.image_path     
 *                        2.image_server 
 *                        3.internal_volume_name
 *                        4.vol_completed_date 
 * @author bmurali
 */
public class TaskEditProjectData extends ClientTask {

    private EditCodingManualData editCodingManualData;
    final private ServerConnection scon = Global.theServerConnection;

    public TaskEditProjectData(EditCodingManualData editCodingManualData) {
        this.editCodingManualData = editCodingManualData;
    }

    /**
     * Write the message with attributes and set the result.
     */
    @Override
    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_EDIT_PROJECT_DATA);
        writer.encode(EditCodingManualData.class, editCodingManualData);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        String msg = reply.getAttribute(A_SQLCODE);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        setResult((Object) ok);
    }
}
