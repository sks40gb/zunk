/* $Header: /home/common/cvsarea/ibase/dia/src/beans/LField.java,v 1.9.8.1 2006/02/22 20:05:51 nancy Exp $ */

package beans;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
            
/** 
 * Interface for Lexpar screen fields.
 * Allows operations on any screen field
 */

public interface LField {

    /**
     * Return the value of the field in <code>String</code> format.
     * @return the field value
     */
    public String getText();

    /**
     * Set the value of the field from <code>String</code> format and
     * check or set <code>changeFlag</code>.
     * @param text the new field value
     */
    public void setText(String text);

    /**
     * Enable or disable the field.
     * @param flag true to enable; false to disable
     */
    public void setEnabled(boolean flag);

    /**
     * Clear the field.
     */
    public void clearField();

    /**
     * Record whether field was changed by the user.
     * Note that a field with default value is
     * NOT considered changed.  A record is
     * considered changed if any field is changed.
     */
    public void setChanged(boolean flag);

    /**
     * Return the changed state.
     * @see #setChanged
     */
    public boolean isChanged();

    /**
     * Set the JCheckBox used in QC and QA.  Default is null.
     */
    public void setCheckBox(JCheckBox checkBox);

    public void setTextField(JTextField textField);
    
    public JTextField getTextField();
    /**
     * Return the JCheckBox used in QC and QA.
     */
    public JCheckBox getCheckBox();

    /** defined in Java components */
    public void requestFocus();
    /** defined in Java components */
    public boolean requestFocusInWindow();
    /** defined in Java components */
    public boolean hasFocus();
    /** defined in Java components */
    public void setBackground(Color c);
    /** defined in Java components */
    public void setForeground(Color c);
    /** defined in Java components */
    public void setNextFocusableComponent(Component c);
    
    public LComboBox getComboBox();
    
    public void setComboBox(LComboBox combo);
}
