/*
 * ManagedTableModel.java
 *
 * Created on December 4, 2007, 5:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.server;

import javax.swing.table.TableModel;

/**
 *
 * @author Bala
 */
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
