/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.lexpar.util.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask for deleting the validation of a Field.
 * @author sunil
 */
public class TaskDeleteAdvanceValidations extends ClientTask {

    /** Fields Id */
    private int projectFieldId;
    /** Mapping Id for the validation */
    private int validation_mapping_details_id;
    /** Function Master Id */
    private int validation_functions_master_id = 0;
    /** The server connection */
    private ServerConnection scon = Global.theServerConnection;

    /*
     * Instantiate the object with the following parmaters
     * @param projectFieldId - field id of the project.
     * @param validation_mapping_details_id - id of the table validation_mapping_detail
     * @param validation_functions_master_id - id of the table validation_functions_master
     */
    public TaskDeleteAdvanceValidations(int projectFieldId, int validation_mapping_details_id, int validation_functions_master_id) {
        this.projectFieldId = projectFieldId;
        this.validation_mapping_details_id = validation_mapping_details_id;
        this.validation_functions_master_id = validation_functions_master_id;
    }

    @Override
    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        try {
            MessageWriter writer = scon.startMessage(T_DELETE_ADVANCE_VALIDATIONS);
            writer.writeAttribute(A_FIELD_ID, Integer.toString(projectFieldId));
            writer.writeAttribute(A_VALIDATION_MAPPING_DETAILS_ID, Integer.toString(validation_mapping_details_id));
            writer.writeAttribute(A_VALIDATION_FUNCTIONS_MASTER_ID, Integer.toString(validation_functions_master_id));
            writer.endElement();
            writer.close();
            //reply is not required.        
            Element reply = scon.receiveMessage();
        } catch (Exception exc) {
            Log.print(exc.toString());
        }
    }
}

