/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.dao.FossaDataSource;
import com.fossa.servlet.dbload.DiaLoad;
import com.fossa.servlet.server.ServerProperties;
import com.fossa.servlet.session.UserTask;
import com.fossa.servlet.util.LoadValidateXeroxBRS;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.w3c.dom.Element;

/**
 * This class does the project import.
 * @author bmurali
 */
public class Command_import_data implements Command {

    public static Connection connection;
    private String database = "";
    private String project_name = "";
    private String volume_name = "";
    private String internal_volume_name = "";
    private int batch_span = 0;
    private int l1_batch_span = 0;
    private String format = "";
    private String lfp_filename = "";
    private String image_pathname = "";
    private boolean overwrite = false;
    private boolean split_documents = false;
    private String codingmanual = "";
    private String status;
    private String brs_filename = "";
    private String img_filename = "";
    private String txt_filename = "";
    private String isL1Opertion = "false";
    private String isUnitizeOptional = "";
    private String volume_completion_date = "";
    private Element action;
    private UserTask user;
    private DBTask dbTask;
    private MessageWriter writer;
    public static int progressCount;
    public static String errorMessage;
    public static String usercontextPath;
    public static int userId;
    
    public String execute(Element _action, UserTask _user, DBTask _dbTask, MessageWriter _writer) {
        try {
            progressCount = 0;
            errorMessage = null;
            action = _action;
            user = _user;
            dbTask = _dbTask;
            connection = new FossaDataSource().getConnection();
            writer = _writer;
            String userSessionId = user.getFossaSessionId();
            new Thread() {
                public void run() {
                    doImport(action, user, dbTask, writer);
                }
            }.start();

            writer.startElement(T_OK);
            writer.writeAttribute(A_FOSSAID, userSessionId);
            writer.endElement();

        } catch (Exception ex) {
            logger.error("Exception in inport : while writing in xml." + ex);
            StringWriter sw = new StringWriter();
            ex.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
        }
        return null;
    }
    
    /**
     * Method to import project data
     * 
     * @param action
     * @param user
     * @param dbTask
     * @param writer
     * @return
     */
    public String doImport(Element action, UserTask user, DBTask dbTask, MessageWriter writer) {
        String projectPath = "";
        String projectVolumePath = "";
        try {
            String contextPath = user.getContextPath();
            usercontextPath = contextPath;
            userId = user.getUsersId();
            Log.print("Command_import_data");
            project_name = action.getAttribute(A_PROJECT_NAME);
            volume_name = action.getAttribute(A_VOLUME_NAME);
            internal_volume_name = action.getAttribute(A_INTERNAL_VOLUME);
            codingmanual = action.getAttribute(A_PATH);
            batch_span = Integer.parseInt(action.getAttribute(A_BATCH_SPAN));
            l1_batch_span = Integer.parseInt(action.getAttribute(A_L1_BATCH_SPAN));
            lfp_filename = action.getAttribute(A_FILENAME);
            image_pathname = action.getAttribute(A_PATHNAME);
            format = action.getAttribute(A_FORMAT);
            overwrite = action.getAttribute(A_OVERWRITE).equals("Yes") ? true : false;
            split_documents = action.getAttribute(A_SPLIT_DOCUMENTS).equals("Yes") ? true : false;

            brs_filename = action.getAttribute(A_BRS_FILENAME);
            img_filename = action.getAttribute(A_IMG_FILENAME);
            txt_filename = action.getAttribute(A_TXT_FILENAME);
            isL1Opertion = action.getAttribute(A_IS_L1_OPERATION);
            isUnitizeOptional = action.getAttribute(A_IS_UNITIZE_OPTIONAL);
            volume_completion_date = action.getAttribute(A_VOLUME_COMPLETION_DATE);            
            projectVolumePath = projectPath + volume_name;
            // if this is l1 operation
            if (image_pathname.endsWith("/") || image_pathname.endsWith("\\")) {
            } else {
                image_pathname = image_pathname + File.separator;
            }            
            if (isL1Opertion.equalsIgnoreCase("true")) {
                LoadValidateXeroxBRS test_file_existance = new LoadValidateXeroxBRS();
                test_file_existance.readBrsFile(brs_filename);
                test_file_existance.readImgFile(img_filename);
                test_file_existance.readTxtFile(txt_filename);
            } //for lfp operation 
            else if (!validate(lfp_filename, user, writer)) {
                return null;
            }

            database = ServerProperties.getProperty("database");
            ArrayList importList = new ArrayList();
            if (overwrite) {
                importList.add("--replace");
            } else {
                importList.add("--append");
            }
            if (split_documents) {
                importList.add("--split");
            }

            // format
            importList.add("-t");
            importList.add(format);

            // project_name
            importList.add("-p");
            importList.add(project_name);

            // volume_name
            importList.add("-v");

            importList.add(volume_name);

            // batch span
            if (batch_span > 0) {
                importList.add("--batchspan");
                importList.add(Integer.toString(batch_span));
            }
            if (l1_batch_span > 0) {
                importList.add("--l1_batchspan");
                importList.add(Integer.toString(l1_batch_span));
            }
            // the name of the database
            if (!database.equals("codingdb")) {
                importList.add("--codingdb");
                importList.add(database);
                Log.print("Writing to database: " + database);
            }
            
            importList.add("-x");
            importList.add(lfp_filename);
            importList.add("-w");
            importList.add(brs_filename);
            importList.add("-y");
            importList.add(img_filename);
            importList.add("-z");
            importList.add(txt_filename);
            importList.add("-l");
            importList.add(isL1Opertion);
            importList.add("-i");
            importList.add(image_pathname);
            importList.add("-u");
            importList.add(isUnitizeOptional);
            importList.add("-n");
            importList.add(internal_volume_name);
            importList.add("-c");
            importList.add(volume_completion_date);
            // convert the args to a string array
            String[] args = (String[]) importList.toArray(new String[importList.size()]);
            DiaLoad diaLoad = new DiaLoad(args, codingmanual, dbTask, user);
            // export
            diaLoad.run();

            String status = diaLoad.getStats();
            if (!status.equals(T_OK)) {
                Command_import_data.errorMessage = status;
            }
        } catch (Throwable t) {
            logger.error("Exception during import." + t);
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(t);
        }
        return null;
    }

