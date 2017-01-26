/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package valueobjects;

import java.sql.ResultSet;

/**
 *
 * @author sunil
 */
public class FieldFormatData {
    
    public static final String OVP_FIELD = "OVP_FIELD";
    public static final String PROJECT_FIELD = "PROJECT_FIELD";    
    private static int volumeId;
    private int sequence;
    private String projectFieldId;    
    private String ovpFieldId;    
    private String name;
    private String separator;
    private String type;
    private String format;
    private boolean suppress = false;
 
    public FieldFormatData(ResultSet rs) {
        /*  
        1. project_field_id       
        2. ovp_fields_id   
        3. field_name  
        4. separator   
        5. sequence    
        6. format      
        7. suppress
        8. type*/
        
        try {
            projectFieldId = rs.getString(1);
            ovpFieldId =  rs.getString(2);
            name = rs.getString(3);
            separator = rs.getString(4);
            sequence = rs.getInt(5);
            format = rs.getString(6);            
            suppress = (rs.getString(7) != null && rs.getString(7).equals("true")) ? true : false;
            type = rs.getString(8);

        } catch (Exception e) {
        }
    }

    public static String [] getDeliminators(){      
        int deliminatorInt [] = {
                        33,34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 58, 
			59,60, 61, 62, 63, 64, 91, 92, 93, 94, 95, 96, 123, 124, 125, 
			126,161, 162, 163, 164, 165, 166, 167, 168, 169, 170, 171, 172, 
			174,175, 176, 177, 178, 179, 180, 181, 182, 183, 184, 185, 186,
			187,188, 189, 190, 191, 192, 193, 194, 195, 196, 197, 198, 199,
			200,201, 202, 203, 204, 205, 206, 207, 208, 209, 210, 211, 212,
			213,214, 215, 216, 217, 218, 219, 220, 221, 222, 223, 224, 225,
			226,227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238,
			239,240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250, 251,
			252,253, 254, 255};
        
        String [] delim = new String[deliminatorInt.length];
        for(int i =0; i< deliminatorInt.length; i++){
            delim[i] = String.valueOf((char)deliminatorInt[i]);
        } 
        return delim;
    }
    
    public FieldFormatData(int sequence, String name, String separator) {
        this.sequence = sequence;     
        this.name = name;
        this.separator = separator;     
    }


    public String getFormat() {
        return format == null ? "" : format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public boolean isSuppress() {
        return suppress;
    }

    public void setSuppress(boolean suppress) {
        this.suppress = suppress;
    }

    public String getType() {
        return (type == null ? FieldFormatData.PROJECT_FIELD : type);
    }

    public void setType(String type) {
        this.type = type;
    }

    public static int getVolumeId() {
        return volumeId;
    }

    public static void setVolumeId(int volumeId) {
        FieldFormatData.volumeId = volumeId;
    }

    public String getOvpFieldId() {
        return ovpFieldId;
    }

    public void setOvpFieldId(String ovpFieldId) {
        this.ovpFieldId = ovpFieldId;
    }

    public String getProjectFieldId() {
        return projectFieldId;
    }

    public void setProjectFieldId(String projectFieldId) {
        this.projectFieldId = projectFieldId;
    }
}
