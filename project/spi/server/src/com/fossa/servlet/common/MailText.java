/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

/**
 *
 * @author Bala
 */
/**
 * A container for data required for display of Mail.
 * 
 */
public class MailText {

    /** name of the mail recipient */
    public String recipientUserName;

    /** name of the mail sender */
    public String mailsentUserName;

    /** text of the recipient list */
    public String recipientList;

    /** Body of the mail */
    public String text;

    /** status of the mail can be 
     * 'Deleted','New','Unread','Read','Replied','Forwarded'
     */
    public String status;
}
