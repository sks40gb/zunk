/* $Header: /home/common/cvsarea/ibase/dia/src/tools/Restart.java,v 1.15.4.1 2006/03/30 12:28:55 nancy Exp $ */

package tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.SecureRandom;
import ui.DownloadFrame;
import tools.JniSystem;
import tools.LocalProperties;
import tools.CommandUtil;
             

import java.text.SimpleDateFormat;
import java.text.FieldPosition;
import java.util.Arrays;
import java.util.Date;
import java.util.Properties;
import javax.swing.UIManager;


/**
 * Program to rename updated files and restart the DIA GUI.
 * Called as a new process after required class files are
 *   copied to a temporary files which are NOT overwritten by the renaming.
 */
public class Restart {

    final private static int SEED_BYTES = 20;

    private static int errorCount = 0;
    private static PrintWriter logWriter = null;

    private static DownloadFrame frame = null;

    private static String logDateTime = null;

    final private static String LOG_NAME = "upgrade.log";


    /**
     * Main program for the installation and restart.
     * <p> Parameters:
     * <p> -- 0: user name -- null if called fron standalone rather than DCS program
     * <p> -- 1: password
     * <p> If the download is not successful, old files are restored from the backups.
     * <p> If the download is successful and a user name and passwords were provided,
     *   DIA is restarted and automatically logs in with the given user name.
     * <p> Messages are logged to upgrade.log
     */
    final public static void main(String[] args) throws IOException {
        try {
            main_aux(args);
        } catch (Throwable t) {
            writeLog("Failed: " + t);
            t.printStackTrace(logWriter);

            if (args.length >= 2
            && (args[0].endsWith("DOSSC") || args[1].endsWith("DOSSC"))) {
                System.out.println("Courtney: You are in restart program");
                System.out.println("Courtney: Press any key to exit program");
                System.in.read();
            }
            System.exit(1);
        }
    }

    final private static void main_aux(String[] args) throws Exception {

        boolean isNT;

        try {
            // force Windows look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        {
            // start the log
            SimpleDateFormat nameFormatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
            logDateTime = nameFormatter.format( new Date()
                                                     , new StringBuffer(19)
                                                     , new FieldPosition(0))
                                            .toString();
            writeLog("Starting install and restart " + logDateTime);

            String osName = System.getProperty("os.name");
            if ("Windows Me".equalsIgnoreCase(osName)
                || "Windows 98".equals(osName)
                || "Windows 95".equals(osName))
            {
                isNT = false;
            } else {
                isNT = true;
            }
            writeLog("OS='"+osName+"' -- isNT="+isNT);
        }

        boolean admin = false;
        int argPos = 0;
        if (args[0].equals("--admin")) {
            admin = true;
            argPos = 1;
        }


        // create a dialog to inform the user of the install and restart
        frame = new DownloadFrame(admin);
        frame.setText("DIA is downloading an upgraded version of the DIA program."
                     +"  This may take a few minutes."
                     +"\n\nInstalling downloaded files.");
        frame.clearOkListener(null);
        frame.setVisible(true);

        // fetch the arguments
        String userName = args[argPos];
        String password = args[argPos + 1];
        try {
            // wait 2 sec. to give caller a chance to quit
            // (probably not needed)
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            writeLog("sleep interrupted");
        }

        // install downloaded files by removing "~~" from end of names
        frame.setExitEnabled(false);
        renameDownloadedFiles(".");

        // check for failure
        if (errorCount > 0) {
            frame.setExitEnabled(true);
            frame.setText("FAILED INSTALLING DOWNLOADED FILES"
                          +"\n\nSee upgrade.log for details."
                          +"\n\nPlease reboot the computer and try again.");
            writeLog("==== Restoring files after failure");
            restoreBackupFiles(".");
            closeLog();
            return;
        }

        // rewrite jexpress/...properties files
        // version 0.01.056 and before to be changed
        rewriteProperties("jexpress/DIAAdmin.properties");
        rewriteProperties("jexpress/i-BaseDIA.properties");

        // generate a random seed
        frame.setText("DIA is downloading an upgraded version of the DIA program."
                     +"  This may take a few minutes."
                     +"\n\nSetting up for encrypted data transmission.  This may"
                     +" take as long as a minute, depending on the speed of your"
                     +" computer.");
        generateRandomSeed();

        frame.setText("DIA is downloading an upgraded version of the DIA program."
                     +"  This may take a few minutes."
                     +"\n\nDeleting temporary files.");
        frame.setExitEnabled(true);

        // delete temporary backups
        deleteBackupFiles(".");

        // delete unused files
        deleteUnusedFiles();

        // delete temporary class files (all classes used should have been loaded)
        if (isNT) {
            Class.forName("tools.JniSystem"); // load this class before deleting
        }
        (new File ("common/FileProperties.class")).delete();
        (new File ("common/Log$1.class")).delete();
        (new File ("common/Log.class")).delete();
        (new File ("rcap/JniSystem.class")).delete(); // TEMPORARY
        (new File ("tools/JniSystem.class")).delete();
        (new File ("tools/Command.class")).delete();
        (new File ("tools/CommandUtil.class")).delete();
        (new File ("tools/LocalProperties.class")).delete();
        (new File ("tools/ReadThread.class")).delete();
        (new File ("tools/Restart.class")).delete();
        (new File ("ui/DownloadFrame$1.class")).delete();
        (new File ("ui/DownloadFrame$2.class")).delete();
        (new File ("ui/DownloadFrame$3.class")).delete();
        (new File ("ui/DownloadFrame$4.class")).delete();
        (new File ("ui/DownloadFrame.class")).delete();
        (new File ("common")).delete();
        (new File ("rcap")).delete(); // TEMPORARY
        (new File ("tools")).delete();
        (new File ("ui")).delete();

        // Call DIA to start new instance, but do NOT wait for process to return
        try {
            StringBuffer buffer = new StringBuffer(80);
            buffer.append("i-BaseDIA.exe ");
            if (admin) {
                buffer.append("--admin ");
            }
            buffer.append("--restart ");
            buffer.append(userName);
            buffer.append(" ");
            buffer.append(password);
            String commandLine = buffer.toString();
            writeLog("restart command: "+commandLine);

            if (args.length >= 2
            && (args[0].endsWith("DOSSC") || args[1].endsWith("DOSSC"))) {
                System.out.println("Courtney: Press any key to continue");
                System.in.read();
            }
            if (isNT) {
                Process dummy = Runtime.getRuntime().exec(commandLine);
            } else {
                new JniSystem().call(commandLine);
            }
        } catch (Exception e) {
            writeLog("Restarting DIA: "+e);
            frame.setText("FAILED RESTARTING DIA"
                           +"\n\nSee upgrade.log for details."
                           +"\n\nPlease reboot and try again");
            if (args.length >= 2
            && (args[0].endsWith("DOSSC") || args[1].endsWith("DOSSC"))) {
                System.out.println("Courtney: Press any key to exit program");
                System.in.read();
            }
        }
        closeLog();
        System.exit(0);
    }

