/* $Header: /home/common/cvsarea/ibase/dia/src/export/DiaExportFromGui.java,v 1.11.6.4 2006/08/23 19:04:52 nancy Exp $ */
package export;


import common.ExportData;
import common.Log;

import common.ProjectFieldsData;
import dbload.XrefConstants;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;



public class DiaExportFromGui implements CsvConstants, XrefConstants {

    CsvWriter csv = null;
    //ValueData text;

    private static String dataFilename     = null;
    private static String dataPath         = null;
    private static String lfpFilename      = null;
    private static String lfpPath          = null;

    //boolean debug           = false;
    //boolean log             = false;

    //static String a_codinghost     = "localhost";
    //static int a_codingport        = 3306;
    //static String a_codingdb       = "codingdb";
    //static String a_codinguser     = "dia";
    //static String a_codingpwd      = "dia4ibase";

    /**
     * The name of the project to be exported.  If no batch names are provided,
     * the entire project will be exported.
     * -p --project
     */ 
    //String a_project_name   = "";
    /**
     * The name of the volume to be exported.  If no batch names are provided,
     * the entire volume will be exported.
     * -v --volume
     */ 
    //String a_volume_name   = "";
    /**
     * The batch.batch_number of the batch to be exported.
     * -b --batch
     */
    //int a_batch_number     = 0;
    /**
     * The batch.batch_number that marks the end of the export range.
     * -eb --endbatch
     */
    //int a_end_batch_number = Integer.MAX_VALUE;

    /**
     * Export all data, even if it hasn't passed QC.
     * --force
     */
    //static boolean a_force         = false;

    /**
     * Uppercase everything or uppercase names.
     * TBD: uppercase projectfields.field_name.
     * --uppercase
     * --uppercasenames
     */
    //boolean a_uppercase = false;
    //boolean a_uppercase_names = false;

    /**
     * Delimiters for name export.
     * -n --namedelimiter
     * -o --orgdelimiter
     * --excludemiddleinitial
     * --firstnameinitial
     */
    //String a_name_delimiter = ", ";
    //String a_org_delimiter = " / ";
    //String a_name_mask = "";
    //boolean a_include_mi = true;
    //boolean a_first_name_initial = false;

    /**
     * Delimiters to use in csv output file.
     * -fd --fielddelimiter
     * -q  --quote
     * -s  --separator
     */
    //String a_field_delimiter = "|";
    //String a_text_qualifier = "~";
    //String a_value_separator = ";";

    /**
     * The format to use for all output dates.
     * -d --dateformat
     */
    //String a_date_format    = "MM/dd/yyyy";

    /**
     * Dates in db use zeroes for missing values; use these values instead.
     * Can be null or "".
     * -my --missingyear
     * -mm --missingmonth
     * -mday --missingday
     * -md --missingdate
     **/
    //String a_missing_year   = "0000";
    //String a_missing_month  = "00";
    //String a_missing_day    = "00";
    //String a_missing_date_character   = "0";

    ProjectFieldsData[] fields;
    private static ResultSet rs;
    PageissueData[] pageissues;
    ExportData data;
    private RunStats stats;
    private int xrefFormat;
    
    public DiaExportFromGui(ProjectFieldsData[] fields, ResultSet rs
                            , PageissueData[] pageissues
                            , ExportData data, RunStats stats
                            , int xrefFormat) {
        Log.print("(DiaExportFromGui)");
        this.fields = fields;
        //this.values = values;
        this.rs = rs;
        this.pageissues = pageissues;
        this.data = data;
        this.stats = stats;
        this.xrefFormat = xrefFormat;
    }

