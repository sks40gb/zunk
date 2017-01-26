/* $Header: /home/common/cvsarea/ibase/dia/src/client/TaskImportData.java,v 1.6.6.2 2006/08/23 18:32:58 nancy Exp $ */
package client;

import common.msg.MessageWriter;
import dbload.XrefConstants;
import java.io.IOException;
import org.w3c.dom.Element;

/**
 * ClientTask to import a project from a text file.
 * @see server.Handler_import_data
 */
public class TaskImportData extends ClientTask implements XrefConstants {

    String project_name = "";
    String volume_name = "";
    int batch_span = 0;
    int l1_batch_span = 0;
    int file_type = 0;
    String xref_filename = "";
    String brs_filename = "";
    String img_filename = "";
    String txt_filename = "";
    String isL1Operation = "false";
    String image_pathname = "";
    String overwrite = "";
    String split = "";
    String codingmanual = "";
    String internalVolume = "";
    String isUnitizationOptional;
    String date = "";
    final private ServerConnection scon = Global.theServerConnection;

    /**
     * Create an instance of this ClientTask and remember the parameters.
     * @param project_name the project.project_name of the project to which
     * the data will be written
     * @param volume_name the volume.volume_name containing project_name
     * @param batch_span the approximate number of pages to include in each batch
     * @param file_type the format of the cross reference file being imported; can be
     * one of "INVALID", "LFP", "DOCULEX", "OPTICON", "SUMMATION", "BRS"
     * @param xref_filename the fully-qualified name of the cross reference
     * file to read
     * @param image_pathname the path to store in volume.image_path to use
     * in retrieval of images
     * @param overwrite Yes to delete an existing project by the given project_name
     * before writing this project; No if no project exists by this name
     * @param split Yes if this project can contain split documents; otherwise No
     */
    public TaskImportData(String project_name, String volume_name, String codingmanual,
            int batch_span, int file_type, String xref_filename,
            String image_pathname, String overwrite, String split, String imageServerPath, String internalVolume, boolean isUnitizeOptional, String date) {

        this.project_name = project_name;
        this.volume_name = volume_name;
        this.batch_span = batch_span;
        this.file_type = file_type;
        this.xref_filename = xref_filename;
        this.image_pathname = imageServerPath + image_pathname;
        this.overwrite = overwrite;
        this.split = split;
        this.codingmanual = codingmanual;
        this.isL1Operation = "false";
        this.internalVolume = internalVolume;

        if (isUnitizeOptional) {
            this.isUnitizationOptional = "true";
        } else {
            this.isUnitizationOptional = "false";
        }
        this.date = date;
    }

    /**
     * Create an instance of this ClientTask and remember the parameters.
     * @param project_name the project.project_name of the project to which
     * the data will be written
     * @param volume_name the volume.volume_name containing project_name
     * @param batch_span the approximate number of pages to include in each batch
     * @param file_type the format of the cross reference file being imported; can be
     * one of "INVALID", "LFP", "DOCULEX", "OPTICON", "SUMMATION", "BRS"
     * @param brs_filename the fully-qualified name of the BRS file to read     
     * @param brs_filename the fully-qualified name of the IMG file to read     
     * @param brs_filename the fully-qualified name of the SOURCE TXT file to read     
     * @param image_pathname the path to store in volume.image_path to use
     * in retrieval of images
     * @param overwrite Yes to delete an existing project by the given project_name
     * before writing this project; No if no project exists by this name
     * @param split Yes if this project can contain split documents; otherwise No
     */
    public TaskImportData(String project_name, String volume_name, String codingmanual,
            int batch_span, int file_type, String brs_filename,
            String img_filename, String txt_filename, String image_pathname,
            String overwrite, String split) {

        this.project_name = project_name;
        this.volume_name = volume_name;
        this.batch_span = batch_span;
        this.file_type = file_type;
        this.brs_filename = brs_filename;
        this.img_filename = img_filename;
        this.txt_filename = txt_filename;
        this.image_pathname = image_pathname;
        this.overwrite = overwrite;
        this.split = split;
        this.codingmanual = codingmanual;
        this.isL1Operation = "true";

    }

    public TaskImportData(String project_name, String volume_name, String codingmanual,
            int l1_batch_span, int batch_span, int file_type, String brs_filename,
            String img_filename, String txt_filename, String image_pathname,
            String overwrite, String split, String imageServerPath, String internalVolume, boolean isUnitizationOptional, String date) {

        this.project_name = project_name;
        this.volume_name = volume_name;
        this.l1_batch_span = l1_batch_span;
        this.batch_span = batch_span;
        this.file_type = file_type;
        this.brs_filename = brs_filename;
        this.img_filename = img_filename;
        this.txt_filename = txt_filename;
        this.image_pathname = imageServerPath + image_pathname;
        this.overwrite = overwrite;
        this.split = split;
        this.codingmanual = codingmanual;
        this.isL1Operation = "true";
        this.internalVolume = internalVolume;

        if (isUnitizationOptional) {
            this.isUnitizationOptional = "true";
        } else {
            this.isUnitizationOptional = "false";
        }
        this.date = date;

    }

    /**
     * Write the message with attributes and set the result.
     */
    public void run() throws IOException {

        MessageWriter writer = scon.startMessage(T_IMPORT_DATA);
        writer.writeAttribute(A_PROJECT_NAME, project_name);
        writer.writeAttribute(A_VOLUME_NAME, volume_name);
        writer.writeAttribute(A_PATH, codingmanual);
        writer.writeAttribute(A_L1_BATCH_SPAN, l1_batch_span);
        writer.writeAttribute(A_BATCH_SPAN, batch_span);
        writer.writeAttribute(A_FORMAT, TYPE_NAMES[file_type]);
        writer.writeAttribute(A_FILENAME, xref_filename);
        writer.writeAttribute(A_PATHNAME, image_pathname);
        writer.writeAttribute(A_OVERWRITE, overwrite);
        writer.writeAttribute(A_SPLIT_DOCUMENTS, split);
        writer.writeAttribute(A_BRS_FILENAME, brs_filename);
        writer.writeAttribute(A_IMG_FILENAME, img_filename);
        writer.writeAttribute(A_TXT_FILENAME, txt_filename);
        writer.writeAttribute(A_IS_L1_OPERATION, isL1Operation);
        writer.writeAttribute(A_IS_UNITIZE_OPTIONAL, isUnitizationOptional);
        writer.writeAttribute(A_INTERNAL_VOLUME, internalVolume);
        if (null != date) {
            writer.writeAttribute(A_VOLUME_COMPLETION_DATE, date);
        }

        writer.endElement();
        writer.close();

        Element reply = scon.receiveMessage();
//
//        String ok = reply.getNodeName();
//        if (ok.equals(T_OK)) {
//            setResult((Object) ok);
//        } else {
//            setResult((String) reply.getAttribute(A_DATA));
//        }
    }
}
