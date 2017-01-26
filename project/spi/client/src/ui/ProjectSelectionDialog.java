/* $Header: /home/common/cvsarea/ibase/dia/src/ui/ProjectSelectionDialog.java,v 1.34.2.3 2006/03/28 17:02:05 nancy Exp $ */
/*
 * ProjectSelectionDialog.java
 *
 * Created on November 3, 2003, 12:17 PM
 */
package ui;


import client.TaskCodingManual;
import common.Log;
import common.msg.MessageConstants;
import common.msg.MessageWriter;
import client.ClientTask;
import client.Global;
import client.ServerConnection;
import client.Sql;
import client.TaskCodingManualTracking;
import client.TaskExecuteUpdate;
import client.TaskOpenBatch;
import java.io.BufferedOutputStream;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import model.ResultSetTableModel;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import org.w3c.dom.Element;
import tools.LocalProperties;

/**
 * This screen shows the user all batches that are available to him/her for
 * opening in the given status.  The user may select a batch and proceed to the
 * appropriate viewer.
 * @author  Bill
 */
public class ProjectSelectionDialog extends javax.swing.JDialog
        implements MessageConstants
{

   final private static int COUNT_COLUMN = 1;  // column containing batch count

   final private static int PROJECT_COLUMN = 2;  // project_id col. (hidden)

   final private static int COUNT_WIDTH = 80;  // preferred width of count column

   final private ServerConnection scon = Global.theServerConnection;
   private int selectedBatchId = 0;
   private int  sub_process;
   // The client task to handle interaction with the server for this dialog

   private ClientTask task;
   private JFrame parent;
   // The status of the batches to be processes.  (value of batch.status)

   private String whichStatus;
   private int project_id;
   private String projectName;
   private long start_time = 0;
   private long end_time = 0;
   int selectedProjectId = 0;
   private Process process =null;   
   private String actualPath = null;
    
  private static final String msWordPath = LocalProperties.getProperty("msWordPath");           //for linux ------------>> soffice
  private  static final String acrobatReaderPath = LocalProperties.getProperty("acrobatReaderPath");        //for linux ------------>> evince
  private static final String notepad =  LocalProperties.getProperty("notepad");        //for linux ------------>> gedit
   
   // constants 
   
   final private static int LISTING = 6;
   final private static int TALLY = 8;
  final private static int QA = 10;
   /**
     * Creates new form ProjectSelectionDialog.
     * @param parent The ActivitySelectionFrame which called this dialog
     * @param whichStatus A value of the enum type of batch.status
     */

   public ProjectSelectionDialog(JFrame parent, String whichStatus)
   {
      super(parent, true);
      this.parent = parent;
      this.whichStatus = whichStatus;
      initComponents();
      setLocationRelativeTo(parent);

      // set a reasonable title
      if ("Unitize".equals(whichStatus)) {
         activitySelectionTitle.setText("Open Batch for Unitization");
      }
      else if ("UQC".equals(whichStatus)) {
         activitySelectionTitle.setText("Open Batch for Unitization QC");
      }
      else if ("Coding".equals(whichStatus)) {
         activitySelectionTitle.setText("Open Batch for Coding");
      }
      else if ("CodingQC".equals(whichStatus)) {
         activitySelectionTitle.setText("Open Batch for Quality Control");
      }
      else if ("codingManual".equals(whichStatus)) {
         activitySelectionTitle.setText("Display the Project Available");
      }
      else if ("InvestigationTool".equals(whichStatus)) {
         activitySelectionTitle.setText("Open Investigation Tool");
      }
      else if ("Masking".equals(whichStatus)) {
         activitySelectionTitle.setText("Display the Project Available");
      }
      else if (" ".equals(whichStatus)) {
         activitySelectionTitle.setText("Display the Project Available");
      }
       else if ("ModifyErrors".equals(whichStatus)) {
         activitySelectionTitle.setText("Display the Project Available");
      }
      else {
         Log.quit("ProjectSelectionDialog: invalid status");
      }

      // set okButton to respond to enter key
        // (doesn't work, but ctrl-Enter works ??)
      this.getRootPane().setDefaultButton(okButton);

      // Render cells grayed out (disabled) if no batches available
        // Also, column 1 is right justified
      ObjectRenderer renderer = new ObjectRenderer();
      projectSelectionTable.setDefaultRenderer(Object.class, renderer);
      //projectSelectionTable.setDefaultRenderer(Number.class, renderer);
      projectSelectionTable.getColumnModel().getColumn(COUNT_COLUMN).setMaxWidth(COUNT_WIDTH);

      ListSelectionModel selectionModel = projectSelectionTable.getSelectionModel();
      selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      selectionModel.addListSelectionListener(new TableListener());

      // Start a task to handle interaction
        // TBD: Worry about what if queue not available to start task
      if ("codingManual".equals(whichStatus)) {
         task = new ProjectSelectionTaskForCodingManual();
      }
      else {
         task = new ProjectSelectionTask();
      }
      task.enqueue(this);

   }

   /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        projectSelectionPanel = new javax.swing.JPanel();
        activitySelectionTitle = new javax.swing.JLabel();
        leftGlue = new javax.swing.JPanel();
        messageArea = new javax.swing.JTextArea();
        rightGlue = new javax.swing.JPanel();
        projectSelectionScroller = new javax.swing.JScrollPane();
        projectSelectionTable = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Project Selection");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        projectSelectionPanel.setLayout(new java.awt.GridBagLayout());

        projectSelectionPanel.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(40, 40, 40, 40)), new javax.swing.border.CompoundBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED), new javax.swing.border.EmptyBorder(new java.awt.Insets(40, 40, 40, 40)))));
        projectSelectionPanel.setMinimumSize(new java.awt.Dimension(800, 500));
        projectSelectionPanel.setPreferredSize(new java.awt.Dimension(800, 500));
        activitySelectionTitle.setBackground(new java.awt.Color(204, 204, 204));
        activitySelectionTitle.setFont(new java.awt.Font("Dialog", 1, 24));
        activitySelectionTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        activitySelectionTitle.setText("Open Batch for Coding");
        activitySelectionTitle.setAlignmentX(0.5F);
        activitySelectionTitle.setMaximumSize(new java.awt.Dimension(299, 60));
        activitySelectionTitle.setMinimumSize(new java.awt.Dimension(400, 60));
        activitySelectionTitle.setPreferredSize(new java.awt.Dimension(400, 60));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = -119;
        gridBagConstraints.ipady = -17;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        projectSelectionPanel.add(activitySelectionTitle, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.5;
        projectSelectionPanel.add(leftGlue, gridBagConstraints);

        // null inherits background from parent
        messageArea.setBackground(null);
        messageArea.setColumns(50);
        messageArea.setEditable(false);
        messageArea.setRows(2);
        messageArea.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        projectSelectionPanel.add(messageArea, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.5;
        projectSelectionPanel.add(rightGlue, gridBagConstraints);

        projectSelectionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Project", "# Batches"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        projectSelectionScroller.setViewportView(projectSelectionTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        projectSelectionPanel.add(projectSelectionScroller, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.GridLayout(1, 0, 60, 0));

        okButton.setText("OK");
        okButton.setEnabled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 0);
        projectSelectionPanel.add(buttonPanel, gridBagConstraints);

        getContentPane().add(projectSelectionPanel, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
       try {
          // Add your handling code here:
          Log.print("ProjectSelectionDialog: Cancel pressed");
          closeDialog(null);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void cancelSubProcessButtonActionPerformed() {                                             
       try {
          // Add your handling code here:
          Log.print("ProjectSelectionDialog: Cancel pressed");
          closeDialog(null);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }     
    
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
       try {
          okButton.setEnabled(false); // prevent user's clicking ok again.
          Log.print("ProjectSelectionDialog: OK pressed " + selectedBatchId);
          int selectedIndex = projectSelectionTable.getSelectedRow();           
          if (selectedIndex >= 0) {
             selectedProjectId = Integer.parseInt((String) projectSelectionTable.getModel().getValueAt(selectedIndex, PROJECT_COLUMN));
          }
          if ("codingManual".equals(whichStatus)) {             
             try {
                final ClientTask task;
                task = new TaskCodingManual(selectedProjectId);
                task.setCallback(new Runnable()
                        {
                           public void run()
                           {                              
                               String filePath = (String) task.getResult();                                
                               if(filePath.trim().length() != 0){
                                   openCodingManual(filePath);
                               }else{
                                   JOptionPane.showMessageDialog(projectSelectionPanel,"No coding manual found for the selected project.");
                               }
                           }
                        });
                task.enqueue(this);
             } catch (Throwable th) {
                Log.quit(th);
             }
          }
          else {//project selection dialog for all the client Process ,such as Unitize, Coding,etc
               
            //Code used to show and select the sub process for Modify Errors process(Edit Checking)
            String processType = null;
            int editprocess = 0;
            if (selectedBatchId == 0) {//Executes only once per batch(Before ModifyErrors batch is selected for editChecking)

                if (whichStatus.equalsIgnoreCase("ModifyErrors")) {//shows list of sub process to do ModifyErrors fixes
                    processType = selectProcessType();
                    if (null != processType) {
                        if (processType.equals("Listing")) {
                            editprocess = LISTING;
                        } else if (processType.equals("Tally")) {
                            editprocess = TALLY;
                        } else if (processType.equals("QA")) {
                            editprocess = QA;
                        }
                    }
                }
            }
            //sub process dialog, cancel button is clicked  
            if (processType == null && whichStatus.equalsIgnoreCase("ModifyErrors") && selectedBatchId == 0) {                
                okButton.setEnabled(true);
                return;

            } else {//sub process dialog ok button action prformed
                //dialog shows conformation of sub process
                if (whichStatus.equalsIgnoreCase("ModifyErrors") && selectedBatchId == 0) {
                    Object[] options = {"Yes",
                            "No"};
                    int response = JOptionPane.showOptionDialog(this,
                            // "You have choosed the " + processType + " on the current screen." + "\n Your options are: " + "\n                 \"Accept\" to accept your changes and continue, " + "\n   \"Cancel\" to continue editing the data on this screen.",
                            "All Changes will be accounted as " + "\n " + processType + " errors . Please Confirm",
                            "Warning",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE,
                            null,
                            options,
                            options[0]);
                    if (response == JOptionPane.NO_OPTION) {//sub process is not conformed
                        okButton.setEnabled(true);
                        return;

                    } else if (response == JOptionPane.YES_OPTION) {//sub rrocess is conformed
                        okButton.setEnabled(false);
                    }
                    whichStatus = whichStatus + "-" + processType;
                    
                }
                //To show the selected sub process
                else if (whichStatus.equalsIgnoreCase("ModifyErrors") && sub_process > 0) {

                    if (sub_process != 0) {
                        if (sub_process == LISTING) {
                            processType = "Listing";
                        } else if (sub_process == TALLY) {
                            processType = "Tally";
                        } else if (sub_process == QA) {
                            processType = "QA";
                        }
                    }
                    whichStatus = whichStatus + "-" + processType;
                }
                
             //opens the ModifyErrors Batch with selected sub process   
            final int edit_process = editprocess;
            final String process_Type = processType;
            final ClientTask task = new TaskOpenBatch(selectedBatchId, selectedProjectId, whichStatus);
            
            task.setCallback(new Runnable() {
                public void run() {
                    Element reply = (Element) task.getResult();
                    String action = reply.getNodeName();
                    SplitPaneViewer viewer = SplitPaneViewer.getInstance();

                    if (T_BATCH_OPENED.equals(action)) {
                        // get data for opened batch// TBD - should probably continue at ClientTask level
                        int batchId = Integer.parseInt(reply.getAttribute(A_BATCH_ID));
                        //insert /update sub process

                        if (batchId != 0 && whichStatus.equals("ModifyErrors-"+process_Type)) {
                            
                            String[] param1 = {Integer.toString(edit_process), Integer.toString(batchId)};
                            String[] param2 = {process_Type, Integer.toString(batchId)};
                            if (null != process_Type) {
                                //insert/update the editChecking process
                                if (sub_process == 0) {                                                    
                                    final ClientTask taskInsert = new TaskExecuteUpdate("insert batchEditChecking", param2);
                                    taskInsert.enqueue();
                                } else {                                                    
                                    final ClientTask taskInsert = new TaskExecuteUpdate("update batchEditChecking", param2);
                                    taskInsert.enqueue();
                                }
                                //Update the batch table sub_process Column , which shows the sub process 
                                if(edit_process != 0){                                              
                                    final ClientTask taskUpdate = new TaskExecuteUpdate("update modify_sub_process", param1);
                                    taskUpdate.enqueue();
                                }
                            }
                        }
                        int projectId = Integer.parseInt(reply.getAttribute(A_PROJECT_ID));
                        int activeGroup = Integer.parseInt(reply.getAttribute(A_GROUP));
                        String project = reply.getAttribute(A_PROJECT_NAME);
                        String splitDocuments = reply.getAttribute(A_SPLIT_DOCUMENTS);
                        Log.print("ready to open b=" + batchId + " p=" + project);
                        viewer.setParent(parent);
                        viewer.setBatchId(batchId);
                        viewer.setActiveGroup(activeGroup);
                        viewer.initializeForProject(project, projectId,
                                whichStatus, splitDocuments);
                        // Open the viewer.  This instance of the viewer becomes
                       // the main window, for error pop-ups
                        viewer.setVisible(true);

                        Global.mainWindow = viewer;

                        // close the dialog without showing the parent
                       // TBD: this is strange coding - worry about restructuring it
                        setVisible(false);
                        dispose();
                    } else if (T_FAIL.equals(action)) {
                        // TBD: How do we tell them of problem opening batch
                        // This gives them a message box, doesn't open viewer
                        JOptionPane.showMessageDialog(ProjectSelectionDialog.this,
                                "Can't open selected batch",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        Log.quit("ProjectSelectionDialog:" + " unexpected message type: " + action);
                  }
               }
            });
            task.enqueue(this);
        }
    }
    dispose();
        } catch (Exception th) {
            Logger.getLogger(ProjectSelectionDialog.class.getName()).log(Level.SEVERE, null, th);
            Log.quit(th);
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void openCodingManual(String filePath){
        int index = 0;
        String fileName = "";
        URLConnection conn = null;
        InputStream in = null;
        OutputStream out = null;
        String response = null;
        try {
            JFileChooser saveFile = new JFileChooser();
            saveFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int i = saveFile.showSaveDialog(ProjectSelectionDialog.this);
            if (i == JFileChooser.APPROVE_OPTION) {
                response = saveFile.getSelectedFile().getPath();
            } else {
                return;
            }
            if (filePath.startsWith("http")) {
                index = filePath.lastIndexOf("/");
                fileName = filePath.substring(++index);
                URL url = new URL(filePath);
                conn = url.openConnection();
                in = conn.getInputStream();
                actualPath = response + File.separator + fileName;
                out = new BufferedOutputStream(new FileOutputStream(actualPath));
            } else {
                index = filePath.lastIndexOf(File.separator);
                fileName = filePath.substring(++index);
                in = new FileInputStream(new File(filePath));
                actualPath = response + File.separator + fileName;
                out = new FileOutputStream(new File(actualPath));
            }
            byte[] buffer = new byte[1024];
            int numRead;
            long numWritten = 0;
            while ((numRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, numRead);
                numWritten += numRead;
            }
            in.close();
            out.close();
            JOptionPane.showMessageDialog(ProjectSelectionDialog.this, "Coding manual has been saved  successfully.");

            String[] commands = null;
            String startCmd = null;
//            String osName = System.getProperty("os.name" );
//            if(osName.equals("Linux")){
//                startCmd = "xdg-open";
//            }else{  //Windows                
//                if (fileName.contains(".doc")) {
//                    startCmd = msWordPath;
//                } else if (fileName.contains(".pdf")) {
//                    startCmd = acrobatReaderPath;
//                } else if (fileName.contains(".txt")) {
//                    startCmd = notepad;
//                }
//            }
            String fileExtension = fileName.substring(fileName.lastIndexOf(".")+1);
             JFileChooser openWithDialog = new JFileChooser();
            openWithDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
            openWithDialog.setDialogTitle("Open the ' "+ fileExtension +" ' file with...");
            int get_choosed_value = openWithDialog.showOpenDialog(this);
            if(get_choosed_value == JFileChooser.APPROVE_OPTION){
                startCmd = openWithDialog.getSelectedFile().getPath();
            }else{
                File deleteFile = new File(actualPath);
                deleteFile.delete();
                return;                
            }
            
            commands = new String[]{startCmd, actualPath};
            try{
                process = Runtime.getRuntime().exec(commands);
            }catch(Exception ex){
                 JOptionPane.showMessageDialog(projectSelectionPanel, "Unable to open the file with the selected application.\nPlease give the path of corresponding executable file.");
                 File deleteFile = new File(actualPath);
                 deleteFile.delete();
                 return;
            }
            Date startDate = new Date();
            start_time = startDate.getTime();
            Thread t = new Thread() {
                public void run() {
                    try {
                        InputStreamReader isr = new InputStreamReader(process.getErrorStream());
                        BufferedReader br = new BufferedReader(isr);
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            System.out.println("---------------------------------" + line);
                        }
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                    Date endDate = new Date();
                    end_time = endDate.getTime();
                    insertCodingManual(start_time, end_time);
                    File deleteFile = new File(actualPath);
                    deleteFile.delete();
                }
            };
            t.start();
            process.waitFor();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(projectSelectionPanel, "Error occured while reading the coding manual.");
            e.printStackTrace();
        }
    }
    
    //shows the  process options for the sub process 
    private String selectProcessType(){       
       String values[]={"--Select Sub Process--","Listing","Tally","QA"};
       String display ="";
       if(sub_process !=0){            
           if(sub_process == LISTING){
            display = "Listing";            
           }else if(sub_process == TALLY){
            display = "Tally";
           }else if(sub_process == QA){
            display = "QA";
           }        
       }else{            
        display= "--Select Sub Process--";
       }
       String input = (String)JOptionPane.showInputDialog(
                      this,
                      "Sub Process: ",
                      "Sub Process Selection:", JOptionPane.PLAIN_MESSAGE,
                      null, values,
                      display);                                
       return input;
    }
    
   private void insertCodingManual(long start_time, long end_time)
   {
      final ClientTask taskTracking;
      taskTracking = new TaskCodingManualTracking(selectedProjectId, start_time, end_time);
      taskTracking.setCallback(new Runnable()
              {

                 public void run()
                 {
                    String result = (String) taskTracking.getResult();
                 }

              });
      taskTracking.enqueue(this);
   }
   /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
       setVisible(false);
       dispose();
       parent.setVisible(true);
    }//GEN-LAST:event_closeDialog

   /**
     * @param args the command line arguments
     */
   public static void main(String args[])
   {
      new ProjectSelectionDialog(new JFrame(), "dummy").setVisible(true);
   }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel activitySelectionTitle;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel leftGlue;
    private javax.swing.JTextArea messageArea;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel projectSelectionPanel;
    private javax.swing.JScrollPane projectSelectionScroller;
    private javax.swing.JTable projectSelectionTable;
    private javax.swing.JPanel rightGlue;
    // End of variables declaration//GEN-END:variables

   // A task to handle server interaction for this dialog

   private class ProjectSelectionTask extends ClientTask
   {

      public void run() throws IOException
      {

         try {
            ResultSet rs1 = null;
            try {
               rs1 = Sql.executeQuery(scon, this, "get assigned batch", whichStatus);
            } catch (ClassNotFoundException ex) {
               ex.printStackTrace();
            }
            final int batchId = (rs1.next() ? rs1.getInt(1) : 0);            
            final String batchName = (batchId != 0 ? rs1.getString(2) : null);
            final String projectName = (batchId != 0 ? rs1.getString(3) : null);
            final int batch_sub_process = (batchId != 0 ? rs1.getInt(4) : 0);
            if(batch_sub_process >0){
            sub_process = batch_sub_process;
            }else{
            sub_process=0;
            }            
              String display ="";
               if(sub_process !=0){                       
                   if(sub_process == LISTING){
                    display = "Listing";            
                   }else if(sub_process == TALLY){
                    display = "Tally";
                   }else if(sub_process == QA){
                    display = "QA";
                   }  
                   
               }
              final String sub_process = display;
            // show user assigned batch status; enable OK if batch exists
            SwingUtilities.invokeLater(new Runnable()
                    {
                       public void run()
                       {
                          if (batchId != 0) {
                              if(!sub_process.isEmpty()){                                  
                                 messageArea.setText("Click 'OK' to open assigned batch:" + "\n    " + projectName + " Batch " + batchName +"   " +whichStatus + "-" + sub_process);   
                               }else{
                                 messageArea.setText("Click 'OK' to open assigned batch:" + "\n    " + projectName + " Batch " + batchName);
                               }
                             
                             selectedBatchId = batchId;
                             okButton.setEnabled(true);
                          }
                          else {
                             messageArea.setText("To open a new batch," + " select project and click 'OK'.");
                          }
                       }

                    });

            // obtain list of available projects and batches
                // can't do it with single query (until MySQL has subqueries)
                // Note: we do it even if there is an assigned batch, to provide
                // information to the user on the avaliable backlog.

            MessageWriter writer;
            writer = scon.startMessage(T_REQUEST_PROJECT_LIST);
            writer.writeAttribute(A_STATUS, whichStatus);
            writer.endElement();
            writer.close();

            Element reply = scon.receiveMessage();

            final ResultSet rs2 = Sql.resultFromXML(reply);
            if (T_RESULT_SET.equals(reply.getNodeName())) {
               synchronized (this) {
               // force cache flush for rs2
               }
            }
            else if (T_FAIL.equals(reply.getNodeName())) {
               Log.quit("Sql.ProjectSelectionDialog: SQL error: " + reply);
            }
            else {
               Log.quit("ProjectSelectionDialog: unexpected message type: " + reply.getNodeName());
            }

            // show available batches
                // (if there is no assigned batch, OK will not be enabled
                //  until user makes a selection)
            SwingUtilities.invokeLater(new Runnable()
                    {

                       public void run()
                       {
                          try {
                             TableModel model = new ResultSetTableModel(rs2,
                                     new String[]{"Project", "# Batches"});
                             projectSelectionTable.setModel(model);
                             // It seems to lose the column widths when providing new table model
                            // So, we set the width again
                             projectSelectionTable.getColumnModel().getColumn(COUNT_COLUMN).setMaxWidth(COUNT_WIDTH);

                             if (batchId == 0) {
                                // Check for no available batches
                                for (int i = model.getRowCount() - 1; i >= 0; i--) {
                                   if (!"0".equals(model.getValueAt(i, COUNT_COLUMN))) {
                                      // there's an available batch
                                      return;
                                   }
                                }
                                // didn't find an available batch, tell user
                                messageArea.setText("You have no batches available");
                             }

                          } catch (Throwable th) {
                             Log.quit(th);
                          }
                       }

                    });
         } catch (SQLException e) {
            Log.quit(e);
         }

      }

   }

   // A task to handle server interaction for this dialog

   private class ProjectSelectionTaskForCodingManual extends ClientTask
   {

      public void run() throws IOException
      {

         try {
            MessageWriter writer;
            writer = scon.startMessage(T_REQUEST_PROJECT_LIST);
            writer.writeAttribute(A_STATUS, whichStatus);
            writer.endElement();
            writer.close();
            Element reply = scon.receiveMessage();

            final ResultSet rs2 = Sql.resultFromXML(reply);
            if (T_RESULT_SET.equals(reply.getNodeName())) {
               synchronized (this) {
               // force cache flush for rs2
               }
            }
            else if (T_FAIL.equals(reply.getNodeName())) {
               Log.quit("Sql.ProjectSelectionDialog: SQL error: " + reply);
            }
            else {
               Log.quit("ProjectSelectionDialog: unexpected message type: " + reply.getNodeName());
            }
            // show available batches
                // (if there is no assigned batch, OK will not be enabled
                //  until user makes a selection)
            SwingUtilities.invokeLater(new Runnable()
                    {
                       public void run()
                       {
                          try {
                             TableModel model = new ResultSetTableModel(rs2,
                                     new String[]{"Project", "# Active"});
                             projectSelectionTable.setModel(model);
                             // It seems to lose the column widths when providing new table model
                            // So, we set the width again
                             projectSelectionTable.getColumnModel().getColumn(COUNT_COLUMN).setMaxWidth(COUNT_WIDTH);
                          } catch (Throwable th) {
                             Log.quit(th);
                          }
                       }

                    });
         } catch (Exception e) {
            Log.quit(e);
         }

      }

   }

   // Custom renderer for table cells.
    // Disables cells of unselectable rows, so that they are dimmed
    // Right justifies count

   private class ObjectRenderer extends DefaultTableCellRenderer
   {

      public Component getTableCellRendererComponent(JTable table,
              Object value, boolean isSelected, boolean hasFocus,
              int row, int column)
      {
         JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
         boolean enable = (selectedBatchId == 0) && (!"0".equals(projectSelectionTable.getModel().getValueAt(row, COUNT_COLUMN)));
         result.setEnabled(enable);
         result.setHorizontalAlignment(column == COUNT_COLUMN ? JLabel.RIGHT : JLabel.LEFT);
         return result;
      }

   }

   // Listener for table row selection
    // Disallows selection of unselectable rows
    // Enables OK when a row has been selected

   private class TableListener implements ListSelectionListener
   {

      public void valueChanged(javax.swing.event.ListSelectionEvent evt)
      {
         try {
            ListSelectionModel selectionModel = projectSelectionTable.getSelectionModel();
            int selected = selectionModel.getMinSelectionIndex();
            if (selected >= 0) {
               if (selectedBatchId != 0 || "0".equals(projectSelectionTable.getValueAt(selected, COUNT_COLUMN))) {
                  // Undo selection of disallowed row
                  projectSelectionTable.removeRowSelectionInterval(selected, selected);
                  if (selectedBatchId == 0) {
                     // disable OK, in case a row was previously selected
                     okButton.setEnabled(false);
                  }
               }
               else {
                  // a row is selected
                  okButton.setEnabled(true);
               }
            }
         } catch (Throwable th) {
            Log.quit(th);
         }
      }

   }

   /**
     * Make this dialog visible.  Overrides JDialog.setVisible to
     * record the current visible window for use in dialogs.
     */
   public void setVisible(boolean flag)
   {
      if (flag) {
         Global.mainWindow = this;
      }
      else if (Global.mainWindow == this) {
         Global.mainWindow = null;
      }
      super.setVisible(flag);
   }

}
