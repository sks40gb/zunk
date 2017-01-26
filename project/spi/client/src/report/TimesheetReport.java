/* $Header: /home/common/cvsarea/ibase/dia/src/report/Attic/TimesheetReport.java,v 1.1.2.3 2005/12/07 16:23:38 nancy Exp $ */
package report;

import client.Global;
import client.TaskGenerateReport;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Timesheet report.
 * <p>
 * Timesheet child, page and field counts are accumulated in the
 * event table during open, insert and close actions for Coding,
 * CodingQC and QA.  Coding and CodingQC are tracked by batch_id
 * (TBD: and QA is tracked by volume_id).  A row is inserted on open of a
 * batch or QA volume that contains the open_timestamp.  All inserts
 * update the add_timestamp, so it is the latest action until a close
 * happens.  (TBD: Updates of existing value and namevalue
 * data is not logged.)  close_timestamp is updated during SplitPaneViewer.
 * closeMenuItem and closeBatchMenuItem for all statuses.  If an open
 * happens and the batch/volume has an event row with a 0 close_timestamp,
 * it will be updated with Integer.MAX_VALUE.
 * 
 * During reporting, if a timestamp is 0 or Integer.MAX_VALUE, add_timestamp
 * is used as the end time.
 * 
 * The big, ugly condition in the middle of sql_text.txt($ report_timesheet)
 * is there because TIMEDIFF was not implemented until mysql v 4.1.1 and
 * we needed to calculate the Elapsed Time.
 * 
 */
public final class TimesheetReport extends AbstractReport {

    Calendar endCalc = null;
    // save time zone to give to server 
    String timeZoneId = null;

    /**
     * Create a new TimesheetReport instance.
     * @param param An instance of ReportParameters,
     *   providing visibility to the controls
     *   on the report page.
     */
    public TimesheetReport(ReportParameters param) {
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
            if (param.getEndDateField().getDate().compareTo(param.getDateField().getDate()) >= 0) {
                param.getGenerateButton().setEnabled(true);
            }
        }
    }

    public void generate() {
        //Log.print("time zone " + TimeZone.getTimeZone(
        //        Global.theServerConnection.getTimeZoneID()));
        TaskGenerateReport task = new TaskGenerateReport("report_timesheet");
        task.setTitle("Timesheet");
        task.addParameter("Start Date",
                          param.getDateField().getTextField().getText(),
                          param.getDateField().getTextField().getText());
        if (endCalc ==  null) {
            endCalc = Calendar.getInstance(TimeZone.getTimeZone(
                Global.theServerConnection.getTimeZoneID()));
        }
        endCalc.setTime(param.getEndDateField().getDate());
        endCalc.add(Calendar.DATE, +1);
        task.addParameter("End Date",
                          param.getEndDateField().getTextField().getText(),
                          param.getEndDateField().getTextField().getText());
        task.addParameter("Start Date",
                          param.getDateField().getTextField().getText(),
                          param.getDateField().getTextField().getText());
        task.addParameter("End Date",
                          param.getEndDateField().getTextField().getText(),
                          param.getEndDateField().getTextField().getText());
        task.enqueue();
    }
}