    private static void renameDownloadedFiles(String directory) {
        File dirFile = new File(directory);
        String[] members = dirFile.list();
        String memberName;
        for (int i = 0; i < members.length; i++) {
            memberName = members[i];
            if (memberName.endsWith("~~") && ! memberName.endsWith("~~~")) {
                String realName = memberName.substring(0, memberName.length() - 2);
                File realFile = new File(dirFile, realName);
                File newFile = new File(dirFile, memberName);
                if (filesAreEqual(realFile, newFile)) {
                    writeLog("skipping "+directory+"/"+realName);
                    realFile.setLastModified(newFile.lastModified());
                } else {
                    writeLog("installing "+directory+"/"+realName);
                    boolean result = realFile.renameTo(new File(directory, realName + "~"));
                    if (!result) {
                        writeLog("(OK) failed renaming "+directory+"/"+realName+" to "+realName+"~");
                    }
                    result = newFile.renameTo(realFile);
                    if (!result) {
                        errorCount++;
                        writeLog("failed renaming "+directory+"/"+memberName+" to "+realName);
                    }
                }
            }
        }
        for (int i = 0; i < members.length; i++) {
            memberName = members[i];
            if ((new File(directory, memberName)).isDirectory()) {
                renameDownloadedFiles(directory+'/'+memberName);
            }
        }
    }


    private static boolean filesAreEqual(File file1, File file2) {
        try {    
            final long length1 = file1.length();
            final long length2 = file2.length();
            if (length1 != length2 || length2 == 0 || length2 > Integer.MAX_VALUE) {
                // Note.  Treat length 0 as unequal, in case file doesn't exist
                return false;
            }
            byte[] buffer1 = readFully(file1, (int) length1);
            byte[] buffer2 = readFully(file2, (int) length1);
            return Arrays.equals(buffer1, buffer2);
        } catch (IOException e) {
            writeLog("in filesAreEqual: " + e);
            return false;
        }
    }


    private static byte[] readFully(File f, int len) throws IOException {
        InputStream stream = new FileInputStream(f);
        byte[] buffer = new byte[len];
        int pos = 0;
        int count;
        while (pos < len) {
            count = stream.read(buffer, pos, len - pos);
            if (count <= 0) {
                throw new IOException("File size incorrect");
            }
            pos += count;
        }
        stream.close();
        return buffer;
    }

