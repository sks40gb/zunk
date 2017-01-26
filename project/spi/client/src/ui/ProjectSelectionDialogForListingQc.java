/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import beans.ToolTipText;
import client.ClientTask;
import client.Global;
import client.TaskOpenBatch;
import common.Log;
import common.msg.MessageConstants;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import model.ManagedTableModel;
import model.ManagedTableSorter;
import model.SQLManagedTableModel;
import org.w3c.dom.Element;


/**
 *
 * @author bmurali
 */
public class ProjectSelectionDialogForListingQc extends javax.swing.JDialog implements MessageConstants
{        private JFrame parent;   
         private javax.swing.JTable fieldsTable;
         private javax.swing.JButton okButton;
         private javax.swing.JButton cancelButton;
         private javax.swing.JPanel fieldsPane;        
         private javax.swing.JScrollPane fieldsScrollPane;
         private javax.swing.JPanel bottomPane;
         private javax.swing.JPanel jPanel1;
         private int userId;
         private String status;
         private int projectId;
         private String fieldName;
         private String projectName;
         private String volumeName;
         final private static int COUNT_WIDTH = 80;  // preferred width of count column
         final private static int COUNT_COLUMN = 1;  // column containing batch count
         
        public ProjectSelectionDialogForListingQc(JFrame parent,int  userId,String status) {      
        super(parent, true);    
        setTitle("Field Selection for ListingQC");
        this.parent = parent;
        this.userId = userId;
        this.status = status;
        initComponents();
		
        setLocationRelativeTo(parent);
        fieldsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //fieldsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);  
        //ObjectRenderer renderer = new ObjectRenderer();
        // fieldsTable.setDefaultRenderer(Object.class, renderer);
        fieldsTable.getColumnModel().getColumn(COUNT_COLUMN).setMaxWidth(COUNT_WIDTH);
        fieldsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) return;
                ListSelectionModel lsm =
                        (ListSelectionModel)e.getSource();
                if (lsm.isSelectionEmpty()) {
                    okButton.setEnabled(false);
                    cancelButton.setEnabled(false);                    
                } else {
                    int row = fieldsTable.getSelectedRow();
                    Object fValue =fieldsTable.getValueAt(row,2);
                    Object pName =fieldsTable.getValueAt(row,0);
                    Object vName =fieldsTable.getValueAt(row,1);
                     fieldName = fValue.toString();
                     projectName = pName.toString();
                     volumeName = vName.toString();
                    //System.out.println("row-------->"+ row);
                    //System.out.println("obj-------->"+ obj.toString());
                    okButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                }
            }

        });
        tabselect();     
    }


    private void initComponents() {
        fieldsPane = new javax.swing.JPanel();
        fieldsScrollPane = new javax.swing.JScrollPane();
        fieldsTable = new ProjectFieldsTableClass();
        bottomPane = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        okButton =new javax.swing.JButton();
        cancelButton =new javax.swing.JButton();
        
        fieldsPane.setLayout(new java.awt.BorderLayout());

        fieldsPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fieldsPane.setPreferredSize(new java.awt.Dimension(600, 250));
        fieldsScrollPane.setPreferredSize(new java.awt.Dimension(600, 250));
        fieldsTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fieldsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
               
            new String [] {
                "Project Name","Volume Name","Field Name","Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                 false,false,false,false
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

        add(fieldsPane, java.awt.BorderLayout.NORTH);
        
        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 20, 0));

        okButton.setText("  Ok  ");
        okButton.setEnabled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel1.add(okButton);        
        cancelButton.setText("  Cancel  ");
        cancelButton.setEnabled(true);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        jPanel1.add(cancelButton);
        bottomPane.add(jPanel1);
        add(bottomPane, java.awt.BorderLayout.CENTER);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        //setTitle("Project Selection");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
         pack();
    }

     private void tabselect() {
           SQLManagedTableModel projectModel = SQLManagedTableModel.makeInstance("listing_qc.get listingQc detail",
                                                      fieldsTable.getModel(),
                                                      userId);        
              System.out.println("projectModel-------->"+ projectModel.getColumnCount());
           // unsortedModel.setColumnClass(6, Integer.class);  // sequence
            ManagedTableModel model = new ManagedTableSorter(0, projectModel);                 
            fieldsTable.setModel(model);                             
            model.register();                 
            TableColumn column = null;
            column = fieldsTable.getColumnModel().getColumn(0);
            column.setPreferredWidth(110); // projectName     
            column = fieldsTable.getColumnModel().getColumn(1);
            column.setPreferredWidth(110); //volumeName
            column = fieldsTable.getColumnModel().getColumn(2);
            column.setPreferredWidth(110); //fieldName
            column = fieldsTable.getColumnModel().getColumn(3);
            column.setPreferredWidth(110); //status
    }
    
    private class ProjectFieldsTableClass extends JTable {
            public String getToolTipText(MouseEvent event) {
                return ToolTipText.getToolTipText(event, fieldsTable);
            }
            public Point getToolTipLocation(MouseEvent event) {
                return ToolTipText.getToolTipLocation(event, fieldsTable);
            }
        }
    
     private void closeDialog(java.awt.event.WindowEvent evt) {                             
        setVisible(false);
        dispose();
        parent.setVisible(true);
    }  
  
    private void fieldsTableMouseClicked(MouseEvent evt) {
         if (evt.getClickCount() >= 1) { 
                int selectedIndex = fieldsTable.getSelectedRow();
                System.out.println("selectedIndex-------->"+ selectedIndex);
                String field_name = (String) fieldsTable.getValueAt(selectedIndex, 0);
                String userName = (String) fieldsTable.getValueAt(selectedIndex, 1);
                String status = (String) fieldsTable.getValueAt(selectedIndex, 3);
                if("Complete".equals(status)){                    
                     okButton.setEnabled(false);
                }else{
                      okButton.setEnabled(true);
                }             
         }
    }    
    
    private void cancelButtonActionPerformed(ActionEvent evt) {
        try {
            // Add your handling code here:
            Log.print("ProjectSelectionDialogForListingQc: Cancel pressed");
            closeDialog(null);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void okButtonActionPerformed(ActionEvent evt) {
        okButton.setEnabled(false);
        try {
            final ClientTask task = new TaskOpenBatch(userId, "ListingQC",fieldName);
            task.setCallback(new Runnable() {
                public void run() {
                    Element reply = (Element) task.getResult();
                    String action = reply.getNodeName();
                    SplitPaneViewer viewer = SplitPaneViewer.getInstance();
                    if (T_BATCH_OPENED.equals(action)) {
                        // get data for opened batch// TBD - should probably continue at ClientTask level
                        int volumeId = Integer.parseInt(reply.getAttribute(A_VOLUME_ID));
                        int projectId = Integer.parseInt(reply.getAttribute(A_PROJECT_ID)); 
                        System.out.println("projectId-------->"+ projectId);
                        String splitDocuments = reply.getAttribute(A_SPLIT_DOCUMENTS);                        
                        viewer.setParent(parent);
                        viewer.setFieldName(fieldName);
                        viewer.setVolume(volumeName);
                        viewer.setProjectName(projectName);
                       // viewer.setProjectId(projectId);
                        viewer.setVolumeId(volumeId);                       
                        viewer.initializeForProject("", projectId,
                                "ListingQC", splitDocuments);
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
                        JOptionPane.showMessageDialog(ProjectSelectionDialogForListingQc.this,
                                "Can't open selected batch",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        Log.quit("ProjectSelectionDialogListingQC:" + " unexpected message type: " + action);
                    }
                }
            });
            task.enqueue(this);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }
    
//    private class ObjectRenderer extends DefaultTableCellRenderer
//   {
//
//      public Component getTableCellRendererComponent(JTable table,
//              Object value, boolean isSelected, boolean hasFocus,
//              int row, int column)
//      {
//         JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//         boolean enable = (fieldName.equals(null)) && (!"0".equals(fieldsTable.getModel().getValueAt(row, COUNT_COLUMN)));
//         result.setEnabled(enable);
//         result.setHorizontalAlignment(column == COUNT_COLUMN ? JLabel.RIGHT : JLabel.LEFT);
//         return result;
//      }
//
//   }
}
