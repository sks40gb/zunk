/*
 * ViewTallyGroupDialog.java
 *
 * Created on September 4, 2008, 3:56 PM
 */

package ui;

import client.ClientTask;
import client.TaskExecuteQuery;
import client.TaskExecuteUpdate;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import model.ResultSetTableModel;

/**
 * This class is used to view the tally group list.
 * @author  anuragg
 */
public class ViewTallyGroupDialog extends javax.swing.JDialog {
   
   private String projectName = null;
   private String volumeName = null;   
   private JDialog parent = null;
   private ResultSetTableModel model = null;
   private ArrayList tallyStatusList = null;    //Contains tally status  i.e Completed or Not Completed   
   private final String GET_TALLY_GROUPS = "Tally.viewGroupDescriptions";
   private final String CHECK_TALLY_STATUS = "Tally.checkTallyStatus";
   private final String CLOSE_TALLY_VOLUME = "Tally.closeVolume";
   private final String TALLY_GET_BATCHES = "Tally.getBatches";
   private final String DEL_USERS_QUEUE = "TallyQC.deleteUsersQueue";
   private String[] viewTallyGroupArray =  new String[]{"Field Name", "Group Number", "Type","5/100"};
   private final String TALLY_STATUS_COMPLETED = "Completed";
   private final String TALLY_STATUS_NOT_COMPLETED = "Not_Completed";
   
   /** Creates new form ViewTallyGroupDialog
    * @param parent  : JDialog object of parent class
    * @param modal : boolean
    */
   public ViewTallyGroupDialog(JDialog parent, boolean modal) {
      super(parent, modal);
      initComponents();
   }
   
