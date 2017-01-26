/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

/**
 * Class VolumeData keeps the record of volume level that could be project id, volume id and other Field.
 * This class is used while implementing the validation.
 * Fuctions used in the BSF (Beans Scripting Framework). 
 * @author sunil
 */
public class VolumeRecord {

    /** Project Id */
    public static int projectId;
    /** Volume Id */
    public static int volumeId;

    /**
     * Reset all Field, Document and Volume level record.
     */
    public static void reset() {
        volumeId = -1;
        projectId = -1;
    }

    /**
     * Get the Project Id
     * @return
     */
    public static int getProjectId() {
        return projectId;
    }

    /**
     * Set the Project Id
     * @param projectId - Project Id
     */
    public static void setProjectId(int projectId) {
        VolumeRecord.projectId = projectId;
    }

    /**
     * Get the Volume Id
     * @return Volume Id
     */
    public static int getVolumeId() {
        return volumeId;
    }

    /**
     * Set the Volume Id
     * @param volumeId - Get the Volume Id
     */
    public static void setVolumeId(int volumeId) {
        VolumeRecord.volumeId = volumeId;
    }
}
