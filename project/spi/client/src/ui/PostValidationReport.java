/*
 * PostValidationReport.java
 *
 * Created on 23 June, 2008, 3:28 PM
 */
package ui;

import client.ClientTask;
import client.TaskRequestPostValidationReport;
import common.CommonProperties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;
import model.QueryComboModel;
import model.ResultSetTableModel;
import model.SQLManagedComboModel;

/**
 * Component to Display DTYG Field where user can Modify the DTYG Field
 *
 * @author  sunil
 */
public class PostValidationReport extends javax.swing.JFrame {
    
    private static final String COMPLETED = "COMPLETED";
    private static final String GET_ALL_PROJECTS = "AdvanceValidation.projectCombo";
    private static final String GET_ALL_VOLUMES = "listing.get volume";
    private static final int GET_BY_PROJECT = 0;
    private static final int GET_BY_VOLUME = 1;
    private static final int GET_ALL = -1;
    private int projectId;
    private int volumeId;
    private String projectName;
    private String volumeName;
    private String fileName;
    PostValidationPage parent = null;
    java.sql.ResultSet results = null;
    private QueryComboModel volumeModel = null;
    private SQLManagedComboModel projectModel = null;
    private ResultSetTableModel model;

    /** Creates new form PostValidationReport */
    public PostValidationReport(PostValidationPage parent) {
        initComponents();
        downloadReportButton.setEnabled(false);
        this.parent = parent;
        loadProjects();
        loadReport(GET_ALL);       
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        downloadReportButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        projectComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        volumeComboBox = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        reportTable = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Post Validation Reports");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        downloadReportButton.setText("Download Report");
        downloadReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadReportButtonActionPerformed(evt);
            }
        });
        jPanel1.add(downloadReportButton, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 8, 3);
        getContentPane().add(jPanel1, gridBagConstraints);

        jPanel2.setBorder(null);
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));

        jPanel4.setBorder(null);

        jLabel1.setFont(new java.awt.Font("DejaVu Sans", 1, 12));
        jLabel1.setText("Select Project");

        projectComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectComboBoxActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("DejaVu Sans", 1, 12));
        jLabel2.setText("Select Volume");

        volumeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                volumeComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(projectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 176, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(volumeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(volumeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(projectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(20, 20, 20))
        );

        jPanel2.add(jPanel4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        getContentPane().add(jPanel2, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel3.setPreferredSize(new java.awt.Dimension(800, 450));
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));

        reportTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Post Validation Id", "Project Name", "Volume Name", "Status", "User Name", "Run Date", "File Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        reportTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                reportTableMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(reportTable);

        jPanel3.add(jScrollPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 20;
        gridBagConstraints.weightx = 500.0;
        gridBagConstraints.weighty = 500.0;
        getContentPane().add(jPanel3, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    this.dispose();
    parent.setReportHistoryButtonEnabled(true);
}//GEN-LAST:event_formWindowClosing

    private void loadProjects() {
        if (projectModel == null) {
            projectModel = new SQLManagedComboModel(GET_ALL_PROJECTS);
            projectModel.register();
            projectComboBox.setModel(projectModel);
        }
    }

    /**
     * Load the Post validation Report 
     * Use some filters like 
     * ProjectId
     * VolumeId
     * @param loadBy -- filter (project or Volume)
     */
    private void loadReport(int loadBy) {
        final ClientTask task;
        switch (loadBy) {
            case 0:
                //get record by project Id
                task = new TaskRequestPostValidationReport(projectId, -1);
                break;
            case 1:
                // get record by volume Id
                task = new TaskRequestPostValidationReport(projectId, volumeId);
                break;
            default:
                // get all record
                task = new TaskRequestPostValidationReport(-1, -1);
        }
        task.setCallback(new Runnable() {

            public void run() {
                results = (java.sql.ResultSet) task.getResult();
                if (results != null) {
                    loadReport();
                }
            }
        });
        boolean ok = task.enqueue(this);
    }

    /*
     * display the records of the selected fields
     */
    private void loadReport() {
        model = new ResultSetTableModel(results, new String[]{
                    "PVR Id", "Project Name", "Volume Name", "Status", "User Name", "Run Date", "File Name"
                });
        reportTable.setModel(model);
        TableColumn column = null;
        column = reportTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(30); // PVR Id

        column = reportTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(80); // Project Name

        column = reportTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(70); // Volume Name

        column = reportTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(50); // Status

        column = reportTable.getColumnModel().getColumn(4);
        column.setPreferredWidth(30); // User Id

        column = reportTable.getColumnModel().getColumn(5);
        column.setPreferredWidth(70); // Run Date

        column = reportTable.getColumnModel().getColumn(6);
        column.setPreferredWidth(160); // File Name

    }

private void downloadReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadReportButtonActionPerformed
    int index = reportTable.getSelectedRow();
    if (index > -1) {
        projectName = (String) reportTable.getValueAt(index, 1);
        volumeName = (String) reportTable.getValueAt(index, 2);
        fileName = (String) reportTable.getValueAt(index, 6);
        proceedToDownload();
    //enable download button        
    } else {
        projectName = null;
        volumeName = null;
        fileName = null;
        downloadReportButton.setEnabled(false);
    }
}//GEN-LAST:event_downloadReportButtonActionPerformed

private void reportTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_reportTableMousePressed
    int index = reportTable.getSelectedRow();
    if (index > -1 && reportTable.getValueAt(index, 3) != null && reportTable.getValueAt(index, 3).toString().trim().equals(COMPLETED)) {
        downloadReportButton.setEnabled(true);
    } else {
        downloadReportButton.setEnabled(false);
    }
}//GEN-LAST:event_reportTableMousePressed

private void projectComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectComboBoxActionPerformed
    int sel = projectComboBox.getSelectedIndex();
    if (sel > -1) {
        projectId = ((SQLManagedComboModel) projectComboBox.getModel()).getIdAt(sel);
        volumeModel = new QueryComboModel(GET_ALL_VOLUMES, /* required? */ false, new String[]{Integer.toString(projectId)}, "");
        volumeComboBox.setModel(volumeModel);
        loadReport(GET_BY_PROJECT);
    }
}//GEN-LAST:event_projectComboBoxActionPerformed

