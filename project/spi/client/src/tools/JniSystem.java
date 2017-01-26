/* $Header: /home/common/cvsarea/ibase/dia/src/tools/JniSystem.java,v 1.2 2003/10/03 02:41:58 weaston Exp $ */
package tools;

public class JniSystem {
    public native void call(String text);

    static {
        System.loadLibrary("jnisys");
    }
    
    // for testing
    public static void main(String[] args) {
        String text = "echo no command given ***";
        if (args.length > 0) {
            text = args[0];
        }
        System.out.println("before the call");
        System.err.println(text);
        new JniSystem().call(text);
        System.err.println("after the call");
        System.exit(0);
    }
}
