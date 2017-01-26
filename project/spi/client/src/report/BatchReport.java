/* $Header: /home/common/cvsarea/ibase/dia/src/report/BatchReport.java,v 1.7 2004/08/25 13:34:28 weaston Exp $ */
package report;

import client.TaskGenerateReport;
import model.ManagedComboModel;

/**
 * Batch report for project.
 */
final public class BatchReport extends AbstractReport {

    public BatchReport(ReportParameters param) {
        super(param);
        registerModel(param.getProjectCombo());
    }
    
    public void enableControls() {
        disableControls();
        param.getProjectCombo().setEnabled(true);
        if (param.getProjectCombo().getSelectedIndex() >= 0) {
            param.getGenerateButton().setEnabled(true);
            param.getBatchCombo().setEnabled(false);
            param.getOccurrence().setEnabled(false);
            param.getVolumeCombo().setEnabled(false);
        }
        param.getOrderCheckBox().setText("Order by Volume");
        param.getOrderCheckBox().setVisible(true);
        param.getOrderCheckBox2().setText("Order by status");
        param.getOrderCheckBox2().setVisible(true);
    }

    public void generate() {

        //select if(substring(B.status,1,1)='U', concat('U-',B.batch_number), B.batch_number) as `=Batch`       
        //    , B.status as `Status` 
        //    , case 
        //        when S.batch_id is not null then 'In Use' 
        //        when A.batch_id is not null then 'Assigned' 
        //        else '' end as `Asg/Use`
        //    , if(X.rework,'YES','') as Rework
        //    , count(distinct C.child_id) as `+Docs`
        //    , count(*) as `+Pages`
        //    , coalesce(U.user_name,'')  as `Coder Id`  
        //    , coalesce(date_format(from_unixtime(mod_time/1000),'%Y-%m-%d %H:%i'),'') as `Mod Time` 
        //from batch B           
        //   inner join child C using (batch_id)
        //   inner join page P using (child_id)        
        //   inner join volume V using(volume_id)
        //   inner join batchuser X on X.batch_id=B.batch_id
        //   left join session S on S.batch_id=B.batch_id
        //   left join assignment A on A.batch_id=B.batch_id
        //   left join users U on X.coder_id=U.users_id
        //where V.project_id=?
        //group by B.batch_id
        //order by substring(B.status,1,1)<>'U', B.batch_number

        TaskGenerateReport task;
        if (param.getOrderCheckBox().isSelected() && param.getOrderCheckBox2().isSelected()) {
            task = new TaskGenerateReport("report_batch_project_by_volume_status");
        } else if (param.getOrderCheckBox2().isSelected()) {
            task = new TaskGenerateReport("report_batch_project_by_status");
        } else if (param.getOrderCheckBox().isSelected()) {
            task = new TaskGenerateReport("report_batch_project_by_volume");
        } else {
            task = new TaskGenerateReport("report_batch_project");
        }

        ManagedComboModel projectModel = (ManagedComboModel) param.getProjectCombo().getModel();
        int index = param.getProjectCombo().getSelectedIndex();
        task.setTitle("Batches for Project");
        task.addParameter("Project",
                          (String) projectModel.getElementAt(index),
                          projectModel.getIdAt(index));
        task.enqueue();
    }
}
