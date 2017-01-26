/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

//import beans.AddAssignQc;
import beans.ToolTipText;
import client.ClientTask;
import client.TaskDeleteListingQC;
import client.TaskGetListingQCField;
import client.TaskSaveListingQC;
import common.Log;
import common.PopulateData;
import common.msg.MessageConstants;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import model.ManagedTableModel;
import model.ManagedTableSorter;
import model.QueryComboModel;
import model.ResultSetTableModel;
import model.SQLManagedComboModel;
import model.SQLManagedTableModel;
import org.w3c.dom.Element;

/**
 *
 * @author bmurali
 */
public class ProjectSelectionDialogForAssignListingQC extends javax.swing.JDialog implements MessageConstants {
    private JFrame parent;     
    private String whichStatus;
    private javax.swing.JTable fieldsTable;
    private javax.swing.JPanel projectPane;
    private javax.swing.JPanel labelPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JComboBox userCombo;
    private javax.swing.JComboBox fieldCombo;
    private javax.swing.JPanel fieldsPane;
    private javax.swing.JScrollPane fieldsScrollPane;
    private javax.swing.JPanel bottomPane;
    private javax.swing.JPanel addEditPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton addButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JButton closeButton;
    private javax.swing.JButton saveButton;
    private SQLManagedComboModel userModel = null;
    private QueryComboModel fieldModel = null;
    PopulateData data;
    private final String GET_ALL_CHECKERS = "listing_qc.get user";
    private final String GET_ALL_VOLUME = "listing.get volume";
    private final String GET_ALL_TALLY_FIXER = "tally_fix.get user";
    private int projectId = 0;
    private int volumeId = 0;
    private int userId =0;
  
    private String projectName = "";
    private String volumeName = "";
    private String selectedFieldName="";
    final private static int PROJECT_COLUMN = 2;    
    private int selectedBatchId;
    private List fieldName;
    private List fields = new ArrayList();
    private boolean addFlag = true;
    SQLManagedTableModel projectModel =null;
    SQLManagedTableModel projectModel1 =null;
    ResultSetTableModel model = null;
    /** Creates new form ProjectSelectionDialogForAssignListingQC */
    public ProjectSelectionDialogForAssignListingQC(JFrame parent, String whichStatus,String projectName,
            String volumeName,List fieldValue,int projectId,int volumeId) {      
        super(parent, true);            
        this.parent = parent;
        this.whichStatus = whichStatus;     
        this.projectName = projectName;
        this.volumeName = volumeName;
        this.fields = fieldValue;
        this.projectId = projectId;
        this.volumeId = volumeId;
        if("Listing".equals(whichStatus)){
              setTitle("Project Selection for ListingFix");
         }else if("Tally".equals(whichStatus)){
             setTitle("Project Selection for TallyFix");
         }
        initComponents();
		
        setLocationRelativeTo(parent);
        //fieldsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fieldsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);      
        fieldsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;
                ListSelectionModel lsm =
                        (ListSelectionModel)e.getSource();
                if (lsm.isSelectionEmpty()) {
                    addButton.setEnabled(false);
                    editButton.setEnabled(false);                    
                } else {
                    int row = fieldsTable.getSelectedRow();
                    Object obj =fieldsTable.getValueAt(row,0);                    
                    addButton.setEnabled(true);
                    editButton.setEnabled(true);
                    saveButton.setEnabled(false);
                }
            }           
        });
       //    tabselect();                 
    }
    
     private void initComponents() {
		
        projectPane = new javax.swing.JPanel(); 
        labelPane = new javax.swing.JPanel(); 
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        userCombo = new javax.swing.JComboBox();
        fieldCombo = new javax.swing.JComboBox();
        
        fieldsPane = new javax.swing.JPanel();
        fieldsScrollPane = new javax.swing.JScrollPane();
        fieldsTable = new ProjectFieldsTableClass();
        bottomPane = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        addEditPane = new javax.swing.JPanel();
        deleteButton = new javax.swing.JButton();
        closeButton = new javax.swing.JButton(); 
        saveButton = new javax.swing.JButton(); 
        addEditPane.setVisible(false);
        
       
        projectPane.setLayout(new java.awt.BorderLayout());
        labelPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel1.setText("Project:");
        labelPane.add(jLabel1);
             
        jLabel3.setText(projectName);
        labelPane.add(jLabel3);        
        
        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));        
        jLabel2.setText(" Volume:");
        labelPane.add(jLabel2);
      
        jLabel4.setText(volumeName);
        labelPane.add(jLabel4);       
        jLabel7.setEnabled(false);
        jLabel7.setText("No Fields Available to Assign");       
        
        projectPane.add(labelPane,java.awt.BorderLayout.NORTH);
        
        addEditPane.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jLabel5.setFont(new java.awt.Font("Dialog", 1, 14));       
        jLabel5.setText("Select Field:");
        addEditPane.add(jLabel5);

        fieldCombo.setPreferredSize(new java.awt.Dimension(150, 25));
        fieldCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldComboActionPerformed(evt);
            }       
        });
         
        addEditPane.add(fieldCombo);
        
        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14));       
        jLabel6.setText("Select User:");
        addEditPane.add(jLabel6);
        
        userCombo.setPreferredSize(new java.awt.Dimension(150, 25));
        userCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userComboActionPerformed(evt);
            }
        });
         addEditPane.add(userCombo);
        projectPane.add(addEditPane,java.awt.BorderLayout.CENTER);
        
        add(projectPane, java.awt.BorderLayout.NORTH);

        fieldsPane.setLayout(new java.awt.BorderLayout());
       
        fieldsPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fieldsPane.setPreferredSize(new java.awt.Dimension(600, 250));
        fieldsScrollPane.setPreferredSize(new java.awt.Dimension(600, 250));
        fieldsTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
