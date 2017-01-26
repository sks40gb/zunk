/* $Header: /home/common/cvsarea/ibase/dia/src/report/MenuReport.java,v 1.2 2005/06/12 12:45:07 weaston Exp $ */
package report;

import client.TaskGenerateReport;

/**
 * Generic report to be displayed from menu item.
 */
final public class MenuReport extends AbstractReport {

    private String sqlName;
    private String title;
    private String name1 = "";
    private String display1 = "";
    private String value1 = "";
    private String name2 = "";
    private String display2 = "";
    private String value2 = "";

    /**
     * Create a new MenuReport.
     * @param sqlName The name of the sql in table sql-text.
     * @param title The title for the report dialog.
     */
    public MenuReport(String sqlName, String title) {
        this(sqlName, title, "", "", "", "", "", "");
    }
    public MenuReport(String sqlName, String title
                      , String name1, String display1, String value1
                      , String name2, String display2, String value2) {
        super(null);
        this.sqlName = sqlName;
        this.title = title;
        this.name1 = name1;
        this.display1 = display1;
        this.value1 = value1;
        this.name2 = name2;
        this.display2 = display2;
        this.value2 = value2;
        
    }
    
    /**
     * Not used for reports selected from menu.
     * @deprecated
     */
    public void enableControls() {
        // (not called from report screen)
        //disableControls();
        //param.getGenerateButton().setEnabled(true);
        throw new UnsupportedOperationException();
    }

    public void addParameter(String name, String value) {
    }

    public void generate() {

        TaskGenerateReport task;
        task = new TaskGenerateReport(sqlName);
        task.setTitle(title);
        if (! name1.equals("")) {
            task.addParameter(name1, display1, value1);
        }
        if (! name2.equals("")) {
            task.addParameter(name2, display2, value2);
        }
        task.enqueue();
    }
}
