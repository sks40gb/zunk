/* $Header: /home/common/cvsarea/ibase/dia/src/beans/IbaseTextField.java,v 1.88.2.4 2007/03/27 10:33:08 nancy Exp $ */

/*
 * IbaseTextField.java
 *
 * Created on March 20, 2003, 7:55 PM
 */
package beans;

import client.ClientTask;
import client.TaskSelectFieldDescription;
import common.CodingData;
import common.edit.ProjectMapper;
import model.FieldMapper;
import model.SortedListModel;
import model.ManagedTableModel;
import ui.SplitPaneViewer;
import common.Log;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.ParseException;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import javax.swing.text.SimpleAttributeSet;

/**
 * This is a <code>JTextField</code> associated with an arrow button that can
 * be clicked to show a popup containing a list of currently-selected values and
 * a list of selection options.  The list of optional values may be added to by
 * the user.
 * @author  bill
 */
public class IbaseTextField extends javax.swing.JPanel implements LField {

    /** control debugging for undo */
    final public static boolean DEBUG = LUndoController.DEBUG;
    private static PopupFactory thePopupFactory = PopupFactory.getSharedInstance();
    private AbstractTableModel tablevalueModel;
    private String type;
    private int tablespecId;
    private boolean repeated = false;
    private boolean mandatory = false;
    private boolean userUpdate = false;
    //DBTask dbTask = new DBTask();
    // following two fields only used if the field controls document levels
    // (projectfields.level_field_name)
    private FieldMapper fieldMap = null;
    private CodingData codingData = null;
    private ProjectMapper projectMap = null;
    private boolean didTab = false;
    private boolean significant = false;
    private boolean tableChanged = false;
    private String capitalText;
    private transient Popup thePopup = null;
    private transient beans.IbasePopup popupPanel = null;
    private SortedListModel choiceModel = null;
    private int initialDot = -1; // point where editing begins >0 0 if perm focus

    /** the document used to edit and make selections while the user types */
    public CheckedDocument document = null;
    private javax.swing.text.MaskFormatter mf = null;
    private int popupX = 0;
    private int popupY = 0;
    private int childId;
    private int batchId;
    private int projectId = 0;
    private String mask = "";
    private String validChars = "";
    private String invalidChars = "";
    private String originalTextField = "";
    private String fieldName = null;
    private String batchNumber;
    private String batesNumber;
    private String whichStatus;
    public String imagePath = "";
    public String queryRaised = "";
    
    /** 
     * Creates new form IbaseTextField with no level field processing and no
     * mask and valid/invalid character processing.
     * @param type "name" or "text"
     * @param tablespecId the tablespec.tablespec_id of the current table
     * @param mandatory true if the user must use values listed in the table model
     * @param userUpdate true if the user may update the table model
     * @param repeated true if there can be more than one value entered
     */
    public IbaseTextField() {
        //this.imagePath = imagePath;
    }

    public IbaseTextField(String type, int tablespecId, boolean mandatory, boolean userUpdate, boolean repeated) {


        this(type, tablespecId, mandatory, userUpdate, repeated, null, null, "", "", "Yes", 0, "", "", "", "", "", 0, 0, null);

//      this(type, tablespecId, mandatory, userUpdate, repeated, null, null, "", "", "Yes", 0, "","" ,"","");

    }

    /** 
     * Creates new form IbaseTextField with no <code>fieldMap</code> and
     * no <code>projectMap</code> because level field processing is not needed.
     * @param type "name" or "text"
     * @param tablespecId the tablespec.tablespec_id of the current table
     * @param mandatory true if the user must use values listed in the table model
     * @param userUpdate true if the user may update the table model
     * @param repeated true if there can be more than one value entered
     * @param mask the user-defined mask for this field (projectfields.mask)
     * @param validChars the user-defined valid characters for this field
     * (projectfields.valid_chars)
     * @param invalidChars the user-defined invalid characters for this field
     * (projectfields.invalid_chars)
     */
    public IbaseTextField(String type, int tablespecId, boolean mandatory, boolean userUpdate, boolean repeated, 
            String mask, String validChars, String invalidChars, int projectId, String fieldName, String batchNumber,
            String batesNumber, String whichStatus, String imagepath, int childId, ProjectMapper projectMap, 
            int batchId, CodingData codingData) 
    {
        this(type, tablespecId, mandatory, userUpdate, repeated, null, projectMap, mask, validChars, invalidChars, 
                projectId, fieldName, batchNumber, batesNumber, whichStatus, imagepath, childId, batchId, codingData);


    }

    /** 
     * Creates new form IbaseTextField.
     * @param type "name" or "text"
     * @param tablespecId the tablespec.tablespec_id of the current table
     * @param mandatory true if the user must use values listed in the table model
     * @param userUpdate true if the user may update the table model
     * @param repeated true if there can be more than one value entered
     * @param fieldMap the <code>model.FieldMapper</code> used to set the visibility of
     * fields based on the value in this field (level processing)
     * @param projectMap the <code>common.edt.ProjectMapper</code> used in
     * level processing
     * @param mask the user-defined mask for this field (projectfields.mask)
     * @param validChars the user-defined valid characters for this field
     * (projectfields.valid_chars)
     * @param invalidChars the user-defined invalid characters for this field
     * (projectfields.invalid_chars)
     */
    public IbaseTextField(String type, int tablespecId, boolean mandatory, boolean userUpdate, boolean repeated, 
            FieldMapper fieldMap, ProjectMapper projectMap, String mask, String validChars, String invalidChars, 
            int projectId, String fieldName, String batchNumber, String batesNumber, String whichStatus,
            String imagepath, int childId, int batchId, CodingData codingData)
    {
        //Log.print("(IbaseTextField) type: " + type + " tablespecId: " + tablespecId
        //          + " mandatory: " + mandatory + " userUpdate: " + userUpdate);
        this.type = type;  // is it name, text, numeric...

        this.tablespecId = tablespecId;
        //this.tableMap = tableMap;
        this.repeated = repeated; // field may contain more than one table value

        this.mandatory = mandatory; // table use is required

        this.userUpdate = userUpdate;  // the user may update the table

        this.fieldMap = fieldMap;
        this.projectMap = projectMap;
        checkMask(mask, validChars, invalidChars);
        this.validChars = validChars;
        this.invalidChars = invalidChars;
        this.mask = mask;
        this.projectId = projectId;
        this.fieldName = fieldName;
        this.batchNumber = batchNumber;
        this.batesNumber = batesNumber;
        this.whichStatus = whichStatus;
        this.imagePath = imagepath;
        this.childId = childId;
        this.batchId = batchId;
        this.codingData = codingData;    
        initComponents();
        document = new CheckedDocument();
        textField.setDocument(document);
        textField.setFocusTraversalKeysEnabled(false);
    }

