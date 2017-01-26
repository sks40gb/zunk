/* $Header: /home/common/cvsarea/ibase/dia/src/report/OpenBatchReport.java,v 1.2 2004/06/02 21:37:07 weaston Exp $ */
package report;

import client.TaskGenerateReport;

/**
 * Batch report for project.
 */
final public class OpenBatchReport extends AbstractReport {

    public OpenBatchReport(ReportParameters param) {
        super(param);
    }
    
    public void enableControls() {
        disableControls();
        param.getGenerateButton().setEnabled(true);
    }

    public void generate() {

        TaskGenerateReport task;
        task = new TaskGenerateReport("report_open_batch");
        task.setTitle("All Open Batches");
        task.enqueue();
    }
}
