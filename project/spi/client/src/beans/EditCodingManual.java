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
import client.TaskEditCodingManualData;
import client.TaskExecuteQuery;
import common.EditCodingManualData;
import common.Log;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * UI for Edit mode of coding manual.
 * @see beans.EditProject
 * @see client.TaskEditCodingManual
 * @see client.TaskEditCodingManualData
 * @see ui.ProjectAdminPage
 * @author murali
 */
public class EditCodingManual extends javax.swing.JDialog {

    private JPanel selectPanel = new javax.swing.JPanel();
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    private JTextField existingFileName;
    private JTextField changeFileName;
    private LGridBag namePane = new LGridBag();
    private EditCodingManualData editCodingManualData;
    private String fileName = "";
    private int projectId = 0;

    /** Creates a new instance of EditCodingManual 
     * 
     * @param parent     Parent window from where it is called.
     * @param fileName   File name
     * @param projectId  Project Id can be retrieve from the project.project_id
     */
    public EditCodingManual(Component parent, String fileName, int projectId) {
        super(JOptionPane.getFrameForComponent(parent));
        this.fileName = fileName;
        this.projectId = projectId;
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
        setTitle("Coding Manual Configuration");
        getEditCodingManualData();
    }

    /**
     * Get the data for the manual coding for the project.
     * and make the ok button enabled.
     */
    private void getEditCodingManualData() {
        if (projectId > 0) {
            final ClientTask task;
            //Log.print("(AddEditUsers.getUserData) " + usersId);
            task = new TaskExecuteQuery("select projectName from project", Integer.toString(projectId));
            task.setCallback(new Runnable() {
                public void run() {
                    getUserByIdDataEntry((ResultSet) task.getResult());
                    // set OK disabled again
                    // (It was enabled by getUserByDataEntry when combo selection set.)
                    okButton.setEnabled(true);
                }
            });

            boolean ok = task.enqueue(this);
            okButton.setEnabled(true);
            addControls();
        }
    }

    /**
     * Called when manual data is found from the server.
     * @see getEditCodingManualData()
     * @param queryResult
     */
    private void getUserByIdDataEntry(ResultSet queryResult) {
        try {

            // updating a user                              
            existingFileName.setText(fileName);
            changeFileName.setText("");
            okButton.setEnabled(true);
        // enableAdminPriv();
        } catch (Exception e) {
            Log.quit(e);
        }
    }

    
    private void addControls() {
        
        existingFileName = new JTextField(30);
        existingFileName.setEnabled(false);
        changeFileName = new JTextField(30);
        changeFileName.getDocument().addDocumentListener(changeFileListener);
        namePane.add(0, 0, "Existing File Name:", existingFileName);
        namePane.add(0, 1, "New File Name:", changeFileName);
        selectPanel.add(namePane, BorderLayout.CENTER);
        okButton.setEnabled(false);
        pack();
    }
    
    /**
     * Create Listener for the buttons (OK and Cancel).
     */
    private ActionListener buttonComboListener = new ActionListener() {
        
        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                Object source = A.getSource();
                if (source == okButton) {

                    if (save(projectId)) {
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

    /**
     * Make the ok button enabled or disabled.
     */
    private void CheckOkEnable() {
        String newDoc = changeFileName.getText().trim();
        // if the document or file is entered then make ok button enabled
        // else disabled.
        if (newDoc.length() == 0) {
            okButton.setEnabled(false);
        } else {
            okButton.setEnabled(true);

        }
    }

    /**
     * Save the coding manuala data.
     * @param  projectId Project id for which coding manual data is going
     *         to be saved.
     * @return false if coding manual name is not entered.
     *         true if the data is sent to save.
     */
    private boolean save(int projectId) {

        final ClientTask task;

        editCodingManualData = new EditCodingManualData();

        editCodingManualData.existingFileName = existingFileName.getText();
        String codingmanual = changeFileName.getText().trim();
        if (codingmanual.length() == 0) { // checking new coding maual field not empty
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this,
                    "Please provide a new coding manual name",
                    " Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }else{            
                if (codingmanual.startsWith("http")) { //check Http path existence
                    try {
                        URL url = new URL(codingmanual);
                        URLConnection conn = url.openConnection();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(this, "Coding manual file not found.\nPlease verify the path.", "Alert", JOptionPane.INFORMATION_MESSAGE);
                        return false;
                    }
                } else {   //check unc path existence 
                    if (codingmanual.startsWith("\\\\")) {
                        File file = new File(codingmanual);
                        if (!file.isFile() || !file.exists()) {
                            JOptionPane.showMessageDialog(this, "Coding manual file not found.\nPlease verify the path.", "Alert", JOptionPane.INFORMATION_MESSAGE);
                            return false;
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Coding Manual path can be either http path or unc path.", "Alert", JOptionPane.INFORMATION_MESSAGE);
                        return false;
                    }
                }            
        }
        editCodingManualData.changeFileName = codingmanual;
        editCodingManualData.projectId = projectId;
        task = new TaskEditCodingManualData(editCodingManualData);
        task.enqueue(this);
        return true;
    }
}
