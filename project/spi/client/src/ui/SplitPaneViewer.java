/* $Header: /home/common/cvsarea/ibase/dia/src/ui/SplitPaneViewer.java,v 1.301.2.22 2006/08/23 19:04:52 nancy Exp $ */
package ui;

import beans.AddEditIssue;
import beans.BatchCommentsDialog;
import beans.CancelDialog;
import beans.DateVerifier;
import beans.IbaseConstants;
import beans.IbaseTextField;
import beans.LComboBox;
import beans.LDateField;
import beans.LField;
import beans.LFormattedTextField;
import beans.LTextButton;
import beans.LTextField;
import beans.LUndoController;
import beans.MaskFieldVerifier;
import beans.MinMaxVerifier;
import beans.SelectBatesDialog;
import beans.SelectRelativeDialog;
import beans.SkipCheckboxFocusTraversalPolicy;
import beans.SpellCheckButton;
import beans.TableSorter;
import beans.ToolTipText;
import client.ClientTask;
import client.Global;
import client.ImageConnection;
import client.ServerConnection;
import client.TaskBinderUpdate;
import client.TaskCloseEvent;
import client.TaskCloseVolume;
import client.TaskExecuteUpdate;
import client.TaskExecuteQuery;
import client.TaskGoodbye;
import client.TaskQcDone;
import client.TaskRequestCoding;
import client.TaskRequestCodingValues;
import client.TaskRequestImage;
import client.TaskRequestProjectFields;
import client.TaskRequestRequeue;
import client.TaskRequestFieldvalue;
import client.TaskRequestFieldvalueDetails;
import client.TaskSaveListingReportListForMarking;
import client.TaskSaveListingReportListForOccurrence;
import client.TaskSendFieldvalueDetails;
import client.TaskSendBatchStatus;
import client.TaskSendBoundary;
import client.TaskSendCodingData;
import client.TaskSendSplit;
import client.TaskStartOtherActivity;
import client.TaskValidateBatch;
import com.lexpar.util.Log;
import common.CodingData;
import common.ImageData;
import common.MarkingData;
import common.OccurrenceData;
import common.edit.ProjectEditor;
import common.edit.ProjectMapper;
import common.msg.MessageConstants;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import model.SQLManagedTableModel;
import model.MailreceivedManagedModel;
import model.ManagedTableFilter;
import model.ManagedTableModel;
import model.ManagedTableSorter;
import model.FieldMapper;
import model.TableRow;
import report.AbstractReport;
import report.MenuReport;
import tools.LocalProperties;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.*;
import java.awt.event.KeyEvent;
import java.awt.Image;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import javax.swing.KeyStroke;
import javax.swing.Timer;
import javax.swing.Action;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import model.ListingMarkingTableModel;
import model.ListingOccurrenceTableModel;
import model.ResultSetTableModel;
import model.SQLManagedComboModel;
import org.w3c.dom.Element;

/**
 * The viewer used for viewing and updating batches and viewing binder data.
 * @author Lexpar
 */
final public class SplitPaneViewer extends JFrame implements MessageConstants {

    private SQLManagedComboModel projectModel = null;
    private final String GET_ALL_FIELDS = "AdvanceValidation.get fields";
    private final String GET_FIELD_TYPE = "Listing.getFieldType";
    private String field_name = "";
    private ResultSetTableModel valueModel;
    private String[] statusString1 = {"Miscoded", "Uncoded", "Added"};
    boolean flagForCloseWindow = false;
    private String selectedActivity = null;
    private int projId = 0;
    private String documentNumber;
    private String batesNumber;
    private String marks;
    private String samplingFields = null;
    // NOTE: INSTANCE VARIABLES MUST BE INITIALIZED in getInstance, because
    // instances are cached
    private String treatmentLevel = null;
    private ManagedTableSorter tablevalueModel;
    private ManagedTableSorter tablespecModel;

    // dummy text so that buttons aren't collapsed before getting real Bates numbers
    final private static String EMPTY_BUTTON_TEXT = "                    ";

    // a list of free SplitPaneViewer's
    private static List freeList = new LinkedList();

    // The frame to be made visible when we close this frame
    private JFrame parent = null;
    private beans.ViewTIFFPanel ourViewer;
    private LUndoController undoController;
    protected final static int PLUS_ONE = 1;
    protected final static int MINUS_ONE = -1;
    protected final static int ACTUAL = 0;
    /** the hashed project_fields project description */
    protected ProjectMapper projectMap;
    protected ProjectMapper projectMap1;
    private ProjectEditor projectEditor;
    private ProjectEditor projectEditor1;
    private int fieldCount = 0; // number of fields on the screen

    /** values of all ibaseTextField tables */
    //TableMapper tableMap;
    /** names from ProjectSelectionDialog, used on frame */
    //int batchNumber = 0;
    private String projectName = "";
    //String volumeName = "";
    // This is either a batch status "Unitize", ..., 
    // or "Admin" for administrator browse
    // or "Folder" for project folder browse
    private String whichStatus = "Coding";
    // Can Split Documents be enabled for this project?
    private boolean splitDocuments = false;
    // flag for whether this is a unitization-mode viewer
    private boolean unitize = false;
    //private boolean closingBatch = false;
    private int activeGroup = 0;
    private int activelevel = 1;// variable shows the level(L1 or L2)
    // The currently-open batch, volume and project
    private int batchId = 0;
    private int volumeId = 0;
    private int projectId = 0;
    private String volume;
    private String imagePath;
    private String fieldType = null;
    // the currently-open binder
    // will be null if this viewer is the binder
    private SplitPaneViewer binderViewer = null;

    // the current resolution for this viewer instance
    private int resolution = RES_HIGH;
    /** the page currently being coded */
    private CodingData theCodingData = null;
    /** the page currently in the viewer */
    private ImageData theImageData = null;
    /** fields for viewer label (i of n) */
    private int showRow = 0;
    private int showRange = 0;
    /** fields to hold data needed in sendCodingData */
    private boolean go = false;
    private boolean goToBoundary = false;
    private boolean goPrev = false;
    /** indicate to CheckedDocument that a reload of data is
     * not necessary because the next child will be loaded */
    private boolean saveAndGo = false;
    private boolean fromServer = false;
    private boolean fromBoundaryActionListener = false;
    private boolean boundaryFromServer = false;
    private int originalPageId = 0;
    private boolean editValue = false;
    /** if batchingFlag = true during save, create a batch row */
    private boolean batchingFlag = false;
    /** fields to hold data needed in getPageValuesEntry */
    private JComponent copyField = null;
    private int savedPageId = 0;
    private String statusString = "";
    // Holds type of viewer for title bar
    private String titlePrefix = null;

    // for report titles
    private String user_name = "";
    private String team_name = "";
    protected FieldMapper theFieldMapper;
    protected boolean saveSuccessful = false;
    private FocusComponent focusComponent = new FocusComponent();
    private BoundaryDocument boundaryDocument = new BoundaryDocument();
    private String fieldValueFromMarking = "";
    private String fieldValueFromOccurrence = "";
    private List fields = new ArrayList();
    private String selectedField;
    private String fieldName;
    private String showQueryRaised;
    Timer timer = null;
    private boolean flag = false;
    private boolean flag1 = false;
    private String bateno = "";
    private int listing_occurrence_id = 0;
    private boolean setvisible;
    //static int viewerCount = 0;
    //int viewerNumber;
    private int tallyAssignementId;
    /** Save the action of the up and down arrows to use when anything
     * but ibaseTextField has focus.  When ibaseTextField, up and down actions
     * will be nullified.
     */
    private Object upKey = null;
    private Object downKey = null;
    private Action upAction = null;
    private Action downAction = null;
    private Object pageUpKey = null;
    private Object pageDownKey = null;
    private Action pageUpAction = null;
    private Action pageDownAction = null;
    Map validationData = null;
    beans.IbaseTextField area = null;
    beans.LGridBag groupPane = new beans.LGridBag();
    private ListingMarkingTableModel listingMarkingTableModel;
    private ListingOccurrenceTableModel listingOccurrenceTableModel;
    private String batchSubProcess = null;
    /**
     * Obtain an instance of this class.  Take instance from
     * a list of free instances, if possible.
     * Synchronized on the class, so that setup can proceed during
     * login.
     */
    public synchronized static SplitPaneViewer getInstance() {
        
        if (freeList.size() == 0) {
            freeList.add(new SplitPaneViewer());
        }
        SplitPaneViewer result = (SplitPaneViewer) freeList.remove(0);
        //viewerCount++;
        //Log.print("@@@SPV.getInstance: "+viewerCount+" (using "+result.viewerNumber+")");
        //result.viewerNumber = viewerCount;

        // reinitialize all instance variables
        result.parent = null;
        result.projectMap = null;
        result.projectEditor = null;
        result.projectName = "";
        result.whichStatus = "Coding";
        result.unitize = false;
        //result.closingBatch = false;
        result.batchId = 0;
        result.volumeId = 0;
        result.projectId = 0;
        result.binderViewer = null;
        result.resolution = RES_HIGH;
        result.theCodingData = null;
        result.theImageData = null;
        result.showRow = 0;
        result.showRange = 0;
        result.go = false;
        result.goToBoundary = false;
        result.goPrev = false;
        result.saveAndGo = false;
        result.fromServer = false;
        result.fromBoundaryActionListener = false;
        result.boundaryFromServer = false;
        result.originalPageId = 0;
        result.batchingFlag = false;
        result.copyField = null;
        result.statusString = "";
        result.titlePrefix = null;
        result.theFieldMapper = null;
        result.saveSuccessful = false;
        result.timer = null;

        // Force cached viewer to come up in hand-cursor mode
        result.ourViewer.hideMagRect();
        result.batchId = 0;
        result.volumeId = 0;
        //result.projectId = 0;
        return result;
    }

    /**
     * Return this instance to the free list.
     * Note.  Also called from login (used to create a cached viewer)
     */
    public void free() {
        synchronized (SplitPaneViewer.class) {
            //Log.print("@@@free "+viewerNumber);
            tablevalueModel = null;
            tablespecModel = null;
            ourViewer.hideMagRect();
            // clear the image, so we won't see it when loading a new viewer
            ourViewer.ViewFromSource(null, null,
                    ImageConnection.ERROR_IMAGE,
                    ImageConnection.ERROR_IMAGE.length);
            dynamicPane.removeAll();

            // Clear instance references to permit garbage collection
            tablevalueModel = null;
            tablespecModel = null;
            parent = null;
            projectMap = null;
            projectEditor = null;
            binderViewer = null;
            theCodingData = null;
            theImageData = null;
            timer = null;

            freeList.add(this);
        }
    }

    /**
     * Return the coding data.  Used by ClientTask to determine volume
     * and batch for task sent by admin.
     * TBD: Warning: there are getCodingData and getCoderData
     */
    public CodingData getCodingData() {
        return theCodingData;
    }

