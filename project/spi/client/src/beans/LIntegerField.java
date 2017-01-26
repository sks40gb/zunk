/* $Header: /home/common/cvsarea/ibase/dia/src/beans/LIntegerField.java,v 1.8.8.4 2007/03/25 12:12:14 bill Exp $ */
package beans;

import client.ClientTask;
import client.TaskSelectFieldDescription;
import com.lexpar.util.Log;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.FocusAdapter;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.ParseException;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.text.Document;

/**
 * Provides an extension of LTextField for integers.  Allows for
 * (locale-dependent) comma separators.
 *
 * <p> Implementation adapted from WholeNumberField.java in The
 * JFC Swing Tutorial. 
 */
public class LIntegerField extends LTextField implements LField {

    int projectId = 0;
    String fieldName = "";

    /**
     * Create an instance of <code>LIntegerField</code>.
     * @param columns the number of columns to define for this field
     */
    public LIntegerField(int columns, int projectId, String fieldName) {
        super(columns);
        super.setText("");
        this.projectId = projectId;
        this.fieldName = fieldName;
        this.setHorizontalAlignment(RIGHT);
        this.addFocusListener(new FocusAdapter() {

            public void focusLost(java.awt.event.FocusEvent evt) {
                // On permanent loss of focus, adjust formatting.

                // Save value of changed flag to reset following setValue,
                // because this setValue causes theImpl.checkBox to be checked as
                // if a change has been made.
                boolean chg = isChanged();
                boolean enabled = false;
                boolean selected = false;
                if (theImpl != null && theImpl.getCheckBox() != null) {
                    enabled = theImpl.getCheckBox().isEnabled();
                    selected = theImpl.getCheckBox().isSelected();
                }
                
                if (!evt.isTemporary()) {
                    setValue(getValue());
                }
                setChanged(chg);
                if (theImpl != null && theImpl.getCheckBox() != null) {
                    theImpl.getCheckBox().setEnabled(enabled);
                    theImpl.getCheckBox().setSelected(selected);
                    theImpl.getComboBox().setEnabled(enabled);
                }            
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldKeyPressed(evt);
            }
        });

    }

    public LIntegerField(int columns) {
        super(columns);
        super.setText("");      
        this.setHorizontalAlignment(RIGHT);
        this.addFocusListener(new FocusAdapter() {

            public void focusLost(java.awt.event.FocusEvent evt) {
                // On permanent loss of focus, adjust formatting.
                // Save value of changed flag to reset following setValue,
                // because this setValue causes theImpl.checkBox to be checked as
                // if a change has been made.
                boolean chg = isChanged();
                boolean enabled = false;
                boolean selected = false;
                if (theImpl != null && theImpl.getCheckBox() != null) {
                    enabled = theImpl.getCheckBox().isEnabled();
                    selected = theImpl.getCheckBox().isSelected();
                }              
                if (!evt.isTemporary()) {
                    setValue(getValue());
                }
                setChanged(chg);
                if (theImpl != null && theImpl.getCheckBox() != null) {
                    theImpl.getCheckBox().setEnabled(enabled);
                    theImpl.getCheckBox().setSelected(selected);
                    theImpl.getComboBox().setEnabled(enabled);
                }            
            }
        });

    }

    /**
     * Constructs a new empty LTextField with no maximum length.
     * The number of columns is 0.
     */
    public LIntegerField() {
        this(0);
    }

    private void textFieldKeyPressed(java.awt.event.KeyEvent evt) {
        try {
            switch (evt.getKeyCode()) {

                case KeyEvent.VK_F1:
                    dof1(evt);
                    break;

            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private int dof1(KeyEvent evt) {        
        final ClientTask task = new TaskSelectFieldDescription(fieldName, projectId);
        task.setCallback(new Runnable() {

            public void run() {
                try {                   
                    String description = (String) task.getResult();
                    String desc = "";
                    String fieldDescription = "";
                    StringTokenizer Tok = new StringTokenizer(description);
                    while (Tok.hasMoreTokens()) {
                        desc = desc + " " + Tok.nextToken();
                        if (desc.length() >= 45) {
                            fieldDescription = fieldDescription + "\n" + desc;
                            desc = "";
                        }
                    }
                    fieldDescription = fieldDescription + "\n" + desc;                   
                    String dialogm = "HELP TEXT FOR  " + fieldName;
                    Component parent = task.getParent();

                    JOptionPane.showMessageDialog(parent,
                            fieldDescription,
                            dialogm,
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        });
        task.enqueue();

        return 0;
    }
    final private static NumberFormat NF = NumberFormat.getIntegerInstance();
    final private static NumberFormat NF1 = NumberFormat.getPercentInstance();
    /**
     * Limit insertions so that field always contains a valid integer,
     * perhaps with comma separators.  
     * <p>
     * If <code>str</code> contains invalid characters, the method
     * returns <code>null</code>; otherwise it returns a string with
     * any replacements.
     *
     * @param str the text to be inserted (may be empty, but never null)
     */
    protected String checkText(String str, int offs) {
        ParsePosition pos = new ParsePosition(0);
        String text = super.getText();
        String newText = text.substring(0, offs) + str + text.substring(offs);
        Number value = NF.parse(newText, pos);
        // check that parsing accepted the entire newText
        return (value != null && pos.getIndex() == newText.length() ? str : null);
    }

    /**
     * Get the textual value.  Will always be a valid integer, except
     * that 0 is returned as "".  Will never be null.
     */
    public String getText() {
        return Integer.toString(getValue());
    }

    /**
     * Get the integer value.  If blank is displayed, 0 will be returned.
     */
    public int getValue() {
        String text = super.getText();
        if (text.length() == 0) {
            return 0;
        }
        try {
            return NF.parse(super.getText()).intValue();
        } catch (ParseException e) {
            Log.quit(e);
            return 0;
        }
    }

    /**
     * Set this field from a text value.  The text value
     * must be blank or a valid integer; the text is
     * stored in formatted form.
     */
    public void setText(String text) {
        if (text == null || text.length() == 0) {
            super.setText("");
        } else {
            setValue(Integer.parseInt(text));
        }
    }

    /**
     * Set this field from an integer value.  If 0,
     * blank will be shown; otherwise the integer
     * is formatted.
     */
    public void setValue(int value) {
        String text = "";
        if (value != 0) {
            text = NF.format(value);
        }
        super.setText(text);
    }

    /**
     * Returns the preferred size Dimensions needed for this 
     * field.  If a non-zero number of columns has been
     * set, the width is set according to the number of
     * characters in the edited representation (digits and
     * commas).
     *
     * @return the dimensions
     */
    public Dimension getPreferredSize() {
        int columns = getColumns();
        if (columns == 0) {
            return super.getPreferredSize();
        } else {
            // use size calculation, with extra columns for commas
            return getPreferredSize((columns * 4 - 1) / 3);
        }
    }

    /**
     * Call to set document (not valid).
     * @deprecated Do not set document explicitly.
     */
    public void setDocument(Document doc) {
        super.setDocument(doc);
    }

    public void setComboBox(LComboBox combo) {
        theImpl.setComboBox(combo);
    }

    public LComboBox getComboBox() {
        return theImpl.getComboBox();
    }
}
