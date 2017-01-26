/* $Header: /home/common/cvsarea/ibase/dia/src/model/ManagedListModel.java,v 1.3 2004/06/03 22:32:09 weaston Exp $ */
package model;

//import common.Log;
import model.TableListModel;


/**
 * A ListModel which uses the first column of an underlying ManagedTableModel
 * for its data.  Optionally, a first element may be explicitly specified;
 * the remaining elements for the ListModel are taken from the underlying model.
 */
public class ManagedListModel extends TableListModel {

    /** Create a new ManagedListModel with no specified first element and a given underlying model */
    public ManagedListModel(ManagedTableModel model) {
        this(null, model);
    }

    /** Create a new ManagedListModel with a given first element */
    public ManagedListModel(String firstValue, ManagedTableModel model) {
        super(firstValue, model);
    }

    /**
     * Register the underlying ManagedTableModel. */
    public void register() {
        ((ManagedTableModel) getModel()).register();
    }

    /**
     * get the id corresponding to the given index.
     */
    public int getIdAt(int index) {
        if (getFirstValue() != null) {
            if (index == 0) {
                // First element, there is no id
                return 0;
            } else {
                // Skip the first element
                index++;
            }
        }
        return ((ManagedTableModel) getModel()).getRowId(index);
    }
}
