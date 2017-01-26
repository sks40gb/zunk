/* $Header: /home/common/cvsarea/ibase/dia/src/beans/DateVerifier.java,v 1.9.10.1 2006/02/22 20:05:51 nancy Exp $ */
package beans;

import com.lexpar.util.Log;

import java.awt.Toolkit;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

/**
 * This class exends <code>beans.LInputVerifier</code> to provide a verifier
 * specific to the DIA date style.
 */
public class DateVerifier extends LInputVerifier {

    private static DateVerifier theInstance = null;
    private boolean changedDate = false;
    // a single instance of Calendar for use in date calculations
    private Calendar theCalendar = new GregorianCalendar();

    /**
     * Create an instance of <code>DateVerifier</code>.
     */
    public DateVerifier() {
        formatter.setLenient(false);
    }

    /**
     * Return the singleton instance of <code>DateVerifier</code>.
     * @return the date verifier
     */
    public static DateVerifier createInstance() {
        if (theInstance == null) {
            theInstance = new DateVerifier();
        }
        return theInstance;
    }
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat mmddformatter = new SimpleDateFormat("MMdd");

    /**
     * Verify that the date is valid and standardize any zero value to eight
     * zeroes.
     * @return true if the date is valid; false if there is an error in the date
     */
    public boolean verify(JComponent input) {
        //Log.print("(DateVerifier) verify");
        LTextField field = (LTextField) input;
        String dateText = field.getText();
        changedDate = field.isChanged();
        int length;
        if (dateText == null || (length = dateText.length()) == 0) {
            return true;
        }

        ParsePosition pos = new ParsePosition(0);
        try {
            if (!field.getText().equals("00000000") && Integer.parseInt(dateText) == 0) {
                // If the date is zero, fill with eight zeroes.
                // empty date is valid
                field.setText("00000000");
                return true;
            }
            if (dateText.length() != 8) {
                // if non-zero, must be 8 digits
                return false;
            }

            // Non-numerics can't be entered -- see LDateField
            int yyyy = Integer.parseInt(dateText.substring(0, 4));
            int mm = Integer.parseInt(dateText.substring(4, 6));
            int dd = Integer.parseInt(dateText.substring(6));

            if (yyyy > 0 && mm > 0 && dd > 0) {
                // the usual case -- complete date entered
                if (yyyy > 1898 && yyyy < 2100 && formatter.parse(dateText, pos) != null) {
                    return true;
                } else {
                    return false;
                }
            }
            if (yyyy > 0) {
                if (yyyy > 1898 && yyyy < 2100) {
                } else {
                    // TODO - need specific invalid year message
                    return false;
                }
                if (mm <= 0) {
                    // year and day entered -- valid?
                    if (dd < 32) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (dd <= 0) {
                    // year and month entered
                    if (mm < 13) {
                        return true;
                    } else {
                        return false;
                    }
                } // else -- checked for all non-zero above

            } else {
                // year is 0
                if (mm <= 0) {
                    // only day entered -- valid?
                    if (dd < 32) {
                        return true;
                    } else {
                        return false;
                    }
                } else if (dd <= 0) {
                    // only month entered
                    if (mm < 13) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    // month and day entered
                    //Log.print("(DateVerifier).verify mm/dd/parsed " + mm + "/" + dd);
                    Date date = mmddformatter.parse(dateText.substring(4), pos);
                    // changed to avoid deprecated methods - wbe 2003-09-19
                    //if ((date.getMonth() + 1) == mm && date.getDate() == dd) {
                    theCalendar.setTime(date);
                    if ((theCalendar.get(Calendar.MONTH) + 1) == mm && theCalendar.get(Calendar.DAY_OF_MONTH) == dd) {
                        // still the same day -- must be same month
                        return true;
                    } else {
                        return false;
                    }
                }
            }
            //field.setText(formatter.format(theDate));

            // reset changedFlag to original value
            Log.print("(DateVerifier).verify !!! date not handled: " + dateText);
            field.setChanged(changedDate);

            return false;
        } catch (Throwable th) {
            // note - invalid date frequently reported as NullPointerException
            return false;
        }
    }

    /**
     * Calls <code>verify</code> to ensure that the input is valid.  Shows an
     * error dialog if the input is invalid.
     * @param input the <code>JComponent</code> upon which this <code>InputVerifier</code>
     * is set
     * @return true if the input is valid, false if the input contains errors
     */
    public boolean lShouldYieldFocus(JComponent input) {

        if (verify(input)) {
            return true;
        }
        //Log.print("(DateVerifier) shouldYieldFocus -- verify() returned false");

        Toolkit.getDefaultToolkit().beep();
        Log.print("BEEP> DateVerifier");
        JOptionPane.showMessageDialog(input, "Not a valid date", "Error", JOptionPane.ERROR_MESSAGE);
        return false;

    }
}
