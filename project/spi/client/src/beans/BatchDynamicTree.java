/* $Header: /home/common/cvsarea/ibase/dia/src/beans/BatchDynamicTree.java,v 1.14.6.1 2006/02/22 20:05:50 nancy Exp $ */
package beans;

import common.Log;
import java.awt.Color;
import java.awt.Component;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * A TreePanel specialized for tree of batches.
 * Batch nodes are colored and use icons to represent their status.
 */
public class BatchDynamicTree extends DynamicTree {

    public BatchDynamicTree() {
        this(null);
    }

    /**
     * Create and instance of DynamicTree specialized for batches.
     * @param model a DefaultTreeModel that labels the tree root
     */
    public BatchDynamicTree(DefaultTreeModel model) {
        super(model);

        // Enable tool tips
        // I'm not quite sure I understand this, but it's taken from
        // TreeIconDemo2.java in the Swing Tutorial
        ToolTipManager.sharedInstance().registerComponent(tree);

        super.setCellRenderer(new BatchTreeRenderer());
    }

    private class BatchTreeRenderer extends DefaultTreeCellRenderer {

        public Component getTreeCellRendererComponent(JTree tree,
                Object value,
                boolean sel, boolean expanded,
                boolean leaf,
                int row,
                boolean hasFocus) {
            Component result;
            Object defaultNode = value;
            if (defaultNode instanceof BatchTreeNode) {
                BatchTreeNode valueNode = (BatchTreeNode) value;
                setTextSelectionColor(Color.white);
                if (valueNode.isQueued() || valueNode.isTeam() || (valueNode.isLabel() 
                        // Note: userObject may be a TableRow instead of a String
                        //&& ((String)valueNode.getUserObject()).indexOf("Queue") > -1)) {
                        && (valueNode.getUserObject().toString()).indexOf("Queue") > -1)) {
                    setTextNonSelectionColor(Color.orange.darker());
                    setBackgroundSelectionColor(Color.orange.darker());
                } else if (valueNode.isAssigned()) {
                    setTextNonSelectionColor(Color.blue.darker());
                    setBackgroundSelectionColor(Color.blue.darker());
                } else if (valueNode.isComplete()) {
                    setTextNonSelectionColor(Color.red.darker());
                    setBackgroundSelectionColor(Color.red.darker());
                    setToolTipText("Completed Batch");
                } else if (valueNode.isListingComplete() || valueNode.isTallyComplete() || valueNode.isMaskingComplete()) {
                    setTextNonSelectionColor(Color.red.darker());
                    setBackgroundSelectionColor(Color.red.darker());
                //setToolTipText("Completed Batch");
                } else {
                    setTextNonSelectionColor(Color.black);
                    setBackgroundSelectionColor(Color.black);
                //setToolTipText("Other");
                }
                // Must set colors before this call and icons after!
                result = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

                if (valueNode.isUnitize()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/newdoc.gif")));
                    setToolTipText("Unitize Batch");
                } else if (valueNode.isUQC()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/uqc.gif")));
                    setToolTipText("Unitized batch in QC");
                } else if (valueNode.isUnitizeComplete()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/uqc.gif")));
                    setToolTipText("Completed Unitize QC");
                } else if (valueNode.isCoding()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/coding.gif")));
                    setToolTipText("Coding Batch");
                } else if (valueNode.isQC()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/magnify.gif")));
                    setToolTipText("Coding Batch in QC");
                } else if (valueNode.isQAError()) {
                        setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/reject.gif")));
                        setToolTipText("Rejected Batch");
                }else if(valueNode.isQA() && valueNode.isComplete()) {
                        setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/star.gif")));
                        setToolTipText("Completed Batch");
                } 
                else if (valueNode.isQA()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/check.gif")));
                    setToolTipText("QA Batch");
                } else if (valueNode.getType().equals(BatchTreeNode.VOLUME)) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cd.gif")));
                    setToolTipText("Volume");
                } else if (valueNode.getType().equals(BatchTreeNode.RANGE)) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/document.gif")));
                } else if (valueNode.isComplete()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/star.gif")));
                    setToolTipText("Completed Batch");
                } else if (valueNode.isTeam()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/papdoll.gif")));
                    setToolTipText("Completed Batch");
                } else if (valueNode.isListing()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/listing.gif")));
                //setToolTipText("Listing Batch");
                } else if (valueNode.isListingComplete()) {

                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/listing.gif")));
                    setToolTipText("Listing Complete");
                } else if (valueNode.isTally()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/tally.gif")));
                    setToolTipText("Tally Batch.....");
                } else if (valueNode.isTallyComplete()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/tally.gif")));
                    setToolTipText("Tally Complete");
                } else if (valueNode.isModifyError()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/modifyerror.gif")));
                    setToolTipText("ModifyError Batch");
                } else if (valueNode.isMasking()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/masking.gif")));
                    setToolTipText("Masking Batch");
                } else if (valueNode.isMaskingComplete()) {
                    setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/masking.gif")));
                    setToolTipText("Masking Complete");
                } else {
                    setOpenIcon(new javax.swing.ImageIcon(getClass().getResource("/images/folder-open.gif")));
                    setClosedIcon(new javax.swing.ImageIcon(getClass().getResource("/images/folder-closed.gif")));
                    setToolTipText(null);
                }
            } else {
                result = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            }
            return result;
        }
    }

    /**
     * Drop is not allowed on batch nodes, so just reject it.
     */
    public void drop(DropTargetDropEvent e) {
        e.rejectDrop();
    }

    /**
     * A DragGestureRecognizer has detected a platform-dependent drag initiating
     * gesture and is notifying this listener in order for it to initiate the action
     * for the user.  Start the drag operation on a valid node.
     * 
     * @param e the DragGestureEvent describing the gesture that has just occurred
     */
    public void dragGestureRecognized(DragGestureEvent e) {
        //Get the selected node
        TreePath dragPath = getSelectionPath();

        boolean isAllTrue = true;
        List nodeList = new ArrayList();
        List flagList = new ArrayList();
        BatchTreeNode dragNode = (BatchTreeNode) (dragPath.getLastPathComponent());
        BatchTreeNode parentNode = ((BatchTreeNode) dragNode.getParent());
        int childCount = ((BatchTreeNode) dragNode.getParent()).getChildCount();

        for (int i = 0; i < childCount; i++) {
            String nodePath = parentNode.getChildAt(i).toString();
            if (nodePath.contains("L1")) {
                nodeList.add(nodePath);
            }
        }
        if (nodeList.size() != 0) {
            for (Object obj : nodeList) {
                String[] valueArray = ((String) obj).split(",");
                String[] status = valueArray[0].split("Batch");
                System.out.println("status" + status[0].trim());
                if (status[0].trim().equals("MComplete") || status[0].trim().equals("LComplete") || status[0].trim().equals("TComplete") || status[0].trim().equals("QA Complete")) {
                    Log.print("now u can do L2 ............................!");
                    flagList.add(true);
                } else {
                    Log.print("still u cannot do L2 ............................!");
                    flagList.add(false);
                }
            }
            for (int j = 0; j < flagList.size(); j++) {
                if (flagList.get(j).equals(false)) {
                    isAllTrue = false;
                }
            }
        }
        String nodePath = dragPath.getLastPathComponent().toString();

        if (dragPath != null && !nodePath.contains("L2")) {
            dragNode(e, dragPath);
        } else if (dragPath != null && nodePath.contains("L2")) {
            if (isAllTrue) {
                isAllTrue = false;
                dragNode(e, dragPath);
            }
        }
    }

    private void dragNode(DragGestureEvent e, TreePath dragPath) {
        BatchTreeNode dragNode = (BatchTreeNode) (dragPath.getLastPathComponent());
        if (dragNode != null && dragNode.isTransferable()) {
            //begin the drag

            try {
                //Transferable transferable = (Transferable) dragNode;
                super.startDrag(e, DragSource.DefaultCopyDrop, dragNode);
            //e.startDrag(DragSource.DefaultCopyDrop, transferable, this);
            } catch (InvalidDnDOperationException idoe) {
                Log.print("(BatchDynamicTree) " + idoe);
            }
        }
    }

    /**
     * Drag and drop is not allowed on batch nodes, so reject a drag.
     */
    public void dragOver(DropTargetDragEvent e) {
        e.rejectDrag();
    }
}
