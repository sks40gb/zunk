/* $Header: /home/common/cvsarea/ibase/dia/src/server/Tables.java,v 1.23.2.2 2005/08/02 22:50:51 nancy Exp $ */
package com.fossa.servlet.server;

import com.fossa.servlet.common.Log;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.log4j.Logger;

/**
 * A class which contains ManagedTable declarations corresponding to 
 * all database tables which are to be managed.
 */
public class Tables {

    public Tables() {
    }
    private static Logger logger = Logger.getLogger("com.fossa.servlet.server");
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
    public static ManagedTable event;
    public static ManagedTable svrage;
    public static ManagedTable coding_manual;
    public static ManagedTable namevalue;
    public static ManagedTable value;
    public static ManagedTable listing_reports;
    public static ManagedTable listing_occurrence;
    public static ManagedTable listing_qc;
    public static ManagedTable tally_occurrence;
    public static ManagedTable tally_qc;
    public static ManagedTable validation_functions_group;
    public static ManagedTable validation_functions_master;
    public static ManagedTable validation_mapping_master;
    public static ManagedTable validation_mapping_details;
    public static ManagedTable event_break;
    public static ManagedTable query_tracker;
    public static ManagedTable post_validation;
    public static ManagedTable project_l1;
    public static ManagedTable sampling;
    public static ManagedTable sampled_documents;
    public static ManagedTable qa_notes;
    public static ManagedTable batch_comments;
    public static ManagedTable history_process;
    public static ManagedTable tally_assignment;
    public static ManagedTable qa_corrections;
    public static ManagedTable qa_assignment;
    public static ManagedTable tally_dictionary;
    public static ManagedTable batchEditChecking;
    public static ManagedTable export_details;
    public static ManagedTable tsearch_export_details;
    public static ManagedTable output_validations_details;
    public static ManagedTable servertaskqueue;
    public static ManagedTable servertasktype;
    public static ManagedTable export_data;
    
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
        event = ManagedTable.forName("event");
        svrage = ManagedTable.forName("svrage");
        coding_manual = ManagedTable.forName("coding_manual");
        namevalue = ManagedTable.forName("namevalue");
        value = ManagedTable.forName("value");
        listing_reports = ManagedTable.forName("listing_reports");
        listing_occurrence = ManagedTable.forName("listing_occurrence");
        listing_qc = ManagedTable.forName("listing_qc");
        tally_occurrence = ManagedTable.forName("tally_occurrence");
        tally_qc = ManagedTable.forName("tally_qc");
        event_break = ManagedTable.forName("event_break");
        query_tracker = ManagedTable.forName("query_tracker");
        validation_mapping_details = ManagedTable.forName("validation_mapping_details");
        validation_functions_group = ManagedTable.forName("validation_functions_group");
        validation_functions_master = ManagedTable.forName("validation_functions_master");
        validation_mapping_master = ManagedTable.forName("validation_mapping_master");
        project_l1 = ManagedTable.forName("project_l1");
        sampling = ManagedTable.forName("sampling");
        sampled_documents = ManagedTable.forName("sampled_documents");
        qa_notes = ManagedTable.forName("qa_notes");
        post_validation = ManagedTable.forName("post_validation");
        batch_comments = ManagedTable.forName("batch_comments");
        history_process = ManagedTable.forName("history_process");
        tally_assignment = ManagedTable.forName("tally_assignment");
        qa_corrections = ManagedTable.forName("qa_corrections");
        qa_assignment = ManagedTable.forName("qa_assignment");
        tally_dictionary = ManagedTable.forName("tally_dictionary");
        batchEditChecking = ManagedTable.forName("batchEditChecking");
        export_details = ManagedTable.forName("export_details");
        tsearch_export_details = ManagedTable.forName("tsearch_export_details");
        output_validations_details = ManagedTable.forName("output_validations_details");
        servertaskqueue = ManagedTable.forName("servertaskqueue");
        servertasktype = ManagedTable.forName("servertasktype");
        export_data = ManagedTable.forName("export_data");

 

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

        tablespec.addDependency(projectfields, "tablespec_id");
        project.addDependency(tablespec, "project_id");
        teams.addDependency(users, "teams_id");
        teams.addDependency(users, "users_id");
        users.addDependency(teams, "users_id");  // leader_users_id
        namevalue.addDependency(child, "child_id");
        

        try {
            con.commit();
        } catch (SQLException e) {
            logger.error("Exception while commiting the connection ." + e);
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            logger.error(sw.toString());
            Log.quit(e);
        }
    }
}