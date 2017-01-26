/* $Header: /home/common/cvsarea/ibase/dia/src/beans/LFormattedTextField.java,v 1.13.2.7 2006/02/16 15:56:45 nancy Exp $ */
package beans;

import client.ClientTask;
import client.TaskSelectFieldDescription;
import common.CodingData;
import common.Log;
import common.edit.ProjectMapper;
import java.awt.Component;

import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.DefaultFormatterFactory;

/**
 * Provides an extension of JFormattedTextField which allows checking
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
 */
public class LFormattedTextField extends JFormattedTextField implements LField {

    private final String PRINTABLE_CHARS = "\\p{Print}";
    private final String UNPRINTABLE_CHARS = "\\P{Print}";
    private boolean include_unprintable = true;

    // cached value of column width
    protected int lColumnWidth = 0;

    //private beans.LFormattedTextField.FocusText focusText = new beans.LFormattedTextField.FocusText();
    private boolean holdChanged;
    private static MaskFieldVerifier maskFieldVerifier;
    private javax.swing.text.MaskFormatter mf;
    private String mask;
    private String validChars;
    private String invalidChars;
    private int length;
    private int i;
    private int projectId;
    private String fieldName;
    private String currentMask = null;
    private String documentNumber;
    private String imagePath;
    private String whichStatus;
    private CodingData codingData;
    private String text;
    private String bates_Number;
    private String image_Path;
    private int child_Id;
    private int batch_Id;
    private ProjectMapper projectMap;
    private static CodingData codingData_F6;    

    public LFormattedTextField(int length, String mask, String validChars, String invalidChars, int projectId,
            String fieldName, CodingData codingData, String whichStatus) {
        this(length, "", mask, validChars, invalidChars, projectId, fieldName, codingData, whichStatus);
    }

    public LFormattedTextField(String text, String mask) {
        this(0, text, mask, "", "", 0, "", null, "");
    }

    /*   public LFormattedTextField(int length, String mask, String validChars, String invalidChars, int projectId, String fieldName);
    {
    this(length, "", mask, validChars, invalidChars, projectId, fieldName);
    }
    
    public LFormattedTextField(String text, String mask)
    {
    this(0, text, mask, "", "", 0, "" );
    }*/
    /**
     * Constructs a new empty LFormattedTextField with the specified mask
     *
     * @param mask - the string to use as a mask
     */
    public LFormattedTextField(String mask) {
        this(0, null, mask, "", "", 0, "", null, "");
        setHorizontalAlignment(javax.swing.JTextField.LEFT);
    }

    public LFormattedTextField(CodingData codingdata) {
        codingData_F6 = codingdata;
    }

//public String imagePath ="";
    /**
     * Constructs a new LFormattedTextField initialized with the specified text
     * and maximum length.
     *
     * @param length the length of the field to mask
     * @param text the text to be displayed, or null
     * @param mask - the user-entered mask or "" to be filled with *
     * @param validChars - characters allowed in the field
     * @param invalidChars - characters not allowed in the field
     */
    public LFormattedTextField(int length, String text, String mask, String validChars, String invalidChars, 
            int projectId, String fieldName, CodingData codingData, String whichStatus) {
        super();
        assert length > 0 || mask.length() > 0; // must have a length for this component

        // "\p{print}" is the regexp for printable characters (note the lower case p).
        // That string as part of validChars means only printable characters a-zA-Z0-9 and space
        // can be included in the field.
        //if (validChars.indexOf("\\p") > -1) {
        //    include_unprintable = false;
        //    // remove the "\\p" from validChars
        //    i = validChars.indexOf("\\p");
        //    validChars = validChars.substring(0, i);
        //    if (i+3 < validChars.length()) {
        //        validChars = validChars.substring(i+3);
        //    }
        //}
        // now, do the same for invalid characters
        // "\P{print}" is the regexp for unprintable characters (note the upper case p).
        // That string as part of validChars means only printable characters a-zA-Z0-9 and space
        // can be included in the field.
        if (invalidChars.indexOf("\\u") > -1) {
            include_unprintable = false;
            // remove the "\\u" from invalidChars
            i = invalidChars.indexOf("\\u");
            invalidChars = invalidChars.substring(0, i);
            if (i + 3 < invalidChars.length()) {
                invalidChars = invalidChars.substring(i + 3);
            //Log.print("(LFormatted) \\u removed: " + invalidChars);
            }
        }

        this.mask = mask;
        this.validChars = validChars;
        this.invalidChars = invalidChars;
        this.length = length;
        this.projectId = projectId;
        this.fieldName = fieldName;
        this.codingData = codingData;
        this.whichStatus = whichStatus;
        this.text = text;
        // this.documentNumber = documentNumber;
        // this.imagePath = imagePath;
        //  this.whichStatus = whichStatus;
        try {
            if (mask.equals("yyyy-MM-dd")) {
                java.text.DateFormat fmt = new java.text.SimpleDateFormat(mask);
                setFormatter(new javax.swing.text.DateFormatter(fmt));
                length = 10;
            } else {
                createMaskFormatter();
            }
            setColumns(length);
            setText(text);
            currentMask = getText();
            //addFocusListener(focusText);
            setFocusLostBehavior(javax.swing.JFormattedTextField.COMMIT);
            setInputVerifier(new MaskFieldVerifier());
            addKeyListener(new java.awt.event.KeyAdapter() {

                public void keyPressed(java.awt.event.KeyEvent evt) {
                    textFieldKeyPressed(evt);
                }
            });
        //Log.print("(LFTF) formatter: " + getFormatter() + "/" + getFormatterFactory());
        } catch (Throwable t) {
            com.lexpar.util.Log.quit(t);
        }
    }

