/* $Header: /home/common/cvsarea/ibase/dia/src/model/ManagedNode.java,v 1.10 2004/03/14 12:28:15 weaston Exp $ */
package model;

import common.DynamicArrays;
import common.Log;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeExpansionEvent;

/**
 * Parent class of all managed node classes.  Managed nodes are
 * used as the nodes in a TreeModel for a managed tree.
 * <p> To enable automatic registration of a node's children when
 * the node is expanded, add a TreeWillExpandListener to the
 * associated tree, and have it's treeWillExpand method
 * call ManagedNode.checkRegisterOnTreeExpansion(evt).
 * The tree should have a DefaultTreeModel as its model,
 * and the model's asksAllowsChildren property should be true.
 */
public abstract class ManagedNode extends DefaultMutableTreeNode {

    // Apparently, drag-and-rop serializes the dragged node
    // We make everything transient that it shouldn't care about
    // This especially includes the TableModelListener, which is
    // not serializable.

    // The TreeModel contains this node.
    private transient DefaultTreeModel treeModel = null;

    // The id of the item in this model row, if the node represents a model row
    // Otherwise, 0.
    private transient int rowId = 0;

    // Array of models for the children of this row
    private transient ManagedNodeModel[] models = new ManagedNodeModel[0];

    // Sizes of models for the children of this row
    // Maintained, to handle non-specific TableModelEvents
    // (Otherwise, there' would be  no way to tell how many children belong
    // to the particular model.)
    private int[] modelSizes = new int[0];

    // Flag that this node should be registered on creation
    private transient boolean autoRegister = false;

    // Flag set when this node is registered
    private transient boolean isRegistered = false;

    /**
     * Create a new ManagedNode which allows children.
     * @param userObject The initial value of the user object for this node.
     */
    public ManagedNode(Object userObject) {
        this(userObject, true);
    }

    /**
     * Create a new ManagedNode.
     * @param userObject The initial value of the user object for this node.
     */
    public ManagedNode(Object userObject, boolean allowsChildren) {
        // Note.  Can't do super(userObject, allowsChildren) because
        // it doesn't call setUserObject.
        super();
        this.setUserObject(userObject);
        this.setAllowsChildren(allowsChildren);
        if (autoRegister) {
            this.register();
        }
    }

    /**
     * Register this node, if required.  By default, does nothing;
     */
    public void register() {
        if (! isRegistered) {
            isRegistered = true;
            for (int i = 0; i < models.length; i++) {
                models[i].register();
            }
        }
    }

    /**
     * Set to automatically register this node on creation.
     * subclasses should set this to true, if this node should
     * be automatically registered on creation.
     * <p> Note:  The root node should not be automatically
     * registered for models used on admin pages; the node should be
     * registered when the admin tab is selected. 
     */
    protected void setAutoRegister(boolean flag) {
        autoRegister = flag;
    }

    /**
     * Get the autoRegister flag.
     */
    protected boolean isAutoRegister() {
        return autoRegister;
    }

    /**
     * Append a new model to this node.  ManagedNodeModel's (which
     * extend TableModel) are concatenated to give the children of
     * this node.
     */
    public void add(ManagedNodeModel newModel) {
        models = (ManagedNodeModel[]) DynamicArrays.append(models, newModel);
        modelSizes = DynamicArrays.append(modelSizes, 0);
        newModel.addTableModelListener(tableListener);
        if (isRegistered) {
            newModel.register();
        }
        initializeForModel(models.length - 1);
    }

    public ManagedNodeModel getSubmodel(int index) {
        return (ManagedNodeModel) models[index];
    }

    /**
     * Get the TreeModel which contains this node; null if none.
     * This implementation gets TreeModel from its parent
     * (Overridden in RootManagedNode)
     */
    protected DefaultTreeModel getTreeModel() {
        if (treeModel != null) {
            return treeModel;
        }
        ManagedNode parent = (ManagedNode) this.getParent();
        return (parent == null ? null : parent.getTreeModel());
    }

    /**
     * Override DefaultMutableTreeModel.setUserObject to also
     * set the rowId corresponding to the database row for this
     * node.  The rowId is set if the given Object is a
     * TableRow.
     */
    public void setUserObject(Object obj) {
        super.setUserObject(obj);
        if (obj instanceof TableRow) {
            TableRow rowObj = (TableRow) obj;
            rowId = rowObj.id;
        } else {
            rowId = 0;
        }
    }

    /**
     * Get the row id for this node.
     * Equivalent to ((TableRow) this.getUserObject().getRowId())
     */
    public int getRowId() {
        return rowId;
    }

    // search the list of models for the current model
    private int getModelIndex(ManagedNodeModel model) {
        for (int i = 0; i < models.length; i++) {
            if (model == models[i]) {
                return i;
            }
        }
        Log.quit("ManagedNode model not found: "
                 +model.getClass().getName()
                 +" -- models.length="+models.length);
        return 0;  // never get here
    }

    // compute the offset of the first row for the given model
    private int getRowOffset(int index) {
        int sum = 0;
        for (int i = 0; i < index; i++) {
            sum += modelSizes[i];
        }
        return sum;
    }

