/* $Header: /home/common/cvsarea/ibase/dia/src/common/edit/ProjectEditor.java,v 1.25.2.3 2006/01/24 16:37:19 nancy Exp $ */
package common.edit;

import beans.IbaseTextField;
import common.Log;
import java.util.Map;
import model.ManagedTableModel;
import server.ServerTask;
import java.awt.Toolkit;
import java.awt.Component;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import java.sql.Connection;

/**
 * Edits coded data for the project.
 */
public class ProjectEditor {
   
    private ServerTask task;
    private boolean unitize;
    private String showQueryRaised;
    private String whichStatus;

    /** save loaded models and table names to avoid reloading data*/
    //ArrayList tableList = new ArrayList();
    //Map tableMap = new HashMap();

    private static ManagedTableModel tablevalueModel;

    /** for display of error dialogs */
    private Component comp;

    /** holds description of project_fields */
    private static ProjectMapper projectMap;
    

    Connection con;

    /**
     * Client invokes with a component to be edited.
     * @param map - contains the description of the project
     * @param comp - the dynamic component to be edited
     */
    public ProjectEditor(ProjectMapper map, Component comp) {
        this(null, map, comp, false);
    }

    /**
     * Server invokes with a task.
     */
    public ProjectEditor(ServerTask task) {
        this(task, null, null, false);
    }
    
    /**
     * Create an instance of ProjectEditor to do edits on either client or server.
     * @param projectMap - contains the description of the project
     * @param comp - the dynamic component to be edited
     */
    public ProjectEditor(ServerTask task, ProjectMapper projectMap
                         , Component comp, boolean unitize) {
        this.task = task;
        this.comp = comp;
        if (projectMap == null) {
            //projectMap = new ProjectMapper(task, volume_id, project_id);
            //Log.print("(ProjectEditor) instantiate projectMap " + projectMap.size());
        } else {
            ProjectEditor.projectMap = projectMap;
        }
        this.unitize = unitize;
    }

    /**
     * Do all edits required for the valueMap provided.
     * 
     * @param map - ProjectMapper instance describing the project
     * @param tablevalueModel - client side provides tablevalues
     * @param valueMap - HashMap containing the values to be edited
     * @param force - true if this is a force save, as opposed to a
     * normal, data-edited save or a validation of batch.
     * @param unitize - skip the required-field edits in unitize mode
     * 
     * @return null=no errors; 
     * @return "" returned when this is a server edit
     * @return field name returned to client edits so the cursor
     * can be positioned to the first error field.
     * 
     * @see ProjectMapper
     * @see model.FieldMapper
     */
    public String edit(ProjectMapper map, Map valueMap, boolean force, boolean unitize, int activeGroup,String treatmentLevel) {
        return edit(map, null, valueMap, force, unitize, activeGroup,treatmentLevel);
    }
   
    public String edit(ProjectMapper mapper, Map valueMap, Map validationMap, boolean unitize, boolean force,
            String treatment_level,String whichStatus) {
       this.whichStatus = whichStatus;
       return edit(mapper,valueMap,validationMap,unitize,force,treatment_level);       
    }

