/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.script;

/**
 * Class VolumeData keeps the record of volume level that could be project id, volume id and other instances.
 * It extends the Field class <code>Data<code> which contains the basic required information for this class like
 * function parameters and error message. This class is used while implementing the validation or formatting
 * Fuctions used in the BSF (Beans Scripting Framework).
 * @see Data
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
    public static synchronized void reset() {
        volumeId = -1;
        projectId = -1;
    }

    /**
     * Get the Project Id
     * @return
     */
    public static synchronized int getProjectId() {
        return projectId;
    }

    /**
     * Set the Project Id
     * @param projectId - Project Id
     */
    public static synchronized void setProjectId(int projectId) {
        VolumeRecord.projectId = projectId;
    }

    /**
     * Get the Volume Id
     * @return Volume Id
     */
    public static synchronized int getVolumeId() {
        return volumeId;
    }

    /**
     * Set the Volume Id
     * @param volumeId - Get the Volume Id
     */
    public static synchronized void setVolumeId(int volumeId) {
        VolumeRecord.volumeId = volumeId;
    }
}
