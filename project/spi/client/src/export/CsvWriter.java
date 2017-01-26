/* $Header: /home/common/cvsarea/ibase/dia/src/export/CsvWriter.java,v 1.20.2.5 2006/08/23 19:04:52 nancy Exp $ */
package export;

import common.Log;
import common.ProjectFieldsData;
import common.msg.MessageConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;

/**
 * Provides methods for writing a csv file.
 */
public class CsvWriter implements MessageConstants {
    String leadingBates = "";
    /** separates fields */
    String fieldDelimiter = "|";
    /** quote */
    String textQualifier = "~";
    /** separates values within a field */
    String valueSeparator = ";";
    
    /** reformat db dates into this format */
    String dateFormat = "";

    /** use in place of null date */
    String missingYear = "";
    String missingMonth = "";
    String missingDay = "";
    String missingDate = "";

    /** convert to uppercase? */
    boolean uppercase = false;
    boolean uppercase_names = false;

    /** use brs format? */
    boolean brsFormat = false;

    /** Export no coded data, just docs and ranges */
    boolean unitizeOnly = false;
    /** if unitize only, repeat docs when not part of an attachment range? */
    boolean nullAttachments = false;

    /** change name format */
    String name_mask1 = "";
    String name_mask2 = "";
    String name_mask3 = "";
    String name_mask4 = "";
    //boolean first_name_initial = false;
    //boolean include_mi = true;

    private ProjectFieldsData[] fields;
    private PageissueData[] pages;

    private int pageissueIdx = 0;
    private int batchNumber = 0;

    /** accumulates the csv line until ready to write */
    private StringBuffer buffer = null;
    private ProjectFieldsData lastField = null;
    private int childLft = -1;
    private int valueSequence = 0;

    private String filename; // must contain path

    private PrintWriter writer;

    private RunStats stats;

    /**
     * Create a new CsvWriter for a given output file.
     * @param filename - the fully qualified output filename
     * @param fieldDelimiter - separates fields; cannot be null; usually comma
     * @param textQualifier - usually quote
     * @param valueSeparator - separates values within a field
     * @param fields - all of the projectfields data defined for this project
     */
    public CsvWriter(String filename, ProjectFieldsData[] fields, String field_delimiter
                     , String text_qualifier, String valueSeparator
                     , String dateFormat, RunStats stats) throws IOException {
        // for writeLog
        this(filename, fields, null, field_delimiter, text_qualifier, valueSeparator, null, null, null, dateFormat
             , false, false, null, null, null, null, false, false, false, stats);
    }

    public CsvWriter(String filename, ProjectFieldsData[] fields, PageissueData[] pageissues, String fieldDelimiter
                     , String textQualifier, String valueSeparator
                     , String missingYear, String missingMonth, String missingDay
                     , String dateFormat
                     , boolean uppercase, boolean uppercase_names
                     , String name_mask1, String name_mask2, String name_mask3, String name_mask4
                     , boolean unitizeOnly, boolean nullAttachments, boolean brsFormat
                     , RunStats stats
                     //, boolean first_name_initial, boolean include_mi
                     ) throws IOException {
        this.filename = filename;
        this.fieldDelimiter = fieldDelimiter;
        this.textQualifier = textQualifier;
        this.valueSeparator = valueSeparator;
        this.fields = fields;
        this.pages = pageissues;
        this.missingYear = missingYear;
        this.missingMonth = missingMonth;
        this.missingDay = missingDay;
        this.dateFormat = dateFormat;
        if (dateFormat != null
            && missingDay != null) {
            // Note the 9 zeroes to force dateValue() to generate a date.
            this.missingDate = dateValue("000000000");
            //Log.print(" missingDate: " + missingDate);
        }
        this.uppercase = uppercase;
        this.uppercase_names = uppercase_names;
        this.name_mask1 = name_mask1;
        this.name_mask2 = name_mask2;
        this.name_mask3 = name_mask3;
        this.name_mask4 = name_mask4;
        this.unitizeOnly = unitizeOnly;
        this.nullAttachments = nullAttachments;
        this.brsFormat = brsFormat;
        this.stats = stats;
        Log.print("(CsvWriter) pageCount is " + stats.pageCount);
        //this.first_name_initial = first_name_initial;
        //this.include_mi = include_mi;
        FileOutputStream fos = new FileOutputStream(new File(filename)) ;
        //writer = new BufferedWriter(
        writer = new PrintWriter(
                     new OutputStreamWriter(fos, MessageConstants.UTF_8));
        buffer = new StringBuffer();
    }

