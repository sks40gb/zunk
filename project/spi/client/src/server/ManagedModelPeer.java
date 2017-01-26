/* $Header: /home/common/cvsarea/ibase/dia/src/server/ManagedModelPeer.java,v 1.24 2005/02/13 10:05:12 weaston Exp $ */
package server;

import common.Log;
import common.msg.MessageConstants;
import common.msg.MessageWriter;
import server.ServerTask;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Representation on the server of a client's SQLManagedTableModel.
 * At the end of a user interaction, ServerTask calls
 * ManagedModelPeer.updateAll, which, in turn, calls
 * ManagedModelPeer.update for each registered peer whose
 * data may have changed.
 */
public class ManagedModelPeer implements MessageConstants {

    ServerTask task;
    String name;
    int parameter = 0;
    ManagedTable table;
    int tableNumber;
    String sql;
    int columnCount;

    PreparedStatement preparedSelect = null;

    /**
     * Create a new ManagedModelPeer.
     * <p>The actual SQL is retrieved from table sql_text,
     * using the name given in the name parameter..
     * <p> The statements are executed as is for 
     * loading the ManagedTableModel initially.  On
     * update calls, the selection in sql_text.text is
     * modified so that only changed rows are retrieved.
     * @param task The current task.
     * @param name The name of the SQL to be used.
     */
    public ManagedModelPeer(ServerTask task, String name) {
        this.task = task;
        this.name = name;
        //Log.print("ManagedModelPeer: "+name);
        if (name.endsWith("]")) {
            int lbrkt = name.indexOf("[");
            parameter = Integer.parseInt(name.substring(lbrkt+1,name.length()-1));
            obtainSql(name.substring(0,lbrkt));
        } else {
            obtainSql(name);
        } 
        table = obtainTable();
        tableNumber = table.getTableNumber();
        //Log.print("ManagedModelPeer: "+tableNumber+":"+table.getTableName()
        //          +"."+parameter+": "+sql);
        load();

        // Add this peer to the modelPeerList structure,
        // so it can be found for updating
        ArrayList[] peers = task.getModelPeerList();
        if (peers == null) {
             peers = new ArrayList[ManagedTable.getManagedTableCount()];
             task.setModelPeerList(peers);
        }
        if (peers[tableNumber] ==  null) {
            peers[tableNumber] = new ArrayList();
        }
        peers[tableNumber].add(this);
    }

    // Send the initial data (as a ResultSet) to the client.
    private void load() {
        try {
            Connection con = task.getConnection();
            Statement st = task.getStatement();
            PreparedStatement pst = con.prepareStatement(sql);
            if (parameter != 0) {
                pst.setInt(1, parameter);
            }
            ResultSet rs3 = pst.executeQuery();

            // remember column count for use in updates
            ResultSetMetaData rsmd3 = rs3.getMetaData();
            columnCount = rsmd3.getColumnCount();

            // send the initial data
            Handler_sql_query.writeXmlFromResult(task, rs3);
            rs3.close();

        } catch (IOException e) {
            Log.quit(e);
        } catch (SQLException e) {
            Log.quit(e);
        }
    }

