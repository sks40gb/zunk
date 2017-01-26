/*
 * To Store the result data for tally process
 * 
 */

package common;

/**
 * Class to Hold TallyProcessData for a Project
 * @author sunil
 */
public class TallyProcessData {
    
    private int projectFieldId;
    private int projectId;
    private int volumeId;
    private String projectFieldName;
    private String tallyType;
    private int groupCount;

    public int getProjectFieldId() {
        return projectFieldId;
    }

    public void setProjectFieldId(int projectFieldId) {
        this.projectFieldId = projectFieldId;
    }

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

    public String getProjectFieldName() {
        return projectFieldName;
    }

    public void setProjectFieldName(String projectFieldName) {
        this.projectFieldName = projectFieldName;
    }

    public String getTallyType() {
        return tallyType;
    }

    public void setTallyType(String tallyType) {
        this.tallyType = tallyType;
    }

    public int getGroupCount() {
        return groupCount;
    }

    public void setGroupCount(int groupCount) {
        this.groupCount = groupCount;
    }
    
    
}
