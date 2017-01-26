/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_daily_total_report.java,v 1.3.8.2 2007/03/27 14:28:27 bill Exp $ */
package server;

import common.Log;
import common.msg.MessageWriter;
import common.msg.XmlUtil;

import java.io.IOException;
import java.sql.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Execute a stored SQL query and add the result to the XML message.
 */
final public class Handler_daily_total_report extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_daily_total_report() {}

    /** 
     * This handler is read-only.
     * @return true
     */
    public boolean isReadOnly() {
        return true;
    }

    public void run (ServerTask task, Element action)
    throws ServerFailException, SQLException, IOException{
        //Connection con = task.getConnection();
        Statement st = task.getStatement();

        String name = action.getAttribute(A_NAME);
        boolean byProject = "YES".equals(action.getAttribute(A_BY_PROJECT));
        boolean byTeam = "YES".equals(action.getAttribute(A_BY_TEAM));
        
        NodeList nodes = action.getElementsByTagName(T_PARAMETER);
        assert nodes.getLength() == 2;
        String startTime = XmlUtil.getTextFromNode(nodes.item(0));
        String endTime = XmlUtil.getTextFromNode(nodes.item(1));
        
        task.createTemporaryTable(
            "create temporary table TEMP"
            +"(project_id int not null"
            +",users_id int not null"
            +",coded_count int not null"
            +",page_coded_count int not null"
            +",qc_count int not null"
            +",page_qc_count int not null"
            +",in_qc_count int not null"
            +",page_in_qc_count int not null)"
            +"type=heap");

        st.executeUpdate(
            "insert into TEMP (project_id, users_id, coded_count, page_coded_count)"
            +" select project_id, users_id, sum(child_count), sum(page_count)"
            +" from batchcredit"
            +" where credit_time between "+startTime+" and "+endTime
            +"   and status = 'Coding'"
            +"   and not rework"
            +" group by project_id, users_id");

        st.executeUpdate(
            "insert into TEMP (project_id, users_id, qc_count, page_qc_count)"
            +" select project_id, users_id, sum(child_count), sum(page_count)"
            +" from batchcredit"
            +" where credit_time between "+startTime+" and "+endTime
            +"   and status = 'CodingQC'"
            +"   and not rework"
            +" group by project_id, users_id");

        // Get count of docs in QC at end of period
        // Batch is "in QC" if there is a batchcredit
        // for Coding before the end of the period
        // and there is no batchcredit for CodingQC
        // Note:  The inner join with batch avoids having
        // a batch be deleted in CodingQC and then live
        // forever in this report.
        st.executeUpdate(
            "insert into TEMP (project_id, users_id, in_qc_count, page_in_qc_count)"
            +" select X.project_id, X.users_id, sum(X.child_count), sum(X.page_count)"
            +" from batchcredit X"
            +"   inner join batch using (batch_id)"
            +"   left join batchcredit Y"
            +"     on Y.batch_id=X.batch_id"
            +"     and Y.status = 'CodingQC'"
            +"     and not Y.rework"
            +"     and Y.credit_time <= "+endTime
            +" where X.credit_time <= "+endTime
            +"   and X.status = 'Coding'"
            +"   and not X.rework"
            +"   and Y.batch_id is null"
            +" group by X.project_id, X.users_id");

        ResultSet rs2;
        if (byProject && byTeam) {
            rs2 = st.executeQuery(
                "select P.project_name as `@Project`"
                +"    , T.team_name as `@Team`"
                +"    , sum(TEMP.coded_count) as `+Docs Coded`"
                +"    , sum(TEMP.page_coded_count) as `+Pages Coded`"
                +"    , sum(TEMP.qc_count) as `+Docs QCed`"
                +"    , sum(TEMP.page_qc_count) as `+Pages QCed`"
                +"    , sum(TEMP.in_qc_count) as `+Docs in QC`"
                +"    , sum(TEMP.page_in_qc_count) as `+Pages in QC`"
                +" from TEMP"
                +"   inner join project P using (project_id)"
                +"   inner join users U on U.users_id=TEMP.users_id"
                +"   inner join teams T using (teams_id)"
                +" group by project_name, team_name"
                +" order by project_name, team_name");
        } else if (byProject) { // but not byTeam
            rs2 = st.executeQuery(
                "select P.project_name as `@Project`"
                +"    , sum(TEMP.coded_count) as `+Docs Coded`"
                +"    , sum(TEMP.page_coded_count) as `+Pages Coded`"
                +"    , sum(TEMP.qc_count) as `+Docs QCed`"
                +"    , sum(TEMP.page_qc_count) as `+Pages QCed`"
                +"    , sum(TEMP.in_qc_count) as `+Docs in QC`"
                +"    , sum(TEMP.page_in_qc_count) as `+Pages in QC`"
                +" from TEMP"
                +"   inner join project P using (project_id)"
                +" group by project_name"
                +" order by project_name");
        } else { // byTeam
            rs2 = st.executeQuery(
                "select T.team_name as `@Team`"
                +"    , sum(TEMP.coded_count) as `+Docs Coded`"
                +"    , sum(TEMP.page_coded_count) as `+Pages Coded`"
                +"    , sum(TEMP.qc_count) as `+Docs QCed`"
                +"    , sum(TEMP.page_qc_count) as `+Pages QCed`"
                +"    , sum(TEMP.in_qc_count) as `+Docs in QC`"
                +"    , sum(TEMP.page_in_qc_count) as `+Pages in QC`"
                +" from TEMP"
                +"   inner join users U on U.users_id=TEMP.users_id"
                +"   inner join teams T using (teams_id)"
                +" group by team_name"
                +" order by team_name");
        }

        Handler_sql_query.writeXmlFromResult(task, rs2, /* requestedMetaData => */ true);
        rs2.close();
        task.finishedWritingTemporaryTable();
    }
}
