/* $Header: /home/common/cvsarea/ibase/dia/src/beans/DynamicTree.java,v 1.30.8.2 2006/03/09 12:09:16 nancy Exp $ */
package beans;

import common.Log;
import java.awt.Cursor;
import java.awt.Event;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

/**
 * The two JTrees shown on <code>ui.BatchingPage</code> are extensions of
 * <code>DynamicTree</code>.  
 */
public class DynamicTree extends JScrollPane implements DragGestureListener, DropTargetListener, DragSourceListener {

    /** the root node of <code>tree</code> */
    protected DefaultMutableTreeNode rootNode;
    /** the model used by <code>tree</code> */
    protected DefaultTreeModel treeModel = null;
    /** the tree shown on <code>ui.BatchingPage</code> */
    protected JTree tree;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    private TransferHandler transferHandler = new TransferHandler("Node");
    /** Variable needed for DnD */
    protected DragSource dragSource = null;
    /** Variable needed for DnD */
    protected DragSourceContext dragSourceContext = null;

    /**
     * Create an instance of <code>DynamicTree</code> with a default
     * root node.  The model can be set via <code>setModel</code).
     */
    public DynamicTree() {
        this(null);
    }

    /**
     * Create an instance of <code>DynamicTree</code> using model as the
     * root node and create the <code>JTree</code>.
     * @param model the model to use as the root node for the <code>JTree</code>
     */
    public DynamicTree(DefaultTreeModel model) {
        if (model == null) {
            rootNode = new DefaultMutableTreeNode("Root Node");
            treeModel = new DefaultTreeModel(rootNode);
        } else {
            treeModel = model;
            rootNode = (DefaultMutableTreeNode) model.getRoot();
        }


        tree = new JTree(treeModel);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);
        tree.setEditable(false);
        tree.setExpandsSelectedPaths(true);
        tree.setTransferHandler(transferHandler);

        dragSource = DragSource.getDefaultDragSource();
        // creating the recognizer is all that's necessary - it
        // does not need to be manipulated after creation
        DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(tree, //DragSource
                DnDConstants.ACTION_COPY_OR_MOVE, //specifies valid actions
                this //DragGestureListener
                );

        /* First argument:  Component to associate the target with
         * Second argument: DropTargetListener 
         */
        DropTarget dropTarget = new DropTarget(tree, this);

