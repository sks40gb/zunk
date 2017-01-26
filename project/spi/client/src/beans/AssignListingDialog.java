/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import common.PopulateData;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import model.SQLManagedComboModel;

/**
 * Called from ui.BatchingPage. this dialog display the list of Listing assigned
 * to the selected user.
 * @see ui.BatchingPage
 * 
 * @author bmurali
 */
public class AssignListingDialog extends javax.swing.JDialog {

    private boolean cancelButtonWasSelected = false;
    private SQLManagedComboModel projectModel;
    private final String GET_TEAM_LEADERS = "listing.get teamLead";
    private PopulateData data;
    private int userId = 0;
    private String userName = "";

    /**
     * Create new form of AssignListingDialog
     * @param parent Parent window
     */
    public AssignListingDialog(java.awt.Component parent) {

        super(JOptionPane.getFrameForComponent(parent), true);
        initComponents();
        okButton.setEnabled(false);
        this.getRootPane().setDefaultButton(okButton);

        listingUserSelectBox.addPropertyChangeListener("text", new PropertyChangeListener() {

            @Override
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if (" ".equals(listingUserSelectBox.getName())) {
                    
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
        jPanel3 = new javax.swing.JPanel();
        listingUserSelectBox = new javax.swing.JComboBox();
        selectListingUserLabel = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        selectListing = new JCheckBox("Listing");
        selectTally = new JCheckBox("Tally");
        tallyUserSelectBox = new javax.swing.JComboBox();
        selectTallyUserLabel  = new javax.swing.JLabel();
        
        getContentPane().setLayout(new java.awt.GridBagLayout());
        setTitle("Assign Volume For Listing");
        setModal(true);
        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });
        selectListing.addActionListener(new java.awt.event.ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                selectListingCheckBoxActionPerform(e);
            }
        });
//        jPanel3.add(selectListing);
//        jPanel3.add(selectTally);
//        gridBagConstraints = new java.awt.GridBagConstraints();
//        gridBagConstraints.gridx = 0;
//        getContentPane().add(jPanel3, gridBagConstraints);
        
        selectListingUserLabel.setFont(new java.awt.Font("Dialog", 1, 14));
        selectListingUserLabel.setText("Select Listing User: ");
        selectListingUserLabel.setFocusable(false);
        jPanel1.add(selectListingUserLabel);

        listingUserSelectBox.setPreferredSize(new java.awt.Dimension(150, 25));
        listingUserSelectBox.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listingUserSelectBoxActionPerformed(evt);
            }
        });
        jPanel1.add(listingUserSelectBox);
        
        
        selectTallyUserLabel.setFont(new java.awt.Font("Dialog", 1, 14));
        selectTallyUserLabel.setText("Select Tally User: ");
        selectTallyUserLabel.setFocusable(false);
       // jPanel1.add(selectTallyUserLabel);
        
        tallyUserSelectBox.setPreferredSize(new java.awt.Dimension(150, 25));
        tallyUserSelectBox.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
               // listingUserSelectBoxActionPerformed(evt);
            }
        });
       // jPanel1.add(tallyUserSelectBox);
        
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        getContentPane().add(jPanel1, gridBagConstraints);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        jPanel2.add(okButton);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
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

    /**
     * Set user Id
     * @param userId User Id
     */
    public void setValue(int userId) {
        this.userId = userId;

    }

    public int getValue() {
        if (cancelButtonWasSelected) {
            return 0;
        }
        return userId;
    }

    /**
     * Make the form visible.
     * @param evt
     */
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

    private void listingUserSelectBoxActionPerformed(ActionEvent evt) {
        if (data != null) {
            userName = data.user_name;
        }
        int sel = listingUserSelectBox.getSelectedIndex();
        if (sel > -1) {
            data.user_name = (String) projectModel.getElementAt(sel);
            userId = ((SQLManagedComboModel) listingUserSelectBox.getModel()).getIdAt(sel);
            okButton.setEnabled(true);
            setValue(userId);
        } else {
            data.user_name = "";
            userId = 0;
            okButton.setEnabled(false);
        }
    }

   private void selectListingCheckBoxActionPerform(ActionEvent evt){
        tallyUserSelectBox.setEnabled(false);
   }
    /**
     * On clicking the current Listing tab.
     */
    protected void tabSelected() {

        // project model
        projectModel = new SQLManagedComboModel(GET_TEAM_LEADERS);
        projectModel.register();
        listingUserSelectBox.setModel(projectModel);
        data = new PopulateData();

    }

    // Variables declaration
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JComboBox listingUserSelectBox;
    private javax.swing.JLabel selectListingUserLabel;
    private javax.swing.JButton okButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JCheckBox selectListing;
    private javax.swing.JCheckBox selectTally;
    private javax.swing.JComboBox tallyUserSelectBox;
    private javax.swing.JLabel selectTallyUserLabel;
}
