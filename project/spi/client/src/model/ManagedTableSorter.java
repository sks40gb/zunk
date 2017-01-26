/* $Header: /home/common/cvsarea/ibase/dia/src/model/ManagedTableSorter.java,v 1.10 2004/07/02 23:19:13 nancy Exp $ */
package model;

import common.Log;

//import javax.swing.table.TableModel;
//import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import java.util.Arrays;

/**
 * A filter to sort a ManagedTableMap using a given sequence of columns as keys.
 */
public class ManagedTableSorter extends ManagedTableMap {

    // The indexes of the sort columns.  First index is sorted first.
    // If all key columns are identical, sort on the rowId.
    int[] sortKeys;
// int key=0;
    // For n children, a permutation of 0..(n-1).  The i'th element
    // of this model is the indirect[i]'th element of the underlying model.
    int[] indirect = null;

    /**
     * Create a new ManagedTablesort with a one key and a given underlying model.
     */
    public ManagedTableSorter(int key1, ManagedTableModel model) {        
        this(new int[] {key1}, model);
        //this.key=key1;
       //System.out.println("key1 "+   key1);
    }

    /**
     * Create a new ManagedTablesort with a two keys and a given underlying model.
     */
    public ManagedTableSorter(int key1, int key2, ManagedTableModel model) {
        this(new int[] {key1, key2}, model);
    }

    /**
     * Create a new ManagedTablesort with given keys and underlying model.
     */
    public ManagedTableSorter(int[] sortKeys, ManagedTableModel model) {
        super();
        this.sortKeys = sortKeys;
       // System.out.println("sortKeys "+   sortKeys.length);
        setModel(model);
    }

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

    /** Add rows from the initial underlying model to this model. */
    protected void initialize() {
        if (model == null || model.getRowCount() == 0) {
            indirect = null;
            return;
        }
        // The array to be filled
        indirect = new int[model.getRowCount()];
       // System.out.println("indirect--------------------"+  indirect.length);
        // An array of Integer objects (so we can use Arrays.sort)
        Integer[] sortIndirect = new Integer[indirect.length];
        //System.out.println("sortIndirect legth------------->"+  sortIndirect.length);
        // Initialize with the identity permutation, 0..(n-1)
        for (int i = 0; i < sortIndirect.length; i++) {
            sortIndirect[i] = new Integer(i);
        }
        // Sort, using defined comparator
        Arrays.sort(sortIndirect, new java.util.Comparator() {
                public int compare(java.lang.Object A, java.lang.Object B) {
                    return compareRows(model.getRowAt(((Integer) A).intValue())
                                     , model.getRowAt(((Integer) B).intValue()));
                }
                public boolean equals(java.lang.Object A) {
                    return this==A;
                }
            });
        // Copy the Integer values to the indirect array
        for (int i = 0; i < indirect.length; i++) {
            indirect[i] = sortIndirect[i].intValue();
        }
    }

    ////////// Overrides of TableMap methods

    /**
     * Get the number of rows.
     * Used instead of model.getRowCount() because the indirect
     * array may contain one less element than the model while
     * a change to a model row causes a change in ordering, since
     * the latter is treated as a delete followed by an insert.
     */
    public int getRowCount() {
        return (indirect == null ? 0 : indirect.length);
    }

