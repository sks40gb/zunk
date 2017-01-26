/* $Header: /home/common/cvsarea/ibase/dia/src/beans/AddEditTeams.java,v 1.12.6.1 2006/02/21 17:02:45 nancy Exp $ */
package beans;

import client.ClientTask;
import client.TaskExecuteQuery;
import client.TaskSendTeams;
import com.lexpar.util.Log;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import model.QueryComboModel;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import ui.TeamAdminPage;

/**
 * Called from ui.TeamAdminPage, this dialog allows the user to add, edit
 * or delete teams table data.  Each user may be assigned to one team and the
 * team leader must be a member of the team.
 * 
 * @author  Nancy
 * 
 * @see client.TaskSendTeams
 * @see server.Handler_teams_data
 * @see ui.TeamAdminPage
 */
final public class AddEditTeams extends javax.swing.JDialog {

    private JPanel selectPanel = new javax.swing.JPanel();
    private JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    private LGridBag teamPane = new LGridBag();
    private LTextField teamName;
    private LComboBox leaderCombo;
    private int teamsId;
    private QueryComboModel leaderModel;
    private TeamAdminPage parent;

    /**
     * Creates new form AddEditTeams.
     * @param parent the component to use in positioning this dialog
     * @param teamsId teams.teams_id of the team to be edited; 0 when
     * adding a team
     */
    public AddEditTeams(Component parent, int teamsId) {
        super(JOptionPane.getFrameForComponent(parent));
        this.teamsId = teamsId;
        this.parent = (TeamAdminPage) parent;
        getContentPane().add(selectPanel, BorderLayout.CENTER);
        selectPanel.setLayout(new BorderLayout());
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
        addWindowListener(new java.awt.event.WindowAdapter() {

            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });       
        setTitle("SPiCA Team Definition");

        getUserData(); // calls addControls

    }

    /**
     * Get user record by team.
     */
    private void getUserData() {
        if (teamsId > 0) {
            leaderModel = new QueryComboModel("get users names by team", /* required->*/ true, new String[]{Integer.toString(teamsId)}, "");
        } else {
            // This is a new team, so get a user who is not yet assigned
            // to a team to be the leader.
            leaderModel = new QueryComboModel("get unassigned users names", /* required->*/ true);
        }
        leaderCombo = new LComboBox(40);
        leaderCombo.setModel(leaderModel);
        //Get the team entry.
        if (teamsId > 0) {
            final ClientTask task;
            task = new TaskExecuteQuery("teams by id select", Integer.toString(teamsId));
            task.setCallback(new Runnable() {

                public void run() {
                    getTeamByIdDataEntry((ResultSet) task.getResult());
                }
            });
            boolean ok = task.enqueue(this);
        }
        addControls();
    }

    /**
     * Load the user's data.
     * @param queryResult - ResultSet returned by ClientTask in getUserData
     */
    private void getTeamByIdDataEntry(ResultSet queryResult) {
        try {
            if (queryResult.next()) {
                teamName.setText(queryResult.getString(1));
                leaderCombo.setSelectedIndex(leaderModel.indexOf(queryResult.getInt(2)));
            }
        } catch (SQLException e) {
            Log.quit(e);
        }
    }
    
    private ActionListener buttonListener = new ActionListener() {
        
        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                Object source = A.getSource();
                if (source == okButton) {
                    if (save()) {
                        dispose();
                        parent.setButtonsEnabled(true);

                    }
                } else if (source == cancelButton) {
                    dispose();
                    parent.setButtonsEnabled(true);
                } else {
                    checkOkSetEnabled();
                }
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    };

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

    /**
     * Make ok button button enabled.
     */
    private void checkOkSetEnabled() {
        if (teamName.getText().equals("")) {           
            okButton.setEnabled(false);
        } else {
            okButton.setEnabled(true);
        }
    }

    private void addControls() {
        teamName = new LTextField(40);
        teamName.setText("");
        teamName.setDocument(getPlainDocument());

        teamPane.add(0, 0, "Team Name:", teamName);
        teamPane.add(0, 4, "Leader:", leaderCombo);
        selectPanel.add(teamPane, BorderLayout.CENTER);
        pack();
    }

    /**
     * Save the team name.
     * @return
     */
    private boolean save() {
        int id = leaderModel.getSelectedId();
        if (id < 0) {
            // no leader for this team
            id = 0;
        }
        final ClientTask task = new TaskSendTeams(teamsId, id, teamName.getText());
        task.enqueue(this);

        return true;
    }
    
    /**
     * On closing this form enable the parent window.
     * @param evt
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        parent.setButtonsEnabled(true);
    }
}
