/* $Header: /home/common/cvsarea/ibase/dia/src/server/Tables.java,v 1.23.2.2 2005/08/02 22:50:51 nancy Exp $ */
package server;

import common.Log;

import java.sql.*;

/**
 * A class which contains ManagedTable declarations corresponding to 
 * all database tables which are to be managed.
 */
public class Tables {

    private Tables() {}

    public static ManagedTable assignment;
    public static ManagedTable batch;
    public static ManagedTable child;
    public static ManagedTable project;
    public static ManagedTable projectfields;
    public static ManagedTable session;
    public static ManagedTable tablespec;
    public static ManagedTable tablevalue;
    public static ManagedTable teams;
    public static ManagedTable teamsqueue;
    public static ManagedTable users;
    public static ManagedTable usersqueue;
    public static ManagedTable volume;
    public static ManagedTable teamsvolume;
    public static ManagedTable mailreceived;
    public static ManagedTable mailsent;
    public static ManagedTable unitprice;
    public static ManagedTable customerprice;
    //public static ManagedTable page;

    /**
     * Load the ManagedTable values and build dependency information.
     */
    public static void load(Connection con) {

        // Note:  The following table order must stay in sync
        //        with changes.table_number used in dbload.DbWriter.java.
        assignment = ManagedTable.forName("assignment");
        batch = ManagedTable.forName("batch");
        child = ManagedTable.forName("child");
        project = ManagedTable.forName("project");
        projectfields = ManagedTable.forName("projectfields");
        session = ManagedTable.forName("session");
        tablespec = ManagedTable.forName("tablespec");
        tablevalue = ManagedTable.forName("tablevalue");
        teams = ManagedTable.forName("teams");
        teamsqueue = ManagedTable.forName("teamsqueue");
        users = ManagedTable.forName("users");
        usersqueue = ManagedTable.forName("usersqueue");
        volume = ManagedTable.forName("volume");
        teamsvolume = ManagedTable.forName("teamsvolume");
        mailreceived = ManagedTable.forName("mailreceived");
        mailsent = ManagedTable.forName("mailsent");
        unitprice = ManagedTable.forName("unitprice");
        customerprice = ManagedTable.forName("customerprice");

        //ManagedTable.checkTables(con);
        assignment.addDependency(batch, "batch_id");
        assignment.addDependency(users, "users_id");
        teamsqueue.addDependency(batch, "batch_id");
        teamsqueue.addDependency(users, "teams_id");
        usersqueue.addDependency(batch, "batch_id");
        usersqueue.addDependency(users, "users_id");
        teamsvolume.addDependency(volume, "volume_id");
        teamsvolume.addDependency(teams, "teams_id");
        session.addDependency(batch, "batch_id");
        //session.addDependency(volume, "volume_id");

        tablespec.addDependency(projectfields, "tablespec_id");
        project.addDependency(tablespec, "project_id");
        teams.addDependency(users, "teams_id");
        teams.addDependency(users, "users_id");
        users.addDependency(teams, "users_id");  // leader_users_id

        try {
            con.commit();
        } catch (SQLException e) {
            Log.quit(e);
        }
    }
}

