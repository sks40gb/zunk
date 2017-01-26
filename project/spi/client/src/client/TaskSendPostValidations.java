package client;

import valueobjects.ProjectsData;
import valueobjects.FieldsData;
import valueobjects.FunctionsData;
import com.lexpar.util.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Collection;
import org.w3c.dom.Element;

/**
 * ClientTask to send the records to server to perform the Post Validation operation
 * Mainly record contains only Ids of project, volume, fields and functions.
 * The main operation (PVR) will be perform in server side.
 * @author sunil
 */
public class TaskSendPostValidations extends ClientTask {
    //Having the IDs for the records.

    private ProjectsData data;
    final private ServerConnection scon = Global.theServerConnection;
    // contains Fields Ids and Functions Ids.
    StringBuilder sendIDsString;

    public TaskSendPostValidations(ProjectsData data) {
        this.data = data;
        sendIDsString = new StringBuilder();
        createSendingData();
    }

    /**
     * Create the Ids format of fieldId and corresponding functionsId
     * IDs String format ---> fieldId-functionsid1,functionsid2 # fieldId-functionsid1,functionsid2....
     * Example ---> 12-33,32#76-909,980....
     * '-' is the separator between Field Id and Functions Ids
     * ',' is the separator among the Functions Ids.
     * '#' is the separator between two Fields.
     */
    private void createSendingData() {
        // Is the first field appending to the <code>sendIDsString</code> ?.
        boolean isFirstField = true;
        sendIDsString = new StringBuilder();
        if (data == null) {
            return;
        }
        Collection<FieldsData> dataFieldsList = data.getFieldsMap().values();
        for (FieldsData field : dataFieldsList) {
            Collection<FunctionsData> functionDataList = field.getFunctionsMap().values();
            if (functionDataList.size() < 1) {
                continue;
            }
            int field_id = field.getId();
            // Is the First Function corresponding to the Field appending to the 
            // Field id.
            boolean isFirstFunction = true;
            //Iterate through the list of the functions for the selected Fields
            for (FunctionsData fun : functionDataList) {
                int fun_id = fun.getId();
                // If the function is selected.
                if (fun.isSelected()) {
                    // If the Function is First Function going to be append just
                    // after the Field Id.
                    if (isFirstFunction) {
                        // Is the First Field to be append in the sendIDsString.
                        // This statement will be true once.
                        if (isFirstField) {
                            sendIDsString.append(field_id);
                            sendIDsString.append("-");
                            sendIDsString.append(fun_id);
                            isFirstField = false;
                            isFirstFunction = false;
                        } else {
                            sendIDsString.append("#");
                            sendIDsString.append(field_id);
                            sendIDsString.append("-");
                            sendIDsString.append(fun_id);
                            isFirstFunction = false;
                        }
                    } else {
                        sendIDsString.append(",");
                        sendIDsString.append(fun_id);
                    }
                }
            }
        }
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer;
        writer = scon.startMessage(T_SEND_POST_VALIDATION);
        writer.writeAttribute(A_PROJECT_ID, data.getProjectId());
        writer.writeAttribute(A_VOLUME_ID, data.getVolumeId());
        writer.writeAttribute(A_POST_VALIDATION_STR, sendIDsString.toString());
        writer.endElement();
        writer.close();
        Element reply = scon.receiveMessage();
        final ResultSet rs = Sql.resultFromXML(reply);
        if (T_RESULT_SET.equals(reply.getNodeName())) {
            synchronized (this) {
                // force cache flush for rs
                }
        } else if (T_FAIL.equals(reply.getNodeName())) {
            Log.quit("Sql.TaskRequestFieldvalue: SQL error: " + reply);
        } else {
            Log.quit("TaskRequestFieldvalue: unexpected message type: " + reply.getNodeName());
        }
        setResult(rs);

    }
}
