/*
 * XrefReaderBRS.java
 *
 * Created on December 10, 2007, 6:51 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.dbload;

import com.fossa.servlet.validate.ImgFields;
import com.fossa.servlet.validate.TxtFields;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author sunil
 */
public class XrefReaderL1BRS extends XrefReader {

    private static final String SACC    = "SACC";       
    private static final String DTYG    = "DTYG";          
    private static String SSOU          = "SSOU";
    private static String SREV          = "SREV";    
    private static String SDOC          = "SDOC";
    private static String FOLDER        = "Folder";
    private static String SOURCE        = "Source";   
    private String bates                = "";   
    private String fieldname;
    private int activeGroup             = 0;
    private ArrayList txtRecordList     = new ArrayList();    
    private HashMap  imgRecordMap       = new HashMap();
    private HashMap  brsRecordMap       = new HashMap();
    private ArrayList saccList          = new ArrayList();    
    private ArrayList page_list         = new ArrayList();
    private ArrayList project_l1_list   = new ArrayList();
    private boolean midSACC             = false;  
    private boolean midDTYG             = false;  
    
    private static Logger logger = Logger.getLogger("com.fossa.servlet.dbload");
    /*
     * get the page table records.
     */
    public ArrayList getPage_list() {
        return page_list;
    }
    
    /*
     * set the page table record in a ArrayList page_list.
     * @param -page_list - contains the record of the 
     * page table record.
     */
    public void setPage_list(ArrayList page_list) {
        this.page_list = page_list;
    }

    /*
     * get the project_l1 table record.
     */
    public ArrayList getProject_l1_list() {
        return project_l1_list;
    }

    /*
     * set the project_l1 table in record in ArrayList.
     * @param -project_l1_list - contains the record of the 
     * project_l1 table record.
     */ 
    public void setProject_l1_list(ArrayList project_l1_list) {
        this.project_l1_list = project_l1_list;
    }
    
       
    /**
     * Create a new XrefReaderBRS for the given XrefAcceptor.
     */
    public XrefReaderL1BRS(XrefAcceptor acceptor) {
        super(acceptor);
    }
    
    
    public HashMap getBrsRecordMap() {
        return brsRecordMap;
    }

    public void setBrsRecordMap(HashMap brsRecordMap) {
        this.brsRecordMap = brsRecordMap;
    }
    
