/* $Header: /home/common/cvsarea/ibase/dia/src/tools/CommandUtil.java,v 1.1 2003/09/30 22:35:22 weaston Exp $ */

package tools;

import tools.Command;
import common.Log;

public class CommandUtil {

    private CommandUtil() {}


    public static int doSystemCommand(String ctext, boolean printOutput) {
        return doSystemCommand(ctext, null, printOutput);
    }

    public static int doSystemCommand(String ctext, String[] env, boolean printOutput) {

        Log.write("System Command: " + ctext);
        Command cmd = new Command(ctext, env);
        runCommand(cmd, printOutput);
        return cmd.exitValue();
    }

    public static int doSystemCommand(String ctext) {
        return doSystemCommand(ctext,true);
    }

    private static String[] runCommand(Command cmd, boolean printOutput) {

        cmd.run();

        String[] errors = cmd.getErrStream();
        String[] result = cmd.getOutStream();
        for (int i = 0; i < errors.length; i++) {
            Log.print(errors[i]);
        }
        if (printOutput) {
            for (int i = 0; i < result.length; i++) {
                Log.write(result[i]);
            }
        }

        return result;
    }
}
