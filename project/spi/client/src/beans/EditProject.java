/*
 * EditCodingManual.java
 *
 * Created on January 27, 2008, 10:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package beans;

import client.ClientTask;
import client.TaskEditProjectData;
import client.TaskExecuteQuery;
import common.EditCodingManualData;
import common.Log;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import model.ManagedComboModel;
import model.SQLManagedComboModel;

/**
 * UI for editing project. It is called from <code>ui.ProjectAdminPage</code>
 * @see ui.ProjectAdminPage
 * @author murali
 */
public class EditProject extends javax.swing.JDialog {

    private ManagedComboModel projectModel = null;
    private JPanel selectPanel = new javax.swing.JPanel();
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    private JButton browse = new JButton("...");
    private javax.swing.JRadioButton http = new JRadioButton();
    private javax.swing.JRadioButton net = new JRadioButton();
    private JTextField changeFileName;
    private javax.swing.JLabel volumeLabel = new JLabel();
    private javax.swing.JLabel pathTypeLabel = new JLabel();
    private javax.swing.JLabel serverIP_PortLabel = new JLabel();
    private javax.swing.JLabel serverPortLabel = new JLabel();
    private javax.swing.JLabel newImagePathLabel = new JLabel();
    private javax.swing.JComboBox projectCombo = new JComboBox();
    private javax.swing.JPanel projectPanel = new javax.swing.JPanel();
    private javax.swing.JPanel pathTypePanel = new javax.swing.JPanel();
    private javax.swing.JPanel editImagePathPanel = new javax.swing.JPanel();
    private javax.swing.JPanel serverIP_PortPanel = new javax.swing.JPanel();
    private javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
    private javax.swing.ButtonGroup pathTypeButtonGroup = new javax.swing.ButtonGroup();
    private javax.swing.JTextField editIamgePath = new JTextField();
    private javax.swing.JLabel internal_volume_nameLabel = new JLabel();
    private javax.swing.JPanel internal_volume_namePanel = new javax.swing.JPanel();
    private LTextField internal_volume_nameTextField = new LTextField();
    private LTextField serverIP = new LTextField();
    private JTextField field;
    private LTextField port = new LTextField();
    private javax.swing.JPanel dateField = new DateSelectionField();
    private javax.swing.JLabel volumeCompletionDateLabel = new JLabel();
    private java.awt.GridBagConstraints gridBagConstraints;
    private EditCodingManualData editCodingManualData;
    private String fileName = "";
    private int projectId = 0;
    private int volumeId = 0;
    private String image_server = "";
    private String vol_completed_date = "";
    private String type = "";
    private String ip = "";
    private String port_number = "";

