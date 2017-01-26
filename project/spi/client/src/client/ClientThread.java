/* $Header: /home/common/cvsarea/ibase/dia/src/client/ClientThread.java,v 1.18 2004/04/15 14:33:42 weaston Exp $ */

/*
 * ClientThread.java
 */
package client;

import com.lexpar.util.Log;
import common.msg.MessageConstants;
import java.net.URLConnection;
import model.SQLManagedTableModel;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.Window;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.w3c.dom.Element;

/**
 * Thread to run client tasks.
 *
 * @author  Bill
 */
public class ClientThread extends Thread implements MessageConstants {

    private static ClientThread theClientThread;
    final private static int CODER_TIMEOUT = 120000;  // ping server every 2 min

    final private static int ADMIN_TIMEOUT = 20000;  // ping server every 20 sec

    private int timeout;
    //server connection.
    URLConnection urlConnection;
    // The currently running client task.
    private ClientTask currentTask;

    // The list of client tasks to be run.
    // To cause a task to be executed, add the task reference the queue,
    // then notify this ClientThread.
    private LinkedList pendingTaskList = new LinkedList();

    /**
     * Return the instance of ClientThread.
     * If necessary, one is created. 
     */
    public static ClientThread startInstance(boolean admin) {
        if (theClientThread == null) {
            theClientThread = new ClientThread(admin);
            theClientThread.start();
        }
        return theClientThread;
    }

    /**
     * Set the thread time out on the basis of user type
     * If the user is Admin then time out would be <code>ADMIN_TIMEOUT</code>
     * else <code>CODER_TIMEOUT</code>
     * @param admin User type
     */
    private ClientThread(boolean admin) {
        super();
        timeout = (admin ? ADMIN_TIMEOUT : CODER_TIMEOUT);
    }

    /**
     * Run client tasks.  Loop, waiting for a non-null
     * pendingTask, then run the task; when the task completes,
     * continue waiting.
     */
    public void run() {
        try {
            for (;;) {
                synchronized (this) {
                    if (pendingTaskList.size() > 0) {
                        currentTask = (ClientTask) pendingTaskList.removeFirst();
                    } else {
                        try {
                            this.wait(timeout);
                        } catch (InterruptedException e) {
                            Log.print("InterruptedException in ClientThread.run");
                        }
                    }
                }

                if (currentTask != null) {
                    // run the task
                    try {
                        // RUN THE CLIENT TASK
                        currentTask.run();
                        // run callback on ED thread
                        Runnable callback = currentTask.getCallback();
                        currentTask = null;
                        if (callback != null) {
                            SwingUtilities.invokeLater(callback);
                        }
                    } catch (final FailException fe) {
                        // Failed, quit the task without callback
                        final ClientTask failedTask = currentTask;
                        currentTask = null;
                        SwingUtilities.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                // If window is showing, move it to the front
                                // Otherwise, show dialog without a parent
                                Component parent = failedTask.getParent();
                                if (parent != null) {
                                    Window failedWindow = SwingUtilities.getWindowAncestor(parent);
                                    if (failedWindow == null || !failedWindow.isShowing()) {
                                        parent = null;
                                    } else {
                                        failedWindow.toFront();
                                    }
                                }
                                JOptionPane.showMessageDialog(parent,
                                        fe.getMessage(),
                                        "Server-detected Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                        });
                    }
                } else if (pendingTaskList.size() == 0) {
                    // must have timed out, ping the server                                       
                    client.Global.theServerConnection.sendMessage(T_PING);
                    Element reply = client.Global.theServerConnection.receiveMessage();

                    //code added ended        
                    if (!T_OK.equals(reply.getNodeName())) {
                        Log.quit("Invalid reply pinging coding server: " + reply.getNodeName());
                    // System.out.println("Invalid reply pinging coding server: "+ result);
                    }
                }
                SQLManagedTableModel.checkForUpdates();
            }
        } catch (SocketTimeoutException ste) {
            Global.theServerConnection.disconnect("Disconnected: " + ste.toString());
        } catch (ConnectException conexc) {
            Global.theServerConnection.disconnect("Disconnected: " + conexc.toString());
        } catch (SocketException se) {
            Global.theServerConnection.disconnect("Disconnected: " + se.toString());
        } catch (IOException ioe) {
            Global.theServerConnection.disconnect("Disconnected: " + ioe.toString());
        } catch (FailException fexc) {
            Global.theServerConnection.disconnect("Disconnected: " + fexc.toString());
        } catch (Throwable th) {
            Log.quit(th);
        }

    }

    // max number of tasks to enqueue at a time
    // TBD: This is a Band-Aid.  Need to limit user requests but maybe not other requests.
    final private static int MAX_QUEUE_SIZE = 50;

    /**
     * Enqueue a ClientTask to be run.
     * If there are already too many tasks queued, 
     * notify the user with a beep;
     * otherwise, make the given task the pending task.
     * (Called only from ClientTask.enqueue)
     * @param parent The parent component (for positioning error dialogs)
     * @param task The task to be run.
     * @return true, if the task was enqueued; otherwise false.
     */
    static boolean enqueue(ClientTask task) {
        synchronized (theClientThread) {
            if (theClientThread.pendingTaskList.size() < MAX_QUEUE_SIZE) {
                theClientThread.pendingTaskList.add(task);
                theClientThread.notify();
                return true;
            } else {
                Toolkit.getDefaultToolkit().beep();
                Log.print("BEEP> Users.enqueue: queue length = " + theClientThread.pendingTaskList.size());
                Log.quit("****** too many queued tasks");
                return false;
            }
        }
    }
}
