/* $Header: /home/common/cvsarea/ibase/dia/src/server/FatalException.java,v 1.2 2004/01/13 19:22:12 weaston Exp $ */

package server;

/**
 * Exception thrown after error in a server thread.  Causes termination
 * of that server thread without terminating the application.
 * Extends RuntimeException, so that it need not be declared.
 */
                            
public class FatalException extends RuntimeException {
    public FatalException() {
        super();
    }
    public FatalException(java.lang.String A) {
        super(A);
    }

    public FatalException(Throwable cause) {
        super(cause);
    }
    public FatalException(java.lang.String A, Throwable cause) {
        super(A, cause);
    }
}
