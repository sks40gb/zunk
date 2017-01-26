/* $Header: /home/common/cvsarea/ibase/dia/src/common/Log.java,v 1.21.6.1 2006/02/16 15:56:45 nancy Exp $ */
 
package common;


import common.CommonProperties;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;


/**
 * General logging facilities.  Methods are provided to write to the
 * console or a log file, with a timestamp.  Methods are also provided
 * for handling fatal errors; optionally, these may throw an exception
 * instead of terminating, to allow per-thread errors to be handled on
 * the server.
 */
public class Log {

    // If existing log file is larger than this, we rename and create new one
    final private static long MAX_INITIAL_LOG_SIZE = 500000;

    private static SimpleDateFormat stampFormatter
                     = new SimpleDateFormat ("MMdd.HH:mm:ss");
    private static PrintWriter writer = null;
    private static Date now = null;
    private static StringBuffer buffer = new StringBuffer();
    private static String logFileName =  CommonProperties.RUN;
    private static JFrame programFrame = null;

    // The declared quit exceptions
    // (When not null, this is the exception that is created and thrown on quit.)
    private static Class quitExceptionClass = null;
    // Per-thread storage of task numbers
    // (For automatic printing of task numbers in log messages)
    final private static ThreadLocal taskNumber = new ThreadLocal();
    // When true AND a quitException has been specified, printing suppressed
    // (Used to allow test drivers to test failures without the wallpaper effect.)
    private static boolean silent = false;

    /**
     * Accept new log file name.
     * Log file will be placed in directory containing subdirectory bin.
     * This should be in the classpath.  If there is no such, the log
     * file is placed in the current working directory.
     * @param name New file name.  ".log" will be appended by this class.
     */
    public static void setLogFileName(String name) {       
        if (! logFileName.equals(name)) {
            // close existing open file, if any
            close();
           
            logFileName = name;
        }
    }

    /**
     * Set exception to be thrown when quit is called.
     * Default behavior is to pop up a notice and then exit.
     * If an exception is provided, quit throws that
     * exception instead.  (Purpose: to allow a server
     * to recover from per-thread crashes.)
     * <p> The exception must be set for each thread
     * for which recovery is desired.  Normally, this
     * is used for (a) server tasks and (b) to allow
     * unit test classes to recover from errors.
     * Client methods will normally use the default
     * behavior.
     * @param cl The exception class to be thrown.  May be null,
     * which restores default behavior.  It must
     * be a subclass of RuntimeException, since it
     * is not required to be declared in methods where
     * it may be thrown.
     */
    public static void setQuitException(Class cl) {
        quitExceptionClass = cl;
    }

    /**
     * Set text for task number indication in messages.
     * Allows task number to be included automatically.  Must
     * be set for each thread.  
     */
    public static void setTaskNumber (String value) {
        taskNumber.set(value);
    }

    /**
     * Set silent status.  When true AND there is a quitException for
     * the quitting thread, print and traceback are suppressed.
     * (Used to suppress messages on expected exception in unit tests.)
     * @param flag true to suppress quitException logging
     */
    public static void setSilent(boolean flag) {
        silent = flag;
    }

    /**
     * Set frame to be used for positioning fatal error
     * dialog.
     * @param frame the frame used for positioning reference
     */
    public static void setProgramFrame(JFrame frame) {
        programFrame = frame;
    }


    /**
     * Write message to System.out only.
     * Intended for progress messages that do not go in log
     * @param msg the message to be written
     */
    public synchronized static void console(String msg) {
        String timedMessage = makeTimedMessage("... ",msg);
        System.out.println(timedMessage);
        System.out.flush();
    }

    /**
     * Write message to System.out and log file
     * Intended for progress/trace messages.
     * @param msg the message to be written
     */
    public synchronized static void print(String msg) {
        if (writer == null) {
            openWriter();
        }
        String timedMessage = makeTimedMessage(msg);
        System.out.println(timedMessage);
        System.out.flush();
        writer.println(timedMessage);
        writer.flush();
    }

