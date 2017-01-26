/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskExecuteQuery.java,v 1.8.8.2 2006/03/29 13:54:20 nancy Exp $ */
/* $Heading$ */

package client;

import java.io.IOException;
import java.sql.ResultSet;


/**
 * ClientTask to execute a named query on the server, optionally using
 * parameters and returning metadata.  All constructors return a ResultSet.
 * The named query is retrieved from the sql_text table.
 * @see Sql
 * @see server.Handler_sql_query
 */
public class TaskExecuteQuery extends ClientTask {

    private String sqlName;
    private String[] parameters;
    private boolean withMetaData;

    final private ServerConnection scon = Global.theServerConnection;
    
    /**
    * Construct a ClientTask to execute a named query on the server
     * with a given array of parameters, returning no metadata.
     * After execution, the returned ResultSet may be retrieved by getResult()
     * @param sqlName The sql_text.name of the SQL statement stored on the server.
     * @param parameters An array of parameters for a prepared statement.
     *        May be null, indicating no parameters.
     */
    public TaskExecuteQuery (String sqlName, String[] parameters) {
        this(sqlName, parameters, false);
    }

    /**
     * Construct a ClientTask to execute a named query on the server
     * with no parameters, returning no metadata.
     * After execution, the returned ResultSet may be retrieved by getResult()
     * @param sqlName The sql_text.name of the SQL statement stored on the server.
     */
    public TaskExecuteQuery (String sqlName) {
        this(sqlName, (String[]) null, false);
    }

    /**
     * Construct a ClientTask to execute a named query on the server
     * with a single parameter, returning no metadata.
     * After execution, the returned ResultSet may be retrieved by getResult()
     * @param sqlName The sql_text.name of the SQL statement stored on the server.
     * @param parameter The parameter for a prepared statement.
     */
    public TaskExecuteQuery (String sqlName, String parameter) {
        this(sqlName, new String[] {parameter}, false);
    }
    
    
    
    
    /**
     * Construct a ClientTask to execute a named query on the server
     * with two parameters, returning no metadata.
     * After execution, the returned ResultSet may be retrieved by getResult()
     * @param sqlName The sql_text.name of the SQL statement stored on the server.
     * @param parameter1 The first parameter for a prepared statement.
     * @param parameter2 The second parameter for a prepared statement.
     */
    public TaskExecuteQuery (String sqlName, String parameter1, String parameter2) {
        this(sqlName, new String[] {parameter1, parameter2}, false);
    }

    /**
     * Construct a ClientTask to execute a named query on the server
     * with three parameters, returning no metadata.
     * After execution, the returned ResultSet may be retrieved by getResult()
     * @param sqlName The sql_text.name of the SQL statement stored on the server.
     * @param parameter1 The first parameter for a prepared statement.
     * @param parameter2 The second parameter for a prepared statement.
     * @param parameter3 The third parameter for a prepared statement.
     */
    public TaskExecuteQuery (String sqlName, String parameter1, String parameter2, String parameter3) {
        this(sqlName, new String[] {parameter1, parameter2, parameter3}, false);
    }

    /**
     * Construct a ClientTask to execute a named query on the server
     * with no parameters.
     * After execution, the returned ResultSet may be retrieved by getResult()
     * @param sqlName The sql_text.name of the SQL statement stored on the server.
     * @param withMetaData If true, metadata (column names) are included and
     *        may be retrieved using ResultSet.getMetaData()
     */
    public TaskExecuteQuery (String sqlName, boolean withMetaData)
    {
        this(sqlName, (String[]) null, withMetaData);
    }

    /**
     * Construct a ClientTask to execute a named query on the server
     * with a single parameter.
     * After execution, the returned ResultSet may be retrieved by getResult()
     * @param sqlName The sql_text.name of the SQL statement stored on the server.
     * @param parameter The parameter for a prepared statement.
     * @param withMetaData If true, metadata (column names) are included and
     *        may be retrieved using ResultSet.getMetaData()
     */
    public TaskExecuteQuery (String sqlName, String parameter, boolean withMetaData) {
        this(sqlName, new String[] {parameter}, withMetaData);
    }

    /**
     * Construct a ClientTask to execute a named query on the server
     * with two parameters.
     * After execution, the returned ResultSet may be retrieved by getResult()
     * @param sqlName The sql_text.name of the SQL statement stored on the server.
     * @param parameter1 The first parameter for a prepared statement.
     * @param parameter2 The second parameter for a prepared statement.
     * @param withMetaData If true, metadata (column names) are included and
     *        may be retrieved using ResultSet.getMetaData()
     */
    public TaskExecuteQuery (String sqlName,
                                         String parameter1,
                                         String parameter2,
                                         boolean withMetaData) {
        this(sqlName, new String[] {parameter1, parameter2}, withMetaData);
    }

    /**
     * Construct a ClientTask to execute a named query on the server
     * with a given array of parameters.
     * After execution, the returned ResultSet may be retrieved by getResult()
     * @param sqlName The sql_text.name of the SQL statement stored on the server.
     * @param parameters An array of parameters for a prepared statement.
     *        May be null, indicating no parameters.
     * @param withMetaData If true, metadata (column names) are included and
     *        may be retrieved using ResultSet.getMetaData()
     */
    private TaskExecuteQuery (String sqlName,
                              String[] parameters,
                              boolean withMetaData)
    {
        this.parameters = parameters;
        this.sqlName = sqlName;
        this.withMetaData = withMetaData;
    }

    /**
     * Execute the query and set the result.
     */
    public void run() throws IOException {
        ResultSet queryResult = null;            
        queryResult = Sql.executeQuery(scon, this, sqlName, parameters, withMetaData);        
        setResult(queryResult);
    }
}
