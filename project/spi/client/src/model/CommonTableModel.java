package model;

/*
 * ErrorTable.java requires no other files.
 */
import javax.swing.table.AbstractTableModel;
/**
 *
 * @author sunil
 */

/** 
 * ErrorTable is just like SimpleErrorTable, except that it
 * uses a custom TableModel.
 */
public class CommonTableModel extends AbstractTableModel {

    private Object[][] data;
    private boolean DEBUG = false;
    private String[] columnNames = {"ERROR TYPE", "ERROR MESSAGE"};

    public CommonTableModel(Object[][] data, String columnNames[]) {
        this.data = data;
        this.columnNames = columnNames;
    }
    
    public CommonTableModel(Object[][] data) {
        this.data = data;
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.length;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

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

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
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
        System.out.println("--------------------------");
    }
}