    /**
     * Write message to System.err and log file
     * Intended for error messages (allows redirect of consol log for non-errors)
     * @param msg the message to be written
     */
    public synchronized static void printError(String msg) {
        if (writer == null) {
            openWriter();
        }
        String timedMessage = makeTimedMessage("*** ",msg);
        System.err.println(timedMessage);
        System.err.flush();
        writer.println(timedMessage);
        writer.flush();
    }

    /**
     * Write message to log file only.  Allows trace/statistics that do not show on console.
     * @param msg the message to be written
     */
    public synchronized static void write(String msg) {
        if (writer == null) {
            openWriter();
        }
        String timedMessage = makeTimedMessage(msg);
        writer.println(timedMessage);
        writer.flush();
    }

    /**
     * Write untimed message line to log file only.  Used for job/ftp output
     * @param msg the message to be written
     */
    public synchronized static void info(String msg) {
        if (writer == null) {
            openWriter();
        }
        //writer.println(msg);
        //writer.flush();
        // Put out multiple lines, using platform concept of line
        String[] msgList = (msg == null ? new String[] {"null"} : msg.split("\r?\n"));
        info(msgList);
    }

    /**
     * Write list of untimed message lines to log file only.
     * @param msgList the list of messages to be written
     */
    public synchronized static void info(String[] msgList) {
        if (writer == null) {
            if (writer == null) {
                openWriter();
            }
        }
        for (int i = 0; i < msgList.length; i++) {
            if (msgList[i].length ()== 0) {
                //writer.println("++blank line==");
                //(new Throwable()).printStackTrace(writer);
                writer.println();
            } else {
                writer.println(msgList[i]);
            }
        }
        writer.flush();
    }

    /**
     * Return the writer, for use by Throwable.printStackTrace
     */
    public synchronized static PrintWriter getWriter() {
        if (writer == null) {
            openWriter();
        }
        return writer;
    }

    /**
     * Open a new log file (with autoflush)
     */
    private static void openWriter() {

        now = new Date(); // current date & time
        SimpleDateFormat nameFormatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        String logDateTime = nameFormatter.format(now
                                                 , new StringBuffer(19)
                                                 , new FieldPosition(0))
                                        .toString();
        
        // find working directory (so it works from IDE and command prompt)
//        File logFile;
//        URL binResource = Object.class.getResource("/bin");
//        if (binResource != null) {
//            String bs = binResource.toString();
//           // System.out.println("bs1--------->" + bs);
//            try {
//            	bs = java.net.URLDecoder.decode(bs, "UTF-8");
//                  //System.out.println("bs2--------->" + bs);
//            } catch (java.io.UnsupportedEncodingException uee) {
//                System.err.println("**** Error in path ("
//                        + logFileName + ".log): " + uee);
//                System.exit(1);
//            }
//            // ends here
//            
//            
//            String dir = bs.substring(6, bs.length() - 4);
//            System.out.println("dir -------> " + dir);
//            // System.out.println("dir--------->" + dir);
//            logFile = new File(dir, logFileName + ".log");
//        } else {
//            logFile = new File(logFileName + ".log");
//        }

        File logFile = new File(CommonProperties.getRunLogFilePath());;
        try {
            // Note: FileWriter(File, boolean) -- introduced in JDK 1.4
            FileWriter fileWriter = new FileWriter(logFile, /* append=> */ true);
            writer = new PrintWriter(fileWriter, true);
            writer.println();
        } catch (IOException e) {
            System.err.println("**** Error opening log file ("
                                + logFileName + ".log): " + e);
            System.exit(1);
        }

        if (logFile.length() >= MAX_INITIAL_LOG_SIZE && ! fileSwitched) {
            // Switch the log file
            // Note.  nothing will be done if another run unit has the file open
            fileSwitched = true;
            print("Log file switched: " + logDateTime);
            writer.close();
            writer = null;

            File oldFile = new File(logFileName + ".oldlog");
            oldFile.delete();
            logFile.renameTo(oldFile);
            openWriter();
            return;
        } else {
            write("Logging started: " + logDateTime);
            return;
        }
    }
    private static boolean fileSwitched = false; // to avoid runaway switching in case of error


