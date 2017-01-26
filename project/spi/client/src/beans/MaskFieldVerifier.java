/* $Header: /home/common/cvsarea/ibase/dia/src/beans/MaskFieldVerifier.java,v 1.8.2.3 2006/02/22 20:05:51 nancy Exp $ */
package beans;

import com.lexpar.util.Log;
import java.awt.Toolkit;
import java.text.ParseException;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JOptionPane;
import javax.swing.text.MaskFormatter;

/**
 * An extension of <code>LInputVerifier</code> to verify that the format
 * of the user-entered data matches the mask defined for this DIA field.
 */
public class MaskFieldVerifier extends LInputVerifier {

    private static MaskFieldVerifier theInstance = null;
    private static boolean checked = false;

    /**
     * Create the singleton instance of MaskFieldVerifier.
     * @return the verifier
     */
    public static MaskFieldVerifier createInstance() {
        if (theInstance == null) {
            theInstance = new MaskFieldVerifier();
        }
        return theInstance;
    }

    /**
     * Check the format of <code>input</code> to see if it matches
     * the mask defined for this field.
     * @param input the component containing the user-entered data
     * @return true of the data contains no errors; false otherwise
     */
    public boolean verify(JComponent input) {
        //Log.print("verify");
        if (checked) {
            return true;
        }
        checked = true;
        if (input instanceof JFormattedTextField) {
            if (!((LFormattedTextField) input).isChanged()) {
                // no changes, allow exit from field
                //Log.print("(MFV.verify) not changed");
                return true;
            }
            JFormattedTextField ftf = (JFormattedTextField) input;
            MaskFormatter formatter = (MaskFormatter) ftf.getFormatter();
            //Log.print("(MaskFieldVerifier) formatter " + formatter.);
            if (formatter != null) {
                String text = ftf.getText();
                JFormattedTextField ftf2 = new JFormattedTextField((AbstractFormatter) formatter);
                //Log.print("(MFV.verify) formatter not null " + ftf2.getText());
                if (((String) ftf2.getText()).equals(text)) {
                    // mask, allow exit from field
                    //Log.print("(MFV.verify) is mask " + ftf.getText());
                    return true;
                }
                if (text.length() < 1) {
                    // no value, allow exit from field
                    return true;
                }
                try {
                    formatter.stringToValue(text);
                    return true;
                } catch (ParseException pe) {
                    Log.print("(MFV.verify) invalid: " + text);
                    return false;
                }
            }
        }
        return true;
    }

    public boolean lShouldYieldFocus(JComponent input) {
        //Log.print("(MaskFieldVerifier) shouldYieldFocus");
        if (verify(input)) {
            return true;
        }
        checked = true;

        Toolkit.getDefaultToolkit().beep();
        Log.print("BEEP> MaskFieldVerifier");
        JOptionPane.showMessageDialog(input, "The data entered is invalid for the field format.", "Format Error",
                JOptionPane.ERROR_MESSAGE);
        return false;
    }

    /**
     * If this field has already been checked, don't check again, but allow
     * the user to exit the field.
     * @param checked true to set the flag; false to clear it
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
