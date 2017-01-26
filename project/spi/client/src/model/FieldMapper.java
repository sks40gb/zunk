/* $Header: /home/common/cvsarea/ibase/dia/src/model/FieldMapper.java,v 1.17.2.5 2006/05/10 14:25:11 nancy Exp $ */

package model;

import beans.IbaseTextField;
import beans.LComboBox;
import beans.LField;
import beans.LFormattedTextField;
import beans.LGridBag;
import common.CodingData;
import common.Log;
import common.edit.ProjectMapper;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 * FieldMapper is a container for the viewer coding fields.  It provides
 * convience classes for moving data to and from the screen fields and
 * manipulating the fields as a group, e.g. clearing all fields.
 */
public class FieldMapper {

    /**
     * list of FieldItems for the defined fields
     * contains every field name and value
     */
    protected ArrayList theList;

    private String source ="";
    private Map fieldValuesMap = null;
    private Map fieldSavedValuesMap = null;
    private ProjectMapper projMapper = null;

    protected class FieldItem{
        protected String fieldname;
        protected LField field;
        
        // The position of the component in dynamicPane for use in
        // retreiving its label during document level processing.
        protected int position;
        protected boolean checkbox;

        FieldItem (String fieldname, LField field, int position, boolean checkbox) {
            this.fieldname = fieldname;
            this.field = field;
            this.position = position;
            this.checkbox = checkbox;
        }
    }

    /**
     * Create an instance of FieldMapper and initialize <code>theList</code>.
     */
    public FieldMapper () {
        theList = new ArrayList();
    }

    /**
     * Add a new screen field.
     */
    public void add(String fieldname, LField field, int position, boolean checkbox) {
        theList.add(new FieldItem(fieldname, field, position, checkbox));
    }

    /** Clear all screen fields.  Checkboxes are
     *  deselected and disabled.
     */
    public void clearScreen(String treatment_level) {
        ProjectMapper.HashValue hv;
        for (int i = 0; i < theList.size(); i++) {
            FieldItem item = (FieldItem) theList.get(i);
            hv = projMapper.getHashValue(item.fieldname);
            if(((hv.fieldLevel == 1 & treatment_level.equals("L1"))||(hv.fieldLevel == 0 & treatment_level.equals("L2"))) && !hv.fieldName.equals("General Document Type")){               
                  item.field.clearField();
                  item.field.setChanged(false);
                  JCheckBox checkBox = item.field.getCheckBox();
                  if (checkBox != null) {
                      checkBox.setEnabled(false);
                      checkBox.setSelected(false);
                  }
            }            
        }
    }

    /**
     * Clear change flags on all screen fields.  Note
     * that this method does not affect the checkboxes.
     */
    public void clearScreenChanged() {
        for (int i = 0; i < theList.size(); i++) {
            FieldItem item = (FieldItem) theList.get(i);
            item.field.setChanged(false);
        }
    }

    /**
     * If the screen fields have checkboxes, unselect them
     * all and set specified enabled state.
     * @param flag true to enable all checkboxes; false to
     * disable all checkboxes
     */
    public void setCheckboxUnselectedEnabled(boolean flag) {
        for (int i = 0; i < theList.size(); i++) {
            FieldItem item = (FieldItem) theList.get(i);
            JCheckBox checkBox = item.field.getCheckBox();
            if (checkBox == null) {
                break;
            } else {
                checkBox.setEnabled(flag);
                checkBox.setSelected(false);
            }
        }
    }

    /**
     * Return the size of <code>theList</code>, which is the number
     * of fields on the screen
     * @return the size of <code>theList</code>
     */
    public int size() {
        return theList.size();
    }

    /**
     * Return the first screen field with the change flag
     * set, or null.
     */
    public LField firstChanged() {
        for (int i = 0; i < theList.size(); i++) {
            FieldItem item = (FieldItem) theList.get(i);
            //Log.print("firstChanged " + i);
            if (item.field.isChanged())  {
                return item.field;
            }
        }
        return null;
    }

    //// (Not currently used)
    //// if one of the fields has focus, run the input verifier on it
    //// needed because selecting a tab doesn't run the verifier
    //// Note.  we don't check if verifier has already failed, because
    //// we want to see the message again
    //public void runInputVerifier() {
    //    for (int i = 0; i< theList.size(); i++) {
    //        FieldItem item = (FieldItem) theList.get(i);
    //        if (item.field.hasFocus()) {
    //            JComponent comp = (JComponent) item.field;
    //            InputVerifier iv = comp.getInputVerifier();
    //            if (iv != null) {
    //                boolean dummy = iv.shouldYieldFocus(comp);
    //            }
    //            break;
    //        }
    //    }
    //}
    
