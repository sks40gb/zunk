/* $Header: /home/common/cvsarea/ibase/dia/src/client/Global.java,v 1.11.8.1 2006/03/09 12:09:16 nancy Exp $ */
package client;

import java.awt.Window;

/**
 * Global is a container for the singleton instances of ServerConnection and
 * ImageConnection, which are used by both viewer and admin to retrieve and
 * store information from the server.
 * @see client.ImageConnection
 * @see client.ServerConnection
 */
public class Global {

    /**
     * The instance of server connection used by all tasks.
     */
    public static ServerConnection theServerConnection = null;
    /**
     * The instance of image connection used by all tasks.
     */
    public static ImageConnection theImageConnection = null;
    /**
     * The window to be used for error messages, when there is no specific
     * window given.  For admin, this is the admin window; for viewer, this
     * is highest level visible window, other than the Binder window.
     */
    public static Window mainWindow = null;
}