    /**
     * Set the format of maskedTextfield so the user input can be checked
     * for valid and invalid characters.
     * 
     * @see CheckedDocument
     */
    private void checkMask(String mask, String validChars, String invalidChars) {
        if (mask.equals("") && validChars.equals("") && invalidChars.equals("")) {
            return;
        }
        try {
            if (mask.equals("")) {
                // If no mask given, fill with * to indicate any character is allowed.
                mask = "*";
            }
            mf = new javax.swing.text.MaskFormatter(mask);
            if (!validChars.equals("")) {
                validChars = validChars + ",.";
                mf.setValidCharacters(validChars);
            }
            if (!invalidChars.equals("")) {
                invalidChars = invalidChars + ";";
                mf.setInvalidCharacters(invalidChars);
            }            
            mf.setAllowsInvalid(false);
        } catch (Throwable t) {
            com.lexpar.util.Log.quit(t);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        textField = new javax.swing.JTextField();
        theArrow = new beans.ArrowButton();

        setLayout(new java.awt.BorderLayout());

        setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        setFocusable(false);
        addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
                hidePopupOnMove(evt);
            }
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                hidePopupOnMove(evt);
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
                hidePopupOnMove(evt);
            }
        });
        addHierarchyBoundsListener(new java.awt.event.HierarchyBoundsListener() {
            public void ancestorMoved(java.awt.event.HierarchyEvent evt) {
                hidePopupOnResize(evt);
            }
            public void ancestorResized(java.awt.event.HierarchyEvent evt) {
                hidePopupOnResize(evt);
            }
        });

        textField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textFieldFocusLost(evt);
            }
        });
        textField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textFieldKeyPressed(evt);
            }
        });

        add(textField, java.awt.BorderLayout.CENTER);

        theArrow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                theArrowActionPerformed(evt);
            }
        });

        add(theArrow, java.awt.BorderLayout.EAST);

    }//GEN-END:initComponents

    /**
     * Return the underlying <code>JTextField</code>.
     * @return <code>textField</code>
     */
    public JTextField getTextField() {
        return textField;
    }

    private void textFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textFieldKeyPressed
        try {                        
            String text = textField.getText();            
            int textLength = textField.getText().length();
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_TAB:
                    doTab(evt);
                    break;
                case KeyEvent.VK_F1:
                    dof1(evt);
                    break;

                case KeyEvent.VK_F10:
                    if ("Coding".equals(whichStatus) || "CodingQC".equals(whichStatus) || "Admin".equals(whichStatus)) {
                        dof10(evt);
                        break;
                    }
                case KeyEvent.VK_F6:
                    if ("CodingQC".equals(whichStatus) || "Admin".equals(whichStatus) || "Masking".equals(whichStatus)) {
                       boolean checkIsEnabled = textField.isEnabled();
                       if(checkIsEnabled){
                         doVerify(evt);
                         break;
                       }                       
                    }              
                case KeyEvent.VK_SEMICOLON:
                    if (!repeated) {
                        //Log.print("(ITF.keyPressed) " + text);
                        JOptionPane.showMessageDialog((Component) theArrow,
                                "This field may contain only one table value; semicolon is not allowed.",
                                "Field Error",
                                JOptionPane.ERROR_MESSAGE);
                        if (textField.getText().equals("") && initialDot == 0) {
                            // started with the entire textField selected
                            document.uncheckedInsertString(initialDot, text);
                            initialDot = textField.getText().length();
                            textField.selectAll();
                        }
                        return;                   
                    }
                    testAcceptString();
                    break;
                case KeyEvent.VK_ENTER:
                    testAcceptString();
                    break;
                    
                case KeyEvent.VK_BACK_SPACE:
                    // remove typeahead
                    // don't know why, but backspace doesn't work right without it
                    int dot = textField.getCaret().getDot();
                    int mark = textField.getCaret().getMark();
                    if (dot != mark) {
                        document.uncheckedRemove(Math.min(dot, mark), Math.abs(dot - mark));
                        setChanged(true);
                    }
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_DOWN:
                    int listSize = choiceModel == null ? 0 : choiceModel.getSize();
                    if (listSize == 0) {
                        break;
                    }
                    if (thePopup == null) {
                        //Log.print("(ITF) thePopup is null");
                        break;
                    }
                    ListSelectionModel selection = popupPanel.getChoiceSelectionModel();
                    int firstSelection = selection.getMinSelectionIndex();
                    int targetSelection;
                    if (evt.getKeyCode() == KeyEvent.VK_UP) {
                        targetSelection = (firstSelection <= 0 ? 0 : firstSelection - 1);
                    } else { // since (evt.getKeyCode() = KeyEvent.VK_DOWN)

                        targetSelection = (firstSelection < 0 || firstSelection == listSize - 1 ? listSize - 1 : firstSelection + 1);
                    }
                    popupPanel.setSelectionInterval(targetSelection, targetSelection);
                    document.uncheckedRemove(initialDot, textLength - initialDot);
                    document.uncheckedInsertString(initialDot, ((String) choiceModel.getElementAt(targetSelection)).trim());
                    setChanged(true);
                    break;
                case KeyEvent.VK_L:
                    // Alt+L = show popup
                    if (evt.getModifiers() == InputEvent.ALT_MASK) {
                        Log.print("(ITextField) showPopup()");
                        showPopup();
                    }
                    break;
                case KeyEvent.VK_O:
                    // Alt+O = press Ok button
                    if (evt.getModifiers() == InputEvent.ALT_MASK && popupPanel.isShowing()) {
                        popupPanel.getOkButton().doClick();
                    }
                    break;
                case KeyEvent.VK_PLUS:
                    // Alt++ = add text field to value list
                    int onmask = KeyEvent.SHIFT_DOWN_MASK | KeyEvent.ALT_DOWN_MASK;
                    if (evt.getModifiersEx() == onmask) {
                        //Log.print("(ITextField) add text item");
                        addTextField(popupPanel.getChoiceList().getSelectedIndex());
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    //Log.print("(ITF).escape " + originalTextField);
                    document.uncheckedRemove(initialDot, textLength - initialDot);
                    hidePopup();
                    initialDot = textField.getText().length();
                    break;
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }//GEN-LAST:event_textFieldKeyPressed

    private void testAcceptString() {       
        int textLength = 0;
        if (acceptStringElement(null)) {
            if (popupPanel != null) {
                popupPanel.setChoiceSelection(null);
            }
            if (textField.getText() == null) {
                setText("");
            }
            textLength = textField.getText().length();
            if (textLength > 0) {
                if (!textField.getText().endsWith(";") && !textField.getText().endsWith("; ")) {
                    significant = true;
                    if (repeated) {
                        document.uncheckedInsertString(textLength, "; ");
                    }
                    significant = false;
                }
                textLength = textField.getText().length();
            //textField.getCaret().setDot(textLength);
            }
            initialDot = textLength;
            textField.getCaret().setDot(initialDot);
        }
    }

    private void doTab(java.awt.event.KeyEvent evt) {
        try {          
            if (acceptStringElement(evt)) {
                checkDocumentLevel();                
                hidePopup();
                if (evt != null && (evt.getModifiers() & KeyEvent.SHIFT_MASK) != 0) {
                    textField.transferFocusBackward();
                } else {
                    textField.transferFocus();
                }
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    /**
     * If this is a projectfield.level_field_name, check the visibility of the other
     * fields on the screen based on the value entered by the user.
     * This method is called from doTab and LostFocus.  doTab because lostFocus is too late
     * to have the field receiving focus be one of the newly visible fields, and lostFocus
     * because if the user clicks the arrow or another field, checkDocumentLevel needs to happen.
     */
    private void checkDocumentLevel() {        
        if (didTab || fieldMap == null) {
            return;
        }
        didTab = true;
        // remove semicolon and space
        String value = textField.getText().trim();
        if (value.lastIndexOf(";") > -1) {
            value = value.substring(0, value.lastIndexOf(";"));
        }
        int i;
        for (i = 0; i < tablevalueModel.getRowCount(); i++) {
            if (((String) tablevalueModel.getValueAt(i, 0)).equals(value)) {
                break;
            }
        }
        // Log.print("(cumentLevelVerifier.verify) " + name + "/" + value
        //            + "/" + i);
        if (i < tablevalueModel.getRowCount()) {            
            fieldMap.setFieldsVisible(projectMap, Integer.parseInt((String) tablevalueModel.getValueAt(i, 2)));
        }
    }

    private boolean acceptStringElement(java.awt.event.ComponentEvent evt) {
        int textLength = textField.getText().length();
        //Log.print("(ITF).acceptStringElement " + textField.getText() + " mandatory is " + mandatory
        //          + "/" + textLength + "/" + initialDot);
        if (textLength > initialDot) {
            int row = choiceModel.containsIgnoreCase(textField.getText().substring(initialDot));
            if (row > -1) {
                // The text already exists in the model -- use it.
                // Remove the text the user typed.
                // Add the text from the list to retain capitalization.
                capitalText = ((String) choiceModel.elementAt(row)).trim();
            } else {
                capitalText = capitalizeText(textField.getText().substring(initialDot));
            }
            document.uncheckedRemove(initialDot, textField.getText().length() - initialDot);
            if (repeated) {
                capitalText = capitalText + "; ";
            }
            document.uncheckedInsertString(initialDot, capitalText);
        //Log.print("(ITF).acceptStringElement " + initialDot + "/" + capitalText
        //          + "/" + textField.getText());
        // show the selected items
        }
        if (popupPanel != null && !mandatory) {  // This is done in checkMandatory().
            //Log.print("(ITF).acceptStringElement setValues");

            popupPanel.setValuesFromString(textField.getText());           
            setTextFromValueList(); // to reload without dups

        }
        initialDot = textField.getText().length();
        if (mandatory) {
            return checkMandatory(evt, textField.getText());
        }
        return true;
    }

    /**
     * Check to see that all values in <code>textField</code> exist in the
     * data model.
     * @param evt the event that caused this method to be invoked
     * @param str the semicolon-separated value string
     */
    public boolean checkMandatory(java.awt.event.ComponentEvent evt, String str) {
        if (!this.isShowing()) {
            // user closed form with popup showing
            return false;
        }
        if (!(evt instanceof FocusEvent)) {
            textField.requestFocus(); // for entry from crossFieldEdits

        }
        if (popupPanel == null) {
            showPopup();
        }
        popupPanel.setValuesFromString(str);

        setTextFromValueList(); // to reload without dups

        ArrayList errorList = compareList();
        //Log.print("(ITF).checkMandatory " + errorList.size());
        // Use of the table is mandatory -- don't let the insert happen
        if (errorList.size() > 0) {
            int[] intArray = new int[errorList.size()];
            for (int i = 0; i < intArray.length; i++) {
                intArray[i] = ((Integer) errorList.get(i)).intValue();
            }
            showPopup();
            if (repeated) {
                (popupPanel.getValueList()).setSelectedIndices(intArray);
                Object[] options = {"Delete", "Cancel"};
                int response = JOptionPane.showOptionDialog(textField,
                        "All values in this field must be selected from the list." +
                        "\nThe highlighted items are not in the selection list." +
                        "\n Your options are: " +
                        "\n            \"Delete\" to remove the highlighted items, which are not in the list," +
                        "\n            \"Cancel\" to manually remove the highlighted items.",
                        "Error",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null,
                        options,
                        options[0]);
                if (response == JOptionPane.YES_OPTION) {
                    // User said delete them.
                    popupPanel.removeValues(popupPanel.getValueList().getSelectedIndices());
                    significant = true;                  
                    setTextFromValueList();
                    significant = false;
                } else {
                    textField.requestFocus();
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog((Component) theArrow,
                        "All values in this field must be selected from the list.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                textField.selectAll();
                return false;
            }
        }
        return true;
    }

    /**
     * Compare the selected values to the data model to see if any do not exist.
     * @return an <code>arrayList</code> of indices where the selected value
     * does not exist in the data model
     */
    protected ArrayList compareList() {
        ArrayList result = new ArrayList();
        int pos;
        DefaultListModel model = popupPanel.getValueModel();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            pos = popupPanel.getModel().containsIgnoreCase(model.getElementAt(i));
            if (pos < 0) {
                //Log.print("(IbaseTextField).compareLists " + pos + '/' + i + "/" + model.getElementAt(i));
                result.ensureCapacity(result.size() + 1);
                result.add((Object) new Integer(i));
            }
        }
        return result;
    }

    private void textFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldFocusLost
        try {           
            if (!evt.isTemporary()) {
                Component parent = this.getParent();
                Component parentSPV;

                // Renew the default actions for the up and down arrows in SplitPaneViewer
                // that may have been reset in an ibaseTextField.
                while (parent != null && !(parent instanceof JScrollPane)) {
                    // looking for midScrollPane in SplitPaneViewer
                    parent = parent.getParent();
                }
                if (parent != null) {
                    parentSPV = parent;
                    while (parentSPV != null && !(parentSPV instanceof SplitPaneViewer)) {
                        // looking for SplitPaneViewer
                        parentSPV = parentSPV.getParent();
                    }
                    if (parentSPV != null) {
                        ((JScrollPane) parent).getActionMap().put("doUp", ((SplitPaneViewer) parentSPV).getUpAction());
                        ((JScrollPane) parent).getActionMap().put("doDn", ((SplitPaneViewer) parentSPV).getDownAction());
                        ((JScrollPane) parent).getActionMap().put("doPageUp", ((SplitPaneViewer) parentSPV).getPageUpAction());
                        ((JScrollPane) parent).getActionMap().put("doPageDn", ((SplitPaneViewer) parentSPV).getPageDownAction());
                    }
                }
                if (acceptStringElement(evt)) {
                    checkDocumentLevel();
                    didTab = false; // see checkDocumentLevel()
                    //Log.print("(ITF) textFieldFocusLost");

                    hidePopup();
                    int textLength = textField.getText().length();
                    if (textLength > 0 && textField.getText().endsWith("; ")) {
                        // remove the semicolon
                        significant = true;
                        document.uncheckedRemove(textLength - 2, 2);
                    }
                    initialDot = -1;
                }
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
        theImpl.checkChanged(textField.getText());
    }//GEN-LAST:event_textFieldFocusLost

    /**
     * When an IbaseTextField receives focus, select the text in the field and save some
     * data, including the length and initial cursor position, then make sure it's in
     * the scrollpane viewport.
     */
    private void textFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textFieldFocusGained
        try {
            //Log.print("(ITF.textFieldFocusGained) " + originalTextField);
            if (initialDot < 0) {
                if (textField.getText() == null) {
                    setText("");
                }
                int textLength = textField.getText().length();
                initialDot = textLength;
                textField.selectAll();
            //Log.print("(ITF).textFieldFocusGained: initialDot " + initialDot);
            }

            Component comp = this;
            Component parent = comp.getParent();
            // Check the scrollPane viewport.
            while (parent != null && !(parent instanceof JViewport)) {
                // looking for the viewport of midScrollPane in SplitPaneViewer
                parent = parent.getParent();
            }
            if (parent == null) {
                return; // no need to scroll the viewport

            }
            Rectangle rectViewport = ((JViewport) parent).getViewRect();
            //Log.print("(ITF.textFieldFocusGained) " + comp + "////" + rectViewport + "/" + comp.getY());
            if (!rectViewport.contains(new Point(0, (int) comp.getY())) || !rectViewport.contains(new Point(0, 
                    (int) comp.getY() + 21))) {
                //Log.print("ITF.focuscomponent) setting view");
                Point p = new Point(0, comp.getLocation().y - 8);
                ((JViewport) parent).setViewPosition(p);
                ((JViewport) parent).repaint();
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }//GEN-LAST:event_textFieldFocusGained

    private void hidePopupOnResize(java.awt.event.HierarchyEvent evt) {//GEN-FIRST:event_hidePopupOnResize
        // Kill the popup when the field is resized
        hidePopup();
    }//GEN-LAST:event_hidePopupOnResize

    private void hidePopupOnMove(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_hidePopupOnMove
        // Kill the popup when the field is moved
        hidePopup();
    }//GEN-LAST:event_hidePopupOnMove

    private void theArrowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_theArrowActionPerformed
        togglePopup();
    }//GEN-LAST:event_theArrowActionPerformed

    /**
     * User has selected one or more list items from the popup and clicked
     * Ok -- pass the selections to popupPanel.addValues, then TAB.
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            //if (saveDataCheck()) {
            if (textField.getText().length() > 0 && !repeated) {
                // delete the existing value before adding the selected value
                if (((JList) popupPanel.getValueList()).getModel().getSize() > 0) {
                    popupPanel.removeValues(new int[]{0});
                }        
            }
            popupPanel.addValues(popupPanel.getChoiceList().getSelectedValues());            
            setTextFromValueList();
            if (!repeated) {
                textField.selectAll();
            }
            textField.requestFocus();
            doTab(null);
            
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    /**
     * From IbasePopup, user clicked the add button to add a value to
     * the text list.
     */
    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int i = popupPanel.getChoiceList().getSelectedIndex();
            String originalText = (String) choiceModel.elementAt(i);
            AddEditTextFieldDialog select = new AddEditTextFieldDialog((Component) textField, type, originalText,
                    /* level-> */ -1, tablespecId, "edit", /* model_tablespecId */ 0, /* model_value */ "",new ArrayList(),true,true);
            select.setModal(true);
            select.show();

            Object[] result = select.getSelection();
            if (result != null) {
                // remove the old value from the valueList
                popupPanel.removeValue(originalText);
                // add the new value to the valueList
                popupPanel.addValues(result);
                // set textField to reflect the valueModel values -- no dups               
                setTextFromValueList();
                // remove the original value from the choice model
                if ((i = choiceModel.containsIgnoreCase(originalText)) > -1) {
                    choiceModel.removeElementAt(i);
                }
                // add values to the choiceModel
                for (i = 0; i < result.length; i++) {
                    choiceModel.add((String) result[i]);
                }
                textField.requestFocus();
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    /**
     * From IbasePopup, user clicked the add button to add a value to
     * the text list.
     */
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int textLength = textField.getText().length();
            if (textLength > initialDot) {
                // Starting with the text the user has already typed,
                // show the add text dialog.
                String text = textField.getText().substring(initialDot);
                // remove the text the user typed -- will be replaced
                // by the result of the addTextField dialog.
                document.uncheckedRemove(initialDot, textField.getText().length() - initialDot);
                addTextField(-1, text);

            } else {
                // start with blank dialog
                addTextField(-1);
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void addTextField(int row) {
        String text;
        if (row < 0) {
            text = "";
        } else {
            text = (String) popupPanel.getModel().getElementAt(row);
        }
        addTextField(row, text);
    }

    private void addTextField(int row, String text) {
        //Log.print("(ITF).addTextField " + row + "/" + text);
        AddEditTextFieldDialog select = new AddEditTextFieldDialog((Component) textField, type, text,
                /* level-> */ -1, tablespecId, "add", /* model_tablespecId */ 0, /* model_value */ "",new ArrayList(),true,true);
        select.setModal(true);
        select.show();

        Object[] result = select.getSelection();
        if (result != null) {
            // AddEditTextFieldDialog returns the name and the level for every name,
            // therefore, remove the level entry for the popupPanel.
            ArrayList list = new ArrayList(result.length / 2);
            for (int i = 0; i < result.length; i = i + 2) {
                list.add(result[i]);
            }
            popupPanel.addValues(list.toArray());
            // set textField to reflect the valueModel values -- no dups           
            setTextFromValueList();
            // add values to the choiceModel
            for (int i = 0; i < result.length; i++) {
                choiceModel.add((String) result[i]);
            }
            textField.requestFocus();
        }
    }

    /**
     * Set the textField back to the value it had when IbasePopup was entered
     * and hide the popup.
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            //if (saveDataCheck()) {
            setText(originalTextField);
            //Log.print("(ITF) cancel");
            hidePopup();
            initialDot = textField.getText().length();
            textField.requestFocus();
        //}
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    /**
     * User has selected one or more list items from the popup and clicked
     * Select or Ok -- pass the selections to popupPanel.addValues.
     */
    private void selectButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (textField.getText().length() > 0 && !repeated) {                
                // delete the existing value before adding the selected value
                if (((JList) popupPanel.getValueList()).getModel().getSize() > 0) {
                    popupPanel.removeValues(new int[]{0});
                }         
            }
            popupPanel.addValues(popupPanel.getChoiceList().getSelectedValues());
            setChanged(true);
            //Log.print("(ITF).selectButton added values " + (popupPanel.getChoiceList().getSelectedValues()).length);           
            setTextFromValueList();
            if (!repeated) {
                textField.selectAll();
            }
            textField.requestFocus();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    /**
     * User clicked the Delete button -- call removeValues with the selected
     * row to have it removed.
     */
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            popupPanel.removeValues(popupPanel.getValueList().getSelectedIndices());
            setChanged(true);            
            setTextFromValueList();
            textField.requestFocus();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    /**
     * User clicked the Save button -- call saveTableValues to save the edits 
     * to the server.
     */
    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            //if (crossFieldEdit()) {
            //ts = tableMap.getTableSpec(tableName);
            //tableMap.saveTableValues(tableName, popupPanel.getModel());

            tableChanged = false;
            textField.requestFocus();
        //}
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void moveTextInValueList(int selectedIndex, int insertIndex) {
        if (selectedIndex > -1) {
            Object selectedObject =
                    ((JList) popupPanel.getValueList()).getSelectedValue();
            ((DefaultListModel) ((JList) popupPanel.getValueList()).getModel()).remove(selectedIndex);
            ((DefaultListModel) ((JList) popupPanel.getValueList()).getModel()).add((insertIndex), selectedObject);            
            setTextFromValueList();
        }
    }

    private void moveUpButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedIndex =
                ((JList) popupPanel.getValueList()).getSelectedIndex();
        moveTextInValueList(selectedIndex, (selectedIndex - 1));
    }

    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {
        int selectedIndex =
                ((JList) popupPanel.getValueList()).getSelectedIndex();
        moveTextInValueList(selectedIndex, (selectedIndex + 1));
    }

    /**
     * User double-clicked a row -- call removeValues with the selected
     * row to have it removed.
     */
    private void valueListMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() > 1) {
            deleteButtonActionPerformed(null);     
        }
        textField.requestFocus();
    }

    /**
     * User double-clicked a row -- call addValues with the selected
     * value to have it added.
     */
    private void choiceListMouseClicked(java.awt.event.MouseEvent evt) {
        if (evt.getClickCount() > 1) {
            selectButtonActionPerformed(null);      
        }
        textField.requestFocus();
    }

    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        hidePopup();
        if ((textField.getText() != null) && (!textField.getText().trim().equals(""))) {
            textField.transferFocus();
        }
    }
    // ends here

    private void tableMandatoryLabelMouseReleased(java.awt.event.MouseEvent evt) {
        popupX = popupX + evt.getX();
        popupY = popupY + evt.getY();
        thePopup.hide();
        popupPanel.setBounds(popupX, popupY, popupPanel.getHeight(), popupPanel.getWidth());
        thePopup = thePopupFactory.getPopup(this, popupPanel, popupX, popupY);
        thePopup.show();
    }

    private void tableMandatoryLabelMouseEntered(java.awt.event.MouseEvent evt) {
        popupPanel.tableMandatoryLabel.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        popupPanel.tableMandatoryLabel.setForeground(Color.RED.darker());
    }

    private void tableMandatoryLabelMouseExited(java.awt.event.MouseEvent evt) {
        popupPanel.tableMandatoryLabel.setForeground(Color.blue.darker());
    }
    // ends here

    /**
     * Create a semicolon separated string from the selected values.
     */
    private void setTextFromValueList() {
        String text = "";
        for (int i = 0; i < popupPanel.getValueModel().getSize(); i++) {
            text = text + popupPanel.getValueModel().getElementAt(i);         
            if (repeated) {
                text = text + "; ";
            }
        }
        //significant = true;
        document.uncheckedRemove(0, textField.getText().length());
        //significant = true;
        document.uncheckedInsertString(0, text);
        initialDot = textField.getText().length();
    //Log.print("(ITF) setTextFromValueList " + initialDot + "/" + text);
    }

    private void showPopup() {
        if (!isPopupVisible()) {
            Component parent = this.getParent();
            while (parent != null && !(parent instanceof JScrollPane)) {
                // looking for midScrollPane of SplitPaneViewer
                parent = parent.getParent();
            }
            if (parent != null) {
                // Disable the up and down arrows of the scrollpane while 
                // the popup of ibaseTextfield is visible.  This has no effect on
                // the recipient list in ComposeDialog in the mail system.
                ((JScrollPane) parent).getActionMap().put("doUp", null);
                ((JScrollPane) parent).getActionMap().put("doDn", null);
                ((JScrollPane) parent).getActionMap().put("doPageUp", null);
                ((JScrollPane) parent).getActionMap().put("doPageDn", null);
            }

            if (popupPanel == null) {
                //Log.print("(IbaseTextField).showPopup: create new popup");
                popupPanel = new IbasePopup(userUpdate, repeated); // shared instance ???

                if (!userUpdate) {
                    //popupPanel.getSaveButton().setEnabled(false);
                    popupPanel.getAddButton().setEnabled(false);
                    popupPanel.getEditButton().setEnabled(false);
                } else {
                    popupPanel.getAddButton().setEnabled(true);                
                    popupPanel.getAddButton().addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            //Log.print("(ITF) add Button clicked " + evt.toString());
                            addButtonActionPerformed(evt);
                        }
                    });
                    popupPanel.getEditButton().addActionListener(new java.awt.event.ActionListener() {

                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            //Log.print("(ITF) edit Button clicked " + evt.toString());
                            editButtonActionPerformed(evt);
                        }
                    });
                }               
                popupPanel.getOkButton().addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        //Log.print("(ITF) ok Button clicked " + evt.toString());
                        okButtonActionPerformed(evt);
                    }
                });
                popupPanel.getSelectButton().addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        //Log.print("(ITF) select Button clicked " + evt.toString());
                        selectButtonActionPerformed(evt);
                    }
                });
                popupPanel.getDeleteButton().addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        deleteButtonActionPerformed(evt);
                    }
                });

                popupPanel.getMoveUpButton().addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        moveUpButtonActionPerformed(evt);
                    }
                });
                popupPanel.getDownButton().addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        downButtonActionPerformed(evt);
                    }
                });
                popupPanel.getValueList().addMouseListener(new java.awt.event.MouseAdapter() {

                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        valueListMouseClicked(evt);
                    }
                });
                popupPanel.getChoiceList().addMouseListener(new java.awt.event.MouseAdapter() {

                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        choiceListMouseClicked(evt);
                    }
                });
                popupPanel.getCloseButton().addActionListener(new java.awt.event.ActionListener() {

                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        closeButtonActionPerformed(evt);
                    }
                });
                popupPanel.tableMandatoryLabel.addMouseListener(new java.awt.event.MouseAdapter() {

                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                        tableMandatoryLabelMouseEntered(evt);
                    }

                    public void mouseExited(java.awt.event.MouseEvent evt) {
                        tableMandatoryLabelMouseExited(evt);
                    }

                    public void mouseReleased(java.awt.event.MouseEvent evt) {
                        tableMandatoryLabelMouseReleased(evt);
                    }
                });
            }
            //Log.print("(IbaseTextField).showPopup: setModel");
            choiceModel = getChoiceModel();
            popupPanel.setModel(choiceModel);
            popupPanel.setFocusable(false);
            if (mandatory) {
                popupPanel.tableMandatoryLabel.setText("List use mandatory.");
            } else {
                popupPanel.tableMandatoryLabel.setText("");
            }
            popupPanel.tableMandatoryLabel.setFont(new Font("Dialog", Font.BOLD, 12));
            popupPanel.tableMandatoryLabel.setForeground(Color.blue.darker());
            // Determine location of popup.  Just below the text, unless it
            // would go off the bottom of the screen, in which case, just above.
            // There's an adjustment of -2; I don't know why, but it gets it in the right place!
            Point textFieldLocation = textField.getLocationOnScreen();
            int textHeight = this.getHeight();
            int popupX = -2 + (int) textFieldLocation.getX();
            int popupY = -2 + textHeight + (int) textFieldLocation.getY();

            // Determine if popup fits below JTextField.  If not, put it above.
            // Note.  screenInsets.bottom accounts for always-on-top Windows toolbar
            Toolkit defaultToolkit = Toolkit.getDefaultToolkit();
            Dimension screenSize = defaultToolkit.getScreenSize();
            GraphicsConfiguration gc = textField.getGraphicsConfiguration();
            int bottomInset = (gc == null
                    ? 0
                    : defaultToolkit.getScreenInsets(gc).bottom);
            int popupHeight = popupPanel.getPreferredSize().height;
            if (popupY + popupHeight > screenSize.height - bottomInset) {
                popupY = -2 + (int) textFieldLocation.getY() - popupHeight;
            }

            // must set values every time in case of deleted text
            //if (! textField.getText().equals("")) {
            //Log.print("(ITF.showPopup) text not empty");
            popupPanel.setValuesFromString(this.getText());          
            setTextFromValueList(); // to reload without dups
            //}

            popupPanel.getValueList().setCellRenderer(new ValueCellRenderer());

            thePopup = thePopupFactory.getPopup(this, popupPanel, popupX, popupY);
            thePopup.show();
            // following is done in focusGained
            //originalTextField = textField.getText();
            if (type.equals("mail")) {
                // user can't change values of mail recipient list
                popupPanel.getAddButton().setEnabled(false);
                popupPanel.getEditButton().setEnabled(false);
            }
        }
        //Log.print("(IbaseTextField).showPopup: Should focus textField");
        textField.requestFocus();
    }

    private SortedListModel getChoiceModel() {
        //Log.print("(ITF.getChoiceModel) " + ((ManagedTableModel)tablevalueModel).getColumnMaxCount());
        SortedListModel choiceSortedModel = new SortedListModel();
        for (int i = 0; i < tablevalueModel.getRowCount(); i++) {
            if (tablevalueModel instanceof ManagedTableModel && ((ManagedTableModel) tablevalueModel)
                    .getColumnMaxCount() > 3 && projectMap != null) {             
                // TBD: if it has more than 3 columns, assume(!) it's the filtered model from SPV
                if (((String) tablevalueModel.getValueAt(i, 3)).equals("") || (((IbaseTextField) projectMap
                        .getComponent((String) tablevalueModel.getValueAt(i, 4)))
                        .getText().equals((String) tablevalueModel.getValueAt(i, 3)))) {
                    choiceSortedModel.add(tablevalueModel.getValueAt(i,/* table_value */ 0));
                }
            } else {
                //Log.print("(ITF.getChoiceModel) all rows" );
                choiceSortedModel.add(tablevalueModel.getValueAt(i,/* table_value */ 0));
            }
        }
        return choiceSortedModel;
    }

    /**
     * Called from <code>ui.SplitPaneViewer</code> following creation of all
     * dynamic fields, because the map cannot be set until all fields exist
     * in the <code>projectMap</code>.
     * @param projectMap the <code>common.edt.ProjectMapper</code> used in
     * level processing
     */
    public void setProjectMap(ProjectMapper projectMap) {
        this.projectMap = projectMap;
    }

    /**
     * Identifies components that can be used as "rubber stamps" to paint the
     * cells in a JList.
     */
    class ValueCellRenderer extends JLabel implements ListCellRenderer {

        /**
         * Return a component that has been configured to display the specified
         * value. That component's paint method is then called to "render" the
         * cell. If it is necessary to compute the dimensions of a list because
         * the list cells do not have a fixed size, this method is called to
         * generate a component on which getPreferredSize can be invoked.
         * @param list the JList we're painting
         * @param value the value returned by list.getModel().getElementAt(index)
         * @param index the cell's index
         * @param isSelected true if the specified cell was selected
         * @param cellHasFocus true if the specified cell has the focus
         * @return a component whose paint() method will render the specified value
         */
        public Component getListCellRendererComponent(JList list,
                Object value, // value to display
                int index, // cell index
                boolean isSelected, // is the cell selected
                boolean cellHasFocus) // the list and the cell have the focus
        {
            String s = value.toString();
            setText(s);
            if (choiceModel.containsIgnoreCase(value) < 0) {
                // value does not exist in choiceModel
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(Color.white);
                } else {
                    setBackground(list.getBackground());
                    setForeground(Color.blue.darker());
                }
            } else {
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            return this;
        }
    }

    /**
     * Remove the popup from the screen.
     */
    public void hidePopup() {
        if (isPopupVisible()) {          
            thePopup.hide();
            thePopup = null;
            int textLength = textField.getText().length();     
        }
    }

    private void togglePopup() {
        if (isPopupVisible()) {           
            hidePopup();        
        } else {
            Log.print("(IbaseTextField).togglePopup");
            showPopup();
        }
    }

    private boolean isPopupVisible() {
        return (thePopup != null);
    }

    /**
     * Set <code>textField</code> to an empty string.
     */
    public void clearField() {
        setText("");
    }

    /**
     * Return the value of <code>textField</code> to the caller.
     * @return the <code>String</code> value of <code>textField</code>
     */
    public String getText() {
        return textField.getText();
    }

    /**
     * Set the value of <code>textField</code> to the given String.
     * @param text the new value
     */
    public void setText(java.lang.String text) {
        // remember if this field is going to be changed
        // then, we can set changed flag properly,
        // ignoring intermediate changes, if changed
        // flag gets set by separate remove and insert
        // operations in the document.  (Kludge!!)
        theImpl.checkChanged(text);
        boolean holdChanged = theImpl.isChanged();
        String oldText = textField.getText();
        if (oldText != null && oldText.length() > 0) {
            //significant = true;
            document.uncheckedRemove(0, oldText.length());
        }
        if (text != null && text.length() > 0) {
            //significant = true;
            if (!textField.hasFocus()) {
                document.uncheckedInsertString(0, text);
            } else {
                // value came from Alt+C in SplitPaneViewer
                // or load of data from server with focus in this field
                if (repeated) {
                    text = text + "; ";
                }
                document.uncheckedInsertString(0, text);
                initialDot = textField.getText().length();
                textField.selectAll();
            }
        } else {
            if (popupPanel != null) {
                popupPanel.setValuesFromString(text);
            }
        }
        //setChanged();
        theImpl.setChanged(holdChanged);
    }

    /**
     * Set the ListModel used for the popup's choice panel.
     */
    public void setProjectModel(AbstractTableModel model) {
        //public void setProjectModel (ManagedTableSorter model) {
        tablevalueModel = model;
    }

    /**
     * Return the model used in this field.
     */
    public ManagedTableModel getProjectModel() {
        return (ManagedTableModel) tablevalueModel;
    }

    /**
     * Return the ListModel used for the popup's choice panel.
     */
    public SortedListModel getModel() {
        return choiceModel;
    }

    /**
     * Set the length of the JTextField.
     */
    public void setColumns(int cols) {
        textField.setColumns(cols);
    }

    private int addValue(String str) {        
        int row = -1;
        if ((row = choiceModel.containsIgnoreCase(str)) == -1) {
            int answer = JOptionPane.showConfirmDialog(this,
                    "Please confirm addition of new item.",
                    "Confirm Addition.",
                    JOptionPane.YES_NO_OPTION);
            if (answer != JOptionPane.YES_OPTION) {
                //Log.print("(ITF).addValue: user said no");
                return -1;
            }
            //Log.print("(ITF).addValue user said yes " + type);

            StringBuffer sb = new StringBuffer(str);

            int commaPosition = -1;
            char[] chars = str.toCharArray();
            if (type.equals(IbaseConstants.DYNAMIC_FIELD_IS_NAME)) {
                //Log.print("(IBTF) addValue is name");
                // Names must be properly capitalized
                for (int i = 0; i < chars.length; i++) {
                    // If there are any upper case letters, don't edit the string.
                    if (Character.isUpperCase(chars[i])) {
                     
                        capitalText = str;
                        row = choiceModel.add(str.trim());
                        return row;
                    }
                    if (chars[i] == ',' && commaPosition < 0) {
                        //Log.print("(IBTF) addValue is comma " + chars[i]);
                        commaPosition = i;
                    } else if (chars[i] == '.' || chars[i] == ';') { // ok
                        //Log.print("(IBTF) addValue is period " + chars[i]);

                    } else if (!Character.isLetter(chars[i]) && !Character.isSpaceChar(chars[i])) {
                        //Log.print("(IBTF) addValue is character " + chars[i]);
                        // Don't edit if special characters or digits exist
                        capitalText = str;
                        row = choiceModel.add(str.trim());
                        return row;
                    }
                }
                // make 1st character upper case
                sb.replace(0, 1, (str.substring(0, 1)).toUpperCase());
                // Make all characters following a space, upper case.
                for (int i = commaPosition + 1; i < chars.length; i++) {
                    if (i == 0 || Character.isSpaceChar(chars[i - 1]) || (i < chars.length - 1 && chars[i + 1] == '.')) {
                        sb.replace(i, i + 1, (str.substring(i, i + 1)).toUpperCase());
                    }
                }
                str = sb.toString();
            //Log.print("(IBTF) capitalizeText new string " + str);
            }
            row = choiceModel.add(str.trim());
        }
        capitalText = str;
        return row;
    }

    private String capitalizeText(String str) {
        int row = 0;

        StringBuffer sb = new StringBuffer(str);

        int commaPosition = -1;
        char[] chars = str.toCharArray();
        if (type.equals(IbaseConstants.DYNAMIC_FIELD_IS_NAME)) {
            //Log.print("(IBTF) addValue is name");
            // Names must be properly capitalized
            for (int i = 0; i < chars.length; i++) {
                // If there are any upper case letters, don't edit the string.
                if (Character.isUpperCase(chars[i])) {
                    //Log.print("(IBTF) addValue is upper " + chars[i]);
                    capitalText = str;
                    break;
                }
                if (chars[i] == ',' && commaPosition < 0) {
                    //Log.print("(IBTF) addValue is comma " + chars[i]);
                    commaPosition = i;
                } else if (chars[i] == '.' || chars[i] == ';') { // ok
                    //Log.print("(IBTF) addValue is period " + chars[i]);

                } else if (!Character.isLetter(chars[i]) && !Character.isSpaceChar(chars[i])) {
                    //Log.print("(IBTF) addValue is character " + chars[i]);
                    // Don't edit if special characters or digits exist
                    capitalText = str;
                    break;
                }
            }
            // make 1st character upper case
            sb.replace(0, 1, (str.substring(0, 1)).toUpperCase());
            // Make all characters following a space, upper case.
            for (int i = commaPosition + 1; i < chars.length; i++) {
                if (i == 0 || Character.isSpaceChar(chars[i - 1]) || (i < chars.length - 1 && chars[i + 1] == '.')) {
                    sb.replace(i, i + 1, (str.substring(i, i + 1)).toUpperCase());
                }
            }
            str = sb.toString();
        }
        return str;
    }
  
    /**
     * Set the background color of <code>textField</code>.
     * @param c the given background color
     */
    public void setBackground(Color c) {
        if (textField != null) {
            textField.setBackground(c);
        }
    }

    /**
     * Set the foreground color of <code>textField</code>.
     * @param c the given foreground color
     */
    public void setForeground(Color c) {
        if (textField != null) {
            textField.setForeground(c);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField textField;
    private beans.ArrowButton theArrow;
    // End of variables declaration//GEN-END:variables

    /**
     * 
     */
    public class CheckedDocument extends PlainDocument {

        /**
         * Inserts some content into the document. Inserting content causes a
         * write lock to be held while the actual changes are taking place,
         * followed by notification to the observers on the thread that grabbed
         * the write lock.
         * @param offs the starting offset >= 0
         * @param str the string to insert; does nothing with null/empty strings
         * @param a the attributes for the inserted content
         * @throws BadLocationException - the given insert position is not a
         * valid position within the document
         */
        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {
            SplitPaneViewer.clearViewerStatus(IbaseTextField.this); // clear status line
            
            if (str == null || str.equals(";") || str.equals("; ")) {
                return;
            }
            setChanged(true);
            
            if (offs < initialDot) {
                initialDot = initialDot + str.length();
                //Log.print("(IbaseTextField) offs < initialDot " + offs + "<" + initialDot);               
                super.insertString(offs, str, a);                
                //Toolkit.getDefaultToolkit().beep();
                return;
            }
            // strip leading spaces, if any, at start of item
            if (offs == initialDot) {
                while (str.length() > 0 && str.charAt(0) == ' ') {
                    str = str.substring(1);
                    if (str.length() == 0) {
                        Toolkit.getDefaultToolkit().beep();
                        Log.print("BEEP> IBaseTextField: all spaces");
                        return;
                    }
                }
            }
            // get rid of any completion text
            int mark = textField.getCaret().getMark();
            int dot = textField.getCaret().getDot();
            //Log.print("(ITF).CheckedDoc: mark/dot " + mark + "/" + dot);
            if (mark != dot) {
                //Log.print("(ITF).CheckedDoc: uncheckedRemove " + mark + "/" + dot);
                // mark & dot being unequeal means there is a selection                
                document.uncheckedRemove(Math.min(mark, dot), Math.abs(mark - dot));            
            }
            // look for semicolon; only allowed at end
            int semicolonOffs = str.lastIndexOf(';');

            if (mf != null) {
                try {
                    mf.stringToValue(str);
                } catch (ParseException pe) {
                    Toolkit.getDefaultToolkit().beep();
                    Log.print("BEEP> IBaseTextField: invalid mask character: " + pe + "/" + str);
                    return;
                }
            }
            if (semicolonOffs >= 0) {
                if (textField.getText().length() + str.length() - 1 <= initialDot) {
                    // semicolon, but no inserted text
                    Toolkit.getDefaultToolkit().beep();
                    Log.print("BEEP> IBaseTextField: semicolon, but no inserted text");
                    //Log.print("(ITF).CheckedDoc: returning");
                    return;
                } else {                   
                    super.insertString(offs, str, a);
                    initialDot = textField.getText().length();                    
                    System.err.println("after semicolon, initialDot = " + initialDot);                    
                    popupPanel.setChoiceSelection(null);
                    return;
                }
            }
            showPopup();

            //Log.print("(ITF) before insert str " + textField.getText() + "/");
            significant = true;
            super.insertString(offs, str, a);
            significant = false;
            //significant = false;
            //Log.print("(ITF) after insert str " + textField.getText() + "/");

            //Log.print("(ITF) insert before requestFocus /" + textField.getText() + "/");
            textField.requestFocus();
            //Log.print("(ITF) insert after requestFocus /" + textField.getText() + "/");
            // set the completion text
            String text = textField.getText();
            if (initialDot < 0) {
                initialDot = 0;
            }
            String initialText = text.substring(initialDot, text.length());
            String fullText = popupPanel.setChoiceSelection(initialText);            
            if (fullText != null && fullText.length() > initialText.length()) {
                //Log.print("(ITF) before uncheckedInsertString");
                document.uncheckedInsertString(text.length(), fullText.substring(initialText.length()));

                textField.getCaret().setDot(textField.getText().length());
                textField.getCaret().moveDot(text.length());            }
       
        }

        /**
         * Removes some content from the document. Removing content causes a
         * write lock to be held while the actual changes are taking place.
         * Observers are notified of the change on the thread that called this method.
         * @param offs the starting offset >= 0
         * @param len the number of characters to remove >= 0 
         * @throws BadLocationException the given remove position is not a valid
         * position within the document
         */
        public void remove(int offs, int len)
                throws BadLocationException {           
            SplitPaneViewer.clearViewerStatus(IbaseTextField.this); // clear status line

            setChanged(true);
            significant = true;
            super.remove(offs, len);
            significant = false;
            if (offs < initialDot) {
                initialDot = initialDot - len;
            }
        }

        public void uncheckedInsertString(int offs, String str) {
            try {               
                significant = true;
                super.insertString(offs, str, SimpleAttributeSet.EMPTY);
                significant = false;
            } catch (BadLocationException e) {
                e.printStackTrace();
                Log.quit(e);
            }
        }

        public void uncheckedRemove(int offs, int len) {
            try {
                if (offs > -1 && len <= textField.getText().length()) {                
                    significant = true;
                    super.remove(offs, len);
                    significant = false;
                }
            } catch (BadLocationException e) {
                Log.quit(e);
            }
        }

        /**
         * Should the edit be placed in the undo queue?
         * @return true - this is an edit which is being accepted by the user
         * @return false - this is an interim edit caused by IBaseTextField processing
         */
        public boolean isSignificant() {
            if (DEBUG) {
                Log.print("(ITF.isSignificant) " + significant);
            }
            return significant;
        }
    }

    /**
     * Enable or disable the <code>textField</code>.
     * @param flag true to enable; false to disable
     */
    public void setEnabled(boolean flag) {
        //Log.print("IbaseTextField.setEnabled: "+flag);
        textField.setEditable(flag);
        theArrow.setEnabled(flag);
        textField.setForeground(Color.black);
        if (flag) {
            textField.setBackground(Color.white);
        } else {
            textField.setBackground(null);
        }
    }

    /**
     * Set focus in <code>textField</code>.
     */
    public void requestFocus() {
        //Log.print("(ITF.requestFocus)");
        textField.requestFocus();
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

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
  
    public String getImagePath() {
        return imagePath;
    }

    public void setBatesNumber(String batesNumber) {
        this.batesNumber = batesNumber;
    }

    public String getBatesNumber() {
        return batesNumber;
    }

    public void setChildId(int childId) {
        this.childId = childId;
    }

    public int getChildId() {
        return childId;
    }

    public void setBatchId(int batchId) {
        this.batchId = batchId;
    }

    public int getBatchId() {
        return batchId;
    }

    public void validation() {
    }

    //This Method is implemented for F1 function 
//By Bala
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

    /**
     * This Method is implemented for F10 function 
     */
    private int dof10(KeyEvent evt) {
        AddEditQuery queryDialog;
        queryDialog = new AddEditQuery(this, projectMap, fieldName, batesNumber, imagePath,
                whichStatus, childId, batchId);
        queryDialog.setModal(true);
        queryDialog.show();
        return 0;
    }

    public void setTextField(JTextField textfield) {
        theImpl.setTextField(textfield);
    }

    public JTextField gettextfield() {
        return theImpl.getTextField();
    }

    public void setComboBox(LComboBox combo) {
        theImpl.setComboBox(combo);
    }

    public LComboBox getComboBox() {
        return theImpl.getComboBox();
    }

    private int doVerify(KeyEvent evt) {
        
        ShowVerifyDialog verifyDialog =null;
        //boolean checkIsEnabled = textField.isEnabled();     
       
            verifyDialog = new ShowVerifyDialog(this, textField.getText(), codingData.
                valueMap, fieldName, codingData.childId, whichStatus, "IbaseTextField");
            verifyDialog.setLocationRelativeTo(this);
            verifyDialog.setModal(true);
            verifyDialog.show();
             
        return 0;
    }
}
