/* $Header: /home/common/cvsarea/ibase/dia/src/dbload/Attic/BrsReader.java,v 1.1.2.1 2007/03/21 10:45:10 nancy Exp $ */
package com.fossa.servlet.dbload;

import com.fossa.servlet.common.PopulateData;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * Class which is used to read the Brs file
 */

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

    private static String message; //error message
    
    String bates = ""; //batesNumber  
    String fieldname = ""; 
    
    private boolean midSACC = false;
    private boolean midATTR = false;
    private boolean midDocumentNumber = false;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.dbload");

    
    public BrsReader(PopulateData delimiters, String message) {
        this.delimiters = delimiters;
        this.message = message; 
    }

    /**
     * reads the brs file line by line and 
     * returns map of fieldname and correponding value
     * @param the fully qualified dataname of the input file on the server
     * @param project fields name 
     */
    public Map read(String dataname, ArrayList fields) {
        this.fields = fields;
        
        rowMap = new HashMap();
        map = new HashMap();
        
        try {
           
            reader = new BufferedReader(new FileReader(dataname));
            // read and parse each line
            String row;
            while ((row = reader.readLine()) != null) {                
                parse(row);
            }
            reader.close();
        } catch (FileNotFoundException e) {
            logger.error("Exception while reading the BRS file." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            message = "File not found: "+dataname;
        } catch (IOException e) {
            logger.error("Exception while reading the BRS file." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            message = "I/O error reading: "+dataname;           
        }
        return rowMap;
    }

    
    /**
     * parse every rows corresponding to the brs file
     */
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
                for (int i = 0; i < values.length; i++) {
                    value.add(values[i].trim());
                }
                map.put(fieldname, value);  // store the fieldname and its values
            }
        }
    }
}


