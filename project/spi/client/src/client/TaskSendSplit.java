/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskSendSplit.java,v 1.2.8.1 2006/03/22 20:27:15 nancy Exp $ */
package client;

import common.Log;
import common.msg.MessageWriter;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask called from <code>ui.SplitPaneViewer</code> to request that a
 * document be split.
 * @see server.Handler_page_split
 * @see ui.SplitPaneViewer
 */
public class TaskSendSplit extends ClientTask {

    private int pageId;
    private boolean isClone;
    private boolean isUnsplit;
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this class and remember the parameters.
     * @param pageId the <code>page.page_id</code> to be split
     * @param isClone true to clone is the split document does not begin at
     * the top of the given page
     * @param isUnsplit to reverse a split document
     */
    public TaskSendSplit(int pageId, boolean isClone, boolean isUnsplit) {
        this.pageId = pageId;
        this.isClone = isClone;
        this.isUnsplit = isUnsplit;
    }

    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_PAGE_SPLIT);
        writer.writeAttribute(A_PAGE_ID, pageId);
        addStandardAttributes(writer);
        if (isUnsplit) {
            writer.writeAttribute(A_UNSPLIT, "YES");
        } else if (isClone) {
            writer.writeAttribute(A_CLONE, "YES");
        }
        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
        String ok = reply.getNodeName();
        if (!T_OK.equals(ok)) {
            Log.quit("TaskSendSplit unexpected message type: " + ok);
        }

        // attribute "page_id" is page to be displayed
        setResult(reply);
    }
}