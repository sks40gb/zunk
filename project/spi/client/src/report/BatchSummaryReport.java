/* $Header: /home/common/cvsarea/ibase/dia/src/report/BatchSummaryReport.java,v 1.1 2004/08/21 14:24:22 weaston Exp $ */
package report;

import client.TaskGenerateReport;
import model.ManagedComboModel;

/**
 * Batch summary report for project.
 */
public final class BatchSummaryReport extends AbstractReport {

    public BatchSummaryReport(ReportParameters param) {
        super(param);
        registerModel(param.getProjectCombo());
    }
    
    public void enableControls() {
        disableControls();
        param.getProjectCombo().setEnabled(true);
        if (param.getProjectCombo().getSelectedIndex() >= 0) {
            param.getGenerateButton().setEnabled(true);
            param.getBatchCombo().setEnabled(false);
            param.getOccurrence().setEnabled(false);
            param.getVolumeCombo().setEnabled(false);
        }
    }

    public void generate() {

        TaskGenerateReport task = new TaskGenerateReport("report_summary_project");
        ManagedComboModel projectModel = (ManagedComboModel) param.getProjectCombo().getModel();
        int index = param.getProjectCombo().getSelectedIndex();
        task.setTitle("Project Summary");
        task.addParameter("Project",
                          (String) projectModel.getElementAt(index),
                          projectModel.getIdAt(index));
        task.enqueue();
    }
}
