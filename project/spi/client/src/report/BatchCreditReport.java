/* $Header: /home/common/cvsarea/ibase/dia/src/report/BatchCreditReport.java,v 1.3 2004/06/02 21:37:07 weaston Exp $ */
package report;

import client.Global;
import client.TaskGenerateReport;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Batch credit report.
 */
public final class BatchCreditReport extends AbstractReport {

    Calendar endCalc = null;

    public BatchCreditReport(ReportParameters param) {
        super(param);
    }
    
    public void enableControls() {
        disableControls();
        param.getDateField().setEnabled(true);
        param.getEndDateField().setEnabled(true);
        if(param.getDateField().getDate() != null  && param.getEndDateField().getDate()!=null) {
            // default for end date
//            if (param.getEndDateField().getDate() == null) {
//                param.getEndDateField().setDate(
//                        param.getDateField().getDate());
//            }
            if (param.getEndDateField().getDate().compareTo(param.getDateField().getDate()) >= 0) {
                param.getGenerateButton().setEnabled(true);
            }
        }
    }

    public void generate() {

        TaskGenerateReport task = new TaskGenerateReport("report_batch_credit");
        task.setTitle("Batch Credit");
        task.addParameter("Start Date",
                          param.getDateField().getTextField().getText(),
                          param.getDateField().getTextField().getText());
        if (endCalc ==  null) {
            endCalc = Calendar.getInstance(
                    TimeZone.getTimeZone(Global.theServerConnection.getTimeZoneID()));
        }
        endCalc.setTime(param.getEndDateField().getDate());
        endCalc.add(Calendar.DATE, +1);
        task.addParameter("End Date",
                          param.getEndDateField().getTextField().getText(),
                          param.getEndDateField().getTextField().getText());
        task.enqueue();
    }
    
    
}
