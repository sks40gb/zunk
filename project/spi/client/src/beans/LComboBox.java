/* $Header: /home/common/cvsarea/ibase/dia/src/beans/LComboBox.java,v 1.14 2004/09/15 02:34:05 weaston Exp $ */
package beans;

import com.lexpar.util.Log;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

/**
 * Provides an extension of JComboBox which allows checking
 * for maximum length.
 *
 * <p> preferredSize is determined from number of characters.
 */
public class LComboBox extends JComboBox implements LField {

    private JCheckBox checkBox = null;
    private int columns = 0;

    // cached value of column width
    private int lColumnWidth = 0;

    //private String originalText = null;
    private FocusText focusText = new FocusText();
    protected JTextField editor;

    /**
     * Constructs a new LComboBox initialized with the specified
     * maximum length.
     *
     * @param columns the maximum length of the text
     */
    public LComboBox(int columns) {
        this(columns, null);
    }

    public LComboBox(int columns, String[] values) {
        super();
        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                this.addItem(values[i]);
            }
        }
        setColumns(columns);
        setForeground(Color.black);
        ComboAgent agent = new ComboAgent(this);
        addFocusListener(focusText);
    }

    /**
     * Gets the column width.
     * (Lexpar) set to be the width of the
     * character <em>n</em> for the font used.  Maybe this
     * isn't quite right, but it's a first approximation.
     *
     * @return the column width >= 1
     */
    protected int getColumnWidth() {
        if (lColumnWidth == 0) {
            FontMetrics metrics = getFontMetrics(getFont());
            lColumnWidth = metrics.charWidth('n');
        }
        return lColumnWidth;
    }

    /**
     * Returns the preferred size Dimensions needed for this 
     * TextField.  If a non-zero number of columns has been
     * set, the width is set to the columns multiplied by
     * the column width. (Lexpar) plus one -- seems to be
     * about right, at least for a first approximation.
     *
     * @return the dimensions
     */
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        int columns = getColumns();
        if (columns != 0) {
            // TODO get rid of magic number in calculation
            size.width = 32 + (columns) * getColumnWidth();
        }
        return size;
    }


    // Fields needed by FieldList
    public void setText(String text) {
        theImpl.checkChanged(text);
        setSelectedItem(text);
    //originalText = text;
    }

    public String getText() {
        Object obj = getSelectedItem();
        if (obj == null || (String) String.valueOf(obj) == null) {
            return "";
        } else {
            return (String) String.valueOf(obj);
        }
    }

    public void setColumns(int columns) {
        //Log.print("(LComboBox) setColumns(" + columns + ")");
        this.columns = columns;
    }

    public int getColumns() {
        return this.columns;
    }

    public void selectedItemChanged() {
        //Log.print("(LComboBox) selectedItemChanged");
        theImpl.setChanged(true);
    }

    public void clearField() {
        setSelectedIndex(-1);
    }

    private class FocusText implements FocusListener {

        public void focusGained(FocusEvent e) {
            try {
                // clear status line
            } catch (Throwable t) {
                Log.quit(t);
            }
        }

        public void focusLost(FocusEvent e) {
        }
    }

    class ComboAgent extends KeyAdapter {

        protected JComboBox aComboBox;
        protected JTextField editor;

        public ComboAgent(JComboBox comboBox) {
            //Log.print("---enter ComboAgent");
            aComboBox = comboBox;
            editor = (JTextField) aComboBox.getEditor().getEditorComponent();
            editor.addKeyListener(this);
        }

        public void keyTyped(KeyEvent e) {
            try {
                Log.print("KeyTyped");
                char ch = e.getKeyChar();
                if (ch == KeyEvent.CHAR_UNDEFINED || Character.isISOControl(ch)) {
                    //Log.print("(LComboBox).keyTyped returning " + ch);
                    return;
                }
                String str = editor.getText();
                if (str == null) {
                    //Log.print("(LComboBox).keyTyped str == null " + str);
                    return;  // Can this happen?

                }
                if (str.length() == 0) {
                    Log.print("(LComboBox).keyTyped str.length == 0 " + ch);
                    return;
                }
                String checkedString = checkText(str);
                if (checkedString == null || (checkedString.length() > getColumns() && getColumns() > 0)) {
                    // Refuse the insertion and beep to notify user
                    Toolkit.getDefaultToolkit().beep();
                    Log.print("BEEP> LComboBox: " + checkedString);
                    //Log.print("      checkedString = " + checkedString);
                    aComboBox.getEditor().setItem((Object) checkedString.substring(0, getColumns()));
                //Log.print("BEEP> LComboBox: insertString("
                //          + str + ",...) ");
                } else {
                    //super.insertString(offs, checkedString, a);
                }
                setChanged(true);
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    }

    /**
     * This method may be overridden to provide checking and modification
     * of the string to be inserted.  
     * <p>
     * If <code>str</code> contains invalid characters, the method
     * returns <code>null</code>; otherwise it returns a string with
     * any replacements.
     *
     * @param str the text to be inserted (may be empty, but never null)
     */
    protected String checkText(String str) {
        return str;
    }

    // Delegate common LField calls to an LFieldImpl
    private LFieldImpl theImpl = new LFieldImpl(this);

    public void setChanged(boolean flag) {
        theImpl.setChanged(flag);
    }

    public boolean isChanged() {
        return theImpl.isChanged();
    }

    public void setCheckBox(JCheckBox checkBox) {
        theImpl.setCheckBox(checkBox);
    }

    public JCheckBox getCheckBox() {
        return theImpl.getCheckBox();
    }

    public void setTextField(JTextField textfield) {
        theImpl.setTextField(textfield);
    }

    public JTextField getTextField() {
        return theImpl.getTextField();
    }

    public void setComboBox(LComboBox combo) {
        theImpl.setComboBox(combo);
    }

    public LComboBox getComboBox() {
        return theImpl.getComboBox();
    }
}