    public String edit(ProjectMapper mapper, Map valueMap, Map validationMap, boolean unitize, boolean force, String treatment_level) {
        ProjectEditor.projectMap = mapper;
        String errorString = null;
        if (!unitize) {
            //check the field validation.
            errorString = new script.Validator(mapper, valueMap, validationMap, force, treatment_level, whichStatus).run();
            if (errorString != null) {
                if (!force) {
                    // Just display the error dialog.
                    errorDialog(new String[]{script.Validator.getActiveFieldName()}, "\n", "\n" + errorString);
                    return errorString;
                } else {
                    // Gripe before forcing save if there are errors in the user's data.
                    if (!gripeDialog(new String[]{script.Validator.getActiveFieldName()}, " is a required field.\n")) {
                        return errorString;
                    }
                }
            }
        }
        return null;
    }


    
    public String edit(ProjectMapper map, ManagedTableModel tablevalueModel, Map valueMap
                       , boolean force, boolean unitize, int activeGroup,String treatmentLevel) {
        
        if (valueMap == null || map == null) {
            return null;
            //return "No data found for edit";
        }
        
        ProjectEditor.projectMap = map;
        ProjectEditor.tablevalueModel = tablevalueModel;
        //this.showQueryRaised = showQueryRaised;
        //Log.print("(ProjectEditor).edit project " + projectMap.size());
        //Log.print("(ProjectEditor).edit value " + valueMap.size());
        
        // Accumulate a list of missing required fields for display in error dialog.
        String[] errorString = new String[projectMap.getSize()];
        int errorCount = -1;
        
        // Check to see if this project has document levels and, if it does,
        // get the ProjectMapper.HashValue for the projectfields.level_field_name.
        int level = getFieldLevel(valueMap);
        //Log.print("(ProjectEditor.edit) level is " + level);

        // 1.  Check for required fields that are missing values.
        if (! unitize) {
            //Log.print("(ProjectEditor).edit #1 required");
            for (int i = 0; i < projectMap.getSize(); i++) {
                String name = projectMap.getFieldName(i);
                ProjectMapper.HashValue field = projectMap.getHashValue(name);
                String value = (String)valueMap.get(name);
                boolean checkLevel = ((field.fieldLevel == 1 & treatmentLevel.equals("L1"))||(field.fieldLevel == 0
                        & treatmentLevel.equals("L2")));
                //Log.print("(ProjectEditor).edit name/value " + name + "/" + value);
                if (field.required.equals("Yes")
                    //&& field.fieldLevel == level
                    && checkLevel    
                    && field.fieldGroup == activeGroup) {
                    //Log.print("(ProjectEditor).edit field is required");
                    if (! requiredExists(value)) {
                        if (task != null) { // server
                            //Log.print("(ProjectEditor).edit return false " + name);
                            return ""; 
                        } else {
                            //Log.print("(ProjectEditor).edit task is null");
                            // client -- accumulate errors
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
            //Log.print("(ProjectEditor).edit #2 table mandatory");
            errorCount = -1;
            for (int i = 0; i < projectMap.getSize(); i++) {
                String name = projectMap.getFieldName(i);
                ProjectMapper.HashValue field = projectMap.getHashValue(name);
                String value = (String)valueMap.get(name);
                boolean checkLevel = ((field.fieldLevel == 1 & treatmentLevel.equals("L1"))
                        ||(field.fieldLevel == 0 & treatmentLevel.equals("L2")));
                if("Yes".equals(field.queryraised) || "Yes".equals(field.queryanswered)){
                if (field.tableMandatory.equals("Yes")
                    //&& field.fieldLevel == level
                    && checkLevel
                    && field.fieldGroup == activeGroup
                    && value != null && ! value.equals("")) {
                    boolean check;
                    if (task != null) {
                        // server
                        //Log.print("(ProjectEditor.edit #2) model is " + field.tableName);
                        check = checkMandatory(value, projectMap.getModel(field.tablespecId));
                    } else {
                        // client
                        ManagedTableModel mtm = (ManagedTableModel)
                            ((IbaseTextField)projectMap.getComponent(i)).getProjectModel();
                        //check = checkMandatory(value, getSortedListModel(field.tableName));
                        check = checkMandatory(value, mtm);
                    }
    
                    if (! check) {
                        if (task != null) {
                            return "";
                        } else {
                            // client -- accumulate errors
                            errorCount++;
                            errorString[errorCount] = name;
                        }
                    }
                }
            }
            if (errorCount > -1) {
                if (! force) {
                    // Just display the error dialog.
                    // errorDialog(errorString, " values must be selected from the list.\n"
                    //            , "\nPlease remove values not shown in the list before saving.");
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
        //Log.print("(ProjectEditor).edit #3 min_value");
        errorCount = -1;
        String[] minMaxString = new String[projectMap.getSize()];
        for (int i = 0; i < projectMap.getSize(); i++) {
            String name = projectMap.getFieldName(i);
            ProjectMapper.HashValue field = projectMap.getHashValue(name);
            String value = (String)valueMap.get(name);
            boolean checkLevel = ((field.fieldLevel == 1 & treatmentLevel.equals("L1"))||(field.fieldLevel == 0
                    & treatmentLevel.equals("L2")));
            if (! minEdit(field.minValue, value)
                //&& field.fieldLevel == level
                && checkLevel
                && field.fieldGroup == activeGroup) {
                // value is less than the min value
                //Log.print("(ProjectEditor) #3 value<min " + value + "<" + field.minValue);
                if (task != null) {
                    return "";
                } else {
                    // client -- accumulate errors
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
        //Log.print("(ProjectEditor).edit #4 max_value");
        errorCount = -1;
        minMaxString = new String[projectMap.getSize()];
        for (int i = 0; i < projectMap.getSize(); i++) {
            String name = projectMap.getFieldName(i);
            ProjectMapper.HashValue field = projectMap.getHashValue(name);
            String value = (String)valueMap.get(name);
            boolean checkLevel = ((field.fieldLevel == 1 & treatmentLevel.equals("L1"))||(field.fieldLevel == 0
                    & treatmentLevel.equals("L2")));
            if (! maxEdit(field.maxValue, value)
//                && field.fieldLevel == level
                 && checkLevel   
                && field.fieldGroup == activeGroup) {
                // value is less than the min value
                //Log.print("(ProjectEditor) #4 value>max " + value + ">" + field.maxValue);
                if (task != null) {
                    return "";
                } else {
                    // client -- accumulate errors
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
        //Log.print("(ProjectEditor).edit #5 minimum_size");
        errorCount = -1;
        String[] minimumSizeString = new String[projectMap.getSize()];
        for (int i = 0; i < projectMap.getSize(); i++) {
            String name = projectMap.getFieldName(i);
            ProjectMapper.HashValue field = projectMap.getHashValue(name);
            String value = (String)valueMap.get(name);
            boolean checkLevel = ((field.fieldLevel == 1 & treatmentLevel.equals("L1"))||(field.fieldLevel == 0
                    & treatmentLevel.equals("L2")));
            //Log.print("(ProjectEditor 5) field.fieldLevel " + field.fieldLevel);
            //Log.print("(ProjectEditor 5) level " + level);
            //Log.print("(ProjectEditor 5) field.fieldGroup " + field.fieldGroup);
            //Log.print("(ProjectEditor 5) activeGroup " + activeGroup);
            //Log.print("(ProjectEditor 5) value.length " + value.length());
            //Log.print("(ProjectEditor 5) field.minimumSize " + field.minimumSize);
            if (//field.fieldLevel == level
                checkLevel    
                && field.fieldGroup == activeGroup
                && value != null && value.length() > 0
                && field.minimumSize > value.length()) {
                // value is less than the min value
                //Log.print("(ProjectEditor) #5 value length <minimumSize " + value + "<" + field.minimumSize);
                if (task != null) {
                    return "";
                } else {
                    // client -- accumulate errors
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
        }
        return null;
    }

    /**
     * Check a single field for the edit parameters defined in project_fields.
     * <p>
     * Since this can be executed on the server or client, task==null means on the client.
     * </p>
     * 
     * @param fieldName - the name of the field to be edited.
     * @param projectField - one instance of HashValue, containing the edit parameters
     * @param value - the value currently in the field
     */
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
            //Log.print("(ProjectEditor.getFieldLevel) field/value " + field.fieldName + "/" + value);
            //if (value.lastIndexOf(";") > -1) {
            //    value = value.substring(0, value.lastIndexOf(";"));
            //}
            int j;
            for (j = 0; j < tablevalueModel.getRowCount(); j++) {
                if ( Integer.parseInt((String)tablevalueModel.getValueAt(j, 1)) == field.tablespecId
                    && ((String)tablevalueModel.getValueAt(j, 0)).equals(value) ) {
                    //Log.print("    checking " + tablevalueModel.getValueAt(j, 0));
                    break;
                }
            }
            if (j < tablevalueModel.getRowCount()) {
                //Log.print("    returning " + tablevalueModel.getValueAt(j, 2));
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
        //Log.print("(ProjectEditor).checkMandatory " + str + "/" + model.size());

        String[] strArray = str.split(";");
        int size = strArray.length;
        for (int i = 0; i < size; i++) {
            if (! model.contains((strArray[i]).trim())) {
                //Log.print("(ProjectEditor).checkMandatory return false " + (strArray[i]).trim());
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
        //Log.print("(ProjectEditor).checkMandatory " + str + "/" + model.size());

        String[] strArray = str.split(";");
        int rowCount = model.getRowCount();
        int size = strArray.length;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < rowCount; j++) {
                if (str.equals((String)model.getValueAt(j, 0))) {
                    break;
                }
                if (j >= rowCount) {
                    // str not found in model
                    return false;
                    
                }
            }
            //if (! model.contains((strArray[i]).trim())) {
            //    //Log.print("(ProjectEditor).checkMandatory return false " + (strArray[i]).trim());
            //    return false;
            //}
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
        String errorString1 = "";
        for (int i = 0; i < field.length; i++) {
            if (field[i] != null) {
                errorString1 = errorString + field[i];
                errorString = errorString + errorMessage;
            }
        }
        showErrorDialog(errorString, errorDirective,errorString1);
    }
    
    /**
     * Show the error dialog.
     * @param errorString - message to be displayed
     * @param errorDirective - message directing user action
     */
    private void showErrorDialog(String errorString, String errorDirective,String errorString1){
        Toolkit.getDefaultToolkit().beep();
        Log.print("BEEP> ProjectEditor.showErrorDialog");
        Object[] options = {"Ok"};
        int response = JOptionPane.showOptionDialog(comp,
                errorString + errorDirective,
                "Validation Field Error:  " +errorString1,
                JOptionPane.OK_OPTION,
                JOptionPane.ERROR_MESSAGE,
                null,
                options,
                options[0]);
    }
   private void showErrorDialog(String errorString, String errorDirective){
        Toolkit.getDefaultToolkit().beep();
        Log.print("BEEP> ProjectEditor.showErrorDialog");
        Object[] options = {"Ok"};
        int response = JOptionPane.showOptionDialog(comp,
                errorString + errorDirective,
                "Field Error:",
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

    /**
     * Filter the tablevalueModel provided by SplitPaneViewer by tableName.
     */
    //private ManagedTableFilter getSortedListModel(String tableName) {
    //    ManagedTableFilter filter = new ManagedTableFilter(tablevalueModel) {
    //                public boolean accept(TableRow theRow) {
    //                    if (tableName.equals((String)theRow.getValue(1))) {
    //                        return true;
    //                    }
    //                    return false;
    //                }
    //            };
    //    if (filter.getRowCount() > 0) {
    //        return filter;
    //    }
    //    // No tablevalue's -- could be a global table.
    //}
}
