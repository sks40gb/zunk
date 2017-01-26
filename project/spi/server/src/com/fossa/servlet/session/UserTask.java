/*
 * FossaUser.java
 *
 * Created on 13 November, 2007, 7:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.session;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MultiInputStream;
import com.fossa.servlet.common.msg.MultiOutputStream;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.ManagedPreparedStatement;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author prakash
 */
/**
 * Class use to Get/Set the users information 
 */
public class UserTask implements java.io.Serializable{
    
    // Deadlock error code.  Note: also used in ManagedModelPeer
    final public static int ER_LOCK_DEADLOCK = 1213;
    // Duplicate Key error code.
    final public static int ER_DUP_ENTRY = 1062;
    
    private int     usersId = 0;
    private int     sessionId = 0;
    private int     teamsId = 0;
    private int     lockVolumeId = 0;
    private int     lockBatchId = 0;
    private int     volumeId = 0;
    private int     batchId = 0;
    private long    transactionStartTime = 0; // non-zero if transaction active
    private int     age = 0;  // age of valid managed tables for this task
    private int     updateAge = 0;  // age of managed updates by this task (0 if none)
    private boolean tempTableHasBeenCreated = false;
    private boolean isReadOnly = false;  // the current transaction is read-only
    private boolean admin = false; // this user is in admin mode    
    private int serverCount = 0;
    private String contextPath = null; //relative path
    private int pingCount = 0;
     private String hostName = null; //host name
    
    private String fossaSessionId = null;    
    
    // The ManagedModelPeers for this task
    // modelPeerList[n] is a list of peers for table number n
    private ArrayList[]     modelPeerList = null;
    
     private MultiInputStream rawin;  
     private MultiOutputStream rawout;
     
     private static Logger logger = Logger.getLogger("com.fossa.servlet.session");
    
    
    /** Creates a new instance of FossaUser */
    public UserTask() {
    }
    
    public int getUsersId() {
        return usersId;
    }

    public void setUsersId(int usersId) {
        this.usersId = usersId;
    }

    public int getTeamsId() {
        return teamsId;
    }

    public void setTeamsId(int teamsId) {
        this.teamsId = teamsId;
    }

    public int getLockVolumeId() {
        return lockVolumeId;
    }

    public void setLockVolumeId(int lockVolumeId) {
        this.lockVolumeId = lockVolumeId;
    }

    public int getLockBatchId() {
        return lockBatchId;
    }

    public void setLockBatchId(int lockBatchId) {
        this.lockBatchId = lockBatchId;
    }

    public int getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(int volumeId) {
        this.volumeId = volumeId;
    }