   /** Custom constructor for passing user-defined parameters
    * @param parent  : JDialog object of parent class
    * @param projectName : name of project selected
    * @param volumeName : name of volume selected
    */
   public ViewTallyGroupDialog(JDialog parent,String projectName,String volumeName){
      this.projectName = projectName;
      this.volumeName = volumeName;
      this.parent = parent;
      initComponents();
      
      //Set the selected project name and volume name from the parent window
      projectLabel.setText(projectName);
      volumeLabel.setText(volumeName);     
      
      //Loads the group table.
      final ClientTask task = new TaskExecuteQuery(GET_TALLY_GROUPS, projectName, volumeName);      
        task.setCallback(new Runnable() {
            public void run() {                            
               ResultSet results = (ResultSet) task.getResult();
               model = new ResultSetTableModel(results,viewTallyGroupArray);
               groupTable.setModel(model);
               int rowsCount = groupTable.getRowCount();
               if(rowsCount > 0){
                 closeVolumeButton.setEnabled(true);
               }else{
                 closeVolumeButton.setEnabled(false);
               }
            }
        });
        task.enqueue(this);       
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      projectHeadingLabel = new javax.swing.JLabel();
      projectLabel = new javax.swing.JLabel();
      volumeHeadingLabel = new javax.swing.JLabel();
      volumeLabel = new javax.swing.JLabel();
      headingLabel = new javax.swing.JLabel();
      jScrollPane1 = new javax.swing.JScrollPane();
      groupTable = new javax.swing.JTable();
      closeVolumeButton = new javax.swing.JButton();
      cancelButton = new javax.swing.JButton();

      setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
      setTitle("View Tally Groups");
      addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosing(java.awt.event.WindowEvent evt) {
            formWindowClosing(evt);
         }
      });

      projectHeadingLabel.setText("Project :");

      volumeHeadingLabel.setText("Volume :");

      headingLabel.setFont(new java.awt.Font("Dialog", 1, 18));
      headingLabel.setText("Tally Groups");

      groupTable.setModel(new javax.swing.table.DefaultTableModel(
         new Object [][] {

         },
         new String [] {
            "Select", "Field Name", "Group Number", "Type"
         }
      ) {
         Class[] types = new Class [] {
            java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
         };
         boolean[] canEdit = new boolean [] {
            true, false, false, false
         };

         public Class getColumnClass(int columnIndex) {
            return types [columnIndex];
         }

         public boolean isCellEditable(int rowIndex, int columnIndex) {
            return canEdit [columnIndex];
         }
      });
      jScrollPane1.setViewportView(groupTable);

      closeVolumeButton.setText("Close Volume");
      closeVolumeButton.setEnabled(false);
      closeVolumeButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            closeVolumeButtonActionPerformed(evt);
         }
      });

      cancelButton.setText("Cancel");
      cancelButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            cancelButtonActionPerformed(evt);
         }
      });

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(layout.createSequentialGroup()
                  .addGap(44, 44, 44)
                  .addComponent(projectHeadingLabel)
                  .addGap(18, 18, 18)
                  .addComponent(projectLabel)
                  .addGap(64, 64, 64)
                  .addComponent(volumeHeadingLabel)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                  .addComponent(volumeLabel))
               .addGroup(layout.createSequentialGroup()
                  .addGap(24, 24, 24)
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addGroup(layout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addComponent(closeVolumeButton)
                        .addGap(29, 29, 29)
                        .addComponent(cancelButton))
                     .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)))
               .addGroup(layout.createSequentialGroup()
                  .addGap(146, 146, 146)
                  .addComponent(headingLabel)))
            .addContainerGap(33, Short.MAX_VALUE))
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(headingLabel)
            .addGap(25, 25, 25)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(projectHeadingLabel)
               .addComponent(projectLabel)
               .addComponent(volumeHeadingLabel)
               .addComponent(volumeLabel))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGap(13, 13, 13)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(closeVolumeButton)
               .addComponent(cancelButton))
            .addContainerGap(25, Short.MAX_VALUE))
      );

      pack();
   }// </editor-fold>//GEN-END:initComponents

   /**
    * Invoked when cancel button is pressed.
    * It disposes the TallyGroupDialog and shows the parent dialog.
    * @param evt
    */
   private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
      dispose();
      this.parent.setVisible(true);
}//GEN-LAST:event_cancelButtonActionPerformed

   /**
    * Invoked when close button of dialog is pressed
    * It disposes the TallyGroupDialog and shows the parent dialog.
    * @param evt
    */
   private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
      dispose();
      this.parent.setVisible(true);
   }//GEN-LAST:event_formWindowClosing

   /**
    * Invoked when close volume button is pressed.
    * It search for all the rows whether all the groups have been 'Completed'.
    * If it is 'Yes',then it set the batch status for the volume as 'TComplete'
    * @param evt
    */
   private void closeVolumeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeVolumeButtonActionPerformed
      tallyStatusList = new ArrayList();   
      int rowsCount = groupTable.getRowCount();  
      //Loop through each row
      for(int i = 0;i < rowsCount ;i++){
         String groupNumber = groupTable.getValueAt(i, 1).toString();         
         //Check the tally status for each group number
         final ClientTask task = new TaskExecuteQuery(CHECK_TALLY_STATUS,groupNumber);
          task.setCallback(new Runnable() {
            public void run() {                            
               try {
                  ResultSet results = (ResultSet) task.getResult();
                  if (results.next()) {
                     addTallyStatusInList(results.getString(1));
                  }else{
                     addTallyStatusInList("");
                  }
               } catch (SQLException ex) {
                  Logger.getLogger(ViewTallyGroupDialog.class.getName()).log(Level.SEVERE, null, ex);
               }
            }
        });
        task.enqueue(this);
        //loop ends
      }      
      //Confirm the user for closing the volume
      int confirmOption = JOptionPane.showConfirmDialog(this, "Are you sure want to close\ntally operations for this volume ?" ,
                                                         "Alert" ,JOptionPane.OK_CANCEL_OPTION);
      if(confirmOption == 0){
         //Check the tally status
         boolean isTallyCompleted = verifyTallyStatus();
         if(isTallyCompleted){
            //Close the selected volume               
            final ClientTask taskGetBatches = new TaskExecuteQuery(TALLY_GET_BATCHES,projectName,volumeName);
            taskGetBatches.setCallback(new Runnable() {
            public void run() {                            
               ResultSet results = (ResultSet) taskGetBatches.getResult();
               updateBatchStatus(results);
            }
            });
            taskGetBatches.enqueue(this);            
         }
      }
   }//GEN-LAST:event_closeVolumeButtonActionPerformed
   
   //Add status in a list for all the rows in the table
   private void addTallyStatusInList(String tallyStatus){
      if(tallyStatus != null && tallyStatus.equalsIgnoreCase(TALLY_STATUS_COMPLETED)){
         tallyStatusList.add(tallyStatus);
      }else{
         tallyStatusList.add(TALLY_STATUS_NOT_COMPLETED);
      }      
   }
   
   //Verify the tally status through the list whether its comleted or not
   private boolean verifyTallyStatus(){      
      for(Object status : tallyStatusList){
         if(status.toString().equalsIgnoreCase(TALLY_STATUS_NOT_COMPLETED)){
            JOptionPane.showMessageDialog(this, 
                    "Cannot close the volume.\n All the groups should be Tally Completed.","Alert",JOptionPane.ERROR_MESSAGE);
            return false;
         }
      }
      return true;
   }
   
   //Set the batch status as TComplete for each batch inside the volume
   private void updateBatchStatus(ResultSet rs){
      ArrayList batchIDList = new ArrayList();
      try {
         int check = 0;
         while (rs.next()) {
            check++;
            int batchId = rs.getInt(1);
            batchIDList.add(batchId);
            final ClientTask task = new TaskExecuteUpdate(CLOSE_TALLY_VOLUME,Integer.toString(batchId));            
            task.enqueue(this);
         }
         if(check > 0){
            for(int i = 0;i < batchIDList.size();i++){
               final ClientTask task = new TaskExecuteUpdate(DEL_USERS_QUEUE,batchIDList.get(i).toString());            
               task.enqueue(this);
            }
            JOptionPane.showMessageDialog(this, 
                    "Tally operations has been closed\nfor this volume successfully.","Volume_Closed",JOptionPane.INFORMATION_MESSAGE);
            cancelButtonActionPerformed(null);
         }else{
            JOptionPane.showMessageDialog(this, 
                    "These volume has already been closed.","Error",JOptionPane.ERROR_MESSAGE);
         }
      } catch (SQLException ex) {
         Logger.getLogger(ViewTallyGroupDialog.class.getName()).log(Level.SEVERE, null, ex);
      }
   }
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton cancelButton;
   private javax.swing.JButton closeVolumeButton;
   private javax.swing.JTable groupTable;
   private javax.swing.JLabel headingLabel;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JLabel projectHeadingLabel;
   private javax.swing.JLabel projectLabel;
   private javax.swing.JLabel volumeHeadingLabel;
   private javax.swing.JLabel volumeLabel;
   // End of variables declaration//GEN-END:variables
   
}
