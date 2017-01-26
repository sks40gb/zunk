/* $Header: /home/common/cvsarea/ibase/dia/src/beans/UsersTreeNode.java,v 1.32.6.1 2006/03/09 12:09:16 nancy Exp $ */
package beans;

import client.Global;
import common.Log;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import model.ManagedNode;
import model.TableRow;


import javax.swing.tree.MutableTreeNode;

/**
 * A <code>UsersTreeNode</code> represents a node in the tree of users
 * and teams queues.
 * All nodes in the Users tree on the <code>BatchingPage</code> are <code>UsersTreeNode</code>s.
 * @see ui.BatchingPage
 */
public class UsersTreeNode extends ManagedNode implements Transferable {

    public static final DataFlavor USER_FLAVOR = new DataFlavor(UsersTreeNode.class, "users node");
    DataFlavor[] flavors = {USER_FLAVOR};
    /** a batch that is in unitize */
    public static final String UNITIZE = "unitize";
    /** the label node of a unitize batch */
    public static final String UNITIZE_LABEL = "ulabel";
    /** a Unitize batch that is assigned to a user */
    public static final String UNITIZE_ASSIGNED = "uassign";
    /** a Unitize batch that is in a team or user queue */
    public static final String UNITIZE_QUEUED = "uqueue";
    /** a batch that is in unitize QC */
    public static final String UQC = "uqc";
    /** the label node of a uQC batch */
    public static final String UQC_LABEL = "uqclabel";
    /** a UQC batch that is assigned to a user */
    public static final String UQC_ASSIGNED = "uqcassign";
    /** a UQC batch that is in a team or user queue */
    public static final String UQC_QUEUED = "uqcqueue";
    /** a coding batch */
    public static final String CODING = "coding";
    /** the label node of a Coding batch */
    public static final String CODING_LABEL = "codinglabel";
    /** a Coding batch that is assigned to a user */
    public static final String CODING_ASSIGNED = "codingassign";
    /** a Coding batch that is in a team or user queue */
    public static final String CODING_QUEUED = "codingqueue";
    /** a coded batch that is in QC */
    public static final String QC = "qc";
    /** the label node of a CodingQC batch */
    public static final String QC_LABEL = "qclabel";
    /** a CodingQC batch that is assigned to a user */
    public static final String QC_ASSIGNED = "qcassign";
    /** a CodingQC batch that is in a team or user queue */
    public static final String QC_QUEUED = "qcqueue";
    /** a batch that is in QA */
    public static final String QA = "qa";
    /** the label node of a QA batch */
    public static final String QA_LABEL = "qalabel";
    /** a QA batch that is assigned to a user */
    public static final String QA_ASSIGNED = "qaassign";
    /** a QA batch that is in a user queue */
    public static final String QA_QUEUED = "qaqueue";
    /** the name of a team */
    public static final String TEAM = "team";
    /** the parent node "Teams" */
    public static final String TEAM_LABEL = "teamlabel";
    /** a user name node */
    public static final String USER = "user";
    /** a volume name queued to a team */
    public static final String VOLUME = "volume";
    /** the parent node "Volumes" */
    public static final String VOLUME_LABEL = "volumelabel";
    /** one of the above node types */
    private String type = null;
    /** key into data */
    // appears to be the batch id
    private int id = -1;
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
    public static final String MASKING_COMPLETE = "maskingcomplete";
    public static final String MODIFYERRORS = "Modifyerrors";
    public static final String MODIFYERRORS_LABEL = "Modifyerrors";
    public static final String MODIFYERRORS_QUEUED = "Modifyerrors";
    public static final String MODIFYERRORS_ASSIGNED = "Modifyerrors";
    public static final String MODIFYERRORS_COMPLETE = "Modifyerrors";

    /**
     * Create a node in userTree with no key and allowing children.
     * @param label the text the user sees in the tree, userObject
     * @param type one of the status/progress Strings
     */
    public UsersTreeNode(Object label, String type) {
        this(label, type, -1, true);
    }

    /**
     * Create a node in userTree with no key.
     * @param label the text the user sees in the tree, userObject
     * @param type one of the status/progress Strings
     * @param allowsChildren true if this node can have child nodes; false otherwise
     */
    public UsersTreeNode(Object label, String type, boolean allowsChildren) {
        this(label, type, -1, allowsChildren);
    }

    /**
     * Create a node in userTree.
     * @param label the text the user sees in the tree, userObject
     * @param type one of the status/progress Strings
     * @param id key into the node
     * @param allowsChildren true if this node can have child nodes; false otherwise
     */
    public UsersTreeNode(Object label, String type, int id, boolean allowsChildren) {
        super(label, allowsChildren);
        this.id = id;
        this.type = type;
    }

