/* $Header: /home/common/cvsarea/ibase/dia/src/model/QueryComboModel.java,v 1.13.4.1 2005/12/01 18:27:29 nancy Exp $ */
/*
 * QueryComboModel.java
 *
 * Created on January 5, 2004, 3:45 PM
 */

package model;

import client.ClientTask;
import client.ClientThread;
import client.TaskExecuteQuery;
import common.Log;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * Get and view a ResultSet as a combo model.
 *
 * @author Nancy
 */
public class QueryComboModel extends javax.swing.DefaultComboBoxModel {
    
    public ResultSet resultSet;
    private ResultSetMetaData metadata = null;
    public ArrayList ids = new ArrayList();

    private boolean required = false;
    public String value = "";
    private String leadValue = "";
    
    /** Creates a new instance of QueryComboModel */
    public QueryComboModel(String query) {
        this(query, false, (String[]) null, "", "");
    }
    public QueryComboModel(String query, boolean required) {
        this(query, required, (String[]) null, "", "");
    }
    public QueryComboModel(String query, boolean required, String leadValue) {
        this(query, required, (String[]) null, "", leadValue);
    }
    public QueryComboModel(String query, boolean required, String[] key, String value) {
        this(query, required, key, value, "");
    }

    /**
     * @param query - the query in sql_text.txt to use to retrieve the combo data
     * @param required - is the combo a required field?  If not, insert a blank first line
     * @param key - pass this value to the query
     * @param value - after loading the data, select this item
     */
    public QueryComboModel(String query, boolean required, String[] key, String value
                           , String leadValue) {
        super();
        this.required = required;
        this.value = value;
        this.leadValue = leadValue;

        // execute the query provided by the user
        final ClientTask task;
        //if (key == null
        //    || key.length < 1) {
        //    task = new TaskExecuteQuery(query);
        //} else {
        //    task = new TaskExecuteQuery(query, key);
        //}
        task = new TaskExecuteQuery(query, key);
        task.setCallback(new Runnable() {
                public void run() {
                    getDataEntry((ResultSet)task.getResult());
                }
            });
        boolean ok = task.enqueue();
    }

    /**
     * Load the combo from the resultSet.
     * @param queryResult - ResultSet returned by ClientTask
     */
    public void getDataEntry(ResultSet queryResult) {
        try {
            this.resultSet = queryResult;
            metadata = resultSet.getMetaData();
            if (! required) {
                // add blank so user is not forced to select a value
                ids.add("-1");
                addElement("");
            }
            if (! leadValue.equals("")) {
                // add the lead value before the data
                ids.add("0");
                addElement(leadValue);
            }
            while (resultSet.next()) {
                if (metadata.getColumnCount() > 1) {
                    ids.add(resultSet.getString(1));
                    addElement(resultSet.getString(2));
                } else {
                    addElement(resultSet.getString(1));
                }
            }
            //Log.print("(QueryComboModel.getDataEntry) setSelectedItem(" + value + ")");
            setSelectedItem(value);
        } catch (SQLException e) {
            Log.quit(e);
        }
    }

    /**
     * Returns the id of the selected item.
     * @return The selected item's id or -1 if there is no selection.
     */
    public int getSelectedId() {
        Object object = super.getSelectedItem();
        int i = super.getIndexOf(object);
        if (i > -1 && i < ids.size()) {
            return Integer.parseInt((String)ids.get(i));
        }
        return 0;
    }

    /**
     * Returns the id of the given index.
     * @return The selected item's id.
     */
    public int getIdAt(int row) {
        return Integer.parseInt((String)ids.get(row));
    }

    /**
     * Returns the index of the given id, which corresponds to the model's comboBox.
     * @param id - The id to look for in the ids ArrayList.
     * @return the index of the given id.
     */
    public int indexOf(int id) {
        return ids.indexOf(Integer.toString(id));
    }

   
}
