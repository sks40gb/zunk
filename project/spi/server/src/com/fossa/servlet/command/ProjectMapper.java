/*
 * ProjectMapper.java
 *
 * Created on December 4, 2007, 3:52 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

/**
 * Class to maintain the project and project fields records.
 * @author bmurali
 */
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import org.apache.log4j.Logger;

/**
 * Puts the project description in a HashMap.
 */
public class ProjectMapper extends HashMap {

    private UserTask task;
    private DBTask dbTask;
    public ResultSet rs1 = null;
    private int project_id = 0;
    public String projectName;
    /** Keep an ArrayList of fieldnames for sequential retrieval of values. */
    public ArrayList key = new ArrayList();
    /** Keep an ArrayList of the gui component for setting of field properties and focus. */
    public ArrayList component = new ArrayList();
    /** Server only -- Load a Map containing a DefaultListModel of each mandatory table used by the project. 
     * <p>
     * Note:  The table_value models are kept by TableMapper for the client side.
     * </p>
     */
    public Map modelMap = new HashMap();
    
    private static Logger logger = Logger.getLogger("com.fossa.servlet.command");

    public class HashValue {

        /** properties for each field in project_fields */
        public int sequence;
        public String fieldName;
        public String oldName;
        public String fieldType;
        public int fieldSize;
        public int minimumSize;
        public String repeated;
        public String required;
        public String defaultValue;
        public String minValue;
        public String maxValue;
        public int tablespecId;
        public String tableMandatory;
        public String mask;
        public String validChars;
        public String invalidChars;
        public String charset;
        public String typeField;
        public String typeValue;
        public String unitize;
        public String spellcheck;
        public int fieldLevel;
        public int fieldGroup;
        public String is_level_field;

        /**
         * Structure to store the fields associated with projects (project_fields).
         * Store zero-length strings rather than nulls for those fields that may not
         * contain data.
         * @param The parameters corrspond to the fields defined in project_fields.
         * @see "projectfields table in database"
         */
        public HashValue(int sequence, String fieldName, String oldName, String fieldType, int fieldSize, 
                int minimumSize, String repeated, String required, String defaultValue, String minValue, String maxValue, int tablespecId, String tableMandatory, String mask, String validChars, String invalidChars, String charset, String typeField, String typeValue, String unitize, String spellcheck, int fieldLevel, String is_level_field, int fieldGroup) {
            this.sequence = sequence;
            this.fieldName = fieldName;
            this.oldName = oldName;
            this.fieldType = fieldType;
            this.fieldSize = fieldSize;
            this.minimumSize = minimumSize;
            this.repeated = repeated;
            this.required = required;
            this.tablespecId = tablespecId;
            this.defaultValue = defaultValue;
            if (defaultValue == null) {
                this.defaultValue = "";
            }
            this.minValue = minValue;
            if (minValue == null) {
                this.minValue = "";
            }
            this.maxValue = maxValue;
            if (this.maxValue == null) {
                this.maxValue = "";
            }
            this.tableMandatory = tableMandatory;
            if (this.tableMandatory == null) {
                this.tableMandatory = "";
            }
            this.mask = mask;
            if (this.mask == null) {
                this.mask = "";
            }
            this.validChars = validChars;
            if (this.validChars == null) {
                this.validChars = "";
            }
            this.invalidChars = invalidChars;
            if (this.invalidChars == null) {
                this.invalidChars = "";
            }
            this.charset = charset;
            if (this.charset == null) {
                this.charset = "";
            }
            this.typeField = typeField;
            if (this.typeField == null) {
                this.typeField = "";
            }
            this.typeValue = typeValue;
            if (this.typeValue == null) {
                this.typeValue = "";
            }
            this.unitize = unitize;
            this.spellcheck = spellcheck;
            this.fieldLevel = fieldLevel;
            this.is_level_field = is_level_field;
            this.fieldGroup = fieldGroup;       
        }
    }
    Connection con;

    /** Server invokes with a task. */
    public ProjectMapper(UserTask task, DBTask dbTask, int project_id) {
        this(task, dbTask, null, project_id);
    }

    /** Client invokes with a map containing the project data */
    public ProjectMapper(Map map) {
        this(null, null, map, 0);
    }

    // Create an instance of ProjectMapper, a hashMap description of project_fields.
    public ProjectMapper(UserTask task, DBTask dbTask, Map map, int project_id) {
        super();
        this.task = task;
        this.dbTask = dbTask;
        this.project_id = project_id;
        if (map == null) {
            loadProjectDescription();
        } else {
            loadProjectDescriptionFromMap(map);
        }
    }

