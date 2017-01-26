/*
 * EditProjectFieldValidation.java
 *
 * Created on January 4, 2008, 12:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package beans;

import bsh.EvalError;
import bsh.Interpreter;
import bsh.TargetError;
import client.TaskEditAdvanceValidations;
import ui.AdvanceValidationPage;
import client.ClientTask;
import client.TaskAddValidationData;
import common.FieldValidationData;
import common.Log;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JScrollPane;

/**
 * Edit mode for the validation of a field. It is called from <code>ui.AdvanceValidationPage</code>.
 * @see ui.AdvanceValidationPage
 * @author sunil
 */
public class AddEditProjectFieldValidation extends javax.swing.JDialog {

    private static final int FIELD_LENGTH = 255;
    // function scope
    private final String FIELD = "Field";
    private final String DOCUMENT = "Document";
    private final String VOLUME = "Volume";
    // Function Type 
    private static final String INPUT_VALIDATION = "Input Validation";
    private static final String OUTPUT_FORMAT = "Output Format";
    private static final String OUTPUT_ERROR = "Output Error";
    private static final String OUTPUT_WARNING = "Output Warning";
    private String mode = ADD;
    private static final String ADD = "Add";
    private static final String EDIT = "Edit";
    private JPanel selectPanel = new javax.swing.JPanel();
    private JPanel typePanel;
    private JPanel errorTypePanel;
    private JPanel validatonTypePanel;
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    private JTextField functionName;
    private JCheckBox generic;
    private JTextField description;
    private LComboBox status;
    private JTextField errorMessage;
    private JTextField userInput;
    private LGridBag namePane = new LGridBag();
    private JScrollPane scrollpane;
    private ScriptEditor methodBody;
    private ButtonGroup buttonGroup1;
    private JComboBox errorTypeComboBox;
    private JLabel errorTypeLabel;
    private JRadioButton formatRadioButton;
    private JRadioButton validationRadioButton;
    private java.sql.ResultSet results = null;
    private AdvanceValidationPage parent;
    private String isGeneric = "false";
    private String std_group_name = "";
    private String std_field_name;
    private String[] statusString = {"False", "True"};
    private int fieldId = 0;
    private int validation_mapping_details_id = 0;
    private int validation_functions_master_id = 0;
    private int std_functions_group_id = -1;
    private int std_group_id = -1;
    private int projectId;
    private ArrayList functionNameList;
    private String scope;

