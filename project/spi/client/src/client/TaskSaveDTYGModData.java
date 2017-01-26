/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.DTYGFieldsData;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to save the DTYG modification data to the database.
 * @see DTYGFieldsData 
 * 
 * @author sunil
 */
public class TaskSaveDTYGModData extends ClientTask {

    /** DTYG Modification data  to be saved */
    private DTYGFieldsData data;
    /** Server connection */
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate this ClientTask class with parameter
     * @param data DTYG data to be saved.
     */
    public TaskSaveDTYGModData(DTYGFieldsData data) {
        this.data = data;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_SAVE_DTYG_MOD_DATA);
        writer.encode(DTYGFieldsData.class, data);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        setResult((Object) ok);
    }
}
