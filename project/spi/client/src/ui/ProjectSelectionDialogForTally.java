/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import beans.ToolTipText;
import client.ClientTask;
import client.Global;
import client.TaskCheckBatchAvailable;
import client.TaskExecuteQuery;
import client.TaskOpenBatch;
import common.Log;
import common.PopulateData;
import common.msg.MessageConstants;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import model.QueryComboModel;
import model.SQLManagedComboModel;
import model.TallyFieldSelectionModel;
import org.w3c.dom.Element;
import javax.swing.JOptionPane;

/**
 *
 * @author bmurali
 */
public class ProjectSelectionDialogForTally extends javax.swing.JDialog implements MessageConstants {

    private JFrame parent;
    private String whichStatus;
    private javax.swing.JTable fieldsTable;
    private javax.swing.JTable viewEditTable;
    private javax.swing.JPanel projectPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox projectCombo;
    private javax.swing.JComboBox fieldCombo;
    private javax.swing.JPanel fieldsPane;
    private javax.swing.JScrollPane fieldsScrollPane;
    private javax.swing.JPanel bottomPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton proceedButton;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton viewButton;
    private SQLManagedComboModel projectModel = null;
    private QueryComboModel fieldModel = null;
    PopulateData data;
    private final String GET_ALL_PROJECTS = "AdvanceValidation.projectCombo";
    private final String GET_ALL_VOLUME = "listing.get volume";
    private int projectId = 0;
    private int volumeId = 0;
    private String projectName = "";
    private String volumeName = "";
    final private static int PROJECT_COLUMN = 2;
    private int selectedBatchId;
    private List fieldName;
    private List values = null;
    private TallyFieldSelectionModel tallymodel;
    private Map<String,Boolean> fieldList = null;
    private Map<String,Boolean> tagList = null;
    private Map<String,Boolean> wordList = null;
    private Map<String,List> projectFieldList = null;

