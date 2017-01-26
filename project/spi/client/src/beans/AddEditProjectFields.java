/* $Header: /home/common/cvsarea/ibase/dia/src/beans/AddEditProjectFields.java,v 1.22.2.8 2006/03/27 18:21:54 nancy Exp $ */
package beans;

import client.ClientTask;
import client.TaskExecuteQuery;
import client.TaskSendProjectFieldsData;
import com.lexpar.util.Log;
import common.ProjectFieldsData;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import model.QueryComboModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.MaskFormatter;
import javax.swing.text.PlainDocument;
import ui.AbstractPage;
import ui.ProjectAdminPage;

/**
 * Called from ui.ProjectAdminPage, this dialog allows the user to add or edit
 * data to the projectfields table for the selected project.  The data is sent
 * via the container common.ProjectFieldsData.
 * 
 * @author  Nancy
 * 
 * @see common.ProjectFieldsData
 * @see ui.ProjectAdminPage
 * @see client.TaskSendProjectFieldsData
 * @see server.Handler_projectfields_data
 */
final public class AddEditProjectFields extends javax.swing.JDialog {

    /** container to hold projectfields data for send to server */
    private ProjectFieldsData projectFieldsData;
    private JPanel selectPanel = new javax.swing.JPanel();
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    private JPanel rPane = new JPanel();
    private JPanel levelPane = new JPanel();
    private JPanel tablePane = new JPanel();
    private JPanel sizePane = new JPanel();
    private LGridBag namePane = new LGridBag();
    private JTextField fieldName;
    private JTextField tagName;
    private LTextField defaultValue;
    private LTextField minValue;
    private LTextField maxValue;
    private LTextField mask;
    private LTextField validChars;
    private LTextField invalidChars;
    private LTextField charset;
    private LIntegerField fieldSize;
    private LIntegerField minimumSize;
    private LIntegerField levelIndicator;
    private LIntegerField fieldGroup;
    private JCheckBox repeatedCheckBox;
    private JCheckBox spellcheckCheckBox;
    private JCheckBox unitizeCheckBox;
    private JCheckBox requiredCheckBox;
    private JCheckBox tableMandatoryCheckBox;
    private JCheckBox fieldLevelCheckBox;
    private JCheckBox allowUnprintableCheckBox;
    private LComboBox typeCombo;
    private LComboBox tableCombo;
    private LComboBox validationCombo;
    private LComboBox l1_informationCombo;
    private String[] typeComboString = {"", "date", "name", "text", "signed", "unsigned"};
    private String[] l1_informationComboString = {"-- SELECT FIELD INFORMATION TYPE --", "Source", "Folder", "Document"};
    private String tableName = "";
    private int projectfieldsId;
    private int projectId;
    private int sequence = 0;
    private JPanel typePane = new JPanel();
    private QueryComboModel tableModel;
    private boolean levelFieldExists;
    private JTextArea tip = new JTextArea("\n\n\n\n\n\n\n\n\n\n\n\n");
    private javax.swing.JScrollPane jScrollPane1;
    private JTextArea description;
    private AbstractPage parent;
    public String oldName = null;


   private String[] l1_informationComboString1 = new String[2];
   private String[] l1_informationComboString2 = new String[4];
   
   
   private JPanel buttonPane = new JPanel();
   private JPanel valuePane = new JPanel();
   private JList valueList = new JList();
   private beans.LGridBag fieldPane = new beans.LGridBag();
   private JPanel groupPane = new JPanel();
   
   public String hasDTYG="";
   
   /**

    /**

     * Creates new form AddEditProjectFields.
     * @param parent - parent page
     * @param comp the component to use in positioning this dialog
     * @param projectfieldsId projectfields.projectfields_id of the
     * row to be edits; 0 for an add
     * @param projectId the project.project_id of the user-selected project
     * @param sequence the sequence of this projectfield row within the given project
     * @param levelFieldExists true if there is a level control field in this project;
     * false otherwise, to track whether the fields associated with the levelCheckBox
     * should be enabled.
     */
   
