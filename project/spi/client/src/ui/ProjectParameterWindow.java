package ui;

import client.ClientTask;
import client.TaskExecuteQuery;
import client.TaskExecuteUpdate;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.AbstractTableModel;

/** 
 * Dialog Window to Display Project Parameter 
 * @author  anurag
 */
public class ProjectParameterWindow extends JDialog {

    public JDialog parent = null;
    CheckBoxTableModel model = null;
    public int userId = 0;
    private ResultSet results = null;
    private final static String GET_FIELD_NAMES = "Sampling.getFields";
    private final static String GET_DOCUMENT_NUMBER = "Sampling.getDocumentNumber";
    private int volume_id = 0;
    private int project_id = 0;
    private int documentChildCount = 0;
    private int documentNumberCount = 0;
    private String sampling_type = null;

    /**
     * Creates new form ProjectParameterWindow
     * @param parent the frame in which to place this screen
     */
    public ProjectParameterWindow(JDialog parent, int volumeid, int projectid) {
        super(parent, true);
        this.parent = parent;
        this.volume_id = volumeid;
        this.project_id = projectid;
        initComponents(this.volume_id, this.project_id);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents(int volumeId, int projectid) {
        coderPanel = new javax.swing.JPanel();
        fieldPanel = new javax.swing.JPanel();
        coderPane = new javax.swing.JScrollPane();
        fieldPane = new javax.swing.JScrollPane();
        fieldCountedTable = new javax.swing.JTable();
        fieldShownTable = new javax.swing.JTable();
        bottomPane = new javax.swing.JPanel();
        headerPane = new javax.swing.JPanel();
        rightheaderPane = new javax.swing.JPanel();
        coderButtonPanel = new javax.swing.JPanel();
        fieldButtonPanel = new javax.swing.JPanel();
        leftContentPanel = new JPanel(new BorderLayout());
        rightContentPanel = new JPanel(new BorderLayout());
        projectParameterPanel = new JPanel(new BorderLayout());
        contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectAllCoderCheckBox = new JCheckBox("Select All Fields");
        selectAllFieldCheckBox = new JCheckBox("Select All Fields");

        addWindowListener(new java.awt.event.WindowAdapter() {

            public void windowClosing(java.awt.event.WindowEvent evt) {
                windowCloseActionPerformed();
            }
        });

        getProjectFields(projectid);
        getDocumentNumbers();
        headerLabel = new javax.swing.JLabel("Fields to be Counted in Sampling Data");
        headerLabel.setFont(new Font("ARIAL", Font.BOLD, 12));
        fieldLabel = new javax.swing.JLabel("Fields to be Shown in Sampling Data");
        fieldLabel.setFont(new Font("ARIAL", Font.BOLD, 12));
        this.setSize(1100, 600);
        this.setLocationRelativeTo(parent);
        this.setTitle("Project Parameters");

        headerPane.setLayout(new FlowLayout());
        headerPane.add(headerLabel);

        rightheaderPane.setLayout(new FlowLayout());
        rightheaderPane.add(fieldLabel);

        coderPanel.setLayout(new java.awt.BorderLayout());

        coderPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        coderPane.setViewportView(fieldCountedTable);

        coderPanel.add(coderPane, java.awt.BorderLayout.CENTER);

        leftContentPanel.setPreferredSize(new Dimension(350, 500));
        leftContentPanel.add(coderPanel, java.awt.BorderLayout.CENTER);

        fieldPanel.setLayout(new java.awt.BorderLayout());

        fieldPanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        fieldPane.setViewportView(fieldShownTable);
        fieldPanel.add(fieldPane, BorderLayout.CENTER);
        rightContentPanel.setPreferredSize(new Dimension(350, 500));
        rightContentPanel.add(rightheaderPane, BorderLayout.NORTH);
        rightContentPanel.add(fieldPanel, java.awt.BorderLayout.CENTER);
        selectAllCoderCheckBox.setSelected(true);
        selectAllCoderCheckBox.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllCheckBoxes(fieldCountedTable, selectAllCoderCheckBox);
            }
        });

        coderButtonPanel.add(selectAllCoderCheckBox);

        bottomPane.add(coderButtonPanel);
        selectAllFieldCheckBox.setSelected(true);
        selectAllFieldCheckBox.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllCheckBoxes(fieldShownTable, selectAllFieldCheckBox);
            }
        });
        fieldButtonPanel.add(selectAllFieldCheckBox);
        rightContentPanel.add(fieldButtonPanel, BorderLayout.SOUTH);

        leftContentPanel.setBorder(new LineBorder(Color.BLACK));
        rightContentPanel.setBorder(new LineBorder(Color.BLACK));
        leftContentPanel.add(headerPane, java.awt.BorderLayout.NORTH);
        leftContentPanel.add(bottomPane, java.awt.BorderLayout.SOUTH);

        projectParameterPanel.add(new SamplingPanel(), BorderLayout.CENTER);
        projectParameterPanel.setBorder(new LineBorder(Color.black));
        projectParameterPanel.setPreferredSize(new Dimension(360, 500));
        contentPanel.add(projectParameterPanel);
        contentPanel.add(leftContentPanel);
        contentPanel.add(rightContentPanel);
        //add(contentPanel);

        getContentPane().add(contentPanel, java.awt.BorderLayout.CENTER);

        pack();



    }

    //Action to be handled for close button action.
    private void windowCloseActionPerformed() {
        ClientTask task = new TaskExecuteUpdate("SplitPaneViewer.closeMenuItem");
        task.enqueue();
        setVisible(false);
        this.parent.setVisible(true);
    }

    public void proceedSampling(String samplingMethod, String samplingType, String inspectionLevel, String aql,
                                String accuracy, String percentSampling) {

        this.sampling_type = samplingType;
        int fieldsCount = fieldCountedTable.getRowCount();
        int fieldsShown = fieldShownTable.getRowCount();
        int lot_batch_size = 0;
        
        //we have two types in Field Sampling - field and tag display.
        if (samplingType.startsWith("Field Sampling")) {
            lot_batch_size = fieldsCount * documentNumberCount;
        } else {
            lot_batch_size = documentNumberCount;
        }
        StringBuffer fieldsCountString = new StringBuffer();
        StringBuffer fieldsShownString = new StringBuffer();
        for (int i = 0; i < fieldsCount; i++) {
            boolean isChecked = (Boolean) fieldCountedTable.getModel().getValueAt(i, 0);
            if (isChecked) {
                if (i != 0 && fieldsCountString.length() != 0) {
                    fieldsCountString.append(",");
                }
                fieldsCountString.append(fieldCountedTable.getModel().getValueAt(i, 2));
            }
        }

        for (int i = 0; i < fieldsShown; i++) {
            boolean isChecked = (Boolean) fieldShownTable.getModel().getValueAt(i, 0);
            if (isChecked) {
                if (i != 0 && fieldsShownString.length() != 0) {
                    fieldsShownString.append(",");
                }
                fieldsShownString.append(fieldShownTable.getModel().getValueAt(i, 2));
            }
        }

        this.setVisible(false);
        ShowCoderBatchInformation scbi = new ShowCoderBatchInformation(this, volume_id, project_id);
        scbi.setLocationRelativeTo(null);
        scbi.setVisible(true);
        scbi.setProjectParameters(documentNumberCount, lot_batch_size, inspectionLevel, aql, samplingMethod,
                                                samplingType, accuracy, fieldsCountString, fieldsShownString);
        
        this.setDocumentChildCount(scbi.getDocumentCount());
    }

    private void selectAllCheckBoxes(JTable jTableObj, JCheckBox jcheckBoxObj) {
        int count = jTableObj.getRowCount();
        for (int i = 0; i < count; i++) {
            jTableObj.getModel().setValueAt(new Boolean(jcheckBoxObj.isSelected()), i, 0);
        }
    }

    private void getProjectFields(int projectid) {
        try {
            final ClientTask task = new TaskExecuteQuery(GET_FIELD_NAMES, Integer.toString(projectid));
            task.setCallback(new Runnable() {

                public void run() {
                    try {
                        results = (java.sql.ResultSet) task.getResult();
                        model = new CheckBoxTableModel(results, new String[]{"", "No.", "Field Name"});
                        fieldShownTable.setModel(model);
                        fieldShownTable.getColumnModel().getColumn(0).setMaxWidth(20);
                        fieldShownTable.getColumnModel().getColumn(1).setMaxWidth(30);
                        selectAllCheckBoxes(fieldShownTable, selectAllFieldCheckBox);

                        model = new CheckBoxTableModel(results, new String[]{"", "No.", "Field Name"});
                        fieldCountedTable.setModel(model);
                        fieldCountedTable.getColumnModel().getColumn(0).setMaxWidth(20);
                        fieldCountedTable.getColumnModel().getColumn(1).setMaxWidth(30);
                        selectAllCheckBoxes(fieldCountedTable, selectAllCoderCheckBox);
                    } catch (SQLException ex) {
                        Logger.getLogger(ProjectParameterWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
            });
            task.enqueue(this);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void getDocumentNumbers() {
        try {
            final ClientTask task = new TaskExecuteQuery(GET_DOCUMENT_NUMBER, Integer.toString(volume_id));

            task.setCallback(new Runnable() {

                public void run() {
                    try {
                        results = (java.sql.ResultSet) task.getResult();
                        while (results.next()) {
                            documentNumberCount++;
                        }
                    } catch (SQLException ex) {
                        System.err.println("Exception in getDocumentNumber() ---> " + ex);
                    }
                }
            });
            task.enqueue(this);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public int getDocumentChildCount() {
        return documentChildCount;
    }

    public void setDocumentChildCount(int documentChildCount) {
        this.documentChildCount = documentChildCount;
    }    // Variables declaration -       
    private javax.swing.JPanel bottomPane;
    private javax.swing.JPanel headerPane;
    private javax.swing.JPanel rightheaderPane;
    private javax.swing.JLabel headerLabel;
    private javax.swing.JLabel fieldLabel;
    private javax.swing.JPanel coderButtonPanel;
    private javax.swing.JPanel coderPanel;
    private javax.swing.JPanel fieldButtonPanel;
    private javax.swing.JPanel fieldPanel;
    private javax.swing.JScrollPane coderPane;
    private javax.swing.JScrollPane fieldPane;
    private javax.swing.JTable fieldCountedTable;
    private javax.swing.JTable fieldShownTable;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JPanel leftContentPanel;
    private javax.swing.JPanel rightContentPanel;
    private javax.swing.JPanel projectParameterPanel;
    private JCheckBox selectAllCoderCheckBox;
    private JCheckBox selectAllFieldCheckBox;
    // End of variables declaration                   

//------new class for displaying checkbox in JTable-------
    class CheckBoxTableModel extends AbstractTableModel {

        private ResultSet resultSet = null;
        private String[] columnNames = null;
        private Object[][] data;

        public CheckBoxTableModel(ResultSet results, String[] headings) throws SQLException {
            this.resultSet = results;
            this.columnNames = headings;
            int count = 0;
            while (results.next()) {
                count++;
            }
            data = new Object[count][4];
            getData(results, count);
        }

        private Object[][] getData(ResultSet resultSet, int count) {
            try {
                for (int i = 0; resultSet.previous(); i++) {
                    data[i][0] = new Boolean(resultSet.getString(1));
                    if (this.columnNames.length < 4) {
                        data[i][1] = i + 1 + "";
                        data[i][2] = resultSet.getString(3);
                    } else {
                        data[i][1] = resultSet.getString(2);
                        data[i][2] = resultSet.getString(3);
                        data[i][3] = resultSet.getString(4);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return data;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public ResultSet getResultSet() {
            return resultSet;
        }

        public void setResultSet(ResultSet resultSet) {
            this.resultSet = resultSet;
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col > 0) {
                return false;
            }
            return true;
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }
    
//------the left most panel for project parameters window---------
    class SamplingPanel extends JPanel {

        public JButton proceedButton;
        public JTextField accuracyTextField;       
        public JLabel samplingMethodComboBox;
        public JComboBox samplingTypeComboBox;
        public JComboBox inspectionLevelComboBox;
        public JComboBox aqlComboBox;
        public JComboBox samplingComboBox;
        public JLabel samplingMethodLabel;
        public JLabel samplingTypeLabel;
        public JLabel inspectionLevelLabel;
        public JLabel aqlLabel;
        public JLabel accuracyLabel;
        public JLabel samplingLabel;
        
        private String[] SAMPLING_TYPES = new String[]{"Field Sampling - Field Display",
                                                       "Field Sampling - Tag Display",
                                                       "Document Sampling - Field Display",
                                                       "Document Sampling - Tag Display"};
        
        private String[] INSPECTION_LEVELS = new String[]{"Normal", "Reduced", "Tightened"};
        private String[] SAMPLING_PERCENTAGES = new String[]{"5%", "10%", "15%", "20%", "50%", "100%"};
        private Double[] ACCEPTED_QUALTIY_LIMITS = new Double[]{0.10, 0.15, 0.25, 0.40, 0.65, 1.0, 1.5, 2.5, 4.0, 6.5};
                
        public SamplingPanel() {

            GridBagLayout gbl = new GridBagLayout();
            setLayout(gbl);                   

            GridBagConstraints gbc = new GridBagConstraints(); 
            // may use for all components given that particular constraints are set preliminary
            samplingMethodComboBox = new JLabel("ISO 2859-1");
            samplingMethodComboBox.setFont(new java.awt.Font("Dialog", Font.BOLD, 14));
            samplingTypeComboBox = new JComboBox( SAMPLING_TYPES );
            inspectionLevelComboBox = new JComboBox( INSPECTION_LEVELS );
            aqlComboBox = new JComboBox( ACCEPTED_QUALTIY_LIMITS );
            accuracyTextField = new JTextField();
            samplingComboBox = new JComboBox( SAMPLING_PERCENTAGES );
            samplingComboBox.setEnabled(false);
            accuracyTextField.setEnabled(false);            
            
            samplingMethodLabel = new JLabel("Sampling Method :");
            samplingTypeLabel = new JLabel("Sampling Type :");
            inspectionLevelLabel = new JLabel("Inspection Level :");
            aqlLabel = new JLabel("AQL :");
            accuracyLabel = new JLabel("Required Accuracy :");
            samplingLabel = new JLabel("Percent Sampling :");
            proceedButton = new JButton("Next");
            proceedButton.setMnemonic('P');
            proceedButton.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    proceedSampling("ISO 2859-1",
                                        samplingTypeComboBox.getSelectedItem().toString(), 
                                        inspectionLevelComboBox.getSelectedItem().toString(),
                                        aqlComboBox.getSelectedItem().toString(),
                                        accuracyTextField.getText(),
                                        samplingComboBox.getSelectedItem().toString());
                }
            });

            //Now start defining constraints for a control and add it to the samplingPanel:

            // samplingMethodLabel first
            buildConstraints(gbc, 0, 0, 1, 1, 30, 10, GridBagConstraints.NONE, GridBagConstraints.CENTER);
            gbl.setConstraints(samplingMethodLabel, gbc);
            add(samplingMethodLabel);

            // next  samplingTypeLabel, e.g.    - same process with different values:
            buildConstraints(gbc, 0, 1, 1, 1, 0, 10, GridBagConstraints.NONE, GridBagConstraints.CENTER);
            gbl.setConstraints(samplingTypeLabel, gbc);
            add(samplingTypeLabel);

            // next  inspectionLevelLabel, e.g.    - same process with different values:
            buildConstraints(gbc, 0, 2, 1, 1, 0, 10, GridBagConstraints.NONE, GridBagConstraints.CENTER);
            gbl.setConstraints(inspectionLevelLabel, gbc);
            add(inspectionLevelLabel);

            // next  aqlLabel, e.g.    - same process with different values:
            buildConstraints(gbc, 0, 3, 1, 1, 0, 10, GridBagConstraints.NONE, GridBagConstraints.CENTER);
            gbl.setConstraints(aqlLabel, gbc);
            add(aqlLabel);

            // next  accuracyLabel, e.g.    - same process with different values:
            buildConstraints(gbc, 0, 4, 1, 1, 0, 10, GridBagConstraints.NONE, GridBagConstraints.CENTER);
            gbl.setConstraints(accuracyLabel, gbc);
            //add(accuracyLabel); 

            // next  samplingLabel, e.g.    - same process with different values:
            buildConstraints(gbc, 0, 5, 1, 1, 0, 10, GridBagConstraints.NONE, GridBagConstraints.CENTER);
            gbl.setConstraints(samplingLabel, gbc);
            //add(samplingLabel); 

            // next  samplingMethodComboBox:
            buildConstraints(gbc, 1, 0, 1, 1, 70, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
            gbl.setConstraints(samplingMethodComboBox, gbc);
            add(samplingMethodComboBox);

            // next  samplingTypeComboBox:
            buildConstraints(gbc, 1, 1, 1, 1, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
            gbl.setConstraints(samplingTypeComboBox, gbc);
            add(samplingTypeComboBox);

            // next  inspectionLevelComboBox:
            buildConstraints(gbc, 1, 2, 1, 1, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
            gbl.setConstraints(inspectionLevelComboBox, gbc);
            add(inspectionLevelComboBox);

            // next  aqlComboBox:
            buildConstraints(gbc, 1, 3, 1, 1, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
            gbl.setConstraints(aqlComboBox, gbc);
            add(aqlComboBox);

            // next  accuracyTextField:
            buildConstraints(gbc, 1, 4, 1, 1, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
            gbl.setConstraints(accuracyTextField, gbc);
            //add(accuracyTextField);

            // next  samplingTextField:
            buildConstraints(gbc, 1, 5, 1, 1, 0, 0, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
            gbl.setConstraints(samplingComboBox, gbc);
            //add(samplingComboBox);    

            // at last, we want to add proceedButtonton, ant it will occupy TWO cells in the third row:
            buildConstraints(gbc, 0, 8, 2, 1, 0, 10, GridBagConstraints.NONE, GridBagConstraints.CENTER);
            gbl.setConstraints(proceedButton, gbc);
            add(proceedButton);

        }

        public void buildConstraints(GridBagConstraints gbc, int x, int y, int w, int h, int wx, int wy, int fill, int anchor) {
            gbc.gridx = x;      // start cell in a row
            gbc.gridy = y;      // start cell in a column
            gbc.gridwidth = w;  // how many column does the control occupy in the row
            gbc.gridheight = h;  // how many column does the control occupy in the column
            gbc.weightx = wx;    // relative horizontal size
            gbc.weighty = wy;    // relative vertical size
            gbc.fill = fill;    // the way how the control fills cells    
            gbc.anchor = anchor; // alignment             
        }

        public Insets getInsets() {
            // Creates and initializes a new Insets object with the specified
            // top, left, bottom, and right insets.
            return new Insets(30, 30, 10, 30);
        }
    }
}