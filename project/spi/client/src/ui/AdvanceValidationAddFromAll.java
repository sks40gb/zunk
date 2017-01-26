/*
 * AdvanceValidationAddAll.java
 *
 * Created on 30 June, 2008, 5:18 PM
 */
package ui;

import client.ClientTask;
import client.Global;
import client.TaskAddValidationData;
import client.TaskViewAdvanceValidations;
import com.lexpar.util.Log;
import common.FieldValidationData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.table.TableColumn;

/**
 * Component which adds selected validation functions from the list of all script functions.
 *
 * @author  sunil
 */
public class AdvanceValidationAddFromAll extends javax.swing.JFrame {

    private final int SELECT_ALL = -2;    
    private java.sql.ResultSet results = null;
    private AdvanceValidationPage parent;
    private List<List> rowList;
    private static final String LOCK = "LOCK";   
    
    public AdvanceValidationAddFromAll(AdvanceValidationPage parent) {
        initComponents();
        this.parent = parent;
        Global.mainWindow.setEnabled(false);
        getAllValidations();
    }
    
    /**
     * Method used to Get All Validations
     */
    public void getAllValidations() {
        final ClientTask task;
        task = new TaskViewAdvanceValidations(SELECT_ALL);
        task.setCallback(new Runnable() {
            public void run() {
                results = (java.sql.ResultSet) task.getResult();
                if (results != null) {
                     displayData();
                } else {
                    Log.print("??? Fieldvalues: null resultset returned");
                }
            }
        });
        boolean ok = task.enqueue(this);
    }

        
    /*
     * display the records of the selected fields
     */
    private void displayData() {
        Object[][] data = null;
        rowList = new ArrayList<List>();  
        // check whether function already added in the list or not.
        // If it is already added then igone the record else add to the list.
        Set<String> functionExitsSet = new HashSet<String>();
        String fName = null;
        String desc = null;
        String error = null;
        String fBody = null;        
        
        //get the record from the resultset and put in the list.
        try {
            boolean isExist = false;
            while (results.next()) {
                fName = results.getString(1);
                desc = results.getString(2);
                error = results.getString(3);
                fBody = results.getString(4);
                //isExist is true if the value is added to the list else false.
                isExist = functionExitsSet.add(fName);
                if (isExist) {
                    List columnList = new ArrayList();
                    columnList.add(new Boolean(false));
                    columnList.add(fName);
                    columnList.add(desc);
                    columnList.add(error);
                    columnList.add(fBody);
                    rowList.add(columnList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        data = new Object[rowList.size()][];
        
        for(int i = 0; i< rowList.size(); i++){                        
            data[i] = rowList.get(i).toArray();              
        }
        
        allValidationTable.setModel(new javax.swing.table.DefaultTableModel(data,
            new String [] {
                "", "Function Name", "Description", "Error Message"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        
        
        TableColumn column = null;
        column = allValidationTable.getColumnModel().getColumn(0);
        column.setPreferredWidth(10); // Check Box

        column = allValidationTable.getColumnModel().getColumn(1);
        column.setPreferredWidth(100); // function name

        column = allValidationTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(150); // description

        column = allValidationTable.getColumnModel().getColumn(3);
        column.setPreferredWidth(150); // error message

    }
    
    private int saveCount = 0;
    private boolean saveAllRecord() {
        int projectId = parent.getProjectId();
        int fieldId = parent.getFieldId();
        List<FieldValidationData> fieldValidationDataList = new ArrayList<FieldValidationData>();
        
        for (int i = 0; i < allValidationTable.getModel().getRowCount(); i++) {
            //if the function row is selected            
            if (Boolean.parseBoolean(allValidationTable.getModel().getValueAt(i, 0).toString())) {
                FieldValidationData fieldValidationData = new FieldValidationData();
                fieldValidationData.functionName = (String) allValidationTable.getModel().getValueAt(i, 1);
                fieldValidationData.description = (String) allValidationTable.getModel().getValueAt(i, 2);
                fieldValidationData.status = "True";
                fieldValidationData.errorMessage = (String) allValidationTable.getModel().getValueAt(i, 3);
                fieldValidationData.userInput = "";              
                fieldValidationData.methodBody = rowList.get(i).get(4).toString();
                fieldValidationData.fieldId = fieldId;
                fieldValidationData.projectId = projectId;
                //add to the list 
                fieldValidationDataList.add(fieldValidationData);
            }
        }
       
        String fName;
        if ((fName = functionExists(fieldValidationDataList)) != null) {
            JOptionPane.showMessageDialog(null,"Function " + fName + " already exists.");
            return false;
        }

        int count = 2;
        for (FieldValidationData fieldValidationData : fieldValidationDataList) {            
            final ClientTask task;
            try {
                task = new TaskAddValidationData(fieldValidationData);
                task.enqueue(this);
                count++;                  
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        parent.refreshPage();
        return true;
    } 
    
    private String functionExists(List <FieldValidationData> fieldValidationDataList){   
        for(FieldValidationData vdata : fieldValidationDataList){
            for(String name : parent.getFunctionsNameList()){               
                if(vdata.functionName.equals(name)){
                    return name;
                }
            }
        }
        return null;
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
jScrollPane1 = new javax.swing.JScrollPane();
allValidationTable = new javax.swing.JTable();
jPanel2 = new javax.swing.JPanel();
backButton = new javax.swing.JButton();
addButton = new javax.swing.JButton();

setTitle("SPiCA All Validations");
addWindowListener(new java.awt.event.WindowAdapter() {
public void windowClosing(java.awt.event.WindowEvent evt) {
formWindowClosing(evt);
}
});

jPanel1.setBorder(null);
jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

allValidationTable.setModel(new javax.swing.table.DefaultTableModel(
	new Object [][] {
		
	},
	new String [] {
		"", "Function Name", "Description", "Error Message"
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
jScrollPane1.setViewportView(allValidationTable);

jPanel1.add(jScrollPane1);

jPanel2.setBorder(null);
jPanel2.setLayout(new java.awt.GridBagLayout());

backButton.setText("Back");
backButton.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
backButtonActionPerformed(evt);
}
});
gridBagConstraints = new java.awt.GridBagConstraints();
gridBagConstraints.ipadx = 40;
gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 25);
jPanel2.add(backButton, gridBagConstraints);

addButton.setText("Add");
addButton.addActionListener(new java.awt.event.ActionListener() {
public void actionPerformed(java.awt.event.ActionEvent evt) {
addButtonActionPerformed(evt);
}
});
gridBagConstraints = new java.awt.GridBagConstraints();
gridBagConstraints.gridx = 6;
gridBagConstraints.gridy = 0;
gridBagConstraints.ipadx = 40;
gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
jPanel2.add(addButton, gridBagConstraints);

javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
getContentPane().setLayout(layout);
layout.setHorizontalGroup(
layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
.addContainerGap()
.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
.addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 777, Short.MAX_VALUE)
.addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 777, Short.MAX_VALUE))
.addContainerGap())
);
layout.setVerticalGroup(
layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
.addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
.addContainerGap()
.addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 439, Short.MAX_VALUE)
.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
.addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
.addContainerGap())
);

pack();
}// </editor-fold>//GEN-END:initComponents

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    Global.mainWindow.setEnabled(true);
    dispose();
    //parent.setEnabled(true);        
}//GEN-LAST:event_formWindowClosing

private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
saveAllRecord();
}//GEN-LAST:event_addButtonActionPerformed

private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backButtonActionPerformed
Global.mainWindow.setEnabled(true);
dispose();
}//GEN-LAST:event_backButtonActionPerformed



// Variables declaration - do not modify//GEN-BEGIN:variables
private javax.swing.JButton addButton;
private javax.swing.JTable allValidationTable;
private javax.swing.JButton backButton;
private javax.swing.JPanel jPanel1;
private javax.swing.JPanel jPanel2;
private javax.swing.JScrollPane jScrollPane1;
// End of variables declaration//GEN-END:variables
}
