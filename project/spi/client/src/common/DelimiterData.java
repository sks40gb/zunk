/* $Header: /home/common/cvsarea/ibase/dia/src/common/DelimiterData.java,v 1.4.6.3 2006/03/21 16:42:41 nancy Exp $ */
package common;

/**
 * A container for delimiters used in export and data population.
 */
public class DelimiterData {

    /** name to store in export.export_name to key these values */
    public String delimiter_set_name = "";
    /** Yes to overwrite existing data; else No */
    public String force = "";
    /** Yes to convert all data to uppercase; else No */
    public String uppercase = "";
    /** Yes to convert all name data to uppercase; else No */
    public String uppercase_names = "";
    /** a mask that applies to one part of the name.  It must contain an
     * f, l, m, or a for first, last, middle and affiliation. */
    public String name_mask1 = "";
    /** a mask that applies to one part of the name.  It must contain an
     * f, l, m, or a for first, last, middle and affiliation. */
    public String name_mask2 = "";
    /** a mask that applies to one part of the name.  It must contain an
     * f, l, m, or a for first, last, middle and affiliation. */
    public String name_mask3 = "";
    /** a mask that applies to one part of the name.  It must contain an
     * f, l, m, or a for first, last, middle and affiliation. */
    public String name_mask4 = "";
    /** character following a field to indicate the beginning of a new field */
    public String field_delimiter = "";
    /** character that surrounds a text to indicate the beginning and end */
    public String text_qualifier = "";
    /** character that separates to values within a field */
    public String value_separator = "";
    /** mask of the date format using characters valid for the
     * java.text.SimpleDateFormat constructor pattern */
    public String date_format = "";
    /** value to use when a date is missing or all zeroes */
    public String missing_date = "";
    /** value to use when a year is missing or all zeroes */
    public String missing_year = "";
    /** value to use when a month is missing or all zeroes */
    public String missing_month = "";
    /** value to use when a day is missing or all zeroes */
    public String missing_day = "";
    /** value to use when any of the date components are missing or all zeroes */
    public String missing_date_character = "";

    /** export only a cross reference, no data */
    public String unitizeOnly = "";
    /** do not repeat the document range in the output when no attachments exist */
    public String nullAttachments = "";
    /** input and output to brs format */
    public String brs_format = "";
}
