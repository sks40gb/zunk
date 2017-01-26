/* $Header: /home/common/cvsarea/ibase/dia/src/model/ManagedTableGroup.java,v 1.8.4.1 2006/03/22 20:27:15 nancy Exp $ */
package model;

import common.Log;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

/**
 * A table model which contains either zero or one row, depending on
 * whether childModel has any rows.
 */
public class ManagedTableGroup extends AbstractTableModel
implements ManagedTableModel, TableModelListener {

    final private int         COLUMN_MAX_COUNT = 1;
    private TableRow          rowData;
    private boolean           isRegistered = false;
    private ManagedTableModel childModel;
    private boolean groupShowing = false;  // group has one or more children

    /**
     * Create a table model with the given parameters.
     * @param userObject the object the user will see in the table
     * @param childModel 
     */
    public ManagedTableGroup (Object userObject, ManagedTableModel childModel) {
        this.childModel = childModel;
        if (userObject instanceof TableRow) {
            rowData = (TableRow) userObject;
        } else {
            rowData = new TableRow(0, new Object[] { userObject });
        }
        initialize();
        childModel.addTableModelListener(this);
    }

    /**
     * Register this SQLManagedTableModel with the server.  If the model has
     * not already been registered, a task is started to load data
     * from the server.
     */
    public synchronized void register() {
        if (! isRegistered) {
            isRegistered = true;
            childModel.register();
        }
    }

    /**
     * Return the number of values in a row.  (May be 
     * greater than the number of columns when viewed
     * as a table model.)
     * @see #getColumnCount
     */
    public int getColumnMaxCount() {
        return COLUMN_MAX_COUNT;
    }

    /**
     * Get the id of this row.  Note that rows in the table model
     * correspond exactly to rows of a particular table.
     */
    public int getRowId(int row) {
        TableRow rowData = getRowAt(row);
        return rowData.getId();
    }

    // TableModel interface methods

    /**
     * TableModel interface method to return the number of
     * columns in the model.
     * @return the number of columns
     */
    public int getColumnCount() {
        return COLUMN_MAX_COUNT;
    }

    /**
     * TableModel interface method to return the number of
     * rows in the model.
     * @return the number of rows
     */
    public int getRowCount() {
        return (childModel.getRowCount() == 0 ? 0 : 1);
    }

    /**
     * TableModel interface method to return the value at
     * a given position.
     * @param row the horizontal position
     * @param column the vertical position
     * @return the requested value
     */
    public Object getValueAt(int row, int column) {
        TableRow rowData = getRowAt(row);
        return rowData.getValue(0);
    }

    /**
     * Return the row at the given index.
     * (Used by ManagedNode)
     * Note that there can be at most one item in the group.
     */
    public TableRow getRowAt(int index) {
        assert getRowCount() > 0 && index == 0;
        return rowData;
    }

    ////////// Implementation of TableModelListener

    // On table changed, determine if we change between zero
    // and non-zero number of children.
    // Do not call super.tableChanged, we fire our own events
    public void tableChanged(TableModelEvent evt) {
        try {
            initialize();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    //
    private void initialize() {
        boolean groupShouldShow = childModel.getRowCount() != 0;
        if (groupShouldShow ^ groupShowing) {
            groupShowing = groupShouldShow;
            if (groupShouldShow) {
                fireTableRowsInserted(0,0);
            } else {
                fireTableRowsDeleted(0,0);
            }
        }
    }
}