    /**
     * Server -- Read all of the fields for the given project from the database.
     */
    private void loadProjectDescription() {
        try {
            Statement st = dbTask.getStatement();
            Connection con = null;
            ResultSet selectFieldResulstSet = st.executeQuery("SELECT F.project_id,   " +
                                                   "sequence,   field_name,   field_type,   field_size,   " +
                                                   "minimum_size,   repeated,   required,   default_value,   " +
                                                   "min_value,   max_value,   tablespec_id,   table_mandatory,   " +
                                                   "mask,   valid_chars,   invalid_chars,   charset,   type_field,   " +
                                                   "type_value,   unitize,   spell_check,   field_level,    " +
                                                   "CASE WHEN field_name = level_field_name   THEN 'Yes' Else 'No' END, " +
                                                   "field_group  FROM projectfields F      " +
                                             "inner join project P on P.project_id = F.project_id  " +
                                             "WHERE P.project_id=" + project_id + "  order by sequence");

            while (selectFieldResulstSet.next()) {
                String fieldName = selectFieldResulstSet.getString(3);
                HashValue value = new HashValue(
                        selectFieldResulstSet.getInt(2), 
                        fieldName,
                        "" /* oldName */, 
                        selectFieldResulstSet.getString(4), 
                        selectFieldResulstSet.getInt(5), 
                        selectFieldResulstSet.getInt(6),
                        selectFieldResulstSet.getString(7),
                        selectFieldResulstSet.getString(8),
                        selectFieldResulstSet.getString(9),
                        selectFieldResulstSet.getString(10), 
                        selectFieldResulstSet.getString(11), 
                        selectFieldResulstSet.getInt(12), 
                        selectFieldResulstSet.getString(13), 
                        selectFieldResulstSet.getString(14), 
                        selectFieldResulstSet.getString(15),
                        selectFieldResulstSet.getString(16), 
                        selectFieldResulstSet.getString(17), 
                        selectFieldResulstSet.getString(18), 
                        selectFieldResulstSet.getString(19), 
                        selectFieldResulstSet.getString(20), 
                        selectFieldResulstSet.getString(21), 
                        selectFieldResulstSet.getInt(22), 
                        selectFieldResulstSet.getString(23), 
                        selectFieldResulstSet.getInt(24));
                put(fieldName, value);
                key.ensureCapacity(key.size() + 1);
                key.add((Object) fieldName);
                
                if (value.tableMandatory.equals("Yes")) {
                    // Server side only needs the table values loaded,
                    // if use of the table is mandatory, to use during validateBatch.
                    // Save the table name now and get the values after this rs is closed.
                    //Log.print("(ProjectMapper).loadProjectDescription should load list model " + value.tablespecId);
                    modelMap.put(Integer.toString(value.tablespecId), new Object());
                }
            }
            selectFieldResulstSet.close();

            // Load mandatory table values, if any.
            if (modelMap.size() > 0) {
                Iterator it = modelMap.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry entry = (Map.Entry) it.next();
                    int key = Integer.parseInt((String) entry.getKey()); // tablespec_id

                    // add the table values to a new model
                    DefaultListModel model = new DefaultListModel();                    
                    PreparedStatement select_pmap_value = con.prepareStatement(SQLQueries.SEL_PMAP_VALUE);
                    select_pmap_value.setInt(1, key);
                    selectFieldResulstSet = select_pmap_value.executeQuery();

                    while (selectFieldResulstSet.next()) {
                        model.addElement((Object) selectFieldResulstSet.getString(1));
                    }
                    selectFieldResulstSet.close();

                    // add the new model to mapModel
                    entry.setValue((Object) model);
                //Log.print("(ProjectMapper).loadProjectDescription loading list " + key);                  
                }
            }
        } catch (SQLException e) {
            logger.error("Exception while getting value from table spec" + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(e);
        }
    }