    /**
     * Return the value at the given index.
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
     *  Return the value in the id data item.
     */
    public int getId() {
        return super.getRowId();
    }

    /**
     *  Return the value in the type data item.
     *  May be overridden, if type is calculated dynamically.
     */
    public String getType() {
        return type;
    }

    /**
     * Is the currently-selected node a team name?
     * @return true if the node type is TEAM; false otherwise
     */
    public boolean isTeam() {
        return getType().equals(TEAM);
    }

    /**
     * Is the currently-selected node a volume name?
     * @return true if the node type is VOLUME; false otherwise
     */
    public boolean isVolume() {
        return getType().equals(VOLUME);
    }

    /**
     * Is the currently-selected node a users name?
     * @return true if the node type is USER; false otherwise
     */
    public boolean isUser() {
        return getType().equals(USER);
    }

    /**
     * Is the currently-selected node assigned?
     * @return true if the node type is [status]_ASSIGNED; false otherwise
     */
    public boolean isAssigned() {
        return getType().equals(CODING_ASSIGNED) || getType().equals(UNITIZE_ASSIGNED) 
                || getType().equals(UQC_ASSIGNED) || getType().equals(QC_ASSIGNED) 
                || getType().equals(QA_ASSIGNED) || getType().equals(MASKING_ASSIGNED)
                || getType().equals(MODIFYERRORS_ASSIGNED) ||getType().equals(LISTING_ASSIGNED)
                || getType().equals(TALLY_ASSIGNED);
    }

    /**
     * Is the currently-selected node queued?
     * @return true if the node type is [status]_QUEUED; false otherwise
     */
    public boolean isQueued() {
        return getType().equals(CODING_QUEUED) || getType().equals(UNITIZE_QUEUED) 
                || getType().equals(UQC_QUEUED) || getType().equals(QC_QUEUED) 
                || getType().equals(QA_QUEUED) || getType().equals(MASKING_QUEUED) 
                || getType().equals(MODIFYERRORS_QUEUED)|| getType().equals(LISTING_QUEUED)
                || getType().equals(TALLY_QUEUED);
    }

    /**
     * Is the currently-selected node neither queued nor assigned?
     * @return true if the node type is [status]; false otherwise
     */
    public boolean isIdle() {
        return getType().equals(CODING) || getType().equals(UNITIZE) || getType().equals(UQC) 
                || getType().equals(QC) || getType().equals(QA)|| getType().equals(LISTING)
                || getType().equals(TALLY);
    }

    /**
     * Is the currently-selected node a label?
     * @return true if the node type is [status]_LABEL; false otherwise
     */
    public boolean isLabel() {
        return getType().equals(CODING_LABEL) || getType().equals(VOLUME_LABEL) 
                || getType().equals(UNITIZE_LABEL) || getType().equals(UQC_LABEL) 
                || getType().equals(QC_LABEL) || getType().equals(QA_LABEL) 
                || getType().equals(MASKING_LABEL) || getType().equals(MODIFYERRORS_LABEL)|| getType().equals(LISTING_LABEL)
                || getType().equals(TALLY_LABEL);
    }

    /**
     * Is the currently-selected node A Unitize batch?
     * @return true if the node type is UNITIZE_[status]; false otherwise
     */
    public boolean isUnitize() {
        return getType().equals(UNITIZE) || getType().equals(UNITIZE_QUEUED) 
                || getType().equals(UNITIZE_LABEL) || getType().equals(UNITIZE_ASSIGNED);
    }

    /**
     * Is the currently-selected node a Unitize batch in QC?
     * @return true if the node type is UQC_[status]; false otherwise
     */
    public boolean isUQC() {
        return getType().equals(UQC) || getType().equals(UQC_QUEUED) || getType().equals(UQC_LABEL) 
                || getType().equals(UQC_ASSIGNED);
    }

    /**
     * Is the currently-selected node a Coding batch?
     * @return true if the node type is CODING_[status]; false otherwise
     */
    public boolean isCoding() {
        return getType().equals(CODING_QUEUED) || getType().equals(CODING) 
                || getType().equals(CODING_LABEL) || getType().equals(CODING_ASSIGNED);
    }

    /**
     * Is the currently-selected node a Coding batch in QC?
     * @return true if the node type is QC_[status]; false otherwise
     */
    public boolean isQC() {
        return getType().equals(QC_QUEUED) || getType().equals(QC) || getType().equals(QC_LABEL) 
                || getType().equals(QC_ASSIGNED);
    }

    /**
     * Is the currently-selected node a batch in QA?
     * @return true if the node type is QA_[status]; false otherwise
     */
    public boolean isQA() {
        return getType().equals(QA_QUEUED) || getType().equals(QA) || getType().equals(QA_LABEL) 
                || getType().equals(QA_ASSIGNED);
    }

