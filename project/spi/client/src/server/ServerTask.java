/* $Header: /home/common/cvsarea/ibase/dia/src/server/ServerTask.java,v 1.71.6.2 2006/02/16 15:56:45 nancy Exp $ */

package server;

import common.CommonProperties;
import com.lexpar.util.Log;
import common.msg.LoggedInputStream;
import common.msg.MessageConstants;
import common.msg.MessageReader;
import common.msg.MessageWriter;
import common.msg.MultiInputStream;
import common.msg.MultiOutputStream;
import java.io.*;
import java.net.Socket;                                                         
import java.net.SocketException;                                                         
import java.sql.*;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Task to handle a connection from the client to the coding server.
 * <p> A connection (session) is recorded in the session table.  It is 
 * marked as live when available for use.  A user may be disconnected
 * by removing the session row or by clearing the live flag; the 
 * client will see the disconnection on the next interaction.  (Note that
 * the client "pings" the server periodically, so the disconnection 
 * will happen within a reasonable period.  The mechanism is that.
 * on any interaction, the ServerTask obtains an update lock on the
 * session row; if there is no session row or it is not live, it
 * does not connect.  If a session
 * row has the live flag cleared, a new connection can be created by using
 * the session_key as a (one-time) password; this is done in Handler_hello.
 */
public class ServerTask extends Thread implements MessageConstants {

    // Deadlock error code.  Note: also used in ManagedModelPeer
    final public static int ER_LOCK_DEADLOCK = 1213;
    // Duplicate Key error code.
    final public static int ER_DUP_ENTRY = 1062;
    public static int connectionCount=0;   
    private Socket          incoming;
    private int             serverNumber;
    private int             sessionId = 0;
    private int             dbPort;
    private String          dbName;
    private boolean         admin = false; // this user is in admin mode

    // Note protected, because test.server.DummyTaskWithWriter uses these
    protected Connection    con = null;
    protected Statement     st = null;
    private int             usersId = 0;
    private int             teamsId = 0;
    private int             lockVolumeId = 0;
    private int             lockBatchId = 0;
    private int             volumeId = 0;
    private int             batchId = 0;
    //private QueueSet        propagateTableSet = null;
    private long            transactionStartTime = 0; // non-zero if transaction active
    private int             age = 0;  // age of valid managed tables for this task
    private int             updateAge = 0;  // age of managed updates by this task (0 if none)
    private boolean         tempTableHasBeenCreated = false;
    private boolean         isReadOnly = false;  // the current transaction is read-only

    private PreparedStatement stmtBinLogEnabled = null;

    // The ManagedModelPeers for this task
    // modelPeerList[n] is a list of peers for table number n
    private ArrayList[]     modelPeerList = null;

    private MultiInputStream rawin;
    private MultiOutputStream rawout;
    /**
     * The MessageWriter for this task.
     * Note.  Also used by test/dummy task.
     */
    protected MessageWriter writer;

    /**
     * Create a new task to serve a connection from a client.
     */
   
    
    public ServerTask (Socket incoming, int serverNumber, int dbPort, String dbName) {
        this.incoming = incoming; 
        this.serverNumber = serverNumber; 
        this.dbPort = dbPort;
        this.dbName = dbName;
    }

    public void run() {

        // Set temporary task number indication for log messages
        // (Will be changed when we know the real task number)
        Log.setTaskNumber("?");

        try {           
            con = getConnection();

            // Set socket timeout to 5 min.  (Client should ping at least
            // every 2 min, so a timeout means a dead connection.
            incoming.setSoTimeout(300000);

            // Get the inout and output streams for the socket
            rawin = new MultiInputStream(incoming.getInputStream());
            rawout = new MultiOutputStream(incoming.getOutputStream());
                      
            // from now on, multi-statement transactions
            con.setAutoCommit(false);

            for (;;) {
                clientInteraction();
            }


        } catch (GoodbyeException e) {
            Log.print("LOGOUT - end task");
            // Close and remove the connection, so we don't kill the session row
            // We should have removed or updated the session row, and there should
            // be no changes that need propagating to any other table.
            try {
                // commit, in case it was explicit logout without restart
                // for restart, fall through to finally clause below
                con.commit();
                updateAge = 0;
            } catch (SQLException e2) {
                Log.print("Failed committing on Goodbye");
            }
        } catch (ServerFailException e) {
            Log.print("LOGOUT FAILED ServerFailException");
            e.printStackTrace();
            sendDisconnectMessage(e);
        } catch (FatalException e) {
            Log.print("LOGOUT FAILED FatalException");
            // suppress message -- already done
            e.printStackTrace();
            sendDisconnectMessage(e);
        } catch (SocketException e) {
            Log.print("LOGOUT FAILED SocketException");
            //e.printStackTrace();
            Log.print("Abort task: " + e);
            sendDisconnectMessage(e);
        } catch (IOException e) {
            Log.print("LOGOUT FAILED IOException");
            e.printStackTrace();
            Log.print("Abort task: " + e);
            sendDisconnectMessage(e);
        } catch (Throwable e) {
            Log.print("LOGOUT FAILED Throwable");
            e.printStackTrace();
            Log.print("Abort task: " + e);
            sendDisconnectMessage(e);
        } finally {
                 System.out.println("closing connection ");
            // clean up before ending task
            try {
                if (con != null) {
                    try {
                        con.rollback();
                    } catch (Throwable th) {
                        // Possibility: Warning about non-transactional tables not rolled back
                        Log.print("On rollback: "+th);
                    }
                    updateAge = 0;

                    // this cleans up temporary table from crash
                    // (avoids having replication server crash from
                    //  automatically-generated drop if task dies)
                    dropTemporaryTable();

                    // set session inactive and clear lock and age data
                    //Tables.session.executeUpdate(this,sessionId,
                    this.executeUpdate(
                        "update session"
                        +" set live=0"
                        +"   , volume_id=0"
                        +"   , batch_id=0"
                        +"   , age="+Integer.MAX_VALUE
                        +"   , lock_time=0"
                        +" where session_id = "+sessionId);
                    con.commit();
                    updateAge = 0;


                    try {
                        DiaListener.checkForShutdown();
                    } catch (Throwable th) {
                        Log.quit(th);
                    }
                    con.close();
                }
            } catch (Throwable e) {
                Log.print("On delete session & close DB connection: " + e);
                Log.quit(e);
            }

            try {
                incoming.close();
            } catch (Throwable e) {
                Log.print("On close socket: " + e);
                Log.quit(e);
            }
        }
    }

    // Attempt to tell the client why we are disconnecting
    private void sendDisconnectMessage(Throwable th) {
        if (th.getMessage() != null) {
            sendDisconnectMessage(th.getMessage());
        } else {
            sendDisconnectMessage(th.toString());
        }
    }

    // Attempt to tell the client why we are disconnecting
    private void sendDisconnectMessage(String text) {
        try {
            if (con != null) {
                try {
                    con.rollback();
                } catch (Throwable th) {
                    // Possibility: Warning about non-transactional tables not rolled back
                    Log.print("On rollback: "+th);
                }
            }
        } catch (Throwable th) {
            // Ignore errors.
        }
        try {
            writer = getMessageWriter();
            writer.startElement(T_DISCONNECT);
            writer.writeContent(text);
            writer.endElement();
            writer.close();
        } catch (Throwable th) {
            // Ignore errors.
        }
    }

    private void clientInteraction()
    throws ServerFailException, GoodbyeException, IOException, SQLException {

        // Make sure transaction is not started before readMessage
        if (transactionStartTime != 0) {
            Log.print("**** TRANSACTION STARTED AT clientInteraction: "
                      +transactionStartTime);
        }
        //Log.print("COMMIT - start client interaction");
        con.commit();

        Element messageAction = null;
        try {
            messageAction = readMessage();
        } catch (IOException th) {
            Log.print("read message rethrows: "+th);
            th.printStackTrace();
            throw th;
        } catch (Throwable th) {
            Log.print("read message throws: "+th);
            Log.quit(th);
        }
        String actionName = messageAction.getNodeName();
        //Log.print("processing message: "+actionName);

        // Make sure we are logged in (A well-behaved client will
        // always send a hello message first.  After login failure,
        // it will send a goodbye message.)
        if (sessionId == 0) {
            if (T_HELLO.equals(actionName)) {
                initiateLogin();
            } else if (T_GOODBYE.equals(actionName)) {
                // nothing to do - just let the goodbye happen
            } else {
                System.out.println("FATAL No active session: message="+actionName);
                throw new FatalException("No active session: message="+actionName);
            }
        }

        // determine the proper handler and dispatch
        Handler h = null;
        try {
            //System.out.println("Bala's log --> action is " + actionName);
            h = (Handler) Class.forName("server.Handler_"+actionName)
                                       .newInstance();
        } catch (InstantiationException e) {
            Log.quit(e);
        } catch (ClassNotFoundException e) {
            Log.quit(e);
        } catch (IllegalAccessException e) {
            Log.quit(e);
        }

        isReadOnly = h.isReadOnly();
        if (isReadOnly) {
            transactionStartTime = System.currentTimeMillis();
            con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
        } else { // since h is read/write
            // transaction already started if transactionStartTime != 0
            // this happens on a hello message
            if (transactionStartTime == 0) {
                transactionStartTime = System.currentTimeMillis();
                con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            }
        }

        // Wrap handler processing in loop, to allow for rerun on deadlock
        // TBD: Need to check that Handler_hello cannot deadlock
        for (;;) {
            try {
                writer = null;
                //propagateTableSet = null;
                updateAge = 0;

                // Check that either session row exists or this is a goodbye message
                // Record the interaction time, for use in finding dead connections
                // For read-only, we only need to have a consistent snapshot
                // For read/write, we hold an exclusive lock for the duration
                ResultSet rs = st.executeQuery(
                    "select volume_id, batch_id from session"
                    +" where live"
                    +"   and session_id="+sessionId
                    +" for update");
                if (rs.next()) {
                    if (isAdmin()) {
                        String volumeString = messageAction.getAttribute(A_VOLUME_ID);
                        String batchString = messageAction.getAttribute(A_BATCH_ID);
                        volumeId = (volumeString.length() == 0 ? 0 : Integer.parseInt(volumeString));
                        batchId = (batchString.length() == 0 ? 0 : Integer.parseInt(batchString));
                    } else {
                        // cache the lock data from the session
                        lockVolumeId = rs.getInt(1);
                        lockBatchId = rs.getInt(2);
                        volumeId = lockVolumeId;
                        batchId = lockBatchId;
                    }
                    rs.close();
                    //if (! "ping".equals(actionName)) {
                    //    System.out.println(messageAction);
                    //}
                    setBinLogEnabled(false);  // suppress replication for this
                    try {
                        st.executeUpdate(
                            "update session"
                            +" set interaction_time="+System.currentTimeMillis()
                            +" where session_id="+sessionId);
                    } finally {
                        setBinLogEnabled(true);
                    }
                    if (isReadOnly) {
                        // TBD do we want this here or earlier?  volume etc validity...
                        con.commit();
                    }
                } else {
                    // no live session, logout or refuse service
                    rs.close();
                    lockVolumeId = 0;
                    lockBatchId = 0;
                    if (h instanceof Handler_goodbye) {
                        // Pretend we succeeded and let the goodbye happen
                    } else {
                        // Refuse service
                        EventLog.logout(this, getUsersId());
                        System.out.println("FATAL Session has been terminated on the server");
                        throw new FatalException(
                            "Session has been terminated on the server");
                    }
                }

                // RUN THE HANDLER CORRESPONDING TO THE MESSAGE
                //Log.print("Call handler for: "+actionName
                //          +" v="+getLockVolumeId()+" b="+getLockBatchId());
                h.run(this, messageAction);
                //Log.print("Done handler for: "+actionName);

                break; 

            } catch (ServerSQLFailException e) {

                // server detected SQL error
                // rollback, then send fail message to client
                //propagateTableSet = null;
                updateAge = 0;
                con.rollback();
                try {
                    con.rollback();
                } catch (Throwable th) {
                    // Possibility: Warning about non-transactional tables not rolled back
                    Log.print("On rollback: "+th);
                }
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                dropTemporaryTable();

                e.printStackTrace();

                writer = getMessageWriter();
                writer.startElement(T_FAIL);
                String message = e.getMessage();
                writer.writeAttribute(A_SQLSTATE, e.getSQLState());
                writer.writeAttribute(A_SQLCODE, e.getErrorCode());
                writer.writeContent(message == null
                                    ? e.toString() : message);
                writer.endElement();

                break; 

            } catch (ServerFailException e) {

                // server detected error (generally login error)
                // rollback, then send fail message to client
                //propagateTableSet = null;
                updateAge = 0;
                try {
                    con.rollback();
                } catch (Throwable th) {
                    // Possibility: Warning about non-transactional tables not rolled back
                    Log.print("On rollback: "+th);
                }
                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                dropTemporaryTable();

                writer = getMessageWriter();
                writer.startElement(T_FAIL);
                String message = e.getMessage();
                writer.writeContent(message == null
                                    ? e.toString() : message);
                writer.endElement();

                break; 

            } catch (SQLException e) {

                String sqlState = e.getSQLState();
                int errorCode = e.getErrorCode();
                Log.write(">>>"+e+" sqlState="+sqlState+" errorCode="+errorCode);

                try {
                    con.rollback();
                } catch (Throwable th) {
                    // Possibility: Warning about non-transactional tables not rolled back
                    Log.print("On rollback: "+th);
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie1) {
                    // ignore exception
                }

                //Note.  sqlState not implemented until MySQL 4.1
                //if ("40001".equals(sqlState)) {
                if (errorCode == ER_LOCK_DEADLOCK) {
                    // it's a deadlock, try again
                    Log.print("DEADLOCK DETECTED");
                    e.printStackTrace();
                    if (T_HELLO.equals(actionName)) {
                        for (;;) {
                            try {
                                Log.print("DEADLOCK WAS IN LOGIN");
                                initiateLogin();
                                break;
                            } catch (SQLException e2) {
                                if (e2.getErrorCode() == ER_LOCK_DEADLOCK) {
                                    con.rollback();
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException ie2) {
                                        // ignore exception
                                    }
                                    continue;
                                } else {
                                    throw e2;
                                }
                            }
                        }
                    }
                    continue;
                }

                con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                throw e;
            }
        }

        //Log.print("after handler, transactionStartTime="+transactionStartTime);
        commitTransaction();

        dropTemporaryTable();
        con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);


        // default OK reply message
        if (writer == null) {
            writer = getMessageWriter();
            writer.startElement(T_OK);
            writer.endElement();
        }

        // send updates for any managed models this task has
        transactionStartTime = System.currentTimeMillis();
        //Log.print("calling updateAll");
        ManagedModelPeer.updateAll(this);
        //Log.print("back from updateAll");
        transactionStartTime = 0;

        con.commit();
        updateAge = 0;
        writer.close();

        dropTemporaryTable();
    }

    private void initiateLogin() throws SQLException {
        // start a transaction
        transactionStartTime = System.currentTimeMillis();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

        // We don't do this here any more.  Handler_hello does it when
        // updating the session row
        //// get the current age for managed tables
        //// Note:  We have "for update" to avoid later deadlock
        ////Log.print("SVRAGE select - hello");
        //ResultSet rs0 = st.executeQuery(
        //    "select age from svrage where dummy_key = 0 for update");
        //rs0.next();
        //age = rs0.getInt(1);
        //rs0.close();

        // create row in session table
        // NOT managed, because it will either be updated or be rolled back
        st.executeUpdate(
            "insert into session"
            +" set server_nbr="+serverNumber
            +"   , start_time="+transactionStartTime
            +"   , interaction_time="+transactionStartTime);
        ResultSet rs = st.executeQuery(
            "select LAST_INSERT_ID()");
        rs.next();
        sessionId = rs.getInt(1);
        rs.close();

        // Set task number indication for log messages
        Log.setTaskNumber(Integer.toString(sessionId));
    }

    /**
     * Return a JDBC connection for this task.  Multiple calls to
     * this method will return the same instance of Connection.
     */
    public Connection getConnection() {
        if (con == null) {

            String dbUrl = "jdbc:mysql://localhost:"+dbPort+"/"+dbName;
            System.out.println("Creating a new Connection. " + ++connectionCount);
            try {
                Log.write("Connecting: " + dbUrl);
                con = DriverManager.getConnection(dbUrl, "dia", "dia4ibase");

                // create a statement for general use with this task
                st = con.createStatement();

            } catch (SQLException e) {
                Log.quit("Can't connect: " + dbUrl);
            }
        }
        return con;
    }

    /**
     * Commit the current transaction.  If it is a read-write transaction,
     * changed tables are recorded in the changes table.
     */
    public void commitTransaction() {
        if (transactionStartTime == 0) {
            // no transaction is in progress
            return;
        }
        if (isReadOnly) {
            // Show read-only commits only for 200 ms or longer transactions
            if ((System.currentTimeMillis() - transactionStartTime) >= 200) {
                Log.print("$$$$$$ COMMIT READ-ONLY TRANSACTION: "
                          +(System.currentTimeMillis() - transactionStartTime));
            }
        } else {
            //if (propagateTableSet != null && ! propagateTableSet.isEmpty()) {
            //    //try {
            //    //    ManagedTable.recordChangedTables(this, propagateTableSet);
            //    //} catch (SQLException e) {
            //    //    Log.quit(e);
            //    //}
            //}
            Log.print("$$$$$$ COMMIT TRANSACTION: "
                      +(System.currentTimeMillis() - transactionStartTime));
        }
        try {
            con.commit();
        } catch (SQLException e) {
            Log.quit(e); 
        }
        updateAge = 0;
        transactionStartTime = 0;
    }


    /**
     * Set the user id for this task.  (Called from Handler_hello).
     */
    public void setUsersId(int id) {
        usersId = id;
    }

    /**
     * Get the users id for this task, 0 if no login yet.
     */
    public int getUsersId() {
        return usersId;
    }


    /**
     * Return a Statement for this task's JDBC connection.  Multiple calls to
     * this method will return the same instance of Statement.
     */
    public Statement getStatement() {
        if (con == null) {
            getConnection();
        }
        return st;
    }

    /**
     * Return the number of current server.  Numbers are assigned 
     * sequentially as servers are created.
     */
    public int getServerNumber() {
        return serverNumber;
    }

    /**
     * Return the number of this task.  Numbers are assigned 
     * sequentially as tasks are created.
     */
    public int getSessionId() {
        return sessionId;
    }

    /**
     * Return the age of this task as of start of the current interaction.
     * The client's managed tables are current as of this age.
     */
    public int getAge() {
        return age;
    }

    /**
     * Set the age of this task as of start of the current interaction.
     * (Used by Handler_goodbye to suppress table updates.)
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Return the age of managed table updates done by this task.
     * Zero, if no updates have been done yet.
     */
    public int getUpdateAge() {
        return updateAge;
    }

    /**
     * Return the age of managed table updates done by this task.
     * Generate new age value if no updates have been done yet.
     */
    public int getUpdateAgeForUpdate() throws SQLException {
        if (updateAge == 0) {
            // note the age for update before doing the changes
            // this also serializes table updated
            Statement st2 = con.createStatement();
            ResultSet rs = st2.executeQuery(
                "select age from svrage where dummy_key = 0 for update");
            rs.next();
            updateAge = 1 + rs.getInt(1);
            rs.close();
            // Note.  Not managed.
            st2.executeUpdate(
                "update svrage set age = "+updateAge
                +" where dummy_key = 0");
            st2.close();
        }
        return updateAge;
    }

    /**
     * Return the ManagedModelPeers registered for this task.
     * These are stored here, for per-task storage, and used in
     * ManagedModelPeer.  For table number n, the peers for that
     * table are listed in modelPeerList[n]
     * @see ManagedModelPeer
     */
    public ArrayList[] getModelPeerList() {
        return modelPeerList;
    }

    /**
     * Store the ManagedModelPeers registered for this task.
     * @see #getModelPeerList
     */
    public void setModelPeerList(ArrayList[] peers) {
        modelPeerList = peers;
    }

    ///**
    // * Store the set of ManagedTables for which changes need to be propagated
    // * to referring tables.  This must be set to a HashSet when any managed
    // * table is changed; otherwise it is null.
    // */
    //private void setPropagateTableSet(QueueSet s) throws SQLException {
    //    // note the age for update before doing the changes
    //    // this also serializes table updated
    //    if (s != null && updateAge == 0) {
    //        Statement st2 = con.createStatement();
    //        ResultSet rs = st2.executeQuery(
    //            "select age from svrage where dummy_key = 0 for update");
    //        rs.next();
    //        updateAge = 1 + rs.getInt(1);
    //        rs.close();
    //        // Note.  Not managed.
    //        st2.executeUpdate(
    //            "update svrage set age = "+updateAge
    //            +" where dummy_key = 0");
    //        st2.close();
    //    }
    //
    //    propagateTableSet = s;
    //}

    ///**
    // * Add a table to the set of tables to be propagated.
    // */
    //public void addPropagateTable(ManagedTable table) throws SQLException {
    //    getUpdateAgeForUpdate();  // force propagateTableSet to exist
    //    propagateTableSet.add(table);
    //}

    /**
     * Read a message for this task.
     * @return the first child of the message DOM.  This will
     * be an XML subtree headed by the action element.
     */
    public Element readMessage() throws IOException {
        MessageReader reader = new MessageReader(
                LoggedInputStream.makeInstance(
                    new GZIPInputStream(rawin.newStream())),
                //(rawin.newStream()),
                CommonProperties.getMessageDTDFilePath());
        Document doc = reader.read();
        Node result = doc.getDocumentElement().getFirstChild();
        System.out.println("result="+result.getNodeValue());
        while (result != null && result.getNodeType() != Node.ELEMENT_NODE) {
            result = result.getNextSibling();
        }
        assert result != null;
        return (Element) result;
    }

    /**
     * Obtain a DataInputStream for non-XML input data
     */
    public DataInputStream getDataStream() {
        DataInputStream result = null;
        try {
            result = new DataInputStream(new BufferedInputStream(new GZIPInputStream(rawin.newStream())));
        } catch (IOException e) {
            Log.quit(e);
        }
        return result;
    }

    /**
     * Obtain an OutputStream for non-XML input data
     */
    public OutputStream getOutputStream() {
        OutputStream result = null;
        try {
            result = new BufferedOutputStream(new GZIPOutputStream(rawout.newStream()));
        } catch (IOException e) {
            Log.quit(e);
        }
        return result;
    }

    /**
     * Get the MessageWriter for this task.
     * If there is no existing MessageWriter for the task,
     * create a new one and write the start of the "message"
     * element; otherwise, return the existing one.
     */
    public MessageWriter getMessageWriter() {
        try {
            if (writer == null) {
                writer = new MessageWriter(
                        new GZIPOutputStream(rawout.newStream()),
                        CommonProperties.MESSAGE_DTD);
            }
            return writer;
        } catch (IOException e) {
            Log.quit(e);
            return null;
        }
    }

    /**
     * Test if a given batch is locked.
     */
    public boolean isLocked(int batchId) throws SQLException {
        boolean result = false;
        ResultSet rs = st.executeQuery(
            "select 0 from session"
            +" where batch_id="+batchId);
        if (rs.next()) {
            result = true;
        }
        rs.close();
        return result;
    }

    /**
     * Lock a given batch.
     * Note.  lockVolumeId and lockBatchId are not set.  This method
     * is only called from Handler_open_batch, and these are not
     * used again in the user interaction.
     */
    public void lockBatch(int batchId) throws SQLException {
        this.executeUpdate(
            "update session S, batch B"
            +" set S.volume_id=B.volume_id"
            +"   , S.batch_id=B.batch_id"
            +"   , lock_time="+System.currentTimeMillis()
            +" where S.session_id="+sessionId
            +"   and B.batch_id="+batchId);
    }

    /**
     * Lock a given volume.
     * Note.  Used when opening volume for QA.  lockVolumeId is set.
     */
    public void lockVolume(int volumeId) throws SQLException {
        this.executeUpdate(
            "update session"
            +" set volume_id="+volumeId
            +"   , lock_time="+System.currentTimeMillis()
            +" where session_id="+sessionId);
        lockVolumeId = volumeId;
    }

    /**
     * Get the volume of currently locked batch, or 0 if no current lock.
     */
    public int getLockVolumeId() {
        return lockVolumeId;
    }

    /**
     * Get the batch currently locked, or 0 if no current lock.
     */
    public int getLockBatchId() {
        return lockBatchId;
    }

    /**
     * Get the volume of current session, or 0.
     */
    public int getVolumeId() {
        return volumeId;
    }

    /**
     * Get the batch current session, or 0.
     */
    public int getBatchId() {
        return batchId;
    }

    /**
     * Determine if current task is for an administrator.
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * Record if the current task is for an administrator.
     */
    public void setAdmin(boolean flag) {
        admin = flag;
    }

    /**
     * Kill this task.  Used for timeout or when reconnecting after
     * lost connection.   Interrupt flag will be checked in main loop;
     * closing the socket should cause an Exception if the socket
     * is currently blocked.  NOT CURRENTLY USED
     */
    public synchronized void kill() {
        this.interrupt();
        try {
            incoming.close();
        } catch (Throwable th) {
        }
    }

    /**
     * Create a temporary table.
     * Called when a temporary table is to be created.  Tne name of the
     * created table should be "TEMP."  Records the fact that one
     * was created and stops binary logging.  The caller should write to the
     * temporary table, then call finishedWritingTemporaryTable to restart
     * binary logging for replication.  The caller should not perform any
     * inserts or updates to any other tables until finishedWritingTemporaryTable
     * has been called, unless it is explicitly intended that binary logging
     * be suppressed for them.  (Note that this is the case for the changes
     * table, which is not to be replicated.)
     * <p>WARNING:  If a temporary table has already been created, it is dropped.
     * THIS CAUSES A NEW DATABASE SNAPSHOT TO BE STARTED.  WHen and if we go
     * to a version of the DBMS that supports subqueries, the use of temporary
     * tables should be replaced by use of subqueries.
     * @param sql The SQL create statement
     * @see #finishedWritingTemporaryTable
     */
    public void createTemporaryTable(String sql) throws SQLException {
        //Log.print("createTemporaryTable (sql)");
        setBinLogEnabled(false);
        if (tempTableHasBeenCreated) {
            Log.print("Dropping temporary table before create");
            st.executeUpdate("drop temporary table if exists TEMP");
        }
        tempTableHasBeenCreated = true;
        st.executeUpdate(sql);
    }

    /**
     * Create a temporary table, using a prepared statement.
     * @see #createTemporaryTable(String sql)
     * @param ps The SQL create statement
     * @see #finishedWritingTemporaryTable
     */
    public void createTemporaryTable(PreparedStatement ps) throws SQLException {
        //Log.print("createTemporaryTable (ps)");
        setBinLogEnabled(false);
        if (tempTableHasBeenCreated) {
            Log.print("Dropping temporary table before create");
            st.executeUpdate("drop temporary table if exists TEMP");
        }
        tempTableHasBeenCreated = true;
        ps.executeUpdate();
    }

    /**
     * Indicate that writing to a temporary table is complete.
     * @see #finishedWritingTemporaryTable
     */
    public void finishedWritingTemporaryTable() throws SQLException {
        //Log.print("finishedWritingTemporaryTable ");
        assert tempTableHasBeenCreated;
        setBinLogEnabled(true);
    }

    // Called to drop the temporary table, if one has been created
    // Note that it may commit (Though it may not be required)
    private void dropTemporaryTable() throws SQLException {
        //Log.print("dropTemporaryTable "+tempTableHasBeenCreated);
        if (tempTableHasBeenCreated) {
            tempTableHasBeenCreated = false;
            setBinLogEnabled(false);
            st.executeUpdate("drop temporary table if exists TEMP");
            setBinLogEnabled(true);
            con.commit();
            updateAge = 0;
        }
    }

    /**
     * Enable or disable binary logging.
     */
    public void setBinLogEnabled (boolean flag) throws SQLException {
        if (stmtBinLogEnabled == null) {
            stmtBinLogEnabled = con.prepareStatement(
                "set sql_log_bin = ?");
        }
        stmtBinLogEnabled.setInt(1, (flag ? 1 : 0));
        stmtBinLogEnabled.executeUpdate();
    }

    /**
     * Prepare a ManagedPreparedStatement.
     */
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return new ManagedPreparedStatement(this, sql);
    }

    /**
     * Execute given SQL as a ManagedPreparedStatement.
     */
    public int executeUpdate(String sql) throws SQLException {
        PreparedStatement ps = prepareStatement(sql);
        int count = ps.executeUpdate();
        ps.close();
        return count;
    }
}
