/*
 * BatchValidate.java
 *
 * Created on December 4, 2007, 3:32 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.server;

import com.fossa.servlet.command.ProjectEditor;
import com.fossa.servlet.command.ProjectMapper;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

/**
 *
 * @author bmurali
 */
public class BatchValidate implements MessageConstants{

    private UserTask task;

    private DBTask dbTask;

    private int volume_id;

    private int batch_id;

    private boolean unitize;

    private int activeGroup;

    private HashMap valueMap;
    
    private String priorFieldName;
    private StringBuffer buffer;
    
    /** Creates a new instance of BatchValidate */
    public BatchValidate(UserTask task,DBTask dbTask, int volume_id, int batch_id, boolean unitize, int activeGroup) {
        this.task = task;
        this.dbTask = dbTask;
        this.volume_id = volume_id;
        this.batch_id = batch_id;
        this.unitize = unitize;
        this.activeGroup = activeGroup;
    }
    
    public static int execute(UserTask task,DBTask dbTask, int volume_id, int batch_id, boolean unitize, int activeGroup) throws SQLException{
          return new BatchValidate(task,dbTask,volume_id,batch_id,unitize, activeGroup)
                   .validateBatch();
    }
    

    //private int validateBatch(int lowestInvalidPageId) {
    private int validateBatch() throws SQLException{

        Log.print("(BatchIO).validateBatch volume/batch " 
                  + volume_id + "/" + batch_id);
        ResultSet rs;
        Statement st = dbTask.getStatement();
        Connection con = null;

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

        ProjectMapper projectMap = new ProjectMapper(task,dbTask, project_id);
        ProjectEditor editor = new ProjectEditor(task,dbTask);

        // Go through all children, validating each
        // TBD: if this is slow, maybe get all values with 3 queries, then
        // make the individual maps from the result sets
        // Note.  Create a new statement, so no conflict with task's common one
        Statement st2 = dbTask.getConnection().createStatement();
        ResultSet rs2 = st2.executeQuery(
            "select C.child_id"
            +" from batch B"
            +"   inner join child C on C.volume_id=B.volume_id"
            +"     and C.lft between B.lft and B.rgt"
            +" where B.batch_id="+batch_id
            +" order by C.lft");
        
        while (rs2.next()) {
            int child_id = rs2.getInt(1);
            valueMap = new HashMap();
            mapValues("value", child_id);
            mapValues("longvalue", child_id);
            mapNameValues(child_id);
            //Log.print("(BatchIO).validateBatch " + child_id + "/" + valueMap.size());

            String error = editor.edit(projectMap, valueMap
                                       , /*force (not used on server)=>*/ false
                                       , unitize, activeGroup);
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
        Statement st     = dbTask.getStatement();
        Connection con = null;
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

        Statement st     = dbTask.getStatement();
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
