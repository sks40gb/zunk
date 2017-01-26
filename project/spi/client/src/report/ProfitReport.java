/* $Header: /home/common/cvsarea/ibase/dia/src/report/Attic/ProfitReport.java,v 1.1.2.6 2005/10/04 17:46:39 nancy Exp $ */
package report;

import client.Global;
import client.Sql;
import client.TaskGenerateReport;
import common.Log;
import common.msg.MessageWriter;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.TimeZone;
import org.w3c.dom.Element;

/**
 * Payroll summary report.
 */
public final class ProfitReport extends AbstractReport {

    Calendar endCalc = null;

    /**
     * Create a new ProfitReport instance.
     * @param param An instance of ReportParameters,
     *   providing visibility to the controls
     *   on the report page.
     */
    public ProfitReport(ReportParameters param) {
        super(param);
    }
    
    public void enableControls() {
        disableControls();
        param.getDateField().setEnabled(true);
        param.getEndDateField().setEnabled(true);
        param.getOrderCheckBox().setText("Detail");
        param.getOrderCheckBox().setVisible(true);
        param.getOrderCheckBox2().setText("Summary");
        //param.getOrderCheckBox().setEnabled(false);
        param.getOrderCheckBox2().setVisible(true);
        param.getOrderCheckBox3().setVisible(false);
        if(param.getDateField().getDate() != null && param.getEndDateField().getDate()!=null) {
            // default for end date

            // enable when date selected and at least one box checked
            if (param.getOrderCheckBox().isSelected()
            || param.getOrderCheckBox2().isSelected()) {
                if (param.getEndDateField().getDate().compareTo(param.getDateField().getDate()) >= 0) {
                    param.getGenerateButton().setEnabled(true);
                }
            }
        }
    }

    public void generate() {
        if (param.getOrderCheckBox().isSelected()) {
            //TaskGenerateReport task = new ProfitDetailReport();
            TaskGenerateReport task = new TaskGenerateReport("report_profit_detail");
            task.setTitle("Profit Detail");
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
            TaskGenerateReport task = new TaskGenerateReport("report_profit_summary");
            task.setTitle("Profit Summary");

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
    
    /**
     * Replaces TaskGenerateReport with special messages.  It was not
     * possible to generate the Profit Detail Report from a single MySQL query.
     **/
    private static class ProfitDetailReport extends TaskGenerateReport {

        //boolean byProject;
        //boolean byTeam;

        //private ProfitSummaryReport(boolean byProject, boolean byTeam) {
        private ProfitDetailReport() {
            //this.byProject = byProject;
            //this.byTeam = byTeam;
        }
        
        /**
         * Replaces call to single SQL statement in TaskGenerateReport
         * with call using special message type.
         */
        protected ResultSet doQuery() throws IOException {

            MessageWriter writer;
            writer = scon.startMessage(T_PROFIT_DETAIL_REPORT);
            //if (byProject) {
            //    writer.writeAttribute(A_BY_PROJECT, "YES");
            //}
            //if (byTeam) {
            //    writer.writeAttribute(A_BY_TEAM, "YES");
            //}
            if (parameters != null) {
                for (int i=0; i < parameters.length; i++) {
                    writer.startElement(T_PARAMETER);
                    writer.writeContent(parameters[i]);
                    writer.endElement();
                }
            }
            writer.endElement();
            writer.close();

            Element reply = scon.receiveMessage();
            //Log.print("received "+reply.getNodeName());

            if (T_RESULT_SET.equals(reply.getNodeName())) {
                return Sql.resultFromXML(reply);
            } else {
                Log.quit("ProfitDetailReport: unexpected message type: "+reply.getNodeName());
                return null; // never get here
            }

        }

    }
}
