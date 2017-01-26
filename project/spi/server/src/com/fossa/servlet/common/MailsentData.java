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
 * A container for data required for update and retrieval of mail.
 */
public class MailsentData {

    /** Id of the mailsent row */
    public int mailsentId;

    /** text of the recipient list */
    public String recipientList;

    /** Subject line of the mail */
    public String subject;

    /** Body of the mail */
    public String text;
}

