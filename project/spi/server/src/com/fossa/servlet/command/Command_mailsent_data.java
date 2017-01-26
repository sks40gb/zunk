/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.MailsentData;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlReader;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.session.UserTask;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.sql.Timestamp;
import java.util.Date;

/**
 * This class handles the updation of mail sent details
 * @author ashish
 */
class Command_mailsent_data implements Command{

    private final String UNION = " union distinct ";    
    private PreparedStatement pst;    
    private Statement st;
    private int mailsentId = 0;
    private ResultSet rs;
    private String selectString = "";

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {   
        int usersId = task.getUsersId();
        int senderUsersId = 0;        
        Element givenValueList = action;
        
        Node firstChild = givenValueList.getFirstChild();
        while (firstChild != null && firstChild.getNodeType() != Node.ELEMENT_NODE) { 
            firstChild = firstChild.getNextSibling();
        }
        if (firstChild == null) {
            return null;
        }
        MailsentData data = new MailsentData();
        // fill in the int and String fields of the MailData
        try {
            XmlReader xmlReader = new XmlReader();
            xmlReader.decode(givenValueList, data);
            
        } catch (Throwable t) {
            logger.error("Exception while reading the XMLReader." + t);
            StringWriter sw = new StringWriter();
            t.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(t);
        }
        if (data == null) {
            return null;
        }
        // update or insert the mail row contained in data        
        try{            
            st = dbTask.getStatement();
            Date date = new Date();
            long time = date.getTime();
            Timestamp timestamp = new Timestamp(time);
            
            if (data.mailsentId > 0
                && data.recipientList.equals("")) {
                // update existing mail                
                ResultSet rs = st.executeQuery(SQLQueries.SEL_SENDER +data.mailsentId);
                if (! rs.next()) {
                    // mail does not exist
                    return null;  // TBD:  set error message?
                }
                senderUsersId = rs.getInt(1);                
                    if (usersId == senderUsersId) {
                        // sender is deleting sent mail -- change status to deleted
                        PreparedStatement updmailsent =  task.prepareStatement(dbTask,SQLQueries.UPD_MAILSENT);
                        updmailsent.setInt(1, usersId);
                        updmailsent.setInt(2, data.mailsentId);
                        updmailsent.executeUpdate();
                    } else {
                        // recipient is deleting received email -- change status to deleted
                        PreparedStatement updmailrecv =  task.prepareStatement(dbTask,SQLQueries.UPD_MAILRECVD);
                        updmailrecv.setInt(1, usersId);
                        updmailrecv.setInt(2, data.mailsentId);
                        updmailrecv.executeUpdate();
                        
                        pst.executeUpdate();
                        pst.close();
                    }
                    return null;                
            }
            // send, reply, replyall or forward of mail
            parseRecipientList(data.recipientList);
            pst = task.prepareStatement(dbTask,"INSERT INTO mailsent(sender_users_id,recipient_list,status" +
                                               ",subject,text,time_sent) values(?,?,'Active',?,?,'"+timestamp+"')");
            pst.setInt(1, usersId);
            pst.setString(2, data.recipientList);
            pst.setString(3, data.subject);
            pst.setString(4, data.text);
            pst.executeUpdate();
            pst.close();
            
            //Get the top mail sent id.
            rs = st.executeQuery(SQLQueries.SEL_TOP_SENDERS);            
            if (rs.next()) {
                mailsentId = rs.getInt(1);
                rs.close();
            } else {
                Log.quit("(Command_mailsent_data) no last_insert_id");
            }
            Log.print("(Command_mailsent_data.run) text is: " + data.text);

            // write a row for each recipient
            pst = task.prepareStatement(dbTask,SQLQueries.INS_MAIL_RECV);
            if (selectString.length() > 0) {
                rs = st.executeQuery(selectString);
                while (rs.next()) {                    
                    pst.setInt(1, rs.getInt(1));
                    pst.setInt(2,mailsentId);
                    pst.executeUpdate();                   
                }
                rs.close();
            }
            pst.close();
        } catch (SQLException sql) {
            CommonLogger.printExceptions(this, "SQLException while inserting the mail sent data.", sql);
            return null;
        } catch (Exception exc) {
            CommonLogger.printExceptions(this, "Exception while inserting the mail sent data.",exc);
            return null;
        }
        return null;
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
                    if (projectTeams.length() == 0) {
                        projectTeams = "'" + text[i].substring(0, pos) + "'";
                    } else {
                        projectTeams = projectTeams + ",'" + text[i].substring(0, pos) + "'";
                    }                    
                } else if ((pos = text[i].indexOf(", All Leaders")) > 0) {                    
                    // project, all team leaders
                    if (projectLeaders.length() == 0) {
                        projectLeaders = "'" + text[i].substring(0, pos) + "'";
                    } else {
                        projectLeaders = projectLeaders + ",'" + text[i].substring(0, pos) + "'";
                    }
                } else if ((pos = text[i].indexOf(", All Team")) > 0) {                    
                    // team, all members
                    if (teams.length() == 0) {
                        teams = "'" + text[i].substring(0, pos) + "'";
                    } else {
                        teams = teams + ",'" + text[i].substring(0, pos) + "'";
                    }
                } else if ((pos = text[i].indexOf(", Leader")) > 0) {                    
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
            appendSelectString(SQLQueries.SEL_USERSID);
        } else if (users.length() > 0) {
            appendSelectString("SELECT users_id  FROM users WHERE user_name in (" + users + ") and active =1");         

        }
        if (teams.length() > 0) {
            appendSelectString("SELECT U.users_id  FROM teams T inner join users U on (U.teams_id = T.teams_id) " +
                                 "WHERE T.team_name in (" + teams + ") and U.active = 1");
        }
        if (allTeamLeaders) {
            appendSelectString("SELECT distinct U.users_id FROM teams T, users U WHERE T.users_id = U.users_id");
        } else if (teamLeaders.length() > 0) {
            appendSelectString("SELECT distinct U.users_id  FROM users U, teams T WHERE (T.users_id = U.users_id " +
                                "and T.team_name in (" + teamLeaders + "))");
        }
        if (projectTeams.length() > 0) {
        	appendSelectString("SELECT distinct U.users_id  FROM project P inner join volume V on V.project_id = P.project_id left join batch B on (V.volume_id = B.volume_id AND B.lft >= P.lft and B.rgt <= P.rgt) inner join teamsqueue TQ on (TQ.batch_id = B.batch_id) inner join teams T on (T.teams_id = TQ.teams_id) inner join users U on (U.teams_id = T.teams_id) WHERE P.project_name in ( " + projectTeams + " )");
        	appendSelectString("SELECT distinct U.users_id FROM project P  inner join volume V on V.project_id = P.project_id  left join batch B on (V.volume_id = B.volume_id  AND B.lft >= P.lft and B.rgt <= P.rgt)  inner join usersqueue UQ on (UQ.batch_id = B.batch_id) inner join users U on (U.users_id = UQ.users_id) WHERE P.project_name in ( " + projectTeams + "  )");
        	appendSelectString("SELECT distinct U.users_id FROM project P  inner join volume V on V.project_id = P.project_id  left join batch B on (V.volume_id = B.volume_id  AND B.lft >= P.lft and B.rgt <= P.rgt)  inner join usersqueue UQ on (UQ.batch_id = B.batch_id)  inner join users U on (U.users_id = UQ.users_id) WHERE P.project_name in ( " + projectTeams + " )");
        }
        if (projectLeaders.length() > 0) {
        	appendSelectString("SELECT distinct U.users_id FROM project P inner join volume V on V.project_id = P.project_id left join batch B on (V.volume_id = B.volume_id AND B.lft >= P.lft and B.rgt <= P.rgt)      inner join teamsqueue TQ on (TQ.batch_id = B.batch_id) inner join teams T on (T.teams_id = TQ.teams_id) inner join users U on (U.users_id = T.users_id)  WHERE P.project_name in ( " + projectLeaders + " )");
        	appendSelectString("SELECT distinct U.users_id FROM project P inner join volume V on V.project_id = P.project_id inner join teamsvolume TV on (TV.volume_id = V.volume_id) inner join teams T on (T.teams_id = TV.teams_id) inner join users U on (U.users_id = T.users_id) WHERE P.project_name in ( " + projectLeaders + "  )");
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
    
    public boolean isReadOnly() {
        return true;
    }
}
