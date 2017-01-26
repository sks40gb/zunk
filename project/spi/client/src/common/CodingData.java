
/* $Header: /home/common/cvsarea/ibase/dia/src/common/CodingData.java,v 1.9.8.1 2006/05/10 14:25:11 nancy Exp $ */
package common;

import java.util.Map;

/**
 * A container for data required for display of a coded document.
 * Data for the corresponding image are included (as parent class).
 */
public class CodingData extends ImageData{

    /** First bates number in the range */
    public String firstBatesOfRange;

    /** Last bates number in the range */
    public String lastBatesOfRange;

    /** Bates number of the current page */
    public String currentBatesOfChild;

    /** First bates number in the child */
    public String firstBatesOfChild;

    /** Last bates number in the child */
    public String lastBatesOfChild;

    /** Relative position of child in batch */
    public int batchChildPosition;

    /** Count of children in batch */
    public int batchChildCount;

    /** Batch number.  Used for title bar -- may vary for admin, QA, binder */
    public int batchNumber;

    /** the active group for the current page */
    public int activeGroup;

    /** 
     * Split indicator.  If true, this child is the result of splitting
     * a document into multiple subdocuments.
     */
    public boolean isSplit;

    /** A Map (normally a HashMap) containing coded values */
    public Map valueMap = null;

    /**
     * A Map (normally a HashMap) containing error flags for QC and QA.
     * value = "Yes" for coder error, "No" for not coder error.
     */
    public Map errorFlagMap = null;
    
    public Map errorTypeMap = null;
    
    // added for F10 
    
     public String query_raised ="";
     
     public String query_answered ="";
     
     //treatment_level column shows the process level(i.e, L1 /L2)
    public String treatment_level ="";
    
    //listing_marking 
    public String listing_marking ="";
}
