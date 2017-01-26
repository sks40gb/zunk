/* $Header: /home/common/cvsarea/ibase/dia/src/model/ProjectBatching.java,v 1.22.4.1 2006/01/10 14:10:34 nancy Exp $ */
package model;

import beans.BatchTreeNode;
import client.Global;
import common.Log;
import common.msg.MessageConstants;
import java.util.HashMap;
import ui.BatchingPage;

/**
 * Description of tree structure for batch tree in BatchingPage.
 */
final public class ProjectBatching implements MessageConstants{

    private static BatchingPage theBatchingPage;
    private static String level1 ="";
    private  static HashMap  batchValueMap       = new HashMap();
    
    final private static int LISTING_PROCESS = 6;
    final private static int TALLY_PROCESS = 8;
    final private static int QA_PROCESS = 10;
    
    /** Root node of batch tree */
    public static class RootNode extends BatchTreeNode {

        public RootNode(BatchingPage givenBatchingPage) {
            super("Projects", "root");
            theBatchingPage = givenBatchingPage;
              
            ////////// SUBTREE FOR EACH PROJECT
            add(new ManagedNodeModel(ProjectNode.makeModel()) {
                    public ManagedNode makeChildNode(Object rowData) {                        
                        return new ProjectNode(rowData);
                    }
                });
        }
    }

    /** Project node of batch tree */
    public static class ProjectNode extends BatchTreeNode {

        ProjectNode(Object userObject) {
            super(userObject, BatchTreeNode.PROJECT);

            ////////// SUBTREE FOR EACH VOLUME
            add(new ManagedNodeModel(VolumeNode.makeModel(getRowId())) {
                        public ManagedNode makeChildNode(Object rowData) {                            
                            return new VolumeNode(rowData);
                        }
                });
        }

        /**
         * Model to select all projects.  For team leader, only projects for
         * his or her team.
         * <pre>
         * select project_id, project_name, split_documents from project
         * </pre>
         */
        public static ManagedTableModel makeModel()  {
            String sqlname = (Global.theServerConnection.getPermissionAdmin()
                              ? "Batching.projects"
                              : "Batching.projectsTL");
            return new ManagedTableSorter(0,
                SQLManagedTableModel.makeInstance (sqlname));
        }

        /**
         * Return the value of project.split_documents, the
         * second item in the ProjectNode model.
         * @return project.split_documents can be "Yes" or "No"
         */
        public String canSplitDocuments() {
            TableRow rowData = (TableRow) getUserObject();
            Log.print("(ProjectBatching.canSplitDocuments) "
                      + rowData.getValue(1));
            return "0".equals((String)rowData.getValue(1)) ? "No" : "Yes";
        }
    }

    /** Volume node of batch tree */
    public static class VolumeNode extends BatchTreeNode {

        VolumeNode(Object userObject) {
            super(userObject, BatchTreeNode.VOLUME);

            ////////// TEAMSVOLUME
            ManagedTableModel teamsvolumeQueueModel = QueueMemberNode.makeTeamsvolumeModel(getRowId());
            addQueueNode("Teams Volume Queue", BatchTreeNode.TEAM, teamsvolumeQueueModel);

            ////////// SUBTREE FOR EACH BATCH
            
//               int batchId = getRowId();              
//               final ClientTask task = new TaskCheckLevel(batchId);
//               task.setCallback(new Runnable() {
//                public void run() {
//                   // Element reply = (Element) task.getResult();
//                      String level = (String) task.getResult();
//                    //String action = reply.getNodeName();    
//                   // String level = reply.getAttribute(A_LEVEL);
//                    //if (level.equals("L1")) {  
//                     System.out.println("level------------------" + level);
//                         LevelData levelData = new LevelData();
//                         levelData.level = level;
//                         setLevel(level);
//                   // }                   
//                }});                 
//                task.enqueue();
            
            add(new ManagedNodeModel(BatchNode.makeModel(getRowId())) {
                        public ManagedNode makeChildNode(Object rowData) {                             
                            return new BatchNode(rowData);
                        }
                });
        }

