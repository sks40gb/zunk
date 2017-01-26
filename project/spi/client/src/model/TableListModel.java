/* $Header: /home/common/cvsarea/ibase/dia/src/model/TableListModel.java,v 1.3.6.1 2006/03/22 20:27:15 nancy Exp $ */
package model;

import javax.swing.AbstractListModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * A ListModel which uses the first column of an underlying TableModel
 * for its data.  Optionally, a first element may be explicitly specified;
 * the remaining elements for the ListModel are taken from the underlying model.
 * <P> TBD: the firstValue stuff probably doesn't work.  Needs work on
 * listener logic.
 */
public class TableListModel extends AbstractListModel {

    final private TableModel model;
    final private String firstValue;
    final private int firstOffset;

    //boolean isRegistered = false;

    /** Create a new TableListModel, backed by a given TableModel,
     * with no specified first element
     */
    public TableListModel(TableModel model) {
        this(null, model);
    }

    /** Create a new TableListModel, backed by a given TableModel,
     * with a specified first element
     */
    public TableListModel(String firstValue, TableModel model) {
        this.model = model;
        this.firstValue = firstValue;
        this.firstOffset = (firstValue == null ? 0 : 1);
        final Object mlmThis = TableListModel.this;
        model.addTableModelListener(new TableModelListener() {
                public void tableChanged(TableModelEvent evt) {
                    int firstIndex = evt.getFirstRow();
                    if (firstOffset > 0 && firstIndex >= 0) {
                        firstIndex += firstOffset;
                    }
                    int lastIndex = evt.getLastRow();
                    if (firstOffset > 0 && lastIndex < Integer.MAX_VALUE) {
                        lastIndex += firstOffset;
                    }
                    //System.out.println("tableChanged "+evt.getType()
                    //                   +" "+firstIndex+".."+lastIndex);
                    switch (evt.getType()) {
                    case TableModelEvent.INSERT:
                        fireIntervalAdded(mlmThis, firstIndex, lastIndex);
                        break;
                    case TableModelEvent.DELETE:
                        fireIntervalRemoved(mlmThis, firstIndex, lastIndex);
                        break;
                    case TableModelEvent.UPDATE:
                        fireContentsChanged(mlmThis, firstIndex, lastIndex);
                        break;
                    default:
                        throw new AssertionError("ManagedListModel: invalid TableModelEvent");
                    }
                }
            });
    }

    /** Return the underlying TableModel */
    public TableModel getModel() {
        return model;
    }

    /** Return the specified first value, or null if there is none. */
    public String getFirstValue() {
        return firstValue;
    }

    /** Return the number of rows in the model */
    public int getSize() {
        return model.getRowCount() + firstOffset;
    }

    /** Return the object at position index */
    public Object getElementAt(int index) {
        
        return (firstValue != null && index == 0
                ? firstValue
                : model.getValueAt(index - firstOffset, 0));
    }
}
