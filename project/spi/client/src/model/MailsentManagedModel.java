/* $Header: /home/common/cvsarea/ibase/dia/src/model/MailsentManagedModel.java,v 1.6.2.1 2006/03/22 20:27:15 nancy Exp $ */
package model;


import javax.swing.table.TableModel;

/**
 * Create a <code>ManagedTableSorter</code> sorted by timestamp for the given
 * user.  This model contains the singleton instance of the mailsent model for the
 * user.
 */
final public class MailsentManagedModel extends ManagedTableSorter {

    private static MailsentManagedModel mailsentModel = null;

    /**
     * Create the instance of the model, initialized with the
     * "MailPanel.mailsentTable" key in <code>files.sql_text.txt</code>
     * @param the users.users_id of the user retreiving mail
     */
    private MailsentManagedModel(int usersId) {
        super(2, // timestamp
                SQLManagedTableModel.makeInstance("MailPanel.mailsentTable", usersId));
        ((SQLManagedTableModel)super.getModel()).setColumnClass(2, Long.class);
        this.register();
    }

    /**
     * Get an instance of mailsentManagedModel.
     * @param usersId the users.users_id of the user retreiving mail
     */
    public static MailsentManagedModel getInstance(int usersId) {
        if (mailsentModel == null) {
            mailsentModel = new MailsentManagedModel(usersId);
        }
        return mailsentModel;
    }

    /**
     * Set the table model so the columns are defined.
     * @param model - tableModel from the one painted in NetBeans
     */
    public void setTableModel(TableModel model) {
        ((SQLManagedTableModel)this.getModel()).setTableModel(model);
    }

}

