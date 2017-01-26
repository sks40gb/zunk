/*
 * AdvanceValidationPage.java
 *
 * Created on January 1, 2008, 11:26 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package ui;

import beans.AddEditProjectFieldValidation;
import beans.ToolTipText;
import client.ClientTask;
import client.TaskDeleteAdvanceValidations;
import client.TaskViewAdvanceValidations;
import common.Log;
import common.PopulateData;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import model.QueryComboModel;
import model.ResultSetTableModel;
import model.SQLManagedComboModel;

/**
 *
 * @author murali
 */
public class AdvanceValidationPage extends ui.AbstractPage {

    private final String BLANK = "";
    private final String FIELD = "Field";
    private final String DOCUMENT = "Document";
    private final String VOLUME = "Volume";
    public static javax.swing.JTable fieldsTable;
    private javax.swing.JButton editButton;
    private javax.swing.JButton addButton;
    private javax.swing.JButton allValidationButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JPanel projectPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox projectCombo;
    private javax.swing.JComboBox fieldCombo;
    private javax.swing.JComboBox scopeCombo;
    private javax.swing.JComboBox volumeCombo;
    private javax.swing.JPanel fieldsPane;
    private javax.swing.JScrollPane fieldsScrollPane;
    private javax.swing.JPanel bottomPane;
    private javax.swing.JPanel jPanel1;
    private final String GET_ALL_PROJECTS = "AdvanceValidation.projectCombo";
    private final String GET_ALL_FIELDS = "AdvanceValidation.get fields";
    private final String GET_ALL_VOLUME = "listing.get volume";
    private SQLManagedComboModel projectModel = null;
    private QueryComboModel fieldModel = null;
    private PopulateData data;
    private int projectId = 0;
    private int fieldId = 0;
    private String scope;
    private String projectName = "";
    private String fieldName = "";
    private int std_group_id = -1;
    private String std_group_name = "";
    private java.sql.ResultSet results = null;
    private ResultSetTableModel model = null;
    private ArrayList<String> functionNameList = new ArrayList();
    private QueryComboModel volumeModel;



