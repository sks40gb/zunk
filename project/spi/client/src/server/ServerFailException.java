/* $Header: /home/common/cvsarea/ibase/dia/src/server/ServerFailException.java,v 1.1 2004/01/13 19:22:12 weaston Exp $ */

/*
 * ServerFailException.java
 *
 * Created on May 9, 2003, 8:36 AM
 */

package server;

/**
 * An exception to indicate failure in a server action.  When caught in ServerTask,
 * a <code>fail</code> message will be created to send to the client.  A 
 * <code>fail</code> message indicates an error on the server which does
 * not require termination of the connection.
 * @author  Bill
 */
public class ServerFailException extends RuntimeException {
    
    ///**
    // * Creates a new instance of <code>ServerFailException</code> without detail message.
    // */
    //public ServerFailException() {
    //    this("Failed on server.")
    //}
    
    
    /**
     * Constructs an instance of <code>ServerFailException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ServerFailException(String msg) {
        super(msg);
    }
}