    public Object getValueAt(int aRow, int aColumn) {
        assert (getRowCount() == 0 & indirect==null || getRowCount() == indirect.length);
        try {
            return model.getValueAt(indirect[aRow], aColumn);
        } catch (Throwable th) {
            Log.quit(th);
            return null;
        }
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
    public void tableChanged(TableModelEvent evt) {
        try {
            //Log.print("ManagedTableSorter.tableChanged: "
            //          +evt.getFirstRow()+"..."+evt.getLastRow()
            //          +" "+evt.getType()+" sortKeys[0]="+sortKeys[0]);
            int type = evt.getType();
            int rowIndex = evt.getLastRow();
            if (rowIndex == TableModelEvent.HEADER_ROW) {  // structure changed
                initialize();
                fireTableStructureChanged();
            } else if (indirect == null
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

    /**
     * Compare two TableRow's by column.  Nulls in columns are sorted first.
     * @param left The first row to compare.
     * @param left The second row to compare.
     * @return -1 if left < right, 0 if left = right, +1 if left > right
     */
    private int compareRows (Object leftObj, Object rightObj) {
        TableRow left = (TableRow) leftObj;
        TableRow right = (TableRow) rightObj;
        if (left == right) {
            return 0;
        }
       // System.out.println("sortKeys-------------------------->"+   sortKeys.length);
        for (int i = 0; i < sortKeys.length; i++) {
            Object leftValue = left.getValue(sortKeys[i]);
            Object rightValue = right.getValue(sortKeys[i]);
            if (leftValue == rightValue) {
                continue;
            }

            // Most likely case - compare String's ignoring case
            if (leftValue instanceof String && rightValue instanceof String) {
                int comparison1 = ((String) leftValue)
                                 .compareToIgnoreCase((String) rightValue);
                if (comparison1 != 0) {
                    return comparison1;
                }
                // else compare again, so upper case comes first
            }

            // nulls sort first
            if (left == null) {
                return -1;
            }
            if (right == null) {
                return 1;
            }

            // now compare objects
            int comparison2 = ((Comparable) leftValue).compareTo(rightValue);
            if (comparison2 != 0) {
                return comparison2;
            }
        }

        // All columns are identical, use id as tiebreaker
        // id's are assumed to be different for different rows
        return (left.getId() < right.getId() ? -1 : 1);
    } 
    
    private void doDelete(int modelIndex) {
        int oldIndex = findIndex(modelIndex);
        if (model.getRowCount() == 0) {
            // Model became empty, make indirect array null
            assert oldIndex == 0;
            indirect = null;
        } else {
            // Model did not become empty
            // create a new array for the insertion
            int[] newIndirect = new int[indirect.length - 1];
            // copy, decrementing values after the modelIndex and skipping modelIndex
            int newIndex = 0;
            for (int i = 0; i < indirect.length; i++) {
                if (indirect[i] != modelIndex) {
                    newIndirect[newIndex] = indirect[i];
                    if (newIndirect[newIndex] > modelIndex) {
                        newIndirect[newIndex]--;
                    }
                    newIndex++;
                }
            }
            // replace the indirect array
            indirect = newIndirect;
        }
        fireTableRowsDeleted(oldIndex, oldIndex);
    }
    
    private void doUpdate(int modelIndex) {
        int oldIndex = findIndex(modelIndex);
        Object rowValue = model.getRowAt(modelIndex);
        if ((oldIndex > 0 && compareRows(getRowAt(oldIndex - 1), rowValue) > 0)
        || (oldIndex < (indirect.length - 1) && compareRows(rowValue, getRowAt(oldIndex + 1)) > 0)) {
            // Position changed.  Treat as a delete and an insert
            doDelete(modelIndex);
            doInsert(modelIndex);
        } else {
            // Position not changed, just fire change (so select not modified)
            fireTableRowsUpdated(oldIndex, oldIndex);
        }
    }
    
    private void doInsert(int modelIndex) {
        int oldIndex = findIndex(modelIndex);
        // create a new array for the insertion
        // Note: newIndirect[indirect.length] not set yet
        int[] newIndirect = new int[indirect.length + 1];
        // copy, incrementing values after the modelIndex
        for (int i = 0; i < indirect.length; i++) {
            newIndirect[i] = indirect[i];
            if (newIndirect[i] >= modelIndex) {
                newIndirect[i]++;
            }
        }
        // replace the indirect array
        indirect = newIndirect;

        // move elements up to make place for new element
        Object rowValue = model.getRowAt(modelIndex);
        int newIndex = indirect.length - 1;
        while (newIndex > 0 && compareRows(rowValue, getRowAt(newIndex - 1)) < 0) {
            newIndex--;
            indirect[newIndex + 1] = indirect[newIndex];
        }
        //Log.print("insert modelIndex="+modelIndex+" oldIndex="+oldIndex+" newIndex="+newIndex);
        // insert the new element
        indirect[newIndex] = modelIndex;
        fireTableRowsInserted(newIndex, newIndex);
    }

    private int findIndex(int modelIndex) {
        if (modelIndex == indirect.length) {
            return indirect.length;
        }
        for (int i = indirect.length - 1; i >= 0; i--) {
            if (modelIndex == indirect[i]) {
                return i;
            }
        }
        Log.quit("ManagedTableSorter: Can't find index");
        return 0;
    }
}
