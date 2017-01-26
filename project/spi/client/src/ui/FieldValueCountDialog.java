/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ui;

import beans.TableSorter;
import beans.ToolTipText;
import client.ClientTask;
import client.TaskFieldValueCount;
import common.Log;
import common.msg.MessageConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import model.ResultSetTableModel;

/**
 * Dialog Window to Display Tally Admin
 * @author bmurali
 */
public class FieldValueCountDialog extends javax.swing.JDialog implements MessageConstants{
  private JFrame parent;
  private javax.swing.JTable fieldsTable;
  private javax.swing.JPanel fieldsPane;
  private javax.swing.JScrollPane fieldsScrollPane;
  private javax.swing.JButton closeButton;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel bottomPane;
  private String whichStatus;
  private int projectId;
  private int volumeId;
  private ResultSetTableModel valueModel;
  private TableSorter sorter;
  
  public FieldValueCountDialog(JFrame parent, String whichStatus,int projectId,int volumeId) {      
        super(parent, true);            
        this.parent = parent;
        this.whichStatus = whichStatus;     
        this.projectId = projectId;
        this.volumeId = volumeId;
       
         if("Tally".equals(whichStatus)){
             setTitle("Document/Field/Word/Character Count For Volume:");
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
                   // addButton.setEnabled(false);
                   // editButton.setEnabled(false);                    
                } else {
                    int row = fieldsTable.getSelectedRow();
                    Object obj =fieldsTable.getValueAt(row,0);
                    System.out.println("row-------->"+ row);
                    System.out.println("obj-------->"+ obj.toString());
                   // addButton.setEnabled(true);
                   // editButton.setEnabled(true);
                }
            }

            
        });
        tabselect();                 
    }
  
    private void initComponents() {
        fieldsPane = new javax.swing.JPanel();
        fieldsScrollPane = new javax.swing.JScrollPane();
        fieldsTable = new ProjectFieldsTableClass();
        closeButton = new javax.swing.JButton(); 
        jPanel1 = new javax.swing.JPanel();
        bottomPane = new javax.swing.JPanel();
        fieldsPane.setLayout(new java.awt.BorderLayout());
       
        fieldsPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fieldsPane.setPreferredSize(new java.awt.Dimension(600, 250));
        fieldsScrollPane.setPreferredSize(new java.awt.Dimension(600, 250));
        fieldsTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fieldsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
               
            new String [] {
                "Coder","Documents","Fields","Words","Characters"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                 false,false,false,false,false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        fieldsTable.setFocusable(false);
        fieldsTable.setIntercellSpacing(new java.awt.Dimension(2, 1));
        fieldsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
               // fieldsTableMouseClicked(evt);
            }
        });

        fieldsScrollPane.setViewportView(fieldsTable);
        fieldsPane.add(fieldsScrollPane, java.awt.BorderLayout.CENTER);
        add(fieldsPane, java.awt.BorderLayout.CENTER); 
        
        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 20, 0));
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
    }
    
    public void tabselect(){
        
        final ClientTask task;
            task = new TaskFieldValueCount(projectId, volumeId,whichStatus);
            task.setCallback(new Runnable() {
                        public void run() {                           
                            //Element reply = (Element) task.getResult();
                           // final ResultSet rs = Sql.resultFromXML(reply);                            
                            ResultSet results = (ResultSet) task.getResult();
                            if (results != null) {
                                loadFieldvalueEntry(results);
                            } else {
                                Log.print("??? Fieldvalues: null resultset returned");
                            }
                        }
                    });
            boolean ok = task.enqueue(this);
    }
    
    private void loadFieldvalueEntry(ResultSet rs) {

        valueModel = new ResultSetTableModel(rs, new String[]{"Coder","Documents","Fields","Words","Characters"});
        sorter = new TableSorter(valueModel);
        fieldsTable.setModel(sorter);
        fieldsTable.setPreferredSize(new Dimension(100, 150));
        sorter.setTableHeader(fieldsTable.getTableHeader());
        fieldsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

                    public void valueChanged(ListSelectionEvent e) {
                        //Ignore extra messages.
                        if (e.getValueIsAdjusting()) {
                            return;
                        }

                        ListSelectionModel lsm =
                                (ListSelectionModel) e.getSource();
                        if (lsm.isSelectionEmpty() || fieldsTable.getSelectedRow() < 0) {
                        //no rows are selected
                        } else {
                            int row = fieldsTable.getSelectedRow();

                            if (null != fieldsTable.getValueAt(row, 0)) {
                              //  fieldValueFromOccurrence = (String) fieldsTable.getValueAt(row, 0);
                               // System.out.println("fieldValueFromOccurrence----------->" + fieldValueFromOccurrence);
                            }
                        }
                    }
                    });

        TableColumn column;
        column = fieldsTable.getColumnModel().getColumn(0); // Coder
        column.setPreferredWidth(50);
        //column.setCellRenderer(centerCellRenderer);
        column = fieldsTable.getColumnModel().getColumn(1); // Documents
         column.setPreferredWidth(50);
         column = fieldsTable.getColumnModel().getColumn(2); // Fields
         column.setPreferredWidth(50);
         column = fieldsTable.getColumnModel().getColumn(3); // Words
         column.setPreferredWidth(50);
         column = fieldsTable.getColumnModel().getColumn(4); // Characters
         column.setPreferredWidth(50);
       // column.setCellRenderer(centerCellRenderer);

        enableComponents();
    }
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
        
          private void enableComponents() {
	if(fieldsTable.getRowCount() > 0) {
           // fontFamilyCombo.setEnabled(true);
           // fontSizeCombo.setEnabled(true);
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
     private void closeDialog(java.awt.event.WindowEvent evt) {                             
        setVisible(false);
        dispose();
        parent.setVisible(true);
    }  
      private void closeButtonActionPerformed(ActionEvent evt) {                                                           
        try {
            // Add your handling code here:        
            closeDialog(null);
        } catch (Throwable th) {
            Log.quit(th);      
    } 
    }
}
