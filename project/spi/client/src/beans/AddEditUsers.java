/* $Header: /home/common/cvsarea/ibase/dia/src/beans/AddEditUsers.java,v 1.34.6.3 2006/02/21 17:02:45 nancy Exp $ */
package beans;

import client.ClientTask;
import client.Global;
import client.TaskExecuteQuery;
import client.TaskSendUsersData;
import com.lexpar.util.Log;
import common.msg.MD5;
import common.UsersData;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionListener;
import model.QueryComboModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Called from ui.TeamAdminPage and UserAdminPage, this dialog allows the user
 * to add or edit data to the users table. Users with Admin and Users privileges
 * can add, edit and delete users.  An Admin user with Profit privileges can
 * grant Profit privileges to another Admin user.  One or more Roles is assigned
 * to each user, which limits the users' activities in the Viewer and Admin screens.
 * The data is sent via the container common.UsersData.
 * <p>
 * Passwords can be assigned on this screen, but they cannot be viewed.  The fix 
 * for a forgotten password is to enter a new one, then notify the user of the new
 * value.
 * 
 * @author  Nancy
 * 
 * @see common.UsersData
 * @see ui.TeamAdminPage
 * @see ui.UserAdminPage
 * @see client.TaskSendUsersData
 * @see server.Handler_users_data
 */
final public class AddEditUsers extends javax.swing.JDialog {

    /** container to hold user data for send to server */
    private int usersId;
    private UsersData usersData;
    private JPanel selectPanel = new javax.swing.JPanel();
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    private JPanel rolePane = new JPanel();
    private JPanel adminPrivPane = new JPanel();
    private LGridBag namePane = new LGridBag();
    private JTextField userName;
    private LTextField fname;
    private LTextField lname;
    private JCheckBox unitizeCheckBox;
    private JCheckBox uqcCheckBox;
    private JCheckBox codingCheckBox;
    private JCheckBox codingqcCheckBox;
    private JCheckBox qaCheckBox;
    private JCheckBox tlCheckBox;
    private JCheckBox adminCheckBox;
    private JCheckBox adminUsersCheckBox;
    private JCheckBox adminProjectCheckBox;
    private JCheckBox adminBatchCheckBox;
    private JCheckBox adminEditCheckBox;
    private JCheckBox adminImportCheckBox;
    private JCheckBox adminExportCheckBox;
    private JCheckBox adminProfitCheckBox;
    private LComboBox teamsCombo;
    private JCheckBox listingCheckBox;
    private JCheckBox tallyCheckBox;
    private JPasswordField password;
    private JPasswordField confirmPassword;
    private javax.swing.JPanel dateField = new DateSelectionField();
    private QueryComboModel teamsModel;
    private LTextField team;
    private JTextField field;

