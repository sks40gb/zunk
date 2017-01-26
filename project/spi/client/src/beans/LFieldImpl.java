/* $Header: /home/common/cvsarea/ibase/dia/src/beans/LFieldImpl.java,v 1.2 2004/09/21 18:39:05 weaston Exp $ */

package beans;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import ui.SplitPaneViewer;
            
/** Implementation support for Lexpar screen fields.
 *  Each instance of LField should declare an LFieldImpl variable
 *  named fieldImpl and initialize it with a new LFieldImpl
 *  or an anonymous subclass of LFieldImpl.  The LField methods
 *  may be forwarded to the LFieldImpl, or they may be overridden.
 *  If overridden, please document the overriding instances of
 *  LField in the javadoc comments below.
 */

class LFieldImpl {

    private LField theField;
    private boolean changed = false;
    private JCheckBox checkBox = null;
    private JTextField textField = null;
    public LComboBox comboBox = null;    
    
    
    
    LFieldImpl(LField theField) {
        this.theField = theField;
    }

    /**
     * Called from LField subclasses at start of setText.
     */
    void checkChanged(String text) {
        if (! changed) {
            String oldText = theField.getText();
            if (text == null || text.length() == 0) {
                if (oldText != null && oldText.length() != 0) {
                    changed = true;
                }
            } else if (! text.equals(oldText)) {
                changed = true;
            }
        }
    }

    /**
     * setChanged
     */
    public void setChanged(boolean flag) {
        this.changed = flag;
        if (checkBox != null) {
            if (flag) {
                if (! checkBox.isEnabled()) {
                    checkBox.setEnabled(true);
                    checkBox.setSelected(true);
                }
            }
        }
    }

    /**
     * isChanged
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * Set the JCheckBox used in QC and QA.  Default is null.
     */
    public void setCheckBox(JCheckBox checkBox) {
        this.checkBox = checkBox;
    }

    /**
     * Return the JCheckBox used in QC and QA.
     */
    public JCheckBox getCheckBox() {
        return checkBox;
    }

   public JTextField getTextField()
   {
      return textField;
   }

   public void setTextField(JTextField textField)
   {
      this.textField = textField;
   }

   public LComboBox getComboBox()
   {
      return comboBox;
   }

   public void setComboBox(LComboBox comboBox)
   {
      this.comboBox = comboBox;
   }
    
    
}
