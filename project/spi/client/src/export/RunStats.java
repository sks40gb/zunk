/* $Header: /home/common/cvsarea/ibase/dia/src/export/RunStats.java,v 1.4 2005/01/06 12:36:57 nancy Exp $ */
package export;

import java.util.ArrayList;

/**
 * This is a container for run statistics that is used to display
 * the information a the end of the run.
 */
public class RunStats {
    public ArrayList batchName = new ArrayList(); // status + " " + batch_number
    public int recordCount = 0;
    public int pageCount = 0;
    public int selectStatus = 0;
    public ArrayList message = new ArrayList();
}

