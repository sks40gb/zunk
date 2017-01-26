/*
 * AddEditCustomerprice.java
 *
 * Created on July 26, 2005, 7:56 AM
 */
package beans;

import beans.LNumberField;
import client.ClientTask;
import client.TaskExecuteQuery;
import client.TaskSendCustomerprice;
import common.Log;
import common.CustomerpriceData;
import model.QueryComboModel;
import java.awt.Toolkit;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Called from ui.CustomerPricePage, this dialog allows the user to enter
 * price-per-page and price-per-document data for a selected project, volume and level.
 * The data is stored in the customerprice table.
 *
 * @author  Nancy McCall
 * 
 * @see client.TaskSendCustomerprice
 * @see server.Handler_customerprice
 * @see ui.CustomerPricePage
 */
public class AddEditCustomerprice extends javax.swing.JDialog {

    private CustomerpriceData cp;   

    /**
     * Creates new form AddEditCustomerprice.
     * @param parent the component to use in positioning this dialog
     * @param projectName the name to show in the protected Project: JTextField
     * @param volumeName the name to show in the protected Volume: JTextField
     * @param cp a container for data required to store customerprice data
     */
    public AddEditCustomerprice(java.awt.Component parent, String projectName, String volumeName, CustomerpriceData cp) {
        super(JOptionPane.getFrameForComponent(parent));
        this.setModal(true);
        this.cp = cp;
        initComponents();
        getRootPane().setDefaultButton(okButton);

        DocumentListener documentListener = new DocumentListener() {

            /**
             * Gives notification that there was an insert into the document.  The
             * range given by the DocumentEvent bounds the freshly inserted region.
             *
             * @param e the document event
             */
            @Override
            public void insertUpdate(DocumentEvent e) {
                checkOkEnabled();
            }

            /**
             * Gives notification that a portion of the document has been
             * removed.  The range is given in terms of what the view last
             * saw (that is, before updating sticky positions).
             *
             * @param e the document event
             */
            @Override
            public void removeUpdate(DocumentEvent e) {
                checkOkEnabled();
            }

            /**
             * Gives notification that an attribute or set of attributes changed.
             *
             * @param e the document event
             */
            @Override
            public void changedUpdate(DocumentEvent e) {
                checkOkEnabled();
            }
        };

        this.projectName.setText(projectName);
        this.volumeName.setText(volumeName);
        this.levelText.setText(cp.field_level == 0 ? "0" : Integer.toString(cp.field_level));

        if (cp.volume_id < 0) {
            // this is an add, so show the volume combo
            volumeCombo.setModel(new QueryComboModel("CustomerPricePage.get volumes", /* required-> */ true, new String[]{Integer.toString(cp.project_id)}, "", "<Volume Default>"));
            volumeCombo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    volumeComboActionPerformed(evt);
                }
            });
            volumePanel.add(volumeCombo);
            volumePanel.remove(this.volumeName);

            levelCombo.setModel(new QueryComboModel("CustomerPricePage.get levels", /* required-> */ true, new String[]{Integer.toString(cp.project_id)}, ""));
            levelCombo.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    levelComboActionPerformed(evt);
                }
            });
            levelPanel.add(levelCombo);
            levelPanel.remove(levelText);
        }


        if (cp.volume_id == 0 && cp.field_level == 0) {
            // no defaults available
            defaultButton.setEnabled(false);
        } else {
            defaultButton.setEnabled(true);
        }

        setScreenPrices();

        unitizePage.getDocument().addDocumentListener(documentListener);
        unitizeDoc.getDocument().addDocumentListener(documentListener);
        codingPage.getDocument().addDocumentListener(documentListener);
        codingDoc.getDocument().addDocumentListener(documentListener);
    }

    private void checkOkEnabled() {
        if (!volumeName.getText().equals("") && !levelText.getText().equals("")) {
            okButton.setEnabled(true);
        } else {
            okButton.setEnabled(false);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        volumeCombo = new javax.swing.JComboBox();
        levelCombo = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        projectNameLabel = new javax.swing.JLabel();
        projectPanel = new javax.swing.JPanel();
        projectName = new javax.swing.JTextField();
        volumeLabel = new javax.swing.JLabel();
        volumePanel = new javax.swing.JPanel();
        volumeName = new javax.swing.JTextField();
        levelLabel = new javax.swing.JLabel();
        levelPanel = new javax.swing.JPanel();
        levelText = new beans.LTextField();
        pricePanel = new javax.swing.JPanel();
        pagePanel = new javax.swing.JPanel();
        unitizePageLabel = new javax.swing.JLabel();
        unitizePage = new LNumberField(2,1);
        codingPageLabel = new javax.swing.JLabel();
        codingPage = new LNumberField(2,1);
        jSeparator2 = new javax.swing.JSeparator();
        docPanel = new javax.swing.JPanel();
        unitizeDocLabel = new javax.swing.JLabel();
        unitizeDoc = new LNumberField(2,1);
        codingDocLabel = new javax.swing.JLabel();
        codingDoc = new LNumberField(2,1);
        jSeparator3 = new javax.swing.JSeparator();
        jPanel4 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        defaultButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        volumeCombo.setFont(new java.awt.Font("Dialog", 1, 11));
        volumeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                volumeComboActionPerformed(evt);
            }
        });

        levelCombo.setEditable(true);
        levelCombo.setFont(new java.awt.Font("Dialog", 1, 11));
        levelCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                levelComboActionPerformed(evt);
            }
        });

        getContentPane().setLayout(new java.awt.FlowLayout());

        setTitle("Customer Prices");
        setModal(true);
        setName("Edit Prices");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel1.setLayout(new java.awt.GridBagLayout());

        jPanel1.setPreferredSize(new java.awt.Dimension(593, 250));
        projectNameLabel.setFont(new java.awt.Font("Dialog", 1, 11));
        projectNameLabel.setText("Project: ");
        projectNameLabel.setFocusable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(projectNameLabel, gridBagConstraints);

        projectPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        projectName.setColumns(40);
        projectName.setEditable(false);
        projectName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        projectName.setEnabled(false);
        projectPanel.add(projectName);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(projectPanel, gridBagConstraints);

        volumeLabel.setFont(new java.awt.Font("Dialog", 1, 11));
        volumeLabel.setText("Volume:  ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(volumeLabel, gridBagConstraints);

        volumePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        volumeName.setColumns(40);
        volumeName.setEditable(false);
        volumeName.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        volumeName.setEnabled(false);
        volumePanel.add(volumeName);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(volumePanel, gridBagConstraints);

        levelLabel.setFont(new java.awt.Font("Dialog", 1, 11));
        levelLabel.setText("Level: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(levelLabel, gridBagConstraints);

        levelPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        levelText.setColumns(1);
        levelText.setEnabled(false);
        levelPanel.add(levelText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(levelPanel, gridBagConstraints);

        pricePanel.setPreferredSize(new java.awt.Dimension(366, 105));
        pagePanel.setLayout(new java.awt.GridBagLayout());

        pagePanel.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Price Per Page", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11)));
        unitizePageLabel.setFont(new java.awt.Font("Dialog", 1, 11));
        unitizePageLabel.setText("Unitize: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pagePanel.add(unitizePageLabel, gridBagConstraints);

        unitizePage.setColumns(7);
        pagePanel.add(unitizePage, new java.awt.GridBagConstraints());

        codingPageLabel.setFont(new java.awt.Font("Dialog", 1, 11));
        codingPageLabel.setText("Coding: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        pagePanel.add(codingPageLabel, gridBagConstraints);

        codingPage.setColumns(7);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        pagePanel.add(codingPage, gridBagConstraints);

        pricePanel.add(pagePanel);

        jSeparator2.setPreferredSize(new java.awt.Dimension(16, 0));
        pricePanel.add(jSeparator2);

        docPanel.setLayout(new java.awt.GridBagLayout());

        docPanel.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)), "Price Per Document", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 11)));
        unitizeDocLabel.setFont(new java.awt.Font("Dialog", 1, 11));
        unitizeDocLabel.setText("Unitize: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        docPanel.add(unitizeDocLabel, gridBagConstraints);

        unitizeDoc.setColumns(7);
        docPanel.add(unitizeDoc, new java.awt.GridBagConstraints());

        codingDocLabel.setFont(new java.awt.Font("Dialog", 1, 11));
        codingDocLabel.setText("Coding: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        docPanel.add(codingDocLabel, gridBagConstraints);

        codingDoc.setColumns(7);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        docPanel.add(codingDoc, gridBagConstraints);

        pricePanel.add(docPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 10;
        jPanel1.add(pricePanel, gridBagConstraints);

        jSeparator3.setPreferredSize(new java.awt.Dimension(0, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(jSeparator3, gridBagConstraints);

        okButton.setMnemonic('O');
        okButton.setText("OK");
        okButton.setEnabled(false);
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jPanel4.add(okButton);

        defaultButton.setMnemonic('D');
        defaultButton.setText("Get Default Prices");
        defaultButton.setToolTipText("Get the <Volume Default> prices for the selected level.");
        defaultButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultButtonActionPerformed(evt);
            }
        });

        jPanel4.add(defaultButton);

        cancelButton.setMnemonic('C');
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel4.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = java.awt.GridBagConstraints.RELATIVE;
        jPanel1.add(jPanel4, gridBagConstraints);

        getContentPane().add(jPanel1);

        pack();
    }//GEN-END:initComponents

    private void defaultButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultButtonActionPerformed
        final ClientTask task;
        if (cp.volume_id == 0) {
            task = new TaskExecuteQuery("customerprice level defaults", Integer.toString(cp.project_id));
        } else {
            task = new TaskExecuteQuery("customerprice defaults", Integer.toString(cp.project_id), Integer.toString(cp.field_level));
        }
        task.setCallback(new Runnable() {

            public void run() {
                ResultSet results = (ResultSet) task.getResult();
                if (results != null) {
                    loadDefaultsEntry(results);
                } else {
                    Log.print("??? Defaults: null resultset returned");
                }
            }
        });
        task.enqueue(this);
    }//GEN-LAST:event_defaultButtonActionPerformed

    private void loadDefaultsEntry(ResultSet results) {
        try {
            if (results.next()) {
                cp.unitize_page_price = results.getString(1);
                cp.unitize_doc_price = results.getString(2);
                cp.coding_page_price = results.getString(3);
                cp.coding_doc_price = results.getString(4);
                setScreenPrices();
            }
            results.close();
        } catch (SQLException e) {
            Log.print("(AddEditCustomerprice.loadDefaultsEntry) " + e);
        }
    }

    private void setScreenPrices() {
        unitizePage.setText(cp.unitize_page_price);
        unitizeDoc.setText(cp.unitize_doc_price);
        codingPage.setText(cp.coding_page_price);
        codingDoc.setText(cp.coding_doc_price);
    }

    private void levelComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_levelComboActionPerformed
        levelText.setText((String) levelCombo.getEditor().getItem());
        checkOkEnabled();
    }//GEN-LAST:event_levelComboActionPerformed

    private void volumeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_volumeComboActionPerformed
        volumeName.setText((String) volumeCombo.getSelectedItem());
        checkOkEnabled();
    }//GEN-LAST:event_volumeComboActionPerformed

    /**
     * Close this dialog without preserving the user's data.
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        closeDialog(null);
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * If the volume and level fields are valid, send the customerprice data
     * to the server via client.TaskSendCustomerprice.  Close this dialog.
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        // volumeId will be 0 for project default
        if (cp.volume_id < 0) {
            // it's an add
            cp.volume_id = ((QueryComboModel) volumeCombo.getModel()).getSelectedId();
        }
        assert cp.volume_id > -1;

        if (!((String) levelCombo.getEditor().getItem()).equals("")) {
            char[] chars = ((String) levelCombo.getEditor().getItem()).toCharArray();
            if (chars.length > 1) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this,
                        "Level must be a numeric value, 0 - 9." + "\nPlease re-enter.",
                        "Data Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            char chr = chars[0];
            if (chr < '0' || chr > '9') {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this,
                        "Level must be a numeric value, 0 - 9." + "\nPlease re-enter.",
                        "Data Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            cp.field_level = Integer.parseInt((String) levelCombo.getEditor().getItem());
        } else {
            cp.field_level = 0;
        }
        cp.unitize_page_price = unitizePage.getText().equals("") ? "0" : unitizePage.getText();
        cp.unitize_doc_price = unitizeDoc.getText().equals("") ? "0" : unitizeDoc.getText();
        cp.coding_page_price = codingPage.getText().equals("") ? "0" : codingPage.getText();
        cp.coding_doc_price = codingDoc.getText().equals("") ? "0" : codingDoc.getText();
        Log.print("(AECustomerprice.ok) level is " + cp.field_level);

        final ClientTask task =
                new TaskSendCustomerprice(cp);
        task.enqueue(this);
        closeDialog(null);
    }//GEN-LAST:event_okButtonActionPerformed

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        new AddEditCustomerprice(null, "", "", null);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private beans.LTextField codingDoc;
    private javax.swing.JLabel codingDocLabel;
    private beans.LTextField codingPage;
    private javax.swing.JLabel codingPageLabel;
    private javax.swing.JButton defaultButton;
    private javax.swing.JPanel docPanel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JComboBox levelCombo;
    private javax.swing.JLabel levelLabel;
    private javax.swing.JPanel levelPanel;
    private beans.LTextField levelText;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel pagePanel;
    private javax.swing.JPanel pricePanel;
    private javax.swing.JTextField projectName;
    private javax.swing.JLabel projectNameLabel;
    private javax.swing.JPanel projectPanel;
    private beans.LTextField unitizeDoc;
    private javax.swing.JLabel unitizeDocLabel;
    private beans.LTextField unitizePage;
    private javax.swing.JLabel unitizePageLabel;
    private javax.swing.JComboBox volumeCombo;
    private javax.swing.JLabel volumeLabel;
    private javax.swing.JTextField volumeName;
    private javax.swing.JPanel volumePanel;
    // End of variables declaration//GEN-END:variables
}
