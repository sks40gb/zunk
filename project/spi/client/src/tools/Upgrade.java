/* $Header: /home/common/cvsarea/ibase/dia/src/tools/Upgrade.java,v 1.8 2004/03/27 20:27:02 weaston Exp $ */

package tools;

import com.lexpar.util.Log;
import ui.DownloadFrame;
import tools.JniSystem;
import client.DownloadUpgrade;
import client.Global;
import client.ServerConnection;


//import java.util.Properties;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
//import java.lang.reflect.InvocationTargetException;
import java.io.File;
//import javax.swing.JFrame;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarFile;
//import javax.swing.JOptionPane;
//import javax.swing.UIManager;
//import javax.swing.SwingUtilities;

public class Upgrade {

    public static void run(String userName, String password, boolean admin) {
        try {

            // Dialog to inform user - a JFrame
            final DownloadFrame frame = new DownloadFrame(admin);
            frame.setText("DIA is about to download an upgraded version of the DIA program."
                             +"  This may take a few minutes."
                             +"\n\nIF THERE ARE ANY DIA WINDOWS OPEN, PLEASE CLOSE THEM NOW."
                             +"  Then, click \"OK\" to proceed."
                             +"\n\nAfter the download is complete, the DIA program"
                             +" will be restarted automatically.");

            frame.pack();
            final Thread downloadThread = new Downloader(
                        frame,Global.theServerConnection,userName,password, admin);
            ActionListener listener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        frame.clearOkListener(this);
                        downloadThread.start();
                    }
                };
            frame.setOkListener(listener);
            frame.setVisible(true);
        } catch (Throwable e) {
            Log.quit(e);
        }
    }

    private static class Downloader extends Thread {

        DownloadFrame frame;          
        ServerConnection scon;
        String userName;
        String password;
        boolean admin;

        Downloader(DownloadFrame frame,
                   ServerConnection scon,
                   String userName,
                   String password,
                   boolean admin)
        {
            this.frame = frame;
            this.scon = scon;
            this.userName = userName;
            this.password = password;
            this.admin = admin;
        }

        public void run() {

            boolean isNT;
            String osName = System.getProperty("os.name");
            if ("Windows Me".equalsIgnoreCase(osName)
                || "Windows 98".equals(osName)
                || "Windows 95".equals(osName))
            {
                isNT = false;
            } else {
                isNT = true;
            }
            Log.write("OS='"+osName+"' -- isNT="+isNT);

            try {
                // called from GUI - we already have a server connection
                frame.setText("DIA is downloading an upgraded version of the DIA program."
                             +"  This may take a few minutes.");

                // download changed files
                DownloadUpgrade.run(frame);
                // shutdown connection, with restart indication
                scon.shutdown(/* restart => */ true);


                // copy files and classes used by download, to avoid having them in use during rsync
                // note ssh and rsync and related are not copied
                // take classes from newly downloaded .jar file, if there is one
                File diaJarFile = new File("dia.jar~~");
                if (! diaJarFile.exists()) {
                    Log.print("using old jar");
                    diaJarFile = new File("dia.jar");
                } else {
                    Log.print("using new jar");
                }
                JarFile diaJar = new JarFile(diaJarFile);
                (new File("common")).mkdirs();
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("common/FileProperties.class")),
                                "common/FileProperties.class");
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("common/Log$1.class")),
                                "common/Log$1.class");
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("common/Log.class")),
                                "common/Log.class");
                (new File("tools")).mkdirs();
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("tools/JniSystem.class")),
                                "tools/JniSystem.class");
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("tools/Command.class")),
                                "tools/Command.class");
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("tools/CommandUtil.class")),
                                "tools/CommandUtil.class");
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("tools/LocalProperties.class")),
                                "tools/LocalProperties.class");
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("tools/ReadThread.class")),
                                "tools/ReadThread.class");
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("tools/Restart.class")),
                                "tools/Restart.class");
                (new File("ui")).mkdirs();
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("ui/DownloadFrame$1.class")),
                                "ui/DownloadFrame$1.class");
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("ui/DownloadFrame$2.class")),
                                "ui/DownloadFrame$2.class");
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("ui/DownloadFrame$3.class")),
                                "ui/DownloadFrame$3.class");
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("ui/DownloadFrame$4.class")),
                                "ui/DownloadFrame$4.class");
                writeFileFromStream(diaJar.getInputStream(diaJar.getEntry("ui/DownloadFrame.class")),
                                "ui/DownloadFrame.class");
                diaJar.close();


                // start the install & restart process and exit
                // -cp . is necessary in case they have a CLASSPATH env variable
                // old alpha version may have left over comma on user name -- temporary
                userName = userName.trim();
                if (userName.length() > 0 && userName.endsWith(",")) {
                    userName = userName.substring(0,userName.length()-1);
                }
                String command;
                command = "runjavaw -cp . tools.Restart"
                    +(admin ? " --admin" : "")
                    +" "+userName
                    +" "+password;
                Log.print("Restart command: "+command);

                try {
                    Log.print("Restart process starting");
                    Log.print(command);
                    if (isNT) {
                        System.out.println("starting on NT");
                        Runtime.getRuntime().exec(command);
                    } else {
                        System.out.println("starting on Windows");
                        new JniSystem().call(command);
                    }
                    Log.print("Restart process started");
                    System.exit(0);
                } catch (Throwable t1) {
                    Log.quit("Starting restart process:",t1);
                }
            } catch (Throwable t2) {
                Log.quit(t2);
            }
        }
    }

    private static byte[] buffer = null;

    private static void writeFileFromStream(InputStream inStream, String fileName) throws IOException {

        if (buffer == null) {
            buffer = new byte[16384];
        }

        int count;
        OutputStream outStream = new FileOutputStream(fileName);

        while ((count = inStream.read(buffer)) > 0) {
            outStream.write(buffer,0,count);
        }

        outStream.close();
        inStream.close();
    }
}



