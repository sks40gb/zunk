/* $Header: /home/common/cvsarea/ibase/dia/src/ui/QAVolumeSelectionDialog.java,v 1.11.2.3 2006/03/28 17:02:05 nancy Exp $ */
/*
 * QAVolumeSelectionDialog.java
 *
 * Created on November 3, 2003, 12:17 PM
 */

package ui;

import common.Log;
import common.msg.MessageConstants;
import client.ClientTask;
import client.Global;
import client.ServerConnection;
import client.Sql;
import client.TaskExecuteQuery;
import client.TaskExecuteUpdate;
import client.TaskOpenQAVolume;
import client.TaskQAGroup;
import model.ResultSetTableModel;

import java.awt.Component;
import java.awt.Font;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import org.w3c.dom.Element;

/**
 * Present a dialog to the QA'er showing the volumes that are
 * available for QA.  If the volume already has batches in QA, the
 * volume name is shown in bold font.
 * @author  Bill
 * @see client.TaskOpenQAVolume
 */
public class QAVolumeSelectionDialog extends javax.swing.JDialog implements MessageConstants {

    final private static int PROJECT_NAME_COLUMN = 0;
    final private static int VOLUME_NAME_COLUMN = 1;
    final private static int CODING_COUNT_COLUMN = 2;
    final private static int READY_COUNT_COLUMN = 3;
    final private static int QA_COUNT_COLUMN = 4;
    final private static int COMPLETE_COUNT_COLUMN = 5;
    final private static int VOLUME_ID_COLUMN = 6;  // volume_id col. (hidden)
    final private static int PROJECT_ID_COLUMN = 7;  // project_id col. (hidden)
    final private static int COUNT_WIDTH = 80;  // preferred width of count column
    final private static String QA_SAMPLING_TYPE_ISO = "ISO 2859-1";
    final private static String QA_SAMPLING_TYPE_FIXED = "Fixed Percentage";
    final private static String QA_SAMPLING_CHARACTER = "Character Sampling";

    final private ServerConnection scon = Global.theServerConnection;
    private int selectedVolumeId = 0;
    private int selectedProjectId = 0;
    // The client task to handle interaction with the server for this dialog
    private ClientTask task;
    private JFrame parent;
    private String projectName = null;
    private String splitDocuments = null;
    private int activeGroup;
    private String samplingField = null;
      
    /** 
     * Creates new form QAVolumeSelectionDialog.
     * @param parent The ActivitySelectionFrame which called this dialog
     */
    public QAVolumeSelectionDialog(JFrame parent) {
        super(parent, true);
        this.parent = parent;
        initComponents();
        
        // set okButton to respond to enter key
        // (doesn't work, but ctrl-Enter works ??)
        this.getRootPane().setDefaultButton(okButton);

        // Render cells bold if volume has batches in QA
        // Also, count columns are right justified
        ObjectRenderer renderer = new ObjectRenderer();
        volumeSelectionTable.setDefaultRenderer(Object.class, renderer);
        setCountColumnWidths();
        checkEnableControls();

        ListSelectionModel selectionModel = volumeSelectionTable.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); 
        selectionModel.addListSelectionListener(new TableListener());