    /**
     * Make this node the root of the given tree model
     * and register it.
     * USE THIS INSTEAD OF TreeModel.setRoot, as this
     * node needs to keep track of the model it belongs to.
     */
    public void setAsModelRoot(DefaultTreeModel treeModel) {
        Object oldRoot = treeModel.getRoot();
        if (oldRoot instanceof ManagedNode) {
            ManagedNode oldRootManagedNode = (ManagedNode) oldRoot;
            oldRootManagedNode.treeModel = null;
        }
        this.treeModel = treeModel;
        //Log.print("setAsModelRoot "+this);
        treeModel.setRoot(this);
        this.register();
    }

    /**
     * Register a node on tree expansion.  Called from a TreeWillExpandListener
     * for a JTree, as the treeWillExpand action.
     */
    public static void checkRegisterOnTreeExpansion(TreeExpansionEvent evt) {
        try {
            TreePath path = evt.getPath();
            Object nodeObject = path.getLastPathComponent();
            if (nodeObject instanceof ManagedNode) {
                ManagedNode node = (ManagedNode) nodeObject;
                node.register();
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    // Insert nodes for a new submodel
    // TBD:  Much of this is a clone of the listener code.  Should be consolidated.
    private void initializeForModel(int modelIndex) {
        DefaultTreeModel treeModel = getTreeModel();
        ManagedNodeModel changedModel = models[modelIndex];
        int oldSize = modelSizes[modelIndex];
        int offset = getRowOffset(modelIndex);
        if (oldSize > 0) {
            int[] removedIndices = new int[oldSize];
            Object[] removedChildren = new Object[oldSize];
            for (int i = oldSize - 1; i >= 0; i--) {
                removedIndices[i] = offset + i;
                removedChildren[i] = this.getChildAt(offset + i);
                this.remove(i);
            }
            if (treeModel != null) {
                treeModel.nodesWereRemoved(this, removedIndices, removedChildren);
            }
        }
        int newSize = changedModel.getRowCount();
        if (newSize > 0) {
            int[] insertedIndices = new int[newSize];
            for (int i = 0; i < newSize; i++) {
                insertedIndices[i] = offset + i;
                TableRow newNodeData = changedModel.getRowAt(i);
                ManagedNode newNode
                    = changedModel.makeChildNode(newNodeData);
                this.insert(newNode,i + offset);
            }
            if (treeModel != null) {
                treeModel.nodesWereInserted(this, insertedIndices);
            }
        }
        modelSizes[modelIndex] = newSize;
    }

    /**
     * Listener to update the children of this node whenever
     * one of the underlying ManagedTableModel's is changed.
     */
    private transient TableModelListener tableListener = new TableModelListener() {
        public void tableChanged(javax.swing.event.TableModelEvent evt) {

            // Find which underlying model was changed
            // Note.  throws ArrayIndexOutOfBoundsException if not found
            ManagedNodeModel changedModel = (ManagedNodeModel) evt.getSource();
            int modelIndex = getModelIndex(changedModel);
            int rowOffset = getRowOffset(modelIndex);

            int firstRow = evt.getFirstRow();
            int lastRow = evt.getLastRow();

            DefaultTreeModel treeModel = getTreeModel();
            //Log.print( 
            //       "MN>"+ManagedNode.this.getClass().getName()
            //       +".tableChanged: column="+evt.getColumn()
            //       +" firstRow="+firstRow
            //       +" lastRow="+lastRow
            //       +" type="+evt.getType()
            //       +" treeModel null? "+(treeModel == null));
                   

            // If it's a complete change, get the initial values
            if (modelSizes[modelIndex] == 0
            || lastRow == TableModelEvent.HEADER_ROW
            || lastRow == Integer.MAX_VALUE) {
                initializeForModel(modelIndex);
                return;
            }

            switch (evt.getType()) {
            case TableModelEvent.DELETE: {
                assert firstRow == lastRow;
                if (treeModel == null) {
                    remove(firstRow + rowOffset);
                } else {
                    ManagedNode node
                        = (ManagedNode) ManagedNode.this.getChildAt(firstRow + rowOffset);
                    treeModel.removeNodeFromParent(node);
                }
                modelSizes[modelIndex]--;
                break;
            }
            case TableModelEvent.UPDATE: {
                assert firstRow == lastRow;
                ManagedNode node
                    = (ManagedNode) ManagedNode.this.getChildAt(firstRow + rowOffset);
                // The content of the node has changed, but it's the same node
                if (treeModel != null) {
                    treeModel.nodeChanged(node);
                }
                break;
            }
            case TableModelEvent.INSERT: {
                assert firstRow <= lastRow;
                for (int i = firstRow; i <= lastRow; i++) {
                    TableRow newNodeData = changedModel.getRowAt(i);
                    ManagedNode newNode
                        = changedModel.makeChildNode(newNodeData);
                    if (treeModel == null) {
                        insert(newNode,i + rowOffset);
                    } else {
                        treeModel.insertNodeInto(newNode,ManagedNode.this,i + rowOffset);
                    }
                }
                modelSizes[modelIndex] += lastRow - firstRow + 1;
                break;
            }
            default:
                Log.quit("Invalid table model event type: "+evt.getType());
            }
        }
    };
}
