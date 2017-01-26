/*
 * Command_ClientTaskDemo.java
 *
 * Created on October 29, 2007, 7:20 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet;

import com.fossa.listeners.FossaSessionListener;
import com.fossa.servlet.command.Command;
import com.fossa.servlet.command.CommandFactory;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.msg.MultiInputStream;
import com.fossa.servlet.common.msg.MultiOutputStream;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.FatalException;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.exception.ServerSQLFailException;
import com.fossa.servlet.server.GoodbyeException;
import com.fossa.servlet.session.UserTask;
import com.fossa.servlet.server.EventLog;
import com.fossa.servlet.server.ManagedModelPeer;
import com.fossa.servlet.common.SQLQueries;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.w3c.dom.Element;
import java.sql.Timestamp;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 * This class handles the request and response operations between server and client.
 * @author bmurali
 */
public class DispatcherServlet extends HttpServlet implements MessageConstants {
   
   
    private Element messageAction = null;
    
    //Holds the command name for the particular action
    private Command command = null;          
    
    //Holds the particular action name
    private String actionName = null;
    
    private String userSesssionId = null;
    final protected static String MESSAGE_DTD = "message.dtd";        
    private static Logger logger = Logger.getLogger("com.fossa.servlet");
    
    /** Declaring  constructor of the class*/
    public DispatcherServlet() {
    }

    /**
     * This method is invoked whenever a client submits a request with a 'get' method.
     * @param request : is the HttpRequest object
     * @param response : is HttpResponse object
     * @throws java.io.IOException
     */
    public void doGet(HttpServletRequest request,
            HttpServletResponse response) throws IOException {
        doPost(request, response);
    }