    public boolean isReadOnly() {
        return true;
    }
    
    /**
     * Method to Validate the Input lfp file
     * 
     * @param lfp_filename  //lfp filename
     * @param user
     * @param writer
     * @return
     * @throws java.io.FileNotFoundException
     * @throws java.io.IOException
     */
    private boolean validate(String lfp_filename, UserTask user, MessageWriter writer) throws FileNotFoundException, IOException {

        String line;
        String bates;
        String lastBate;
        String userSessionId = user.getFossaSessionId();

        int batesIndex;
        int docIndex;
        int listSize;
        boolean flag = false; //Just to indicate that this is the first BATES

        List docList = new LinkedList();
        File lfp_file = new File(lfp_filename);

        if (lfp_file.isFile()) {
            FileReader lfpFile = new FileReader(lfp_file);
            BufferedReader lfpbuffer = new BufferedReader(lfpFile);

            if (lfp_filename.contains(".LFP")) {
                while ((line = lfpbuffer.readLine()) != null) {
                    batesIndex = line.indexOf(",");
                    docIndex = line.indexOf(",", batesIndex + 1);
                    bates = line.substring(batesIndex + 1, docIndex);
                    if (flag) {
                        listSize = docList.size();
                        lastBate = (String) docList.get(listSize - 1);
                        if (!(isEqualLength(bates, lastBate, writer, userSessionId)) ||
                                !(hasSamePrefix(bates, lastBate, writer, userSessionId)) ||
                                !(isBatesSuffixInSequence(bates, lastBate, writer, userSessionId))) {
                            return false;
                        }
                    } else {
                        flag = true;
                    }                    
                    docList.add(bates);
                }
            }
        } else {
            status = "Error in input file ";
            Command_import_data.errorMessage = status;
            return false;
        }
        return true;
    }
    
    /**
     * Method to Check the Given BateNumber are in Sequence
     * 
     * @param bates  //Start BatesNumber
     * @param lastBate //End BatesNumber
     * @param writer
     * @param userSessionId //user session id
     * @return
     * @throws java.io.IOException
     */
    private boolean isBatesSuffixInSequence(String bates, String lastBate, MessageWriter writer, String userSessionId) throws IOException {
        int batesSuffixIndex;
        int lastBateSuffixIndex;
        int batesNo;
        int lastBateNo;
        String batesSuffix;
        String lastBateSuffix;
        batesSuffixIndex = bates.indexOf("0");
        lastBateSuffixIndex = lastBate.indexOf("0");
        if (batesSuffixIndex != lastBateSuffixIndex) {
            status = "Error in LFP file: Suffix index didn't match";            
            Command_import_data.errorMessage = status;
            return false;
        } else {
            batesSuffix = bates.substring(batesSuffixIndex);
            lastBateSuffix = lastBate.substring(lastBateSuffixIndex);
            batesNo = Integer.parseInt(batesSuffix);
            lastBateNo = Integer.parseInt(lastBateSuffix);
            if (batesNo != (lastBateNo + 1)) {
                status = "Error in LFP file: Bates missing at " + bates;                
                Command_import_data.errorMessage = status;
                return false;
            }
        }
        return true;
    }
    
    /**
     * Method to Check the given Batesnumber are in Equal Length
     * 
     * @param bates   //Start BatesNumber
     * @param lastBate //End BatesNumber
     * @param writer
     * @param userSessionId
     * @return
     * @throws java.io.IOException
     */
    private boolean isEqualLength(String bates, String lastBate, MessageWriter writer, String userSessionId) throws IOException {

        int lastBatelength;
        int bateslength;
        lastBatelength = lastBate.length();
        bateslength = bates.length();
        if (bateslength != lastBatelength) {
            status = "Error in LFP file: Bates Length not equal for " + bates;
            Command_import_data.errorMessage = status;
            return false;
        }
        return true;
    }
    
    /**
     * Method to check the given BatesNumber has same Prefix
     * 
     * @param bates    //Start BatesNumber
     * @param lastBate //End BatesNumber
     * @param writer
     * @param userSessionId
     * @return
     * @throws java.io.IOException
     */
    private boolean hasSamePrefix(String bates, String lastBate, MessageWriter writer, String userSessionId) throws IOException {

        int batesPrefixIndex;
        int lastBatePrefixIndex;
        String batesPrefix;
        String lastBatePrefix;
        batesPrefixIndex = bates.indexOf("0");
        lastBatePrefixIndex = lastBate.indexOf("0");
        if (batesPrefixIndex != lastBatePrefixIndex) {
            status = "Error in LFP file: Bates Prefix length didn't match for " + bates;            
            Command_import_data.errorMessage = status;
            return false;
        } else {
            batesPrefix = bates.substring(0, batesPrefixIndex);
            lastBatePrefix = lastBate.substring(0, lastBatePrefixIndex);
            if (!batesPrefix.equals(lastBatePrefix)) {
                status = "Error in LFP file: Bates prefix didn't match for " + bates;
                Command_import_data.errorMessage = status;
                return false;
            }
        }
        return true;
    }
}
