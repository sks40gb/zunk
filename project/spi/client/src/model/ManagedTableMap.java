/* $Header: /home/common/cvsarea/ibase/dia/src/model/ManagedTableMap.java,v 1.4 2004/03/27 20:27:01 weaston Exp $ */
package model;

//import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
//import javax.swing.event.TableModelListener;
//import javax.swing.event.TableModelEvent;

/**
 * A null filter for ManagedTableMap.
 * Extends TableMap by adding the additional methods in ManagedTableMap.
 */
public class ManagedTableMap extends TableMap implements ManagedTableModel {

    /**
     * The underlying ManagedTableModel.
     * Note.  This declaration hides the declaration of model in TableMap,
     * within subclasses of ManagedTableMap.
     * The two variables should always contain the same reference, but the
     * one declared here is declared as a ManagedTableModel.
     * This variable should only be set by the setModel method.
     */
    protected ManagedTableModel model;

    /**
     * Create a new ManagedTableMap with a given underlying model.
     */
    protected ManagedTableMap(ManagedTableModel model) {
        super();
        setModel(model);
    }

    /**
     * Create a new ManagedTableMap with no underlying model.
     * Subclasses should call setModel to provide the underlying model.
     */
    protected ManagedTableMap() {
        this(null);
    }

    /**
     * Override setModel in TableMap. <p><ul>
     *   <li> Maintains the same value in both model and TableModel.this.model.  
     *   <li> Calls initialize() to set up initial data
     */
    public void setModel(TableModel model) {
        this.model = (ManagedTableModel) model;
        initialize();
        super.setModel(model);
    }

    /**
     * Initialize model with initial values of underlying model.
     * By default, does nothing.  Subclasses should override so
     * that the initial data are passed through from the 
     * underlying model.
     */
    protected void initialize() {
    }


    /**
     * Register this ManagedTableModel with the server.
     */
    public void register() {
        model.register();
    }

    /**
     * Return the number of values in a row.  (May be 
     * greater than the number of columns when viewed
     * as a table model.)
     */
    public int getColumnMaxCount() {
        return model.getColumnMaxCount();
    }

    /**
     * Get the id of this row.  Note that rows in the table model
     * correspond exactly to rows of a particular table.
     */
    public int getRowId(int row) {
        return model.getRowId(row);
    }

    /**
     * Return the row at the given index.
     * (Used by ManagedNode)
     */
    public TableRow getRowAt(int index) {
        return model.getRowAt(index);
    }
}
