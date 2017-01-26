/* $Header: /home/common/cvsarea/ibase/dia/src/export/DiaExportFromGui.java,v 1.11.6.4 2006/08/23 19:04:52 nancy Exp $ */
package com.fossa.servlet.writer;

import com.fossa.servlet.common.ExportData;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MessageConstants;
import com.fossa.servlet.common.ProjectFieldsData;
import com.fossa.servlet.dbload.XrefConstants;
import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import com.fossa.servlet.script.DocumentRecord;
import com.fossa.servlet.script.FieldRecord;
import com.fossa.servlet.script.Formatter;
import com.fossa.servlet.script.ScriptExecutable;
import com.fossa.servlet.script.Validator;
import com.fossa.servlet.server.valueobjects.FieldData;

/** 
 * The class DiaExportFromGui validates and formates the data going to be exported.
 * If the validation is enabled and validation does not fail then the record for a valume is exported to
 * a zip file else it generates a report list.
 */
public class DiaExportFromGui implements CsvConstants, XrefConstants, MessageConstants {

    CsvWriter csv = null;
    ProjectFieldsData[] fields;
    ArrayList<ValueData> valueDataList;
    PageissueData[] pageissues;
    ExportData data;
    private RunStats stats;
    private int xrefFormat;
    private Connection connection;

