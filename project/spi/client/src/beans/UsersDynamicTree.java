/* $Header: /home/common/cvsarea/ibase/dia/src/beans/UsersDynamicTree.java,v 1.14.8.1 2006/03/09 12:09:16 nancy Exp $ */
package beans;

import common.Log;
//import model.TableRow;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.InvalidDnDOperationException;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

/**
 * A TreePanel specialized for a tree of users.
 * User batch nodes are colored to represent their status.
 */
public class UsersDynamicTree extends DynamicTree {

    /**
     * Create and instance of DynamicTree specialized for users.
     */
    public UsersDynamicTree() {
        super();

        // Enable tool tips
        // I'm not quite sure I understand this, but it's taken from
        // TreeIconDemo2.java in the Swing Tutorial
        ToolTipManager.sharedInstance().registerComponent(tree);

        super.setCellRenderer(new UsersTreeRenderer());
    }

    private class UsersTreeRenderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree,
                Object value,
                boolean sel, boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
            Component result;
            DefaultMutableTreeNode defaultNode = (DefaultMutableTreeNode) value;
            if (defaultNode instanceof UsersTreeNode) {
                UsersTreeNode valueNode = (UsersTreeNode) value;
                //if (valueNode.getUserObject() instanceof TableRow) {
                //    Log.print("..."+((TableRow) (valueNode.getUserObject())).dump());
                //}
                setTextSelectionColor(Color.white);
                if (valueNode.isVolume() || valueNode.isQueued() || (valueNode.isLabel() && (valueNode.getUserObject().toString()).indexOf("Queue") > -1)) {
                    setTextNonSelectionColor(Color.orange.darker());
                    setBackgroundSelectionColor(Color.orange.darker());
                } else if (valueNode.isAssigned()) {
                    setTextNonSelectionColor(Color.blue.darker());
                    setBackgroundSelectionColor(Color.blue.darker());
                } else {
                    setTextNonSelectionColor(Color.black);
                    setBackgroundSelectionColor(Color.black);
                //setToolTipText("Other");
                }
                // Must set colors before this call and icons after!
                result = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (valueNode.isVolume()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cd.gif")));
                    setToolTipText("Volume");
                } else if (valueNode.isUQC()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/uqc.gif")));
                    setToolTipText("Unitized batch in QC");
                } else if (valueNode.isCoding()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/coding.gif")));
                    setToolTipText("Coding Batch");
                } else if (valueNode.isQC()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/magnify.gif")));
                    setToolTipText("Coded Batch in QC");
                } else if (valueNode.isQA()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/check.gif")));
                    setToolTipText("Batch in QA");
                } else if (valueNode.isUnitize()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/newdoc.gif")));
                    setToolTipText("Unitize Batch");
                } else if (valueNode.isMasking()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/masking.gif")));
                    setToolTipText("Masking Batch");
                }else if (valueNode.isListing()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/listing.gif")));
                    setToolTipText("Listing Batch");
                } else if (valueNode.isTally()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/tally.gif")));
                    setToolTipText("Tally Batch");
                }  else if (valueNode.isModifyErrors()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/modifyerror.gif")));
                    setToolTipText("ModifyError Batch");
                } else {
                    setOpenIcon(new javax.swing.ImageIcon(getClass().getResource("/images/folder-open.gif")));
                    setClosedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/folder-closed.gif")));
                }
            } else {
                result = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            }
            return result;
        }
    }

    /**
     * Drop must be overridden in <code>BatchingPage.java</code>.
     * @param e The DropTargetDropEvent is delivered via the
     * DropTargetListener drop() method.
     */
    public void drop(DropTargetDropEvent e) {
    }

    /**
     * Override <code>DynamicTree</code>'s method to start a
     * drag event.
     * @param e the DragGestureEvent describing the gesture that
     * has just occurred
     */
    public void dragGestureRecognized(DragGestureEvent e) {
        //Get the first selected node
        TreePath dragPath = getSelectionPath();
        if (dragPath != null) {
            UsersTreeNode dragNode = (UsersTreeNode) (dragPath.getLastPathComponent());
            if (dragNode != null && dragNode.isTransferable()) {
                //begin the drag
                try {
                    //Get the Transferable Object
                    //Transferable transferable = (Transferable) dragNode;
                    //dragSource.startDrag(e, DragSource.DefaultCopyDrop, transferable, this);
                    super.startDrag(e, DragSource.DefaultCopyDrop, dragNode);
                } catch (InvalidDnDOperationException idoe) {
                    Log.print("(UsersDynamicTree) " + idoe);
                }
            }
        }
    }

    /**
     * Override <code>DynamicTree</code>'s method to determine
     * whether the drag is acceptable.
     * @param e The DropTargetDragEvent reports the source drop actions
     * and the user drop action that reflect the current state of the
     * drag operation.
     */
    public void dragOver(DropTargetDragEvent e) {
        Point pt = e.getLocation();
        int action = e.getDropAction();
        TreePath destinationPath = getPathForLocation(pt.x, pt.y);
        if (destinationPath != null) {

            UsersTreeNode node = (UsersTreeNode) destinationPath.getLastPathComponent();
            if (node instanceof UsersTreeNode) {

                //Log.print("(BatchingPage.dragOver drop target) " + destinationPath + "/"
                //    + node);

                // if drag and drop paths are OK, accept drop
                if (node.isDroppable()) {
                    e.acceptDrag(action);
                } else {
                    e.rejectDrag();
                }
            }
        }
    }

    /**
     * Override <code>DynamicTree</code>'s method to determine
     * whether the drop is acceptable.
     * @param dsde The DragSourceDragEvent reports the target drop action
     * and the user drop action that reflect the current state of the
     * drag operation.
     */
    public void dragOver(DragSourceDragEvent dsde) {
        //set cursor location. Needed in setCursor method
        Point pt = dsde.getLocation();
        TreePath destinationPath = getPathForLocation(pt.x, pt.y);
        if (destinationPath != null) {

            UsersTreeNode node = (UsersTreeNode) destinationPath.getLastPathComponent();
            if (node instanceof UsersTreeNode) {

                //Log.print("(BatchingPage.dragOver) drag source " + destinationPath + "/"
                //    + node);

                // if drop path is OK, accept drop
                if (node.isDroppable()) {
                    int action = dsde.getDropAction();
                    if (action == DnDConstants.ACTION_COPY) {
                        dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
                    } else if (action == DnDConstants.ACTION_MOVE) {
                        dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
                    } else {
                        dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
                    }
                }
            }
        }
    }
}
