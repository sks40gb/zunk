/* $Header: /home/common/cvsarea/ibase/dia/src/beans/LTextField.java,v 1.26.8.2 2005/12/02 13:38:50 nancy Exp $ */
package beans;

import client.ClientTask;
import client.TaskSelectFieldDescription;
import com.lexpar.util.Log;
import common.CodingData;
import common.edit.ProjectMapper;
import java.awt.Color;
import java.awt.Component;
import java.util.StringTokenizer;
import ui.SplitPaneViewer;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * Provides an extension of JTextField which allows checking
 * for maximum length and for valid characters on insertion.
 * The <code>checkText</code> method is overridden in subclasses
 * to provide the valid character checking; it may also replace
 * characters in the string to be inserted.
 *
 * <p> preferredSize is determined from number of characters.
 * (Overrides JTextField, because the latter produces fields that
 * are too wide for proportional fonts.)  Currently, we do this
 * by changing the default character width.
 *
 * <p> Implementation adapted from WholeNumberField.java in The
 * JFC Swing Tutorial. 
 *
 * <p> A PropertyChangeEvent is fired for property "text" when
 * the text of the field is changed.
 *
 * <p> NOTE.  Clears status in keys.status.  (Should not -- should be via listener.)
 */
public class LTextField extends JTextField implements LField {

    // cached value of column width
    private int lColumnWidth = 0;
    private int projectId = 0;
    private String fieldName;
    private CodingData codingData;
    private String whichStatus;
    private String text;
    private String bates_Number;
    private String image_Path;
    private int child_Id;
    private int batch_Id;
    private ProjectMapper projectMap;
    private String documentNumber;
    private static CodingData codingData_F6;
    //private String originalText = null;

