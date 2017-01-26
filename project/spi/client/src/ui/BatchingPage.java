/* $Header: /home/common/cvsarea/ibase/dia/src/ui/BatchingPage.java,v 1.64.2.4 2006/08/23 19:04:52 nancy Exp $ */
/*
 * BatchingPage.java
 *
 * Created on December 14, 2003, 4:28 AM
 */
package ui;

import beans.AssignListingDialog;
import beans.AssignTallyDialog;
import beans.BatchCommentsDialog;
import beans.BatchDynamicTree;
import beans.BatchTreeNode;
import beans.DocsPerBatchDialog;
import beans.UsersDynamicTree;
import beans.UsersTreeNode;
import client.ClientTask;
import client.Global;
import client.TaskBatchBoundary;
import client.TaskBatchQueue;
import client.TaskBatchRemove;
import client.TaskCreateCodingBatches;
import client.TaskCreateListingBatches;
import client.TaskCreateModifyErrorBatches;
import client.TaskCreateTallyBatches;
import client.TaskDeleteVolume;
import client.TaskExecuteUpdate;
import client.TaskTeamsvolume;
import common.BatchStatus;
import common.Log;
import common.msg.MessageConstants;
import java.awt.Component;
import java.awt.Point;
import model.ProjectBatching;
import model.ManagedNode;
import model.ManagedNodeModel;
import model.ManagedTableModel;
import model.UserBatching;
import org.w3c.dom.Element;
import ui.AdminFrame;


import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.MenuListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;


/**
 * One of the <code>AdminFrame</code>, this is a two-sided page that shows
 * project/volume/batches in a tree format on one side and teams/users/batches 
 * on the other.  Batches and volumes can be queued by dragging them to a team
 * or user.  
 * @author  Bill
 */
public class BatchingPage extends ui.AbstractPage implements MessageConstants
{

   // TBD: Need to be able to change frame title

   private void setTitle(String text)
   {
      //Log.print("Set title to: "+text);
      frame.setTitle(text);
   }

   private ResultSet queryResult;
   final private boolean ALLOWS_CHILDREN = true;
   final private boolean NO_CHILDREN = false;

   /** the viewMode the user selected from the View menu */
   private String viewMode = null;
   // These are used in ProjectBatching

   final public static String ALL = "all";
   final public static String UNITIZE = "un";
   final public static String UQC = "uqc";
   final public static String CODING = "cqc";
   final public static String QC = "qc";
   final public static String QA = "qa";
   final public static String QACOMPLETE = "qacomplete";
   final public static String IDLE = "idle";
   final public static String COMPLETE = "complete";
   final public static String ASSIGNED = "assigned";
   final public static String MASKING = "Masking";
   final public static String MODIFYERRORS = "Modifyerrors";
   final public static String LISTING = "Listing";
   final public static String TALLY = "Tally";
   private String projectName = "";
   private String volumeName = "";
   private int projectId = -1;
   private int volumeId = -1;
   private int activeGroup = 0;
   /** current project and volume nodes */
   private BatchTreeNode projectNode;
   private BatchTreeNode volumeNode;
   private DefaultMutableTreeNode userSelectedNode = null;
   private DefaultMutableTreeNode selectNode = null;
   private TreePath path = null;

   /** from usersTree listener, always the currently-selected path(s) */
   private TreePath[] usersPath = null;
   //TreePath[] usersPaths = null;
    /** from usersTree listener, always the users node of the currently-selected node */

   private UsersTreeNode usersNode = null;

   /** from batchTree listener, always the currently-selected path(s) */
   private TreePath[] batchPath = null;
   private TreePath[] selectedPath = null;
   // ends here

   /** from batchTree listener, always the first node of the currently-selected nodes */
   private BatchTreeNode batchNode = null;
   private DefaultTreeModel batchModel;
   private DefaultTreeModel batchTreeModel;
   private DefaultTreeModel usersTreeModel;
   private TreePath dragOverTreePath = null;
   private JPopupMenu popupMenu = new JPopupMenu();
   private JPopupMenu usersPopupMenu = new JPopupMenu();
   // The client task to handle interaction with the server for this dialog

   private ClientTask task;

   //private TreeWillExpandListener treeWillExpandListener;

   /** Creates new form BatchingPage, modal, gets default project from viewer. */
   public BatchingPage(AdminFrame frame)
   {
      super(frame);

      // override isLeaf() of DefaultMutableTreeNode to show
        // + on the volume nodes before batch data has been loaded.
      BatchTreeNode root = new BatchTreeNode("Projects", "root", ALLOWS_CHILDREN);
      batchModel = new DefaultTreeModel(root)
              {

            @Override
                 public boolean isLeaf(Object node)
                 {
                    if (node instanceof BatchTreeNode) {
                       BatchTreeNode valueNode = (BatchTreeNode) node;                       
                       int count =  valueNode.getLeafCount();
                       for(int i=0;i<count;i++){                           
                           if(valueNode.getType().equals(BatchTreeNode.TALLY)){                               
                           }
                       }                       
                       if (valueNode.getType().equals(BatchTreeNode.VOLUME) || valueNode.getUserObject().equals("Attachment Ranges")) {
                          return false;
                       }

                    }
                    return super.isLeaf(node);
                 }

              };

      initComponents();

      batchTree = new BatchDynamicTree();
      batchTree.setModel(batchModel);
      usersTree = new UsersDynamicTree()
              {

            @Override
                 public void drop(DropTargetDropEvent e)
                 {
                    userDrop(e);
                 }

              };
      treePanel.add(batchTree, java.awt.BorderLayout.CENTER);
      treePanel2.add(usersTree, java.awt.BorderLayout.CENTER);

      addBatchCommentButton.setEnabled(false);

      setBatchTree();

      batchTreeModel = (DefaultTreeModel) batchTree.getTree().getModel();
      usersTreeModel = (DefaultTreeModel) usersTree.getTree().getModel();
      batchTreeModel.setAsksAllowsChildren(true);
      usersTreeModel.setAsksAllowsChildren(true);
      //(new ProjectBatching.RootNode(this)).setAsModelRoot(batchTreeModel);
      TreeWillExpandListener treeListener = new javax.swing.event.TreeWillExpandListener()
              {

            @Override
                 public void treeWillExpand(javax.swing.event.TreeExpansionEvent evt)
                         throws javax.swing.tree.ExpandVetoException
                 {
                    ManagedNode.checkRegisterOnTreeExpansion(evt);
                 }

            @Override
                 public void treeWillCollapse(javax.swing.event.TreeExpansionEvent evt)
                         throws javax.swing.tree.ExpandVetoException
                 {
                 }

              };
      batchTree.getTree().addTreeWillExpandListener(treeListener);
      usersTree.getTree().addTreeWillExpandListener(treeListener);
      createModifyErrorAction.setEnabled(false);
      popupMenu.add(increaseAction);
      popupMenu.add(decreaseAction);
      popupMenu.add(splitUnitizeAction);
      popupMenu.add(removeVolumeAction);
      popupMenu.add(createListingAction);
      popupMenu.add(createTallyAction);
      popupMenu.add(createModifyErrorAction);
      usersPopupMenu.add(removeAction);
   }

   /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        buttonGroup = new javax.swing.ButtonGroup();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        startBatchMenuItem = new javax.swing.JMenuItem();
        removeBatchMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        moveUpMenuItem = new javax.swing.JMenuItem();
        moveDownMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        browseMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        refreshMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        deleteVolumeMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        allMenuItem = new javax.swing.JRadioButtonMenuItem();
        unitizeMenuItem = new javax.swing.JRadioButtonMenuItem();
        unitizeQCMenuItem = new javax.swing.JRadioButtonMenuItem();
        codingMenuItem = new javax.swing.JRadioButtonMenuItem();
        codingQCMenuItem = new javax.swing.JRadioButtonMenuItem();
        qaMenuItem = new javax.swing.JRadioButtonMenuItem();
        completeMenuItem = new javax.swing.JRadioButtonMenuItem();
        idleMenuItem = new javax.swing.JRadioButtonMenuItem();
        assignedMenuItem = new javax.swing.JRadioButtonMenuItem();
        toolBar = new javax.swing.JToolBar();
        addBatchCommentButton = new javax.swing.JButton();
        unitizeLabel21211 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        unitizeLabel2 = new javax.swing.JLabel();
        unitizeLabel12 = new javax.swing.JLabel();
        unitizeLabel111 = new javax.swing.JLabel();
        unitizeLabel211 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        unitizeQCLabel2 = new javax.swing.JLabel();
        unitizeQCLabel21 = new javax.swing.JLabel();
        unitizeQCLabel211 = new javax.swing.JLabel();
        unitizeLabel2112 = new javax.swing.JLabel();
        jPanel31 = new javax.swing.JPanel();
        unitizeQCLabel22 = new javax.swing.JLabel();
        unitizeQCLabel212 = new javax.swing.JLabel();
        unitizeQCLabel2111 = new javax.swing.JLabel();
        unitizeLabel212 = new javax.swing.JLabel();
        jPanel311 = new javax.swing.JPanel();
        unitizeQCLabel221 = new javax.swing.JLabel();
        unitizeQCLabel2121 = new javax.swing.JLabel();
        unitizeQCLabel21111 = new javax.swing.JLabel();
        unitizeLabel2111 = new javax.swing.JLabel();
        jPanel3111 = new javax.swing.JPanel();
		jPanellisting = new javax.swing.JPanel();
		tallyPanel = new javax.swing.JPanel();
		jPanelmasking = new javax.swing.JPanel();
        jPanelmodifyError = new javax.swing.JPanel();
        unitizeQCLabel2211 = new javax.swing.JLabel();
		listingLabel  = new javax.swing.JLabel();
		listingCompleteLabel = new javax.swing.JLabel();
		tallyLabel  = new javax.swing.JLabel();
		tallyCompleteLabel = new javax.swing.JLabel();
		maskingLabel = new javax.swing.JLabel();
		maskingCompleteLabel  = new javax.swing.JLabel();
        modifyErrorLabel = new javax.swing.JLabel();
        unitizeQCLabel21211 = new javax.swing.JLabel();
        unitizeQCLabel211111 = new javax.swing.JLabel();
        unitizeLabel21 = new javax.swing.JLabel();
        jSplitPane = new javax.swing.JSplitPane();
        batchPanel = new javax.swing.JPanel();
        treePanel = new javax.swing.JPanel();
        usersPanel = new javax.swing.JPanel();
        treePanel2 = new javax.swing.JPanel();
        maskingQueueLabel = new javax.swing.JLabel();
		maskingAssignLabel = new javax.swing.JLabel();
		emptyLabel1 = new javax.swing.JLabel();
		emptyLabel2 = new javax.swing.JLabel();
		emptyLabel3 = new javax.swing.JLabel();
		emptyLabel4 = new javax.swing.JLabel();
		emptyLabel5 = new javax.swing.JLabel();
		modifyErrorAssignLabel = new javax.swing.JLabel();
		modifyErrorQueueLabel = new javax.swing.JLabel();

