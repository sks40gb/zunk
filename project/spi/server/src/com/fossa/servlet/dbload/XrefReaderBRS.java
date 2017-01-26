/*
 * XrefReaderBRS.java
 *
 * Created on December 10, 2007, 6:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.dbload;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.log4j.Logger;

/**
 *
 * @author Bala
 */
public class XrefReaderBRS extends XrefReader {
    private static String GROUP_ONE_BATES = "00000000";
    
    private static String PATH  = "data";
    private static String DBID   = "DBID";    
    private static String range = "";
    private static String document_number = "";  
    private String groupOnePath = "";
    private String groupOneFileName = "";
    private int activeGroup = 0;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.dbload");

    /**
     * Create a new XrefReaderBRS for the given XrefAcceptor.
     */
    public XrefReaderBRS(XrefAcceptor acceptor) {
        super(acceptor);
    }

    /**
     * Accept and process the given input line.
     *                                                                      range  | | doc_num | bates
     * \\Rankinv03\DRRC\Data\Jobs\P04\GPDIC001594_000\DATA|00000003.tif|31|00000001|4|06809646|00000000|..DBID:GPDIS|..CDVO:GPDIC001594|..CBOX:GPDIC001594|..CNUM:|..CNAM:|..DISC:|..COLL:|..MCAS:|..MCFN:|..CREL:N|..RCFN:|..STCL:N|..DTYG:NHTSA IR|..DTVC:NHTSA IR|..ISI1:|..ISI2:|..ISCM:|..SCHM:NON-PRIVILEGED|..PRTR:|..PROT:|..INVI:|..INVD:|..TFID:-1||
     * \\Rankinv03\DRRC\Data\Jobs\P04\GPDIC001594_000\DATA|00000004.tif|31|00000001|4|06809646|21302873|..DBID:GPDIS|..CDVO:GPDIC001594|..CBOX:GPDIC001594|..CNUM:|..CNAM:|..DISC:|..COLL:|..MCAS:|..MCFN:|..CREL:N|..RCFN:|..STCL:N|..DTYG:NHTSA IR|..DTVC:NHTSA IR|..ISI1:|..ISI2:|..ISCM:|..SCHM:NON-PRIVILEGED|..PRTR:|..PROT:|..INVI:|..INVD:|..TFID:-1||
     * \\Rankinv03\DRRC\Data\Jobs\P04\GPDIC001594_000\DATA|00000005.tif|31|00000001|5|06809647|00000000|..DBID:GPDIS|..CDVO:GPDIC001594|..CBOX:GPDIC001594|..CNUM:|..CNAM:|..DISC:|..COLL:|..MCAS:|..MCFN:|..CREL:N|..RCFN:|..STCL:N|..DTYG:NHTSA IR|..DTVC:NHTSA IR|..ISI1:|..ISI2:|..ISCM:|..SCHM:NON-PRIVILEGED|..PRTR:|..PROT:|..INVI:|..INVD:|..TFID:-1||
     * \\Rankinv03\DRRC\Data\Jobs\P04\GPDIC001594_000\DATA|00000006.tif|31|00000001|5|06809647|21302874|..DBID:GPDIS|..CDVO:GPDIC001594|..CBOX:GPDIC001594|..CNUM:|..CNAM:|..DISC:|..COLL:|..MCAS:|..MCFN:|..CREL:N|..RCFN:|..STCL:N|..DTYG:NHTSA IR|..DTVC:NHTSA IR|..ISI1:|..ISI2:|..ISCM:|..SCHM:NON-PRIVILEGED|..PRTR:|..PROT:|..INVI:|..INVD:|..TFID:-1||
     */
    public void acceptLine(String line) {
        try {
            acceptLineBRS(line);
        } catch (ErrorException e) {
            logger.error("Exception while reading brs file" + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            error(e.getMessage());
        }
    }
     
    private void acceptLineBRS(String line) throws ErrorException {

        // Break line into fields, on | boundaries
        String[] fields = line.split("\\|"); // escape the regex 'or' operator

        if (fields.length < 6) {
            throw new ErrorException(
                    "Wrong number of semicolon-separated fields in Cross Ref. File");
        }

        ImageXref data = null;

        //Log.print("(XRBRS) line: " + line);
        //Log.print("(XRBRS) " + fields.length + "/" + fields[1]);
        if (! fields[6].trim().equals(GROUP_ONE_BATES)) {
            data = new ImageXref();
        }

        // get path, changing backslash to forward slash
        StringBuffer pathBuffer = new StringBuffer(fields[0].trim());
        int pos;
        while ((pos = pathBuffer.indexOf("\\")) >= 0) {
            pathBuffer.setCharAt(pos, '/');
        }
        
        if (fields[6].trim().equals(GROUP_ONE_BATES)) {
            // this is the group 1 record, just save some data
            groupOnePath = pathBuffer.toString();
            //groupOnePath = GROUP_ONE_PATH;
            groupOneFileName = fields[1].trim();
            //Log.print("(XRBRS.acceptLine) group 1 " + groupOneFileName);
            activeGroup = 1;
            return;
        }
        
        data.path = PATH;
        data.groupOnePath = groupOnePath;
        data.activeGroup = activeGroup;

        // filename of the image
        data.fileName = fields[1].trim();
        data.groupOneFileName = groupOneFileName;

        // boundary
        if (! fields[3].trim().equals(range)) {
            range = fields[3].trim();
            document_number = fields[5].trim();
            data.boundary = 'D';
        } else if (! fields[5].trim().equals(document_number)) {
            document_number = fields[5].trim();
            data.boundary = 'C';
        } else {
            data.boundary = ' ';
        }

        data.bates = fields[6].trim().toUpperCase();
        data.documentNumber = fields[5].trim().toUpperCase();
        data.offset = 0;
        data.fileType = 0;

        // volume
        //int count = 0;
        //for (int i = 7; i < fields.length; i++) {
        //    //if (fields[i].trim().equals(VOLUME)) {
            //    data.originalVolume = fields[i].trim().substring(7);
            //    data.volume = fields[i].trim().substring(7);
            //    count++;
            //}
            //if (count > 2) {
            //    // got all three fields
            //    break;
            //}
        //}

        // Accept the data.
        accept(data);
    }
}