    /** Creates new form SplitPaneViewer */
    private SplitPaneViewer() {

        ourViewer = new beans.ViewTIFFPanel();

        initComponents();
        projId = getProjectId();
        boundaryText.setDocument(boundaryDocument);
        KeyStroke deleteStroke = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        KeyStroke spaceStroke = KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0);
        KeyStroke backspaceStroke = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
        boundaryText.registerKeyboardAction(boundaryActionListener, deleteStroke, JComponent.WHEN_FOCUSED);
        boundaryText.registerKeyboardAction(boundaryActionListener, spaceStroke, JComponent.WHEN_FOCUSED);
        boundaryText.registerKeyboardAction(boundaryActionListener, backspaceStroke, JComponent.WHEN_FOCUSED);
        boundaryText.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void keyPressed(KeyEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (!(boundaryText.getText().equalsIgnoreCase("C") || boundaryText.getText().equalsIgnoreCase("D"))) {
                    JOptionPane.showMessageDialog(null, "Boundary value should be either C or D", "Wrong Boundary", JOptionPane.ERROR_MESSAGE);
                    boundaryText.setFocusable(true);
                    boundaryText.setText("C");
                }
            }
        });
        editMenu.add(getUndoController().getUndoAction());
        editMenu.add(getUndoController().getRedoAction());

        Constrain(theViewer, ourViewer, 0, -2, 3, 1, java.awt.GridBagConstraints.BOTH,
                java.awt.GridBagConstraints.WEST, 100, 100, 0, 0, 0, 0);

        // formerly: loadScreenFields();
        // creates save&go button and sets icon
        createScreenButtonAndIcon();

        // removed - done again in initializeForProject and needs unitize flag - wbe
        //initUnitizingComponents();

        //midPane.add(dynamicPane);
        //midPane.add(dynamicPane, java.awt.BorderLayout.NORTH);

        statusLabel.setForeground(Color.red.darker());

        setActionMap(coderPanel);
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setFields(List fields) {
        this.fields = fields;
    }

    public List getFields() {
        return fields;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setMarks(String marks) {
        this.marks = marks;
    }

    public String getMarks() {
        return marks;
    }

    public void setQueryRaised(String showQueryRaised) {
        this.showQueryRaised = showQueryRaised;
    }

    public String getQueryRaised() {
        return showQueryRaised;
    }

    public void setProjectName(String project_name) {
        this.projectName = project_name;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getVolume() {
        return volume;
    }

    public synchronized void setVolume(String volume) {
        this.volume = volume;
    }

    public String getSamplingFields() {
        return samplingFields;
    }

    public void setSamplingFields(String samplingFields) {
        this.samplingFields = samplingFields;
    }

    public int getTallyAssignementId() {
        return tallyAssignementId;
    }

   public void setTallyAssignementId(int _tallyAssignementId, int tally_dictionary_group_id) {      
      if (_tallyAssignementId <= 0) {
         final ClientTask task = new TaskExecuteQuery("TallyQC.getTallyAssignmentId", Integer.toString(tally_dictionary_group_id), "Assigned");
         task.setCallback(new Runnable() {
                    public void run() {
                       try {
                          ResultSet result = (ResultSet) task.getResult();
                          if (result != null && result.next()) {
                             tallyAssignementId = result.getInt(1);
                          }
                       } catch (Exception e) {
                          e.printStackTrace();
                       }
                    }

                 });
         task.enqueue(this);
      }else{
         this.tallyAssignementId = _tallyAssignementId;
      }
   }

    private void enableComponents() {
        if (fieldsTable.getRowCount() > 0) {
            // fontFamilyCombo.setEnabled(true);
            // fontSizeCombo.setEnabled(true);
        } else if (markingTable.getRowCount() > 0) {
            // fontFamilyCombo.setFocusable(false);
            //  fontFamilyCombo.setEnabled(false);
            //  fontSizeCombo.setFocusable(false);
            //  fontSizeCombo.setEnabled(false);            
        }

    }

    private void setActionMap(JPanel thePane) {
        upKey = midScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
        downKey = midScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        midScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0),
                "doUp");
        midScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0),
                "doDn");

        pageUpKey = midScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(KeyStroke.getKeyStroke("PAGE_UP"));
        pageDownKey = midScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(KeyStroke.getKeyStroke("PAGE_DOWN"));
        midScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("PAGE_UP"),
                "doPageUp");
        midScrollPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("PAGE_DOWN"),
                "doPageDn");

        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.ALT_MASK),
                "doSaveAndGo");
        thePane.getActionMap().put("doSaveAndGo", saveAction);

        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.ALT_MASK),
                "doCopyAll");
        thePane.getActionMap().put("doCopyAll", copyAllAction);

        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.ALT_MASK),
                "doCopyField");
        thePane.getActionMap().put("doCopyField", copyFieldAction);

        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, InputEvent.ALT_MASK),
                "doPrevDoc");
        thePane.getActionMap().put("doPrevDoc", prevDocAction);

        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, InputEvent.ALT_MASK),
                "doNextDoc");
        thePane.getActionMap().put("doNextDoc", nextDocAction);

        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.ALT_MASK),
                "doF1");
        thePane.getActionMap().put("doF1", vPagePrevAction);
        thePane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_F2, InputEvent.ALT_MASK),
                "doF2");
        thePane.getActionMap().put("doF2", vPageNextAction);
        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F3, InputEvent.ALT_MASK),
                "doF3");
        thePane.getActionMap().put("doF3", vAttachPrevAction);
        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK),
                "doF4");
        thePane.getActionMap().put("doF4", vAttachNextAction);
        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.ALT_MASK),
                "doF5");
        thePane.getActionMap().put("doF5", vDocPrevAction);
        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F6, InputEvent.ALT_MASK),
                "doF6");
        thePane.getActionMap().put("doF6", vDocNextAction);
        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F7, InputEvent.ALT_MASK),
                "doF7");
        thePane.getActionMap().put("doF7", vFirstCurrAction);
        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_F8, InputEvent.ALT_MASK),
                "doF8");
        thePane.getActionMap().put("doF8", vLastCurrAction);

        // Home for first focusable component (text field)
        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_HOME, 0),
                "doHome");
        thePane.getActionMap().put("doHome", homeAction);

        // Unitizing keystrokes, below
        // B for Batch
        //coderPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
        //                    .put(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.ALT_MASK),
        //                        "doBatch");
        //coderPanel.getActionMap().put("doBatch",
        //                         batchAction);
        // D for Document (Attachment Range)
        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, InputEvent.ALT_MASK),
                "doDoc");
        thePane.getActionMap().put("doDoc", docAction);
        // I for Child
        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.ALT_MASK),
                "doChild");
        thePane.getActionMap().put("doChild", childAction);
        // X for Clear
        thePane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK),
                "doClear");
        thePane.getActionMap().put("doClear", clearAction);
        // Ctl Z for Undo
        thePane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK),
                "doUndo");
        thePane.getActionMap().put("doUndo", getUndoController().getUndoAction());
        // Ctl Y for Redo
        thePane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK),
                "doRedo");
        thePane.getActionMap().put("doRedo", getUndoController().getRedoAction());

    }

    public int getBatchId() {
        return batchId;
    }

    public synchronized void setBatchId(int id) {
        batchId = id;
    }

    public int getVolumeId() {
        return volumeId;
    }

    public synchronized void setVolumeId(int id) {
        volumeId = id;
    }

    public int getProjectId() {
        return projectId;
    }

    public synchronized void setProjectId(int id) {
        projectId = id;
    }

    public synchronized void setActiveGroup(int group) {
        activeGroup = group;
    }

    //added for L1 process
    public synchronized void setLevel(int level) {
        activelevel = level;
    }

    public void initializeForProject(String projectName, int projectId, String whichStatus, String splitDocuments) {
           
        Log.print("SPV.initializeForProject" + " projectName=" + projectName + " whichStatus=" + whichStatus + " splitDocuments=" + splitDocuments);
        this.projectName = projectName;
        this.projectId = projectId;
        String split[] = whichStatus.split("-");
        if(split.length >1){            
        whichStatus = split[0];
        this.whichStatus = whichStatus;       
        batchSubProcess = split[1];         
        }
        else{
        this.whichStatus = whichStatus;
        }
        
        this.splitDocuments = splitDocuments.equals("Yes") ? true : false;
        defaultCoordinates = ("Binder".equals(whichStatus)
                //                x, y   w, h, div    x, y   w, h, div     x, y,  w, h, x2, y2, w2, h2
                ? new String[]{"20,20,856,648,310", "20,20,856,648,546", "20,20,310,648,330,20,546,648"}
                : new String[]{"0,0,856,648,310", "0,0,856,648,546", "0,0,310,648,310,0,546,648"});

        // Set the current view from the properties file
        viewPrefix = ("Binder".equals(whichStatus) ? "binder_" : "viewer_");
        setView(LocalProperties.getProperty(viewPrefix + "view", CODING_LEFT));
        // make sure radio button is set correctly
        switch (currentView) {
            case CODING_LEFT:
                codingLeftButton.setSelected(true);
                break;
            case CODING_RIGHT:
                codingRightButton.setSelected(true);
                break;
            case CODING_UNDOCKED:
                codingUndockedButton.setSelected(true);
                break;
        }

        resolution = LocalProperties.getProperty(viewPrefix + "resolution", RES_HIGH);
        switch (resolution) {
            case RES_HIGH:
                highResButton.setSelected(true);
                break;
            case RES_MEDIUM:
                mediumResButton.setSelected(true);
                break;
            case RES_LOW:
                lowResButton.setSelected(true);
                break;
            case RES_DRAFT:
                draftResButton.setSelected(true);
                break;
        }

        // Set indication of which viewer, for constructing frame title
        this.unitize = false;
        addToBinderItem.setVisible(false);
        removeFromBinderItem.setVisible(false);
        qcCoderErrorReportItem.setVisible(false);
        qcProjectSummaryItem.setVisible(false);
        qcProjectReportMenu.setVisible(false);
        // saveAndGoButton must be recreated here or it doesn't
        // show on a second creation of the dynamicPane screen.
        saveAndGoButton = new javax.swing.JButton();
        saveAndGoButton.setAction(saveAction);
        saveAndGoButton.setText("Save & Go");
        saveAndGoButton.setToolTipText("Save, then go to next document (Alt+N).");

        //edit button to hide the coded value
        editButton = new javax.swing.JButton();
        editButton.setAction(editAction);
        editButton.setText("Clear & Edit");
        editButton.setToolTipText("Clear the coded value");


        firstNewButton.setEnabled(true);
        saveButton.setEnabled(true);
        binderLabel.setVisible(false);
        attachPane.setVisible(true);
        // File menu
        closeBatchMenuItem.setEnabled(true);
        // Edit menu
        copyCurrentMenuItem.setEnabled(true);
        copyAllMenuItem.setEnabled(true);
        boundaryMenu.setEnabled(true);

        sampleMenuItem.setEnabled(false);
        qcBatchOpenReportMenu.setVisible(false);
        qcBatchQCReportMenu.setVisible(false);

        // all users can run the payroll detail for themselves
        payrollDetailSelfMenu.setEnabled(true);
        payrollDetailTeamMenu.setEnabled(false);
        payrollSummarySelfMenu.setEnabled(true);
        payrollSummaryTeamMenu.setEnabled(false);

        if ("Unitize".equals(whichStatus)) {
            selectedActivity = "Unitize";
            titlePrefix = "Unitization";
            coderToolBar.setVisible(true);
            this.unitize = true;
            rejectBatchMenuItem.setEnabled(false);
            closeVolume.setVisible(false);
            createQAIRMenuItem.setVisible(false);
            listingQCDone.setVisible(false);
            tallyQCDone.setVisible(false);
            assignListingQCMenuItem.setVisible(false);
            assignTallyQCMenuItem.setVisible(false);
            count.setVisible(false);
            setUnitizeNavigation();

        } else if ("UQC".equals(whichStatus)) {
            selectedActivity = "UQC";
            titlePrefix = "Unitization QC";
            coderToolBar.setVisible(true);
            this.unitize = true;
            rejectBatchMenuItem.setEnabled(true);
            qcBatchOpenReportMenu.setVisible(true);
            qcBatchQCReportMenu.setVisible(true);
            closeVolume.setVisible(false);
            listingQCDone.setVisible(false);
            tallyQCDone.setVisible(false);
            createQAIRMenuItem.setVisible(false);
            assignListingQCMenuItem.setVisible(false);
            assignTallyQCMenuItem.setVisible(false);
            count.setVisible(false);
            setUnitizeNavigation();
        } else if ("Coding".equals(whichStatus)) {
            selectedActivity = "Coding";
            titlePrefix = "Coding";
            coderToolBar.setVisible(true);
            rejectBatchMenuItem.setEnabled(false);
            boundaryMenu.setEnabled(false);
            closeVolume.setVisible(false);
            createQAIRMenuItem.setVisible(false);
            listingQCDone.setVisible(false);
            tallyQCDone.setVisible(false);
            count.setVisible(false);
            assignListingQCMenuItem.setVisible(false);
            assignTallyQCMenuItem.setVisible(false);
        } else if ("CodingQC".equals(whichStatus)) {
            selectedActivity = "CodingQC";
            titlePrefix = "Quality Control";
            coderToolBar.setVisible(true);
            rejectBatchMenuItem.setEnabled(true);
            addToBinderItem.setVisible(true);
            createQAIRMenuItem.setVisible(false);
            qcCoderErrorReportItem.setVisible(true);
            qcProjectSummaryItem.setVisible(true);
            qcProjectReportMenu.setVisible(true);
            qcBatchOpenReportMenu.setVisible(true);
            qcBatchQCReportMenu.setVisible(true);
            closeVolume.setVisible(false);
            listingQCDone.setVisible(false);
            tallyQCDone.setVisible(false);
            assignListingQCMenuItem.setVisible(false);
            assignTallyQCMenuItem.setVisible(false);
            count.setVisible(false);
        } else if ("QA".equals(whichStatus)) {
            selectedActivity = "QA";
            titlePrefix = "Quality Assurance";
            coderToolBar.setVisible(true);
            rejectBatchMenuItem.setEnabled(true);
            addToBinderItem.setVisible(true);
            createQAIRMenuItem.setVisible(false);
            qcCoderErrorReportItem.setVisible(true);
            sampleMenuItem.setEnabled(true);
            closeVolume.setVisible(false);
            listingQCDone.setVisible(false);
            tallyQCDone.setVisible(false);
            assignListingQCMenuItem.setVisible(false);
            assignTallyQCMenuItem.setVisible(false);
            count.setVisible(false);
        } else if ("Admin".equals(whichStatus)) {
            selectedActivity = "Admin";
            titlePrefix = "Admin Browse";
            addToBinderItem.setVisible(true);
            createQAIRMenuItem.setVisible(false);
            // File menu
            rejectBatchMenuItem.setEnabled(false);
            closeBatchMenuItem.setEnabled(false);
            listingQCDone.setVisible(false);
            tallyQCDone.setVisible(false);
            closeVolume.setVisible(false);
            assignListingQCMenuItem.setVisible(false);
            assignTallyQCMenuItem.setVisible(false);
            count.setVisible(false);
        } else if ("Binder".equals(whichStatus)) {
            selectedActivity = "Binder";
            titlePrefix = "Project Binder";
            openBinderMenuItem.setVisible(true);
            openBinderMenuItem.setEnabled(false);
            createQAIRMenuItem.setVisible(false);
            assignListingQCMenuItem.setVisible(false);
            assignTallyQCMenuItem.setVisible(false);
            if (!(Global.mainWindow instanceof SplitPaneViewer)) {
                // Admin can remove from binder while viewing binder
                removeFromBinderItem.setVisible(true);
            }
            saveAndGoButton.setAction(nextDocAction);
            saveAndGoButton.setText("Next Doc.");
            saveAndGoButton.setToolTipText("Go to next document.");
            firstNewButton.setEnabled(false);
            saveButton.setEnabled(false);
            binderLabel.setVisible(true);
            attachPane.setVisible(false);
            // File menu
            rejectBatchMenuItem.setEnabled(false);
            closeBatchMenuItem.setEnabled(false);
            removeFromBinderItem.setEnabled(true);
            // Edit menu
            copyCurrentMenuItem.setEnabled(false);
            copyAllMenuItem.setEnabled(false);
            boundaryMenu.setEnabled(false);
            closeVolume.setVisible(false);
            listingQCDone.setVisible(false);
            tallyQCDone.setVisible(false);
        } else if ("Listing".equals(whichStatus)) {
            selectedActivity = "Listing";
            titlePrefix = "Listing";
            viewerLabel.setText(" ");
            rejectBatchMenuItem.setEnabled(false);
            boundaryMenu.setEnabled(false);
        //closeVolume.setEnabled(false); 
        } else if ("ListingQC".equals(whichStatus)) {
            selectedActivity = "ListingQC";
            titlePrefix = "ListingQC";
            viewerLabel.setText(" ");
            rejectBatchMenuItem.setEnabled(false);
            boundaryMenu.setEnabled(false);
            listingQCDone.setVisible(true);            
        //closeVolume.setEnabled(false); 
        } else if ("Tally".equals(whichStatus)) {
            selectedActivity = "Tally";
            titlePrefix = "Tally";
            viewerLabel.setText(" ");
            rejectBatchMenuItem.setEnabled(false);
            boundaryMenu.setEnabled(false);
        } else if ("TallyQC".equals(whichStatus)) {
            selectedActivity = "TallyQC";
            titlePrefix = "Tally";
            viewerLabel.setText(" ");
            rejectBatchMenuItem.setEnabled(false);
            boundaryMenu.setEnabled(false);
            tallyQCDone.setVisible(true);
            copyCurrentMenuItem.setEnabled(false);
            copyAllMenuItem.setEnabled(false);
        } else if ("Masking".equals(whichStatus)) {
            selectedActivity = "Masking";
            titlePrefix = "Masking";
            coderToolBar.setVisible(true);
            viewerLabel.setText(" ");
            rejectBatchMenuItem.setEnabled(false);
            boundaryMenu.setEnabled(false);
            createQAIRMenuItem.setVisible(false);
            assignListingQCMenuItem.setVisible(false);
            assignTallyQCMenuItem.setVisible(false);
            listingQCDone.setVisible(false);
            tallyQCDone.setVisible(false);
            count.setVisible(false);
            closeVolume.setVisible(false);
        } else if ("ModifyErrors".equals(whichStatus)) {            
            selectedActivity = "ModifyErrors";
            titlePrefix = "ModifyErrors"+"-"+batchSubProcess;
           // titlePrefix = "ModifyErrors";
            coderToolBar.setVisible(true);
            rejectBatchMenuItem.setEnabled(false);
            boundaryMenu.setEnabled(false);
            closeVolume.setVisible(false);
            createQAIRMenuItem.setVisible(false);
            listingQCDone.setVisible(false);
            tallyQCDone.setVisible(false);
            count.setVisible(false);
            assignListingQCMenuItem.setVisible(false);
            assignTallyQCMenuItem.setVisible(false);
        } else {
            Log.quit("SPV.initializeForProject - invalid status: " + whichStatus);
        }

        if ("Listing".equals(whichStatus)) {
            viewerLabel.setText(" ");
            coderToolBar.setVisible(false);
            setTitle("Listing for project :" + getProjectName());
            coderPane.setVisible(false);
            topPane.setVisible(true);
            addTimerPanel("Listing");
            occurrence.setVisible(true);
            marking.setVisible(false);
            closeBatchMenuItem.setVisible(false);
            //closeMenuItem.setVisible(false);
            assignListingQCMenuItem.setVisible(false);
            assignListingQCMenuItem.setFocusable(false);
            assignTallyQCMenuItem.setVisible(false);
            openBinderMenuItem.setVisible(false);
            errorTypePane.setVisible(false);
            closeVolume.setVisible(true);
            tallyQCDone.setVisible(false);
            listingQCDone.setVisible(false);
            count.setVisible(false);
            rejectBatchMenuItem.setEnabled(false);
            createQAIRMenuItem.setVisible(false);
            boundaryMenu.setEnabled(false);
            copyCurrentMenuItem.setEnabled(false);
            copyAllMenuItem.setEnabled(false);
            // tabSelected();            
            //loadFieldvalue();
            fields = getFields();
            for (int i = 0; i < fields.size(); i++) {
                //  System.out.println("fields.get(i)....."+fields.get(i));
                fieldComoBox.addItem(fields.get(i));
            }
        } else if ("ListingQC".equals(whichStatus)) {
            selectedActivity = "ListingQC";
            //titlePrefix = "ListingQC";
            viewerLabel.setText(" ");
            coderToolBar.setVisible(false);
            setTitle("ListingQC for project :" + getFieldName());
            coderPane.setVisible(false);
            occurrence.setVisible(true);
            jPanel22.setVisible(false);
            marking.setVisible(false);
            addTimerPanel("ListingQC");
            closeBatchMenuItem.setVisible(false);
            //closeMenuItem.setVisible(false);
            assignListingQCMenuItem.setVisible(false);
            assignTallyQCMenuItem.setVisible(false);
            openBinderMenuItem.setVisible(false);
            errorTypePane.setVisible(false);
            closeVolume.setVisible(false);
            tallyQCDone.setVisible(false);
            listingQCDone.setVisible(true);
            count.setVisible(false);
            rejectBatchMenuItem.setEnabled(false);
            createQAIRMenuItem.setVisible(false);
            boundaryMenu.setEnabled(false);
            loadFieldvalue(projectId);

        } else if ("Tally".equals(whichStatus)) {
            selectedActivity = "Tally";
            viewerLabel.setText(" ");

            coderToolBar.setVisible(false);
            setTitle("Tally for project :" + getProjectName());
            coderPane.setVisible(false);
            topPane.setVisible(true);
            occurrence.setVisible(true);
            marking.setVisible(false);
            addTimerPanel("Tally");
            closeBatchMenuItem.setVisible(false);
            //closeMenuItem.setVisible(false);
            openBinderMenuItem.setVisible(false);
            assignListingQCMenuItem.setVisible(false);
            assignTallyQCMenuItem.setVisible(true);
            errorTypePane.setVisible(true);
            closeVolume.setVisible(true);
            tallyQCDone.setVisible(false);
            listingQCDone.setVisible(false);
            count.setVisible(true);
            copyCurrentMenuItem.setEnabled(false);
            copyAllMenuItem.setEnabled(false);
            // tabSelected();            
            //loadFieldvalue();
            fields = getFields();
            for (int i = 0; i < fields.size(); i++) {
                //  System.out.println("fields.get(i)....."+fields.get(i));
                fieldComoBox.addItem(fields.get(i));
            }
        } else if ("TallyQC".equals(whichStatus)) {
            selectedActivity = "TallyQC";
            rejectBatchMenuItem.setEnabled(false);
            boundaryMenu.setEnabled(false);
            createQAIRMenuItem.setVisible(false);
            tallyQCDone.setVisible(true);

            viewerLabel.setText(" ");
            coderToolBar.setVisible(false);
            setTitle("Tally for project :" + getFieldName());
            coderPane.setVisible(false);
            occurrence.setVisible(true);
            jPanel22.setVisible(false);
            marking.setVisible(false);
            closeBatchMenuItem.setVisible(false);
            addTimerPanel("Tally");
            //closeMenuItem.setVisible(false);
            assignListingQCMenuItem.setVisible(false);
            assignTallyQCMenuItem.setVisible(false);
            openBinderMenuItem.setVisible(false);
            errorTypePane.setVisible(true);
            closeVolume.setVisible(false);
            tallyQCDone.setVisible(true);
            listingQCDone.setVisible(false);
            count.setVisible(false);
            loadFieldvalue(projectId);

        } else {
            // set visibility and placement of unitizing controls
            // depending on the unitize flag
            initUnitizingComponents();

            occurrence.setVisible(false);
            marking.setVisible(false);
            if (!unitize) {
                coderPanel.getActionMap().put("doSaveAndGoToPage", null);
                coderPanel.getActionMap().put("doPrevPageDoc", null);
                coderPanel.getActionMap().put("doNextPageDoc", null);
            }

            if (!"Binder".equals(whichStatus)) {
                validateBatchMenuItem.setEnabled(true);
                forceSaveMenuItem.setEnabled(true);
                addToBinderItem.setEnabled(true);
            }
            //removeFromBinderItem.setEnabled(true);

            // initiate managed-table download of table values
            // Note.  Loads for project based on [volume]
            // Note.  Must come before getProjectFields.  (enqueue will serialize tasks.)
            loadTableValues();

            // set up projectMap and projectEditor
            // ... uses locked volume to determine project
            // ... TBD: admin app must pass a volume
            // callback calls initDynamicComponents to create screen fields
            // callback calls getCoderData initiate population screen fields and image
            // ... locked batch used for getCoderData
            getProjectFields();
            // getValidationData();
            getUndoController().discardAllEdits();

            // Save the Actions defined for up and down arrow and page up and down to reset
            // those actions following disabling them when the ibaseTextField popup is visible.
            upAction = midScrollPane.getActionMap().get(upKey);
            downAction = midScrollPane.getActionMap().get(downKey);
            pageUpAction = midScrollPane.getActionMap().get(pageUpKey);
            pageDownAction = midScrollPane.getActionMap().get(pageDownKey);
            midScrollPane.getActionMap().put("doUp", upAction);
            midScrollPane.getActionMap().put("doDn", downAction);
            midScrollPane.getActionMap().put("doPageUp", pageUpAction);
            midScrollPane.getActionMap().put("doPageDn", pageDownAction);
            if (!whichStatus.equalsIgnoreCase("Binder") && !whichStatus.equalsIgnoreCase("Admin")) {

                addTimerPanel(titlePrefix);
            }
        }

        getUserId();

    // ------------------end ---------------------------
    // }
    }

    /**
     * Used in IbaseTextField to disable the up arrow when the popup is visible.
     * @return the Action defined for the up arrow.
     */
    public Action getUpAction() {
        return upAction;
    }

    /**
     * Used in IbaseTextField to disable the down arrow when the popup is visible.
     * @return the Action defined for the down arrow.
     */
    public Action getDownAction() {
        return downAction;
    }

    /**
     * Used in IbaseTextField to disable the alt+up arrow when the popup is visible.
     * @return the Action defined for the alt+up arrow.
     */
    public Action getPageUpAction() {
        return pageUpAction;
    }

    /**
     * Used in IbaseTextField to disable the alt+down arrow when the popup is visible.
     * @return the Action defined for the alt+down arrow.
     */
    public Action getPageDownAction() {
        return pageDownAction;
    }
    //for F10

    public void setVisibleQueryTracker(boolean color) {
        this.setvisible = color;
    }

    public boolean getVisibleQueryTracker() {
        return setvisible;
    }

    /**
     * Unitizers have navigation between pages, in addition to standard
     * document navigation.  This method sets up the Save & Go To Page button and 
     * two speedkeys, alt+Up and alt+Down.
     */
    private void setUnitizeNavigation() {
        saveAndGoBoundaryButton = new javax.swing.JButton();
        saveAndGoBoundaryButton.setAction(savePageAction);
        saveAndGoBoundaryButton.setText("Save & Go To Page");
        saveAndGoBoundaryButton.setToolTipText("Save, then go to next page (Ctl+N).");
        coderPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK),
                "doSaveAndGoToPage");
        coderPanel.getActionMap().put("doSaveAndGoToPage",
                savePageAction);

        coderPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, InputEvent.ALT_MASK),
                "doPrevPageDoc");
        coderPanel.getActionMap().put("doPrevPageDoc",
                prevDocPageAction);

        coderPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, InputEvent.ALT_MASK),
                "doNextPageDoc");
        coderPanel.getActionMap().put("doNextPageDoc",
                nextDocPageAction);
    }

    // set visibility and placement of unitizing controls
    private void initUnitizingComponents() {
        Dimension dim = topPane.getPreferredSize();
        if (unitize) {
            dim.height = 235;
            topPane.setPreferredSize(dim);
            boundaryText.requestFocus();
            unitizingPanel.setVisible(true);
            boundaryPane.setVisible(true);
            absolutePane.setVisible(true);
            boundaryMenu.setEnabled(false);
            //firstNewButton.setEnabled(false);
            vPagePrevButton.setEnabled(false);
            vPageNextButton.setEnabled(false);
            vDocPrevButton.setEnabled(false);
            vDocNextButton.setEnabled(false);
            vAttachPrevButton.setEnabled(false);
            vAttachNextButton.setEnabled(false);
            vFirstCurrButton.setEnabled(false);
            vLastCurrButton.setEnabled(false);
            vAbsoluteButton.setEnabled(false);
            vBeginImageRunButton.setEnabled(false);
        } else {
            dim.height = 110;
            topPane.setPreferredSize(dim);
            unitizingPanel.setVisible(false);
            boundaryPane.setVisible(false);
            absolutePane.setVisible(false);
            // Enable these two because the could have been
            // disabled in a previous unitize session, above.
            // The other image nav buttons are set when retrieving every image.
            vAbsoluteButton.setEnabled(true);
            vBeginImageRunButton.setEnabled(true);
        }
    //saveAndGoButton.setNextFocusableComponent(firstFocus);
    }

    /**
     * Enqueue a task to get the project description from project_fields.
     * The data is returned in common.edit.ProjectMapper format via the entry point, projectFieldsEntry.
     *
     * @see projectFieldsEntry
     * @see common.edit.ProjectMapper
     */
    private void getProjectFields() {
        //Log.print("(SPV).getProjectFields enter");
        final ClientTask task = new TaskRequestProjectFields(volumeId, projectId);
        //Log.print("(SPV).getProjectFields setCallback");
        task.setCallback(new Runnable() {
                public void run() {
                   // try {
                        //Log.print("(SPV).getProjectFields create map");
                        Map map = (Map) task.getResult();
                       // if(null != map){
                            Map data = (Map) map.get(T_VALUE_LIST);                        
                            validationData = (Map) map.get(T_VALIDATIONS);
                            //Log.print("(SPV).getProjectFields test map size");
                            if (data != null) {                           
                                getProjectFieldsEntry(data);
                                //Log.print("getProjectFields: back from projectFieldsEntry");
                            } 
                            else { // since data == null
                               // Log.quit("getProjectFields: null returned");
                                getProjectFieldsEntry(data);
                            }

                }
            });
        boolean ok = task.enqueue(this);

    }

    /**
     * Entry point method to receive and format the project_fields data
     * based on data provided by client.ClientTask.
     *
     * @param map -
     *
     * @see client.ClientTask
     * @see client.ClientThread
     */
    public void getProjectFieldsEntry(Map map) {
        if (map == null) {
            Log.write("(Coding).getProjectFieldsEntry: map is null");
            //return;
        }
        //Log.print("(Coding).getProjectFieldsEntry " + map.size());
        projectMap = new ProjectMapper(map);
        //Log.print("(Coding).getProjectFieldsEntry create projectEditor");
        projectEditor = new ProjectEditor(projectMap, /*error component=>*/ this);
        theFieldMapper = new FieldMapper();

        // Do this here to be sure the dynamic fields have been created
        // before loading coder data.

        // Note.  Need to clear the pageId, in case we had been viewing a different batch
        // First parameter is pageId = 0 for start of batch

        //--------for tally validations----------
        if (!"Tally".equals(whichStatus)) {
            initDynamicComponents();
            System.out.println("=====================> NOT TALLY");
            getCoderData(0, PLUS_ONE, B_CHILD);
        }
        // If comments exist for this batch, show them.
        if (!"QA".equals(whichStatus)) {
            BatchCommentsDialog.showDialog(this, /*showIfNoComments->*/ false);
        }

        if (projectMap != null && projectMap.size() > 0 && projectMap.component.size() > 0) {
            projectMap.getComponent(0).requestFocus();
        }
    }

    public void getProjectFieldsValidationEntry(Map map) {
        if (map == null) {
            Log.write("(Coding).getProjectFieldsValidationEntry: map is null");
            return;
        }
        //Log.print("(Coding).getProjectFieldsEntry " + map.size());
        int j = 1;
        // projectMap1 = new ProjectMapper(map ,j);
        //Log.print("(Coding).getProjectFieldsEntry create projectEditor");
        projectEditor1 = new ProjectEditor(projectMap1, /*error component=>*/ this);

    /* theFieldMapper = new FieldMapper();
    initDynamicComponents();
    
    // Do this here to be sure the dynamic fields have been created
    // before loading coder data.
    
    // Note.  Need to clear the pageId, in case we had been viewing a different batch
    // First parameter is pageId = 0 for start of batch
    getCoderData(0, PLUS_ONE, B_CHILD);
    
    // If comments exist for this batch, show them.
    if (! "QA".equals(whichStatus)) {
    BatchCommentsDialog.showDialog(this
    //                                  , showIfNoComments/ false);
    //     }
    
    /* if (projectMap != null && projectMap.size() > 0
    && projectMap.component.size() > 0) {
    projectMap.getComponent(0).requestFocus();
    }*/
    }

    /**
     * Dynamically create the components defined for this coding screen and load
     * the list selection data.
     */
    private void initDynamicComponents() {
        //JPanel groupBorderPane = new JPanel();
        beans.LGridBag groupBorderPane = new beans.LGridBag();
        //groupBorderPane.setLayout(new BorderLayout());
        String imagePath = "";
        String batesNumber = "";
        int childId = 0;
        int batchId = 0;
        String image_Path = "";
        String bates_Number = "";
        int child_Id = 0;
        int batch_Id = 0;
        int previousGroup = -1;
        int componentCount = 0;
        fieldCount = 0;

        boolean firstFocus = false;
        // remove any existing dynamic components
        dynamicPane.removeAll();

        compCount = projectMap.size();
        JComponent comp = null;
        int row = 2; // 3rd row to leave 1 and 2 for unitizing boundary
        // put the fields defined for this batch onto the screen

        String clearEdit = "";

        for (int i = 0; i < compCount; i++) {
            //Log.print("(Coding) " + i + "/" + compCount + "/" + projectMap.getFieldName(i)
            //          + " unitize=" + unitize);
            //String projectName = projectMap.projectName;   
            
            String name = projectMap.getFieldName(i);
            JLabel nameLabel = new JLabel(projectMap.getFieldName(i));
            String tooltip = null;

            ProjectMapper.HashValue value = projectMap.getHashValue(name);    
           //Log.print("(SPV.initDynamic) " + value.unitize);
            clearEdit = value.l1_information ;
            System.out.println("clearEdit=====>" + clearEdit);
            setProjectId(value.projectId);

            if (value.fieldGroup != previousGroup) {
                //Log.print("(SPV.initDynamicComponents) group is " + value.fieldGroup);
                groupPane = new beans.LGridBag();
                //groupPane.setFocusCycleRoot(true); // excludes save&go button
                groupPane.setFocusTraversalPolicy(new SkipCheckboxFocusTraversalPolicy());

                groupBorderPane = new beans.LGridBag();
                //groupBorderPane.setLayout(new BorderLayout());
                if (value.fieldGroup > 0) { // no groups in this project when 0
                    //groupBorderPane.add(getButton(value.fieldGroup), BorderLayout.NORTH);

                    groupBorderPane.add(0, 0, getButton(value.fieldGroup));
                }
                //groupBorderPane.add(groupPane, BorderLayout.CENTER);
                groupBorderPane.add(0, 1, (JComponent) groupPane);

                dynamicPane.add(0, row++, groupBorderPane);
                previousGroup = value.fieldGroup;
                firstFocus = true;
            }

            if (!unitize || value.unitize.equals("Yes")) {
                boolean repeated = (value.repeated).equals("Yes");
                boolean required = (value.required).equals("Yes");

                //Log.print("(CoderScreen) field " + i + "/" + value.fieldType + "/" + value.fieldSize + "/" + repeated);

                // put the field name on the screen
                if (required) {
                    // required field - make label bold
                    nameLabel.setFont(new Font("Dialog", Font.BOLD, 11));
                    nameLabel.setToolTipText(
                            "<html>Bold field name indicates <b>Required</b> field</html>");
                }
                nameLabel.setFocusable(false);
                // check for minimum and maximum values -- add to screen next to field
                String minmax = null;
                if (value.minValue != null && !value.minValue.equals("") && value.maxValue != null && !value.maxValue.equals("")) {
                    minmax = " (" + value.minValue + " - " + value.maxValue + ")";
                } else if (value.minValue != null && !value.minValue.equals("")) {
                    minmax = " (>=" + value.minValue + ")";
                } else if (value.maxValue != null && !value.maxValue.equals("")) {
                    minmax = " (<=" + value.maxValue + ")";
                }
                if (minmax != null) {
                    nameLabel.setText(name + " " + minmax);
                }
                // projectfields.field_name
                groupPane.add(0, ++row, nameLabel);

                // Show the user the characters allowed or disallowed for this field.
                if (!value.validChars.equals("")) {
                    JLabel charsLabel = new JLabel("");
                    charsLabel.setText("(Valid Chars: " + value.validChars + ")");
                    charsLabel.setFocusable(false);
                    groupPane.add(0, ++row, charsLabel);
                }
                if (!value.invalidChars.equals("")) {
                    int j = 0;
                    String format = "";
                    if ((j = value.invalidChars.indexOf("\\u")) > -1) {
                        //format = "Unprintable";
                        value.invalidChars = value.invalidChars.substring(0, j);
                        if (j + 3 < value.invalidChars.length()) {
                            value.invalidChars = value.invalidChars.substring(j + 3);
                        //format = format + "; ";
                        //Log.print("(SPV) \\u removed: " + value.invalidChars);
                        }
                    }
                    if (!value.invalidChars.equals("")) {
                        // if there are invalid characters, other than unprintable chars,
                        // show them on the screen.
                        JLabel charsLabel = new JLabel("");
                        charsLabel.setText("(Invalid Chars: " + format + value.invalidChars + ")");
                        charsLabel.setFocusable(false);
                        groupPane.add(0, ++row, charsLabel);
                    }
                }

                // Now put the field on the screen
                if (value.fieldType.equals("date")) {
                    JPanel date = new JPanel();
                    if (null != area) {
                        imagePath = area.getImagePath();
                        batesNumber = area.getBatesNumber();
                        childId = area.getChildId();
                        batchId = area.getBatchId();                            
                    }
                    comp = new beans.LDateField(value.projectId, value.fieldName, theCodingData, whichStatus, documentNumber, bates_Number, image_Path, child_Id, batch_Id, projectMap);
                    JLabel label = new JLabel(" (YYYYMMDD)");
                    label.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
                    label.setFocusable(false);
                    groupPane.add(0, ++row, makeJPanel(comp, label));
                    // add the undo listener
                    getUndoController().registerDocument(((JTextField) comp).getDocument(), (JTextField) comp);
                } else if (value.fieldType.equals(IbaseConstants.DYNAMIC_FIELD_IS_TEXT)) {
                    // mask
                    if (value.tablespecId > 0) {
                        // table_name is not empty -- this is a list field
                        if (null != area) {
                            imagePath = area.getImagePath();
                            batesNumber = area.getBatesNumber();
                            childId = area.getChildId();

                            batchId = area.getBatchId();
                            
                        }
                        if (setvisible && "CodingQC".equals(whichStatus)) {
                            //area.getTextField().setEnabled(false);
                        }


                        comp = makeIbaseTextField(value.fieldType, value.tablespecId, name, value.tableMandatory, value.repeated, value.is_level_field, value.mask, value.validChars, value.invalidChars, value.projectId, value.fieldName, documentNumber, batesNumber, whichStatus, imagePath, childId, batchId, theCodingData);

                        // if(null != value.queryraised && "Yes".equals(value.queryraised) && "Yes".equals(showQueryRaised)){                       

//                         if(null != value.queryraised && ("Yes".equals(value.queryraised) || "Yes".equals(value.listing_marking) &&( whichStatus.equals("CodingQC") || whichStatus.equals("Coding") || whichStatus.equals("ModifyErrors")))){                              
//
//                           
//
//                             JLabel label = new JLabel();
//                             if("Yes".equals(value.queryraised) && (whichStatus.equals("CodingQC") || whichStatus.equals("Coding"))){
//                                  label.setText("F10");
//                                  label.setForeground(Color.RED);
//                            }else if( "Yes".equals(value.listing_marking)&& whichStatus.equals("ModifyErrors")){
//
//                                //label.setText("LM");                                
//                                if (null != theCodingData && null != theCodingData.listing_marking && theCodingData.listing_marking.equals("Yes")) {                                    
//                                    label.setText("LM");
//                                }else{
//                                    label.setText("");
//                                }
//                            }
//
//
//                            //JLabel label = new JLabel("F10");
//                            label.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
//                            // label.setForeground(Color.RED);                              
//                            label.setVisible(setvisible);
//                            //comp.setEnabled(false);
//
//                            groupPane.add(0, ++row, makeJPanel(comp, label));
//                        } else if (null != value.queryraised && "No".equals(value.queryraised) && "Yes".equals(value.queryanswered)) {
//                            JLabel label = new JLabel("F10");
//                            label.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
//                            label.setForeground(Color.GREEN);
//                            label.setVisible(setvisible);
//
//                            groupPane.add(0, ++row, makeJPanel(comp, label));
//                        }
                      //  else {
                            groupPane.add(0, ++row, addSpellCheck(comp, value.spellcheck), "f=H,wx=100");
                        //}

                    //} else if (! (value.mask).equals("")) {
                    //    comp = getJFormattedTextField(value.mask, value.validChars, value.invalidChars);
                    //    dynamicPane.add(0, ++row, comp, "f=H,wx=100");
                    } else {
                        // text field
                       
                        if (value.fieldSize > 25) {
                            // too long for screen, so make it an LTextbutton
                            //comp = new beans.LTextButton(name, value.mask, value.validChars, value.invalidChars);

                            comp = new beans.LTextButton(name, value.fieldSize, value.mask, value.validChars, value.invalidChars, value.projectId, value.fieldName, theCodingData, whichStatus);
                            // comp = new beans.LTextButton(value.fieldSize,value.projectId,value.fieldName,theCodingData,whichStatus);
                            //getUndoController().registerDocument(((LTextField)comp).getDocument(), (LTextField)comp);
                            ((beans.LTextButton) comp).setMaximumColumns(value.fieldSize);
                            ((beans.LTextButton) comp).setName(name);
                            getUndoController().registerDocument(((beans.LTextButton) comp).getDocument(), ((beans.LTextButton) comp).getTextField());
                        } else {

                            if (!value.mask.equals("") || !value.validChars.equals("") || !value.invalidChars.equals("")) {
                                Log.print("(SPV.initDynamic) comp is text LFormatted(" + value.fieldSize + "," + value.mask + "," +
                                        value.validChars + "," + value.invalidChars);
                                comp = new beans.LFormattedTextField(value.fieldSize, value.mask, value.validChars, value.invalidChars, value.projectId, value.fieldName, theCodingData, whichStatus);
                                getUndoController().registerDocument(((beans.LFormattedTextField) comp).getDocument(), (beans.LFormattedTextField) comp);

                            } else {
                                LTextField obj = new beans.LTextField();
                                int child_id = obj.getChildId();
                                comp = new beans.LTextField(value.fieldSize, value.projectId, value.fieldName, theCodingData, whichStatus, projectMap, child_id);
                                getUndoController().registerDocument(((LTextField) comp).getDocument(), (LTextField) comp);
                            }
                        }
                        if (!value.mask.equals("")) {
                            tooltip = "Mask: " + value.mask;
                        }
                        Log.print(" back");

//                        if("Yes".equals(value.queryraised) || "Yes".equals(value.listing_marking) &&( (whichStatus.equals("CodingQC") || whichStatus.equals("Coding") || whichStatus.equals("ModifyErrors")))){                                                  
//                            JLabel label = new JLabel();
//
//                            if("Yes".equals(value.queryraised) && (whichStatus.equals("CodingQC") || whichStatus.equals("Coding"))){
//                                  label.setText("F10") ;
//                                         
//                            }
////                            else if( "Yes".equals(value.listing_marking)&& whichStatus.equals("ModifyErrors")){                              
////                                    //if(flag1){     
////                                    if (null != theCodingData.listing_marking && theCodingData.listing_marking.equals("Yes")) {
////                                       label.setText("LM") ;
////                                    }else{
////                                      label.setText("");
////                                    }                               
////                            }
//
//                            // JLabel label = new JLabel("F10");
//                            label.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
//                            groupPane.add(0, ++row, makeJPanel(comp, label));
//                        }
//                        else {
                            groupPane.add(0, ++row, addSpellCheck(comp, value.spellcheck), "f=H,wx=100");
                        //}
                        ((JComponent) comp).setName(name);
                    }
                } else if (value.fieldType.equals(IbaseConstants.DYNAMIC_FIELD_IS_NAME)) {
                    //if (repeated) {                    
                    if (value.tablespecId > 0) {
                        // table_name is not empty -- this is a list field   
                        if (null != area) {
//                             LTextField obj = new beans.LTextField(child_Id,"");
//                             obj.setChildId(child_Id);
                            image_Path = area.getImagePath();
                            bates_Number = area.getBatesNumber();
                            child_Id = area.getChildId();
                            batch_Id = area.getBatchId();
                        }
                        if (setvisible && "CodingQC".equals(whichStatus)) {
                            //area.getTextField().setEnabled(false);
                        }
                        comp = makeIbaseTextField(value.fieldType,
                                value.tablespecId, name, value.tableMandatory, value.repeated, value.is_level_field, value.mask, value.validChars, value.invalidChars, value.projectId, value.fieldName, documentNumber, bates_Number, whichStatus, image_Path, child_Id, batch_Id, theCodingData);
                        //code added by bala , to  show the fields were raised for F10
                        //below line commented by bala
                        //groupPane.add(0, ++row, comp, "f=H,wx=100");
                        //below line added

//
//                        if(null != value.queryraised && ("Yes".equals(value.queryraised) || "Yes".equals(value.listing_marking) && ((whichStatus.equals("CodingQC") || whichStatus.equals("Coding") || whichStatus.equals("ModifyErrors"))))){    
//
//                            JLabel label = new JLabel();
//
//                            if("Yes".equals(value.queryraised) && (whichStatus.equals("CodingQC") || whichStatus.equals("Coding"))){
//                                   label.setText("F10");
//                                  label.setForeground(Color.RED);
//                            }
//                            else if( "Yes".equals(value.listing_marking)&& whichStatus.equals("ModifyErrors")){

//                        if(null != value.queryraised && ("Yes".equals(value.queryraised) || "Yes".equals(value.listing_marking) && ((whichStatus.equals("CodingQC") || whichStatus.equals("Coding") || whichStatus.equals("ModifyErrors"))))){    
//                           

//
//                            JLabel label = new JLabel();
//
//                            if("Yes".equals(value.queryraised) && (whichStatus.equals("CodingQC") || whichStatus.equals("Coding"))){
//                                   label.setText("F10");
//                                  label.setForeground(Color.RED);
//                            }
//
//                          
//                           //JLabel label = new JLabel("F10");
//                            label.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));                            
//                            System.out.println("setvisible" +  setvisible);
//                            label.setVisible(setvisible); 
//                            groupPane.add(0, ++row, makeJPanel(comp, label));                                               
//                        }
//                        else if(null != value.queryraised && "No".equals(value.queryraised) && "Yes".equals(value.queryanswered)){    
//                          
//                            JLabel label = new JLabel("F10");
//                            label.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
//                            label.setForeground(Color.GREEN);
//                            label.setVisible(setvisible);
//                            groupPane.add(0, ++row, makeJPanel(comp, label));
//                        } else {

////                            else if( "Yes".equals(value.listing_marking)&& whichStatus.equals("ModifyErrors")){
////
////                                // label.setText("LM");
////                                if (null != theCodingData.listing_marking && theCodingData.listing_marking.equals("Yes")) {                                   
////                                    label.setText("LM");
////                                    
////                                }else{
////                                    label.setText("");
////                                }
////                            }
//
//                           
//                           //JLabel label = new JLabel("F10");
//                            label.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));                            
//                            System.out.println("setvisible" +  setvisible);
//                            label.setVisible(setvisible); 
//                            groupPane.add(0, ++row, makeJPanel(comp, label));                                               
//                        }
//                        else if(null != value.queryraised && "No".equals(value.queryraised) && "Yes".equals(value.queryanswered)){    
//                          
//                            JLabel label = new JLabel("F10");
//                            label.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
//                            label.setForeground(Color.GREEN);
//                            label.setVisible(setvisible);
//                            groupPane.add(0, ++row, makeJPanel(comp, label));
//                        } 
//                        else {

                            groupPane.add(0, ++row, comp, "f=H,wx=100");
                        //}
                    // ends
                    } else {
                        if (!value.mask.equals("") || !value.validChars.equals("") || !value.invalidChars.equals("")) {
                            Log.print("(SPV.initDynamic) comp is name LFormatted(25," + value.mask + "," +
                                    value.validChars + "," + value.invalidChars);

                            comp = new beans.LFormattedTextField(25, value.mask, value.validChars, value.invalidChars, value.projectId, value.fieldName, theCodingData, whichStatus);
                        } else {
                            LTextField obj = new beans.LTextField();
                            int child_id = obj.getChildId();
                            comp = new beans.LTextField(25, value.projectId, value.fieldName, theCodingData, whichStatus, projectMap, child_id);
                        }
                        

//                        if("Yes".equals(value.queryraised) ||"Yes".equals(value.listing_marking) &&( whichStatus.equals("CodingQC") || whichStatus.equals("Coding") || whichStatus.equals("ModifyErrors"))){
//
//                            JLabel label = new JLabel();
//
//                            if("Yes".equals(value.queryraised) && (whichStatus.equals("CodingQC") || whichStatus.equals("Coding"))){
//                                   label.setText("F10");
//                            }
////                            else if("Yes".equals(value.listing_marking)&& whichStatus.equals("ModifyErrors")){
////                                if(null != theCodingData ){
////                                        if(theCodingData.listing_marking.equals("Yes")){                                             
////                                             label.setText("LM");
////                                        }else{
////                                            label.setText("");
////                                        }
////                                }                                 
////                            }
//                            //JLabel label = new JLabel("F10");
//                            label.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
//                            groupPane.add(0, ++row, makeJPanel(comp, label));
//                           
//                        }
//                        else {
                            groupPane.add(0, ++row, comp, "f=H,wx=100");
                     //   }
                    //ends
                    }
                } else if (value.fieldType.equals("signed")) {
                    comp = new beans.LIntegerField(value.fieldSize, value.projectId, value.fieldName);
                    groupPane.add(0, ++row, comp, "f=H,wx=100");
                } else if (value.fieldType.equals("unsigned")) {
                    comp = new beans.LNumberField(value.fieldSize, value.projectId, value.fieldName);
                    groupPane.add(0, ++row, comp, "f=H,wx=100");
                } else {
                    Log.quit("(Coding) invalid field type");
                }

                // if there is a min or max_value, set the input verifier for this field.
                if ((value.minValue != null && !value.minValue.equals("")) || (value.maxValue != null && !value.maxValue.equals(""))) {
                    comp.setInputVerifier(MinMaxVerifier.createInstance(
                            value.minValue, value.maxValue));
                }

                // Get the position of the component in dynamicPane for use in
                // retreiving its label during document level processing in FieldMapper.
                componentCount = ((JPanel) groupPane).getComponentCount() - 1;

                comp.setName(name);
                comp.setToolTipText(tooltip);
                comp.addFocusListener(focusComponent);
                if ("QA".equals(whichStatus)) {

                    JCheckBox checkBox = new JCheckBox();
                    checkBox.setEnabled(false);
                    ((LField) comp).setCheckBox(checkBox);
                    groupPane.add(1, row, checkBox, "il=15");
                    checkBox.setToolTipText("Check indicates coder error");

                    final LComboBox errorTypeCombo = new LComboBox(1, new String[]{"M", "U", "A"});

                    ((LField) comp).setComboBox(errorTypeCombo);

                    groupPane.add(2, row, errorTypeCombo, "il=15");

                    if ("QA".equals(whichStatus) && getSamplingFields() != null && getSamplingFields().trim().length() != 0) {
                        createQAIRMenuItem.setVisible(true);
                        String[] samplefield = getSamplingFields().split(",");
                        for (String fieldname : samplefield) {
                            if (name.equalsIgnoreCase(fieldname)) {
                                theFieldMapper.add(name, (LField) comp, componentCount, /* has checkbox -> */ true);
                            }
                        }
                    } else {
                        theFieldMapper.add(name, (LField) comp, componentCount, /* has checkbox -> */ true);
                    }
                } else {                    
                     if((whichStatus.equals("Coding") || whichStatus.equals("CodingQC"))&& ("Yes".equals(value.queryraised) || "Yes".equals(value.queryanswered))){
                        if(null != theCodingData ){
                             JLabel label = new JLabel();
                            if(theCodingData.query_raised.equals("Yes")){                              
                               label.setText("F10");
                               label.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
                               label.setForeground(Color.RED);
                              groupPane.add(1, row, label, "il=15");
                            }else if(theCodingData.query_answered.equals("Yes")){           
                               label.setText("F10");
                               label.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
                               label.setForeground(Color.GREEN);
                               groupPane.add(1, row, label, "il=15");                            
                            }else{
                               label.setText("      ");
                               label.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 10));
                               groupPane.add(1, row, label, "il=15");  
                            }
                        }                    
                        theFieldMapper.add(name, (LField) comp, componentCount, false);
                    }
                    if(whichStatus.equals("ModifyErrors") && "Yes".equals(value.listing_marking) && batchSubProcess.equals("Listing")){
                        if(null != theCodingData ){
                             JLabel label = new JLabel();
                            if(theCodingData.listing_marking.equals("Yes")){                              
                              label.setText("L");
                              groupPane.add(1, row, label, "il=15");
                            }else{                                
                               label.setText("");
                               groupPane.add(1, row, label, "il=15");                            
                            }
                        }
                      
                    }
                    theFieldMapper.add(name, (LField) comp, componentCount, false);
                }
                projectMap.addComponent(comp);
                // count the fields actually placed on the screen
                fieldCount++;

                if ("Binder".equals(whichStatus)) {
                    if (comp instanceof JTextField) {
                        JTextField field = (JTextField) comp;
                        field.setEditable(false);
                    } else {
                        comp.setEnabled(false);
                    }
                } else if ("QA".equals(whichStatus)) {
                } else if (value.fieldGroup != activeGroup && !unitize) {
                    comp.setFocusable(false);
                    comp.setEnabled(false);
                } else {
                    comp.setEnabled(true);
                    if (firstFocus) {
                        // only the currently-active fields are enabled
                        comp.requestFocus();
                        firstFocus = false;
                    }
                }
            }
            if (previousGroup > 0) {
                // add last group to screen
                dynamicPane.add(0, row++, groupBorderPane, "it=12");
            }
        }
 
        if (compCount > 0
            || unitize) {            

            // Add a save&go button following all dynamic components, if there
            // are components or if the screen is in unitize mode.
            if ("Unitize".equals(whichStatus) || "UQC".equals(whichStatus)) {
                JPanel p = new JPanel();
                p.add(saveAndGoButton);
                p.add(new JLabel("  "));
                p.add(saveAndGoBoundaryButton);
                dynamicPane.add(0, ++row, p, "it=12");

            } else if(("CodingQC".equals(whichStatus) || "Masking".equals(whichStatus)) && !clearEdit.equals("Document")){                

                JPanel p = new JPanel();
                p.add(saveAndGoButton);
                p.add(editButton);
                dynamicPane.add(0, ++row, p, "it=12");
            //dynamicPane.add(0, ++row, editButton, "it=12");
            } else {
                dynamicPane.add(0, ++row, saveAndGoButton, "it=12");
            }
        }

        // empty pane to push fields to top of dynamic area
        dynamicPane.add(0, ++row, new JPanel(), "wy=100");

        dynamicPane.validate();

        ManagedTableModel model = tablevalueModel;
        model.register();

        // projectMap is final, so set it in the ibasetextfields.
        for (int i = 0; i < fieldCount; i++) {
            if (projectMap.getComponent(i) instanceof IbaseTextField) {
                ((IbaseTextField) projectMap.getComponent(i)).setProjectMap(projectMap);
            }
        }
    }

    //---------for including error types ------------------
    private String getErrorType() {
        String values[] = {"miscoded", "uncoded", "added"};
        String input = null;
        input = (String) JOptionPane.showInputDialog(
                this,
                "Select the Correction Type : ",
                "ERROR_TYPE", JOptionPane.INFORMATION_MESSAGE,
                null, values,
                "miscoded");
        if (input != null) {
            return Character.toString(input.charAt(0));
        }
        return input;
    }

    private void flag() {
        // flag1= true;        
    }

    /**
     * If the text component is defined with a spell check, create a new SpellCheckButton
     * and set it's component to the one being placed on the screen (comp).
     * @param comp - an LField text component
     * @param sc - projectfields.spell_check (Yes or No)
     */
    private JComponent addSpellCheck(JComponent comp, String sc) {
        // if this text item gets the spellcheck, create the button and
        // pass it to makeJPanel for inclusion on the screen.
        if (sc.equals("Yes")) {
            SpellCheckButton button = new SpellCheckButton();
            button.setComponent((LField) comp);
            return makeJPanel(comp, button);
        } else {
            return comp;
        }
    }

    /**
     * Open and close the pane containing group projectfields as the user
     * clicks the group button.  The button and one LGridBag are contained in
     * a JPanel, so that getting the parent of the button allows the second component,
     * the group pane, to be set not visible.
     */
    private void buttonMouseClicked(java.awt.event.MouseEvent evt) {
        JButton button = (JButton) evt.getSource();
        String group = button.getName();
        JPanel groupBorderPane = (JPanel) button.getParent();
        boolean visible = groupBorderPane.getComponent(1).isVisible();
        groupBorderPane.getComponent(1).setVisible(!visible);
        if (visible) {
            button.setText("+   Group " + group);
        } else {
            button.setText("-   Group " + group);
        }
    }

    /**
     * Create a button with the properties required of the group separator and
     * add a listener that will open or close the group pane when clicked.
     * @param group - projectfields.field_group
     * @return a new JButton for the group
     */
    private JButton getButton(int group) {
        JButton button = new JButton("-   Group " + group);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(240, 18));
        button.setBackground(new Color(57, 70, 125));
        button.setForeground(Color.white);
        button.setName(Integer.toString(group));
        button.setFocusable(false);
        button.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttonMouseClicked(evt);
            }
        });
        return button;
    }

    /**
     * Register managedTableModels for tablevalue and tablespec.
     */
    private void loadTableValues() {
        //
        // select TV.tablevalue_id, TV.value, TS.table_name, TV.field_level                                       
        // from tablevalue TV
        //   left join tablespec TS using (tablespec_id)
        // where TS.project_id = ?
        //
        // Sort on column 1, tablevalue.table_name; 0, tablevalue.value
        Log.print("(SPV.loadTableValues) projectId " + projectId);
        tablevalueModel = new ManagedTableSorter(1, 0,
                SQLManagedTableModel.makeInstance("SplitPaneViewer.tablevalue", projectId));
        ManagedTableModel model;
        //ManagedTableModel model = tablevalueModel;
        //model.register();

        // select TS.tablespec_id, TS.table_name, TS.table_type, TS.updateable, TS.project_id
        // from tablespec TS
        // where TS.project_id in (?, 0)
        //
        // Sort on column 0, tablespec.table_name (need sort?)
        tablespecModel = new ManagedTableSorter(0,
                SQLManagedTableModel.makeInstance("SplitPaneViewer.tablespec", projectId));
        model = tablespecModel;
        model.register();
    }

    // make a JPanel with 2 given components
    // first component is in center (for greedy layout)
    private JPanel makeJPanel(JComponent comp1, JComponent comp2) {
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
        pane.add(comp1, BorderLayout.CENTER);
        if (comp2 != null) {
            pane.add(comp2, BorderLayout.EAST);
        }
        return pane;
    }

    // create an IbaseTextField with given attributes
    private beans.IbaseTextField makeIbaseTextField(String type, final int tablespecId, String name, String mandatory, String repeated, String is_level_field, String mask, String validChars, String invalidChars, int projectId, String fieldName, String documentNumber, String batesNumber, String whichStatus, String imagepath, int childId, int batchId, CodingData theCodingData) {
        //Log.print("(Coding).makeIbaseTextField type/name "
        //          + type + "/" + name);
        System.out.println("(Coding).makeIbaseTextField type/name " + type + "/" + name);
        int project_Id = projectId;
        //int tablespecId = 0;
        String tableName = "";

        // get current type and updateable parameters to use in IbaseTextField
        String updateable = "";
        for (int i = 0; i < tablespecModel.getRowCount(); i++) {
            if (tablespecModel.getRowId(i) == tablespecId) {
                updateable = (String) tablespecModel.getValueAt(i, 2);
                projectId = Integer.parseInt((String) tablespecModel.getValueAt(i, 3));
                tableName = (String) tablespecModel.getValueAt(i, 0);
            //tablespecId = tablespecModel.getRowId(i);
            }
        }
        //Log.print("(SPV.makeITF) updateable: " + updateable + "/" + projectId
        //          + "/" + tablespecId);

        if (is_level_field.equals("No")) {    
             
            area = new beans.IbaseTextField(type, tablespecId
                        , ((mandatory.equals("Yes")) ? true : false)
                        , ((updateable.equals("CoderAdd")) ? true : false)
                        , ((repeated.equals("Yes")) ? true : false)
                        , mask, validChars, invalidChars,project_Id,fieldName,documentNumber,batesNumber,whichStatus,imagepath,childId,projectMap,batchId,theCodingData);
            
        } else {   
            

            area = new beans.IbaseTextField(type, tablespecId, ((mandatory.equals("Yes")) ? true : false), ((updateable.equals("CoderAdd")) ? true : false), ((repeated.equals("Yes")) ? true : false), theFieldMapper, projectMap, mask, validChars, invalidChars, project_Id, fieldName, documentNumber, batesNumber, whichStatus, imagepath, childId, batchId, theCodingData);
        }
        if (projectId == 0) {
            // ibaseTextField uses a global table -- load it by table_name
            ManagedTableSorter mts = new ManagedTableSorter(1, 0,
                    SQLManagedTableModel.makeInstance("SplitPaneViewer.tablevalueGlobal", tablespecId));
            area.setProjectModel(mts);
            mts.register();
        } else {
            // A tablevalue model has been loaded for all tables specific to the
            // current project; filter the already-loaded tablevalues by table_name.
            ManagedTableFilter filter = new ManagedTableFilter(tablevalueModel) {

                public boolean accept(TableRow theRow) {
                    if (tablespecId == Integer.parseInt((String) theRow.getValue(1))) {
                        return true;
                    }
                    return false;
                }
            };
            Log.print("(SPV.accept) filter columns " + filter.getColumnCount());
            area.setProjectModel(filter);
        }
        area.setColumns(25);
        area.getTextField().setName(name);
        area.getTextField().addFocusListener(focusComponent);

        // add the undo listener
        getUndoController().registerDocument(((JTextField) area.getTextField()).getDocument(), (JTextField) area.getTextField());
        //Log.print("(SPV.makeIbaseTextField) is_level_field(" + is_level_field + ")");
        return area;
    }

    private javax.swing.JFormattedTextField getJFormattedDateField() {
        try {
            beans.LFormattedTextField ftf = new beans.LFormattedTextField("yyyy-MM-dd");
            // add the undo listener
            getUndoController().registerDocument(ftf.getDocument(), (JTextField) ftf);
            return ftf;
        } catch (Throwable e) {
            Log.quit(e);
        }
        return null;
    }

    // not currently used
    //private javax.swing.JFormattedTextField getJFormattedTextField(String mask) {
    //    try {
    //        beans.LFormattedTextField ftf = new beans.LFormattedTextField(mask);
    //        // add the undo listener
    //        getUndoController().registerDocument( ftf.getDocument(), (JTextField)ftf);
    //        return ftf;
    //    } catch (Throwable e) {
    //        Log.quit(e);
    //    }
    //    return null;
    //}

    // following is vestige of former loadScreenFields()
    private void createScreenButtonAndIcon() {

        // Define the save button for the coding screen and add a listener.
        //saveAndGoButton = new javax.swing.JButton("Save & Go");
        //saveAndGoButton.setFocusable(true);
        //getRootPane().setDefaultButton(saveAndGoButton);
        //saveAndGoButton.setAction(saveAction);
        //saveAndGoButton.setToolTipText("Save, then go to next document");
        //saveAndGoButton.addFocusListener(focusComponent); // Scroll button into view, if off screen.

        // set the frame's icon
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image iconImage;
        iconImage = toolkit.getImage("images/ibase8-32.gif");
        setIconImage(iconImage);
    }

    private class BoundaryDocument extends PlainDocument {

        public void insertString(int offs, String str, AttributeSet a)
                throws BadLocationException {
            //Log.print("(SPV.BoundaryDocument) insertString /" + str + "/");
            if (offs > 0 && this.getLength() > 0) {
                // Refuse the insertion and beep to notify user
                Toolkit.getDefaultToolkit().beep();
                Log.print("BEEP> LTextField: insertString(" + offs + ",'" + str + "') " + this.getLength());
            } else if (str.equals(" ")) {
                super.insertString(offs, str, a);
                return;
            } else {
                String old = boundaryText.getText();
                super.insertString(offs, str, a);
                if (!old.equals(str)) {
                    boundaryTextChange(old, str);
                }
            }
        }

        public void remove(int offs, int len)
                throws BadLocationException {
            //Log.print("(SPV.BoundaryDocument) remove");
            if (!fromServer && !fromBoundaryActionListener) {
                originalPageId = theCodingData.pageId;
            }
            super.remove(offs, len);
        }
    }
    /**
     * Listens for the delete key, backspace or space and, since the
     * user entered the data, processes the keystroke.  This, as opposed to
     * the BoundaryDocument, above, which can have a remove() invoked as a result
     * of the boundaryText field being selected and the user entering a code.
     */
    ActionListener boundaryActionListener = new ActionListener() {

        public void actionPerformed(ActionEvent actionEvent) {
            //Log.print("(SPV.boundaryActionListener)");
            if (!isBoundary(theCodingData, FIRST_PAGE_OF_BATCH)) {
                String old = boundaryText.getText();
                boundaryTextChange(old, "");
            }
            fromBoundaryActionListener = true;
            boundaryText.setText("");
            fromBoundaryActionListener = false;
        }
    };

    /**
     * The boundaryText field has changed, so change the coding data on the screen
     * to reflect the new status of the page.  
     * <p>
     * Any character marks the first page of a child.
     * "D" marks the first page of a range (and, therefore, begins a child).
     * </p>
     * @param old - the data in boundaryText before the change
     * @param str - the data the user entered in boundaryText
     */
    private void boundaryTextChange(String old, String str) {
        //Log.print("(SPV.boundaryTextChange) old/new " + old + "/" + str);
        if (fromServer) {
            // going to next child, so don't need to update data
            //Log.print("(SPV.boundaryTextChange) fromServer");
            return;
        }
        if (!checkFirstPageOfVolume(str)) {
            return;
        }
        if (old.trim().equals("") && str.trim().equals("")) {
            // no change
            return;
        }
        if (originalPageId > 0 && !str.trim().equals("")) {
            getBoundaryCoderData(theCodingData.pageId, ACTUAL, B_NONE);
            originalPageId = 0;
            return;
        }
        if (!old.trim().equals("") && !str.trim().equals("")) {
            // still a child of some sort -- no change to data on screen
            return;
        }
        // was a child or range
        originalPageId = theCodingData.pageId;
        if (theFieldMapper.size() > 0) {
            if (str.trim().equals("")) {
                // child or range cleared, so it now belongs to the
                // previous child or range -- get previous data
                // get the coding data values for the previous page
                if (theImageData.childImagePosition > 1) {
                    // The page of the cleared boundary is within the current child.
                    getBoundaryCoderData(theCodingData.pageId, ACTUAL, B_NONE);
                    originalPageId = 0;
                } else {
                    getPageValues(theCodingData.pageId, MINUS_ONE);
                }
            } else {
                // child or range being added -- provide empty fields
                // checkboxes enabled, but default to not user error
                theFieldMapper.populateScreen((Map) null, projectMap, 0, "", null);
                theFieldMapper.clearScreenChanged();
            // Don't need to do this, because it's unitize only
            //theFieldMapper.setCheckboxUnselectedEnabled(true);
            }
        }
        //Log.print("(SPV.boundaryTextChange) is changed");
        boundaryText.setChanged(true);
    }

    private boolean checkFirstPageOfVolume(String str) {
        if (isBoundary(theCodingData, FIRST_PAGE_OF_VOLUME) && (str.trim().equals("") || str.equalsIgnoreCase("C"))) {
            // boundary must be document level for first page of region
            Toolkit.getDefaultToolkit().beep();
            Log.print("BEEP> SplitPaneViewer.crossFieldEdit");
            Object[] options = {"Ok"};
            int response = JOptionPane.showOptionDialog(this,
                    "The first page of a volume must begin a document range.\n" + "Please enter a 'D' or another document code.",
                    "Unitizing Error",
                    JOptionPane.OK_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,
                    options,
                    options[0]);
            return false;
        }
        return true;
    }

    /**
     * Exit the Application.
     * Shut down image and server connections, then exit the VM.
     */
    private void exitApplication() {
        //Log.print("@@@SplitPaneViewer.exitApplication");
        try {
            LocalProperties.storeFile();
        } catch (Exception e) {
        }
//        try {
//            synchronized (ImageThread.class) {
//                if (Global.theImageConnection != null) {
//                    Global.theImageConnection.shutdown();
//                }
//            }
//        } catch (Exception e) {
//        }
        try {
            //client.Global.theServerConnection.shutdown();
            final ClientTask task;
            task = new TaskGoodbye();
            task.enqueue(this);
        } catch (Exception e) {
        }
        try {
            LocalProperties.storeFile();
        } catch (Exception e) {
        }
        System.exit(0);
    }

    public void exitForm(java.awt.event.WindowEvent evt) {
        //Log.print("@@@SplitPaneViewer.exitForm");
        if (Global.mainWindow == this) {
            // Client's main window, just exit.
            exitApplication();
        } else {
            // Admin browse or binder, just close this viewer.
            closeViewer();
        }
    }

    /**
     * Do the cross-field editing.
     */
    protected boolean crossFieldEdit() {
        return crossFieldEdit(false);
    }

    /**
     * Do the cross-field editing.
     * @param force If true, continue with save even if edits fail.
     */
    protected boolean crossFieldEdit(boolean force) {
        if (projectEditor == null) {
            return false;
        }

        if (unitize) {
            // nothing is required when unitizing
            if (isBoundary(theCodingData, FIRST_PAGE_OF_VOLUME) && (boundaryText.getText().trim().equals("") || boundaryText.getText().equalsIgnoreCase("C"))) {
                // boundary must be document level for first page of region
                Toolkit.getDefaultToolkit().beep();
                Log.print("BEEP> SplitPaneViewer.crossFieldEdit");
                Object[] options = {"Ok"};
                int response = JOptionPane.showOptionDialog(this,
                        "The first page of a Volume must be a document.\n" + "Please enter a 'D' or another document code before saving",
                        "Unitizing Error",
                        JOptionPane.OK_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null,
                        options,
                        options[0]);
                return false; // unitize fields don't have names

            }
            if (isBoundary(theCodingData, FIRST_PAGE_OF_BATCH) && (boundaryText.getText().trim().equals(""))) //|| boundaryText.getText().equalsIgnoreCase("C")))
            {
                // boundary must be document level for first page of region
                Toolkit.getDefaultToolkit().beep();
                Log.print("BEEP> SplitPaneViewer.crossFieldEdit");
                Object[] options = {"Ok"};
                int response = JOptionPane.showOptionDialog(this,
                        "The first page of a Batch must be a document.\n" + "Please enter a 'C' or 'D' or another document code before saving",
                        "Unitizing Error",
                        JOptionPane.OK_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null,
                        options,
                        options[0]);
                return false; // unitize fields don't have names

            }
        //return true; // no errors
        }

        // Edit the fields.  populateRow produces a map with the values
        // from the screen.  If force is true, edit will allow saving
        // to continue (with user OK) if there are errors.
        // TBD:  Note that we call populateRow here and again when saving.
        String error = projectEditor.edit(projectMap, tablevalueModel,
                theFieldMapper.populateRow(), force, unitize,
                unitize ? 0 : activeGroup, treatmentLevel); // don't use groups when unitizing

        //if (error != null) {
        //Log.print("(Coding)crossFieldEdit error returned on " + error);
        //}

        if (error != null && !error.equals("")) {
            LField comp = (LField) projectMap.getComponent(error);
            if (comp instanceof IbaseTextField && !comp.getText().equals("")) {
                //Log.print("(Coding)crossFieldEdit error value " + comp.getText());
                comp.requestFocus();
                ((IbaseTextField) comp).checkMandatory(null, comp.getText());
            //} else {
            //Log.print("(Coding)crossFieldEdit request Focus on " + error);
            //comp.requestFocus();
            }
            return false;
        }
        String validationError = projectEditor.edit(projectMap, theFieldMapper.populateRow(), validationData, unitize, force, treatmentLevel);

        if (validationError != null && !validationError.equals("")) {
            LField comp = (LField) projectMap.getComponent(error);
            if (comp instanceof IbaseTextField && !comp.getText().equals("")) {
                //Log.print("(Coding)crossFieldEdit error value " + comp.getText());
                comp.requestFocus();
                ((IbaseTextField) comp).checkMandatory(null, comp.getText());
            //} else {
            //Log.print("(Coding)crossFieldEdit request Focus on " + error);
            //comp.requestFocus();
            }
            return false;
        }
        return true;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {
        resButtonGroup = new javax.swing.ButtonGroup();
        codingButtonGroup = new javax.swing.ButtonGroup();
        saveAndGoButton = new javax.swing.JButton();
        saveAndGoButton.addFocusListener(focusComponent);
        saveAndGoBoundaryButton = new javax.swing.JButton();
        saveAndGoButton.addFocusListener(focusComponent);

        editButton = new javax.swing.JButton();
        editButton.addFocusListener(focusComponent);

        coderPanel = new javax.swing.JPanel();
        splitpane = new javax.swing.JSplitPane();
        theCoderPane = new javax.swing.JPanel();
        coderToolBar = new javax.swing.JToolBar();
        docFirstButton = new javax.swing.JButton();
        docPrevButton = new javax.swing.JButton();
        docNextButton = new javax.swing.JButton();
        docLastButton = new javax.swing.JButton();
        absoluteButton = new javax.swing.JButton();
        firstNewButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        addBatchCommentButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        mailButton = new javax.swing.JButton();
        coderPane = new javax.swing.JPanel();
        topScrollPane = new javax.swing.JScrollPane();
        topPane = new javax.swing.JPanel();
        binderLabel = new javax.swing.JLabel();
        docPane = new javax.swing.JPanel();
        hyphen11 = new javax.swing.JLabel();
        beginDocButton = new javax.swing.JButton();
        hyphen1 = new javax.swing.JLabel();
        endDocButton = new javax.swing.JButton();
        hyphen12 = new javax.swing.JLabel();
        attachPane = new javax.swing.JPanel();
        hyphen111 = new javax.swing.JLabel();
        beginAttachButton = new javax.swing.JButton();
        hyphen2 = new javax.swing.JLabel();
        endAttachButton = new javax.swing.JButton();
        hyphen112 = new javax.swing.JLabel();
        unitizingPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        uPagePrevButton = new javax.swing.JButton();
        uPageNextButton = new javax.swing.JButton();
        boundaryPane = new javax.swing.JPanel();
        boundaryLabel = new javax.swing.JLabel();
        boundaryText = new beans.LTextField();
        boundaryBatesLabel = new javax.swing.JLabel();
        absolutePane = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        absoluteGoButton = new javax.swing.JButton();
        absolutePage = new beans.LIntegerField();
        issueButton = new javax.swing.JButton();
        midScrollPane = new javax.swing.JScrollPane();
        dynamicPane = new beans.LGridBag();
        statusPane = new javax.swing.JPanel();
        statusLabel = new javax.swing.JLabel();
        theViewerPane = new javax.swing.JPanel();
        viewerToolBar = new javax.swing.JToolBar();
        vPagePrevButton = new javax.swing.JButton();
        vPageNextButton = new javax.swing.JButton();
        vDocPrevButton = new javax.swing.JButton();
        vDocNextButton = new javax.swing.JButton();
        vAttachPrevButton = new javax.swing.JButton();
        vAttachNextButton = new javax.swing.JButton();
        vFirstCurrButton = new javax.swing.JButton();
        vLastCurrButton = new javax.swing.JButton();
        vAbsoluteButton = new javax.swing.JButton();
        vBeginImageRunButton = new javax.swing.JButton();
        vHorizButton = new javax.swing.JButton();
        vVertButton = new javax.swing.JButton();
        vScreenButton = new javax.swing.JButton();
        vTurn90ClockwiseButton = new javax.swing.JButton();
        vTurn90CounterwiseButton1 = new javax.swing.JButton();
        vZoomWindowButton = new javax.swing.JButton();
        vZoomInButton = new javax.swing.JButton();
        vZoomOutButton = new javax.swing.JButton();
        viewerPane = new javax.swing.JPanel();
        viewerLabel = new javax.swing.JLabel();
        theViewer = new javax.swing.JPanel();
        coderMenu = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        sampleMenuItem = new javax.swing.JMenuItem();
        validateBatchMenuItem = new javax.swing.JMenuItem();
        closeBatchMenuItem = new javax.swing.JMenuItem();
        rejectBatchMenuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        forceSaveMenuItem = new javax.swing.JMenuItem();
        openBinderMenuItem = new javax.swing.JMenuItem();
        addToBinderItem = new javax.swing.JMenuItem();
        createQAIRMenuItem = new javax.swing.JMenuItem();
        removeFromBinderItem = new javax.swing.JMenuItem();
        jSeparator61 = new javax.swing.JSeparator();
        closeMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        copyCurrentMenuItem = new javax.swing.JMenuItem();
        copyAllMenuItem = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JSeparator();
        splitMenuItem = new javax.swing.JMenuItem();
        unsplitMenuItem = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JSeparator();
        boundaryMenu = new javax.swing.JMenu();
        documentMenuItem = new javax.swing.JMenuItem();
        childMenuItem = new javax.swing.JMenuItem();
        clearMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        highResButton = new javax.swing.JRadioButtonMenuItem();
        mediumResButton = new javax.swing.JRadioButtonMenuItem();
        lowResButton = new javax.swing.JRadioButtonMenuItem();
        draftResButton = new javax.swing.JRadioButtonMenuItem();
        jSeparator62 = new javax.swing.JSeparator();
        codingLeftButton = new javax.swing.JRadioButtonMenuItem();
        codingRightButton = new javax.swing.JRadioButtonMenuItem();
        codingUndockedButton = new javax.swing.JRadioButtonMenuItem();
        reportMenu = new javax.swing.JMenu();
        qcCoderErrorReportItem = new javax.swing.JMenuItem();
        qcProjectSummaryItem = new javax.swing.JMenuItem();
        qcProjectReportMenu = new javax.swing.JMenu();
        projectByBatchItem = new javax.swing.JMenuItem();
        projectByVolumeItem = new javax.swing.JMenuItem();
        projectByStatusItem = new javax.swing.JMenuItem();
        projectByVolumeAndStatusItem = new javax.swing.JMenuItem();
        qcBatchOpenReportMenu = new javax.swing.JMenu();
        openByBatchItem = new javax.swing.JMenuItem();
        openByCoderItem = new javax.swing.JMenuItem();
        qcBatchQCReportMenu = new javax.swing.JMenu();
        qcByBatchItem = new javax.swing.JMenuItem();
        qcByCoderItem = new javax.swing.JMenuItem();
        payrollDetailReportMenu = new javax.swing.JMenu();
        payrollDetailTeamMenu = new javax.swing.JMenu();
        payrollDetailTeamItem = new javax.swing.JMenuItem();
        payrollDetailTeamItem1 = new javax.swing.JMenuItem();
        payrollDetailTeamItem2 = new javax.swing.JMenuItem();
        payrollDetailTeamItem3 = new javax.swing.JMenuItem();
        payrollDetailSelfMenu = new javax.swing.JMenu();
        payrollDetailSelfItem = new javax.swing.JMenuItem();
        payrollDetailSelfItem1 = new javax.swing.JMenuItem();
        payrollDetailSelfItem2 = new javax.swing.JMenuItem();
        payrollDetailSelfItem3 = new javax.swing.JMenuItem();
        payrollSummaryReportMenu = new javax.swing.JMenu();
        payrollSummaryTeamMenu = new javax.swing.JMenu();
        payrollSummaryTeamItem = new javax.swing.JMenuItem();
        payrollSummaryTeamItem1 = new javax.swing.JMenuItem();
        payrollSummaryTeamItem2 = new javax.swing.JMenuItem();
        payrollSummaryTeamItem3 = new javax.swing.JMenuItem();
        payrollSummarySelfMenu = new javax.swing.JMenu();
        payrollSummarySelfItem = new javax.swing.JMenuItem();
        payrollSummarySelfItem1 = new javax.swing.JMenuItem();
        payrollSummarySelfItem2 = new javax.swing.JMenuItem();
        payrollSummarySelfItem3 = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        AboutMenuItem = new javax.swing.JMenuItem();

        //------------->
        fieldsPane = new javax.swing.JPanel();
        fieldsScrollPane = new javax.swing.JScrollPane();
        fieldsTable = new ProjectFieldsTableClass();
        bottomPane = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        viewButton = new javax.swing.JButton();
        backButton = new javax.swing.JButton();
        reportButton = new javax.swing.JButton();
        fieldComoBox = new javax.swing.JComboBox();
        //errorComoBox = new javax.swing.JComboBox(30, statusString1);
        errorComoBox = new javax.swing.JComboBox(statusString1);

        jLabel11 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();

        markingTable = new ProjectMarkingTableClass();
        markingPane = new javax.swing.JPanel();
        markingScrollPane = new javax.swing.JScrollPane();
        sweeppane1 = new javax.swing.JPanel();
        sweeppane2 = new javax.swing.JPanel();
        errorTypePane = new javax.swing.JPanel();
        oldValueLabel = new javax.swing.JLabel();
        sweepButton = new javax.swing.JButton();
        sweepAllButton = new javax.swing.JButton();
        oldValuetextfield = new javax.swing.JTextField();
        newValueLabel = new javax.swing.JLabel();
        back2Button = new javax.swing.JButton();
//	     newValuetextfield= new javax.swing.JTextField(); 
        newValuetextfield = new LTextField();
        occurrence = new javax.swing.JPanel();
        marking = new javax.swing.JPanel();
        sweep = new javax.swing.JPanel();
        assignListingQCMenuItem = new javax.swing.JMenuItem();
        assignTallyQCMenuItem = new javax.swing.JMenuItem();
        closeVolume = new javax.swing.JMenuItem();
        listingQCDone = new javax.swing.JMenuItem();
        tallyQCDone = new javax.swing.JMenuItem();
        count = new javax.swing.JMenuItem();
        tagCount = new javax.swing.JMenuItem();


        saveAndGoButton.setAction(saveAction);
        saveAndGoButton.setText("Save & Go");
        saveAndGoButton.setToolTipText("Save, then go to next document.");
        saveAndGoBoundaryButton.setAction(savePageAction);
        saveAndGoBoundaryButton.setText("Save & Go To Page");
        saveAndGoBoundaryButton.setToolTipText("Save, then go to next page.");
        editButton.setAction(editAction);
        editButton.setText("Edit");


        dynamicPane.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(1, 20, 1, 20)));
        dynamicPane.setFocusCycleRoot(true);
        dynamicPane.setFocusTraversalPolicy(new SkipCheckboxFocusTraversalPolicy());
        dynamicPane.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dynamicPaneMouseClicked(evt);
            }
        });

        setFocusable(false);
        setName("splitPaneViewer");
        addComponentListener(new java.awt.event.ComponentAdapter() {

            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }

            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addFocusListener(new java.awt.event.FocusAdapter() {

            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        coderPanel.setLayout(new java.awt.BorderLayout());

        coderPanel.setFocusable(false);
        coderPanel.setName("splitPaneViewer_jPanel1");
        coderPanel.setPreferredSize(new java.awt.Dimension(850, 600));
        coderPanel.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                coderPanelMouseClicked(evt);
            }
        });

        splitpane.setDividerLocation(310);
        splitpane.setContinuousLayout(true);
        splitpane.setFocusable(false);
        splitpane.setName("splitpane");
        splitpane.setPreferredSize(new java.awt.Dimension(850, 700));
        splitpane.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                splitpaneMouseClicked(evt);
            }
        });
        splitpane.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                splitpanePropertyChange(evt);
            }
        });

        theCoderPane.setLayout(new java.awt.BorderLayout());

        theCoderPane.setFocusable(false);
        theCoderPane.setMinimumSize(new java.awt.Dimension(236, 76));
        coderToolBar.setBorder(null);
        coderToolBar.setFocusable(false);
        coderToolBar.setBorderPainted(false);
        coderToolBar.setPreferredSize(new java.awt.Dimension(235, 32));
        docFirstButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/docFirst.gif")));
        docFirstButton.setMnemonic('1');
        docFirstButton.setToolTipText("First Document");
        docFirstButton.setFocusable(false);
        docFirstButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                docFirstButtonActionPerformed(evt);
            }
        });

        coderToolBar.add(docFirstButton);

        docPrevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/docPrev.gif")));
        docPrevButton.setMnemonic('2');
        docPrevButton.setToolTipText("Previous Document");
        docPrevButton.setFocusable(false);
        docPrevButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                docPrevButtonActionPerformed(evt);
            }
        });

        coderToolBar.add(docPrevButton);

        docNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/docNext.gif")));
        docNextButton.setMnemonic('3');
        docNextButton.setToolTipText("Next Document");
        docNextButton.setFocusable(false);
        docNextButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                docNextButtonActionPerformed(evt);
            }
        });

        coderToolBar.add(docNextButton);

        docLastButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/docLast.gif")));
        docLastButton.setMnemonic('4');
        docLastButton.setToolTipText("Last Document");
        docLastButton.setFocusable(false);
        docLastButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                docLastButtonActionPerformed(evt);
            }
        });

        coderToolBar.add(docLastButton);

        coderToolBar.addSeparator();
        absoluteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/filview3.gif")));
        absoluteButton.setToolTipText("Select Document by Number");
        absoluteButton.setFocusable(false);
        absoluteButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                absoluteButtonActionPerformed(evt);
            }
        });

        coderToolBar.add(absoluteButton);

        firstNewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/firstNew.gif")));
        firstNewButton.setToolTipText("First Untouched Document");
        firstNewButton.setFocusable(false);
        firstNewButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstNewButtonActionPerformed(evt);
            }
        });

        coderToolBar.add(firstNewButton);

        coderToolBar.addSeparator();
        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.gif")));
        saveButton.setMnemonic('S');
        saveButton.setToolTipText("Save Coded Data");
        saveButton.setFocusable(false);
        saveButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });

        coderToolBar.add(saveButton);

        coderToolBar.addSeparator();
        addBatchCommentButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/addBatchComment.gif")));
        addBatchCommentButton.setToolTipText("Add/View Batch Comments");
        addBatchCommentButton.setFocusable(false);
        addBatchCommentButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBatchCommentButtonActionPerformed(evt);
            }
        });

        coderToolBar.add(addBatchCommentButton);

        coderToolBar.add(jSeparator1);

        mailButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/checkmail.gif")));
        mailButton.setToolTipText("Mail");
        mailButton.setPreferredSize(new java.awt.Dimension(28, 28));
        mailButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mailButtonActionPerformed(evt);
            }
        });

        coderToolBar.add(mailButton);

        theCoderPane.add(coderToolBar, java.awt.BorderLayout.SOUTH);
        //---------->
        occurrence.setLayout(new java.awt.BorderLayout());

        jPanel22.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        jLabel11.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel11.setText("Select Field:");
        jPanel22.add(jLabel11);

        fieldComoBox.setPreferredSize(new java.awt.Dimension(150, 25));
        fieldComoBox.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldComoBoxActionPerformed(evt);
            }
        });
        jPanel22.add(fieldComoBox);
        occurrence.add(jPanel22, java.awt.BorderLayout.PAGE_START);
        //  theCoderPane.add(jPanel22,java.awt.BorderLayout.PAGE_START); 

        fieldsPane.setLayout(new java.awt.BorderLayout());
        fieldsPane.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 50)), "Occurrence", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 2), new java.awt.Color(0, 0, 100)));
        fieldsPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        //fieldsPane.setPreferredSize(new java.awt.Dimension(375, 190));
        fieldsScrollPane.setPreferredSize(new java.awt.Dimension(375, 200));
        fieldsTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        if (whichStatus.equals("Listing")) {
            fieldsTable.setModel(new javax.swing.table.DefaultTableModel(
                    new Object[][]{},
                    new String[]{
                        "Field Value", "Occurrence", "Mark"
                    }) {

                boolean[] canEdit = new boolean[]{
                    false, false, false
                };

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
        } else {
            fieldsTable.setModel(new javax.swing.table.DefaultTableModel(
                    new Object[][]{},
                    new String[]{
                        "Field Value", "Occurrence"
                    }) {

                boolean[] canEdit = new boolean[]{
                    false, false, false
                };

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
        }


        fieldsTable.setFocusable(false);
        fieldsTable.setIntercellSpacing(new java.awt.Dimension(2, 1));
        fieldsTable.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fieldsTableMouseClicked(evt);
            }
        });

        fieldsScrollPane.setViewportView(fieldsTable);

        fieldsPane.add(fieldsScrollPane, java.awt.BorderLayout.CENTER);

        //add(fieldsPane, java.awt.BorderLayout.CENTER);
        occurrence.add(fieldsPane, java.awt.BorderLayout.AFTER_LINE_ENDS);
        // theCoderPane.add(fieldsPane, java.awt.BorderLayout.AFTER_LINE_ENDS);

        //jPanel1.setLayout(new java.awt.GridLayout(1, 0, 20, 0));
        jPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));
        viewButton.setText(" View ");
        viewButton.setEnabled(false);
        viewButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                viewButtonActionPerformed(evt);
            }
        });

        jPanel1.add(viewButton);
        backButton.setText(" More Fields ");
        backButton.setEnabled(true);
        backButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backButtonActionPerformed(evt);
            }
        });
        jPanel1.add(backButton);
        reportButton.setText(" Save ");
        reportButton.setEnabled(false);
        reportButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportButtonActionPerformed(evt);
            }
        });
        jPanel1.add(reportButton);
        bottomPane.add(jPanel1);
        // add(bottomPane, java.awt.BorderLayout.SOUTH);
        //theCoderPane.add(bottomPane, java.awt.BorderLayout.PAGE_END);
        occurrence.add(bottomPane, java.awt.BorderLayout.PAGE_END);

        //changed bala
        //theCoderPane.add(occurrence, java.awt.BorderLayout.CENTER);
        theCoderPane.add(occurrence, java.awt.BorderLayout.PAGE_START);

        marking.setLayout(new java.awt.BorderLayout());

        markingPane.setLayout(new java.awt.BorderLayout());
        markingPane.setBorder(new javax.swing.border.TitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 50)), "View Detail", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 2), new java.awt.Color(0, 0, 50)));
        markingPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        //markingPane.setPreferredSize(new java.awt.Dimension(375, 190));
        markingScrollPane.setPreferredSize(new java.awt.Dimension(375, 200));
        markingTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        if (whichStatus.equals("Listing")) {
            
	        markingTable.setModel(new javax.swing.table.DefaultTableModel(
	            new Object [][] {
	
	            },
	               
	            new String [] {
                   "Start Bate","End Bate","Field Value","Select","Mark"
	            }
	        ) {
	            boolean[] canEdit = new boolean [] {
	                 false,false,false,false,true
	            };
	
                @Override
	            public boolean isCellEditable(int rowIndex, int columnIndex) {
	                return canEdit [columnIndex];
	            }
	        });
        } else if (whichStatus.equals("TallyQC")) {
            System.out.println("22222222222222222222222");
            markingTable.setModel(new javax.swing.table.DefaultTableModel(
                    new Object[][]{},
                    new String[]{
                        "Start Bate", "End Bate", "Field Value", "Correction Data", "Correction Type"
                    }) {

                boolean[] canEdit = new boolean[]{
                    false, false, false, false, false
                };

                public boolean isCellEditable(int rowIndex, int columnIndex) {
                    return canEdit[columnIndex];
                }
            });
        }

        markingTable.setFocusable(false);
        markingTable.setIntercellSpacing(new java.awt.Dimension(2, 1));
        markingTable.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                markingTableMouseClicked(evt);
            }
        });

        markingScrollPane.setViewportView(markingTable);

        markingPane.add(markingScrollPane, java.awt.BorderLayout.CENTER);

        //add(fieldsPane, java.awt.BorderLayout.CENTER);

        // theCoderPane.add(markingPane, java.awt.BorderLayout.LINE_START);
        marking.add(markingPane, java.awt.BorderLayout.PAGE_START);



