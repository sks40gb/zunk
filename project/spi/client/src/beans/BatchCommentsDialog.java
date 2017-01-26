/* $Header: /home/common/cvsarea/ibase/dia/src/beans/BatchCommentsDialog.java,v 1.9.6.2 2006/02/22 20:05:50 nancy Exp $ */
package beans;

import client.ClientTask;
import client.TaskExecuteQuery;
import client.TaskExecuteUpdate;
import com.lexpar.util.Log;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Called from ui.BatchingPage and ui.SplitPaneViewer to show and update batch comments.
 */
final public class BatchCommentsDialog extends JDialog {

    private JPanel selectPanel = new javax.swing.JPanel();
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    private LTextArea comment = new LTextArea(12, 50);
    private String originalComment = "";
    private boolean showIfNoComments;
    private boolean result = false;

    /**
     * Show BatchCommentsDialog.
     * Called from viewer -- batch will be determined automatically.
     * @param parent the component to use in positioning this dialog
     * @param showIfNoComments true if the dialog was requested by the user
     * to be shown for input of batch comments; false if it is to be shown
     * because of opening a batch in SplitPaneViewer
     */
    public static boolean showDialog(Component parent, boolean showIfNoComments) {
        return showDialog(0, parent, showIfNoComments);
    }

    /**
     * Show BatchCommentsDialog.
     * @param givenBatchId Batch id given when called from BatchingPage, selected
     * by the user
     * @param parent the component to use in positioning this dialog
     * @param showIfNoComments true if the dialog was requested by the user
     * to be shown for input of batch comments; false if it is to be shown
     * because of opening a batch in SplitPaneViewer
     * 
     * @return true if (1) showIfNoComments AND (2) user did not cancel.
     *   Note: if showIfNoComments is false, we don't wait for 
     *   the query, so we can't determine if comments will be found.
     */
    public static boolean showDialog(int givenBatchId, Component parent, boolean showIfNoComments) {
        BatchCommentsDialog dialog = new BatchCommentsDialog(givenBatchId, parent, showIfNoComments);
        if (showIfNoComments) {
            // We are showing the dialog in any case
            // Showing modal dialog suspends until data read
            dialog.addControls();
            dialog.setModal(true);
            dialog.setVisible(true);
            return dialog.getResult();
        } else {
            return false;
        }
    }

    /**
     * Create new BatchCommentsDialog.
     * If from BatchingPage, non-zero batch id must be given.
     */
    private BatchCommentsDialog(int givenBatchId, Component parent, boolean showIfNoComments) {
        super(JOptionPane.getFrameForComponent(parent));

        getContentPane().add(selectPanel, BorderLayout.CENTER);

        selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.Y_AXIS));
        selectPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        okButton.addActionListener(buttonListener);
        okButton.setEnabled(false);
        cancelButton.addActionListener(buttonListener);
        JPanel outerButtonPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 30, 20));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        outerButtonPanel.add(buttonPanel);

        selectPanel.add(outerButtonPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(parent);

        this.showIfNoComments = showIfNoComments;
        setTitle("Batch Comments");

        comment.setFocusable(true);
        getBatchCommentsData(givenBatchId);
    }

    private void addControls() {
        javax.swing.JPanel otherGroup = new javax.swing.JPanel();
        otherGroup.setFocusable(false);
        otherGroup.setLayout(new BorderLayout());
        otherGroup.add(comment);
        comment.setDocument(new PlainDocument() {

            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (str == null) {
                    return;
                }
                super.insertString(offs, str, a);
                okSetEnabled(true);  // must be enabled at all times because the
            // comments are shown at startup of batch and
            // the user should be able to click ok.

            }

            public void remove(int offs, int len)
                    throws BadLocationException {
                super.remove(offs, len);
                if (!comment.getText().equals(originalComment)) {
                    okSetEnabled(true);
                } else {
                    okSetEnabled(false);
                }
            }
        });

        selectPanel.add(Box.createVerticalStrut(20), 0);
        selectPanel.add(otherGroup, 1);        
        comment.requestFocus();
        pack();
    }

    private void okSetEnabled(boolean flag) {
        okButton.setEnabled(flag);
        okButton.setSelected(flag);    
    }

    /**
     * Call ClientTask to enqueue a task to the comments for the given batch.
     * The data is returned in a ResultSet.
     */
    private void getBatchCommentsData(int batchId) { // 2007-05-31: changed here

        Log.print("(BatchCommentsDialog.getBatchCommentsData) ");
        final ClientTask task = new TaskExecuteQuery("batch comments select",
                Integer.toString(batchId)); // 2007-05-31: changed here

        task.setCallback(new Runnable() {

            public void run() {
                getBatchCommentsDataEntry((ResultSet) task.getResult());
            }
        });
        boolean ok = task.enqueue(this);
    }

    /**
     * Retrieve batch comments from server.
     * @param queryResult - ResultSet containing the batch comments
     * @see getBatchCommentsData
     */
    private void getBatchCommentsDataEntry(ResultSet queryResult) {
        try {
            if (queryResult.next()) {
                originalComment = queryResult.getString(1);
            } else {
                originalComment = "";
            }

            //Log.print("length="+originalComment.length()+" show="+ showIfNoComments);
            if (originalComment.length() > 0 || showIfNoComments) {
                if (!showIfNoComments) {
                    // We didn't show the dialog before, but now
                    // that we know there are comments, we show it.
                    addControls();
                    comment.setText(originalComment);
                    setModal(true);
                    setVisible(true);
                } else {
                    // Dialog already showing, just change the text.
                    comment.setText(originalComment);
                }
            }
        } catch (SQLException e) {
            Log.quit(e);
        }
    }
    /**
     * Save the text entered by the user to the server.
     */
    private ActionListener buttonListener = new ActionListener() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                Object source = A.getSource();
                if (source == okButton) {
                    Log.print("(BatchCommentDialog.getSelection) ok " + comment.getText() + "/" + originalComment);
                    if (!((String) comment.getText()).trim().equals(originalComment)) {
                        final ClientTask task = new TaskExecuteUpdate("batch comments update",
                                comment.getText().trim());
                        boolean ok = task.enqueue(BatchCommentsDialog.this);
                    }
                    result = true;
                } else {
                    result = false;
                }
                setVisible(false);
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    };

    private boolean getResult() {
        return result;
    }
}