    /*
     * Get the required file to get the full information about L1
     * @param brs_fileName - BRS file name
     * @param img_fileName - IMG file name
     * @param txt_fileName - Source txt file name
     */ 
    public void acceptLine(String brs_fileName, String img_fileName, String txt_fileName ) {
        try {         
            acceptBRS(brs_fileName, img_fileName, txt_fileName);
        } catch (ErrorException e) {
            logger.error("Exception while reading brs file" + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            error(e.getMessage());
        }
    }    
    
     /*
     * keep the l1 information into the ArrayList
     * @param brs_fileName - BRS file name
     * @param img_fileName - IMG file name
     * @param txt_fileName - Source txt file name
     */ 
      public void acceptBRS(String brs_fileName, String img_fileName, String txt_fileName ) throws ErrorException {
         readTxtFile(txt_fileName);
         readImgFile(img_fileName);
         readBrsFile(brs_fileName);
         createXrefListAndL1List();         
         
         for(Object data : this.getPage_list())
         {
             accept((ImageXref)data);
         }
    }
    
    
    /*
     * collect the page table information in page_list  
     * collect the project_l1 table information in project_l1_list  
     */ 
    private void createXrefListAndL1List()
    {
         
        int currentPointerTxt = 0;
        boolean midSREV = true;
        char boundary;
        String currentDocNumber = "";
        String preDocNumber = "DeFUltDocnUMBer";
        String preGroupType = "";
        String currentGroupType = "";
        
        L1Xref l1xref = new L1Xref();
        String type = "";
        String sourceFileName = "";
        String path ="";
        boolean  midSSOU = false;
       
        ImageXref data = new ImageXref();
        String current_Tif_File = null;             //to keep the current tif file name
        String prev_Tif_File_in_txt = null;         //to keep the previous tif file name in TXT and IMG file
        String prev_Tif_File = null;                //to keep the previous tif file name in TXT file
        String next_Tif_File = null;                //to keep the next tif file name
        
        //start with txt file to get date.
        for(Object record : txtRecordList)
        {   
            TxtFields txtfields = (TxtFields)record;            
            current_Tif_File = txtfields.group_one_filename.trim();
            
            //get the data for project_l1
            String groupType = txtfields.group_Type.trim();  
            
            if(groupType.equals(SSOU))
            {                
                midSSOU = true;
                type = SOURCE;
                sourceFileName = txtfields.group_one_filename;
                path = txtfields.group_one_path;
            }
            else if(groupType.equals(SREV))
            {                
                String folderFileName = txtfields.group_one_filename;  
                //added for L1
                String folderFileNameForL1 = txtfields.group_one_filename;    
                String pathL1 = txtfields.group_one_path;
                ImgFields imgfields = imgfields  = (ImgFields)imgRecordMap.get(folderFileName);
                
                int i = 0;
                while((imgfields == null) && (i < imgRecordMap.size()))
                {   
                    try
                    {
                        folderFileName = getNextTifFile(folderFileName);
                        imgfields  = (ImgFields)imgRecordMap.get(folderFileName);
                    }
                    catch(NumberFormatException e){
                       logger.error("Exception while getting the page information" + e);
                       StringWriter sw = new StringWriter();
                       e.printStackTrace(new PrintWriter(sw));
                       logger.error(sw.toString());
                    }
                }
                
                if(imgfields != null)
                {                                  
                    String bates_num = imgfields.bates_number;
                    if(midSSOU)
                    {
                        l1xref = new L1Xref();
                        l1xref.bates_number = bates_num;
                        l1xref.type = SOURCE;
                        l1xref.l1_file_name = sourceFileName;    
                        l1xref.group_one_path = pathL1;
                        project_l1_list.add(l1xref);
                        midSSOU = false;                                                  
                    }

                    l1xref = new L1Xref();
                    l1xref.bates_number = bates_num;
                    //l1xref.l1_file_name = folderFileName;
                    l1xref.l1_file_name = folderFileNameForL1;
                    l1xref.type = FOLDER ;
                    l1xref.group_one_path = path;
                    project_l1_list.add(l1xref);                   
                }
            }            
          
            //initailizing the prev_Tif_File
            if(currentPointerTxt == 0)
            {   
                prev_Tif_File = current_Tif_File;
                currentPointerTxt++;
            }
            else
            {   
                //if current file match with the next file ( file name is increment by 1) of the prevous file.
                // store the information of project_l1 table 
                // keep the record in project_l1_list having collection of L1Xref object.
                if(current_Tif_File.equals(getNextTifFile(prev_Tif_File)))
                {                                       
                    //ImgFields imgfields = (ImgFields)imgRecordMap.get(prev_Tif_File);
                }
                
                // store the information of page table. 
                // keep the record in page_list having collection of imagexref
                else
                {  
                    //keep the prev_Tif_File to prev_Tif_File_in_txt as prev_Tif_File can be changed
                    prev_Tif_File_in_txt = prev_Tif_File;
                    //untill the next file of the previous file is matched to the current tif file                                     
                    while(! current_Tif_File.equals(getNextTifFile(prev_Tif_File)))
                    {                                                  
                        prev_Tif_File = getNextTifFile(prev_Tif_File);                        
                        ImgFields imgfields = (ImgFields)imgRecordMap.get(prev_Tif_File);
                        //------------------------------------------------------                                                
                        ImageXref imagexref = new ImageXref();
                        imagexref.fileName = prev_Tif_File;
                        imagexref.groupOneFileName = prev_Tif_File_in_txt;                        
                        imagexref.groupOnePath = txtfields.group_one_path;                        
                        imagexref.bates = imgfields.bates_number;
                        imagexref.path = imgfields.path;
                        imagexref.documentNumber = imgfields.document_number;                        
                        imagexref.DTYG = (String)brsRecordMap.get(imgfields.bates_number);                                                
                        imagexref.activeGroup = activeGroup;
                        imagexref.offset = 0;
                        imagexref.fileType = 0;                                                
                        currentDocNumber = imgfields.document_number;
                        //getting the group type.(SSOU, SREV, SDOC)
                        currentGroupType = txtfields.group_Type;
                        
                        if(txtfields.group_Type.equals(SDOC) && ! currentGroupType.equals(preGroupType))
                        {                              
                           midSREV = true;
                        }
                        
                       if(midSREV)
                        {
                            boundary = 'D';     //"RANGE"
                            midSREV = false;                                
                        }
                        else
                        {
                            if(currentDocNumber.equals(preDocNumber))
                            {                               
                                boundary = ' '; //"NONE";
                            }
                            else
                            {
                                boundary = 'C' ;//"CHILD"
                            }
                        }
                       
                        imagexref.boundary = boundary;
                        page_list.add(imagexref);
                 
                        preDocNumber = currentDocNumber;
                        preGroupType = currentGroupType;
                    }
                }               
                prev_Tif_File = current_Tif_File;
                 
            }
        }
    }
    
  /*
   * to get the next tif file name 
   * @param  currentTifFile - current tif file name
   */ 
    private String getNextTifFile(String currentTifFile)
    {        
        int nextTifFile =  Integer.parseInt(currentTifFile.substring(0,currentTifFile.indexOf(".")));
        nextTifFile++;
        int numOfZeros = currentTifFile.length() - (nextTifFile+"").length();      
        String zeros = "";
        for(int i=0; i<(numOfZeros-4); i++)
        {
            zeros +="0"; 
        }                
        return (zeros + nextTifFile + ".tif");        
    }
    
    
   /*
    * Reading the TXT file.
    * Checking whether its exits or not.
    * Storing the information in txtRecordList.
    */ 
    private void readTxtFile(String txtFileName)
    {
        try{
            BufferedReader txtReader = new BufferedReader(new FileReader(txtFileName));        
            String readLine;   
                while( (readLine = txtReader.readLine())!=null)
                {  
                    //checking for blank line
                    if(readLine.trim().length() != 0)
                    {
                        try{                           
                            String lineInfo[] = readLine.split(",");        
                            String lineInfoFirstColumn = lineInfo[0];
                            String lineInfoSecColumn = lineInfo[1];        
                            String tifFileName = lineInfo[2].substring((lineInfo[2].indexOf('\\')+1));
                            String sourceFolder = lineInfo[2].substring(0, (lineInfo[2].length() - tifFileName.length()-1));        
                            String txtLineInfo[] = {lineInfoFirstColumn,lineInfoSecColumn,sourceFolder,tifFileName};                            
                            
                            //do not store the information of the tif file having the character or 
                            //string with the tif file name
                            try 
                            {
                               int filNameInIntFormat = Integer.parseInt(tifFileName.substring(0,tifFileName.indexOf(".")));
                            }catch(NumberFormatException e) {
                               logger.error("Exception while reading txt file" + e);
                               StringWriter sw = new StringWriter();
                               e.printStackTrace(new PrintWriter(sw));
                               logger.error(sw.toString());
                               continue;
                            }
                                
                            TxtFields txtfields = new TxtFields();
                            txtfields.group_Type = lineInfoFirstColumn;
                            txtfields.group_one_path = sourceFolder;
                            txtfields.group_one_filename = tifFileName;        
                            txtRecordList.add(txtfields);
                            }catch(Exception e){e.printStackTrace();}
                     }
                }
            }catch(Exception e){
               logger.error("Exception while reading txt file" + e);
               StringWriter sw = new StringWriter();
               e.printStackTrace(new PrintWriter(sw));
               logger.error(sw.toString());
            }
                
      }           
    
    /*
    * Reading the IMG file.
    * Checking whether its exits or not.
    * Storing the information in imgRecordList for further operation.
    */    
    private void readImgFile(String imgFileName)
    {
        try{                    
        ImgFields imgfields = new ImgFields();
        BufferedReader txtReader = new BufferedReader(new FileReader(imgFileName));        
        String row;   
        while( (row = txtReader.readLine())!=null)
        {              
            if(row.startsWith("."))
            {
                row = row.trim();    
                String field = row.substring(4, row.length()).trim();        
                //add img file record into map class      

                if(row.startsWith(".AN."))
                {
                   imgfields.bates_number = field;
                }
                else if(row.startsWith(".IA."))
                {   
                    //get the file name and then add extenstion of the file. 
                    String fileName =  field.substring(0, field.indexOf("tif"));            
                    imgfields.filename = fileName + ".tif";
                }

                else if(row.startsWith(".IT."))
                {          
                    imgfields.IT = field;
                }
                else if(row.startsWith(".IV."))
                {
                    imgfields.IV = field;
                }
                else if(row.startsWith(".ID."))
                {               
                    String path = field.substring(0,(field.indexOf('\\')));
                    String document_number = field.substring((field.indexOf('\\')+1),field.length());
                    
                    imgfields.path = path;                    
                    imgfields.document_number = document_number.trim();
                    
                    
                    //add records to fieldMap when all required details is found.
                    imgRecordMap.put(imgfields.filename, imgfields);  
                    //reset the imgFields to have next set of records. 
                    imgfields = new ImgFields();
                } 
          }
        }
        }catch(Exception e){
            logger.error("Exception while reading img file" + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
          }
    }
   
  
   /*
    * Reading the BRS file.
    * Checking whether its exits or not.
    * Storing the information in brsRecordList.
    */        
    private void readBrsFile(String brsFileName)
    {   
        try{
                ImgFields imgfields = new ImgFields();
                BufferedReader txtReader = new BufferedReader(new FileReader(brsFileName));        
                String row;   
                while( (row = txtReader.readLine())!=null)
                {      
                    row = row.trim();
                    if (row.startsWith("..")
                        && row.endsWith(":")) 
                    {        
                        // Clear the mid flags because if they were true,
                        // the previous tag was empty.            
                        midSACC = false;  
                        midDTYG = false;
                        // document_number, bates range or tag name (fieldname)
                        fieldname = row.substring(2, row.length()-1);

                        if(fieldname.equals(SACC)) 
                        {
                            //brsRecordMap.put(row, bates);
                            midSACC = true;
                        } 

                        if(fieldname.equals(DTYG)) 
                        {
                            //brsRecordMap.put(row, bates);
                            midDTYG = true;
                        }            
                    }              

                    else if (midSACC) 
                    {    
                        addToSaccList(row);
                    }

                    else if (midDTYG) 
                    {    
                        for(Object batesNumber : saccList)
                        {                         
                           brsRecordMap.put(batesNumber+"", row.trim());
                        }
                    }        
             }
        }catch(Exception e){
            logger.error("Exception while reading brs file" + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }
   
    
     /*
    * @param row - single row of file
    * it split the row where it find the value like "00003483-00003489", it saves all regular values to 
    * a arrayList (ie saccRecordList).
    */   
    private void addToSaccList(String row) throws Exception{
                // get the bates_number                 
                
                saccList = new ArrayList();
                bates = row.substring(0, row.indexOf(" ")).trim();
                String batesSplit[] = row.split(" - ");              
                
                if(batesSplit.length == 2) {                     
                    int startRange = Integer.parseInt( batesSplit[0]);
                    int endRange = Integer.parseInt(batesSplit[1]);
                    int totalBatesRange = endRange - startRange;
                    
                    for(int i=0; i<= totalBatesRange; i++)
                    {  
                        //adding all SACC to the arrayList
                        saccList.add((startRange+i)); 
                    }
                }
    }

  
    public void acceptLine(String line) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
