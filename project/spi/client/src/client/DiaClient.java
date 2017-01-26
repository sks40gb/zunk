/* $Header: /home/common/cvsarea/ibase/dia/src/client/DiaClient.java,v 1.37.2.1 2006/03/09 12:09:16 nancy Exp $ */
package client;

import common.CommonProperties;
import common.Log;
import model.MailreceivedManagedModel;
import tools.DiaProperties;
import tools.LocalProperties;
import tools.Upgrade;
import ui.ActivitySelectionFrame;
import ui.AdminFrame;
import ui.ImageThread;
import ui.SplitPaneViewer;
import java.io.IOException;
import java.util.Arrays;
import javax.swing.SwingUtilities;

/**
 * Main program for DIA client.  Used for both viewer and admin applications.
 */
public class DiaClient {

    private static final String PROP_USER_NAME = "user_name";
    private static final String PROP_SERVER_IP = "server_ip";
    private static final String PROP_SERVER_PORT = "server_port";
    private static final String PROP_IMAGE_IP = "image_ip";
    private static final String PROP_IMAGE_PORT = "image_port";
    private static final String PROP_DEFAULT_SERVER_IP = "default_server_ip";
    private static final String PROP_DEFAULT_SERVER_PORT = "default_server_port";
    private static final String PROP_DEFAULT_IMAGE_IP = "default_image_ip";
    private static final String PROP_DEFAULT_IMAGE_PORT = "default_image_port";
    private static final String PROGRAM_VERSION = "1.00.054";
    private static final String ADMIN = "--admin";
    private static final String RESTART = "--restart";
    private static DiaLogin loginFrame;

