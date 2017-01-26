/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskGenerateReport.java,v 1.8.2.1 2006/03/14 15:08:46 nancy Exp $ */
package client;

import common.Log;
import common.DynamicArrays;
import model.ResultSetTableModel;
import ui.TableViewer;

import java.io.IOException;
import java.sql.ResultSet;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * ClientTask to execute a query and show the ResultSet to the user
 * via <code>client.TaskGenerateReport.TableReport</code>.  
 */
public class TaskGenerateReport extends ClientTask {

    final private static String[] EMPTY_STRING_ARRAY = new String[0];
    private String sqlName = null;
    private String title = null;
    private String[] paramNames = EMPTY_STRING_ARRAY;
    private String[] paramDisplay = EMPTY_STRING_ARRAY;
    protected String[] parameters = EMPTY_STRING_ARRAY;
    final protected ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this ClientTask and remember the parameter.
     * @param sqlName the sql_text.name to be used as the report query
     */
    public TaskGenerateReport(String sqlName) {
        this.sqlName = sqlName;
    }

    protected TaskGenerateReport() {
    }

    /**
     * Set the title to be used on the report to the given String.
     * @param title the String report title
     */
    public synchronized void setTitle(String title) {
        this.title = title;
    }

    /**
     * Remember parameters to be used in the sql query.
     * @param name the name of the parameter to be displayed on the report
     * @param value the value of the parameter to use in the sql and to be
     * displayed on the report
     */
    public void addParameter(String name, String value) {
        addParameter(name, value, value);
    }

    /**
     * Remember parameters to be used in the sql query.
     * @param name the name of the parameter to be displayed on the report
     * @param display the value of the parameter to displayed on the report
     * @param value the String value of the parameter to use in the sql
     */
    public synchronized void addParameter(String name, String display, String value) {
        //Log.print("(TaskGenerateReport.addParameter) " + name + "/" + display + "/" + value);
        paramNames = (String[]) DynamicArrays.append(paramNames, name);
        paramDisplay = (String[]) DynamicArrays.append(paramDisplay, display);
        parameters = (String[]) DynamicArrays.append(parameters, value);
    }

    /**
     * Remember parameters to be used in the sql query.
     * @param name the name of the parameter to be displayed on the report
     * @param display the value of the parameter to displayed on the report
     * @param value the int value of the parameter to use in the sql
     */
    public void addParameter(String name, String display, int value) {
        addParameter(name, display, Integer.toString(value));
    }

    /**
     * Execute the query and start <code>TableReport</code> as a Runnable.
     */
    public void run() {
        try {
            //Log.print("(TaskGenerateReport.run)");
            final ResultSet queryResult = doQuery();

            if (queryResult == null) {
                Log.quit("TaskGenerateReport: " + sqlName + ": queryResult is null");
            } else if (queryResult instanceof ResultSet) {
                // Display the report in a non-modal dialog
                //Log.print("(TaskGenerateReport.run) start report thread");
                TableReport reportThread = new TableReport(queryResult, title,
                        paramNames, paramDisplay);
                SwingUtilities.invokeLater(reportThread);
            } else {
                Log.quit("TaskGenerateReport: " + sqlName + ": invalid return type: " + queryResult);
            }

        } catch (Throwable e) {
            Log.quit(e);
        }
    }

    /**
     * Run a query on the server to obtain report data.
     * @see Sql
     * @see server.Handler_sql_query
     */
    protected ResultSet doQuery() throws IOException, ClassNotFoundException {
        return Sql.executeQueryWithMetaData(scon, this, sqlName, parameters);
    }

    /**
     * A Runnable to show an instance of TableViewer to show the
     * <code>ResultSet</code> to the user in a dialog.
     * @see TaskGenerateReport
     * @see TableViewer
     */
    private class TableReport implements Runnable {

        ResultSet queryResult;
        private String title;
        private String[] paramNames;
        private String[] paramDisplay;

        /**
         * Create an instance of this Runnable and remember the parameters.
         * @param results the ResultSet to show the user
         * @param title the title of the report
         * @param paramNames the names of the parameters to print on the report
         * @param paramDisplay the display format of the parameters to print on the report
         */
        TableReport(ResultSet results, String title, String[] paramNames, String[] paramDisplay) {
            //Log.print("(TaskGenerateReport.TableReport)");
            queryResult = results;
            this.title = title;
            this.paramNames = paramNames;
            this.paramDisplay = paramDisplay;
        }

        /**
         * Show the TableViewer dialog to the user using the stored parameters.
         */
        public void run() {
            try {
                //Log.print("(TaskGenerateReport.TableReport.run)");
                TableViewer dialog;
                dialog = new TableViewer((JFrame) Global.mainWindow, false);
                dialog.setReportHeading(title, paramNames, paramDisplay);
                dialog.setModel(new ResultSetTableModel(queryResult));
                dialog.setVisible(true);
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    }
}
