/*
 * TaskEditCodingManualData.java
 *
 * Created on January 27, 2008, 11:15 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package client;

import common.EditCodingManualData;
import common.msg.MessageWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.w3c.dom.Element;

/**
 * ClientTask to edit the coding manual 
 * @author murali
 */
public class TaskEditCodingManualData extends ClientTask {

    /** Coding Manual data having all fields for coding manual */
    private EditCodingManualData editCodingManualData;
    /** Ther server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate for the object with the following parameters
     * @param editCodingManualData Coding manual data to be updated.
     */
    public TaskEditCodingManualData(EditCodingManualData editCodingManualData) {
        this.editCodingManualData = editCodingManualData;
    }

    /**
     * Write the message with attributes and set the result.
     */
    @Override
    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_EDIT_CODING_MANUAL_DATA);
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