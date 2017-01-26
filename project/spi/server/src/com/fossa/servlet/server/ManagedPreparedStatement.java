/* $Header: /home/common/cvsarea/ibase/dia/src/server/ManagedPreparedStatement.java,v 1.11.6.1 2005/11/29 11:25:22 nancy Exp $ */
package com.fossa.servlet.server;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.DynamicArrays;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;


import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 * A wrapper for a PreparedStatement that handles recording changes for
 * ManagedTable synchronization.   The executeUpdate method executes
 * statements before and after the execution of the given statement
 * in order to properly maintain the changes table for managed tables.
 * Note that statements affecting the changes table are not written
 * to the binary log, and so are not replicated.
 * <p>
 * --------------------
 * <p>
 * An object that represents a precompiled SQL statement.
 * <P>A SQL statement is precompiled and stored in a
 * <code>PreparedStatement</code> object. This object can then be used to
 * efficiently execute this statement multiple times. 
 *
 * <P><B>Note:</B> The setter methods (<code>setShort</code>, <code>setString</code>,
 * and so on) for setting IN parameter values
 * must specify types that are compatible with the defined SQL type of
 * the input parameter. For instance, if the IN parameter has SQL type
 * <code>INTEGER</code>, then the method <code>setInt</code> should be used.
 *
 * <p>If arbitrary parameter type conversions are required, the method
 * <code>setObject</code> should be used with a target SQL type.
 * <P>
 * In the following example of setting a parameter, <code>con</code> represents
 * an active connection:  
 * <PRE>
 *   PreparedStatement pstmt = con.prepareStatement("UPDATE EMPLOYEES
 *                                     SET SALARY = ? WHERE ID = ?");
 *   pstmt.setBigDecimal(1, 153833.00)
 *   pstmt.setInt(2, 110592)
 * </PRE>
 *
 * @see Connection#prepareStatement
 * @see ResultSet 
 */

public class ManagedPreparedStatement extends PreparedStatementAdapter {

    // values for type
    final private int INSERT = 0;
    final private int UPDATE = 1;
    final private int DELETE = 2;
    final private String[] TYPE_NAMES = {"INSERT", "UPDATE", "DELETE"};

    // regular expressions for parsing statements
    final private static String INSERT_TEXT
        = "insert\\s+(?:into\\s+)?"
        +"(\\w+)"                                   // 1. table name
        //+"\\s+(?:set|select|values|\\()\\W.*";      // rest of statement
        +"\\W.*";                                   // rest of statement
    final private static String UPDATE_TEXT
        = "update\\s+?"
        +"(\\w+)\\s*+"                              // 1. table name
        +"(\\w+)?\\s*+"                             // 2. alias (optional)
        +"((?:,|inner|left|right|straight_join"
        +     "|cross|join).*\\W)?"                 // 3. rest of tbls (optional)
        +"(set\\W.*\\W)"                            // 4. set part
        +"where(.*)";                               // 5. where clause
    final private static String DELETE_TEXT
        = "delete\\s(.*?)(?:from\\s+)?"             // 1. delete fieldlist
        +"(\\w+)"                                // 2. table name
        +"(\\s.*)"                                     // 3. joined tables
        +"where(\\s.*)";                            // 4. where clause
    final private static Pattern INSERT_PATTERN = Pattern.compile(
            INSERT_TEXT, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    final private static Pattern UPDATE_PATTERN = Pattern.compile(
            UPDATE_TEXT, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    final private static Pattern DELETE_PATTERN = Pattern.compile(
            DELETE_TEXT, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    final private static Pattern[] PATTERNS
            = {INSERT_PATTERN, UPDATE_PATTERN, DELETE_PATTERN};

    private UserTask  task;
    private DBTask dbTask;
    private String sql;

    private int         type;   // INSERT/UPDATE/DELETE
    private Connection  con;
    private PreparedStatement   stmtActual; // prepared statement for the given SQL
    private PreparedStatement[] stmtBefore = null; // to exec before given SQL
    private PreparedStatement[] stmtAfter = null; // to exec after given SQL

    // The primary table for this ManagedPreparedStatement
    private String tableName;
    private ManagedTable table;

    // The number of parameters which appear before the WHERE clause in the
    // given sql.  Used so that replacements can be done in the generated
    // before and after statements.
    private int paramOffset;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.server");

    /**
     * Create a new ManagedPreparedStatement.  This constructor should not be 
     * called directly; use task.prepareStatement(sql) instead.  The principal 
     * table for the statement
     */
    public ManagedPreparedStatement(UserTask task, DBTask dbTask, String sql) throws SQLException {
        
        this.task = task;
        this.dbTask = dbTask;
        this.sql = sql;
        try { 
           con = dbTask.getConnection();
         //con = new FossaDataSource().getConnection();
        } catch (Exception ex) {
             logger.error("Exception while getting db connection." + ex);
             StringWriter sw = new StringWriter();
             ex.printStackTrace(new PrintWriter(sw));
             logger.error(sw.toString());
        }
        switch (sql.charAt(0)) {
        case 'i':
        case 'I':
            type = INSERT;
            break;
        case 'u':
        case 'U':
            type = UPDATE;
            break;
        case 'd':
        case 'D':
            type = DELETE;
            break;
        default: 
            Log.quit("Invalid SQL statement type: "+sql);
        }

        Matcher mat = PATTERNS[type].matcher(sql);
        if (! mat.matches()) {
            Log.print("INSERT: "+INSERT_TEXT);
            Log.print("UPDATE: "+UPDATE_TEXT);
            Log.print("DELETE: "+DELETE_TEXT);
            try {
                for (int i = 0; ; i++) {
                    int first = mat.start(i);
                    if (first < 0) {
                        Log.print(i+": UNMATCHED");
                    } else {
                        int last = mat.end(i);
                        Log.print(i+": "+first+"..."+last+" '"
                                  +sql.substring(first,last)+"' "
                                  +countQuestionMarks(sql.substring(first,last)));
                    }
                }
            } catch (Exception e) {
                // ignore exception when end of array reached
                logger.error("Exception in ManagedPreparedStatement()." + e);
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                logger.error(sw.toString());
            }
            Log.quit("Invalid SQL for pattern: "+sql);
        }
        if (false) {
            // DEBUG PRINTOUT
            Log.print("INSERT: "+INSERT_TEXT);
            Log.print("UPDATE: "+UPDATE_TEXT);
            Log.print("DELETE: "+DELETE_TEXT);
            try {
                for (int i = 0; ; i++) {
                    int first = mat.start(i);
                    if (first < 0) {
                        Log.print(i+": UNMATCHED");
                    } else {
                        int last = mat.end(i);
                        Log.print(i+": "+first+"..."+last+" '"
                                  +sql.substring(first,last)+"' "
                                  +countQuestionMarks(sql.substring(first,last)));
                    }
                }
            } catch (Exception e) {
                // ignore exception when end of array reached
               logger.error("Exception in ManagedPreparedStatement()." + e);
               StringWriter sw = new StringWriter();
               e.printStackTrace(new PrintWriter(sw));
               logger.error(sw.toString());
            }
        }
        if (type == DELETE) {
            tableName = sql.substring(mat.start(2),mat.end(2));
        } else {
            tableName = sql.substring(mat.start(1),mat.end(1));
        }
        table = ManagedTable.lookup(tableName);
        if (table == null) {
            Log.quit("Table "+tableName+" not managed: "+sql);
        } else if("TEMP".equalsIgnoreCase(tableName)) {
            tableName = "#" + tableName; //#TEMP is the table name
        }

        //Log.print("ManagedPreparedStatement for: "+tableName+"\n"+sql);
        stmtActual = con.prepareStatement(sql);


        // generate type-dependent before and after statements
        switch(type) {
        case INSERT: prepareInsert(); break;
        case UPDATE: prepareUpdate(mat); break;
        case DELETE: prepareDelete(mat); break;
        }
        //Log.print("PREPARED "+TYPE_NAMES[type]+" for "+tableName
        //          +" before#"+(stmtBefore == null ? 0 : stmtBefore.length)
        //          +" after#"+(stmtAfter == null ? 0 : stmtAfter.length));
       
    }

    private void prepareInsert() throws SQLException { 
        paramOffset = Integer.MAX_VALUE;

        sql = "INSERT INTO changes (table_nbr, id, age) SELECT "+table.getTableNumber()+","+tableName+"_id,? FROM  "+tableName+" " +
                " WHERE "+tableName+"_id >= (SELECT TOP 1 "+tableName+"_id FROM "+tableName+" ORDER BY "+tableName+"_id DESC)";
        
        stmtAfter = addStmt(stmtAfter, sql);
        System.out.println("The insert statement : " + sql);
        for (int i = 0; i < table.getTargetCount(); i++) {
            ManagedTable targetTable = table.getTargetTable(i);
            String targetName = targetTable.getTableName();
            String idColumn = table.getTargetIdColumn(i);

            sql = "INSERT INTO changes (table_nbr, id, age) SELECT "+targetTable.getTableNumber()+", X."+targetName+"_id,? " +
                    " FROM changes C, "+targetName+" X, "+tableName+" T " +
                    " WHERE C.table_nbr="+table.getTableNumber()+" and C.id=T."+tableName+"_id " +
                    " and X."+idColumn+"=T."+idColumn+" and T."+tableName+"_id > " +
                    " (SELECT TOP 1 "+tableName+"_id FROM "+tableName+" ORDER BY "+tableName+"_id DESC)";            	
            stmtAfter = addStmt(stmtAfter, sql);
        }        
    }

    private void prepareUpdate(Matcher mat) throws SQLException { 
        // 1. table name              
        // 2. alias (optional)        
        // 3. rest of tbls (optional) 
        // 4. set part                
        // 5. where clause           
        String alias = tableName;
        String spaceAndAlias = "";
        if (mat.start(2) >= 0) {
            alias = sql.substring(mat.start(2), mat.end(2));
            spaceAndAlias = " "+alias;
        }
        if (mat.start(3) >= 0) {
            spaceAndAlias += " "+sql.substring(mat.start(3), mat.end(3));
        }
        paramOffset = countQuestionMarks(sql.substring(0,mat.start(5)));
        String whereClause = sql.substring(mat.start(5));

        String sql = "Insert  into changes (table_nbr, id, age)"
        	+" select "+table.getTableNumber()
        	+" , "+alias+"."+tableName+"_id"
        	+" ,?"
        	+" from "+tableName+""+spaceAndAlias 
        	+" where"+whereClause;

        stmtBefore = addStmt(stmtBefore,sql);
        
        int tableNumber = 0;
        for (int i = 0; i < table.getTargetCount(); i++) {
            ManagedTable targetTable = table.getTargetTable(i);
            String targetName = targetTable.getTableName();
            String idColumn = table.getTargetIdColumn(i);
            boolean exec = true;
            if(i == 0){
               tableNumber = targetTable.getTableNumber();
            }
            if(i > 0 && tableNumber == targetTable.getTableNumber()){
               exec = false;
            }

            sql = "Insert into changes (table_nbr, id, age)"
            	  +" select "+targetTable.getTableNumber()
            	  +" , X."+targetName+"_id"
            	  +" , C.age"
            	  +" from changes C"
            	  +" , "+targetName+" X"
            	  +" , "+tableName+ " T"  
            	  +" where C.table_nbr="+table.getTableNumber()
            	  +" and C.id=T."+tableName+"_id"
            	  +" and X."+idColumn+"=T."+idColumn
            	  +" and C.age=?";
            if(exec){
               stmtBefore = addStmt(stmtBefore, sql);
               stmtAfter = addStmt(stmtAfter, sql);
            }
        }
        
    }

    private void prepareDelete(Matcher mat) throws SQLException { 
        paramOffset = countQuestionMarks(sql.substring(0,mat.start(3)));
        //String fieldList = sql.substring(mat.start(1), mat.end(1));
        String joinedTables = sql.substring(mat.start(3), mat.end(3));
        String whereClause = sql.substring(mat.start(4));

         if(tableName.equals("projectfields") && joinedTables.trim().equalsIgnoreCase("FROM projectfields PF INNER JOIN project on project.project_id = PF.project_id")){
         
             sql = "insert  into changes (table_nbr, id, age) select "+table.getTableNumber()+", "+tableName+"_id,? "+joinedTables+" where"+whereClause;
         }
         else{
         
           sql = "insert  into changes (table_nbr, id, age) select "+table.getTableNumber()+", "+tableName+"_id,? from "+tableName+""+joinedTables+" where"+whereClause;
         }
  
        stmtBefore = addStmt(stmtBefore, sql);
        for (int i = 0; i < table.getTargetCount(); i++) {
            ManagedTable targetTable = table.getTargetTable(i);
            String targetName = targetTable.getTableName();
            String idColumn = table.getTargetIdColumn(i);

            sql = "insert  into changes (table_nbr, id, age) select distinct "+targetTable.getTableNumber()+", X."+targetName+"_id, C.age from changes C, "+targetName+" X, "+tableName+" T where C.table_nbr="+table.getTableNumber()+" and C.id=T."+tableName+"_id and X."+idColumn+"=T."+idColumn+" and C.age=?";
            stmtBefore = addStmt(stmtBefore, sql);
        }
   
    }

    // Create a PreparedStatement and add it to the specified array. (convenience method)
    private PreparedStatement[] addStmt(PreparedStatement[] arr, String sql)
    throws SQLException {
        //Log.print("Preparing:\n"+sql);
        return (PreparedStatement[]) DynamicArrays.append(arr, con.prepareStatement(sql));
    }

    // count the number of question marks in a string
    private int countQuestionMarks(String text) {
        int count = 0;
        int pos = -1;
        while ((pos = text.indexOf('?', pos+1)) >= 0) {
            count++;
        }
        return count;
    }

    // determine if given index is in the where clause
    private boolean isInWhereClause(int index) {
        return (index > paramOffset);
    }


    
    /**
     * Executes the SQL statement in this <code>PreparedStatement</code> object,
     * which must be an SQL <code>INSERT</code>, <code>UPDATE</code> or
     * <code>DELETE</code> statement.  Statements are executed before and/or
     * after the given statement to maintain the changes table.
     *
     * @return the row count for <code>INSERT</code>, <code>UPDATE</code>,
     *         or <code>DELETE</code> statements
     * @exception SQLException if a database access error occurs or the SQL
     *            statement returns a <code>ResultSet</code> object
     */
    public int executeUpdate() throws SQLException {
        int updateAge = task.getUpdateAge();
   
        updateAge = 0;
   
        //Log.print("$$managed executeUpdate "+TYPE_NAMES[type]+" "+tableName+" age="+updateAge);
        if (stmtBefore != null) {
            task.setBinLogEnabled(false, dbTask);
            updateAge = task.getUpdateAgeForUpdate(dbTask);
   
            if (stmtBefore != null) {
                for (int i = 0; i < stmtBefore.length; i++) {
                    //Log.print("Before:\n"+stmtBefore[i]);
                    stmtBefore[i].setInt(1, updateAge);
                    stmtBefore[i].executeUpdate();
                }
            }
            task.setBinLogEnabled(true, dbTask);
        }
        int count = stmtActual.executeUpdate();
        //Log.print("count="+count+" before#="+(stmtBefore == null ? 0 : stmtBefore.length)+"\n"+stmtActual);
        if (count > 0 && stmtAfter != null) {
            updateAge = task.getUpdateAgeForUpdate(dbTask);
            //if (type == UPDATE) {
            //    task.addPropagateTable(table);
            //}
            //if (stmtAfter != null || stmtBefore != null) {
            //    for (int i = 0; i < table.getTargetCount(); i++) {
            //        if (table.getTargetIdColumn(i) != null) {
            //            task.addPropagateTable(table.getTargetTable(i));
            //        }
            //    }
            //}
            task.setBinLogEnabled(true, dbTask);
            for (int i = 0; i < stmtAfter.length; i++) {
                //Log.print("After:\n"+stmtAfter[i]);
                stmtAfter[i].setInt(1, updateAge);
                //count = stmtAfter[i].executeUpdate();
                stmtAfter[i].executeUpdate();
            }
            task.setBinLogEnabled(true, dbTask);
        }
        return count;
    }

    /**
     * Releases this <code>Statement</code> object's database 
     * and JDBC resources immediately instead of waiting for
     * this to happen when it is automatically closed.
     * It is generally good practice to release resources as soon as
     * you are finished with them to avoid tying up database
     * resources.
     * <P>
     * Calling the method <code>close</code> on a <code>Statement</code>
     * object that is already closed has no effect.
     * <P>
     * <B>Note:</B> A <code>Statement</code> object is automatically closed 
     * when it is garbage collected. When a <code>Statement</code> object is 
     * closed, its current <code>ResultSet</code> object, if one exists, is 
     * also closed.  
     *
     * @exception SQLException if a database access error occurs
     */
    public void close() throws SQLException {
        stmtActual.close();
        if (stmtBefore != null) {
            for (int i = 0; i < stmtBefore.length; i++) {
                stmtBefore[i].close();
            }
        }
        if (stmtAfter != null) {
            for (int i = 0; i < stmtAfter.length; i++) {
                stmtAfter[i].close();
            }
        }
    }

    /**
     * Sets the designated parameter to SQL <code>NULL</code>.
     *
     * <P><B>Note:</B> You must specify the parameter's SQL type.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param sqlType the SQL type code defined in <code>java.sql.Types</code>
     * @exception SQLException if a database access error occurs
     */
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        stmtActual.setNull(parameterIndex, sqlType);
        if (isInWhereClause(parameterIndex) && stmtBefore != null) {
            stmtBefore[0].setNull(parameterIndex - paramOffset + 1, sqlType);
        }
    }

    /**
     * Sets the designated parameter to the given Java <code>boolean</code> value.
     * The driver converts this
     * to an SQL <code>BIT</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        stmtActual.setBoolean(parameterIndex, x);
        if (isInWhereClause(parameterIndex) && stmtBefore != null) {
            stmtBefore[0].setBoolean(parameterIndex - paramOffset + 1, x);
        }
    }

    /**
     * Sets the designated parameter to the given Java <code>byte</code> value.  
     * The driver converts this
     * to an SQL <code>TINYINT</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
    public void setByte(int parameterIndex, byte x) throws SQLException {
        stmtActual.setByte(parameterIndex, x);
        if (isInWhereClause(parameterIndex) && stmtBefore != null) {
            stmtBefore[0].setByte(parameterIndex - paramOffset + 1, x);
        }
    }

    /**
     * Sets the designated parameter to the given Java array of bytes.
     * The driver converts this to an SQL VARBINARY or LONGVARBINARY
     * (depending on the argument's size relative to the driver's limits
     * on VARBINARY values) when it sends it to the database.
     * 
     * @param parameterIndex - the first parameter is 1, the second is 2, ...
     * @param x - the parameter value
     */
    public void setBytes(int parameterIndex, byte x[]) throws SQLException {
        stmtActual.setBytes(parameterIndex, x);
        if (isInWhereClause(parameterIndex) && stmtBefore != null) {
            stmtBefore[0].setBytes(parameterIndex - paramOffset + 1, x);
        }
    }

    /**
     * Sets the designated parameter to the given Java <code>short</code> value. 
     * The driver converts this
     * to an SQL <code>SMALLINT</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
    public void setShort(int parameterIndex, short x) throws SQLException {
        stmtActual.setShort(parameterIndex, x);
        if (isInWhereClause(parameterIndex) && stmtBefore != null) {
            stmtBefore[0].setShort(parameterIndex - paramOffset + 1, x);
        }
    }

    /**
     * Sets the designated parameter to the given Java <code>int</code> value.  
     * The driver converts this
     * to an SQL <code>INTEGER</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
    public void setInt(int parameterIndex, int x) throws SQLException {
        stmtActual.setInt(parameterIndex, x);
        if (stmtBefore != null && isInWhereClause(parameterIndex)) {
            stmtBefore[0].setInt(parameterIndex - paramOffset + 1, x);
        }
    }

    /**
     * Sets the designated parameter to the given Java <code>long</code> value. 
     * The driver converts this
     * to an SQL <code>BIGINT</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
    public void setLong(int parameterIndex, long x) throws SQLException {
        stmtActual.setLong(parameterIndex, x);
        if (stmtBefore != null && isInWhereClause(parameterIndex)) {
            stmtBefore[0].setLong(parameterIndex - paramOffset + 1, x);
        }
    }

    /**
     * Sets the designated parameter to the given Java <code>float</code> value. 
     * The driver converts this
     * to an SQL <code>FLOAT</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
    public void setFloat(int parameterIndex, float x) throws SQLException {
        stmtActual.setFloat(parameterIndex, x);
        if (stmtBefore != null && isInWhereClause(parameterIndex)) {
            stmtBefore[0].setFloat(parameterIndex - paramOffset + 1, x);
        }
    }

    /**
     * Sets the designated parameter to the given Java <code>double</code> value.  
     * The driver converts this
     * to an SQL <code>DOUBLE</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
    public void setDouble(int parameterIndex, double x) throws SQLException {
        stmtActual.setDouble(parameterIndex, x);
        if (stmtBefore != null && isInWhereClause(parameterIndex)) {
            stmtBefore[0].setDouble(parameterIndex - paramOffset + 1, x);
        }
    }

    
    /**
     * Sets the designated parameter to the given Java <code>String</code> value. 
     * The driver converts this
     * to an SQL <code>VARCHAR</code> or <code>LONGVARCHAR</code> value
     * (depending on the argument's
     * size relative to the driver's limits on <code>VARCHAR</code> values)
     * when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
    public void setString(int parameterIndex, String x) throws SQLException {
        stmtActual.setString(parameterIndex, x);
        if (stmtBefore != null && isInWhereClause(parameterIndex)) {
            stmtBefore[0].setString(parameterIndex - paramOffset + 1, x);
        }
    }
    
    /**
     * Clears the current parameter values immediately.
     * <P>In general, parameter values remain in force for repeated use of a
     * statement. Setting a parameter value automatically clears its
     * previous value.  However, in some cases it is useful to immediately
     * release the resources used by the current parameter values; this can
     * be done by calling the method <code>clearParameters</code>.
     *
     * @exception SQLException if a database access error occurs
     */
    public void clearParameters() throws SQLException {
        stmtActual.clearParameters();
        if (stmtBefore != null) {
            for (int i = 0; i < stmtBefore.length; i++) {
                stmtBefore[i].clearParameters();
            }
        }
        if (stmtAfter != null) {
            for (int i = 0; i < stmtAfter.length; i++) {
                stmtAfter[i].clearParameters();
            }
        }
    }
  
    // Advanced features:

    /**
     * <p>Sets the value of the designated parameter with the given object. The second
     * argument must be an object type; for integral values, the
     * <code>java.lang</code> equivalent objects should be used.
     *
     * <p>The given Java object will be converted to the given targetSqlType
     * before being sent to the database.
     *
     * If the object has a custom mapping (is of a class implementing the 
     * interface <code>SQLData</code>),
     * the JDBC driver should call the method <code>SQLData.writeSQL</code> to 
     * write it to the SQL data stream.
     * If, on the other hand, the object is of a class implementing
     * <code>Ref</code>, <code>Blob</code>, <code>Clob</code>, <code>Struct</code>, 
     * or <code>Array</code>, the driver should pass it to the database as a 
     * value of the corresponding SQL type.
     *
     * <p>Note that this method may be used to pass database-specific
     * abstract data types. 
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the object containing the input parameter value
     * @param targetSqlType the SQL type (as defined in java.sql.Types) to be 
     * sent to the database. The scale argument may further qualify this type.
     * @param scale for java.sql.Types.DECIMAL or java.sql.Types.NUMERIC types,
     *          this is the number of digits after the decimal point.  For all other
     *          types, this value will be ignored.
     * @exception SQLException if a database access error occurs
     * @see Types 
     */
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
    throws SQLException {
        stmtActual.setObject(parameterIndex, x, targetSqlType, scale);
        if (stmtBefore != null && isInWhereClause(parameterIndex)) {
            stmtBefore[0].setObject(
                  parameterIndex - paramOffset + 1, x, targetSqlType, scale);
        }
    }

    //public void setObject(int parameterIndex, Object x, int targetSqlType) 
    // is implemented by superclass

    /**
     * <p>Sets the value of the designated parameter using the given object. 
     * The second parameter must be of type <code>Object</code>; therefore, the
     * <code>java.lang</code> equivalent objects should be used for built-in types.
     *
     * <p>The JDBC specification specifies a standard mapping from
     * Java <code>Object</code> types to SQL types.  The given argument 
     * will be converted to the corresponding SQL type before being
     * sent to the database.
     *
     * <p>Note that this method may be used to pass datatabase-
     * specific abstract data types, by using a driver-specific Java
     * type.
     *
     * If the object is of a class implementing the interface <code>SQLData</code>,
     * the JDBC driver should call the method <code>SQLData.writeSQL</code>
     * to write it to the SQL data stream.
     * If, on the other hand, the object is of a class implementing
     * <code>Ref</code>, <code>Blob</code>, <code>Clob</code>, <code>Struct</code>, 
     * or <code>Array</code>, the driver should pass it to the database as a 
     * value of the corresponding SQL type.
     * <P>
     * This method throws an exception if there is an ambiguity, for example, if the
     * object is of a class implementing more than one of the interfaces named above.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the object containing the input parameter value 
     * @exception SQLException if a database access error occurs or the type 
     *            of the given object is ambiguous
     */
    public void setObject(int parameterIndex, Object x) throws SQLException {
        stmtActual.setObject(parameterIndex, x);
        if (stmtBefore != null && isInWhereClause(parameterIndex)) {
            stmtBefore[0].setObject(parameterIndex - paramOffset + 1, x);
        }
    }

    //--------------------------JDBC 2.0-----------------------------

    /**
     * Retrieves a <code>ResultSetMetaData</code> object that contains
     * information about the columns of the <code>ResultSet</code> object
     * that will be returned when this <code>PreparedStatement</code> object 
     * is executed.
     * <P>
     * Because a <code>PreparedStatement</code> object is precompiled, it is
     * possible to know about the <code>ResultSet</code> object that it will
     * return without having to execute it.  Consequently, it is possible
     * to invoke the method <code>getMetaData</code> on a
     * <code>PreparedStatement</code> object rather than waiting to execute
     * it and then invoking the <code>ResultSet.getMetaData</code> method
     * on the <code>ResultSet</code> object that is returned.
     * <P>
     * <B>NOTE:</B> Using this method may be expensive for some drivers due
     * to the lack of underlying DBMS support.
     *
     * @return the description of a <code>ResultSet</code> object's columns or
     *         <code>null</code> if the driver cannot return a
     *         <code>ResultSetMetaData</code> object
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public ResultSetMetaData getMetaData() throws SQLException {
        return stmtActual.getMetaData();
    }

    /**
     * Sets the designated parameter to SQL <code>NULL</code>.
     * This version of the method <code>setNull</code> should
     * be used for user-defined types and REF type parameters.  Examples
     * of user-defined types include: STRUCT, DISTINCT, JAVA_OBJECT, and 
     * named array types.
     *
     * <P><B>Note:</B> To be portable, applications must give the
     * SQL type code and the fully-qualified SQL type name when specifying
     * a NULL user-defined or REF parameter.  In the case of a user-defined type 
     * the name is the type name of the parameter itself.  For a REF 
     * parameter, the name is the type name of the referenced type.  If 
     * a JDBC driver does not need the type code or type name information, 
     * it may ignore it.     
     *
     * Although it is intended for user-defined and Ref parameters,
     * this method may be used to set a null parameter of any JDBC type.
     * If the parameter does not have a user-defined or REF type, the given
     * typeName is ignored.
     *
     *
     * @param paramIndex the first parameter is 1, the second is 2, ...
     * @param sqlType a value from <code>java.sql.Types</code>
     * @param typeName the fully-qualified name of an SQL user-defined type;
     *  ignored if the parameter is not a user-defined type or REF 
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public void setNull (int paramIndex, int sqlType, String typeName) 
    throws SQLException {
        stmtActual.setNull(paramIndex, sqlType, typeName);
        if (stmtBefore != null && isInWhereClause(paramIndex)) {
            stmtBefore[0].setNull(
                  paramIndex - paramOffset + 1, sqlType, typeName);
        }
    }

    /**
     * Retrieves the <code>Connection</code> object
     * that produced this <code>Statement</code> object.
     * @return the connection that produced this statement
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public Connection getConnection() throws SQLException {
        return con;
    }

}
