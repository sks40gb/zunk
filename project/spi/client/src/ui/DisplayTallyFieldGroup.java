/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import beans.ToolTipText;
import common.Log;
import common.TallyProcessData;
import common.msg.MessageConstants;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.swing.JDialog;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import model.QueryComboModel;
import model.SQLManagedComboModel;
import model.TallyFieldSelectionModel;

/**
 * Container to Hold Tally Field Group for a Selected Project and Volume
 *
 * @author bmurali
 */
public class DisplayTallyFieldGroup extends javax.swing.JDialog implements MessageConstants {

    private final String GET_ALL_PROJECTS = "AdvanceValidation.projectCombo";
    private final String GET_ALL_VOLUME = "listing.get volume";
    private JDialog parent;
    private String whichStatus;
    private javax.swing.JTable fieldsTable;
    private javax.swing.JTable viewEditTable;
    private javax.swing.JComboBox fieldCombo;
    private javax.swing.JPanel fieldsPane;
    private javax.swing.JScrollPane fieldsScrollPane;
    private javax.swing.JPanel bottomPane;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton okButton;
    private SQLManagedComboModel projectModel;
    private QueryComboModel fieldModel;    
    private int projectId = 0;   
    private TallyFieldSelectionModel tallymodel;
    private Map tallyProcessDataMap;
    private TallyProcessData tallyProcessData;

    public DisplayTallyFieldGroup(JDialog parent) {

        super(parent, true);
        setTitle("Project Selection for Tally");
        this.parent = parent;
        initComponents();

        setLocationRelativeTo(parent);
        //fieldsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fieldsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        fieldsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
                //Ignore extra messages.
                if (e.getValueIsAdjusting()) {
                    return;
                }
                ListSelectionModel lsm =
                        (ListSelectionModel) e.getSource();
                if (lsm.isSelectionEmpty()) {
                    okButton.setEnabled(false);

                } else {
                    int row = fieldsTable.getSelectedRow();
                    Object obj = fieldsTable.getValueAt(row, 0);
                    System.out.println("row-------->" + row);
                    System.out.println("obj-------->" + obj.toString());
                    okButton.setEnabled(true);

                }
            }
        });
        if ("Tally".equals(whichStatus)) {
            tabSelected();
        }
    }

    public DisplayTallyFieldGroup(JDialog parent, Map tallyProcessDataMap) {
        initComponents();
        this.tallyProcessDataMap = tallyProcessDataMap;
        this.parent = parent;
        fillTallyFieldData();
    }

    private void initComponents() {


        fieldCombo = new javax.swing.JComboBox();
        viewEditTable = new javax.swing.JTable();

        fieldsPane = new javax.swing.JPanel();
        fieldsScrollPane = new javax.swing.JScrollPane();
        fieldsTable = new ProjectFieldsTableClass();
        bottomPane = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();

        fieldCombo.setEnabled(false);
        fieldCombo.setPreferredSize(new java.awt.Dimension(150, 25));
        fieldCombo.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldComboActionPerformed(evt);
            }
        });
        fieldCombo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {

            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }

            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                fieldComboPopupMenuWillBecomeVisible(evt);
            }
        });



        fieldsPane.setLayout(new java.awt.BorderLayout());

        fieldsPane.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fieldsScrollPane.setPreferredSize(new java.awt.Dimension(500, 300));
        fieldsTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));


        viewEditTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Field Name", "Tally Group", "Tally Type"
                }) {

            boolean[] canEdit = new boolean[]{
                true, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        fieldsTable.setFocusable(false);
        fieldsTable.setIntercellSpacing(new java.awt.Dimension(2, 1));
        fieldsTable.addMouseListener(new java.awt.event.MouseAdapter() {

            public void mouseClicked(java.awt.event.MouseEvent evt) {
                fieldsTableMouseClicked(evt);
            }
        });

        //fieldsScrollPane.setViewportView(fieldsTable);
        fieldsScrollPane.setViewportView(viewEditTable);

        fieldsPane.add(fieldsScrollPane, java.awt.BorderLayout.CENTER);

        add(fieldsPane, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridLayout(1, 0, 20, 0));

        okButton.setText("  OK  ");
        okButton.setEnabled(true);
        okButton.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        jPanel1.add(okButton);

        bottomPane.add(jPanel1);
        add(bottomPane, java.awt.BorderLayout.SOUTH);

        //setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
        //setTitle("Project Selection");
        addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        /*   jPanel1.setLayout(new java.awt.GridBagLayout());
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
         */
        pack();
    }// </editor-fold>                        

    
    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible(false);
        dispose();
        parent.setVisible(true);
    }

    public Map getTallyProcessDataMap() {
        return tallyProcessDataMap;
    }

    public void setTallyProcessDataMap(Map tallyProcessDataMap) {
        this.tallyProcessDataMap = tallyProcessDataMap;
    }

    private class ProjectFieldsTableClass extends JTable {

        public String getToolTipText(MouseEvent event) {
            return ToolTipText.getToolTipText(event, fieldsTable);
        }

        public Point getToolTipLocation(MouseEvent event) {
            return ToolTipText.getToolTipLocation(event, fieldsTable);
        }
    }

    protected void tabSelected() {
        Log.print("Tally Button selected");
        // project model
        projectModel = new SQLManagedComboModel(GET_ALL_PROJECTS);
        projectModel.register();
        //projectCombo.setModel(projectModel);        
    }

    private void fieldComboPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
        //Log.print("(PopulatePage.volumeComboPopupMenuWillBecomeVisisble) projectId " + projectId);
        fieldModel = new QueryComboModel(GET_ALL_VOLUME, /* required? */ false, new String[]{Integer.toString(projectId)}, "");

        fieldCombo.setModel(fieldModel);
    }

    private void fieldComboActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void fillTallyFieldData() {
        String headings[] = {"Field Name", "Tally Group", "Type"};
        Object object[][] = new Object[tallyProcessDataMap.size()][headings.length + 1];

        if (null != tallyProcessDataMap) {
            Set dataKeys = tallyProcessDataMap.keySet();
            Iterator dataIterator = dataKeys.iterator();
            int i = 0;
            while (dataIterator.hasNext()) {
                tallyProcessData = (TallyProcessData) tallyProcessDataMap.get(dataIterator.next());

                if (null != tallyProcessData) {
                    object[i][0] = tallyProcessData.getProjectFieldName();
                    object[i][1] = tallyProcessData.getGroupCount();
                    object[i][2] = tallyProcessData.getTallyType();
                    i++;
                }
            }
        }

        tallymodel = new TallyFieldSelectionModel(object, headings);
        viewEditTable.setModel(tallymodel);
    }

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
        dispose();
        //parent.setVisible(true);
        ((ProjectSelectionDialogForTally)parent).clearComponents();
        ((ProjectSelectionDialogForTally)parent).setVisible(true);
    }

    private void fieldsTableMouseClicked(java.awt.event.MouseEvent evt) {
        try {
            if (evt.getClickCount() > 1) {
                // double-click on a row
                //proceedButton.doClick();
                okButton.setEnabled(true);
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }
}
