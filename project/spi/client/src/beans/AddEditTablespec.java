/* $Header: /home/common/cvsarea/ibase/dia/src/beans/AddEditTablespec.java,v 1.27.6.4 2006/03/22 20:27:14 nancy Exp $ */
/*
 * AddEditTablespec.java
 *
 * Created on April 7, 2004, 6:54 AM
 */
package beans;

import client.ClientTask;
import client.TaskExecuteQuery;
import client.TaskSendTablespec;
import client.TaskSendTablevalue;
import common.Log;
import common.TablespecData;
import common.TablevalueData;

import model.QueryComboModel;
import model.ManagedTableModel;
import model.ManagedTableSorter;
import model.SQLManagedTableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Called from ui.TableAdminPage, this dialog is used in two ways:
 * 1. Allows the user to add or edit tablespec data or
 * 2. Allows the user to add or edit tablevalue data.
 * <p>
 * 1.  When the user clicks 'Add Table' or 'Edit Table' on ui.TableAdminPage,
 * the top portion of this dialog is shown with fields editable.  The user fills
 * in the tablespec fields and the data is sent to the server via the container
 * common.TablespecData.
 * <p>
 * 2. When the user clicks 'Edit Values' on ui.TableAdminPage, the top portion of
 * this dialog is shown with tablespec values in protected fields.  The valuePanel is
 * also visible and unprotected.  The user may then Add, Edit or Delete tablevalue
 * data. common.TablevalueData is used to transport the request and data to the
 * server.
 * 
 * @author  Nancy
 * 
 * @see beans.AddEditTextFieldDialog
 * @see common.TablespecData
 * @see common.TablevalueData
 * @see ui.ProjectAdminPage
 * @see client.TaskSendTablespec
 * @see client.TaskSendTablevalue
 * @see server.Handler_tablespec
 * @see server.Handler_tablevalue
 * @see server.Handler_update_values
 */
public class AddEditTablespec extends javax.swing.JDialog {

    private int tablespecId;
    private int projectId = 0;
    private TablespecData tablespecData;
    private boolean edit = true;
    private String action;
    private String modelName = "";
    private PlainDocument checkTextEnabled;

    /**
     * Creates new form AddEditTablespec.
     * @param parent the component to use in positioning this dialog
     * @param tablespecId the tablespec_id of the table to be updated
     */
    public AddEditTablespec(Component parent, int tablespecId) {
        this(parent, "", tablespecId);
    }

