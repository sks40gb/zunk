/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendPageIssue.java,v 1.6.6.1 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called from SplitPaneViewer to send pageissue updates to the
 * server for storage.  <code>pageissue</code>s can be resequenced, inserted or
 * updated.
 * @see server.Handler_pageissue
 */
public class TaskSendPageIssue extends ClientTask {

    private int pageId = 0;
    private int sequence = 0;
    private int direction = 0;
    private String issue = "";
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class that inserts a new <code>pageissue</code>
     * row and remember the parameters.
     * @param pageId page_id of the pageissue to update or insert
     * @param sequence 0 if insert, otherwise the sequence of the pageissue to update
     * @param issue text to insert or update in the pageissue row
     */
    public TaskSendPageIssue(int pageId, int sequence, String issue) {
        this(pageId, sequence, 0, issue);
    }

    /**
     * Create an instance of this class that moves a <code>pageissue</code>
     * up or down and remember the parameters.
     * @param pageId page_id of the pageissue to update or insert
     * @param sequence 0 if insert, otherwise the sequence of the pageissue to update
     * @param direction -1 if move up; 1 if move down
     */
    public TaskSendPageIssue(int pageId, int sequence, int direction) {
        this(pageId, sequence, direction, "");
    }

    /**
     * Create an instance of this class and remember the parameters.
     * @param pageId page_id of the pageissue to update or insert
     * @param sequence 0 if insert, otherwise the sequence of the pageissue to update
     * @param direction -1 if move up; 1 if move down
     * @param issue text to insert or update in the pageissue row
     */
    public TaskSendPageIssue(int pageId, int sequence, int direction, String issue) {
        this.pageId = pageId;
        this.sequence = sequence;
        this.direction = direction;
        this.issue = issue;
    }

    /**
     * Write the message with attributes and set the result.
     * Message should be in XML format. 
     */
    @Override
    public void run() throws IOException {
        MessageWriter writer = scon.startMessage(T_PAGEISSUE);

        if (pageId > 0) {
            writer.writeAttribute(A_PAGE_ID, pageId);
        }
        if (sequence > 0) {
            writer.writeAttribute(A_SEQUENCE, sequence);
        }
        if (direction != 0) {
            writer.writeAttribute(A_DELTA, direction);
        }
        if (!issue.equals("")) {
            writer.writeAttribute(A_ISSUE, issue);
        }
        addStandardAttributes(writer);
        writer.endElement();
        writer.close();
        //Log.print("(TaskSendPageIssue.run) " + pageId + "/" + sequence + "/" + issue);

        Element reply = scon.receiveMessage();

        String ok = reply.getNodeName();
        //Log.print("T_PAGE_ISSUE reply " + ok);
        setResult((Object) reply);

        if (!T_OK.equals(ok) && !T_FAIL.equals(ok)) {
            Log.quit("SendPageIssue unexpected message type: " + ok);
        }
    }
}