    /**
     * Client -- Load all of the fields from a Map to ProjectMapper format.
     * 
     * <p>
     * @param map - format HashMap:  key="Recip" value=(HashMap) valueMap
     *                             key="Copyee" value=(HashMap) valueMap
     *                              ...
     *        valueMap format:     key="sequence" value=1
     *                             key="field_type" value=name
     *                              ...
     */
    private void loadProjectDescriptionFromMap(Map map) {
        String fieldName = "";
        Map valueMap;
        //Log.print("(ProjectMapper).loadProjectDescriptionFromMap map is " + map.size());
        int i = 1;
        while (key.size() < map.size()) {
            valueMap = (Map) map.get(String.valueOf(i));
            if (valueMap != null) {
                fieldName = (String) valueMap.get("field_name");
                HashValue value = new HashValue(i, fieldName,
                        "", // oldName
                        (String) valueMap.get("field_type"),
                        Integer.parseInt((String) valueMap.get("field_size")),
                        Integer.parseInt((String) valueMap.get("minimum_size")),
                        (String) valueMap.get("repeated"),
                        (String) valueMap.get("required"),
                        (String) valueMap.get("default_value"),
                        (String) valueMap.get("min_value"),
                        (String) valueMap.get("max_value"),
                        Integer.parseInt((String) valueMap.get("tablespec_id")),
                        (String) valueMap.get("table_mandatory"),
                        (String) valueMap.get("mask"),
                        (String) valueMap.get("valid_chars"),
                        (String) valueMap.get("invalid_chars"),
                        (String) valueMap.get("charset"),
                        (String) valueMap.get("type_field"),
                        (String) valueMap.get("type_value"),
                        (String) valueMap.get("unitize"),
                        (String) valueMap.get("spell_check"),
                        Integer.parseInt((String) valueMap.get("field_level")),
                        (String) valueMap.get("is_level_field"),
                        Integer.parseInt((String) valueMap.get("field_group")));
                //Log.print("(projectMapper) mapped unitize is "+(String)valueMap.get("unitize"));
                put(fieldName, value);
                //key.ensureCapacity(key.size() + 1);
                if (task == null) {
                    // client side -- all fields will have dynamic components
                    component.ensureCapacity(key.size());
                }
                key.add((Object) fieldName);
            }
            i++;
        }
    }

    /**
     * Return a HashMap created from a projectMapper.
     * <p>
     * @return Map - format HashMap:  key="Recip" value=(HashMap) valueMap
     *                                key="Copyee" value=(HashMap) valueMap
     *                                 ...
     *        valueMap format:        key="sequence" value=1
     *                                key="field_type" value=name
     *                                 ...
     */
    public Map getMap() {
        HashValue hv = null;
        HashMap map = new HashMap();

        for (Iterator i = entrySet().iterator(); i.hasNext();) {
            Map.Entry e = (Map.Entry) i.next();
            //Log.print("(ProjectMapper).getMap 1 " + e.getKey() + ": " + e.getValue());
            if (e.getValue() == null) {
                // deleted field
                map.put(e.getKey(), null);
            } else {
                hv = getHashValue((String) e.getKey());
                //Log.print("(ProjectMapper).getMap 2 " + hv.sequence + "/" + hv.fieldName + "/" + e.getKey());
                HashMap valueMap = new HashMap();
                valueMap.put("sequence", String.valueOf(hv.sequence));
                valueMap.put("field_name", hv.fieldName);
                valueMap.put("old_name", hv.oldName);
                valueMap.put("field_type", hv.fieldType);
                valueMap.put("field_size", String.valueOf(hv.fieldSize));
                valueMap.put("minimum_size", String.valueOf(hv.minimumSize));
                valueMap.put("repeated", hv.repeated);
                valueMap.put("required", hv.required);
                valueMap.put("default_value", hv.defaultValue);
                valueMap.put("min_value", hv.minValue);
                valueMap.put("max_value", hv.maxValue);
                valueMap.put("tablespec_id", Integer.toString(hv.tablespecId));
                valueMap.put("table_mandatory", hv.tableMandatory);
                valueMap.put("mask", hv.mask);
                valueMap.put("valid_chars", hv.validChars);
                valueMap.put("invalid_chars", hv.invalidChars);
                valueMap.put("charset", hv.charset);
                valueMap.put("type_field", hv.typeField);
                valueMap.put("type_value", hv.typeValue);
                valueMap.put("unitize", hv.unitize);
                valueMap.put("spell_check", hv.spellcheck);
                valueMap.put("field_level", Integer.toString(hv.fieldLevel));
                valueMap.put("is_level_field", hv.is_level_field);
                valueMap.put("field_group", Integer.toString(hv.fieldGroup));
                map.put(e.getKey(), valueMap);
            }
        }
        return map;
    }

