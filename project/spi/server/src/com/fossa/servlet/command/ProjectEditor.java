/*
 * ProjectEditor.java
 *
 * Created on December 4, 2007, 4:50 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.Log;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.server.ManagedTableModel;
import com.fossa.servlet.session.UserTask;
import java.awt.Component;
import java.awt.Toolkit;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 * This class handles the request for editing the project fields coded values
 * @author bmurali
 */
public class ProjectEditor {
    
    private UserTask task;
    private boolean unitize;        
    private static ManagedTableModel tablevalueModel;   
    
    /** for display of error dialogs */
    private Component comp;

    /** holds description of project_fields */
    private static ProjectMapper projectMap;   
    
    /** Creates a new instance of ProjectEditor */
    public ProjectEditor(ProjectMapper map, Component comp) {
        this(null,null, map, comp, false);
    }
    
    /**
     * Server invokes with a task.
     */
    public ProjectEditor(UserTask task,DBTask dbTask) {
        this(task,dbTask, null, null, false);
    }
    
    public ProjectEditor(UserTask task,DBTask dbTask, ProjectMapper projectMap
                         , Component comp, boolean unitize) {
        this.task = task;
        this.comp = comp;
        if (projectMap == null) {            
        } else {
            this.projectMap = projectMap;
        }
        this.unitize = unitize;
    }
          
     public String edit(ProjectMapper map, Map valueMap, boolean force, boolean unitize
                       ,int activeGroup) {
        return edit(map, null, valueMap, force, unitize, activeGroup);
    }

    public String edit(ProjectMapper map, ManagedTableModel tablevalueModel, Map valueMap
                       , boolean force, boolean unitize, int activeGroup) {
        if (valueMap == null || map == null) {
            return null;
            //return "No data found for edit";
        }
        
        this.projectMap = map;
        this.tablevalueModel = tablevalueModel;      
        
        // Accumulate a list of missing required fields for display in error dialog.
        String[] errorString = new String[projectMap.getSize()];
        int errorCount = -1;
        
        // Check to see if this project has document levels and, if it does,
        // get the ProjectMapper.HashValue for the projectfields.level_field_name.
        int level = getFieldLevel(valueMap);       

        // 1.  Check for required fields that are missing values.
        if (! unitize) {           
            for (int i = 0; i < projectMap.getSize(); i++) {
                String name = projectMap.getFieldName(i);
                ProjectMapper.HashValue field = projectMap.getHashValue(name);
                String value = (String)valueMap.get(name);               
                if (field.required.equals("Yes")
                    && field.fieldLevel == level
                    && field.fieldGroup == activeGroup) {                    
                    if (! requiredExists(value)) {
                        if (task != null) {                             
                            return ""; 
                        } else {                            
                            errorCount++;
                            errorString[errorCount] = name;
                        }
                    }
                }
            }
            if (errorCount > -1) {
                if (! force) {
                    // Just display the error dialog.
                    errorDialog(errorString, " is a required field.\n"
                                ,"\nPlease enter required data before saving.");
                    return errorString[0];
                } else {
                    // Gripe before forcing save if there are errors in the user's data.
                    if (! gripeDialog(errorString, " is a required field.\n")) {
                        return errorString[0];
                    }
                }
            }
        }
        
        // 2.  Check for values in mandatory list fields that are not from the table.
        if (! unitize) {           
            errorCount = -1;
            for (int i = 0; i < projectMap.getSize(); i++) {
                String name = projectMap.getFieldName(i);
                ProjectMapper.HashValue field = projectMap.getHashValue(name);
                String value = (String)valueMap.get(name);
                if (field.tableMandatory.equals("Yes")
                    && field.fieldLevel == level
                    && field.fieldGroup == activeGroup
                    && value != null && ! value.equals("")) {
                    boolean check;
                    if (task != null) {                        
                        check = checkMandatory(value, projectMap.getModel(field.tablespecId));
                   }                     
                }
            }
            if (errorCount > -1) {
                if (! force) {                    
                    return errorString[0]; // crossFieldEdit will show the errors
                } else {
                    // Gripe before forcing save if there are errors in the user's data.
                    if (! gripeDialog(errorString, " values must be selected from the list.\n")) {
                        return errorString[0];
                    }
                }
            }
        }

        // 3.  Check for values < min_value.        
        errorCount = -1;
        String[] minMaxString = new String[projectMap.getSize()];
        for (int i = 0; i < projectMap.getSize(); i++) {
            String name = projectMap.getFieldName(i);
            ProjectMapper.HashValue field = projectMap.getHashValue(name);
            String value = (String)valueMap.get(name);
            if (! minEdit(field.minValue, value)
                && field.fieldLevel == level
                && field.fieldGroup == activeGroup) {                
                if (task != null) {
                    return "";
                } else {                
                    errorCount++;
                    errorString[errorCount] = name;
                    minMaxString[errorCount] = field.minValue;
                }
            }
        }
        if (errorCount > -1) {
            if (! force) {
                // Just display the error dialog.
                errorDialog(errorString, " values cannot be less than " + minMaxString[0]
                            , "\nPlease adjust the values before saving.");
                return errorString[0]; // crossFieldEdit will show the errors
            } else {
                // Gripe before forcing save if there are errors in the user's data.
                if (! gripeDialog(errorString, " values cannot be less than " + minMaxString[0])) {
                    return errorString[0];
                }
            }
        }
        
        // 4.  Check for values > max_value.        
        errorCount = -1;
        minMaxString = new String[projectMap.getSize()];
        for (int i = 0; i < projectMap.getSize(); i++) {
            String name = projectMap.getFieldName(i);
            ProjectMapper.HashValue field = projectMap.getHashValue(name);
            String value = (String)valueMap.get(name);
            if (! maxEdit(field.maxValue, value)
                && field.fieldLevel == level
                && field.fieldGroup == activeGroup) {                
                if (task != null) {
                    return "";
                } else {                    
                    errorCount++;
                    errorString[errorCount] = name;
                    minMaxString[errorCount] = field.maxValue;
                }
            }
        }
        if (errorCount > -1) {
            if (! force) {
                // Just display the error dialog.
                errorDialog(errorString, " values cannot be greater than " + minMaxString[0]
                            , "\nPlease adjust the values before saving.");
                return errorString[0]; // crossFieldEdit will show the errors
            } else {
                // Gripe before forcing save if there are errors in the user's data.
                if (! gripeDialog(errorString, " values cannot be greater than " + minMaxString[0])) {
                    return errorString[0];
                }
            }
        }

        // 5.  Check that values are at least as long as the minimum_size.        
        errorCount = -1;
        String[] minimumSizeString = new String[projectMap.getSize()];
        for (int i = 0; i < projectMap.getSize(); i++) {
            String name = projectMap.getFieldName(i);
            ProjectMapper.HashValue field = projectMap.getHashValue(name);
            String value = (String)valueMap.get(name);            
            if (field.fieldLevel == level
                && field.fieldGroup == activeGroup
                && value != null && value.length() > 0
                && field.minimumSize > value.length()) {                
                if (task != null) {
                    return "";
                } else {                    
                    errorCount++;
                    errorString[errorCount] = name;
                    minimumSizeString[errorCount] = Integer.toString(field.minimumSize);
                }
            }
        }
        if (errorCount > -1) {
            if (! force) {
                // Just display the error dialog.
                errorDialog(errorString, " values must be at least " + minimumSizeString[0] + " characters long."
                            , "\nPlease adjust the values before saving.");
                return errorString[0]; // crossFieldEdit will show the errors
            } else {
                // Gripe before forcing save if there are errors in the user's data.
                if (! gripeDialog(errorString, " values must be at least " + minimumSizeString[0] + " characters long.")) {
                    return errorString[0];
                }
            }
        }
        
        return null;
    }
    
    
    private boolean requiredExists(String value) {
        if (value == null || value.length() == 0) {
            return false;
        }
        return true;
    }

