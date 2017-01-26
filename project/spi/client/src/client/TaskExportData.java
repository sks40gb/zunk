/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import common.ExportData;
import common.msg.MessageWriter;
import java.io.IOException;
import java.sql.ResultSet;
import org.w3c.dom.Element;

/**
 * ClientTask to export data.
 * @author anurag
 */
public class TaskExportData extends ClientTask {

    /** The server connection */
    final private ServerConnection scon = Global.theServerConnection;
    /** export data */
    private ExportData exportData;

    public TaskExportData(ExportData exportData) {
        this.exportData = exportData;
    }

    /**
     * Write the message with parameters.
     * The parameters are obtained from the fields of 
     * <code>exportData</code> by encoding.
     * 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_EXPORT_DATA);
        writer.encode(ExportData.class, exportData);
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        final ResultSet results = Sql.resultFromXML(reply);
        setResult(results);

    }
}
