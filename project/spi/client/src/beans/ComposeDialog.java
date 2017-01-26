/* $Header: /home/common/cvsarea/ibase/dia/src/beans/ComposeDialog.java,v 1.26.6.1 2006/02/22 20:05:51 nancy Exp $ */
/*
 * ComposeDialog.java
 *
 * Created on August 11, 2004, 1:43 PM
 */

package beans;

import beans.LTextArea;
import client.ClientTask;
import client.TaskRequestRecipientList;
import client.TaskSendMailsentData;
import com.lexpar.util.Log;
import common.MailsentData;
import model.ResultSetTableModel;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.table.AbstractTableModel;

/**
 * This dialog is used by <code>ui.MailPanel</code>, <code>ui.QAMailDialog</code> and
 * <code>StatisticsDialog</code> to create a new mailsent table entry.  The data is
 * sent to the server via common.MailsentData.
 * @author  Nancy McCall
 * @see client.TaskRequestRecipientList
 * @see client.TaskSendMailsentData
 * @see common.MailsentData
 * @see model.ResultSetTableModel
 * @see server.Handler_request_recipient_list
 * @see server.Handler_mailsent_data
 */
public class ComposeDialog extends javax.swing.JDialog {
    private final String ASTERISKS = "***************";
    private boolean reply = false;
    private IbaseTextField to;    
    private int mailsentId = 0;
    private String recipientList = "";
    private boolean sendStatus = false;
    
    /**
     * Creates new form ComposeDialog.
     * @param parent the component to use in positioning this dialog
     * @param modal true if this dialog should be modal; false
     * for one that allows other windows to be active at the same time
     * @param mailsentId the mailsent.mailsent_id if this is a reply or forward
     * of an existing mailsent row; 0 if it is a new mailsent row
     * @param recipientList the textual, semicolon-separated list of previous
     * recipients for this mailsent row
     * @param textArea the body of the mail
     * @param prefix "Re: " or "Fwd: " or empty String
     * @param subject the Subject line of the mail
     * @param dateString the date of this mail
     * @param sender the user.user_name of the sender
     * @param addr the recipient of this new mail; empty string for forwarded mail
     */
    public ComposeDialog(Component parent, boolean modal
                         , int mailsentId, String recipientList
                         , String textArea, String prefix, String subject
                         , String dateString, String sender, String addr) {
        super(JOptionPane.getFrameForComponent(parent), modal);
        this.mailsentId = mailsentId;
        this.recipientList = recipientList;
        //Log.print("(ComposeDialog) mailsentId/recipientList " + mailsentId + "/"
        //          + recipientList);
        initComponents();
        Set keys = subjectTextArea.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        keys = new HashSet(keys);
        keys.add(KeyStroke.getKeyStroke("TAB"));
        keys.add(KeyStroke.getKeyStroke("shift TAB"));
        subjectTextArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS
                                              , keys);

        if (prefix.length() > 0) {
            reply = true;
        }
        
        to = new beans.IbaseTextField("mail", 0
                    , /* table use mandatory ->*/ true
                    , /* table updateable ->*/ false
                    , /* repeated value ->*/ true);
        getToModel();
        to.setColumns(40);
        to.setFocusable(true);
        toPanel.add(to);
        this.to.setText(addr);
        if (prefix.length() > 0) {
            this.textTextArea.setText("\n\n\n" + ASTERISKS + " Original Message " + ASTERISKS
                                      +"\n > From:    " + sender
                                      +"\n > To:      " + recipientList
                                      +"\n > Date:    " + dateString
                                      +"\n > Subject: " + subject
                                      +"\n > " + textArea);
            // remove old prefix
            if (subject.startsWith("Re: ")) {
                subject = subject.substring(4);
            } else if (subject.startsWith("Fwd: ")) {
                subject = subject.substring(5);
            }
        } else {
            this.textTextArea.setText(textArea);
        }

        // set subject line
        this.subjectTextArea.setText(prefix + subject);
        
        textTextArea.setCaretPosition(0);
        