    public void writeValue(int pageIndex, ValueData text
                           , ProjectFieldsData field) {
        if (((String)text.value).trim().length() == 0) {
            return;
        }
        //Log.print("(CsvWriter.writeValue) " + text.value);
        if (childLft == -1) { // first write
            childLft = 0;
            delimitToCurrentProjectfield(pageIndex, field);
            fieldDelimiter(field.tagName);
            putBufferValue(field.fieldType, text.value);
            lastField = field;
            childLft = text.childLft;
            return;
        } else if (childLft == text.childLft
            && lastField.sequence == field.sequence
            && text.sequence == valueSequence) {
            // more of the same, just separate from the last value
            //Log.print("(CsvWriter.writeValue) more of the same");
            
            buffer.append(valueSeparator + text.value);
            lastField = field;
            childLft = text.childLft;
            return;
        } else if (childLft != text.childLft) {
            // different child, end buffer and write it
            //Log.print("(CsvWriter.writeValue) child is different");
            delimitValueBuffer();
            // On the new line, write all projectfield delimiters preceding this projectfield.
            delimitToCurrentProjectfield(pageIndex, field);
            fieldDelimiter(field.tagName);
            putBufferValue(field.fieldType, text.value);
            childLft = text.childLft;
            lastField = field;
            return;
        } else if (lastField.sequence != field.sequence) {
            // same child, different projectfield
            //Log.print("(CsvWriter.writeValue) different projectfield");
            delimitToCurrentProjectfield(pageIndex, field);
            fieldDelimiter(field.tagName);
            putBufferValue(field.fieldType, text.value);
            childLft = text.childLft;
            lastField = field;
            return;
        } else {
            //Log.print("(CsvWriter.writeValue) delimit value");
            buffer.append(valueSeparator);
        }
        // add this value to the buffer
        if (field.fieldType.equals("date")) {
            buffer.append(dateValue(text.value));
        } else if (field.fieldType.equals("name")) {
            buffer.append(nameValue(text.value));
        } else {
            buffer.append(upperValue(field.fieldType, text.value));
        }
    }

    private void putBufferValue(String type, String val) {
        if (type.equals("date")) {
            // date values don't get quotes
            buffer.append(dateValue(val));
        } else if (type.equals("name")) {
            buffer.append(textQualifier + nameValue(val));
        } else {
            // must quote everything else
            buffer.append(textQualifier + upperValue(type, val));
        }
    }

    /**
     * Add page.bates_numbers and delimiters to mark a child containing no data
     */
    public void delimitEmptyChild(int pageIndex) {
        //Log.print("enter DelimitEmptyChild(" + pageIndex + ")");
        int idx = 0;
        int i = 0;

        // finish the existing buffer
        delimitValueBuffer();

        putLeadingDocs(pageIndex);

        if (! unitizeOnly) {
            // add empty delimiters for each projectfields
            for (i = 0; i < fields.length; i++) {
                if (((ProjectFieldsData)fields[i]).fieldType.equals("date")) {
                    buffer.append(fieldDelimiter(i) + missingDate);
                } else {
                    buffer.append(fieldDelimiter(i) + textQualifier + textQualifier);
                }
                if (brsFormat) {
                    writeBuffer();
                }
            }
        }
        if (! brsFormat) {
            writeBuffer();
        }
    }

