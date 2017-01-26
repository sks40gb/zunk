/* $Header: /home/common/cvsarea/ibase/dia/src/report/StaleBatchReport.java,v 1.4 2004/06/02 21:37:07 weaston Exp $ */
package report;

import client.TaskGenerateReport;
import model.ManagedComboModel;

/**
 * Batch report for project.
 */
public final class StaleBatchReport extends AbstractReport {

    public StaleBatchReport(ReportParameters param) {
        super(param);
    }
    
    public void enableControls() {
        disableControls();
        param.getDaysField().setEditable(true);
        try {
            if(Integer.parseInt(param.getDaysField().getText()) > 0) {
                param.getGenerateButton().setEnabled(true);
            }
        } catch (Exception e) {
            // Do not enable on invalid value
        }
    }

    public void generate() {

        TaskGenerateReport task = new TaskGenerateReport("report_stale_batch");
        ManagedComboModel projectModel = (ManagedComboModel) param.getProjectCombo().getModel();
        int index = param.getProjectCombo().getSelectedIndex();
        task.setTitle("Stale Batches");
        task.addParameter("Days Elapsed",
                          param.getDaysField().getText());
        task.enqueue();
    }
    
    
}
