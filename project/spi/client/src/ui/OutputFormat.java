/*
 * OutputValidation.java
 *
 * Created on 29 September, 2008, 12:08 PM
 */
package ui;

import ui.FieldFormat;
import model.OutputFormatModel;
import valueobjects.FieldFormatData;
import beans.InputTextDialog;
import client.ClientTask;
import client.TaskRequestOutputFormat;
import client.TaskSaveOutputFormat;
import java.awt.Color;
import java.awt.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import ui.ExportPage;

/**
 * Component for Arranging the sequence, suprressing and creating new OVP Field.
 *
 * @author  sunil
 */
public class OutputFormat extends javax.swing.JFrame {

    enum move {

        UP, DOWN
    };
    private Component parent;
    private int volumeId;

    /** Creates new form OutputValidation */
    public OutputFormat(Component parent, int volumeId) {
        this.parent = parent;
        this.volumeId = volumeId;
        initComponents();
        fieldFormatTable.setSelectionMode(0);
        fieldFormatTable.getTableHeader().setReorderingAllowed(false);
        if (parent instanceof ExportPage) {
            ((ExportPage) parent).setEnableFormatButton(false);
        }
        initValue();
        setLocationRelativeTo(parent);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
private void initComponents() {//GEN-BEGIN:initComponents

jScrollPane1 = new javax.swing.JScrollPane();
fieldFormatTable = new javax.swing.JTable();
upButton = new javax.swing.JButton();
downButton = new javax.swing.JButton();
jPanel1 = new javax.swing.JPanel();
formatButton = new javax.swing.JButton();
suppressButton = new javax.swing.JButton();
formatButton1 = new javax.swing.JButton();
deleteFieldButton = new javax.swing.JButton();
formatButton2 = new javax.swing.JButton();
closeButton = new javax.swing.JButton();

setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
setTitle("Output Format");
setFocusCycleRoot(false);
setResizable(false);
addWindowListener(new java.awt.event.WindowAdapter() {
public void windowClosing(java.awt.event.WindowEvent evt) {
formWindowClosing(evt);
}
});

fieldFormatTable.setModel(new javax.swing.table.DefaultTableModel(
	new Object [][] {
		
	},
	new String [] {
		"No.", "Name", "Separator"
	}
) {
	boolean[] canEdit = new boolean [] {
		false, false, false
	};

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return canEdit [columnIndex];
	}
});
fieldFormatTable.addMouseListener(new java.awt.event.MouseAdapter() {
public void mousePressed(java.awt.event.MouseEvent evt) {
fieldFormatTableMousePressed(evt);
}
});
jScrollPane1.setViewportView(fieldFormatTable);

upButton.setText("^");
upButton.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
upButtonActionPerformed(evt);
}
});

downButton.setText("v");
downButton.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
downButtonActionPerformed(evt);
}
});

jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

formatButton.setText("Format");
formatButton.setEnabled(false);
formatButton.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
formatButtonActionPerformed(evt);
}
});
jPanel1.add(formatButton);

suppressButton.setText("Suppress");
suppressButton.setEnabled(false);
suppressButton.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
suppressButtonActionPerformed(evt);
}
});
jPanel1.add(suppressButton);

formatButton1.setText("Create New Field");
formatButton1.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
formatButton1ActionPerformed(evt);
}
});
jPanel1.add(formatButton1);

deleteFieldButton.setText(" Delete Field ");
deleteFieldButton.setEnabled(false);
deleteFieldButton.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
deleteFieldButtonActionPerformed(evt);
}
});
jPanel1.add(deleteFieldButton);

formatButton2.setText("Save");
formatButton2.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
formatButton2ActionPerformed(evt);
}
});
jPanel1.add(formatButton2);

closeButton.setText("Close");
closeButton.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
closeButtonActionPerformed(evt);
}
});
jPanel1.add(closeButton);

javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
getContentPane().setLayout(layout);
layout.setHorizontalGroup(
layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(layout.createSequentialGroup()
.addGap(50, 50, 50)
.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
.addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
.addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE))
.addGap(12, 12, 12)
.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
.addComponent(downButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
.addComponent(upButton))
.addContainerGap(21, Short.MAX_VALUE))
);
layout.setVerticalGroup(
layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(layout.createSequentialGroup()
.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(layout.createSequentialGroup()
.addGap(38, 38, 38)
.addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE))
.addGroup(layout.createSequentialGroup()
.addGap(117, 117, 117)
.addComponent(upButton)
.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
.addComponent(downButton)))
.addGap(18, 18, 18)
.addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
.addContainerGap(29, Short.MAX_VALUE))
);

pack();
}//GEN-END:initComponents

private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
    moveFieldRow(move.DOWN);
}//GEN-LAST:event_downButtonActionPerformed

private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
    moveFieldRow(move.UP);
    enableButtons();//GEN-LAST:event_upButtonActionPerformed
    }

private void fieldFormatTableMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_fieldFormatTableMousePressed
    enableButtons();
    enableButtons();//GEN-LAST:event_fieldFormatTableMousePressed
    }

    private void enableButtons() {
        int row = fieldFormatTable.getSelectedRow();
        //enable up down buttons
        if (fieldFormatTable.getRowCount() < 2) {
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        } else {
            if (row < 1) {
                upButton.setEnabled(false);
                downButton.setEnabled(true);
            } else if (row > (fieldFormatTable.getRowCount() - 2)) {
                upButton.setEnabled(true);
                downButton.setEnabled(false);
            } else {
                upButton.setEnabled(true);
                downButton.setEnabled(true);
            }
        }

        //enable Format, Suppress and Delete buttons
        if (row < 0) {
            formatButton.setEnabled(false);
            deleteFieldButton.setEnabled(false);
            suppressButton.setEnabled(false);
        } else {
            formatButton.setEnabled(true);
            suppressButton.setEnabled(true);
            if (list.get(row).getType().equals(FieldFormatData.OVP_FIELD)) {
                deleteFieldButton.setEnabled(true);
            } else {
                deleteFieldButton.setEnabled(false);
            }
        }
    }

private void suppressButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_suppressButtonActionPerformed
    int index = fieldFormatTable.getSelectedRow();
    if (index < 0) {
        return;
    }
    list.get(index).setSuppress(!list.get(index).isSuppress());
    updateTable();
    enableButtons();
}//GEN-LAST:event_suppressButtonActionPerformed

private void formatButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formatButton1ActionPerformed
    InputTextDialog inputText = new InputTextDialog(this);
    inputText.setVisible(true);
    enableButtons();
}//GEN-LAST:event_formatButton1ActionPerformed

private void formatButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formatButtonActionPerformed
    int selectedRow = fieldFormatTable.getSelectedRow();
    if (selectedRow < 0) {
        formatButton.setEnabled(false);
    } else {
        new FieldFormat(this, selectedRow).setVisible(true);
    }
    enableButtons();
}//GEN-LAST:event_formatButtonActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    dispose();
    if (parent instanceof ExportPage) {
        ((ExportPage) parent).setEnableFormatButton(true);
    }
    parent.setEnabled(true);
}//GEN-LAST:event_formWindowClosing

private void deleteFieldButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteFieldButtonActionPerformed
    enableButtons();
    list.remove(fieldFormatTable.getSelectedRow());
    updateTable();
}//GEN-LAST:event_deleteFieldButtonActionPerformed

private void formatButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_formatButton2ActionPerformed

    try {
        ClientTask task = new TaskSaveOutputFormat(list);
        task.enqueue(this);
    } catch (Exception e) {
        e.printStackTrace();
    }

    JOptionPane.showMessageDialog(this, "Record has been saved successfully.");
}//GEN-LAST:event_formatButton2ActionPerformed

