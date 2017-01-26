/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avi.sql.table;

import com.avi.sql.table.model.TableModel;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Admin
 */

public class Criteria {
    private TableModel table;
    private List<SColumn> columnList;
    private List<CriteriaType> typeList;
    private List<Object> matchToList;

    public Criteria(TableModel table) {
        this.table = table;
        columnList = new ArrayList<SColumn>();
        typeList = new ArrayList<CriteriaType>();
        matchToList = new ArrayList<Object>();
    }

    public void addCriteria(SColumn column,Object matchTo,CriteriaType type) {        
        columnList.add(column);
        typeList.add(type);
        matchToList.add(matchTo);
    }

    public String createCriteria() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, Exception {
        StringBuffer buffer = new StringBuffer("");
        buffer.append(" WHERE ");
        for (int i = 0; i < columnList.size(); i++) {
            CriteriaType type = typeList.get(i);
            String matchTo = matchToList.get(i) == null ? null : matchToList.get(i).toString();
            SColumn column = columnList.get(i);

            switch (type) {
                case STRING_EQUAL:
                    buffer.append(getColumnWithValueString(column,matchTo, "="));
                    break;
                case EQUAL_TO:
                    buffer.append(getColumnWithValueString(column,matchTo, "="));;
                    break;
                case NOT_EQUAL_TO:
                    buffer.append(getColumnWithValueString(column,matchTo, "!="));;
                    break;
                case GREATER_THAN:
                    buffer.append(getColumnWithValueString(column,matchTo, ">"));;
                    break;
                case GREATER_THAN_EQUAL_TO:
                    buffer.append(getColumnWithValueString(column,matchTo, ">="));;
                    break;
                case LESS_THAN:
                    buffer.append(getColumnWithValueString(column,matchTo, "<"));;
                    break;
                case LESS_THAN_EQAUL_TO:
                    buffer.append(getColumnWithValueString(column,matchTo, "<="));;
                    break;
                case LIKE:
                    buffer.append(getColumnWithValueString(column,matchTo, "%"));;
                    break;
            }
            if(i < (columnList.size()-1)){
                buffer.append(" AND ");
            }
        }
        return buffer.toString();
    }

    private String getColumnWithValueString(SColumn column, String matchTo, String sign) throws NoSuchFieldException, IllegalArgumentException, Exception {
        StringBuffer buffer = new StringBuffer();
        if(sign.equals("%")){
            buffer.append(column.getName() + " LIKE '%" + matchTo + "'");
        }else{
            buffer.append(column.getName() + sign);
            if (column.getType() == Type.STRING) {
                buffer.append("'" + matchTo + "'");
            }else{
               buffer.append(matchTo);
            }
        }
        return buffer.toString();
    }
}
