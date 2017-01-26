/* $Header: /home/common/cvsarea/ibase/dia/src/ui/ProjectAdminPage.java,v 1.27.6.7 2006/03/28 17:02:05 nancy Exp $ */
package ui;

import beans.AddEditProjectFields;
import beans.BoxButton;
import beans.EditCodingManual;
import beans.EditProject;
import beans.ToolTipText;
import client.ClientTask;
import client.Global;
import client.TaskDeleteProject;
import client.TaskEditCodingManual;
import client.TaskExecuteQuery;
import client.TaskExecuteUpdate;
import client.TaskSendProjectFieldsData;
import common.Log;
import common.ProjectFieldsData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ManagedComboModel;
import model.ManagedTableModel;
import model.ManagedTableSorter;
import model.SQLManagedTableModel;
import ui.AdminFrame;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.util.HashSet;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import model.QueryComboModel;


/**
 * The Project screen on the AdminFrame shows a list of the fields
 * defined for a selected project.  The fields may be deleted from this
 * screen or added by clicking the Add button.  Fields may be edited
 * by selecting a row and clicking the Edit button or double clicking
 * a row.
 * @see beans.AddEditProjectFields
 * @see client.TaskDeleteProject
 * @see client.TaskSendProjectFieldsData
 *
 * @author  Nancy
 */
public final class ProjectAdminPage extends ui.AbstractPage
{

   ManagedComboModel projectModel = null;
   AdminFrame frame;
   private int projectId = 0;
   private String projectName = "";
   private String fieldName = "";
   private boolean isUPButtonClicked = false;
   /**
     * Creates new form ProjectAdminPage.
     * @param frame the frame in which to place this screen
     */
   public ProjectAdminPage(AdminFrame frame)
   {
      super(frame);
      this.frame = frame;

      initComponents();

      fieldsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      fieldsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener()
              {
                 public void valueChanged(ListSelectionEvent e)
                 {
                    //Ignore extra messages.

                    if (e.getValueIsAdjusting()) {
                       return;
                    }
                    ListSelectionModel lsm =
                            (ListSelectionModel) e.getSource();
                    if (lsm.isSelectionEmpty()) {
                       //no rows are selected          
                        if(isUPButtonClicked){
                            upButton.setEnabled(true);                            
                        }else{
                               upButton.setEnabled(false);                            
                        }
                       downButton.setEnabled(false);
                       editButton.setEnabled(false);
                       
                    }
                    else {
                       int row = fieldsTable.getSelectedRow();
                       
                       // set the up and down arrow buttons
                       if (row <= 0) {
                          upButton.setEnabled(false);
                       }
                       else {
                          upButton.setEnabled(true);
                           
                       }
                       if (row >= fieldsTable.getRowCount() - 1) {
                          downButton.setEnabled(false);
                       }
                       else {
                          downButton.setEnabled(true);                           
                       }
                       String selectedValue = fieldsTable.getValueAt(row, 0).toString();
                       if(selectedValue.equals("General Document Type")){
                         editButton.setEnabled(false);                             
                       }else{
                         editButton.setEnabled(true);                              
                       }
                                                 
                       //editButton.setEnabled(true);
                       // enable delete if not level indicator field
                       TableModel fieldsModel = (TableModel) fieldsTable.getModel();
                       deleteButton.setEnabled(!"*".equals(fieldsModel.getValueAt(row, 10)));
                       editCodingManual.setEnabled(true);
                    }
                 }

              });
                                      