//        fieldsTable.setModel(new javax.swing.table.DefaultTableModel(
//            new Object [][] {
//
//            },
//               
//            new String [] {
//                "Field Name","User Name","Status"
//            }
//         ) {
//            boolean[] canEdit = new boolean [] {
//                 false,false,false
//            };
//
//            public boolean isCellEditable(int rowIndex, int columnIndex) {
//                return canEdit [columnIndex];
//            }
//         });
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

        addButton.setText(" Add ");
        addButton.setEnabled(true);
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }

        });
        jPanel1.add(addButton);        
        editButton.setText(" Edit ");
        editButton.setEnabled(false);
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //editButtonActionPerformed(evt);
            }
        });
        //jPanel1.add(editButton);
        deleteButton.setText(" Delete ");
        deleteButton.setEnabled(false);
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        jPanel1.add(deleteButton);
        saveButton.setText(" Save ");
        saveButton.setEnabled(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        jPanel1.add(saveButton);
        closeButton.setText(" Close ");
        closeButton.setEnabled(true);
        closeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeButtonActionPerformed(evt);
            }
        });
        jPanel1.add(closeButton);
        bottomPane.add(jPanel1);
        add(bottomPane, java.awt.BorderLayout.SOUTH);

      
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        //setTitle("Project Selection");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        pack();
     // tabselect();  
    }// </editor-fold>  
     
      public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ProjectSelectionDialogForAssignListingQC(new javax.swing.JFrame(),
                        "dummy","","",new ArrayList(),0,0).setVisible(true);                
            }
        });
    }

     public void tabselect() {       
//          if("Listing".equals(whichStatus)){
//                   fieldsTable.setModel(new ManagedTableSorter(0,
//                  SQLManagedTableModel.makeInstance("listing_qc.get values",
//                  fieldsTable.getModel(),projectId)));
//          }else if("Tally".equals(whichStatus)){
//                  fieldsTable.setModel(new ManagedTableSorter(0,
//                  SQLManagedTableModel.makeInstance("tally_qc.get values",
//                  fieldsTable.getModel(),projectId)));
//          }       
               
             try{
                 final ClientTask task = new TaskGetListingQCField(projectId,volumeId,whichStatus);
                 task.setCallback(new Runnable(){
                          public void run(){
                             java.sql.ResultSet results = (java.sql.ResultSet) task.getResult();
                             model = new ResultSetTableModel(results,new String [] {        
                                                             "Field Name","User Name" ,"Status"});                         
                             fieldsTable.setModel(model);  
                          }
                       });
                task.enqueue(this);      
             }catch(Throwable th){Log.quit(th);}
               
        
        
//            ManagedTableModel model = (ManagedTableModel) fieldsTable.getModel();
//            fieldsTable.setModel(model);                
//            model.register();   
            
//            TableColumn column = null;
//            column = fieldsTable.getColumnModel().getColumn(0);
//            column.setPreferredWidth(110); // fieldName     
//            column = fieldsTable.getColumnModel().getColumn(1);
//            column.setPreferredWidth(110); //userName
//            column = fieldsTable.getColumnModel().getColumn(2);            
//            column.setPreferredWidth(110); //status 
            
            //------populate field Combo ----------
            if(addFlag){
               boolean flag =true;         
                  for (int i = 0; i < fields.size(); i++) {                 
                      if(flag){
                          fieldCombo.addItem(" ");
                          flag = false;
                      }
                      fieldCombo.addItem(fields.get(i));                  
                }
               addFlag = false;
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
       
      private void addButtonActionPerformed(ActionEvent evt) {
              addEditPane.setVisible(true);
              saveButton.setEnabled(true);
              addButton.setEnabled(false);
              editButton.setEnabled(false);
              deleteButton.setEnabled(false);
//              if(addFlag){                 
//               fieldCombo.removeAllItems();
//              }               
//             boolean flag =true; 
//             int k = 0;
//             int fieldComboSize = fieldCombo.getModel().getSize();             
//               for (int i = 0; i < fieldsTable.getModel().getRowCount(); i++) {                   
//                   for(int j = 0; j < fieldComboSize; j++){
//                      if(fieldCombo.getItemAt(j) == fieldsTable.getModel().getValueAt(i,0)){
//                          k++; 
//                      }
//                   }
//                   if(k == 0 && !fieldsTable.getModel().getValueAt(i,0).equals(" ")){                      
//                      if(flag){
//                              fieldCombo.addItem(" ");
//                              flag = false;
//                      }
//                      fieldCombo.addItem(fieldsTable.getModel().getValueAt(i,0));                                           
//                   }                   
//             }
              if("Listing".equals(whichStatus)){
                  userModel = new SQLManagedComboModel(GET_ALL_CHECKERS);
              }
              else if("Tally".equals(whichStatus)){
                  userModel = new SQLManagedComboModel(GET_ALL_TALLY_FIXER);
              }          
            userModel.register();
            userCombo.setModel(userModel);               
       }
      
      private void saveButtonActionPerformed(ActionEvent evt) {
               if(selectedFieldName.trim().length() <= 0){
                  JOptionPane.showMessageDialog(ProjectSelectionDialogForAssignListingQC.this,
                                   "Please select any field",
                                   "Error",
                                   JOptionPane.ERROR_MESSAGE);
               }else if(userId <= 0){
                  JOptionPane.showMessageDialog(ProjectSelectionDialogForAssignListingQC.this,
                                   "Please select any user",
                                   "Error",
                                   JOptionPane.ERROR_MESSAGE);
               }else{
                  saveButton.setEnabled(false);               
                  final ClientTask task = new TaskSaveListingQC(projectId, volumeId, userId, selectedFieldName,whichStatus);
                  task.setCallback(new Runnable() {
                   public void run() {
                       Element reply = (Element) task.getResult();
                       String action = reply.getNodeName();
                        if (T_FAIL.equals(action)) {
                           // addFlag = true;
                           // TBD: How do we tell them of problem opening batch
                       // This gives them a message box, doesn't open viewer
                           JOptionPane.showMessageDialog(ProjectSelectionDialogForAssignListingQC.this,
                                   "Can't open selected batch",
                                   "Error",
                                   JOptionPane.ERROR_MESSAGE);
                       }
                   }});
                  addFlag = false;
                  task.enqueue(this);   
                  addButton.setEnabled(true);
                  deleteButton.setEnabled(false);
                  tabselect();
               }
      }
      
      private void fieldComboActionPerformed(ActionEvent evt) {                           
         if(fieldCombo.getItemCount() > 0){                
             selectedFieldName = fieldCombo.getSelectedItem().toString();                
         }
      }
      
     private void userComboActionPerformed(ActionEvent evt) {
                saveButton.setEnabled(true);         
                int sel = userCombo.getSelectedIndex();     
                if (sel > -1) {                  
                     userId = ((SQLManagedComboModel)userCombo.getModel()).getIdAt(sel);                     
                }
     }
     
    private void fieldsTableMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            if (evt.getClickCount() >= 1) {

                int selectedIndex = fieldsTable.getSelectedRow();
                String field_name = (String) fieldsTable.getValueAt(selectedIndex, 0);
                String userName = (String) fieldsTable.getValueAt(selectedIndex, 1);
                String status = (String) fieldsTable.getValueAt(selectedIndex, 2);
                if ("Assigned".equals(status) || "Complete".equals(status)) {
                    //addEditPane.setVisible(true);
                    saveButton.setEnabled(false);
                    deleteButton.setEnabled(false);
                    addButton.setEnabled(false);
                    addEditPane.setVisible(false);
                }else {
                    addEditPane.setVisible(true);
                    deleteButton.setEnabled(true);
                    editButton.setEnabled(false);
                    addButton.setEnabled(false);
                    saveButton.setEnabled(false);
                }
                fieldCombo.setSelectedItem(field_name);
//                int k = 0;
//                int fieldComboSize = fieldCombo.getModel().getSize();
//                for(int j = 0; j < fieldComboSize; j++){
//                   if(fieldCombo.getItemAt(j) == field_name){
//                       k++; 
//                   }
//                }
//                fieldCombo.removeAllItems();
//                if(k == 0){
//                   fieldCombo.addItem(field_name);
//                }                
                userModel = new SQLManagedComboModel(GET_ALL_CHECKERS);
                userModel.register();
                userCombo.setModel(userModel);
                userCombo.setSelectedItem(userName);
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }
  
    private void deleteButtonActionPerformed(ActionEvent evt) {
         deleteButton.setEnabled(false);
         addButton.setEnabled(true);         
         int selectedIndex = fieldsTable.getSelectedRow();    
         //int listing_qc_id = ((ManagedTableModel) fieldsTable.getModel()).getRowId(selectedIndex);
         int listing_qc_id = Integer.parseInt(fieldsTable.getModel().getValueAt(selectedIndex,3).toString());        
         String status = (String) fieldsTable.getValueAt(selectedIndex, 2);        
         if(status.equals("Idle")){               
            final ClientTask task = new TaskDeleteListingQC(listing_qc_id);
            task.enqueue(this);
         }
         tabselect();
    }
    
    private void closeButtonActionPerformed(ActionEvent evt) {                                                           
        try {
            // Add your handling code here:        
            closeDialog(null);
        } catch (Throwable th) {
            Log.quit(th);      
    } 
    }
    
    private void closeDialog(java.awt.event.WindowEvent evt) {                             
        setVisible(false);
        dispose();
        parent.setVisible(true);
    }  
     
}
