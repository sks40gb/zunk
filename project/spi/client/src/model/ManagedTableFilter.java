/* $Header: /home/common/cvsarea/ibase/dia/src/model/ManagedTableFilter.java,v 1.5 2004/03/27 20:27:01 weaston Exp $ */
package model;

import common.DynamicArrays;
import common.Log;

//import javax.swing.table.TableModel;
//import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.util.Arrays;

/**
 * A filter to select a subset of the rows of a ManagedTableMap.
 */
public abstract class ManagedTableFilter extends ManagedTableMap {

    // An array containing the selected children
    int[] indirect = null;

    /**
     * Create a new ManagedTableFilter for a given model.
     */
    public ManagedTableFilter(ManagedTableModel model) {
        super();
        setModel(model);
    }
    

    /**
     * Determine if the given row is retained after filtering.
     */
    public abstract boolean accept(TableRow theRow);


    ////////// Overrides of ManagedTableMap methods

    /**
     * Get the id of this row.  Note that rows in the table model
     * correspond exactly to rows of a particular table.
     */
    public int getRowId(int row) {
        return model.getRowId(indirect[row]);
    }

    /**
     * Return the row at the given index.
     * (Used by ManagedNode)
     */
    public TableRow getRowAt(int index) {
        return model.getRowAt(indirect[index]);
    }

    ////////// Overrides of TableMap methods

    public int getRowCount() {
        return (indirect == null ? 0 : indirect.length);
    }

    public Object getValueAt(int aRow, int aColumn) {
        assert (getRowCount() == 0 & indirect==null || getRowCount() == indirect.length);
        return model.getValueAt(indirect[aRow], aColumn);
    }

    public void setValueAt(Object aValue, int aRow, int aColumn) {
        throw new UnsupportedOperationException();
    }

    public boolean isCellEditable(int row, int column) {
         return model.isCellEditable(indirect[row], column);
    }

    //////////

    // On table changed, handle updating the indirect array
    // Do not call super.tableChanged, we fire our own events
    // TBD: This code is identical to ManagedTableSorter.
    // Maybe it should be moved to ManagedTableMap or an 
    // intervening common class.
    public void tableChanged(TableModelEvent evt) {
        try {
            //Log.print("ManagedTableFilter.tableChanged: "
            //          +evt.getFirstRow()+"..."+evt.getLastRow()
            //          +" "+evt.getType());
            int type = evt.getType();
            int rowIndex = evt.getLastRow();
            if (rowIndex == TableModelEvent.HEADER_ROW) {  // structure changed
                initialize();
                fireTableStructureChanged();
            } else if (getRowCount() == 0
            || rowIndex == Integer.MAX_VALUE) {        // data changed
                // It's a brand new model
                initialize();
                fireTableDataChanged();
            } else {
                // It's a change to an existing model, must be only one row.
                assert rowIndex == evt.getFirstRow();
                switch (evt.getType()) {
                case TableModelEvent.DELETE:
                    doDelete(rowIndex);
                    break;
                case TableModelEvent.UPDATE:
                    doUpdate(rowIndex);
                    break;
                case TableModelEvent.INSERT:
                    doInsert(rowIndex);
                    break;
                }
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    /** Add rows from the initial underlying model to this model. */
    protected void initialize() {
        if (model == null || model.getRowCount() == 0) {
            indirect = null;
            return;
        }

        // The array to be filled
        indirect = new int[model.getRowCount()];

        int j = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (accept(model.getRowAt(i))) {
                indirect[j] = i;
                j++;
            }
        }
        indirect = DynamicArrays.truncate(indirect, j);
    }

    
    private void doDelete(int modelIndex) {
        if (indirect != null) {
            int oldIndex = Arrays.binarySearch(indirect, modelIndex);
            // decrement values greater than the deleted value
            for (int i = indirect.length - 1; i >= 0 && indirect[i] > modelIndex; i--)
            {
                indirect[i]--;
            }
            // remove the old indirect, if any
            if (oldIndex >= 0) {
                indirect = DynamicArrays.remove(indirect, oldIndex);
                fireTableRowsDeleted(oldIndex, oldIndex);
            }
        }
    }
    
    private void doUpdate(int modelIndex) {
        int oldIndex
                = (indirect == null ? -1 : Arrays.binarySearch(indirect, modelIndex));
        if (accept(model.getRowAt(modelIndex))) {
            if (oldIndex >= 0) {
                fireTableRowsUpdated(oldIndex, oldIndex);
            } else {
                oldIndex = (- oldIndex - 1);
                indirect = DynamicArrays.insert(indirect, oldIndex, modelIndex);
                fireTableRowsInserted(oldIndex, oldIndex);
            }
        } else {
            if (oldIndex >= 0) {
                indirect = DynamicArrays.remove(indirect, oldIndex);
                fireTableRowsDeleted(oldIndex, oldIndex);
            }
        }
    }
    
    private void doInsert(int modelIndex) {
        int oldIndex = Arrays.binarySearch(indirect, modelIndex);
        int position = (oldIndex < 0 ? - oldIndex - 1 : oldIndex);
        // increment values greater than or equal to the inserted value
        for (int i = indirect.length - 1; i >= position; i--) {
            indirect[i]++;
        }
        if (accept(model.getRowAt(modelIndex))) {
            indirect = DynamicArrays.insert(indirect, position, modelIndex);
            fireTableRowsInserted(position, position);
        }
    }
}