    /**
     * Return a HashMap created from a projectMapper.
     * <p>
     * @return Map - format HashMap:  key="1" value=(HashMap) valueMap
     *                                key="2" value=(HashMap) valueMap
     *                                 ...
     *        valueMap format:        key="sequence" value=1
     *                                key="field_type" value=name
     *                                 ...
     */
    public Map getMapOfChanges(Map oldMap) {
        HashMap map = new HashMap();
        ProjectMapper pm = new ProjectMapper(oldMap);

        HashValue hv = null;
        HashValue pmHv = null;

        //Log.print("(ProjectMapper.getMapOfChanges) -- enter -- keysize=" + key.size());

        int i = 0;
        int j = 0;
        while (i < key.size() && j < pm.key.size()) {
            hv = getHashValue(i);
            pmHv = pm.getHashValue(j);
            if (hv == null && pmHv == null) {
                //Log.print("(ProjectMapper.getMapOfChanges) all things null " + i + "/" + j);
                i++;
                j++;
            // no data for this sequence
            } else if (hv == null || hv.sequence > pmHv.sequence) {
                // deleted field
                //Log.print("(ProjectMapper).getMapOfChanges delete==>" + pmHv.sequence);
                map.put(String.valueOf(pmHv.sequence), null);
                i++;
            } else if (pmHv == null || hv.sequence < pmHv.sequence) {
                // added field
                //Log.print("(ProjectMapper).getMapOfChanges add==>" + pmHv.sequence);
                map = putNewMap(hv, map);
                j++;
            } else if (!hv.fieldName.equals(pmHv.fieldName) || !hv.fieldType.equals(pmHv.fieldType) || hv.fieldSize != pmHv.fieldSize || hv.minimumSize != pmHv.minimumSize || !hv.repeated.equals(pmHv.repeated) || !hv.required.equals(pmHv.required) || !hv.defaultValue.equals(pmHv.defaultValue) || !hv.minValue.equals(pmHv.minValue) || !hv.maxValue.equals(pmHv.maxValue) || hv.tablespecId != pmHv.tablespecId || (!pmHv.tableMandatory.equals("") // can be blank or No
                    && !hv.tableMandatory.equals(pmHv.tableMandatory)) || !hv.mask.equals(pmHv.mask) || !hv.validChars.equals(pmHv.validChars) || !hv.invalidChars.equals(pmHv.invalidChars) || !hv.charset.equals(pmHv.charset) || !hv.typeField.equals(pmHv.typeField) || !hv.typeValue.equals(pmHv.typeValue) || !hv.unitize.equals(pmHv.unitize) || !hv.spellcheck.equals(pmHv.spellcheck) || hv.fieldLevel != pmHv.fieldLevel || !hv.is_level_field.equals(pmHv.is_level_field)) {
                printHashValue("new: ", hv);
                printHashValue("old: ", pmHv);
                map = putNewMap(hv, map);
                i++;
                j++;
            } else {
                // all things equal
                i++;
                j++;
            }
        }
        for (; i < key.size(); i++) {
            // process added records at end of key arrayList
            hv = getHashValue(i);
            map = putNewMap(hv, map);
        }
        return map;
    }

    private HashMap putNewMap(HashValue hv, HashMap map) {
        //Log.print("(ProjectMapper).putNewMap " + hv.sequence + "/" + hv.fieldName);
        HashMap valueMap = new HashMap();
        valueMap.put("sequence", String.valueOf(hv.sequence));
        valueMap.put("field_name", hv.fieldName);
        if (!hv.oldName.equals("")) {
            valueMap.put("old_name", hv.oldName);
        }
        valueMap.put("field_type", hv.fieldType);
        valueMap.put("field_size", String.valueOf(hv.fieldSize));
        valueMap.put("minimum_size", String.valueOf(hv.minimumSize));
        valueMap.put("repeated", hv.repeated);
        valueMap.put("required", hv.required);
        valueMap.put("default_value", hv.defaultValue);
        valueMap.put("min_value", hv.minValue);
        valueMap.put("max_value", hv.maxValue);
        valueMap.put("tablespec_id", Integer.toString(hv.tablespecId));
        valueMap.put("table_mandatory", hv.tableMandatory);
        valueMap.put("mask", hv.mask);
        valueMap.put("valid_chars", hv.validChars);
        valueMap.put("invalid_chars", hv.invalidChars);
        valueMap.put("charset", hv.charset);
        valueMap.put("type_field", hv.typeField);
        valueMap.put("type_value", hv.typeValue);
        valueMap.put("unitize", hv.unitize);
        valueMap.put("spell_check", hv.spellcheck);
        valueMap.put("field_level", Integer.toString(hv.fieldLevel));
        valueMap.put("is_level_field", hv.is_level_field);
        valueMap.put("field_group", Integer.toString(hv.fieldGroup));
        map.put(String.valueOf(hv.sequence), valueMap);
        return map;
    }