    /**
     * Start the buffer with bates numbers corresponding to the first bates
     * of the child, the last bates of the child, and then enumerate each
     * bates in that range.
     * @param pageIndex - the first page of the child
     */
    private void putLeadingDocs(int pageIndex) {
        String endBates = "";
        // this marks the beginning of every page written
        int i = 0;
        
        stats.recordCount++;
        if (pages[pageIndex].batchNumber != batchNumber) {
            batchNumber = pages[pageIndex].batchNumber;
            stats.batchName.add(pages[pageIndex].status + " " + batchNumber);
        }
        
        if (brsFormat) {
            writer.println("*** BRS DOCUMENT BOUNDARY ***");
            writer.println("..Document-Number:");
            writer.println(pages[pageIndex].documentNumber);
            // TBD: put Document Number?
        }
        
        leadingBates = pages[pageIndex].batesNumber;
        // put the child's beginning bates_number in the buffer
        if (brsFormat) {
            fieldDelimiter("SACC");
            buffer.append(textQualifier + leadingBates + " - ");
        } else {
            buffer.append(textQualifier + leadingBates
                          + textQualifier + fieldDelimiter);
        }
        
        // write the bates_numbers marking the end of the child
        for (i = pageIndex + 1; i < pages.length; i++) {
            if (pages[i].lft > pages[pageIndex].lft) {
                endBates = pages[i-1].batesNumber;
                buffer.append(textQualifier + endBates
                              + textQualifier + fieldDelimiter);
                break;
            }
        }

        if (i >= pages.length) {
            // last doc
            endBates = pages[i-1].batesNumber;
            buffer.append(textQualifier + endBates
                          + textQualifier + fieldDelimiter);
        }

        // Enumeration not required for pilot production project, TEV001.
        // TBD: Perhaps make this an option, later.
        // now, enumerate them
        //buffer.append(textQualifier + pages[pageIndex].batesNumber);
        //for (i = pageIndex + 1; i < pages.length; i++) {
        //    if (pages[i].lft > pages[pageIndex].lft) {
        //        break;
        //    } else {
        //        buffer.append(valueSeparator + " " + pages[i].batesNumber);
        //    }
        //}
        //buffer.append(textQualifier);

        // Pilot production project, TEV001, requires the first and last
        // bates of range.
        //for (i = 0; i < pages.length; i++) {
        //    if (pages[i].seq == pages[pageIndex].rangeLft) {
        //        // first bates of range
        //        beginRange = pages[i].batesNumber;
        //        break;
        //    }
        //}
        //for (; i < pages.length; i++) {
        //    if (pages[i].seq == pages[pageIndex].rangeRgt) {
        //        // last bates of range
        //        endRange = pages[i].batesNumber;
        //        break;
        //    }
        //}
        if (! brsFormat) {
            if (nullAttachments
                && leadingBates.equals(pages[pageIndex].beginBatesNumber)
                && endBates.equals(pages[pageIndex].endBatesNumber)) {
                // leave the attachment range empty if the doc is not
                // part of a range
                buffer.append(textQualifier
                              + textQualifier + fieldDelimiter);
                buffer.append(textQualifier
                              + textQualifier);
            } else {
                // repeat the doc bates for attachment range if the doc is not
                // part of a range
                buffer.append(textQualifier + pages[pageIndex].beginBatesNumber
                              + textQualifier + fieldDelimiter);
                buffer.append(textQualifier + pages[pageIndex].endBatesNumber
                              + textQualifier);
            }
        } else {
            writeBuffer();
            fieldDelimiter("ATTR");
            if (leadingBates.equals(pages[pageIndex].beginBatesNumber)
                && endBates.equals(pages[pageIndex].endBatesNumber)) {
            } else {
                // write the bates range on its own line for brs
                buffer.append(textQualifier + pages[pageIndex].beginBatesNumber + " - ");
                buffer.append(textQualifier + pages[pageIndex].endBatesNumber
                              + textQualifier);
                writeBuffer();
            }
        }
    }

    /**
     * Add delimiters to finish the current child, write it and initialize the buffer.
     */
    private void delimitValueBuffer() {
        if (lastField == null) {
            // haven't had any data, yet, or nothing to do to the line
            return;
        }
        int idx = -1;
        
        // find the last field written to buffer
        for (int i = 0; i < fields.length; i++) {
            if (lastField.sequence == ((ProjectFieldsData)fields[i]).sequence) {
                // this field has been started
                idx = i;
                break;
            }
        }

        // finish the current field
        if (idx >= 0) {
            if (((ProjectFieldsData)fields[idx]).fieldType.equals("date")) {
            } else {
                buffer.append(textQualifier);
            }
            if (brsFormat) {
                writeBuffer();
            }
            idx++;
        }
        
        // put empty delimiters for the remainder of the line
        for (int i = idx; i < fields.length; i++) {
            if (((ProjectFieldsData)fields[i]).fieldType.equals("date")) {
                buffer.append(fieldDelimiter(i) + missingDate);
            } else {
                buffer.append(fieldDelimiter(i) + textQualifier + textQualifier);
            }
            if (brsFormat) {
                writeBuffer();
            }
        }

        if (idx >= 0) {
            if (! brsFormat) {
                writeBuffer();
            }
            lastField = null;
        }
    }

