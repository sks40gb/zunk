/* $Header: /home/common/cvsarea/ibase/dia/src/common/ExportData.java,v 1.5 2004/12/31 15:32:22 weaston Exp $ */
package com.fossa.servlet.common;

/**
 * A container for data required export of coded data.
 */
public class ExportData extends DelimiterData {

    /** name of the project */
    public String project_name;

    /** id of the volume */
    public int volume_id;
    public String volume_name;

    /** first or only batch.batch_number */
    public int batch_number;

    /** end of the batch.batch_number range */
    public int end_batch_number;

    /** output data filename */
    public String data_filename;

    /** output lfp filename */
    public String lfp_filename;

    /** output log filename */
    public String log_filename;
    
    public String isQCBatchAllowed;
    
    public String isDoculex;
    
    public String isOpticon;
    
    public String isSummation;
    
    public String isBRS;
    
    public String doValidation;
    
    public String isTSearchExport;
    
}
