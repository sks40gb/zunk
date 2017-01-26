/* $Header: /home/common/cvsarea/ibase/dia/src/model/UserBatching.java,v 1.14.4.1 2006/01/10 14:10:34 nancy Exp $ */
package model;

import beans.UsersTreeNode;
import client.Global;
import common.Log;
import ui.BatchingPage;

/**
 * Description of tree structure for team and user tree in BatchingPage.
 */
final public class UserBatching {

    private static BatchingPage theBatchingPage;

    /** Root node of user/team tree */
    public static class RootNode extends UsersTreeNode {

        public RootNode(BatchingPage givenBatchingPage) {
            super("Teams and Users", "root");
            theBatchingPage = givenBatchingPage;

            ////////// SUBTREEs FOR TEAMS and USERS
            add(new ManagedNodeModel(TeamLabelNode.makeModel()) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new TeamLabelNode(rowData);
                    }
                });
            add(new ManagedNodeModel(UserLabelNode.makeModel()) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new UserLabelNode(rowData);
                    }
                });
        }
    }


    /** Label node for team subtree */
    protected static class TeamLabelNode extends UsersTreeNode {

        TeamLabelNode(Object rowData) {
            super(rowData, /* status => */ "");

            add(new ManagedNodeModel(TeamNode.makeModel()) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new TeamNode(rowData);
                    }
                });
        }

        /** One-row model for label legend */
        public static ManagedTableModel makeModel() {
            return new ManagedTableLabel("Teams");
        }
    }
    

    /** Label node for user subtree */
    protected static class UserLabelNode extends UsersTreeNode {

        UserLabelNode(Object rowData) {
            super(rowData, /* status => */ "");

            add(new ManagedNodeModel(UserNode.makeModel()) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new UserNode(rowData);
                    }
                });
        }

        /** One-row model for label legend */
        public static ManagedTableModel makeModel() {
            return new ManagedTableLabel("Users");
        }
    }
    

    /** Team node of user/team tree */
    protected static class TeamNode extends UsersTreeNode {

        TeamNode(Object userObject) {
            super(userObject, "team");

            ////////// GROUP NODES FOR THE QUEUES
            ManagedTableModel teamsQueueBatchModel
                    = TeamsQueueBatchNode.makeModel(getRowId());
            
            ////////// GROUP NODE FOR THE VOLUMES
            ManagedTableModel teamsQueueVolumeModel = TeamsQueueVolumeNode.makeModel(getRowId());
            addTeamsvolumeQueueNode("Volumes",
                              UsersTreeNode.VOLUME_LABEL, teamsQueueVolumeModel);

            String viewMode = theBatchingPage.getViewMode();
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.UNITIZE)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addTeamsQueueNode("Unitize Queue", "Unitize",
                             UsersTreeNode.UNITIZE_LABEL, teamsQueueBatchModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.UQC)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addTeamsQueueNode("Unitize QC Queue", "UQC",
                             UsersTreeNode.UQC_LABEL, teamsQueueBatchModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.CODING)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addTeamsQueueNode("Coding Queue", "Coding",
                             UsersTreeNode.CODING_LABEL, teamsQueueBatchModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.QC)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addTeamsQueueNode("Coding QC Queue", "CodingQC",
                             UsersTreeNode.QC_LABEL, teamsQueueBatchModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.QA)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addTeamsQueueNode("QA Queue", "QA",
                             UsersTreeNode.QA_LABEL, teamsQueueBatchModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.MASKING)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addTeamsQueueNode("Masking Queue", "Masking",
                             UsersTreeNode.MASKING_LABEL, teamsQueueBatchModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.MODIFYERRORS)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addTeamsQueueNode("ModifyErrors Queue", "ModifyErrors",
                             UsersTreeNode.MODIFYERRORS_LABEL, teamsQueueBatchModel);
            }
            //Listing
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.LISTING)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addTeamsQueueNode("Listng Queue", "Listing",
                             UsersTreeNode.LISTING_LABEL, teamsQueueBatchModel);
            }
            //Tally
             if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.TALLY)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addTeamsQueueNode("Tally Queue", "Tally",
                             UsersTreeNode.TALLY_LABEL, teamsQueueBatchModel);
            }
        }

        /**
         * Model to select all teams.  (SQL re-used from TeamAdminPage)
         * <pre>
         * select T.teams_id,T.team_name, U.user_name
         *   from teams T left join users U on users_id = U.users_id
         * </pre>
         */
        public static ManagedTableModel makeModel()  {
            String sqlname = (Global.theServerConnection.getPermissionAdmin()
                              ? "TeamAdminPage.teams"
                              : "UserBatching.teamsTL");
            return new ManagedTableSorter(0,
                SQLManagedTableModel.makeInstance (sqlname));
        }


        void addTeamsQueueNode(String legend,
                          final String viewStatus,
                          final String nodeType,
                          final ManagedTableModel grandchildModel)
        {

            final ManagedTableModel filteredGrandchildModel
                    = new ManagedTableFilter(grandchildModel) {
                            public boolean accept(TableRow rowData) {
                                return (viewStatus.equals(rowData.getValue(2)));
                            }
                        };

            ManagedTableModel queueLabelModel
                    = new ManagedTableGroup(legend, filteredGrandchildModel);

            add(new ManagedNodeModel(queueLabelModel) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new TeamsQueueLabelNode(rowData, nodeType,
                                                       filteredGrandchildModel);
                    }
                });
        }

        void addTeamsvolumeQueueNode(String legend,
                          final String nodeType,
                          final ManagedTableModel grandchildModel)
        {
            ManagedTableModel queueLabelModel
                    = new ManagedTableGroup(legend, grandchildModel);

            add(new ManagedNodeModel(queueLabelModel) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new TeamsvolumeQueueLabelNode(rowData, nodeType, grandchildModel);
                    }
                });
        }


        /**
         * Overridden to append the team leader name, if any, to the team name
         * Note.  toString() is used to print the tree legend.
         */
        public String toString() {
            TableRow rowData = (TableRow) getUserObject();
            String leaderName = (String) rowData.getValue(1);
            if (leaderName == null || leaderName.length() == 0) {
                return super.toString();
            } else {
                return super.toString()+" ("+leaderName+")";
            }
        }
    }


    /** User node of user/team tree */
    protected static class UserNode extends UsersTreeNode {

        UserNode(Object userObject) {
            super(userObject, "user");

            ////////// ASSIGNMENTS
            add(new ManagedNodeModel(AssignmentNode.makeModel(getRowId())) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new AssignmentNode(rowData);
                    }
                });

            ////////// GROUP NODES FOR THE QUEUES
            ManagedTableModel usersQueueBatchModel
                    = UsersQueueBatchNode.makeModel(getRowId());

            String viewMode = theBatchingPage.getViewMode();
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.UNITIZE)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addUsersQueueNode("Unitize Queue", "Unitize",
                             UsersTreeNode.UNITIZE_LABEL, usersQueueBatchModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.UQC)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addUsersQueueNode("Unitize QC Queue", "UQC",
                             UsersTreeNode.UQC_LABEL, usersQueueBatchModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.CODING)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addUsersQueueNode("Coding Queue", "Coding",
                             UsersTreeNode.CODING_LABEL, usersQueueBatchModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.QC)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addUsersQueueNode("Coding QC Queue", "CodingQC",
                             UsersTreeNode.QC_LABEL, usersQueueBatchModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.QA)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addUsersQueueNode("QA Queue", "QA",
                             UsersTreeNode.QA_LABEL, usersQueueBatchModel);
            }
             if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.MASKING)
            || viewMode.equals(BatchingPage.ASSIGNED)) { 
                addUsersQueueNode("Masking Queue", "Masking",
                             UsersTreeNode.MASKING_LABEL, usersQueueBatchModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.MODIFYERRORS)
            || viewMode.equals(BatchingPage.ASSIGNED)) { 
                addUsersQueueNode("ModifyErrors Queue", "ModifyErrors",
                             UsersTreeNode.MODIFYERRORS_LABEL, usersQueueBatchModel);
            }
            //Listing
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.LISTING)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addUsersQueueNode("Listng Queue", "Listing",
                             UsersTreeNode.LISTING_LABEL, usersQueueBatchModel);
            }
            //Tally
             if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.TALLY)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addUsersQueueNode("Tally Queue", "Tally",
                             UsersTreeNode.TALLY_LABEL, usersQueueBatchModel);
            }
        }

        /**
         * Model to select all users.
         * <pre>
         * select U.users_id,U.user_name, T.team_name
         *   from users U left join teams T on U.teams_id = T.teams_id
         * </pre>
         */
        public static ManagedTableModel makeModel()  {
            String sqlname = (Global.theServerConnection.getPermissionAdmin()
                              ? "UserBatching.users"
                              : "UserBatching.usersTL");
            return new ManagedTableSorter(0,
                SQLManagedTableModel.makeInstance (sqlname));
        }


        void addUsersQueueNode(String legend,
                          final String viewStatus,
                          final String nodeType,
                          final ManagedTableModel grandchildModel)
        {

            final ManagedTableModel filteredGrandchildModel
                    = new ManagedTableFilter(grandchildModel) {
                            public boolean accept(TableRow rowData) {
                                return (viewStatus.equals(rowData.getValue(2)));
                            }
                        };

            ManagedTableModel queueLabelModel
                    = new ManagedTableGroup(legend, filteredGrandchildModel);

            add(new ManagedNodeModel(queueLabelModel) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new UsersQueueLabelNode(rowData, nodeType,
                                                       filteredGrandchildModel);
                    }
                });
        }


        /**
         * Overridden to append the team name, if any, to the user name
         * Note.  toString() is used to print the tree legend.
         */
        public String toString() {
            TableRow rowData = (TableRow) getUserObject();
            String teamName = (String) rowData.getValue(1);
            if (teamName == null || teamName.length() == 0) {
                return super.toString();
            } else {
                return super.toString()+", "+teamName;
            }
        }
    }


    /** Assignment node for users subtree */
    protected static class AssignmentNode extends UsersTreeNode {

        AssignmentNode(Object rowData) {
            super (rowData, null, /* allowsChildren => */ false);
        }


        public String getType() {
            TableRow rowData = (TableRow) getUserObject();
            String status = (String) rowData.getValue(2);
            for (int i = 0; i < STATUS_TABLE.length; i++) {
                if (STATUS_TABLE[i][0].equals(status)) {
                    return STATUS_TABLE[i][3];
                }
            }
            Log.quit("AssignmentNode:  Invalid status: "+status);
            return null;
        }

        /**
         * Model to select assignments for a user.
         * These will be further filtered according to status.
         * Note.  Status+0 is to allow sort by status numeric value
         * <pre>
         * select A.assignment_id, P.project_name, B.batch_number
         *      , B.status, B.status + 0, B.batch_id                         
         * from assignment A                                     
         *   inner join batch B using (batch_id)                 
         *   inner join volume V on V.volume_id = B.volume_id    
         *   inner join project P on P.project_id = V.project_id 
         * where A.users_id = ?                                  
         * </pre>
         */
        public static ManagedTableModel makeModel(int batchId) {
            SQLManagedTableModel sqlModel
                = SQLManagedTableModel.makeInstance("UserBatching.assignments", batchId);
            sqlModel.setColumnClass(3, Integer.class);
            return new ManagedTableSorter(3,
                new ManagedTableFilter(sqlModel) {
                    public boolean accept(TableRow rowData) {
                        String viewMode = theBatchingPage.getViewMode();
                        if (viewMode.equals(BatchingPage.ALL)
                        ||  viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                            return true;
                        }
                        String status = (String) rowData.getValue(2);
                        return viewMode.equals(BatchingPage.UNITIZE)
                                 && status.equals("Unitize")               
                               || viewMode.equals(BatchingPage.UQC)        
                                 && status.equals("UQC")
                               || viewMode.equals(BatchingPage.CODING)     
                                 && status.equals("Coding")
                               || viewMode.equals(BatchingPage.QC)         
                                 && status.equals("CodingQC")
                               || viewMode.equals(BatchingPage.QA)         
                                 && status.equals("QA")
                               || viewMode.equals(BatchingPage.MASKING)         
                                 && status.equals("Masking")
                                || viewMode.equals(BatchingPage.MODIFYERRORS)         
                                 && status.equals("ModifyErrors");  
                    }
                });
        }

        public String toString() {
            TableRow rowData = (TableRow) getUserObject();
            String status = (String) rowData.getValue(2);
            String legend = "";
            for (int i = 0; i < STATUS_TABLE.length; i++) {
                if (STATUS_TABLE[i][0].equals(status)) {
                    legend = STATUS_TABLE[i][4];
                    break;
                }
            }
            String projectName = (String) rowData.getValue(0);
            String batchNumber = (String) rowData.getValue(1);
            String group = Integer.parseInt((String) rowData.getValue(5)) > 0 ?
                                ", Group " + Integer.parseInt((String)rowData.getValue(5)) : "";
            return legend
                   +": "+projectName
                   +" Batch "+batchNumber + group;
        }
    }


    /**
     * Label node for teams queue.
     */
    protected static class TeamsQueueLabelNode extends UsersTreeNode {

        TeamsQueueLabelNode(Object rowData, String nodeType,
                       final ManagedTableModel childModel)
        {
            super(rowData, nodeType);

            ManagedNodeModel nodeModel = new ManagedNodeModel(childModel) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new TeamsQueueBatchNode(rowData);
                    }
                };
            add(nodeModel);
        }
    }

    /**
     * Label node for teams volume queue.
     */
    protected static class TeamsvolumeQueueLabelNode extends UsersTreeNode {

        TeamsvolumeQueueLabelNode(Object rowData, String nodeType,
                       final ManagedTableModel childModel)
        {
            super(rowData, nodeType);

            ManagedNodeModel nodeModel = new ManagedNodeModel(childModel) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new TeamsQueueVolumeNode(rowData);
                    }
                };
            add(nodeModel);
        }
    }


    /**
     * Label node for users queue.
     */
    protected static class UsersQueueLabelNode extends UsersTreeNode {

        UsersQueueLabelNode(Object rowData, String nodeType,
                       final ManagedTableModel childModel)
        {
            super(rowData, nodeType);

            ManagedNodeModel nodeModel = new ManagedNodeModel(childModel) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new UsersQueueBatchNode(rowData);
                    }
                };
            add(nodeModel);
        }
    }



    /**
     * Batch on a team's queue.
     */
    public static class TeamsQueueBatchNode extends UsersTreeNode {

        TeamsQueueBatchNode (Object userObject) {
            super(userObject, null, /* allowsChildren => */ false);
        }

        public String getType() {
            Object status = ((TableRow) getUserObject()).getValue(2);
            String result = "";
            for (int i = 0; i < STATUS_TABLE.length; i++) {
                if (STATUS_TABLE[i][0].equals(status)) {
                    result = STATUS_TABLE[i][2];
                }
            }
            return result;
        }

        public int getId() {
            // return batchId.  Goes away???
            return Integer.parseInt(((TableRow) getUserObject()).getValue(5).toString());
        }

        /**
         * Model to select batches queued for a team.
         * These will be further filtered according to status.
         * Note: Priority is negated for sorting.
         * <pre>
         * select TQ.teamsqueue_id, P.project_name, B.batch_number
         *   , B.status, - B.priority, TQ.teams_id, TQ.batch_id
         * from teamsqueue TQ
         *   inner join teams T using (teams_id)
         *   inner join batch B on TQ.batch_id=B.batch_id
         *   inner join volume V on V.volume_id=B.volume_id
         *   inner join project P on P.project_id = V.project_id
         * where TQ.teams_id=?
         * </pre>
         */
        public static ManagedTableModel makeModel(int batchId) {
            SQLManagedTableModel sqlModel
                = SQLManagedTableModel.makeInstance("Batching.teamsbatchqueue", batchId);
            sqlModel.setColumnClass(1, Integer.class);
            sqlModel.setColumnClass(3, Integer.class);
            // sort by project, priority, batch number
            return new ManagedTableSorter(new int[] {0,3,1}, sqlModel);
        }


        /**
         * Overridden to return project + batch + (priority)
         * Note.  toString() is used to print the tree legend.
         */
        public String toString() {
            TableRow rowData = (TableRow) getUserObject();
            int priority = - ((Integer) rowData.getValue(3)).intValue();
            String group = Integer.parseInt((String) rowData.getValue(6)) > 0 ?
                                ", Group " + Integer.parseInt((String)rowData.getValue(6)) : "";
            return rowData.getValue(0)              // project name
                 + " Batch "+rowData.getValue(1) + group    // batch number // TBD: should be queue type? batch or unit?
                 +" ("+priority+")"; // priority  (negative is returned)
        }
    }


    /**
     * Volume on a team's queue.
     */
    protected static class TeamsQueueVolumeNode extends UsersTreeNode {

        TeamsQueueVolumeNode (Object userObject) {
            super(userObject, UsersTreeNode.VOLUME, false);
        }

        /**
         * Model to select volumes queued for a team.
         * <pre>
         * select TV.teamsvolume_id, V.volume_name, V.volume_id
         * from teamsvolume TV \n 
         *   inner join volume V using (volume_id)
         * where TV.teams_id = ?
         * </pre>
         */
        public static ManagedTableModel makeModel(int teamsId) {
            SQLManagedTableModel sqlModel
                = SQLManagedTableModel.makeInstance("Batching.teamsvolumequeue", teamsId);
            // sort by volume_name
            return new ManagedTableSorter(new int[] {0}, sqlModel);
        }


        /**
         * Overridden to return project + batch + (priority)
         * Note.  toString() is used to print the tree legend.
         */
        //public String toString() {
        //    TableRow rowData = (TableRow) getUserObject();
        //    int priority = - ((Integer) rowData.getValue(3)).intValue();
        //    return rowData.getValue(0)              // project name
        //         + " Batch "+rowData.getValue(1)    // batch number // TBD: should be queue type? batch or unit?
        //         +" ("+priority+")"; // priority  (negative is returned)
        //}
    }


    /**
     * Batch on a user's queue.
     */
    public static class UsersQueueBatchNode extends UsersTreeNode {

        UsersQueueBatchNode (Object userObject) {
            super(userObject, null, /* allowsChildren => */ false);
        }

        public String getType() {
            //Log.print("(UserBatching.UsersQueueBatchNode.getType) "
            //          + getUserObject());
            Object status = ((TableRow) getUserObject()).getValue(2);
            String result = "";
            for (int i = 0; i < STATUS_TABLE.length; i++) {
                if (STATUS_TABLE[i][0].equals(status)) {
                    result = STATUS_TABLE[i][2];
                }
            }
            return result;
        }

        public int getId() {
            // return batchId.  Goes away???
            return Integer.parseInt(((TableRow) getUserObject()).getValue(5).toString());
        }

        /**
         * Model to select batches queued for a user.
         * These will be further filtered according to status.
         * Note: Priority is negated for sorting.
         * <pre>
         * select UQ.usersqueue_id, P.project_name, B.batch_number 
         *   , B.status, - B.priority, UQ.users_id, UQ.batch_id    
         * from usersqueue UQ                                      
         *   inner join users U using (users_id)                   
         *   inner join batch B on UQ.batch_id=B.batch_id          
         *   inner join volume V on V.volume_id=B.volume_id        
         *   inner join project P on P.project_id = V.project_id   
         * where UQ.users_id=?                                     
         * </pre>
         */
        public static ManagedTableModel makeModel(int usersId) {
            SQLManagedTableModel sqlModel
                = SQLManagedTableModel.makeInstance("UserBatching.usersbatchqueue", usersId);
            sqlModel.setColumnClass(1, Integer.class);
            sqlModel.setColumnClass(3, Integer.class);
            // sort by project, priority, batch number
            return new ManagedTableSorter(new int[] {0,3,1}, sqlModel); // sort by project, priority
        }


        /**
         * Overridden to return project + batch + (priority)
         * Note.  toString() is used to print the tree legend.
         */
        public String toString() {
            TableRow rowData = (TableRow) getUserObject();
            int priority = - ((Integer) rowData.getValue(3)).intValue();
            String group = Integer.parseInt((String) rowData.getValue(6)) > 0 ?
                                ", Group " + Integer.parseInt((String)rowData.getValue(6)) : "";
            return rowData.getValue(0)              // project name
                 + " Batch "+rowData.getValue(1) + group    // batch number // TBD: should be queue type? batch or unit?
                 +" ("+priority+")"; // priority  (negative is returned)
        }
    }


    // Table giving:
    //   col 0:  status
    //   col 1:  unqueued node type
    //   col 2:  queued node type
    //   col 3:  assigned node type
    //   col 4:  the text the user sees in the trees.
    // TBD: Cloned from ProjectBatching - we don't use all of the fields
    final private static String[][] STATUS_TABLE
     = {
        { "Unitize", UsersTreeNode.UNITIZE, UsersTreeNode.UNITIZE_QUEUED,
             UsersTreeNode.UNITIZE_ASSIGNED, "Unitize" },
        { "UQC", UsersTreeNode.UQC, UsersTreeNode.UQC_QUEUED,
             UsersTreeNode.UQC_ASSIGNED, "Unitize QC" },
        { "Coding", UsersTreeNode.CODING, UsersTreeNode.CODING_QUEUED,
             UsersTreeNode.CODING_ASSIGNED, "Coding" },
        { "CodingQC", UsersTreeNode.QC, UsersTreeNode.QC_QUEUED,
             UsersTreeNode.QC_ASSIGNED, "Coding QC" },
        { "QA", UsersTreeNode.QA, UsersTreeNode.QA_QUEUED,
             UsersTreeNode.QA_ASSIGNED, "QA" },
        { "Masking", UsersTreeNode.MASKING, UsersTreeNode.MASKING_QUEUED,
             UsersTreeNode.MASKING_ASSIGNED, "Masking" },
       { "ModifyErrors", UsersTreeNode.MODIFYERRORS, UsersTreeNode.MODIFYERRORS_QUEUED,
             UsersTreeNode.MODIFYERRORS_ASSIGNED, "ModifyErrors" },      
    };
}

