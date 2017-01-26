/* $Header: /home/common/cvsarea/ibase/dia/src/tools/Command.java,v 1.1 2003/09/30 22:35:22 weaston Exp $ */
 
package tools;

import common.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;



public class Command {

    String      command = null;
    String[]    commandAndArgs = null;
    String[]    env;

    Process     process = null;
    ReadThread  outThread = null;
    ReadThread  errThread = null;

    String[]    outResult = null;
    String[]    errResult = null;
    int         exitValue = -1;


    public Command(String command) {
        this(command, null);
    }

    public Command(String command, String[] env) {
        this.command = command;
        this.env = env;
    }

    public Command(String[] command) {
        this(command, null);
    }

    public Command(String[] command, String[] env) {
        this.commandAndArgs = command;
        this.env = env;
    }


    public void run () {
        try {
            Runtime rt = Runtime.getRuntime();
            if (command == null) {
                process = rt.exec(commandAndArgs, env);
            } else {
                process = rt.exec(command, env);
            }
        } catch (IOException e) {
            Log.quit(e);
        }

        outThread = new ReadThread(process.getInputStream());
        errThread = new ReadThread(process.getErrorStream());
        outThread.start();
        errThread.start();

        // send an empty standard input
        try {
            process.getOutputStream().flush();
            process.getOutputStream().close();
        } catch (IOException e) {
            Log.quit(e);
        }

        try {
            process.waitFor();
            outThread.join();
            errThread.join();
        } catch (InterruptedException e) {
            Log.quit(e);
        }

        synchronized (this) {
            outResult = outThread.getResult();
            errResult = errThread.getResult();
            exitValue = process.exitValue();
        }
    }

    public synchronized String[] getOutStream() {
        return outResult;
    }


    public synchronized String[] getErrStream() {
        return errResult;
    }


    public synchronized int exitValue() {
        return exitValue;
    }


    public synchronized void abort() {
        if (process != null) {
            process.destroy();
        }
    }
}


final class ReadThread extends Thread {

    InputStream stream;
    ArrayList result = new ArrayList();
    ArrayList finalResult = null;

    ReadThread(InputStream stream) {
        super();
        this.stream = stream;
    }

    public void run() {
        BufferedReader reader = new BufferedReader
                ( new InputStreamReader (stream) );
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {
            Log.quit(e);
        }
        setResult(result);
    }

    private synchronized void setResult(ArrayList result) {
        finalResult = result;
    }

    synchronized String[] getResult() {
        return (String[]) finalResult.toArray(new String[finalResult.size()]);
    }
}
