/* $Header: /home/common/cvsarea/ibase/dia/src/beans/QACommentsDialog.java,v 1.3.6.1 2006/02/22 20:05:51 nancy Exp $ */
package beans;

import com.lexpar.util.Log;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
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
 * Called from QACloseDialog to show QACommentsDialog.
 */
public final class QACommentsDialog extends JDialog {

    private JPanel selectPanel = new javax.swing.JPanel();
    public JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    private LTextArea comment = new LTextArea(12, 50);
    private String result = null;

    /**
     * Create and instance of QACommentsDialog.
     * @param parent the component to use in positioning this dialog
     * @return comments to be added to each batch, or null if user cancelled
     */
    public static String showDialog(Component parent) {
        QACommentsDialog dialog = new QACommentsDialog(parent);
        dialog.addControls();
        dialog.setModal(true);
        dialog.setVisible(true);
        return dialog.result;
    }

    /**
     * Create new BatchCommentsDialog.
     */
    private QACommentsDialog(Component parent) {
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

        setTitle("Batch Comments for Rejected Batches");

        comment.setFocusable(true);
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
                if (comment.getText().trim().length() == 0) {
                    okSetEnabled(false);
                } else {
                    okSetEnabled(true);
                }
            }
        });

        selectPanel.add(Box.createVerticalStrut(20), 0);
        selectPanel.add(otherGroup, 1);
        comment.requestFocus();
        pack();
    }

    /**
     * Enable or disable the <code>okButton</code>.
     * @param flag true to enable; false to disable
     */
    protected void okSetEnabled(boolean flag) {
        okButton.setEnabled(flag);
        okButton.setSelected(flag);
    }
    /**
     * Save the text entered by the user to the server.
     */
    protected ActionListener buttonListener = new ActionListener() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                Object source = A.getSource();
                if (source == okButton) {
                    result = comment.getText().trim();
                } else {
                    result = null;
                }
                dispose();
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    };
}
