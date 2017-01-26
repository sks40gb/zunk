/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskExecuteUpdate.java,v 1.6.8.3 2006/03/29 13:54:20 nancy Exp $ */
/* $Heading$ */

package client;

import java.io.IOException;

/**
 * ClientTask to execute a named sql update on the server, optionally using
 * parameters and returning metadata.  All constructors return a count of the
 * number of rows affected or -1 for a sql error.
 * The named query is retrieved from the sql_text table.
 * @see Sql
 * @see server.Handler_sql_update
 */
public class TaskExecuteUpdate extends ClientTask {

    private String sqlName;
    private String[] parameters;

    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Construct a ClientTask to execute a named update on the server
     * with no parameters.
     * After execution, the returned integer value may be retrieved by
     *           ((Integer) getResult()).intValue()
     * If an SQL error occurred, the returned integer is -1.
     * @param sqlName The sql_text.name of the SQL statement stored on the server.
     */
    public TaskExecuteUpdate (String sqlName) {
        this(sqlName, (String[]) null);
    }

    /**
     * Construct a ClientTask to execute a named update on the server
     * with a single parameter.
     * After execution, the returned integer value may be retrieved by
     *           ((Integer) getResult()).intValue()
     * If an SQL error occurred, the returned integer is -1.
     * @param sqlName The sql_text.name of the SQL statement stored on the server.
     * @param parameter The parameter for a prepared statement.
     */
    public TaskExecuteUpdate (String sqlName, String parameter) {
        this(sqlName, new String[] {parameter});
    }

    /**
     * Construct a ClientTask to execute a named update on the server
     * with two parameters.
     * After execution, the returned integer value may be retrieved by
     *           ((Integer) getResult()).intValue()
     * If an SQL error occurred, the returned integer is -1.
     * @param sqlName The sql_text.name of the SQL statement stored on the server.
     * @param parameter1 The first parameter for a prepared statement.
     * @param parameter2 The second parameter for a prepared statement.
     */
    public TaskExecuteUpdate (String sqlName, String parameter1, String parameter2) {
        this(sqlName, new String[] {parameter1, parameter2});
    }

    /**
     * Construct a ClientTask to execute a named update on the server
     * with a given array of parameters.
     * After execution, the returned integer value may be retrieved by
     *           ((Integer) getResult()).intValue()
     * If an SQL error occurred, the returned integer is -1.
     * @param sqlName The sql_text.name of the SQL statement stored on the server.
     * @param parameters An array of parameters for a prepared statement.
     *        May be null, indicating no parameters.
     */
    public TaskExecuteUpdate (String sqlName,
                              String[] parameters)
    {
        this.parameters = parameters;
        this.sqlName = sqlName;
        try{
            synchronized (sqlName){
            sqlName.notify();
            }
        }catch(Exception e){
            
        }
    }

    /**
     * Execute the update and set the result.
     */
    public void run() throws IOException {
        int updateResult
                = Sql.executeUpdate(scon, this, sqlName, parameters);
        setResult(new Integer(updateResult));
    }
}
