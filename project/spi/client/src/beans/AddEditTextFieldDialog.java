/* $Header: /home/common/cvsarea/ibase/dia/src/beans/AddEditTextFieldDialog.java,v 1.23.6.3 2006/02/21 17:02:45 nancy Exp $ */
package beans;

import com.lexpar.util.Log;
import common.TablevalueData;
import client.ClientTask;
import client.TaskSendTablevalue;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import model.ManagedTableSorter;
import model.SQLManagedTableModel;

import java.lang.Integer;
import java.util.ArrayList;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Called from beans.AddEditTablespec and beans.IbaseTextField, this dialog allows
 * the user to add or edit a String of text or a name made up of first, middle initial,
 * last name and affiliation.  If there is no last name, affilition should be entered
 * in the last name field.  The data is sent to the server via container common.TablevalueData.
 * 
 * @author  Nancy
 * 
 * @see beans.AddEditTablespec
 * @see beans.IbaseTextField
 * @see common.TablevalueData
 * @see client.TaskSendTablevalue
 * @see server.Handler_tablevalue
 */
final public class AddEditTextFieldDialog extends JDialog {

    private JPanel selectPanel = new javax.swing.JPanel();
    private JButton okButton = new JButton("OK");
    private JButton saveAndAddButton = new JButton("Save & Add");
    private JButton cancelButton = new JButton("Cancel");
    private String type;
    private LTextField text = new LTextField(40);
    private LIntegerField level = new LIntegerField(1);
    private LTextField f_name = new LTextField(30);
    private LTextField m_name = new LTextField(2);
    private LTextField l_name = new LTextField(40);
    private LTextField organization = new LTextField(40);
    private String originalText;
    private int originalLevel;
    private String action;
    private int tablespecId;
    /** in the viewer, this value will show when a value from modelTextField is selected */
    private int modelTablespecId;
    private String modelText;
    private IbaseTextField modelTextField;   
    private ArrayList results = new ArrayList();
    private beans.LGridBag fieldPane = new beans.LGridBag();
    private ArrayList valuesList = new ArrayList();
    private boolean isRequired = false;
    private boolean isUpdatable = false;