    /**
     * Find the field in this project that controls Document Level,
     * projectfields.level_field_name, then get the tablevalue.field_level for that value.
     * @return an int that is the level of the value in the controlling field
     */
    private int getFieldLevel(Map valueMap) {
        if (tablevalueModel == null) {
            return 0;
        }
        int i;
        ProjectMapper.HashValue field = null;
        for (i = 0; i < projectMap.getSize(); i++) {
            field = projectMap.getHashValue(i);
            if (field.is_level_field.equals("Yes")) {
                break;
            }
        }
        if (i < projectMap.getSize()) {
            // found the controlling field
            String value = ((String)valueMap.get(field.fieldName)).trim();            
            int j;
            for (j = 0; j < tablevalueModel.getRowCount(); j++) {
                if ( Integer.parseInt((String)tablevalueModel.getValueAt(j, 1)) == field.tablespecId
                    && ((String)tablevalueModel.getValueAt(j, 0)).equals(value) ) {                    
                    break;
                }
            }
            if (j < tablevalueModel.getRowCount()) {                
                return Integer.parseInt((String)tablevalueModel.getValueAt(j, 2));
            }

        }
        // no document levels for this project or no value in level field
        return 0;
    }

    /** See if the given string is listed in the model.
     * @param str - the string for which to search in the model
     * @param model - the tablevalue model from the server in which to search for the string
     * @return true if the model contains the string
     * @return false if the model does not contain the string
     */
    public boolean checkMandatory(String str, DefaultListModel model) {
        String[] strArray = str.split(";");
        int size = strArray.length;
        for (int i = 0; i < size; i++) {
            if (! model.contains((strArray[i]).trim())) {                
                return false;
            }
        }
        return true;
    }

