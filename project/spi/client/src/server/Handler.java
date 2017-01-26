/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler.java,v 1.11.8.1 2006/03/21 16:42:41 nancy Exp $ */

package server;

//import common.Log;
import common.msg.MessageConstants;

import java.io.IOException;
import java.sql.SQLException;
import org.w3c.dom.Element;

/**
 * The common interface for all server message handler classes.
 */
public abstract class Handler implements MessageConstants {

    /**
     * Execute the handler.
     * @param task current ServerTask to handle the connection from
     * the calling client to the coding server
     * @param action An action node from a message.
     */
    public abstract void run (ServerTask task, Element action)
    throws ServerFailException, GoodbyeException, IOException, SQLException;

    /**
     * Determine if this Handler is read-only.  Subclasses should
     * override this to return true, if they are read-only.  Read-only
     * handlers are executed as TRANSACTION_REPEATABLE_READ, while non-read-only
     * handlers are executed as TRANSACTION_SERIALIZABLE.
     * @return true if this Handler is read-only.
     */
    public boolean isReadOnly() {
        return false;
    }
}