//  contains new value text field and label

//**************************** New Value panel *********************************

        JPanel newValuePanel = new javax.swing.JPanel();
//newValuetextfield = new javax.swing.JTextField();
        newValueLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jPanel1.setName("jPanel1"); // NOI18N

        newValuePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        newValuePanel.setName("labelTextPanel"); // NOI18N

        newValuetextfield.setText(""); // NOI18N

        newValuetextfield.setName("newValuetextfield"); // NOI18N

        newValueLabel.setText("New Value"); // NOI18N

        newValueLabel.setName("newValueLabel"); // NOI18N

        javax.swing.GroupLayout labelTextPanelLayout = new javax.swing.GroupLayout(newValuePanel);
        newValuePanel.setLayout(labelTextPanelLayout);
        labelTextPanelLayout.setHorizontalGroup(
                labelTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, labelTextPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(newValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newValuetextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap()));
        labelTextPanelLayout.setVerticalGroup(
                labelTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(labelTextPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(newValuetextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(newValueLabel)));

//*********************** Old Value Panel***********************************************************

        JPanel oldValuePanel = new javax.swing.JPanel();
        oldValuetextfield = new javax.swing.JTextField();
        oldValueLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setName("Form"); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N

        oldValuePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        oldValuePanel.setName("oldValuePanel"); // NOI18N

        oldValuetextfield.setText(""); // NOI18N

        oldValuetextfield.setName("oldValuetextfield"); // NOI18N

        oldValuetextfield.setEditable(false);

        oldValueLabel.setText("Old Value"); // NOI18N

        oldValueLabel.setName("oldValueLabel"); // NOI18N

        javax.swing.GroupLayout oldValuePanelLayout = new javax.swing.GroupLayout(oldValuePanel);
        oldValuePanel.setLayout(oldValuePanelLayout);
        oldValuePanelLayout.setHorizontalGroup(
                oldValuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, oldValuePanelLayout.createSequentialGroup().addContainerGap().addComponent(oldValueLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(oldValuetextfield, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()));
        oldValuePanelLayout.setVerticalGroup(
                oldValuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(oldValuePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(oldValuetextfield, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(oldValueLabel)));

//**************************** error Type Panel**********************

        errorTypePane = new javax.swing.JPanel();
        errorLabel = new javax.swing.JLabel();
        errorComoBox = new javax.swing.JComboBox();

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setName("jPanel1"); // NOI18N

        errorTypePane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        errorTypePane.setName("errorTypePane"); // NOI18N

        errorLabel.setText("Error Type"); // NOI18N

        errorLabel.setName("errorLabel"); // NOI18N

        errorComoBox.setModel(new javax.swing.DefaultComboBoxModel(new String[]{"Miscoded", "Uncoded", "Added"}));
        errorComoBox.setName("errorComoBox"); // NOI18N

        javax.swing.GroupLayout errorTypePaneLayout = new javax.swing.GroupLayout(errorTypePane);
        errorTypePane.setLayout(errorTypePaneLayout);
        errorTypePaneLayout.setHorizontalGroup(
                errorTypePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, errorTypePaneLayout.createSequentialGroup().addContainerGap().addComponent(errorLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(errorComoBox, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)));
        errorTypePaneLayout.setVerticalGroup(
                errorTypePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(errorTypePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(errorLabel).addComponent(errorComoBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)));

        sweep.setLayout(new java.awt.BorderLayout());
        //add panels
        sweeppane1.setLayout(new java.awt.BorderLayout());
        sweeppane1.add(oldValuePanel, java.awt.BorderLayout.NORTH);
        sweeppane1.add(newValuePanel, java.awt.BorderLayout.CENTER);
        sweeppane1.add(errorTypePane, java.awt.BorderLayout.SOUTH);

        sweepButton.setText(" Sweep ");
        sweepButton.setEnabled(false);
        sweepButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sweepButtonActionPerformed(evt);
            }
        });
        sweepAllButton.setText(" Sweep All ");
        sweepAllButton.setEnabled(false);
        sweepAllButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sweepAllButtonActionPerformed(evt);
            }
        });

        sweep.add(sweeppane1, java.awt.BorderLayout.NORTH);

        sweeppane2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER));

        newValuetextfield.setColumns(13);
        newValuetextfield.setDocument(new PlainDocument() {

            public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
                super.insertString(offs, str, a);
                checkEnableSweepButton();
            }

            public void remove(int offs, int len)
                    throws BadLocationException {
                super.remove(offs, len);
                checkEnableSweepButton();
            }
        });

        //batchspanField.setText("2500");

        back2Button.setText(" Back ");
        back2Button.setEnabled(true);
        back2Button.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                back2ButtonActionPerformed(evt);
            }
        });
        saveViewButton = new javax.swing.JButton();
        saveViewButton.setText("Save ");
        saveViewButton.setEnabled(false);
        saveViewButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveViewButtonActionPerformed(evt);
            }
        });
        sweeppane2.add(back2Button);
        sweeppane2.add(sweepButton);
        sweeppane2.add(sweepAllButton);
        sweeppane2.add(saveViewButton);
        sweep.add(sweeppane2, java.awt.BorderLayout.CENTER);
        marking.add(sweep, java.awt.BorderLayout.CENTER);
        theCoderPane.add(marking, java.awt.BorderLayout.AFTER_LINE_ENDS);
        coderPane.setLayout(new java.awt.BorderLayout());

        coderPane.setFocusCycleRoot(true);
        coderPane.setPreferredSize(new java.awt.Dimension(238, 90));
        topPane.setLayout(new javax.swing.BoxLayout(topPane, javax.swing.BoxLayout.Y_AXIS));

        topPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        topPane.setFocusCycleRoot(true);
        topPane.setMaximumSize(new java.awt.Dimension(270, 318));
        topPane.setPreferredSize(new java.awt.Dimension(250, 130));
        topPane.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                topPaneMouseClicked(evt);
            }
        });

        binderLabel.setFont(new java.awt.Font("MS Sans Serif", 0, 24));
        binderLabel.setForeground(new java.awt.Color(0, 0, 255));
        binderLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        binderLabel.setText("Project Binder");
        binderLabel.setAlignmentX(0.5F);
        binderLabel.setAlignmentY(0.0F);
        binderLabel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(5, 1, 5, 1)));
        binderLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        topPane.add(binderLabel);

        docPane.setLayout(new javax.swing.BoxLayout(docPane, javax.swing.BoxLayout.X_AXIS));

        docPane.setBorder(new javax.swing.border.TitledBorder("Current Document Range"));
        docPane.setFocusable(false);
        docPane.setPreferredSize(new java.awt.Dimension(250, 51));
        hyphen11.setForeground(new java.awt.Color(0, 0, 204));
        hyphen11.setText("    ");
        hyphen11.setFocusable(false);
        docPane.add(hyphen11);

        beginDocButton.setForeground(new java.awt.Color(0, 0, 204));
        beginDocButton.setText(EMPTY_BUTTON_TEXT);
        beginDocButton.setToolTipText("Click to View");
        beginDocButton.setFocusable(false);
        beginDocButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beginDocButtonActionPerformed(evt);
            }
        });

        docPane.add(beginDocButton);

        hyphen1.setForeground(new java.awt.Color(0, 0, 204));
        hyphen1.setText("  -  ");
        hyphen1.setFocusable(false);
        docPane.add(hyphen1);

        endDocButton.setForeground(new java.awt.Color(0, 0, 204));
        endDocButton.setText(EMPTY_BUTTON_TEXT);
        endDocButton.setToolTipText("Click to View");
        endDocButton.setFocusable(false);
        endDocButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endDocButtonActionPerformed(evt);
            }
        });

        docPane.add(endDocButton);

        hyphen12.setForeground(new java.awt.Color(0, 0, 204));
        hyphen12.setText("    ");
        hyphen12.setFocusable(false);
        docPane.add(hyphen12);

        topPane.add(docPane);

        attachPane.setLayout(new javax.swing.BoxLayout(attachPane, javax.swing.BoxLayout.X_AXIS));

        attachPane.setBorder(new javax.swing.border.TitledBorder("Attachment Range"));
        attachPane.setFocusable(false);
        attachPane.setPreferredSize(new java.awt.Dimension(250, 51));
        hyphen111.setForeground(new java.awt.Color(0, 0, 204));
        hyphen111.setText("    ");
        hyphen111.setFocusable(false);
        attachPane.add(hyphen111);

        beginAttachButton.setForeground(new java.awt.Color(0, 0, 204));
        beginAttachButton.setText(EMPTY_BUTTON_TEXT);
        beginAttachButton.setToolTipText("Click to View");
        beginAttachButton.setFocusable(false);
        beginAttachButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beginAttachButtonActionPerformed(evt);
            }
        });

        attachPane.add(beginAttachButton);

        hyphen2.setForeground(new java.awt.Color(0, 0, 204));
        hyphen2.setText("  -  ");
        hyphen2.setFocusable(false);
        attachPane.add(hyphen2);

        endAttachButton.setForeground(new java.awt.Color(0, 0, 204));
        endAttachButton.setText(EMPTY_BUTTON_TEXT);
        endAttachButton.setToolTipText("Click to View");
        endAttachButton.setFocusable(false);
        endAttachButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endAttachButtonActionPerformed(evt);
            }
        });

        attachPane.add(endAttachButton);

        hyphen112.setForeground(new java.awt.Color(0, 0, 204));
        hyphen112.setText("    ");
        hyphen112.setFocusable(false);
        attachPane.add(hyphen112);

        topPane.add(attachPane);

        unitizingPanel.setFocusable(false);
        unitizingPanel.setPreferredSize(new java.awt.Dimension(125, 35));
        uPagePrevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ImagePrev.gif")));
        uPagePrevButton.setToolTipText("Previous Image");
        uPagePrevButton.setFocusable(false);
        uPagePrevButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uPagePrevButtonActionPerformed(evt);
            }
        });

        jPanel2.add(uPagePrevButton);

        uPageNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ImageNext.gif")));
        uPageNextButton.setToolTipText("Next Image");
        uPageNextButton.setFocusable(false);
        uPageNextButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uPageNextButtonActionPerformed(evt);
            }
        });

        jPanel2.add(uPageNextButton);

        unitizingPanel.add(jPanel2);

        topPane.add(unitizingPanel);

        boundaryPane.setFocusTraversalPolicy(getFocusTraversalPolicy());
        boundaryPane.setMinimumSize(new java.awt.Dimension(77, 25));
        boundaryLabel.setFont(new java.awt.Font("Dialog", 0, 11));
        boundaryLabel.setText("Boundary: ");
        boundaryLabel.setFocusable(false);
        boundaryPane.add(boundaryLabel);

        boundaryText.setColumns(1);
        boundaryText.setToolTipText("D (Alt+D) Begin Attachment Range (Document)\nC (Alt+I) Begin Document Range (Child)\nsp (Alt+X) Clear Boundary");
        boundaryPane.add(boundaryText);

        boundaryBatesLabel.setToolTipText("Bates associated with Boundary.");
        boundaryBatesLabel.setFocusable(false);
        boundaryPane.add(boundaryBatesLabel);

        topPane.add(boundaryPane);

        absolutePane.setFocusTraversalPolicy(getFocusTraversalPolicy());
        absolutePane.setMinimumSize(new java.awt.Dimension(173, 45));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        jPanel5.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0)));
        absoluteGoButton.setFont(new java.awt.Font("Dialog", 0, 11));
        absoluteGoButton.setText("Go To Page");
        absoluteGoButton.setToolTipText("Show the image and data corresponding to the entered page number.");
        absoluteGoButton.setFocusable(false);
        absoluteGoButton.setPreferredSize(null);
        absoluteGoButton.setEnabled(false);
        absoluteGoButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                absoluteGoButtonActionPerformed(evt);
            }
        });

        jPanel5.add(absoluteGoButton, new java.awt.GridBagConstraints());

        absolutePage.setColumns(8);
        absolutePage.setText("");
        absolutePage.setPreferredSize(new Dimension(100, 25));
        absolutePage.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                absolutePagePropertyChanged(evt);
            }
        });

        /* absolutePage.addKeyListener(new KeyAdapter() {
        public void keyReleased(KeyEvent e) {
        String value;
        if ((value = absolutePage.getText()).length() > 15) {
        absolutePage.setText(value.substring(0, 14));
        }
        }
        });*/

        jPanel5.add(absolutePage, new java.awt.GridBagConstraints());

        absolutePane.add(jPanel5);

        issueButton.setFont(new java.awt.Font("Dialog", 0, 11));
        issueButton.setText("Issues");
        issueButton.setToolTipText("Create or edit an Issue line for the LFP for this page.");
        issueButton.setFocusable(false);
        issueButton.setPreferredSize(null);
        issueButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                issueButtonActionPerformed(evt);
            }
        });

        absolutePane.add(issueButton);

        topPane.add(absolutePane);

        topScrollPane.setViewportView(topPane);

        coderPane.add(topScrollPane, java.awt.BorderLayout.NORTH);

        midScrollPane.setFocusable(false);
        midScrollPane.setRequestFocusEnabled(false);
        midScrollPane.setViewportView(dynamicPane);

        coderPane.add(midScrollPane, java.awt.BorderLayout.CENTER);

        statusPane.setLayout(new java.awt.BorderLayout());

        statusPane.setFocusable(false);
        statusPane.setRequestFocusEnabled(false);
        statusLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusLabel.setText("  (status label)");
        statusLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        statusLabel.setBorder(new javax.swing.border.EtchedBorder());
        statusLabel.setFocusable(false);
        statusLabel.setRequestFocusEnabled(false);
        statusLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        statusLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        statusPane.add(statusLabel, java.awt.BorderLayout.CENTER);

        coderPane.add(statusPane, java.awt.BorderLayout.SOUTH);

        theCoderPane.add(coderPane, java.awt.BorderLayout.CENTER);

        splitpane.setLeftComponent(theCoderPane);

        theViewerPane.setLayout(new java.awt.BorderLayout());

        theViewerPane.setFocusable(false);
        theViewerPane.setName("theViewerPane");
        theViewerPane.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                theViewerPaneMouseClicked(evt);
            }
        });

        viewerToolBar.setBorder(null);
        viewerToolBar.setFocusable(false);
        viewerToolBar.setName("viewerToolBar");
        viewerToolBar.setPreferredSize(new java.awt.Dimension(592, 32));
        viewerToolBar.setBorderPainted(false);
        vPagePrevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ImagePrev.gif")));
        vPagePrevButton.setToolTipText("Previous Image");
        vPagePrevButton.setFocusable(false);
        vPagePrevButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vPagePrevButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vPagePrevButton);

        vPageNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ImageNext.gif")));
        vPageNextButton.setToolTipText("Next Image");
        vPageNextButton.setFocusable(false);
        vPageNextButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vPageNextButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vPageNextButton);

        vDocPrevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/docPrev.gif")));
        vDocPrevButton.setToolTipText("Previous Document");
        vDocPrevButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vDocPrevButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vDocPrevButton);

        vDocNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/docNext.gif")));
        vDocNextButton.setToolTipText("Next Document");
        vDocNextButton.setFocusable(false);
        vDocNextButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vDocNextButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vDocNextButton);

        viewerToolBar.addSeparator();
        vAttachPrevButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/attachPrev.gif")));
        vAttachPrevButton.setToolTipText("Previous Attachment Range");
        vAttachPrevButton.setFocusable(false);
        vAttachPrevButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vAttachPrevButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vAttachPrevButton);

        vAttachNextButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/attachNext.gif")));
        vAttachNextButton.setToolTipText("Next Attachment Range");
        vAttachNextButton.setFocusable(false);
        vAttachNextButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vAttachNextButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vAttachNextButton);

        viewerToolBar.addSeparator();
        vFirstCurrButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/docFirst.gif")));
        vFirstCurrButton.setToolTipText("Show First Image in Current Document");
        vFirstCurrButton.setFocusable(false);
        vFirstCurrButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vFirstCurrButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vFirstCurrButton);

        vLastCurrButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/docLast.gif")));
        vLastCurrButton.setToolTipText("Show Last Image in Current Document");
        vLastCurrButton.setFocusable(false);
        vLastCurrButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vLastCurrButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vLastCurrButton);

        viewerToolBar.addSeparator();
        vAbsoluteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/filview3.gif")));
        vAbsoluteButton.setToolTipText("Select Image by Bates Number");
        vAbsoluteButton.setFocusable(false);
        vAbsoluteButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vAbsoluteButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vAbsoluteButton);

        vBeginImageRunButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/beginImageRun.gif")));
        vBeginImageRunButton.setToolTipText("Auto Scroll Images");
        vBeginImageRunButton.setFocusable(false);
        vBeginImageRunButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vBeginImageRunButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vBeginImageRunButton);

        viewerToolBar.addSeparator();
        vHorizButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fit_Horiz.gif")));
        vHorizButton.setToolTipText("Fit Horizontally");
        vHorizButton.setFocusable(false);
        vHorizButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vHorizButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vHorizButton);

        vVertButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fit_Vert.gif")));
        vVertButton.setToolTipText("Fit Vertically");
        vVertButton.setFocusable(false);
        vVertButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vVertButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vVertButton);

        vScreenButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fit_Screen.gif")));
        vScreenButton.setToolTipText("Fit to Screen");
        vScreenButton.setFocusable(false);
        vScreenButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vScreenButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vScreenButton);

        viewerToolBar.addSeparator();
        vTurn90ClockwiseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/turn90clockwise.gif")));
        vTurn90ClockwiseButton.setToolTipText("Rotate Clockwise");
        vTurn90ClockwiseButton.setFocusable(false);
        vTurn90ClockwiseButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vTurn90ClockwiseButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vTurn90ClockwiseButton);

        vTurn90CounterwiseButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/turn90counterclockwise.gif")));
        vTurn90CounterwiseButton1.setToolTipText("Rotate Counter Clockwise");
        vTurn90CounterwiseButton1.setFocusable(false);
        vTurn90CounterwiseButton1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vTurn90CounterwiseButton1ActionPerformed(evt);
            }
        });

        viewerToolBar.add(vTurn90CounterwiseButton1);

        viewerToolBar.addSeparator();
        vZoomWindowButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/zoomReset.gif")));
        vZoomWindowButton.setToolTipText("Zoom Window");
        vZoomWindowButton.setFocusable(false);
        vZoomWindowButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vZoomWindowButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vZoomWindowButton);

        vZoomInButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/zoomIn16.gif")));
        vZoomInButton.setToolTipText("Zoom In");
        vZoomInButton.setFocusable(false);
        vZoomInButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vZoomInButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vZoomInButton);

        vZoomOutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/zoomOut16.gif")));
        vZoomOutButton.setToolTipText("Zoom Out");
        vZoomOutButton.setFocusable(false);
        vZoomOutButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vZoomOutButtonActionPerformed(evt);
            }
        });

        viewerToolBar.add(vZoomOutButton);

        theViewerPane.add(viewerToolBar, java.awt.BorderLayout.NORTH);

        viewerPane.setLayout(new java.awt.BorderLayout());

        viewerPane.setFocusable(false);
        viewerPane.setName("viewerPane");
        viewerLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        viewerLabel.setText("(viewerLabel)");
        viewerLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        viewerLabel.setFocusable(false);
        viewerLabel.setRequestFocusEnabled(false);
        viewerLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        viewerLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        viewerLabel.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                viewerLabelMouseClicked(evt);
            }
        });

        viewerPane.add(viewerLabel, java.awt.BorderLayout.NORTH);

        theViewer.setLayout(new java.awt.GridBagLayout());

        theViewer.setName("theViewer");
        theViewer.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                theViewerMouseClicked(evt);
            }
        });

        viewerPane.add(theViewer, java.awt.BorderLayout.CENTER);

        theViewerPane.add(viewerPane, java.awt.BorderLayout.CENTER);

        splitpane.setRightComponent(theViewerPane);

        coderPanel.add(splitpane, java.awt.BorderLayout.CENTER);

        getContentPane().add(coderPanel, java.awt.BorderLayout.CENTER);

        coderMenu.setFocusable(false);
        fileMenu.setMnemonic('F');
        fileMenu.setText("File");
        fileMenu.setFocusable(false);
        sampleMenuItem.setMnemonic('Q');
        sampleMenuItem.setText("Add to QA Sample");
        sampleMenuItem.setToolTipText("Select additional documents for QA");
        sampleMenuItem.setEnabled(false);
        sampleMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sampleMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(sampleMenuItem);

        validateBatchMenuItem.setMnemonic('V');
        validateBatchMenuItem.setText("Validate Batch");
        validateBatchMenuItem.setEnabled(false);
        validateBatchMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateBatchMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(validateBatchMenuItem);

        closeBatchMenuItem.setMnemonic('C');
        closeBatchMenuItem.setText("Close Batch");
        closeBatchMenuItem.setEnabled(false);
        closeBatchMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBatchMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(closeBatchMenuItem);

        rejectBatchMenuItem.setMnemonic('R');
        rejectBatchMenuItem.setText("Reject Batch");
        rejectBatchMenuItem.setToolTipText("Close this batch and revert to its previous status.");
        rejectBatchMenuItem.setEnabled(false);
        rejectBatchMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rejectBatchMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(rejectBatchMenuItem);

        fileMenu.add(jSeparator6);

        forceSaveMenuItem.setText("Force Save");
        forceSaveMenuItem.setEnabled(false);
        forceSaveMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forceSaveMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(forceSaveMenuItem);
        if (whichStatus.equals("Coding") || whichStatus.equals("CodingQC")) {
            openBinderMenuItem.setMnemonic('V');
            openBinderMenuItem.setText("Open Project Binder");
            openBinderMenuItem.addActionListener(new java.awt.event.ActionListener() {

                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    openBinderMenuItemActionPerformed(evt);
                }
            });
            fileMenu.add(openBinderMenuItem);
        }
        addToBinderItem.setText("Add to Binder");
        addToBinderItem.setEnabled(false);
        addToBinderItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToBinderItemActionPerformed(evt);
            }
        });

        fileMenu.add(addToBinderItem);

        createQAIRMenuItem.setText("Create QAIR");
        createQAIRMenuItem.setVisible(false);
        createQAIRMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createQAIRMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(createQAIRMenuItem);

        removeFromBinderItem.setText("Remove from Binder");
        removeFromBinderItem.setEnabled(false);
        removeFromBinderItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFromBinderItemActionPerformed(evt);
            }
        });

        fileMenu.add(removeFromBinderItem);

        fileMenu.add(jSeparator61);

        //closeMenuItem.setMnemonic('C');
        assignListingQCMenuItem.setText("Assign Listing QC");
        assignListingQCMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assignListingQCMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(assignListingQCMenuItem);

        assignTallyQCMenuItem.setText("Assign Tally Fix");
        assignTallyQCMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                assignTallyQCMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(assignTallyQCMenuItem);

        closeVolume.setText("Listing Done");
        closeVolume.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeVolumeActionPerformed(evt);
            }
        });

        fileMenu.add(closeVolume);

        listingQCDone.setText("ListingQC Done");
        listingQCDone.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listingQCDoneActionPerformed(evt);
            }
        });

        fileMenu.add(listingQCDone);

        tallyQCDone.setText("Tally Done");
        tallyQCDone.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tallyQCDoneActionPerformed(evt);
            }
        });

        fileMenu.add(tallyQCDone);

        count.setText("Count");
        count.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                countActionPerformed(evt);
            }
        });

        fileMenu.add(count);

        closeMenuItem.setMnemonic('C');
        closeMenuItem.setText("Close");
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(closeMenuItem);

        exitMenuItem.setMnemonic('X');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitMenuItem);

        coderMenu.add(fileMenu);

        editMenu.setMnemonic('E');
        editMenu.setText("Edit");
        editMenu.setFocusable(false);
        editMenu.addMenuListener(new javax.swing.event.MenuListener() {

            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }

            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }

            public void menuSelected(javax.swing.event.MenuEvent evt) {
                editMenuMenuSelected(evt);
            }
        });
        editMenu.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                editMenuMouseEntered(evt);
            }
        });

        copyCurrentMenuItem.setMnemonic('C');
        copyCurrentMenuItem.setText("Copy Current Field");
        copyCurrentMenuItem.setToolTipText("Copy the value from the previously-saved record.");
        copyCurrentMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyCurrentMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(copyCurrentMenuItem);

        copyAllMenuItem.setMnemonic('A');
        copyAllMenuItem.setText("Copy All Fields");
        copyAllMenuItem.setToolTipText("Copy all values from the previously-saved record.");
        copyAllMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyAllMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(copyAllMenuItem);

        editMenu.add(jSeparator8);

        splitMenuItem.setMnemonic('A');
        splitMenuItem.setText("Split Document");
        splitMenuItem.setToolTipText("Create a subdocument");
        splitMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                splitMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(splitMenuItem);

        unsplitMenuItem.setMnemonic('A');
        unsplitMenuItem.setText("Unsplit Document");
        unsplitMenuItem.setToolTipText("Remove subdocument break");
        unsplitMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unsplitMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(unsplitMenuItem);

        editMenu.add(jSeparator9);

        boundaryMenu.setMnemonic('B');
        boundaryMenu.setText("Change Boundary");
        boundaryMenu.setToolTipText("Change the boundary of the current image.");
        boundaryMenu.setFocusable(false);
        boundaryMenu.addMenuListener(new javax.swing.event.MenuListener() {

            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }

            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }

            public void menuSelected(javax.swing.event.MenuEvent evt) {
                boundaryMenuMenuSelected(evt);
            }
        });
        boundaryMenu.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boundaryMenuMouseEntered(evt);
            }
        });

        documentMenuItem.setMnemonic('D');
        documentMenuItem.setText("Document");
        documentMenuItem.setToolTipText("Make this image the first page of a document range.");
        documentMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                documentMenuItemActionPerformed(evt);
            }
        });

        boundaryMenu.add(documentMenuItem);

        childMenuItem.setMnemonic('C');
        childMenuItem.setText("Child");
        childMenuItem.setToolTipText("Make this a image start a child.");
        childMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                childMenuItemActionPerformed(evt);
            }
        });

        boundaryMenu.add(childMenuItem);

        clearMenuItem.setMnemonic('X');
        clearMenuItem.setText("Clear");
        clearMenuItem.setToolTipText("Clear this image's boundary.");
        clearMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearMenuItemActionPerformed(evt);
            }
        });

        boundaryMenu.add(clearMenuItem);

        editMenu.add(boundaryMenu);

        coderMenu.add(editMenu);

        viewMenu.setMnemonic('V');
        viewMenu.setText("View");
        viewMenu.setFocusable(false);
        highResButton.setMnemonic('H');
        highResButton.setSelected(true);
        highResButton.setText("High Resolution");
        highResButton.setToolTipText("Set image quality to High Resolution");
        resButtonGroup.add(highResButton);
        highResButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                highResButtonActionPerformed(evt);
            }
        });

        viewMenu.add(highResButton);

        mediumResButton.setMnemonic('M');
        mediumResButton.setText("Medium Resolution");
        mediumResButton.setToolTipText("Set image quality to Medium Resolution");
        resButtonGroup.add(mediumResButton);
        mediumResButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mediumResButtonActionPerformed(evt);
            }
        });

        viewMenu.add(mediumResButton);

        lowResButton.setMnemonic('L');
        lowResButton.setText("Low Resolution");
        lowResButton.setToolTipText("Set image quality to Low Resolution");
        resButtonGroup.add(lowResButton);
        lowResButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lowResButtonActionPerformed(evt);
            }
        });

        viewMenu.add(lowResButton);

        draftResButton.setMnemonic('D');
        draftResButton.setText("Draft Resolution");
        draftResButton.setToolTipText("Set image quality to Draft Resolution");
        resButtonGroup.add(draftResButton);
        draftResButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                draftResButtonActionPerformed(evt);
            }
        });

        viewMenu.add(draftResButton);

        viewMenu.add(jSeparator62);

        codingLeftButton.setSelected(true);
        codingLeftButton.setText("Coding on Left");
        codingLeftButton.setToolTipText("Coding to Left of Viewer");
        codingButtonGroup.add(codingLeftButton);
        codingLeftButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codingLeftButtonActionPerformed(evt);
            }
        });

        viewMenu.add(codingLeftButton);

        codingRightButton.setText("Coding on Right");
        codingRightButton.setToolTipText("Coding to Right of Viewer");
        codingButtonGroup.add(codingRightButton);
        codingRightButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codingRightButtonActionPerformed(evt);
            }
        });

        viewMenu.add(codingRightButton);

        codingUndockedButton.setText("Coding Undocked");
        codingUndockedButton.setToolTipText("Coding ndocked from Viewer");
        codingButtonGroup.add(codingUndockedButton);
        codingUndockedButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                codingUndockedButtonActionPerformed(evt);
            }
        });

        viewMenu.add(codingUndockedButton);

        coderMenu.add(viewMenu);

        reportMenu.setMnemonic('E');
        reportMenu.setText("Report");
        reportMenu.setFocusable(false);
        qcCoderErrorReportItem.setMnemonic('C');
        qcCoderErrorReportItem.setText("Coding Errors");
        qcCoderErrorReportItem.setToolTipText("Report coding errors for batch");
        qcCoderErrorReportItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qcCoderErrorReportItemActionPerformed(evt);
            }
        });

        reportMenu.add(qcCoderErrorReportItem);

        qcProjectSummaryItem.setText("Project Summary");
        qcProjectSummaryItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qcProjectSummaryItemActionPerformed(evt);
            }
        });

        reportMenu.add(qcProjectSummaryItem);

        qcProjectReportMenu.setText("All Batches for Project");
        projectByBatchItem.setText("by Batch");
        projectByBatchItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectByBatchItemActionPerformed(evt);
            }
        });

        qcProjectReportMenu.add(projectByBatchItem);

        projectByVolumeItem.setText("by Volume");
        projectByVolumeItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectByVolumeItemActionPerformed(evt);
            }
        });

        qcProjectReportMenu.add(projectByVolumeItem);

        projectByStatusItem.setText("by Status");
        projectByStatusItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectByStatusItemActionPerformed(evt);
            }
        });

        qcProjectReportMenu.add(projectByStatusItem);

        projectByVolumeAndStatusItem.setText("by Volume and Status");
        projectByVolumeAndStatusItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectByVolumeAndStatusItemActionPerformed(evt);
            }
        });

        qcProjectReportMenu.add(projectByVolumeAndStatusItem);

        reportMenu.add(qcProjectReportMenu);

        qcBatchOpenReportMenu.setText("Open Batches for Team");
        openByBatchItem.setText("by Batch");
        openByBatchItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openByBatchItemActionPerformed(evt);
            }
        });

        qcBatchOpenReportMenu.add(openByBatchItem);

        openByCoderItem.setText("by Coder");
        openByCoderItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openByCoderItemActionPerformed(evt);
            }
        });

        qcBatchOpenReportMenu.add(openByCoderItem);

        reportMenu.add(qcBatchOpenReportMenu);

        qcBatchQCReportMenu.setText("QC Batches for Team");
        qcByBatchItem.setText("by Batch");
        qcByBatchItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qcByBatchItemActionPerformed(evt);
            }
        });

        qcBatchQCReportMenu.add(qcByBatchItem);

        qcByCoderItem.setText("by Coder");
        qcByCoderItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qcByCoderItemActionPerformed(evt);
            }
        });

        qcBatchQCReportMenu.add(qcByCoderItem);

        reportMenu.add(qcBatchQCReportMenu);

        payrollDetailReportMenu.setText("Payroll");
        payrollDetailReportMenu.addMenuListener(new javax.swing.event.MenuListener() {

            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }

            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }

            public void menuSelected(javax.swing.event.MenuEvent evt) {
                payrollDetailReportMenuMenuSelected(evt);
            }
        });

        payrollDetailTeamMenu.setText("for Team");
        payrollDetailTeamItem.setText("Current Period");
        payrollDetailTeamItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollDetailTeamItemActionPerformed(evt);
            }
        });

        payrollDetailTeamMenu.add(payrollDetailTeamItem);

        payrollDetailTeamItem1.setText("Current Period - 1");
        payrollDetailTeamItem1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollDetailTeamItem1ActionPerformed(evt);
            }
        });

        payrollDetailTeamMenu.add(payrollDetailTeamItem1);

        payrollDetailTeamItem2.setText("Current Period - 2");
        payrollDetailTeamItem2.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollDetailTeamItem2ActionPerformed(evt);
            }
        });

        payrollDetailTeamMenu.add(payrollDetailTeamItem2);

        payrollDetailTeamItem3.setText("Current Period - 3");
        payrollDetailTeamItem3.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollDetailTeamItem3ActionPerformed(evt);
            }
        });

        payrollDetailTeamMenu.add(payrollDetailTeamItem3);

        payrollDetailReportMenu.add(payrollDetailTeamMenu);

        payrollDetailSelfMenu.setText("for Self");
        payrollDetailSelfItem.setText("Current Period");
        payrollDetailSelfItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollDetailSelfItemActionPerformed(evt);
            }
        });

        payrollDetailSelfMenu.add(payrollDetailSelfItem);

        payrollDetailSelfItem1.setText("Current Period - 1");
        payrollDetailSelfItem1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollDetailSelfItem1ActionPerformed(evt);
            }
        });

        payrollDetailSelfMenu.add(payrollDetailSelfItem1);

        payrollDetailSelfItem2.setText("Current Period - 2");
        payrollDetailSelfItem2.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollDetailSelfItem2ActionPerformed(evt);
            }
        });

        payrollDetailSelfMenu.add(payrollDetailSelfItem2);

        payrollDetailSelfItem3.setText("Current Period - 3");
        payrollDetailSelfItem3.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollDetailSelfItem3ActionPerformed(evt);
            }
        });

        payrollDetailSelfMenu.add(payrollDetailSelfItem3);

        payrollDetailReportMenu.add(payrollDetailSelfMenu);

        reportMenu.add(payrollDetailReportMenu);

        payrollSummaryReportMenu.setText("Payroll Summary");
        payrollSummaryReportMenu.addMenuListener(new javax.swing.event.MenuListener() {

            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }

            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }

            public void menuSelected(javax.swing.event.MenuEvent evt) {
                payrollSummaryReportMenuMenuSelected(evt);
            }
        });

        payrollSummaryTeamMenu.setText("for Team");
        payrollSummaryTeamItem.setText("Current Period");
        payrollSummaryTeamItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollSummaryTeamItemActionPerformed(evt);
            }
        });

        payrollSummaryTeamMenu.add(payrollSummaryTeamItem);

        payrollSummaryTeamItem1.setText("Current Period - 1");
        payrollSummaryTeamItem1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollSummaryTeamItem1ActionPerformed(evt);
            }
        });

        payrollSummaryTeamMenu.add(payrollSummaryTeamItem1);

        payrollSummaryTeamItem2.setText("Current Period - 2");
        payrollSummaryTeamItem2.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollSummaryTeamItem2ActionPerformed(evt);
            }
        });

        payrollSummaryTeamMenu.add(payrollSummaryTeamItem2);

        payrollSummaryTeamItem3.setText("Current Period - 3");
        payrollSummaryTeamItem3.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollSummaryTeamItem3ActionPerformed(evt);
            }
        });

        payrollSummaryTeamMenu.add(payrollSummaryTeamItem3);

        payrollSummaryReportMenu.add(payrollSummaryTeamMenu);

        payrollSummarySelfMenu.setText("for Self");
        payrollSummarySelfItem.setText("Current Period");
        payrollSummarySelfItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollSummarySelfItemActionPerformed(evt);
            }
        });

        payrollSummarySelfMenu.add(payrollSummarySelfItem);

        payrollSummarySelfItem1.setText("Current Period - 1");
        payrollSummarySelfItem1.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollSummarySelfItem1ActionPerformed(evt);
            }
        });

        payrollSummarySelfMenu.add(payrollSummarySelfItem1);

        payrollSummarySelfItem2.setText("Current Period - 2");
        payrollSummarySelfItem2.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollSummarySelfItem2ActionPerformed(evt);
            }
        });

        payrollSummarySelfMenu.add(payrollSummarySelfItem2);

        payrollSummarySelfItem3.setText("Current Period - 3");
        payrollSummarySelfItem3.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                payrollSummarySelfItem3ActionPerformed(evt);
            }
        });

        payrollSummarySelfMenu.add(payrollSummarySelfItem3);

        payrollSummaryReportMenu.add(payrollSummarySelfMenu);

        reportMenu.add(payrollSummaryReportMenu);

        coderMenu.add(reportMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");
        helpMenu.setFocusable(false);
        AboutMenuItem.setText("About");
        AboutMenuItem.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(AboutMenuItem);

        coderMenu.add(helpMenu);

        setJMenuBar(coderMenu);

        pack();
    }

    private void payrollSummaryReportMenuMenuSelected(javax.swing.event.MenuEvent evt) {
        try {
            enableReportMenu();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void payrollSummaryTeamItem3ActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollSummaryTeam(getPayPeriod(3));
    }

    private void payrollSummaryTeamItem2ActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollSummaryTeam(getPayPeriod(2));
    }

    private void payrollSummaryTeamItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollSummaryTeam(getPayPeriod(1));
    }

    private void payrollSummaryTeamItemActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollSummaryTeam(getPayPeriod(0));
    }

    private void generatePayrollSummaryTeam(String[] payPeriod) {
        AbstractReport currentReport = new MenuReport(
                "report_payroll_summary_team", "Payroll for Team " + team_name, "Start Date", payPeriod[0], payPeriod[1], "End Date", payPeriod[2], payPeriod[3]);
        currentReport.generate();
    }

    private void payrollSummarySelfItem3ActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollSummarySelf(getPayPeriod(3));
    }

    private void payrollSummarySelfItem2ActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollSummarySelf(getPayPeriod(2));
    }

    private void payrollSummarySelfItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollSummarySelf(getPayPeriod(1));
    }

    private void payrollSummarySelfItemActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollSummarySelf(getPayPeriod(0));
    }

    private void generatePayrollSummarySelf(String[] payPeriod) {
        AbstractReport currentReport = new MenuReport(
                "report_payroll_summary_user", "Payroll Summary for " + user_name, "Start Date", payPeriod[0], payPeriod[1], "End Date", payPeriod[2], payPeriod[3]);
        currentReport.generate();
    }

    private void payrollDetailReportMenuMenuSelected(javax.swing.event.MenuEvent evt) {
        try {
            enableReportMenu();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void enableReportMenu() {
        final ClientTask task = new TaskExecuteQuery("SplitPaneViewer.get users status");
        task.setCallback(new Runnable() {

            public void run() {
                ResultSet queryResult = (ResultSet) task.getResult();
                enableReportMenuEntry(queryResult);
            }
        });
        task.enqueue();
    }

    private void enableReportMenuEntry(ResultSet rs) {
        try {
            if (rs.next()) {
                user_name = rs.getString(1);
                team_name = rs.getString(2);
                if (rs.getBoolean(3)) {
                    // qcer or team leader
                    payrollDetailTeamMenu.setEnabled(true);
                    payrollSummaryTeamMenu.setEnabled(true);
                } else {
                    payrollDetailTeamMenu.setEnabled(false);
                    payrollSummaryTeamMenu.setEnabled(false);
                }
            }
            rs.close();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void payrollDetailSelfItem3ActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollTotalsSelf(getPayPeriod(3));
    }

    private void payrollDetailSelfItem2ActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollTotalsSelf(getPayPeriod(2));
    }

    private void payrollDetailSelfItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollTotalsSelf(getPayPeriod(1));
    }

    private void payrollDetailSelfItemActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollTotalsSelf(getPayPeriod(0));
    }

    private void generatePayrollTotalsSelf(String[] payPeriod) {
        AbstractReport currentReport = new MenuReport(
                "report_payroll_detail_user", "Payroll for " + user_name, "Start Date", payPeriod[0], payPeriod[1], "End Date", payPeriod[2], payPeriod[3]);
        currentReport.generate();
    }

    private void payrollDetailTeamItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollTotals(getPayPeriod(1));
    }

    private void payrollDetailTeamItem2ActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollTotals(getPayPeriod(2));
    }

    private void payrollDetailTeamItem3ActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollTotals(getPayPeriod(3));
    }

    private void payrollDetailTeamItemActionPerformed(java.awt.event.ActionEvent evt) {
        generatePayrollTotals(getPayPeriod(0));
    }

    private void generatePayrollTotals(String[] payPeriod) {
        AbstractReport currentReport = new MenuReport(
                "report_payroll_detail_team", "Payroll for Team " + team_name, "Start Date", payPeriod[0], payPeriod[1], "End Date", payPeriod[2], payPeriod[3]);
        currentReport.generate();
    }

    private String[] getPayPeriod(int period) {
        String[] payPeriod = {"", "", "", ""};
        //Calendar cal = Calendar.getInstance(); // today
        Calendar cal = Calendar.getInstance(
                TimeZone.getTimeZone(Global.theServerConnection.getTimeZoneID()));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if (period > 0) {
            if (cal.get(Calendar.DAY_OF_MONTH) > 30) {
                cal.add(Calendar.DATE, -1);
            }
            cal.add(Calendar.DATE, -(period * 15));
        }

        int firstDate = cal.get(Calendar.DAY_OF_MONTH) < 16 ? 1 : 16;
        int lastDate = cal.get(Calendar.DAY_OF_MONTH) < 16 ? 15 : cal.getActualMaximum(cal.DAY_OF_MONTH);

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        // start date
        cal.set(Calendar.DAY_OF_MONTH, firstDate);
        payPeriod[0] = formatter.format(cal.getTime());
        payPeriod[1] = formatter.format(cal.getTime());
        //Log.print("(SPV.getPayPeriod) start " + cal.getTime());
        //Log.print("(SPV.getPayPeriod) start " + payPeriod[0] + "/" + payPeriod[1]);

        // end date
        cal.set(Calendar.DAY_OF_MONTH, lastDate);
        cal.add(Calendar.DATE, +1);
        cal.setTimeInMillis(cal.getTimeInMillis() - 1);
        payPeriod[2] = formatter.format(cal.getTime());
        payPeriod[3] = formatter.format(cal.getTime());
        //Log.print("(SPV.getPayPeriod) end   " + cal.getTime());
        //Log.print("(SPV.getPayPeriod) end   " + payPeriod[2] + "/" + payPeriod[3]);

        return payPeriod;
    }

    private void theViewerMouseClicked(java.awt.event.MouseEvent evt) {
        hideCurrentPopup();
    }

    private void viewerLabelMouseClicked(java.awt.event.MouseEvent evt) {
        hideCurrentPopup();
    }

    private void theViewerPaneMouseClicked(java.awt.event.MouseEvent evt) {
        hideCurrentPopup();
    }

    private void splitpaneMouseClicked(java.awt.event.MouseEvent evt) {
        hideCurrentPopup();
    }

    private void dynamicPaneMouseClicked(java.awt.event.MouseEvent evt) {
        hideCurrentPopup();
    }

    private void topPaneMouseClicked(java.awt.event.MouseEvent evt) {
        hideCurrentPopup();
    }

    private void coderPanelMouseClicked(java.awt.event.MouseEvent evt) {
        hideCurrentPopup();
    }

    private void formMouseClicked(java.awt.event.MouseEvent evt) {
        hideCurrentPopup();
    }

    private void sampleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        JDialog dialog = new QASampleDialog(this, fileMenu);
        dialog.setVisible(true);
        //System.out.println("back from QA dialog");
        // get the coder data again for the current page
        getCoderData(theCodingData.pageId, ACTUAL, B_NONE);
    }

    private void qcByCoderItemActionPerformed(java.awt.event.ActionEvent evt) {
        AbstractReport currentReport = new MenuReport(
                "qc_report_qc_batch_team_by_coder", "QC Batches for Team");
        currentReport.generate();
    }

    private void qcByBatchItemActionPerformed(java.awt.event.ActionEvent evt) {
        AbstractReport currentReport = new MenuReport(
                "qc_report_qc_batch_team", "QC Batches for Team");
        currentReport.generate();
    }

    private void openByCoderItemActionPerformed(java.awt.event.ActionEvent evt) {
        AbstractReport currentReport = new MenuReport(
                "qc_report_open_batch_team_by_coder", "Open Batches for Team");
        currentReport.generate();
    }

    private void openByBatchItemActionPerformed(java.awt.event.ActionEvent evt) {
        AbstractReport currentReport = new MenuReport(
                "qc_report_open_batch_team", "Open Batches for Team");
        currentReport.generate();
    }

    private void projectByVolumeAndStatusItemActionPerformed(java.awt.event.ActionEvent evt) {
        AbstractReport currentReport = new MenuReport(
                "qc_report_batch_project_by_volume_status", "Batches for Project");
        currentReport.generate();
    }

    private void projectByVolumeItemActionPerformed(java.awt.event.ActionEvent evt) {
        AbstractReport currentReport = new MenuReport(
                "qc_report_batch_project_by_volume", "Batches for Project");
        currentReport.generate();
    }

    private void projectByBatchItemActionPerformed(java.awt.event.ActionEvent evt) {
        AbstractReport currentReport = new MenuReport(
                "qc_report_batch_project", "Batches for Project");
        currentReport.generate();
    }

    private void qcProjectSummaryItemActionPerformed(java.awt.event.ActionEvent evt) {
        AbstractReport currentReport = new MenuReport(
                "qc_report_summary_project", "Project Summary");
        currentReport.generate();
    }

    private void projectByStatusItemActionPerformed(java.awt.event.ActionEvent evt) {
        AbstractReport currentReport = new MenuReport(
                "qc_report_batch_project_by_status", "Batches for Project");
        currentReport.generate();
    }

    private void qcCoderErrorReportItemActionPerformed(java.awt.event.ActionEvent evt) {
        AbstractReport currentReport;
        if ("QA".equals(whichStatus)) {
            currentReport = new MenuReport(
                    "qa_report_error", "QA Coding Error Report");
        } else {
            currentReport = new MenuReport(
                    "qc_report_error", "Coding Error Report");
        }
        currentReport.generate();
    }

    private void boundaryMenuMenuSelected(javax.swing.event.MenuEvent evt) {
        // handle the case where user gets to menu with alt key and mnemonics
        boundaryMenuMouseEntered(null);
    }

    private void editMenuMouseEntered(java.awt.event.MouseEvent evt) {
        // handle the case where menu is left open and user does something elsewhere                                          
        editMenuMenuSelected(null);
    }

    private void editMenuMenuSelected(javax.swing.event.MenuEvent evt) {
        if (theCodingData != null && theImageData != null && theCodingData.childId == theImageData.childId && ("Coding".equals(whichStatus) || "CodingQC".equals(whichStatus) || "QA".equals(whichStatus) || "Admin".equals(whichStatus)) && splitDocuments) {
            splitMenuItem.setEnabled(true);
            if (theCodingData.isSplit) {
                unsplitMenuItem.setEnabled(true);
            } else {
                unsplitMenuItem.setEnabled(false);
            }
        } else {
            splitMenuItem.setEnabled(false);
            unsplitMenuItem.setEnabled(false);
        }
    }

    private void splitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            boolean clone = true;
            if (!isBoundary(theImageData, FIRST_PAGE_OF_CHILD)) {
                int answer = JOptionPane.showConfirmDialog(this,
                        "Does the split document begin at the top of this page?",
                        "At Top Selection",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
                if (answer == JOptionPane.YES_OPTION) {
                    clone = false;
                } else if (answer == JOptionPane.CANCEL_OPTION) {
                    return;
                }
            }
            splitOrUnsplit(clone, /* unsplit => */ false);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void unsplitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            splitOrUnsplit(/* clone => */false, /* unsplit => */ true);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void splitOrUnsplit(boolean clone, boolean unsplit) {
        //Log.print("(SPV).splitOrUnsplit enter");
        final ClientTask task = new TaskSendSplit(theImageData.pageId, clone, unsplit);
        task.setCallback(new Runnable() {

            public void run() {
                try {
                    //returned OK message contains correct page id to show
                    Element result = (Element) task.getResult();
                    int pageId = Integer.parseInt(result.getAttribute(A_PAGE_ID));
                    // Make the coding data correct
                    // TBD: Should avoid reading the image
                    getCoderData(pageId, ACTUAL, B_NONE);
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        });
        boolean ok = task.enqueue(this);
    }

    private void splitpanePropertyChange(java.beans.PropertyChangeEvent evt) {
        if (JSplitPane.DIVIDER_LOCATION_PROPERTY.equals(evt.getPropertyName())) {
            saveView();
        }
    }

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {
        saveView();
    }

    private void formComponentResized(java.awt.event.ComponentEvent evt) {
        saveView();
    }
    final private static int CODING_LEFT = 0;
    final private static int CODING_RIGHT = 1;
    final private static int CODING_UNDOCKED = 2;
    private String[] defaultCoordinates;
    private int currentView = CODING_LEFT;
    private UndockedViewer undockedDialog = null;
    private String viewPrefix;

    private void setView(int view) {
        try {

            // remove old view
            if (currentView == CODING_UNDOCKED) {
                coderPanel.remove(theCoderPane);
                if (undockedDialog != null) {
                    undockedDialog.setVisible(false);
                }
            } else { // since currentView == CODING_LEFT or CODING_RIGHT

                splitpane.setLeftComponent(null);
                splitpane.setRightComponent(null);
                coderPanel.remove(splitpane);
            }

            // add the new view
            currentView = view;
            LocalProperties.setProperty(viewPrefix + "view", view);
            String[] coordinates = LocalProperties.getProperty(
                    viewPrefix + view + "_coordinates", defaultCoordinates[view]).split(",");
            this.setLocation(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]));
            this.setSize(Integer.parseInt(coordinates[2]), Integer.parseInt(coordinates[3]));
            if (view == CODING_UNDOCKED) {
                if (undockedDialog == null) {
                    undockedDialog = new UndockedViewer(this);
                } else {
                    undockedDialog.getContentPane().remove(theViewerPane);
                }
                undockedDialog.setLocation(Integer.parseInt(coordinates[4]), Integer.parseInt(coordinates[5]));
                undockedDialog.setSize(Integer.parseInt(coordinates[6]), Integer.parseInt(coordinates[7]));
                coderPanel.add(theCoderPane, java.awt.BorderLayout.CENTER);
                undockedDialog.getContentPane().add(theViewerPane, java.awt.BorderLayout.CENTER);
                setActionMap(theViewerPane);
                undockedDialog.setVisible(true);
                coderPanel.validate();
                undockedDialog.validate();
            } else {
                splitpane.setDividerLocation(Integer.parseInt(coordinates[4]));
                coderPanel.add(splitpane, java.awt.BorderLayout.CENTER);
                if (view == CODING_LEFT) {
                    splitpane.setLeftComponent(theCoderPane);
                    splitpane.setRightComponent(theViewerPane);
                } else { // since view == CODING_RIGHT

                    splitpane.setLeftComponent(theViewerPane);
                    splitpane.setRightComponent(theCoderPane);
                }
            }
            this.validate();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    /**
     * Save location and size information for current view.
     * Note: We do NOT save if viewer is full-screen (or iconified).
     */
    public void saveView() {

        if (viewPrefix == null) {
            Log.print("saveView: SKIPPING null");
        } else if (getExtendedState() == 0) {
            String position = this.getX() + "," + this.getY() + "," + this.getWidth() + "," + this.getHeight();
            if (currentView == CODING_UNDOCKED) {
                position += "," + undockedDialog.getX() + "," + undockedDialog.getY() + "," + undockedDialog.getWidth() + "," + undockedDialog.getHeight();
            } else {
                position += "," + splitpane.getDividerLocation();
            }
            //Log.print("saving view "+currentView +" "+LocalProperties.getProperty(viewPrefix+currentView+"_view")
            //          + "-->" + position);
            LocalProperties.setProperty(viewPrefix + currentView + "_coordinates", position);
        }

    }

    private void codingLeftButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setView(CODING_LEFT);
    }

    private void codingRightButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setView(CODING_RIGHT);
    }

    private void codingUndockedButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setView(CODING_UNDOCKED);
    }

    private void mailButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            MailreceivedManagedModel.getInstance().showMailDialog();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void openBinderMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        Log.print("open binder pressed----------------------->");
        try {
            assert binderViewer == null;
            assert whichStatus != "Binder";
            final ClientTask task = new TaskExecuteQuery("open binder check");
            task.setCallback(new Runnable() {

                public void run() {
                    try {
                        ResultSet queryResult = (ResultSet) task.getResult();

                        if (!queryResult.next()) {
                            JOptionPane.showMessageDialog(
                                    SplitPaneViewer.this,
                                    "There is no binder for this project.",
                                    "No Binder Error",
                                    JOptionPane.ERROR_MESSAGE);
                            queryResult.close();
                            return;
                        }

                        String project = queryResult.getString(1);
                        int binderVolumeId = queryResult.getInt(2);
                        int binderBatchId = queryResult.getInt(3);
                        queryResult.close();

                        Log.print("open binder b=" + binderBatchId + " v=" + binderVolumeId + " p=" + project);
                        binderViewer = SplitPaneViewer.getInstance();
                        binderViewer.setBatchId(binderBatchId);
                        binderViewer.setVolumeId(binderVolumeId);
                        binderViewer.initializeForProject(// batchNumber,
                                project, projectId,
                                /* whichStatus=> */ "Binder",
                                /* splitDocuments=> */ "No");
                        binderViewer.setParent(SplitPaneViewer.this);
                        // Open the viewer.
                        Log.print("set binder visible------------------------>");
                        binderViewer.setVisible(true);
                    } catch (Throwable th) {
                        Log.quit(th);
                    }
                }
            });
            task.enqueue(this);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    // A class to put up a confirmation dialog.
    // Used as callback for client tasks
    // (If task fails, should have a fail exception.)
    private class Confirmation implements Runnable {

        String message;

        Confirmation(final String message) {
            this.message = message;
        }

        public void run() {
            try {
                JOptionPane.showMessageDialog(
                        SplitPaneViewer.this,
                        message,
                        "Confirmation",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Throwable th) {
                Log.quit(th);
            }
        }
    }

    private void removeFromBinderItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // find child to be displayed after removal
            // next if there is one, then prior, then none if binder empty
            final int nextRelativeChild = theCodingData.batchChildCount <= 1
                    ? 0
                    : theCodingData.batchChildPosition < theCodingData.batchChildCount
                    ? theCodingData.batchChildPosition
                    : theCodingData.batchChildPosition - 1;
            final ClientTask task = new TaskBinderUpdate(theCodingData.pageId, /* remove => */ true);
            task.setCallback(new Runnable() {

                public void run() {
                    try {
                        if (nextRelativeChild == 0) {
                            JOptionPane.showMessageDialog(
                                    SplitPaneViewer.this,
                                    "Document removed from binder;" + "\nBinder is now empty.",
                                    "Confirmation",
                                    JOptionPane.INFORMATION_MESSAGE);
                            // Nothing to show, close the (admin browse) viewer
                            closeViewer();
                        } else {
                            JOptionPane.showMessageDialog(
                                    SplitPaneViewer.this,
                                    "Document removed from binder.",
                                    "Confirmation",
                                    JOptionPane.INFORMATION_MESSAGE);
                            // show next or prior child
                            getCoderData(0, nextRelativeChild, B_CHILD);
                        }
                    } catch (Throwable th) {
                        Log.quit(th);
                    }
                }
            });
            task.enqueue(this);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void addToBinderItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (saveDataCheck() && crossFieldEdit()) {
                final ClientTask task = new TaskBinderUpdate(theCodingData.pageId, /* remove => */ false);
                task.setCallback(new Confirmation("Document added to binder."));
                task.enqueue(this);
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void createQAIRMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        QAIRDialog qair_dialog = new QAIRDialog(this, true, projectId, getVolumeId());
        qair_dialog.setLocationRelativeTo(null);
        qair_dialog.setVisible(true);
    }

    private void absolutePagePropertyChanged(java.beans.PropertyChangeEvent evt) {
        if ("text".equals(evt.getPropertyName())) {
            absoluteGoButton.setEnabled(absolutePage.getValue() != 0);
        }
    }

    private void draftResButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setResolution(RES_DRAFT);
    }

    private void lowResButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setResolution(RES_LOW);
    }

    private void mediumResButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setResolution(RES_MEDIUM);
    }

    private void highResButtonActionPerformed(java.awt.event.ActionEvent evt) {
        setResolution(RES_HIGH);
    }

    // Set new resolution, when resolution is changed by user
    // Reread the image at the specified resolution
    private void setResolution(int newResolution) {
        try {
            if (newResolution != resolution) {
                resolution = newResolution;
                LocalProperties.setProperty(viewPrefix + "resolution", resolution);
                if (theImageData != null) {
                    doViewFromSource(theImageData);
                }
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void boundaryMenuMouseEntered(java.awt.event.MouseEvent evt) {
        try {
            if ("Listing".equals(whichStatus) || "ListingQC".equals(whichStatus) || "Tally".equals(whichStatus) || "TallyQC".equals(whichStatus)) {
                    if (theImageData != null && !isBoundary(theImageData, FIRST_PAGE_OF_CHILD)) {
                        documentMenuItem.setEnabled(true);
                        childMenuItem.setEnabled(true);
                        clearMenuItem.setEnabled(false);
                    } else if (theImageData != null && theImageData.boundaryFlag.equals("C")) {
                        // child
                        documentMenuItem.setEnabled(true);
                        childMenuItem.setEnabled(false);
                        if (theCodingData != null && isBoundary(theCodingData, FIRST_CHILD_OF_BATCH)) {
                            clearMenuItem.setEnabled(false);
                        } else {
                            clearMenuItem.setEnabled(true);
                        }
                    } else {
                        // first page of range (D, ...)
                        documentMenuItem.setEnabled(false);
                        childMenuItem.setEnabled(theImageData != null && !isBoundary(theImageData,
                                FIRST_PAGE_OF_VOLUME));
                        if (theCodingData != null && isBoundary(theCodingData, FIRST_CHILD_OF_BATCH)) {
                            clearMenuItem.setEnabled(false);
                        } else {
                            clearMenuItem.setEnabled(true);
                        }
                    }
            }else if (!unitize && !whichStatus.equals("Binder")) {
                if (theImageData != null && theCodingData != null && theImageData.childId != theCodingData.childId) {
                    documentMenuItem.setEnabled(false);
                    childMenuItem.setEnabled(false);
                    clearMenuItem.setEnabled(false);
                }
            }
            
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void clearMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        changeBoundary(" ");
    }

    private void childMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        changeBoundary("C");
    }

    private void documentMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        changeBoundary("D");
    }

    private void changeBoundary(String boundaryString) {
        // TBD: For change to blank, should warn user that data will be
        // lost, if there are any non-default values
        try {
            if (saveDataCheck()) {
                sendBoundary(theImageData.pageId, boundaryString);
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void issueButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // Allow user to enter IS line for the LFP for the current page.
            AddEditIssue isDialog = new AddEditIssue((Component) issueButton, theCodingData.pageId);
            isDialog.show();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void absoluteGoButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            //Log.print("(SPV).absoluteGoButton pressed ========| text="
            //          +absolutePage.getText());
            if ((absolutePage.getValue() != 0) && saveDataCheck()) {
                getCoderData(0, absolutePage.getValue(), B_NONE);
                absolutePage.setText("0");
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void formFocusGained(java.awt.event.FocusEvent evt) {
        hideCurrentPopup();
    }

    private void rejectBatchMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (saveDataCheck()) {
                if (whichStatus.equals("QA")) {
                    doQACloseDialog(/* reject => */true);
                } else {
                    if (batchComments(/*showIfNoComments->*/true)) {
                        sendBatchStatus(/* closingBatch->*/false); // because it's being rejected

                    }
                }
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void closeBatchMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            closeBatchMenuItem.setEnabled(false);
            if (saveDataCheck()) {
                if (whichStatus.equals("QA")) {
                    doQACloseDialog(/* reject => */false);
                } else {
                    validateBatch(/* closeAfterValidate => */true);
                }
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void doQACloseDialog(boolean reject) {
        final JDialog dialog = new QACloseDialog(
                this, fileMenu, reject);
        dialog.setVisible(true);
        //Log.print("back from QACloseDialog: reject="+reject);
        // set to first remaining document or close viewer, if none
        final ClientTask task = new TaskExecuteQuery("qaClose.first_remaining_page");
        task.setCallback(new Runnable() {

            public void run() {
                try {
                    ResultSet rs = (ResultSet) task.getResult();
                    if (rs.next()) {
                        int pageId = rs.getInt(1);
                        getCoderData(pageId, 0, B_CHILD);
                    } else {
                        closeViewer();
                    }
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        });
        task.enqueue();
    }

    private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if ("Listing".equals(whichStatus)) {
                fieldComoBox.removeAllItems();
                closeViewer();
            } else if ("ListingQC".equals(whichStatus)) {
                fieldComoBox.removeAllItems();
                closeViewer();
            } else if ("TallyQC".equals(whichStatus)) {
                fieldComoBox.removeAllItems();
                closeViewer();
            } else if ("Tally".equals(whichStatus)) {
                fieldComoBox.removeAllItems();
                closeViewer();
            } else if (saveDataCheck()) {
                closeViewer();
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void assignListingQCMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            //JDialog dialog = new ProjectSelectionDialogForAssignListingQC(this,"Listing",projectName,volume,fields,projectId,volumeId);
            ProjectSelectionDialogForAssignListingQC dialog = new ProjectSelectionDialogForAssignListingQC(this, "Listing", projectName, volume, fields, projectId, volumeId);
            dialog.tabselect();
            this.setVisible(false);
            dialog.setVisible(true);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void assignTallyQCMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            //JDialog dialog = new ProjectSelectionDialogForAssignListingQC(this,"Tally",projectName,volume,fields,projectId,volumeId);
            ProjectSelectionDialogForAssignListingQC dialog = new ProjectSelectionDialogForAssignListingQC(this, "Tally", projectName, volume, fields, projectId, volumeId);
            dialog.tabselect();
            this.setVisible(false);
            dialog.setVisible(true);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void closeVolumeActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            final ClientTask task = new TaskCloseVolume(volumeId, whichStatus, projectId);
            task.setCallback(new Runnable() {

                public void run() {
                    Element reply = (Element) task.getResult();
                    // final ResultSet rs = Sql.resultFromXML(reply);
                    String action = reply.getNodeName();
                    if (T_FAIL.equals(action)) {
                        JOptionPane.showMessageDialog(SplitPaneViewer.this,
                                " open Error Dialog",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(SplitPaneViewer.this,
                                "Listing Batches has been Closed",
                                "MESSAGE",
                                JOptionPane.INFORMATION_MESSAGE);
                        closeViewer();
                    }
                }
            });
            task.enqueue();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void listingQCDoneActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            final ClientTask task = new TaskQcDone(projectId, volumeId, fieldName, whichStatus);
            closeViewer();
            task.enqueue();


        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void tallyQCDoneActionPerformed(java.awt.event.ActionEvent evt) {
        try {
//                  final ClientTask task = new TaskQcDone(projectId,volumeId,fieldName,whichStatus);
//                   closeViewer();
//                  task.enqueue();

            //paramenters for query "TallyQC.updateFieldsValue" are 
            // 1. tally_status
            // 2. tallyAssignementId            

            final ClientTask task = new TaskExecuteUpdate("TallyQC.closeGroup", "Completed", Integer.toString(tallyAssignementId));
            closeViewer();
            task.enqueue(this);


        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void countActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            JDialog dialog = new FieldValueCountDialog(this, whichStatus, projectId, volumeId);
            this.setVisible(false);
            dialog.setVisible(true);

        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    //// action for "Test Report" -- removed from menu
    //private void testReportActionPerformed(java.awt.event.ActionEvent evt) {                                           
    //    boolean ok = ClientThread.enqueue(new TaskTestReport("users select"));
    //}                                          
    /**
     * Show the project screen, then, if changes have been made to the project, reload the
     * dynamicPane with the new project definition.
     * @see     ui.SplitPaneViewer.copyAll
     */
    private void copyAllMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            copyAll();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void copyCurrentMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            copyField();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void validateBatchMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (saveDataCheck()) {
                validateBatch(/* closeAfterValidate => */false);
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void forceSaveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // Save the coded record without checking data validity.
            if (crossFieldEdit(/* force */true)) {
                save();
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void endAttachButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // View the last page of the current Range
        getViewerData(endAttachButton.getText());
    }

    private void beginAttachButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // View the first page of the current Range
        getViewerData(beginAttachButton.getText());
    }

    private void endDocButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // View the last page of the current child
        getViewerData(endDocButton.getText());
    }

    private void beginDocButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // View the first page of the current child
        getViewerData(beginDocButton.getText());
    }

    private void uPageNextButtonActionPerformed(java.awt.event.ActionEvent evt) {
        uPageNext();
    }

    private void uPagePrevButtonActionPerformed(java.awt.event.ActionEvent evt) {
        uPagePrev();
    }

    private void vZoomOutButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            ourViewer.zoomReduce();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void vZoomInButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            ourViewer.zoomEnlarge();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void vZoomWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {
        //Log.print("(SplitPaneViewer).vZoomWindowButtonActionPerformed ---");
        //ourViewer.zoomPercent(120);
        try {
            ourViewer.openMagWindow();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void vTurn90CounterwiseButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            ourViewer.rotateCounterClockwise();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void vTurn90ClockwiseButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            ourViewer.rotateClockwise();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void vScreenButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            ourViewer.zoomFitWindow();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void vVertButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            ourViewer.zoomFitHeight();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void vHorizButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            ourViewer.zoomFitWidth();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void vBeginImageRunButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (theImageData == null) {
                return;
            }
            if (timer == null) {
                timer = new Timer(IbaseConstants.ONE_SECOND, new ActionListener() {

                    public void actionPerformed(ActionEvent evt) {
                        try {
                            if (isBoundary(theImageData, LAST_PAGE_OF_VOLUME)) {
                                timer.stop();
                            // wait until no queued image for this viewer
                            } else if (!ImageThread.isBusy(ourViewer)) {
                                nextImage();
                            }
                        } catch (Throwable th) {
                            Log.quit(th);
                        }
                    }
                });
            }
            timer.start();
            CancelDialog cancelDialog = new CancelDialog((Component) viewerToolBar, /* modal ==> */ true);
            // keep this dialog in front of the coding screen
            cancelDialog.addFocusListener(new FocusAdapter() {

                public void focusLost(java.awt.event.FocusEvent evt) {
                    // on permanent loss of focus, keep on top
                    if (!evt.isTemporary()) {
                        ((JDialog) evt.getSource()).toFront();
                    }
                }
            });
            cancelDialog.setVisible(true);
            timer.stop();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void vAbsoluteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // Allow user to enter a bates number for coding.
            if (theImageData == null) {
                return;
            }
            //String bates = "";
            SelectBatesDialog selectDialog = new SelectBatesDialog(null, theImageData.batesNumber);
            // batesNumber = theImageData.batesNumber;
            selectDialog.setModal(true);
            selectDialog.show();

            Object[] selection = selectDialog.getSelection();
            if (selection != null) {
                getViewerData((String) selection[0]);
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void vLastCurrButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Show the last page of the current document (the last page before the first attachment).
        vLastCurr();
    }

    private void vLastCurr() {
        if (theImageData != null) {
            getViewerData(theImageData.pageId, ACTUAL, B_CHILD, true);
        }
    }

    private void vFirstCurrButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Show the first page of the current document.
        vFirstCurr();
    }

    private void vFirstCurr() {
        if (theImageData != null) {
            getViewerData(theImageData.pageId, ACTUAL, B_CHILD);
        }
    }

    private void vAttachNextButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Show the first page of the next doc set
        vDocNext();
    }

    private void vDocNext() {
        if (theImageData != null) {
            getViewerData(theImageData.pageId, PLUS_ONE, B_RANGE);
        }
    }

    private void vAttachPrevButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // get the first page of the previous doc set
        vDocPrev();
    }

    private void vDocPrev() {
        if (theImageData != null) {
            getViewerData(theImageData.pageId, MINUS_ONE, B_RANGE);
        }
    }

    private void vDocNextButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // get the first page of the next doc set's attachment
        vAttachNext();
    }

    private void vAttachNext() {
        if (theImageData != null) {
            getViewerData(theImageData.pageId, PLUS_ONE, B_CHILD);
        }
    }

    private void vDocPrevButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // get the first page of the previous doc set's attachment
        vAttachPrev();
    }

    private void vAttachPrev() {
        if (theImageData != null) {
            getViewerData(theImageData.pageId, MINUS_ONE, B_CHILD);
        }
    }

    private void vPageNextButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // get the next image without regard to doc sets
        nextImage();
    }

    private void nextImage() {
        if (theImageData != null) {
            getViewerData(theImageData.pageId, PLUS_ONE, B_NONE);
        }
    }

    private void vPagePrevButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // get the previous image without regard to doc sets
        vPagePrev();
    }

    private void vPagePrev() {
        if (theImageData != null) {
            getViewerData(theImageData.pageId, MINUS_ONE, B_NONE);
        }
    }

    /**
     * Show the BatchCommentsDialog to allow user to enter comments for this batch.
     */
    private void addBatchCommentButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            batchComments(/*showIfNoComments->*/true);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private boolean batchComments(boolean showIfNoComments) {
        if (theCodingData != null) {
            return BatchCommentsDialog.showDialog(this, /*showIfNoComments->*/ true);
        }
        return false;
    }

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (crossFieldEdit()) {
            go = false;
            save();
        }
    //saveAndGoButton.doClick();
    }

    private void firstNewButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (saveDataCheck()) {
                getCoderData(0, 0, 0); // all 0's means uncoded

            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void absoluteButtonActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            // Allow user to enter a relative image number for coding.
            if (saveDataCheck()) {
                SelectRelativeDialog selectDialog = new SelectRelativeDialog(null, String.valueOf(theCodingData.batchChildPosition), theCodingData.batchChildCount);
                selectDialog.setModal(true);
                selectDialog.show();

                Object[] selection = selectDialog.getSelection();
                if (selection != null) {
                    int relativePosition = Integer.parseInt((String) selection[0]);
                    getCoderData(0, relativePosition, B_CHILD);
                }
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void docLastButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // get the first page of the last child
        if (saveDataCheck()) {
            int lastDocPos = theCodingData.batchChildCount - theCodingData.batchChildPosition;
            getCoderData(theCodingData.pageId + lastDocPos, lastDocPos, B_CHILD);
        }
    }

    private void docNextButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // get the first page of the next doc set
        //Log.print("(SPV).docNextButtonActionPerformed enter ----------------");
        docNext();
    //Log.print("(SPV).docNextButtonActionPerformed exit -----------------");
    }

    private void docPrevButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // get the first page of the previous doc set
        docPrev();
    }

    private void docFirstButtonActionPerformed(java.awt.event.ActionEvent evt) {
        // Show the first page of the first document
        if (saveDataCheck()) {
            getCoderData(0, PLUS_ONE, B_CHILD);
        }
    }

    private void clear() {  // Alt+X
        //Log.print("(SplitPaneViewer).clearButtonActionPerformed");

        if (unitize) {
            saveAndGo = true;
            boundaryText.setText("");
            //saveAndGoButton.doClick();
            saveAndGoBoundaryButton.doClick();
        }
    }

    private void child() {  // Alt+I
        //Log.print("(SplitPaneViewer).childButtonActionPerformed");

        if (unitize) {
            saveAndGo = true;
            boundaryText.setText("C");
            //saveAndGoButton.doClick();
            saveAndGoBoundaryButton.doClick();
        }
    }

    private void doc() {   // Alt+D
        //Log.print("(SplitPaneViewer).docButtonActionPerformed");

        if (unitize) {
            saveAndGo = true;
            boundaryText.setText("D");
            //saveAndGoButton.doClick();
            saveAndGoBoundaryButton.doClick();
        }
    }
    //private void batch() {
    //    Log.print("(SplitPaneViewer).batchButtonActionPerformed");
    //    if (unitize) {
    //        batchingFlag = true;
    //        boundaryText.setText("D");
    //        saveAndGoButton.doClick();
    //    }
    //}

    private void uPagePrev() {
        if (saveDataCheck()) {
            getCoderData(theCodingData.pageId, MINUS_ONE, B_NONE);
        }
    }

    private void uPageNext() {
        if (saveDataCheck()) {
            getCoderData(theCodingData.pageId, PLUS_ONE, B_NONE);
        }
    }

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        try {
            // JAboutDialog Create with owner and show as modal
            {
                JAboutDialog JAboutDialog1 = new JAboutDialog(this);
                JAboutDialog1.setModal(true);
                JAboutDialog1.show();
            }
        } catch (Exception e) {
        }
    }

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        exitApplication();
    }

    private void docNext() {
        //Log.print("(SPV).docNext saveDataCheck >>>>>");
        go = true;  // in case of save action

        goToBoundary = true;  // in case of save action

        if (saveDataCheck()) {
            getCoderData(theCodingData.pageId, PLUS_ONE, B_CHILD);
        }
    }

    private void docPrev() {
        goPrev = true;
        if (saveDataCheck()) {
            getCoderData(theCodingData.pageId - 1, MINUS_ONE, B_CHILD);
        }
    }

    /**
     * Call ClientTask to enqueue a task to get the requested data.
     * The data is returned in common.imageData via the entry point, imageDataEntry.
     * @param bates number - requested bates number.
     *
     * @see viewerEntry
     * @see common.ImageData
     */
    private void getViewerData(String bates) {

        try {
            final ClientTask task = new TaskRequestImage(bates, whichStatus);
            setViewerCallback(task);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    /**
     * Call ClientTask to enqueue a task to get the requested data.
     * The data is returned in common.imageData via the entry point, imageDataEntry.
     * @param pageId - requested pageId or * "get image relative to" pageId.
     * @param offset - get the image +/- offset from pageId.  Zero for the
     * one described in pageId.
     * @param boundary - get the offset image that matches boundary
     * (Child, Document, blank).
     * @findEnd If true, find the last page at the given boundary level
     *
     * @see viewerEntry
     * @see common.ImageData
     */
    private void getViewerData(int pageId, int offset, int boundary) {
        getViewerData(pageId, offset, boundary, false);
    }

    private void getViewerData(int pageId, int offset, int boundary, boolean findLast) {
        try {
            final ClientTask task = new TaskRequestImage(
                    pageId, offset, boundary, whichStatus, findLast);
            //Log.print("(SPV).getViewerData pageId/offset " + pageId + "/" + offset);
            setViewerCallback(task);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void setViewerCallback(final ClientTask task) {
        task.setCallback(new Runnable() {

            public void run() {
                try {
                    ImageData data = (ImageData) task.getResult();
                    if (data != null) {
                        viewerEntry(data);
                    //Log.print("getViewerData: back from viewerEntry");
                    } else { // since data == null

                        viewerLabel.setText("Requested page not found");
                        viewerLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
                    }
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        });
        boolean ok = task.enqueue(this);
    //System.out.println("after viewer enqueue - ok="+ok);
    }

    /**
     * Call ClientTask to enqueue a task that will validate every record in the batch.
     * An OK or CodingData is returned by getResult() in the callback.
     *
     * @see common.CodingData
     * @see #codingEntry
     * @see #crossFieldEdit()
     */
    protected void validateBatch(final boolean closeAfterValidate) {
        Log.print("(SPV).validateBatch " + batchId + "/" + fieldCount);
        //final ClientTask task = new TaskValidateBatch(batchId, lowestInvalidPageId);

        if (fieldCount <= 0) {
            // no fields on the screen
            if (closeAfterValidate) {
                doCloseAfterValidate();
            } else {
                JOptionPane.showMessageDialog(null,
                        "Validate Batch ended successfully." + "\n\nNo fields to validate!",
                        "Validate Batch",
                        JOptionPane.INFORMATION_MESSAGE);
            //Log.print("validateBatch -- after dialog");
            }
            return;
        }

        final ClientTask task = new TaskValidateBatch(unitize, unitize ? 0 : activeGroup); // don't use groups when unitizing

        task.setCallback(new Runnable() {

            public void run() {
                try {
                    Object result = task.getResult();
                    if (result instanceof String) {
                        if (((String) result).equals(T_OK)) {
                            //validateBatchEntry();
                            //lowestInvalidPageId = lastPageOfRegion + 1;
                            statusLabel.setText("Validate batch successful!");
                            //Toolkit.getDefaultToolkit().beep();
                            //Log.print("Validate batch successful!");
                            if (closeAfterValidate) {
                                doCloseAfterValidate();
                            } else {
                                JOptionPane.showMessageDialog(null,
                                        "Validate Batch ended successfully." + "\n\nNo errors encountered!",
                                        "Validate Batch",
                                        JOptionPane.INFORMATION_MESSAGE);
                            //Log.print("validateBatch -- after dialog");
                            }
                        } else {
                            //lowestInvalidPageId = 0; // must start over
                            viewerLabel.setText("Validate batch failed -- see run log");
                            closeBatchMenuItem.setEnabled(true);
                            viewerLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
                        //Log.print("SplitPaneViewer.validateBatch returned notOK error ");
                        }
                    } else if (result instanceof CodingData) {
                        statusLabel.setText("Validate batch errors found.");
                        //Log.print("Validate batch errors found.");
                        // Get the coder data and do crossFieldEdits to display page errors.
                        CodingData data = (CodingData) result;
                        //lowestInvalidPageId = data.pageId;
                        codingEntry(data);
                        statusLabel.setText("Validate batch -- after codingEntry");
                        crossFieldEdit();
                        statusLabel.setText("Validate batch -- after crossFieldEdit");
                    } else {
                        //lowestInvalidPageId = 0; // must start over
                        viewerLabel.setText("Validate batch failed -- see run log");
                        closeBatchMenuItem.setEnabled(true);
                        viewerLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
                    //Log.print("SplitPaneViewer.validateBatch returned error ");
                    }
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        });
        boolean ok = task.enqueue(this);
    //System.out.println("after validate batch enqueue - ok="+ok);
    }

    private void doCloseAfterValidate() {
        //Log.print("doCloseAfterValidate");
System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaa         "+ whichStatus);
        String sqlName = null;
        if ("Coding".equals(whichStatus) || "CodingQC".equals(whichStatus)) {
            sqlName = "check_unsaved_coding";
        //} else if ("QA".equals(whichStatus)) {
        //    sqlName="check_unsaved_QA";
        }
        assert !"QA".equals(whichStatus);

        if (sqlName != null) {
            final ClientTask task = new TaskExecuteQuery(sqlName);
            task.setCallback(new Runnable() {

                public void run() {
                    try {
                        ResultSet queryResult = (ResultSet) task.getResult();
                        if (queryResult.next()) {
                            // There is an unsaved child
                            int pageId = queryResult.getInt(1);
                            //Log.print("... unsaved child "+pageId);
                            getCoderData(pageId, ACTUAL, B_CHILD);
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    JOptionPane.showMessageDialog(
                                            SplitPaneViewer.this,
                                            "Cannot close batch" + "\nThere are unsaved documents.",
                                            "Unsaved Document Error",
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            });
                            closeBatchMenuItem.setEnabled(true);
                        } else {
                            //Log.print("... ok, send status");
                            sendBatchStatus(/* closingBatch->*/true);
                        }
                    } catch (Throwable th) {
                        Log.quit(th);
                    }
                }
            });
            task.enqueue();
        } else {
            sendBatchStatus(/* closingBatch->*/true);
        }
    }

    /**
     * Call ClientTask to enqueue a task to save the batch status.
     * @param closingBatch - Indicate true or false, respectively, whether this batch is being
     * closed or rejected.
     */
    protected void sendBatchStatus(final boolean closingBatch) {
        final int holdBatchId = batchId;
        final ClientTask task = new TaskSendBatchStatus(whichStatus, closingBatch);
        //Log.print("(SPV).sendBatchStatusData " + batchId + "/" + whichStatus + "/" + closingBatch);
        task.setCallback(new Runnable() {

            public void run() {
                try {
                    // Show statistics before closing window (modal dialog)
                    String batchName = SplitPaneViewer.this.projectName + " Batch " + SplitPaneViewer.this.theCodingData.batchNumber;
                    assert (!"QA".equals(whichStatus));

                    StatisticsDialog dialog = new StatisticsDialog(
                            Global.mainWindow,
                            whichStatus,
                            holdBatchId,
                            closingBatch,
                            batchName);
                    dialog.setVisible(true);
                    int hours = dialog.getHours();

                    // If requeueing requested, tell the server
                    if (hours > 0) {
                        assert "CodingQC".equals(whichStatus);
                        ClientTask task = new TaskRequestRequeue(
                                holdBatchId, hours);
                        task.enqueue();
                    }

                    // Close the window.
                    // Note that batch has already been released.
                    Global.mainWindow = null;
                    SplitPaneViewer.this.closeViewer();
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        });
        task.enqueue(this);
    }

    // Never used -- was originally for use with first/last child/range
    // buttons on coding screen, but these now  move the image not the coding
    ///**
    //* Call ClientTask to enqueue a task to get the requested data.
    //* The data is returned in common.imageData via the entry point, imageDataEntry.
    //* @param bates number - requested bates number or
    //* "get image relative to" bates number.
    //* @param offset - get the image +/- offset from bates -- NOT uSED YET.  Zero for the
    //* one described in bates.
    //* @param boundary - get the offset image that matches boundary
    //* (Child, Document, blank).
    //*
    //* @see #codingEntry
    //* @see common.ImageData
    //* @see common.CodingData
    //*/
    //private void getCoderData(String bates, int offset, int boundary) {
    //    // offset not used yet
    //    try {
    //        final ClientTask task = new TaskRequestCoding(bates);
    //        setCoderCallback(task);
    //    } catch (Throwable th) {
    //        Log.quit(th);
    //    }
    //}
    /**
     * Call ClientTask to enqueue a task to get the requested data.
     * The data is returned in common.imageData via the entry point, imageDataEntry.
     * @param pageId - requested pageId or * "get image relative to" pageId.
     * @param offset - get the image +/- offset from pageId.  Zero for the
     * one described in pageId.
     * @param boundary - get the offset image that matches boundary
     * (Child, Document, blank).
     *
     * @see #codingEntry
     * @see common.ImageData
     * @see common.CodingData
     */
    protected void getCoderData(int pageId, int offset, int boundary) {
        try {
            Log.print("(SPV).getCoderData pageId/offset " + pageId + "/" + offset + " bdry=" + boundary);
            final ClientTask task = new TaskRequestCoding(pageId, offset, boundary, whichStatus);
            setCoderCallback(task);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    protected void getCoderData(int pageId, int offset, int boundary, String whichStatus, String bateno, int volumeId) {
        try {
            //Log.print("(SPV).getCoderData pageId/offset " + pageId + "/" + offset +" bdry="+boundary);
            flag = true;
            final ClientTask task = new TaskRequestCoding(pageId, offset, boundary, whichStatus, bateno, volumeId, flag);
            setCoderCallback(task);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    protected void getBoundaryCoderData(int pageId, int offset, int boundary) {
        try {
            Log.print("(SPV).getBoundaryCoderData pageId/offset " + pageId + "/" + offset + " bdry=" + boundary);
            // Note: only called during unitize and UQC, so don't care about whichStatus
            final ClientTask task = new TaskRequestCoding(pageId, offset, boundary, null);
            task.setCallback(new Runnable() {

                public void run() {
                    try {
                        CodingData data = (CodingData) task.getResult();
                        if (data != null) {
                            //Log.print("getBoundaryCoderData: coding data returned");
                            boundaryFromServer = true;
                            codingEntry(data);
                            boundaryFromServer = false;

                        //Log.print("getBoundaryCoderData: back from boundaryCodingEntry " + boundaryText.getText());
                        } else { // since data == null
                            // ??? What now ???

                            viewerLabel.setText("Requested coding page not found");
                            viewerLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
                            viewerLabel.setForeground(Color.red.darker());
                        //Log.print("getCoderData: null returned");
                        }
                    } catch (Throwable th) {
                        Log.quit(th);
                    }
                }
            });
            boolean ok = task.enqueue(this);
        //System.out.println("after boundary coding enqueue - ok="+ok);
        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    private void setCoderCallback(final ClientTask task) {
        task.setCallback(new Runnable() {

            public void run() {
                try {
                    CodingData data = (CodingData) task.getResult();
                    if (data != null) {
                        codingEntry(data);
                    } else { // since data == null
                        // ??? What now ???

                        viewerLabel.setText("Requested coding page not found");
                        viewerLabel.setFont(new Font("Dialog", Font.PLAIN, 14));
                        viewerLabel.setForeground(Color.red.darker());
                    //Log.print("getCoderData: null returned");
                    }
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        });
        task.enqueue(this);
    }

    /**
     * Entry point method to show the coding data and set the coding toolbar
     * based on data provided by client.ClientTask.
     *
     * @param codingData common.CodingData structure provides the
     * coding data for the display of a coded document.
     *
     * @see common.CodingData
     * @see common.ImageData
     * @see client.ClientTask
     * @see client.ClientThread
     */
    public void codingEntry(CodingData codingData) {
        if (codingData == null) {
            Log.write("SplitPaneViewer.codingEntry codingData is null");
            return;
        }
        // Log.print("(SplitPaneViewer).codingEntry " + codingData.pageId + " ProjectMap " + projectMap.size());
        theCodingData = codingData;
        LTextField obj = new beans.LTextField(codingData.childId, codingData);
        obj.setChildId(codingData.childId);

        LTextButton textButtonObj = new beans.LTextButton(codingData);
        LFormattedTextField formatTextField = new LFormattedTextField(codingData);
        
        LDateField dateField = new LDateField(codingData.childId, codingData);

        //set the flag for F10 
        if (null != area) {
            area.setImagePath(codingData.imagePath + codingData.filename);
            area.setBatesNumber(codingData.batesNumber);
            area.setChildId(codingData.childId);
            area.setBatchId(codingData.batchId);
            initDynamicComponents();
        }
        //set the flag for listing
        if ("Yes".equals(codingData.query_raised) || "Yes".equals(theCodingData.listing_marking) || "No".equals(theCodingData.listing_marking) ) {           
             treatmentLevel = theCodingData.treatment_level;
             showQueryRaised = codingData.query_raised;            
             setVisibleQueryTracker(true);
             setvisible = getVisibleQueryTracker();
             flag1 = true;
             if(!whichStatus.equals("Listing") &&  !"TallyQC".equals(whichStatus)){
               initDynamicComponents();  
             }                                  
        }    
       

        // Show the image and set the viewer buttons
        viewerEntry(codingData);
        //Log.print("(SplitPaneViewer).codingEntry back from viewerEntry");
        if ((whichStatus.equals("Unitize") || whichStatus.equals("UQC"))) {
            fileMenu.remove(openBinderMenuItem);
        }

        if ("Listing".equals(whichStatus)) {
            setTitle("Listing for project :" + getProjectName());
        } else if ("Tally".equals(whichStatus)) {
            setTitle("Tally for project :" + getProjectName());
        } else {
            setTitle(titlePrefix + ": " + projectName + ("Binder".equals(whichStatus) ? " BINDER"
                    : "QA".equals(whichStatus) ? " QA"
                    : " Batch " + codingData.batchNumber) + " [" + codingData.batchChildPosition + " of " + codingData.batchChildCount + "]");


        }
        //if (codingData.valueMap == null) {
        //    Log.print("(SPV).codingEntry valueMap is null");
        //} else {
        //    Log.print("(SPV).codingEntry valueMap is " + codingData.valueMap.size() + "/"
        //              + boundaryText.getText());
        //}
        if (!flag) {
            theFieldMapper.populateScreen(codingData.valueMap, projectMap, codingData.activeGroup, codingData.treatment_level, codingData);
            treatmentLevel = codingData.treatment_level;
            theFieldMapper.clearScreenChanged();
            theFieldMapper.setCheckboxUnselectedEnabled(false);
            if (codingData.errorFlagMap != null) {
                theFieldMapper.populateErrorScreen(codingData.errorFlagMap);
            }
            if (codingData.errorTypeMap != null) {
                theFieldMapper.populateErrorTypes(codingData.errorTypeMap);
            }
            boundaryText.setChanged(false);
        }
        // can't undo the initial population of the screen
        getUndoController().discardAllEdits();
        hideCurrentPopup();
        resetInputVerifier();

        // Set the coder-side button text
        beginDocButton.setText(codingData.firstBatesOfChild);
        endDocButton.setText(codingData.lastBatesOfChild);
        beginAttachButton.setText(codingData.firstBatesOfRange);
        endAttachButton.setText(codingData.lastBatesOfRange);

        // set the coder buttons
        if (isBoundary(theCodingData, LAST_CHILD_OF_BATCH)) {
            docNextButton.setEnabled(false);
            docLastButton.setEnabled(false);
        } else {
            docNextButton.setEnabled(true);
            docLastButton.setEnabled(true);
        }
        if (isBoundary(theCodingData, FIRST_CHILD_OF_BATCH)) {
            docFirstButton.setEnabled(false);
            docPrevButton.setEnabled(false);
        } else {
            docFirstButton.setEnabled(true);
            docPrevButton.setEnabled(true);
        }
        if (unitize) {
            if (isBoundary(theCodingData, LAST_PAGE_OF_BATCH)) {
                uPageNextButton.setEnabled(false);
            } else {
                uPageNextButton.setEnabled(true);
            }
            if (isBoundary(theCodingData, FIRST_PAGE_OF_BATCH)) {
                uPagePrevButton.setEnabled(false);
            } else {
                uPagePrevButton.setEnabled(true);
            }
        }
        if (unitize) {
            boundaryBatesLabel.setText(theCodingData.currentBatesOfChild);
        }
        if (saveAndGoButton.hasFocus()) {
            if (unitize) {
                boundaryText.requestFocus();
            } else if (projectMap != null && projectMap.size() > 0) {
                projectMap.getComponent(0).requestFocus();
            }
        } else if (saveAndGoBoundaryButton.hasFocus()) {
            // Note.  Should always be unitize; check anyway
            if (unitize) {
                boundaryText.requestFocus();
            }
        }
        if (editButton.hasFocus()) {
            if (unitize) {
                boundaryText.requestFocus();
            } else if (projectMap != null && projectMap.size() > 0) {
                projectMap.getComponent(0).requestFocus();
            }
        }
    }

    /**
     * Entry point method to show the image and set the image toolbar
     * based on data provided by client.ClientTask.
     *
     * @param data common.ImageData structure provides the
     * image displayed on the viewer side and the id's required to
     * set the viewer-side buttons.
     *
     * @see common.CodingData
     * @see common.ImageData
     * @see client.ClientTask
     * @see client.ClientThread
     */
    public void viewerEntry(ImageData data) {
        if (data == null) {
            Log.write("SplitPaneViewer.viewerEntry data is null");
            return;
        }
        //batch_number = data.documentNumber;
        //System.out.println("documentNumber---------------->" + documentNumber);
        Log.print("(SplitPaneViewer).viewerEntry " + " " + data.imagePath + "/" + data.path + "/" + data.filename + " " + Integer.toHexString(data.boundaryInfo));
        theImageData = data;
        // setQueryRaised(data.queryRaised);

        // show the image in the viewer
        //
        // data.imagePath is volume.image_path
        // data.path is page.path
        // data.filename is page.filename
        //
        doViewFromSource(data);

        // Update boundaryText only if the coder page is the one in the viewer.
        if (unitize) {
            if (!boundaryFromServer && theCodingData.pageId == theImageData.pageId) {
                // put the boundary flag on the screen
                fromServer = true;
                //Log.print("(SplitPaneViewer).viewerEntry set boundaryText");
                boundaryText.setText(theImageData.boundaryFlag);
                fromServer = false;
            }
        }

        // set the viewer label identifying the image
        viewerLabel.setText(" " + theImageData.batesNumber + " (" + theImageData.childImagePosition + " of " + theImageData.childImageCount + ")");
        viewerLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        // if the image is not part of the current child, turn the label red
        if (theImageData.childId != theCodingData.childId) {
            viewerLabel.setForeground(Color.red.darker());
        } else {
            if (unitize && !theCodingData.firstBatesOfChild.equals(theImageData.batesNumber)) {
                viewerLabel.setForeground(Color.yellow.darker());
                viewerLabel.setToolTipText("Bates of the image in the viewer.\nBlue - Boundary page and the document being coded. \nYellow - Part of the document being coded,\n         not the boundary page. \nRed - Not part of the document being coded");
            } else {
                viewerLabel.setForeground(Color.blue.darker());
                viewerLabel.setToolTipText("Bates of the image in the viewer.\nBlue - Part of the document being coded. \nRed - Not part of the document being coded");
            }
        }

        //Log.print("SplitPaneViewer.viewerEntry set buttons " + boundaryText.getText());
        if (!unitize) {
            // set the viewer buttons
            if (isBoundary(theImageData, LAST_CHILD_OF_VOLUME)) {
                vDocNextButton.setEnabled(false);
            } else {
                //Log.print("enable vDocNextButton");
                vDocNextButton.setEnabled(true);
            }
            if (isBoundary(theImageData, FIRST_CHILD_OF_VOLUME)) {
                vDocPrevButton.setEnabled(false);
            } else {
                //Log.print("enable vDocPrevButton");
                vDocPrevButton.setEnabled(true);
            }
            if ("Binder".equals(whichStatus) || isBoundary(theImageData, LAST_RANGE_OF_VOLUME)) {
                vAttachNextButton.setEnabled(false);
            } else {
                //Log.print("enable vAttachNextButton");
                vAttachNextButton.setEnabled(true);
            }
            if ("Binder".equals(whichStatus) || isBoundary(theImageData, FIRST_RANGE_OF_VOLUME)) {
                vAttachPrevButton.setEnabled(false);
            } else {
                //Log.print("enable vDocPrevButton");
                vAttachPrevButton.setEnabled(true);
            }
            if (isBoundary(theImageData, FIRST_PAGE_OF_VOLUME)) {
                vPagePrevButton.setEnabled(false);
            } else {
                vPagePrevButton.setEnabled(true);
            }
            if (isBoundary(theImageData, LAST_PAGE_OF_VOLUME)) {
                // if it's the last page
                vPageNextButton.setEnabled(false);
            } else {
                vPageNextButton.setEnabled(true);
            }
            if (isBoundary(theImageData, FIRST_PAGE_OF_CHILD)) {
                vFirstCurrButton.setEnabled(false);
            } else {
                vFirstCurrButton.setEnabled(true);
            }
            if (isBoundary(theImageData, LAST_PAGE_OF_CHILD)) {
                vLastCurrButton.setEnabled(false);
            } else {
                vLastCurrButton.setEnabled(true);
            }
        }
        Log.print("SplitPaneViewer.viewerEntry after set buttons " + boundaryText.getText());
    }

    /**
     * Call ClientTask to enqueue a task to save the coding data.
     * @param pageId Requested pageId or * "get image relative to" pageId.
     * @param valueMap Update the database with the values contained in this Map.
     * Map is a hashMap containing fieldname/value for each field.
     *
     * @see "sendCodingDataEntry"
     * @see model.FieldMapper
     */
    protected void sendCodingData(int pageId, Map valueMap) {
        saveSuccessful = false;
        //Log.print("(SPV.sendCodingData) page " + pageId);
        // null boundaryFlag does means no update on the server
        String boundaryFlag = null;  // changed value of boundary, or null if no change

        if (unitize) {
            //Log.print("(SPV.sendCodingData) unitize");
            if (!((boundaryText.getText()).toUpperCase()).equals(theCodingData.boundaryFlag)) {
                boundaryFlag = (boundaryText.getText()).toUpperCase();
            }
        } else if (boundaryText.isChanged()) {
            //Log.print("(SPV.sendCodingData) boundary changed");
            if (!((boundaryText.getText()).toUpperCase()).equals(theImageData.boundaryFlag)) {
                boundaryFlag = (boundaryText.getText()).toUpperCase();
            }
        }
        Map errorMap = null;
        Map errorTypeMap = null;
        if ("QA".equals(whichStatus)) {
            errorMap = theFieldMapper.populateErrorRow();
            errorTypeMap = theFieldMapper.getFieldErrorType();
        }
        final ClientTask task = new TaskSendCodingData(pageId, boundaryFlag, valueMap, theCodingData.valueMap, errorMap, errorTypeMap, whichStatus);
        //Log.print("(SPV).sendCodingData pageId/boundaryFlag " + pageId + "/" + boundaryFlag);
        task.setCallback(new Runnable() {

            public void run() {
                try {
                    String ok = (String) task.getResult();
                    //Log.print("sendCodingData: pageId returned " + ok);
                    if (ok.equals(T_OK)) {
                        sendCodingDataEntry();
                    //Log.print("sendCodingData: back from sendCodingDataEntry");
                    } else {
                        statusLabel.setText("Unsuccessful add task.");
                    }
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        });
        task.enqueue(this);
    }

    /**
     * Do the GUI processing after receiving notification that the save task
     * has completed.
     * If an OK message is returned from the task, a call is made to
     * sendCodingDataEntry to do the GUI processing.
     * @param pageId The pageId of the codingData that should be retrieved.
     *
     * @see sendCodingData
     * @see sendCodingDataCallback
     * @see table.FieldMapper
     */
    private void sendCodingDataEntry() {
        theFieldMapper.clearScreenChanged();
        saveSuccessful = true;
        // If the save was successful, record must be validated with batch -- save the id.

        if (go) { // User clicked Save & Go

            if (unitize || boundaryText.isChanged()) {
                // if unitizing, get next image
                if (isBoundary(theCodingData, LAST_PAGE_OF_BATCH)) {                    
                    statusLabel.setText("End of unitization batch");
                    // If the save updated a boundary, reload the data.
                    // !!! TODO !!! reload without getting the data and image
                   // getCoderData(theCodingData.pageId, ACTUAL, B_NONE);
                // Not here -- gets cleared after data read
                //statusLabel.setText("End of unitization batch");
                }else if (isBoundary(theCodingData, LAST_CHILD_OF_BATCH)) {                    
                    statusLabel.setText("End of unitization batch");
                } else {
                    if (goToBoundary) {
                        // Alt+N - Save & Go to Boundary
                        getCoderData(theCodingData.pageId, PLUS_ONE, B_CHILD);
                    } else {
                        // Unitizer has pressed ctl+n for Save & Go to Page
                        getCoderData(theCodingData.pageId, PLUS_ONE, B_NONE);
                    }
                }
            } else {
                if (isBoundary(theCodingData, LAST_CHILD_OF_BATCH)) {
                    statusLabel.setText("End of coding batch");
                } else {
                    // go to next document
                    // For QA, B_UNCODED indicates next uncoded child
                    getCoderData(theCodingData.pageId, PLUS_ONE, ("QA".equals(whichStatus) ? B_UNCODED : B_CHILD));
                }
            }
        } else {
            if (unitize || boundaryText.isChanged()) {
                // If the save updated a boundary, reload the data.
                // !!! TODO !!! reload without getting the data and image
                getCoderData(theCodingData.pageId, ACTUAL, B_NONE);
            }
            statusLabel.setText(statusString);
        }
    }

    /**
     * Call ClientTask to enqueue a task to change the boundaryFlag.
     * @param pageId Requested pageId.
     * @param boundaryString The new boundary.
     */
    protected void sendBoundary(final int pageId, final String boundaryString) {
        final ClientTask task = new TaskSendBoundary(pageId, boundaryString);
        //Log.print("(SPV).sendBoundary pageId " + pageId+" boundaryString="+boundaryString);
        task.setCallback(new Runnable() {

            public void run() {
                try {
                    String ok = (String) task.getResult();
                    //Log.print("sendBoundary: returned " + ok);
                    if (ok.equals(T_OK)) {
                        // Make the coding data correct
                        // TBD: Should avoid reading the image
                        getCoderData(pageId, ACTUAL, B_NONE);
                        if (boundaryString.length() == 0) {
                            theFieldMapper.setCheckboxUnselectedEnabled(true);
                        }
                    } else {
                        statusLabel.setText("Unsuccessful boundary change.");
                    }
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        });
        task.enqueue(this);
    }

    /**
     * Call ClientTask to enqueue a task to retrieve the data for the previous child.
     * This is used by the copy all, copy field and boundaryTextChange functions.
     * @param pageId Current pageId.
     *
     * @see model.FieldMapper
     */
    protected void getPageValues(int pageId, int offset) {
        final ClientTask task = new TaskRequestCodingValues(pageId, offset, B_CHILD);
        //Log.print("(SPV).getPageValues pageId " + pageId);
        task.setCallback(new Runnable() {

            public void run() {
                try {
                    Map map = (Map) task.getResult();
                    if (map != null) {
                        //Log.print("(SPV).getPageValues: map returned " + map.size());
                        getPageValuesEntry(map);
                    } else {
                        statusLabel.setText("Data for previous document not available.");
                    }
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        });
        boolean ok = task.enqueue(this);
    //System.out.println("after getPageValues enqueue - ok="+ok);
    }

    private void getPageValuesEntry(Map map) {
        if (copyField == null) {
            theFieldMapper.populateScreen(map, projectMap, activeGroup, " ", null);
            // can't undo the initial population of the screen
            getUndoController().discardAllEdits();
        } else {
            //Log.print("(SPV).getPageValuesEntry " + copyField.getName());
            theFieldMapper.populateScreenField(copyField.getName(), map);
            copyField = null;
        }
        hideCurrentPopup();
        resetInputVerifier();
    }

    /**
     * Call the viewer to render an image.
     * @param filename - the fully-qualified filename of the image to be retrieved from the server.
     * @return Return the success or failure of the rendering of the image.
     * (Returns only true until the viewer is able to return an indicator.)
     */
    private boolean doViewFromSource(ImageData data) {

        String filename;
        if (activeGroup == 1) {
            // for BRS, group one uses a different image.
            Log.print("(SPV.doViewFromSource) " + data.imagePath + "/" + data.groupOnePath + "/" + data.groupOneFilename);
            filename = data.imagePath + "/" + data.groupOnePath + "/" + data.groupOneFilename;
        } else {
            filename = data.imagePath + "/" + data.path + "/" + data.documentNumber + "/" + data.filename;
        }
        ImageThread.renderImage(ourViewer, filename, data.offset, resolution, data.serverIP_port);
        setImagePath(filename);
        return true;
    }

    void Constrain(java.awt.Container container, java.awt.Component component, int gridx, int gridy, int gridwidth, int gridheight, int fill, int anchor, int weightx, int weighty, int top, int left, int bottom, int right) {
        java.awt.GridBagConstraints c = new java.awt.GridBagConstraints();

        if (gridy == -2) {
            gridYPos = gridYPos + gridheight;
            gridy = gridYPos;
            gridXPos = 0;
        }
        if (gridx == -1) {
            gridx = gridXPos;
        }
        if (gridy == -1) {
            gridy = gridYPos;
        }
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = gridwidth;
        c.gridheight = gridheight;
        gridXPos = gridx + gridwidth;
        c.fill = fill;
        c.anchor = anchor;
        if (weightx == 0) {
            c.weightx = 0.0;
        } else {
            c.weightx = weightx;
        }
        if (weighty == 0) {
            c.weighty = 0.0;
        } else {
            c.weighty = weighty;
        }
        if (top + left + bottom + right > 0) {
            c.insets = new java.awt.Insets(top, left, bottom, right);
        }
        ((java.awt.GridBagLayout) container.getLayout()).setConstraints(component, c);
        container.add(component);
    }

    ///**
    // * Set the changedFlag of every coding-side field.
    // * @param flag True or False.
    // */
    //protected void setScreenChanged(boolean flag) {
    //    for (int i = 0; i < projectMap.size()
    //          && i < projectMap.component.size(); i++) {
    //        ((LField)projectMap.getComponent(i)).setChanged(flag);
    //    }
    //}
    /** Check for required save before operation.
     *  Give user the option of cancelling the operation.
     * @return false = don't proceed with operation, true = proceed
     */
    public boolean saveDataCheck() {
        if (theFieldMapper == null) {
            // probably this is the initial load of the screen, just ignore it.
            return false;
        }
        if (fieldCount <= 0) {
            // no fields on the screen
            return true;
        }
        checkCloseAllDialogs();
        LField changed = theFieldMapper.firstChanged();
        //Log.print("(SPV.saveDataCheck()) enter " + changed);
        //Log.print("                            " + unitize);
        //Log.print("(SPV).saveDataCheck text/flag " + boundaryText.getText());
        //Log.print("          /" + theCodingData.boundaryFlag);
        if (changed == null && unitize && !((boundaryText.getText()).toUpperCase()).equals(theCodingData.boundaryFlag)) {
            // No fields are required when unitizing,
            // but see if the boundary field has changed.
            changed = boundaryText;
        }
        if (changed != null) {
            //Toolkit.getDefaultToolkit().beep();
            //Log.print("first changed " + fields.firstChanged());
            Object[] options = {"Save",
                "Don't Save",
                "Cancel"
            };
            int response = JOptionPane.showOptionDialog(this,
                    "You have changed the data on the current screen." + "\n Your options are: " + "\n                 \"Save\" to save your changes and continue, " + "\n                 \"Don't Save\" to continue without saving your changes, and" + "\n                 \"Cancel\" to continue editing the data on this screen.",
                    "Warning",
                    JOptionPane.YES_NO_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]);
            if (response == JOptionPane.YES_OPTION) {
                //Log.print("do save");
                //if (go) {
                //    saveAndGoButton.doClick();
                //} else {
                if (crossFieldEdit()) {
                    saveButton.doClick();
                    return true;
                } else {
                    return false;
                }
            //}
            } else if (response == JOptionPane.CANCEL_OPTION) {
                // Cancel
                // cursor on first changed field
                changed.requestFocus();
                closeBatchMenuItem.setEnabled(true);
                return false;
            } else {
                // User says to continue and lose changes.
                theFieldMapper.clearScreen(treatmentLevel);
                theFieldMapper.clearScreenChanged();
            }
        }
        return true;
    }
    protected Action savePageAction = new AbstractAction("Save & Go To Next Page") {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                //Log.print("Save and go to Page pressed");
                saveAndGo = false;
                if (crossFieldEdit()) {
                    go = true;
                    goToBoundary = false;
                    save();
                }
            } catch (Throwable th) {
                Log.quit(th);
            }
        }
    };
    protected Action saveAction = new AbstractAction("Save & Go") {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                System.out.println("inside save and go method---------------------->");
                //Log.print("Save and go pressed");
                saveAndGo = false;
                if (crossFieldEdit()) {
                    go = true;
                    goToBoundary = true;
                    save();
                }
            } catch (Throwable th) {
                Log.quit(th);
            }
        }
    };
    protected Action editAction = new AbstractAction("Edit") {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                System.out.println("inside Edit method---------------------->");
                //Log.print("Save and go pressed");
                theFieldMapper.clearScreen(treatmentLevel);
            } catch (Throwable th) {
                Log.quit(th);
            }
        }
    };
    //protected Action saveOnlyAction = new AbstractAction("Save")
    //    {
    //        public void actionPerformed(java.awt.event.ActionEvent A) {
    //            Log.print("Save");
    //            go = false;
    //            save();
    //        }
    //    };
/*
    class UndoAction extends AbstractAction {
    public UndoAction() {
    super("Undo");
    setEnabled(false);
    }
    
    public void actionPerformed(ActionEvent e) {
    try {
    //Log.print("undo action " + (JComponent)KeyboardFocusManager
    //            .getCurrentKeyboardFocusManager().getFocusOwner() + "####"
    //          + ((JComponent)((PlainDocument)undo.getSource())
    //                        .getProperty((Object)"field"))
    //          + "^^^^" + undo.getUndoPresentationName());
    
    //if ((JComponent)((PlainDocument)undo.getSource()).getProperty((Object)"field") != (JComponent)KeyboardFocusManager
    //            .getCurrentKeyboardFocusManager().getFocusOwner()) {
    Log.print("(SPV).undoAction requestFocus");
    ((JComponent)undo.getSource()).requestFocus();
    //}
    undo.undo();
    } catch (CannotUndoException ex) {
    System.out.println("Unable to undo: " + ex);
    ex.printStackTrace();
    }
    updateUndoState();
    redoAction.updateRedoState();
    }
    
    protected void updateUndoState() {
    if (undo.canUndo()) {
    setEnabled(true);
    putValue(Action.NAME, undo.getUndoPresentationName());
    } else {
    setEnabled(false);
    putValue(Action.NAME, "Undo");
    }
    }
    }
    
    class RedoAction extends AbstractAction {
    public RedoAction() {
    super("Redo");
    setEnabled(false);
    }
    
    public void actionPerformed(ActionEvent e) {
    try {
    undo.redo();
    } catch (CannotRedoException ex) {
    System.out.println("Unable to redo: " + ex);
    ex.printStackTrace();
    }
    updateRedoState();
    undoAction.updateUndoState();
    }
    
    protected void updateRedoState() {
    if (undo.canRedo()) {
    setEnabled(true);
    putValue(Action.NAME, undo.getRedoPresentationName());
    } else {
    setEnabled(false);
    putValue(Action.NAME, "Redo");
    }
    }
    }
     */
    /**
     * The Action which, for the current page, gets the values from the previously-saved page.
     */
    protected Action copyAllAction = new AbstractAction("Copy All") {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                copyAll();
            } catch (Throwable th) {
                Log.quit(th);
            }
        }
    };

    /**
     * For the current page, get the values from the previously-saved page.
     */
    private void copyAll() {
        //Log.print("Copy All");
        // copy all from previous record
        copyField = null;
        if (savedPageId != 0) {
            getPageValues(savedPageId, ACTUAL);
        }
    }
    /**
     * The action which, for the current field, gets the value from the focused field in the
     * previously-saved page or, if that doesn't exist, the previous page.
     */
    protected Action copyFieldAction = new AbstractAction("Copy Field") {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                copyField();
            } catch (Throwable th) {
                Log.quit(th);
            }
        }
    };

    /**
     * For the current field, get the value from the focused field in the
     * previously-saved page or, if that doesn't exist, the previous page.
     */
    private void copyField() {
        //Log.print("Copy Field");
        // copy one field from previous record
        copyField = (JComponent) KeyboardFocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
        //Log.print("(SPV).copyFieldAction fieldname " + copyField + "----" + copyField.getName());
        if (savedPageId == 0) {
            // if no previous save, get the preceding page's values
            getPageValues(theCodingData.pageId, MINUS_ONE);
        } else {
            getPageValues(savedPageId, ACTUAL);
        }
    }

    /**
     * Save the coding data for the current page, after closing any open
     * dialogs.
     */
    private void save() {
        Log.print("(SPV.save) coding/image " + theCodingData.pageId + "/" + theImageData.pageId);
        checkCloseAllDialogs();
        if (theCodingData.valueMap == null) {
            statusString = "Add successful";
        } else {
            statusString = "Update successful";
        }
        //if (! unitize
        //    && boundaryText.isChanged()) {
        //    if (boundaryText.getText().equals("")) {
        //        //Log.print("(SPV.save) clearBoundary");
        //        clearBoundary(theImageData.pageId);
        //    }
        //    //Log.print("(SPV.save) sendCodingData " + boundaryText.getText());
        //    sendCodingData(theImageData.pageId, theFieldMapper.populateRow());
        //} else {
        //    //Log.print("(SPV.save) sendCodingData 2");
        //    sendCodingData(theCodingData.pageId, theFieldMapper.populateRow());
        //    // Save the pageId for use in the copyField and copyAll functions.
        //    savedPageId = theCodingData.pageId;
        //}
        sendCodingData(theCodingData.pageId, theFieldMapper.populateRow());
        // since data being sent to server, screen is no longer changed
        // TBD: is this safe?  what if there's an error?
        theFieldMapper.clearScreenChanged();
        // Save the pageId for use in the copyField and copyAll functions.
        savedPageId = theCodingData.pageId;
        originalPageId = 0;
    }

    // Not currently used
    ///** User has pressed Delete.  Verify the action.
    // */
    //public int deleteDialog() {
    //    try {
    //        //Log.write("in deleteDialog");
    //        Toolkit.getDefaultToolkit().beep();
    //        Log.print("BEEP> SplitPaneViewer.deleteDialog");
    //        Object[] options = {"Yes"
    //                          , "No"};
    //        return JOptionPane.showOptionDialog(this,
    //                "Do you want to delete this document?",
    //                "Delete Verification",
    //                JOptionPane.YES_NO_OPTION,
    //                JOptionPane.QUESTION_MESSAGE,
    //                null,
    //                options,
    //                options[1]);
    //    } catch (Throwable th) {
    //        Log.quit(th);
    //        return 0;
    //    }
    //}
    protected Action nextDocAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            //Log.print("(SplitPaneViewer).nextDocAction event is RIGHT ");
            //if (unitize){
            //    uPageNext();
            //} else {
            docNext();
        //}
        }
    };
    /**
     * Used in UQC mode with ctrl+-> to go to next boundary
     */
    protected Action nextDocPageAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            //Log.print("(SplitPaneViewer).nextDocBoundaryAction event is RIGHT ");
            uPageNext();
        //docNext();
        }
    };
    protected Action homeAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            Log.print("(SplitPaneViewer).homeAction ");
            if (unitize) {
                boundaryText.requestFocus();
            } else {
                projectMap.getComponent(0).requestFocus();
            }
        }
    };
    protected Action vPagePrevAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            Log.print("(SplitPaneViewer).F1Action ");
            vPagePrev();
        }
    };
    protected Action vPageNextAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            Log.print("(SplitPaneViewer).F2Action ");
            nextImage();
        }
    };
    protected Action vAttachPrevAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            Log.print("(SplitPaneViewer).F3Action ");
            vAttachPrev();
        }
    };
    protected Action vAttachNextAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            Log.print("(SplitPaneViewer).F4Action ");
            vAttachNext();
        }
    };
    protected Action vDocPrevAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            Log.print("(SplitPaneViewer).F5Action ");
            vDocPrev();
        }
    };
    protected Action vDocNextAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            Log.print("(SplitPaneViewer).F6Action ");
            vDocNext();
        }
    };
    protected Action vFirstCurrAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            Log.print("(SplitPaneViewer).F7Action ");
            vFirstCurr();
        }
    };
    protected Action vLastCurrAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            Log.print("(SplitPaneViewer).F8Action ");
            vLastCurr();
        }
    };
    protected Action prevDocAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            //Log.print("(SplitPaneViewer).prevDocAction event is LEFT ");
            //if (unitize){
            //    uPagePrev();
            //} else {
            docPrev();
        //}
        }
    };
    /**
     * Used in UQC mode with ctrl+<- to go to prev boundary
     */
    protected Action prevDocPageAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            //Log.print("(SplitPaneViewer).prevDocBoundaryAction event is LEFT ");
            uPagePrev();
        //docPrev();
        }
    };
    protected Action clearAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            clear();
        }
    };
    protected Action docAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            doc();
        }
    };
    protected Action childAction = new AbstractAction() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            child();
        }
    };

    private void checkCloseAllDialogs() {
        if (projectMap != null && projectMap.size() > 0) {
            for (int i = 0; i < projectMap.component.size(); i++) {
                checkCloseDialog(projectMap.getComponent(i));
            }
        }
    }

    private void checkCloseDialog(Component comp) {
        //Log.print("(SPV.checkCloseDialog) is " + comp.getClass());
        if (comp != null && comp.getParent() instanceof beans.LTextButton) {
            ((beans.LTextButton) comp.getParent()).closeDialog();
        } else if (comp != null && comp instanceof beans.LTextButton) {
            ((beans.LTextButton) comp).closeDialog();
        }
    }

    /**
     * Hide the popup if the currently-focused component is an IbaseTextField.
     */
    private void hideCurrentPopup() {
        Component currentFocus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        //Log.print("(SPV).hideCurrentPopup " + currentFocus.getParent() + "/"
        //          + (IbaseTextField.getInstance()).getTextField().getParent());
        if (currentFocus != null && currentFocus.getParent() instanceof IbaseTextField) {
            //Log.print("(SPV) >>>>> isInstance = true");
            ((IbaseTextField) currentFocus.getParent()).hidePopup();
        }
    }

    /**
     * Reset the input verifier if the currently-focused component has one.
     */
    private void resetInputVerifier() {
        Component currentFocus = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        if (currentFocus != null && currentFocus.getParent() != null && (currentFocus.getParent() instanceof LTextField || currentFocus.getParent() instanceof beans.LDateField)) {
            if (currentFocus instanceof JComponent) {
                Object obj = ((JComponent) currentFocus).getInputVerifier();
                if (obj != null) {
                    if (obj instanceof MinMaxVerifier) {
                        ((MinMaxVerifier) obj).setChecked(false);
                    } else if (obj instanceof MaskFieldVerifier) {
                        ((MaskFieldVerifier) obj).setChecked(false);
                    }
                }
            }
        }
    }

    private class ProjectFieldsTableClass extends JTable {

        public String getToolTipText(MouseEvent event) {
            return ToolTipText.getToolTipText(event, fieldsTable);
        }

        public Point getToolTipLocation(MouseEvent event) {
            return ToolTipText.getToolTipLocation(event, fieldsTable);
        }
    }

    private class ProjectMarkingTableClass extends JTable {

        public String getToolTipText(MouseEvent event) {
            return ToolTipText.getToolTipText(event, markingTable);
        }

        public Point getToolTipLocation(MouseEvent event) {
            return ToolTipText.getToolTipLocation(event, markingTable);
        }
    }
    // Variables declaration - do not modify                     
    private javax.swing.JMenuItem AboutMenuItem;
    private javax.swing.JButton absoluteButton;
    private javax.swing.JButton absoluteGoButton;
    private beans.LIntegerField absolutePage;
    private javax.swing.JPanel absolutePane;
    private javax.swing.JButton addBatchCommentButton;
    private javax.swing.JMenuItem addToBinderItem;
    private javax.swing.JMenuItem createQAIRMenuItem;
    protected javax.swing.JPanel attachPane;
    private javax.swing.JButton beginAttachButton;
    private javax.swing.JButton beginDocButton;
    private javax.swing.JLabel binderLabel;
    private javax.swing.JLabel boundaryBatesLabel;
    private javax.swing.JLabel boundaryLabel;
    private javax.swing.JMenu boundaryMenu;
    private javax.swing.JPanel boundaryPane;
    private beans.LTextField boundaryText;
    private javax.swing.JMenuItem childMenuItem;
    private javax.swing.JMenuItem clearMenuItem;
    private javax.swing.JMenuItem closeBatchMenuItem;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JMenuBar coderMenu;
    protected javax.swing.JPanel coderPane;
    private javax.swing.JPanel coderPanel;
    private javax.swing.JToolBar coderToolBar;
    private javax.swing.ButtonGroup codingButtonGroup;
    private javax.swing.JRadioButtonMenuItem codingLeftButton;
    private javax.swing.JRadioButtonMenuItem codingRightButton;
    private javax.swing.JRadioButtonMenuItem codingUndockedButton;
    private javax.swing.JMenuItem copyAllMenuItem;
    private javax.swing.JMenuItem copyCurrentMenuItem;
    private javax.swing.JButton docFirstButton;
    private javax.swing.JButton docLastButton;
    private javax.swing.JButton docNextButton;
    protected javax.swing.JPanel docPane;
    private javax.swing.JButton docPrevButton;
    private javax.swing.JMenuItem documentMenuItem;
    private javax.swing.JRadioButtonMenuItem draftResButton;
    private beans.LGridBag dynamicPane;
    private javax.swing.JMenu editMenu;
    private javax.swing.JButton endAttachButton;
    private javax.swing.JButton endDocButton;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JButton firstNewButton;
    private javax.swing.JMenuItem forceSaveMenuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JRadioButtonMenuItem highResButton;
    private javax.swing.JLabel hyphen1;
    private javax.swing.JLabel hyphen11;
    private javax.swing.JLabel hyphen111;
    private javax.swing.JLabel hyphen112;
    private javax.swing.JLabel hyphen12;
    private javax.swing.JLabel hyphen2;
    private javax.swing.JButton issueButton;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator61;
    private javax.swing.JSeparator jSeparator62;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JRadioButtonMenuItem lowResButton;
    private javax.swing.JButton mailButton;
    private javax.swing.JRadioButtonMenuItem mediumResButton;
    private javax.swing.JScrollPane midScrollPane;
    private javax.swing.JMenuItem openBinderMenuItem;
    private javax.swing.JMenuItem openByBatchItem;
    private javax.swing.JMenuItem openByCoderItem;
    private javax.swing.JMenu payrollDetailReportMenu;
    private javax.swing.JMenuItem payrollDetailSelfItem;
    private javax.swing.JMenuItem payrollDetailSelfItem1;
    private javax.swing.JMenuItem payrollDetailSelfItem2;
    private javax.swing.JMenuItem payrollDetailSelfItem3;
    private javax.swing.JMenu payrollDetailSelfMenu;
    private javax.swing.JMenuItem payrollDetailTeamItem;
    private javax.swing.JMenuItem payrollDetailTeamItem1;
    private javax.swing.JMenuItem payrollDetailTeamItem2;
    private javax.swing.JMenuItem payrollDetailTeamItem3;
    private javax.swing.JMenu payrollDetailTeamMenu;
    private javax.swing.JMenu payrollSummaryReportMenu;
    private javax.swing.JMenuItem payrollSummarySelfItem;
    private javax.swing.JMenuItem payrollSummarySelfItem1;
    private javax.swing.JMenuItem payrollSummarySelfItem2;
    private javax.swing.JMenuItem payrollSummarySelfItem3;
    private javax.swing.JMenu payrollSummarySelfMenu;
    private javax.swing.JMenuItem payrollSummaryTeamItem;
    private javax.swing.JMenuItem payrollSummaryTeamItem1;
    private javax.swing.JMenuItem payrollSummaryTeamItem2;
    private javax.swing.JMenuItem payrollSummaryTeamItem3;
    private javax.swing.JMenu payrollSummaryTeamMenu;
    private javax.swing.JMenuItem projectByBatchItem;
    private javax.swing.JMenuItem projectByStatusItem;
    private javax.swing.JMenuItem projectByVolumeAndStatusItem;
    private javax.swing.JMenuItem projectByVolumeItem;
    private javax.swing.JMenu qcBatchOpenReportMenu;
    private javax.swing.JMenu qcBatchQCReportMenu;
    private javax.swing.JMenuItem qcByBatchItem;
    private javax.swing.JMenuItem qcByCoderItem;
    private javax.swing.JMenuItem qcCoderErrorReportItem;
    private javax.swing.JMenu qcProjectReportMenu;
    private javax.swing.JMenuItem qcProjectSummaryItem;
    private javax.swing.JMenuItem rejectBatchMenuItem;
    private javax.swing.JMenuItem removeFromBinderItem;
    private javax.swing.JMenu reportMenu;
    private javax.swing.ButtonGroup resButtonGroup;
    private javax.swing.JMenuItem sampleMenuItem;
    private javax.swing.JButton saveAndGoBoundaryButton;
    private javax.swing.JButton saveAndGoButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JMenuItem splitMenuItem;
    private javax.swing.JSplitPane splitpane;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel statusPane;
    private javax.swing.JPanel theCoderPane;
    private javax.swing.JPanel theViewer;
    private javax.swing.JPanel theViewerPane;
    protected javax.swing.JPanel topPane;
    private javax.swing.JScrollPane topScrollPane;
    private javax.swing.JButton uPageNextButton;
    private javax.swing.JButton uPagePrevButton;
    protected javax.swing.JPanel unitizingPanel;
    private javax.swing.JMenuItem unsplitMenuItem;
    private javax.swing.JButton vAbsoluteButton;
    private javax.swing.JButton vAttachNextButton;
    private javax.swing.JButton vAttachPrevButton;
    private javax.swing.JButton vBeginImageRunButton;
    private javax.swing.JButton vDocNextButton;
    private javax.swing.JButton vDocPrevButton;
    private javax.swing.JButton vFirstCurrButton;
    private javax.swing.JButton vHorizButton;
    private javax.swing.JButton vLastCurrButton;
    private javax.swing.JButton vPageNextButton;
    private javax.swing.JButton vPagePrevButton;
    private javax.swing.JButton vScreenButton;
    private javax.swing.JButton vTurn90ClockwiseButton;
    private javax.swing.JButton vTurn90CounterwiseButton1;
    private javax.swing.JButton vVertButton;
    private javax.swing.JButton vZoomInButton;
    private javax.swing.JButton vZoomOutButton;
    private javax.swing.JButton vZoomWindowButton;
    private javax.swing.JMenuItem validateBatchMenuItem;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JLabel viewerLabel;
    private javax.swing.JPanel viewerPane;
    private javax.swing.JToolBar viewerToolBar;
    private javax.swing.JTable fieldsTable;
    private javax.swing.JPanel fieldsPane;
    private javax.swing.JScrollPane fieldsScrollPane;
    private javax.swing.JPanel bottomPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JButton viewButton;
    private javax.swing.JButton backButton;
    private javax.swing.JButton reportButton;
    private javax.swing.JComboBox fieldComoBox;
    private javax.swing.JComboBox errorComoBox;
    //private LComboBox status;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JTable markingTable;
    private javax.swing.JPanel markingPane;
    private javax.swing.JScrollPane markingScrollPane;
    private javax.swing.JPanel sweeppane1;
    private javax.swing.JPanel sweeppane2;
    private javax.swing.JPanel errorTypePane;
    private javax.swing.JLabel oldValueLabel;
    private javax.swing.JButton sweepButton;
    private javax.swing.JButton sweepAllButton;
    private javax.swing.JTextField oldValuetextfield;
    private javax.swing.JLabel newValueLabel;
    private javax.swing.JButton back2Button;