        /**
         * Model to select volumes for a project.
         * <pre>
         * select volume_id, volume_name, sequence from volume where project_id=?
         * </pre>
         */
        public static ManagedTableModel makeModel(int projectId) {
            SQLManagedTableModel sqlModel
                    = SQLManagedTableModel.makeInstance ("Batching.volumes", projectId);
            // sort sequence numerically
            sqlModel.setColumnClass(1, Integer.class);
            return new ManagedTableSorter(1,0,sqlModel);
        }

        void addQueueNode(String legend,
                          final String nodeType,
                          final ManagedTableModel grandchildModel)
        {

            ManagedTableModel queueLabelModel
                    = new ManagedTableGroup(legend, grandchildModel);

            add(new ManagedNodeModel(queueLabelModel) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new QueueLabelNode(rowData, nodeType, grandchildModel);
                    }
                });
        }
    }

    /** Batch node of batch tree */
    public static class BatchNode extends BatchTreeNode {

        protected BatchNode(Object userObject) {
            // Status will be computed dynamically
            super(userObject, null);

            ////////// GROUP NODES FOR THE QUEUES      
            
            ManagedTableModel teamsQueueModel = QueueMemberNode.makeTeamsModel(getRowId());
            ManagedTableModel usersQueueModel = QueueMemberNode.makeUsersModel(getRowId());
            
            String viewMode = theBatchingPage.getViewMode();            
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.UNITIZE)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here                
                addQueueNode("Teams Unitize Queue", "Unitize",
                             BatchTreeNode.UNITIZE_LABEL, teamsQueueModel);
                addQueueNode("Users Unitize Queue", "Unitize",
                             BatchTreeNode.UNITIZE_LABEL, usersQueueModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.UQC)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addQueueNode("Teams Unitize QC Queue", "UQC",
                             BatchTreeNode.UQC_LABEL, teamsQueueModel);
                addQueueNode("Users Unitize QC Queue", "UQC",
                             BatchTreeNode.UQC_LABEL, usersQueueModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.CODING)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addQueueNode("Teams Coding Queue", "Coding",
                             BatchTreeNode.CODING_LABEL, teamsQueueModel);
                addQueueNode("Users Coding Queue", "Coding",
                             BatchTreeNode.CODING_LABEL, usersQueueModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.QC)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addQueueNode("Teams Coding QC Queue", "CodingQC",
                             BatchTreeNode.QC_LABEL, teamsQueueModel);
                addQueueNode("Users Coding QC Queue", "CodingQC",
                             BatchTreeNode.QC_LABEL, usersQueueModel);
            }
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.QA)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addQueueNode("Teams QA Queue", "QA",
                             BatchTreeNode.QA_LABEL, teamsQueueModel);
                addQueueNode("Users QA Queue", "QA",
                             BatchTreeNode.QA_LABEL, usersQueueModel);
            }
            //Listing
           if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.LISTING)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addQueueNode("Teams Listing Queue", "Listing",
                             BatchTreeNode.LISTING_LABEL, teamsQueueModel);
                addQueueNode("Users Listing Queue", "Listing",
                             BatchTreeNode.LISTING_LABEL, usersQueueModel);
            }
            //Tally            
            if (viewMode.equals(BatchingPage.ALL)
            || viewMode.equals(BatchingPage.TALLY)
            || viewMode.equals(BatchingPage.ASSIGNED)) { // 2007-06-14: changed here
                addQueueNode("Teams Tally Queue", "Tally",
                             BatchTreeNode.TALLY_LABEL, teamsQueueModel);
                addQueueNode("Users Tally Queue", "Tally",
                             BatchTreeNode.TALLY_LABEL, usersQueueModel);
            }
            
            ////////// LABEL NODE FOR THE ATTACHMENT RANGES
            add(new ManagedNodeModel(AttachmentLabelNode.makeModel(getRowId())) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new AttachmentLabelNode(rowData);
                    }
                });
        }

        /**
         * Model to select batches visible in current view mode.
         * <pre>
         * select B.batch_id, B.status, B.batch_number, U.user_name,
         *    , min(UQ.batch_id is null and BQ.batch_id is null), B.lft
         *    , S.batch_id is null, B.active_group
         * from batch B
         *    left join assignment A using (batch_id)                          
         *    left join users U using (users_id)      
         *    left join usersqueue UQ on B.batch_id = UQ.batch_id
         *    left join teamsqueue TQ on T.batch_id = TQ.batch_id
         *    left join session S on S.batch_id=B.batch_id
         * where B.volume_id=?        
         * group by batch_id
         * </pre>
         */
        public static ManagedTableModel makeModel (int volumeId) { 
            SQLManagedTableModel sqlModel
                   = SQLManagedTableModel.makeInstance ("Batching.batches", volumeId);
            // lft to sort as integer
            sqlModel.setColumnClass(4, Integer.class);            
            return new ManagedTableFilter(new ManagedTableSorter(4,sqlModel)) {
                    public boolean accept(TableRow rowData) {                         
                        String viewMode = theBatchingPage.getViewMode();                        
                        if (viewMode.equals(BatchingPage.ALL)) {
                            return true;
                        }
                        String status = (String) rowData.getValue(0);
                        //String level = (String) rowData.getValue(7);
                       // System.out.println("level0000000000000000" + level);
                        if (viewMode.equals(BatchingPage.COMPLETE)) {
                            return status.equals("UComplete")
                                   || status.equals("QAComplete")
                                   || status.equals("LComplete")
                                   || status.equals("TComplete")
                                   || status.equals("MComplete");
                        }
//                        if ("".equals(rowData.getValue(2))
//                        && ! "0".equals(rowData.getValue(3))) {
//                            // not assigned and not queued
//                           
//                            return viewMode.equals(BatchingPage.IDLE);
//                           
//                        }
                        if(((! ("".equals(rowData.getValue(2))))
                            || ("0".equals(rowData.getValue(3))))
                            && viewMode.equals(BatchingPage.ASSIGNED)) {
                            return true;
                        }   
                        //shows the batchs for corresponding view mode
                        if(viewMode.equals(BatchingPage.UNITIZE) && status.equals("Unitize")){
                         return status.equals("Unitize");
                        }
                        if(viewMode.equals(BatchingPage.UQC)&& status.equals("UQC")){
                           return status.equals("UQC");
                        }if(viewMode.equals(BatchingPage.CODING) && status.equals("Coding")){
                           return status.equals("Coding");
                        }if(viewMode.equals(BatchingPage.QC) && status.equals("CodingQC")){
                           return status.equals("CodingQC");
                        }if(viewMode.equals(BatchingPage.QA) && status.equals("QA")){
                           return status.equals("QA");
                        }if(viewMode.equals(BatchingPage.MASKING) && status.equals("Masking")){
                           return status.equals("Masking");
                        }if(viewMode.equals(BatchingPage.LISTING) && status.equals("Listing")){
                           return status.equals("Listing");
                        }if(viewMode.equals(BatchingPage.TALLY) && status.equals("Tally")){
                           return status.equals("Tally");
                        }if(viewMode.equals(BatchingPage.MODIFYERRORS) && status.equals("ModifyErrors")){
                           return status.equals("ModifyErrors");
                        }
                        
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
                               || viewMode.equals(BatchingPage.LISTING)
                                 && status.equals("Listing")
                               || viewMode.equals(BatchingPage.TALLY)
                                 && status.equals("Tally")
                               || viewMode.equals(BatchingPage.MODIFYERRORS)
                                 && status.equalsIgnoreCase("ModifyErrors");
                    }
                };
        }

        /**
         * Determine if batch boundary can be modified.  Check this batch,
         * plus adjacent batch, if any, to which this batch will be moved.
         * (If it's a "add batch" operation, there is no adjacent batch.)
         * <p>Requirements:<ul>
         *    <li> The adjacent batch must exist.  I.e., if delta = -1,
         *         this batch must not be the first batch in the volume,
         *         and, if delta = +1, it must not be the last batch.
         *    <li> Both batches must not be in use.
         *    <li> The two batches must have the same status.
         * </ul>
         * @param delta 1 Identifies the adjacent batch. <ul>
         *    <li> -1 -- The prior batch is the adjacent batch. 
         *    <li> 0 -- There is no adjacent batch. 
         *    <li> +1 -- The next batch is the adjacent batch. 
         * </ul>
         */
        public boolean rebatchAllowed(int delta) {
            assert delta >= -1 && delta <= 1;
            TableRow rowData = (TableRow) getUserObject();
            if ("0".equals(rowData.getValue(5))) {
                // this batch is in use
                return false;
            }
            if (delta == 0) {
                // add batch -- don't need to check adjacent batches
                return true;
            }
            int rowId = getRowId();

            // need to look at adjacent batch -- navigate to it
            VolumeNode theVolumeNode = (VolumeNode) getParent();
            // get model for batches under this volume
            // Note.  volume node has two submodels, the second is the batches
            ManagedNodeModel theBatchNodeModel
                    = (ManagedNodeModel) theVolumeNode.getSubmodel(1);
            // get model filtered by by current view mode
            ManagedTableFilter theFilteredModel
                    = (ManagedTableFilter) theBatchNodeModel.getModel();
            // get underlying unfiltered model
            ManagedTableModel theBatchModel
                    = (ManagedTableModel) theFilteredModel.getModel();
            // find the current batch in that model
            int index = Integer.MAX_VALUE;
            for (int i = 0; i < theBatchModel.getRowCount(); i++) {
                if (rowId == theBatchModel.getRowId(i)) {
                    index = i;
                    break;
                }
            }

            // check that the adjacent batch exists
            int adjacentIndex = index + delta;
            if (adjacentIndex < 0 || adjacentIndex >= theBatchModel.getRowCount()) {
                return false;
            }

            // check that the adjacent batch is not in use
            if (! "1".equals(theBatchModel.getValueAt(adjacentIndex, 5))) {
                return false;
            }

            // check that the two batches have the same status
            String adjacentStatus = (String) theBatchModel.getValueAt(adjacentIndex, 0);
            if (! adjacentStatus.equals(rowData.getValue(0))) {
                return false;
            }

            return true;
        }


        /**
         * Override to show batch status and append userName to assigned batch.
         */
        public String toString() {
            
            TableRow rowData = (TableRow) getUserObject();
            String batchStatus = rowData.getValue(0).toString();
            String batchNumber = rowData.getValue(1).toString();
            String userName = (String) rowData.getValue(2);
            String level = (String) rowData.getValue(7);
            int process_id =  Integer.parseInt(rowData.getValue(8).toString());           
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < STATUS_TABLE.length; i++) {
                if (batchStatus.equals(STATUS_TABLE[i][0])) {
                    buffer.append(STATUS_TABLE[i][4]);
                    buffer.append(' ');
                }
            }
            //Append the corresponding sub process for modifyErrors batches when displayed in admin batching page
            if(process_id > 0){
                
                String display = "";
                if (process_id != 0) {
                    if (process_id == LISTING_PROCESS) {
                        display = "Listing ";
                    } else if (process_id == TALLY_PROCESS) {
                        display = "Tally ";
                    } else if (process_id == QA_PROCESS) {
                        display = "QA ";
                    }
                    buffer.append("- " + display);
                   }
            }
            buffer.append("Batch ");
            buffer.append(batchNumber);
            // add the group to the batch label, if groups are being used
            if (Integer.parseInt(rowData.getValue(6).toString()) > 0) {
                buffer.append(", Group " + rowData.getValue(6).toString());
            }
            if(null != level){// Append the batch level(L1/L2)
                 buffer.append(", Level " + rowData.getValue(7).toString());
            }
            if (userName != null && userName.length() != 0) {
                if ("0".equals(rowData.getValue(5))) {
                    buffer.append(" In use by ");
                } else {
                    buffer.append(" Assigned to ");
                }
                buffer.append(userName);
            }
            batchValueMap.put(batchNumber, level);
            return buffer.toString();
        }

        /**
         * Override to dynamically compute type, depending on batch
         * status and whether it is queued or assigned.
         * TBD: if GUI becomes sluggish, this could be cached in type on changes
         */
        public String getType() {
            TableRow rowData = (TableRow) getUserObject();
            String batchStatus = (String) rowData.getValue(0);
            boolean isQueued = "0".equals(rowData.getValue(3));
            String userName = (String) rowData.getValue(2);
            boolean isAssigned = (userName != null && userName.length() > 0);
            String result = "";

            //Log.print("BatchNode.getType: "+rowData.dump());
            //Log.print("batchStatus="+batchStatus+" isQueued="+isQueued+" isAssigned="+isAssigned);

            for (int i = 0; i < STATUS_TABLE.length; i++) {
                if (batchStatus.equals(STATUS_TABLE[i][0])) {
                    if (isQueued) {
                        result = STATUS_TABLE[i][2];
                    } else if (isAssigned) {
                        result = STATUS_TABLE[i][3];
                    } else {
                        result = STATUS_TABLE[i][1];
                    }
                    break;
                }
            }
            return result;
        }



        void addQueueNode(String legend,
                          final String viewType,
                          final String nodeType,
                          final ManagedTableModel grandchildModel)
        {

            final ManagedTableModel filteredGrandchildModel
                    = new ManagedTableFilter(grandchildModel) {
                            public boolean accept(TableRow rowData) {
                                return (viewType.equals(rowData.getValue(1)));
                            }
                        };

            ManagedTableModel queueLabelModel
                    = new ManagedTableGroup(legend, filteredGrandchildModel);

            add(new ManagedNodeModel(queueLabelModel) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new QueueLabelNode(rowData, nodeType, grandchildModel);
                    }
                });
        }
    }


    /**
     * Label node for teams or users queue.
     */
    private static class QueueLabelNode extends BatchTreeNode {

        QueueLabelNode(Object rowData, String nodeType,
                       final ManagedTableModel childModel)
        {
            super(rowData, nodeType);

            ManagedNodeModel nodeModel = new ManagedNodeModel(childModel) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new QueueMemberNode(rowData);
                    }
                };
            add(nodeModel);
        }
    }

                          
    /**
     * Team or user on a queue.
     */
    protected static class QueueMemberNode extends BatchTreeNode {
        QueueMemberNode (Object userObject) {
            super(userObject, BatchTreeNode.QUEUE, /* allowsChildren => */ false);
        }

        /**
         * Model to select batches queued for a team.
         * <pre>
         * select TQ.teamsqueue_id, T.team_name, B.status
         * from teamsqueue TQ
         *   inner join teams T using (teams_id)
         *   inner join batch B on TQ.batch_id=B.batch_id
         * where TQ.batch_id=?
         * </pre>
         */
        public static ManagedTableModel makeTeamsModel(int batchId) {
            return new ManagedTableSorter(0,
                SQLManagedTableModel.makeInstance("batching.teamsqueue", batchId));
        }

        /*
        * Model to select batches queued for a user.
        * <pre>
        * select UQ.usersqueue_id, user_name, B.status
        * from usersqueue UQ
        *   inner join users U using (users_id)
        *   inner join batch B on UQ.batch_id=B.batch_id
        * where UQ.batch_id=?
        * </pre>
        */
        public static ManagedTableModel makeUsersModel(int batchId) {
            return new ManagedTableSorter(0,
                SQLManagedTableModel.makeInstance("batching.usersqueue", batchId));
        }

        /*
        * Model to select teams assigned to the volume.
        * <pre>
        * select TV.teamsvolume_id, T.team_name, T.teams_id
        * from teamsvolume TV
        *   inner join teams T using (teams_id) 
        * where TV.volume_id = ?
        * </pre>
        */
        public static ManagedTableModel makeTeamsvolumeModel(int volumeId) {
            return new ManagedTableSorter(0,
                SQLManagedTableModel.makeInstance("batching.teamsvolume", volumeId));
        }
       }

    // Table giving:
    //   col 0:  status
    //   col 1:  unqueued node type
    //   col 2:  queued node type
    //   col 3:  assigned node type
    //   col 4:  the text the user sees in the trees.
    final private static String[][] STATUS_TABLE
     = {
        { "Unitize", BatchTreeNode.UNITIZE, BatchTreeNode.UNITIZE_QUEUED,
             BatchTreeNode.UNITIZE_ASSIGNED, "Unitize" },
        { "UQC", BatchTreeNode.UQC, BatchTreeNode.UQC_QUEUED,
             BatchTreeNode.UQC_ASSIGNED, "Unitize QC" },
        { "UComplete", BatchTreeNode.UNITIZE_COMPLETE, "", "", "Unitize QC Complete" },
        { "Coding", BatchTreeNode.CODING, BatchTreeNode.CODING_QUEUED,
             BatchTreeNode.CODING_ASSIGNED, "Coding" },
        { "CodingQC", BatchTreeNode.QC, BatchTreeNode.QC_QUEUED,
             BatchTreeNode.QC_ASSIGNED, "Coding QC" },
        { "Masking", BatchTreeNode.MASKING, BatchTreeNode.MASKING_QUEUED,
             BatchTreeNode.MASKING_ASSIGNED, "Masking" },  
       { "MaskingComplete", BatchTreeNode.MASKING_COMPLETE, "", "", "MComplete" },      
         //added for listing    
        { "Listing", BatchTreeNode.LISTING, BatchTreeNode.LISTING_QUEUED,
             BatchTreeNode.LISTING_ASSIGNED, "Listing" },              
        { "LComplete", BatchTreeNode.LISTING_COMPLETE, "", "", "LComplete" },      
        { "ListingQC", BatchTreeNode.LISTING_QC, BatchTreeNode.LISTING_QC_QUEUED, BatchTreeNode.LISTING_QC_ASSIGN,"ListingQC"},    
//        { "LQCComplete", BatchTreeNode.LISTING_COMPLETE, "", "", "LQCComplete" },     
        { "Tally", BatchTreeNode.TALLY, BatchTreeNode.TALLY_QUEUED,
             BatchTreeNode.TALLY_ASSIGNED, "Tally" },      
        { "TComplete", BatchTreeNode.TALLY_COMPLETE, "", "", "TComplete" },
        {"ModifyErrors", BatchTreeNode.MODIFYERRORS, BatchTreeNode.MODIFYERRORS_QUEUED,
             BatchTreeNode.MODIFYERRORS_ASSIGNED, "ModifyErrors" },
        { "QCComplete", BatchTreeNode.QC_COMPLETE, "", "", "Coding QC Complete" },
        { "QA", BatchTreeNode.QA, BatchTreeNode.QA_QUEUED,
             BatchTreeNode.QA_ASSIGNED, "QA" },
        { "QAComplete", BatchTreeNode.QA_COMPLETE, "", "", "QA Complete" },
        { "QAError", BatchTreeNode.QA_ERROR, "", "", "QA Error" }};


    /** Label node for attachment ranges on batch tree */
    public static class AttachmentLabelNode extends BatchTreeNode {

        protected AttachmentLabelNode(Object rowData) {
            super(rowData, BatchTreeNode.LABEL);

            add(new ManagedNodeModel(RangeNode.makeModel(getRowId())) {
                    public ManagedNode makeChildNode(Object rowData) {
                        return new RangeNode(rowData);
                    }
                });
        }

        /** One-row model for label legend */
        public static ManagedTableModel makeModel(int batchId) {
            return new ManagedTableLabel("Documents", batchId);
        }
    }


    /** Attachment range node on batch tree */
    public static class RangeNode extends BatchTreeNode {
        private static String result = "";
        protected RangeNode (Object userObject) {
            super(userObject, BatchTreeNode.RANGE, /* allowsChildren => */ false);
        }

        /**
         * Model to select children for a batch.
         * <pre>
         * select C.child_id, P1. bates_number, P2.bates_number, P1.boundary_flag, C.lft
         * from child C                                                           
         *    inner join page P1 on P1.volume_id=C.volume_id and P1.seq=C.lft     
         *    inner join page P2 on P2.volume_id=C.volume_id and P2.seq=C.rgt     
         * where C.batch_id = ?                                                   
         * </pre>
         */
        
        
        public static ManagedTableModel makeModel(int batchId) {
            SQLManagedTableModel sqlModel =null;
            //sqlModel = SQLManagedTableModel.makeInstance ("Batching.children", batchId);            
              sqlModel = SQLManagedTableModel.makeInstance ("Batching.children_level", batchId);
            sqlModel.setColumnClass(3, Integer.class);
       
            return new ManagedTableSorter(3, sqlModel);
        }

        /**
         * Override to return first_bates - last_bates (boundary_flag).
         */
        public String toString() {
            TableRow row = (TableRow) getUserObject();
            String firstBates = (String) row.getValue(0);
            String lastBates = (String) row.getValue(1);
            String boundaryFlag = (String) row.getValue(2);
            StringBuffer buffer = new StringBuffer(firstBates);
            if (! firstBates.equals(lastBates)) {
                buffer.append(" - ");
                buffer.append(lastBates);
            }
            if (! "C".equals(boundaryFlag)) {
                buffer.append(" (");
                buffer.append(boundaryFlag);
                buffer.append(')');
            }
            return buffer.toString();
        }
    }
    
    public static String getLevel() {
        return level1;
    }

    public static  void setLevel(String level) {
        level1 = level;
    }
}
    
