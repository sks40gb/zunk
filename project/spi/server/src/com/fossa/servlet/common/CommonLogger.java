/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.log4j.Logger;

/**
 *
 * @author anuragg
 */
public class CommonLogger {
    /**
     * This methods prints the stack trace of generated exceptions
     * @param customMessage : is the error message to be shown
     * @param exception : is the exception object caught or thrown
     */   
    public static void printExceptions(Object obj, String customMessage,Exception exception){
       Logger logger = Logger.getLogger(obj.getClass().getPackage().getName());
       logger.error(customMessage + exception);            
       StringWriter swt = new StringWriter();
       exception.printStackTrace(new PrintWriter(swt));
       logger.error(swt.toString());
    }  
}
