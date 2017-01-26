/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_request_projectfields.java,v 1.4.8.2 2006/03/21 16:42:41 nancy Exp $ */
package server;

//import common.CodingData;
import common.Log;
import common.edit.ProjectWriter;
import common.msg.MessageWriter;

import org.w3c.dom.Element;

/**
 * Handler for request_projectfields message.  This Handler calls
 * <code>common.edit.ProjectWriter</code> to read projectfield data
 * from the database for the given project or locked project, then
 * to write the data to this task's XML message.
 * @see common.edit.ProjectWriter
 */
final public class Handler_request_projectfields extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_request_projectfields() {}

    /** 
     * This handler is read-only.
     * @return true
     */
    public boolean isReadOnly() {
        return true;
    }

    public void run (ServerTask task, Element action)
    throws java.io.IOException {
        Log.print("(Handler_request_projectfields).run");
        
        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_PROJECTFIELDS_DATA);
        int volumeId = task.getVolumeId();
        Log.print("Handler_request_projectfields: v="+volumeId+" lv="+task.getLockVolumeId());
        ProjectWriter.write(task, volumeId);
        writer.endElement();  // end message
    }
}
