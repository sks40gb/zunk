/*
 * ImageFileExistance.java
 *
 * Created on 13 March, 2008, 8:45 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.validate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
/**
 * @author sunil
 */

/**Class which is used  to read the text file
 */
public class TestFilesContent
{
    
    private final String DOCUMENT_NUMBER = "Document-Number";
    private final String SACC = "SACC";   
    private final String UPLOAD = "UPLOAD";
    private String error_message = null;   
    
    private boolean midSACC = false;  
    private boolean midDocumentNumber = false;
    private String bates;
    
    private List batesList;
    private List saccRecordList;
    private Map brsRecordMap; 
    private Map imgRecordMap;
    
    private ImgFields imgfields; 
    private String fieldname;
    private int countSource;
    private String documentnumber = "";
    private boolean isToBeExcuted = false;
    private static Logger logger = Logger.getLogger("com.fossa.servlet.validate");
    
    /** Creates a new instance of BRSReader */
    public TestFilesContent() {
        brsRecordMap = new HashMap();
        imgRecordMap = new HashMap();
        saccRecordList = new ArrayList();
        imgfields  = new ImgFields();
        isToBeExcuted = true;
        countSource = 0;
    }
    
    /*
     * reading files (*.brs) and getting its record.
     * @brsFilePath - path of the brs file ex ""GPDIC024944/GPDIC024944_ln.brs""
     */ 
    public void readBrsFile(String brsFilePath){                 
        
        //if there is already another error terminate the process.
        if(! isToBeExcuted)
        {
            return;
        }
        
        batesList = new ArrayList();
         try {
            //Log.print("(BrsReader.read) reading " + dataname);
            BufferedReader reader = new BufferedReader(new FileReader(brsFilePath));
            // read and parse each line           
            String row;            
            
            while ((row = reader.readLine()) != null) {             
                parseBrs(row);
            }
            
            reader.close();            
            
        } catch (FileNotFoundException e) {
            logger.error("Exception while reading the BRS file." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            //JOptionPane.showMessageDialog(null,"File " + brsFilePath + " not found");
            String filePath = getRelativePath(brsFilePath);
            error_message = "File " + filePath + " not found";
            isToBeExcuted = false;            
            return;            
        } catch (IOException e) {
            logger.error("Exception while reading the BRS file." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        } catch(Exception exc) {
            logger.error("Exception while reading the BRS file." + exc);
            StringWriter sw = new StringWriter();
            exc.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }
    
    /*
     * relative path of the file
     * @param path - absolute path of file
     */ 
    private String getRelativePath(String path)
    {
        String relativePath = path;
        if(path.contains(UPLOAD))
        {
            relativePath = path.substring(path.indexOf(UPLOAD) + UPLOAD.length());
             
        }
        return relativePath;
    }
    
    /*
     * reading files (*.img) and getting its record.
     * @brsFilePath - path of the img file ex ""GPDIC024944/GPDIC024944_ln.img""
     */ 
    public void readImgFile(String imgFilePath){                 
        
         //if there is already another error terminate the process.
       if(error_message != null)
       {
           return;
       }
            
        batesList = new ArrayList();
         try {
            //Log.print("(BrsReader.read) reading " + dataname);
             
            BufferedReader reader = new BufferedReader(new FileReader(imgFilePath));
            // read and parse each line           
            String row;            
            
            while ((row = reader.readLine()) != null) {             
                parseImg(row);
            }
            reader.close();            
            
        } catch (FileNotFoundException e) {
            logger.error("Exception while reading the img file." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            //JOptionPane.showMessageDialog(null,"File " + imgFilePath + " not found");
            error_message = "File " + getRelativePath(imgFilePath) + " not found";
            isToBeExcuted = false;
            
        } catch (IOException e) {
            logger.error("Exception while reading the img file." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        } catch(Exception exc) {
            logger.error("Exception while reading the img file." + exc);
            StringWriter sw = new StringWriter();
            exc.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
    }
    
    /*
     * reading files (*.brs) and getting its record.
     * @brsFilePath - path of the txt file ex ""data/source_folder.txt""
     */ 
    public void readTxtFile(String txtFilePath){                 
        //if there is already another error terminate the process.
      if(error_message != null)
       {
           return;
       }
        
        batesList = new ArrayList();
         try {
            //Log.print("(BrsReader.read) reading " + dataname);
            BufferedReader reader = new BufferedReader(new FileReader(txtFilePath));
            // read and parse each line           
            String row;            
            
            while ((row = reader.readLine()) != null) {             
                parseTxt(row);
            }
            
            reader.close();            
            
        } catch (FileNotFoundException e) {
            logger.error("Exception while reading the txt file." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());            
            isToBeExcuted = false;
            //JOptionPane.showMessageDialog(null,"File " + txtFilePath + " not found");
            error_message = "File " + getRelativePath(txtFilePath) + " not found";
        } catch (IOException e) {
            logger.error("Exception while reading the txt file." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        } catch(Exception exc) {
            logger.error("Exception while reading the txt file." + exc);
            StringWriter sw = new StringWriter();
            exc.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }      
    }
    
    
    /*
    * @param row  passing a line from a file *.brs,
    *        It add keep the records of .brs file in a ArrayList using  addTosaccList.
    *        which is used to compare with .img file.
    */
    private void parseBrs(String row) throws Exception{
        row = row.trim();
        if (row.startsWith("..")
            && row.endsWith(":")) 
        {        
            // Clear the mid flags because if they were true,
            // the previous tag was empty.            
            midSACC = false;  
            midDocumentNumber = false;
            // document_number, bates range or tag name (fieldname)
            fieldname = row.substring(2, row.length()-1);
            
            if (fieldname.equals(DOCUMENT_NUMBER)) 
            {                
                midDocumentNumber = true;                
            }
            
            else if(fieldname.equals(SACC)) 
            {
                brsRecordMap.put(row, bates);
                midSACC = true;
            }            
        }
             
        else if (midDocumentNumber) 
        {             
            documentnumber = row.trim();                          
        }        
        
        else if (midSACC) 
        {    
            //keep the records of *.brs records(document number as key and bates number as value) into brsRecordMap
            brsRecordMap.put(documentnumber, row.trim());            
            addToSaccList(row);                 
        }        
    }
   
       
   /*
    * @param row  passing a line from a file
    *        It add keep the records of .img file in a object.
    *        object is stored in a map.
    */       
   public void parseImg(String row)
   {         
       
      if(row.startsWith("."))
      {
        row = row.trim();    
        String field = row.substring(4, row.length()).trim();        
        //add img file record into map class      
        
        if(row.startsWith(".AN."))
        {
           imgfields.AN = field;
        }
        else if(row.startsWith(".IA."))
        {   
            //get the file name and then add extenstion of the file. 
            String fileName =  field.substring(0, field.indexOf("tif"));              
            imgfields.IA = fileName + ".tif";
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
            //String directoryName = field.substring((field.indexOf('\\')+1),field.length());            
            String directoryName = field;            
            imgfields.ID = directoryName;
            //add records to fieldMap when all required details is found.
            imgRecordMap.put(imgfields.AN, imgfields);  
            //reset the imgFields to have next set of records. 
            imgfields = new ImgFields();
        }       
       
      }
      
   }   
    
   
   private void parseTxt(String row) throws Exception{
//        row = row.trim();
//        String source = row.substring(0,row.indexOf(',')).trim();
//        if(source.equals("SSOU"))
//        {               
//            countSource++;      
//            if(countSource > 1)
//            {
//                System.out.println("SOURCE documents coming after the other........");
//            }
//        }
        
    }
   
   
   /*
    * @param row - single row of file
    * it split the row where it find the value like "00003483-00003489", it saves all regular values to 
    * a arrayList (ie saccRecordList).
    */   
    private void addToSaccList(String row) throws Exception{
                // get the bates_number                 
                    
                bates = row.substring(0, row.indexOf(" ")).trim();
                String batesSplit[] = row.split(" - ");              
                
                if(batesSplit.length == 2) {                     
                    int startRange = Integer.parseInt( batesSplit[0]);
                    int endRange = Integer.parseInt(batesSplit[1]);
                    int totalBatesRange = endRange - startRange;
                    
                    for(int i=0; i<= totalBatesRange; i++)
                    {  
                        //adding all SACC to the arrayList
                        saccRecordList.add((startRange+i)); 
                    }
                }
    }
   
   /*
    * for checking whether a *.img file have relevant records of *.brs file.
    * if *.brs file has the requested record then it will check whether this file exists or not.
    */ 
   public String ValidateImgFile(String projectVolumePath) 
   {
       if(error_message != null)
       {
           return error_message;
       }
       //if there is already another error terminate the process.       
      
       File file = null;
       for (Object object : saccRecordList) 
       {
         ImgFields imgFileDetails = (ImgFields)imgRecordMap.get(object.toString());
                    
         if(imgFileDetails == null )
         {   
             isToBeExcuted = false;            
             error_message = "invalide img file";
             return error_message;
         }  
         else
         {                         
            String filePath = projectVolumePath + "/" +imgFileDetails.IV + "/" + imgFileDetails.ID.replace('\\','/' ) + "/" + imgFileDetails.IA;             
            BufferedReader reader = null;                            
            file = new File(filePath);            
            if(! file.isFile())
            {
                isToBeExcuted = false;              
                error_message = getRelativePath(file.getPath()) + " doesn't exists";
                return error_message;
            }
            reader = null;               
         }          
       }
       return error_message;
   }   
 
   
    public static void main(String[] args) {      
        
        TestFilesContent reader = new TestFilesContent();
        reader.readBrsFile("sdfjoisdjfo");  
        
    }
    
}
