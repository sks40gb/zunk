/* $Header: /home/common/cvsarea/ibase/dia/src/model/MailreceivedManagedModel.java,v 1.14.2.2 2006/03/22 20:27:15 nancy Exp $ */
package model;

import common.Log;
import client.ClientTask;
import client.TaskExecuteQuery;
import client.TaskSendMailreceivedData;
import java.sql.ResultSet;
import ui.MailDialog;
import ui.AdminFrame;


import java.util.HashMap;
import java.util.Map;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * Create a <code>ManagedTableSorter</code> sorted by timestamp for the given
 * user.  Set a <code>Runnable</code> in the <code>TableModelListener</code> so
 * new mail's status is changed following complete update of the mail table.
 * This model contains the singleton instance of the mailreceived model for the
 * user.
 */
final public class MailreceivedManagedModel extends ManagedTableSorter {

    private static MailreceivedManagedModel mailreceivedModel = null;

    private static MailDialog mail = null;

    /**
     * Create the instance of the model, initialized with the
     * "MailPanel.mailreceivedTable" key in <code>files.sql_text.txt</code>
     * @param userId the users.users_id of the user retreiving mail
     */
    public MailreceivedManagedModel(int userId) {
        super(3 // timestamp
              ,SQLManagedTableModel.makeInstance("MailPanel.mailreceivedTable", userId));
        ((SQLManagedTableModel)getModel()).setColumnClass(3, Long.class);
        this.addTableModelListener(new TableModelListener() {
            public void tableChanged( final TableModelEvent evt) {
                // invokeLater is used here to delay handling new messages until
                // after the mail table has been updated completely
                SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            final MailreceivedManagedModel model = (MailreceivedManagedModel)evt.getSource();
                            final SQLManagedTableModel sModel = (SQLManagedTableModel)model.getModel();
                            for (int i = 0; i < sModel.getRowCount(); i++) {
                                if (sModel.getValueAt(i, 0).equals("New")) {
                                    //Log.print("(MMM.tableChanged) rowId " 
                                    //          + sModel.getRowId(i) + " " + evt.getSource());
                                    // New mail has arrived, change its status to Unread 
                                    // and show the mailDialog, if necessary.
                                    updateMailreceivedStatus(model);
                                    showMailDialog();
                                    break;
                                }
                            }
                        }
                });
            
            }
        });
        // register moved after addTableListener, otherwise, in principle,
        // we could miss a download that happens before register is
        // executed.  (Probably can't happen, as this probably
        // gets called only on the event thread, but let's be safe.)
        register();
    }

    /**
     * Get the model instance for this user.
     * 
     * (Called from DiaClient.)
     */
    public static void instantiate() {
        if (mailreceivedModel == null) {
            final ClientTask task = new TaskExecuteQuery("DiaClient.get users_id");
            task.setCallback(new Runnable() {
                public void run(){
                    try {
                        ResultSet queryResult = (ResultSet) task.getResult();

                        if (queryResult.next()) {
                            // query returns users_id
                            mailreceivedModel = new MailreceivedManagedModel(queryResult.getInt(1));
                        }
                    } catch (Throwable th) {
                        Log.quit(th);
                    }
                }
            });
            task.enqueue();
        }
    }
    
    /**
     * Get the previously-instantiated model instance for the current user.
     */
    public static MailreceivedManagedModel getInstance() {
        assert mailreceivedModel != null;
        return mailreceivedModel;
    }

    /**
     * If the <code>ui.MailPage</code> is showing on the <code>AdminFrame</code>,
     * do nothing; otherwise, show the singleton instance of <code>MailDialog</code>.
     */
    public void showMailDialog() {
        //Log.print("(MMM.showMailDialog) mailDialog.isShowing "+mail.isShowing()
        //          + "/" + mail.isVisible());

        if (AdminFrame.getInstance() != null) {
            //Log.print("(MMM.showMailDialog) adminFrame.isShowing " + AdminFrame.getInstance().isShowing()
            //          + "/" + AdminFrame.getInstance().isVisible()
            //          + " name " + AdminFrame.getInstance().getVisibleName());
            if (AdminFrame.getInstance().getVisibleName().indexOf("Mail") < 0) {
                // mail tab is NOT showing
                if (mail == null) {
                    mail = MailDialog.getInstance();
                }
                if (! mail.isShowing()) {
                    mail.show();
                }
            }
        } else {
            if (mail == null) {
                mail = MailDialog.getInstance();
            }
            if (! mail.isShowing()) {
                mail.show();
            }
        }
    }

    /**
     * For each "New" status in the model, update the server with a
     * status of "Unread."
     * @param mmmodel - the current mailreceivedManagedModel (this)
     */
    private void updateMailreceivedStatus(MailreceivedManagedModel mmmodel) {
        SQLManagedTableModel model = (SQLManagedTableModel)mmmodel.getModel();
        Map idMap = new HashMap();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (((String)model.getValueAt(i, 0)).equals("New")) {
                idMap.put(Integer.toString(model.getRowId(i)), "Unread");
            }
        }
        if (idMap.size() > 0) {
            final ClientTask task = new TaskSendMailreceivedData(idMap);
            task.enqueue();
        }
    }

    /**
     * Update one status on the server.
     * @param id - the mailreceived.mailreceived_id containing the status to be changed
     * @param status - the new status, "Read," "Replied," "Forwarded"
     */
    public void updateStatus(int id, String status) {
        //Log.print("(MMM.updateStatus) " + id + "/" + status);
        Map idMap = new HashMap();
        idMap.put(Integer.toString(id), status);
        final ClientTask task = new TaskSendMailreceivedData(idMap);
        task.enqueue();
    }

    /**
     * Set the table model so the columns are defined.
     * @param model - tableModel from the one painted in NetBeans
     */
    public void setTableModel(TableModel model) {
        ((SQLManagedTableModel)getModel()).setTableModel(model);
    }
}

