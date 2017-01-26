/* $Header: /home/common/cvsarea/ibase/dia/src/report/PayrollTotalsReport.java,v 1.2 2005/06/12 12:45:07 weaston Exp $ */
package report;

import client.Global;
import client.TaskGenerateReport;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Payroll detail report.
 */
public final class PayrollTotalsReport extends AbstractReport {

    Calendar endCalc = null;

    /**
     * Create a new PayrollTotalsReport instance.
     * @param param An instance of ReportParameters,
     *   providing visibility to the controls
     *   on the report page.
     */
    public PayrollTotalsReport(ReportParameters param) {
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
//                    param.getDateField().getDate());
//            }

            // enable when date selected and at least one box checked
            System.out.println(param.getDateField().getDate()+".."+param.getEndDateField().getDate());
            if (param.getEndDateField().getDate().compareTo(param.getDateField().getDate()) >= 0) {
                param.getGenerateButton().setEnabled(true);
            }
        }
    }

    public void generate() {
        TaskGenerateReport task = new TaskGenerateReport("report_payroll_detail");
        task.setTitle("Payroll");
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