    //// (Not currently used)
    //public void enableScreen() {
    //    for (int i = 0; i < theList.size(); i++) {
    //        FieldItem item = (FieldItem) theList.get(i);
    //        item.field.setEnabled(true);
    //    }
    //}
    
    //// (Not currently used)
    //public void disableScreen() {
    //    for (int i = 0; i < theList.size(); i++) {
    //        FieldItem item = (FieldItem) theList.get(i);
    //        item.field.setEnabled(false);
    //    }
    //}
    
    /**
     * Fill in a single screen field with coding data from the database.
     * @param fieldname - The screen field to be populated.
     * @param map - A hashMap containing fieldname/value for all coding
     * data fields that have data.
     */
    public void populateScreenField(String fieldname, Map map) {
        if (map != null) {
            for (int i = 0; i < theList.size(); i++) {
                FieldItem item = (FieldItem) theList.get(i);
                if (item.fieldname.equals(fieldname)) {
                    item.field.setText((String)map.get(item.fieldname));
                    //setScreenChanged(true); -- wbe 2004-08-31 only the one field
                    //item.field.setChanged(true);  -- change set by setText
                }
            }
        }
    }
    
    /**
     * Fill in the screen fields with coding data from the database and
     * default values from projectfields.
     * Note: Change flags are set according to whether filling in
     * changes the field.  Change flags should be cleared after
     * populateScreen, if appropriate
     * @param map - A hashMap containing fieldname/value for all coding
     * data fields that have values.
     * @param projectMap - The common.edit.ProjectMapper instance defining the currently opened
     * project.
     */
    public void populateScreen(Map map, ProjectMapper projectMap, int activeGroup,String treatment_level,CodingData codingData) {
        //clearScreen();  -- no longer here
        ProjectMapper.HashValue hv;
        this.projMapper = projectMap;
        for (int i = 0; i < theList.size(); i++) {
            FieldItem item = (FieldItem) theList.get(i);
            hv = projectMap.getHashValue(item.fieldname);
            String value = null;
            if (map != null) {
                value = (String)map.get(item.fieldname);
            }
            if (hv.fieldGroup == activeGroup) {
                item.field.setEnabled(true);
            } else {
                item.field.setEnabled(false);
            }
            
            //to show only the L1 and L2 fields
            if(((hv.fieldLevel == 1 & treatment_level.equals("L1"))||(hv.fieldLevel == 0 & treatment_level.equals("L2"))) && !hv.fieldName.equals("General Document Type")){               
                item.field.setEnabled(true);              
            }else{
                item.field.setEnabled(false);                
            }
            if (value != null) {
              
                String boundary  = codingData.boundaryFlag;
                // wbe 2004-09-02 do this even if value == ""
                // could be blank in field with default
                //Log.print("(FieldMapper) set value " + value);

                if(treatment_level.equals("L1")&& boundary.equals("D")&& hv.l1_information.equals("Source")){                           
                      // source = value;
                       item.field.setText(value);                      
                       fieldValuesMap = new HashMap();
                       if(!item.fieldname.equals("General Document Type")){
                           fieldValuesMap.put(item.fieldname,value);
                       }
                } 
                if(treatment_level.equals("L1")&& boundary.equals("C")&& hv.l1_information.equals("Source")){
                     
                      //source = value;
                    if(fieldValuesMap == null){                       
                        fieldValuesMap = new HashMap();
                        item.field.setText(value); 
                         if(!item.fieldname.equals("General Document Type")){
                             fieldValuesMap.put(item.fieldname,value);
                         }
                    }else{
                        item.field.setText(value);
                    } 
                    
                    // need to check for what purpose this loop is used here.
                    // its creating the problem while filling the data in the
                    // last text field.
                    
//                     for(Object obj : fieldValuesMap.keySet()){                        
//                        if(item.fieldname.equals(obj)){                           
//                            item.field.setText(fieldValuesMap.get(obj).toString());                           
//                        }else{                           
//                           item.field.setText(value);
//                        }
//                     }
                }                 
                if(treatment_level.equals("L1")&& hv.l1_information.equals("Folder")){                    
                     item.field.setText(value);
                }
                if(treatment_level.equals("L2")){                   
                     item.field.setText(value);
                }               

//                Log.print("(FieldMapper).populateScreen item " + i + "/" + item.fieldname + "/" +
//                          value);
            } else {
                // String source ="";
                String boundary  = codingData.boundaryFlag;
                // set default value
                if (hv != null && hv.defaultValue != null
                    && ((String)hv.defaultValue).length() > 0) {
                    //Log.print("(FieldMapper).populateScreen default " + item.fieldname
                    //          + " to " + hv.defaultValue);

                    
                    if(treatment_level.equals("L1")&& boundary.equals("D")&& hv.l1_information.equals("Source")){  
                      source = hv.defaultValue;
                      item.field.setText(source);
                      fieldValuesMap = new HashMap();
                       if(!item.fieldname.equals("General Document Type")){
                           fieldValuesMap.put(item.fieldname,hv.defaultValue);
                       }
                    }else if(treatment_level.equals("L1")&& boundary.equals("C")&& hv.l1_information.equals("Source")){
                         source = hv.defaultValue;
                        //item.field.setText(source);                         
                         if(fieldValuesMap == null){                             
                              item.field.setText(source);
                              fieldValuesMap = new HashMap();
                               if(!item.fieldname.equals("General Document Type")){
                                   fieldValuesMap.put(item.fieldname,hv.defaultValue);
                                 }
                         }else{
                              item.field.setText(source);
                         }
                         for(Object obj : fieldValuesMap.keySet()){                        
                              if(item.fieldname.equals(obj)){
                                  item.field.setText(fieldValuesMap.get(obj).toString());                           
                              }else{
                                 item.field.setText(hv.defaultValue);
                              }
                           }
                    } else if(treatment_level.equals("L1")&& hv.l1_information.equals("Folder")){                         
                         item.field.setText(hv.defaultValue);
                    }else if(treatment_level.equals("L2")){                         
                         item.field.setText(hv.defaultValue);
                    }
                   
                } else {
                    // clear value in field if nothing in map
                    // note that this may set change flag
                    //Log.print("(FieldMapper) set ''");
                    if(treatment_level.equals("L1")&& boundary.equals("D")&& hv.l1_information.equals("Source")){                        
                         item.field.setText("");                         
                    }else if(treatment_level.equals("L1")&& boundary.equals("C")&& hv.l1_information.equals("Source")){                       
                       if(fieldSavedValuesMap != null){
                           item.field.setText(fieldSavedValuesMap.get(item.fieldname).toString());                           
                       }else{
                            item.field.setText("");
                       }                       
                     }
                    else if(treatment_level.equals("L1")||treatment_level.equals("L2")){                      
                       item.field.setText("");
                }
            }
            //Log.print("(FieldMapper.populateScreen) " + item.fieldname + "/" + hv.is_level_field);
            if (hv.is_level_field.equals("Yes")) {
                int j;
                // show or don't show projectfields based on the value for this table
                ManagedTableModel model = ((IbaseTextField)item.field).getProjectModel();
                for (j = 0; j < model.getRowCount(); j++) {
                    if (((String)model.getValueAt(j, 0)).equals(value)) {
                        break;
                    }
                }
                //Log.print("    " + value + "/" + i);
                if (value == null
                    || value.equals("")) {
                    // no value for document level field, so show 0-level fields
                    setFieldsVisible(projectMap, 0);
                } else if (j < model.getRowCount()) {
                    setFieldsVisible(projectMap, Integer.parseInt((String)model.getValueAt(j, 2)));
                }
            }
        }
        //clearScreenChanged();  -- no longer here
    }
}
    
