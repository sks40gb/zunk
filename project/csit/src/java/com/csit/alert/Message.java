/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.csit.alert;

/**
 *
 * @author sunil
 */
public class Message {

    private static String message;

    public static String getMessage() {
        return message == null ? "" : message;
    }

    public static void setMessage(String message) {
        Message.message = message;
    }

}