        // Start a task to handle populating tablef
        task = new QAVolumeSelectionTask();
        task.enqueue(this);
    }


    private void setCountColumnWidths() {
        volumeSelectionTable.getColumnModel().getColumn(CODING_COUNT_COLUMN)
                .setMaxWidth(COUNT_WIDTH);
        volumeSelectionTable.getColumnModel().getColumn(READY_COUNT_COLUMN)
                .setMaxWidth(COUNT_WIDTH);
        volumeSelectionTable.getColumnModel().getColumn(QA_COUNT_COLUMN)
                .setMaxWidth(COUNT_WIDTH);
        volumeSelectionTable.getColumnModel().getColumn(COMPLETE_COUNT_COLUMN)
                .setMaxWidth(COUNT_WIDTH);
    }

   
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        volumeSelectionPanel = new javax.swing.JPanel();
        activitySelectionTitle = new javax.swing.JLabel();
        leftGlue = new javax.swing.JPanel();
        messageArea = new javax.swing.JTextArea();
        rightGlue = new javax.swing.JPanel();
        volumeSelectionScroller = new javax.swing.JScrollPane();
        volumeSelectionTable = new javax.swing.JTable();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
		tSearchButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Open Volume for Quality Assurance");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        volumeSelectionPanel.setLayout(new java.awt.GridBagLayout());

        volumeSelectionPanel.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(40, 40, 40, 40)), new javax.swing.border.CompoundBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED), new javax.swing.border.EmptyBorder(new java.awt.Insets(40, 40, 40, 40)))));
        volumeSelectionPanel.setMinimumSize(new java.awt.Dimension(800, 500));
        volumeSelectionPanel.setPreferredSize(new java.awt.Dimension(800, 500));
        activitySelectionTitle.setBackground(new java.awt.Color(204, 204, 204));
        activitySelectionTitle.setFont(new java.awt.Font("Dialog", 1, 24));
        activitySelectionTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        activitySelectionTitle.setText("Open Volume for QA");
        activitySelectionTitle.setAlignmentX(0.5F);
        activitySelectionTitle.setMaximumSize(new java.awt.Dimension(299, 60));
        activitySelectionTitle.setMinimumSize(new java.awt.Dimension(400, 60));
        activitySelectionTitle.setPreferredSize(new java.awt.Dimension(400, 60));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = -119;
        gridBagConstraints.ipady = -17;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        volumeSelectionPanel.add(activitySelectionTitle, gridBagConstraints);

        leftGlue.setDoubleBuffered(false);
        leftGlue.setPreferredSize(new java.awt.Dimension(0, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.5;
        volumeSelectionPanel.add(leftGlue, gridBagConstraints);

        // null inherits background from parent
        messageArea.setBackground(null);
        messageArea.setColumns(50);
        messageArea.setEditable(false);
        messageArea.setRows(2);
        messageArea.setText("Select volume and click 'OK'");
        messageArea.setWrapStyleWord(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        volumeSelectionPanel.add(messageArea, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.5;
        volumeSelectionPanel.add(rightGlue, gridBagConstraints);

        volumeSelectionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Project", "Volume", "# Not Ready", "# Ready", "# In QA", "# Complete"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        volumeSelectionScroller.setViewportView(volumeSelectionTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        volumeSelectionPanel.add(volumeSelectionScroller, gridBagConstraints);

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

		tSearchButton.setText("TSearch");
        tSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tSearchButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(tSearchButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(30, 0, 0, 0);
        volumeSelectionPanel.add(buttonPanel, gridBagConstraints);

        getContentPane().add(volumeSelectionPanel, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        try {
            // Add your handling code here:
            Log.print("QAVolumeSelectionDialog: Cancel pressed");
            closeDialog(null);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }//GEN-LAST:event_cancelButtonActionPerformed

   private void tSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
        try {
            // Add your handling code here:
            Log.print("QAVolumeSelectionDialog: TSearch pressed");
            setVisible(false);
            
            TSearchDownloadDialog importDialog = new TSearchDownloadDialog(this,true);
            importDialog.setLocationRelativeTo(null);
            importDialog.setVisible(true);
            
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        try {

            int selectedIndex = volumeSelectionTable.getSelectedRow();
            assert selectedIndex >= 0;
            TableModel model = volumeSelectionTable.getModel();
            final int selectedvolumeId
                    = Integer.parseInt((String) model
                                   .getValueAt(selectedIndex, VOLUME_ID_COLUMN));
            this.selectedProjectId
                    = Integer.parseInt((String) model
                                   .getValueAt(selectedIndex, PROJECT_ID_COLUMN));
            final boolean isNewVolume = "0".equals(model
                                   .getValueAt(selectedIndex, QA_COUNT_COLUMN));
            Log.print("QAVolumeSelectionDialog: OK pressed: "+ selectedVolumeId);
            final String projectName = (String) model.getValueAt(selectedIndex, 0);
            final String volumeName = (String) model.getValueAt(selectedIndex, 1);

            final ClientTask task = new TaskOpenQAVolume(selectedvolumeId);

            task.setCallback(
                new Runnable() {
                    public void run() {
                        Element reply = (Element) task.getResult();
                        String action = reply.getNodeName();

                        if (T_BATCH_OPENED.equals(action)) {
                            String projectName = reply.getAttribute(A_PROJECT_NAME);
                            String splitDocuments = reply.getAttribute(A_SPLIT_DOCUMENTS);
                            int activeGroup = 0;
                            if (isNewVolume) {      
                                    String samplingType = selectSamplingType();
                                 if(samplingType != null && samplingType.equalsIgnoreCase("ISO 2859-1")){
                                    doISOSampling(projectName, selectedvolumeId, splitDocuments, activeGroup);
                                 }else if(samplingType != null && samplingType.equalsIgnoreCase("Fixed Percentage")){
                                    doSampleQA(projectName, selectedvolumeId, splitDocuments, activeGroup);
                                 }else if(samplingType != null && samplingType.equalsIgnoreCase(QA_SAMPLING_CHARACTER)){
                                     System.out.println("Character Sampling");
                                    doCharacterSampling(projectName, volumeName, samplingType,selectedProjectId, selectedvolumeId);
                                 }else{
                                       ClientTask task = new TaskExecuteUpdate("SplitPaneViewer.closeMenuItem");
                                       task.enqueue();
                                 }
                                Log.print("back from doSampleQA");
                            } else {                                
                                openViewerForQA(projectName, selectedvolumeId, splitDocuments, activeGroup);
                                Log.print("back from openViewerForQA");
                            }
                        } else if (T_FAIL.equals(action)) {
                            // TBD: How do we tell them of problem opening batch
                            // This gives them a message box, doesn't open viewer
                            JOptionPane.showMessageDialog(QAVolumeSelectionDialog.this,
                                                          "Can't open selected volume",
                                                          "Error",
                                                          JOptionPane.ERROR_MESSAGE);
                        } else {
                            Log.quit("ProjectSelectionDialog:"
                                     +" unexpected message type: "+action);
                        }
                    }
                });
            task.enqueue(this);
        } catch (Throwable th) {
            Log.quit(th);
        }   
    }//GEN-LAST:event_okButtonActionPerformed
    //--------choose sampling type either Percentage or ISO 2859-1 -------
    private String selectSamplingType(){
          String values[]={QA_SAMPLING_TYPE_ISO,QA_SAMPLING_TYPE_FIXED,QA_SAMPLING_CHARACTER};
          String input = (String)JOptionPane.showInputDialog(
                         this,
                         "Select the Sampling Type : ",
                         "SAMPLING_TYPE", JOptionPane.INFORMATION_MESSAGE,
                         null, values,
                         QA_SAMPLING_TYPE_ISO);                   
         return input;
      }
    
   //------------------for ISOSampling ------------------------
    private void doISOSampling(String projectName, int selectedVolumeId
                            , String splitDocuments, int activeGroup){
       try{
             int selectedIndex = volumeSelectionTable.getSelectedRow();
             assert selectedIndex >= 0;
             TableModel model = volumeSelectionTable.getModel();
             final int selectedvolumeId
                          = Integer.parseInt((String) model
                                         .getValueAt(selectedIndex, VOLUME_ID_COLUMN));
             final int selectedprojectid =  Integer.parseInt((String) model
                                   .getValueAt(selectedIndex, PROJECT_ID_COLUMN));
             this.projectName = projectName;
             this.selectedVolumeId = selectedVolumeId;
             this.splitDocuments = splitDocuments;
             this.activeGroup = activeGroup;
             this.setVisible(false);             
             ProjectParameterWindow ppw = new ProjectParameterWindow(this, selectedvolumeId, selectedprojectid);             
             ppw.setVisible(true);
             
           }catch(Exception e){
             e.printStackTrace();
          }
    }
    
    private void doCharacterSampling(String projectName, String volumeName, String samplingType,
            int projectId, int volumeId){
        
        final String pName = projectName;
        final String vName = volumeName;
        final String sType = samplingType;
        final JDialog jdialog = this;
        final int vId = volumeId;
        final int pId = projectId;
        
        System.out.println("volumeId: "+volumeId);
        
        final TaskExecuteQuery taskExecuteQuery = new TaskExecuteQuery("getQALevel",""+volumeId);
        taskExecuteQuery.setCallback(new Runnable()
        {
            public void run()
            {
             try {  
                    System.out.println("Task Executed"+taskExecuteQuery.getResult());
                     String qaLevel = "";
                     int qa_level_value  = 1;
                     ResultSet resultSet = (ResultSet)taskExecuteQuery.getResult();
                     if(null != resultSet && resultSet.next()) {
                          qaLevel = resultSet.getString(1);
                          qa_level_value = Integer.parseInt(qaLevel.substring(2)) + 1; 
                     }
                     qaLevel = "QA"+qa_level_value;                     
                     new CharSamplingParameterWindow(jdialog,pName, vName, sType, qaLevel,pId,vId,parent);
                 } catch (Exception ex) {
                   ex.printStackTrace();                        
                }

               }
        });
        taskExecuteQuery.enqueue(this);
        
        
    }
    //----------for ISO Sampling ---------------------

    public void checkCount(int count,String coders, String samplingField){
             this.samplingField = samplingField;
             openViewerForQA(projectName, selectedVolumeId, coders, samplingField);
           
    }
    
    private void doSampleQA(String projectName, int selectedVolumeId
                            , String splitDocuments, int activeGroup) {
        QASampleDialog dialog = new QASampleDialog(this, this, selectedVolumeId, parent);
        dialog.setVisible(true);
        //int count = dialog.getDocumentCount();
        //System.out.println("back from QA dialog:"+count);
        // If sample found, set up a viewer
        // If no sample, close the volume and stay on this dialog
//        Log.print("doSampleQA: count="+count);
//        if (count > 0) {
//            openViewerForQA(projectName, selectedVolumeId, splitDocuments, activeGroup);
//        } else {
//            ClientTask task = new TaskExecuteUpdate("SplitPaneViewer.closeMenuItem");
//            task.enqueue();
//        }
    }

    
    private void openViewerForQA(String projectName, int selectedVolumeId, String coders, String fields) {
        
        
        final String pName = projectName;
        final String codersValue = coders;
        final String fieldValues = fields;
        final int volumeId = selectedVolumeId;
        final TaskQAGroup task = new TaskQAGroup(selectedVolumeId);   
        task.setCallback(new Runnable()
        {
            public void run()
            {
             try {
                    QAGroupAssignPage qa_groupviewer = new QAGroupAssignPage((Map)task.getResult(),pName,volumeId,codersValue,fieldValues,parent);
                    
                    qa_groupviewer.setVisible(true);
                    Global.mainWindow = qa_groupviewer;
                    setVisible(false);
                    dispose();

                 } catch (Exception ex) {
                   ex.printStackTrace();                        
                }

               }
        });
        task.enqueue(this);  
        
    }
    
     private void openViewerForQA(String projectName, int selectedVolumeId
                                 , String splitDocuments, int activeGroup) {
        SplitPaneViewer viewer = SplitPaneViewer.getInstance();
        viewer.setVolumeId(selectedVolumeId);
        viewer.setParent(parent);
        viewer.setActiveGroup(activeGroup);
        viewer.setSamplingFields(samplingField);
        viewer.initializeForProject(projectName, selectedProjectId, "QA", splitDocuments);
        // Open the viewer.  This instance of the viewer becomes
        // the main window, for error pop-ups
        viewer.setVisible(true);
        Global.mainWindow = viewer;
        // close the dialog without showing the parent
        // TBD: this is strange coding - worry about restructuring it
        setVisible(false);
        dispose();
    }
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        Log.print("qAVolumeSelectionDialog.closeDialog");
        setVisible(false);
        dispose();
        parent.setVisible(true);
    }//GEN-LAST:event_closeDialog
   
    ///**
    // * @param args the command line arguments
    // */
    //public static void main(String args[]) {
    //    new QAVolumeSelectionDialog(new JFrame()).setVisible(true);
    // }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel activitySelectionTitle;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel leftGlue;
    private javax.swing.JTextArea messageArea;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel rightGlue;
    private javax.swing.JPanel volumeSelectionPanel;
    private javax.swing.JScrollPane volumeSelectionScroller;
    private javax.swing.JTable volumeSelectionTable;
	private javax.swing.JButton tSearchButton;
    // End of variables declaration//GEN-END:variables
    
    // A task to handle server interaction for this dialog
    private class QAVolumeSelectionTask extends ClientTask {

        public void run() throws IOException {

            // select project_name, volume_name
            //           , sum(status in ('Coding', 'CodingQC'))
            //           , sum(status='QCComplete') as qc_count
            //           , sum(status='QA') as qa_count
            //           , sum(status='QAComplete') 
            //           , V.volume_id
            //  from batch B
            //    inner join volume V using(volume_id)
            //    inner join project P using(project_id)
            //    left join session S on (S.volume_id = V.volume_id and S.batch_id = 0)
            //  where S.volume_id is null
            //  group by volume_id
            //  having qc_count > 0 or qa_count > 0
            final ResultSet rs1 = Sql.executeQuery(scon, this, "QAVolumeSelectionDialog.volumes");

            // show available volumes
            // (OK will not be enabled until user makes a selection)
            SwingUtilities.invokeLater(new Runnable () {
                    public void run() {
                        try {

                            // get column names from painted table model
                            TableModel model = volumeSelectionTable.getModel();
                            int columnCount = model.getColumnCount();
                            String[] columnNames = new String[columnCount];
                            for (int i=0; i < columnCount; i++) {
                                columnNames[i] = model.getColumnName(i);
                            }

                            model = new ResultSetTableModel(rs1, columnNames);
                            volumeSelectionTable.setModel(model);
                            // It loses the column widths when providing new
                            // table model, so we set the width again
                            setCountColumnWidths();

                            if (model.getRowCount() == 0) {
                                messageArea.setText("No volumes available for QA");
                            }

                        } catch (Throwable th) {
                            Log.quit(th);
                        }
                    }
                });
        }
    }

    // Custom renderer for table cells.
    // Disables cells of unselectable rows, so that they are dimmed
    // Right justifies count
    private class ObjectRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus,
                int row, int column)
        {
            JLabel result = (JLabel) super.getTableCellRendererComponent(
                    table,value,isSelected,hasFocus,row,column);
            // Make it bold if there are already batches in QA
            result.setFont(result.getFont().deriveFont(
                    "0".equals(volumeSelectionTable.getModel()
                                     .getValueAt(row,QA_COUNT_COLUMN))
                    ? Font.PLAIN
                    : Font.BOLD));
            result.setHorizontalAlignment(
                    column >= CODING_COUNT_COLUMN && column <= COMPLETE_COUNT_COLUMN
                    ? JLabel.RIGHT
                    : JLabel.LEFT);
            return result;
        }
    }

    // Listener for table row selection
    // Enables OK when a row has been selected
    private class TableListener implements ListSelectionListener {
        public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
            checkEnableControls();
        }
    }

    /**
     * Make this dialog visible.  Overrides JDialog.setVisible to
     * record the current visible window for use in dialogs.
     */
    public void setVisible(boolean flag) {
        if (flag) {
            Global.mainWindow = this;
        } else if (Global.mainWindow == this) {
            Global.mainWindow = null;
        }
        super.setVisible(flag);
    }

    private void checkEnableControls() {
        try {
            int selectedIndex = volumeSelectionTable.getSelectedRow();
            
            if (selectedIndex >= 0) {
                int readyState = Integer.parseInt(volumeSelectionTable.getModel().getValueAt(selectedIndex, 3).toString());
                if(readyState > 0) {
                    okButton.setEnabled(true);
                }else {
                    okButton.setEnabled(false);
                }
                
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }
}