    /**
     * Creates new form AddEditTextFieldDialog.
     * @param parent the component to use in positioning this dialog
     * @param type can be 'text' or 'name'
     * @param text for an edit, the value of the existing data
     * @param level for an edit, the existing level associated with this value
     * @param tablespecId tablespec.tablespec_id of the table to be edited
     * @param action 'add' or 'edit'
     * @param modelTablespecId the tablespec.tablespec_id of the table to use
     * as the values for this table
     * @param modelText values in an ibaseTextField will be shown based on this value
     */
    public AddEditTextFieldDialog(Component parent, String type, String text, int level, int tablespecId, String action, int modelTablespecId, String modelText,ArrayList valuesList,boolean isRequired,boolean isUpdatable) {
        super(JOptionPane.getFrameForComponent(parent), true);
        this.type = type;
        originalText = text;
        originalLevel = level;
        this.tablespecId = tablespecId;
        this.action = action;
        this.modelTablespecId = modelTablespecId;
        this.modelText = modelText;        
        this.valuesList = valuesList;
        this.isRequired = isRequired;
        this.isUpdatable = isUpdatable;

        Log.print("(AddEditTextField).text " + type + "/" + text + "/" + this.modelTablespecId);
        setTitle("Text Field");

        getContentPane().add(selectPanel, BorderLayout.CENTER);
        selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.Y_AXIS));
        selectPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        okButton.addActionListener(buttonListener);
        okButton.setEnabled(false);
        cancelButton.addActionListener(buttonListener);
        JPanel outerButtonPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 30, 20));
        buttonPanel.add(okButton);
        if (action.equals("add")) {
            saveAndAddButton.addActionListener(buttonListener);
            saveAndAddButton.setEnabled(false);
            buttonPanel.add(saveAndAddButton);
        }
        buttonPanel.add(cancelButton);
        outerButtonPanel.add(buttonPanel);
        getContentPane().add(outerButtonPanel, BorderLayout.SOUTH);

        addControls();
        getRootPane().setDefaultButton(okButton);
        pack();
    }
    
    
    /**
     * Listener for the buttons.
     * If Ok button is click - make this dialog invisible and perfrom addition new record.
     * If saveAndAddButton is clicked - set the results and add new one.
     * If cancel is clicked - make it invisible.
     */
    private ActionListener buttonListener = new ActionListener() {        
        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                Object source = A.getSource();
                if (source == okButton) {
                    if (setResults()) {
                        setVisible(false);
                        action = "add";
                    }
                } else if (source == saveAndAddButton) {
                    setResults();
                } else if (source == cancelButton) {
                    setVisible(false);
                }
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    };

    /**
     * Add control to form.
     */
    private void addControls() {
        if (type.equals(IbaseConstants.DYNAMIC_FIELD_IS_NAME)) {
            fieldPane.add(0, 1, new JLabel("First Name: "), f_name);
            fieldPane.add(0, 2, new JLabel("Middle Initial:"), m_name);
            fieldPane.add(0, 3, new JLabel("Last Name (or Affiliation with no Last Name): "), l_name);
            fieldPane.add(0, 4, new JLabel("Affiliation: "), organization);

            f_name.setDocument(new ThirtyDocument());
            m_name.setDocument(new NineteenDocument());
            l_name.setDocument(new LongDocument());
            organization.setDocument(new LongDocument());
            String name = originalText;
            int slashPos = name.lastIndexOf('/');
            if (slashPos >= 0) {
                if (slashPos < name.length()) {
                    organization.setText(name.substring(slashPos + 1).trim());
                }
                name = name.substring(0, slashPos);
            }
            int commaPos = name.indexOf(",");
            if (commaPos < 0) {
                l_name.setText(name.trim());
            } else {
                l_name.setText(name.substring(0, commaPos).trim());
                name = name.substring(commaPos + 1).trim();
                int spacePos = name.lastIndexOf(' ');
                if (spacePos < 0) {
                    f_name.setText(name.trim());
                } else {
                    f_name.setText(name.substring(0, spacePos).trim());
                    m_name.setText(name.substring(spacePos + 1).trim());
                }
            }
            f_name.requestFocus();

        } else {
            fieldPane.add(0, 1, new JLabel("Value: "), text);
            fieldPane.add(0, 2, new JLabel("Document Level: "), level);
            if (modelTablespecId > 0) {
                modelTextField = new beans.IbaseTextField("text", modelTablespecId, /* mandatory */ isRequired, /* updateable */ isUpdatable, /* repeated */ false);
                ManagedTableSorter mts = new ManagedTableSorter(0,
                        SQLManagedTableModel.makeInstance("AddEditTextFieldDialog.tablevalue", modelTablespecId));
                modelTextField.setProjectModel(mts);
                mts.register();
                fieldPane.add(0, 3, new JLabel("Model Value: "), modelTextField);
                modelTextField.setText(modelText);
                modelTextField.setPreferredSize(new Dimension(100, 25));
            } else {
                JTextField textField = new JTextField("<None>");
                textField.setEnabled(false);
                fieldPane.add(0, 3, new JLabel("Model Value: "), textField);
            }
            text.setDocument(new PlainDocument() {

                public void insertString(int offs, String str, AttributeSet a)
                        throws BadLocationException {
                    if (str == null) {
                        return;
                    }
                    okButton.setEnabled(true);
                    saveAndAddButton.setEnabled(true);

                    super.insertString(offs, str, a);
                }

                public void remove(int offs, int len)
                        throws BadLocationException {
                    super.remove(offs, len);
                    if (text.getText().equals("")) {
                        okButton.setEnabled(false);
                        saveAndAddButton.setEnabled(false);
                    }
                }
            });
            text.setText(originalText);
            level.setValue(originalLevel);
            text.requestFocus();
        }
        
        selectPanel.add(fieldPane, BorderLayout.CENTER);
    }

    private class LongDocument extends PlainDocument {

        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {
            if (str == null || (this.getLength() + str.length() > 100)) {
                // Refuse the insertion and beep to notify user
                Toolkit.getDefaultToolkit().beep();
            } else {
                super.insertString(offs, str, a);
            }
            checkOkSetEnabled();
        }

        public void remove(int offs, int len) throws BadLocationException {
            super.remove(offs, len);
            checkOkSetEnabled();
        }
    }

    private class ThirtyDocument extends PlainDocument {

        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {
            if (str == null || (this.getLength() + str.length() > 30)) {
                // Refuse the insertion and beep to notify user
                Toolkit.getDefaultToolkit().beep();
            } else {
                super.insertString(offs, str, a);
            }
            checkOkSetEnabled();
        }

        public void remove(int offs, int len)
                throws BadLocationException {
            super.remove(offs, len);
            checkOkSetEnabled();
        }
    }

    private class NineteenDocument extends PlainDocument {

        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {
            if (str == null || (this.getLength() + str.length() > 19)) {
                // Refuse the insertion and beep to notify user
                Toolkit.getDefaultToolkit().beep();
            } else {
                super.insertString(offs, str, a);
            }
            checkOkSetEnabled();
        }

        public void remove(int offs, int len)
                throws BadLocationException {
            super.remove(offs, len);
            checkOkSetEnabled();
        }
    }

    /**
     * Make ok button enabled.
     */
    private void checkOkSetEnabled() {
        if (f_name.getText().trim().equals("") && l_name.getText().trim().equals("")) {
            okButton.setEnabled(false);
            saveAndAddButton.setEnabled(false);
        } else {
            okButton.setEnabled(true);
            saveAndAddButton.setEnabled(true);
        }
    }

    /**
     * Set and save the result.
     * @return
     */
    private boolean setResults() {
        String result = "";
        String f = f_name.getText().trim();
        String m = m_name.getText().trim();
        String l = l_name.getText().trim();
        String o = organization.getText().trim();
        if (type.equals(IbaseConstants.DYNAMIC_FIELD_IS_TEXT)) {
            if (text != null && !text.getText().trim().equals("")) {
                result = text.getText().trim();
                text.setText("");
            }
            text.requestFocus();
        } else if (type.equals(IbaseConstants.DYNAMIC_FIELD_IS_NAME)) {
            if (editText("First Name", f) && editText("Last Name", l) && editText("Middle Initial", m) && editText("Affiliation", o)) {
                if (!l.equals("") && !m.equals("") && !f.equals("")) {
                    // last, first middle
                    result = l + ", " + f + " " + m;
                } else if (!l.equals("") && !f.equals("")) {
                    // last, first
                    result = l + ", " + f;
                } else if (!l.equals("") && !m.equals("")) {
                    // last middle
                    result = l + " " + m;
                } else if (!l.equals("")) {
                    // last
                    result = l;
                } else if (!f.equals("") && !m.equals("")) {
                    // middle first
                    result = m + " " + f;
                } else if (!f.equals("")) {
                    // first
                    result = f;
                }
                if (result == null || result.equals("")) {
                    result = o;
                } else if (!o.equals("")) {
                    result = result.trim() + " / " + o;
                }
                f_name.setText("");
                m_name.setText("");
                l_name.setText("");
                organization.setText("");
                f_name.requestFocus();
            } else {
                return false;
            }
        }
        if (!result.equals("")) {            
            for(int i =0 ; i< valuesList.size();i++){
                if(result.equals(valuesList.get(i).toString())){            //while editing the value
                    if(action.equals("edit")){
                        if(!originalText.equals(result)){
                            JOptionPane.showMessageDialog(this, "Value already exists.Please try some different value.");                    
                            return false;
                        }
                    }else{
                         JOptionPane.showMessageDialog(this, "Value already exists.Please try some different value.");                    
                        return false;
                    }
                }
            }
            // update tablevalue on server
            //Log.print("(AddEditTextFieldDialog.setResults) update server " + result
            //          + "/" + level.getValue());
            TablevalueData data = new TablevalueData();
            data.tablevalue_id = 0;
            data.tablespec_id = tablespecId;
            data.value = result;
            data.level = level.getValue();
            if (modelTextField != null) {
                data.model_value = modelTextField.getText();
            } else {
                data.model_value = "";
            }
            if (action.equals("edit")) {
                data.old_value = originalText;
                final ClientTask task = new TaskSendTablevalue(data);
                task.enqueue(this);
            } else {
                data.old_value = "";
                final ClientTask task = new TaskSendTablevalue(data);
                task.enqueue(this);
            }
            // add to arraylist that will be returned to caller
            results.add((String) result.trim());
            results.add(Integer.toString(level.getValue()));
        }
        level.setValue(0);
        return true;
    }

    private boolean editText(String name, String text) {
        if (text.indexOf(",") > -1 || text.indexOf(";") > -1 || text.indexOf("/") > -1) {
            Toolkit.getDefaultToolkit().beep();
            Log.print("BEEP> AddEditTextField.setResults");
            JOptionPane.showMessageDialog((Component) f_name,
                    "Illegal character in " + name + ".",
                    "Name Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * Return the user's entry via results to the calling program.
     * @return an Object array of Strings, the text entered by the user
     */
    public Object[] getSelection() {
        if (results.size() == 0) {
            return null;
        } else {
            return results.toArray();
        }
    }
}