    public DiaExportFromGui(ProjectFieldsData[] fields, ArrayList<ValueData> valueDataList, PageissueData[] pageissues, 
            ExportData data, RunStats stats, int xrefFormat) {

        Log.print("(DiaExportFromGui)");
        this.fields = fields;
        this.valueDataList = valueDataList;
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

                csv = new CsvWriter(data.data_filename, fields, pageissues, data.field_delimiter, data.text_qualifier, 
                        data.value_separator, data.missing_year, data.missing_month, data.missing_day, data.date_format,
                        data.uppercase.equals("Yes") ? true : false, data.uppercase_names.equals("Yes") ? true : false, 
                        data.name_mask1, data.name_mask2, data.name_mask3, data.name_mask4, data.unitizeOnly.equals("Yes") ? true : false, 
                        data.nullAttachments.equals("Yes") ? true : false, data.brs_format.equals("Yes") ? true : false 
                        //, dia.a_first_name_initial, dia.a_include_mi
                        , stats);
                // Values and fields entries will be in order by child, projectfield.sequence,
                // then value.seq.
                // There will be 0 to n values per fields entry.
                int fieldsIndex = 0;
                int pageIndex = 0;

                int index = 0;
                ValueData value = getValueData(index);

                // System.out.println("pageissues.length : " + pageissues.length);

                for (pageIndex = 0; pageIndex < pageissues.length; pageIndex++) {
                    
                    System.out.println(value.childLft + " > page[" + pageIndex + "]" + pageissues[pageIndex].lft);
                    if (value.childLft > pageissues[pageIndex].lft) {

                        if (!pageissues[pageIndex].boundaryFlag.equals("")) { // is a child

                            csv.delimitEmptyChild(pageIndex);
                        }
                        
                    } else if (value.childLft == pageissues[pageIndex].lft) {

                        // get the index of the projectfield that goes with this value
                        while (value.childLft == pageissues[pageIndex].lft) {

                            fieldsIndex = getFieldIndex(fields, value.projectfieldsSequence, fieldsIndex);
                            System.out.println(fieldsIndex + " : " + ((ProjectFieldsData)fields[fieldsIndex]).fieldName + " ------------ " + value.value);
                            csv.writeValue(pageIndex, value, (ProjectFieldsData)fields[fieldsIndex]);
                            index++;
                            value = getValueData(index);
                        }
                        System.out.println("=====================================");
                    }
                }

                csv.close();
            } catch (IOException e) {
                System.out.println("DB Error: " + e);
                //if (dia.debug | dia.log ) Log.write("DB Error: "+e);
                return;
            }
        }
        if (data.lfp_filename.length() > 0) {
            //
            // Build Cross Reference Data
            //
            if (data.lfp_filename != null && data.lfp_filename.length() > 0) {
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
                    System.out.println("DB Error: " + e);
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
        System.out.println("\n\n" + data.project_name + "  " + stats.recordCount + " records/" + stats.pageCount + " pages");
        if (fields != null) {
            System.out.println("\nThe field order is:\n");
            for (i = 0; i < fields.length; i++) {
                System.out.println("    " + fields[i].fieldName.toUpperCase());
            }
            System.out.println("\nDelimiters are " + data.field_delimiter + data.text_qualifier + " and the date is formatted " + data.date_format.toLowerCase() + ".\n");
        }
        if (data.log_filename != null && data.log_filename.length() > 0) {
            Log.print("Write Log " + data.log_filename);
            try {
                csv = new CsvWriter(data.log_filename, fields, data.field_delimiter, data.text_qualifier, data.value_separator, data.date_format, stats);
                csv.writeLog(data.project_name);
                csv.close();
            } catch (IOException e) {
                System.out.println("DB Error: " + e);
                return;
            }
        }
    }

    public boolean executeScript(ScriptExecutable scriptExecutor) {

        if (data.data_filename.length() > 0) {
                 System.out.println("executeScript "+ data.log_filename);
            // Values and fields entries will be in order by child, projectfield.sequence,
            // then value.seq.
            // There will be 0 to n values per fields entry.
            int fieldsIndex = 0;
            int pageIndex = 0;
            int index = 0;
            ProjectFieldsData fieldData = null;


            //add all bate number to the bates list.        
            DocumentRecord.reset(true);
            for (int i = 0; i < pageissues.length; i++) {
                DocumentRecord.addBatesNumber(pageissues[i].batesNumber);
            }
            //--------------------------------------
            ValueData value = getValueData(index);

            for (pageIndex = 0; pageIndex < pageissues.length; pageIndex++) {

                if (value.childLft == pageissues[pageIndex].lft) {

                    //reset the document record  so that it erase old record.
                    DocumentRecord.reset();

                    // get the index of the projectfield that goes with this value
                    while (value.childLft == pageissues[pageIndex].lft) {
                        fieldsIndex = getFieldIndex(fields, value.projectfieldsSequence, fieldsIndex);
                        //csv.writeValue(pageIndex, value, (ProjectFieldsData)fields[fieldsIndex]);
                        fieldData = (ProjectFieldsData) fields[fieldsIndex];
                        //value.fieldName
                        //System.out.println("FIELD NAME : " + value.fieldName + " -------------  FIELD VALUE : " + value.value);


                        //validator = new Validator(connection);
                        scriptExecutor.executeField(fieldData.projectfieldsId, fieldData.fieldName, value, fieldData.fieldType);
                        //----------------------
                        PageissueData page = pageissues[pageIndex];

                        FieldData fd = new FieldData().addName(value.fieldName).addType(fieldData.fieldType).addValue(value.value);

                        FieldRecord.setCodedValue(value.value);

                        //for the formtter only
                        if (scriptExecutor instanceof Formatter) {
                            fd.addValueData(value);
                            FieldRecord.setValueData(value);
                        }

                        DocumentRecord.addField(fd);
                        DocumentRecord.setCurrentBatesNumber(page.batesNumber);
                        DocumentRecord.setBeginBatesNumber(page.beginBatesNumber);
                        DocumentRecord.setEndBatesNumber(page.endBatesNumber);
                        DocumentRecord.setDocumentNumber(page.documentNumber);
                        DocumentRecord.setBatchNumber(page.batchNumber);

                        // System.out.println("value.name : " + value.fieldName + "          value.value : " + value.value);

                        index++;
                        value = getValueData(index);

                    //-----------------------
                    }
                    scriptExecutor.executeDocument(data.volume_id);

                }
            }
        }

        if (Validator.getReportList().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private ValueData getValueData(int index) {

        ValueData vd = new ValueData();
        if (index < valueDataList.size() && valueDataList.get(index) != null) {
            vd = valueDataList.get(index);
        } else {
            vd.childLft = Integer.MAX_VALUE;
        }
        //   System.out.println(vd.fieldName + " --:-- " + vd.value);
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
        idx++;
        for (int i = idx; i < fields.length; i++) {
            if (sequence == ((ProjectFieldsData) fields[i]).sequence) {
                return i;
            }
        }
        // didn't find it, start from 0
        for (int i = 0; i < idx; i++) {

            if (sequence == ((ProjectFieldsData) fields[i]).sequence) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get the connection for DB operation.
     * @return Connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Set the DB connection.
     * @param connection Connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
