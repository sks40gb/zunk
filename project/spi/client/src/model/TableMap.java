/* $Header: /home/common/cvsarea/ibase/dia/src/model/TableMap.java,v 1.4.8.1 2006/03/22 20:27:15 nancy Exp $ */
package model;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;

/**
 * This program taken from the Java Tutorial.  The only change is that it was
 * placed in package model.  (The original was in the default package.)
 * http://java.sun.com/docs/books/tutorial/uiswing/components/example-1dot4/TableMap.java
 * <p>
 * Changes: <ul>
 *   <li> Moved to package model (from default package)
 *   <li> setModel handles null models and fires a TableModelEvent
 * </ul><p>
 * In a chain of data manipulators some behaviour is common. TableMap
 * provides most of this behaviour and can be subclassed by filters
 * that only need to override a handful of specific methods. TableMap
 * implements TableModel by routing all requests to its model, and
 * TableModelListener by routing all events to its listeners. Inserting
 * a TableMap which has not been subclassed into a chain of table filters
 * should have no effect.
 *
 * @version 1.4 12/17/97
 * @author Philip Milne */
public class TableMap extends AbstractTableModel
implements TableModelListener {

    protected TableModel model = null;

    public TableModel getModel() {
        return model;
    }

    public void setModel(TableModel model) {
        if (this.model != model) {
            if (this.model != null) {
                this.model.removeTableModelListener(this);
            }
            this.model = model;
            if (model != null) {
                model.addTableModelListener(this);
            }
            fireTableStructureChanged();
        }
    }

    // By default, implement TableModel by forwarding all messages
    // to the model.

    /**
     * Returns the value for the cell at aColumn and aRow.
     */
    public Object getValueAt(int aRow, int aColumn) {
        return model.getValueAt(aRow, aColumn);
    }

    /**
     * Sets the value in the cell at aColumn and aRow to aValue.
     */
    public void setValueAt(Object aValue, int aRow, int aColumn) {
        model.setValueAt(aValue, aRow, aColumn);
    }

    /**
     * Returns the number of rows in the model.
     */
    public int getRowCount() {
        return (model == null) ? 0 : model.getRowCount();
    }

    /**
     * Returns the number of columns in the model.
     */
    public int getColumnCount() {
        return (model == null) ? 0 : model.getColumnCount();
    }

    /**
     * Returns the name of the column at aColumn.
     */
    public String getColumnName(int aColumn) {
        return model.getColumnName(aColumn);
    }

    public Class getColumnClass(int aColumn) {
        return model.getColumnClass(aColumn);
    }

    public boolean isCellEditable(int row, int column) {
         return model.isCellEditable(row, column);
    }

    //                                                     
    // Implementation of the TableModelListener interface, 
    //                                                     

    /**
     * By default forward all events to all the listeners.
     * Source is replaced by this model ($$$ Change from tutorial code $$$)
     */
    public void tableChanged(TableModelEvent e) {
        TableModelEvent e2 = new TableModelEvent(this,
                                                 e.getFirstRow(),
                                                 e.getLastRow(),
                                                 e.getColumn(),
                                                 e.getType());

        fireTableChanged(e2);
    }
}
