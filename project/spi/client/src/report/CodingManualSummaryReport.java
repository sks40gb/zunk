/*
 * CodingManualSummaryReport.java
 *
 * Created on January 28, 2008, 2:34 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package report;

import client.TaskGenerateReport;
import model.ManagedComboModel;

/**
 *
 * @author bmurali
 */
public class CodingManualSummaryReport extends AbstractReport{
    
    /** Creates a new instance of CodingManualSummaryReport */
    public CodingManualSummaryReport(ReportParameters param) {
         super(param);
        registerModel(param.getProjectCombo());
    }

    public void enableControls() {
        disableControls();
        param.getProjectCombo().setEnabled(true);
        if (param.getProjectCombo().getSelectedIndex() >= 0) {
            param.getGenerateButton().setEnabled(true);
            
        }
    }

    public void generate() {
        
        TaskGenerateReport task = new TaskGenerateReport("report_coding_manual");
        ManagedComboModel projectModel = (ManagedComboModel) param.getProjectCombo().getModel();
        int index = param.getProjectCombo().getSelectedIndex();
        task.setTitle("Coding Manual Summary");
        task.addParameter("Project",
                          (String) projectModel.getElementAt(index),
                          projectModel.getIdAt(index));
        task.enqueue();
    }
    
}