    /** Creates a new instance of AdvanceValidationPage */
    public AdvanceValidationPage(AdminFrame frame) {
        super(frame);
        this.frame = frame;

        initComponents();

        fieldsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fieldsTable.setDragEnabled(false);        
        fieldsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        //fieldsTable.setAutoCreateRowSorter(true);
        
        fieldsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.

                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm =
                        (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                    editButton.setEnabled(false);
                    deleteButton.setEnabled(false);

                } else {
                    int row = fieldsTable.getSelectedRow();
                    editButton.setEnabled(true);
                    deleteButton.setEnabled(true);
                }
            }
        });
    }

    protected boolean exitPageCheck() {
        // TBD
        return true;
    }

    /** Get the menu bar for the current page.  Subclasses must override this to provide a
     * page-dependent menu bar.
     */
    protected javax.swing.JMenuBar getPageJMenuBar() {
        return menuBar;
    }

    protected void tabSelected() {
        if (data == null) {
            Log.print("Advance Validation tabSelected");
            QueryComboModel combo = new QueryComboModel(GET_ALL_PROJECTS);
            projectCombo.setModel(combo);
            data = new PopulateData();
        }
    }
    
 
    private class ProjectFieldsTableClass extends JTable {

        public String getToolTipText(MouseEvent event) {
            return ToolTipText.getToolTipText(event, fieldsTable);
        }

        public Point getToolTipLocation(MouseEvent event) {
            return ToolTipText.getToolTipLocation(event, fieldsTable);
        }
    }

    private void initComponents() {
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        projectPane = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        projectCombo = new javax.swing.JComboBox();
        fieldCombo = new javax.swing.JComboBox();
       // scopeCombo = new javax.swing.JComboBox(new String[]{BLANK ,FIELD, DOCUMENT, VOLUME});
        scopeCombo = new javax.swing.JComboBox(new String[]{BLANK ,FIELD, DOCUMENT});
        volumeCombo = new javax.swing.JComboBox();
        fieldsPane = new javax.swing.JPanel();
        fieldsScrollPane = new javax.swing.JScrollPane();
        fieldsTable = new ProjectFieldsTableClass();
        bottomPane = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        editButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        allValidationButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        fileMenu.setMnemonic('F');
        fileMenu.setText("File");
        exitMenuItem.setMnemonic('E');
        exitMenuItem.setText("Exit");
        exitMenuItem.setToolTipText("Exit program.");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);
        setLayout(new java.awt.BorderLayout());
        projectPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel1.setText("Project:");
        projectPane.add(jLabel1);
        projectCombo.setPreferredSize(new java.awt.Dimension(150, 25));
        projectCombo.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectComboActionPerformed(evt);
            }
        });
        projectCombo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {

            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                projectComboPopupMenuWillBecomeVisible(evt);
            }
        });

        projectPane.add(projectCombo);

         //START==========================================================================================

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel3.setText("Validation Scope :");
        projectPane.add(jLabel3);
        scopeCombo.setPreferredSize(new java.awt.Dimension(150, 25));
        scopeCombo.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearAll();
                scope = scopeCombo.getSelectedItem().toString();
                getRecordAsLevel();
                enableAddButton();
            }
        });
        scopeCombo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {

            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                //fieldComboPopupMenuWillBecomeVisible(evt);
            }
        });

        projectPane.add(scopeCombo);


        //for volume
        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel4.setText("Volume :");
       // projectPane.add(jLabel4);
        volumeCombo.setPreferredSize(new java.awt.Dimension(150, 25));
        volumeCombo.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {

                //createDynamiControl(volumeCombo.getSelectedItem().toString());
            }
        });
        volumeCombo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {

            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                //fieldComboPopupMenuWillBecomeVisible(evt);
            }
        });

        //projectPane.add(volumeCombo);


        //END========================================================================================




        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel2.setText("Project Field:");
        projectPane.add(jLabel2);
        jLabel2.setVisible(false);
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
        fieldCombo.setVisible(false);
        
        add(projectPane, java.awt.BorderLayout.NORTH);
        fieldsPane.setLayout(new java.awt.BorderLayout());
        fieldsPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fieldsScrollPane.setPreferredSize(new java.awt.Dimension(753, 503));
        fieldsTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fieldsTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Function Name", "Description", "Type", "Error Message", "Enabled", "Parameter"
                }) {

            boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false
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
        
                
    JTableHeader header = fieldsTable.getTableHeader();
        header.setUpdateTableInRealTime(true);
        header.addMouseListener(new MouseAdapter() {

            public void mouseClicked(final MouseEvent mouseEvent) {
                final TableColumnModel columnModel;
                final int xClick;
                final int xView;            

                columnModel = fieldsTable.getColumnModel();
                xClick = mouseEvent.getX(); // horizontal pixel
                xView = columnModel.getColumnIndexAtX(xClick); // view index                
                System.out.println("===============> " + xView);
            }
        });
        header.setReorderingAllowed(true);

        fieldsScrollPane.setViewportView(fieldsTable);

        fieldsPane.add(fieldsScrollPane, java.awt.BorderLayout.CENTER);

        add(fieldsPane, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 20, 0));

        editButton.setText("  Edit  ");
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        jPanel1.add(editButton);

        addButton.setText("  Add  ");
        addButton.setEnabled(false);
        addButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });
        jPanel1.add(addButton);

        allValidationButton.setText("All Validations");
        allValidationButton.setEnabled(false);
        allValidationButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allValidationButtonActionPerformed(evt);
            }
        });
        jPanel1.add(allValidationButton);

        deleteButton.setText("  Delete  ");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        jPanel1.add(deleteButton);
        bottomPane.add(jPanel1);
        add(bottomPane, java.awt.BorderLayout.SOUTH);
    }


    /**
     * Create the dynamic Label and ComboBox for the Project Field or Volume.
     * It depends upon the <code>level</code> of the function.
     * @param level Level of the function.
     */
    private void getRecordAsLevel() {
        if(scope.equals(DOCUMENT) || scope.equals(VOLUME)){            
            refreshPage();
        }
        createDynamicControls();
    }


    private void createDynamicControls() {
        if (scope.equals(FIELD)) {

            jLabel2.setVisible(true);
            fieldCombo.setVisible(true);
            repaint();

        } else {

            jLabel2.setVisible(false);
            fieldCombo.setVisible(false);

            repaint();
        }
    }


    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            exitForm();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void projectComboActionPerformed(java.awt.event.ActionEvent evt) {
        if (data != null) {
            projectName = data.project_name;
        }
        int sel = projectCombo.getSelectedIndex();
        if (sel > -1) {
            addButton.setEnabled(false);
            allValidationButton.setEnabled(false);
            data.project_name = (String) projectCombo.getSelectedItem();
            projectId = ((QueryComboModel) projectCombo.getModel()).getIdAt(sel);
            projectFields();
            projectVolumes();           
        } else {
            data.project_name = "";
            projectId = 0;            
        }
        scopeCombo.setSelectedItem(BLANK);
    }

     private void projectComboPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt){
       tabSelected();
     }
     
    private void projectFields() {
        fieldModel = new QueryComboModel(GET_ALL_FIELDS, /* required? */ false, new String[]{Integer.toString(projectId)}, "");
        fieldCombo.setModel(fieldModel);
    }

    private void projectVolumes() {
    volumeModel = new QueryComboModel(GET_ALL_VOLUME, /* required? */ false, new String[]{Integer.toString(projectId)}, "");
        volumeCombo.setModel(volumeModel);
    }

    /*
     * get all the fields when field combo box id opened
     */
    private void fieldComboPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {                  
    
    }

    /*
     * get all the records of selected fields
     */
    private boolean isRemoved = false;
    int countOnProjectClick = 0;

    private void fieldComboActionPerformed(java.awt.event.ActionEvent evt) {

        try {

            if (fieldCombo.getItemAt(0) != null && (((String) fieldCombo.getItemAt(0)).equals(""))) {
                countOnProjectClick = 0;
                fieldCombo.removeItem("");
            }

            if (countOnProjectClick < 2) {
                countOnProjectClick++;
                return;
            }

            int sel = fieldCombo.getSelectedIndex();
            sel++;

            if (sel > -1) {
                data.field_names = (String) fieldModel.getElementAt(sel);
                fieldId = fieldModel.getIdAt(sel);
            }

            if (fieldId > 0) {
                //-----------------------------------------------------   
                java.sql.ResultSet rs = fieldModel.resultSet;
                rs.absolute(sel);
                if (rs.getString(11) != null) {
                    std_group_id = Integer.parseInt(rs.getString(11));
                    std_group_name = rs.getString(12);
                } else {
                    std_group_id = -1;
                    std_group_name = "";
                }
                //------------------------------------------------------

                fieldName = (String) fieldCombo.getSelectedItem();
                addButton.setEnabled(true);
                allValidationButton.setEnabled(true);
                final ClientTask task;
                task = new TaskViewAdvanceValidations(-1, fieldId, FIELD);
                task.setCallback(new Runnable() {

                    public void run() {
                        results = (java.sql.ResultSet) task.getResult();
                        if (results != null) {
                            displayData();

                        } else {
                            Log.print("??? Fieldvalues: null resultset returned");
                        }
                    }
                });
                boolean ok = task.enqueue(this);
            } else {

                clearAll();
                if (fieldId < 0) {
                    addButton.setEnabled(false);
                    allValidationButton.setEnabled(false);
                    return;
                }
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void clearAll() {
        fieldsTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Function Name", "Description", "Type", "Error Message", "Enabled", "Parameter"
                }) {

            boolean[] canEdit = new boolean[]{
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
    }

    /*
     *refresh the advance validation page after adding new function.
     */
    public void refreshPage() {
        try {

            if (fieldId > 0 || !(scope.equals(FIELD))) {

                final ClientTask task;
                task = new TaskViewAdvanceValidations(projectId, fieldId, scope);
                task.setCallback(new Runnable() {
                    public void run() {
                        results = (java.sql.ResultSet) task.getResult();
                        if (results != null) {                 
                            displayData();

                        } else {
                            Log.print("??? Fieldvalues: null resultset returned");
                        }
                    }
                });
                boolean ok = task.enqueue(this);


            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    public void enableAddButton(){
        if(scope.equals(DOCUMENT) || scope.equals(VOLUME)){
            addButton.setEnabled(true);
        }else{
            addButton.setEnabled(false);
        }

    }
    /*
     * display the records of the selected fields
     */
    private void displayData() {
        model = new ResultSetTableModel(results, new String[]{
                    "Function Name", "Description", "Type", "Error Message", "Enabled", "Parameter"
                });
        fieldsTable.setModel(model);
        // model.register();   
        TableColumn column = null;
        column = fieldsTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(110); // functionName

        column = fieldsTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(58); // description

        column = fieldsTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(40); // type

        column = fieldsTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(70); // error message        
        
        column = fieldsTable.getColumnModel().getColumn(4);
        column.setPreferredWidth(15); // status
        
        column = fieldsTable.getColumnModel().getColumn(5);
        column.setPreferredWidth(20); //parameter
        
        addAllfunctionsNameToList();
    }

    private void fieldsTableMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            if (evt.getClickCount() > 1) {
                // double-click on a row
                editButton.doClick();
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    /*
     * get all functions name belongs to selected fields
     */
    public void addAllfunctionsNameToList() {
        //get total no. of rows
        functionNameList = new ArrayList<String>();
        int size = fieldsTable.getRowCount();
        for (int i = 0; i < size; i++) {
            functionNameList.add((String) ((ResultSetTableModel) fieldsTable.getModel()).getValueAt(i, 0));
        }
    }

    /*
     *it is called when Add button is clicked 
     */
    private void addButtonActionPerformed(ActionEvent evt) {
        try {
            if (fieldId < 0 && !(scope.equals(DOCUMENT) || scope.equals(VOLUME))) {
                addButton.setEnabled(false);
                return;
            }
            String std_field_name = null;
            int rowSize = fieldsTable.getRowCount();
            for (int i = 0; i < rowSize; i++) {
                if (((ResultSetTableModel) fieldsTable.getModel()).getValueAt(i, 6) != null) {
                    std_field_name = (String) ((ResultSetTableModel) fieldsTable.getModel()).getValueAt(i, 6);
                    break;
                }
            }
            AddEditProjectFieldValidation addValidationDialog = new AddEditProjectFieldValidation(this, fieldId, projectId, std_field_name, std_group_id, std_group_name, functionNameList, scope);
            addValidationDialog.setModal(true);
            addValidationDialog.show();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void allValidationButtonActionPerformed(ActionEvent evt) {
        AdvanceValidationAddFromAll addAll = new AdvanceValidationAddFromAll(this);
        addAll.setVisible(true);
    }

    /*
     *it is called when Edit button is clicked 
     */
    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {    
            int i = fieldsTable.getSelectedRow();

            String validation_mapping_details_id = (String) ((ResultSetTableModel) fieldsTable.getModel()).getValueAt(i, 6);
            String validation_functions_master_id = (String) ((ResultSetTableModel) fieldsTable.getModel()).getValueAt(i, 7);
            //String std_function_group_name = (String) ((ResultSetTableModel) fieldsTable.getModel()).getValueAt(i, 7);
            String std_function_group_id = (String) ((ResultSetTableModel) fieldsTable.getModel()).getValueAt(i, 9);

            if (std_function_group_id == null) {
                std_function_group_id = "-1";
            }

            if (validation_mapping_details_id == null) {
                validation_mapping_details_id = "-1";
            }

            AddEditProjectFieldValidation validationDialog = new AddEditProjectFieldValidation(this, fieldId, Integer.parseInt(validation_mapping_details_id),
                    Integer.parseInt(validation_functions_master_id),
                    Integer.parseInt(std_function_group_id), std_group_id, std_group_name, scope);
            validationDialog.setModal(true);
            validationDialog.show();

        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    /*
     *it is called when Delete button is clicked 
     */
    private void deleteButtonActionPerformed(ActionEvent evt) {
        try {

            int i = fieldsTable.getSelectedRow();
            String function_name = (String) ((ResultSetTableModel) fieldsTable.getModel()).getValueAt(i, 0);
            String validation_mapping_details_id = (String) ((ResultSetTableModel) fieldsTable.getModel()).getValueAt(i, 6);
            String validation_functions_master_id = (String) ((ResultSetTableModel) fieldsTable.getModel()).getValueAt(i, 7);
            //String std_function_group_name = (String) ((ResultSetTableModel) fieldsTable.getModel()).getValueAt(i, 7);
            String std_function_group_id = (String) ((ResultSetTableModel) fieldsTable.getModel()).getValueAt(i, 9);
           
            //if the function is field type and it is generic then do not delete the function.
            if (scope.equals(FIELD) && std_function_group_id != null) {
                JOptionPane.showMessageDialog(this, "Generic Function cannot be deleted");
                return;
            }

            String promptMessage = "Do you want to delete the function " + function_name;
            int confirm = JOptionPane.showConfirmDialog(this, promptMessage, "Confirm method delete", 0);            

            //if user Clicks Ok button.
            if (confirm == 0) {

                final ClientTask task;
                task = new TaskDeleteAdvanceValidations(fieldId, Integer.parseInt(validation_mapping_details_id), Integer.parseInt(validation_functions_master_id));
                task.setCallback(new Runnable() {

                    public void run() {
                        results = (java.sql.ResultSet) task.getResult();
                    }
                });
                boolean ok = task.enqueue(this);
                //this is for refreshing the page
                refreshPage();
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    public int getProjectId() {
        return projectId;
    }

    public int getFieldId() {
        return fieldId;
    }

    public List<String> getFunctionsNameList() {
        return functionNameList;
    }
    }