    /**
     * Return a boolean indicating whether the map has changed.
     * <p>
     * @return Map - format HashMap:  key="Recip" value=(HashMap) valueMap
     *                                key="Copyee" value=(HashMap) valueMap
     *                                 ...
     *        valueMap format:        key="sequence" value=1
     *                                key="field_type" value=name
     *                                 ...
     */
    public boolean isScreenChanged(Map oldMap) {
        ProjectMapper pm = new ProjectMapper(oldMap);

        HashValue hv = null;
        HashValue pmHv = null;

        if (key.size() != pm.key.size()) {
            return true;
        }

        for (int i = 0; i < key.size(); i++) {
            hv = getHashValue(i);
            pmHv = pm.getHashValue(i);
            if (hv == null || pmHv == null) {
                return true; // name changed or something

            }
            if (!hv.fieldName.equals(pmHv.fieldName) || !hv.fieldType.equals(pmHv.fieldType) 
                    || hv.fieldSize != pmHv.fieldSize || hv.minimumSize != pmHv.minimumSize 
                    || !hv.repeated.equals(pmHv.repeated) || !hv.required.equals(pmHv.required) 
                    || !hv.defaultValue.equals(pmHv.defaultValue) || !hv.minValue.equals(pmHv.minValue) 
                    || !hv.maxValue.equals(pmHv.maxValue) || hv.tablespecId != pmHv.tablespecId 
                    || (!pmHv.tableMandatory.equals("") // can be blank or No
                    && !hv.tableMandatory.equals(pmHv.tableMandatory)) 
                    || !hv.mask.equals(pmHv.mask) || !hv.validChars.equals(pmHv.validChars) 
                    || !hv.invalidChars.equals(pmHv.invalidChars) || !hv.charset.equals(pmHv.charset) 
                    || !hv.typeField.equals(pmHv.typeField) || !hv.typeValue.equals(pmHv.typeValue) 
                    || !hv.unitize.equals(pmHv.unitize) || !hv.spellcheck.equals(pmHv.spellcheck) 
                    || hv.fieldLevel != pmHv.fieldLevel || !hv.is_level_field.equals(pmHv.is_level_field)) {
                printHashValue("new: ", hv);
                printHashValue("old: ", pmHv);
                return true;
            }
        }
        return false;
    }

    private void printHashValue(String str, HashValue hv) {
        Log.print("(ProjectMapper) " + str + hv.fieldName + "/" + hv.fieldType + "/" + hv.fieldSize + "/" 
                + hv.minimumSize + "/" + hv.repeated + "/" + hv.required + "/" + hv.defaultValue + "/" 
                + hv.minValue + "/" + hv.maxValue + "/" + hv.tablespecId + "/" + hv.tableMandatory + "/" 
                + hv.mask + "/" + hv.validChars + "/" + hv.invalidChars + "/" + hv.charset + "/" + hv.typeField 
                + "/" + hv.typeValue + "/" + hv.unitize + "/" + hv.spellcheck + "/" + hv.fieldLevel + "/" 
                + hv.is_level_field);

    }

    /**
     * Get the HashValue representation of a projectfields field.
     * @param fieldName - the name of the field_name in projectfields to retrieve
     * @return HashValue - the HashValue for the requested fieldName
     */
    public HashValue getHashValue(String fieldName) {
        //Log.print("(ProjectMapper).getHashValue " + fieldName);
        return (HashValue) get(fieldName);
    }

    /**
     * Get the HashValue representation of a projectfields field.
     * @param seq - the sequence of the field_name in projectfields to retrieve
     * @return HashValue - the HashValue for the requested fieldName
     */
    public HashValue getHashValue(int seq) {
        String fieldName = (String) key.get(seq);
        //Log.print("(ProjectMapper).getHashValue " + seq + "/" + fieldName);
        return (HashValue) get(fieldName);
    }

