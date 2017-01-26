package script;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;
import java.util.List;

/**
 * Class DocumentRecord cantains all the record related to the document.
 * This state of this class is saved in SPiCA java file and that value is used while
 * running the scipt for the validation.
 * 
 * @author sunil
 */
public class DocumentRecord extends FieldRecord{

    /** Begin bates Number */
    private static String beginBatesNumber = "";
    /** End bates number */
    private static String endBatesNumber = "";
    /** Current bates number */
    private static String currentBatesNumber;
    /** Document Number */
    private static String documentNumber;
    /** Batch Number */
    private static int batchNumber = 0;
    /** Current position or cursor which point that which field is accessed currently. */
    private static int currentPostion;
    /** Size of the Field List or Number of Field in the FieldList */
    private static int maxSize;
    /** bates List */
    protected static List<String> batesList;
    /** Field List contains all the field for the document */
    private static List<FieldData> fieldList = new ArrayList<FieldData>();
    ;

    /**
     * Reset all Field, Document and Volume level record.
     */
    public static void reset() {
        batesList = new ArrayList<String>();
        currentBatesNumber = null;
        fieldList = new ArrayList<FieldData>();
        documentNumber = null;
        currentPostion = 0;
        maxSize = 0;
    }

    /**
     * Get the total number of Field of document.
     * @return number of fields.
     */
    public static int getMaxSize() {
        return maxSize;
    }

    /**
     * Add Field to the Field List
     * @param field
     */
    public static void addField(FieldData field) {
        if (fieldList == null) {
            fieldList = new ArrayList<FieldData>();
            maxSize = 0;
        }
        fieldList.add(field);
        maxSize++;
    }

    /**
     * Get Field by using the Field name.
     * @param name Field Name
     * @return Field if the field exists in the list else return null.
     */
    public static FieldData getField(String name) {
        for (int i = 0; i < fieldList.size(); i++) {
            if (fieldList.get(i).getFieldName().equals(name)) {
                currentPostion = i;
                return fieldList.get(i);
            }
        }
        return null;
    }

    /**
     * Get the Field using the index.
     * @param index  Index
     * @return       if the index is greater than return null else return Field.
     */
    public static FieldData getField(int index) {
        if (index >= maxSize) {
            return null;
        }
        currentPostion = index;
        return fieldList.get(index);
    }

    /**
     * Get the previous Field.
     * @return
     */
    public static FieldData getPreviousField() {
        if (currentPostion > 0) {
            return fieldList.get(--currentPostion);
        } else {
            return null;
        }
    }

    public static String getFieldValue(String name) {

        for (int i = 0; i < fieldList.size(); i++) {
            if (fieldList.get(i).getFieldName().equals(name)) {
                currentPostion = i;
                return fieldList.get(i).getFieldValue();
            }
        }
        return null;
    }

    /**
     * Get Field Value of the Field using the index in the Field List.
     * @param index Index
     * @return      Field Value.
     */
    public static String getFieldValue(int index) {
        return fieldList.get(index).getFieldValue();
    }

    public static int getTotalFields() {
        return fieldList == null ? 0 : fieldList.size();
    }

    /**
     * Get the list of bates number for the volume.
     * @return
     */
    public static List getBatesNumberList() {
        return batesList;
    }

    /**
     * Add the bates number to the bates list.
     * @param bateNumber
     */
    public static void addBatesNumber(String bateNumber) {

        if (batesList == null) {
            batesList = new ArrayList();
        }
        batesList.add(bateNumber);
    }

    /**
     * Get the next bates number from the current bates number.
     * @return
     */
    public static String getNextBatesNumber() {
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
    public static String getPreviousBatesNumber() {
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
    public static String getCurrentBatesNumber() {
        return currentBatesNumber;
    }

    /**
     * Set the current bates number for the document.
     * @param batesNumber - Current Document Number
     */
    public static void setCurrentBatesNumber(String batesNumber) {
        currentBatesNumber = batesNumber;
    }

    /**
     * Get Document Number
     * @return Document Number
     */
    public static String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Set the Document Number
     * @param Document Number
     */
    public static void setDocumentNumber(String documentNumber) {
        DocumentRecord.documentNumber = documentNumber;
    }

    /**
     * Get the begin bates number.
     * @return
     */
    public static String getBeginBatesNumber() {
        return beginBatesNumber;
    }

    /**
     * Set the begin bates number
     * @param beginBatesNumber begin bates number
     */
    public static void setBeginBatesNumber(String beginBatesNumber) {
        DocumentRecord.beginBatesNumber = beginBatesNumber;
    }

    /**
     * Get the end bates number
     * @return end bates number
     */
    public static String getEndBatesNumber() {
        return endBatesNumber;
    }

    /**
     * Set end bates number
     * @param endBatesNumber end bates number
     */
    public static void setEndBatesNumber(String endBatesNumber) {
        DocumentRecord.endBatesNumber = endBatesNumber;
    }
}