private void volumeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_volumeComboBoxActionPerformed
    int sel = volumeComboBox.getSelectedIndex();
    if (sel > -1) {
        volumeId = ((QueryComboModel) volumeComboBox.getModel()).getIdAt(sel);
        loadReport(GET_BY_VOLUME);
    }
}//GEN-LAST:event_volumeComboBoxActionPerformed

    public void proceedToDownload() {
        JFileChooser chooseFile = new JFileChooser();
        chooseFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooseFile.showOpenDialog(jPanel1);
        String localDir = "";
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            java.io.File file = chooseFile.getSelectedFile();
            localDir = file.getPath();
            downloadReportFile(localDir);
        }
    }

    /**
     * Get the connection for the url and get the input stream of file ane write
     * to the local system. Path specified by the user. 
     * @param localDir - local directoty where the file is to be written.
     */
    private void downloadReportFile(String localDir) {
        try {
            
            //get the Url connection
            java.net.URL url = new URL(CommonProperties.getUploadFolderPath() +                    
                    projectName + File.separator +
                    volumeName + File.separator +
                    fileName);

            URLConnection conn = url.openConnection();
            InputStream in = conn.getInputStream();

            //file name with path where to write the file.
            String absoluteFileName = localDir + File.separator + fileName;

            File f = new File(absoluteFileName);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
            }
            OutputStream out = new FileOutputStream(absoluteFileName);
            byte[] buf = new byte[1024];
            int len = 0;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            // Close the streams
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(null, "Downloading completed", "Downloading Completed", JOptionPane.INFORMATION_MESSAGE);
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton downloadReportButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JComboBox projectComboBox;
    private javax.swing.JTable reportTable;
    private javax.swing.JComboBox volumeComboBox;
    // End of variables declaration//GEN-END:variables
}
