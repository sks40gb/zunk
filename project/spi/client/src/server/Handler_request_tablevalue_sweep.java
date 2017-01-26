/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_request_tablevalue_sweep.java,v 1.4.2.2 2006/03/21 16:42:41 nancy Exp $ */

package server;

import common.Log;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for request_tablevalue_sweep message.  Create a temporary table
 * to accumulate values and mark them with 'Yes' if they are used in the coded
 * data.  This Handler was created because the Handler_execute_query version
 * was too slow.
 * @see client.TaskRequestTablevalueSweep
 */
final public class Handler_request_tablevalue_sweep extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_request_tablevalue_sweep() {}

    /** 
     * This handler is read-only.
     * @return true
     */
    public boolean isReadOnly() {
        return true;
    }

    public void run (ServerTask task, Element action)
    throws SQLException, IOException {
         
        Statement st = task.getStatement();
        String tablename = action.getAttribute(A_NAME);
        int tablespecId = Integer.parseInt(action.getAttribute(A_ID));

        // Suppress writing the log for the temporary table
        // changed from heap to myisam wbe 2005-04-28.  bugzilla #224--getting full temp table  
        task.createTemporaryTable(
            "create temporary table TEMP"
            +" (value varchar(255) not null primary key,"
            +" count int not null,"
            +" in_table enum('No','Yes')) type=myisam");

        // Find all values in value or namevalue that are in the given tablespec
        // and insert them into TEMP

        st.executeUpdate(
            "insert into TEMP"
            +" select NV.value, count(*), ''"
            +" from tablespec TS"
            +" inner join volume V using (project_id)"
            +" inner join projectfields PF using (project_id)"
            +" inner join child C on C.volume_id=V.volume_id and C.lft between V.lft and V.rgt"
            +" inner join "+tablename+" NV on (NV.child_id = C.child_id and NV.field_name=PF.field_name)"
            +" where TS.tablespec_id="+tablespecId
            +" and PF.tablespec_id = TS.tablespec_id"
            +" group by NV.value");

        st.executeUpdate(
            "update TEMP"
            +" inner join tablevalue TV using (value)"
            +" inner join tablespec TS using (tablespec_id)"
            +" set in_table='Yes'"
            +" where TS.tablespec_id = "+tablespecId);
        
        // Note: project but not batch if this user was the original coder
        /*st.executeUpdate(
            "insert ignore into TEMP"
            +" select value, 0, 'Yes'"
            +" from tablespec TS"
            +" inner join tablevalue TV using (tablespec_id)"
            +" where TS.tablespec_id = "+tablespecId);*/
        st.executeUpdate(
            "insert ignore into TEMP" 
            + " select value, 0, 'Yes'" 
            + " from tablespec TS" 
            + " inner join tablevalue TV using (tablespec_id)" 
            + " where TS.tablespec_id = " + tablespecId 
            + " order by TV.tablevalue_id");
        
        // Finished updating temp table, start logging again
        task.finishedWritingTemporaryTable();

        // get tablevalues and counts
        /*ResultSet rs = st.executeQuery(
            "SELECT value, count, in_table FROM TEMP ORDER BY value");*/
        ResultSet rs = st.executeQuery(
            "SELECT value, count, in_table FROM TEMP");
        
        Handler_sql_query.writeXmlFromResult(task, rs);
        rs.close();
    }
}
