/* $Header: /home/common/cvsarea/ibase/dia/src/model/TableRow.java,v 1.4.4.1 2006/03/22 20:27:15 nancy Exp $ */
package model;

/**
 * A structure to store data for a managed table row.  The id is the xxx_id of 
 * the table xxx which is the primary table for this model.
 * value[] holds the TableModel data. 
 */
public class TableRow {
    int       id;
    Object[]  value; // = new Object[columnMaxCount];

    /**
     * Create an instance of TableRow and remember the parameters.
     * @param id the id for this row
     * @param value an Object array of values for this row
     */
    TableRow(int id, Object[] value) {
        this.id = id;
        this.value = value;
    }

    /**
     * Return the id associated with this row
     */
    public int getId() {
        return id;
    }

    /**
     * Return the Object at the given index.
     */
    public Object getValue(int index) {
        //System.out.println("value[index]-------------------->" +  value[index]);
        return value[index];
    }

    /**
     * Returns content of column 0.  (That's the value used,
     * by default, for tree nodes with this Row as userObject.)
     */
    public String toString() {
        return value[0].toString();
    }

    /**
     * Return content for debugging.
     */
    public String dump() {
        StringBuffer buffer = new StringBuffer("TableRow[");
        buffer.append(Integer.toString(id));
        for (int i = 0; i < value.length; i++) {
            buffer.append(",");
            buffer.append(value[i]);
        }
        buffer.append("]");
        return buffer.toString();
    }
}
