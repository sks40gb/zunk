/* $Header: /home/common/cvsarea/ibase/dia/src/beans/LUpperCaseTextField.java,v 1.3 2004/08/07 15:20:18 weaston Exp $ */
package beans;

//import com.lexpar.util.Log;
/**
 * Provides an extension of JTextField which allows checking
 * for maximum length and for forcing characters to upper case.
 *
 * <p> preferredSize is determined from number of characters.
 * (Overrides JTextField, because the latter produces fields that
 * are too wide for proportional fonts.)  Currently, we do this
 * by changing the default character width.
 *
 * <p> Implementation adapted from WholeNumberField.java in The
 * JFC Swing Tutorial. 
 */
public class LUpperCaseTextField extends LTextField {

    /**
     * Constructs a new LUpperCaseTextField initialized with the specified text
     * and maximum length.
     *
     * @param text the text to be displayed, or null
     * @param columns the maximum length of the text
     */
    public LUpperCaseTextField(String text, int columns) {
        super(text, columns);
    }

    /**
     * Constructs a new LUpperCaseTextField initialized with the specified text
     * and with no maximum length. The number of columns is 0.
     *
     * @param text the text to be displayed, or null
     */
    public LUpperCaseTextField(String text) {
        this(text, 0);
    }

    /**
     * Constructs a new empty LUpperCaseTextField with the specified
     * maximum length. The number of columns is 0.
     *
     * @param columns the maximum length of the text
     */
    public LUpperCaseTextField(int columns) {
        this(null, columns);
    }

    /**
     * Constructs a new empty LUpperCaseTextField with no maximum length.
     * The number of columns is 0.
     */
    public LUpperCaseTextField() {
        this(null, 0);
    }

    /**
     * Convert inserted text to upper case.  Overrides checkText
     * in LTextField
     *
     * @param str the text to be inserted (may be empty, but never null)
     */
    protected String checkText(String str, int offs) {
        return str.toUpperCase();
    }
}
