/* $Header: /home/common/cvsarea/ibase/dia/src/server/BatchValidate.java,v 1.9.6.2 2006/03/22 20:27:15 nancy Exp $ */
package server;

//import beans.IbaseConstants;
import common.Log;
import common.edit.ProjectEditor;
import common.edit.ProjectMapper;
import common.msg.MessageConstants;
//import common.msg.MessageWriter;

//import java.io.IOException;

//import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Validate the fields in a batch by creating a <code>common.edit.ProjectMapper</code)
 * and Map of field values for each child in the given batch, then providing them to the
 * <code>common.edit.ProjectEditor</code>.  The editor will return null when no errors.
 * If an error is encountered, return the child_id to the caller.
 * @see common.edit.ProjectMapper
 * @see common.edit.ProjectEditor
 */
public class BatchValidate implements MessageConstants{
   
    private ServerTask task;
    private int volume_id;
    private int batch_id;
    private int activeGroup;
    private boolean unitize;

    private Map valueMap;

    private String priorFieldName;
    private StringBuffer buffer;

    // Create an instance of BatchValidate, remembering task, volume and batch id
    private BatchValidate(ServerTask task, int volume_id, int batch_id, boolean unitize
                          , int activeGroup) {
        this.task = task;
        this.volume_id = volume_id;
        this.batch_id = batch_id;
        this.unitize = unitize;
        this.activeGroup = activeGroup;
    }


    /**
     * Perform crossFieldEdits on each child for the given batchId.
     * @param task current ServerTask to handle the connection from
     * the calling client to the coding server
     * @param volume_id the volume.volume_id containing the batch to be edited
     * @param batch_id the batch.batch_id of the batch to be edited
     * @param unitize true if this is a unitize batch, which uses different
     * editing rules
     * @param activeGroup 0 if no groups; the group currently being coded
     */
    public static int run(
        ServerTask task, int volume_id, int batch_id, boolean unitize, int activeGroup)
    throws SQLException {
        return new BatchValidate(task,volume_id,batch_id,unitize, activeGroup)
                   .validateBatch();
    }

    //private int validateBatch(int lowestInvalidPageId) {
    private int validateBatch() throws SQLException{

        Log.print("(BatchIO).validateBatch volume/batch " 
                  + volume_id + "/" + batch_id);
        ResultSet rs;
        Statement st = task.getStatement();

        // obtain the projectId
        int project_id;
        rs = st.executeQuery(
            "select project_id"
            +" from volume"
            +"  where volume_id ="+volume_id
            +"  and sequence > 0");
        if (! rs.next()) {
            throw new ServerFailException("Volume not found: "+volume_id);
        }
        project_id = rs.getInt(1);
        rs.close();

        ProjectMapper projectMap = new ProjectMapper(task, project_id);
        ProjectEditor editor = new ProjectEditor(task);

        // Go through all children, validating each
        // TBD: if this is slow, maybe get all values with 3 queries, then
        // make the individual maps from the result sets
        // Note.  Create a new statement, so no conflict with task's common one
        Statement st2 = task.getConnection().createStatement();
        ResultSet rs2 = st2.executeQuery(
            "select C.child_id,B.treatment_level"
            +" from batch B"
            +"   inner join child C on C.volume_id=B.volume_id"
            +"     and C.lft between B.lft and B.rgt"
            +" where B.batch_id="+batch_id
            +" order by C.lft");

        while (rs2.next()) {
            int child_id = rs2.getInt(1);
            String treatmentLevel = rs2.getString(2);
            valueMap = new HashMap();
            mapValues("value", child_id);
            mapValues("longvalue", child_id);
            mapNameValues(child_id);
            //Log.print("(BatchIO).validateBatch " + child_id + "/" + valueMap.size());

            String error = editor.edit(projectMap, valueMap
                                       , /*force (not used on server)=>*/ false
                                       , unitize, activeGroup,treatmentLevel);
            if (error != null) {
                // edit returns null if no errors
                // Return the child_id of the child in error.
                // Note.  error is a message, which will be recomputed by the client
                //Log.print("(BatchIO).validateBatch return error child="+child_id);
                st2.close();
                return child_id;
            }
        }
        st2.close();
        //Log.print("(BatchIO).validateBatch return 0");
        return 0;
    }

    private void mapValues(String tableName, int child_id) throws SQLException {
        Statement st     = task.getStatement();
        ResultSet valueset = st.executeQuery(
            "select field_name, value"
            +" from "+tableName
            +" where child_id="+child_id
            +" order by field_name, sequence");

        priorFieldName = null;
        buffer = new StringBuffer();
        String fieldName = "";
        while (valueset.next()) {
            fieldName = valueset.getString(1);
            //Log.print("(BatchIO).mapValues " + fieldName);
            String value = valueset.getString(2);
            if (fieldName.equals(priorFieldName)) {
                buffer.append("; ");
            } else{
                if (priorFieldName != null) {
                    valueMap.put(priorFieldName, (Object)buffer.toString());
                }
                priorFieldName = fieldName;
                buffer = new StringBuffer();
            }
            buffer.append(value);
        }
        if (buffer != null) {
            valueMap.put(fieldName, (Object)buffer.toString());
        }
        valueset.close();
    }

    private void mapNameValues(int child_id) throws SQLException {

        Statement st     = task.getStatement();
        ResultSet nameset = st.executeQuery(
            "select field_name, last_name, first_name, middle_name, organization"
            +" from namevalue"
            +" where  child_id="+child_id
            +" order by field_name, sequence");

        priorFieldName = null;
        buffer = new StringBuffer();
        String fieldName = "";
        while (nameset.next()) {
            fieldName = nameset.getString(1);
            //Log.print("(BatchIO).mapNameValues " + fieldName);
            String lastName = nameset.getString(2);
            String firstName = nameset.getString(3);
            String middleName = nameset.getString(4);
            String organization = nameset.getString(5);
            if (fieldName.equals(priorFieldName)) {
                buffer.append("; ");
            } else {
                if (priorFieldName != null) {
                    valueMap.put(priorFieldName, (Object)buffer.toString());
                }
                priorFieldName = fieldName;
                buffer = new StringBuffer();
            }
            if (lastName.length() > 0) {
                buffer.append(lastName);
            }
            if (firstName.length() > 0) {
                buffer.append(", ");
                buffer.append(firstName);
            }
            if (middleName.length() > 0) {
                buffer.append(' ');
                buffer.append(middleName);
            }
            if (organization.length() > 0) {
                buffer.append(" / ");
                buffer.append(organization);
            }
        }
        nameset.close();
        if (buffer != null) {
            valueMap.put(fieldName, (Object)buffer.toString());
        }
    }
}
