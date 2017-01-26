/* $Header: /home/common/cvsarea/ibase/dia/src/beans/BatchTreeNode.java,v 1.34.6.1 2006/02/22 20:05:50 nancy Exp $ */
package beans;

import client.Global;
import common.Log;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import model.ManagedNode;
import model.TableRow;
import java.io.IOException;
import javax.swing.tree.MutableTreeNode;

/**
 * All nodes in the Project tree on the <code>BatchingPage</code> are <code>BatchTreeNode</code>s.
 * 
 * @see ui.BatchingPage
 */
public class BatchTreeNode extends ManagedNode implements Transferable {

    public static final DataFlavor BATCH_FLAVOR = new DataFlavor(BatchTreeNode.class, "batch node");
    DataFlavor[] flavors = {BATCH_FLAVOR};
    /** a project name */
    public static final String PROJECT = "project";
    /** a volume name */
    public static final String VOLUME = "volume";
    /** the name of a team */
    public static final String TEAM = "team";
    /** a unitize batch */
    public static final String UNITIZE = "unitize";
    /** the unitize batch that has been split into Coding batches */
    public static final String UNITIZE_BATCHED = "ubatched";
    /** the label node of a unitize batch */
    public static final String UNITIZE_LABEL = "ulabel";
    /** a Unitize batch that is assigned to a user */
    public static final String UNITIZE_ASSIGNED = "uassign";
    /** a Unitize batch that is in a team or user queue */
    public static final String UNITIZE_QUEUED = "uqueue";
    /** a unitize batch that is Complete, waiting being split into Coding batches */
    public static final String UNITIZE_COMPLETE = "ucomplete";
    /** a unitize batch that is in QC */
    public static final String UQC = "uqc";
    /** the label node of a uQC batch */
    public static final String UQC_LABEL = "uqclabel";
    /** a UQC batch that is assigned to a user */
    public static final String UQC_ASSIGNED = "uqcassign";
    /** a UQC batch that is in a team or user queue */
    public static final String UQC_QUEUED = "uqcqueue";
    /** a coding batch - out of unitize */
    public static final String CODING = "coding";
    /** the label node of a Coding batch */
    public static final String CODING_LABEL = "codinglabel";
    /** a Coding batch that is assigned to a user */
    public static final String CODING_ASSIGNED = "codingassign";
    /** a Coding batch that is in a team or user queue */
    public static final String CODING_QUEUED = "codingqueue";
    /** a coding batch that is in QC */
    public static final String QC = "qc";
    /** the label node of a CodingQC batch */
    public static final String QC_LABEL = "qclabel";
    /** a CodingQC batch that is assigned to a user */
    public static final String QC_ASSIGNED = "qcassign";
    /** a CodingQC batch that is in a team or user queue */
    public static final String QC_QUEUED = "qcqueue";
    /** a CodingQC batch that is through QC and QA */
    public static final String QC_COMPLETE = "qccomplete";
    /** a batch that is in QA */
    public static final String QA = "qa";
    /** the label node of a QA batch */
    public static final String QA_LABEL = "qalabel";
    /** a QA batch that is assigned to a user */
    public static final String QA_ASSIGNED = "qaassign";
    /** a QA batch that is in a user queue */
    public static final String QA_QUEUED = "qaqueue";
    /** a batch that is ready for export */
    public static final String QA_COMPLETE = "qacomplete";
    /** a document range */
    public static final String RANGE = "range";
    /** a label node, such as "User Queue," "Batches," etc. */
    public static final String LABEL = "label";
    /** a user or team queue */
    public static final String QUEUE = "queue";
    /** qa error status **/
    public static final String QA_ERROR = "qaerror";
    /** one of the above node types */
    private String type = null;
    /** key to identify this node -- see BatchingPage for use details */
    private String key = null;
    public static final String LISTING = "listing";
    public static final String LISTING_QUEUED = "listingqueue";
    public static final String LISTING_LABEL = "listinglabel";
    public static final String LISTING_ASSIGNED = "listingassigned";
    public static final String LISTING_QC = "listingqc";
    public static final String LISTING_QC_QUEUED = "listingqcqueue";
    public static final String LISTING_QC_ASSIGN = "listingqcassign";
    public static final String LISTING_COMPLETE = "listingcomplete";
    public static final String TALLY = "tally";
    public static final String TALLY_QUEUED = "tallyqueue";
    public static final String TALLY_LABEL = "tallylabel";
    public static final String TALLY_ASSIGNED = "tallyassigned";
    public static final String TALLY_COMPLETE = "tallycomplete";
    public static final String MASKING = "masking";
    public static final String MASKING_LABEL = "maskinglabel";
    public static final String MASKING_QUEUED = "maskingqueue";
    public static final String MASKING_ASSIGNED = "maskingassigned";
    public static final String MASKING_COMPLETE = "Mcomplete";
    public static final String MODIFYERRORS = "modifyerrors";
    public static final String MODIFYERRORS_QUEUED = "modifyqueue";
    public static final String MODIFYERRORS_LABEL = "modifylabel";
    public static final String MODIFYERRORS_ASSIGNED = "modifyassigned";
    public static final String MODIFYERRORS_COMPLETE = "modifycomplete";

    
    //constants
    
    
    /**
     * Create a node in batchTree, defaulting <code>allowsChildren</code> to true.
     * @param name the text the user sees in the tree, userObject
     * @param type one of the status/progress Strings
     */
    public BatchTreeNode(Object name, String type) {
        this(name, type, true);
    }

