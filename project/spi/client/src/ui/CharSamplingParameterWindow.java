/*
 * CharSamplingParameterWindow.java
 *
 * Created on Spp 18, 2008, 10:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package ui;

import beans.LIntegerField;
import beans.LNumberField;
import client.ClientTask;
import client.TaskCreateCharSampling;
import client.TaskExecuteUpdate;
import client.TaskQAGroup;
import client.TaskSendProjectParameters;
import common.CommonConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Dialog Window to Get Character Sampling Parameters
 * @author rsamy
 */
public class CharSamplingParameterWindow extends javax.swing.JDialog {

    private javax.swing.JPanel projectDetailsPanel = new javax.swing.JPanel();
    private javax.swing.JPanel samplingDetailsPanel = new javax.swing.JPanel();
    private javax.swing.JPanel volumeDetailsPanel = new javax.swing.JPanel();
    private javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
    private javax.swing.JPanel parameterPanel = new javax.swing.JPanel();
    private javax.swing.JLabel projectNameLabel = new javax.swing.JLabel();
    private javax.swing.JLabel projectValueLabel = new javax.swing.JLabel();
    private javax.swing.JLabel projectCodeLabel = new javax.swing.JLabel();
    private javax.swing.JLabel projectCodeValueLabel = new javax.swing.JLabel();
    private javax.swing.JLabel requiredAccuracyLabel = new javax.swing.JLabel();
    private javax.swing.JLabel samplingMethodLabel = new javax.swing.JLabel();
    private javax.swing.JLabel samplingTypeLabel = new javax.swing.JLabel();
    private javax.swing.JLabel percentageSamplingLabel = new javax.swing.JLabel();
    private javax.swing.JLabel samplingTypeValueLabel = new javax.swing.JLabel();
    private javax.swing.JLabel samplingMethodValueLabel = new javax.swing.JLabel();
    //private javax.swing.JTextField requiredAccuracyTextField = new javax.swing.JTextField();
    private LNumberField requiredAccuracyTextField = new LNumberField(2, 1);
    private LIntegerField percentageSamplingTextField = new LIntegerField(2);
    private javax.swing.JLabel jobNameLabel = new javax.swing.JLabel();
    private javax.swing.JLabel jobNameValueLabel = new javax.swing.JLabel();
    private javax.swing.JLabel qaLevelLabel = new javax.swing.JLabel();
    private javax.swing.JLabel qaLevelValueLabel = new javax.swing.JLabel();
    private javax.swing.JButton cancelButton = new javax.swing.JButton();
    private javax.swing.JButton proceedButton = new javax.swing.JButton();
    private java.awt.GridBagConstraints gridBagConstraints = null;
    private final static String EAST = "EAST";
    private final static String WEST = "WEST";
    private Component parent;
    private Logger logger;
    private int projectId;
    private int volumeId;
    private String projectName;
    private JFrame activitySelectionFrame;
    private int percentageSampling;
    private float accuracy;
    