    /**
     * Add timestamp to a message.
    * @param msg The given message.
     */
    public static String makeTimedMessage(String msg) {
        return makeTimedMessage(null, msg);
    }

    /**
     * Add timestamp and given prefix to a message.
     * <p> PREFIX NOT CURRENTLY USED
     * @param pfx The given prefix.
     * @param msg The given message.
     */
    private synchronized static String makeTimedMessage(String pfx, String msg) {
        buffer.setLength(0);
        now.setTime(System.currentTimeMillis());
        stampFormatter.format(now, buffer, new FieldPosition(0));
        if (taskNumber.get() != null) {
            buffer.append(' ');
            buffer.append(taskNumber.get());
        }
        buffer.append(' ');
        if (pfx != null) {
            buffer.append(pfx);
        }
        buffer.append(msg);
        return buffer.toString();
    }

    /**
     * Fatal error, terminate with traceback for given exception.
     * @param e The given exception or error.
     */
    public static void quit(Throwable e) {
        quit(null, e);
    }

    /**
     * Fatal error, terminate with traceback for given exception.
     * @param msg A message to be printed
     * @param e The given exception or error.
     */
    public synchronized static void quit(String msg, Throwable e) {
        System.out.println("QUIT:"+msg+"::"+e);
        // If silent has not been specified OR no exception has been
        // specified, print the stack trace on the console
        if (! silent || quitExceptionClass == null) {
            if (msg != null) {
                System.out.println(msg);
            }
            if (e != null) {
                e.printStackTrace();
            }
        }
        // Write the stack trace to the log
        if (writer == null) {
            openWriter();
        }
        if (msg != null) {
            writer.println(msg);
        }
        if (e != null) {
            e.printStackTrace(writer);
        }

        if (quitExceptionClass != null) {
            // An exception class has been given.  Construct and throw the exception.
            Constructor quitClassConstructor = null;
            try {
                quitClassConstructor = quitExceptionClass
                                        .getConstructor(new Class[] { String.class });
            } catch (NoSuchMethodException e1) {
                e1.printStackTrace();
                System.exit(1);
            }
            RuntimeException quitException = null;
            try {
                quitException = (RuntimeException) quitClassConstructor
                                                  .newInstance(new Object[] { msg });
            } catch (InstantiationException e1) {
                e1.printStackTrace();
                System.exit(1);
            } catch (IllegalAccessException e1) {
                e1.printStackTrace();
                System.exit(1);
            } catch (InvocationTargetException e1) {
                e1.printStackTrace();
                System.exit(1);
            }
            if (e != null) {
                quitException.initCause(e);
            }
            throw quitException;
        } else {
            // No exception class given, make a pop-up.
            if (SwingUtilities.isEventDispatchThread()) {
                showFatalDialog();
            } else {
                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                showFatalDialog();
                            }
                        } );
                } catch (Throwable t) {
                    System.err.println("Exception during fatal error processing");
                    t.printStackTrace();
                    if (writer != null) {
                        writer.println("Exception during fatal error processing");
                        t.printStackTrace(writer);
                    }
                }
            }

            if (writer != null) {
                writer.close();
            }

            System.exit(1);
        }
    }

    private static void showFatalDialog() {
        JOptionPane.showMessageDialog(programFrame,
                                      "Program terminated with fatal error - see "+logFileName+".log",
                                      "Fatal Error",
                                      JOptionPane.ERROR_MESSAGE); 
    }

    /**
     * Fatal error, terminate with no error-specific message.
     */
    public static void quit() {
        quit(new Throwable("Quit"));
    }

    /**
     * Fatal error, terminate with given message.
     * @param s The given message.
     */
    public static void quit(String s) {
        quit(new Throwable("Quit: " + s));
    }

    /** Close this log file */
    public static void close() {
        if (writer != null) {
            writer.close();
            writer = null;
        }
    }
}

