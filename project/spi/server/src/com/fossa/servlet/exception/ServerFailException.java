/*
 * ServerFailException.java
 *
 * Created on 14 November, 2007, 3:30 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.exception;

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