    public ProjectSelectionDialogForTally(JFrame parent, String whichStatus) {

        super(parent, true);
        setTitle("Project Selection for Tally");
        this.parent = parent;
        this.whichStatus = whichStatus;
        initComponents();

        setLocationRelativeTo(parent);
        //fieldsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fieldsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fieldsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm =
                        (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                    proceedButton.setEnabled(false);
                    exitButton.setEnabled(false);
                } else {
                    int row = fieldsTable.getSelectedRow();
                    Object obj = fieldsTable.getValueAt(row, 0);
                    proceedButton.setEnabled(true);
                    exitButton.setEnabled(true);
                }
            }
        });
        if ("Tally".equals(whichStatus)) {
            tabSelected();
        }
    }

    private void initComponents() {

        projectPane = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        projectCombo = new javax.swing.JComboBox();
        fieldCombo = new javax.swing.JComboBox();
        viewEditTable = new javax.swing.JTable();

        fieldsPane = new javax.swing.JPanel();
        fieldsScrollPane = new javax.swing.JScrollPane();
        fieldsTable = new ProjectFieldsTableClass();
        bottomPane = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        proceedButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        viewButton = new javax.swing.JButton();

        projectPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel1.setText("Select Project:");
        projectPane.add(jLabel1);

        projectCombo.setPreferredSize(new java.awt.Dimension(150, 25));
        projectCombo.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectComboActionPerformed(evt);
            }
        });

        projectPane.add(projectCombo);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel2.setText("Select Project Volumes:");
        projectPane.add(jLabel2);



        fieldCombo.setEnabled(false);
        fieldCombo.setPreferredSize(new java.awt.Dimension(150, 25));
        fieldCombo.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldComboActionPerformed(evt);
            }
        });
        fieldCombo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {

            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                fieldComboPopupMenuWillBecomeVisible(evt);
            }
        });

        projectPane.add(fieldCombo);

        add(projectPane, java.awt.BorderLayout.NORTH);

        fieldsPane.setLayout(new java.awt.BorderLayout());

        fieldsPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fieldsScrollPane.setPreferredSize(new java.awt.Dimension(400, 300));
        fieldsTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));


        viewEditTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
            "Select", "Project Fields", "Type", "5/100"
        }) {

            boolean[] canEdit = new boolean[]{
                true, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        fieldsTable.setFocusable(false);
        fieldsTable.setIntercellSpacing(new java.awt.Dimension(2, 1));
        fieldsTable.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fieldsTableMouseClicked(evt);
            }
        });

        //fieldsScrollPane.setViewportView(fieldsTable);
        fieldsScrollPane.setViewportView(viewEditTable);

        fieldsPane.add(fieldsScrollPane, java.awt.BorderLayout.CENTER);

        add(fieldsPane, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 20, 0));

        proceedButton.setText("  Proceed  ");
        proceedButton.setEnabled(true);
        proceedButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                proceedButtonActionPerformed(evt);
            }
        });
        jPanel1.add(proceedButton);
        exitButton.setText("  Exit  ");
        exitButton.setEnabled(true);
        exitButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        jPanel1.add(exitButton);
        viewButton.setText("  View Group List  ");
        viewButton.setEnabled(false);
        viewButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewButtonActionPerformed(evt);
            }
        });
        jPanel1.add(viewButton);
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
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new ProjectSelectionDialogForTally(new javax.swing.JFrame(), "dummy").setVisible(true);
            }
        });
    }

    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
        parent.setVisible(true);
    }

    private class ProjectFieldsTableClass extends JTable {

        public String getToolTipText(MouseEvent event) {
            return ToolTipText.getToolTipText(event, fieldsTable);
        }

        public Point getToolTipLocation(MouseEvent event) {
            return ToolTipText.getToolTipLocation(event, fieldsTable);
        }
        }

    protected void tabSelected() {
        Log.print("Tally Button selected");
        // project model
        projectModel = new SQLManagedComboModel(GET_ALL_PROJECTS);
        projectModel.register();
        projectCombo.setModel(projectModel);
        data = new PopulateData();
    }

    private void projectComboActionPerformed(java.awt.event.ActionEvent evt) {
        //Log.print("(PopulatePage.projectComboActionPerformed) " + evt.getActionCommand());

        if (data != null) {
            projectName = data.project_name;
        }
        int sel = projectCombo.getSelectedIndex();
        if (sel > -1) {
            viewButton.setEnabled(false);
            data.project_name = (String) projectModel.getElementAt(sel);
            projectId = ((SQLManagedComboModel) projectCombo.getModel()).getIdAt(sel);
            if (fieldModel != null) {
                if (!projectName.equals(data.project_name)) { // changed here
                    fieldCombo.setSelectedIndex(-1);
                } // changed here
            }
            fieldCombo.setEnabled(true);
        } else {
            data.project_name = "";
            projectId = 0;
            fieldCombo.setEnabled(false);
        }
    }

    private void fieldComboPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
        //Log.print("(PopulatePage.volumeComboPopupMenuWillBecomeVisisble) projectId " + projectId);
        fieldModel = new QueryComboModel(GET_ALL_VOLUME, /* required? */ false, new String[]{Integer.toString(projectId)}, "");

        fieldCombo.setModel(fieldModel);
    }

    private void fieldComboActionPerformed(java.awt.event.ActionEvent evt) {
       // fillValuesInArray();
        exitButton.setEnabled(true);
        try {
            int sel = fieldCombo.getSelectedIndex();
            if (sel > -1) {
                data.field_names = (String) fieldModel.getElementAt(sel);
                // data.standard_field_validations = (String)fieldModel.getElementAt(sel);
                volumeId = fieldModel.getIdAt(sel);
               
            }
            if (sel > 0) {
                 if(Global.theServerConnection.getPermissionTeamLeader()){
                                viewButton.setEnabled(true);
                 }
                volumeId = fieldModel.getIdAt(sel);
                volumeName = (String) fieldCombo.getSelectedItem();
                proceedButton.setEnabled(true);
                 fillValuesInArray();
                final ClientTask task = new TaskCheckBatchAvailable(projectId, volumeId, whichStatus);
                task.setCallback(new Runnable() {

                    public void run() {
                        Element reply = (Element) task.getResult();
                        String action = reply.getNodeName();
                        if (T_FAIL.equals(action)) {
                            JOptionPane.showMessageDialog(ProjectSelectionDialogForTally.this,
                                    "Can't open selected batch",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {                            
                            viewButton.setEnabled(true);
                            String headings[] = {"Select", "Project Fields", "Type", "5/100"};
                            Object object[][] = new Object[values.size()][headings.length + 1];
                            
                            String project_field = null;
                            JComboBox comboBox = new JComboBox();
                            comboBox.addItem("field");
                            comboBox.addItem("word");
                            comboBox.addItem("tag");
                            for (int i = 0; i < values.size(); i++) {
                                project_field = values.get(i).toString();

                                object[i][0] = new Boolean(false);
                                object[i][1] = project_field;
                                object[i][2] = "tag";
                                object[i][3] = new Boolean(true);
                            }

                            tallymodel = new TallyFieldSelectionModel(object, headings);
                            viewEditTable.setModel(tallymodel);

                            setTallyType(viewEditTable, viewEditTable.getColumnModel().getColumn(2));

                        }
                    }
                });
                task.enqueue(this);
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    public void setTallyType(JTable table,
            TableColumn sportColumn) {
        //Set up the editor for the sport cells.
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("field");
        comboBox.addItem("word");
        comboBox.addItem("tag");
        sportColumn.setCellEditor(new DefaultCellEditor(comboBox));

        //Set up tool tips for the sport cells.
        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for combo box");
        sportColumn.setCellRenderer(renderer);
    }

    private void fillValuesInArray() {
        values = new ArrayList();        
        //Get all the fields corresponding to particular project and volume
        System.out.println("volumeId==============>"+ volumeId);
        final TaskExecuteQuery tallytask = new TaskExecuteQuery("tally.get fields", "" + projectId,""+volumeId);
        tallytask.setCallback((new Runnable() {

            public void run() {
                try {
                    ResultSet results = (ResultSet) tallytask.getResult();
                    while (results.next()) {
                        values.add(results.getString(2));
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(ProjectSelectionDialogForTally.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }));
        tallytask.enqueue(this);
        Thread.currentThread().yield();

    }

    private void proceedButtonActionPerformed(java.awt.event.ActionEvent evt) {
        proceedButton.setEnabled(true);

        try {
            projectFieldList = new HashMap<String,List>();
            fieldList = new HashMap<String,Boolean>();
            tagList = new HashMap<String,Boolean>();
            wordList = new HashMap<String,Boolean>();
            int rows = viewEditTable.getRowCount();

            boolean proceedToNextWindow = false;
            for (int i = 0; i < rows; i++) {
                String selectedValue = viewEditTable.getValueAt(i, 0).toString();

                if (null != selectedValue && selectedValue.equals("true")) {
                    fieldName = new ArrayList();
                    proceedToNextWindow = true;
                    fieldName.add(viewEditTable.getValueAt(i, 2));
                    fieldName.add(viewEditTable.getValueAt(i, 3).toString());
                    String type = viewEditTable.getValueAt(i, 2).toString();
                    if (null != type && type.equals("field")) {
                        fieldList.put(viewEditTable.getValueAt(i, 1).toString(),new Boolean(viewEditTable.getValueAt(i, 3).toString()));
                    } else if (null != type && type.equals("tag")) {
                        tagList.put(viewEditTable.getValueAt(i, 1).toString(),new Boolean(viewEditTable.getValueAt(i, 3).toString()));
                    } else if (null != type && type.equals("word")) {
                        wordList.put(viewEditTable.getValueAt(i, 1).toString(),new Boolean(viewEditTable.getValueAt(i, 3).toString()));
                    }
                    projectFieldList.put(viewEditTable.getValueAt(i, 1).toString(), fieldName);
                }

            }

            if (!proceedToNextWindow) {
                JOptionPane.showMessageDialog(parent,
                        "Select rows and proceed");
            } else {
                final ClientTask task = new TaskOpenBatch(projectId, whichStatus, volumeId);
                final ProjectSelectionDialogForTally dialogForTally = this;
                task.setCallback(new Runnable() {

                    public void run() {
                        Element reply = (Element) task.getResult();
                        String action = reply.getNodeName();
                        ConfirmTallyFields fieldsViewer = new ConfirmTallyFields(fieldList, tagList, wordList,projectFieldList,projectId,volumeId
                               ,whichStatus);

                        //SplitPaneViewer viewer = SplitPaneViewer.getInstance();
                        if (T_BATCH_OPENED.equals(action)) {
                            // get data for opened batch// TBD - should probably continue at ClientTask level
                            int batchId = Integer.parseInt(reply.getAttribute(A_BATCH_ID));
                            int projectId = Integer.parseInt(reply.getAttribute(A_PROJECT_ID));
                            // int activeGroup = Integer.parseInt(reply.getAttribute(A_GROUP));
                            String project = reply.getAttribute(A_PROJECT_NAME);
                            String splitDocuments = reply.getAttribute(A_SPLIT_DOCUMENTS);
                            // Log.print("ready to open b="+batchId+" p="+project);
                            fieldsViewer.setParent(dialogForTally);
                            fieldsViewer.setProjectSelectionDialog(parent);
                            
                            fieldsViewer.setVisible(true);
                            Global.mainWindow = fieldsViewer;
                            // close the dialog without showing the parent
                            // TBD: this is strange coding - worry about restructuring it
                            setVisible(false);
                            dispose();
                        } else if (T_FAIL.equals(action)) {
                            // TBD: How do we tell them of problem opening batch
                            // This gives them a message box, doesn't open viewer
                            JOptionPane.showMessageDialog(ProjectSelectionDialogForTally.this,
                                    "Can't open selected batch",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        } else {
                            Log.quit("ProjectSelectionDialogForTally:" + " unexpected message type: " + action);
                        }
                    }
                });
                task.enqueue(this);
            }

        } catch (Throwable th) {
            Log.quit(th);
        }
    }


    protected void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {

        try {
            // Add your handling code here:
            Log.print("ProjectSelectionDialogForTally: Cancel pressed");
            closeDialog(null);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }
    
    private void viewButtonActionPerformed(java.awt.event.ActionEvent evt){       
       this.setVisible(false);
       ViewTallyGroupDialog viewDialog = new ViewTallyGroupDialog(this,projectCombo.getSelectedItem().toString(),volumeName);
       viewDialog.setVisible(true);
       viewDialog.setLocationRelativeTo(null);
    }

    private void fieldsTableMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            if (evt.getClickCount() > 1) {
                // double-click on a row
                //proceedButton.doClick();
                proceedButton.setEnabled(true);
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }
    
    public void clearComponents(){       
       projectCombo.setSelectedIndex(-1);
       fieldCombo.setSelectedIndex(-1);
       viewEditTable.clearSelection();
       proceedButton.setEnabled(false);
       viewButton.setEnabled(false);
    }
}
