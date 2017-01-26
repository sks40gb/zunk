package beans;

import com.lexpar.util.Log;
import de.hunsicker.jalopy.Jalopy;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

public class ScriptEditor extends JTextPane {

    String insertText = "";

    public ScriptEditor() {
        if (System.getProperty("os.name").equals("Linux")) {
            //setListener();
        }
        setBackground(Color.WHITE);
    }

    private void setListener() {
        addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent e) {
            }

            public void keyPressed(KeyEvent e) {
                setTabNSpace_KeyPressed(e);
            }

            public void keyReleased(KeyEvent e) {
                cursorMovement(e);
                setTabNSpace_keyReleased(e);
            }
        });
    }

    private void setTabNSpace_KeyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == 127 || k == 8 || k == 17 || k == 16 || k == 65) {
            return;
        }
        int currentCursorPoint = getCaretPosition();
        int cursorAt = currentCursorPoint - 1;
        String inputText = getText();
        String line = "";
        while (true) {
            char c = inputText.charAt(cursorAt);
            line = "" + c + line;
            if (c == '\r' || c == '\n' || cursorAt == 0) {
                break;
            }
            cursorAt--;
        }

        Matcher m = Pattern.compile("^(\\s*)|(\\t*)").matcher(line);
        insertText = "";
        if (m.find()) {
            insertText = m.group().replace("\n", "");
        }
    }

    private void setTabNSpace_keyReleased(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == 127 || k == 8 || k == 17 || k == 16 || k == 65) {
            return;
        }
        if (e.getKeyCode() == 10) {
            int cursorIndex = getCaretPosition() - 1;
            String inputText = getText();
            inputText = inputText.substring(0, cursorIndex + 1) + insertText + inputText.substring(cursorIndex + 1);
            setText(inputText);
            setCaretPosition(cursorIndex + insertText.length() + 1);
        }
    }

    public void cursorMovement(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == 127 || k == 8 || k == 17 || k == 16 || k == 65) {
            return;
        }
        String inputText = getText();
        int cursorPoint = getCaretPosition();
        //if(e.getKeyCode() == 10 && getCaretPosition() == inputText.length()){

        //System.out.println("keycode------------> " + k);
        if (!((k == 33) || (k == 34) || (k == 35) || (k == 36) || (k == 37) || (k == 38) || (k == 39) || (k == 40) || (k == 16) || (k == 127))) {
            setText(inputText);
            setCaretPosition(cursorPoint);
        }
    }

    public void appendNaive(Color c, String s) {

        SimpleAttributeSet aset = new SimpleAttributeSet();
        StyleConstants.setForeground(aset, c);

        int len = getText().length();
        setCaretPosition(len); // place caret at the end (with no selection)

        setCharacterAttributes(aset, false);
        replaceSelection(s); // there is no selection, so inserts at caret

    }

    public void append(Color c, String s) { // better implementation--uses
        // StyleContext

        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, c);

        int len = getDocument().getLength();
        //setCaretPosition(len);
        setCharacterAttributes(aset, true);
        replaceSelection(s);
    }

    public static void main(String argv[]) throws ClassNotFoundException {

        String data_ = "public String paranthesis(String errorMessage, String param) {" +
                "String message = null;" +
                "char singleLeftParanthesis = '(';" +
                "char singleRightParanthesis = ')';" +
                "int n = 0;" +
                "for ( int j = 0, j < codedValue.length(), j++) {" +
                "if (codedValue.charAt(j) == singleLeftParanthesis) {" +
                "    n++;" +
                "} else if (codedValue.charAt(j) == singleRightParanthesis) {" +
                "    n--;" +
                "}" +
                " if (n < 0) {" +
                "    break;" +
                "}" +
                "}" +
                "    message = errorMessage;" +
                "}" +
                "return message;" +
                "}";

        String data_1 = "public String chars(String errorMessage, String param) " +
                "{" +
                "  String message = null;           " +
                "if(param.length() == 1) " +
                "{ if (codedValue.contains(param))                  " +
                "{                        " +
                "message =errorMessage;" +
                "                        " +
                "if(codedValue.equals(\"nothing\"))" +
                "                        {                              " +
                "if (" +
                "false)                              " +
                "{                              " +
                "int i = 0;                                                            " +
                "}                        " +
                "}                        " +
                "}                  " +
                "}                      " +
                "return message;                                    " +
                "}";



        String data_2 = "public String paranthesis(String errorMessage, String param) { String message = null;  char singleLeftParanthesis = '('; " +
                "char singleRightParanthesis =')';        int n = 0;       for (int j = 0; j &lt; codedValue.length(); j++) {           " +
                " if (codedValue.charAt(j) == singleLeftParanthesis) {                n++;            } else if (codedValue.charAt(j) == " +
                "singleRightParanthesis) {                n--;            }            if (n > 0) {                break;           " +
                " }        }        if (n != 0) {            message = errorMessage;        }        return message;    }";

        String data = "/*sunil kum\nsingh*/public String beginChar(String errorMessage, String param){ String message = null;int i =0; if (codedValue.startsWith(param)){message = errorMessage;if(true){int i;}}return message;}";
        final ScriptEditor pane = new ScriptEditor();

        pane.setText(data);
        JFrame f = new JFrame("ColorPane example");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setContentPane(new JScrollPane(pane));
        f.setSize(600, 400);
    //  f.setVisible(true);    

    }

    @Override
    public void setText(String codeString) {
        super.setText("");
        int length = 0;
        String word = "";
        String replace = "";
        String matchString = codeString;
        codeString = getFormattedCode(codeString);


        System.out.println("codeString ==== : " + codeString);

        // if the code string have the comment with /* */
        if (codeString.contains("*/")) {
            matchString = codeString.substring(codeString.indexOf("*/"));
        }

        Matcher m = Pattern.compile("([\\s,;}{\\)\\(])(abstract|finally|public|assert|float|return|boolean|for|short|break|goto|static|" +
                "byte|if|strictfp|case|implements|super|catch|import|catch|switch|char|instanceof|synchronized|class|" +
                "int|this|const|interface|throw|const|continue|long|throws|default|native|transient|do|new|try|double|" +
                "package|void|else|private|volatile|extends|protected|while|final)[\\{\\}\\(\\)\\s,]").matcher(matchString);

        int searchIndex = 0;
        while (m.find(searchIndex)) {

            word = m.group();
            searchIndex = m.start() + 1;
            length = word.length();
            replace = word.substring(0, 1) + "~#" + word.substring(1, length - 1) + "~" + word.substring(length - 1);
            //System.out.println("----" + word + "-----" + replace + "----");
            try {
                codeString = codeString.replaceFirst(word, replace);
            } catch (Exception e) {
                Log.print(this.getClass() + " : " + e.toString());
            }
        }

        StringTokenizer tokens = new StringTokenizer(codeString, "~");
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            if (token.startsWith("#")) {
                append(Color.BLUE, token.replace("#", ""));
            } else {
                append(Color.BLACK, token);
            }
        }
    }

    public String getFormattedCode(String str) {
        // create a new Jalopy instance with the currently active code convention settings

        StringBuffer buffer = new StringBuffer();
        try {
            Jalopy jalopy = new Jalopy();
            String tempDir = System.getProperty("java.io.tmpdir");

            File file = new File(tempDir + File.separator + "Test.java");

            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer = new FileWriter(file);
            writer.append("class Test {");
            writer.append(str);
            writer.append("/***/}");
            writer.close();

            // Get the temporary directory and print it.
            System.out.println("OS current temporary directory is " + tempDir);

            jalopy.setInput(file);
            jalopy.setOutput(file);

            // format and overwrite the given input file
            jalopy.format();

            Reader in = new FileReader(file);
            BufferedReader reader = new BufferedReader(in);
            String line = "";
            boolean isFirstLine = true;
            while ((line = reader.readLine()) != null) {
                if (!isFirstLine) {
                    buffer.append(line + "\n");
                } else {
                    System.out.println("line : " + line);
                    String firstLine = line.replace("class Test {", "").trim();
                    if(firstLine.length()>0){
                        buffer.append("   " + firstLine + "\n");
                    }
                    isFirstLine = false;
                }
            }
            if (jalopy.getState() == Jalopy.State.OK) {
                System.out.println(file + " successfully formatted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return buffer.toString().replace("/***/ }", "");
    }
}



