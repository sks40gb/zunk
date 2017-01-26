/* $Header: /home/common/cvsarea/ibase/dia/src/report/PayrollSummaryReport.java,v 1.4 2005/07/25 16:09:50 nancy Exp $ */
package report;

import client.Global;
import client.TaskGenerateReport;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Payroll summary report.
 */
public final class PayrollSummaryReport extends AbstractReport {

    Calendar endCalc = null;

    /**
     * Create a new PayrollSummaryReport instance.
     * @param param An instance of ReportParameters,
     *   providing visibility to the controls
     *   on the report page.
     */
    public PayrollSummaryReport(ReportParameters param) {
        super(param);
    }
    
    public void enableControls() {
        disableControls();
        param.getDateField().setEnabled(true);
        param.getEndDateField().setEnabled(true);
        param.getOrderCheckBox().setText("By User/Volume");
        param.getOrderCheckBox().setVisible(true);
        param.getOrderCheckBox2().setText("By User/Project");
        param.getOrderCheckBox2().setVisible(true);
        param.getOrderCheckBox3().setText("By Project/Volume");
        param.getOrderCheckBox3().setVisible(true);
        if(param.getDateField().getDate() != null  && param.getEndDateField().getDate()!=null) {
            // default for end date
//            if (param.getEndDateField().getDate() == null) {
//                param.getEndDateField().setDate(
//                    param.getDateField().getDate());
//            }

            // enable when date selected and at least one box checked
            if (param.getOrderCheckBox().isSelected()
            || param.getOrderCheckBox2().isSelected()
            || param.getOrderCheckBox3().isSelected()) {
                if (param.getEndDateField().getDate().compareTo(param.getDateField().getDate()) >= 0) {
                    param.getGenerateButton().setEnabled(true);
                }
            }
        }
    }

    public void generate() {
        if (param.getOrderCheckBox().isSelected()) {
            TaskGenerateReport task = new TaskGenerateReport("report_payroll_summary");
            task.setTitle("Payroll Summary by User/Volume");
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
        } else if (param.getOrderCheckBox2().isSelected()) {
            TaskGenerateReport task = new TaskGenerateReport("report_payroll_user_project_status");
            task.setTitle("Payroll Summary by User/Project");
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
        } else if (param.getOrderCheckBox3().isSelected()) {
            TaskGenerateReport task = new TaskGenerateReport("report_payroll_project_volume_status");
            task.setTitle("Payroll Summary by Project/Volume");
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
}
