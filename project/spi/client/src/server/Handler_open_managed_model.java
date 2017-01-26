/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_open_managed_model.java,v 1.8.8.1 2006/03/14 15:08:46 nancy Exp $ */

package server;

//import common.CodingData;
//import common.ImageData;
//import common.Log;
//import common.msg.MessageWriter;

//import java.sql.ResultSet;
//import java.sql.Statement;
import org.w3c.dom.Element;

/**
 * Handler for open_managed_table message.
 */
final public class Handler_open_managed_model extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_open_managed_model() {}

    public void run (ServerTask task, Element action) {
        //Log.print("in Handler_open_managedModel.run "+action);
        String name = action.getAttribute(A_NAME);
        ManagedModelPeer modelPeer = new ManagedModelPeer(task,name);
    }

    public boolean isReadOnly() {
        return true;
    }
}