    /**
     * Creates new form AddEditTablespec.
     * @param parent the component to use in positioning this dialog
     * @param action null string indicates edit of tablevalues;
     * otherwise, edit of tablespec
     * @param tablespecId the tablespec_id of the table to be updated
     */
    public AddEditTablespec(Component parent, String action, int tablespecId) {
        super(JOptionPane.getFrameForComponent(parent));
        this.tablespecId = tablespecId;
        this.action = action;
        initComponents();

        if (tablespecId > 0) {
            // This is an edit, so load the data.
            loadTablespecData(action);
            if (action.equals("")) {
                // edit the table values
                addButton.setEnabled(true);
                tableName.setEnabled(false);
                tableName.setForeground(Color.black);
                typeCombo.setEnabled(false);
                typeCombo.setForeground(Color.black);
                projectCombo.setEnabled(false);
                projectCombo.setForeground(Color.black);
                requirementCheckBox.setEnabled(false);
                requirementCheckBox.setForeground(Color.black);
                updateableCheckBox.setEnabled(false);
                updateableCheckBox.setForeground(Color.black);
                okButton.setVisible(false);
                cancelButton.setVisible(false);
            } else {
                // edit the tablespec values
                addButton.setEnabled(false);
                valuePanel.setVisible(false);
                updateLabel.setVisible(false);
//                tableName.setEnabled(false);
//                tableName.setForeground(Color.black);
//                typeCombo.setEnabled(false);
//                typeCombo.setForeground(Color.black);
                projectCombo.setEnabled(true);
                requirementCheckBox.setEnabled(true);
                updateableCheckBox.setEnabled(true);
            }
        } else {
            // add
            edit = false;
            addButton.setEnabled(false);
            valuePanel.setVisible(false);
            updateLabel.setVisible(false);

            // Remove addPanel and valuePanel to make the dialog the right size for an Add.
            this.remove(addPanel);
            this.remove(valuePanel);
            //valuePanel.setPreferredSize(new Dimension(463, 62));

            typeCombo.setSelectedIndex(-1);
            projectCombo.setSelectedIndex(-1);
        }
        pack();
        getRootPane().setDefaultButton(okButton);

        // set listeners for ok button enable/disable
        checkTextEnabled = new PlainDocument() {

            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (str == null) {
                    return;
                }
                super.insertString(offs, str, a);
                checkOkEnabled();
            }

            public void remove(int offs, int len)
                    throws BadLocationException {
                super.remove(offs, len);
                checkOkEnabled();
            }
        };
        tableName.setDocument(checkTextEnabled);
        ButtonAction buttonAction = new ButtonAction();
        typeCombo.addActionListener(buttonAction);
        projectCombo.addActionListener(buttonAction);
        projectCombo.addFocusListener(new FocusCombo());
    }

    class ButtonAction implements java.awt.event.ActionListener {

        public void actionPerformed(java.awt.event.ActionEvent event) {
            checkOkEnabled();
        }
    }

    /**
     * Enable the ok button
     */
    private void checkOkEnabled() {
        // If the table name is not blank and type and project is selected then 
        // enable the ok button.
        if (!tableName.getText().trim().equals("") //changed here
                && typeCombo.getSelectedIndex() > -1 && projectCombo.getSelectedIndex() > -1) {
            okButton.setEnabled(true);
        } else {
            okButton.setEnabled(false);
        }
    }

    private class FocusCombo implements FocusListener {

        public void focusGained(FocusEvent e) {
            try {
                // Set the first entry of projectCombo to "All Projects" rather than blank.
                // (Do it here to make sure the combo model has time to load data.)
                if (projectCombo.getItemCount() < 1) {
                    return;
                }
                if (((String) ((QueryComboModel) projectCombo.getModel()).getElementAt(0)).equals("")) {
                    int index = projectCombo.getSelectedIndex();
                    ((QueryComboModel) projectCombo.getModel()).removeElementAt(0);
                    ((QueryComboModel) projectCombo.getModel()).insertElementAt("<All Projects>", 0);
                    projectCombo.setSelectedIndex(index);
                }
            } catch (Throwable t) {
                Log.quit(t);
            }
        }

        public void focusLost(FocusEvent e) {
            try {
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        tableNameLabel = new javax.swing.JLabel();
        tableName = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        typeCombo = new javax.swing.JComboBox();
        jSeparator2 = new javax.swing.JSeparator();
        requirementCheckBox = new javax.swing.JCheckBox();
        updateableCheckBox = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        projectCombo = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        modelCombo = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        addPanel = new javax.swing.JPanel();
        updateLabel = new javax.swing.JLabel();
        valuePanel = new javax.swing.JPanel();
        valueScrollPane = new javax.swing.JScrollPane();
        valueTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton();
        getContentPane().setLayout(new java.awt.BorderLayout(50, 10));

        setTitle("Lookup Tables");
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 153)), "Lookup Table", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11), new java.awt.Color(0, 0, 153)));
        tableNameLabel.setFont(new java.awt.Font("Dialog", 1, 11));
        tableNameLabel.setText("Name: ");
        tableNameLabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(tableNameLabel, gridBagConstraints);

        tableName.setColumns(40);
        jPanel1.add(tableName, new java.awt.GridBagConstraints());

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel1.setText("Type: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel1, gridBagConstraints);

        jPanel3.setAlignmentX(0.0F);
        jPanel3.setAlignmentY(0.0F);
        typeCombo.setFont(new java.awt.Font("Dialog", 1, 11));
        typeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "name", "text" }));
        jPanel3.add(typeCombo);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator2.setPreferredSize(new java.awt.Dimension(40, 0));
        jPanel3.add(jSeparator2);

        requirementCheckBox.setFont(new java.awt.Font("Dialog", 1, 11));
        requirementCheckBox.setText(" Required");
        jPanel3.add(requirementCheckBox);

        updateableCheckBox.setFont(new java.awt.Font("Dialog", 1, 11));
        updateableCheckBox.setText("Coder Updateable");
        jPanel3.add(updateableCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jPanel3, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 11));
        jLabel2.setText("Project: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jLabel2, gridBagConstraints);

        projectCombo.setFont(new java.awt.Font("Dialog", 1, 11));
        projectCombo.setModel(
            new QueryComboModel("get project names"
                , /* required-> */ false));
            projectCombo.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    projectComboActionPerformed(evt);
                }
            });

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridy = 2;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            jPanel1.add(projectCombo, gridBagConstraints);

            jLabel3.setFont(new java.awt.Font("Dialog", 1, 11));
            jLabel3.setText("Model Table: ");
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridy = 3;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            jPanel1.add(jLabel3, gridBagConstraints);

            modelCombo.setFont(new java.awt.Font("Dialog", 1, 11));
            modelCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "<None>" }));
            modelCombo.setPreferredSize(new java.awt.Dimension(200, 25));
            modelCombo.setEnabled(false);
            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridy = 3;
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            jPanel1.add(modelCombo, gridBagConstraints);

            okButton.setText("OK");
            okButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    okButtonActionPerformed(evt);
                }
            });

            jPanel4.add(okButton);

            cancelButton.setText("Cancel");
            cancelButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cancelButtonActionPerformed(evt);
                }
            });

            jPanel4.add(cancelButton);

            gridBagConstraints = new java.awt.GridBagConstraints();
            gridBagConstraints.gridy = 4;
            gridBagConstraints.gridwidth = 2;
            gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
            jPanel1.add(jPanel4, gridBagConstraints);

            getContentPane().add(jPanel1, java.awt.BorderLayout.NORTH);

            updateLabel.setForeground(new java.awt.Color(204, 0, 51));
            updateLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
            addPanel.add(updateLabel);

            getContentPane().add(addPanel, java.awt.BorderLayout.EAST);

            valuePanel.setLayout(new java.awt.BorderLayout());

            valuePanel.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 153)), "Table Values", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11), new java.awt.Color(0, 0, 153)));
            valueTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
            valueTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {

                },
                new String [] {
                    "Model Value", "Value", "Level"
                }
            ) {
                boolean[] canEdit = new boolean [] {
                    false, false, false
                };

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit [columnIndex];
                }
            });
            valueTable.setFocusable(false);
            valueTable.setIntercellSpacing(new java.awt.Dimension(2, 1));
            valueTable.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    valueTableMouseClicked(evt);
                }
            });

            valueScrollPane.setViewportView(valueTable);

            valuePanel.add(valueScrollPane, java.awt.BorderLayout.CENTER);

            addButton.setFont(new java.awt.Font("Dialog", 1, 11));
            addButton.setForeground(new java.awt.Color(0, 0, 153));
            addButton.setMnemonic('A');
            addButton.setText("Add");
            addButton.setToolTipText("Add a new value to the table.");
            addButton.setFocusable(false);
            addButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    addButtonActionPerformed(evt);
                }
            });

            jPanel2.add(addButton);

            editButton.setFont(new java.awt.Font("Dialog", 1, 11));
            editButton.setForeground(new java.awt.Color(0, 0, 153));
            editButton.setMnemonic('E');
            editButton.setText("Edit");
            editButton.setToolTipText("Change the selected value and, optionally, all such values in the database.");
            editButton.setFocusable(false);
            editButton.setEnabled(false);
            editButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    editButtonActionPerformed(evt);
                }
            });

            jPanel2.add(editButton);

            deleteButton.setFont(new java.awt.Font("Dialog", 1, 11));
            deleteButton.setForeground(new java.awt.Color(0, 0, 153));
            deleteButton.setMnemonic('D');
            deleteButton.setText("Delete");
            deleteButton.setToolTipText("Delete the entered or selected value from the table and, optionally, the database.");
            deleteButton.setFocusable(false);
            deleteButton.setEnabled(false);
            deleteButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    deleteButtonActionPerformed(evt);
                }
            });

            jPanel2.add(deleteButton);
            
            closeButton.setFont(new java.awt.Font("Dialog", 1, 11));
            closeButton.setForeground(new java.awt.Color(0, 0, 153));
            closeButton.setMnemonic('C');
            closeButton.setText("Close");
            closeButton.setToolTipText("Close the window.");
            closeButton.setFocusable(false);
            closeButton.setEnabled(true);
            closeButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    closeButtonActionPerformed(evt);
                }
            });

            jPanel2.add(closeButton);
            //ends here

            valuePanel.add(jPanel2, java.awt.BorderLayout.SOUTH);

            getContentPane().add(valuePanel, java.awt.BorderLayout.SOUTH);

            pack();
        }//GEN-END:initComponents

    /**
     * Click the Edit button if the user double-clicked a row.
     */
    private void valueTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_valueTableMouseClicked
        //Log.print("(AddEditTablespec.valueTableMouse) clicked");
        try {
            if (evt.getClickCount() > 1) {
                // double-click on a row
                editButton.doClick();
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }//GEN-LAST:event_valueTableMouseClicked

    /**
     * On clicking the cancel button make this dialog invisible.
     * @param evt On clicking cancel button.
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        tablespecData = new TablespecData();
        tablespecData.tablespec_id = tablespecId;
        tablespecData.table_name = tableName.getText();
        tablespecData.table_type = (String) typeCombo.getSelectedItem();
        tablespecData.project_id = ((QueryComboModel) projectCombo.getModel()).getSelectedId() < 0 ? 0
                : ((QueryComboModel) projectCombo.getModel()).getSelectedId();
        tablespecData.requirement = requirementCheckBox.isSelected() ? "Required" : "Optional";
        tablespecData.updateable = updateableCheckBox.isSelected() ? "CoderAdd" : "SuperMod";
        tablespecData.model_tablespec_id = ((QueryComboModel) modelCombo.getModel()).getSelectedId();

        final ClientTask task =
                new TaskSendTablespec(tablespecData);
        task.setCallback(new Runnable() {

            public void run() {
                String task_ok = (String) task.getResult();
                //Log.print("AddEditTablespec: okButton " + task_ok);
                if (task_ok.equals(common.msg.MessageConstants.T_OK)) {
                    if (edit) {
                        updateLabel.setText("Update Successful");
                    } else {
                        updateLabel.setText("Add Successful");
                    }
                } else {
                    JOptionPane.showMessageDialog(null,
                            task_ok, "Duplicate Entry",
                            JOptionPane.ERROR_MESSAGE);
                    // ends here
                    Log.print("??? AddEditTablespec delete: not ok");
                }
            }
        });
        task.enqueue(this);
        setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * Delete tablespec data.
     * @param evt
     */
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        try {
            int i = valueTable.getSelectedRow();
            if (i > -1) {
                //Log.print("(ABT).deleteButton row i = " + i + "/" + originalText);
                TablevalueData data = new TablevalueData();
                data.tablevalue_id = 0; // signals delete

                data.tablespec_id = tablespecId;
                data.value = "";
                data.level = -1;
                data.old_value = (String) valueTable.getModel().getValueAt(i, 1);
                data.model_value = "";
                final ClientTask task;
                task = new TaskSendTablevalue(data);
                task.enqueue(this);
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        try {
            ArrayList valuesList = new ArrayList();
            for(int j =0;j < valueTable.getRowCount();j++){
                valuesList.add(valueTable.getValueAt(j, 1));
            }
            AddEditTextFieldDialog dialog =
                    new AddEditTextFieldDialog(projectCombo, (String) typeCombo.getSelectedItem(), "", /* level -> */ 0, tablespecId, "add", ((QueryComboModel) modelCombo.getModel()).getSelectedId(), "",valuesList,requirementCheckBox.isSelected(),updateableCheckBox.isSelected());
            dialog.setModal(true);
            dialog.show();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }//GEN-LAST:event_addButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        try {            
            ArrayList valuesList = new ArrayList();
            for(int j =0;j < valueTable.getRowCount();j++){
                valuesList.add(valueTable.getValueAt(j, 1));
            }
            int i = valueTable.getSelectedRow();
            if (i > -1) {
                String originalText = (String) valueTable.getModel().getValueAt(i, 1);
                int originalLevel = Integer.parseInt((String) valueTable.getModel().getValueAt(i, 2));
                AddEditTextFieldDialog dialog =
                        new AddEditTextFieldDialog(projectCombo, (String) typeCombo.getSelectedItem(), originalText, originalLevel, tablespecId, "edit", ((QueryComboModel) modelCombo.getModel()).getSelectedId(), (String) valueTable.getModel().getValueAt(i, 0),valuesList,requirementCheckBox.isSelected(),updateableCheckBox.isSelected());
                dialog.setModal(true);
                dialog.show();
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }//GEN-LAST:event_editButtonActionPerformed

    /** Closes the dialog. */
    private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setVisible(false);
        dispose();
    }
    // ends here

    /**
     * Enable modelCombo after a project has been selected.
     */
    private void projectComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectComboActionPerformed
        projectId = ((QueryComboModel) projectCombo.getModel()).getSelectedId();
        Log.print("(AddEditTablespec.projectCombo) " + projectId);
        if (projectId > -1) {
            String[] key = {Integer.toString(projectId), "text"};
            modelCombo.setModel(new QueryComboModel("get tablespec names", /* required-> */ true, key, modelName, "<None>"));
            modelCombo.setEnabled(projectCombo.isEnabled());
        } else {
            modelCombo.setEnabled(false);
        }
    }//GEN-LAST:event_projectComboActionPerformed

    /** Closes the dialog. */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog


    /**
     * Fill the table with table spec data.
     * @param action
     */
    private void loadTablespecData(final String action) {
        final ClientTask task;
        //Log.print("(AddEditTablespec.loadTablespecData) id=" + tablespecId);
        task = new TaskExecuteQuery("tablespec select", Integer.toString(tablespecId));
        task.setCallback(new Runnable() {

            public void run() {
                ResultSet results = (ResultSet) task.getResult();
                if (results != null) {
                    //Log.print("   result not null");
                    loadTablespecDataEntry(results, action);
                } else {
                    Log.print("??? Tables: null resultset returned");
                }
            }
        });
        boolean ok = task.enqueue(this);
    }

    private void loadTablespecDataEntry(ResultSet results, String action) {
        try {
            String type = "";
            String table = "";
            if (projectCombo.getItemCount() > 0) {
                ((QueryComboModel) projectCombo.getModel()).removeElementAt(0);
                ((QueryComboModel) projectCombo.getModel()).insertElementAt("<All Projects>", 0);
            }
            if (results.next()) {
                type = results.getString(2);
                table = results.getString(1);
                tableName.setText(table);
                typeCombo.setSelectedItem(type);
                modelName = results.getString(6);
                Log.print("(AddEditTablespec.loadTablespec) model is " + modelName);
                projectCombo.setSelectedItem(results.getString(3));
                if (projectCombo.getSelectedIndex() < 0) {
                    projectCombo.setSelectedIndex(0);
                }
                requirementCheckBox.setSelected(results.getString(4).equals("Required") ? true : false);
                updateableCheckBox.setSelected(results.getString(5).equals("CoderAdd") ? true : false);         
            }

            if (action.equals("editTable")) {
                // Don't load tablevalue data; this is an edit of tablespec.
                return;
            }       
            String select = "tablevalue select";
            
            // Painted TableModel replaced by ManagedTableModel
            // The painted column names are retained
            // Sort on column 0, value
            valueTable.setModel(new ManagedTableSorter(0,
                    SQLManagedTableModel.makeInstance(select, valueTable.getModel(), tablespecId)));

            ((ManagedTableModel) valueTable.getModel()).register();
            valueTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                public void valueChanged(ListSelectionEvent e) {
                    //Ignore extra messages.
                    if (e.getValueIsAdjusting()) {
                        return;
                    }

                    ListSelectionModel lsm =
                            (ListSelectionModel) e.getSource();
                    if (lsm.isSelectionEmpty() || valueTable.getSelectedRow() < 0) {
                        //no rows are selected
                        editButton.setEnabled(false);
                        deleteButton.setEnabled(false);
                    } else {
                        editButton.setEnabled(true);
                        deleteButton.setEnabled(true);
                    }
                }
            });

            TableColumn column;
            column = valueTable.getColumnModel().getColumn(1);
            column.setPreferredWidth(250); // value

            column = valueTable.getColumnModel().getColumn(2); // level

            column.setCellRenderer(centerCellRenderer);
        } catch (SQLException e) {
            Log.print("(AddEditTablespec.loadTablespecDataEntry) " + e);
        }
    }
    // Use this renderer to center column values
    TableCellRenderer centerCellRenderer = new DefaultTableCellRenderer() {

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            // Value is text
            setText((value == null) ? "" : value.toString());
            //setIcon(null);

            setHorizontalAlignment(JLabel.CENTER);
            return this;
        }
    };

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel addPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JComboBox modelCombo;
    private javax.swing.JButton okButton;
    private javax.swing.JComboBox projectCombo;
    private javax.swing.JCheckBox requirementCheckBox;
    private javax.swing.JTextField tableName;
    private javax.swing.JLabel tableNameLabel;
    private javax.swing.JComboBox typeCombo;
    private javax.swing.JLabel updateLabel;
    private javax.swing.JCheckBox updateableCheckBox;
    private javax.swing.JPanel valuePanel;
    private javax.swing.JScrollPane valueScrollPane;
    private javax.swing.JTable valueTable;
    // End of variables declaration//GEN-END:variables
}