    // replace the "args" property for the installer
    // (this is to replace the classpath)
    private static void rewriteProperties(String fileName) {

        final String NEW_ARGS
                      = "-enableassertions"
                        +" -cp .;dia.jar;files\\\\VTJBean.jar;files\\\\ssce.jar;ssce"
                        +" client.DiaClient";

        Properties pr = new Properties();
        try {
            InputStream stream = new BufferedInputStream(new FileInputStream(fileName));
            pr.load(stream);
            stream.close();
        } catch (Exception e) {
            writeLog("Fail reading jexpress properties: "+e);
            return;
        }
        String args = pr.getProperty("args","");
        if (! NEW_ARGS.equals(args)) {
            pr.setProperty("args", NEW_ARGS);
            try {
                OutputStream stream = new BufferedOutputStream(new FileOutputStream(fileName));
                pr.store(stream, "Rewritten by Restart.java");
                stream.close();
            } catch (Exception e) {
                writeLog("Fail rewriting jexpress properties: "+e);
            }
        }
    }


    private static void deleteBackupFiles(String directory) {
        File dirFile = new File(directory);
        String[] members = dirFile.list();
        String memberName;
        for (int i = 0; i < members.length; i++) {
            memberName = members[i];
            if (memberName.endsWith("~")) {
                boolean result = (new File(dirFile, memberName)).delete();
                if (!result) {
                    errorCount++;
                    writeLog("failed deleting: "+directory+"/"+memberName);
                }
            }
        }
        for (int i = 0; i < members.length; i++) {
            memberName = members[i];
            if ((new File(directory, memberName)).isDirectory()) {
                deleteBackupFiles(directory+'/'+memberName);
            }
        }
    }


    private static void restoreBackupFiles(String directory) {
        File dirFile = new File(directory);
        String[] members = dirFile.list();
        String memberName;
        for (int i = 0; i < members.length; i++) {
            memberName = members[i];
            if (memberName.endsWith("~") && ! memberName.endsWith("~~")) {
                String realName = memberName.substring(0,memberName.length() - 1);
                writeLog("restoring: "+directory+"/"+realName);
                File realFile = new File(dirFile, realName);
                File downloadFile = new File(dirFile, realName+"~~~");
                downloadFile.delete();
                boolean result = realFile.renameTo(downloadFile);
                if (!result) {
                    writeLog("failed renaming: "+directory+"/"+realName+" to "+realName+"~~~");
                }
                result = (new File(dirFile, memberName)).renameTo(realFile);
                if (!result) {
                    writeLog("failed renaming: "+directory+"/"+memberName+" to "+realName);
                }
            }
        }
        for (int i = 0; i < members.length; i++) {
            memberName = members[i];
            if ((new File(directory, memberName)).isDirectory()) {
                restoreBackupFiles(directory+'/'+memberName);
            }
        }
    }


    private static void writeLog (String msg) {
        try {
            System.err.println(msg);
            if (logWriter == null) {
                FileWriter fileWriter = new FileWriter(LOG_NAME, true);
                logWriter = new PrintWriter(fileWriter, true);
                logWriter.println();
            }
            logWriter.println(msg);
            logWriter.flush();
        } catch (IOException e) {
            if (frame != null) {
                frame.setText("Error writing upgrade.log: " + e + " -- "+ msg);
            }
            System.err.println("Error writing upgrade.log: " + e);
            System.err.println(msg);
            //downloadFailed();
        }
    }

    private static void closeLog() {
        if (logWriter != null) {
            logWriter.close();
            logWriter = null;
        }
    }


    public static void generateRandomSeed () {
        
        // create a 160-bit seed
        SecureRandom sr0 = new SecureRandom();
        byte[] seed = sr0.generateSeed(SEED_BYTES);

        // make a string representation
        StringBuffer buffer = new StringBuffer(SEED_BYTES * 3);
        for (int i = 0; i < seed.length; i++) {
            buffer.append(',');
            int seedByte = seed[i] & 0xFF;
            if (seedByte < 16) {
                buffer.append('0');
            }
            buffer.append(Integer.toHexString(seedByte));
        }
        
        // save it in local.properties (Note: remove leading comma)
        LocalProperties.setProperty("random_seed", buffer.substring(1));
    }


    // delete files from previous versions that are no longer used
    private static void deleteUnusedFiles() {
        //// Install compatibility database if this is Windows XP
        //if (isXP) {
        //    CommandUtil.doSystemCommand("Sdbinst -q DIAFIX.sdb");
        //}
    }
}
