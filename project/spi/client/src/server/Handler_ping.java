/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_ping.java,v 1.6.8.1 2006/03/14 15:08:46 nancy Exp $ */
package server;

//import common.Log;
import common.msg.MessageWriter;

import org.w3c.dom.Element;

/**
 * Handler to reply to a ping from the client.
 */
final public class Handler_ping extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_ping() {}

    /** 
     * This handler is read-only.
     * @return true
     */
    public boolean isReadOnly() {
        return true;
    }

    public void run (ServerTask task, Element action)
    throws java.io.IOException {
        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_OK);
        writer.endElement();
    }
}
