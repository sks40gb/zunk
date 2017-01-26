/* $Header: /home/common/cvsarea/ibase/dia/src/client/SQLFailException.java,v 1.2 2004/03/27 20:27:01 weaston Exp $ */

/*
 * SQLFailException.java
 *
 * Created on May 9, 2003, 8:36 AM
 */
package client;

//import java.sql.SQLException;
/**
 * An exception to indicate SQL failure in a server action.  Thrown in a
 * client task on receiving a <fail> message with an sqlstate attribute.
 * The client may catch and recover; otherwise the client will send 
 * a goodbye message and die.
 * @author  Bill
 */
public class SQLFailException extends FailException {

    private String sqlState;
    private int errorCode;

    /**
     * Creates a new instance of <code>SQLFailException</code>
     */
    public SQLFailException(String message, String sqlState, int errorCode) {
        super(message);
        this.sqlState = sqlState;
        this.errorCode = errorCode;
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