    /**
     * Constructs a new LFormattedTextField initialized with the specified text
     * and maximum length.
     *
     * @param length the length of the field to mask
     * @param text the text to be displayed, or null
     * @param mask - the user-entered mask or "" to be filled with *
     * @param validChars - characters allowed in the field
     * @param invalidChars - characters not allowed in the field
     */
    public LFormattedTextField(int length, String text, String mask, String validChars, String invalidChars, 
            int projectId, String fieldName, CodingData codingData, String whichStatus, String documentNumber,
            String bates_Number, String image_Path, int child_Id, int batch_Id, ProjectMapper projectMap) {
        super();
        assert length > 0 || mask.length() > 0; // must have a length for this component

        // "\p{print}" is the regexp for printable characters (note the lower case p).
        // That string as part of validChars means only printable characters a-zA-Z0-9 and space
        // can be included in the field.
        //if (validChars.indexOf("\\p") > -1) {
        //    include_unprintable = false;
        //    // remove the "\\p" from validChars
        //    i = validChars.indexOf("\\p");
        //    validChars = validChars.substring(0, i);
        //    if (i+3 < validChars.length()) {
        //        validChars = validChars.substring(i+3);
        //    }
        //}
        // now, do the same for invalid characters
        // "\P{print}" is the regexp for unprintable characters (note the upper case p).
        // That string as part of validChars means only printable characters a-zA-Z0-9 and space
        // can be included in the field.
        if (invalidChars.indexOf("\\u") > -1) {
            include_unprintable = false;
            // remove the "\\u" from invalidChars
            i = invalidChars.indexOf("\\u");
            invalidChars = invalidChars.substring(0, i);
            if (i + 3 < invalidChars.length()) {
                invalidChars = invalidChars.substring(i + 3);
            //Log.print("(LFormatted) \\u removed: " + invalidChars);
            }
        }

        this.mask = mask;
        this.validChars = validChars;
        this.invalidChars = invalidChars;
        this.length = length;
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
        // this.documentNumber = documentNumber;
        // this.imagePath = imagePath;
        //  this.whichStatus = whichStatus;
        try {
            if (mask.equals("yyyy-MM-dd")) {
                java.text.DateFormat fmt = new java.text.SimpleDateFormat(mask);
                setFormatter(new javax.swing.text.DateFormatter(fmt));
                length = 10;
            } else {
                createMaskFormatter();
            }
            setColumns(length);
            setText(text);
            currentMask = getText();
            //addFocusListener(focusText);
            setFocusLostBehavior(javax.swing.JFormattedTextField.COMMIT);
            setInputVerifier(new MaskFieldVerifier());
            addKeyListener(new java.awt.event.KeyAdapter() {

                public void keyPressed(java.awt.event.KeyEvent evt) {
                    textFieldKeyPressed(evt);
                }
            });
        //Log.print("(LFTF) formatter: " + getFormatter() + "/" + getFormatterFactory());
        } catch (Throwable t) {
            com.lexpar.util.Log.quit(t);
        }
    }

    public LFormattedTextField(int length, String mask, String validChars, String invalidChars, int projectId, 
            String fieldName, CodingData codingData, String whichStatus, String documentNumber, String bates_Number, 
            String image_Path, int child_Id, int batch_Id, ProjectMapper projectMap) {
        this(length, "", mask, validChars, invalidChars, projectId, fieldName, codingData, whichStatus, documentNumber,
                bates_Number, image_Path, child_Id, batch_Id, projectMap);
    }

