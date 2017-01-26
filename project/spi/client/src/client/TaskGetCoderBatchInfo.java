/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.msg.MessageWriter;
import java.io.IOException;
import java.sql.ResultSet;
import org.w3c.dom.Element;

/**
 * ClientTask to execute the query and get the coded batch information.
 * like batch and user who coded the batch. 
 */
public class TaskGetCoderBatchInfo extends ClientTask {

    /** Volume Id */
    private int volumeid;
    /** The server connection */
    private ServerConnection scon = Global.theServerConnection;

    /**
     * Instantiate the object of this class with parameter.
     * @param volumeid Volume Id get the batch info for this volume id only.
     */
    public TaskGetCoderBatchInfo(int volumeid) {
        this.volumeid = volumeid;

    }
    //Requsets sent to the server in xml format.     

    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_GET_CODER_BATCH_INFO);
        writer.writeAttribute(A_VOLUME_ID, volumeid);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        final ResultSet rsObj = Sql.resultFromXML(reply);
        setResult(rsObj);
    }
}