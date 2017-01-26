/* $Header: /home/common/cvsarea/ibase/dia/src/client/FailException.java,v 1.2.10.1 2006/03/22 20:27:14 nancy Exp $ */
package client;

/**
 * An exception to indicate that a fail message was received.  A fail message
 * indicates that a message should be presented to the user and the current 
 * client task completed with no further action.  It may be caught for more
 * specific handling, especially in the login process.
 */
public class FailException extends RuntimeException {

    /**
     * Create an instance of this class with the given message.
     * @param message an error message to be presented to the user
     */
    public FailException(String message) {
        super(message);
    }
}
