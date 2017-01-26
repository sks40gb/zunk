/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Element;

/** 
 * This class handles the requests for 'sweep databases'
 * @author ashish
 */
class Command_request_tablevalue_sweep implements Command{
   
    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {      
        Statement st = null;
        ResultSet rs = null;        
        try{
            st = dbTask.getStatement();
            String tablename = action.getAttribute(A_NAME);
            int tablespecId = Integer.parseInt(action.getAttribute(A_ID));
            // Suppress writing the log for the temporary table
            // changed from heap to myisam wbe 2005-04-28.  bugzilla #224--getting full temp table       
           st.executeUpdate(SQLQueries.CREATE_TBL_SWEEP_DB);
           st.executeUpdate(
                "insert into sweep_database"
                +" select NV.value, count(*), ''"
                +" from tablespec TS"
                +" inner join volume V on V.project_id = TS.project_id"
                +" inner join projectfields PF on PF.project_id = V.project_id"
                +" inner join child C on C.volume_id=V.volume_id and C.lft between V.lft and V.rgt"
                +" inner join "+tablename+" NV on (NV.child_id = C.child_id and NV.field_name=PF.field_name)"
                +" where TS.tablespec_id="+tablespecId
                +" and PF.tablespec_id = TS.tablespec_id"
                +" group by NV.value");
            st.executeUpdate(
                "update sweep_database"
                +" set in_table='Yes'"
                +" from sweep_database"
                +" inner join tablevalue TV on TV.value = sweep_database.value"
                +" inner join tablespec TS on TS.tablespec_id = TV.tablespec_id"
                +" where TS.tablespec_id = "+tablespecId);
 
            // Note: project but not batch if this user was the original coder            
            st.executeUpdate("INSERT into sweep_database  SELECT value, 0, 'Yes' FROM tablespec TS inner join tablevalue TV on TV.tablespec_id = TS.tablespec_id  WHERE TS.tablespec_id = "+ tablespecId +" ORDER BY TV.tablevalue_id");
            
            // Finished updating temp table, start logging again
            task.finishedWritingTemporaryTable(dbTask);

            // get tablevalues and counts         
            rs = st.executeQuery("SELECT value, count, in_table FROM sweep_database ORDER BY VALUE");
            
            CommandFactory factory = CommandFactory.getInstance();
            Command_sql_query sqlCommand = (Command_sql_query)factory.getCommand(T_SQL_QUERY);
            sqlCommand.writeXmlFromResult(task, rs, writer, false);
            
            st.executeUpdate("drop table sweep_database");        
        } catch (IOException sql) {
            CommonLogger.printExceptions(this, "IOException while table value sweep." , sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while table value sweep." , exc);
            return null;
        } 
        return null;
    }

    public boolean isReadOnly() {
        return false;
    }

}