      addUpDownButtons();
   }

   private void addUpDownButtons()
   {
      upButton = new BoxButton(BoxButton.UP);
      downButton = new BoxButton(BoxButton.DOWN);
      upButton.setEnabled(false);
      downButton.setEnabled(false);
      upDownPane.add(upButton);
      upDownPane.add(downButton);
      upButton.addActionListener(new java.awt.event.ActionListener()
              {

                 public void actionPerformed(java.awt.event.ActionEvent evt)
                 {
                    upButtonActionPerformed(evt);
                 }

              });
      downButton.addActionListener(new java.awt.event.ActionListener()
              {

                 public void actionPerformed(java.awt.event.ActionEvent evt)
                 {
                    downButtonActionPerformed(evt);
                 }

              });
   }

   /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        projectPane = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        projectCombo = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        upDownPane = new javax.swing.JPanel();
        splitsCheckBox = new javax.swing.JCheckBox();
        deleteProjectButton = new javax.swing.JButton();
        fieldsPane = new javax.swing.JPanel();
        fieldsScrollPane = new javax.swing.JScrollPane();
        fieldsTable = new ProjectFieldsTableClass();
        bottomPane = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        editButton = new javax.swing.JButton();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        editCodingManual = new javax.swing.JButton();
        editProject = new javax.swing.JButton();
        createProjectButton = new javax.swing.JButton();
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
        jLabel1.setText("Select Project:");
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

        jLabel2.setText("  Move Selected Field: ");
        projectPane.add(jLabel2);

        upDownPane.setLayout(new java.awt.GridLayout(2, 1));

        upDownPane.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(0, 0, 0, 40)));
        projectPane.add(upDownPane);

        splitsCheckBox.setText("Enable Split Documents");
        splitsCheckBox.setEnabled(false);
        splitsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                splitsCheckBoxActionPerformed(evt);
            }
        });

        projectPane.add(splitsCheckBox);

		createProjectButton.setText("Create Project");
        createProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createProjectButtonActionPerformed(evt);
            }
        });

        projectPane.add(createProjectButton);

        deleteProjectButton.setText("Delete Project");
        deleteProjectButton.setEnabled(false);
        deleteProjectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteProjectButtonActionPerformed(evt);
            }
        });

        projectPane.add(deleteProjectButton);

        editCodingManual.setText("Edit Coding Manual");
        editCodingManual.setEnabled(false);
        editCodingManual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCodingManualActionPerformed(evt);
            }
        });

        projectPane.add(editCodingManual);

		editProject.setText("Edit Config");
        editProject.setEnabled(false);
        editProject.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editProjectActionPerformed(evt);
            }
        });

        projectPane.add(editProject);
        

        add(projectPane, java.awt.BorderLayout.NORTH);

        fieldsPane.setLayout(new java.awt.BorderLayout());

        fieldsPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fieldsScrollPane.setPreferredSize(new java.awt.Dimension(753, 503));
        fieldsTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fieldsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Field Name", "Type", "Size", "Min.", "Req.", "Rep.", "Spell", "Unitize", "Table Name", "Tbl Mand.", "Lvl", "Grp", "Default", "Min", "Max", "More..."
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        fieldsTable.setFocusable(false);
        fieldsTable.setIntercellSpacing(new java.awt.Dimension(2, 1));
        fieldsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fieldsTableMouseClicked(evt);
            }
        });

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

        addButton.setText("  Add ");
        addButton.setEnabled(false);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        jPanel1.add(addButton);

        deleteButton.setText("Delete");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });

        jPanel1.add(deleteButton);

		/*advanceValidation.setText("AdvanceValidation");
        advanceValidation.setEnabled(false);
        advanceValidation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                advanceValidationActionPerformed(evt);
            }
        });
		//jPanel1.add(advanceValidation);
*/
        bottomPane.add(jPanel1);


        add(bottomPane, java.awt.BorderLayout.SOUTH);

    }//GEN-END:initComponents

    private void splitsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_splitsCheckBoxActionPerformed
       try {
          int projectIndex = projectCombo.getSelectedIndex();
          projectId = (projectIndex >= 0
                  ? ((QueryComboModel) projectCombo.getModel()).getIdAt(projectIndex)
                  : 0);
          if (projectId > 0) {
             final ClientTask task = new TaskExecuteUpdate("ProjectAdminPage.split_documents", splitsCheckBox.isSelected() ? "1" : "0", Integer.toString(projectId));
             task.enqueue();
          }
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_splitsCheckBoxActionPerformed

    private void createProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {
      CreateProjectDialog createProjDialog = new CreateProjectDialog(this.frame,true);
      createProjDialog.setLocationRelativeTo(null);
      createProjDialog.setVisible(true);
    }

    private void deleteProjectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteProjectButtonActionPerformed
       try {
          assert projectId > 0;

          Object[] options = {"Yes", "No"};
          int ok = JOptionPane.showOptionDialog(this,
                  "Do you want to delete this project:  " + projectName + "\n\nAll coding data and error statistics" + " will be removed.",
                  "Delete Verification",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null,
                  options,
                  options[1]);
          if (ok == JOptionPane.YES_OPTION) {
             final ClientTask task = new TaskDeleteProject(projectId);
             task.setCallback(new Runnable()
                     {

                        public void run()
                        {
                           try {
                              projectCombo.setSelectedItem(null);
                              JOptionPane.showMessageDialog(ProjectAdminPage.this,
                                      "Project " + projectName + " has been deleted.",
                                      "Confirm Delete",
                                      JOptionPane.INFORMATION_MESSAGE);
                           } catch (Throwable th) {
                              Log.quit(th);
                           }
                        }

                     });

             task.enqueue();

          }

       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_deleteProjectButtonActionPerformed

     private void editCodingManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCodingManualActionPerformed
        try {
           if (projectId > 0) {

              final ClientTask task = new TaskEditCodingManual(projectId);
              task.setCallback(new Runnable()
                      {
                         public void run()
                         {
                            try {
                               String fileName = (String) task.getResult();
                               System.out.println("fileName----------->" + fileName);
                               EditCodingManual editCodingManualDialog = new EditCodingManual(editCodingManual, fileName, projectId);
                               editCodingManualDialog.setModal(true);
                               editCodingManualDialog.show();
                            //   projectModel.setSelectedItem(null);
                            //  JOptionPane.showMessageDialog(ProjectAdminPage.this,
                            //             "Project "+projectName+" Coding Manual has been changed.",
                            //             "Confirm changed",
                            //            JOptionPane.INFORMATION_MESSAGE);
                            } catch (Throwable th) {
                               Log.quit(th);
                            }
                         }

                      });
              task.enqueue();

           }

        } catch (Throwable th) {
           Log.quit(th);
        }
    }//GEN-LAST:event_editCodingManualActionPerformed

    
     private void editProjectActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (projectId > 0) {
                try {
                    EditProject editProjectDialog = new EditProject(editProject, "", projectId);
                    // editProjectDialog.tabSelected();
                    editProjectDialog.setModal(true);
                    editProjectDialog.show();
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        } catch (Throwable th) {
            Log.quit(th);
        }

    }


    private void projectComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectComboActionPerformed
       try {
          int projectIndex = projectCombo.getSelectedIndex();
          projectId = (projectIndex >= 0
                  ? ((QueryComboModel) projectCombo.getModel()).getIdAt(projectIndex)
                  : 0);
          if (projectId > 0) {      
                   
             projectName = (String) projectCombo.getSelectedItem();
             setProjectSplitDocuments(projectId);
             addButton.setEnabled(true);
             deleteProjectButton.setEnabled(Global.theServerConnection.getPermissionAdminImport());
             editCodingManual.setEnabled(true);
             editProject.setEnabled(true);
             // The painted column names are retained
                // Sort on column 21, sequence
             SQLManagedTableModel unsortedModel = SQLManagedTableModel.makeInstance("ProjectAdminPage.projectfieldsTable",
                     fieldsTable.getModel(),
                     projectId);
             unsortedModel.setColumnClass(20, Integer.class);  // sequence
             ManagedTableModel model = new ManagedTableSorter(20, unsortedModel);
             fieldsTable.setModel(model);
             model.register();

             TableColumn column = null;
             column = fieldsTable.getColumnModel().getColumn(0);
             column.setPreferredWidth(110); // field_name
             column = fieldsTable.getColumnModel().getColumn(1);
             column.setPreferredWidth(58); // type
             column = fieldsTable.getColumnModel().getColumn(2);
             column.setPreferredWidth(40); // size
             column = fieldsTable.getColumnModel().getColumn(3);
             column.setPreferredWidth(35); // minimum size
             column = fieldsTable.getColumnModel().getColumn(4);
             column.setPreferredWidth(42); // required
             column = fieldsTable.getColumnModel().getColumn(5);
             column.setPreferredWidth(47); // repeated
             column = fieldsTable.getColumnModel().getColumn(6);
             column.setPreferredWidth(40); // spell
             column = fieldsTable.getColumnModel().getColumn(7);
             column.setPreferredWidth(50); // unitize
             column = fieldsTable.getColumnModel().getColumn(8);
             column.setPreferredWidth(120); // table name
             column = fieldsTable.getColumnModel().getColumn(9);
             column.setPreferredWidth(75); // mandatory
             column = fieldsTable.getColumnModel().getColumn(10);
             column.setPreferredWidth(35); // field level
             column = fieldsTable.getColumnModel().getColumn(11);
             column.setPreferredWidth(35); // field group
             column = fieldsTable.getColumnModel().getColumn(13);
             column.setPreferredWidth(40); // min
             column = fieldsTable.getColumnModel().getColumn(14);
             column.setPreferredWidth(40); // max

             splitsCheckBox.setEnabled(true);
                                      
          }
          else {
             splitsCheckBox.setEnabled(false);
             projectName = "";
             addButton.setEnabled(false);
             deleteProjectButton.setEnabled(false);
             editCodingManual.setEnabled(false);
             editProject.setEnabled(false);
          }
          
           
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_projectComboActionPerformed

    private void projectComboPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt){
       tabSelected();
    }
    
   private void setProjectSplitDocuments(int projectId)
   {
      final ClientTask task = new TaskExecuteQuery("ProjectAdminPage.get_split_documents", Integer.toString(projectId));
      task.setCallback(new Runnable()
              {

                 public void run()
                 {
                    try {
                       ResultSet rs = (ResultSet) task.getResult();
                       if (rs != null) {
                          setProjectSplitDocumentsEntry(rs);
                       }
                    } catch (Throwable th) {
                       Log.quit(th);
                    }
                 }

              });
      task.enqueue();
   }

   private void setProjectSplitDocumentsEntry(ResultSet rs)
   {
      try {
         if (rs.next()) {
            splitsCheckBox.setSelected("0".equals(rs.getString(1)) ? false : true);
         }
      } catch (Throwable th) {
         Log.quit(th);
      }
   }

   private class ProjectFieldsTableClass extends JTable
   {

      public String getToolTipText(MouseEvent event)
      {
         return ToolTipText.getToolTipText(event, fieldsTable);
      }

      public Point getToolTipLocation(MouseEvent event)
      {
         return ToolTipText.getToolTipLocation(event, fieldsTable);
      }

   }

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
       try {
          exitForm();
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void fieldsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fieldsTableMouseClicked
       try {
          if (evt.getClickCount() > 1) {
             // double-click on a row
             editButton.doClick();
          }
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_fieldsTableMouseClicked

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
       try {
           final ClientTask task = new TaskExecuteQuery("listing.get Volume", Integer.toString(projectId));
           task.setCallback(new Runnable() {
                @Override
                public void run() {
                    ResultSet results= (ResultSet)task.getResult();
                    try {
                        if (results.next()) {
                            // check this project has DTYG field or not
                            int rows = fieldsTable.getRowCount();
                            for (int i = 0; i < rows; i++) {
                                String firstColumnValue = fieldsTable.getValueAt(i, 0).toString();
                                if (firstColumnValue.equals("General Document Type")) {
                                    fieldName = firstColumnValue;
                                    break;
                                } else {
                                    fieldName = "";
                                }
                            }
                            showAddEditDialog(false);
                        } else {
                                fieldName = "General Document Type";
                                showAddEditDialog(false);
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(ProjectAdminPage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            task.enqueue(this);           
          //          
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_addButtonActionPerformed

    private void showAddEditDialog(boolean isEdit){
        if(!isEdit){
                 AddEditProjectFields fieldsDialog;
                  fieldsDialog = new AddEditProjectFields(this, projectCombo, 0 //, projectModel.getSelectedId() // project_id
                          , projectId, 0 // sequence
                          , levelFieldExists() < 1 ? false : true
                          ,fieldName);
                  fieldsDialog.setModal(true);
                  fieldsDialog.show();
        }else{
                  int i = fieldsTable.getSelectedRow();
                  int id = ((ManagedTableModel) fieldsTable.getModel()).getRowId(i);
                  int levelId = levelFieldExists();          
                  AddEditProjectFields fieldsDialog = new AddEditProjectFields(this, projectCombo, id                  
                          , projectId, ((Integer) ((ManagedTableSorter) fieldsTable.getModel()).getValueAt(i, 20)).intValue() // sequence
                          , levelId == id || levelId < 1 ? false : true
                          ,fieldName);

                  fieldsDialog.setModal(true);
                  fieldsDialog.oldName = ((ManagedTableSorter) fieldsTable.getModel()).getValueAt(i,0).toString();          
                  fieldsDialog.show();
                  fieldsDialog.setLocationRelativeTo(this);
                  if (!fieldsDialog.isLevelIndicator()) {
                     // enable delete if not level indicator field
                     deleteButton.setEnabled(true);
                  }
                  else {
                     deleteButton.setEnabled(false);
                  }
        }
    }
    
    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
       try {
          // currently showing fieldsTable
          int i = fieldsTable.getSelectedRow();
          //Log.print("Project.deleteButtonActionPerformed, i="+i);
          if (i > -1) {
             ManagedTableModel fieldsModel = (ManagedTableModel) fieldsTable.getModel();
              Object[] options = {"Yes", "No"};
              int ok = JOptionPane.showOptionDialog(this,
                      "Do you want to delete this field:  " + fieldsModel.getValueAt(i, 0) + " ", // fieldname
                      "Delete Verification",
                      JOptionPane.YES_NO_OPTION,
                      JOptionPane.QUESTION_MESSAGE,
                      null,
                      options,
                      options[1]);
              if (ok == JOptionPane.YES_OPTION) {

//                final ClientTask task = new TaskExecuteUpdate("ProjectAdminPage.delete_field",
//                        Integer.toString(fieldsModel.getRowId(i)));
//                task.enqueue();
                  //----------------------------
//                  try {
//                      int projectfieldsId = ((ManagedTableModel) fieldsTable.getModel()).getRowId(i);
//                      final ClientTask task;
//                      ProjectFieldsData projectFieldsData = new ProjectFieldsData();
//                      projectFieldsData.projectfieldsId = projectfieldsId;
//                      projectFieldsData.mode = "Delete";
//                      task = new TaskSendProjectFieldsData(projectFieldsData);
//                      task.enqueue(this);
//                  } catch (Throwable th) {
//                      Log.quit(th);
//                  }
                  
                  try {
                      int projectfieldsId = ((ManagedTableModel) fieldsTable.getModel()).getRowId(i);
                      final ClientTask task;
                      ProjectFieldsData projectFieldsData = clearProjectFieldsData(0);
                      projectFieldsData.projectfieldsId = projectfieldsId;
                      projectFieldsData.mode = "Delete";
                      task = new TaskSendProjectFieldsData(projectFieldsData);
                      task.enqueue(this);
                  } catch (Throwable th) {
                      Log.quit(th);
                  }
                  
                  //----------------------------------

              }
          }
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
       try {
          final ClientTask task = new TaskExecuteQuery("listing.get Volume", Integer.toString(projectId));
           task.setCallback(new Runnable() {
                @Override
                public void run() {
                    ResultSet results= (ResultSet)task.getResult();
                    try {
                        if (results.next()) {
                            // check this project has DTYG field or not
                            int rows = fieldsTable.getRowCount();
                            for (int i = 0; i < rows; i++) {
                                String firstColumnValue = fieldsTable.getValueAt(i, 0).toString();
                                if (firstColumnValue.equals("General Document Type")) {
                                    fieldName = firstColumnValue;
                                    break;
                                } else {
                                    fieldName = "";
                                }
                            }
                            showAddEditDialog(true);
                        } else {
                                fieldName = "General Document Type";
                                showAddEditDialog(true);
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(ProjectAdminPage.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            task.enqueue(this);      
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_editButtonActionPerformed

    public HashSet<String> getProjectFieldsNames(){
        HashSet<String> fieldsNameSet = new HashSet<String>();
        for(int i=0; i< fieldsTable.getModel().getRowCount(); i++){             
            fieldsNameSet.add(fieldsTable.getModel().getValueAt(i,0).toString());
        }
        return fieldsNameSet;
    }

   /**
     * Send task to server to move the currently-selected row up one position.
     */
   private void upButtonActionPerformed(java.awt.event.ActionEvent evt)
   {
      try {          
          int selRow  = fieldsTable.getSelectedRow();
          if(selRow < 0){
              JOptionPane.showMessageDialog(this, "Please select the field to move it up.");
              isUPButtonClicked = false;
              return;              
          }else{
              isUPButtonClicked = true;
          }
         final ClientTask task;
         task = new TaskSendProjectFieldsData(clearProjectFieldsData(1)); // 1 indicates move up
         task.enqueue(this);
      } catch (Throwable th) {
         Log.quit(th);
      }
   }

   /**
     * Send task to server to move the currently-selected row down one position.
     */
   private void downButtonActionPerformed(java.awt.event.ActionEvent evt)
   {
      try {
         final ClientTask task;
         task = new TaskSendProjectFieldsData(clearProjectFieldsData(-1)); // -1 indicates move down
         task.enqueue(this);
      } catch (Throwable th) {
         Log.quit(th);
      }
   }

   /**
     * Create a ProjectFieldsData container containing only the
     * projectfields_id and moveIndicator.
     * @param moveIndicator - +1 for move up or -1 for move down to enter into moveIndicator
     * @return the projectFieldsData container
     */
   private ProjectFieldsData clearProjectFieldsData(int moveIndicator)
   {
      ProjectFieldsData projectFieldsData = new ProjectFieldsData();

      int row = fieldsTable.getSelectedRow();
      if (row < 0) {
         Log.print("(Project.clearProjectFieldsData) row not selected on move !!!???");
         return null;
      }
      projectFieldsData.projectfieldsId = ((ManagedTableModel) fieldsTable.getModel()).getRowId(row);
      projectFieldsData.projectId = 0;
      projectFieldsData.sequence = 0;
      projectFieldsData.fieldName = "";
      projectFieldsData.tagName = "";
      projectFieldsData.defaultValue = "";
      projectFieldsData.minValue = "";
      projectFieldsData.maxValue = "";
      projectFieldsData.charset = "";
      projectFieldsData.mask = "";
      projectFieldsData.validChars = "";
      projectFieldsData.invalidChars = "";
      projectFieldsData.typeField = "";
      projectFieldsData.typeValue = "";
      projectFieldsData.fieldSize = 0;
      projectFieldsData.minimumSize = 0;
      projectFieldsData.repeated = "";
      projectFieldsData.unitize = "";
      projectFieldsData.spellCheck = "";
      projectFieldsData.required = "";
      projectFieldsData.fieldType = "";
      projectFieldsData.tablespecId = 0;
      projectFieldsData.tableMandatory = "";
      projectFieldsData.fieldLevel = "";
      projectFieldsData.fieldGroup = 0;
      projectFieldsData.description = "";
      projectFieldsData.standardFieldValidations = "";
      projectFieldsData.moveIndicator = moveIndicator;
      projectFieldsData.l1_information ="";

      return projectFieldsData;
   }

   /**
     * Search the projectfields for this project to see if a
     * projectfields.level_field_name exists.  If the name exists,
     * another one cannot be created.
     * @return true if the field exists, else false
     */
   private int levelFieldExists()
   {
      int max = fieldsTable.getRowCount();
      for (int i = 0; i < max; i++) {
         if (((String) fieldsTable.getModel().getValueAt(i, 10)).equals("*")) {
            return ((ManagedTableModel) fieldsTable.getModel()).getRowId(i);
         }
      }
      return 0;
   }

   /**
     * @param args the command line arguments
     */
   public static void main(String args[])
   {
      new ProjectAdminPage(AdminFrame.getInstance(null)).setVisible(true);
   }

   private void deleteField(int id)
   {
      final ClientTask task;
      task = new TaskExecuteUpdate("fields delete", Integer.toString(id));
      task.setCallback(new Runnable()
              {

                 public void run()
                 {
                 }

              });
      task.enqueue(this);
   }

   private boolean crossFieldEdits()
   {
      return true;
   }

   /**
     * Check that it's OK to exit the current page.  Subclasses must override this to provide a
     * page-dependent check.
     * @return true if it's OK to exit.  If field cancels save/no-save/cancel dialog,
     *         false is returned.
     */
   protected boolean exitPageCheck()
   {
      // TBD
      return true;
   }

   /** Get the menu bar for the current page.  Subclasses must override this to provide a
     * page-dependent menu bar.
     */
   protected javax.swing.JMenuBar getPageJMenuBar()
   {
      return menuBar;
   }

   /**
     * Perform page initialization.  Subclasses must override this to provide any
     * required page-dependent initialization.
     */
   protected void tabSelected()
   {
      Log.print("ProjectAdminPage tabSelected");
//      if (projectModel == null) {
//         projectModel = new SQLManagedComboModel("Batching.projects");
//         projectModel.register();
//         projectCombo.setModel(projectModel);
//      }
      
      QueryComboModel combo = new QueryComboModel("Batching.projects");
      projectCombo.setModel(combo);
   }

   private javax.swing.plaf.basic.BasicArrowButton upButton;
   private javax.swing.plaf.basic.BasicArrowButton downButton;

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JPanel bottomPane;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton deleteProjectButton;
    private javax.swing.JButton editButton;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JPanel fieldsPane;
    private javax.swing.JScrollPane fieldsScrollPane;
    private javax.swing.JTable fieldsTable;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JComboBox projectCombo;
    private javax.swing.JPanel projectPane;
    private javax.swing.JCheckBox splitsCheckBox;
    private javax.swing.JPanel upDownPane;

	private javax.swing.JButton editCodingManual;
	private javax.swing.JButton editProject;
	private javax.swing.JButton createProjectButton;
    // End of variables declaration//GEN-END:variables
}