    /**
     * Create a new HashValue and add to this hashMap.  Increase the
     * sequence of the fields following the new field.
     * @param sequence - the sequence number of the new hashValue, relative to 1
     */
    public ProjectMapper addHashValue(int sequence) {
        HashValue hv = new HashValue(sequence, getUniqueName("Field"), /* old_name */ "", "text",
                /*field_size*/ 40, /*minimumSize*/ 0, /*repeated*/ "Yes", /*required*/ "Yes",
                /*defaultValue*/ "", /*minValue*/ "", /*maxValue*/ "", /*tablespecId*/ 0, 
                /*tableMandatory*/ "No", /*mask*/ "", /*validChars*/ "", /*invalidChars*/ "",
                /*charset*/ "", /*typeField*/ "", /*typeValue*/ "", /*unitize*/ "Yes", 
                /*spellcheck*/ "Yes", /*field_level*/ 0, /*is_field_level*/ "No", /*field_group*/ 0);
        put(hv.fieldName, hv);
        //Log.print("(ProjectMapper).addHashValue adding " + hv.fieldName + " at " + hv.sequence);
        // add the new fieldname to the key list
        int index = getFieldIndex(hv.fieldName);
        if (index > -1) {
            key.add(index, hv.fieldName);
        } else {
            key.add(hv.fieldName);
        }

        // adjust sequences following new hv
        for (int i = index + 1; i < key.size(); i++) {
            HashValue workHv = getHashValue(i);
            //Log.print("(ProjectMapper).addHashValue update " + workHv.fieldName + " at " + i);
            workHv.sequence++;
            put(workHv.fieldName, workHv);
        }
        for (int i = 0; i < key.size(); i++) {
            hv = getHashValue(i);
        //Log.print("(ProjectMapper).__ after add __ i=" + i + "/key=" + key.get(i) + "/hv.seq="
        //          + hv.sequence + "/hv.name=" + hv.fieldName);
        }
        return this;
    }

    /**
     * Make a copy of a HashValue and add it to this hashMap.  Increase the
     * sequence of the fields following the new field.
     * @param hv - the HashValue to be copied
     * @see HashValue
     */
    public ProjectMapper cloneHashValue(HashValue hv) {
        HashValue newHv = new HashValue(hv.sequence + 1, getUniqueName(hv.fieldName), "" // oldName
                , hv.fieldType, hv.fieldSize, hv.minimumSize, hv.repeated, hv.required, hv.defaultValue,
                hv.minValue, hv.maxValue, hv.tablespecId, hv.tableMandatory, hv.mask, hv.validChars,
                hv.invalidChars, hv.charset, hv.typeField, hv.typeValue, hv.unitize, hv.spellcheck,
                hv.fieldLevel, hv.is_level_field, hv.fieldGroup);
        put(newHv.fieldName, newHv);
        // add the new fieldname to the key list
        //Log.print("(ProjectMapper).clone  hv/newHv " + hv.sequence + "/" + newHv.sequence);
        //Log.print("(ProjectMapper).cloneHashValue add " + newHv.fieldName + " in seq " + (hv.sequence + 1));
        int index = getFieldIndex(hv.fieldName);
        key.add(index + 1, newHv.fieldName);

        // adjust sequence
        for (int i = index + 2; i < key.size(); i++) {
            HashValue workHv = getHashValue(i);
            //Log.print("(ProjectMapper).cloneHashValue update " + workHv.fieldName + " at " + i);
            workHv.sequence++;
            put(workHv.fieldName, workHv);
        }
        return this;
    }

    /**
     * Create a new field_name by appending a number to the end of name, if possible.
     * @param name - the base for the new name
     * @return a new field_name string
     */
    private String getUniqueName(String name) {
        // quick test
        if (!key.contains((String) (name + " 1")) && name.length() <= 38) {
            return (name + " 1");
        }
        // number 1 is taken, so figure out something else
        for (int index = 2; index < 9999; index++) {
            //Log.print("(ProjectMapper).getUniqueName " + index);
            if ((name.length() + Integer.toString(index).length() + 1) <= 40) {
                if (!key.contains((String) (name + " " + Integer.toString(index)))) {
                    return (name + " " + Integer.toString(index));
                }
            } else {
                // name is too long to just append a number
                String str = (name.substring(0, name.length() - Integer.toString(index).length() - 1));
                if (!key.contains(str + " " + Integer.toString(index))) {
                    return str + " " + Integer.toString(index);
                }
            }
            index++;
        }
        return "Name Failure";
    }

    /**
     * Replace this field's HashValue with null in this HashMap.
     * @param hv - the HashValue to remove from the HashMap
     */
    public ProjectMapper removeHashValue(HashValue hv) {
        //Log.print("(ProjectMapper).removeHashValue " + hv.sequence + "/" + hv.fieldName);
        int index = getFieldIndex(hv.fieldName);
        if (index > -1) {
            key.remove(index);
            put(hv.fieldName, null);
        }
        return this;
    }

