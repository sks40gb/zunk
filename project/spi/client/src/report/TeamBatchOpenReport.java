/* $Header: /home/common/cvsarea/ibase/dia/src/report/TeamBatchOpenReport.java,v 1.2 2004/12/14 20:36:55 weaston Exp $ */
package report;

import client.TaskGenerateReport;
import client.Global;
import model.ManagedComboModel;

/**
 * Open Batch report for team.
 */
final public class TeamBatchOpenReport extends AbstractReport {

    public TeamBatchOpenReport(ReportParameters param) {
        super(param);
        registerModel(param.getTeamCombo());
    }
    
    public void enableControls() {
        disableControls();
        boolean isAdmin = Global.theServerConnection.getPermissionAdmin();
        param.getTeamCombo().setEnabled(isAdmin);
        if (! isAdmin && param.getTeamCombo().getModel().getSize() > 0) {
            param.getTeamCombo().setSelectedIndex(0);
        }
        if (param.getTeamCombo().getSelectedIndex() >= 0) {
            param.getGenerateButton().setEnabled(true);
        }
        param.getOrderCheckBox().setText("Order by Coder");
        param.getOrderCheckBox().setVisible(true);
    }

    /**
     * Disable all controls and clear values.
     * Called when report radio button is selected.
     * Overridden -- do not clear Team combo for TL
     */
    public void initializeControls() {
        disableControls();
        param.getProjectCombo().setSelectedIndex(-1);
        if (Global.theServerConnection.getPermissionAdmin()) {
            param.getTeamCombo().setSelectedIndex(-1);   
        }
        //param.getDateField().setDate(null);   
        //param.getEndDateField().setDate(null);   
        //param.getDaysField().setText(""); 
        param.getOrderCheckBox().setSelected(false);
        param.getOrderCheckBox2().setSelected(false);
    }

    public void generate() {

        TaskGenerateReport task;
        if (param.getOrderCheckBox().isSelected()) {
            task = new TaskGenerateReport("report_open_batch_team_by_coder");
        } else {
            task = new TaskGenerateReport("report_open_batch_team");
        }
        task.setTitle("Open Batches for Team");
        ManagedComboModel teamsModel = (ManagedComboModel) param.getTeamCombo().getModel();
        int index = param.getTeamCombo().getSelectedIndex();
        task.addParameter("Team",
                          (String) teamsModel.getElementAt(index),
                          teamsModel.getIdAt(index));
        task.enqueue();
    }
}
