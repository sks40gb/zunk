/*
 * Command_hello.java
 *
 * Created on 13 November, 2007, 1:08 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package com.fossa.servlet.command;

import com.fossa.servlet.common.CommonLogger;
import com.fossa.servlet.common.Log;
import com.fossa.servlet.common.SQLQueries;
import com.fossa.servlet.common.msg.MD5;
import com.fossa.servlet.common.msg.MessageWriter;
import com.fossa.servlet.common.msg.XmlUtil;
import com.fossa.servlet.dao.DBTask;
import com.fossa.servlet.exception.ServerFailException;
import com.fossa.servlet.exception.UserErrorMessage;
import com.fossa.servlet.session.UserTask;
import com.fossa.servlet.session.SecureKey;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimeZone;
import org.w3c.dom.Element;

/**
 * This class handles the logging of new user , starting new sessions
 * @author prakash
 */
public class Command_hello implements Command {

    public String execute(Element action, UserTask task, DBTask dbTask, MessageWriter writer) {

        String userName = getAttribute(action, A_NAME);
        String password = MD5.computeDigest(XmlUtil.getTextFromNode(action));

        String clientVersion = getAttribute(action, A_VERSION);
        String clientSessionKey = getAttribute(action, A_SESSION_KEY);
        boolean isAdmin = "YES".equals(getAttribute(action, A_ADMIN));
        String newPassword = getAttribute(action, A_NEW_PASSWORD);        
        Connection con = null;
        Statement getClientVersionStatement = null;
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
        boolean permissionListing;
        boolean permissionTally;        
        String error = "";

        try {
            con = dbTask.getConnection();
            getClientVersionStatement = dbTask.getStatement();
            ResultSet rs;
            // Check that this server task isn't already logged in
            /*TODO: Cyrus - plz check. Get this from UserSessionObject
            if (task.getUsersId() != 0) {                
            throw new FatalException("login: User is already logged in");
            }*/

            // Get allowed version information
            rs = getClientVersionStatement.executeQuery(SQLQueries.SEL_MIN_CLIENT);
            rs.next();
            String minVersion = rs.getString(1);
            String maxVersion = rs.getString(2);            
            rs.close();

            //Check that this version is supported, at least for upgrading
            /* TODO: Cyrus - need to implement the below LOC
            if (clientVersion.compareTo(minSupportedVersion) < 0) {
            Log.print(task.getSessionId()
            +" LOGIN "+userName+" FAILED version="+clientVersion);
            throw new ServerFailException(
            "Versions prior to "+minSupportedVersion+" are no longer supported."
            +"  Please reinstall DIA.");
            }*/
            
            //Get user details like userID,password etc for corresponding userName
            PreparedStatement pst = con.prepareStatement(SQLQueries.SEL_PASS);
            pst.setString(1, userName);
            rs = pst.executeQuery();

            if (!rs.next()) {
                pst.close();                
                throw new ServerFailException("Can't connect -- Invalid user name");
            } else {

                usersId = rs.getInt(2);
                permissionUnitize = "Yes".equalsIgnoreCase(rs.getString(3));
                permissionUqc = "Yes".equalsIgnoreCase(rs.getString(4));
                permissionCoding = "Yes".equalsIgnoreCase(rs.getString(5));
                permissionCodingqc = "Yes".equalsIgnoreCase(rs.getString(6));
                permissionQa = "Yes".equalsIgnoreCase(rs.getString(7));
                permissionTeamLeader = "Yes".equalsIgnoreCase(rs.getString(8));
                permissionAdmin = "Yes".equalsIgnoreCase(rs.getString(9));
                permissionAdminUsers = "Yes".equalsIgnoreCase(rs.getString(10));
                permissionAdminProject = "Yes".equalsIgnoreCase(rs.getString(11));
                permissionAdminBatch = "Yes".equalsIgnoreCase(rs.getString(12));
                permissionAdminEdit = "Yes".equalsIgnoreCase(rs.getString(13));
                permissionAdminImport = "Yes".equalsIgnoreCase(rs.getString(14));
                permissionAdminExport = "Yes".equalsIgnoreCase(rs.getString(15));
                permissionAdminProfit = "Yes".equalsIgnoreCase(rs.getString(16));
                permissionListing = "Yes".equalsIgnoreCase(rs.getString(17));
                permissionTally = "Yes".equalsIgnoreCase(rs.getString(18));
                //TODO: Cyrus - log below LOC
                //Log.print("Command_hello: clientSessionKey='"+clientSessionKey+"'");
                if (clientSessionKey.length() > 0) {
                    pst.close();
                    //Get sessionId for the corresponding session key
                    pst = con.prepareStatement(SQLQueries.SEL_SESSIONID);

                    pst.setString(1, clientSessionKey);
                    rs = pst.executeQuery();
                    if (rs.next()) {                        
                        if (usersId == rs.getInt(2)) {
                            pst.close();                                                
                        //TODO: Cyrus - very serious - Need to implement the below LOC
                        //task.executeUpdate("delete from session where session_id="+sessionId);
                        // EventLog.logout(task, usersId);
                        } else {
                            pst.close();
                            Log.print(task.getSessionId() + " LOGIN " + userName + " FAILED bad reconnect users_id");
                            error = "Please provide a password";

                            throw new ServerFailException(UserErrorMessage.providePassword);
                        }
                    } else {
                        pst.close();
                        Log.print(task.getSessionId() + " LOGIN " + userName + " FAILED no reconnect session");
                        error = "Please provide a password";
                        throw new ServerFailException(UserErrorMessage.providePassword);
                    }

                } else {
                    // For the viewer app, allow only one login
                    if (isAdmin) {
                        if (!permissionAdmin & !permissionTeamLeader) {
                            throw new ServerFailException(UserErrorMessage.adminAuthentication);
                        } else if (!rs.getString(1).equals(password)) {
                            pst.close();
                            Log.print(task.getSessionId() + " LOGIN " + userName + " FAILED Invalid password");
                            error = "Can't connect -- Invalid password";
                            throw new ServerFailException(UserErrorMessage.invalidPassword);
                        }
                    } else {
                        if (!permissionUnitize && !permissionUqc && !permissionCoding && !permissionCodingqc && 
                             !permissionQa && !permissionListing && !permissionTally) {
                            throw new ServerFailException(UserErrorMessage.userAuthentication);
                        }                       
                        PreparedStatement getSessionHoldLockPrepStmt = con.prepareStatement(SQLQueries.SEL_SESSION_HOLDLOCK);
                        getSessionHoldLockPrepStmt.setInt(1, usersId);
                        ResultSet getSessionHoldLockResultSet = getSessionHoldLockPrepStmt.executeQuery();
                        if (getSessionHoldLockResultSet.next()) {
                            getSessionHoldLockResultSet.close();
                            Log.print(task.getSessionId() + " LOGIN " + userName + " FAILED User name in use");
                            // Commit, to remove old session in case this was a reconnect
                            // TBD: HOW TO COMMIT?
                            throw new ServerFailException(UserErrorMessage.userInUse);
                        } else if (!rs.getString(1).equals(password)) {
                            pst.close();
                            Log.print(task.getSessionId() + " LOGIN " + userName + " FAILED Invalid password");
                            error = "Can't connect -- Invalid password";
                            throw new ServerFailException(UserErrorMessage.invalidPassword);
                        }
                    }
                }
                // Create a session key
                String sessionKey = SecureKey.generate();

                // if new password given, replace the password in the users table
                if (newPassword.length() > 0) {
                    PreparedStatement updateUsersPrepStmt = con.prepareStatement(SQLQueries.UPD_USERS);
                    updateUsersPrepStmt.setString(1, newPassword);
                    updateUsersPrepStmt.setInt(2, usersId);
                    updateUsersPrepStmt.executeUpdate();
                    updateUsersPrepStmt.close();
                }
                // set current age of this task -- use the update age
                task.setAge(task.getUpdateAge());
                // Insert user information in session record.
                PreparedStatement updateSessionPrepStmt = task.prepareStatement(dbTask, SQLQueries.UPD_SESSION_KEY);
                updateSessionPrepStmt.setInt(1, usersId);
                updateSessionPrepStmt.setString(2, sessionKey);
                updateSessionPrepStmt.setBoolean(3, isAdmin);
                updateSessionPrepStmt.setInt(4, task.getAge());
                updateSessionPrepStmt.setString(5,task.getFossaSessionId());
                updateSessionPrepStmt.setInt(6, task.getSessionId());
                updateSessionPrepStmt.executeUpdate();

                // Remember the user id, thus indicating that login has occurred.
                task.setUsersId(usersId);
                //EventLog.login(task);
                // Remember if this is an administrator task
                task.setAdmin(isAdmin);
                dbTask.commitTransaction(task);
                String userSessionId = task.getFossaSessionId();
                //Start writing the XML
                writer.startElement(T_ACCEPT);
                writer.writeAttribute(A_FOSSAID, userSessionId);
                writer.writeAttribute(A_MIN_VERSION, minVersion);
                writer.writeAttribute(A_MAX_VERSION, maxVersion);
                writer.writeAttribute(A_SESSION_KEY, sessionKey);

                if (isAdmin) {
                    if (clientVersion.compareTo("0.02.058") >= 0) {
                        if (permissionTeamLeader) {
                            writer.writeAttribute(A_OK_TEAM_LEADER, "YES");
                        }
                        if (permissionAdmin) {
                            writer.writeAttribute(A_OK_ADMIN, "YES");
                        }
                        if (permissionAdminUsers) {
                            writer.writeAttribute(A_OK_ADMIN_USERS, "YES");
                        }
                        if (permissionAdminProject) {
                            writer.writeAttribute(A_OK_ADMIN_PROJECT, "YES");
                        }
                        if (permissionAdminBatch) {
                            writer.writeAttribute(A_OK_ADMIN_BATCH, "YES");
                        }
                        if (permissionAdminEdit) {
                            writer.writeAttribute(A_OK_ADMIN_EDIT, "YES");
                        }
                        if (permissionAdminImport) {
                            writer.writeAttribute(A_OK_ADMIN_IMPORT, "YES");
                        }
                        if (permissionAdminExport) {
                            writer.writeAttribute(A_OK_ADMIN_EXPORT, "YES");
                        }
                        if (permissionAdminProfit) {
                            writer.writeAttribute(A_OK_ADMIN_PROFIT, "YES");
                        }
                    }

                } else { // since its not a Admin
                    if (clientVersion.compareTo("0.01.028") >= 0) {
                        if (permissionUnitize) {
                            writer.writeAttribute(A_OK_UNITIZE, "YES");
                        }
                        if (permissionUqc) {
                            writer.writeAttribute(A_OK_UQC, "YES");
                        }
                        if (permissionCoding) {
                            writer.writeAttribute(A_OK_CODING, "YES");
                        }
                        if (permissionCodingqc) {
                            writer.writeAttribute(A_OK_CODINGQC, "YES");
                        }
                        if (permissionQa) {
                            writer.writeAttribute(A_OK_QA, "YES");
                        }
                        if (permissionListing) {
                            writer.writeAttribute(A_OK_LISTING, "YES");
                        }
                        if (permissionTally) {
                            writer.writeAttribute(A_OK_TALLY, "YES");
                        }
                        if (permissionTeamLeader) {
                            writer.writeAttribute(A_OK_TEAM_LEADER, "YES");
                        }
                    }
                }

                if (clientVersion.compareTo("0.01.065") >= 0) {
                    writer.writeAttribute(A_TIME_ZONE, TimeZone.getDefault().getID());
                }
                writer.endElement();
            }
        } catch (SQLException sql) {
               CommonLogger.printExceptions(this, "Exception while opening a new user session." , sql);
               return sql.getMessage();
        } catch (ServerFailException fail) {
               CommonLogger.printExceptions(this, "ServerFailException while opening a new user session." , fail);
               return fail.getMessage();
        } catch (Exception exc) {
               CommonLogger.printExceptions(this, "Exception while opening a new user session." ,exc);
               return exc.getMessage();
        }        
        return null;
    }

    // helper method to get attribute, forcing value to "" if it is null
    // (for missing $IMPLIED attribute, get "" if we are using a validating
    // parser, but null if we are not.
    private String getAttribute(Element action, String attributeName) {
        String result = action.getAttribute(attributeName);
        return (result == null ? "" : result);
    }

    public boolean isReadOnly() {
        return true;
    }
}
