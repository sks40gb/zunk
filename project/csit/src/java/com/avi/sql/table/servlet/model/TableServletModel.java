/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.avi.sql.table.servlet.model;

import com.avi.sql.exception.InvalidColumnOption;
import com.avi.sql.exception.InvalidFieldValueException;
import com.avi.sql.table.SColumn;
import com.avi.sql.table.model.TableModel;
import com.avi.sql.table.Type;
import com.avi.util.DateFormatter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Admin
 */

public class TableServletModel extends TableModel{

    private HttpServletRequest request;

    public TableServletModel(){

    }
    
    public TableServletModel(HttpServletRequest request) throws IllegalArgumentException, Exception{
        this.request = request;       
        setFieldsValue();        
    }

    private void setFieldsValue() throws IllegalArgumentException, Exception{
        for(String name : getAttirbuteList()){
            setFieldValueFromRequest(name);
        }
    }

    private void setFieldValueFromRequest(String name) throws IllegalArgumentException,IllegalAccessException, InvalidFieldValueException, InvalidColumnOption, NoSuchFieldException{
        SColumn column = getColumnByFieldName(name);
        String value = request.getParameter(name);        
        if(value == null || value.trim().equals("")){
            return;
        }
        try{
        if (column.getType() == Type.INT) {
            column.setValue(this,Integer.parseInt(value));
        } else if (column.getType() == Type.DOUBLE) {
           column.setValue(this,Double.parseDouble(value));
        } else if (column.getType() == Type.FLOAT) {
            column.setValue(this,Float.parseFloat(value));
        } else if (column.getType() == Type.STRING) {
            column.setValue(this,value);
        } else if (column.getType() == Type.DATE) {
            column.setValue(this, DateFormatter.convertStringToDate(value));
        } else {
            throw new Exception(column.getType() + " : IS NOT SUPPORTED YET");
        }
        }catch(Exception e){
            throw new InvalidFieldValueException(column, value);
        }
    }

    /** get final variables which act as attribute */
   public List<String> getAttirbuteList() throws IllegalArgumentException, IllegalAccessException{
       List<String> list = new ArrayList<String>();
       for(Field f : this.getClass().getFields()){
           if(f.getModifiers() == 25){
               list.add(f.get(this).toString());
           }
       }
       return list;
   }
}