    /**
     * Add delimiters from the last field in the buffer to this one.
     * @param pageIndex
     * @param pf - projectfields.* for the column to be written
     */
    private void delimitToCurrentProjectfield(int pageIndex, ProjectFieldsData pf) {
        int i;
        int idx = -1;
        int stopIdx = -1;
        // find the last projectfield written to buffer
        if (lastField != null) {
            for (i = 0; i < fields.length; i++) {
                if (lastField.sequence == ((ProjectFieldsData)fields[i]).sequence) {
                    idx = i;
                    break;
                }
            }
        }

        if (idx < 0) {
            // new buffer line; start the line with the begin, end and enum bates_numbers
            putLeadingDocs(pageIndex);
        }

        // find the projectfields position of the current value
        for (i = 0; i < fields.length; i++) {
            if (pf.sequence == ((ProjectFieldsData)fields[i]).sequence) {
                stopIdx = i;
                break;
            }
        }

        // end the current projectfield, assuming the value(s) are there
        if (idx < stopIdx) {
            if (brsFormat) {
                // for brs, just write the buffer, no delimiters needed
            //    if (idx > -1) {
            //        fieldDelimiter(idx);
            //    }
                writeBuffer();
            } else if (idx < 0 // new line, delimit the bates fields
                || ((ProjectFieldsData)fields[idx]).fieldType.equals("date")) { // no quote required
                //if (brsFormat && idx > -1) {
                //    fieldDelimiter(idx);
                //}
                buffer.append(fieldDelimiter);
            } else {
                // everything else is quoted
                buffer.append(textQualifier + fieldDelimiter);
            }
            idx++;
        }

        // delimit empty fields until current field is reached
        for (i = idx; i < stopIdx; i++) {
            if (brsFormat) {
                fieldDelimiter(i);
            }
            if (((ProjectFieldsData)fields[i]).fieldType.equals("date")) {
                buffer.append(missingDate + fieldDelimiter);
            } else {
                // for text, add an empty set of quotes and a field delimiter
                buffer.append(textQualifier + textQualifier + fieldDelimiter);
            }
            if (brsFormat) {
                writeBuffer();
            }
        }

        //if (idx == stopIdx) {
        //    if (((ProjectFieldsData)fields[i]).fieldType.equals("date")) {
        //    } else {
        //        // for text, add an empty set of quotes and a field delimiter
        //        buffer.append(textQualifier);
        //    }
        //}
    }

    /**
     * Add delimiters to finish the current field.
     */
    //private void delimitField() {
    //    if (lastField.fieldType.equals("date")) {
    //        // no quotes, just a field separator
    //        buffer.append(fieldDelimiter);
    //    } else {
    //        // everything else is quoted
    //        buffer.append(textQualifier + fieldDelimiter);
    //    }
    //}

    private String fieldDelimiter(String tag) {
        writer.println(".." + tag + ":");
        return "";
    }

    private String fieldDelimiter(int index) {
        writer.println(".." + ((ProjectFieldsData)fields[index]).tagName + ":");
        return "";
    }