    public boolean isMasking() {
        return getType().equals(MASKING) || getType().equals(MASKING_QUEUED) 
                || getType().equals(MASKING_LABEL) || getType().equals(MASKING_ASSIGNED) 
                || getType().equals(MASKING_COMPLETE);
    }

    public boolean isModifyErrors() {
        return getType().equals(MODIFYERRORS) || getType().equals(MODIFYERRORS_ASSIGNED) 
                || getType().equals(MODIFYERRORS_LABEL) || getType().equals(MODIFYERRORS_QUEUED);
    }
    
    //Listing
    public boolean isListing() {
        return getType().equals(LISTING) || getType().equals(LISTING_QUEUED) 
                || getType().equals(LISTING_LABEL) || getType().equals(LISTING_ASSIGNED) 
                || getType().equals(LISTING_COMPLETE);
    }
    //Tally
    public boolean isTally() {
        return getType().equals(TALLY) || getType().equals(TALLY_QUEUED) 
                || getType().equals(TALLY_LABEL) || getType().equals(TALLY_ASSIGNED) 
                || getType().equals(TALLY_COMPLETE);
    }
    /**
     * Return the getType() that corresponds to the users table role.
     * @return a String that is the type
     */
    public String getTypeLevel() {
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
        }
        return "";
    }

    /**
     * Not used.
     */
    public void add(MutableTreeNode node) {
        throw new UnsupportedOperationException();
    }

    /**
     * If the node type is one that can be dropped upon, return true.
     * @return true if type is a TEAM OR USER
     */
    public boolean isDroppable() {
        return getType().equals(TEAM) || getType().equals(USER);
    }

    /**
     * Test type to see if it is the transferable node types.
     * @return true if type = [status]_QUEUED.
     */
    public boolean isTransferable() {
        if (!Global.theServerConnection.getPermissionAdminBatch()) {
            // user does not have privilege to manage batches
            Log.print("(UsersTreeNode).isTransferable? - PERMISSION DENIED");
            return false;
        }
        Log.print("(UsersTreeNode).isTransferable? " + getType());
        return getType().equals(CODING_QUEUED) || getType().equals(UNITIZE_QUEUED) 
                || getType().equals(UQC_QUEUED) || getType().equals(QC_QUEUED) 
                || getType().equals(QA_QUEUED) || getType().equals(MASKING) 
                || getType().equals(MASKING_QUEUED);
    }

    /**
     * Returns an object which represents the data to be transferred
     * The class of the object returned is defined by the representation
     * class of the flavor.
     * @param flavor - the requested flavor for the data
     * @throws IOException - if the data is no longer available in the
     * requested flavor. 
     * @throws UnsupportedFlavorException - if the requested data flavor is not
     * supported.
     */
    public synchronized Object getTransferData(DataFlavor flavor) {
        if (USER_FLAVOR == flavor) {
            return this;
        }
        return null;
    }

    /**
     * Returns an array of DataFlavor objects indicating the flavors the
     * data can be provided in. The array should be ordered according to
     * preference for providing the data (from most richly descriptive
     * to least descriptive).
     * @return an array of data flavors in which this data can be transferred
     */
    public synchronized DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    /**
     * Returns whether or not the specified data flavor is supported
     * for this object.
     * @param flavor - the requested flavor for the data  
     * @return boolean indicating whether or not the data flavor is
     * supported and the type of this node is BATCH or ASSIGNMENT
     */
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return (USER_FLAVOR == flavor && isBatch());
    }

    /**
     * Is the current node any sort of batch?
     * @return true or false
     */
    public boolean isBatch() {
        return getType().equals(CODING) || getType().equals(CODING_QUEUED) 
                || getType().equals(CODING_ASSIGNED) || getType().equals(UNITIZE) 
                || getType().equals(UNITIZE_QUEUED) || getType().equals(UNITIZE_ASSIGNED) 
                || getType().equals(UQC) || getType().equals(UQC_QUEUED) 
                || getType().equals(UQC_ASSIGNED) || getType().equals(QC) 
                || getType().equals(QC_QUEUED) || getType().equals(QC_ASSIGNED) 
                || getType().equals(QA) || getType().equals(QA_QUEUED) 
                || getType().equals(QA_ASSIGNED) || getType().equals(MASKING) 
                || getType().equals(MASKING_QUEUED) || getType().equals(MASKING_COMPLETE) 
                || getType().equals(MASKING_ASSIGNED) || getType().equals(MODIFYERRORS) 
                || getType().equals(MODIFYERRORS_QUEUED) || getType().equals(MODIFYERRORS_ASSIGNED)
                || getType().equals(LISTING) 
                || getType().equals(LISTING_QUEUED) || getType().equals(LISTING_ASSIGNED);
    }
}