    private void textFieldKeyPressed(java.awt.event.KeyEvent evt) {
        try {
            //System.err.println("ITF-->"+evt.paramString());

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
                    //projectModel.setSelectedItem(null);
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

    //For F10(Query Tracker)
    private int dof10(KeyEvent evt) {
        AddEditQuery queryDialog;
        if (child_Id == 0) {
            child_Id = codingData_F6.childId;
        }
        queryDialog = new AddEditQuery(this, projectMap, fieldName, bates_Number, imagePath, whichStatus, child_Id, batch_Id);
        queryDialog.setModal(true);
        queryDialog.show();
        return 0;
    }
    //For L1 verification F6

    private int doVerify(KeyEvent evt) {
        ShowVerifyDialog verifyDialog = null;
         Object getComponent =  evt.getSource();
         LFormattedTextField field = (LFormattedTextField)getComponent;
         boolean checkIsEnabled = field.isEnabled();      
        if (codingData == null) {
           if(!checkIsEnabled){
                    verifyDialog = new ShowVerifyDialog(this, text, codingData_F6.valueMap, fieldName, codingData_F6.childId, 
                                                        whichStatus, "LFormattedTextField");
                    verifyDialog.setLocationRelativeTo(this);
                    verifyDialog.setModal(true);
                    verifyDialog.show();
           }            
        } else {
              if(!checkIsEnabled){
                    verifyDialog = new ShowVerifyDialog(this, text, codingData.valueMap, fieldName, codingData.childId,
                                                        whichStatus, "LFormattedTextField");
                    verifyDialog.setLocationRelativeTo(this);
                    verifyDialog.setModal(true);
                    verifyDialog.show();
              }            
        }
        
        return 0;
    }

    protected javax.swing.text.Document createDefaultModel() {
        //return getDocument();        
        return new beans.LFormattedTextField.CheckedDocument();
    }

    public void setValidCharacters(String str) {
        mf.setValidCharacters(str);
    }

    public void setInvalidCharacters(String str) {
        mf.setInvalidCharacters(str);
    }

    public void setEnabled(boolean flag) {
        super.setEditable(flag);
        setForeground(java.awt.Color.black);
        if (flag) {
            setBackground(java.awt.Color.white);
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
            java.awt.FontMetrics metrics = getFontMetrics(getFont());
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
    public java.awt.Dimension getPreferredSize() {
        // Note.  super synchronizes on getTreeLock().  Shouldn't matter,
        // since we only mess with this on the event thread?
        java.awt.Dimension size = super.getPreferredSize();
        int columns = getColumns();
        if (this.getParent() instanceof beans.LTextButton && columns > 22) {
            columns = 22;
        }
        if (columns != 0) {
            size.width = 6 + (columns) * getColumnWidth();
        }
        return size;
    }

    public javax.swing.JToolTip createToolTip() {
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

    public class FocusText implements java.awt.event.FocusListener {

        public void focusGained(java.awt.event.FocusEvent e) {
            try {
                //Log.print("LTF focus gained");
                holdChanged = isChanged();
                FocusListener[] fl = e.getComponent().getParent().getFocusListeners();
                for (int i = 0; i < fl.length; i++) {
                    fl[i].focusGained(e);
                }
                setChanged(holdChanged);
            } catch (Throwable t) {
                com.lexpar.util.Log.quit(t);
            }
        }

        public void focusLost(java.awt.event.FocusEvent e) {
            try {
                FocusListener[] fl = e.getComponent().getParent().getFocusListeners();
                for (int i = 0; i < fl.length; i++) {
                    fl[i].focusLost(e);
                }
            } catch (Throwable t) {
                com.lexpar.util.Log.quit(t);
            }
        }
    }

    protected class CheckedDocument extends javax.swing.text.PlainDocument {

        public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
            //Log.print("(LFTF.insertString) insert " + str);
            String s = str;
            if (s == null || ((this.getLength() + s.length()) > getColumns() && getColumns() > 0)) {
            } else {
                if (!include_unprintable && !str.matches(PRINTABLE_CHARS)) {
                    // string contains unprintable characters
                    //Log.print("(LFTF.insertString) unprintable " + str);
                    s = str.replaceAll(UNPRINTABLE_CHARS, "");
                }
                super.insertString(offs, s, a);
                checkChanged(s);
            //theImpl.setChanged(true);
            }
        }

        public void remove(int offs, int len) throws javax.swing.text.BadLocationException {
            checkRemoveChanged(offs, len);
            super.remove(offs, len);
            //Log.print("setChanged from remove true");
            //theImpl.setChanged(true);

            checkChanged("");
        }

        public void replace(int offset,
                int length,
                String text,
                javax.swing.text.AttributeSet attrs) throws javax.swing.text.BadLocationException {
            String t = text;
            int l = length;
            //Log.print("(LFTF) replace '" + text + "'/" + mf.getMask()
            //          + "/" + mf.getValidCharacters() + "/" + mf.getInvalidCharacters());
            if (!include_unprintable && !text.matches(PRINTABLE_CHARS)) {
                //Log.print("  ... unprintable '" + text + "'");
                // string contains unprintable characters
                t = text.replaceAll(UNPRINTABLE_CHARS, "");
            //l = text.length();
            }
            //Log.print("  ... use '" + t + "'");
            super.replace(offset, l, t, attrs);
            checkChanged(text);
        }
    }

    private void checkChanged(String text) {
        //Log.print("checking ... " + currentMask + "/" + text);
        if (currentMask != null && text.length() > 0 && !currentMask.equals(super.getText())) {
            //Log.print("setChanged(true) " + currentMask + "/" + text);
            theImpl.setChanged(true);
        }
    }

    private void checkRemoveChanged(int offs, int len) {
        if (currentMask == null) {
            return;
        }
        if (offs == 0 && len == super.getText().length() && !currentMask.equals(super.getText())) {
            theImpl.setChanged(true);
        }
    }

    /**
     * Return the edited text.
     * @return the edited string or an empty string
     */
    public String getText() {
        return super.getText();
    }

    public void setText(String text) {
        //Log.print("(LFormattedTextField.setText(" + text + ")");
        theImpl.checkChanged(text);
        //Log.print("(LFTF.setText) " + text);
        if (text == null || text.equals("")) {
            try {
                createMaskFormatter();
                super.setValue(null);
            } catch (Throwable t) {
                com.lexpar.util.Log.quit(t);
            }
        } else {
            super.setText(text);
        }
        if (text != null) {
            // if the text is longer than the receiving field,
            // set a tool tip to show all of it.
            if (text.length() > getColumns() && getColumns() > 0) {
                setToolTipText(text);
            }
        }
    }

    private void createMaskFormatter() {
        try {
            if (mask.equals("")) {
                // If no mask given, make it * to indicate any character is allowed.                
                String m = "";
                for (int i = 0; i < length; i++) {
                    m = m + "*";
                }
                mf = new javax.swing.text.MaskFormatter(m);
            // never allow invalid characters to be entered
            } else {
                mf = new javax.swing.text.MaskFormatter(mask);
                length = mask.length();
                mf.setPlaceholderCharacter('_');
                mf.setOverwriteMode(true);
                if (length == 0) {
                    length = mask.length();
                }
            }

            mf.setAllowsInvalid(false);
            setFormatterFactory(new DefaultFormatterFactory(mf, mf, mf));
            if (!validChars.equals("")) {
                mf.setValidCharacters(validChars);
            }
            if (!invalidChars.equals("")) {
                mf.setInvalidCharacters(invalidChars);
            }
        //Log.print("(LFTF) formatter just set: " + mf.getMask() + "/" + mf.getValidCharacters()
        //          + "/" + mf.getInvalidCharacters());
        //setFormatter(mf);
        } catch (Throwable t) {
            com.lexpar.util.Log.quit(t);
        }
    }

    public void clearField() {
        //Log.print("(LFTF.clearField)");
        setText("");
    }

    /**
     * Return the currentMask for use in FieldMapper.
     * @return the current mask, containing place holders
     */
    public String getCurrentMask() {
        return currentMask;
    }

    ///**
    // * This method may be overridden to provide checking and modification
    // * of the string to be inserted.  
    // * <p>
    // * If <code>str</code> contains invalid characters, the method
    // * returns <code>null</code>; otherwise it returns a string with
    // * any replacements.
    // *
    // * @param str the text to be inserted (may be empty, but never null)
    // */
    /* protected String checkText(String str, int offs) {
    try {
    String text;
    Log.print("(LFTF.checkText) str=" + str);
    if (offs < getText().length() - 1) {
    text = getText().substring(0, offs +1) + str
    + getText().substring(offs);
    } else {
    text = getText() + str;
    }
    Log.print("(LFTF.checkText) text=" + text);
    mf.stringToValue(text);
    return str;
    } catch (ParseException pe) {
    Log.print("(LFormattedTextField.setText) invalid characters: " + str);
    return null;
    }
    } */

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

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
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