    /**
     * Reformat the name as required by the name parameters.
     * @param name - the name value from the database
     * @return the reformatted name
     */
    private String nameValue(String name) {
        if (name.length() < 1) {
            // no name
            return "";
        }
        String text = upperValue("name", name);
        if (name_mask1.length() > 0
            || name_mask2.length() > 0
            || name_mask3.length() > 0
            || name_mask4.length() > 0) {
            int i = 0;
            // parse the name
            String last = "";
            String first = "";
            String middle = "";
            String affiliation = "";
            int comma = text.indexOf(",");
            int slash = text.indexOf(" /");
            if (slash > -1) {
                affiliation = text.substring(slash+3);
            } else {
                slash = text.length();
            }
            last = text.substring(0, comma > -1 ? comma : slash);
            if (comma > -1) {
                String firstMiddle = text.substring(comma+2, slash);
                i = firstMiddle.lastIndexOf(" ");
                first = firstMiddle.substring(0, i > -1 ? i : firstMiddle.length());
                if (i > -1) {
                    middle = firstMiddle.substring(i+1);
                }
            }
            if (middle.length() < 1
                && last.length() < 1
                && affiliation.length() < 1) {
                // TBD: Specific to BRS?
                // When only first name is coded, return XX-first.
                // (It's either first name or initials.)
                return "XX-" + first;
            }

            // put the name pieces together to match the name_masks
            text = "";
            text = getMaskedName(text, name_mask1
                                 , first, last, middle, affiliation);
            text = getMaskedName(text, name_mask2
                                 , first, last, middle, affiliation);
            text = getMaskedName(text, name_mask3
                                 , first, last, middle, affiliation);
            text = getMaskedName(text, name_mask4
                                 , first, last, middle, affiliation);
            return text;
        }
        //if (! name_delimiter.equals(", ")
        //    && (text.indexOf("/") < 0
        //        || text.indexOf(",") < text.indexOf("/")) ) {
        //    // the delimiter between last name and first is not comma
        //    // and the comma occurs before the slash for organization
        //    text = text.replaceFirst(", ", name_delimiter);
        //}
        //if (! org_delimiter.equals(" / ")) {
        //    text = text.replaceFirst(" / ", org_delimiter);
        //}
        return text;
    }

    /**
     * Apply the mask provided to the correct name part and append it to
     * the provided text.
     * @param text - the accumulated name parts
     * @param mask - a mask that applies to one part of the name.  It must contain an 
     * f, l, m, or a for first, last, middle and affiliation.
     * @param first - first name, can be blank
     * @param last - last name, can be blank
     * @param middle - middle initial, can be blank
     * @param affiliation - affiliation, can be blank
     * @return the provided text with the edited name part appended
     */
    private String getMaskedName(String text, String mask
                                 , String first, String last, String middle, String affiliation) {
        if (mask.length() <= 0) {
            // TBD: if no mask, just return?
            return text;
        }
        int i;
        char[] chr = mask.toCharArray();
        
        // if there is no name part for a mask part, return ""
        for (i = 0; i < chr.length; i++) {
            if (chr[i] == 'f') {
                if (first.length() <= 0) {
                    return text;
                }
            } else if (chr[i] == 'l') {
                if (last.length() <= 0) {
                    return text;
                }
            } else if (chr[i] == 'a') {
                if (affiliation.length() <= 0) {
                    return text;
                }
            } else if (chr[i] == 'm') {
                if (middle.length() <= 0) {
                    return text;
                }
            }
        }
        
        // apply the mask
        for (i = 0; i < chr.length; i++) {
            if (chr[i] == 'f') {
                text = text + first;
            } else if (chr[i] == 'l') {
                text = text + last;
            } else if (chr[i] == 'm') {
                text = text + middle;
            } else if (chr[i] == 'a') {
                text = text + affiliation;
            } else {
                // put the special character
                text = text + chr[i];
            }
        }
        return text;
    }

    /**
     * Convert the given value to uppercase, if requested by the user.
     * @param type - "name" or "text" (else ignored)
     * @param value - any string value
     * @return the given string converted to uppercase
     */
    private String upperValue(String type, String value) {
        if (uppercase) {
            return value.toUpperCase();
        }
        if (type.equals("name")
            && uppercase_names) {
            return value.toUpperCase();
        }
        return value;
    }

