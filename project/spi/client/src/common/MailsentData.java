/* $Header: /home/common/cvsarea/ibase/dia/src/common/MailsentData.java,v 1.2.6.1 2005/11/11 15:13:58 nancy Exp $ */
package common;

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
