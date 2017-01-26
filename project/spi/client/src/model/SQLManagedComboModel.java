/* $Header: /home/common/cvsarea/ibase/dia/src/model/SQLManagedComboModel.java,v 1.2 2004/05/20 20:33:28 weaston Exp $ */
package model;

/**
 * A ComboBoxModel backed by the first column of a SQLManagedTable
 * with a named SQL command to be taken from sql_text on the server.
 * The SQL statement should return an id as the first column and
 * the value for the combo box item as the second column.
 */
final public class SQLManagedComboModel extends ManagedComboModel {

    /**
     * Create an SQLManagedComboModel for a named SQL statement,
     * with values from the SQL data.
     */
    public SQLManagedComboModel(String sql) {
        //this(null, sql);
        super(new ManagedTableSorter (0,
                    SQLManagedTableModel.makeInstance(sql)));
    }
    
    public SQLManagedComboModel(String sql,int id) {
        //this(null, sql);
        super(new ManagedTableSorter (0,
                    SQLManagedTableModel.makeInstance(sql,id)));
    }

    ///**
    // * Create an SQLManagedComboModel for a named SQL statement,
    // * with a given first element prepended to the SQL data.
    // */
    //public SQLManagedComboModel(String firstValue, String sql) {
    //    super(firstValue, 
    //            new ManagedTableSorter (0,
    //                SQLManagedTableModel.makeInstance(sql)));
    //}
}
