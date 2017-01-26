/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_mailsent_data.java,v 1.12.6.5 2006/03/22 20:27:15 nancy Exp $ */
package server;

import common.Log;
import common.msg.MessageReader;
import common.MailsentData;

import java.sql.*;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Handler for mailsent message, this class stores <code>mailsent</code> data
 * and parses <code>mailsentData.recipientList</code> for users_id's to store
 * the <code>mailsent.mailsent_id</code> in a <code>mailreceived</code> row
 * for each recipient.
 * @see client.TaskSendMailsentData
 * @see common.MailsentData
 */
final public class Handler_mailsent_data extends Handler {
    
    final String UNION = " union distinct ";
    
    PreparedStatement pst;
    Connection con;
    Statement st;

    int mailsentId = 0;

    ResultSet rs;
    String selectString = "";

    /**
     * This class cannot be instantiated.
     */
    public Handler_mailsent_data() {
        //Log.print("Handler_mailsent_data");
    }

    public void run (ServerTask task, Element action) throws SQLException {
        //Log.print("Handler_mailsent_data run");
        int usersId = task.getUsersId();
        int senderUsersId = 0;
        int[] recipients;
        Element givenValueList = action;

        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild == null) {
            return;
        }
        MailsentData data = new MailsentData();
        // fill in the int and String fields of the MailData
        try {
            MessageReader.decode(givenValueList, data);
        } catch (Throwable t) {
            Log.quit(t);
        }
        if (data == null) {
            return;
        }
        // update or insert the mail row contained in data
        //Log.print("(Handler_mailsent_data.run) users_id/mailsentId="
        //          + usersId + "/" + data.mailsentId);
        con = task.getConnection();
        st = task.getStatement();
        if (data.mailsentId > 0
            && data.recipientList.equals("")) {
            // update existing mail
            ResultSet rs = st.executeQuery(
                "select sender_users_id"
                +" from mailsent"
                +"   where mailsent_id ="+data.mailsentId);
            if (! rs.next()) {
                // mail does not exist
                return;  // TBD:  set error message?
            }
            senderUsersId = rs.getInt(1);
            //Log.print("  ... delete " + data.recipientList);
            //if (data.recipientList.equals("")) {
                // a delete
                if (usersId == senderUsersId) {
                    // sender is deleting sent mail -- change status to deleted
                    task.executeUpdate(
                        "update mailsent"
                        +" set status='Deleted'"
                        +" where sender_users_id = "+usersId
                        +"   and mailsent_id = "+data.mailsentId);
                } else {
                    // recipient is deleting received email -- change status to deleted
                    task.executeUpdate(
                        "update mailreceived"
                        +" set status='Deleted'"
                        +" where recipient_users_id = "+usersId
                        +"   and mailsent_id = "+data.mailsentId);
                    pst.executeUpdate();
                    pst.close();
                }
                return;
            //}
        }
        // send, reply, replyall or forward of mail
        parseRecipientList(data.recipientList);
        //Log.print("   ... after parse " + data.recipientList);
        pst = task.prepareStatement(
            "insert into mailsent set sender_users_id = ?,"
            +" recipient_list = ?,"
            +" status = 'Active',"
            +" subject = ?, text = ?,"
            +" time_sent = ?");
        pst.setInt(1, usersId);
        pst.setString(2, data.recipientList);
        pst.setString(3, data.subject);
        pst.setBytes(4, data.text.getBytes());
        pst.setLong(5, System.currentTimeMillis());
        pst.executeUpdate();
        pst.close();
        rs = st.executeQuery(
            "select last_insert_id()");
        if (rs.next()) {
            mailsentId = rs.getInt(1);
            rs.close();
        } else {
            Log.quit("(Handler_mailsent_data) no last_insert_id");
        }
        Log.print("(Handler_mailsent_data.run) text is: " + data.text);

        //Log.print("   ... last_insert_id() " + mailsentId);

