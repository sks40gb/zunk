/* $Header: /home/common/cvsarea/ibase/dia/src/common/PopulateData.java,v 1.3.6.2 2006/03/22 20:27:15 nancy Exp $ */
package common;

/**
 * A container for parameters used in Import Data.
 */
public class PopulateData extends DelimiterData {

    /** the project.project_name of the project to populate */
    public String project_name;
    /** the volume.volume_name of the volume being populated */
    public String volume_name;
    /** the fully qualified dataname of the input file on the server */
    public String dataFilename;
    
    public String field_names;
     
    public String standard_field_validations;
     
    public String user_name;
    
}
