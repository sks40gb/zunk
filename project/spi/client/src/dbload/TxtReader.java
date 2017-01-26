/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/TxtReader.java,v 1.5.6.1 2005/08/31 16:11:57 nancy Exp $ */
package dbload;

import common.PopulateData;
import common.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class TxtReader {

    /* the delimiters to use in parsing the input coded data file */
    private static PopulateData delimiters = null;

    /* an HashMap of one map per projectfield per record, keyed by bates_number */
    Map rowMap;
    
    /* the field_names for the given project, in order */
    private ArrayList fields;

    /* a hashmap containing all values, keyed by fieldname */
    private Map map;

    /* an arrayList of values for each fieldname */
    private ArrayList value;

    private static String message;

    public TxtReader(PopulateData delimiters, String message) {
        this.delimiters = delimiters;
        this.message = message;
    }

    public Map read(String dataname, ArrayList fields) {
        this.fields = fields;
        
        rowMap = new HashMap();
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(dataname));

            // read and parse each line
            String row;
            while ((row = reader.readLine()) != null) {
                parse(row);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            Log.write("TxtReader: "+e);
            message = "File not found: "+dataname;
        } catch (IOException e) {
            Log.write("TxtReader: "+e);
            message = "I/O error reading: "+dataname;
            e.printStackTrace();
        }
        return rowMap;
    }
/*
    private void readLogFile(String log) {
        delimiters = new DelimiterData();
        int col;
        int i;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(log));
            // read and parse each line
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Delimiters are ")) {
                    delimiters.field_delimiter = line.substring(15, 16);
                    delimiters.text_qualifier = line.substring(16, 17);
                    delimiters.date_format = line.substring(44, 52);
                    delimiters.name_delimiter = ",";
                    delimiters.org_delimiter = "/";
                    delimiters.value_separator = ";";
                    delimiters.missing_date = "00000000";
                    delimiters.missing_year = "0000";
                    delimiters.missing_month = "00";
                    delimiters.missing_day = "00";
                    delimiters.missing_date_character = "0";
                    break;
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            Log.write("TxtReader: "+e);
            message = "File not found: "+log;
        } catch (IOException e) {
            Log.write("TxtReader: "+e);
            message = "I/O error reading: "+log;
        }
    }
*/
    public void parse(String row) {
        StringBuffer buffer;
        String bates = "";
        char c;
        boolean inString;
        int cindex = 0;
        int i, count;

        map = new HashMap();

        row = row.trim();

        // get the bates_number
        buffer = new StringBuffer();
        // begin at 1 to skip initial text_qualifier (")
        for (i = 1; i < row.length(); i++) {
            c = row.charAt(i);
            if (c == ((String)delimiters.text_qualifier).charAt(0)) {
                break;
            }
            buffer.append(c);
        }
        bates = buffer.toString().trim();

        // skip the next 3 bates_numbers
        count = 0;
        for (; i < row.length(); i++) {
            c = row.charAt(i);
            if (c == ((String)delimiters.field_delimiter).charAt(0)) {
                // note:  first delimiter follows first bates
                count++;
            }
            if (count >= 4) {
                // skip the delimiter and break
                i++;
                break;
            }
        }

        if (i >= row.length()) {
            // no data fields for this row
            return;
        }
        cindex = i;
        for (i = 0; i < fields.size(); i++) {
            // parse the coded data
            value = new ArrayList();
            buffer = new StringBuffer();
            inString = false;
            while (true) {
                if (cindex > row.length()) {
                    Log.write("TxtReader: Too few fields in row: "+rowMap.size());
                    message = "TxtReader: Too few fields in row: "+rowMap.size();
                    break;
                }
                c = row.charAt(cindex);
                if (! inString
                    && c == ((String)delimiters.text_qualifier).charAt(0)) {
                    inString = true;
                } else if (inString
                           && c == ((String)delimiters.text_qualifier).charAt(0)) {
                    // end of field
                    inString = false;
                } else if (c == ((String)delimiters.value_separator).charAt(0)) {
                    // end of one value for this field
                    if (buffer.length() > 0) {
                        value.add(buffer.toString().trim());
                        buffer = new StringBuffer();
                    }
                } else if (! inString
                           && c == ((String)delimiters.field_delimiter).charAt(0)) {
                    break;
                } else {
                    buffer.append(c);
                }
                cindex++;
                if (cindex >= row.length()) {
                    break;
                }
            }
            if (buffer.length() > 0) {
                value.add(buffer.toString().trim());
                map.put((String)fields.get(i), value);  // store the fieldname and its values
            }
            cindex++;
        }
        if (map.size() > 0) {
            rowMap.put(bates, map); // store the values with the bates number
        }
    }
}


