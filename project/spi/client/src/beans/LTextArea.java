/* $Header: /home/common/cvsarea/ibase/dia/src/beans/LTextArea.java,v 1.10 2005/04/08 17:27:16 nancy Exp $ */
package beans;

//import com.lexpar.util.Log;
import java.awt.Color;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Provides an extension of scrollable JTextArea with length checkint
 */
public class LTextArea extends JScrollPane implements LField {

    private CheckedTextArea textArea = null;

    /**
     * Constructs a new LTextField with the specified title, rows, columns
     * and maximum length.
     */
    public LTextArea(String title, int rows, int columns) {
        super();
        this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS); // at least for now

        setColumnHeaderView(new JLabel(title));
        textArea = new CheckedTextArea(rows, columns);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        setViewportView(textArea);
    }

    public LTextArea(int rows, int columns) {
        super();
        this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS); // at least for now
        textArea = new CheckedTextArea(rows, columns);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        setViewportView(textArea);
    }

    protected void setDocument(PlainDocument doc) {
        textArea.setDocument(doc);
    }

    private class CheckedTextArea extends JTextArea {

        CheckedTextArea(int rows, int columns) {
            super(rows, columns);
        }

        protected Document createDefaultModel() {
            return new CheckedDocument();
        }

        protected class CheckedDocument extends PlainDocument {

            CheckedDocument() {
                super();
            }

            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                String checkedString = str;
                if (checkedString == null) {
                    checkedString = "";
                }
                super.insertString(offs, checkedString, a);
                setChanged(true);
            }

            public void remove(int offs, int len)
                    throws BadLocationException {
                super.remove(offs, len);
                setChanged(true);
            }
        }
    }

    public void setEnabled(boolean flag) {       
        textArea.setEditable(flag);
        textArea.setForeground(Color.black);
        if (flag) {
            textArea.setBackground(Color.white);
        } else {
            textArea.setBackground(null);
        }
    }

    // overrides for LField
    public void setText(String text) {
        theImpl.checkChanged(text);
        textArea.setText(text);
        textArea.setCaretPosition(0);
        setChanged(false);
    }

    public String getText() {
        return textArea.getText();
    }

    public void setColumns(int columns) {
    }

    public void setEditable(boolean flag) {
        textArea.setEditable(flag);
    }

    public void setLineWrap(boolean flag) {
        textArea.setLineWrap(flag);
    }

    public void setWrapStyleWord(boolean flag) {
        textArea.setWrapStyleWord(flag);
    }

    public void setSelectionStart(int columns) {
        textArea.setSelectionStart(columns);
    }

    public void setSelectionEnd(int columns) {
        textArea.setSelectionEnd(columns);
    }

    public void replaceSelection(String str) {
        textArea.replaceSelection(str);
    }

    public void insert(String str, int pos) {
        textArea.insert(str, pos);
    }

    public int getSelectionEnd() {
        return textArea.getSelectionEnd();
    }

    public void requestFocus() {
        textArea.requestFocus();
    }

    public void setCaretPosition(int pos) {
        textArea.setCaretPosition(pos);
    }

    public void clearField() {
        setText("");
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

    public void setTextField(JTextField textField) {
        theImpl.setTextField(textField);
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