    /**
     * This method is invoked whenever a client submits a request with a 'post' method or with a 'get' method.
     * @param request : is the HttpRequest object
     * @param response : is HttpResponse object
     * @throws java.io.IOException
     */
    public void doPost(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        // variable declaration
        InputStream inputStream = null;
        OutputStream outputStream = null;
        MessageWriter writer = null;
        MultiInputStream multiInputStream = null;
        MultiOutputStream multiOutStream = null;
        Connection connection = null;
        Statement st = null;
        String userSessionId = null;
        HttpSession usersSession = null;
        UserTask task = null;

        XmlReader reader = new XmlReader();
        DBTask dbTask = new DBTask();

        try {
            connection = dbTask.getConnection();
            st = dbTask.getStatement();
            multiInputStream = new MultiInputStream(request.getInputStream());
            multiOutStream = new MultiOutputStream(response.getOutputStream());
            messageAction = reader.readMessage(multiInputStream.newStream());
            actionName = messageAction.getNodeName();
            boolean hasAttributes = messageAction.hasAttributes();
            if (hasAttributes) {
                userSessionId = messageAction.getAttribute("fossaSession_id");
            }
            // from now on, multi-statement transactions
            dbTask.setAutoCommit(false);

            // The first time user logs in            
            if (null == userSessionId || userSessionId.equals("-1")) {
                usersSession = request.getSession();
                task = new UserTask();
                ServletContext context = getServletContext();
                String contextPath = context.getRealPath("/");
                task.setContextPath(contextPath);
                task.setHostName(request.getRemoteHost());
              // System.out.println("  request.getRemoteHost()==============="+   request.getRemoteHost());
                task.setFossaSessionId(usersSession.getId());
                usersSession.setAttribute(usersSession.getId(), task);

            } else {   
                // This happens once the user is logged in
               
                usersSession = FossaSessionListener.getAssociatedSession(userSessionId);
                if(null != usersSession){
                  Object usersSessionObject = usersSession.getAttribute(userSessionId);
                   if (null == usersSessionObject) {
                       throw new FatalException("Invalid session: message=" + userSesssionId);
                   }
                   task = (UserTask) usersSessionObject;
                   if (null == task) {
                       throw new FatalException("Invalid session: message=" + userSesssionId);
                   }
                }else{
                   throw new IllegalStateException();
                }
            }
            // This is for the first time user is logging in
            if (task.getSessionId() == 0) {                
                if (MessageConstants.T_HELLO.equals(actionName)) {
                    initiateLogin(task, dbTask);
                    userSessionId = usersSession.getId();
                    task.setFossaSessionId(userSessionId);
                    usersSession.setAttribute(userSessionId, task);
                } else if (T_GOODBYE.equals(actionName)) {
                    try {
                        // nothing to do - just let the goodbye happen
                        throw new GoodbyeException("Goodbye");
                    } catch (GoodbyeException ex) {
                        
                    }
                } else {
                    // This is not a first time and so throw error
                    throw new FatalException("No active session: message=" + actionName);
                }
            }

            CommandFactory factory = CommandFactory.getInstance();
            command = null;
            if (null != actionName) {
                command = factory.getCommand(actionName);
            } else {
                throw new ServerFailException("Invalid Server Request. Please login again");
            }

            task.setIsReadOnly(command.isReadOnly());
            if (task.isIsReadOnly()) {
                task.setTransactionStartTime(System.currentTimeMillis());
                connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } else { // since h is read/write
                // transaction already started if transactionStartTime != 0
                // this happens on a hello message

                if (task.getTransactionStartTime() == 0) {
                    task.setTransactionStartTime(System.currentTimeMillis());
                    connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                }
            }
            // Wrap handler processing in loop, to allow for rerun on deadlock
            task.setUpdateAge(0);
            
            /* Check that either session row exists or this is a goodbye message
               Record the interaction time, for use in finding dead connections
               For read-only, we only need to have a consistent snapshot
               For read/write, we hold an exclusive lock for the duration
            */
            
            ResultSet getVolumeIdAndBatchIDResultSet = st.executeQuery(SQLQueries.SEL_VOL_BATCH + task.getSessionId());

            //Resultset will return only one volume id & batch id
            if (getVolumeIdAndBatchIDResultSet.next()) {  
                if (task.isAdmin()) {
                    String volumeString = messageAction.getAttribute(MessageConstants.A_VOLUME_ID);
                    String batchString = messageAction.getAttribute(MessageConstants.A_BATCH_ID);
                    int volumeId = (volumeString.length() == 0 ? 0 : Integer.parseInt(volumeString));
                    int batchId = (batchString.length() == 0 ? 0 : Integer.parseInt(batchString));
                    task.setVolumeId(volumeId);
                    task.setBatchId(batchId);

                } else {
                    // cache the lock data from the session
                    int lockVolumeId = getVolumeIdAndBatchIDResultSet.getInt(1);
                    int lockBatchId = getVolumeIdAndBatchIDResultSet.getInt(2);
                    int volumeId = lockVolumeId;
                    int batchId = lockBatchId;
                    task.setLockVolumeId(lockVolumeId);
                    task.setLockBatchId(lockBatchId);
                    task.setVolumeId(volumeId);
                    task.setBatchId(batchId);
                }
                getVolumeIdAndBatchIDResultSet.close();
                
                // suppress replication for this
                try {
                     
                    Date date = new Date();
                    long time = date.getTime();
                    Timestamp timestamp = new Timestamp(time);
                    st.executeUpdate("update session set interaction_time='" + timestamp + 
                                       "' where session_id=" + task.getSessionId());
                } finally {
                    // task.setBinLogEnabled(true, dbTask);
                }
                if (task.isIsReadOnly()) {
                    connection.commit();
                }else{
                   //Cannot commit the transaction
                }
            } else {
                // no live session, logout or refuse service
                getVolumeIdAndBatchIDResultSet.close();
                task.setLockVolumeId(0);
                task.setLockBatchId(0);

                if (MessageConstants.T_GOODBYE.equalsIgnoreCase(actionName)) {
                    // Pretend we succeeded and let the goodbye happen
                } else {
                    // Refuse service
                    EventLog.logout(task, dbTask, task.getUsersId());
                    //throw new FatalException("Session has been terminated on the server");
                    throw new ServerFailException("Session has been terminated on the server.\nPlease login again");
                }
            }
            // RUN THE COMMAND CORRESPONDING TO THE MESSAGE
            writer = getMessageWriter(multiOutStream.newStream());
            String error = command.execute(messageAction, task, dbTask, writer);
            if (error != null) {
                throw new ServerFailException(error);
            }
            commitTransaction(task, dbTask);
            dropTemporaryTable(task, dbTask);
            connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);            
            if (writer.getHasAppendOk()) {
                writer.startElement(T_OK);
                writer.endElement();
            }
            // send updates for any managed models this task has
            task.setTransactionStartTime(System.currentTimeMillis());            
            ManagedModelPeer.updateAll(task, dbTask, writer);            
            task.setTransactionStartTime(0);
            connection.commit();
            task.setUpdateAge(0);
            dropTemporaryTable(task, dbTask);

            // save the UserTask into Session again
            usersSession.setAttribute(userSessionId, task);
            // close the connection
            if (null != connection) {
                connection.close();
                connection = null;
            }
            if (null != writer) {
                writer.close();
            }
            if (null != inputStream) {
                inputStream.close();
            }
            if (null != outputStream) {
                outputStream.close();
            }

        } catch (ServerSQLFailException sqlFail) {
            // server detected SQL error rollback, then send fail message to client
            // propagateTableSet = null;
            task.setUpdateAge(0);
            try {
                connection.rollback();
                connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (Throwable th) {
                // Possibility: Warning about non-transactional tables not
                // rolled back
                Log.print("On rollback: " + th);
                printExceptions("Exception while connection rollback." , sqlFail);
            }
            // TODO ----> dropTemporaryTable();
            sqlFail.printStackTrace();
            writer.startElement(T_FAIL);
            String message = sqlFail.getMessage();
            writer.writeAttribute(A_SQLSTATE, sqlFail.getSQLState());
            writer.writeAttribute(A_SQLCODE, sqlFail.getErrorCode());
            writer.writeContent(message == null ? sqlFail.toString() : message);
            writer.endElement();
            closeAll(connection, writer, inputStream, outputStream);

        } catch (ServerFailException fail) {            
            printExceptions("Exception while dispatching the servlet.", fail);
            if (null != task) {
                task.setUpdateAge(0);
            }
            try {
                connection.rollback();
                connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (Throwable th) {
                // Possibility: Warning about non-transactional tables not
                // rolled back
                Log.print("On rollback: " + th);
                printExceptions("Exception while connection rollback." , fail);
            }
            // TODO ----> dropTemporaryTable();
            if(writer == null){
               writer = getMessageWriter(multiOutStream.newStream());               
            }
            writer.startElement(T_FAIL);
            String message = fail.getMessage();
            writer.writeContent(message == null ? fail.toString() : message);
            writer.endElement();
            closeAll(connection, writer, inputStream, outputStream);

        } catch (SQLException sqlExc) {            
            printExceptions("Exception while dispatching servlet.", sqlExc);
            force2Invalidate(userSessionId);
            String sqlState = sqlExc.getSQLState();
            int errorCode = sqlExc.getErrorCode();
            Log.write(">>>" + sqlExc + " sqlState=" + sqlState + " errorCode=" + errorCode);

            try {
                connection.rollback();
            } catch (Throwable th) {
                // Possibility: Warning about non-transactional tables not rolled back
                Log.print("On rollback: " + th);
                printExceptions("Exception while connection rollback.", sqlExc);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
                // ignore exception
                printExceptions("Exception during sleep() of Thread.", ignored);
            }

            // Note. sqlState not implemented until MySQL 4.1
            // if ("40001".equals(sqlState)) {
            if (T_HELLO.equals(actionName)) {
                for (;;) {
                    try {
                        Log.print("DEADLOCK WAS IN LOGIN");
                        initiateLogin(task, dbTask);
                        break;
                    } catch (SQLException sqlException) {                        
                        printExceptions("Exception while deadlock occured.", sqlException);
                    }
                }
            }
            try {
                connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                throw sqlExc;
            } catch (SQLException ex) {
                printExceptions("Exception while setting Transaction Isolation to repeatable read in Dispatcher.", ex);
            }
            closeAll(connection, writer, inputStream, outputStream);

        }catch(IllegalStateException illexc){
            printExceptions("IllegalStateException occured in DispatcherServlet." , illexc);
            //Terminates the user session            
            closeUserSession(userSessionId,connection);
            //Invalidate the user session
            force2Invalidate(userSessionId);
                        
            if (null == writer) {                
                writer = getMessageWriter(multiOutStream.newStream()); 
            }
            writer.startElement(T_FAIL);
            String message = "Your session has been expired.";
            writer.writeContent(message);
            writer.endElement();           
            writer.close();
            
            try{
                  connection.commit();    
                  connection.close();
            }catch(SQLException e){
               printExceptions("SQLException occured during closing & committing the connection", e);
            }                
            if (null != inputStream) {
                inputStream.close();
            }
            if (null != outputStream) {
                outputStream.close();
            }         
        }catch (Exception exc) {
            printExceptions("Exception occured in DispatcherServlet." , exc);                 
            //Invalidate the user session
            force2Invalidate(userSessionId);
            try {
                connection.rollback();
                connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
            } catch (Throwable th) {
                // Possibility: Warning about non-transactional tables not rolled back
                Log.print("On rollback: " + th);
            }
            //TODO----> dropTemporaryTable();
            if (null == writer) {
                // hack to fix NullPointerException. Happens when the client starts 'Other Activity' like break.
                writer = getMessageWriter(multiOutStream.newStream()); //Please check this for other transactions                
            }
                writer.startElement(T_FAIL);
                String message = "Server not available. Please contact Administrator.";
                writer.writeContent(message);
                writer.endElement();
            
            closeAll(connection, writer, inputStream, outputStream);
        }
    }

    private void closeAll(Connection connection, MessageWriter writer, InputStream inputStream, OutputStream outputStream) {
        try {
            if (null != connection) {
                connection.close();
                connection = null;
            }
            writer.close();

            if (null != inputStream) {
                inputStream.close();
            }
            if (null != outputStream) {
                outputStream.close();
            }
        } catch (Exception exc) {
            printExceptions("Exception when closing connection and streams ", exc);
        }

    }

   private void closeUserSession(String fossaSessionId,Connection conn) {
      try {                                   
         PreparedStatement ps = conn.prepareStatement("delete from session where fossa_session_id = ?");
         ps.setString(1, fossaSessionId);
         ps.executeUpdate();                  
      } catch (SQLException ex) {
         printExceptions("SQLException caught during closing user session", ex);
      }catch(Exception ex){
         printExceptions("Exception caught during closing user session", ex);
      }
   }

    /**
     * invaidates the user session.
     * @param userSessionId : is a string containing the userSessionId
     */
    private void force2Invalidate(String userSessionId) {
        if (null != userSessionId) {
            FossaSessionListener.force2Invalidate(userSessionId);
        } else {
            System.err.println("Cannot force Session to get invalidated.");
            System.err.println("Session is not found for sesssion Id : " + userSessionId);
        }
    }
    
    /**
     * Invoked when login action is performed.
     * @param task : is a UserTask object
     * @param dbTask : is a DBTask object
     * @throws java.sql.SQLException
     */
    private void initiateLogin(UserTask task, DBTask dbTask) throws SQLException {
        // start a transaction
        task.setTransactionStartTime(System.currentTimeMillis());

        Connection con = dbTask.getConnection();
        Statement st = dbTask.getStatement();
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);        
        Date date = new Date();
        long time = date.getTime();
        Timestamp timestamp = new Timestamp(time);

        // TODO: Cyrus - shortcut
        int serverNumber = 0;
        Statement getServerInstanceStatement = dbTask.getStatement();        
        ResultSet getServerInstanceResultSet = getServerInstanceStatement.executeQuery(SQLQueries.SEL_SERVER_INSTANCE);
        getServerInstanceResultSet.next();
        serverNumber = getServerInstanceResultSet.getInt(1);
        
        // We don't do this here any more. Handler_hello does it when
        // updating the session row get the current age for managed tables
        // Note: We have "for update" to avoid later deadlock
                
        // create row in session table
        // NOT managed, because it will either be updated or be rolled back
        
        st.executeUpdate("INSERT INTO session(server_nbr,start_time,interaction_time) " +
                           "values(" + serverNumber + ",'" + timestamp + "','" + timestamp + "')");

        ResultSet getSessionIdResultSet = st.executeQuery(SQLQueries.SEL_SESSION_ID);

        getSessionIdResultSet.next();
        task.setSessionId(getSessionIdResultSet.getInt(1));
        getSessionIdResultSet.close();
        getServerInstanceResultSet.close();
        // Set task number indication for log messages
        // TODO: Cyrus - Use log to set task number
        // Log.setTaskNumber(Integer.toString(sessionId));
    }