    /**
     * Create a node in batchTree.
     * @param name the text the user sees in the tree, userObject
     * @param type one of the status/progress Strings
     * @param allowsChildren true if this node can have child nodes; false otherwise
     */
    public BatchTreeNode(Object name, String type, boolean allowsChildren) {
        // Note.  name becomes the userObject.  This is different from UserTreeNode, which uses both
        super(name, allowsChildren);
        this.type = type;
        if (name instanceof TableRow) {
            // make key correspond to ManagedNode id
            int keyId = ((TableRow) name).getId();
            if (keyId != 0) {
                this.key = Integer.toString(keyId);
            }
        }
    }

    /**
     *  Return the value in the key data item.
     */
    public String getKey() {
        return key;
    }

    /**
     * Return the value in the type data item.
     * May be overridden to calculate type dynamically.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the value of the type data item.
     * @param type a String defining the node type or status/progress of the node
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Not implemented -- <code>BatchingPage</code> uses <code>DynamicTree.addObject</code>.
     */
    public void add(MutableTreeNode node) {
        throw new UnsupportedOperationException();
    }

    /**
     * Is the currently-selected node a batch that is assigned to a user?
     * @return true if the node type is [status]_ASSIGNED; false otherwise
     */
    public boolean isAssigned() {
        return getType().equals(UNITIZE_ASSIGNED)     || getType().equals(UQC_ASSIGNED) 
                || getType().equals(CODING_ASSIGNED)  || getType().equals(QC_ASSIGNED) 
                || getType().equals(QA_ASSIGNED)      || getType().equals(LISTING_QC_ASSIGN) 
                || getType().equals(MASKING_ASSIGNED) || getType().equals(MODIFYERRORS_ASSIGNED);
    }

    /**
     * Is the currently-selected node a batch that is in a team or user queue?
     * @return true if the node type is [status]_QUEUED; false otherwise
     */
    public boolean isQueued() {
        return getType().equals(UNITIZE_QUEUED)     || getType().equals(UQC_QUEUED) 
                || getType().equals(CODING_QUEUED)  || getType().equals(QC_QUEUED) 
                || getType().equals(QA_QUEUED)      || getType().equals(LISTING_QC_QUEUED) 
                || getType().equals(MASKING_QUEUED) || getType().equals(MODIFYERRORS_QUEUED);
    }

    /**
     * Is the currently-selected node a batch that is queued or assigned?
     * @return true if the node type is [status]; false otherwise
     */
    public boolean isIdle() {
        return getType().equals(UNITIZE)     || getType().equals(UQC) 
                || getType().equals(CODING)  || getType().equals(QC)
                || getType().equals(QA)      || getType().equals(LISTING_QC) 
                || getType().equals(MASKING) || getType().equals(MODIFYERRORS);
    }

    /**
     * Is the currently-selected node a label?
     * @return true if the node type is [status]_LABEL; false otherwise
     */
    public boolean isLabel() {
        return getType().equals(UNITIZE_LABEL)    || getType().equals(UQC_LABEL) 
                || getType().equals(CODING_LABEL) || getType().equals(QC_LABEL) 
                || getType().equals(QA_LABEL)     || getType().equals(MASKING_LABEL) 
                || getType().equals(MODIFYERRORS_LABEL);
    }

    /**
     * Does the currently-selected node have a UQC status?
     * @return true if the node type is UQC_[anytype]; false otherwise
     */
    public boolean isUQC() {
        return getType().equals(UQC) || getType().equals(UQC_QUEUED) 
                || getType().equals(UQC_LABEL) || getType().equals(UQC_ASSIGNED);
    //|| getType().equals(UNITIZE_COMPLETE);
    }

