/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import client.TaskExecuteQuery;
import common.Log;
import common.msg.MessageConstants;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import model.TallyFieldSelectionModel;
import javax.swing.JOptionPane;

/**
 * Dialog Window to List Project Field which are not Selected in Listing
 * @author anurag
 */
public class AddMoreFieldsDialog extends javax.swing.JDialog implements MessageConstants {
    
    //Declaration of Private Variables
    private JFrame parent;
    private String whichStatus;    
    private javax.swing.JTable viewEditTable;
    private javax.swing.JPanel projectPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel projectCombo;
    private javax.swing.JLabel fieldCombo;
    private javax.swing.JPanel fieldsPane;
    private javax.swing.JScrollPane fieldsScrollPane;
    private javax.swing.JPanel bottomPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton proceedButton;
    private javax.swing.JButton exitButton;
     
    private int projectId = 0;
    private int volumeId = 0;
    private String projectName = "";
    private String volumeName = "";        
    private List values = null;
    private List fieldsList = null;
    private TallyFieldSelectionModel tallymodel;    
    
    /**
     * Create Instance of this Class With Following Prameter
     *
     * @param parent           //Parent Window of this Dialog
     * @param whichStatus      //Batch Status
     * @param projectId        //Project
     * @param volumeId         //Volume
     * @param projectName      //Name of Project
     * @param volumeName       //Name Of Volume
     * @param fieldsList       //Project FieldList
     */
    public AddMoreFieldsDialog(JFrame parent, String whichStatus,int projectId,int volumeId,String projectName,String volumeName,ArrayList fieldsList) {

        super(parent, true);
        setTitle("Add More Fields");
        this.parent = parent;
        this.whichStatus = whichStatus;
        this.projectId = projectId;
        this.volumeId = volumeId;
        this.projectName = projectName;
        this.volumeName = volumeName;
        this.fieldsList = fieldsList;
        initComponents();                
        getListingFields(whichStatus);        
    }

    private void initComponents() {

        projectPane = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        projectCombo = new javax.swing.JLabel();
        fieldCombo = new javax.swing.JLabel();
        viewEditTable = new javax.swing.JTable();

        fieldsPane = new javax.swing.JPanel();
        fieldsScrollPane = new javax.swing.JScrollPane();        
        bottomPane = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        proceedButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();       

        projectPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel1.setText("Project : ");
        projectPane.add(jLabel1);

        projectPane.add(projectCombo);
        projectCombo.setText(this.projectName);
        
        projectPane.add(new JLabel("                        "));
        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel2.setText("Volume : ");
        projectPane.add(jLabel2);
        
        fieldCombo.setText(this.volumeName);
        projectPane.add(fieldCombo);

        add(projectPane, java.awt.BorderLayout.NORTH);

        fieldsPane.setLayout(new java.awt.BorderLayout());

        fieldsPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fieldsScrollPane.setPreferredSize(new java.awt.Dimension(400, 300));

        viewEditTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
            "Select", "Project Fields"
        }) {

            boolean[] canEdit = new boolean[]{
                true, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });       

        //fieldsScrollPane.setViewportView(fieldsTable);
        fieldsScrollPane.setViewportView(viewEditTable);

        fieldsPane.add(fieldsScrollPane, java.awt.BorderLayout.CENTER);

        add(fieldsPane, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 20, 0));

        proceedButton.setText("  Add  ");
        proceedButton.setEnabled(true);
        proceedButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proceedButtonActionPerformed(evt);
            }
        });
        jPanel1.add(proceedButton);
        exitButton.setText("  Cancel  ");
        exitButton.setEnabled(true);
        exitButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        jPanel1.add(exitButton);        
        bottomPane.add(jPanel1);
        add(bottomPane, java.awt.BorderLayout.SOUTH);

        //setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        //setTitle("Project Selection");
        addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

       
        pack();

    }// </editor-fold>                        
    
    /**
     * Methood To Close Dialog,Get Invoked when Dialog Window Closed
     *
     * @param evt java.awt.event.WindowEvent
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {
        this.setVisible(false);                
    }
  
    /**
     * Method Get Invoked when value selected in FieldList Table
     *
     * @param fieldname //Project FieldName
     */
    protected void tabSelected(String fieldname) {     
        values.add(fieldname);        
        String headings[] = {"Select", "Project Fields"};
        Object object[][] = new Object[values.size()][headings.length + 1];        
        String project_field = null;        
        for (int i = 0; i < values.size(); i++) {
           project_field = values.get(i).toString();
           object[i][0] = new Boolean(false);
           object[i][1] = project_field;           
        }

        tallymodel = new TallyFieldSelectionModel(object, headings);
        viewEditTable.setModel(tallymodel);         
        viewEditTable.getColumnModel().getColumn(0).setMaxWidth(50);
    }

    /**
     * Method to Get Selected ProjectFields
     *
     * @param batchStatus //Batch Status
     */
    public void getListingFields(String batchStatus) {       
        values = new ArrayList();
        String sql_text = null;
        if(batchStatus.equalsIgnoreCase("Listing")){
           sql_text = "listing.get fields";
        }
        
        final TaskExecuteQuery tallytask = new TaskExecuteQuery(sql_text, "" + projectId);

        tallytask.setCallback((new Runnable() {

            public void run() {
                try {

                    ResultSet results = (ResultSet) tallytask.getResult();
                    while (results.next()) {
                        String fieldName = results.getString(2);
                        if(!fieldsList.contains(fieldName)){                           
                           tabSelected(fieldName);
                        }
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(AddMoreFieldsDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }));
        tallytask.enqueue(this);
        Thread.currentThread().yield();
    }
    
    /**
     * Method Get Invoked When Proceed Button Clicked
     *
     * @param evt  java.awt.event.ActionEvent
     */
    private void proceedButtonActionPerformed(java.awt.event.ActionEvent evt) {
        proceedButton.setEnabled(true);
        ArrayList fields = new ArrayList();
        try {
            int rows = viewEditTable.getRowCount();

            boolean proceedToNextWindow = false;
            for (int i = 0; i < rows; i++) {
                String selectedValue = viewEditTable.getValueAt(i, 0).toString();

                if (null != selectedValue && selectedValue.equals("true")) {                    
                    proceedToNextWindow = true;                    
                    String fieldName = viewEditTable.getValueAt(i, 1).toString();
                    fields.add(fieldName);
                }
            }
            if (!proceedToNextWindow) {
                JOptionPane.showMessageDialog(parent,
                        "Select rows and proceed");
            } else{               
                   ((SplitPaneViewer)parent).addMoreFields(fields);
                   closeDialog(null);
            }

        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    /**
     * Method get Invoked when Exit Button Clicked
     * 
     * @param evt java.awt.event.ActionEvent
     */
    protected void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {

        try {            
            closeDialog(null);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }   
   
}
