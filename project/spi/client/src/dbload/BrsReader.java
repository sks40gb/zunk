/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/Attic/BrsReader.java,v 1.1.2.1 2007/03/21 10:45:10 nancy Exp $ */
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


public class BrsReader {

    private final String DOCUMENT_BOUNDARY = "*** BRS DOCUMENT BOUNDARY ***";
    private final String SACC = "SACC";
    private final String ATTR = "ATTR";
    private final String DOCUMENT_NUMBER = "Document-Number";

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
    
    BufferedReader reader;

    private static String message;
    
    String bates = "";
    int i;
    String fieldname = "";
    
    private boolean midSACC = false;
    private boolean midATTR = false;
    private boolean midDocumentNumber = false;

    public BrsReader(PopulateData delimiters, String message) {
        this.delimiters = delimiters;
        this.message = message;
    }

    public Map read(String dataname, ArrayList fields) {
        this.fields = fields;

        //Log.print("(BrsReader.read) enter ===");
        
        rowMap = new HashMap();
        map = new HashMap();
        
        try {
            //Log.print("(BrsReader.read) reading " + dataname);
            reader = new BufferedReader(new FileReader(dataname));

            // read and parse each line
            String row;
            while ((row = reader.readLine()) != null) {
                //Log.print("(BrsReader.read) row " + row);
                parse(row);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            Log.write("BrsReader: "+e);
            message = "File not found: "+dataname;
        } catch (IOException e) {
            Log.write("BrsReader: "+e);
            message = "I/O error reading: "+dataname;
            e.printStackTrace();
        }
        return rowMap;
    }

    public void parse(String row) {
        row = row.trim();

        if (row.equals(DOCUMENT_BOUNDARY)) {
            // new document, put last doc in rowMap and initialize map for this doc
            if (map.size() > 0) {
                rowMap.put(bates, map); // store the values with the bates number
            }
            map = new HashMap();
            return;
        }

        if (row.startsWith("..")
            && row.endsWith(":")) {
            //Log.print("(BrsReader.parse) field is " + row);
            // Clear the mid flags because if they were true,
            // the previous tag was empty.
            midSACC = false;
            midATTR = false;
            midDocumentNumber = false;
            // document_number, bates range or tag name (fieldname)
            fieldname = row.substring(2, row.length()-1);
            if (fieldname.equals(SACC)) {
                midSACC = true;
            } else if (fieldname.equals(ATTR)) {
                midATTR = true;
            } else if (fieldname.equals(DOCUMENT_NUMBER)) {
                midDocumentNumber = true;
            }
        } else {
            if (midSACC) {
                // get the bates_number
                bates = row.substring(0, row.indexOf(" ")).trim();
            } else if (midATTR || midDocumentNumber) {
                // skip this document range and document-number
            } else {
                //Log.print("(BrsReader.parse) value is " + row);
                String[] values = row.split(";"); // value separator?
                value = new ArrayList();
                for (i = 0; i < values.length; i++) {
                    value.add(values[i].trim());
                }
                map.put(fieldname, value);  // store the fieldname and its values
            }
        }
    }
}


