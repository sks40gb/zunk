/* $Header: /home/common/cvsarea/ibase/dia/src/model/ManagedNodeModel.java,v 1.2.8.1 2006/03/22 20:27:15 nancy Exp $ */
package model;

/**
 * Parent class of the nodes used on <code>ui.BatchingPage</code> and
 * <code>ui.TeamsPage</code> for implementing managed trees.
 */
public abstract class ManagedNodeModel extends ManagedTableMap {

    /**
     * Create a <code>ManagedTableMap</code> for the given
     * <code>ManagedTableModel</code>.
     * @param model the <code>ManagedTableModel</code> instance to
     * use for this model
     */
    public ManagedNodeModel(ManagedTableModel model) {
        super(model);
    }

    public abstract ManagedNode makeChildNode(Object rowData);

}
