/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import common.PopulateData;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import model.SQLManagedComboModel;

/**
 * Called from ui.BatchingPage, this dialog display the Tally list assined to the selected user.
 * @see ui.BatchingPage
 * @author bmurali
 */
public class AssignTallyDialog extends javax.swing.JDialog {

    private final String GET_TALLY_TEAM = "tally.get tallyTeam";
    private boolean cancelButtonWasSelected = false;
    private SQLManagedComboModel projectModel = null;
    private PopulateData data;
    /** User Id */
    private int userId = 0;
    /** User name */
    private String userName = "";

    /**
     * Create an instance of this dialog.
     * @param parent; Parent window for this form.
     */
    public AssignTallyDialog(java.awt.Component parent) {
        super(JOptionPane.getFrameForComponent(parent), true);
        initComponents();
        okButton.setEnabled(false);
        this.getRootPane().setDefaultButton(okButton);

        userSelectBox.addPropertyChangeListener("text", new PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if (" ".equals(userSelectBox.getName())) {
                    okButton.setEnabled(false);
                } else {
                    okButton.setEnabled(true);
                    okButton.setSelected(true);
                }
            }
        });
        tabSelected();
    }

    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        userSelectBox = new javax.swing.JComboBox();
        selectUserLabel = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        getContentPane().setLayout(new java.awt.GridBagLayout());
        setTitle("Assign Volume For Tally");
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        selectUserLabel.setFont(new java.awt.Font("Dialog", 1, 14));
        selectUserLabel.setText("Select User: ");
        selectUserLabel.setFocusable(false);
        jPanel1.add(selectUserLabel);
        
        userSelectBox.setPreferredSize(new java.awt.Dimension(150, 25));
        userSelectBox.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userSelectBoxActionPerformed(evt);
            }
        });
        jPanel1.add(userSelectBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        getContentPane().add(jPanel1, gridBagConstraints);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jPanel2.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        jPanel2.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        getContentPane().add(jPanel2, gridBagConstraints);
        
        pack();
    }

    public void setValue(int userId) {
        this.userId = userId;

    }

    public int getValue() {
        if (cancelButtonWasSelected) {
            return 0;
        }
        return userId;
    }

    private void okButtonActionPerformed(ActionEvent evt) {
        setVisible(false);
    }

    private void cancelButtonActionPerformed(ActionEvent evt) {
        cancelButtonWasSelected = true;
        setVisible(false);
    }

    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
    }

    private void userSelectBoxActionPerformed(ActionEvent evt) {
        if (data != null) {
            userName = data.user_name;
        }
        int sel = userSelectBox.getSelectedIndex();
        if (sel > -1) {
            data.user_name = (String) projectModel.getElementAt(sel);
            userId = ((SQLManagedComboModel) userSelectBox.getModel()).getIdAt(sel);
            setValue(userId);
            okButton.setEnabled(true);
        } else {
            data.user_name = "";
            userId = 0;
            okButton.setEnabled(false);
        }
    }

    protected void tabSelected() {

        // project model
        projectModel = new SQLManagedComboModel(GET_TALLY_TEAM);
        projectModel.register();
        userSelectBox.setModel(projectModel);
        data = new PopulateData();

    }

    // Variables declaration
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JComboBox userSelectBox;
    private javax.swing.JLabel selectUserLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JButton cancelButton;
}
