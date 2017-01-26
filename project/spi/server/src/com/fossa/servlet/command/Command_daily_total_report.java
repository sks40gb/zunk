/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlUtil;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * This class generates the daily total report
 * @author ashish
 */
class Command_daily_total_report implements Command
{

   public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer)
   {

      boolean byProject = "YES".equals(action.getAttribute(A_BY_PROJECT));
      boolean byTeam = "YES".equals(action.getAttribute(A_BY_TEAM));

      NodeList nodes = action.getElementsByTagName(T_PARAMETER);
      assert nodes.getLength() == 2;
      String startTime = XmlUtil.getTextFromNode(nodes.item(0));
      String endTime = XmlUtil.getTextFromNode(nodes.item(1));

      Statement st = null;
      Connection con = null;
      try {
         st = dbTask.getStatement();
         con = dbTask.getConnection();

         task.createTemporaryTable(SQLQueries.CREATE_TABLE, dbTask);
         PreparedStatement insProjIdToTempTablePrepStmt = con.prepareStatement(SQLQueries.INS_TEMP_PROJID);
         insProjIdToTempTablePrepStmt.setString(1, startTime);
         insProjIdToTempTablePrepStmt.setString(2, endTime);
         insProjIdToTempTablePrepStmt.executeUpdate();
         PreparedStatement insUserIdToTempTablePrepStmt = con.prepareStatement(SQLQueries.INS_TEMP_USERID);
         insUserIdToTempTablePrepStmt.setString(1, startTime);
         insUserIdToTempTablePrepStmt.setString(2, endTime);
         insUserIdToTempTablePrepStmt.executeUpdate();

         // Get count of docs in QC at end of period
            // Batch is "in QC" if there is a batchcredit
            // for Coding before the end of the period
            // and there is no batchcredit for CodingQC
            // Note:  The inner join with batch avoids having
            // a batch be deleted in CodingQC and then live
            // forever in this report.
         PreparedStatement insQCCountToTempTablePrepStmt = con.prepareStatement(SQLQueries.INS_TEMP_QCCOUNT);
         insQCCountToTempTablePrepStmt.setString(1, endTime);
         insQCCountToTempTablePrepStmt.setString(2, endTime);
         insQCCountToTempTablePrepStmt.executeUpdate();
         ResultSet resultSet;
         if (byProject && byTeam) {
            //Get reports by project && team
            resultSet = st.executeQuery(SQLQueries.SEL_P_PROJ);
         }
         else if (byProject) {
            //Get coded docs by only project 
            resultSet = st.executeQuery(SQLQueries.SEL_CODED_COUNT);
         }
         else {
            //Get reports by only team
            resultSet = st.executeQuery(SQLQueries.SEL_TEAM_NAME);
         }
         CommandFactory factory = CommandFactory.getInstance();
         Command_sql_query sqlCommand = (Command_sql_query) factory.getCommand(T_SQL_QUERY);
         sqlCommand.writeXmlFromResult(task, resultSet, writer, true);
         resultSet.close();
         task.finishedWritingTemporaryTable(dbTask);

         //drop the temporary table created
         st.executeQuery("drop table #TEMP");
      } catch (SQLException sql) {
         CommonLogger.printExceptions(this, "SQLException while calculating daily total report.", sql);
         return null;
      } catch (Exception exc) {
         CommonLogger.printExceptions(this, "Exception while calculating daily total report.", exc);
         return null;
      }
      return null;
   }

   public boolean isReadOnly()
   {
      return true;
   }

}
