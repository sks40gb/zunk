/* $Header: /home/common/cvsarea/ibase/dia/src/model/ResultSetTableModel.java,v 1.3.8.1 2006/03/22 20:27:15 nancy Exp $ */

/*
 * ResultTableModel.java
 *
 * Created on October 12, 2003, 3:18 PM
 */

package model;

import common.Log;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


/**
 * View a ResultSet as a table model.
 *
 * @author  Bill
 */
public class ResultSetTableModel extends javax.swing.table.AbstractTableModel {
    
    private ResultSet results;
    private ResultSetMetaData metadata = null;
    private String[] headings = null;
    
    /** Creates a new instance of ResultSetTableModel */
    public ResultSetTableModel(ResultSet results) {
        this(results, null);
    }

    /**
     * Creates a new instance of ResultSetTableModel
     * If headings is not null, the column count and column names
     * are taken from headings; otherwise, the ResultSet's metadata
     * is used.
     * @param results The ResultSet to be mapped.
     * @param headings An array of headings.
     */
    public ResultSetTableModel(ResultSet results, String[] headings) {
        try {
            this.results = results;
            this.headings = headings;
            if (headings == null) {
                // metadata only used for count and headings
                metadata = results.getMetaData();
            }
        } catch (SQLException e) {
            Log.quit(e);
        }
    }

    /**
     * Return the number of headings in the model or the number
     * of columns in the metadata.
     */
    public int getColumnCount() {
        try {
            if (headings != null) {
                return headings.length;
            } else {
                return metadata.getColumnCount();
            }
        } catch (SQLException e) {
            Log.quit(e);
            return 0; // never get here
        }
    }

    /**
     * Return the number of rows in the resultSet.
     */
    public int getRowCount() {
        try {
            results.last();
            return results.getRow();
        } catch (SQLException e) {
            Log.quit(e);
            return 0; // never get here
        }
    }
    
    /**
     * Obtain the value at the given row and column.  If headings
     * were given, column may be >= rowCount, but < dimension
     * of rows from metadata.
     */
    public Object getValueAt(int row, int column) {
        try {
            results.absolute(row + 1);
            return results.getString(column + 1);
        } catch (SQLException e) {
            Log.quit(e);
            return null; // never get here
        }
    }
    
    /**
     * Return the name of the given column from headings, if it
     * exists, otherwise from the metadata.
     */
    public String getColumnName(int column) {
        try {
            if (headings != null) {
                return headings[column];
            } else {
                return metadata.getColumnName(column + 1);
            }
        } catch (SQLException e) {
            Log.quit(e);
            return null; // never get here
        }
    }
}