    /**
     * When a document projectfields.level_field_name is edited, go through the projectMap
     * fields to set them visible or not, depending on whether they match the value of level.
     * @param projectMap - values associated with the dynamic fields on the screen
     * @param level - the level of the selected tablevalue
     */
    public void setFieldsVisible(ProjectMapper projectMap, int level) {
        for (int i = 0; i < theList.size(); i++) {
            FieldItem item = (FieldItem) theList.get(i);
            ProjectMapper.HashValue hv = projectMap.getHashValue(item.fieldname);
            Container comp = ((JComponent)item.field).getParent();
            if (! (comp instanceof LGridBag)) {
                comp = comp.getParent();
            }
            if (hv.fieldLevel == 0
                || level == hv.fieldLevel) {
                //Log.print("(FieldMapper.setFieldsVisible) level parent=" + comp
                //          + " COMPONENT=" + comp.getComponent(item.position-1));
                ((JComponent)item.field).setVisible(true);
                ((JComponent)item.field).setEnabled(true);
                // Make the label visible, too.
                comp.getComponent(item.position - 1).setVisible(true);
                
                // make all associated components visible, such as
                // the qa checkbox or the date format label.
                Container cont = ((JComponent)item.field).getParent();
                if (! (cont instanceof LGridBag)) {
                    cont.setVisible(true);
                    if (item.checkbox) {
                        comp.getComponent(item.position + 1).setVisible(true);
                    }
                } else {
                    if (item.checkbox) {
                        comp.getComponent(item.position + 1).setVisible(true);
                    }
                }
            } else {
                //Log.print("(FieldMapper.setFieldsVisible) parent="
                //          + ((JComponent)item.field).getParent() + " root="
                //          + ((JComponent)item.field).getRootPane());
                ((JComponent)item.field).setVisible(false);
                ((JComponent)item.field).setEnabled(false);
                // Make the label invisible, too.
                comp.getComponent(item.position - 1).setVisible(false);
                
                // make all associated components invisible, such as
                // the qa checkbox or the date format label.
                Container cont = ((JComponent)item.field).getParent();
                //Log.print("(FieldMapper.setFieldsVisible) container is " + cont);
                if (! (cont instanceof LGridBag)) {
                    cont.setVisible(false);
                    //Log.print("(FieldMapper.setFieldsVisible) " + item.checkbox
                    //          + "/" + comp.getComponent(item.position + 1));
                    if (item.checkbox) {
                        comp.getComponent(item.position + 1).setVisible(false);
                    }
                } else {
                    if (item.checkbox) {
                        comp.getComponent(item.position + 1).setVisible(false);
                    }
                }
            }
        }
    }