    /**
     * Constructs a new LTextField initialized with the specified text
     * and maximum length.
     *
     * @param text the text to be displayed, or null
     * @param columns the maximum length of the text
     */
    public LTextField(String text, int columns, int projectId, String fieldName, CodingData codingData, String whichStatus, ProjectMapper projectMap, int childid) {
        super();
        this.projectId = projectId;
        this.fieldName = fieldName;
        this.codingData = codingData;
        this.whichStatus = whichStatus;
        this.text = text;
        this.projectMap = projectMap;
        this.child_Id = childid;
        setText(text);
        setColumns(columns);
        setHorizontalAlignment(JTextField.LEFT);
        addFocusListener(new FocusText());
        addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldKeyPressed(evt);
            }
        });
    }

    /**
     * Constructs a new LTextField initialized with the specified text
     * and maximum length.
     *
     * @param text the text to be displayed, or null
     * @param columns the maximum length of the text
     */
    public LTextField(String text, int columns, int projectId, String fieldName, CodingData codingData, String whichStatus,
            String documentNumber, String bates_Number, String image_Path, int child_Id, int batch_Id, ProjectMapper projectMap) {
        super();
        this.projectId = projectId;
        this.fieldName = fieldName;
        this.codingData = codingData;
        this.whichStatus = whichStatus;
        this.text = text;
        this.documentNumber = documentNumber;
        this.bates_Number = bates_Number;
        this.image_Path = image_Path;
        this.child_Id = child_Id;
        this.batch_Id = batch_Id;
        this.projectMap = projectMap;
        setText(text);
        setColumns(columns);
        setHorizontalAlignment(JTextField.LEFT);
        addFocusListener(new FocusText());
        addKeyListener(new java.awt.event.KeyAdapter() {

            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldKeyPressed(evt);
            }
        });
    }

    public LTextField(String text, int columns) {
        super();
        setText(text);
        setColumns(columns);
        setHorizontalAlignment(JTextField.LEFT);
        addFocusListener(new FocusText());
    }

    /**
     * Constructs a new LTextField initialized with the specified text
     * and with no maximum length. The number of columns is 0.
     *
     * @param text the text to be displayed, or null
     */
    public LTextField(String text) {
        this(text, 0, 0, "", null, "", null, 0);
        setHorizontalAlignment(JTextField.LEFT);
    }

    /**
     * Constructs a new empty LTextField with the specified
     * maximum length. The number of columns is 0.
     *
     * @param columns the maximum length of the text
     */
    public LTextField(int columns, int projectId, String fieldName, CodingData codingData, String whichStatus, ProjectMapper projectMap, int childid) {
        this(null, columns, projectId, fieldName, codingData, whichStatus, projectMap, childid);
        setHorizontalAlignment(JTextField.LEFT);
    }

    public LTextField(int columns) {
        this(null, columns);
        setHorizontalAlignment(JTextField.LEFT);
    }

    public LTextField(int data, CodingData codingdata) {
        this.child_Id = data;
        codingData_F6 = codingdata;
    }

    /**
     * Constructs a new empty LTextField with no maximum length.
     * The number of columns is 0.
     */
    public LTextField() {
        this(null, 0, 0, "", null, "", null, 0);

        setHorizontalAlignment(JTextField.LEFT);
    }

    /**
     * Constructs a new empty LTextField with the specified
     * maximum length. The number of columns is 0.
     *
     * @param columns the maximum length of the text
     */
    public LTextField(int columns, int projectId, String fieldName, CodingData codingData, String whichStatus,
            String documentNumber, String bates_Number, String image_Path, int child_Id, int batch_Id, ProjectMapper projectMap) {
        this(null, columns, projectId, fieldName, codingData, whichStatus, documentNumber, bates_Number, image_Path, child_Id, batch_Id, projectMap);
        setHorizontalAlignment(JTextField.LEFT);
    }

    private void textFieldKeyPressed(java.awt.event.KeyEvent evt) {
        try {
            switch (evt.getKeyCode()) {

                case KeyEvent.VK_F1:
                    dof1(evt);
                    break;
                case KeyEvent.VK_F10:
                    if ("Coding".equals(whichStatus) || "CodingQC".equals(whichStatus) || "Admin".equals(whichStatus)) {
                        dof10(evt);
                    }
                    break;
                case KeyEvent.VK_F6:
                    if ("CodingQC".equals(whichStatus) || "Admin".equals(whichStatus) || "Masking".equals(whichStatus)) {
                        doVerify(evt);
                        break;
                    }
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

    /**
     * For F10(Query Tracker)
     * @param Tracker
     * @return
     */
    private int dof10(KeyEvent evt) {
        if (child_Id == 0) {
            child_Id = codingData_F6.childId;
        }
        AddEditQuery queryDialog;
        queryDialog = new AddEditQuery(this, projectMap, fieldName, bates_Number, image_Path, whichStatus, child_Id, batch_Id);
        queryDialog.setModal(true);
        queryDialog.show();
        return 0;
    }

    /**
     * For L1 verification dialog 
     * @param evt
     * @return
     */
    private int doVerify(KeyEvent evt) {
        Object getComponent =  evt.getSource();
        LTextField field = (LTextField)getComponent;
        boolean checkIsEnabled = field.isEditable();
        System.out.println("LTextField=========="+ checkIsEnabled);
        ShowVerifyDialog verifyDialog =null;              
        if (codingData == null) {
            if(checkIsEnabled){
                verifyDialog = new ShowVerifyDialog(this, getText(), codingData_F6.valueMap, fieldName, codingData_F6.childId, whichStatus, "LTextField");
                verifyDialog.setLocationRelativeTo(this);
                verifyDialog.setModal(true);
                verifyDialog.show();
            }             
            //verifyDialog = new ShowVerifyDialog(this, getText(), codingData_F6.valueMap, fieldName, codingData_F6.childId, whichStatus, "LTextField");
        } else {
            if(checkIsEnabled){
              verifyDialog = new ShowVerifyDialog(this, getText(), codingData.valueMap, fieldName, codingData.childId, whichStatus, "LTextField");
              verifyDialog.setLocationRelativeTo(this);
              verifyDialog.setModal(true);
              verifyDialog.show();   
            }                    
        }
        return 0;
    }

    protected Document createDefaultModel() {
        return new CheckedDocument();
    }

    public void setEnabled(boolean flag) {
        // $$$$$$$ replaced setEnabled by setEditable -- probably should be fixed everywhere
        super.setEditable(flag);
        setForeground(Color.black);
        if (flag) {
            setBackground(Color.white);
        } else {
            setBackground(null);
        }
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
            lColumnWidth = metrics.charWidth('W');
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
        return getPreferredSize(getColumns());
    }

    /**
     * Calculate size for specified number of columns.
     * Used to calculate size in subclasses, such as
     * LIntegerField, allowing for extra characters.
     */
    protected Dimension getPreferredSize(int columns) {
        Dimension size = super.getPreferredSize();
        if (this.getParent() instanceof beans.LTextButton && columns > 22) {
            columns = 22;
        }
        if (columns != 0) {
            size.width = 6 + (columns) * getColumnWidth();
        }
        return size;
    }

    public JToolTip createToolTip() {
        return new LMultiLineToolTip();
    }

    public void setToolTipText(String tip) {
        this.createToolTip();
        super.setToolTipText(tip);
    }

    public void stripLeadingSpace() {
        String text = getText();
        while (text.startsWith(" ")) {
            text = text.substring(1);
        }
        setText(text);
    }

    public class FocusText implements FocusListener {

        public void focusGained(FocusEvent e) {
            try {
                SplitPaneViewer.clearViewerStatus(LTextField.this);
                selectAll();
            } catch (Throwable t) {
                Log.quit(t);
            }
        }

        public void focusLost(FocusEvent e) {
            try {
                setCaretPosition(0);
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    }

    public void requestFocus() {
        super.requestFocus();
    }

    protected class CheckedDocument extends PlainDocument {

        boolean isReplacing = false;

        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {
            String checkedString = checkText(str, offs);
            if (checkedString == null || (this.getLength() + checkedString.length() > getColumns() && getColumns() > 0) //&& userEntered
                    ) {
                // Refuse the insertion and beep to notify user
                Toolkit.getDefaultToolkit().beep();
                Log.print("BEEP> LTextField: insertString(" + offs + ",'" + str + "') " + this.getLength());
            } else {
                String oldText = LTextField.this.getText();
                super.insertString(offs, checkedString, a);
                //Log.print("(LTextField) changed");
                if (!isReplacing) {
                    setChanged(true);
                    firePropertyChange("text", oldText, LTextField.super.getText());
                }
            }
        }

        public void remove(int offs, int len)
                throws BadLocationException {
            String oldText = LTextField.this.getText();
            super.remove(offs, len);
            SplitPaneViewer.clearViewerStatus(LTextField.this);
            //Log.print("setChanged from remove true");
            if (!isReplacing) {
                setChanged(true);
                firePropertyChange("text", oldText, LTextField.super.getText());
            }
        }

        /**
         * Overrides PlainDocument.replace, to suppress separate change operations
         * on delete and insert.  This avoids temporarily seeing an invalid
         * field when setText is called, thus possibly losing a button press.
         */
        public void replace(int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            isReplacing = true;
            String oldText = LTextField.this.getText();
            super.replace(offset, length, text, attrs);
            isReplacing = false;
            setChanged(true);
            firePropertyChange("text", oldText, LTextField.super.getText());
        }
    }

    public void setText(String text) {
        //Log.print("(LTextField.setText(" + text + ")");
        theImpl.checkChanged(text);
        super.setText(text);
        //Log.print("(LTextField).setText " + text);
        if (text != null) {
            // if the text is longer than the receiving field,
            // set a tool tip to show all of it.
            if (text.length() > getColumns() && getColumns() > 0) {
                setToolTipText(text);
            }
        }
        //Log.print("LTF setText " + text );
        if (super.hasFocus()) {
            super.selectAll();
        }
    }

    public void clearField() {
        setText("");
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
    protected String checkText(String str, int offs) {
        return str;
    }

    // Delegate common LField calls to an LFieldImpl
    protected LFieldImpl theImpl = new LFieldImpl(this);

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

    public void setChildId(int childId) {
        this.child_Id = childId;
    }

    public int getChildId() {
        return child_Id;
    }

    public void setComboBox(LComboBox combo) {
        theImpl.setComboBox(combo);
    }

    public LComboBox getComboBox() {
        return theImpl.getComboBox();
    }
}