    public int getBatchId() {
        return batchId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public long getTransactionStartTime() {
        return transactionStartTime;
    }

    public void setTransactionStartTime(long transactionStartTime) {
        this.transactionStartTime = transactionStartTime;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        //System.out.println("setting age in usertask" +         age);
        this.age = age;
    }

    public int getUpdateAge() {
        //System.out.println("updateAge  " +  updateAge + " : where age is : " +      age);
        return updateAge;
    }

    public void setUpdateAge(int updateAge) {
        this.updateAge = updateAge;
    }

    public boolean isTempTableHasBeenCreated() {
        return tempTableHasBeenCreated;
    }

    public void setTempTableHasBeenCreated(boolean tempTableHasBeenCreated) {
        this.tempTableHasBeenCreated = tempTableHasBeenCreated;
    }

    public boolean isIsReadOnly() {
        return isReadOnly;
    }

    public void setIsReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    
      public void setContextPath(String contextPath) {
                this.contextPath = contextPath;
            }
     public String getContextPath() {
        return contextPath;
    }
    
       public void setServerCount(int serverCount) {
        this.serverCount = serverCount;
    }
     public int getServerCount() {
        return serverCount;
    }
     
      public int getPingCount() {
        return pingCount;
    }

    public void setPingCount(int pingCount) {
        this.pingCount = pingCount;
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
    
    /**
     * Enable or disable binary logging.
     */
    public void setBinLogEnabled (boolean flag, DBTask dbTask) throws SQLException {      
        
        /*      
        if (null == stmtBinLogEnabled) {
            System.out.println("stmtBinLogEnabled  null " );
            Connection con = dbTask.getConnection();
            stmtBinLogEnabled = con.prepareStatement(
                "set sql_log_bin = ?");
        }      
        stmtBinLogEnabled.setInt(1, (flag ? 1 : 0));
        System.out.println("stmtBinLogEnabled 4444 ");
        stmtBinLogEnabled.executeUpdate();        
        */
    }

    
    /**
     * Return the age of managed table updates done by this task.
     * Generate new age value if no updates have been done yet.
     */
    public int getUpdateAgeForUpdate(DBTask dbTask) throws SQLException {
        
        updateAge = 0;
        if (updateAge == 0) {
        
            // note the age for update before doing the changes
            // this also serializes table updated
            Connection con = dbTask.getConnection();
            Statement st2 = con.createStatement();
        
//            ResultSet rs = st2.executeQuery(SQLQueries.SEL_UTASK_SVRAGE);
            ResultSet rs = st2.executeQuery("SELECT age FROM svrage WITH(UPDLOCK) WHERE dummy_key = 0");
            rs.next();
            
            updateAge = 1 + rs.getInt(1);
        
            rs.close();
            // Note.  Not managed.
        
            st2.executeUpdate("update svrage set age = "+updateAge+" where dummy_key = 0");

        }
        return updateAge;
    }
    
    /**
     * Prepare a ManagedPreparedStatement.
     */
    public PreparedStatement prepareStatement(DBTask dbTask, String sql) throws SQLException {
        return new ManagedPreparedStatement(this, dbTask, sql);
    }
    
    /**
     * Execute given SQL as a ManagedPreparedStatement.
     */
    public int executeUpdate(DBTask dbTask, String sql) throws SQLException {
        PreparedStatement ps = prepareStatement(dbTask, sql);
        int count = ps.executeUpdate();
        ps.close();
        return count;
    }
    
    /**
     * Commit the current transaction.  If it is a read-write transaction,
     * changed tables are recorded in the changes table.
     */
    public void commitTransaction(DBTask dbTask) {
        
        Connection con = dbTask.getConnection();        
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
            logger.error("Exception while committing transactions." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(e); 
        }
        updateAge = 0;
        transactionStartTime = 0;
    }
    
    public void setFossaSessionId(String fossaSessionId) {
        this.fossaSessionId = fossaSessionId;
    }
    
    public String getFossaSessionId() {
        return fossaSessionId;
    }
    
    /**
     * Obtain a DataInputStream for non-XML input data
     */
    public DataInputStream getDataStream() {
        DataInputStream result = null;
        try {
            result = new DataInputStream(new BufferedInputStream(new GZIPInputStream(rawin.newStream())));
        } catch (IOException e) {
            logger.error("Exception while getting DataInputStream for non-XML input data." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
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
            logger.error("Exception while getting DataOutputStream for non-XML input data." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(e);
        }
        return result;
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
    public void createTemporaryTable(String sql,DBTask dbTask) throws SQLException {
        //Log.print("createTemporaryTable (sql)");
         setBinLogEnabled(false,dbTask);
         Connection con = dbTask.getConnection();
         Statement st=con.createStatement();                     
         st.executeUpdate(SQLQueries.DROP_TEMP_TBL);         
    
       // st.executeUpdate("drop table #TEMP"); 
       // tempTableHasBeenCreated = true;        
        st.executeUpdate(sql);
    }
    
    public void finishedWritingTemporaryTable(DBTask dbTask) throws SQLException {
        //Log.print("finishedWritingTemporaryTable ");
        assert tempTableHasBeenCreated;
        setBinLogEnabled(true,dbTask);
    }
    
     public void lockBatch(DBTask dbTask,int batchId) throws SQLException {                     
           Statement st = dbTask.getStatement();
           Date date = new Date();
           long time = date.getTime();
           Timestamp timestamp = new Timestamp(time);
           ResultSet rs =st.executeQuery("select volume_id from batch where batch_id ="+batchId);
           int volume_Id = 0;
           if(rs.next()){
             volume_Id = rs.getInt(1);
           }
           rs.close();

           this.executeUpdate(dbTask,"update session set volume_id="+ volume_Id +", batch_id="+ batchId +
                   " , lock_time='"+timestamp+"'" +
                   " where session_id="+sessionId);
         
    }
     
     public void lockVolume(DBTask dbtask,int volumeId) throws SQLException {       

        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time); 
        lockVolumeId = volumeId;
        this.executeUpdate(dbtask,"update session set volume_id="+volumeId+", lock_time='"+timestamp+
                "' where session_id= "+sessionId);        
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
         
   
}