    /** See if the given string is listed in the model.
     * @param str - the string for which to search in the model
     * @param model - the tablevalue model from the client in which to search for the string
     * @return true if the model contains the string
     * @return false if the model does not contain the string
     */
    public boolean checkMandatory(String str, ManagedTableModel model) {
        String[] strArray = str.split(";");
        int rowCount = model.getRowCount();
        int size = strArray.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < rowCount; j++) {
                if (str.equals((String)model.getValueAt(j, 0))) {
                    break;
                }
                if (j >= rowCount) {                    
                    return false;
                    
                }
            }
        }
        return true;
    }

    /**
     * See if the given value is less than the given minimum value.
     * @param min - the minimum value allowd for the given value
     * @param value - the value to compare to min
     * @return true if the value is >= the min; false if the value
     * is < min
     */
    public boolean minEdit(String min, String value) {
        if (min != null && ! min.equals("")
            && value != null && ! value.equals("")) {
            if (value.compareToIgnoreCase(min) < 0) {
                // value is less than the min value
                return false;
            }
        }
        return true;
    }

    /**
     * See if the given value is greater than the given maximum value.
     * @param max - the maximum value allowd for the given value
     * @param value - the value to compare to max
     * @return true if the value is <= the max; false if the value
     * is > max
     */
    public boolean maxEdit(String max, String value) {
        if (max != null && ! max.equals("")
            && value != null && ! value.equals("")) {
            if (value.compareToIgnoreCase(max) > 0) {
                // value is greater than the max value
                return false;
            }
        }
        return true;
    }

    /** 
     * Show a dialog complaining about errors in the data.
     * @param field - array of fields containing errors to be displayed on the dialog
     * @param errorMessage - message to display after each error field
     * @param value - value to display after errorMessage on each field line
     * @param errorDirective - message directing user action
     */
    public void errorDialog(String[] field, String errorMessage, String[] value
                            , String errorDirective) {
        String errorString = "";
        for (int i = 0; i < field.length; i++) {
            if (field[i] != null) {
                errorString = errorString + field[i] + errorMessage + value[i] + "\n";
            }
        }
        showErrorDialog(errorString, errorDirective);
    }

    /** 
     * Show a dialog complaining about errors in the data.
     * @param field - array of fields containing errors to be displayed on the dialog
     * @param errorMessage - message to display after each error field
     * @param errorDirective - message directing user action
     */
    public void errorDialog(String[] field, String errorMessage
                            , String errorDirective) {
        String errorString = "";
        for (int i = 0; i < field.length; i++) {
            if (field[i] != null) {
                errorString = errorString + field[i] + errorMessage;
            }
        }
        showErrorDialog(errorString, errorDirective);
    }
    
    /**
     * Show the error dialog.
     * @param errorString - message to be displayed
     * @param errorDirective - message directing user action
     */
    private void showErrorDialog(String errorString, String errorDirective){
        Toolkit.getDefaultToolkit().beep();
        Log.print("BEEP> ProjectEditor.showErrorDialog");
        Object[] options = {"Ok"};
        int response = JOptionPane.showOptionDialog(comp,
                errorString + errorDirective,
                "Field Error",
                JOptionPane.OK_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]);
    }

    /** 
     * Show a dialog complaining about errors in the data and giving the user
     * the option of proceeding with save or cancelling save.
     * @param field - array of fields containing errors to be displayed on the dialog
     * @param errorMessage - message to display after each error field
     * @param value - value to display after errorMessage on each field line
     * @return true if the user selected "Save with Errors"; else false
     */
    private boolean gripeDialog(String[] field, String errorMessage, String[] value) {
        String errorString = "";
        for (int i = 0; i < field.length; i++) {
            if (field[i] != null) {
                errorString = errorString + field[i] + errorMessage + value + "\n";
            }
        }
        return showGripeDialog(errorString);
    }
    
    /** 
     * Show a dialog complaining about errors in the data and giving the user
     * the option of proceeding with save or cancelling save.
     * @param field - array of fields containing errors to be displayed on the dialog
     * @param errorMessage - message to display after each error field
     * @return true if the user selected "Save with Errors"; else false
     */
    private boolean gripeDialog(String[] field, String errorMessage) {
        String errorString = "";
        for (int i = 0; i < field.length; i++) {
            if (field[i] != null) {
                errorString = errorString + field[i] + errorMessage;
            }
        }
        return showGripeDialog(errorString);
    }

    /**
     * Show the error dialog.
     * @param errorString - message to be displayed
     * @return true if the user selected "Save with Errors"; else false
     */
    private boolean showGripeDialog(String errorString) {
        Object[] options = {"Save With Errors",
                            "Don't Save"};
        int response = JOptionPane.showOptionDialog(comp,
                errorString 
                + "\nThe data on the screen contains the above error(s)."
                + "\n Your options are: "
                  + "\n         \"Save With Errors\" to force the save of your changes, "
                  + "\n         \"Don't Save\" to continue editing the data on this screen.",
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);
        if (response == JOptionPane.NO_OPTION) {
            return false;
        }
        return true;
    }

}
