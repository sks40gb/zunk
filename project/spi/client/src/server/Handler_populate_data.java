/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_populate_data.java,v 1.4.6.4 2006/03/30 12:28:55 nancy Exp $ */
package server;

import common.Log;
import common.PopulateData;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import dbload.DiaLoad;

import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for populate_data message.  Call <code>dbload.DiaPopulate</code> with
 * the given parameters to import data to a project.
 * @see common.PopulateData
 * @see client.TaskPopulateData
 * @see dbload.DiaPopulate
 */
final public class Handler_populate_data extends Handler {
    String database = "";
    
    /**
     * This class cannot be instantiated.
     */
    public Handler_populate_data() {
    }

    public void run (ServerTask task, Element action) {
        try {
            Log.print("Handler_populate_data");
            
            database = ServerProperties.getProperty("database");
            
            Element givenValueList = action;
            Node firstChild = givenValueList.getFirstChild();
            while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
                firstChild = firstChild.getNextSibling();
            }
            if (firstChild != null) {
                PopulateData data = new PopulateData();
                // fill in the int and String fields of the ExportData
                MessageReader.decode(givenValueList, data, /* trim? */ false);
                if (data != null) {
                    // update or insert the export row contained in data
                    Log.print("(Handler_populate_data.run) project is " + data.dataFilename);
        
                    ArrayList list = new ArrayList();
                    // project_name
                    list.add("-p");
                    list.add(data.project_name);
        
                    // volume_name
                    list.add("-v");
                    list.add(data.volume_name);
        
                    list.add("-d");
                    list.add(data.dataFilename);

                    if (data.force.equals("Yes")) {
                        list.add("--replace");
                    }
                    // convert the args to a string array
                    String[] args = (String[])list.toArray(new String[list.size()]);
        
                    dbload.DiaPopulate diaPopulate = new dbload.DiaPopulate(data);
                    diaPopulate.setDatabase(database);
                    diaPopulate.run();
        
                    MessageWriter writer = task.getMessageWriter();
                    String status = diaPopulate.getStats();
                    if (status.equals(T_OK)) {
                        writer.startElement(T_OK);
                    } else {
                        writer.startElement(T_ERROR);
                        writer.writeAttribute(A_DATA, status);
                    }
                    writer.endElement();
                }
            }
        } catch (Throwable t) {
            Log.quit(t);
        }
    }
}