    /**
     * Create Instance of this class with following Parameter
     *
     * @param parent          //Parent of this Dialog
     * @param projectName     //Name of Project
     * @param volumeName      //Name Of Volume
     * @param samplingtype    //Sampling Type
     * @param qaLevel         //QALevel 
     * @param projectId       //Project
     * @param volumeId        //volume
     * @param activitySelectionFrame //Instance Of ActivitySelectionFrame
     */
    public CharSamplingParameterWindow(Component parent, String projectName, String volumeName,
            String samplingtype, String qaLevel, int projectId, int volumeId, JFrame activitySelectionFrame) {
        super();
        logger = Logger.getLogger("ui");
        this.parent = parent;
        this.volumeId = volumeId;
        this.projectId = projectId;
        this.projectName = projectName;
        this.activitySelectionFrame = activitySelectionFrame;
        setLocationRelativeTo(parent);
        addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        getContentPane().add(projectDetailsPanel);
        setTitle("Character Sampling Parameter Window");
        setPreferredSize(new Dimension(350, 400));
        //percentageSamplingTextField.setColumns(2);

        projectDetailsPanel.setLayout(new java.awt.GridBagLayout());
        projectCodeValueLabel.setPreferredSize(new Dimension(150, 30));
        requiredAccuracyTextField.setPreferredSize(new Dimension(40, 30));
        //requiredAccuracyTextField.setColumns(3);
       
         
        projectValueLabel.setPreferredSize(new Dimension(150, 30));
        samplingTypeValueLabel.setPreferredSize(new Dimension(150, 30));
        samplingMethodValueLabel.setPreferredSize(new Dimension(150, 30));
        percentageSamplingTextField.setPreferredSize(new Dimension(150, 30));

        jobNameValueLabel.setPreferredSize(new Dimension(150, 30));
        qaLevelValueLabel.setPreferredSize(new Dimension(150, 30));
        projectCodeValueLabel.setPreferredSize(new Dimension(150, 30));
        projectCodeValueLabel.setPreferredSize(new Dimension(150, 30));
        projectNameLabel.setText("Project Name : ");
        projectNameLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        projectDetailsPanel.add(projectNameLabel, getGridBagConstraints(0, 0, EAST));
        projectValueLabel.setText(projectName);
        projectDetailsPanel.add(projectValueLabel, getGridBagConstraints(1, 0, EAST));
        requiredAccuracyLabel.setText("Required Accuracy : ");
        requiredAccuracyLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        projectDetailsPanel.add(requiredAccuracyLabel, getGridBagConstraints(0, 3, EAST));
        projectDetailsPanel.add(requiredAccuracyTextField, getGridBagConstraints(1, 3, WEST));
        samplingMethodLabel.setText("Sampling Method : ");
        samplingMethodLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        projectDetailsPanel.add(samplingMethodLabel, getGridBagConstraints(0, 4, EAST));
        samplingMethodValueLabel.setText("Percentage");
        projectDetailsPanel.add(samplingMethodValueLabel, getGridBagConstraints(1, 4, EAST));
        samplingTypeLabel.setText("Sampling Type : ");
        samplingTypeLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        projectDetailsPanel.add(samplingTypeLabel, getGridBagConstraints(0, 6, EAST));
        samplingTypeValueLabel.setText(samplingtype);
        projectDetailsPanel.add(samplingTypeValueLabel, getGridBagConstraints(1, 6, EAST));
        percentageSamplingLabel.setText("Percentage Sampling : ");
        percentageSamplingLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        projectDetailsPanel.add(percentageSamplingLabel, getGridBagConstraints(0, 8, EAST));
        projectDetailsPanel.add(percentageSamplingTextField, getGridBagConstraints(1, 8, WEST));
        jobNameLabel.setText("Job Name : ");
        jobNameLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        projectDetailsPanel.add(jobNameLabel, getGridBagConstraints(0, 10, EAST));
        jobNameValueLabel.setText(volumeName);
        projectDetailsPanel.add(jobNameValueLabel, getGridBagConstraints(1, 10, EAST));
        qaLevelLabel.setText("QA Level : ");
        qaLevelLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        projectDetailsPanel.add(qaLevelLabel, getGridBagConstraints(0, 12, EAST));
        qaLevelValueLabel.setText(qaLevel);
        projectDetailsPanel.add(qaLevelValueLabel, getGridBagConstraints(1, 12, EAST));
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        projectDetailsPanel.add(cancelButton, getGridBagConstraints(0, 14, EAST));
        proceedButton.setText("Proceed");
        proceedButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proceedButtonActionPerformed(evt);
            }
        });
        projectDetailsPanel.add(proceedButton, getGridBagConstraints(1, 14, WEST));

        setVisible(true);
        pack();
        parent.setVisible(false);

    }
    
    /**
     * Create Instance of this class
     */
    public CharSamplingParameterWindow() {
        super();
        getContentPane().add(parameterPanel);
        parameterPanel.setLayout(new java.awt.GridLayout(4, 1));
        parameterPanel.add(projectDetailsPanel);
        parameterPanel.add(samplingDetailsPanel);
        parameterPanel.add(volumeDetailsPanel);
        parameterPanel.add(buttonPanel);
    }

    /**
     * This Method used to create GridBagcontraints for a given values
     *
     * @param x  //Position x
     * @param y  //Position y
     * @param directions //Direction
     * @return GridBagConstraints  //java.awt.GridBagConstraints
     */
    private GridBagConstraints getGridBagConstraints(int x, int y, String directions) {
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = x;
        gridBagConstraints.gridy = y;
        if (null != directions) {
            if (directions.equals(EAST)) {
                gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            } else if (directions.equals(WEST)) {
                gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            }

        }

        return gridBagConstraints;
    }

    /**
     * This Method get Invoked when cancel button is clicked
     *
     * @param evt  //Action Event
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
        dispose();
        if (null != parent) {
            ClientTask task = new TaskExecuteUpdate("SplitPaneViewer.closeMenuItem");
            task.enqueue();
            parent.setVisible(true);
        } else {
            logger.info("Null Value for parent object");
        }
    }

    /**
     * This Method get Invoked when the Procced button is clicked
     *
     * @param evt  //Action Event
     */
    private void proceedButtonActionPerformed(java.awt.event.ActionEvent evt) {
        logger.info("Proceed Buttom Clicked");
        
        boolean isFormDetailsFilled = true;

        if (null == requiredAccuracyTextField.getText() || requiredAccuracyTextField.getText().equals("") || requiredAccuracyTextField.getText().equals(" ")) {

            JOptionPane.showMessageDialog(this, "Please Enter 'Required Accuracy'");
            isFormDetailsFilled = false;

        } else if (percentageSamplingTextField.getValue() <= 0) {

            JOptionPane.showMessageDialog(this, "Please Enter 'Percentage Sampling'");
            isFormDetailsFilled = false;

        }

        if (isFormDetailsFilled) {
            percentageSampling = Integer.parseInt(percentageSamplingTextField.getText());
            try {
                accuracy = Float.parseFloat(requiredAccuracyTextField.getText());
                proceedButton.setEnabled(false);
                createSamplingDetails(accuracy);
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please Enter Valid Number");
            }

        }
    }
    String fields = "";
    String coders = "";

    /**
     * This Method used to open QAGroupAssignPage with Sampled Groups
     *
     * @param selectedVolumeId  //volume id
     * @param selectedFields    //project fields
     * @param selectedCoders    //selected coders
     */
    private void openViewerForQA(int selectedVolumeId, String selectedFields, String selectedCoders) {


        final String pName = projectName;
        final int volumeId = selectedVolumeId;
        this.fields = selectedFields;
        this.coders = selectedCoders;
        final TaskQAGroup task = new TaskQAGroup(selectedVolumeId);
        task.setCallback(new Runnable() {

            public void run() {
                try {
                    QAGroupAssignPage qa_groupviewer = new QAGroupAssignPage((Map) task.getResult(), pName, volumeId, coders, fields, activitySelectionFrame);

                    qa_groupviewer.setVisible(true);
                    setVisible(false);
                    dispose();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        task.enqueue(this);

    }

    /**
     * This Method used to create the sampling details for given details
     *
     * @param accuracy  //Required Accuracy
     */
    private void createSamplingDetails(float accuracy) {
        final TaskSendProjectParameters task = new TaskSendProjectParameters(volumeId, 0, "Normal", "0.1", projectId, "Fixed-Percentage",
                "Character Sampling", "" + accuracy);
        task.setCallback(new Runnable() {

            public void run() {
                try {

                    createCharacterSampling();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        task.enqueue(this);
    }

    /**
     * This Method used to create 'Character Sampling' by invoking the task 'TaskCreateCharSampling'
     */
    private void createCharacterSampling() {

        final TaskCreateCharSampling task = new TaskCreateCharSampling(volumeId, projectId, percentageSampling,
                CommonConstants.QA_SAMPLING_CHARACTER,accuracy);
        task.setCallback(new Runnable() {

            public void run() {
                try {
                    List result = (List) task.getResult();
                    String fields = "";
                    String coders = "";
                    if (null != result) {
                        fields = result.get(0).toString();
                        coders = result.get(1).toString();
                    }
                    openViewerForQA(volumeId, fields, coders);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        });
        task.enqueue(this);
    }

    /**
     * This Method Get Invoked when Window is closed
     *
     * @param evt  //Window event
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
        if (null != parent) {
            ClientTask task = new TaskExecuteUpdate("SplitPaneViewer.closeMenuItem");
            task.enqueue();
            parent.setVisible(true);
        } else {
            logger.info("Null Value for parent object");
        }
    }
}
