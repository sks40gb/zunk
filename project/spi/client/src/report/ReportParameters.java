/* $Heading$ */
package report;

import beans.DateSelectionField;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

/**
 * Interface to allow access by report specification
 * classes to the controls on the report page.
 */
public interface ReportParameters {

    public JComboBox getProjectCombo();
    public JComboBox getFieldCombo();
    public JComboBox getTeamCombo();   
    public DateSelectionField getDateField();   
    public DateSelectionField getEndDateField();   
    public JTextField getDaysField();   

    public JCheckBox getOrderCheckBox();
    public JCheckBox getOrderCheckBox2();
    public JCheckBox getOrderCheckBox3();
    public JButton getGenerateButton();

    public JComboBox getVolumeCombo();
    public JComboBox getBatchCombo();
    public JTextField getOccurrence(); 
}
