/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

import java.util.List;
import java.util.Map;

/**
 * Class to Hold Tally Process Input Data
 * @author sunil
 */
public class TallyProcessInputData {

    private int projectId;
    private int volumeId;
    private Map<String, List> projectFieldData;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getVolumeId() {
        return volumeId;
    }

    public void setVolumeId(int volumeId) {
        this.volumeId = volumeId;
    }

    public Map<String, List> getProjectFieldData() {
        return projectFieldData;
    }

    public void setProjectFieldData(Map<String, List> projectFieldData) {
        this.projectFieldData = projectFieldData;
    }
}
