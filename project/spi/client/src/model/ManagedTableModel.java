/* $Header: /home/common/cvsarea/ibase/dia/src/model/ManagedTableModel.java,v 1.17 2004/05/05 16:33:07 weaston Exp $ */
package model;

import javax.swing.table.TableModel;

/**
 * A table model for use by a managed table or managed tree.  This interface adds
 * a few required methods over and above TableModel.
 */
public interface ManagedTableModel extends TableModel {

    /**
     * Register this ManagedTableModel with the server.
     */
    public void register();

    /**
     * Return the number of values in a row.  (May be 
     * greater than the number of columns when viewed
     * as a table model.)
     */
    public int getColumnMaxCount();

    /**
     * Get the id of this row.  Note that rows in the table model
     * correspond exactly to rows of a particular table.
     */
    public int getRowId(int row);

    /**
     * Return the row at the given index.
     * (Used by ManagedNode)
     */
    public TableRow getRowAt(int index);
}