    /**
     * Create map containing all field values from screen.
     */
    public Map populateRow() {
        //Log.print("(FieldMapper).populateRow " + theList.size());
        HashMap theMap = new HashMap();
        for (int i = 0; i < theList.size(); i++) {
            FieldItem item = (FieldItem) theList.get(i);
            if (((LField)item.field) instanceof LFormattedTextField) {
                String mask = ((LFormattedTextField)item.field).getCurrentMask();
                if (((LField)item.field).getText().equals(mask)) {
                    // if the value in the field matches the mask, the
                    // user has entered no text.
                    theMap.put(item.fieldname, "");
                } else {
                    theMap.put(item.fieldname, ((LField)item.field).getText());
                }
            } else {
                theMap.put(item.fieldname, ((LField)item.field).getText());
            }
        }
        if (theMap.size() > 0) {
            fieldSavedValuesMap = new HashMap();
            fieldSavedValuesMap = theMap;
            return theMap;
        } else {
            return null;
        }
    }

    /**
     * Populate a table with error indications.  Called only
     * for QC and QA, so fields should have checkboxes.
     */
    public Map populateErrorRow() {
        HashMap theMap = theMap = new HashMap();

        for (int i = 0; i < theList.size(); i++) {
            FieldItem item = (FieldItem) theList.get(i);
            JCheckBox checkBox = item.field.getCheckBox();
            if (checkBox.isEnabled()) {
                if (theMap == null) {
                    theMap = theMap = new HashMap();
                }
                theMap.put(item.fieldname,
                           (checkBox.isSelected() ? Boolean.TRUE : Boolean.FALSE));
            }
        }
        return theMap;
    }
    
    public Map getFieldErrorType(){
        HashMap theMap = theMap = new HashMap();
      
        for (int i = 0; i < theList.size(); i++) {
            FieldItem item = (FieldItem) theList.get(i);
            JCheckBox checkBox = item.field.getCheckBox();
             JTextField textField = item.field.getTextField();
             String selItem = item.field.getComboBox().getSelectedItem().toString();
             String error_type = null;           
            if(selItem.equalsIgnoreCase("M")){
               error_type = "miscoded";
            }
            else if(selItem.equalsIgnoreCase("U")){
               error_type = "uncoded";
            }
            else if(selItem.equalsIgnoreCase("A")){
               error_type = "added";
            }            
             if (checkBox.isEnabled()) {
                if (theMap == null) {
                    theMap = theMap = new HashMap();
                }
                theMap.put(item.fieldname,error_type);
             }
        }
        return theMap;
    }

    /**
     * Enable and select error checkboxes.  Called only
     * for QC and QA, so fields should have checkboxes.
     */
    public void populateErrorScreen(Map theMap) {

        if (theMap == null) {
            theMap = Collections.EMPTY_MAP;
        }

        for (int i = 0; i < theList.size(); i++) {
            FieldItem item = (FieldItem) theList.get(i);
            JCheckBox checkBox = item.field.getCheckBox();
            Object value = theMap.get(item.fieldname);
            if (value == null) {
                checkBox.setEnabled(false);
                checkBox.setSelected(false);
            } else {
                checkBox.setEnabled(true);
                checkBox.setSelected("Yes".equals(value));
            }
        }
    }
    
    public void populateErrorTypes(Map theMap) {

        if (theMap == null) {
            theMap = Collections.EMPTY_MAP;
        }

        for (int i = 0; i < theList.size(); i++) {
            FieldItem item = (FieldItem) theList.get(i);
            JCheckBox checkBox = item.field.getCheckBox();
            LComboBox combo = item.field.getComboBox();
            Object value = theMap.get(item.fieldname);
            if(!checkBox.isEnabled()){
               combo.setEnabled(false);
            }
            if (value == null) {
                combo.setSelectedItem("U");
            } else {
                if(value.toString().startsWith("m")){
                  combo.setSelectedItem("M");
                }
               else if(value.toString().startsWith("u")){
                  combo.setSelectedItem("U");
               }
               else if(value.toString().startsWith("a")){
                  combo.setSelectedItem("A");
               }
            }
        }
    }
}