    /** Creates a new instance of EditCodingManual with the following parameters
     * 
     * @param parent      Parent window from it is called.
     * @param fileName    File name
     * @param projectId   Project Id for which is going to be updated.
     */
    public EditProject(Component parent, String fileName, int projectId) {
        super(JOptionPane.getFrameForComponent(parent));
        this.fileName = fileName;
        this.projectId = projectId;

        getContentPane().add(selectPanel);
        selectPanel.setLayout(new java.awt.GridBagLayout());

        volumeLabel.setFont(new java.awt.Font("Dialog", 0, 11));
        volumeLabel.setText("  Select Volume:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectPanel.add(volumeLabel, gridBagConstraints);

        projectCombo.setEditable(false);
        projectCombo.setPreferredSize(new java.awt.Dimension(175, 25));
        projectCombo.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                volumeComboActionPerformed(evt);
            }
        });

        projectPanel.add(projectCombo);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectPanel.add(projectPanel, gridBagConstraints);

        internal_volume_nameLabel.setFont(new java.awt.Font("Dialog", 0, 11));
        internal_volume_nameLabel.setText(" Internal volume :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectPanel.add(internal_volume_nameLabel, gridBagConstraints);

        internal_volume_nameTextField.setColumns(20);
        internal_volume_namePanel.add(internal_volume_nameTextField);
        volumeCompletionDateLabel.setText("Volume Completion Date:");
        internal_volume_namePanel.add(volumeCompletionDateLabel);

        dateField.getComponent(1).setMaximumSize(new Dimension(50, 20));
        field = (JTextField) dateField.getComponent(0);
        field.setColumns(10);
        dateField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateFieldPropertyChange(evt);
            }
        });

        internal_volume_namePanel.add(dateField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectPanel.add(internal_volume_namePanel, gridBagConstraints);

        pathTypeLabel.setFont(new java.awt.Font("Dialog", 0, 11));
        pathTypeLabel.setText("  Path Type: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectPanel.add(pathTypeLabel, gridBagConstraints);

        net.addActionListener(buttonComboListener);
        http.addActionListener(buttonComboListener);
        net.setText("UNC Path");
        net.setSelected(true);
        net.setToolTipText("File Type UNC");
        http.setText("Image Server ");
        http.setToolTipText("File Type HTTP");
        pathTypeButtonGroup.add(net);
        pathTypeButtonGroup.add(http);
        pathTypePanel.add(net);
        pathTypePanel.add(http);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectPanel.add(pathTypePanel, gridBagConstraints);

        newImagePathLabel.setFont(new java.awt.Font("Dialog", 0, 11));
        newImagePathLabel.setText("   Image Path: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectPanel.add(newImagePathLabel, gridBagConstraints);

        editIamgePath.setColumns(50);
        editIamgePath.setEnabled(true);
        editImagePathPanel.add(editIamgePath);
        browse.addActionListener(buttonComboListener);
        editImagePathPanel.add(browse);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectPanel.add(editImagePathPanel, gridBagConstraints);

        serverIP_PortLabel.setFont(new java.awt.Font("Dialog", 0, 11));
        serverIP_PortLabel.setText("   Image Server IP :");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectPanel.add(serverIP_PortLabel, gridBagConstraints);

        serverIP.setColumns(15);
        serverIP.setEnabled(false);
        port.setColumns(4);
        port.setMaximumSize(new Dimension(4, 10));
        port.setEnabled(false);
        serverIP_PortPanel.add(serverIP);
        serverPortLabel.setText("Image Server Port :");
        serverIP_PortPanel.add(serverPortLabel);
        serverIP_PortPanel.add(port);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        selectPanel.add(serverIP_PortPanel, gridBagConstraints);

        okButton.addActionListener(buttonComboListener);
        okButton.setEnabled(false);
        cancelButton.addActionListener(buttonComboListener);
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.CENTER;
        selectPanel.add(buttonPanel, gridBagConstraints);
        setTitle("Edit Volume Configuration");
        pack();
        tabSelected();
    }
    /**
     * Create actionListener for the buttons (Ok and cancel button).
     */
    private ActionListener buttonComboListener = new ActionListener() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                Object source = A.getSource();
                //If ok button is clicked.
                if (source == okButton) {
                    // save the project.
                    // If project is saved successfully then dispose
                    // this dialog.
                    if (save(projectId)) {
                        dispose();
                    }
                    //if cancel button is clicked.
                } else if (source == cancelButton) {
                    // dipose this dialog window.
                    dispose();
                } else if (source == http) {
                    // if <code>JRadioButton http </code>  is selected 
                    // enable the <code>serverIp</code> to enter the 
                    // server path.
                    serverIP.setEnabled(true);
                    port.setEnabled(true);
                    if (http.isSelected()) {
                        serverIP.setText(ip);
                        port.setText(port_number);
                    }
                } else if (source == net) {
                    serverIP.setEnabled(false);
                    port.setEnabled(false);
                    serverIP.setText("");
                    port.setText("");
                    // if the <code>browse</code> is clicked.
                    // Open the file dialog to select the file.                    
                } else if (source == browse) {
                    JFileChooser saveFile = new JFileChooser();
                    saveFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    int i = saveFile.showSaveDialog(browse);
                    if (i == JFileChooser.APPROVE_OPTION) {
                        editIamgePath.setText(saveFile.getSelectedFile().getPath());
                    }
                }
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    };
    
    DocumentListener changeFileListener = new DocumentListener() {

        public void insertUpdate(DocumentEvent e) {
            CheckOkEnable();
        }

        public void removeUpdate(DocumentEvent e) {
            CheckOkEnable();

        }

        public void changedUpdate(DocumentEvent e) {
            CheckOkEnable();

        }
    };

    /*
     * Make the ok button enabled or disabled.
     */
    private void CheckOkEnable() {
        String newDoc = changeFileName.getText().trim();
        if (newDoc.length() == 0) {
            okButton.setEnabled(false);
        } else {
            okButton.setEnabled(true);

        }
    }

    /**
     * Save the data for the project
     * @param projectId  Project Id for which the data is being saved.
     * @return true if after successfully data entry for the project.
     *         false if any required field is not properly filled up.
     */
    private boolean save(int projectId) {
        final ClientTask task;
        editCodingManualData = new EditCodingManualData();
        editCodingManualData.existingFileName = editIamgePath.getText();
        editCodingManualData.projectId = projectId;
        editCodingManualData.volume_id = volumeId;
        editCodingManualData.internal_volume = internal_volume_nameTextField.getText();
        editCodingManualData.volume_completion_date = field.getText();
        String date = field.getText();
        if(date.trim().length() == 0){
             JOptionPane.showMessageDialog(this,
                            "Enter the volume completion date.");
            return false;
        }
        String[] split = date.split("-");
        Calendar cal = new GregorianCalendar();
        //system input
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);

        int day = cal.get(Calendar.DAY_OF_MONTH);
        //userInput
        int inputYear = Integer.parseInt(split[0]);
        int inputMonth = Integer.parseInt(split[1]);
        int inputDate = Integer.parseInt(split[2]);
        // Date should not be less than current date.
        if (inputYear >= year) {
            if (inputMonth >= month) {
                if (inputDate >= day) {
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Date should Be Greater/Equal to Current Date");
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Date should Be Greater/Equal to Current Date");
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Date should Be Greater/Equal to Current Date");
            return false;
        }

        String serverIp = serverIP.getText();
        // Server IP is required field.
        if (http.isSelected()) {
            if (serverIp.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "serverIP should not be empty");
                return false;
            } else {
                // Special character is not allowed in server ip.
                Pattern specialPattern = Pattern.compile("[a-z,A-Z]");
                Matcher specialMatcher = specialPattern.matcher(serverIp);
                Pattern specialPattern1 = Pattern.compile("[/,+=! \\[#$-]");
                Matcher specialMatcher1 = specialPattern1.matcher(serverIp);
                if (specialMatcher.find()) {
                    JOptionPane.showMessageDialog(this,
                            "Character are not allowed for serverIP");
                    return false;
                } else if (specialMatcher1.find()) {
                    JOptionPane.showMessageDialog(this,
                            "Special Character are not allowed for serverIP");
                    return false;
                }
                editCodingManualData.serverIP = serverIP.getText();
            }
            String portNumber = port.getText();
            //port number is required.
            if (portNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "port number should not be empty");
                return false;
            } else {
                try {
                    int x = Integer.parseInt(portNumber);

                } catch (NumberFormatException nFE) {
                    JOptionPane.showMessageDialog(this,
                            "Only integer are allowed for port number");
                    return false;
                }

                editCodingManualData.port = port.getText();
            }

        }
        if (editIamgePath.getText().trim().length() == 0) { // checking new coding maual field not empty

            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this,
                    "Please provide a new coding manual name",
                    " Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }      
        task = new TaskEditProjectData(editCodingManualData);
        task.enqueue(this);
        return true;
    }

    
    private void volumeComboActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            int sel = projectCombo.getSelectedIndex();
            if (sel > -1) {
                volumeId = ((SQLManagedComboModel) projectCombo.getModel()).getIdAt(sel);
            }
            if (volumeId > 0) {

                final ClientTask task = new TaskExecuteQuery("getImagePath", Integer.toString(volumeId));
                task.setCallback(new Runnable() {

                    public void run() {
                        try {
                            getUserByIdDataEntry((ResultSet) task.getResult());
                        } catch (Throwable th) {
                            Log.quit(th);
                        }
                    }
                });
                task.enqueue();
                okButton.setEnabled(true);
            }
        } catch (Exception e) {
             Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Fill up the fields with the result fetched from the server.
     * and update the new values.
     * @param queryResult Result from the server.
     */
    private void getUserByIdDataEntry(ResultSet queryResult) {
        if (queryResult != null) {
            try {
                if (queryResult.next()) {
                    // updating a user
                    editIamgePath.setText(queryResult.getString(1));                    
                    internal_volume_nameTextField.setText(queryResult.getString(2));
                    image_server = queryResult.getString(3);
                    vol_completed_date = queryResult.getString(4);  
                    
                    if(vol_completed_date != null && vol_completed_date.length() != 0){
                        String[] splitDate = vol_completed_date.split(" ");              
                        field.setText(splitDate[0]);
                    }else{
                        field.setText("");
                    }
                    
                    if(image_server != null){
                        if (image_server.startsWith("u")) {
                            net.setSelected(true);
                            http.setSelected(false);
                            port.setText("");
                            serverIP.setText("");
                        } else if (image_server.startsWith("i")) {
                            http.setSelected(true);
                            port.setEnabled(true);
                            net.setSelected(false);
                            serverIP.setEnabled(true);
                            String tokens[] = image_server.split(":");

                            type = tokens[0];
                            ip = tokens[1];
                            port_number = tokens[2];

                            serverIP.setText(ip);
                            port.setText(port_number);
                        }
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(EditProject.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void dateFieldPropertyChange(java.beans.PropertyChangeEvent evt) {
    }

    public void tabSelected() {
        if (projectModel == null) {
            projectModel = new SQLManagedComboModel("Batching.get volume", projectId);
            projectModel.register();
            projectCombo.setModel(projectModel);
        }   
    }
}