    /**
     * Start the DIA client viewer or admin.
     * @param args The command Line arguments.
     *   <p>&nbsp;&nbsp;&nbsp;&nbsp;[--admin] [--restart user_name session_key]
     *   <p>where
     *   <p>&nbsp;&nbsp;&nbsp;&nbsp;--admin indicates that this is the administration app
     *   <p>&nbsp;&nbsp;&nbsp;&nbsp;--restart indicates that this is a restart after
     *       an upgrade.  The session key is a key uniquely generated for the
     *       current session, which allows logging in again for a short time.
     */
    final public static void main(String[] args) throws IOException {
        try {
            setLookandFeel();

            //String programVersion = DiaProperties.getProperty("program_version");
            String programVersion = PROGRAM_VERSION;

            // Show program and argument information in log file
            Log.print("DIA client version " + programVersion);
            Log.print("Java " + System.getProperty("java.vm.version") + " on " + System.getProperty("os.name") + " version " + System.getProperty("os.version"));
            for (int i = 0; i < args.length; i++) {
                Log.print("args[" + i + "]='" + args[i] + "'");
            }

            // Suppress print screen
            //  ClipboardWatcher.startClipboardWatcher();
            DiaLogin.Data loginData = new DiaLogin.Data();
            boolean isRestart = false;
            boolean admin = false;

            // process --admin argument
            int argPos = 0;
            if (argPos < args.length) {
                if (args[0].equals(ADMIN)) {
                    admin = true;
                    argPos++;
                }
            }

            // process --restart parameter
            if ((argPos + 2) < args.length && args[argPos].equals(RESTART)) {
                isRestart = true;
                loginData.userName = args[argPos + 1].trim();
                //// ancient alpha version may have left extra comma
                //if (loginData.userName.length() > 1 && loginData.userName.endsWith(",")) {
                //    loginData.userName = loginData.userName.substring(0,loginData.userName.length() - 1);
                //}
                loginData.password = args[argPos + 2].trim().toCharArray();
                loginData.newPassword = "";
                Log.print("restarting " + loginData.userName);
            }

            // Initialize login data from property file
            if (!isRestart) {
                loginData.userName = LocalProperties.getProperty(PROP_USER_NAME, "");
            }
            loginData.serverIP = LocalProperties.getProperty(PROP_SERVER_IP, DiaProperties.getProperty(PROP_DEFAULT_SERVER_IP));
            try {
                loginData.serverPort = Integer.parseInt(LocalProperties.getProperty(PROP_SERVER_PORT, DiaProperties.getProperty(PROP_DEFAULT_SERVER_PORT)));
            } catch (Exception e) {
                // ignore if there isn't one or if it is not numeric
            }
            loginData.imageIP = LocalProperties.getProperty(PROP_IMAGE_IP, DiaProperties.getProperty(PROP_DEFAULT_IMAGE_IP));
            try {
                loginData.imagePort = Integer.parseInt(LocalProperties.getProperty(PROP_IMAGE_PORT, DiaProperties.getProperty(PROP_DEFAULT_IMAGE_PORT)));
            } catch (Exception e) {
                // ignore if there isn't one or if it is not numeric
            }

            loginFrame = new DiaLogin(loginData, admin);
            loginFrame.setVisible(true);

            // Start initialization of application frame while user logs in 
            if (admin) {
                Thread initAdminThread = new Thread() {

                    public void run() {
                        try {
                            AdminFrame.getInstance(loginFrame);
                        } catch (Throwable t) {
                            Log.quit(t);
                        }
                    }
                };
                initAdminThread.setPriority(Thread.NORM_PRIORITY - 1);
                initAdminThread.start();
            } else {
                Thread initViewerThread = new Thread() {

                    public void run() {
                        try {
                            synchronized (SplitPaneViewer.class) {
                                // create an instance and put it on the free list
                                // synchronized -- so we'll get this one on the
                                // next call to getInstance
                                SplitPaneViewer.getInstance().free();
                            }
                        } catch (Throwable t) {
                            Log.quit(t);
                        }
                    }
                };
                initViewerThread.setPriority(Thread.NORM_PRIORITY - 1);
                initViewerThread.start();
            }

            for (;;) {

                if (isRestart) {
                    // use the restart data instead of waiting for user input
                    loginFrame.disable("Connecting to server.");
                } else {
                    // wait for user input and receive the input
                    loginData = loginFrame.getLoginData();
                }

                // Save the login data (will be stored after successful login)
                LocalProperties.setProperty(PROP_USER_NAME, loginData.userName);
                LocalProperties.setProperty(PROP_SERVER_IP, loginData.serverIP);
                CommonProperties.SERVER_IP = loginData.serverIP;

                //Try to create a server connection
                Global.theServerConnection = new ServerConnection();
                try {
                    Global.theServerConnection.connect(loginData.userName,
                            loginData.password,
                            admin,
                            isRestart,
                            loginData.newPassword);
                    isRestart = false;

                    // successful, now try the image connection (viewer only)
                    // do it here, since image IP is given by user

                    //below code will do the image server connection,since we are opening the image  
                    //by FileInputStream the below lines are commented
                    if (!admin) {
                        ImageThread.makeImageConnection();
                    }

                    // Store the login data
                    // Do it here, so that upgrade will have it
                    LocalProperties.storeFile();

                    // Check version.  If version less than min required, do download
                    String minVersion = Global.theServerConnection.getMinVersion();
                    String maxVersion = Global.theServerConnection.getMaxVersion();
                    Log.print("minVersion=" + minVersion + " maxVersion=" + maxVersion);
                    if (programVersion.compareTo(minVersion) < 0) {
                        // close the image connection gently
                        if (client.Global.theImageConnection != null) {
                            try {
                                client.Global.theImageConnection.shutdown();
                            } catch (Exception e) {
                                // ignore any failure
                            }
                        }
                        // TODO: must use a one-time capability instead of the password !!!!!!!
                        Upgrade.run(loginData.userName, Global.theServerConnection.getSessionKey(), admin);
                        loginFrame.setVisible(false);
                        // leave GUI running -- DownloadUpgrade will kill it (With System.exit())
                        return;
                    }
                    // Make sure version is allowed (e.g., not test version and live data)
                    if (programVersion.compareTo(maxVersion) > 0) {
                        throw new FailException("Invalid program version: " + programVersion);
                    }
                    // end the login loop
                    break;
                } catch (FailException e) {
                    isRestart = false;
                    // failed to connect -  clean up and tell user
                    Arrays.fill(loginData.password, '\u0000');
                    loginData.password = new char[0];
                    loginData.newPassword = "";
                    Global.theServerConnection.shutdown();
                    loginFrame.enableAfterFailure(e.getMessage());
                } catch (IOException e) {
                    isRestart = false;
                    e.printStackTrace();
                    // failed to connect -  clean up and tell user
                    Arrays.fill(loginData.password, '\u0000');
                    loginData.password = new char[0];
                    loginData.newPassword = "";
                    try {
                        Global.theServerConnection.shutdown();
                    } catch (Throwable ex) {
                    }
                    loginFrame.enableAfterFailure(e.getMessage());
                }

            }

            Arrays.fill(loginData.password, '\u0000');
            loginData.password = new char[0];
            // TODO: handle changing passwords
            loginData.newPassword = null;

            // Start a client thread
            ClientThread.startInstance(admin);

            // Start the application
            // Note.  loginFrame is passed so new window can close it
            Log.write("start application");
            final boolean finalIsAdmin = admin;
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    try {
                        if (finalIsAdmin) {
                            AdminFrame.getInstance(loginFrame).setVisible(true);
                        } else {
                            new ActivitySelectionFrame(loginFrame).setVisible(true);
                        }

                        /**
                         * The model with TableModelListener for the mailPanel, placed
                         * here because it's after the ClientThread is started.
                         */
                        MailreceivedManagedModel.instantiate();
                    } catch (Throwable t) {
                        Log.quit(t);
                    }
                }
            });

        } catch (Throwable t) {
            Log.quit(t);
        }
    }

    private static void setLookandFeel() {
        try {
            // force Windows look and feel
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e) {
            Log.quit(e);
        }
    }
}
