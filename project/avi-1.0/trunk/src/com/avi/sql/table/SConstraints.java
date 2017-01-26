/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.avi.sql.table;

import com.avi.sql.table.annotation.Column;
import com.avi.sql.table.annotation.ForeignKey;
import com.avi.sql.table.annotation.Table;
import java.lang.reflect.Field;

/**
 *
 * @author sunil
 */
public class SConstraints {

    private String referenceTable;
    private String referenceColumn;
    private String columnName;
    private String onDelete;
    private String onUpdate;

    public SConstraints(String columnName, ForeignKey fk) throws NoSuchFieldException {
        if (fk != null) {
            this.columnName = columnName;
            setReferenceTable(fk.table()); 
            setReferenceColumn(fk);            
            onDelete = fk.onDelete().equals(ForeignKey.NO_ACTION) ? null : fk.onDelete();
            onUpdate = fk.onUpdate().equals(ForeignKey.NO_ACTION) ? null : fk.onUpdate();
        }
    }

    public void setReferenceTable(Class tableClass) {
        Table c = (Table) tableClass.getAnnotation(Table.class);
        if (c != null) {
            if (c.name().equals(Column.DEFAULT_NAME)) {
                referenceTable = this.getClass().getSimpleName();
            } else {
                referenceTable = c.name();
            }
        } else {
            referenceTable = this.getClass().getSimpleName();
        }
    }

    public void setReferenceColumn(ForeignKey fk) throws NoSuchFieldException {       
        Field f = fk.table().getDeclaredField(fk.column());
        if (f != null) {
            Column c = f.getAnnotation(Column.class);
            if (c.name().equals(Column.DEFAULT_NAME)) {
                referenceColumn = f.getName();
            } else {
                referenceColumn = c.name();
            }
        } 
    }

    public String getOnDelete() {
        return onDelete;
    }

    public String getOnUpdate() {
        return onUpdate;
    }

    public String getReferenceTable() {
        return referenceTable;
    }

    public String getReferenceColumn() {
        return referenceColumn;
    }
   
    /*
    INDEX (customer_id),
    FOREIGN KEY (customer_id)
    REFERENCES customer(id) ON UPDATE CASCADE ON DELETE RESTRICT,
     */
    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer(" INDEX(");
        buffer.append(columnName);
        buffer.append("),");
        buffer.append("FOREIGN KEY (");
        buffer.append(columnName);
        buffer.append(")");
        buffer.append(" REFERENCES ");
        buffer.append(referenceTable);
        buffer.append("(");
        buffer.append(referenceColumn);
        buffer.append(")");
        if (getOnDelete() != null) {
            buffer.append(" ON UPDATE ");
            buffer.append(getOnDelete());
        }
        if (getOnUpdate() != null) {
            buffer.append(" ON DELETE ");
            buffer.append(getOnUpdate());
        }
        //buffer.append(",");
        return buffer.toString();
    }
}