/* $Header: /home/common/cvsarea/ibase/dia/src/model/ManagedTableLabel.java,v 1.3.6.1 2006/03/22 20:27:15 nancy Exp $ */
package model;

import javax.swing.table.AbstractTableModel;

/**
 * A table model which contains exactly one row, 
 * with a childModel which can be registered on tree expansion.
 */
public class ManagedTableLabel extends AbstractTableModel
implements ManagedTableModel {

    final private int         COLUMN_MAX_COUNT = 1;
    private TableRow          rowData;

    public ManagedTableLabel (Object userObject) {
        if (userObject instanceof TableRow) {
            rowData = (TableRow) userObject;
        } else {
            rowData = new TableRow(0, new Object[] { userObject });
        }
    }

    public ManagedTableLabel (String legend, int parentId) {
        this(new TableRow(parentId, new Object[] { legend }));
    }

    /**
     * Register this SQLManagedTableModel with the server.
     * Null implementation, as this model is unchanging.
     */
    public synchronized void register() {}

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
     * Get the id of this row.  (Always row 0).
     */
    public int getRowId(int row) {
        assert row == 0;
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
        return 1;
    }

    /**
     * TableModel interface method to return the number of
     * rows in the model.
     * @return the number of rows
     */
    public Object getValueAt(int row, int column) {
        assert row == 0;
        assert column == 0;
        return rowData.getValue(0);
    }

    /**
     * Return the row at the given index.
     * (Used by ManagedNode)
     */
    public TableRow getRowAt(int index) {
        assert index == 0;
        return rowData;
    }
}

