/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author sunil
 */
public class ConfirmTallyFieldModel extends AbstractTableModel {
    
    private Object[][] data;
    private boolean DEBUG = false;
    private String[] columnNames = {"Column1", "Column2"};

    public ConfirmTallyFieldModel(Object[][] data) {
        this.data = data;
    }
    
    public ConfirmTallyFieldModel(Object[][] data ,String headings[]) {
        this.data = data;
        this.columnNames = headings;
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    @Override
    public Class getColumnClass(int c) {        
        return getValueAt(0, c).getClass();
    }

    public void addRow(String type, String error) {
        Object tempArray[][] = new String[data.length + 1][2];
        for (int i = 0; i < data.length; i++) {
            tempArray[i][0] = data[i][0];
            tempArray[i][1] = data[i][1];
        }
        tempArray[tempArray.length - 1][0] = type; 
        tempArray[tempArray.length - 1][1] = error;

        this.data = tempArray;
    //this.data = tempArray;
    }

    public Object[][] getData(){
        return data;
    }
    /*
     * No need to implement this method unless your table's
     * data can change.
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (DEBUG) {
            System.out.println("Setting value at " + row + "," + col + " to " + value + " (an instance of " + value.getClass() + ")");
        }

        data[row][col] = value;
        fireTableCellUpdated(row, col);

        if (DEBUG) {
            System.out.println("New value of data:");
            printDebugData();
        }
    }

    private void printDebugData() {
        int numRows = getRowCount();
        int numCols = getColumnCount();

        for (int i = 0; i < numRows; i++) {
            System.out.print("    row " + i + ":");
            for (int j = 0; j < numCols; j++) {
                System.out.print("  " + data[i][j]);
            }
            System.out.println();
        }        
    }
    
    
    boolean[] canEdit = new boolean[]{
         false, false
    };
    

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return canEdit[columnIndex];
    }
    
    
}