    /**
     * Move the field at row 'from' to row 'to.'
     * <p>
     * Note that 'from' and 'to' are relative to 0; hv.sequence to 1.
     * @param from - position of the hv to be moved
     * @param to - the target position for the hv
     */
    public ProjectMapper moveHashValue(int from, int to) {
        HashValue hv = getHashValue(from);
        HashValue tohv = getHashValue(to);
        if (to < 0 || to >= key.size()) {
            Log.print("(ProjectMapper).moveHashValue ??? invalid move to: " + to);
            return this;
        }
        int index = getFieldIndex(hv.fieldName);
        if (index != from) {
            Log.print("(ProjectMapper).moveHashValue ??? invalid move from: " + from);
            return this;
        }
        int toSeq = hv.sequence;
        // first, update the current hv sequence
        hv.sequence = tohv.sequence;
        //Log.print("(ProjectMapper).moveHashValue put " + hv.fieldName + " seq " + hv.sequence);
        put(hv.fieldName, hv);

        hv = tohv;
        // then update the 'to' sequence
        hv.sequence = toSeq;
        //Log.print("(ProjectMapper).moveHashValue put " + hv.fieldName + " seq " + hv.sequence);
        put(hv.fieldName, hv);

        // last, swap key positions
        Object fromKey = key.set(from, key.get(to));
        //Log.print("(ProjectMapper).moveHashValue set " + from + " to " + key.get(to));
        key.set(to, fromKey);
        //Log.print("(ProjectMapper).moveHashValue set " + to + " to " + fromKey);
        return this;
    }

    /** 
     * Get the projectfields.field_name for a given index position.
     * @param i - the index position of the field in the key ArrayList
     * @return the fieldName stored in the key ArrayList
     */
    public String getFieldName(int i) {
        //Log.print("(ProjectMapper).getFieldName " + i + "/" + key.get(i));
        return (String) key.get(i);
    }

    /**
     * return the value of the HashValue field requested.
     * @param fieldName - name of the field owning the HashValue 
     * @param name - name of the HashValue field being requested
     * @return the value of fieldName.name being requested in Object format
     */
    public String getFieldValue(HashValue hv, String name) {
        if (name.equals("field_name")) {
            return hv.fieldName;
        } else if (name.equals("field_type")) {
            return hv.fieldType;
        } else if (name.equals("field_size")) {
            return String.valueOf(hv.fieldSize);
        } else if (name.equals("minimum_size")) {
            return String.valueOf(hv.minimumSize);
        } else if (name.equals("repeated")) {
            return hv.repeated;
        } else if (name.equals("unitize")) {
            return hv.unitize;
        } else if (name.equals("spell_check")) {
            return hv.spellcheck;
        } else if (name.equals("required")) {
            return hv.required;
        } else if (name.equals("default_value")) {
            return hv.defaultValue;
        } else if (name.equals("min_value")) {
            return hv.minValue;
        } else if (name.equals("max_value")) {
            return hv.maxValue;
        } else if (name.equals("tablespec_id")) {
            return Integer.toString(hv.tablespecId);
        } else if (name.equals("table_mandatory")) {
            return hv.tableMandatory;
        } else if (name.equals("mask")) {
            return hv.mask;
        } else if (name.equals("valid_chars")) {
            return hv.validChars;
        } else if (name.equals("invalid_chars")) {
            return hv.invalidChars;
        } else if (name.equals("charset")) {
            return hv.charset;
        } else if (name.equals("type_field")) {
            return hv.typeField;
        } else if (name.equals("type_value")) {
            return hv.typeValue;
        } else if (name.equals("field_level")) {
            return Integer.toString(hv.fieldLevel);
        } else if (name.equals("is_level_field")) {
            return hv.is_level_field;
        } else if (name.equals("field_group")) {
            return Integer.toString(hv.fieldGroup);
        } else {
            Log.print("(ProjectMapper).getFieldValue ???? returning null ????");
            return null;
        }
    }