        // write a row for each recipient
        pst = task.prepareStatement(
                "insert into mailreceived"
                +" set recipient_users_id = ?,"
                +" mailsent_id = "+mailsentId+","
                +" status = 'New'");
        //Log.print("   ... selectString " + selectString);
        if (selectString.length() > 0) {
            rs = st.executeQuery(selectString);
            while (rs.next()) {
                //Log.print("(Handler_mailsent_data.run) users_id " + rs.getInt(1));
                pst.setInt(1, rs.getInt(1));
                pst.executeUpdate();
            }
            rs.close();
        }
        pst.close();
    }

    /**
     * Given a semicolon-separated text list of mail recipients,
     * figure out the users_ids of all recipients and create ResultSets
     * of their users_id's.
     * 
     * TBD: (soon) Worry about apostrophes in the names being quoted, below.
     * TBD:  Worry about duplicate recipient users_ids, e.g. a user also part of a team.
     * 
     * @param list - the text list of recipients
     */
    private void parseRecipientList(String list) {
        String users = "";
        String teams = "";
        String teamLeaders = "";
        String projectTeams = "";
        String projectLeaders = "";
        boolean allTeamLeaders = false;
        boolean allUsers = false;
        int pos;
        String str;
        String[] text = list.split(";");

        // Build a string containing the comma-separated, quoted names of the
        // recipients for each category, 
        // user, team, team leader, project teams, project teams leaders.
        //
        // NOTE:  The text in the following conditions must match the text
        // generated in Handler_request_recipient_list!
        //
        for (int i = 0; i < text.length; i++) {
            text[i] = text[i].trim();
            if (text[i].indexOf(", ") > 0) {
                if ((pos = text[i].indexOf(", All Teams")) > 0) {
                    //Log.print("(Handler_mailsent_data.parseRecipientList) all teams all members " + text[i]);
                    // project, all teams
                    if (projectTeams.length() == 0) {
                        projectTeams = "'" + text[i].substring(0, pos) + "'";
                    } else {
                        projectTeams = projectTeams + ",'" + text[i].substring(0, pos) + "'";
                    }
                    //Log.print("   ... result " + projectTeams);
                } else if ((pos = text[i].indexOf(", All Leaders")) > 0) {
                    //Log.print("(Handler_mailsent_data.parseRecipientList) all team leaders " + text[i]);
                    // project, all team leaders
                    if (projectLeaders.length() == 0) {
                        projectLeaders = "'" + text[i].substring(0, pos) + "'";
                    } else {
                        projectLeaders = projectLeaders + ",'" + text[i].substring(0, pos) + "'";
                    }
                } else if ((pos = text[i].indexOf(", All Team")) > 0) {
                    //Log.print("(Handler_mailsent_data.parseRecipientList) all members " + text[i]);
                    // team, all members
                    if (teams.length() == 0) {
                        teams = "'" + text[i].substring(0, pos) + "'";
                    } else {
                        teams = teams + ",'" + text[i].substring(0, pos) + "'";
                    }
                } else if ((pos = text[i].indexOf(", Leader")) > 0) {
                    //Log.print("(Handler_mailsent_data.parseRecipientList) team leader " + text[i]);
                    // team leader
                    if (teamLeaders.length() == 0) {
                        teamLeaders = "'" + text[i].substring(0, pos) + "'";
                    } else {
                        teamLeaders = teamLeaders + ",'" + text[i].substring(0, pos) + "'";
                    }
                }
            } else if (text[i].equals("All Team Leaders")) {
                allTeamLeaders = true;
            } else if (text[i].equals("All Users")) {
                allUsers = true;
            } else {
                // user
                //Log.print("(Handler_mailsent_data.parseRecipientList) user " + text[i]);
                if (users.length() == 0) {
                    users = "'" + text[i] + "'";
                } else {
                    users = users + ",'" + text[i] + "'";
                }
            }
        }
        // Create a string containing a query that will retrieve a ResultSet of
        // users_ids for each category built, above.
        if (allUsers) {
            appendSelectString(
                "select users_id from users where active");
        } else if (users.length() > 0) {
            appendSelectString(
                "select users_id from users"
                +" where user_name in (" + users + ")"
                +" and active");
        }
        if (teams.length() > 0) {
            appendSelectString(
                "select U.users_id from teams T"
                +" inner join users U on (U.teams_id = T.teams_id)" 
                +" where T.team_name in (" + teams + ")"
                +"   and U.active");
        }
        if (allTeamLeaders) {
            appendSelectString(
                "select distinct U.users_id from teams T, users U"
                +" where T.users_id = U.users_id");
        } else if (teamLeaders.length() > 0) {
            appendSelectString(
                "select distinct U.users_id from users U, teams T"
                +" where (T.users_id = U.users_id" 
                +"   and T.team_name in (" + teamLeaders + ")");
        }
        if (projectTeams.length() > 0) {
            appendSelectString(
                "select distinct U.users_id" 
                +" from project P"
                +" inner join volume V using (project_id)"
                +" left join batch B on (V.volume_id = B.volume_id"
                +"   AND B.lft >= P.lft and B.rgt <= P.rgt)"
                +" inner join teamsqueue TQ on (TQ.batch_id = B.batch_id)"
                +" inner join teams T on (T.teams_id = TQ.teams_id)"
                +" inner join users U on (U.teams_id = T.teams_id)"
                +" where P.project_name in (" + projectTeams + ")");
            appendSelectString(
                "select distinct U.users_id"
                +" from project P"
                +" inner join volume V using (project_id)"
                +" left join batch B on (V.volume_id = B.volume_id"
                +"   AND B.lft >= P.lft and B.rgt <= P.rgt)"
                +" inner join usersqueue UQ on (UQ.batch_id = B.batch_id)"
                +" inner join users U on (U.users_id = UQ.users_id)"
                +" where P.project_name in (" + projectTeams + ")");
            appendSelectString(
                "select distinct U.users_id"
                +" from project P"
                +" inner join volume V using (project_id)"
                +" left join batch B on (V.volume_id = B.volume_id"
                +"   AND B.lft >= P.lft and B.rgt <= P.rgt)"
                +" inner join assignment A on (B.batch_id = A.batch_id)"
                +" inner join users U on (U.users_id = A.users_id)"
                +" where P.project_name in (" + projectTeams + ")");
            //Log.print("   ... select " + selectString);
        }
        if (projectLeaders.length() > 0) {
            appendSelectString(
                "select distinct U.users_id"
                +" from project P"
                +" inner join volume V using (project_id)"
                +" left join batch B on (V.volume_id = B.volume_id"
                +"   AND B.lft >= P.lft and B.rgt <= P.rgt)"
                +" inner join teamsqueue TQ on (TQ.batch_id = B.batch_id)"
                +" inner join teams T on (T.teams_id = TQ.teams_id)"
                +" inner join users U on (U.users_id = T.users_id)"
                +" where P.project_name in (" + projectLeaders + ")");
            appendSelectString(
                "select distinct U.users_id"
                +" from project P"
                +" inner join volume V using (project_id)"
                +" inner join teamsvolume TV on (TV.volume_id = V.volume_id)"
                +" inner join teams T on (T.teams_id = TV.teams_id)"
                +" inner join users U on (U.users_id = T.users_id)"
                +" where P.project_name in (" + projectLeaders + ")");
        }
    }

    /**
     * Append the given select string to the existing text in selectString.
     * @param text - a string containing the text of a select statement
     */
    private void appendSelectString(String text) {
        if (selectString.length() < 1) {
            selectString = text;
        } else {
            selectString = selectString + UNION + text;
        }
    }
}
