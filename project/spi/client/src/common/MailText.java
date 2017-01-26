/* $Header: /home/common/cvsarea/ibase/dia/src/common/MailText.java,v 1.1.2.2 2006/03/22 20:27:15 nancy Exp $ */
package common;

/**
 * A container for data required for retrieval of mailsent or mailreceived text.
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