    /**
     * Is the currently-selected node an idle batch with Unitize status?
     * @return true if the node type is UNITIZE; false otherwise
     */
    public boolean isNewUnitize() {
        return getType().equals(UNITIZE);
    }

    /**
     * Check to see if the currently-selected node a Unitize batch that has been split
     * into Coding batches.  If so, these batches are not shown in the tree.
     * @return true if the node type is UNITIZE_BATCHED; false otherwise
     */
    public boolean isUnitizeBatched() {
        return getType().equals(UNITIZE_BATCHED);
    }

    /**
     * Is the currently-selected node a Unitize batch that has finished UQC?
     * @return true if the node type is UNITIZE_COMPLETE; false otherwise
     */
    public boolean isUnitizeComplete() {
        return getType().equals(UNITIZE_COMPLETE);
    }

    /**
     * Is the currently-selected node associated with a Unitize batch?
     * @return true if the node type is UNITIZE_[anytype]; false otherwise
     */
    public boolean isUnitize() {
        return getType().equals(UNITIZE)            || getType().equals(UNITIZE_BATCHED) 
                || getType().equals(UNITIZE_QUEUED) || getType().equals(UNITIZE_LABEL) 
                || getType().equals(UNITIZE_ASSIGNED);
    }

    /**
     * Is the currently-selected node associated with a Coding batch?
     * @return true if the node type is CODING_[anytype]; false otherwise
     */
    public boolean isCoding() {
        return getType().equals(CODING)           || getType().equals(CODING_QUEUED) 
                || getType().equals(CODING_LABEL) || getType().equals(CODING_ASSIGNED);
    }

    public boolean isListing() {
        return getType().equals(LISTING)           || getType().equals(LISTING_QUEUED) 
                || getType().equals(LISTING_LABEL) || getType().equals(LISTING_ASSIGNED);
    }

    public boolean isListingComplete() {
        return getType().equals(LISTING_COMPLETE);
    }

    public boolean isTally() {
        return getType().equals(TALLY) || getType().equals(TALLY_ASSIGNED) 
                || getType().equals(TALLY_LABEL) || getType().equals(TALLY_QUEUED);
    }

    public boolean isTallyComplete() {
        return getType().equals(TALLY_COMPLETE);
    }

    public boolean isModifyError() {
        return getType().equals(MODIFYERRORS) || getType().equals(MODIFYERRORS_ASSIGNED) 
                || getType().equals(MODIFYERRORS_LABEL) || getType().equals(MODIFYERRORS_QUEUED);
    }

    /**
     * Is the currently-selected node associated with a QC batch?
     * @return true if the node type is QC_[anytype]; false otherwise
     */
    public boolean isQC() {
        return getType().equals(QC) || getType().equals(QC_QUEUED) || getType().equals(QC_LABEL) 
                || getType().equals(QC_ASSIGNED) || getType().equals(QC_COMPLETE);
    }

    public boolean isLQC() {
        return getType().equals(LISTING_QC) || getType().equals(LISTING_QC_ASSIGN) || getType().equals(LISTING_COMPLETE);
    }

    /**
     * Is the currently-selected node associated with a QA batch?
     * @return true if the node type is QA_[anytype]; false otherwise
     */
    public boolean isQA() {
        return getType().equals(QA) || getType().equals(QA_QUEUED) || getType().equals(QA_LABEL) 
                || getType().equals(QA_ASSIGNED) || getType().equals(QA_COMPLETE) || getType().equals(QA_ERROR);
    }

    public boolean isQAComplete() {
        return getType().equals(QA_COMPLETE);
    }

    public boolean isMasking() {
        return getType().equals(MASKING) || getType().equals(MASKING_QUEUED) 
                || getType().equals(MASKING_LABEL) || getType().equals(MASKING_ASSIGNED);
    }

    public boolean isMaskingComplete() {
        return getType().equals(MASKING_COMPLETE);
    }

    /**
     * Is the currently-selected node a team name?
     * @return true if the node type is TEAM; false otherwise
     */
    public boolean isTeam() {
        return getType().equals(TEAM);
    }

    /**
     * Return the value at the given index.
     * @param index the row of the batch model
     * @return the data stored in the batch model at index
     */
    public String getValue(int index) {
        Object obj = super.getUserObject();
        if (obj instanceof TableRow) {
            TableRow tr = (TableRow) obj;
            return (String) tr.getValue(index);
        }
        return "";
    }