        setViewportView(tree);
        setPreferredSize(new java.awt.Dimension(330, 303));
        setAutoscrolls(true);
        addMouseMotionListener(new MML());
    }

    /** Remove all nodes except the root node. */
    public void clear() {
        rootNode.removeAllChildren();
        treeModel.reload();
    }

    /**
     * Return the underlying JTree.
     * @return the dynamic tree
     */
    public JTree getTree() {
        return tree;
    }

    /**
     * Returns the TreeModel that is providing the data.
     * @return the model of the underlying <code>JTree(/code>
     */
    public TreeModel getModel() {
        return tree.getModel();
    }

    /**
     * Sets the TreeModel that will provide the data.
     * @param model the model that will provide the data shown in the tree
     */
    public void setModel(TreeModel model) {
        tree.setModel(model);
    }

    /**
     * Returns the child of parent at index index in the parent's child array.
     * Parent must be a node previously obtained from this data source. This
     * should not return null if index is a valid index for parent (that is
     * index >= 0 && index < getChildCount(parent)).
     * @param parent a node in the tree, obtained from this data source
     * @param index the position in the parent's child array
     * @return the child of <code>parent</code> at index <code>index</code>
     */
    public DefaultMutableTreeNode getChild(DefaultMutableTreeNode parent, int index) {
        if (!parent.isLeaf()) {
            return (DefaultMutableTreeNode) treeModel.getChild(parent, index);
        }
        return null;
    }

    /**
     * Adds the specified mouse listener to receive mouse events from
     * this component. If listener l is null, no exception is thrown
     * and no action is performed.
     * @param ma the mouse listener
     */
    public void addMouseListener(MouseAdapter ma) {
        tree.addMouseListener(ma);
    }

    /**
     * Sets the <code>TreeCellRenderer</code> that will be used to draw each cell.
     * @param renderer the <code>TreeCellRenderer</code> that is to render each cell
     */
    public void setCellRenderer(DefaultTreeCellRenderer renderer) {
        tree.setCellRenderer(renderer);
    }

    /**
     * Sets the user object for this node to <code>name</code>.
     * @param name the text to be used as the user object
     */
    public void setRootUserObject(String name) {
        rootNode.setUserObject(name);
    }

    /**
     * Change the text that is viewable by the user for this node.
     * @param path the path in the tree to reach the changing text
     * @param name the new text
     */
    public void setUserObject(TreePath path, String name) {
        //node.setUserObject(name);
        treeModel.valueForPathChanged(path, name);

        treeModel.nodeChanged((DefaultMutableTreeNode) path.getLastPathComponent());
    }

    /**
     * Returns the number of nodes selected.
     * @return the number of nodes selected
     */
    public int getSelectionCount() {
        return tree.getSelectionCount();
    }

    /**
     * Returns the path for the node at the specified location.
     * @param x an integer giving the number of pixels horizontally from
     * the left edge of the display area, minus any left margin
     * @param y an integer giving the number of pixels vertically from
     * the top of the display area, minus any top margin
     * @return the TreePath for the node at that location
     */
    public TreePath getPathForLocation(int x, int y) {
        return tree.getPathForLocation(x, y);
    }

    /**
     * Returns the path to the first selected node.
     * @return the TreePath for the first selected node,
     * or null if nothing is currently selected
     */
    public TreePath getSelectionPath() {
        return tree.getSelectionPath();
    }

    /**
     * Returns the paths of all selected values.
     * @return an array of TreePath objects indicating the selected nodes,
     * or null if nothing is currently selected
     */
    public TreePath[] getSelectionPaths() {
        return tree.getSelectionPaths();
    }
    // ends here

    /**
     * Sets the selection model, which must be one of SINGLE_TREE_SELECTION,
     * CONTIGUOUS_TREE_SELECTION or DISCONTIGUOUS_TREE_SELECTION.
     * <p>This may change the selection if the current selection is not valid
     * for the new mode. For example, if three TreePaths are selected when
     * the mode is changed to SINGLE_TREE_SELECTION, only one TreePath will
     * remain selected. It is up to the particular implementation to decide
     * what TreePath remains selected.
     * @param tsm one of SINGLE_TREE_SELECTION, CONTIGUOUS_TREE_SELECTION
     * or DISCONTIGUOUS_TREE_SELECTION
     */
    public void setTreeSelectionModel(int tsm) {
        tree.getSelectionModel().setSelectionMode(tsm);
    }

    /**
     * Sets the dragEnabled property, which must be true to enable automatic drag
     * handling (the first part of drag and drop) on this component. The
     * transferHandler property needs to be set to a non-null value for the drag
     * to do anything. The default value of the dragEnabled property is false.
     * @param flag the value to set the dragEnabled property to
     */
    public void setDragEnabled(boolean flag) {
        tree.setDragEnabled(flag);
    }

    /**
     * Makes sure all the path components in path are expanded (except for the
     * last path component) and scrolls so that the node identified by the path
     * is displayed. Only works when this JTree is contained in a JScrollPane.
     * @param node the node to make visible
     */
    public void showNode(DefaultMutableTreeNode node) {
        if (node == null) {
            return;
        }
        //Log.print("(DynamicTree).showNode " + node);
        TreePath path = new TreePath(node.getPath());
        tree.scrollPathToVisible(path);
        tree.setSelectionPath(path);
    }

    /**
     * Selects the node identified by the specified path. If any component of
     * the path is hidden (under a collapsed node), and getExpandsSelectedPaths
     * is true it is exposed (made viewable).
     * @param path the path to the node to be selected
     */
    public void setSelectionPath(TreePath path) {
        tree.setSelectionPath(path);
    }

    /**
     * Selects the nodes identified by the specified array of paths. 
     * If any component in any of the paths is hidden (under a collapsed node), 
     * and getExpandsSelectedPaths is true it is exposed (made viewable). 
     * @param paths an array of TreePath objects that specifies the nodes to select
     */
    public void setSelectionPaths(TreePath[] path) {
        tree.setSelectionPaths(path);
    }

    /**
     * insert the child to either the top or bottom of the given parent's children.
     * @param child - child node to be inserted
     * @param parent - new parent of the child node to be inserted
     * @param top - true means insert at top of child list; false at the bottom
     */
    public void insert(DefaultMutableTreeNode child,
            DefaultMutableTreeNode parent,
            boolean top) {
        int row;
        if (top) {
            row = 0;
        } else {
            row = parent.getChildCount();
        }

        //Log.print("DynamicTree).insert child/parent/top " + child + "/" + parent
        //          + "/" + top + "/" + row);
        parent.insert(child, row);
    }

    /**
     * Call <code>removeNodeFromParent</code> defaulting to
     * <code>removeChildlessParent</code>.
     * @param node the node to be removed from the model
     */
    public void removeNodeFromParent(DefaultMutableTreeNode node) {
        removeNodeFromParent(node, true);
    }

    /**
     * Message this to remove node from its parent. This will message
     * nodesWereRemoved to create the appropriate event. This is the
     * preferred way to remove a node as it handles the event creation for you.
     * @param node the node to be removed from the model
     * @param removeChildlessParent true to remove the parent if no children remain
     */
    public void removeNodeFromParent(DefaultMutableTreeNode node, boolean removeChildlessParent) {
        DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
        treeModel.removeNodeFromParent(node);
        if (removeChildlessParent && parent.getChildCount() < 1) {
            treeModel.removeNodeFromParent(parent);
        }
    }

    /** Remove the currently selected node from the model. */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode) (currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        }

        // Either there was no selection, or the root was selected.
        toolkit.beep();
        Log.print("BEEP> DynamicTree: Either there was no selection, or the root was selected");
    }

    /**
     * Add child to the currently selected node.
     * @param child the node to be added
     * @return the node to which the child was added
     */
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode) (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true, -1);
    }

    /**
     * Create a new node and insert it in the tree, defaulting to not
     * making the new node visible and to appending as last child.
     * @param parent the parent of the new node
     * @param child The new node or user object.  If child is a 
     *      DefaultMutableTreeNode, it is used as the new node;
     *      otherwise it is used as the userObject for a new
     *      DefaultMutableTreeNode
     * @return the created node
     */
    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
            Object child) {
        return addObject(parent, child, false, -1);
    }

    /**
     * Create a new node and insert it in the tree, defaulting to append
     * as last child.
     * @param parent the parent of the new node
     * @param child The new node or user object.  If child is a 
     *      DefaultMutableTreeNode, it is used as the new node;
     *      otherwise it is used as the userObject for a new
     *      DefaultMutableTreeNode
     * @param shouldBeVisible indicates that the new node should be made visible
     * @return the created node
     */
    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
            Object child,
            boolean shouldBeVisible) {
        return addObject(parent, child, shouldBeVisible, -1);
    }

    /**
     * Create a new node and insert it in the tree.
     * @param parent the parent of the new node
     * @param child The new node or user object.  If child is a 
     *      DefaultMutableTreeNode, it is used as the new node;
     *      otherwise it is used as the userObject for a new
     *      DefaultMutableTreeNode
     * @param shouldBeVisible indicates that the new node should be made visible
     * @param index the child index under the parent. If -1, append as last child.
     * @return the created node
     */
    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
            Object child,
            boolean shouldBeVisible,
            int index) {
        DefaultMutableTreeNode childNode;
        //if (DefaultMutableTreeNode.class.isInstance(child)) {
        if (child instanceof DefaultMutableTreeNode) {
            childNode = (DefaultMutableTreeNode) child;
        } else {
            childNode = new DefaultMutableTreeNode(child);
        }

        if (parent == null) {
            parent = rootNode;
        }

        if (index > -1) {
            treeModel.insertNodeInto(childNode, parent, index);
        } else {
            treeModel.insertNodeInto(childNode, parent,
                    parent.getChildCount());
        }

        // Make sure the user can see the new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }

    /**
     * Makes sure all the path components in path are expanded (except for
     * the last path component) and scrolls so that the node identified by
     * the path is displayed. Only works when this JTree is contained in a JScrollPane.
     * @param node the node to bring into view
     */
    public void scrollPathToVisible(DefaultMutableTreeNode node) {
        TreePath path = new TreePath(node.getPath());
        tree.collapsePath(path);
        tree.scrollPathToVisible(path);
    }

    /**
     * DragGestureListener interface method - A DragGestureRecognizer has
     * detected a platform-dependent drag initiating gesture and is
     * notifying this listener in order for it to initiate the action
     * for the user.
     * @param e the DragGestureEvent describing the gesture that has just occurred
     */
    public void dragGestureRecognized(DragGestureEvent e) {
        try {
            //Get the selected node
            Log.print("(DynamicTree).dragGestureRecognized drag component is =====> " + e.getComponent());
            Log.print("(DynamicTree).dragGestureRecognized drag source is =====> " + e.getDragSource());
            TreePath currentSelection = tree.getSelectionPath();
            if (currentSelection != null) {
                DefaultMutableTreeNode dragNode = (DefaultMutableTreeNode) (currentSelection.getLastPathComponent());
                if (dragNode != null) {

                    //Get the Transferable Object
                    Transferable transferable = (Transferable) dragNode;

                    //Select the appropriate cursor;
                    Cursor cursor = DragSource.DefaultCopyNoDrop;
                    int action = e.getDragAction();
                    if (action == DnDConstants.ACTION_COPY_OR_MOVE) {
                        cursor = DragSource.DefaultCopyDrop;
                    }

                    //begin the drag
                    dragSource.startDrag(e, cursor, transferable, this);
                }
            }
        } catch (Throwable th) {
            System.out.print(th);
        }
    }

    public void startDrag(DragGestureEvent e, Cursor dragCursor, Transferable transferable) {
        DataFlavor[] flavors = transferable.getTransferDataFlavors();
        //("(DynamicTree.startDrag) " + transferable);
        //for (int i = 0; i < flavors.length; i++) {
        //    Log.print("(DynamicTree.startDrag) " + i + " " + flavors[i]);
        //}
        dragSource.startDrag(e, dragCursor, transferable, this);
    //trigger.startDrag(dragCursor, transferable, this);

    }

    /** DragSourceListener interface method */
    public void dragDropEnd(DragSourceDropEvent dsde) {
    }

    /** DragSourceListener interface method */
    public void dragEnter(DragSourceDragEvent e) {
        //DragSourceContext context = e.getDragSourceContext();
        ////intersection of the users selected action, and the source and target actions
        //int myaction = e.getDropAction();
        //if( (myaction & DnDConstants.ACTION_COPY) != 0) { 
        //    context.setCursor(DragSource.DefaultCopyDrop);   
        //} else {
        //    context.setCursor(DragSource.DefaultCopyNoDrop);    
        //}
    }

    /** TreeSelectionListener interface method */
    public void dragEnter(DropTargetDragEvent dtde) {
    }

    /** DragSourceListener interface method */
    public void dragOver(DragSourceDragEvent dsde) {
    }

    /** DropTargetListener interface method */
    public void dragOver(DropTargetDragEvent dtde) {
    }

    /** DragSourceListener interface method */
    public void dropActionChanged(DragSourceDragEvent dsde) {
    }

    /** DragSourceListener interface method */
    public void dropActionChanged(DropTargetDragEvent dtde) {
    }

    /** DragSourceListener interface method */
    public void dragExit(DragSourceEvent dsde) {
    }

    /** DragSourceListener interface method */
    public void dragExit(DropTargetEvent dsde) {
    }

    /** TreeSelectionListener interface method */
    public void valueChanged(TreeSelectionEvent tse) {
    }

    /** DropTargetListener interface method - what happens when drag is released */
    public void drop(DropTargetDropEvent e) {
        Log.print("(DynamicTree.drop)");
    }

    private class MML implements MouseMotionListener {

        /**
         * Invoked when a mouse button is pressed on a component and then
         * dragged.  <code>MOUSE_DRAGGED</code> events will continue to be
         * delivered to the component where the drag originated until the
         * mouse button is released (regardless of whether the mouse position
         * is within the bounds of the component).
         * <p>
         * Due to platform-dependent Drag&Drop implementations,
         * <code>MOUSE_DRAGGED</code> events may not be delivered during a native
         * Drag&Drop operation.
         */
        public void mouseDragged(MouseEvent e) {
            //The user is dragging, so scroll.
            Log.print("(DynamicTree.mml.mouseDragged) ");
            Rectangle r = new Rectangle(e.getX(), e.getY(), 1, 1);
            scrollRectToVisible(r);
        }

        /**
         * Invoked when the mouse cursor has been moved onto a component
         * but no buttons have been pushed.
         */
        public void mouseMoved(MouseEvent e) {
        }

        /**
         *  This method is called whenever an event occurs of the type for which
         * the <code> EventListener</code> interface was registered.
         * @param evt The <code>Event</code> contains contextual information
         *   about the event. It also contains the <code>stopPropagation</code>
         *   and <code>preventDefault</code> methods which are used in
         *   determining the event's flow and default action.
         */
        public void handleEvent(Event evt) {
        }
    }
}
