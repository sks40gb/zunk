/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import com.lexpar.util.Log;
import common.FieldValidationData;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;
import java.sql.ResultSet;

/**
 * ClientTask to display the validation data or modify the validation data.
 * @author sunil
 */
public class TaskEditAdvanceValidations extends ClientTask {

   
    /** display the record for the validation or update the data for the validation. */
    private boolean EditOrDisplay = false;
    /** The server connection */
    private ServerConnection scon = Global.theServerConnection;
    private FieldValidationData fieldValidationData;

    /*
     * @param projectFieldId - field id 
     * @param validation_mapping_details_id -- this id refer to record of parameter,error message, status.
     * @param validation_functions_master_id -- it is the id for the function_master and locates to       
     * function name,function desciption, and function body.
     * @param editOrDisplay - if it is true, the data will be fetched  from DB and shown to the edit window,
     *                        if if is false, it will updated the existing function.
     * @param arrFunctionRecord -  contains new records for the function.
     */
    public TaskEditAdvanceValidations(FieldValidationData fieldValidationData) {
       
        this.EditOrDisplay = new Boolean(fieldValidationData.editOrDisplay);
        this.fieldValidationData = fieldValidationData;
    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {
        try {
            //true for fetching and display the funcitons records.
            if (EditOrDisplay) {
                
                MessageWriter writer = scon.startMessage(T_EDIT_ADVANCE_VALIDATIONS);
                writer.encode(FieldValidationData.class, fieldValidationData);
                writer.endElement();
                writer.close();
                Element reply = scon.receiveMessage();

                final ResultSet rs = Sql.resultFromXML(reply);

                if (T_RESULT_SET.equals(reply.getNodeName())) {
                    synchronized (this) {
                        // force cache flush for rs
                    }
                } else if (T_FAIL.equals(reply.getNodeName())) {
                    Log.quit("TaskEditAdvanceValidations: SQL error: " + reply);
                } else {
                    Log.quit("TaskEditAdvanceValidations: unexpected message type: " + reply.getNodeName());
                }
                setResult(rs);

            } else {
                MessageWriter writer = scon.startMessage(T_EDIT_ADVANCE_VALIDATIONS);
                writer.encode(FieldValidationData.class, fieldValidationData);
                writer.endElement();
                writer.close();
                Element reply = scon.receiveMessage();
            }

        } catch (Exception exc) {
        }

    }
}

