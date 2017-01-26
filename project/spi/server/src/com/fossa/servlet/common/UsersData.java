/* $Header: /home/common/cvsarea/ibase/dia/src/common/UsersData.java,v 1.4.6.1 2005/07/27 15:48:50 nancy Exp $ */
package com.fossa.servlet.common;

/**
 * A container for data required for update of a user.
 */
public class UsersData {

    /** id of the user */
    public int users_id;

    /** id of the team the user belongs to */
    public int teams_id = 0;

    /** name of the user */
    public String user_name = "";

    /** user's first name */
    public String first_name = "";

    /** user's last name */
    public String last_name = "";

    /** does the user have unitize privilege */
    public String unitize = "No";

    /** does the user have uqc privilege */
    public String uqc = "No";

    /** does the user have coding privilege */
    public String coding = "No";

    /** does the user have codingqc privilege */
    public String codingqc = "No";

    /** does the user have qa privilege */
    public String qa = "No";

    /** does the user have listing privilege */
    public String listing = "No";
    
     /** does the user have tally privilege */
    public String tally = "No";

    /** does the user have team leader privilege */
    public String teamLeader = "No";

    /** does the user have admin privilege */
    public String admin = "No";

    /** if admin, can admin Users */
    public String canAdminUsers = "No";

    /** if admin, can admin Project */
    public String canAdminProject = "No";

    /** if admin, can admin Batch */
    public String canAdminBatch = "No";

    /** if admin, can admin Edit */
    public String canAdminEdit = "No";

    /** if admin, can admin Import */
    public String canAdminImport = "No";

    /** if admin, can admin Export */
    public String canAdminExport = "No";

    /** if admin, can admin customer pricing and profit reporting */
    public String canAdminProfit = "No";

    /** user's password */
    public String password = "";
    
    /** user's Join date */
    public String dateOfJoin = "";
    
}
