/* $Header: /home/common/cvsarea/ibase/dia/src/model/TableComboModel.java,v 1.1 2004/11/22 11:59:32 weaston Exp $ */
package model;

import common.Log;

import javax.swing.ComboBoxModel;
import javax.swing.table.TableModel;

/**
 * A ComboBoxModel backed by the first column of a given TableModel.
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
public class TableComboModel extends TableListModel implements ComboBoxModel {

    private Object selectedItem;

    /**
     * Create a TableComboModel from a given ManagedTableModel,
     * with no first first element.
     */
    public TableComboModel(TableModel model) {
        this(null, model);
    }

    /**
     * Create a TableComboModel from a given ManagedTableModel,
     * with a given first element prepended to the table data.
     */
    public TableComboModel(String firstValue, TableModel model) {
        super(firstValue, model);
    }

    public Object getSelectedItem() {
        return selectedItem;
    }

    /**
     * WARNING: The user should not call this; it is for use by JComboBox.
     * A proper implementation would require firing a selection change
     * when the selectedItem is changed.  This should not be a problem,
     * as long as the JComboBox is not editable.
     */ 
    public void setSelectedItem(Object item) {
        selectedItem = item;
    }
}