    public HashValue putFieldValue(HashValue hv, String name, String value) {
        if (name.equals("field_name")) {
            //Log.print("(ProjectMapper.putFieldValue) " + name + "=" + value + "/" + hv.sequence);
            hv.fieldName = value;
        } else if (name.equals("old_name")) {
            //Log.print("(ProjectMapper.putFieldValue) " + name + "=" + value + "/" + hv.sequence);
            hv.oldName = value;
            // put the new name in key
            int index = getFieldIndex(hv.oldName);
            key.remove(index);
            key.add(index, hv.fieldName);
            remove(value); // will be added with new name, below

        } else if (name.equals("field_type")) {
            hv.fieldType = value;
        } else if (name.equals("field_size")) {
            if (value == null || value.equals("")) {
                hv.fieldSize = 0;
            } else {
                hv.fieldSize = Integer.parseInt(value);
            }
        } else if (name.equals("minimum_size")) {
            if (value == null || value.equals("")) {
                hv.minimumSize = 0;
            } else {
                hv.minimumSize = Integer.parseInt(value);
            }
        } else if (name.equals("repeated")) {
            hv.repeated = value;
        } else if (name.equals("unitize")) {
            hv.unitize = value;
        } else if (name.equals("spell_check")) {
            hv.spellcheck = value;
        } else if (name.equals("required")) {
            hv.required = value;
        } else if (name.equals("default_value")) {
            hv.defaultValue = value;
        } else if (name.equals("min_value")) {
            hv.minValue = value;
        } else if (name.equals("max_value")) {
            hv.maxValue = value;
        } else if (name.equals("tablespec_id")) {
            hv.tablespecId = Integer.parseInt(value);
        } else if (name.equals("table_mandatory")) {
            hv.tableMandatory = value;
        } else if (name.equals("mask")) {
            hv.mask = value;
        } else if (name.equals("valid_chars")) {
            hv.validChars = value;
        } else if (name.equals("invalid_chars")) {
            hv.invalidChars = value;
        } else if (name.equals("charset")) {
            hv.charset = value;
        } else if (name.equals("type_field")) {
            hv.typeField = value;
        } else if (name.equals("type_value")) {
            hv.typeValue = value;
        } else if (name.equals("field_level")) {
            hv.fieldLevel = Integer.parseInt(value);
        } else if (name.equals("is_field_level")) {
            hv.is_level_field = value;
        } else if (name.equals("field_group")) {
            hv.fieldGroup = Integer.parseInt(value);
        } else {
            Log.print("(ProjectMapper).putFieldValue ???? unknown field name ????");
        }
        put(hv.fieldName, hv);
        return hv;
    }

    /**
     * Get the index into the key ArrayList by looking up the field_name.
     * @param fieldName - string containing the field for which to look
     * @return the index position of fieldName in the key ArrayList
     */
    public int getFieldIndex(String fieldName) {
        return key.indexOf(fieldName);
    }

    /**
     * Get the number of fields in this projectfields table for this project.
     * @return the number of fields in the project.
     */
    public int getSize() {
        //Log.print("(ProjectMapper).getSize " + key.size());
        return key.size();
    }

    /**
     * Store a component in the component ArrayList for use in setting dynamic field
     * properties and focus.
     * @param comp - the dynamic component shown on the coding screen
     */
    public void addComponent(JComponent comp) {
        Log.print("(ProjectMapper.addComponent) " + comp.getName());
        component.add((Object) comp);
    }

    /** Get the component at the given index position.
     * @param i - the index position of the requested component
     * @return the dynamic component stored at position i
     */
    public JComponent getComponent(int i) {
        return (JComponent) component.get(i);
    }

    /** Get the component stored for the dynamic field.
     * @param name - the name (projectfields.field_name) of the requested component
     * @return the dynamic component stored for the given name
     */
    public JComponent getComponent(String name) {
        int i = key.indexOf((Object) name);
        if (i > -1) {
            return (JComponent) component.get(i);
        }
        return null;
    }

    ///**
    // * Store a model in the modelMap ArrayList to use during validateBatch.
    // * @param tablespecId - the name of the table associated with the model
    // * @param model - the model to be stored
    // */
    //public void addModel(int tablespecId, DefaultListModel model) {
    //    //Log.print("(ProjectMapper).addModel " + tablespecId + "/" + model.size());
    //    modelMap.put(tablespecId, (Object)model);
    //}
    /**
     * Get the model stored for the dynamic field.
     * @param tablespecId - the name (projectfields.field_name) of the requested model
     * @return - the model stored for the given name
     */
    public DefaultListModel getModel(int tablespecId) {
        //Log.print("(ProjectMapper).getModel " + tablespecId);
        return (DefaultListModel) modelMap.get(Integer.toString(tablespecId));
    }
}