        if (addr.length() == 0) {
            // forwarded mail, start in address line
            to.requestFocus();
        } else {
            textTextArea.requestFocus();
        }
    }

    /**
     * Enqueue a task to get the recipient list for the choiceModel in ibaseTextField.
     * The data is returned via the entry point, getToModelEntry.
     *
     * @see getToModelEntry
     */
    private void getToModel() {
        final ClientTask task = new TaskRequestRecipientList();
        task.setCallback(new Runnable() {
                public void run() {
                    try {
                        ResultSet rs = (ResultSet) task.getResult();
                        if (rs != null) {
                            getToModelEntry(rs);
                        } else { // since data == null
                            Log.quit("ComposeDialog.getToModel: null returned");
                        }
                    } catch (Throwable th) {
                        Log.quit(th);
                    }
                }
            });
        boolean ok = task.enqueue(this);
    }

    /**
     * Entry point method to receive and format the recipient list data
     * based on data provided by client.ClientTask.
     * 
     * @param rs - a ResultSet containing all addressees who are valid for this user
     *
     * @see client.ClientTask
     * @see client.ClientThread
     */
    public void getToModelEntry(ResultSet rs) {
        ResultSetTableModel model = new ResultSetTableModel(rs);
        //Log.print("(ComposeDialog.getToModelEntry) " + model.getRowCount());
        to.setProjectModel(model);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        jToolBar1 = new javax.swing.JToolBar();
        jPanel3 = new javax.swing.JPanel();
        sendButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        spellCheckButton =         new beans.SpellCheckButton() {
            public void actionOnSpeller() {
                LTextArea area = new LTextArea(25, 50);
                String text = textTextArea.getText();
                if (reply) {
                    int i = text.indexOf(ASTERISKS);
                    if (i > 0) {
                        text = text.substring(0, i-1);
                    }
                    area.setText(text);
                    spellCheckButton.setComponent(area);
                    super.actionOnSpeller();
                    if (! area.getText().equals(text)) {
                        text = textTextArea.getText();
                        // Put together the checked area and the reply part of the message.
                        textTextArea.setText(area.getText() + text.substring(i));
                    }
                } else {
                    area.setText(textTextArea.getText());
                    // new mail, just check the entire textArea
                    spellCheckButton.setComponent(area);
                    super.actionOnSpeller();
                    if (! area.getText().equals(textTextArea.getText())) {
                        textTextArea.setText(area.getText());
                    }
                }
            }
        };

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        toPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane(
            javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER, 
            javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        subjectTextArea = new JTextArea() {
            public boolean isManagingFocus() {
                return false;
            }
        };

        jScrollPane1 = new javax.swing.JScrollPane();
        textTextArea = new javax.swing.JTextArea();

        setTitle("Mail Editor");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        jPanel3.setMaximumSize(new java.awt.Dimension(210, 49));
        jPanel3.setMinimumSize(new java.awt.Dimension(210, 53));
        jPanel3.setPreferredSize(new java.awt.Dimension(200, 51));
        sendButton.setFont(new java.awt.Font("Dialog", 0, 10));
        sendButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/send.gif")));
        sendButton.setText("Send");
        sendButton.setToolTipText("Send Mail");
        sendButton.setFocusable(false);
        sendButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sendButton.setPreferredSize(new java.awt.Dimension(50, 51));
        sendButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        sendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendButtonActionPerformed(evt);
            }
        });

        jPanel3.add(sendButton);

        cancelButton.setFont(new java.awt.Font("Dialog", 0, 10));
        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cancel.gif")));
        cancelButton.setText("Cancel");
        cancelButton.setToolTipText("Discard this mail.");
        cancelButton.setFocusable(false);
        cancelButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cancelButton.setPreferredSize(new java.awt.Dimension(50, 10));
        cancelButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel3.add(cancelButton);

        spellCheckButton.setText("Spell ... ");
        spellCheckButton.setFont(new java.awt.Font("Dialog", 0, 10));
        spellCheckButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        spellCheckButton.setMinimumSize(new java.awt.Dimension(45, 53));
        spellCheckButton.setPreferredSize(new java.awt.Dimension(50, 51));
        spellCheckButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jPanel3.add(spellCheckButton);

        jToolBar1.add(jPanel3);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(618, 500));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("To:     ");
        jLabel1.setFocusable(false);
        toPanel.add(jLabel1);

        jPanel2.add(toPanel, new java.awt.GridBagConstraints());

        jLabel2.setLabelFor(subjectTextArea);
        jLabel2.setText("Subject: ");
        jPanel4.add(jLabel2);

        subjectTextArea.setColumns(50);
        subjectTextArea.setRows(1);
        subjectTextArea.setBorder(new javax.swing.border.BevelBorder(javax.swing.border.BevelBorder.LOWERED));
        subjectTextArea.setMinimumSize(new java.awt.Dimension(22, 20));
        subjectTextArea.setDocument(new javax.swing.text.PlainDocument() {
            public void insertString(int offs, String str, 
            	javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
                if(! str.equals("\n")) {
                   super.insertString(offs, str, a);
                }
            }
        });
        jScrollPane2.setViewportView(subjectTextArea);
        jPanel4.add(jScrollPane2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        jPanel2.add(jPanel4, gridBagConstraints);

        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        textTextArea.setColumns(50);
        textTextArea.setLineWrap(true);
        textTextArea.setRows(25);
        textTextArea.setWrapStyleWord(true);
        jScrollPane1.setViewportView(textTextArea);

        jPanel1.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        pack();
    }//GEN-END:initComponents

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendButtonActionPerformed
        if (to.getText().length() < 1) {
            Toolkit.getDefaultToolkit().beep();
            Log.print("BEEP> ComposeDialog.sendButton To: empty");
            Object[] options = {"Ok"};
            int response = JOptionPane.showOptionDialog(this,
                    "There are no recipients in the \'To\' box.\n\n"
                    + "Please enter one or more recipients before clicking Send.",
                    "Send Error",
                    JOptionPane.OK_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    options[0]);

        } else {
            MailsentData mailsent = new MailsentData();
            mailsent.mailsentId = mailsentId;
            mailsent.recipientList = to.getText();
            mailsent.subject = subjectTextArea.getText();
            mailsent.text = textTextArea.getText();
            final ClientTask task = new TaskSendMailsentData(mailsent);
            boolean ok = task.enqueue(this);
            sendStatus = true;
            setVisible(false);
            dispose();
        }
    }//GEN-LAST:event_sendButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    /**
     * Let the caller know whether anything was sent.
     * @return true if the mail was sent; false otherwise
     */
    public boolean getStatus() {
        return sendStatus;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new ComposeDialog(new javax.swing.JFrame(), true, 0, "", "", "", "","", "", "").show();
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JButton sendButton;
    private beans.SpellCheckButton spellCheckButton;
    private javax.swing.JTextArea subjectTextArea;
    private javax.swing.JTextArea textTextArea;
    private javax.swing.JPanel toPanel;
    // End of variables declaration//GEN-END:variables
    
}