    /**
     * Return the type that corresponds to the users table role.
     */
    public String getTypeLevel() {
        //Log.print("(BatchTreeNode.getTypeLevel) type is " + getType());
        if (isUnitize()) {
            return "unitize";
        } else if (isUQC()) {
            return "uqc";
        } else if (isCoding()) {
            return "coding";
        } else if (isQC()) {
            return "codingqc";
        } else if (isQA()) {
            return "qa";
        } else if (isLQC()) {
            return "listingqc";
        } else if (isMasking()) {
            return "masking";
        } else if (isVolume()) {
            return VOLUME;
        }
        return "";
    }

    /**
     * Is the currently-selected node a batch whose status is Complete?
     * @return true if the node type is [status]_COMPLETE; false otherwise
     */
    public boolean isComplete() {
        return getType().equals(QC_COMPLETE) || getType().equals(UNITIZE_COMPLETE) || getType().equals(QA_COMPLETE) || getType().equals(QA_ERROR);
    }

    /**
     * Is the currently-selected node an attachment range?
     * @return true if the node type is RANGE; false otherwise
     */
    public boolean isAttachmentRange() {
        return getType().equals(RANGE);
    }

    /**
     *  BatchingPage.batchTree accepts no drops, so always return false.
     */
    public boolean isDroppable() {
        //Log.print("(BatchTreeNode).isDroppable? " + getType());
        return false;
    }

    /**
     * Test type to see if it is the only transferable node type, BATCH.
     * @return true if type = BATCH.
     */
    public boolean isTransferable() {
        if (!Global.theServerConnection.getPermissionAdminBatch()) {
            // user does not have privilege to manage batches
            Log.print("(BatchTreeNode).isTransferable? - PERMISSION DENIED");
            return false;
        }        
        return getType().equals(UNITIZE)           || getType().equals(UNITIZE_QUEUED) 
                || getType().equals(UQC)           || getType().equals(UQC_QUEUED) 
                || getType().equals(CODING)        || getType().equals(CODING_QUEUED) 
                || getType().equals(QC)            || getType().equals(QC_QUEUED) 
                || getType().equals(MASKING)       || getType().equals(MASKING_QUEUED) 
                || getType().equals(VOLUME)        || getType().equals(MODIFYERRORS);
    }

    /**
     * Returns an object which represents the data to be transferred.
     * The class of the object returned is defined by the representation
     * class of the flavor.
     * @param flavor - the requested flavor for the data
     * @throws IOException - if the data is no longer available in the
     * requested flavor. 
     * @throws UnsupportedFlavorException if the requested data flavor is not
     * supported.
     */
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException,
            IOException {
        //Log.print("(BatchTreeNode.getTransferData) " + flavor);
        if (BATCH_FLAVOR == flavor) {
            return this;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /**
     * Returns an array of DataFlavor objects indicating the flavors in which the
     * data can be provided. The array should be ordered according to
     * preference for providing the data (from most richly descriptive
     * to least descriptive).
     * @return an array of data flavors in which this data can be transferred
     */
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    /**
     * Returns whether or not the specified data flavor is supported
     * for this object.
     * @param flavor - the requested flavor for the data  
     * @return boolean indicating whether or not the data flavor is
     * supported and the type of this node is BATCH
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (BATCH_FLAVOR == flavor && (isBatch() || isVolume()));
    }

    /**
     * Is the currently-selected node a volume name?
     * @return true if the node type is VOLUME; false otherwise
     */
    public boolean isVolume() {
        return getType().equals(VOLUME);
    }

    /**
     * Determine if node is a batch BUT NOT ASSIGNED.
     */
    public boolean isBatch() {
        return getType().equals(QA_COMPLETE)            || getType().equals(UNITIZE) 
                || getType().equals(UNITIZE_QUEUED)     || getType().equals(UNITIZE_COMPLETE) 
                || getType().equals(UQC)                || getType().equals(UQC_QUEUED) 
                || getType().equals(CODING)             || getType().equals(CODING_QUEUED) 
                || getType().equals(QC)                 || getType().equals(QC_QUEUED) 
                || getType().equals(QC_COMPLETE)        || getType().equals(LISTING) 
                || getType().equals(LISTING_QC_QUEUED)  || getType().equals(LISTING_QUEUED) 
                || getType().equals(LISTING_QC)         || getType().equals(LISTING_COMPLETE) 
                || getType().equals(TALLY)              || getType().equals(TALLY_COMPLETE) 
                || getType().equals(QA)                 || getType().equals(QA_QUEUED) 
                || getType().equals(MASKING)            || getType().equals(MASKING_QUEUED) 
                || getType().equals(MASKING_COMPLETE)   || getType().equals(MODIFYERRORS)
                || getType().equals(QA_ERROR);
    }
    
    public boolean isQAError() {        
        return getType().equals(QA_ERROR);
    }
}