//	private javax.swing.JTextField newValuetextfield;
    private LTextField newValuetextfield;
    private javax.swing.JPanel occurrence;
    private javax.swing.JPanel marking;
    private javax.swing.JPanel sweep;
    private TableSorter sorter;
    private javax.swing.JMenuItem assignListingQCMenuItem;
    private javax.swing.JMenuItem assignTallyQCMenuItem;
    private javax.swing.JMenuItem closeVolume;
    private javax.swing.JMenuItem listingQCDone;
    private javax.swing.JMenuItem tallyQCDone;
    private javax.swing.JMenuItem count;
    private javax.swing.JMenuItem tagCount;
    private javax.swing.JButton editButton;
    private javax.swing.JButton saveViewButton;
    //---------------->
    //-----------coded anurag -------------------
    private javax.swing.JPanel belowPanel;
    public TimerDemo timerDemo;
    private javax.swing.JButton breakButton;
    //------------ends---------------------------	
    // End of variables declaration                   
    private String imageLocField = null; // doc name to show

    private String bates = null; // doc name to show

    // fields for dynamic components
    protected int compCount = 0;

    // fields to hold the current doc
    private int viewerDoc = -1;
    private int viewerAttach = -1;
    private String viewedName = "";
    private boolean isDoc = false;
    private int gridXPos,  gridYPos;
    private String duration = null;
    private boolean isBreak = false;

    /**
     * Set frame to be made visible when this frame is closed
     */
    public void setParent(JFrame newParent) {
        parent = newParent;
    }

    /**
     * Make this viewer invisible and return it to the free list.
     */
    private void closeViewer() {
        //Log.print("@@@closeViewer: "+viewerNumber+" "+(binderViewer == null));

        // save any properties that were set
        try {
            LocalProperties.storeFile();
        } catch (Exception e) {
        }

        // get rid of the subordinate binder viewer, if there is one
        if (binderViewer != null) {
            //Log.print("@@@close binder viewer first");
            binderViewer.closeViewer();
        //Log.print("@@@back from close binder viewer");
        }

        // If this viewer has an undocked frame, get rid of it
        if (undockedDialog != null) {
            undockedDialog.setVisible(false);
            undockedDialog.dispose();
            undockedDialog = null;
        }

        // If this is a binder, tell the parent it no longer has a binder
        //Log.print("status="+whichStatus+" parent="+parent);
        if ("Binder".equals(whichStatus) && parent != null) {
            ((SplitPaneViewer) parent).binderViewer = null;
        }

        // If it's a client's main window, release batch
        // TBD: Want to unassign and requeue batch if user hasn't coded anything?
        // In callback, call closeViewer again (recursively) 
        if (Global.mainWindow == this) {
            Global.mainWindow = null;

            // update the event with the close_timestamp
            // do this before releasing batch
            if ("Listing".equals(whichStatus) || "ListingQC".equals(whichStatus) || "Tally".equals(whichStatus) || "TallyQC".equals(whichStatus)) {
                ClientTask taskEvent = new TaskCloseEvent(whichStatus, volumeId);
                taskEvent.enqueue(this);
            } else {
                ClientTask taskEvent = new TaskCloseEvent();
                taskEvent.enqueue(this);
            }

            //clear active (locked) batch
            //    update session set volume_id=0, batch_id=0, lock_time=0
            //     where session_id=[task]              
            //Log.print("closing main window viewer");
            ClientTask task = new TaskExecuteUpdate("SplitPaneViewer.closeMenuItem");
            task.setCallback(new Runnable() {

                public void run() {
                    try {
                        SplitPaneViewer.this.closeViewer();
                    } catch (Throwable th) {
                        Log.quit(th);
                    }
                }
            });
            boolean ok = task.enqueue(this);

            // update the event with the close_timestamp
            if ("Listing".equals(whichStatus) || "ListingQC".equals(whichStatus) || "Tally".equals(whichStatus) || "TallyQC".equals(whichStatus)) {
                task = new TaskCloseEvent(whichStatus, volumeId);
            } else {
                task = new TaskCloseEvent();
            }
            task.enqueue(this);

        // Otherwise, just get rid of the window
        // We can get here on callback after client releases batch
        } else {
            if (parent != null) {
                if (!isBreak) {
                    parent.setVisible(true);
                    if (!whichStatus.equalsIgnoreCase("Binder")) {
                        duration = timerDemo.destroy();
                        viewerPane.remove(belowPanel);
                        this.free(); // put the viewer on the free list

                    }
                } else {    //now the activity is 'BREAK'
                    duration = timerDemo.destroy();
                    showBreakDialog();
                    isBreak = false;
                }
            }
            setVisible(false);
        }
    }

    //-----------------------coded by anurag----------------------------------------
    //Adds a panel containing the timer & break button.
    private void addTimerPanel(String status) {
        belowPanel = new JPanel();
        belowPanel.setLayout(new java.awt.FlowLayout());

        timerDemo = new TimerDemo();
        timerDemo.taskLabel.setText("Task : " + status);

        belowPanel.add(timerDemo);
        breakButton = new JButton("Break");
        belowPanel.add(breakButton);
        breakButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isBreak = true;
                closeViewer();
            }
        });

        //Adding the panel in bottom of container.
        viewerPane.add(belowPanel, java.awt.BorderLayout.SOUTH);
    }

    //Displays the break dialog form.
    private void showBreakDialog() {

        Calendar timeStamp = Calendar.getInstance();
        final ClientTask tsk = new TaskStartOtherActivity(userId, "Break", "", timeStamp.getTimeInMillis());
        tsk.setCallback(new Runnable() {

            public void run() {
                Element element = (Element) tsk.getResult();
                String action = element.getNodeName();
                if (T_SEND_EVENT_BREAK_ID.equals(action)) {
                    int event_break_id = Integer.parseInt(element.getAttribute(A_EVENT_BREAK_ID));
                    createBreakDialog(event_break_id);
                }
            }
        });

        tsk.enqueue(this);
    }
    private int userId = 0;

    //Get user id for the logged in user.
    private void getUserId() {
        final ClientTask task;
        ServerConnection sconn = Global.theServerConnection;
        String userName = sconn.getUserName();
        task = new TaskExecuteQuery("select users_id from users", userName);
        task.setCallback(new Runnable() {

            public void run() {
                try {
                    ResultSet rs = (ResultSet) task.getResult();
                    rs.next();
                    userId = rs.getInt(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        task.enqueue(this);
    }

    //Creates the break dialog.
    public void createBreakDialog(int event_Break_Id) {
        if (projId == 0) {
            projId = projectId;
        }
        if (getBatchId() == 0 || selectedActivity.equals("Listing")) {
            projId = getProjectId();
        } else if (getBatchId() == 0 || selectedActivity.equals("Tally")) {
            projId = getProjectId();
        } else if (getBatchId() == 0 || selectedActivity.equals("TallyQC")) {
            projId = getProjectId();
        } else if (getBatchId() == 0 || selectedActivity.equals("ListingQC")) {
            projId = getProjectId();
        }

        JDialog breakDialog = new ShowBreakDialog(this.parent, "Break", event_Break_Id, duration, getBatchId(), projId, selectedActivity, getVolumeId(), getVolume(), getFields(), userId, fieldName);
        breakDialog.setLocationRelativeTo(null);

        breakDialog.setVisible(true);
    }

    //------------------------------------End-----------------------------------------------

    // Check if boundary matches pattern.  (Convenience method
    // for readablilty.)
    private boolean isBoundary(ImageData data, int pattern) {
        return (data.boundaryInfo & pattern) != 0;
    }

    /**
     * Make sure the field is showing in the scrollpane's viewport.
     * Note:  There is a version of this in IbaseTextField.focusGained
     * and LTextbutton.focusGained.
     */
    private class FocusComponent implements FocusListener {

        public void focusGained(FocusEvent e) {
            //Log.print("(SPV).focusGained " + e.getComponent().getName());
            // see if the previous component was an LTextButton and 
            // close the dialog, if so.
            checkCloseDialog(e.getOppositeComponent());
            Component comp = e.getComponent();
            Rectangle rect;
            //Log.print("(SPV.focus) " + comp);
            if (comp instanceof LTextField) {
                rect = comp.getParent().getBounds();
            } else if (comp instanceof JButton) {
                rect = comp.getBounds();
            } else {
                //Log.print("    leaving with " + comp);
                return;
            }
            JViewport jvp = (JViewport) midScrollPane.getViewport();
            Rectangle rectViewport = jvp.getViewRect();
            //Log.print("(SPV.focusGained) rectViewport="
            //          + " comp is " + comp
            //          + " y " + rect.getY());

            if (!rectViewport.contains(new Point(0, (int) rect.getY())) || !rectViewport.contains(new Point(0, (int) rect.getY() + 20))) {
                Point p = new Point(0, (int) rect.getY() - 8);
                //Log.print("(SPV.focuscomponent) point " + p);
                jvp.setViewPosition(p);
                jvp.repaint();
            }
        }

        public void focusLost(FocusEvent e) {
        }
    }

    // calls to this could be replaced with undoController
    // (Left over from a static undoController)
    private LUndoController getUndoController() {
        if (undoController == null) {
            //Log.print("(SPV.getUndoController) new controller");
            undoController = new LUndoController();
        }
        return undoController;
    }

    /**
     * Called from various controls to clear the status field
     * on whatever SplitPaneViewer contains the control
     * @param location The control which wants to clear viewer.
     */
    public static void clearViewerStatus(Component location) {
        if (location instanceof SplitPaneViewer) {
            SplitPaneViewer viewer = (SplitPaneViewer) location;
            viewer.statusLabel.setText("");
        } else if (location != null) {
            clearViewerStatus(location.getParent());
        }
    }

    private void viewButtonActionPerformed(java.awt.event.ActionEvent evt) {
        viewFieldsDetails();
    }

    private void viewFieldsDetails() {
        viewerLabel.setText(" ");
        marking.setVisible(true);
        reportButton.setEnabled(false);
        saveViewButton.setEnabled(false);
        // newValuetextfield.setEditable(false);
        loadFieldvalueDetails();
        //---- get field type for selected field -----
        String fieldName = null;
        if ("Listing".equals(whichStatus) || "Tally".equals(whichStatus)) {
            fieldName = selectedField;
        } else {
            fieldName = getFieldName();
        }
        final ClientTask task = new TaskExecuteQuery(GET_FIELD_TYPE, Integer.toString(projectId), fieldName);
        task.setCallback(new Runnable() {

            public void run() {
                try {
                    ResultSet queryResult = (ResultSet) task.getResult();
                    while (queryResult.next()) {
                        setFieldType(queryResult.getString(1));
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        task.enqueue(this);
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
        if (this.fieldType.equals("date")) {
            newValuetextfield.setInputVerifier(DateVerifier.createInstance());
        } else {
            newValuetextfield.setInputVerifier(null);
        }
    }

    protected void loadFieldvalue(int projectId) {
        int volume_id = getVolumeId();
        if ("Listing".equals(whichStatus) || "Tally".equals(whichStatus)) {
            // assignTallyQCMenuItem.setVisible(false);
            final ClientTask task;
            task = new TaskRequestFieldvalue(selectedField, projectId, volume_id, whichStatus);
            task.setCallback(new Runnable() {

                public void run() {
//                            Element reply = (Element) task.getResult();
//                            final ResultSet rs = Sql.resultFromXML(reply);
//                            String action = reply.getNodeName();
//                            if (T_RESULT_SET.equals(reply.getNodeName())) {
//                                // ResultSet results = (ResultSet) task.getResult();
//                                if (rs != null) {
//                                    loadFieldvalueEntry(rs);
//                                } else {
//                                    Log.print("??? Fieldvalues: null resultset returned");
//                                }
//                            } else if (T_FAIL.equals(action)) {                                                           
//                               fieldComoBox.removeAllItems();
//                                JOptionPane.showMessageDialog(SplitPaneViewer.this,
//                                        "Can't open Field selection Dialog",
//                                        "Error",
//                                        JOptionPane.ERROR_MESSAGE);                                                            
//                                SplitPaneViewer.this.closeViewer();                               
//                            } 

                    Map map = (Map) task.getResult();
                    if (map != null) {
                        //Log.print("(SPV).getPageValues: map returned " + map.size());

                        loadFieldvalueEntry(map);
                    }
                }
            });
            boolean ok = task.enqueue(this);
        } else if ("ListingQC".equals(whichStatus) || "TallyQC".equals(whichStatus)) {
            assignListingQCMenuItem.setVisible(false);
            assignTallyQCMenuItem.setVisible(false);
            int volumeId = getVolumeId();
            // int project_Id = getProjectId();
            String field_name = getFieldName();

            //


            /**
             * Query parameters   
            1. user_name
            2. tally_status
            3. project_id
            4. volume_id
            5. project_id
            6. field_name
             */
            String param[] = {Global.theServerConnection.getUserName(), "Assigned",
                Integer.toString(projectId), Integer.toString(volumeId), Integer.toString(projectId), field_name
            };
            final ClientTask task = new TaskExecuteQuery("TallyQC.getFieldsValue", param);
            task.setCallback(new Runnable() {

                public void run() {
                    ResultSet results = (ResultSet) task.getResult();
                    int i = 0;
                    Map map = new HashMap();
                    try {
                        while (results.next()) {
                            OccurrenceData occurrence = new OccurrenceData();
                            occurrence.setFieldValue(results.getString(1));
                            occurrence.setOccurrence(results.getString(2));
                            map.put(i, occurrence);
                            i++;
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(SplitPaneViewer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (map != null) {
                        loadFieldvalueEntry(map);
                    }
                }
            });
            task.enqueue(this);
        }
    }

    public void error() {
        JOptionPane.showMessageDialog(SplitPaneViewer.this,
                "Can't open Field selection Dialog",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        fieldComoBox.removeAllItems();
    // closeViewer();   

    }

    @SuppressWarnings("empty-statement")
    private void loadFieldvalueEntry(Map map) {

//        valueModel = new ResultSetTableModel(rs, new String[]{"Field Value", "Occurrence"});
//        sorter = new TableSorter(valueModel);
//        fieldsTable.setModel(sorter);
//        //fieldsTable.setPreferredSize(new Dimension(100, 150));
//        sorter.setTableHeader(fieldsTable.getTableHeader());
        fieldsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fieldsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm =
                        (ListSelectionModel) e.getSource();
                // int row = fieldsTable.getSelectedRow();
                if (lsm.isSelectionEmpty() || fieldsTable.getSelectedRow() < 0) {
                    //no rows are selected
                    int rows = fieldsTable.getRowCount();
                    if (whichStatus.equals("Listing")) {
                        for (int i = 0; i < rows; i++) {
                            String selectedValue = fieldsTable.getValueAt(i, 2).toString();
                            if (selectedValue.equals("true")) {
                                reportButton.setEnabled(true);
                                break;
                            }
                        }
                    }
                } else {
                    viewerLabel.setText(" ");
                    int row1 = fieldsTable.getSelectedRow();
                    if (null != fieldsTable.getValueAt(row1, 0)) {
                        fieldValueFromOccurrence = (String) fieldsTable.getValueAt(row1, 0);
                    }

                    int rows = fieldsTable.getRowCount();
                    if (whichStatus.equals("Listing")) {
                        for (int i = 0; i < rows; i++) {
                            String selectedValue = fieldsTable.getValueAt(i, 2).toString();
                            if (selectedValue.equals("true")) {
                                reportButton.setEnabled(true);
                                break;
                            }
                        }
                    }
//                            String selectedValue = fieldsTable.getValueAt(row1, 2).toString();
//                            if(selectedValue.equals("true")){
//                              reportButton.setEnabled(true);
//                               viewButton.setEnabled(false);
//                            }else{
//                              reportButton.setEnabled(true);
//                              viewButton.setEnabled(true);
//                            }

                }
            // System.out.println("aaaa"+ fieldsTable.getValueAt(row, 2));
            // fieldsTable.getRowCount();
            }
        });

//        TableColumn column;
//        column = fieldsTable.getColumnModel().getColumn(0); // value
//        column.setPreferredWidth(50);
//        //column.setCellRenderer(centerCellRenderer);
//        column = fieldsTable.getColumnModel().getColumn(1); // Data Count
//        column.setCellRenderer(centerCellRenderer);
        String headings[] = null;
        ArrayList<String> headingList = new ArrayList<String>();
        headingList.add("Field value");
        headingList.add("Occurrence");
        if (whichStatus.equals("Listing")) {
            headingList.add("Mark");
        }
        headings = headingList.toArray(new String[2]);
        Object object[][] = new Object[map.size()][headings.length + 1];

        if (null != map) {
            Set keys = map.keySet();
            int i = 0;
            boolean mark = false;
            for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
                Integer key = (Integer) iterator.next();
                OccurrenceData occurrenceValues = (OccurrenceData) map.get(key);
                object[i][0] = occurrenceValues.getFieldValue();
                object[i][1] = occurrenceValues.getOccurrence();
                if (occurrenceValues.getMarking() != null) {
                    if (occurrenceValues.getMarking().equals("Yes")) {
                        mark = true;
                    } else {
                        mark = false;
                    }
                    object[i][2] = new Boolean(mark);
                }
                i++;
            }
        }

        listingOccurrenceTableModel = new ListingOccurrenceTableModel(object, headings);
        fieldsTable.setModel(listingOccurrenceTableModel);
        enableComponents();
    }
    TableCellRenderer centerCellRenderer = new DefaultTableCellRenderer() {

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            // Value is text
            setText((value == null) ? "" : value.toString());
            //setIcon(null);

            setHorizontalAlignment(JLabel.CENTER);
            return this;
        }
    };

    private void loadFieldvalueDetails() {
        clearFieldsDetails();
        if ("Listing".equals(whichStatus) || "Tally".equals(whichStatus)) {
            int volume_id = getVolumeId();
            final ClientTask task;
            task = new TaskRequestFieldvalueDetails(selectedField, fieldValueFromOccurrence, projectId, volume_id, whichStatus);
            task.setCallback(new Runnable() {

                public void run() {
//                    ResultSet results = (ResultSet)task.getResult();
//                    if (results != null) {
//                        loadFieldvalueDetailsEntry(results);
//                    } else {
//                        Log.print("??? FieldvalueDetails: null resultset returned");
//                    }
                    Map map = (Map) task.getResult();
                    if (map != null) {
                        //Log.print("(SPV).getPageValues: map returned " + map.size());
                        loadFieldvalueDetailsEntry(map);
                    }
                }
            });
            boolean ok = task.enqueue(this);
        } else if ("ListingQC".equals(whichStatus) || "TallyQC".equals(whichStatus)) {
            int volume_id = getVolumeId();
            String fieldname = getFieldName();
            //======================================
            /**
             * Parameters for query "TallyQC.getFieldsDetailsValue"  
            1. user_name
            2. tally_status
            3. field_value
            4. project_id
            5. volume_id
            6. project_id
            7. field_name
             */
            String param[] = {Global.theServerConnection.getUserName(), "Assigned", fieldValueFromOccurrence,
                Integer.toString(projectId), Integer.toString(volumeId), Integer.toString(projectId), fieldname
            };
            final ClientTask task = new TaskExecuteQuery("TallyQC.getFieldsDetailsValue", param);
            task.setCallback(new Runnable() {

                public void run() {
                    ResultSet results = (ResultSet) task.getResult();
                    int i = 0;
                    Map map = new HashMap();
                    try {
                        while (results.next()) {
                            MarkingData marking = new MarkingData();
                            marking.setFirstBatesOfRange(results.getString(1));
                            marking.setLastBatesOfRange(results.getString(2));
                            marking.setFieldvalue(results.getString(3));
                            marking.setSequence(results.getString(4));
                            marking.setCorrectionData(results.getString(5));
                            marking.setCorrectionType(results.getString(6));
                            map.put(i, marking);
                            i++;
                        }
                    } catch (SQLException ex) {
                        Logger.getLogger(SplitPaneViewer.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    if (map != null) {
                        loadFieldvalueDetailsEntry(map);
                    }
                }
            });
            task.enqueue(this);
        //==========================================

//            
//            final ClientTask task;
//            task = new TaskRequestFieldvalueDetails(field_name,fieldValueFromOccurrence,projectId,volume_id,whichStatus);
//            task.setCallback(new Runnable() {
//                public void run() {
////                    ResultSet results = (ResultSet)task.getResult();
////                    if (results != null) {
////                       // loadFieldvalueDetailsEntry(results);
////                    } else {
////                        Log.print("??? FieldvalueDetails: null resultset returned");
////                    }
//                     Map map = (Map) task.getResult();
//                        if (map != null) {
//                            //Log.print("(SPV).getPageValues: map returned " + map.size());
//                            loadFieldvalueDetailsEntry(map);
//                        }
//                }
//            });
//            boolean ok = task.enqueue(this);
        }
    //           else if("Tally".equals(whichStatus)){
//            int volume_id = getVolumeId();
//            final ClientTask task;
//            task = new TaskRequestFieldvalueDetails(selectedField,fieldValueFromOccurrence,projectId,volume_id);
//            task.setCallback(new Runnable() {
//                public void run() {
//                    ResultSet results = (ResultSet)task.getResult();
//                    if (results != null) {
//                        loadFieldvalueDetailsEntry(results);
//                    } else {
//                        Log.print("??? FieldvalueDetails: null resultset returned");
//                    }
//                }
//
//            });
//            boolean ok = task.enqueue(this);
//        }else if("TallyQC".equals(whichStatus)){
//            int volume_id = getVolumeId();
//            String field_name = getFieldName();
//            final ClientTask task;
//            task = new TaskRequestFieldvalueDetails(field_name,fieldValueFromOccurrence,projectId,volume_id);
//            task.setCallback(new Runnable() {
//                public void run() {
//                    ResultSet results = (ResultSet)task.getResult();
//                    if (results != null) {
//                        loadFieldvalueDetailsEntry(results);
//                    } else {
//                        Log.print("??? FieldvalueDetails: null resultset returned");
//                    }
//                }
//
//            });
//            boolean ok = task.enqueue(this);
//        }
    }

    private void clearFieldsDetails() {
        newValuetextfield.setText("");
        oldValuetextfield.setText("");
        errorComoBox.setSelectedItem("Miscoded");
    }
    Object object[][];

    private void loadFieldvalueDetailsEntry(Map map) {
        newValuetextfield.setEnabled(false);
        sweepButton.setEnabled(false);
        sweepAllButton.setEnabled(false);
        viewButton.setEnabled(false);
        final int volume_id = getVolumeId();
//        valueModel = new ResultSetTableModel(rs, new String[] {"Start Bate","End Bate","Field Value"});
//        sorter = new TableSorter(valueModel);
//        markingTable.setModel(sorter);
//        sorter.setTableHeader(markingTable.getTableHeader());
        markingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//       // markingTable.selectAll();
        markingTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm =
                        (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty() || markingTable.getSelectedRow() < 0) {
                    //no rows are selected
                    int rows = markingTable.getRowCount();
                    if (whichStatus.equals("Listing")) {
                        for (int i = 0; i < rows; i++) {
                            String selectedValue = markingTable.getValueAt(i, 4).toString();
                            if (selectedValue.equals("true")) {
                                saveViewButton.setEnabled(true);
                                break;
                            }
                        }
                    }
                } else {
                    //markingTable.selectAll();
                    newValuetextfield.setEnabled(true);
                    int row = markingTable.getSelectedRow();
                    //if (null != markingTable.getValueAt(row,2)){
                    if(whichStatus.equals("TallyQC")){
                        fieldValueFromMarking = (String) markingTable.getValueAt(row, 2);
                        String newValue = (String) markingTable.getValueAt(row, 3);
                        oldValuetextfield.setText(fieldValueFromMarking);
                        newValuetextfield.setText(newValue);
                        listing_occurrence_id = Integer.parseInt(markingTable.getModel().getValueAt(row, 5).toString());
                        bateno = (String) markingTable.getValueAt(row, 0);
                        Object selectedErrorType = (markingTable.getValueAt(row, 4) == null || markingTable.getValueAt(row, 4).toString().equals("")) ? "Miscoded" : (markingTable.getValueAt(row, 4));
                        errorComoBox.setSelectedItem(selectedErrorType);
                        saveViewButton.setVisible(false);
                   }else if(whichStatus.equals("Listing")){
                          fieldValueFromMarking = (String) markingTable.getValueAt(row, 2);
                        //newValue = (String) markingTable.getValueAt(row, 3);
                        oldValuetextfield.setText(fieldValueFromMarking);                        
                        listing_occurrence_id = Integer.parseInt(markingTable.getModel().getValueAt(row, 3).toString());
                        bateno = (String) markingTable.getValueAt(row, 0);
                        saveViewButton.setVisible(true);
                   }
                   if (whichStatus.equals("Listing") || whichStatus.equals("TallyQC")) {
                            getCoderData(0, 1, 2, whichStatus, bateno, volume_id);     //@TODO
                   }                 

                    int rows = markingTable.getRowCount();
                    if (whichStatus.equals("Listing")) {
                        for (int i = 0; i < rows; i++) {
                            String selectedValue = markingTable.getValueAt(i, 4).toString();
                            if (selectedValue.equals("true")) {
                                saveViewButton.setEnabled(true);
                                break;
                            }
                        }
                    }
                }
            }
        });
        //        
//        TableColumn column;
//        column = markingTable.getColumnModel().getColumn(2); // field Value
//        column.setPreferredWidth(50);
//        //column.setCellRenderer(centerCellRenderer);
//        column = markingTable.getColumnModel().getColumn(0); // Start Bate
//        column.setCellRenderer(centerCellRenderer);
//        column = markingTable.getColumnModel().getColumn(1); // end Bate
//        column.setCellRenderer(centerCellRenderer);
//        
////        column.setCellRenderer(centerCellRenderer);
//        
        String headings[] = null;

	if(whichStatus.equals("Listing")){
           
            String heading[] = {"Start Bate","End Bate","Field Value", "Select","Mark"};  
            headings = heading;

        }else if(whichStatus.equals("TallyQC")){            
           
            String heading[] = {"Start Bate","End Bate","Field Value", "Correction Data", "Correction Type"};
            headings = heading;
        }

        object = new Object[map.size()][headings.length + 1];

        if (null != map) {
            Set keys = map.keySet();
            int i = 0;
            boolean mark = false;
            for (Iterator iterator = keys.iterator(); iterator.hasNext();) {
                Integer key = (Integer) iterator.next();
                MarkingData markingValues = (MarkingData) map.get(key);
                object[i][0] = markingValues.getFirstBatesOfRange();
                object[i][1] = markingValues.getLastBatesOfRange();
                object[i][2] = markingValues.getFieldvalue();

                if(whichStatus.equals("Listing")){
                 object[i][3] = markingValues.getSequence();
                 
                }else{
                object[i][3] = markingValues.getCorrectionData();
                object[i][4] = markingValues.getCorrectionType();
                object[i][5] = markingValues.getSequence();    

                }
                if (markingValues.getView_marking() != null && whichStatus.equals("Listing")) {
                    if (markingValues.getView_marking().equals("Yes")) {
                        mark = true;
                    } else {
                        mark = false;
                    }
                    object[i][4] = new Boolean(mark);
                }
                i++;
            }
        }


        listingMarkingTableModel = new ListingMarkingTableModel(object, headings);
        markingTable.setModel(listingMarkingTableModel);

        //  setListingOccurrenceType(markingTable, markingTable.getColumnModel().getColumn(2));
        enableComponents();
        if ("Tally".equals(whichStatus)) {
            getProjectFields();
        }
    }

    public void setListingOccurrenceType(JTable table,
            TableColumn sportColumn) {
        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for combo box");
        sportColumn.setCellRenderer(renderer);
    }

    private void checkEnableSweepButton() {

        if (newValuetextfield.getText().equals("")) {
            sweepButton.setEnabled(false);
            sweepAllButton.setEnabled(false);
        } else {
            sweepButton.setEnabled(true);
            sweepAllButton.setEnabled(true);
        }
    }

    private void back2ButtonActionPerformed(java.awt.event.ActionEvent evt) {
        //  occurrence.setVisible(true);
        viewerLabel.setText(" ");
        marking.setVisible(false);
    }

    private void backButtonActionPerformed(java.awt.event.ActionEvent evt) {
        JDialog dialog = null;
        ArrayList fieldsList = new ArrayList();
        for(int i = 0;i < fieldComoBox.getItemCount();i++){
           fieldsList.add(fieldComoBox.getItemAt(i));           
        }
        
        dialog = new AddMoreFieldsDialog(this, whichStatus, projectId,volumeId,projectName,volume,fieldsList);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);        
    }

    protected void addMoreFields(ArrayList fields){
       for(int i = 0;i < fields.size();i++){
         fieldComoBox.addItem(fields.get(i));
       }
       fieldComoBox.requestFocus();
    }
    
    private void fieldComoBoxActionPerformed(java.awt.event.ActionEvent evt) {
        if (fieldComoBox.getItemCount() > 0) {
            reportButton.setEnabled(false);
            saveViewButton.setEnabled(false);
            selectedField = fieldComoBox.getSelectedItem().toString();
            loadFieldvalue(projectId);
        }
    }

    private void sweepButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Map theMap = new HashMap();
        theMap.put(selectedField, newValuetextfield.getText());
        String validationError = null;
        boolean isValidated = true;
        if ("Listing".equals(whichStatus) || "Tally".equals(whichStatus)) {
            isValidated = true;
            int volume_id = getVolumeId();
            String newfieldValueFromMarking = newValuetextfield.getText();
            String errorType = (String) errorComoBox.getSelectedItem();
            newValuetextfield.setEnabled(false);
            sweepButton.setEnabled(false);
            sweepAllButton.setEnabled(false);
            newValuetextfield.setText(" ");
            if ("Tally".equals(whichStatus)) {
                validationError = projectEditor.edit(projectMap, theMap, validationData, unitize, false, "L2", whichStatus);
                if (validationError != null || validationError.trim().length() != 0) {
                    isValidated = false;
                }
            }
            if (isValidated) {
                final ClientTask task;
                task = new TaskSendFieldvalueDetails(projectId, selectedField, fieldValueFromMarking, newfieldValueFromMarking, bateno, volume_id, "sweep", whichStatus, errorType, listing_occurrence_id);
                task.setCallback(new Runnable() {

                    public void run() {
//                               ResultSet results = (ResultSet) task.getResult();
//                               if (results != null) {                              
//                                  // loadFieldvalueDetailsEntry(results);                                
//                               } else {
//                                   Log.print("??? SendFieldvalueDetails: null resultset returned");
//                               }
                        Map map = (Map) task.getResult();
                        if (map != null) {
                            //Log.print("(SPV).getPageValues: map returned " + map.size());
                            loadFieldvalueDetailsEntry(map);
                        }
                    }
                });
                boolean ok = task.enqueue(this);

//               final ClientTask task = new TaskExecuteUpdate("Listing.updateFieldValue", newValuetextfield.getText(), Integer.toString(listing_occurrence_id));   
//            task.setCallback(new Runnable() {
//                @Override
//                public void run() {
//                       loadFieldvalueDetails();
//                }
//            });
//            task.enqueue(this);  
//            
                loadFieldvalue(projectId);
                oldValuetextfield.setText(" ");
                viewerLabel.setText("  ");
            }
        } else if ("ListingQC".equals(whichStatus) || "TallyQC".equals(whichStatus)) {
            int volume_id = getVolumeId();
            String field_name = getFieldName();
            String newfieldValueFromMarking = newValuetextfield.getText();
            String errorType = (String) errorComoBox.getSelectedItem();
            newValuetextfield.setEnabled(false);
            sweepButton.setEnabled(false);
            sweepAllButton.setEnabled(false);
            newValuetextfield.setText("");

            //====================================================

            //paramenters for query "TallyQC.updateFieldsValue" are 
            // 1. correction_data
            // 2. correction_type
            // 3. tally_dictionay_id

            String param[] = {newfieldValueFromMarking, errorType, Integer.toString(listing_occurrence_id)};

            final ClientTask task = new TaskExecuteUpdate("TallyQC.updateFieldsValue", param);
            task.setCallback(new Runnable() {

                @Override
                public void run() {
                    loadFieldvalueDetails();
                }
            });
            task.enqueue(this);

        //====================================================

//            final ClientTask task;
//            task = new TaskSendFieldvalueDetails(projectId, field_name, fieldValueFromMarking, newfieldValueFromMarking, bateno, volume_id, "sweep", whichStatus, errorType, listing_occurrence_id);
//            task.setCallback(new Runnable() {
//
//                public void run() {
////                            ResultSet results = (ResultSet) task.getResult();
////                            if (results != null) {
////                               // loadFieldvalueDetailsEntry(results);
////                            } else {
////                                Log.print("??? SendFieldvalueDetails: null resultset returned");
////                            }
//
//                    Map map = (Map) task.getResult();
//                    if (map != null) {
//                        //Log.print("(SPV).getPageValues: map returned " + map.size());
//                        loadFieldvalueDetailsEntry(map);
//                    }
//                }
//            });
//            boolean ok = task.enqueue(this);
//            loadFieldvalue(projectId);
//            viewerLabel.setText("  ");
        }
//         else if("Tally".equals(whichStatus)){
//            int volume_id = getVolumeId();
//            String newfieldValueFromMarking = newValuetextfield.getText();
//            newValuetextfield.setEnabled(false);
//            sweepButton.setEnabled(false);
//            sweepAllButton.setEnabled(false);
//            newValuetextfield.setText("");
//            System.out.println("newfieldValueFromMarking--------->" + newfieldValueFromMarking);
//            final ClientTask task;
//            task = new TaskSendFieldvalueDetails(projectId, selectedField, fieldValueFromMarking, newfieldValueFromMarking, bateno, volume_id,"sweep");
//            task.setCallback(new Runnable() {
//
//                        public void run() {
//                            ResultSet results = (ResultSet) task.getResult();
//                            if (results != null) {
//                                loadFieldvalueDetailsEntry(results);
//                            } else {
//                                Log.print("??? SendFieldvalueDetails: null resultset returned");
//                            }
//                        }
//                    });
//            boolean ok = task.enqueue(this);
//            loadFieldvalue(projectId);
//            oldValuetextfield.setText(" ");
//            viewerLabel.setText("  ");
//         }
    }
    private int i;

    private void sweepAllButtonActionPerformed(java.awt.event.ActionEvent evt) {
        Map theMap = new HashMap();
        theMap.put(selectedField, newValuetextfield.getText());
        String validationError = null;
        boolean isValidated = true;
        if ("Listing".equals(whichStatus) || "Tally".equals(whichStatus)) {
            isValidated = true;
            int volume_id = getVolumeId();
            String newfieldValueFromMarking = newValuetextfield.getText();
            String errorType = (String) errorComoBox.getSelectedItem();
            newValuetextfield.setText("");
            newValuetextfield.setEnabled(false);
            sweepButton.setEnabled(false);
            sweepAllButton.setEnabled(false);
            if ("Tally".equals(whichStatus)) {
                validationError = projectEditor.edit(projectMap, theMap, validationData, unitize, false, "L2", whichStatus);
                if (validationError != null || validationError.trim().length() != 0) {
                    isValidated = false;
                }
            }
            if (isValidated) {

//              for ( i = 0; i < object.length; i++) {                 
//                 final ClientTask task = new TaskExecuteUpdate("Listing.updateFieldValue",  object[i][5].toString());
//                 task.setCallback(new Runnable() {
//                     @Override
//                     public void run() {                                                      
//                        loadFieldvalueDetails();                         
//                     }
//                 });
//                 task.enqueue(this);
//             }


                final ClientTask task;
                task = new TaskSendFieldvalueDetails(projectId, selectedField, fieldValueFromMarking, newfieldValueFromMarking, bateno, volume_id, "sweepAll", whichStatus, errorType, listing_occurrence_id);
                task.setCallback(new Runnable() {

                    public void run() {
//                               ResultSet results = (ResultSet) task.getResult();
//                               if (results != null) {
//                                 //  loadFieldvalueDetailsEntry(results);
//                               } else {
//                                   Log.print("??? SendFieldvalueDetails: null resultset returned");
//                               }
                        Map map = (Map) task.getResult();
                        if (map != null) {
                            //Log.print("(SPV).getPageValues: map returned " + map.size());
                            loadFieldvalueDetailsEntry(map);
                        }
                    }
                });
                boolean ok = task.enqueue(this);
                loadFieldvalue(projectId);
                viewerLabel.setText("  ");
            }
        } else if ("ListingQC".equals(whichStatus) || "TallyQC".equals(whichStatus)) {


            int volume_id = getVolumeId();
            String field_name = getFieldName();
            String newfieldValueFromMarking = newValuetextfield.getText();
            String errorType = (String) errorComoBox.getSelectedItem();
            newValuetextfield.setText("");
            newValuetextfield.setEnabled(false);
            sweepButton.setEnabled(false);
            sweepAllButton.setEnabled(false);


            for (i = 0; i < object.length; i++) {
                //paramenters for query "TallyQC.updateFieldsValue" are 
                // 1. correction_data
                // 2. correction_type
                // 3. tally_dictionay_id

                String param[] = {newfieldValueFromMarking, errorType, object[i][5].toString()};
                final ClientTask task = new TaskExecuteUpdate("TallyQC.updateFieldsValue", param);
                task.setCallback(new Runnable() {

                    @Override
                    public void run() {
                        loadFieldvalueDetails();
                    }
                });
                task.enqueue(this);
            }

//            final ClientTask task;
//            task = new TaskSendFieldvalueDetails(projectId, field_name, fieldValueFromMarking, newfieldValueFromMarking, bateno, volume_id,"sweepAll",whichStatus,errorType,listing_occurrence_id);
//            task.setCallback(new Runnable() {
//                        public void run() {
//                            ResultSet results = (ResultSet) task.getResult();
//                            if (results != null) {
//                              //  loadFieldvalueDetailsEntry(results);
//                            } else {
//                                Log.print("??? SendFieldvalueDetails: null resultset returned");
//                            }
//                        }
//                    });
//            boolean ok = task.enqueue(this);
            loadFieldvalue(projectId);
            viewerLabel.setText("  ");
        }
//         else if("Tally".equals(whichStatus)){
//            int volume_id = getVolumeId();
//            String newfieldValueFromMarking = newValuetextfield.getText();
//            newValuetextfield.setText("");
//            newValuetextfield.setEnabled(false);
//            sweepButton.setEnabled(false);
//            sweepAllButton.setEnabled(false);
//            System.out.println("newfieldValueFromMarking--------->" + newfieldValueFromMarking);
//            final ClientTask task;
//            task = new TaskSendFieldvalueDetails(projectId, selectedField, fieldValueFromMarking, newfieldValueFromMarking, bateno, volume_id,"sweepAll");
//            task.setCallback(new Runnable() {
//
//                        public void run() {
//                            ResultSet results = (ResultSet) task.getResult();
//                            if (results != null) {
//                                loadFieldvalueDetailsEntry(results);
//                            } else {
//                                Log.print("??? SendFieldvalueDetails: null resultset returned");
//                            }
//                        }
//                    });
//            boolean ok = task.enqueue(this);
//            loadFieldvalue(projectId);
//            viewerLabel.setText("  ");
//         }
    }
    //Listing Occurrencepanel   

    private void reportButtonActionPerformed(java.awt.event.ActionEvent evt) {

        ArrayList list = new ArrayList();
        int rows = fieldsTable.getRowCount();
        for (int i = 0; i < rows; i++) {
            String selectedValue = fieldsTable.getValueAt(i, 2).toString();
            if (selectedValue.equals("true") || selectedValue.equals("false")) {
                list.add(fieldsTable.getValueAt(i, 0) + "-" + fieldsTable.getValueAt(i, 2));
            }
        }
//            int row1 = fieldsTable.getSelectedRow();
//            String selectedValue = fieldsTable.getValueAt(row1, 2).toString();
//                            if(selectedValue.equals("true")){
//                              reportButton.setEnabled(true);
//                               viewButton.setEnabled(false);
//                            }else{
//                              reportButton.setEnabled(true);
//                              viewButton.setEnabled(true);
//                            }

        final ClientTask task;
        task = new TaskSaveListingReportListForOccurrence(list, selectedField, projectId, volumeId);
        task.setCallback(new Runnable() {

            public void run() {
                Element reply = (Element) task.getResult();

            }
        });
        task.enqueue(this);

        reportButton.setEnabled(false);
//        int volume_id = getVolumeId();
//        final ClientTask task;
//        task = new TaskSaveListingReport(projectId, volume_id, selectedField, fieldValueFromOccurrence);
//        task.setCallback(new Runnable() {
//
//                    public void run() {
//                        ResultSet results = (ResultSet) task.getResult();
//                        if (results != null) {
//                        //loadFieldvalueDetailsEntry(results);
//                        } else {
//                            Log.print("??? FieldvalueDetails: null resultset returned");
//                        }
//                    }
//                });
//        boolean ok = task.enqueue(this);
//

    }
    //Listing Markingpanel

    private void saveViewButtonActionPerformed(java.awt.event.ActionEvent evt) {

        ArrayList list = new ArrayList();

        int rows = markingTable.getRowCount();
        for (int i = 0; i < rows; i++) {

            String selectedValue = markingTable.getValueAt(i, 4).toString();
            if (selectedValue.equals("true") || selectedValue.equals("false")) {
                list.add(markingTable.getValueAt(i, 0) + "-" + markingTable.getValueAt(i, 2) + "-" + markingTable.getValueAt(i, 4));
            }
        }

        final ClientTask task;
        task = new TaskSaveListingReportListForMarking(list, selectedField, projectId, volumeId);
        task.setCallback(new Runnable() {

            public void run() {
                Element reply = (Element) task.getResult();

            }
        });
        task.enqueue(this);
        saveViewButton.setEnabled(false);
    }

    private void fieldsTableMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            if (evt.getClickCount() >= 1) {
                // double-click on a row

//               int rows = fieldsTable.getRowCount();        
//                for (int i = 0; i < rows; i++) {
//                    String selectedValue = fieldsTable.getValueAt(i, 2).toString();                    
//                    if(selectedValue.equals("true")){                       
//                          reportButton.setEnabled(true);
//                          viewButton.setEnabled(false);
//                          break;
//                       }
//                    reportButton.setEnabled(false);
//                    viewButton.setEnabled(true);                   
//                }
//                 saveButton.setEnabled(false);
//                 marking.setVisible(false);
                int row1 = fieldsTable.getSelectedRow();
                if (whichStatus.equals("Listing")) {
                    String selectedValue = fieldsTable.getValueAt(row1, 2).toString();
                    if (selectedValue.equals("true")) {
                        reportButton.setEnabled(true);
                        viewButton.setEnabled(false);
                    } else if (selectedValue.equals("false")) {
                        reportButton.setEnabled(false);
                        viewButton.setEnabled(true);
                    }
                } else if (whichStatus.equals("TallyQC")) {
                    viewFieldsDetails();
                } else {
                    viewButton.setEnabled(true);
                }
            }


        } catch (Throwable th) {
            Log.quit(th);
        }
    }

    /**
     * Remove the panel contains the view, other fields, save button
     * This is used in case of TallyQC
     */
    public void removeViewPanel() {
        bottomPane.remove(jPanel1);
    }

    private void markingTableMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            if (evt.getClickCount() >= 1) {
                // double-click on a row
//               int rows = markingTable.getRowCount();        
//                for (int i = 0; i < rows; i++) {
//                    String selectedValue = markingTable.getValueAt(i, 4).toString();
//                    if(selectedValue.equals("true")){
//                          saveViewButton.setEnabled(true);
//                          newValuetextfield.setEnabled(false);
//                          back2Button.setEnabled(false);
//                          break;
//                       }
//                    saveViewButton.setEnabled(false);
//                    newValuetextfield.setEnabled(true);
//                    back2Button.setEnabled(true);
//                }

                int row1 = markingTable.getSelectedRow();
                if (whichStatus.equals("Listing")) {
                    String selectedValue = markingTable.getValueAt(row1, 4).toString();

                    if (selectedValue.equals("true")) {
                        saveViewButton.setEnabled(true);
                        newValuetextfield.setEnabled(false);
                    //back2Button.setEnabled(false);
                    } else if (selectedValue.equals("false")) {
                        saveViewButton.setEnabled(false);
                        newValuetextfield.setEnabled(true);
                        back2Button.setEnabled(true);
                    }
                }
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }
}
