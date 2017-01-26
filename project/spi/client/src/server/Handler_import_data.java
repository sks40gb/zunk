/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_import_data.java,v 1.8.6.3 2006/08/23 19:04:52 nancy Exp $ */
package server;

import common.Log;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import dbload.DiaLoad;

import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler to create a String array of parameters, then use it in creating
 * and running an instance of dbload.DiaLoad to import the volume.
 * @see dbload.DiaLoad
 */
final public class Handler_import_data extends Handler {
    String database = "";
    String project_name = "";
    String volume_name = "";
    int batch_span = 0;
    String format = "";
    String lfp_filename = "";
    String image_pathname = "";
    boolean overwrite = false;
    boolean split_documents = false;

    /**
     * This class cannot be instantiated.
     */
    public Handler_import_data() {
    }

    public void run (ServerTask task, Element action) {
        try {
            Log.print("Handler_import_data");
            project_name = action.getAttribute(A_PROJECT_NAME);
            volume_name = action.getAttribute(A_VOLUME_NAME);
            batch_span = Integer.parseInt(action.getAttribute(A_BATCH_SPAN));
            lfp_filename = action.getAttribute(A_FILENAME);
            image_pathname = action.getAttribute(A_PATHNAME);
            format = action.getAttribute(A_FORMAT);
            overwrite = action.getAttribute(A_OVERWRITE).equals("Yes") ? true : false;
            split_documents = action.getAttribute(A_SPLIT_DOCUMENTS).equals("Yes") ? true : false;
            
            database = ServerProperties.getProperty("database");
            
            ArrayList list = new ArrayList();

            if (overwrite) {
                list.add("--replace");
            } else {
                list.add("--append");
            }

            if (split_documents) {
                list.add("--split");
            }
            //list.add("--debug");

            // format
            list.add("-t");
            list.add(format);

            // project_name
            list.add("-p");
            list.add(project_name);

            // volume_name
            list.add("-v");
            list.add(volume_name);

            // batch span
            if (batch_span > 0) {
                list.add("--batchspan");
                list.add(Integer.toString(batch_span));
            }
            // the name of the database
            if (! database.equals("codingdb")) {
                list.add("--codingdb");
                list.add(database);
                Log.print("Writing to database: " + database);
            }
            list.add("-x");
            list.add(lfp_filename);
            list.add("-i");
            list.add(image_pathname);
            // convert the args to a string array
            String[] args = (String[])list.toArray(new String[list.size()]);

            dbload.DiaLoad diaLoad = new dbload.DiaLoad(args);
            // export
            diaLoad.run();

            MessageWriter writer = task.getMessageWriter();
            String status = diaLoad.getStats();
            if (status.equals(T_OK)) {
                writer.startElement(T_OK);
            } else {
                writer.startElement(T_ERROR);
                writer.writeAttribute(A_DATA, status);
            }
            writer.endElement();
        } catch (Throwable t) {
            Log.quit(t);
        }
    }
}
