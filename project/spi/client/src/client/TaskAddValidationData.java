/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.FieldValidationData;
import common.msg.MessageWriter;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.w3c.dom.Element;

/**
 * ClientTask to add validation data.
 * @author bmurali
 */
public class TaskAddValidationData extends ClientTask {

    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** validation data */
    private FieldValidationData fieldValidationData;

    public TaskAddValidationData(FieldValidationData fieldValidationData) {
        this.fieldValidationData = fieldValidationData;
    }

    /**
     * Write the message with parameters.
     * The parameters are obtained from the fields of 
     * <code>fieldValidationData</code> by encoding.
     * 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_ADD_VALIDATION_DATA);
        writer.encode(FieldValidationData.class, fieldValidationData);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        setResult((Object) ok);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            JOptionPane.showMessageDialog(null,
                    "Error while adding validation data",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
}
