/*
 * ListingSummaryReport.java
 *
 * Created on February 21, 2008, 4:45 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package report;

import client.TaskGenerateReport;
import common.PopulateData;
import model.QueryComboModel;

/**
 *
 * @author anurag
 */
public class TallySummaryReport extends AbstractReport{
    private QueryComboModel fieldModel = null;
     PopulateData data;
    /** Creates a new instance of ListingSummaryReport */
    public TallySummaryReport(ReportParameters param) {        
        super(param);        
        registerModel(param.getProjectCombo());       
    }

    public void enableControls() {    
        disableControls();      
        param.getProjectCombo().setEnabled(true);
        if (param.getProjectCombo().getSelectedIndex() >= 0) {
            param.getVolumeCombo().setEnabled(true);
            param.getVolumeCombo().setSelectedIndex(-1);
            param.getFieldCombo().setEnabled(false);           
            param.getOccurrence().setEnabled(false);
            param.getBatchCombo().setEnabled(false);
            param.getGenerateButton().setEnabled(false);
        }       
         
    }

 
    public void generate() {
      QueryComboModel volumeModel =  (QueryComboModel)param.getVolumeCombo().getModel();
//       QueryComboModel batchModel = (QueryComboModel) param.getBatchCombo().getModel();
//       int batchId = batchModel.getSelectedId();       
       int vol_index = param.getVolumeCombo().getSelectedIndex(); 
       int vol_id = volumeModel.getSelectedId();       
       TaskGenerateReport task = new TaskGenerateReport("tally_report");
       task.setTitle("Tally Summary");
       task.addParameter("Volume Name",
               (String) (String) volumeModel.getElementAt(vol_index),
               vol_id);
       task.enqueue();
    }

    
    
}
