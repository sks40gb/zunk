/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_shutdown.java,v 1.5.8.1 2006/03/14 15:08:47 nancy Exp $ */
package server;

//import common.Log;
import common.msg.MessageWriter;

import org.w3c.dom.Element;

/**
 * Handler to reply to a shutdown from the client.
 */
final public class Handler_shutdown extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_shutdown() {}

    public void run (ServerTask task, Element action)
    throws java.io.IOException {
        DiaListener.shutdown();

        task.commitTransaction();

        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_OK);
        writer.endElement();
    }
}
