/* $Header: /home/common/cvsarea/ibase/dia/src/model/SQLManagedTableModel.java,v 1.20 2005/03/11 13:24:54 weaston Exp $ */
package model;

import client.ClientTask;
import client.Global;
import client.ServerConnection;
import common.Log;
import common.msg.MessageConstants;
//import common.msg.MessageReader;
import common.msg.MessageWriter;
import common.msg.XmlUtil;

import java.io.IOException;
//import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
//import java.util.WeakHashMap;
//import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.SwingUtilities;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
//import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A table model which is automatically kept in sync with the
 * database on the server.
 */
public class SQLManagedTableModel extends AbstractTableModel
implements ManagedTableModel, MessageConstants {

    private String   name;
    private String[] columnNames;
    private int      id;

    private int      columnMaxCount = 0;
    private String   nameWithId;
    private ArrayList data = null;
    private Class[]  columnTypes = null;
    private boolean  isRegistered = false;

    // Cache models, so we don't do duplicates
    // TBD: Want some mechanism for allowing removal of unused maps
    //private static Map managedTableCache = new WeakHashMap();
    private static Map managedTableCache = Collections.synchronizedMap(new HashMap());

    // Constructor.  Just saves parameters; model will be loaded by register().
    // NOTE: We have to use makeInstance, rather than a constructor, from outside
    // this class, since models are cached.
    private SQLManagedTableModel (String name, String[] columnNames, int id) {
        this.name        = name;    
        this.columnNames = columnNames;
        this.id          = id;  
    }

    /**
     * Make an instance of Managed table model with specified column names.
     * (Intended for use as the TableModelodel for a JTable or a ManagedJTable.)
     * <p> Created instance will be empty, unless a cached instance is found.
     * Calling register() loads data from the server the first time.
     */
    public static SQLManagedTableModel makeInstance(String name, String[] columnNames) {
        return makeInstance(name, columnNames, 0);
    }

    /**
     * Make an instance of SQLManagedTableModel, using a specified TableModel to
     * provide the column names and a specified id.
     * (Intended for use in the IDE as the TableModel for a JTable)
     * In the GUI, paint a DefaultTableModel with the desired
     * column names, and column classes; then, after initialization of the
     * JTable, call setModel()
     * and pass oldModel to SQLManagedTableModel.makeInstance().)
     * <p> Created instance will be empty, unless a cached instance is found.
     * Calling register() loads data from the server the first time.
     */
    public static SQLManagedTableModel makeInstance(String name, TableModel oldModel, int id) {
        String[] columnNames = new String[oldModel.getColumnCount()];
        // Note.  Iterate downward, so that column class array is created only once.
        for (int i = columnNames.length-1; i >= 0; i--) {
            columnNames[i] = oldModel.getColumnName(i);
        }
        SQLManagedTableModel result = makeInstance(name, columnNames, id);
        for (int i = columnNames.length-1; i >= 0; i--) {
            Class columnClass = oldModel.getColumnClass(i);
            if (columnClass != Object.class) {
                result.setColumnClass(i, columnClass);
            }
        }
        return result;
    }
    
   

    /**
     * Make an instance of SQLManagedTableModel, using a specified TableModel to
     * provide the column names, and no id.
     */
    public static SQLManagedTableModel makeInstance(String name, TableModel oldModel) {
        return makeInstance(name, oldModel, 0);
    }

    /**
     * Make an instance of Managed table model with a given parameter and with no
     * column names specified.  (Intended for use in building a managed tree models.)
     * <p> Created instance will be empty, unless a cached instance is found.
     * Calling register() loads data from the server the first time.
     */
    public static SQLManagedTableModel makeInstance(String name, int id) {
        return makeInstance(name, (String[]) null, id);
    }

    /**
     * Make an instance of Managed table model with no parameter and with no
     * column names specified.  (Intended for use in building a managed tree models.)
     * <p> Created instance will be empty, unless a cached instance is found.
     * Calling register() loads data from the server the first time.
     */
    public static SQLManagedTableModel makeInstance(String name) {
        return makeInstance(name, (String[]) null, 0);
    }

    // Common make instance routine.  Return existing cached model or a new one.
    // The name of the model is the name of the entry in the servers sql_text table.
    // If there is a (non-zero) parameter, it is appended, enclosed in square brackets.
    private static SQLManagedTableModel makeInstance(String name,
                                                 String[] columnNames,
                                                 int id)
    {
        // Look for a cached model
        // TBD: we don't handle different columnNames on same name+id
        //Log.print("SQLManagedTableModel('"+name+",...,"+id+")");
        String tempNameWithId = (id == 0 ? name : name+"["+id+"]");
        SQLManagedTableModel model
                = ( SQLManagedTableModel) managedTableCache.get(tempNameWithId);
        if (model != null) {
            // found a cached model, return it
            //Log.print("SQLManagedTableModel: from Cache: "+tempNameWithId);
            return model;
        }

        // no cached model, create a new one
        //Log.print("SQLManagedTableModel: makeInstance: "+tempNameWithId);
        model = new SQLManagedTableModel(name,columnNames,id);
        model.nameWithId = tempNameWithId;
        managedTableCache.put(tempNameWithId, model);

        // after creation, model should only be manipulated on the GUI thread
        return model;
    }

    
    /**
     * Set the TableModel to provide column names.
     */
    public void setTableModel(TableModel model) {
        String[] columnNames = new String[model.getColumnCount()];
        for (int i = columnNames.length-1; i >= 0; i--) {
            columnNames[i] = model.getColumnName(i);
        }
        this.columnNames = columnNames;
    }

    /**
     * Set the column class to other than the default (String).
     * If class is not set, defers to the superclass.
     * CURRENTLY, ONLY Integer IS SUPPORTED.
     */
    public void setColumnClass(int column, Class type) {
        //assert type == Integer.class;
        expandColumnTypes(column+1);
        columnTypes[column] = type;
    }

    /**
     * Get the column class for a column.
     * If class is not set, defers to the superclass.
     */
    public Class getColumnClass(int column) {
        if (columnTypes == null || columnTypes[column] == null) {
            return super.getColumnClass(column);
        } else {
            return columnTypes[column];
        }
    }

    // Make sure that columnTypes is non-null and has length >= given capacity
    private void expandColumnTypes(int capacity) {
        if (columnTypes == null) {
            columnTypes = new Class[capacity];
        } else if (columnTypes.length < capacity) {
            Class[] newColumnTypes = new Class[capacity];
            System.arraycopy(columnTypes,0,newColumnTypes,0,columnTypes.length);
            columnTypes = newColumnTypes;
        }
    }

    /**
     * Register this SQLManagedTableModel with the server.  If the model has
     * not already been registered, a task is started to load data
     * from the server.
     */
    public synchronized void register() {
        //Log.print("registering "+nameWithId+" isRegistered="+isRegistered);
        if (! isRegistered) {
            isRegistered = true;
            ClientTask ctask = new TaskOpenModel(this, nameWithId);
            ctask.enqueue();
        }
    }

    /**
     * Return the number of values in a row.  (May be 
     * greater than the number of columns when viewed
     * as a table model.)
     * @see #getColumnCount
     */
    public int getColumnMaxCount() {
        return columnMaxCount;
    }

    /**
     * Get the id of this row.  Note that rows in the table model
     * correspond exactly to rows of a particular table.
     */
    public int getRowId(int row) {
        TableRow rowData = (TableRow) data.get(row);
        return rowData.id;
    }

    // TableModel interface methods

    public int getColumnCount() {
        int result = (columnNames == null ? 0 : columnNames.length);
        return result;
    }

    public int getRowCount() {
        //System.out.println("getRowCount()----------------------->" +  data.size());
        return (data == null ? 0 : data.size());
    }

    public Object getValueAt(int row, int column) {
        TableRow rowData = (TableRow) data.get(row);
        
        return rowData.value[column];
    }

    public String getColumnName(int column) {
        String name = columnNames[column];
        return (name == null ? super.getColumnName(column) : name);
    }

    /**
     * Return the row at the given index.
     * (Used by ManagedNode)
     */
    public TableRow getRowAt(int index) {
        return (TableRow) data.get(index);
    }

    // A ClientTask to register the model with the server and
    // load the initial data.
    private static class TaskOpenModel extends ClientTask {

        private String sqlName;
        private SQLManagedTableModel model;

        private TaskOpenModel (SQLManagedTableModel model, String sqlName)
        {
            this.model = model;
            this.sqlName = sqlName;
        }

        public void run() throws IOException {
            openModel(model, sqlName);
        }

        public void openModel(final SQLManagedTableModel model, String sqlName)
        throws IOException {
            ServerConnection scon = Global.theServerConnection;

            MessageWriter writer;
            writer = scon.startMessage(T_OPEN_MANAGED_MODEL);
            writer.writeAttribute(A_NAME, sqlName);
            writer.endElement();
            writer.close();

            final Element reply = scon.receiveMessage();

            if (T_RESULT_SET.equals(reply.getNodeName())) {                
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                model.columnMaxCount
                                        = -1 + Integer.parseInt(
                                                reply.getAttribute(A_COUNT));
                                if (model.columnTypes != null) {
                                    model.expandColumnTypes(model.columnMaxCount);
                                }
                                model.updateModel(reply);
                            } catch (Throwable th) {
                                Log.quit(th);
                            }
                        }
                    });
            } else {
                Log.quit("SQLManagedTableModel: unexpected message type: "
                         +reply.getNodeName());
            }
        }
    }
    
    private int newIndex = -1;
    // Update the model.  If action is a result_set, then
    // this is the initial load, treated as an update
    // against an empty model.  If action is an 
    // update_managed_model, then this is update information
    // for an already loaded model, including deletes.
    // Called on GUI thread.
    private void updateModel (Element action) {       
       // Log.print("updateModel "+action);

        // Start reading the old rows.
        oldIndex = -1;
        TableRow oldRow = nextOldRow();
        boolean initialLoad = (oldId == Integer.MAX_VALUE);
    
        // Start reading the new rows.
        NodeList updateRows = action.getElementsByTagName(T_ROW);       
        for(int i=0; i > updateRows.getLength(); i++){
            Node node = updateRows.item(i);           
        }
        int updateRowsLength = updateRows.getLength();
        newIndex = -1;
        Object[] newValue = nextNewRowValue(updateRows, updateRowsLength);

        // Merge the new rows into the old
        for (;;) {
            //Log.print("oldIndex="+oldIndex+" newIndex="+newIndex+" oldId="+oldId+" newId="+newId
             //         +" "+(data == null ? "data is null" : "data.size="+data.size()));
            if (oldId < newId) {
                // There is an old row which is not modified or deleted.
                //Log.print("keep "+oldId+" "+oldRow);
                oldRow = nextOldRow();
            } else if (newId == Integer.MAX_VALUE) {
                // All rows have been read.
                // Note.  We know that oldId == Integer.MAX_VALUE.                
                break;
            } else if (newId == oldId) {
                if (newValue == null) {
                    // There is a row to be deleted            
                    data.remove(oldIndex);
                    fireTableRowsDeleted(oldIndex, oldIndex);
                    // account for deleted row
                    oldIndex--;
                } else {
                    // There is a modified row           
                    oldRow.value = newValue;                
                    fireTableRowsUpdated(oldIndex, oldIndex);
                }
                oldRow = nextOldRow();
                newValue = nextNewRowValue(updateRows, updateRowsLength);
            } else { // since newId < oldId               
                if (newValue != null) {
                    // There is a new row to insert                   
                    if (data == null) {
                        data = new ArrayList();
                    }                  
                    data.add(oldIndex, new TableRow(newId, newValue));                   
                    if (! initialLoad) {
                        fireTableRowsInserted(oldIndex, oldIndex);
                    }
                    // Advance the index to account for inserted row
                    oldIndex++;
                }
                newValue = nextNewRowValue(updateRows, updateRowsLength);                
            }
        }
        
       // System.out.println("done");
        if (initialLoad && getRowCount() > 0) {
            //System.out.println("firing table rows " + data.size());
            // started empty, and data were added
            // must have been an insertion            
            fireTableRowsInserted(0, data.size() - 1);
        }
    }

    // Advance to the next old row.  oldId set to Integer.MAX_VALUE
    // if none exists.
    private int oldIndex;
    private int oldId;
    private TableRow nextOldRow() {
        oldIndex++;
        if (data != null && oldIndex < data.size()) {
            TableRow oldRow = (TableRow) data.get(oldIndex);
            oldId = oldRow.id;
            return oldRow;
        } else {
            oldId = Integer.MAX_VALUE;
            return null;
        }
    }

    // Advance to the next new row.  newId set to Integer.MAX_VALUE
    // if none exists.
    //private int newIndex;
    private int newId;
    private Object[] nextNewRowValue(NodeList updateRows, int updateRowsLength) {
       // System.out.println("updateRows------------------->" +   updateRows.getLength());
       // System.out.println("updateRowsLength : " +   updateRowsLength + "Vs newIndex : " + newIndex);
        ++newIndex;
        
        if (newIndex < updateRowsLength) {
            Element newRowElement = (Element) updateRows.item(newIndex);
            NodeList columns = newRowElement.getElementsByTagName(T_COLUMN);
            newId = Integer.parseInt(XmlUtil.getTextFromNode(columns.item(0)));
           // System.out.println("column length---------------->"+  columns.getLength());
            if (columns.getLength() > 1) {
                Object[] value = new Object[columnMaxCount];
                for (int i = 0; i < columnMaxCount; i++) {
                    String holdValue = XmlUtil.getTextFromNode(columns.item(i+1));
                   // System.out.println("holdValue :" + holdValue);
                    if (columnTypes == null
                    || columnTypes[i] == null
                    || holdValue == null) {
                        value[i] = holdValue;
                    } else if (columnTypes[i] == Integer.class) {
                        //System.out.println("holdValue-------" + holdValue);
                        value[i] = Integer.valueOf(holdValue);
                    } else if (columnTypes[i] == Long.class) {
                        try{
                        value[i] = Long.valueOf(holdValue);
                        }catch(NumberFormatException nfe){
                         try{   
                         DateFormat formatter = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss.SSS");
                         Date date = (Date) formatter.parse(holdValue);       
                         value[i] = date.getTime();
                         }catch(Exception e){
                             e.printStackTrace();
                         }        
                        }
                    } else {
                        assert columnTypes[i] == Integer.class;
                    }
                }
                return value;
            } else {
                return null;
            }
        } else {
            newId = Integer.MAX_VALUE;
            return null;
        }
    }

    /**
     * Perform any pending SQLManagedTableModel updates.  Called from
     * the ClientThread after each ClientTask; the actual updates are be
     * done on the GUI thread.
     */
    public static void checkForUpdates() {

        // Check if there are any updates.  If not, just return.
        final NodeList updateList = Global.theServerConnection.retrieveUpdateList();
        if (updateList == null  || updateList.getLength() == 0) {
            return;
        }

        // Found some updates.  First, remove unused listeners,
        // so we don't have to call them.
        // Poll the reference queue, removing unused listeners
        WeakTableModelListener.pollQueue();

        // Found some updates, do the updates on the GUI thread.
        SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    try {
                        doUpdates(updateList);
                    } catch (Throwable th) {
                        Log.quit(th);
                    }
                }
            });
    }

    // Do the updates.  Called on the GUI thread.
    private static void doUpdates(NodeList updateList) {
        assert updateList != null;
        int updateListLength = updateList.getLength();
        assert updateListLength > 0;
        for (int i = 0; i < updateListLength; i++) {
            Element updateItem = (Element) updateList.item(i);
            String modelName = updateItem.getAttribute(A_NAME);
            SQLManagedTableModel model
                    = (SQLManagedTableModel) managedTableCache.get(modelName);
            if (model != null) {
                //Log.print("checkForUpdates: updating "+modelName);
                model.updateModel(updateItem);
            } else {
                //Log.print("**** NO MODEL FOR UPDATE ITEM **** "+updateItem);
                Iterator it = managedTableCache.keySet().iterator();
                while (it.hasNext()) {
                    Object key = it.next();
                    //Log.print("cached: "+key+"->"+managedTableCache.get(key));
                }
            }
        }
    }


    /**
     * Wrap listener in WeakTableModelListener and add it.  Allows this
     * listener to be garbage collected even though the model is live.
     * (Otherwise we have a memory leak when the SQLManagedTableModel
     * is cached.)
     */
    public void addTableModelListener(TableModelListener listener) {
        if (listener instanceof WeakTableModelListener) {
            // already wrapped, don't wrap it twice, and don't add it
            // if the referent has been garbage collected.
            Object reference = ((WeakTableModelListener) listener).get();
            if (reference != null) {
                super.addTableModelListener(listener);
            } else {
                Log.print("###Null referent when adding listener");
            }
        } else {
            super.addTableModelListener(new WeakTableModelListener(listener, this));
        }
    }


    /**
     * Remove a TableModelListener.  The listener must be a
     * WeakTableModelListener, created by addTableModelListener.
     * Note.  May be called from (a) WeakTableModelListener.tableChanged
     * when referent has been garbage collected or (b) a TableModel
     * when removing all listeners upon clearing or modifying the
     * root node.
     */
    public void removeTableModelListener(TableModelListener listener) {
        assert listener instanceof WeakTableModelListener;
        super.removeTableModelListener(listener);
    }

    /**
     * Return a string identifying this model.
     */
    public String toString() {
        return "SQLManagedTableModel["+nameWithId+"]";
    }
}