   public AddEditProjectFields(AbstractPage parent, Component comp, int projectfieldsId, int projectId, int sequence, boolean levelFieldExists,String hasDTYG)
   {
      super(JOptionPane.getFrameForComponent(comp));
      this.parent = parent;
      this.projectfieldsId = projectfieldsId;
      this.projectId = projectId;
      this.sequence = sequence;
      this.levelFieldExists = levelFieldExists;
      this.hasDTYG = hasDTYG;
      
        getContentPane().add(selectPanel, BorderLayout.CENTER);
        getRootPane().setDefaultButton(okButton);
        selectPanel.setLayout(new BorderLayout());
        selectPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        okButton.addActionListener(actionListener);
        // disabled for edit until first change
        okButton.setEnabled(false);
        cancelButton.addActionListener(actionListener);

        JPanel outerButtonPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 30, 20));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        outerButtonPanel.add(buttonPanel);
        selectPanel.add(outerButtonPanel, BorderLayout.SOUTH);
        
        setTitle("SPiCA Project Field Definition");

        getValidationList();
        getProjectData(); // calls addControls

    }

    /**
     * Overrides JDialog.dispose.
     */
    public void dispose() {
        setVisible(false);
        super.dispose();
    }

    /**
     * Use a client.TaskExecuteQuery to get the data for the given projectfields_id.
     */
    private void getProjectData() {
        tableCombo = new LComboBox(40);

        if (projectfieldsId > 0) {
            final ClientTask task;
            //Log.print("(AddEditProjectFields.getProjectData) " + projectfieldsId);
            task = new TaskExecuteQuery("projectfields by id select", Integer.toString(projectfieldsId));
            task.setCallback(new Runnable() {

                public void run() {
                    getProjectBySequenceDataEntry((ResultSet) task.getResult());
                    // set OK disabled again
                    // (It was enabled by getProjectBySequenceDataEntry when combo selection set.)
                    okButton.setEnabled(false);
                }
            });
            boolean ok = task.enqueue(this);
        }
        addControls();
        if (projectfieldsId <= 0) {
            // this is an add -- see if a level control exists for this project
            if (this.levelFieldExists) {
                fieldLevelCheckBox.setEnabled(false);
            }
        }
        fieldLevelCheckBox.addActionListener(actionListener);
    }

    /**
     * Used by ProjectAdminPage to enable Delete button if this is not
     * a field_level control.
     * @return true if level control, else false
     */
    public boolean isLevelIndicator() {
        return fieldLevelCheckBox.isSelected();
    }

    /**
     * Load the projectfields data to the screen.
     * @param queryResult ResultSet returned by ClientTask in getProjectData
     */
    private void getProjectBySequenceDataEntry(ResultSet queryResult) {
        try {
            if (queryResult.next()) {
                // updating a user
                //Log.print("(AddEditProjectFields).addControls username " + fieldName.getText());
                String field_name = queryResult.getString(3);
                fieldName.setText(field_name);
                if (field_name != null && field_name.trim().equalsIgnoreCase("General Document Type")) {
                    fieldName.setEditable(false);
                }
                tagName.setText(queryResult.getString(23));
                tableName = queryResult.getString(11);
                typeCombo.setText(queryResult.getString(4));
                fieldSize.setText(queryResult.getString(5));
                minimumSize.setText(queryResult.getString(22));
                requiredCheckBox.setSelected((queryResult.getString(7).equals("Yes") ? true : false));
                repeatedCheckBox.setSelected((queryResult.getString(6).equals("Yes") ? true : false));
                unitizeCheckBox.setSelected((queryResult.getString(19).equals("Yes") ? true : false));
                spellcheckCheckBox.setSelected((queryResult.getString(20).equals("Yes") ? true : false));
                tableMandatoryCheckBox.setSelected((queryResult.getString(12).equals("Yes") ? true : false));
                defaultValue.setText(queryResult.getString(8));
                minValue.setText(queryResult.getString(9));
                maxValue.setText(queryResult.getString(10));
                mask.setText(queryResult.getString(13));
                validChars.setText(queryResult.getString(14));
                invalidChars.setText(queryResult.getString(15));
                Log.print("(AEProjectFields.get) invalidChars=" + invalidChars.getText());
                int i = 0;
                if ((i = invalidChars.getText().indexOf("\\u")) > -1) {
                    // If a "\\u" is appended to projectfields.invalidChars,
                    // unselect the checkbox and remove it from invalidChars.
                    allowUnprintableCheckBox.setSelected(false);
                    if (i > 0) {
                        invalidChars.setText(invalidChars.getText().substring(0, i));
                    } else {
                        invalidChars.setText("");
                    }
                //Log.print(" edited=" + invalidChars.getText());
                } else {
                    allowUnprintableCheckBox.setSelected(true);
                }
                charset.setText(queryResult.getString(16));
                // 20 will be * if the field is the projectfields.level_field_name.
                // otherwise, it will be the level (default 0)
                fieldLevelCheckBox.setSelected((queryResult.getString(21).equals("*") ? true : false));
                levelIndicator.setText(queryResult.getString(21).equals("*") ? "0" : queryResult.getString(21));
                fieldGroup.setValue(queryResult.getInt(24));
                description.setText(queryResult.getString(25));
                validationCombo.setText(queryResult.getString(26));
                l1_informationCombo.setText(queryResult.getString(27));
            }
        } catch (SQLException e) {
            Log.quit(e);
        }

    }
    /**
     * Enable and disable fields based on the projectfields.field_type,
     * or process Ok/Save and Cancel.
     */
    private ActionListener actionListener = new ActionListener() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                Object source = A.getSource();
                if (source == okButton) {
                    if (save()) {
                        dispose();
                    }
                } else if (source == cancelButton) {
                    dispose();
                } else if (source == tableCombo) {
                    if (tableCombo.getText() != null && !tableCombo.getText().equals("")) {
                        tableMandatoryCheckBox.setEnabled(true);
                        String type = typeCombo.getText();
                        defaultValue.setEnabled(true);
                        fieldLevelCheckEnabled();
                        minValue.clearField();
                        maxValue.clearField();
                        minValue.setEnabled(false);
                        maxValue.setEnabled(false);
                    } else {
                        tableMandatoryCheckBox.setEnabled(false);
                    }
                } else if (source == typeCombo) {
                    if (typeCombo.getText() != null && !typeCombo.getText().equals("")) {

                        //set the table model on the basis of table type
                        String[] key = {Integer.toString(projectId), typeCombo.getText()};
                        tableModel = new QueryComboModel("get tablespec names", /* required-> */ false, key, tableName);
                        tableCombo.setModel(tableModel);

                        if (typeCombo.getText().equals("text") || typeCombo.getText().equals("name")) {
                            if (typeCombo.getText().equals("text")) {
                                spellcheckCheckBox.setEnabled(true);
                            } else {
                                spellcheckCheckBox.setEnabled(false);
                            }

                            fieldSize.setEnabled(true);
                            minimumSize.setEnabled(true);
                            defaultValue.setEnabled(true);
                            minValue.setEnabled(true);
                            maxValue.setEnabled(true);
                            mask.setEnabled(true);
                            validChars.setEnabled(true);
                            invalidChars.setEnabled(true);
                            allowUnprintableCheckBox.setEnabled(true);
                            tableCombo.setEnabled(true);
                            tableMandatoryCheckBox.setEnabled(true);
                        } else if (typeCombo.getText().equals("date")) {
                            fieldSize.setEnabled(false);
                            fieldSize.clearField();
                            minimumSize.setEnabled(false);
                            minimumSize.clearField();
                            defaultValue.setEnabled(true);
                            minValue.setEnabled(true);
                            maxValue.setEnabled(true);
                            mask.setEnabled(false);
                            mask.clearField();
                            validChars.setEnabled(false);
                            invalidChars.setEnabled(false);
                            allowUnprintableCheckBox.setEnabled(false);
                            validChars.clearField();
                            invalidChars.clearField();
                            fieldSize.clearField();
                            fieldSize.setEnabled(false);
                            minimumSize.clearField();
                            minimumSize.setEnabled(false);
                            tableCombo.clearField();
                            tableCombo.setEnabled(false);
                            tableMandatoryCheckBox.setEnabled(false);
                            tableMandatoryCheckBox.setSelected(false);
                            spellcheckCheckBox.setEnabled(false);
                        } else if (typeCombo.getText().equals("signed") || typeCombo.getText().equals("unsigned")) {
                            fieldSize.setEnabled(true);
                            minimumSize.setEnabled(true);
                            defaultValue.setEnabled(true);
                            minValue.setEnabled(true);
                            maxValue.setEnabled(true);
                            mask.setEnabled(false);
                            mask.clearField();
                            validChars.setEnabled(false);
                            invalidChars.setEnabled(false);
                            allowUnprintableCheckBox.setEnabled(false);
                            validChars.clearField();
                            invalidChars.clearField();
                            tableCombo.setEnabled(false);
                            tableCombo.clearField();
                            tableMandatoryCheckBox.setEnabled(false);
                            tableMandatoryCheckBox.setSelected(false);
                            spellcheckCheckBox.setEnabled(false);
                        } else {
                            Log.print("(AddEditProjectFields) typeCombo Listener: " + "Unknown type !!!???");
                        }
                        fieldLevelCheckEnabled();
                    }
                } else if (source == repeatedCheckBox || source == fieldLevelCheckBox) {
                    fieldLevelCheckEnabled();
                } else if (source == requiredCheckBox) {
                    if (fieldLevelCheckBox.isSelected()) {
                        if (!requiredCheckBox.isSelected()) {
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(levelIndicator,
                                    "Level Control field must be Required.",
                                    "Field Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } else if (source == l1_informationCombo) {
                    String selItem = l1_informationCombo.getSelectedItem().toString();
                    if (selItem.equalsIgnoreCase("Source") || selItem.equalsIgnoreCase("Folder")) {
                        levelIndicator.setText("1");
                        levelIndicator.setEnabled(false);
                    } else if (selItem.equalsIgnoreCase("Document")) {
                        levelIndicator.setText("");
                        levelIndicator.setEnabled(false);
                    }
                }
                checkOkSetEnabled();
                fieldGroup.setEnabled(true);
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    };

    private PlainDocument getPlainDocument() {
        return new PlainDocument() {

            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (str == null) {
                    return;
                }
                super.insertString(offs, str, a);
                checkOkSetEnabled();
            }

            public void remove(int offs, int len)
                    throws BadLocationException {
                super.remove(offs, len);
                checkOkSetEnabled();
            }
        };
    }

    /**
     * Check if fieldLevelCheckBox should be enabled.
     * Called on:
     *      -- change repeatedCheckBox
     *      -- change requiredCheckBox
     *      -- change type
     *      -- change size
     *      -- tableCombo
     * Required:
     *      -- not repeated
     *      -- required
     *      -- text
     *      -- <= 128
     *      -- with table (tablevalue.value size is 128)
     */
    private void fieldLevelCheckEnabled() {
        //Log.print("(AddEditProjectFields.fieldLevelCheckEnabled) ");

        //crossFieldEdits();
        if (levelFieldExists) {
            // There is another field in this project defined as the control field.
            // Only one per project is allowed.
            fieldLevelCheckBox.setEnabled(false);
        } else {
            fieldLevelCheckBox.setEnabled(true);
            if (fieldLevelCheckBox.isSelected()) {
                tableMandatoryCheckBox.setEnabled(true);
                levelIndicator.setEnabled(false);
            } else {
                fieldLevelCheckBox.setEnabled(true);
                repeatedCheckBox.setEnabled(true);
                requiredCheckBox.setEnabled(true);
                levelIndicator.setEnabled(false);
                if (!typeCombo.getText().equals("text")) {
                    fieldLevelCheckBox.setEnabled(false);
                }
                if (levelIndicator.getValue() > 0) {
                    fieldLevelCheckBox.setEnabled(false);
                } else {
                    if (repeatedCheckBox.isSelected()) {
                        fieldLevelCheckBox.setEnabled(false);
                    }
                }
            }
        }
        tableCombo.setEnabled(true);
        typeCombo.setEnabled(true);
    }

    /**
     * Validate field data.
     * @return true if no errors, else false
     */
    private boolean crossFieldEdits() {
        if (fieldLevelCheckBox.isSelected()) {
            levelIndicator.setEnabled(false);
            requiredCheckBox.setEnabled(true);
            repeatedCheckBox.setEnabled(false);
            if (!typeCombo.getText().equals("text")) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this,
                        "Type must be \'Text\' for Level Control field.",
                        "Field Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (repeatedCheckBox.isSelected()) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this,
                        "Level Control field cannot be Repeated.",
                        "Field Error",
                        JOptionPane.ERROR_MESSAGE);
                repeatedCheckBox.setEnabled(true);
                return false;
            }
            if (!requiredCheckBox.isSelected()) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this,
                        "Level Control field must be Required.",
                        "Field Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (levelIndicator.getValue() > 0) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this,
                        "Level Control cannot have a Level Indicator.",
                        "Field Error",
                        JOptionPane.ERROR_MESSAGE);
                levelIndicator.setEnabled(true);
                return false;
            }
            if (tableCombo.getSelectedIndex() < 0) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this,
                        "Table use is required for a Level Control.\n\n" + "Please select a table.",
                        "Field Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            } else if (!tableMandatoryCheckBox.isSelected()) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this,
                        "Table use is Mandatory for a Level Control.",
                        "Field Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        if (tableCombo.getSelectedIndex() > 0 && typeCombo.getText().equals("text") && fieldSize.getValue() > 255) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this,
                    "Field Size cannot be longer than 255 characters for a text Table field." +
                    "\nField Size is the maximum length of one table entry." +
                    "\n\nPlease reduce the Field Size.",
                    "Field Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (validationCombo.getSelectedIndex() < 0) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this,
                    "Select a Standard Field Validation",
                    "Field Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;

        }
        if (!mask.getText().equals("")) {
            try {
                MaskFormatter mf = new javax.swing.text.MaskFormatter(mask.getText());
            } catch (Throwable t) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this,
                        "Invalid Mask.  Please review the following characters: # - any number" +
                        " \n U - any alphabetic character, lower case will be mapped to upper" +
                        " \n L - any alphabetic character, upper case will be mapped to lower" +
                        " \n A - any alphabetic character or number " +
                        " \n ? - any alphabetic character " +
                        " \n H - any hex character " +
                        " \n * - anything" +
                        " \n ' - escape character, used to escape any of the special " +
                        " \n     formatting characters" +
                        " \n Other characters may be entered to require their presence " +
                        " \n     in the field position.",
                        "Field Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        return true;
    }

    /**
     * Check if OK should be enabled
     * Called on:
     *      -- change in text field
     *      -- change in check box selection
     *      -- change in team combo selection
     * Required:
     *      -- fieldName
     *      -- at least one check box
     *      -- password, if this is an Add, but not if Edit
     * Not required: TBD: ???
     *      -- First and Last Name
     *      -- Team
     */
    private void checkOkSetEnabled() {
        if (!fieldName.getText().equals("") && !typeCombo.getText().equals("")) {
            okButton.setEnabled(true);
        } else {
            okButton.setEnabled(false);
        }
    }

    private void addControls() {
        requiredCheckBox = new JCheckBox("Required");
        requiredCheckBox.addActionListener(actionListener);
        repeatedCheckBox = new JCheckBox("Repeated");
        repeatedCheckBox.addActionListener(actionListener);
        unitizeCheckBox = new JCheckBox("Unitize");
        unitizeCheckBox.addActionListener(actionListener);
        spellcheckCheckBox = new JCheckBox("Spell Check");
        spellcheckCheckBox.addActionListener(actionListener);
        tableMandatoryCheckBox = new JCheckBox("Table Mandatory");
        fieldLevelCheckBox = new JCheckBox("Level Control");
        tableMandatoryCheckBox.addActionListener(actionListener);
        typeCombo = new LComboBox(15, typeComboString);
        typeCombo.addActionListener(actionListener);
        tableCombo.addActionListener(actionListener);
        validationCombo = new LComboBox(30);
        validationCombo.addActionListener(actionListener);
        l1_informationCombo = new LComboBox(30, l1_informationComboString);
        l1_informationCombo.addActionListener(actionListener);
        fieldName = new LTextField(40);
        tagName = new LTextField(20);
        fieldSize = new LIntegerField(5);
        minimumSize = new LIntegerField(5);
        levelIndicator = new LIntegerField(1);
        levelIndicator.setEnabled(false);
        levelIndicator.setInputVerifier(new LevelVerifier());
        fieldGroup = new LIntegerField(2);
        defaultValue = new LTextField(40);
        minValue = new LTextField(40);
        maxValue = new LTextField(40);
        mask = new LTextField(40);
        validChars = new LTextField(40);
        invalidChars = new LTextField(40);
        allowUnprintableCheckBox = new JCheckBox("Allow Unprintable Characters");
        charset = new LTextField(40);
        fieldName.setDocument(getPlainDocument());
        tagName.setDocument(getPlainDocument());
        jScrollPane1 = new javax.swing.JScrollPane();


      validationCombo = new LComboBox(30);
      validationCombo.addActionListener(actionListener);
      if(hasDTYG.isEmpty()){
          l1_informationComboString1[0] = "-- SELECT FIELD INFORMATION TYPE --";
          l1_informationComboString1[1] = "Document";            
          l1_informationCombo = new LComboBox(30, l1_informationComboString1);   
      }else{
          l1_informationComboString2[0] = "-- SELECT FIELD INFORMATION TYPE --";
          l1_informationComboString2[1] = "Source";      
          l1_informationComboString2[2] = "Folder"; 
          l1_informationComboString2[3] = "Document";  
          l1_informationCombo = new LComboBox(30, l1_informationComboString2);   
      }
         
      l1_informationCombo.addActionListener(actionListener);

        rPane.add(requiredCheckBox);
        rPane.add(repeatedCheckBox);
        rPane.add(unitizeCheckBox);
        rPane.add(spellcheckCheckBox);


        levelPane.add(levelIndicator);
        levelPane.add(fieldLevelCheckBox);
        levelPane.add(new JLabel(" Group:"));
        levelPane.add(fieldGroup);
        tablePane.add(tableCombo);
        tablePane.add(tableMandatoryCheckBox);

        sizePane.add(fieldSize);
        sizePane.add(new JLabel(" Minimum Size: "));
        sizePane.add(minimumSize);

        typePane.add(typeCombo);
        typePane.add(new JLabel("Tag Name: "));
        typePane.add(tagName);

        namePane.add(0, 0, "Field Name:", fieldName);
        description = new JTextArea(2, 50);
        description.setLineWrap(true);
        description.setBorder(new LineBorder(Color.white));
        description.setFocusable(true);
        description.setEditable(true);
        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(description);
        namePane.add(0, 1, "Description:*", jScrollPane1);
        //  namePane.add(0, 1, "Description:", description); 
        namePane.add(0, 2, "Standard Field Validations :", validationCombo);
        namePane.add(0, 3, "Field Information:*", l1_informationCombo);
        namePane.add(0, 4, "Type:", typePane);
        namePane.add(0, 5, "", rPane);
        namePane.add(0, 6, "Level Indicator:", levelPane);
        namePane.add(0, 7, "Field Size:", sizePane);
        namePane.add(0, 8, "Table Name:", tablePane);
        namePane.add(0, 9, "Default Value:", defaultValue);
        namePane.add(0, 10, "Minimum Value:", minValue);
        namePane.add(0, 11, "Maximum Value:", maxValue);

        JTextArea maskLabel = new JTextArea(null, "\n In Mask, in the position of each character entered, allow:   " +
                " \n # - any number" + "\n U - any alphabetic character, lower case will be mapped to upper  " +
                " \n L - any alphabetic character, upper case will be mapped to lower   " +
                " \n A - any alphabetic character or number " +
                " \n ? - any alphabetic character" +
                " \n H - any hex character" +
                " \n * - anything" +
                " \n ' - escape character, used to escape any of the special" +
                " \n     formatting characters " +
                " \n Other characters may be entered to require their presence" +
                " \n     in the field position.", 13, 40);
        //maskLabel.setBorder(new LineBorder(Color.black));
        maskLabel.setBackground(null);
        maskLabel.setFocusable(false);
        maskLabel.setEditable(false);
        namePane.add(0, 11, "", maskLabel);
        namePane.add(0, 12, "Mask:", mask);
        JTextArea charsLabel = new JTextArea(null, "\n In the following two fields, characters may be entered" +
                " \n to allow or disallow their presence in the field.", 3, 40);
        //charsLabel.setBorder(new LineBorder(Color.black));
        charsLabel.setBackground(null);
        charsLabel.setFocusable(false);
        charsLabel.setEditable(false);
        namePane.add(0, 13, "", charsLabel);
        namePane.add(0, 14, "Valid Characters:", validChars);
        namePane.add(0, 15, "Invalid Characters:", invalidChars);
        namePane.add(0, 16, "", allowUnprintableCheckBox);
        //namePane.add(0,11, tip, "w=2,h=12");
        tip.setEditable(false);
        tip.setFocusable(false);
        tip.setBackground(null);
        tip.setBorder(new LineBorder(Color.black));

        selectPanel.add(namePane, BorderLayout.CENTER);
        pack();
    }

    private void getValidationList() {

        final ClientTask task;
        task = new TaskExecuteQuery("Validations.getGroups");
        task.setCallback(new Runnable() {

            public void run() {
                fillValidationCombo((ResultSet) task.getResult());
            }
        });
        boolean ok = task.enqueue(this);
    }

    private void fillValidationCombo(ResultSet resultSet) {
        try {
            while (resultSet.next()) {
                String validationGroup = resultSet.getString(1);
                validationCombo.addItem(validationGroup);
            }
        } catch (SQLException e) {
            Log.quit(e);
            //remove this
            e.printStackTrace();
        }
    }

    class LevelVerifier extends InputVerifier {

        public boolean verify(JComponent input) {
            if (levelIndicator.getValue() > 0) {
                fieldLevelCheckBox.setEnabled(false);
            } else {
                fieldLevelCheckEnabled();
            }
            checkOkSetEnabled();
            return true;
        }
    }

    /**
     * Check whether field is already present in the project.
     * @return -true if field is presenet else false.
     */
    private boolean isFieldsNameExists() {
        Set<String> fieldNameSet = ((ProjectAdminPage) parent).getProjectFieldsNames();
        if (oldName != null) {
            fieldNameSet.remove(oldName);
            return (!fieldNameSet.add(fieldName.getText()));
        } else {
            return (!fieldNameSet.add(fieldName.getText()));
        }
    }

    /**
     * Fill in the common.ProjectFieldsData container with data from the screen
     * and send it to the server.
     * @return returns true if no errors, otherwise false
     */
    public boolean save() {
        if (!crossFieldEdits()) {
            return false;
        }
        //check the field name while adding the new field.
        if (isFieldsNameExists()) {
            JOptionPane.showMessageDialog(this,
                    "Field Name already exists." + "\nPlease re-enter.",
                    "Field Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        //Log.print("(AddEditProjectfields.save) "
        //          + fieldLevelCheckBox.isSelected() + "/"
        //          + requiredCheckBox.isSelected());
        if (((String) typeCombo.getSelectedItem()).equals("text")) {
            if (Integer.parseInt(fieldSize.getText()) == 0 && tableCombo.getSelectedIndex() <= 0) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this,
                        "Field Size cannot be 0 when no table has been selected." + "\nPlease re-enter.",
                        "Field Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } else if (((String) typeCombo.getSelectedItem()).equals("signed") || ((String) typeCombo.getSelectedItem()).equals("unsigned")) {
            if (Integer.parseInt(fieldSize.getText()) > 16) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this,
                        "Field Size cannot be greater than 16 for numeric field." + "\nPlease re-enter.",
                        "Field Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (defaultValue.getText().length() > Integer.parseInt(fieldSize.getText())) {
                JOptionPane.showMessageDialog(this,
                        "Default value length cannot be greater than '" +
                        Integer.parseInt(fieldSize.getText()) + "' for numeric field.\nPlease re-enter.",
                        "Field Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (((String) typeCombo.getSelectedItem()).equals("signed")) {
                try {
                    Long.parseLong(defaultValue.getText());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Default value cannot be invalid characters for numeric field" + "\nPlease re-enter.",
                            "Field Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            if (((String) typeCombo.getSelectedItem()).equals("unsigned")) {
                try {
                    Float.valueOf(defaultValue.getText());
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                            "Default value cannot be invalid characters for numeric field" + "\nPlease re-enter.",
                            "Field Error",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            
        } else if (Integer.parseInt(minimumSize.getText()) > Integer.parseInt(fieldSize.getText())) {  
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this,
                    "Minimum Size cannot be greater than Field Size." + "\nPlease re-enter.",
                    "Field Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (l1_informationCombo.getSelectedItem().toString().contains("SELECT")) {
            JOptionPane.showMessageDialog(this,
                    "Please select the Field Information type.",
                    "Field Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        //setting the description size ,field size 
        if (description.getText().trim().length() > 500) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this,
                    "Description should be maximum of  500 characters",
                    "Field Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (description.getText().trim().length() == 0) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this,
                    "pleaes provide description",
                    "Field Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;

         }
         if (((String) typeCombo.getSelectedItem()).equals("signed")) {
            try {
               Long.parseLong(defaultValue.getText());
            } catch (Exception e) {
               JOptionPane.showMessageDialog(this,
                       "Default value cannot be invalid characters for numeric field" + "\nPlease re-enter.",
                       "Field Error",
                       JOptionPane.ERROR_MESSAGE);
               return false;
            }
         }
         if (((String) typeCombo.getSelectedItem()).equals("unsigned")) {
            try {
               Float.valueOf(defaultValue.getText());
            } catch (Exception e) {
               JOptionPane.showMessageDialog(this,
                       "Default value cannot be invalid characters for numeric field" + "\nPlease re-enter.",
                       "Field Error",
                       JOptionPane.ERROR_MESSAGE);
               return false;
            }
         }
      //ends here
    //  }
      else if (Integer.parseInt(minimumSize.getText()) > Integer.parseInt(fieldSize.getText())) {
         Toolkit.getDefaultToolkit().beep();
         JOptionPane.showMessageDialog(this,
                 "Minimum Size cannot be greater than Field Size." + "\nPlease re-enter.",
                 "Field Error",
                 JOptionPane.ERROR_MESSAGE);
         return false;
      }else if(l1_informationCombo.getSelectedItem().toString().contains("SELECT")){
         JOptionPane.showMessageDialog(this,
                 "Please select the Field Information type.",
                 "Field Error",
                 JOptionPane.ERROR_MESSAGE);
         return false;
      }
      //yuvaraj code ; setting the description size ,field size 
      if (description.getText().trim().length() > 500) {
         Toolkit.getDefaultToolkit().beep();
         JOptionPane.showMessageDialog(this,
                 "Description should be maximum of  500 characters",
                 "Field Error",
                 JOptionPane.ERROR_MESSAGE);
         return false;
      }
      if (description.getText().trim().length() == 0) {
         Toolkit.getDefaultToolkit().beep();
         JOptionPane.showMessageDialog(this,
                 "pleaes provide description",
                 "Field Error",
                 JOptionPane.ERROR_MESSAGE);
         return false;
      }
      if (fieldName.getText().length() > 30) {
         Toolkit.getDefaultToolkit().beep();
         JOptionPane.showMessageDialog(this,
                 "Filed Name should be maximum of 30 characters",
                 "Field Error",
                 JOptionPane.ERROR_MESSAGE);
         return false;

        }
        
        final ClientTask task;
        projectFieldsData = new ProjectFieldsData();
        projectFieldsData.projectfieldsId = projectfieldsId;
        projectFieldsData.projectId = projectId;
        projectFieldsData.sequence = sequence;
        projectFieldsData.fieldName = fieldName.getText();
        projectFieldsData.tagName = tagName.getText();
        projectFieldsData.defaultValue = defaultValue.getText();
        projectFieldsData.minValue = minValue.getText();
        projectFieldsData.maxValue = maxValue.getText();
        projectFieldsData.charset = charset.getText();
        projectFieldsData.mask = mask.getText();
        projectFieldsData.validChars = validChars.getText();
        projectFieldsData.invalidChars = invalidChars.getText();
        projectFieldsData.l1_information = l1_informationCombo.getSelectedItem().toString();

        //for description
        projectFieldsData.description = description.getText();

        //for Standard Field Validations
        projectFieldsData.standardFieldValidations = validationCombo.getText();

        if (allowUnprintableCheckBox.isEnabled() && !allowUnprintableCheckBox.isSelected()) {
            Log.print("      unprintable not selected");
            projectFieldsData.invalidChars = invalidChars.getText() + "\\u";
        }
        Log.print("(AEProjectFields.save) invalidChars=" + projectFieldsData.invalidChars);
        projectFieldsData.typeField = "";
        projectFieldsData.typeValue = "";
        if (fieldSize.getText().equals("")) {
            projectFieldsData.fieldSize = 0;
        } else {
            projectFieldsData.fieldSize = Integer.parseInt(fieldSize.getText());
        }
        if (minimumSize.getText().equals("")) {
            projectFieldsData.minimumSize = 0;
        } else {
            projectFieldsData.minimumSize = Integer.parseInt(minimumSize.getText());
        }
        projectFieldsData.repeated = repeatedCheckBox.isSelected() ? "Yes" : "No";
        projectFieldsData.unitize = unitizeCheckBox.isSelected() ? "Yes" : "No";
        projectFieldsData.required = requiredCheckBox.isSelected() ? "Yes" : "No";
        projectFieldsData.spellCheck = spellcheckCheckBox.isSelected() ? "Yes" : "No";
        projectFieldsData.fieldType = (String) typeCombo.getSelectedItem();

        int row = tableCombo.getSelectedIndex();
        if (row > -1) {
            projectFieldsData.tablespecId = tableModel.getSelectedId();
            projectFieldsData.tableMandatory = tableMandatoryCheckBox.isSelected() ? "Yes" : "No";
        } else {
            projectFieldsData.tablespecId = 0;
            projectFieldsData.tableMandatory = "No";
        }
        if (projectFieldsData.tablespecId < 0) {
            projectFieldsData.tablespecId = 0;
        }
        projectFieldsData.fieldLevel = fieldLevelCheckBox.isSelected()
                ? "*" : Integer.toString(levelIndicator.getValue());
        projectFieldsData.fieldGroup = fieldGroup.getValue();

        projectFieldsData.moveIndicator = 0;

        task = new TaskSendProjectFieldsData(projectFieldsData);
        task.enqueue(this);
        return true;
    }
}
