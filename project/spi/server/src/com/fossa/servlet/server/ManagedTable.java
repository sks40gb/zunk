/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 *
 * @author Jude
 */
/**
 * Keep a static Map of <code>ManagedTable</code> (this), keyed by tableName, and
 * an ArrayList of <code>ManagedTable</code> (this) instances.
 * @see Tables
 */
public class ManagedTable {
   
    private static Map tableMap = new HashMap();
    private static ArrayList tableList = new ArrayList();
    private ArrayList targetTables = new ArrayList(); // table to mark
    //private ArrayList targetSQL = new ArrayList();    // SQL to do the mark
    private ArrayList targetIdColumn = new ArrayList();
        // column in this table pointing to target (null for column on target)

    private String tableName;
    private int tableNumber;

    /**
     * Create a <code>ManagedTable</code> instance and add it to the
     * static ArrayList of instances.
     * @param name the table name of this instance
     */
      
    private ManagedTable(String name) {
        this.tableName = name;
        tableList.add(this);
        tableNumber = tableList.size() - 1;
        //Log.print(this.toString());
        
        if (tableNumber > 127) {
            //Log.quit("Too many managed tables");
            System.out.println("Too many managed tables");
        }
    }

    /**
     * For tableName, create an instance of this and add it to the
     * static Map of instances.
     * @param tableName the name of the managed table
     * @return this instance
     */
    public static ManagedTable forName(String tableName) {
        ManagedTable instance = lookup(tableName);
        if (instance == null) {
            instance = new ManagedTable(tableName);
            synchronized (ManagedTable.class) {
                tableMap.put(tableName, instance);
            }
        }
        return instance;
    }

    /**
     * Return the instance of this stored in the Map and keyed
     * by tableName.
     * @param tableName the name of the key into Map
     * @return the instance of this keyed by tableName
     */
    public static ManagedTable lookup(String tableName) {
        
        return (ManagedTable) tableMap.get(tableName);
    }

    /**
     * Return the name of this instance of <code>ManagedTable</code>.
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Return the number that tracks the order in which this instance
     * of ManagedTable was created.
     */
    public int getTableNumber() {
        return tableNumber;
    }

    /**
     * Return the number of <code>ManagedTable</code>s.
     */
    public static int getManagedTableCount() {
        return tableList.size();
    }

    /**
     * Return the <code>ManagedTable</code> instance stored
     * at <code>index</code> in the <code>tableList</code>.
     */
    public static ManagedTable getTable(int index) {
        return (ManagedTable) tableList.get(index);
    }

    /**
     * Return the number of dependencies for this <code>ManagedTable</code>.
     */
    public int getTargetCount() {
        return targetTables.size();
    }

    /**
     * Return the dependency instance stored at <code>index</code>.
     */
    public ManagedTable getTargetTable(int index) {
        return (ManagedTable) targetTables.get(index);
    }

    /**
     * Return the name of the id column in the dependency.
     */
    public String getTargetIdColumn(int index) {
        return (String) targetIdColumn.get(index);
    }

    /**
     * Return a String representation of this class.
     */
    public String toString() {
        return "ManagedTable[" + tableNumber +"," + tableName + "]";
    }

    /**
     * Add a dependency, where this table has a field pointing to a target table.
     * A change to this table causes a change to the target table.
     * Must be explicitly called from Tables.
     */
    public void addDependency(ManagedTable targetTable, String columnName) {
        String targetName = targetTable.tableName;
        targetTables.add(targetTable);   
        targetIdColumn.add(columnName);
        //Log.print(tableName+" added dependency: "+targetName);
        System.out.println(tableName+" added dependency: "+targetName);
    }
}
