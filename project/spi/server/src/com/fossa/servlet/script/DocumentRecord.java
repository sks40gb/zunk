/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.script;

import com.fossa.servlet.server.valueobjects.FieldData;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * Class DocumentRecord cantains all the record related to the document.
 * This state of this class is saved in SPiCA java file and that value is used while
 * running the script for the validation and formatting.
 * 
 * @author sunil
 */
public class DocumentRecord {

    /** Batch Number */
    private static int batchNumber = 0;    
    /** bates number*/
    private static String currentBatesNumber;
    /** Begin bates number */
    private static String beginBatesNumber; 
    /** End bates number */
    private static String endBatesNumber; 
    /** List of all bates no for the child */
    protected static List<String> batesList;
    /** Document number */
    private static String documentNumber;
    /** List of Fields record. */
    private static List<FieldData> fieldList = new ArrayList<FieldData>();    
    /** Current position of cursor means which field of list has been accessed last time . */
    private static int currentPostion;
    /** Total size of Field List */
    private static int maxSize;

    /**
     * Reset all Field and Documen level record.
     */
    public static synchronized void reset() {
        reset(false);
    }
    public static synchronized void reset(boolean clearAll) {
        if(clearAll){
            batesList = new ArrayList<String>();
        }
        currentBatesNumber = null;
        fieldList = new ArrayList<FieldData>();
        documentNumber = null;
        currentPostion = 0;
        maxSize = 0;
    }

    /**
     * Get total number of Fields in the list.
     * Max Size is equals to the total number of Field in the document.
     * @return
     */
    public static synchronized int getMaxSize() {
        return maxSize;
    }

    /**
     * Add the new Field to the list.
     * @param field Field to be added into the list.
     */
    public static synchronized void addField(FieldData field) {
        if (fieldList == null) {
            fieldList = new ArrayList<FieldData>();
            maxSize = 0;
        }
        fieldList.add(field);
        //increase the maximumn size as the field is added to the list.
        maxSize++;
    }

    /**
     * Get the field from the list by the Field Name.     
     * @param name Field Name
     * @return     If the field name is matched in the list then Field Object will be returned else
     *             null is returned.
     */
    public static synchronized FieldData getField(String name) {
        for (int i = 0; i < fieldList.size(); i++) {
            if (fieldList.get(i).getFieldName().equals(name)) {
                currentPostion = i;
                return fieldList.get(i);
            }
        }
        return null;
    }

    /**
     * Get the field from the Field List by index.
     * @param index Field Index in the list.
     * @return      returns Field if the index is not greater than the list size else
     *              returns null value.
     */
    public static synchronized FieldData getField(int index) {
        if (index >= maxSize) {
            return null;
        }
        currentPostion = index;
        return fieldList.get(index);
    }

    /**
     * Get the Previous data from the currently accessed Field.
     * This works on the basis of index of list. If the current Field is in index 3 of 
     * List then access the Field in index of 2 of List.
     * @return
     */
    public static synchronized FieldData getPreviousField() {
        if (currentPostion > 0) {
            return fieldList.get(--currentPostion);
        } else {
            return null;
        }
    }

    /**
     * Get the Field Value by using the Field Name.
     * @param name  Field Name
     * @return      return Field Value if field name is found in the list else
     *              retrun null.
     */
    public static synchronized String getFieldValue(String name) {

        for (int i = 0; i < fieldList.size(); i++) {
            if (fieldList.get(i).getFieldName().equals(name)) {
                currentPostion = i;
                return fieldList.get(i).getFieldValue();
            }
        }
        return null;
    }

    /**
     * Get the Field value by using the index of the list.
     * @param index  Field Index in the list.
     * @return rerurn Field value if the index is not greater than Field List size else
     *         return Null value.
     */
    public static synchronized String getFieldValue(int index) {

        return index >= maxSize ? null : fieldList.get(index).getFieldValue();
    }

    /**
     * Get the total no of Fields in the Field List.
     * @return return total no of Fields in the list.
     *         If the Field List is not set and it is null then return the size 0.
     */
    public static synchronized int getTotalFields() {
        return fieldList == null ? 0 : fieldList.size();
    }

    /**
     * Get the list of bates number for the volume.
     * @return
     */
    public static synchronized List getBatesNumberList() {
        return batesList;
    }

    /**
     * Add the bates number to the bates list.
     * @param bateNumber
     */
    public static synchronized void addBatesNumber(String bateNumber) {

        if (batesList == null) {
            batesList = new ArrayList();
        }
        batesList.add(bateNumber);
    }

    /**
     * Get the next bates number from the current bates number.
     * @return
     */
    public static synchronized String getNextBatesNumber() {
        int indexOfCurentBatesNumber = batesList.indexOf(currentBatesNumber);
        indexOfCurentBatesNumber++;
        if (indexOfCurentBatesNumber >= batesList.size()) {
            return null;
        }
        return batesList.get(indexOfCurentBatesNumber).toString();
    }

    /**
     * Get the previous bates number from the current bates number.
     * @return
     */
    public static synchronized String getPreviousBatesNumber() {
        int indexOfCurentBatesNumber = batesList.indexOf(currentBatesNumber);
        indexOfCurentBatesNumber--;
        assert batesList.size() > 0 : "There is no bates number";
        if (indexOfCurentBatesNumber < 0) {
            return null;
        }
        return batesList.get(indexOfCurentBatesNumber).toString();
    }

    /**
     * Get the current bates number for the document.
     * @return - Bates Number of the document.
     */
    public static synchronized String getCurrentBatesNumber() {
        return currentBatesNumber;
    }
    
    
    /**
     * Set the current bates number for the document.
     * @param batesNumber - Current Document Number
     */
    public static synchronized void setCurrentBatesNumber(String batesNumber) {
        currentBatesNumber = batesNumber;
    }

    /**
     * Get the Document Number.
     * @return Document Number
     */
    public static synchronized String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Set the Document Number
     * @param documentNumber
     */
    public static synchronized void setDocumentNumber(String documentNumber) {
        DocumentRecord.documentNumber = documentNumber;
    } 

    /**
     * Get the Batch Number for the Document.
     * @return Batch Number
     */
    public static synchronized int getBatchNumber() {
        return batchNumber;
    }

    /**
     * Set the Batch Number for the Document.
     * @param batchNumber
     */
    public static synchronized void setBatchNumber(int batchNumber) {
        DocumentRecord.batchNumber = batchNumber;
    }

    /**
     * Set begin bates number
     * @param beginBatesNumber begin bates number
     */
    public static void setBeginBatesNumber(String beginBatesNumber) {
        DocumentRecord.beginBatesNumber = beginBatesNumber;
    }
    
    /**
     * Get teh begin bates number     
     * @return begin bates number
     */
    public static String getBeginBatesNumber() {
        return beginBatesNumber;
    }

    /**
     * Set end bates number
     * @param endBatesNumber end bates number
     */
    public static void setEndBatesNumber(String endBatesNumber) {
        DocumentRecord.endBatesNumber = endBatesNumber;
    }
    
    /**
     * Get end bates number
     * @return
     */
    public static String getEndBatesNumber() {
        return endBatesNumber;
    }
    
}
