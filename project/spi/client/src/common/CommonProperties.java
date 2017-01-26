/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package common;

/**
 * Class to Hold Common Property Values
 * @author sunil
 */
public class CommonProperties {

    public static  String SERVER_IP;
    public static  int SERVER_PORT;
    private static final String PROP_SERVER_CONTEXT = "serverContext";
    public final static String MESSAGE_DTD = "message.dtd";
    public final static String DIA_PROPERTIES = "dia.properties";
    public final static String LOCAL_PROPERTIES = "local.properties";
    public final static String SERVER_PROPERTIES = "server.properties";
    public final static String RUN = "run";
    private final static String SERVER_URL = "http://localhost:8080/fossa/client";
    private final static String IMAGE_URL = "http://localhost:8080/fossa/image";    //base directory
    private static String workingDir = getBaseDirectory();

    
    
    public static String getUploadFolderPath(){        
      
     return   "http://" + SERVER_IP + 
                ":" +  (SERVER_PORT == 0 ? 8080 : SERVER_PORT) +
                "/fossa/UPLOAD/";
    }
    
    /**
     *
     * @return message.dtd file name with path
     */
    public static String getWorkingDirectory() {
        return workingDir;
    }

    /**
     *
     * @return message.dtd file name with path
     */
    public static String getMessageDTDFilePath() {
        return "files/" + MESSAGE_DTD;
    }

    /**
     *
     * @return dia.properties file name with path
     */
    public static String getDiaPropertiesFilePath() {
        return "files/" + DIA_PROPERTIES;
    }

    /**
     *
     * @return local.properties file name with path
     */
    public static String getLocalPropertiesFilePath() {
        return LOCAL_PROPERTIES;
    }

    /**
     *
     * @return server.properties file name with path
     */
    public static String getServerPropertiesFilePath() {
        return SERVER_PROPERTIES;
    }

    /**
     *
     * @return run.log file name with path
     */
    public static String getRunLogFilePath() {
        return workingDir + "/" + RUN + ".log";
    }

    /**
     *
     * @return server url
     */
    public static String getServerUrl() {
        return SERVER_URL;
    }

    /**
     *
     * @return server url
     */
    public static String getImageUrl() {
        return IMAGE_URL;
    }

    /**  
     * get actual working directory.
     */
    public static String getBaseDirectory() {        
        String result;
        StringBuffer buffer = new StringBuffer(System.getProperty("user.dir"));
        int pos;
        while ((pos = buffer.indexOf("\\")) >= 0) {
            buffer.setCharAt(pos, '/');
        }
        result = buffer.toString();
        //Log.write("FileProperties.getBaseDIrectory() => '"+result+"'");
        return result;
    }
}