    /**
     * Commit the current transaction. If it is a read-write transaction,
     * changed tables are recorded in the changes table.
     */
    public void commitTransaction(UserTask task, DBTask dbTask) {
        long transactionStartTime = task.getTransactionStartTime();
        Connection con = dbTask.getConnection();

        if (transactionStartTime == 0) {
            // no transaction is in progress
            return;
        }
        if (task.isIsReadOnly()) {
            // Show read-only commits only for 200 ms or longer transactions
            if ((System.currentTimeMillis() - transactionStartTime) >= 200) {
                Log.print("$$$$$$ COMMIT READ-ONLY TRANSACTION: " + (System.currentTimeMillis() - transactionStartTime));
            }
        } else {
            Log.print("$$$$$$ COMMIT READ-ONLY TRANSACTION: " + (System.currentTimeMillis() - transactionStartTime));
        }
        try {
            con.commit();
        } catch (SQLException e) {
            printExceptions("Exception while committing the transactions.", e);
        // Log.quit(e);
        }
        task.setUpdateAge(0);
        task.setTransactionStartTime(0);
    }

    // Called to drop the temporary table, if one has been created
    // Note that it may commit (Though it may not be required)
    private void dropTemporaryTable(UserTask task, DBTask dbTask) throws SQLException {
        Connection con = dbTask.getConnection();
        Statement dropTemporaryTableStatement = dbTask.getStatement();
        if (task.isTempTableHasBeenCreated()) {
            task.setTempTableHasBeenCreated(false);            
            dropTemporaryTableStatement.executeUpdate(SQLQueries.DROP_TEMP_TBL);            
            con.commit();
            task.setUpdateAge(0);
        }
    }

    /**
     * Returns a MessageWriter object
     * @param stream : is a OutputStream object
     * @return MessageWriter object or a null value
     */
    private MessageWriter getMessageWriter(OutputStream stream) {
        MessageWriter writer = null;
        try {
            writer = new MessageWriter(stream, MESSAGE_DTD);
        } catch (IOException ex) {
            printExceptions("Exception while getting message writer", ex);
        }
        return writer;
    }
    
    /**
     * This methods prints the stack trace of generated exceptions
     * @param customMessage : is the error message to be shown
     * @param execption : is the exception object caught or thrown
     */
    private void printExceptions(String customMessage,Exception execption){
       logger.error(customMessage + execption);            
       StringWriter swt = new StringWriter();
       execption.printStackTrace(new PrintWriter(swt));
       logger.error(swt.toString());
    }
}