		tallyMenuItem = new javax.swing.JRadioButtonMenuItem();
		listingMenuItem = new javax.swing.JRadioButtonMenuItem();
		maskingMenuItem = new javax.swing.JRadioButtonMenuItem();
        modifyErrorsMenuItem = new javax.swing.JRadioButtonMenuItem();

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");
        exitMenuItem.setMnemonic('E');
        exitMenuItem.setText("Exit");
        exitMenuItem.setToolTipText("Exit this screen and return to the Activity Selection screen.");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        toolsMenu.setMnemonic('T');
        toolsMenu.setText("Tools");
        toolsMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toolsMenuActionPerformed(evt);
            }
        });

        startBatchMenuItem.setMnemonic('S');
        startBatchMenuItem.setText("Start Batch");
        startBatchMenuItem.setToolTipText("Begin a batch with the currently-selected document.");
        startBatchMenuItem.setEnabled(false);
        startBatchMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startBatchMenuItemActionPerformed(evt);
            }
        });

        toolsMenu.add(startBatchMenuItem);

        removeBatchMenuItem.setMnemonic('R');
        removeBatchMenuItem.setText("Remove Batch Boundary");
        removeBatchMenuItem.setToolTipText("Remove the currently-selected batch boundary.");
        removeBatchMenuItem.setEnabled(false);
        removeBatchMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeBatchMenuItemActionPerformed(evt);
            }
        });

        toolsMenu.add(removeBatchMenuItem);

        toolsMenu.add(jSeparator1);

        moveUpMenuItem.setMnemonic('U');
        moveUpMenuItem.setText("Move Up");
        moveUpMenuItem.setToolTipText("Move the currently-selected document to the previous batch.");
        moveUpMenuItem.setEnabled(false);
        moveUpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveUpMenuItemActionPerformed(evt);
            }
        });

        toolsMenu.add(moveUpMenuItem);

        moveDownMenuItem.setMnemonic('D');
        moveDownMenuItem.setText("Move Down");
        moveDownMenuItem.setToolTipText("Move the currently-selected document to the next batch.");
        moveDownMenuItem.setEnabled(false);
        moveDownMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveDownMenuItemActionPerformed(evt);
            }
        });

        toolsMenu.add(moveDownMenuItem);

        toolsMenu.add(jSeparator2);

        browseMenuItem.setMnemonic('U');
        browseMenuItem.setText("Browse");
        browseMenuItem.setToolTipText("Open the selected batch");
        browseMenuItem.setEnabled(false);
        browseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseMenuItemActionPerformed(evt);
            }
        });

        toolsMenu.add(browseMenuItem);

        toolsMenu.add(jSeparator3);

        deleteVolumeMenuItem.setText("Delete Volume");
        deleteVolumeMenuItem.setToolTipText("Delete the selected volume.");
        deleteVolumeMenuItem.setEnabled(false);
        deleteVolumeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteVolumeMenuItemActionPerformed(evt);
            }
        });

        toolsMenu.add(deleteVolumeMenuItem);
        
        toolsMenu.add(jSeparator4);
        
        refreshMenuItem.setText("Refesh");
        refreshMenuItem.setToolTipText("Refresh the project and batch tree.");
        refreshMenuItem.setEnabled(true);
        refreshMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshMenuItemActionPerformed(evt);
            }
        });
        
        toolsMenu.add(refreshMenuItem);        
        //ends here
        
        menuBar.add(toolsMenu);

        viewMenu.setMnemonic('V');
        viewMenu.setText("View");
        allMenuItem.setMnemonic('A');
        allMenuItem.setSelected(true);
        allMenuItem.setText("All");
        allMenuItem.setToolTipText("Show all batches.");
        buttonGroup.add(allMenuItem);
        allMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(allMenuItem);

        unitizeMenuItem.setMnemonic('U');
        unitizeMenuItem.setText("Unitize");
        unitizeMenuItem.setToolTipText("Show batches in Unitize.");
        buttonGroup.add(unitizeMenuItem);
        unitizeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unitizeMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(unitizeMenuItem);

        unitizeQCMenuItem.setMnemonic('N');
        unitizeQCMenuItem.setText("Unitize QC");
        unitizeQCMenuItem.setToolTipText("Show batches in Unitize QC.");
        buttonGroup.add(unitizeQCMenuItem);
        unitizeQCMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unitizeQCMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(unitizeQCMenuItem);

        codingMenuItem.setMnemonic('C');
        codingMenuItem.setText("Coding");
        codingMenuItem.setToolTipText("Show batches in coding.");
        buttonGroup.add(codingMenuItem);
        codingMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codingMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(codingMenuItem);

        codingQCMenuItem.setMnemonic('O');
        codingQCMenuItem.setText("Coding QC");
        codingQCMenuItem.setToolTipText("Show batches in Coding QC.");
        buttonGroup.add(codingQCMenuItem);
        codingQCMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codingQCMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(codingQCMenuItem);

        qaMenuItem.setMnemonic('Q');
        qaMenuItem.setText("QA");
        qaMenuItem.setToolTipText("Show batches in QA.");
        buttonGroup.add(qaMenuItem);
        qaMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qaMenuItemActionPerformed(evt);
            }
        });
        
		
        viewMenu.add(qaMenuItem);
       		
        //masking
        maskingMenuItem.setMnemonic('M');
        maskingMenuItem.setText("Masking");
        maskingMenuItem.setToolTipText("Show batches in Masking.");
        buttonGroup.add(maskingMenuItem);
        maskingMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskingMenuItemActionPerformed(evt);
            }
        });
        
        viewMenu.add(maskingMenuItem);

		//listing
        listingMenuItem.setMnemonic('M');
        listingMenuItem.setText("Listing");
        listingMenuItem.setToolTipText("Show batches in Listing.");
        buttonGroup.add(listingMenuItem);
        listingMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listingMenuItemActionPerformed(evt);
            }
        });
        
        viewMenu.add(listingMenuItem);
        //tally
		tallyMenuItem.setMnemonic('M');
        tallyMenuItem.setText("Tally");
        tallyMenuItem.setToolTipText("Show batches in Tally.");
        buttonGroup.add(tallyMenuItem);
        tallyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tallyMenuItemActionPerformed(evt);
            }
        });
        
        viewMenu.add(tallyMenuItem);
        
        

        modifyErrorsMenuItem.setMnemonic('M');
        modifyErrorsMenuItem.setText("ModifyErrors");
        modifyErrorsMenuItem.setToolTipText("Show batches in ModifyErrors.");
        buttonGroup.add(modifyErrorsMenuItem);
        modifyErrorsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modifyErrorsMenuItemActionPerformed(evt);
            }
        });
        
        viewMenu.add(modifyErrorsMenuItem);

        completeMenuItem.setMnemonic('M');
        completeMenuItem.setText("Complete");
        completeMenuItem.setToolTipText("Show batches that have been completed.");
        buttonGroup.add(completeMenuItem);
        completeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                completeMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(completeMenuItem);

        idleMenuItem.setMnemonic('I');
        idleMenuItem.setText("Idle");
        idleMenuItem.setToolTipText("Show batches that are not assigned and in no queues.");
        buttonGroup.add(idleMenuItem);
        idleMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idleMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(idleMenuItem);
        
        assignedMenuItem.setMnemonic('G');
        assignedMenuItem.setText("Assigned");
        assignedMenuItem.setToolTipText("Show batches that are not assigned and in no queues.");
        buttonGroup.add(assignedMenuItem);
        assignedMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assignedMenuItemActionPerformed(evt);
            }
        });

        viewMenu.add(assignedMenuItem);
        // ends here
        
        menuBar.add(viewMenu);

        setLayout(new java.awt.BorderLayout());

        toolBar.addSeparator();
        addBatchCommentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addBatchComment.gif")));
        addBatchCommentButton.setToolTipText("Add/View Batch Comments");
        addBatchCommentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBatchCommentButtonActionPerformed(evt);
            }
        });

        toolBar.add(addBatchCommentButton);

        unitizeLabel21211.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeLabel21211.setForeground(new java.awt.Color(204, 51, 0));
        unitizeLabel21211.setIcon(new javax.swing.ImageIcon(""));
        unitizeLabel21211.setText("     ");
        toolBar.add(unitizeLabel21211);

        jPanel1.setLayout(new java.awt.GridLayout(9, 6));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel21.setLayout(new java.awt.GridLayout(1, 0));

        unitizeLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeLabel2.setForeground(new java.awt.Color(204, 153, 0));
        unitizeLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/newdoc.gif")));
        unitizeLabel2.setText("Unitize Queue ");
        jPanel21.add(unitizeLabel2);

        unitizeLabel12.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeLabel12.setForeground(new java.awt.Color(0, 102, 204));
        unitizeLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/newdoc.gif")));
        unitizeLabel12.setText("Unitize Assigned  ");
        jPanel21.add(unitizeLabel12);

        unitizeLabel111.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeLabel111.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/newdoc.gif")));
        unitizeLabel111.setText("Unitize Idle");
        jPanel21.add(unitizeLabel111);

        unitizeLabel211.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeLabel211.setForeground(new java.awt.Color(204, 51, 0));
        unitizeLabel211.setIcon(new javax.swing.ImageIcon(""));
        jPanel21.add(unitizeLabel211);

        jPanel1.add(jPanel21);

        jPanel3.setLayout(new java.awt.GridLayout(1, 0));

        unitizeQCLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeQCLabel2.setForeground(new java.awt.Color(204, 153, 0));
        unitizeQCLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/uqc.gif")));
        unitizeQCLabel2.setText("Unitize QC Queue ");
        jPanel3.add(unitizeQCLabel2);

        unitizeQCLabel21.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeQCLabel21.setForeground(new java.awt.Color(0, 102, 204));
        unitizeQCLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/uqc.gif")));
        unitizeQCLabel21.setText("Unitize QC Assigned");
        jPanel3.add(unitizeQCLabel21);

        unitizeQCLabel211.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeQCLabel211.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/uqc.gif")));
        unitizeQCLabel211.setText("Unitize QC Idle");
        jPanel3.add(unitizeQCLabel211);

        unitizeLabel2112.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeLabel2112.setForeground(new java.awt.Color(204, 51, 0));
        unitizeLabel2112.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/uqc.gif")));
        unitizeLabel2112.setText("Unitize QC Complete");
        jPanel3.add(unitizeLabel2112);

        jPanel1.add(jPanel3);

        jPanel31.setLayout(new java.awt.GridLayout(1, 0));

        unitizeQCLabel22.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeQCLabel22.setForeground(new java.awt.Color(204, 153, 0));
        unitizeQCLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/coding.gif")));
        unitizeQCLabel22.setText("Coding Queue");
        jPanel31.add(unitizeQCLabel22);

        unitizeQCLabel212.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeQCLabel212.setForeground(new java.awt.Color(0, 102, 204));
        unitizeQCLabel212.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/coding.gif")));
        unitizeQCLabel212.setText("Coding Assigned");
        jPanel31.add(unitizeQCLabel212);

        unitizeQCLabel2111.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeQCLabel2111.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/coding.gif")));
        unitizeQCLabel2111.setText("Coding Idle");
        jPanel31.add(unitizeQCLabel2111);

        unitizeLabel212.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeLabel212.setForeground(new java.awt.Color(204, 51, 0));
        unitizeLabel212.setIcon(new javax.swing.ImageIcon(""));
        jPanel31.add(unitizeLabel212);

        jPanel1.add(jPanel31);

        jPanel311.setLayout(new java.awt.GridLayout(1, 0));

        unitizeQCLabel221.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeQCLabel221.setForeground(new java.awt.Color(204, 153, 0));
        unitizeQCLabel221.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/magnify.gif")));
        unitizeQCLabel221.setText("Coding QC Queue ");
        jPanel311.add(unitizeQCLabel221);

        unitizeQCLabel2121.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeQCLabel2121.setForeground(new java.awt.Color(0, 102, 204));
        unitizeQCLabel2121.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/magnify.gif")));
        unitizeQCLabel2121.setText("Coding QC Assigned");
        jPanel311.add(unitizeQCLabel2121);

        unitizeQCLabel21111.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeQCLabel21111.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/magnify.gif")));
        unitizeQCLabel21111.setText("Coding QC Idle");
        jPanel311.add(unitizeQCLabel21111);

        unitizeLabel2111.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeLabel2111.setForeground(new java.awt.Color(204, 51, 0));
        unitizeLabel2111.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/magnify.gif")));
        unitizeLabel2111.setText("Coding QC Complete");
        jPanel311.add(unitizeLabel2111);
         
        jPanel1.add(jPanel311);
        
        jPanelmasking.setLayout(new java.awt.GridLayout(1, 0));


        maskingQueueLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        maskingQueueLabel.setForeground(new java.awt.Color(204, 153, 0));
        maskingQueueLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/masking.gif")));
        maskingQueueLabel.setText(BatchStatus.S_MASKINGQUEUE);
        jPanelmasking.add(maskingQueueLabel);

        maskingAssignLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        maskingAssignLabel.setForeground(new java.awt.Color(0, 102, 204));
        maskingAssignLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/masking.gif")));
        maskingAssignLabel.setText(BatchStatus.S_MASKINGASSIGN);
        jPanelmasking.add(maskingAssignLabel);

        maskingLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        //listingLabel.setForeground(new java.awt.Color(204, 153, 0));
        maskingLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/masking.gif")));
        maskingLabel.setText(BatchStatus.S_MASKING);

        jPanelmasking.add(maskingLabel);
        
        maskingCompleteLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        maskingCompleteLabel.setForeground(new java.awt.Color(204, 51, 0));
        maskingCompleteLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/masking.gif")));
        maskingCompleteLabel.setText(BatchStatus.S_MASKINGCOMPLETE);

        jPanelmasking.add(maskingCompleteLabel);

        jPanel1.add(jPanelmasking);


        jPanellisting.setLayout(new java.awt.GridLayout(1, 0));
        
		emptyLabel1.setFont(new java.awt.Font("Dialog", 0, 12));
        emptyLabel1.setForeground(new java.awt.Color(204, 153, 0));
        //emptyLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/listing.gif")));
        emptyLabel1.setText("          ");
        jPanellisting.add(emptyLabel1);
        
		emptyLabel2.setFont(new java.awt.Font("Dialog", 0, 12));
        emptyLabel2.setForeground(new java.awt.Color(204, 153, 0));
       // emptyLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/listing.gif")));
        emptyLabel2.setText("          ");
        jPanellisting.add(emptyLabel2);

        listingLabel.setFont(new java.awt.Font("Dialog", 0, 12));
       // listingLabel.setForeground(new java.awt.Color(204, 153, 0));
        listingLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/listing.gif")));
        listingLabel.setText(BatchStatus.S_LISTING);

        jPanellisting.add(listingLabel);
        
        listingCompleteLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        listingCompleteLabel.setForeground(new java.awt.Color(204, 51, 0));
        listingCompleteLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/listing.gif")));
        listingCompleteLabel.setText(BatchStatus.S_LISTINGCOMPLETE);

        jPanellisting.add(listingCompleteLabel);

		jPanel1.add(jPanellisting);

        tallyPanel.setLayout(new java.awt.GridLayout(1, 0));
        
		emptyLabel3.setFont(new java.awt.Font("Dialog", 0, 12));
        emptyLabel3.setForeground(new java.awt.Color(204, 153, 0));
        //emptyLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/listing.gif")));
        emptyLabel3.setText("          ");
        tallyPanel.add(emptyLabel3);
        
		emptyLabel4.setFont(new java.awt.Font("Dialog", 0, 12));
        emptyLabel4.setForeground(new java.awt.Color(204, 153, 0));
       // emptyLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/listing.gif")));
        emptyLabel4.setText("          ");
        tallyPanel.add(emptyLabel4);

        tallyLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        //tallyLabel.setForeground(new java.awt.Color(204, 153, 0));
        tallyLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/tally.gif")));
        tallyLabel.setText(BatchStatus.S_TALLY);

        tallyPanel.add(tallyLabel);
        
		tallyCompleteLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        tallyCompleteLabel.setForeground(new java.awt.Color(204, 51, 0));
        tallyCompleteLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/tally.gif")));
        tallyCompleteLabel.setText(BatchStatus.S_TALLYCOMPLETE);

        tallyPanel.add(tallyCompleteLabel);

        jPanel1.add(tallyPanel);
       
        jPanel3111.setLayout(new java.awt.GridLayout(1, 0));

        unitizeQCLabel2211.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeQCLabel2211.setForeground(new java.awt.Color(204, 153, 0));
        unitizeQCLabel2211.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/check.gif")));
        unitizeQCLabel2211.setText("QA Queue ");
        jPanel3111.add(unitizeQCLabel2211);

        unitizeQCLabel21211.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeQCLabel21211.setForeground(new java.awt.Color(0, 102, 204));
        unitizeQCLabel21211.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/check.gif")));
        unitizeQCLabel21211.setText("QA Assigned");
        jPanel3111.add(unitizeQCLabel21211);

        unitizeQCLabel211111.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeQCLabel211111.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/check.gif")));
        unitizeQCLabel211111.setText("QA Idle");
        jPanel3111.add(unitizeQCLabel211111);

        unitizeLabel21.setFont(new java.awt.Font("Dialog", 0, 12));
        unitizeLabel21.setForeground(new java.awt.Color(204, 51, 0));
        unitizeLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/star.gif")));
        unitizeLabel21.setText("QA Complete");
        jPanel3111.add(unitizeLabel21);
     
        jPanel1.add(jPanel3111);
        
		jPanelmodifyError.setLayout(new java.awt.GridLayout(1, 0));
        
        modifyErrorQueueLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        modifyErrorQueueLabel.setForeground(new java.awt.Color(204, 153, 0));
        modifyErrorQueueLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/modifyerror.gif")));
        modifyErrorQueueLabel.setText(BatchStatus.S_MODIFYERRORSQUEUE);
        jPanelmodifyError.add(modifyErrorQueueLabel);

        modifyErrorAssignLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        modifyErrorAssignLabel.setForeground(new java.awt.Color(0, 102, 204));
        modifyErrorAssignLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/modifyerror.gif")));
        modifyErrorAssignLabel.setText(BatchStatus.S_MODIFYERRORSASSIGN);
        jPanelmodifyError.add(modifyErrorAssignLabel);

        modifyErrorLabel.setFont(new java.awt.Font("Dialog", 0, 12));
        //listingLabel.setForeground(new java.awt.Color(204, 153, 0));
        modifyErrorLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/modifyerror.gif")));
        modifyErrorLabel.setText(BatchStatus.S_MODIFYERRORS);
       
        jPanelmodifyError.add(modifyErrorLabel);
                
		emptyLabel5.setFont(new java.awt.Font("Dialog", 0, 12));
        emptyLabel5.setForeground(new java.awt.Color(204, 153, 0));
       // emptyLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/listing.gif")));
        emptyLabel5.setText("          ");
        jPanelmodifyError.add(emptyLabel5);

		jPanel1.add(jPanelmodifyError);

        toolBar.add(jPanel1);

        add(toolBar, java.awt.BorderLayout.NORTH);

        jSplitPane.setDividerLocation(320);
        batchPanel.setLayout(new java.awt.BorderLayout());

        treePanel.setLayout(new java.awt.BorderLayout());

        batchPanel.add(treePanel, java.awt.BorderLayout.CENTER);

        jSplitPane.setLeftComponent(batchPanel);

        usersPanel.setLayout(new java.awt.BorderLayout());

        treePanel2.setLayout(new java.awt.BorderLayout());

        usersPanel.add(treePanel2, java.awt.BorderLayout.CENTER);

        jSplitPane.setRightComponent(usersPanel);

        add(jSplitPane, java.awt.BorderLayout.CENTER);

    }//GEN-END:initComponents

    private void deleteVolumeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteVolumeMenuItemActionPerformed
       try {
          final String volumeName = volumeNode.toString();
          Object[] options = {"Yes", "No"};
          int ok = JOptionPane.showOptionDialog(BatchingPage.this,
                  "Do you want to delete this volume: " + volumeName + "?" + "\n\nAll coding data and error statistics will be removed.",
                  "Delete Verification",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.QUESTION_MESSAGE,
                  null,
                  options,
                  options[1]);
          if (ok == JOptionPane.OK_OPTION) {
             final ClientTask task = new TaskDeleteVolume(Integer.parseInt(volumeNode.getKey()));
             task.setCallback(new Runnable()
                     {

                        public void run()
                        {
                           try {
                              JOptionPane.showMessageDialog(BatchingPage.this,
                                      "Volume " + volumeName + " has been deleted.",
                                      "Confirm Delete",
                                      JOptionPane.INFORMATION_MESSAGE);
                           } catch (Throwable th) {
                              Log.quit(th);
                           }
                        }

                     });
             task.enqueue();
          }
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_deleteVolumeMenuItemActionPerformed

    private void browseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseMenuItemActionPerformed
       //Log.print("browse pressed");
       try {
          TreePath currentSelection = batchTree.getSelectionPath();
          if (currentSelection != null) {
             BatchTreeNode currentNode = (ProjectBatching.BatchNode) (currentSelection.getLastPathComponent());
             int batchId = Integer.parseInt(currentNode.getKey());
             activeGroup = Integer.parseInt(currentNode.getValue(6));
             ProjectBatching.VolumeNode vNode = (ProjectBatching.VolumeNode) currentNode.getParent();
             int volumeId = Integer.parseInt(vNode.getKey());
             ProjectBatching.ProjectNode pNode = (ProjectBatching.ProjectNode) vNode.getParent();
             String project = pNode.toString();
             int projectId = Integer.parseInt(pNode.getKey());
             Log.print("ready to open b=" + batchId + " v=" + volumeId + " p=" + project);
             SplitPaneViewer viewer = SplitPaneViewer.getInstance();
             viewer.setBatchId(batchId);
             viewer.setVolumeId(volumeId);
             viewer.setActiveGroup(activeGroup);
             viewer.initializeForProject(project, projectId,
                     /* whichStatus=> */ "Admin",
                     pNode.canSplitDocuments());
             // Open the viewer.
             viewer.setVisible(true);
          }
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_browseMenuItemActionPerformed

   private void refreshMenuItemActionPerformed(java.awt.event.ActionEvent evt)
   {
      for (int idx = 0; idx < batchTree.getTree().getRowCount(); idx++) {
         TreePath treePath = batchTree.getTree().getPathForRow(idx);

         if ((treePath.getLastPathComponent() instanceof ProjectBatching.VolumeNode) && (batchTree.getTree().isExpanded(idx))) {
            BatchTreeNode currentNode = (ProjectBatching.VolumeNode) (treePath.getLastPathComponent());
            int volumeId = Integer.parseInt(currentNode.getKey());
            String sqlName = "Batching.batches";
            model.SQLManagedTableModel sqlModel = model.SQLManagedTableModel.makeInstance(sqlName, volumeId);
            String sqlNameWithId = (volumeId == 0
                    ? sqlName : sqlName + "[" + volumeId + "]");
            ClientTask ctask = new TaskOpenModel(sqlModel, sqlNameWithId);
            ctask.enqueue();
         }

         if (batchTree.getTree().isExpanded(idx) && (treePath.getLastPathComponent() instanceof ProjectBatching.BatchNode)) {
            BatchTreeNode currentNode = (ProjectBatching.BatchNode) (treePath.getLastPathComponent());
            int id = Integer.parseInt(currentNode.getKey());
            System.out.println("currentNode getTypeLevel()-----------------"     + currentNode.getTypeLevel());
            //commented
            //String sqlName = "Batching.children";
            //added for L1
            String sqlName = "Batching.children_level";
            model.SQLManagedTableModel sqlModel = model.SQLManagedTableModel.makeInstance(sqlName, id);
            String sqlNameWithId = (id == 0
                    ? sqlName : sqlName + "[" + id + "]");
            ClientTask ctask = new TaskOpenModel(sqlModel, sqlNameWithId);
            ctask.enqueue();
         }
      }
   }
   //ends here

   private static class TaskOpenModel extends ClientTask
   {

      private String sqlName;
      private model.SQLManagedTableModel model;

      private TaskOpenModel(model.SQLManagedTableModel model, String sqlName)
      {
         this.model = model;
         this.sqlName = sqlName;
      }

      public void run() throws java.io.IOException
      {
         openModel(model, sqlName);
      }

      public void openModel(final model.SQLManagedTableModel model, String sqlName)
              throws java.io.IOException
      {
         client.ServerConnection scon = Global.theServerConnection;

         common.msg.MessageWriter writer;
         writer = scon.startMessage(T_OPEN_MANAGED_MODEL);
         writer.writeAttribute(A_NAME, sqlName);
         writer.endElement();
         writer.close();

         final org.w3c.dom.Element reply = scon.receiveMessage();

         if (T_RESULT_SET.equals(reply.getNodeName())) {
            SwingUtilities.invokeLater(new Runnable()
                    {

                       public void run()
                       {
                          try {
                          //System.out.println("reply: " + reply);
                          } catch (Throwable th) {
                             Log.quit(th);
                          }
                       }

                    });
         }
         else {
            Log.quit("SQLManagedTableModel: unexpected message type: " + reply.getNodeName());
         }
      }

   }
   //ends here

    private void toolsMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolsMenuActionPerformed
    // Add your handling code here:
    }//GEN-LAST:event_toolsMenuActionPerformed

   /**
     * Return the viewMode property.
     * This is used by ProjectBatching to filter the batches that are visible.
     */
   public String getViewMode()
   {
      return viewMode;
   }

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
       try {
          exitForm();
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void idleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idleMenuItemActionPerformed
       try {
          setTitle("SPiCA Batching - Idle Batches");
          setView(IDLE);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_idleMenuItemActionPerformed
    

	private void tallyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tallyMenuItemActionPerformed
       try {
          setTitle("SPiCA Batching - Tally Batches");
          setView(TALLY);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_tallyMenuItemActionPerformed
    
	
	private void listingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listingMenuItemActionPerformed
       try {
          setTitle("SPiCA Batching - Listing Batches");
          setView(LISTING);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_listingMenuItemActionPerformed
    
	private void maskingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskingMenuItemActionPerformed
       try {
          setTitle("SPiCA Batching - Masking Batches");
          setView(MASKING);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_maskingMenuItemActionPerformed
    
	private void modifyErrorsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modifyErrorsMenuItemActionPerformed
       try {
          setTitle("SPiCA Batching - ModifyErrors Batches");
          setView(MODIFYERRORS);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_modifyErrorsMenuItemActionPerformed


    private void completeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_completeMenuItemActionPerformed
       try {
          setTitle("SPiCA Batching - Complete Batches");
          setView(COMPLETE);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_completeMenuItemActionPerformed

   private void assignedMenuItemActionPerformed(java.awt.event.ActionEvent evt)
   {
      try {
         setTitle("SPiCA Batching - Assigned Batches");
         setView(ASSIGNED);
      } catch (Throwable th) {
         Log.quit(th);
      }
   }
   // ends here

   /**
     * Show BatchCommentsDialog for the currently-selected batchTree batch to allow
     * the user to enter or edit comments.
     */
    private void addBatchCommentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBatchCommentButtonActionPerformed
       try {
          TreePath currentSelection = batchTree.getSelectionPath();
          if (currentSelection == null) {
             return;
          }
          BatchTreeNode batchNode = (BatchTreeNode) (currentSelection.getLastPathComponent());
          String key = batchNode.getKey();
          System.out.println("key: " + key);
          if (!batchNode.isBatch() && !batchNode.isAssigned()) {
             // If this happens, the AddBatchCommentButton has been erroneously enabled.
             Log.print("(Batching.addBatchComment) !!! Add comment not a batch !!! " + batchNode);
             return;
          }

          BatchCommentsDialog.showDialog(Integer.parseInt(batchNode.getKey()), (Component) this, /*showIfNoComments->*/ true);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_addBatchCommentButtonActionPerformed

    private void qaMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qaMenuItemActionPerformed
       try {
          setTitle("SPiCA Batching - QA Batches");
          setView(QA);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_qaMenuItemActionPerformed

    private void codingQCMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codingQCMenuItemActionPerformed
       try {
          setTitle("SPiCA Batching - Coding QC Batches");
          setView(QC);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_codingQCMenuItemActionPerformed

    private void codingMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_codingMenuItemActionPerformed
       try {
          setTitle("SPiCA Batching - Coding Batches");
          setView(CODING);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_codingMenuItemActionPerformed

    private void unitizeQCMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unitizeQCMenuItemActionPerformed
       try {
          setTitle("SPiCA Batching - Unitize QC Batches");
          setView(UQC);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_unitizeQCMenuItemActionPerformed

    private void unitizeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unitizeMenuItemActionPerformed
       try {
          setTitle("SPiCA Batching - Unitize Batches");
          setView(UNITIZE);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_unitizeMenuItemActionPerformed

    private void allMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allMenuItemActionPerformed
       try {
          setTitle("SPiCA Batching - All Batches");
          setView(ALL);
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_allMenuItemActionPerformed

   private void setView(String viewMode)
   {
       System.out.println("viewMode1=============== "+  viewMode);
        System.out.println("viewMode2=============== "+  this.viewMode);
      if (!viewMode.equals(this.viewMode)) {
         this.viewMode = viewMode;
         // Force the tree to refresh
         System.out.println("111111111111111111"  + this.viewMode);
         ManagedNode root = new ProjectBatching.RootNode(this);
         root.setAsModelRoot(batchTreeModel);
         ManagedNode userTeamRoot = new UserBatching.RootNode(this);
         userTeamRoot.setAsModelRoot(usersTreeModel);
         root.register();
      }
   }

   /**
    * Move the currently-selected document down to the following batch.
    * The currently-selected doc must be the last doc in a batch and it
    * will become the first node in the following batch.
     */
    private void moveDownMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveDownMenuItemActionPerformed
       try {
          Log.print("moveDown pressed");
          TreePath currentSelection = batchTree.getSelectionPath();
          if (currentSelection != null) {
             // get the child_id
             BatchTreeNode currentNode = (ProjectBatching.RangeNode) (currentSelection.getLastPathComponent());
             int childId = Integer.parseInt(currentNode.getKey());
             Log.print("...childId=" + childId);
             ClientTask task = new TaskBatchBoundary(childId, /* move down->*/ 1);
             task.enqueue(this);
          }
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_moveDownMenuItemActionPerformed

   /**
     * Move the currently-selected document up to the previous batch.
     * The currently-selected doc must be the first doc in a batch and it
     * will become the last node in the previous batch.
     */
    private void moveUpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveUpMenuItemActionPerformed
       Log.print("moveUp pressed");
       try {
          TreePath currentSelection = batchTree.getSelectionPath();
          if (currentSelection != null) {
             // get the page_id
             BatchTreeNode currentNode = (ProjectBatching.RangeNode) (currentSelection.getLastPathComponent());
             int rangeId = Integer.parseInt(currentNode.getKey());
             ClientTask task = new TaskBatchBoundary(rangeId, /* move up->*/ -1);
             task.enqueue(this);
          }
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_moveUpMenuItemActionPerformed

    private void removeBatchMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeBatchMenuItemActionPerformed
       try {
          TreePath currentSelection = batchTree.getSelectionPath();
          if (currentSelection != null) {
             // get the page_id
             BatchTreeNode currentNode = (ProjectBatching.BatchNode) (currentSelection.getLastPathComponent());
             int batchId = Integer.parseInt(currentNode.getKey());
             ClientTask task = new TaskBatchRemove(batchId);
             task.enqueue(this);
          }
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_removeBatchMenuItemActionPerformed

    private void startBatchMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startBatchMenuItemActionPerformed
       try {
          TreePath currentSelection = batchTree.getSelectionPath();
          if (currentSelection != null) {
             // get the page_id
             BatchTreeNode currentNode = (ProjectBatching.RangeNode) (currentSelection.getLastPathComponent());
             int rangeId = Integer.parseInt(currentNode.getKey());
             ClientTask task = new TaskBatchBoundary(rangeId, /* add->*/ 0);
             task.enqueue(this);
          }
       } catch (Throwable th) {
          Log.quit(th);
       }
    }//GEN-LAST:event_startBatchMenuItemActionPerformed

   private void userDrop(DropTargetDropEvent e)
   {
      try {
         // the drag terminated with a drop on this DropTarget
         System.out.println("userDrop-------------->");
         Transferable tr = e.getTransferable();

         String level = "";

         if (tr.isDataFlavorSupported(BatchTreeNode.BATCH_FLAVOR)) {
            Log.print("(BatchingPage.userDrop) is BATCH_FLAVOR");
            batchNode = (BatchTreeNode) tr.getTransferData(BatchTreeNode.BATCH_FLAVOR);
            // a batch node is being dragged -- is it allowed?
            BatchTreeNode bNode = (BatchTreeNode) batchPath[0].getLastPathComponent();
            level = bNode.getTypeLevel();
            usersPath = null;
            batchPath = batchTree.getSelectionPaths();
            if (batchPath == null || !bNode.isTransferable()) {
               // drag from batchTree not allowed -- shouldn't get here
               Log.print("(Batching.DynamicTree).drop batchNode not transferable");
               if (e != null) {
                  e.rejectDrop();
               }
               e.getDropTargetContext().dropComplete(true);
               return;
            }
         }
         else if (tr.isDataFlavorSupported(UsersTreeNode.USER_FLAVOR)) {
            usersNode = (UsersTreeNode) tr.getTransferData(UsersTreeNode.USER_FLAVOR);
            // a user node is being dragged -- is it allowed?
            UsersTreeNode uNode = (UsersTreeNode) usersPath[0].getLastPathComponent();
            Log.print("(BatchingPage.userDrop) is USER_FLAVOR " + uNode);
            batchPath = null;
            usersPath = usersTree.getSelectionPaths();
            //Log.print("  usersPath " + usersPath);
            if (usersPath == null || !uNode.isTransferable()) {
               // drag from usersTree not allowed -- shouldn't get here
               Log.print("(Batching.DynamicTree).drop node not transferable");
               if (e != null) {
                  e.rejectDrop();
               }
               e.getDropTargetContext().dropComplete(true);
               return;
            }
            level = uNode.getTypeLevel();
         }
         else {
            Log.print("(Batching userDrop) !!! unknown drag flavor !!!");
            return;
         }

         UsersTreeNode newParent = null;
         if (e != null) {
            Point loc = e.getLocation();
            TreePath destinationPath = usersTree.getPathForLocation(loc.x, loc.y);            
            if (destinationPath == null) {
               return; // drop not on usersTree
            }
            newParent = (UsersTreeNode) destinationPath.getLastPathComponent();
         }
         else {
            newParent = usersNode;
         }
         if (newParent == null) {
            Log.print("(Batching.DynamicTree).drop node not droppable");
            if (e != null) {
               e.rejectDrop();
            }
            e.getDropTargetContext().dropComplete(true);
            return;
         }
         //Log.print("(Batching.userDrop) newParent is " + newParent);
         int usersId = 0;
         int teamsId = 0;
         if (newParent.isTeam()) {
            teamsId = newParent.getId();
         }
         else {
            usersId = newParent.getId();
         }

         BatchTreeNode volumeNode = null;
         TreePath[] tp;
         int batchId = 0;
         if (level.equals(BatchTreeNode.VOLUME)) {
            Log.print("(Batching.userDrop) drag is volume");
            // The dragged data came from batchTree
            tp = batchTree.getSelectionPaths();
            for (int i = 0; i < tp.length; i++) {
               // get each child and enqueue a task to add it to the queue
               volumeNode = (BatchTreeNode) tp[i].getLastPathComponent();
               if (volumeNode.isTransferable()) {
                  volumeId = Integer.parseInt(volumeNode.getKey());
                  if (volumeId > 0 && teamsId > 0) {
                     final ClientTask task = new TaskBatchQueue(/* batchId->*/0, volumeId, /* usersId->*/ 0, teamsId);
                     boolean ok = task.enqueue(this);
                  }
                  else {
                     Log.print("(Batching.startBatchMenuItem) !!!??? drop from volume value is 0 !!!??? batch/volume/users/teams " + batchId + "/" + volumeId + "/" + usersId + "/" + teamsId);
                  }
               }
            }
         }
         else if (usersPath != null) {        
            // The dragged data came from usersTree
            tp = usersPath;
            for (int i = 0; i < tp.length; i++) {
               // get each child and add it to the queue
                    // Note:  status stays the same when dragged from usersTree
               UsersTreeNode newChild = (UsersTreeNode) tp[i].getLastPathComponent();
               batchId = newChild.getId();

               if (batchId > 0 && (usersId > 0 || teamsId > 0)) {
                  final ClientTask task = new TaskBatchQueue(batchId, /* volumeid-> */ 0, usersId, teamsId, /* remove queues-> */ true);
                  boolean ok = task.enqueue(this);
               }
               else {
                  Log.quit("(Batching.startBatchMenuItem) !!!??? drop from user value is 0 !!!??? batch/users/teams " + batchId + "/" + usersId + "/" + teamsId);
               }
            }
         }
         else if (batchPath != null) {            
            // The dragged data came from batchTree
            tp = batchTree.getSelectionPaths();
            //Log.print("(Batching.userDrop) treePath.length == " + tp.length);
            for (int i = 0; i < tp.length; i++) {
               // get each child and enqueue a task to add it to the queue
               BatchTreeNode bChild = (BatchTreeNode) tp[i].getLastPathComponent();
               //Log.print("(Batching.userDrop) batchPath[" + i + "] is " + bChild);
               if (bChild.isTransferable()) {
                  batchId = Integer.parseInt(bChild.getKey());

                  volumeNode = (BatchTreeNode) bChild.getParent(); // parent of batch is volume
                  volumeId = Integer.parseInt(volumeNode.getKey());
                  if (batchId > 0 && volumeId > 0 && (usersId > 0 || teamsId > 0)) {
                     final ClientTask task = new TaskBatchQueue(batchId, volumeId, usersId, teamsId);
                     boolean ok = task.enqueue(this);
                  }
                  else {
                     Log.print("(Batching.startBatchMenuItem) !!!??? drop from batch value is 0 !!!??? batch/volume/users/teams " + batchId + "/" + volumeId + "/" + usersId + "/" + teamsId);
                  }
               }
            }
         }
         e.getDropTargetContext().dropComplete(true);
      } catch (Throwable th) {
         Log.quit(th);
      }
   }

   /**
     * Set menuItems and listeners for batchTree.
     */
   private void setBatchTree()
   {
      batchTree.setTreeSelectionModel(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
      batchTree.setDragEnabled(true);

      startBatchMenuItem.setEnabled(false);
      removeBatchMenuItem.setEnabled(false);

      batchTree.getTree().addTreeSelectionListener(new TreeSelectionListener()
              {
                 public void valueChanged(TreeSelectionEvent e)
                 {
                    try {
                       // enable batch comment if single selection and node is batch
                       addBatchCommentButton.setEnabled(false);
                       batchPath = batchTree.getSelectionPaths();  // a TreePath[]
                       if (batchPath != null && batchPath.length == 1 && batchPath[0].getLastPathComponent() instanceof ProjectBatching.BatchNode) {
                          addBatchCommentButton.setEnabled(true);
                          addBatchCommentButton.setEnabled(true);
                       }
                    } catch (Throwable th) {
                       Log.quit(th);
                    }
                 }
              });

      toolsMenu.addMenuListener(new MenuListener()
              {

                 public void menuSelected(javax.swing.event.MenuEvent evt)
                 {
                    try {
                       // Enable  batch movement items, as appropriate
                       startBatchMenuItem.setEnabled(false);
                       removeBatchMenuItem.setEnabled(false);
                       moveUpMenuItem.setEnabled(false);
                       moveDownMenuItem.setEnabled(false);
                       browseMenuItem.setEnabled(false);
                       deleteVolumeMenuItem.setEnabled(false);

                       batchPath = batchTree.getSelectionPaths();  // a TreePath[]
                       if (batchPath == null || batchPath.length != 1) {
                          return;
                       }
                       Object selectedNode = batchPath[0].getLastPathComponent();

                       if (selectedNode instanceof UserBatching.TeamsQueueBatchNode || selectedNode instanceof UserBatching.UsersQueueBatchNode) {
                          browseMenuItem.setEnabled(true);
                       }
                       else if (selectedNode instanceof ProjectBatching.BatchNode) {
                          // maybe selected batch can be removed
                          if (Global.theServerConnection.getPermissionAdminBatch()) {
                             ProjectBatching.BatchNode theBatchNode = (ProjectBatching.BatchNode) selectedNode;
                             if (theBatchNode.rebatchAllowed(-1)) {
                                removeBatchMenuItem.setEnabled(true);
                             }
                          }
                          browseMenuItem.setEnabled(true);
                       }
                       else if (selectedNode instanceof ProjectBatching.RangeNode) {
                          // maybe selected range can be moved or split
                          if (Global.theServerConnection.getPermissionAdminBatch()) {
                             ProjectBatching.RangeNode theRangeNode = (ProjectBatching.RangeNode) batchPath[0].getLastPathComponent();
                             ProjectBatching.AttachmentLabelNode theLabelNode = (ProjectBatching.AttachmentLabelNode) theRangeNode.getParent();
                             ProjectBatching.BatchNode theBatchNode = (ProjectBatching.BatchNode) theLabelNode.getParent();

                             ManagedNodeModel theRangeNodeModel = (ManagedNodeModel) theLabelNode.getSubmodel(0);
                             ManagedTableModel theRangeModel = (ManagedTableModel) theRangeNodeModel.getModel();

                             int rangeId = theRangeNode.getRowId();
                             int rangeCount = theRangeModel.getRowCount();
                             boolean isFirstRange = (theRangeModel.getRowId(0) == rangeId);
                             boolean isLastRange = (theRangeModel.getRowId(rangeCount - 1) == rangeId);

                             if (!isLastRange) {
                                if (theBatchNode.rebatchAllowed(-1)) {
                                   moveUpMenuItem.setEnabled(true);
                                }
                             }

                             if (!isFirstRange) {
                                if (theBatchNode.rebatchAllowed(0)) {
                                   startBatchMenuItem.setEnabled(true);
                                }
                                if (theBatchNode.rebatchAllowed(+1)) {
                                   moveDownMenuItem.setEnabled(true);
                                }
                             }
                          }
                       }
                       else if (selectedNode instanceof ProjectBatching.VolumeNode) {
                          volumeNode = (ProjectBatching.VolumeNode) batchPath[0].getLastPathComponent();
                          deleteVolumeMenuItem.setEnabled(true);
                       }

                    } catch (Throwable th) {
                       Log.quit(th);
                    }
                 }

                 public void menuDeselected(javax.swing.event.MenuEvent evt)
                 {
                 }

                 public void menuCanceled(javax.swing.event.MenuEvent evt)
                 {
                 }
              });

      batchTree.addMouseListener(new MouseAdapter()
              {

                 public void mouseReleased(MouseEvent ev)
                 {          
                    try {
                       if (ev.isPopupTrigger()) {
                          if (!Global.theServerConnection.getPermissionAdminBatch()) {
                             // no right-click if user does not have admin batch perm.
                             return;
                          }
                          // batch or volume node
                          if (batchTree.getSelectionCount() > 1) {
                             // no right-click on multiple selections                              
                             return;
                          }
                          path = batchTree.getPathForLocation(ev.getX(), ev.getY());
                          
                          if (path != null) {
                             batchNode = (BatchTreeNode) path.getLastPathComponent();
                             volumeNode = (BatchTreeNode) path.getLastPathComponent();
                             if (batchNode != null && batchNode.isBatch()) {
                                if (batchNode.isTransferable() || batchNode.isUnitizeComplete()) {
                                   removeVolumeAction.setEnabled(false);
                                   batchTree.setSelectionPath(path);
                                   if (batchNode.isUnitizeComplete()) {
                                      splitUnitizeAction.setEnabled(true);
                                   }
                                   else {
                                      splitUnitizeAction.setEnabled(false);
                                   }
                                   
                                   popupMenu.show((JComponent) ev.getSource(), ev.getX(), ev.getY());
                                }
                                //modified to suit for Modify error process
                                if (batchNode.isTallyComplete() || batchNode.isListingComplete()) {
                                    splitUnitizeAction.setEnabled(false);
                                    increaseAction.setEnabled(false);
                                    decreaseAction.setEnabled(false);
                                    createListingAction.setEnabled(false);
                                    createTallyAction.setEnabled(false);
                                    removeVolumeAction.setEnabled(false);
                                    createModifyErrorAction.setEnabled(true);
                                    popupMenu.show((JComponent) ev.getSource(), ev.getX(), ev.getY());
                                }else if(batchNode.isQAComplete()) {
                                    System.out.println("batchNode.isQAComplete()" +batchNode.isQAComplete());
                                    splitUnitizeAction.setEnabled(false);
                                    increaseAction.setEnabled(false);
                                    decreaseAction.setEnabled(false);
                                    createListingAction.setEnabled(false);
                                    createTallyAction.setEnabled(false);
                                    removeVolumeAction.setEnabled(false);
                                    createModifyErrorAction.setEnabled(false);
                                    popupMenu.show((JComponent) ev.getSource(), ev.getX(), ev.getY());
                                 }else if(batchNode.isQAError()) {
                                     System.out.println("batchNode.isQAError()" +batchNode.isQAError());
                                    splitUnitizeAction.setEnabled(false);
                                    increaseAction.setEnabled(false);
                                    decreaseAction.setEnabled(false);
                                    createListingAction.setEnabled(false);
                                    createTallyAction.setEnabled(false);
                                    removeVolumeAction.setEnabled(false);
                                    createModifyErrorAction.setEnabled(true);
                                    popupMenu.show((JComponent) ev.getSource(), ev.getX(), ev.getY());
                                 }
                             }
                             else if (batchNode != null && batchNode.isVolume()) {
                                 
                                splitUnitizeAction.setEnabled(false);
                                increaseAction.setEnabled(false);
                                decreaseAction.setEnabled(false);
                                createListingAction.setEnabled(true);
                                createTallyAction.setEnabled(true);
                                removeVolumeAction.setEnabled(true);
                                createModifyErrorAction.setEnabled(false);
                                popupMenu.show((JComponent) ev.getSource(), ev.getX(), ev.getY());
                             }
                             else if (volumeNode != null && batchNode.isTally()) {
                                
                                splitUnitizeAction.setEnabled(false);
                                increaseAction.setEnabled(false);
                                decreaseAction.setEnabled(false);
                                createListingAction.setEnabled(true);
                                removeVolumeAction.setEnabled(true);
                                popupMenu.show((JComponent) ev.getSource(), ev.getX(), ev.getY());
                             }
                          }
                       }
                    } catch (Throwable th) {
                       Log.quit(th);
                    }

                    if (batchTree.getSelectionCount() > 1 && (!ev.isControlDown()) && (!ev.isShiftDown())) {
                       if (selectedPath != null) {
                          selectedPath = null;
                       }
                       batchTree.setSelectionPath(batchTree.getPathForLocation(ev.getX(), ev.getY()));
                    }
                 // ends here
                 }

                 public void mousePressed(MouseEvent ev)
                 {
                    
                    if ((batchTree.getSelectionCount() > 1) && (ev.isControlDown() || ev.isShiftDown())) {
                       selectedPath = batchTree.getSelectionPaths();
                       
                    }
                    else if ((batchTree.getSelectionCount() == 1) && (!ev.isControlDown()) && (!ev.isShiftDown())) {
                       if (selectedPath != null) {
                          
                          boolean isSelected = false;
                          TreePath currentPath = batchTree.getPathForLocation(ev.getX(), ev.getY());
                          if (currentPath != null) {
                             for (int idx = 0; idx < selectedPath.length; idx++) {
                                if (currentPath.equals(selectedPath[idx])) {
                                   isSelected = true;
                                }
                             }
                          }

                          if (isSelected) {
                             batchTree.setSelectionPaths(selectedPath);
                          }
                          else {
                             selectedPath = null;
                             batchTree.setSelectionPath(currentPath);
                          }
                       }
                    }
                    else if (ev.isControlDown()) {
                       selectedPath = batchTree.getSelectionPaths();
                    }
                 // ends here
                 }

              });
      usersTree.setDragEnabled(true);

      usersTree.getTree().addTreeSelectionListener(new TreeSelectionListener()
              {

                 public void valueChanged(TreeSelectionEvent e)
                 {
                    try {
                       Log.print("(batchingDialog.valueChanged) dragOver = " + e.getNewLeadSelectionPath());
                       dragOverTreePath = e.getNewLeadSelectionPath();
                       usersPath = usersTree.getSelectionPaths();
                       //usersPath = usersTree.getSelectionPath();
                       if (usersPath != null && usersPath[0].getLastPathComponent() instanceof UsersTreeNode) {
                          Log.print("(Batching) usersTree listener " + usersPath[0].toString());
                          usersNode = (UsersTreeNode) (usersPath[0].getLastPathComponent());

                          if (usersNode == null || usersNode.isRoot() || usersNode.toString().equals("Users") || usersNode.toString().equals("Teams")) {
                             usersNode = null;
                             return;
                          }
                       }
                       else {
                          usersPath = null;
                          usersNode = null;
                       }
                    } catch (Throwable th) {
                       Log.quit(th);
                    }
                 }

              });

      usersTree.addMouseListener(new MouseAdapter()
              {
                 public void mouseReleased(MouseEvent ev)
                 {
                    try {
                       if (ev.isPopupTrigger() && Global.theServerConnection.getPermissionAdminBatch()) {
                          //Log.print("(Batching.mouseReleased) type " + usersNode.getType());
                          Log.print("(Batching.mouseReleased)");
                          if (usersNode != null && (usersNode.isQueued() || usersNode.isVolume() || usersNode.isAssigned())) {
                             Log.print("... type " + usersNode.getType());
                             // batch node
                             path = usersTree.getPathForLocation(ev.getX(), ev.getY());
                             usersNode = (UsersTreeNode) path.getLastPathComponent();
                             usersTree.setSelectionPath(path);
                             usersPopupMenu.show((JComponent) ev.getSource(), ev.getX(), ev.getY());
                          }
                       }
                    } catch (Throwable th) {
                       Log.quit(th);
                    }
                 }
                 //public void mousePressed(MouseEvent ev) {
               //    try {
               //        if (ev.isPopupTrigger()) {
               //            if (usersNode != null
               //                && (usersNode.isQueued()
               //                || usersNode.isAssigned())) {
               //                // batch node
               //                path = usersTree.getPathForLocation(ev.getX(), ev.getY());
               //                usersNode = (UsersTreeNode)path.getLastPathComponent();
               //                usersTree.setSelectionPath(path);
               //                usersPopupMenu.show((JComponent)ev.getSource(), ev.getX(), ev.getY());
               //            }
               //        }
               //    } catch (Throwable th) {
               //        Log.quit(th);
               //    }
               //}

              });
   //Log.print("(BatchingPage) components initialized");
   }

   /**
     * Call ClientTask to enqueue a task to increase the batch priority by one.
     */
   protected Action increaseAction = new AbstractAction("Increase Priority")
           {

              public void actionPerformed(java.awt.event.ActionEvent A)
              {
                 Log.print("(Batching).increaseAction " + batchNode + "/" + batchNode.getKey());
                 if (batchNode.isTransferable()) {
                    // queued batch
                    final ClientTask task = new TaskExecuteUpdate("batch priority increase", batchNode.getKey()); // batch_id
                    boolean ok = task.enqueue(BatchingPage.this);
                 }
              }

           };

   /**
     * Call ClientTask to enqueue a task to increase the batch priority by one.
     */
   protected Action decreaseAction = new AbstractAction("Decrease Priority")
           {

              public void actionPerformed(java.awt.event.ActionEvent A)
              {
                 if (batchNode.isTransferable()) {
                    // must be batch
                    final ClientTask task = new TaskExecuteUpdate("batch priority decrease", batchNode.getKey()); // batch_id
                    boolean ok = task.enqueue(BatchingPage.this);
                 }
              }

           };

   /**
     * Can be clicked on a batch having a status of UComplete to break the batch into
     * n batches of status Coding.
     */
   protected Action splitUnitizeAction = new AbstractAction("Create Coding Batches")
           {
              /**
             * Invoked when an action occurs.
             */

              public void actionPerformed(ActionEvent e)
              {
                 try {
                    Log.print("(BatchingPage.splitUnitizeAction) " + path + "/" + batchNode + " next--> " + batchNode.getNextNode());
                    int batchIndex = -1;
                    int batchId = 0;
                    if (batchNode.isBatch()) {
                       DocsPerBatchDialog docsDialog = new DocsPerBatchDialog(batchTree);
                       docsDialog.setModal(true);
                       docsDialog.show();
                       int interval = docsDialog.getValue();
                       if (interval < 1) {
                          return; // user cancelled
                       }
                       final ClientTask task = new TaskCreateCodingBatches(Integer.parseInt((String) batchNode.getKey()), interval);
                       boolean ok = task.enqueue(BatchingPage.this);
                    }

                 } catch (Throwable th) {
                    Log.quit(th);
                 }
              }

           };

   /**
     * Remove the selected batch from its parent queue or assignment on usersTree.
     */
   protected Action removeAction = new AbstractAction("Remove Batch from Queue or Assignment")
           {

              public void actionPerformed(java.awt.event.ActionEvent A)
              {
                 remove();
              }

           };

   /**
     * From batchTree remove all occurrences of the selected volume from teamsvolume and
     * remove all batches in the volume from usersqueue and teamsqueue and assignment.
     */
   protected Action removeVolumeAction = new AbstractAction("Remove Volume from Queues/Assignments")
           {

        @Override
              public void actionPerformed(java.awt.event.ActionEvent A)
              {
                 removeVolume();
              }

           };
   protected Action createListingAction = new AbstractAction("create Listing Batch")
           {
              /**
             * Invoked when an action occurs.
             */

        @Override
              public void actionPerformed(ActionEvent e)
              {
                 System.out.println("createListingAction------------------->");
                 try {
                    int volumeId = 0;
                    TreePath currentSelection = batchTree.getSelectionPath();

                    if (currentSelection != null) {
                       // get the volume_id
                       BatchTreeNode currentNode = (BatchTreeNode) currentSelection.getLastPathComponent();

                       if (currentNode.isVolume()) {
                          AssignListingDialog listingDialog = new AssignListingDialog(batchTree);
                          listingDialog.setModal(true);
                          listingDialog.show();
                          int userId = listingDialog.getValue();
                           System.out.println("userId------------------->" + userId);
                          if (userId < 1) {
                             return; // user cancelled
                          }
                          //Log.print("(Batching.removeVolume) currentNode " + currentNode);
                          volumeId = Integer.parseInt(currentNode.getKey());
                          System.out.println("volumeId------------------->" + volumeId);
                          System.out.println("userId------------------->" + userId);
                          final ClientTask task = new TaskCreateListingBatches(volumeId, userId);
                          task.setCallback(new Runnable() {
                            @Override
                            public void run() {
                                Element reply = (Element) task.getResult();
                                String action = reply.getNodeName();
                                if (T_FAIL.equals(action)) {                        
                                JOptionPane.showMessageDialog(BatchingPage.this,
                                        "Can't open selected batch",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            }});
                          boolean ok = task.enqueue(BatchingPage.this);
                          
//                          if (volumeId > 0) {
//                             //final ClientTask task = new TaskBatchQueue(batchId, volumeId, usersId, teamsId);
//                               // boolean ok = task.enqueue(this);
//                             final ClientTask listingTask = new TaskListingQueue(volumeId, userId);
//                             boolean value = listingTask.enqueue(BatchingPage.this);
//                          }
                       }
                    }

                 } catch (Throwable th) {
                    Log.quit(th);
                 }
              }

           };
    
           
           protected Action createTallyAction = new AbstractAction("create Tally Batch")
           {
              /**
             * Invoked when an action occurs.
             */

        @Override
              public void actionPerformed(ActionEvent e)
              {
                 System.out.println("createTallyAction------------------->");
                 try {
                    int volumeId = 0;
                    TreePath currentSelection = batchTree.getSelectionPath();

                    if (currentSelection != null) {
                       // get the volume_id
                       BatchTreeNode currentNode = (BatchTreeNode) currentSelection.getLastPathComponent();

                       if (currentNode.isVolume()) {
                          AssignTallyDialog tallyDialog = new AssignTallyDialog(batchTree);
                          tallyDialog.setModal(true);
                          tallyDialog.show();
                          int userId = tallyDialog.getValue();                          
                          if (userId < 1) {
                             return; // user cancelled
                          }
                          //Log.print("(Batching.removeVolume) currentNode " + currentNode);
                          volumeId = Integer.parseInt(currentNode.getKey());
                          System.out.println("volumeId------------------->" + volumeId);
                          System.out.println("userId------------------->" + userId);
                          final ClientTask task = new TaskCreateTallyBatches(volumeId, userId);
                           task.setCallback(new Runnable() {
                            @Override
                            public void run() {
                                Element reply = (Element) task.getResult();
                                String action = reply.getNodeName();
                                if (T_FAIL.equals(action)) {                        
                                JOptionPane.showMessageDialog(BatchingPage.this,
                                        "Can't open selected batch",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            }});
                          boolean ok = task.enqueue(BatchingPage.this);

//                          if (volumeId > 0) {
//                             //final ClientTask task = new TaskBatchQueue(batchId, volumeId, usersId, teamsId);
//                               // boolean ok = task.enqueue(this);
//                             final ClientTask tallyTask = new TaskTallyQueue(volumeId, userId);
//                             boolean value = tallyTask.enqueue(BatchingPage.this);
//                          }
                       }
                    }

                 } catch (Throwable th) {
                    Log.quit(th);
                 }
              }

           };
           
           //It will create modify error batch
           protected Action createModifyErrorAction = new AbstractAction("create Modify Error Batch")
           {
              /**
             * Invoked when an action occurs.
             */

        @Override
              public void actionPerformed(ActionEvent e)
              {
                 System.out.println("createModifyErrorAction------------------->");
                 try {                
                    TreePath currentSelection = batchTree.getSelectionPath();

                    if (currentSelection != null) {
                       // get the volume_id
                       BatchTreeNode currentNode = (BatchTreeNode) currentSelection.getLastPathComponent();

                       if (currentNode.isBatch()) {
                           //System.out.println("1111111111111");
                            int batch_id = Integer.parseInt(currentNode.getKey());
                            final ClientTask task = new TaskCreateModifyErrorBatches(batch_id);
                            task.setCallback(new Runnable() {
                            @Override
                            public void run() {
                                Element reply = (Element) task.getResult();
                                String action = reply.getNodeName();
                                if (T_FAIL.equals(action)) {                        
                                JOptionPane.showMessageDialog(BatchingPage.this,
                                        "Can't open selected batch",
                                        "Error",
                                        JOptionPane.ERROR_MESSAGE);
                            }
                            }});
                          boolean ok = task.enqueue(BatchingPage.this);

                            
                          System.out.println("batch_id------------------->" + batch_id);
                       }
                    }

                 } catch (Throwable th) {
                    Log.quit(th);
                 }
              }

           };
           
           
           
   /**
     * @see removeAction
     */
   private void remove()
   {
      int batchId = 0;
      UsersTreeNode node;
      final ClientTask task;
      TreePath currentSelection = usersTree.getSelectionPath();

      // get the batch_id
      UsersTreeNode currentNode = (UsersTreeNode) currentSelection.getLastPathComponent();

      // get the volume or batch's parent
        // If the batch is assigned, the parent will be the user;
        // otherwise, the parent is the queue label.
      node = (UsersTreeNode) currentNode.getParent();

      //Log.print("(Batching.remove) current node queued/assigned"
        //          + currentNode.isQueued() + "/" + currentNode.isAssigned());
      if (currentNode.isQueued()) {
         if (currentNode.isBatch()) {
            batchId = Integer.parseInt(currentNode.getValue(5)); // batch
         }
         if (node.isLabel()) { // queue label
            node = (UsersTreeNode) node.getParent();  // user or team
                //Log.print("(Batching.remove) batch/user " + batchId + "/" + node.getId()
                //          + "/" + node);
            if (node.isUser()) {
               task = new TaskExecuteUpdate("usersqueue delete", Integer.toString(batchId), Integer.toString(node.getId()));
               boolean ok = task.enqueue(this);
            }
            else {
               task = new TaskExecuteUpdate("teamsqueue delete", Integer.toString(batchId), Integer.toString(node.getId()));
               boolean ok = task.enqueue(this);
            }
         }
         else {
            Log.print("(Batching.remove) !!! unexpected node type " + node);
            return;
         }
      }
      else if (currentNode.isAssigned()) {
         if (currentNode.isBatch()) {
            batchId = Integer.parseInt(currentNode.getValue(4)); // batch
         }
         if (node.isUser()) {
            //Log.print("(Batching.remove) batch/user " + batchId + "/" + node.getId());
            task = new TaskExecuteUpdate("assignment delete", Integer.toString(batchId), Integer.toString(node.getId()));
            boolean ok = task.enqueue(this);
         }
         else {
            Log.print("(Batching.remove) unexpected assign node type " + node);
            return;
         }
      }
      else if (currentNode.isVolume()) {
         volumeId = Integer.parseInt(currentNode.getValue(1));
         if (node.isLabel()) { // volume label
            node = (UsersTreeNode) node.getParent();  // user or team
            if (node.isTeam()) {
               Log.print("(Batching.remove) volume/team " + volumeId + "/" + node.getId());
               task = new TaskExecuteUpdate("teamsvolume delete", Integer.toString(volumeId), Integer.toString(node.getId()));
               boolean ok = task.enqueue(this);
            }
            else {
               Log.print("(Batching.remove) !!! unexpected volume team node type " + node);
            }
         }
         else {
            Log.print("(Batching.remove) !!! unexpected volume label node type " + node);
         }
      }
   }


   /**
     * @see removeVolumeAction
     */
   private void removeVolume()
   {
      int volumeId = 0;
      BatchTreeNode node;
      TreePath currentSelection = batchTree.getSelectionPath();

      if (currentSelection != null) {
         // get the volume_id
         BatchTreeNode currentNode = (BatchTreeNode) currentSelection.getLastPathComponent();

         if (currentNode.isVolume()) {
            //Log.print("(Batching.removeVolume) currentNode " + currentNode);
            volumeId = Integer.parseInt(currentNode.getKey());
            final ClientTask task = new TaskTeamsvolume(volumeId);
            boolean ok = task.enqueue(this);
         }
      }
   }

   /**
     * Check that it's OK to exit the current page.  Subclasses must override this to provide a
     * page-dependent check.
     * @return true if it's OK to exit.  If user cancels save/no-save/cancel dialog,
     *         false is returned.
     */
    @Override
   protected boolean exitPageCheck()
   {
      return true;
   }

   /** Get the menu bar for the current page.  Subclasses must override this to provide a
     * page-dependent menu bar.
     */
    @Override
   protected JMenuBar getPageJMenuBar()
   {
      return menuBar;
   }

   /**
     * Perform page initialization.  Subclasses must override this to provide any
     * required page-dependent initialization.
     */
    @Override
   protected void tabSelected()
   {
      Log.print("BatchingPage tabSelected");
      // 
        // refresh data on every tabSelected  -- not done per Bugzilla #109
        //ManagedNode root = new ProjectBatching.RootNode(this);
        //root.setAsModelRoot(batchTreeModel);
        //ManagedNode usersTeamsRoot = new UserBatching.RootNode(this);
        //usersTeamsRoot.setAsModelRoot(usersTreeModel);
        //
        // load data first time through
      if (viewMode == null) {
         setView("all");
      }
   }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBatchCommentButton;
    private javax.swing.JRadioButtonMenuItem allMenuItem;
    private javax.swing.JPanel batchPanel;
    private javax.swing.JMenuItem browseMenuItem;
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JRadioButtonMenuItem codingMenuItem;
    private javax.swing.JRadioButtonMenuItem codingQCMenuItem;
    private javax.swing.JRadioButtonMenuItem completeMenuItem;
    private javax.swing.JMenuItem deleteVolumeMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JRadioButtonMenuItem idleMenuItem;
    private javax.swing.JRadioButtonMenuItem assignedMenuItem;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel311;
    private javax.swing.JPanel jPanel3111;
	private javax.swing.JPanel jPanellisting;
	private javax.swing.JPanel jPanelmasking;
	private javax.swing.JPanel jPanelmodifyError;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSplitPane jSplitPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem moveDownMenuItem;
    private javax.swing.JMenuItem moveUpMenuItem;
    private javax.swing.JRadioButtonMenuItem qaMenuItem;
    private javax.swing.JMenuItem removeBatchMenuItem;
    private javax.swing.JMenuItem startBatchMenuItem;
    private javax.swing.JMenuItem refreshMenuItem;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JPanel treePanel;
    private javax.swing.JPanel treePanel2;
    private javax.swing.JLabel unitizeLabel111;
    private javax.swing.JLabel unitizeLabel12;
    private javax.swing.JLabel unitizeLabel2;
    private javax.swing.JLabel unitizeLabel21;
    private javax.swing.JLabel unitizeLabel211;
    private javax.swing.JLabel unitizeLabel2111;
    private javax.swing.JLabel unitizeLabel2112;
    private javax.swing.JLabel unitizeLabel212;
    private javax.swing.JLabel unitizeLabel21211;
    private javax.swing.JRadioButtonMenuItem unitizeMenuItem;
    private javax.swing.JLabel unitizeQCLabel2;
    private javax.swing.JLabel unitizeQCLabel21;
    private javax.swing.JLabel unitizeQCLabel211;
    private javax.swing.JLabel unitizeQCLabel2111;
    private javax.swing.JLabel unitizeQCLabel21111;
    private javax.swing.JLabel unitizeQCLabel211111;
    private javax.swing.JLabel unitizeQCLabel212;
    private javax.swing.JLabel unitizeQCLabel2121;
    private javax.swing.JLabel unitizeQCLabel21211;
    private javax.swing.JLabel unitizeQCLabel22;
    private javax.swing.JLabel unitizeQCLabel221;
    private javax.swing.JLabel unitizeQCLabel2211;
	private javax.swing.JLabel listingLabel;
	private javax.swing.JLabel listingCompleteLabel;
	private javax.swing.JLabel tallyLabel;
	private javax.swing.JLabel tallyCompleteLabel;
	private javax.swing.JLabel maskingLabel;
    private javax.swing.JLabel maskingCompleteLabel;
	private javax.swing.JLabel modifyErrorLabel;
	private javax.swing.JLabel maskingQueueLabel;
    private javax.swing.JLabel maskingAssignLabel;
    private javax.swing.JLabel emptyLabel1;
	private javax.swing.JLabel emptyLabel2;
	private javax.swing.JLabel emptyLabel3;
	private javax.swing.JLabel emptyLabel4;
	private javax.swing.JLabel emptyLabel5;
	private javax.swing.JLabel modifyErrorAssignLabel;
	private javax.swing.JLabel modifyErrorQueueLabel;
    private javax.swing.JRadioButtonMenuItem unitizeQCMenuItem;
    private javax.swing.JPanel usersPanel;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JPanel tallyPanel;
    // End of variables declaration//GEN-END:variables

   private beans.DynamicTree usersTree;
   private beans.DynamicTree batchTree;
   private javax.swing.JRadioButtonMenuItem tallyMenuItem;
   private javax.swing.JRadioButtonMenuItem listingMenuItem;
   private javax.swing.JRadioButtonMenuItem maskingMenuItem;
   private javax.swing.JRadioButtonMenuItem modifyErrorsMenuItem;
}