    public void run() {
        if (data.data_filename.length() > 0) {
            try {
                if (fields == null) {
                    Log.print("(ExportFromGui.run) fields is null");
                }
                if (pageissues == null) {
                    Log.print("(ExportFromGui.run) pageissues is null");
                }
                csv = new CsvWriter(data.data_filename, fields, pageissues
                                    , data.field_delimiter, data.text_qualifier
                                    , data.value_separator
                                    , data.missing_year, data.missing_month
                                    , data.missing_day, data.date_format
                                    , data.uppercase.equals("Yes") ? true : false
                                    , data.uppercase_names.equals("Yes") ? true : false
                                    , data.name_mask1, data.name_mask2, data.name_mask3, data.name_mask4
                                    , data.unitizeOnly.equals("Yes") ? true : false
                                    , data.nullAttachments.equals("Yes") ? true : false
                                    , data.brs_format.equals("Yes") ? true : false
                                    //, dia.a_first_name_initial, dia.a_include_mi
                                    , stats);
                // Values and fields entries will be in order by child, projectfield.sequence,
                // then value.seq.
                // There will be 0 to n values per fields entry.
                int fieldsIndex = 0;
                int pageIndex = 0;
                //int valueIndex = 0;
                ValueData value = loadValueData(rs);

                for (pageIndex = 0 ; pageIndex < pageissues.length; pageIndex++) {
                    //Log.print(value.childLft + " > page[" + pageIndex + "]" + pageissues[pageIndex].lft);
                    if (value.childLft > pageissues[pageIndex].lft) {
                        if (! pageissues[pageIndex].boundaryFlag.equals("")) { // is a child
                            csv.delimitEmptyChild(pageIndex);
                        }
                    } else if (value.childLft == pageissues[pageIndex].lft) {
                       // get the index of the projectfield that goes with this value
                        while (value.childLft == pageissues[pageIndex].lft) {
                            fieldsIndex = getFieldIndex(fields, value.projectfieldsSequence, fieldsIndex);
                            csv.writeValue(pageIndex, value, (ProjectFieldsData)fields[fieldsIndex]);
                            value = loadValueData(rs);
                        }
                    }
                }
                csv.close();
            } catch (IOException e) {
                System.out.println("DB Error: "+e); 
                //if (dia.debug | dia.log ) Log.write("DB Error: "+e);
                return;
            }
        }
        if (data.lfp_filename.length() > 0) {
            //
            // Build Cross Reference Data
            //
            if (data.lfp_filename != null
                && data.lfp_filename.length() > 0) {
                Log.print("Build Cross Reference " + data.lfp_filename);

                XrefWriter xwriter;
                switch (xrefFormat) {
                case DOCULEX:
                    xwriter = new XrefWriterDOCULEX(data.lfp_filename, stats);
                    break;
                case OPTICON:
                    xwriter = new XrefWriterOPTICON(data.lfp_filename, stats);
                    break;
                case SUMMATION:
                    xwriter = new XrefWriterSUMMATION(data.lfp_filename, stats);
                    break;
                case BRS:
                    xwriter = new XrefWriterBRS(data.lfp_filename, stats);
                    break;
                default:
                    xwriter = new XrefWriterLFP(data.lfp_filename, stats);
                    break;
                }

                try {
                    xwriter.writeXref(pageissues);
                } catch (IOException e) {
                    System.out.println("DB Error: "+e); 
                    //if (dia.debug | dia.log ) Log.write("DB Error: "+e);
                    return;
                }
                //if (dia.log) Log.write( "Successful LFP Creation");
            }
        }
        // write job statistics
        int i;
        System.out.println("\n" + stats.batchName.size() + " Batches:");
        for (i = 0; i < stats.batchName.size(); i++) {
             System.out.println("    " + stats.batchName.get(i));
        }
        System.out.println("\n\n" + data.project_name + "  " + stats.recordCount + " records/"
                  + stats.pageCount + " pages");
        if (fields != null) {
            System.out.println("\nThe field order is:\n");
            for (i =0; i < fields.length; i++) {
                System.out.println("    " + fields[i].fieldName.toUpperCase());
            }
            System.out.println("\nDelimiters are " + data.field_delimiter
                      + data.text_qualifier + " and the date is formatted "
                      + data.date_format.toLowerCase() + ".\n");
        }
        if (data.log_filename != null
            && data.log_filename.length() > 0) {
            Log.print("Write Log " + data.log_filename);
            try {
                csv = new CsvWriter(data.log_filename, fields, data.field_delimiter
                                    , data.text_qualifier, data.value_separator
                                    , data.date_format, stats);
                csv.writeLog(data.project_name);
                csv.close();
            } catch (IOException e) {
                System.out.println("DB Error: "+e); 
                return;
            }
        }
    }

    private ValueData loadValueData(ResultSet rs) {
        ValueData vd = new ValueData();
        try {
            if (rs != null // export unitize only
                && rs.next()) {
                vd.childLft = rs.getInt(2);
                vd.projectfieldsSequence = rs.getInt(3);
                vd.sequence = rs.getInt(4);
                vd.fieldName = rs.getString(1);
                vd.value = rs.getString(5).trim();
                System.out.println("vd.value : --------------------------> " + vd.value);
            } else {
                vd.childLft = Integer.MAX_VALUE;
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: "+e); 
        }
        return vd;
    }

    public RunStats getStats() {
        return stats;
    }

    /**
     * Find the position of the given field_name in an arrayList of ProjectFieldData.
     * @param fields - an arrayList of ProjectFieldData's for the project being exported
     * @param sequence - the sequence for which to search
     * @param idx - the start position of the search
     * @return the index of the given name in the fields ArrayList
     */
    private static int getFieldIndex(ProjectFieldsData[] fields, int sequence, int idx) {
        for (int i = idx; i < fields.length; i++) {
            if (sequence == ((ProjectFieldsData)fields[i]).sequence) {
                return i;
            }
        }
        // didn't find it, start from 0
        for (int i = 0; i < idx; i++) {
            if (sequence == ((ProjectFieldsData)fields[i]).sequence) {
                return i;
            }
        }
        return -1;
    }
}
