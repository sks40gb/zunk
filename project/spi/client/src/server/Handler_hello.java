/* $Header: /home/common/cvsarea/ibase/dia/src/server/Handler_hello.java,v 1.39.6.4 2006/03/14 15:08:46 nancy Exp $ */

package server;

import com.lexpar.util.Log;
import common.msg.MD5;
import common.msg.MessageWriter;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.TimeZone;
import org.w3c.dom.Element;

/**
 * Handler for hello (login) message
 */
public class Handler_hello extends Handler {

    /**
     * This class cannot be instantiated.
     */
    public Handler_hello() {}

    public void run (ServerTask task, Element action)
    throws ServerFailException, IOException, SQLException {
        String userName = getAttribute(action, A_NAME);
        String password = MD5.computeDigest(common.msg.XmlUtil.getTextFromNode(action));
        String clientVersion = getAttribute(action, A_VERSION);
        String clientSessionKey = getAttribute(action, A_SESSION_KEY);
        boolean isAdmin = "YES".equals(getAttribute(action, A_ADMIN));
        String newPassword = getAttribute(action, A_NEW_PASSWORD);

        Connection con = task.getConnection();
        Statement st = task.getStatement();
        ResultSet rs;

        // Check that this server task isn't already logged in
        if (task.getUsersId() != 0) {
            System.out.println("FATAL login: User is already logged in");
            throw new FatalException("login: User is already logged in");
        }

        // Get allowed version information
        rs = task.getStatement().executeQuery(
            "select min_client_version, max_client_version, min_supported_version"
            +" from svradmin"
            +" where dummy_key = 0");
        rs.next();
        String minVersion = rs.getString(1);
        String maxVersion = rs.getString(2);
        String minSupportedVersion = rs.getString(3);
        rs.close();

        //Check that this version is supported, at least for upgrading
        if (clientVersion.compareTo(minSupportedVersion) < 0) {
            Log.print(task.getSessionId()
                      +" LOGIN "+userName+" FAILED version="+clientVersion);
            throw new ServerFailException(
                "Versions prior to "+minSupportedVersion+" are no longer supported."
            +"  Please reinstall DIA.");
        }

        PreparedStatement pst = con.prepareStatement(
            "SELECT password, users_id,"
            +"   unitize, uqc, coding, codingqc, qa, team_leader, admin,"
            +"   admin_users, admin_project, admin_batch,"
            +"   admin_edit, admin_import, admin_export, admin_profit"
            +" FROM users"
            +" where user_name=?"
            +" lock in share mode");
        pst.setString(1, userName);
        rs = pst.executeQuery();
        int usersId;
        boolean permissionUnitize;
        boolean permissionUqc;
        boolean permissionCoding;
        boolean permissionCodingqc;
        boolean permissionQa;
        boolean permissionTeamLeader;
        boolean permissionAdmin;
        boolean permissionAdminUsers;
        boolean permissionAdminProject;
        boolean permissionAdminBatch;
        boolean permissionAdminEdit;
        boolean permissionAdminImport;
        boolean permissionAdminExport;
        boolean permissionAdminProfit;
        if (! rs.next()) {
            pst.close();
            Log.print(task.getSessionId()+" LOGIN "+userName+" FAILED Invalid user name");
            throw new ServerFailException("Can't connect -- Invalid user name");
        } else {
            usersId = rs.getInt(2);
            permissionUnitize =  "Yes".equalsIgnoreCase(rs.getString(3));
            permissionUqc =      "Yes".equalsIgnoreCase(rs.getString(4));
            permissionCoding =   "Yes".equalsIgnoreCase(rs.getString(5));
            permissionCodingqc = "Yes".equalsIgnoreCase(rs.getString(6));
            permissionQa =       "Yes".equalsIgnoreCase(rs.getString(7));
            permissionTeamLeader =    "Yes".equalsIgnoreCase(rs.getString(8));
            permissionAdmin =    "Yes".equalsIgnoreCase(rs.getString(9));
            permissionAdminUsers =    "Yes".equalsIgnoreCase(rs.getString(10));
            permissionAdminProject =    "Yes".equalsIgnoreCase(rs.getString(11));
            permissionAdminBatch =    "Yes".equalsIgnoreCase(rs.getString(12));
            permissionAdminEdit =    "Yes".equalsIgnoreCase(rs.getString(13));
            permissionAdminImport =    "Yes".equalsIgnoreCase(rs.getString(14));
            permissionAdminExport =    "Yes".equalsIgnoreCase(rs.getString(15));
            permissionAdminProfit =    "Yes".equalsIgnoreCase(rs.getString(16));

            Log.print("Handler_hello: clientSessionKey='"+clientSessionKey+"'");
            if (clientSessionKey.length() > 0) {
                pst.close();
                pst = con.prepareStatement(
                    "select session_id, users_id from session"
                    +" where session_key=?");
                pst.setString(1, clientSessionKey);
                rs = pst.executeQuery();
                if (rs.next()) {
                    int sessionId = rs.getInt(1);
                    if (usersId == rs.getInt(2)) {
                        pst.close();
                        // Successful reconnect
                        //st.executeUpdate(
                        //    "delete from session where session_id="+sessionId);
                        //Tables.session.executeDelete(task,sessionId);
                        task.executeUpdate("delete from session where session_id="+sessionId);
                        EventLog.logout(task, usersId);
                    } else {
                        pst.close();
                        Log.print(task.getSessionId()
                                  +" LOGIN "+userName+" FAILED bad reconnect users_id");
                        throw new ServerFailException("Please provide a password");
                    }
                } else {
                    pst.close();
                    Log.print(task.getSessionId()
                              +" LOGIN "+userName+" FAILED no reconnect session");
                    throw new ServerFailException("Please provide a password");
                }
            } else if (! rs.getString(1).equals(password)) {
                pst.close();
                Log.print(task.getSessionId()+" LOGIN "+userName+" FAILED Invalid password");
                throw new ServerFailException("Can't connect -- Invalid password");
            }
        }

        // For the viewer app, allow only one login
        if (isAdmin) {
            if (! permissionAdmin & ! permissionTeamLeader) {
                throw new ServerFailException(
                    "Administration application not permitted for this userid");
            }
        } else {
            if (! permissionUnitize
                && ! permissionUqc
                && ! permissionCoding
                && ! permissionCodingqc
                && ! permissionQa)
            {
                throw new ServerFailException(
                    "Viewer application not permitted for this userid");
            }

            rs = st.executeQuery("select 0 from session"
                                 +" where users_id ="+usersId
                                 +"   and not is_admin"
                                 +"   and live"
                                 +" lock in share mode");
            if (rs.next()) {
                rs.close();
                Log.print(task.getSessionId()
                          +" LOGIN "+userName+" FAILED User name in use");
                // Commit, to remove old session in case this was a reconnect
                // TBD: HOW TO COMMIT?
                throw new ServerFailException("Can't connect -- User name in use");
            }
        }

        // Create a session key
        String sessionKey = SecureKey.generate();

        // if new password given, replace the password in the users table
        if (newPassword.length() > 0) {
            PreparedStatement pst2 = con.prepareStatement(
                "update users"
                +" set password=?"
                +" where users_id=?");
            pst2.setString(1, newPassword);
            pst2.setInt(2, usersId);
            pst2.executeUpdate();
            pst2.close();
        }

        // set current age of this task -- use the update age
        task.setAge(task.getUpdateAge());

        // Insert user information in session record.
        //Tables.session.executeUpdate(task,task.getSessionId(),
        task.executeUpdate(
            "update session"
            +" set users_id ="  +usersId
            +"   , session_key = '"+sessionKey+"'"
            +"   , is_admin = "+(isAdmin ? 1 : 0)
            +"   , age = "+task.getAge()
            +" where session_id="+task.getSessionId());

        Log.print(" LOGIN "+userName);

        // Remember the user id, thus indicating that login has occurred.
        task.setUsersId(usersId);
        //EventLog.login(task);
        // Remember if this is an administrator task
        task.setAdmin(isAdmin);

        task.commitTransaction();

        MessageWriter writer = task.getMessageWriter();
        writer.startElement(T_ACCEPT);
        writer.writeAttribute(A_MIN_VERSION, minVersion);
        writer.writeAttribute(A_MAX_VERSION, maxVersion);
        writer.writeAttribute(A_SESSION_KEY, sessionKey);

        if (isAdmin) {
            if (clientVersion.compareTo("0.02.058") >= 0) {
                if (permissionTeamLeader) {
                     writer.writeAttribute(A_OK_TEAM_LEADER,"YES");
                }
                if (permissionAdmin) {
                     writer.writeAttribute(A_OK_ADMIN,"YES");
                }
                if (permissionAdminUsers) {
                     writer.writeAttribute(A_OK_ADMIN_USERS,"YES");
                }
                if (permissionAdminProject) {
                     writer.writeAttribute(A_OK_ADMIN_PROJECT,"YES");
                }
                if (permissionAdminBatch) {
                     writer.writeAttribute(A_OK_ADMIN_BATCH,"YES");
                }
                if (permissionAdminEdit) {
                     writer.writeAttribute(A_OK_ADMIN_EDIT,"YES");
                }
                if (permissionAdminImport) {
                     writer.writeAttribute(A_OK_ADMIN_IMPORT,"YES");
                }
                if (permissionAdminExport) {
                     writer.writeAttribute(A_OK_ADMIN_EXPORT,"YES");
                }
                if (permissionAdminProfit) {
                     writer.writeAttribute(A_OK_ADMIN_PROFIT,"YES");
                }
            }

        } else { // since ! isAdmin
            if (clientVersion.compareTo("0.01.028") >= 0) {
                if (permissionUnitize) {
                     writer.writeAttribute(A_OK_UNITIZE,"YES");
                }
                if (permissionUqc) {
                     writer.writeAttribute(A_OK_UQC,"YES");
                }
                if (permissionCoding) {
                     writer.writeAttribute(A_OK_CODING,"YES");
                }
                if (permissionCodingqc) {
                     writer.writeAttribute(A_OK_CODINGQC,"YES");
                }
                if (permissionQa) {
                     writer.writeAttribute(A_OK_QA,"YES");
                }
            }
        }

        if (clientVersion.compareTo("0.01.065") >= 0) {
            writer.writeAttribute(A_TIME_ZONE, TimeZone.getDefault().getID());
        }

        writer.endElement();
    }

    // helper method to get attribute, forcing value to "" if it is null
    // (for missing $IMPLIED attribute, get "" if we are using a validating
    // parser, but null if we are not.
    private String getAttribute(Element action, String attributeName) {
        String result = action.getAttribute(attributeName);
        return (result == null ? "" : result); 
    }
}
