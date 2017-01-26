/* $Header: /home/common/cvsarea/ibase/dia/src/beans/MinMaxVerifier.java,v 1.5.2.1 2006/02/22 20:05:51 nancy Exp $ */
package beans;

import com.lexpar.util.Log;
import common.edit.ProjectMapper;


import java.awt.Toolkit;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 * An extension of <code>LInputVerifier</code> to verify that the value
 * of the user-entered data is within the bounds of the minimum and maximum
 * values defined for this field.
 */
public class MinMaxVerifier extends LInputVerifier {

    private static MinMaxVerifier theInstance = null;
    private static String min = null;
    private static String max = null;
    /** Track whether this field has been checked */
    public static boolean checked = false;

    /**
     * Create the singleton instance of MinMaxVerifier.
     * @param min the minimum value allowed in this field
     * @param max the maximum value allowed in this field
     */
    public MinMaxVerifier(String min, String max) {
        MinMaxVerifier.min = min;
        MinMaxVerifier.max = max;
    }

    /**
     * Create the singleton instance of MinMaxVerifier.
     * @param min the minimum value allowed in this field
     * @param max the maximum value allowed in this field
     * @return the verifier
     */
    public static MinMaxVerifier createInstance(String min, String max) {
        if (theInstance == null) {
            theInstance = new MinMaxVerifier(min, max);
        }
        return theInstance;
    }

    /**
     * Check the value of <code>input</code> to see if it is within
     * the bounds of the minimum and maximum defined for this field.
     * @param input the component containing the user-entered data
     * @return true of the data contains no errors; false otherwise
     */
    public boolean verify(JComponent input) {
        LField field = (LField) input;
        if (checked) {
            return true;
        }
        checked = true;
        String value = field.getText();

        if (value == null || value.length() == 0) {
            return true;
        }
        if (min != null && !min.equals("") && value != null && !value.equals("")) {
            if (value.compareToIgnoreCase(min) < 0) {
                // value is less than the min value
                return false;
            }
        }
        if (max != null && !max.equals("") && value != null && !value.equals("")) {
            if (value.compareToIgnoreCase(max) > 0) {
                // value is greater than the max value
                return false;
            }
        }
        return true;
    }

    public boolean lShouldYieldFocus(JComponent input) {

        if (verify(input)) {
            return true;
        }
        String message = "";
        if (min != null && !min.equals("") && max != null && !max.equals("")) {
            message = "Value must be between " + min + " and " + max + ", inclusive.";
        } else if (min != null && !min.equals("")) {
            message = "Value cannot be less than " + min + ".";
        } else {
            message = "Value cannot be greater than " + max + ".";
        }

        Toolkit.getDefaultToolkit().beep();
        Log.print("BEEP> MinMaxVerifier.lShouldYieldFocus");
        JOptionPane.showMessageDialog(input, message, "Error", JOptionPane.ERROR_MESSAGE);
        return false;

    }

    /**
     * If this field has already been checked, don't check again, but allow
     * the user to exit the field.
     * @param checked true to set the flag; false to clear it
     */
    public void setChecked(boolean checked) {
        MinMaxVerifier.checked = checked;
    }
}