    /**
     * Update all changed tables for the given task.  Called by ServerTask
     * at the end of a user interaction for the task.
     */
    public static void updateAll(ServerTask task) throws SQLException{

        ArrayList[] peers = task.getModelPeerList();

        Connection con = task.getConnection();
        Statement st = task.getStatement();

        ResultSet rs0 = st.executeQuery(
            "select age from svrage where dummy_key = 0");
        rs0.next();
        int newAge = rs0.getInt(1);
        rs0.close();

        int age = task.getAge();
        if (newAge <= age) {
            con.commit();
            return;
        }

        //Log.print("updateAll: age="+task.getAge()+" newAge="+newAge
        //  +" #peers="+(peers==null ? "null" : Integer.toString(peers.length)));
        if (peers != null) {
            // Note.  st is used in peerToUpdate.update, need st1 here
            Statement st1=con.createStatement();
            ResultSet rs1 = st1.executeQuery(
                "select distinct table_nbr from changes"
                +" where age >"+task.getAge());
            while (rs1.next()) {
                int updateTableNumber = rs1.getInt(1);
                //Log.print("updates for table "+updateTableNumber);
                ArrayList peerList = peers[updateTableNumber];
                if (peerList != null) {
                    Iterator it = peerList.iterator();
                    while (it.hasNext()) {
                        ManagedModelPeer peerToUpdate
                                = (ManagedModelPeer) it.next();
                        peerToUpdate.update(task);
                    }
                }
            }
            st1.close();
        }

        // Update current task's age, since we have sent all updates
        // Note.  Not managed.
        // Note.  In case of deadlock, just repeat until success
        // We can't get a deadlock before this, because only non-blocking reads used
        //Log.print("setting age to "+newAge);
        // TBD This should come after message is closed?
        for (;;) {
            try {

                st.executeUpdate(
                    "update session set age="+newAge
                    +" where session_id="+task.getSessionId());
                con.commit();
                task.setAge(newAge);
                // no exception, return
                break;

            } catch (SQLException e) {

                // SQLException.  See if it's a deadlock.
                String sqlState = e.getSQLState();
                int errorCode = e.getErrorCode();
                Log.write(">>>(in updateAll) "+e+" sqlState="+sqlState+" errorCode="+errorCode);

                // always rollback
                try {
                    con.rollback();
                } catch (Throwable th) {
                    // Possibility: Warning about non-transactional tables not rolled back
                    Log.print("On rollback: "+th);
                }

                //Note.  sqlState not implemented until MySQL 4.1
                //if ("40001".equals(sqlState)) {
                if (errorCode == ServerTask.ER_LOCK_DEADLOCK) {
                    // it's a deadlock, try again
                    Log.print("DEADLOCK DETECTED");
                    e.printStackTrace();

                    // wait a second, to avoid runaway exceptions
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie1) {
                        // ignore exception
                    }
                    // try again
                    continue;
                }

                // not a deadlock.  Rethrow the SQLException;
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                throw e;
            }
        }
    }

    /**
     * Send updates for one table to the client.  Only rows which
     * have been marked changed in the changes table are considered.
     */
    private void update(ServerTask task) throws SQLException {
        Connection con = task.getConnection();
        Statement st = task.getStatement();
        int age = task.getAge();
        //Log.print("updating "+this.name+" age="+age);

        if (preparedSelect == null) {
            Log.print("in Update method preparing ["+parameter+"]--------------------> "+obtainSelect());
            preparedSelect = con.prepareStatement(obtainSelect());
            if (parameter != 0) {
                // Set id parameter.  (Note: first ? is age)
                preparedSelect.setInt(2, parameter);
            }
        }
        // set age parameter
        preparedSelect.setInt(1, age);
        Statement st1 = null;
        ResultSet rs1 = preparedSelect.executeQuery();
        int updateId = Integer.MAX_VALUE;
        if (rs1.next()) {
            updateId = rs1.getInt(1);
        }

        ResultSet rs2 = st.executeQuery(
            "select distinct id from changes"
            +" where table_nbr="+this.tableNumber
            +"   and age >"+age
            +" order by id");
        if (rs2.next()) {
            try {
                MessageWriter writer = task.getMessageWriter();
                writer.startElement(T_UPDATE_MANAGED_MODEL);
                writer.writeAttribute(A_NAME, this.name);
                do {
                    writer.startElement(T_ROW);
                    int deleteId = rs2.getInt(1);
                    writer.startElement(T_COLUMN);
                    writer.writeContent(Integer.toString(deleteId));
                    writer.endElement();
                    if (deleteId < updateId) {
                        // deletion - indicated by no more columns
                        //Log.print("delete: "+deleteId);
                    } else {
                        assert deleteId == updateId;
                        // insertion or update
                        //Log.print("insert: "+deleteId);
                        for (int i = 2; i <= columnCount; i++) {
                            //Log.print("... column "+i);
                            writer.startElement(T_COLUMN);
                            String content = rs1.getString(i);
                            if (content != null) {
                                writer.writeContent(content);
                            }
                            writer.endElement();  // </column>
                        }
                        updateId = (rs1.next() ? rs1.getInt(1) : Integer.MAX_VALUE);
                    }
                    writer.endElement();  // </row>

                } while (rs2.next());
                writer.endElement();
            } catch (IOException e) {
                Log.quit(e);
            }
        }
        rs1.close();
        if (st1 != null) {
            st1.close();
        }
    }

    /**
     * Load the sql and sql_2 Strings from the database.
     */
    private void obtainSql(String name) {
        try {
            Statement st = task.getStatement();

            ResultSet rs1 = st.executeQuery(
                "select text from sql_text"
                +" where name='"+name+"'");

            if (rs1.next()) { 
                sql = SqlUtil.substitute(task, rs1.getString(1) + " order by 1");
                System.out.println("obtainSql--------------------->" +  sql);
                rs1.close();
            } else {
                Log.quit("SQL not found: '"+name+"'");
            }
        } catch (SQLException e) {
            Log.quit(e);
        }
    }

    /**
     * Extract the name of the principal table from a select statement.
     * The principal table is the one named first in the table
     * specification, following the from keyword.  Also works
     * for a create temporary table ... select.
     */
    private ManagedTable obtainTable() {
        Pattern pat = Pattern.compile("\\sfrom\\s+(\\w+)",
                                      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher mat = pat.matcher(sql);
        boolean isMatch = mat.find();
        if (! isMatch) {
            Log.quit("ManagedModelPeer: obtainTable: match failed: "+sql);
        }
        String tableName = sql.substring(mat.start(1),mat.end(1));
        ManagedTable table = ManagedTable.lookup(tableName);
        if (table == null) {
            Log.quit("ManagedModelPeer: table not managed: "+tableName);
        }
        Log.print("ManagedModelPeer.load:  obtainTable   ----------------> table="  +    tableName);
        return table;
    }

    /**
     * Modifies the sql String to insert a join with the changes
     * table, limiting selection to changed rows. 
     */
    private String obtainSelect() {
        assert sql.startsWith("select");
        Pattern pat = Pattern.compile("select.*\\Wfrom\\s+(\\w++)\\s*(\\w++|,?+)",
                                      Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher mat = pat.matcher(sql);
        boolean isMatch = mat.lookingAt();
        if (! isMatch) {
            Log.quit("ManagedModelPeer: obtainSelect: match failed: "+sql);
        }
        String tableName = sql.substring(mat.start(1),mat.end(1));
        String aliasName = sql.substring(mat.start(2),mat.end(2));
        ManagedTable table = ManagedTable.forName(tableName);
        if (table == null) {
            Log.quit("ManagedModelPeer.obtainSelect: Not managed table: "+tableName);
        }
        int startTable = mat.start(1);
        int endAlias = mat.end(2);
        if (aliasName.charAt(0) == ','
            || aliasName.equalsIgnoreCase("WHERE")
            || aliasName.equalsIgnoreCase("INNER")
            || aliasName.equalsIgnoreCase("LEFT")
            || aliasName.equalsIgnoreCase("RIGHT")
            || aliasName.equalsIgnoreCase("CROSS")
            || aliasName.equalsIgnoreCase("JOIN")
            || aliasName.equalsIgnoreCase("STRAIGHT_JOIN")
            || aliasName.equalsIgnoreCase("ORDER"))
        {
            aliasName = tableName;
            endAlias = mat.end(1);
        }
        String selectSql = 
            "select distinct"
            +sql.substring(6, startTable)
            +"changes inner join "
            +sql.substring(startTable,endAlias)
            +" on changes.id="+aliasName+"."+tableName+"_id"
            +" and changes.age > ?"
            //+" and changes.table_nbr="+table.TableNumber()
            +sql.substring(endAlias);

        Log.print("ManagedModelPeer.load:   obtainSelect  ----------------> " +    selectSql);
        return selectSql;
    }
}
