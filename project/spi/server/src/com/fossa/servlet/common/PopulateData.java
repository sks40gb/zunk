/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.common;

/**
 *
 * @author Bala
 */
public class PopulateData extends DelimiterData{

    /** the project.project_name of the project to populate */
    public String project_name;
    
    /** the volume.volume_name of the volume being populated */
    public String volume_name;
    
    /** the fully qualified dataname of the input file on the server */
    public String dataFilename;
    
    /** the projectfields.fieldname of the project to populated */
    public String field_names;
     
    /** the standardField validation for corresponding project field  */
    public String standard_field_validations;
    
    public String user_name;
}