    /** Creates a new instance of AddNewProjectFieldValidation to add new validation for the field.
     *  remember the parameters
     * @param parent         Parent window
     * @param fieldId        Field Id for which the validation is going to be created.
     * @param projectId      Project Id, Poject field belongs to.
     * @param std_field_name Global Field name 
     * @param std_group_id   Generic Id for the field. 
     * @param std_group_name Generic group name 
     * @param functionNameList 
     * @param scope
     */
    public AddEditProjectFieldValidation(Component parent, int fieldId, int projectId, String std_field_name,
            int std_group_id, String std_group_name, ArrayList functionNameList, String scope) {
        super(JOptionPane.getFrameForComponent(parent));
        this.parent = (AdvanceValidationPage) parent;
        this.fieldId = fieldId;
        this.projectId = projectId;
        this.std_field_name = std_field_name;
        this.std_group_id = std_group_id;
        this.std_group_name = std_group_name;
        this.functionNameList = functionNameList;
        this.mode = ADD;
        this.scope = scope;

        getContentPane().add(selectPanel, BorderLayout.CENTER);
        selectPanel.setLayout(new BorderLayout());
        selectPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        okButton.addActionListener(buttonComboListener);
        //disabled for edit until first change
        okButton.setEnabled(false);
        cancelButton.addActionListener(buttonComboListener);
        JPanel outerButtonPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 30, 20));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        outerButtonPanel.add(buttonPanel);
        selectPanel.add(outerButtonPanel, BorderLayout.SOUTH);

        //if the field belongs to a std field the mention it in title else make it blank. 
        String std_group_title = (std_group_name.equals("") || !scope.equals(FIELD) ? "" : "[" + std_group_name + "] ");

        setTitle("SPiCA Validation Field Definition " + std_group_title);
        addControls(true);
    }

    /** Creates a new instance of EditProjectFieldValidation to edit the existing validation of the field.
     * 
     * @param parent                          Parent window
     * @param projectfields_id                Field Id for which the validaton is being updated.   
     * @param validation_mapping_details_id   Can be achieved from validation_mapping_details.validation_mapping_details_id
     * @param validation_functions_master_id  Can be achieved from validation_functions_master.validation_functions_master_id
     * @param std_functions_group_id          The group, the validation belongs to.
     * @param std_group_id                    Id for the group.
     * @param std_group_name                  Group name.
     */
    public AddEditProjectFieldValidation(Component parent, int projectfields_id, int validation_mapping_details_id,
            int validation_functions_master_id, int std_functions_group_id,
            int std_group_id, String std_group_name, String scope) {
        super(JOptionPane.getFrameForComponent(parent));
        this.validation_mapping_details_id = validation_mapping_details_id;
        this.validation_functions_master_id = validation_functions_master_id;
        this.std_functions_group_id = std_functions_group_id;
        this.std_group_id = std_group_id;
        this.std_group_name = std_group_name;
        this.fieldId = projectfields_id;
        this.parent = (AdvanceValidationPage) parent;
        this.isGeneric = "false";
        this.mode = EDIT;
        this.scope = scope;

        getContentPane().add(selectPanel, BorderLayout.CENTER);
        selectPanel.setLayout(new BorderLayout());
        selectPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        okButton.addActionListener(buttonComboListener);
        //disabled for edit until first change
        okButton.setEnabled(false);
        cancelButton.addActionListener(buttonComboListener);
        JPanel outerButtonPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 30, 20));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        outerButtonPanel.add(buttonPanel);
        selectPanel.add(outerButtonPanel, BorderLayout.SOUTH);

        //if the field belongs to a std field the mention it in title else make it blank. 
        String std_group_title = (std_group_name.equals("") || !scope.equals(FIELD) ? "" : "[" + std_group_name + "] ");

        setTitle("SPiCA Validation Field Definition " + std_group_title);
        addControls(false);

        getValidationData();
    }

    /**
     *get existing details of the selected function to be updated
     */
    private void getValidationData() {
        if (fieldId > 0 || !(scope.equals(FIELD))) {
            final ClientTask task;

            FieldValidationData fieldValidationData = getFieldValidationData(true);
            task = new TaskEditAdvanceValidations(fieldValidationData);
            task.setCallback(new Runnable() {

                public void run() {
                    results = (java.sql.ResultSet) task.getResult();
                    setDataInTextFields(results);
                    okButton.setEnabled(true);
                }
            });
            boolean ok = task.enqueue(this);
        }
    }

    /**
     * filling the records of the function in different contols
     */
    private void setDataInTextFields(ResultSet queryResult) {
        try {
            if (queryResult.next()) {
                // updating a user  
                functionName.setText(queryResult.getString(1));
                description.setText(queryResult.getString(2));
                String type = queryResult.getString(3);
                setTypeUiComponent(type);
                errorMessage.setText(queryResult.getString(4));
                userInput.setText(queryResult.getString(5));
                status.setText(queryResult.getString(6));                
                methodBody.setText(queryResult.getString(7));
                //append(Color.RED, queryResult.getString(7));
                if (std_functions_group_id != -1) {
                    generic.setSelected(true);
                    generic.setEnabled(false);
                    isGeneric = "true";

                }
                if (std_group_id < 0) {
                    generic.setSelected(false);
                    generic.setEnabled(false);
                    isGeneric = "false";
                }
            }
        } catch (SQLException e) {
            Log.quit(e);
        }
    }

    /**
     * Set the type UI component from the field type.
     * @param type Type of the field.
     */
    private void setTypeUiComponent(String type) {
        if (null != type && type.equals(OUTPUT_FORMAT)) {
            formatRadioButton.setSelected(true);
            validationRadioButton.setEnabled(false);
            errorTypePanel.setVisible(false);
        } else {
            validationRadioButton.setSelected(true);
            formatRadioButton.setEnabled(false);
            errorTypePanel.setVisible(true);
            if (null != type && !type.trim().equals("")) {
                errorTypeComboBox.setSelectedItem(type);
            } else {
                errorTypeComboBox.setSelectedItem(INPUT_VALIDATION);
            }

        }
    }
    /**
     * action performed on clicking ok button
     */
    private ActionListener buttonComboListener = new ActionListener() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                Object source = A.getSource();
                if (source == okButton) {
                    if (save()) {
                        dispose();
                    }
                } else if (source == cancelButton) {
                    dispose();
                }
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    };

    /**
     * adding controls to the Edit page        
     */
    private void addControls(boolean enable) {
        scrollpane = new JScrollPane();
        functionName = new JTextField(50);
        functionName.setEditable(enable);
        if (enable) {
            functionName.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    generateDefaultMethod();
                }
            });
        }

        generic = new JCheckBox();
        description = new JTextField(50);
        description.setEditable(enable);
        status = new LComboBox(30, statusString);
        status.addActionListener(buttonComboListener);
        errorMessage = new JTextField(50);
        errorMessage.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                checkFields();
            }
        });
        userInput = new JTextField(50);
        methodBody = new ScriptEditor();
        methodBody.setPreferredSize(new Dimension(25, 50));     


        //Create add type panel----------------------------------------
        buttonGroup1 = new javax.swing.ButtonGroup();
        validatonTypePanel = new javax.swing.JPanel();
        typePanel = new javax.swing.JPanel();
        validationRadioButton = new javax.swing.JRadioButton();
        formatRadioButton = new javax.swing.JRadioButton();
        errorTypePanel = new javax.swing.JPanel();
        errorTypeLabel = new javax.swing.JLabel();
        errorTypeComboBox = new javax.swing.JComboBox();

        //typePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        validatonTypePanel.setName("typePanel"); // NOI18N

        validatonTypePanel.setLayout(new java.awt.GridLayout(1, 0, 20, 0));

        //jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        typePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        typePanel.setName("jPanel1"); // NOI18N

        typePanel.setLayout(new javax.swing.BoxLayout(typePanel, javax.swing.BoxLayout.LINE_AXIS));

        buttonGroup1.add(validationRadioButton);
        validationRadioButton.setSelected(true);
        validationRadioButton.setText("Validation "); // NOI18N

        validationRadioButton.setName("validationRadioButton"); // NOI18N

        validationRadioButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validationRadioButtonActionPerformed(evt);
            }

            private void validationRadioButtonActionPerformed(ActionEvent evt) {
                errorTypePanel.setVisible(true);
            }
        });

        typePanel.add(validationRadioButton);


        formatRadioButton.setText("format"); // NOI18N

        formatRadioButton.setName("formatRadioButton"); // NOI18N

        formatRadioButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                formatRadioButtonActionPerformed(evt);
            }

            private void formatRadioButtonActionPerformed(ActionEvent evt) {
                errorTypePanel.setVisible(false);
            }
        });
        buttonGroup1.add(formatRadioButton);
        typePanel.add(formatRadioButton);

        validatonTypePanel.add(typePanel);

        //jPanel2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        errorTypePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        errorTypePanel.setName("jPanel2"); // NOI18N

        errorTypeLabel.setText("Error Type"); // NOI18N

        errorTypeLabel.setName("errorTypeLabel"); // NOI18N

        errorTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{INPUT_VALIDATION, OUTPUT_ERROR, OUTPUT_WARNING}));
        errorTypeComboBox.setName("errorTypeComboBox"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(errorTypePanel);
        errorTypePanel.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).
                addGroup(jPanel2Layout.createSequentialGroup().addContainerGap().addComponent(errorTypeLabel).
                addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).
                addComponent(errorTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
                javax.swing.GroupLayout.PREFERRED_SIZE)));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).
                addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).
                addComponent(errorTypeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE).
                addComponent(errorTypeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 19, Short.MAX_VALUE)));

        validatonTypePanel.add(errorTypePanel);
        //-----------------------------------

        //methodBody.setLineWrap(true);
        scrollpane.setPreferredSize(new Dimension(657, 400));
        scrollpane.setViewportView(methodBody);

        int i = 0;
        namePane.add(0, i++, "Function Name:", functionName);
        String genericLabel = "Make Global:";
        if(scope.equals(FIELD)){
            namePane.add(0, i++, genericLabel, generic);
        }
        namePane.add(0, i++, "Description:", description);
        namePane.add(0, i++, "Type:", validatonTypePanel);
        namePane.add(0, i++, "ErrorMessage:", errorMessage);
        namePane.add(0, i++, "Status:", status);
        namePane.add(0, i++, "Match Data:", userInput);
        namePane.add(0, i++, "Method Body:", scrollpane);
        selectPanel.add(namePane, BorderLayout.CENTER);
        pack();
    }

    /**
     * Enable the Ok button when all required field is filled up.
     */
    private void checkFields() {
        checkFieldLength();
        if ((errorMessage.getText().isEmpty()) || methodBody.getText().isEmpty()) {
            okButton.setEnabled(false);
        } else {
            okButton.setEnabled(true);
        }
    }

    /**
     * Check the field length.
     * If the length of the field is increased beyond the limit then
     * ignore the charaters.
     */
    private void checkFieldLength() {
        String value;
        if ((value = functionName.getText()).length() > 50) {
            functionName.setText(value.substring(0, 49));
        }
        if ((value = description.getText()).length() > FIELD_LENGTH) {
            description.setText(value.substring(0, FIELD_LENGTH));
        }

        if ((value = errorMessage.getText()).length() > FIELD_LENGTH) {
            errorMessage.setText(value.substring(0, FIELD_LENGTH));
        }
    }

    /**
     * saving the changes made in the selected functions
     */
    private boolean save() {
        if (!checkAllFields()) {
            return false;
        }
        if (!isProperFunction()) {
            return false;
        }

        if (mode.equals(ADD)) {
            add();
        } else {
            edit();
        }
        return true;
    }

    private void edit() {

        final ClientTask task;
        FieldValidationData fieldValidationData = getFieldValidationData(false);
        task = new TaskEditAdvanceValidations(fieldValidationData);
        task.setCallback(new Runnable() {

            public void run() {
                results = (java.sql.ResultSet) task.getResult();
                parent.refreshPage();
            }
        });
        boolean ok = task.enqueue(this);
    }

    /**
     * Save the validation data for the field.
     * Adding new functions with all its details like
     * 1. function name,
     * 2. function desciption,
     * 3. error message,
     * 4. parameter
     */
    private void add() {

        final ClientTask task;
        FieldValidationData fieldValidationData = new FieldValidationData();
        fieldValidationData.standard_field_validations_mapping_id = 0;
        fieldValidationData.functionName = functionName.getText();
        fieldValidationData.description = description.getText();
        fieldValidationData.status = status.getText();
        fieldValidationData.isGeneric = Boolean.toString(generic.isSelected());
        fieldValidationData.errorMessage = errorMessage.getText();
        fieldValidationData.userInput = userInput.getText();
        fieldValidationData.methodBody = methodBody.getText();
        fieldValidationData.fieldId = fieldId;
        fieldValidationData.projectId = projectId;
        fieldValidationData.type = getType();
        fieldValidationData.scope = scope;
        Interpreter interpreter = new Interpreter();
        try {
            task = new TaskAddValidationData(fieldValidationData);
            task.setCallback(new Runnable() {

                public void run() {
                    parent.refreshPage();
                }
            });
            task.enqueue(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Fields Description, errorMessage, methodBody should not be empty
     */
    private boolean checkAllFields() {   //sunil

        if (functionName.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Method name is required");
            return false;
        } else if (description.getText().isEmpty()) {//end

            JOptionPane.showMessageDialog(this, "Desciption is required");
            return false;
        } else if (errorMessage.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Error Message is required");
            return false;
        } else if (methodBody.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Method body is required");
            return false;
        }

        return true;
    }

    /**
     * validating the funtion name,
     * Function Name should not contain special characters,
     * Function Name should not contain white spaces,
     * Function Name should not start with numeric values,     
     * Function Name should not be empty.      
     */
    private boolean checkFunctionName(String functionName) {

        String spChar[] = {"|", "\",", "=", "!", "[", "#", "$", "*", "@", "~", "`", "^", "%", "&", "/", ".", "<",
            ">", ",", "-", "+", "{", "}", "(", ")"
        };
        for (int i = 0; i < spChar.length; i++) {
            if (functionName.contains(spChar[i])) {
                JOptionPane.showMessageDialog(this, "Function Name should not contain special characters");
                return false;
            }
        }
        
        if (functionName.contains(" ")) {
            JOptionPane.showMessageDialog(this, "Function Name should not contain white spaces");
            return false;
        }

        for (int i = 0; i < 10; i++) {
            if (functionName.startsWith("" + i)) {
                JOptionPane.showMessageDialog(this, "Function Name should not start with numeric values");
                return false;
            }
        }

        if (functionName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Function Name should not be empty");
            return false;
        }

        String _method = methodBody.getText();
        String _methodName = functionName;
        Pattern pattern = Pattern.compile(_methodName);
        Matcher matcher = pattern.matcher(_method);

        boolean found = false;
        while (matcher.find()) {
            if (matcher.start() != 0 && (_method.charAt(matcher.end()) == '(')) {
                found = true;
            }
        }

        if (!found) {
            JOptionPane.showMessageDialog(this, "Method Name doesn't match");
            return false;
        }

        //Check the existing functions when new validation function is being created 
        // Do not check while  editing the validation function.
        if (mode.equals(ADD)) {
            for (Object name : functionNameList) {
                if (functionName.equalsIgnoreCase((String) name)) {
                    JOptionPane.showMessageDialog(this, functionName + " Method already exists");
                    return false;
                }
            }
        }

        return true;

    }


    /**
     * this check the newly constructed funtion syntax.
     * it will prompt if function has wrong sysntx.
     */
    private boolean isProperFunction() {
        if (!checkFunctionName(functionName.getText())) {
            return false;
        }

        String data = "";
        if(scope.equals(FIELD)){            
            data = " public script.FieldRecord data = new script.FieldRecord();" +
                   " public String codedValue = data.getCodedValue();";
        }else if(scope.equals(DOCUMENT)){
            data = " public script.DocumentRecord data = new script.DocumentRecord();" +
                   " public script.FieldData Field ;" +
                   " public String codedValue = \"\" ;";
                   
        }else if(scope.equals(VOLUME)){
            data = " public script.VolumeRecord data = new script.VolumeRecord();";
        }
        
        String validationFunctions =  data +                
                " public String execute(String methodName, String errorMessage,String param) {" +
                " String message = this.invokeMethod( methodName, new Object [] {errorMessage,param}); " +
                " return message;" +
                " }";

        Interpreter interpreter = new Interpreter();
        try {         
            String scriptFileContent = validationFunctions + "\n" + methodBody.getText();
            Object error = interpreter.eval(scriptFileContent + "execute(\"" + functionName.getText() + "\",\"" + "error_message" + "\",\"" + "param" + "\")");                            
        }catch (TargetError e) {            
            
        }catch (Exception e) {            
            e.printStackTrace();
            //JOptionPane.showMessageDialog(this, "Method has wrong syntax");
            return true;

        }
        return true;
    }

    /**
     * create the default method. 
     */
    private void generateDefaultMethod() {
        if ((functionName.getText().isEmpty()) || (description.getText().isEmpty()) ||
                (errorMessage.getText().isEmpty()) || methodBody.getText().isEmpty()) {
            okButton.setEnabled(false);
        } else {
            okButton.setEnabled(true);
        }

        String methodText = "";
        methodText += "/* @errorMessage : The error message configured for this validation\n";
        methodText += "*  @param : The parameter which has the pattern to be validated\n";
        methodText += "*                    against the coded value\n";
        if(!scope.equals(VOLUME)){
        methodText +=  "*  @codedValue : input value to be validated\n";
        }
        methodText += "*/\n\n";
        methodText += "public String " + functionName.getText();
        methodText += "(String errorMessage , String param)";
        methodText += "\n{";
        methodText += "\t\nString message = null;";
        methodText += "\n\nreturn message;\n";
        methodText += "}";
        methodBody.setText(methodText);
    }

    private String getType() {
        if (validationRadioButton.isSelected()) {
            return errorTypeComboBox.getSelectedItem().toString();
        } else {
            return OUTPUT_FORMAT;
        }
    }

    /**
     * Get the instance of <code>FieldValidationData</code>.
     * @param editOrDisplay true if the FieldValidationData  is for getting the values
     *                      from the server.
     *                      false if the <code>FieldValidationData</code> needs to be saved.
     * @return instance of <code>FieldValidationData</code>.
     */
    private FieldValidationData getFieldValidationData(boolean editOrDisplay) {
        String functionNameWithGenericCase = functionName.getText();
        if ((generic.isSelected() && std_group_id > 0 && (std_functions_group_id != std_group_id))) {
            functionNameWithGenericCase += ":" + "true";            //make it generic

        } else {
            functionNameWithGenericCase += ":" + "false";           // do not make it generic

        }
        FieldValidationData fvd = new FieldValidationData();
        fvd.functionName = functionNameWithGenericCase;
        fvd.fieldId = fieldId;
        fvd.validation_mapping_details_id = validation_mapping_details_id;
        fvd.validation_functions_master_id = validation_functions_master_id;
        fvd.editOrDisplay = new Boolean(editOrDisplay).toString();
        fvd.description = description.getText();
        fvd.errorMessage = errorMessage.getText();
        fvd.userInput = userInput.getText();
        fvd.status = status.getText();
        fvd.methodBody = methodBody.getText();
        fvd.std_group_id = std_group_id;
        fvd.isGeneric = isGeneric;
        fvd.scope = scope;
        fvd.type = getType();
        return fvd;
    }

}