    /**
     * Reformat the date, if requested, and check for missing date,
     * year, month or day and fill with the appropriate value.
     * @param date - the date value from the database
     * @return the value with missing pieces replaced
     */
    Format formatter;
    private String dateValue(String date) {
        //Log.print("(CsvWriter.dateValue) date " + date);
        if (date.length() < 1) {
            return "";
        } else if (date.length() < 8) {
            stats.message.add("Bad date format for " + leadingBates + ".  Format: " + date);
            Log.print("(CsvWriter.dateValue) bad input format: " + leadingBates + " / " + date);
            return missingDate;
        }
        Date dateEdit = null;
        String formattedDate = null;
        String edit = date;
        //Log.print("(dateValue) format: " + dateFormat + " date: " + date);
        formatter = new SimpleDateFormat(dateFormat);

        if (date.equals("00000000")) {
            return missingDate;
        } else {
            int phonyYYYY = 1888;
            int phonyMM = 12;
            int phonyDD = 13;
            boolean phonyYYYYUsed = false;
            boolean phonyMMUsed = false;
            boolean phonyDDUsed = false;
            
            if (date.substring(6).equals("00")
                || date.length() > 8) {
                //Log.print("  empty day");
                // empty day
                for (int i = 0; i < 30; i++) {
                    if (date.indexOf(phonyDD) > -1) {
                        phonyDD++;
                    } else {
                        break;
                    }
                }
                edit = date.substring(0, 6) + phonyDD;
                phonyDDUsed = true;
            }
            //Log.print("  month " + date.substring(4, 6) + "^" + date);
            if (date.substring(4, 6).equals("00")) {
                //Log.print("  empty month");
                // empty month
                for (int i = 0; i < 11; i++) {
                    if (edit.indexOf(phonyMM) > -1) {
                        phonyMM--;
                    } else {
                        break;
                    }
                }
                edit = date.substring(0, 4) + phonyMM + edit.substring(6, 8);
                phonyMMUsed = true;
            }
            if (date.substring(0, 4).equals("0000")) {
                //Log.print("  empty year");
                // empty year
                for (int i = 0; i < 10; i++) {
                    if (edit.indexOf(phonyYYYY) > -1) {
                        phonyYYYY--;
                    } else {
                        break;
                    }
                }
                edit = phonyYYYY + edit.substring(4);
                phonyYYYYUsed = true;
            }
            try {
                DateFormat f = new SimpleDateFormat("yyyyMMdd");
                dateEdit = (Date)f.parse(edit);
            } catch (ParseException e) {
                Log.print("(CsvWriter.dateValue) bad date parse: " + edit);
            }
            
            //Log.print("  dateEdit = " + edit);
            // put the date in the output format
            formattedDate = formatter.format(dateEdit);
            //Log.print("  Date buffer = " + formattedDate);
            if (phonyDDUsed) {
                formattedDate = formattedDate.replaceFirst(Integer.toString(phonyDD), missingDay);
            }
            if (phonyMMUsed) {
                formattedDate = formattedDate.replaceFirst(Integer.toString(phonyMM), missingMonth);
            }
            if (phonyYYYYUsed) {
                formattedDate = formattedDate.replaceFirst(Integer.toString(phonyYYYY), missingYear);
            }
            return formattedDate;
        }
    }

    private void writeBuffer() {
        if (buffer.length() > 0) {
            writer.println(buffer.toString());
            buffer = new StringBuffer();
        }
    }
    
    public void writeLog(String project_name) {
        int i = 0;
        
        buffer = new StringBuffer();
        
        writer.println(" ");
        
        String errors = "";
        if (stats.message.size() > 0) {
            errors = "Export Messages:\n";
        }
        for (i = 0; i < stats.message.size(); i++) {
            errors = errors + "    " + stats.message.get(i) + "\n";
        }
        writer.println(errors);

        writer.println(stats.batchName.size() + " Batches:");
        for (i = 0; i < stats.batchName.size(); i++) {
             writer.println("    " + stats.batchName.get(i));
        }
        writer.println(" ");
        writer.println(" ");
        writer.println(project_name + "  " + stats.recordCount + " records/"
                  + stats.pageCount + " pages");
        if (fields != null) {
            writer.println("The field order is:");
            for (i =0; i < fields.length; i++) {
                writer.println("    " + fields[i].fieldName.toUpperCase());
            }
            writer.println(" ");
            writer.println("Delimiters are " + fieldDelimiter
                      + textQualifier + " and the date is formatted "
                      + dateFormat.toLowerCase() + ".");
        }
    }

    /**
     * Finish the last buffer line, write it and close the writer.
     */
    public void close() throws IOException {
        if (missingDate != null) {
            // writing csv, not lfp
            delimitValueBuffer();
        }
        writer.close();
        writer = null;
    }
}
