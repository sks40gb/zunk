/* $Header: /home/common/cvsarea/ibase/dia/src/report/AbstractReport.java,v 1.6.2.1 2005/08/31 16:13:51 nancy Exp $ */
package report;

import model.ManagedComboModel;

import javax.swing.JComboBox;
import model.QueryComboModel;
import ui.ImportPageWithL1;

public abstract class AbstractReport {

    protected ReportParameters param;
   
    protected AbstractReport(ReportParameters param) {
        this.param = param;
    }
    
    /**
     * Disable all controls on the Report page.
     */
    public void disableControls() {

        param.getProjectCombo().setEnabled(false);
        param.getTeamCombo().setEnabled(false);   
        param.getDateField().setEnabled(false);   
        param.getEndDateField().setEnabled(false);   
        param.getDaysField().setEditable(false);  
        param.getOrderCheckBox().setVisible(false);
        param.getOrderCheckBox2().setVisible(false);
        param.getOrderCheckBox3().setVisible(false);
        param.getGenerateButton().setEnabled(false);
        param.getFieldCombo().setEnabled(false);
        param.getVolumeCombo().setEnabled(false);
        param.getBatchCombo().setEnabled(false);
        param.getOccurrence().setEnabled(false);
    }
    
    /**
     * Disable all controls and clear values.
     * Called when report radio button is selected.
     * Note.  Overridden for Team open and Team qc reports
     */
    public void initializeControls() {
        disableControls();
        param.getProjectCombo().setSelectedIndex(-1);
        param.getTeamCombo().setSelectedIndex(-1);   
        param.getDateField().setDate(null);   
        param.getEndDateField().setDate(null);   
        param.getDaysField().setText(""); 
        param.getOrderCheckBox().setEnabled(true);
        param.getOrderCheckBox().setSelected(false);
        param.getOrderCheckBox2().setEnabled(true);
        param.getOrderCheckBox2().setSelected(false);
        param.getOrderCheckBox3().setEnabled(true);
        param.getOrderCheckBox3().setSelected(false);
       // param.getFieldCombo().setSelectedIndex(-1);
        param.getVolumeCombo().setSelectedIndex(-1);
        param.getBatchCombo().setSelectedIndex(-1);
        param.getGenerateButton().setEnabled(false);
         
    }

    /**
     * Enable correct parameter controls for this report.
     * Subclasses should first call disableControls(),
     * then enable those controls that should
     * be enabled.  Generally, this method is
     * called whenever anything changes on the
     * ReportPage.
     */
    public abstract void enableControls();

    /**
     * Generate the report
     */
    public abstract void generate();

    /**
     * Convenience method to register the model for a (managed) JComboBox.
     */
    protected void registerModel(JComboBox control) {
        ManagedComboModel model = (ManagedComboModel) control.getModel();
        model.register();
    }
    
    protected void registerModel(JComboBox control,String value) {
        
          QueryComboModel fieldModel = null;
    }
}