private void closeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeButtonActionPerformed
    dispose();
    if (parent instanceof ExportPage) {
        ((ExportPage) parent).setEnableFormatButton(true);
    }
    parent.setEnabled(true);
}//GEN-LAST:event_closeButtonActionPerformed

    private void initValue() {

        final ClientTask task;
        task = new TaskRequestOutputFormat(volumeId);
        task.setCallback(new Runnable() {

            public void run() {
                ResultSet rs = (java.sql.ResultSet) task.getResult();
                initCollectionAndModel(rs);
            }
        });
        boolean ok = task.enqueue(this);
    }
    private ArrayList<FieldFormatData> list;
    OutputFormatModel model;

    private void initCollectionAndModel(ResultSet rs) {
        list = new ArrayList<FieldFormatData>();
        FieldFormatData.setVolumeId(volumeId);
        try {
            int seq = 1;
            while (rs.next()) {
                FieldFormatData data = new FieldFormatData(rs);
                data.setSequence(seq++);
                list.add(data);
            }

        } catch (SQLException ex) {
            Logger.getLogger(OutputFormat.class.getName()).log(Level.SEVERE, null, ex);
        }

        fieldFormatTable.setDefaultRenderer(Object.class, new MyTableCellRender(list));
        model = new OutputFormatModel(list);
        fieldFormatTable.setModel(model);
        fieldFormatTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox(FieldFormatData.getDeliminators())));
    }

    private void moveFieldRow(move m) {
        int selectedRowIndex = fieldFormatTable.getSelectedRow();
        if (selectedRowIndex == -1) {
            return;
        }
        if (m == move.UP) {
            changeSequence(selectedRowIndex, -1);
            Collections.swap(list, selectedRowIndex, --selectedRowIndex);
        } else if (m == move.DOWN) {
            changeSequence(selectedRowIndex, 1);
            Collections.swap(list, selectedRowIndex, ++selectedRowIndex);
        }
        updateTable();

    //  fieldFormatTable.setDropTarget(new JComboBox());
    }

    private void changeSequence(int selectedRowIndex, int delta) {

        int seq_1 = list.get(selectedRowIndex).getSequence();
        int seq_2 = list.get(selectedRowIndex + delta).getSequence();
        list.get(selectedRowIndex).setSequence(seq_2);
        list.get(selectedRowIndex + delta).setSequence(seq_1);
    }

    public void updateTable() {
        model = new OutputFormatModel(list);
        fieldFormatTable.setModel(model);
        fieldFormatTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox(FieldFormatData.getDeliminators())));
    }

    public List<FieldFormatData> getFieldList() {
        return list;
    }
// Variables declaration - do not modify//GEN-BEGIN:variables
private javax.swing.JButton closeButton;
private javax.swing.JButton deleteFieldButton;
private javax.swing.JButton downButton;
private javax.swing.JTable fieldFormatTable;
private javax.swing.JButton formatButton;
private javax.swing.JButton formatButton1;
private javax.swing.JButton formatButton2;
private javax.swing.JPanel jPanel1;
private javax.swing.JScrollPane jScrollPane1;
private javax.swing.JButton suppressButton;
private javax.swing.JButton upButton;
// End of variables declaration//GEN-END:variables
}

class MyTableCellRender extends DefaultTableCellRenderer {

    private List<FieldFormatData> list;

    public MyTableCellRender(List<FieldFormatData> list) {
        super();
        // setOpaque(true);
        this.list = list;
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        //Integer.parseInt((String) table.getValueAt(row, 0)) > 5
        if (list.get(row) != null && list.get(row).isSuppress()) {
            setBackground(new Color(57, 139, 17));
            setForeground(Color.WHITE);
        } else {
            setBackground(null);
            setForeground(null);
        }
        if (isSelected) {
            setForeground(Color.WHITE);
            setBackground(new Color(140, 160, 219));
        }
        setText(value != null ? value.toString() : "");
        return this;
    }
}

