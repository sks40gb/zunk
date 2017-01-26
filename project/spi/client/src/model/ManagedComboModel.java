/* $Header: /home/common/cvsarea/ibase/dia/src/model/ManagedComboModel.java,v 1.5 2004/11/21 10:48:06 weaston Exp $ */
package model;

import common.Log;

import javax.swing.ComboBoxModel;

/**
 * A ComboBoxModel backed by the first column of a given ManagedTableModel.
 * A special first element may be specified; it is prepended to the values
 * from the TableModel.
 * <p>
 * To make the special first element initially selected, call
 * setSelectedIndex(0) on the JComboBox.  Do this if the
 * first element is to be the default.  If this is not done,
 * The initial selection is null, and you can detect whether
 * or not a selection has ever been made.  Once a selection is 
 * made, it canot be unmade via the GUI.
 * <p>
 * For example, you might specify the firstValue as "<All Widgets>",
 * then the table would provide "Widget 1", "Widget 2",...  If
 * setSelectedIndex(0) is called, the ComboBos will show 
 * "<All Widgets>" initially; otherwise it will be blank
 * until something is selected.
 *
 */
public class ManagedComboModel extends TableComboModel {

    private ManagedTableModel model;

    /**
     * Create a ManagedComboModel from a given ManagedTableModel,
     * with no first first element.
     */
    public ManagedComboModel(ManagedTableModel model) {
        this(null, model);
    }

    /**
     * Create a ManagedComboModel from a given ManagedTableModel,
     * with a given first element prepended to the table data.
     */
    public ManagedComboModel(String firstValue, ManagedTableModel model) {
        super(firstValue, model);
        this.model = model;
    }

    /**
     * Register the underlying ManagedTableModel with the server.
     */
    public void register() {
        model.register();
    }

    /**
     * Get the id corresponding to the given index.
     */
    public int getIdAt(int index) {

        // implementation copied from ManagedListModel

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