    /**
     * Creates new form AddEditUsers.
     * @param parent the component to use in positioning this dialog
     * @param usersId the users.users_id of the selected user
     */
    public AddEditUsers(Component parent, int usersId) {
        super(JOptionPane.getFrameForComponent(parent));
        this.usersId = usersId;

        getContentPane().add(selectPanel, BorderLayout.CENTER);

        selectPanel.setLayout(new BorderLayout());
        selectPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        okButton.addActionListener(buttonComboListener);
        // disabled for edit until first change
        okButton.setEnabled(false);
        cancelButton.addActionListener(buttonComboListener);
        JPanel outerButtonPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 30, 20));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        outerButtonPanel.add(buttonPanel);
        selectPanel.add(outerButtonPanel, BorderLayout.SOUTH);
        
        setTitle("SPiCA User Definition");

        getUserData(); // calls addControls

    }

    /**
     * Overrides JDialog.setVisible to overwrite password fields
     * before exiting.
     */
    public void setVisible(boolean flag) {
        if (!flag) {
            // Attempt to overwrite passwords in JPasswordFields
            // TBD: ?? Not sure if this does it or not
            char[] pchars = password.getPassword();
            if (pchars.length > 0) {
                Arrays.fill(pchars, '#');
                password.setText(new String(pchars));
            }
            char[] cchars = confirmPassword.getPassword();
            if (cchars.length > 0) {
                Arrays.fill(cchars, '#');
                confirmPassword.setText(new String(cchars));
            }
        }
        super.setVisible(flag);
    }

    /**
     * Overrides JDialog.dispose to overwrite password fields
     * before exiting.
     */
    public void dispose() {
        setVisible(false);
        super.dispose();
    }

    /**
     * Get the user record 
     */
    private void getUserData() {
        teamsModel = new QueryComboModel("get teams names");
        teamsCombo = new LComboBox(40);
        teamsCombo.setModel(teamsModel);

        if (usersId > 0) {
            final ClientTask task;
            //Log.print("(AddEditUsers.getUserData) " + usersId);
            task = new TaskExecuteQuery("users by id select", Integer.toString(usersId));
            task.setCallback(new Runnable() {

                public void run() {
                    getUserByIdDataEntry((ResultSet) task.getResult());
                    // set OK disabled again
                    // (It was enabled by getUserByDataEntry when combo selection set.)
                    okButton.setEnabled(false);
                }
            });
            boolean ok = task.enqueue(this);
        }
        addControls();
    }

    /**
     * Load the user's data.
     * @param queryResult - ResultSet returned by ClientTask in loadUsersDataEntry
     */
    private void getUserByIdDataEntry(ResultSet queryResult) {
        try {
            if (queryResult.next()) {
                // updating a user
                userName.setText(queryResult.getString(2));
                fname.setText(queryResult.getString(4));
                lname.setText(queryResult.getString(3));
                unitizeCheckBox.setSelected((queryResult.getString(5).equals("Yes") ? true : false));
                uqcCheckBox.setSelected((queryResult.getString(6).equals("Yes") ? true : false));
                codingCheckBox.setSelected((queryResult.getString(7).equals("Yes") ? true : false));
                codingqcCheckBox.setSelected((queryResult.getString(8).equals("Yes") ? true : false));
                listingCheckBox.setSelected(((queryResult.getString(9) == null || queryResult.getString(9).equals("Yes")) ? true : false));
                tallyCheckBox.setSelected(((queryResult.getString(10) == null || queryResult.getString(10).equals("Yes")) ? true : false));
                qaCheckBox.setSelected((queryResult.getString(11).equals("Yes") ? true : false));
                tlCheckBox.setSelected((queryResult.getString(12).equals("Yes") ? true : false));
                adminCheckBox.setSelected((queryResult.getString(13).equals("Yes") ? true : false));
                adminUsersCheckBox.setSelected((queryResult.getString(14).equals("Yes") ? true : false));
                adminProjectCheckBox.setSelected((queryResult.getString(15).equals("Yes") ? true : false));
                adminBatchCheckBox.setSelected((queryResult.getString(16).equals("Yes") ? true : false));
                adminEditCheckBox.setSelected((queryResult.getString(17).equals("Yes") ? true : false));
                adminImportCheckBox.setSelected((queryResult.getString(18).equals("Yes") ? true : false));
                adminExportCheckBox.setSelected((queryResult.getString(19).equals("Yes") ? true : false));
                adminProfitCheckBox.setSelected((queryResult.getString(20).equals("Yes") ? true : false));
                teamsCombo.setSelectedIndex(teamsModel.indexOf(queryResult.getInt(1)));
                password.setText("");
                confirmPassword.setText("");
                String date = queryResult.getString(21);
                
                if (null != date) {
                    String[] splitDate = date.split(" ");
                    field.setText(splitDate[0]);
                }
                
                enableAdminPriv();
            }
        } catch (SQLException e) {
            Log.quit(e);
        }
    }
    
    
    private ActionListener buttonComboListener = new ActionListener() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                Object source = A.getSource();
                if (source == okButton) {
                    if (save()) {
                        dispose();
                    }
                } else if (source == cancelButton) {
                    dispose();
                } else if (source == tlCheckBox) {
                    if (tlCheckBox.isSelected()) {
                        adminCheckBox.setSelected(false);
                    }
                    enableAdminPriv();
                    checkOkSetEnabled();
                } else if (source == adminCheckBox) {
                    if (adminCheckBox.isSelected()) {
                        tlCheckBox.setSelected(false);
                    }
                    enableAdminPriv();
                    checkOkSetEnabled();
                } else {
                    checkOkSetEnabled();
                }
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    };

    private void enableAdminPriv() {
        if (adminCheckBox.isSelected()) {
            adminUsersCheckBox.setEnabled(true);
            adminProjectCheckBox.setEnabled(true);
            adminBatchCheckBox.setEnabled(true);
            adminEditCheckBox.setEnabled(true);
            adminImportCheckBox.setEnabled(true);
            adminExportCheckBox.setEnabled(true);
            if (Global.theServerConnection.getPermissionAdminProfit()) {
                // only profit-privileged users can see and change profit status
                adminProfitCheckBox.setVisible(true);
                adminProfitCheckBox.setEnabled(true);
            } else {
                adminProfitCheckBox.setVisible(false);
                adminProfitCheckBox.setEnabled(false);
            }
        } else {
            adminUsersCheckBox.setEnabled(false);
            adminProjectCheckBox.setEnabled(false);
            adminBatchCheckBox.setEnabled(false);
            adminEditCheckBox.setEnabled(false);
            adminImportCheckBox.setEnabled(false);
            adminExportCheckBox.setEnabled(false);
            adminProfitCheckBox.setEnabled(false);
            adminUsersCheckBox.setSelected(false);
            adminProjectCheckBox.setSelected(false);
            adminBatchCheckBox.setSelected(false);
            adminEditCheckBox.setSelected(false);
            adminImportCheckBox.setSelected(false);
            adminExportCheckBox.setSelected(false);
            if (Global.theServerConnection.getPermissionAdminProfit()) {
                adminProfitCheckBox.setVisible(true);
            } else {
                adminProfitCheckBox.setVisible(false);
            }
            adminProfitCheckBox.setSelected(false);
        }
    }

    private PlainDocument getPlainDocument() {
        return new PlainDocument() {

            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (str == null) {
                    return;
                }
                super.insertString(offs, str, a);
                checkOkSetEnabled();
            }

            public void remove(int offs, int len)
                    throws BadLocationException {
                super.remove(offs, len);
                checkOkSetEnabled();
            }
        };
    }

    private PlainDocument getUpperPlainDocument() {
        return new PlainDocument() {

            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (str == null) {
                    return;
                }

                str = str.toUpperCase();

                char[] chars = str.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    char chr = chars[i];
                    if ((chr >= '0' && chr <= '9') || (chr >= 'A' && chr <= 'Z') || (chr == '_')) {
                    } else {
                        Log.print("AddEditUsers: inserting invalid chars: " + str);
                        return;
                    }
                }

                super.insertString(offs, str, a);
                checkOkSetEnabled();
            }

            public void remove(int offs, int len)
                    throws BadLocationException {
                super.remove(offs, len);
                checkOkSetEnabled();
            }
        };
    }

    // Check if OK should be enabled
    // Called on:
    //      -- change in text field
    //      -- change in check box selection
    //      -- change in team combo selection
    // Required:
    //      -- userName
    //      -- at least one check box
    //      -- password, if this is an Add, but not if Edit
    // Not required: TBD: ???
    //      -- First and Last Name
    //      -- Team
    private void checkOkSetEnabled() {
        char[] pchars = password.getPassword();
        char[] cchars = confirmPassword.getPassword();
        if (!userName.getText().equals("") && (unitizeCheckBox.isSelected() || uqcCheckBox.isSelected() || codingCheckBox.isSelected() || codingqcCheckBox.isSelected() || listingCheckBox.isSelected() || tallyCheckBox.isSelected() || qaCheckBox.isSelected() || tlCheckBox.isSelected() || adminCheckBox.isSelected()) && (usersId != 0 || pchars.length != 0) && (pchars.length == 0 || cchars.length != 0)) {
            okButton.setEnabled(true);
        } else {
            okButton.setEnabled(false);
        }
        if (pchars.length == 0) {
            confirmPassword.setEditable(false);
        } else {
            confirmPassword.setEditable(true);
        }
        Arrays.fill(pchars, '#');
        Arrays.fill(cchars, '#');
    }

    /**
     * Add ui controls
     */
    private void addControls() {
        unitizeCheckBox = new JCheckBox("Unitize");
        unitizeCheckBox.addActionListener(buttonComboListener);
        uqcCheckBox = new JCheckBox("Unitize QC");
        uqcCheckBox.addActionListener(buttonComboListener);
        codingCheckBox = new JCheckBox("Coding");
        codingCheckBox.addActionListener(buttonComboListener);
        codingqcCheckBox = new JCheckBox("Coding QC");
        codingqcCheckBox.addActionListener(buttonComboListener);
        qaCheckBox = new JCheckBox("QA");
        qaCheckBox.addActionListener(buttonComboListener);
        listingCheckBox = new JCheckBox("Listing");
        listingCheckBox.addActionListener(buttonComboListener);
        tallyCheckBox = new JCheckBox("Tally");
        tallyCheckBox.addActionListener(buttonComboListener);
        tlCheckBox = new JCheckBox("Team Leader");
        tlCheckBox.addActionListener(buttonComboListener);
        adminCheckBox = new JCheckBox("Admin");
        adminCheckBox.addActionListener(buttonComboListener);
        teamsCombo.addActionListener(buttonComboListener);

        adminUsersCheckBox = new JCheckBox("Users");
        adminUsersCheckBox.addActionListener(buttonComboListener);
        adminProjectCheckBox = new JCheckBox("Project");
        adminProjectCheckBox.addActionListener(buttonComboListener);
        adminBatchCheckBox = new JCheckBox("Batch");
        adminBatchCheckBox.addActionListener(buttonComboListener);
        adminEditCheckBox = new JCheckBox("Global Edit");
        adminEditCheckBox.addActionListener(buttonComboListener);
        adminImportCheckBox = new JCheckBox("Import");
        adminImportCheckBox.addActionListener(buttonComboListener);
        adminExportCheckBox = new JCheckBox("Export");
        adminExportCheckBox.addActionListener(buttonComboListener);
        adminProfitCheckBox = new JCheckBox("Profit");
        adminProfitCheckBox.addActionListener(buttonComboListener);

        userName = new LTextField(40);
        fname = new LTextField(40);
        lname = new LTextField(40);
        team = new LTextField(40);
        userName.setDocument(getUpperPlainDocument());
        userName.setToolTipText("User Id may contain only letters and digits.");
        fname.setDocument(getPlainDocument());
        lname.setDocument(getPlainDocument());
        
        JLabel seperator_lbl_1 = new JLabel("    ");        
        JLabel seperator_lbl_2 = new JLabel("    ");        
        JLabel seperator_lbl_3 = new JLabel("    ");        

        password = new JPasswordField(32);
        confirmPassword = new JPasswordField(32);
        password.setDocument(getPlainDocument());
        confirmPassword.setDocument(getPlainDocument());

        dateField.getComponent(1).setMaximumSize(new Dimension(50, 20));
        field = (JTextField) dateField.getComponent(0);
        field.setColumns(10);
        dateField.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                //dateFieldPropertyChange(evt);
            }
        });

        //internal_volume_namePanel.add(dateField);

        rolePane.add(unitizeCheckBox);
        rolePane.add(uqcCheckBox);
        rolePane.add(codingCheckBox);
        rolePane.add(codingqcCheckBox);
        rolePane.add(listingCheckBox);
        rolePane.add(tallyCheckBox);
        rolePane.add(qaCheckBox);
        rolePane.add(tlCheckBox);
        rolePane.add(adminCheckBox);
        
        namePane.add(0, 0, "User Id:", userName);
        namePane.add(0, 1, "", new Label());
        namePane.add(0, 2, "First Name:", fname);
        namePane.add(0, 3, "", new Label());
        namePane.add(0, 4, "Last Name:", lname);
        namePane.add(0, 5, "", new Label());
        namePane.add(0, 6, "Join Date:", dateField);
        namePane.add(0, 7, "Roles:", rolePane);      
        namePane.add(0, 8, "Admin:", adminPrivPane);        
        namePane.add(0, 9, "Team:", teamsCombo);      
        namePane.add(0, 10, "", seperator_lbl_1);
        namePane.add(0, 11, "Password:", password);
        namePane.add(0, 12, "", seperator_lbl_2);
        namePane.add(0, 13, "Confirm Password:", confirmPassword);
        namePane.add(0, 14, "", seperator_lbl_3);

        adminPrivPane.add(adminUsersCheckBox);
        adminPrivPane.add(adminProjectCheckBox);
        adminPrivPane.add(adminBatchCheckBox);
        adminPrivPane.add(adminEditCheckBox);
        adminPrivPane.add(adminImportCheckBox);
        adminPrivPane.add(adminExportCheckBox);
        adminPrivPane.add(adminProfitCheckBox);

        selectPanel.add(namePane, BorderLayout.CENTER);
        pack();
    }

    /**
     * Check password filled if it is true then save the user data     
     * @return true if the data is saved successfully, false if password is entered
     *              and it does not match.
     */
    private boolean save() {
        char[] pchars = password.getPassword();
        String pword = "";
        if (pchars.length > 0) {
            pword = MD5.computeDigest(pchars);
            Arrays.fill(pchars, '\u0000');
            char[] cchars = confirmPassword.getPassword();
            String cword = MD5.computeDigest(cchars);
            Arrays.fill(cchars, '\u0000');
            if (!pword.equals(cword)) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this,
                        "Password must match Confirm Password." + "\nPlease re-enter.",
                        "Password Error",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        final ClientTask task;
        int teamsId = teamsModel.getSelectedId();
        if (teamsId < 0) {
            teamsId = 0;
        }
        usersData = new UsersData();
        usersData.users_id = usersId;
        usersData.teams_id = teamsId;
        usersData.user_name = userName.getText();
        usersData.first_name = fname.getText();
        usersData.last_name = lname.getText();
        usersData.unitize = unitizeCheckBox.isSelected() ? "Yes" : "No";
        usersData.uqc = uqcCheckBox.isSelected() ? "Yes" : "No";
        usersData.coding = codingCheckBox.isSelected() ? "Yes" : "No";
        usersData.codingqc = codingqcCheckBox.isSelected() ? "Yes" : "No";
        usersData.qa = qaCheckBox.isSelected() ? "Yes" : "No";
        usersData.listing = listingCheckBox.isSelected() ? "Yes" : "No";
        usersData.tally = tallyCheckBox.isSelected() ? "Yes" : "No";
        usersData.teamLeader = tlCheckBox.isSelected() ? "Yes" : "No";
        usersData.admin = adminCheckBox.isSelected() ? "Yes" : "No";
        usersData.canAdminUsers = adminUsersCheckBox.isSelected() ? "Yes" : "No";
        usersData.canAdminProject = adminProjectCheckBox.isSelected() ? "Yes" : "No";
        usersData.canAdminBatch = adminBatchCheckBox.isSelected() ? "Yes" : "No";
        usersData.canAdminEdit = adminEditCheckBox.isSelected() ? "Yes" : "No";
        usersData.canAdminImport = adminImportCheckBox.isSelected() ? "Yes" : "No";
        usersData.canAdminExport = adminExportCheckBox.isSelected() ? "Yes" : "No";
        usersData.canAdminProfit = adminProfitCheckBox.isSelected() ? "Yes" : "No";
        usersData.password = pword;
        usersData.dateOfJoin = field.getText();
        task = new TaskSendUsersData(usersData);
        task.enqueue(this);
        return true;
    }
}
