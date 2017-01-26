/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package valueobjects;

import java.util.HashMap;
import java.util.Map;

/**
 * This class has the project, volume and the validations available for the project fields in the volume.
 *
 * @author Sunil
 */
public class ProjectsData {

    // ID of the project
    private int projectId;
    
    //ID of the volume
    private int volumeId;
    
    //Name of the project
    private String projectName;
    
    //Name of the Volume
    private String volumeName;
    
    //Map which maps Project Field Name with Fields Data value object
    private Map<String, FieldsData> fieldsMap;

    public ProjectsData() {
        fieldsMap = new HashMap<String, FieldsData>();
    }

    /**
     * Gets a Map between Project Field Name and Fields Data value object
     *
     * @return  fieldsMap   Map which has the mapping between Project Field Name and Fields Data value object
     */
    public Map<String, FieldsData> getFieldsMap() {
        return fieldsMap;
    }

    /**
     * Gets a Map between Project Field Name and Fields Data value object
     *
     * @param fieldsMap Map which has the mapping between Project Field Name and Fields Data value object
     */
    public void setFieldsMap(Map<String, FieldsData> fieldsMap) {
        this.fieldsMap = fieldsMap;
    }

    /**
     * Gets Project Id
     *
     * @return  projectId   The project ID
     */
    public int getProjectId() {
        return projectId;
    }

    /**
     * Sets the project ID
     * @param projectId   The project ID
     */
    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    /**
     * Gets Volume Id
     *
     * @return  volumeId   The Volume ID
     */
    public int getVolumeId() {
        return volumeId;
    }

    /**
     * Gets Volume Id
     *
     * @return  volumeId   The Volume ID
     */
    public void setVolumeId(int volumeId) {
        this.volumeId = volumeId;
    }

    /**
     * Creates a new entry in Map for a Project Field and a FieldsData object
     *
     * @param key   Project field name
     * @param value The FieldsData object
     */
    public void putFieldInMap(String key, FieldsData value) {
        fieldsMap.put(key, value);
    }

    /**
     * Gets the project name
     *
     * @return  projectName     The name of the project
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Sets the project name
     *
     * @param projectName   The name of the project
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    /**
     * Gets the Volume name
     *
     * @return  volumeName  The name of the volume
     */
    public String getVolumeName() {
        return volumeName;
    }

    /**
     * Sets the Volume name
     *
     * @param   volumeName  The name of the volume
     */
    public void setVolumeName(String volumeName) {
        this.volumeName = volumeName;
    }
}
