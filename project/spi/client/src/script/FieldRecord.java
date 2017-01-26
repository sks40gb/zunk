/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

/**
 * Class FieldRecord contains the record related to the field for which the validation is going to be executed.
 * @author sunil
 */
public class FieldRecord{

    /** Field value  for which the validation ot formatter function is going to be executed */
    private static String codedValue;

    /**
     * Reset all Coded Value for the field.
     */
    public static void reset() {
        codedValue = null;
    }

    /**
     * Get the coded value for the field for which the function is being applied.
     * @return Coded value of the field.
     */
    public static String getCodedValue() {
        return codedValue== null ? "" :codedValue;
    }

    /**
     * Set the coded value for the field.
     * @param codedValue - Coded Value
     */
    public static void setCodedValue(String value) {
        codedValue = value;
    }
}
