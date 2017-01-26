/* $Header: /home/common/cvsarea/ibase/dia/src/beans/AddEditIssueDialog.java,v 1.7.6.1 2006/02/17 13:40:06 nancy Exp $ */
package beans;

import client.ClientTask;
import client.TaskExecuteQuery;
import client.TaskSendPageIssue;

import java.lang.Integer;

import com.lexpar.util.Log;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Called from beans.AddEditIssue, this dialog allows the user to add and edit the
 * pageissue data; then sends it to the server via client.TaskSendPageIssue to be
 * written to the pageissue table.  Pageissue data is exported to an IS line in the LFP.
 * 
 * @author  Nancy
 * 
 * @see client.TaskSendPageIssue
 * @see server.Handler_pageissue
 */
final public class AddEditIssueDialog extends SelectDialog {

    private LTextField text = new LTextField(40);
    private String originalText = "";
    private int pageId;
    private int sequence;

    /**
     * Creates new jdialog AddEditIssueDialog.
     * @param parent the component to use in positioning this dialog
     * @param pageId page.page_id of the page so associate with this issue
     * @param sequence the position of this issue in the pageissue table
     * for the given page
     */
    public AddEditIssueDialog(Component parent, int pageId, int sequence) {
        super(parent, true); // true to add buttons to dialog

        this.pageId = pageId;
        this.sequence = sequence;
        //Log.print("(IssuesDialog)page/sequence " + pageId + "/" + sequence);
        setTitle("Issue Line");
        okButton.addActionListener(buttonListener);
        text.setFocusable(true);
        addControls();
        if (sequence > 0) {
            getIssueData();
        }
    }

    private void addControls() {
        javax.swing.JPanel otherGroup = new javax.swing.JPanel();
        otherGroup.setFocusable(false);
        otherGroup.setLayout(new BorderLayout());
        otherGroup.add(text);
        text.setDocument(new PlainDocument() {

            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (str == null) {
                    return;
                }
                super.insertString(offs, str, a);
                okSetEnabled(true);
            }

            public void remove(int offs, int len)
                    throws BadLocationException {
                super.remove(offs, len);
                if (text.getText().length() < 1) {
                    okSetEnabled(false); // must delete from the main dialog

                } else {
                    okSetEnabled(true);
                }
            }
        });

        add(otherGroup);
        add(new JLabel(" "));
        text.setText(originalText);
        text.requestFocus();
        pack();
    }

    /**
     * Call ClientTask to enqueue a task to get the existing IS text for the given page.
     * The data is returned in a ResultSet.
     */
    private void getIssueData() {
        //Log.print("(IssueDialog.getIssueData) " + pageId);
        final ClientTask task = new TaskExecuteQuery("pageissue select one", Integer.toString(pageId), Integer.toString(sequence));
        task.setCallback(new Runnable() {

            public void run() {
                getIssueDataEntry((ResultSet) task.getResult());
            }
        });
        boolean ok = task.enqueue(this);
    }

    /**
     * Retrieve pageissue from server.
     * @param queryResult - ResultSet containing the issue_name
     * @see getIssueData
     */
    private void getIssueDataEntry(ResultSet queryResult) {
        try {
            if (queryResult.next()) {
                text.setText(queryResult.getString(1));
                originalText = text.getText();
            }
        } catch (SQLException e) {
            Log.quit(e);
        }
    }
    /**
     * On click of okButton, save the text entered by the user to the server
     * and close this dialog.
     */
    protected ActionListener buttonListener = new ActionListener() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                Object source = A.getSource();
                if (source == okButton) {
                    //Log.print("(IssueDialog.getSelection) ok " + text.getText()
                    //          + "/" + originalText + "/" + pageId);
                    if (!((String) text.getText()).equals(originalText)) {
                        final ClientTask task;
                        if (sequence > 0) {
                            // edit
                            task = new TaskSendPageIssue(pageId, sequence, text.getText());
                        } else {
                            // add
                            task = new TaskSendPageIssue(pageId, 0, text.getText());
                        }
                        boolean ok = task.enqueue(AddEditIssueDialog.this);
                    }
                }
                setVisible(false);
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    };

    /**
     * Not currently used.
     */
    public Object[] getSelection() {
        return null;
    }

    /**
     * Not currently used.
     */
    public Object[][] getSelections() {
        return null;
    }

    /**
     * Not currently used.
     */
    public ListSelectionModel getSelectionModel() {
        return null;
    }
}
