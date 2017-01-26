/* $Header: /home/common/cvsarea/ibase/dia/src/server/ServerSQLFailException.java,v 1.1 2004/01/13 19:22:12 weaston Exp $ */

/*
 * ServerSQLFailException.java
 *
 * Created on May 9, 2003, 8:36 AM
 */

package server;

import java.sql.SQLException;

/**
 * An exception to indicate SQL failure in a server action.  When caught in ServerTask,
 * a <code>fail</code> message will be created to send to the client.  The
 * fail message will include an SQLState and the message from the SQLException.
 * This is intended to be used when the client may be able to recover from
 * an SQL exception on the server.
 * @author  Bill
 */
public class ServerSQLFailException extends ServerFailException {

    private String sqlState;
    private int errorCode;
    
    /**
     * Creates a new instance of <code>ServerSQLFailException</code>
     * from the given SQLException.
     */
    public ServerSQLFailException(SQLException e) {
        super(e.getMessage());
        sqlState = e.getSQLState();
        errorCode = e.getErrorCode();
    }

    /**
     * Retrieves the SQLState for this <code>SQLFailException</code> object.
     *
     * @return the SQLState value
     */
    public String getSQLState() {
	return sqlState;
    }	

    /**
     * Retrieves the vendor-specific exception code
     * for this <code>SQLFailException</code> object.
     *
     * @return the vendor's error code
     */
    public int getErrorCode() {
	return errorCode;
    }
}
