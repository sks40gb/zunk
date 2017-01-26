/* $Header: /home/common/cvsarea/ibase/dia/src/report/DailyTotalReport.java,v 1.5 2004/10/07 10:19:33 weaston Exp $ */
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
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;

/**
 * Daily totals reports.
 */
public final class DailyTotalReport extends AbstractReport {

    Calendar endCalc = null;

    /**
     * Create a new DailyTotalReport instance.
     * @param param An instance of ReportParameters,
     *   providing visibility to the controls
     *   on the report page..
     */
    public DailyTotalReport(ReportParameters param) {
        super(param);
    }
    
    public void enableControls() {
        disableControls();
        param.getDateField().setEnabled(true);
        param.getEndDateField().setEnabled(true);
        param.getOrderCheckBox().setText("By Project");
        param.getOrderCheckBox().setVisible(true);
        param.getOrderCheckBox2().setText("By Team");
        param.getOrderCheckBox2().setVisible(true);
        if(param.getDateField().getDate() != null  && param.getEndDateField().getDate()!=null) {
            // default for end date
//            if (param.getEndDateField().getDate() == null) {
//                param.getEndDateField().setDate(
//                    param.getDateField().getDate());
//            }

            // enable when date selected and at least one box checked
            //System.out.println(param.getDateField().getDate()+".."+param.getEndDateField().getDate());
            if (param.getOrderCheckBox().isSelected()
            || param.getOrderCheckBox2().isSelected()) {
                if (param.getEndDateField().getDate().compareTo(param.getDateField().getDate()) >= 0) {
                    param.getGenerateButton().setEnabled(true);
                }
            }
        }
    }

    public void generate() {

        TaskGenerateReport task = new TaskDailyReport(
                param.getOrderCheckBox().isSelected(),
                param.getOrderCheckBox2().isSelected());
        task.setTitle("Daily Totals");
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
    
    /**
     * Replaces TaskGenerateReport with special messages.  It was not
     * possible to generate the Daily Total Report from a single MySQL query.
     **/
    private static class TaskDailyReport extends TaskGenerateReport {

        boolean byProject;
        boolean byTeam;

        TaskDailyReport(boolean byProject, boolean byTeam) {
            this.byProject = byProject;
            this.byTeam = byTeam;
        }
        
        /**
         * Replaces call to single SQL statement in TaskGenerateReport
         * with call using special message type.
         */
        protected ResultSet doQuery() throws IOException {

            MessageWriter writer;
            writer = scon.startMessage(T_DAILY_TOTAL_REPORT);
            if (byProject) {
                writer.writeAttribute(A_BY_PROJECT, "YES");
            }
            if (byTeam) {
                writer.writeAttribute(A_BY_TEAM, "YES");
            }
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
                Log.quit("DailyTotalReport: unexpected message type: "+reply.getNodeName());
                return null; // never get here
            }

        }

    }
}
