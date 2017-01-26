/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import client.ClientTask;
import client.TaskExecuteQuery;
import client.TaskSendQueryData;
import com.lexpar.util.Log;
import common.QueryData;
import common.edit.ProjectMapper;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import model.QueryComboModel;
import ui.SplitPaneViewer;

/**
 * Called from beans.IbaseTextField. Adding and editing the Query Data.
 * @see beans.IbaseTextField 
 * @author bmurali
 */
final public class AddEditQuery extends JDialog {

    private static final int FIELD_LENGTH = 255;
    private ProjectMapper projectMaper;
    private ProjectMapper.HashValue field;
    private QueryData queryData;
    private String field_name;
    private String batchNumber;
    private String batesNumber;
    private String filePath;
    private String whichStatus;
    private int childId;
    private int batchId;
    private JPanel selectPanel = new javax.swing.JPanel();
    private JButton saveButton = new JButton("Save");
    private JButton cancelButton = new JButton("Cancel");
    private JTextField fieldName;
    private JTextField documentName;
    private JTextField volumeName;
    private JTextField projectName;
    private JComboBox processlevel;
    private JComboBox dtyg;
    private JComboBox dtys;
    private JComboBox collection;
    private JTextArea description;
    private JComboBox generalQuestion;
    private JTextArea specificQuestion;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private LComboBox teamLead;
    private JCheckBox imagePath;
    private JTextArea answer;
    private QueryComboModel fieldModel = null;
    private QueryComboModel generalQuestionModel = null;
    private final String GET_ALL_TL = "Get all team lead";
    private final String GET_ALL_GENERAL_QUESTION = "Get general question";
    private LGridBag namePane = new LGridBag();    
    private String[] collectionValue = {"<--Select-->", "GPDIC", "LDPC"};
    private String[] processlevelValue = {"<--Select-->", "L1", "L2"};
    private String[] dtygtypes = {"<--Select-->", "Bulletin"};
    private String[] dtystypes = {"<--Select-->", "Bulletin"};    
    private ProjectMapper.HashValue value = null;

    public AddEditQuery(Component parent, ProjectMapper projectMap, String fieldName, String batesNumber, 
                        String filePath, String whichStatus, int childId, int batchId) {
        super(JOptionPane.getFrameForComponent(parent));
        this.projectMaper = projectMap;
        this.field_name = fieldName;
        this.batesNumber = batesNumber;
        this.filePath = filePath;
        this.whichStatus = whichStatus;
        this.childId = childId;
        Log.print("childId     ----->" + childId);
        Log.print("projectMaper----->" + projectMaper);
        this.batchId = batchId;
        value = projectMaper.getHashValue(field_name);
        getContentPane().add(selectPanel, BorderLayout.CENTER);
        selectPanel.setLayout(new BorderLayout());
        selectPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        saveButton.addActionListener(buttonComboListener);
        // disabled for edit until first change
        //saveButton.setEnabled(false);
        cancelButton.addActionListener(buttonComboListener);
        JPanel outerButtonPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 30, 20));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        outerButtonPanel.add(buttonPanel);
        selectPanel.add(outerButtonPanel, BorderLayout.SOUTH);        
        setTitle("iBase Query Tracker");
        field = projectMaper.getHashValue(field_name);
        getUserData();    
    }
    private ActionListener buttonComboListener = new ActionListener() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                Object source = A.getSource();
                if (source == saveButton) {
                    if (save()) {
                        dispose();
                    }
                } else if (source == cancelButton) {
                    dispose();
                }

            } catch (Exception exc) {
            }
        }
    };

    private void addControls() {

        fieldName = new JTextField(30);
        fieldName.setText(field_name);
        fieldName.setEnabled(false);
        namePane.add(0, 0, "  ", new JLabel(" "));
        namePane.add(0, 1, "Field Name:", fieldName);
        namePane.add(0, 2, "  ", new JLabel(" "));
        namePane.add(0, 4, "  ", new JLabel(" "));
        namePane.add(0, 6, "  ", new JLabel(" "));
        namePane.add(0, 8, "  ", new JLabel(" "));
        namePane.add(0, 10, "  ", new JLabel(" "));
        namePane.add(0, 12, "  ", new JLabel(" "));
        namePane.add(0, 14, "  ", new JLabel(" "));
        namePane.add(0, 16, "  ", new JLabel(" "));
        namePane.add(0, 18, "  ", new JLabel(" "));
        namePane.add(0, 20, "  ", new JLabel(" "));
        namePane.add(0, 22, "  ", new JLabel(" "));
        namePane.add(0, 24, "  ", new JLabel(" "));
        namePane.add(0, 26, "  ", new JLabel(" "));
        namePane.add(0, 28, "  ", new JLabel(" "));
        documentName = new JTextField(30);
        documentName.setEnabled(false);
        documentName.setText(batchNumber);
        namePane.add(0, 3, "Document Name:", documentName);
        projectName = new JTextField(30);
        namePane.add(0, 5, "Project Name:", projectName);
        volumeName = new JTextField(30);
        namePane.add(0, 7, "Volume Name:", volumeName);
        processlevel = new JComboBox(processlevelValue);        
        namePane.add(0, 9, "Process Level:", processlevel);
        dtyg = new JComboBox(dtygtypes);
        namePane.add(0, 11, "DTYG:", dtyg);
        dtys = new JComboBox(dtystypes);
        namePane.add(0, 13, "DTYS:", dtys);
        collection = new JComboBox(collectionValue);        
        namePane.add(0, 15, "Collection:", collection);
        description = new JTextArea(5, 30);
        jScrollPane1 = new javax.swing.JScrollPane();
        description.setLineWrap(true);
        description.setBorder(new LineBorder(Color.white));
        description.setFocusable(true);
        description.setEditable(true);
        description.addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent e) {
                testFields();
            }
        });

        jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane1.setViewportView(description);
        namePane.add(0, 17, "Description:", jScrollPane1);
        //generalQuestion = new JComboBox(generalquestion); 
        generalQuestion.addActionListener(buttonComboListener);
        namePane.add(0, 19, "General Question:", generalQuestion);
        specificQuestion = new JTextArea(5, 30);
        jScrollPane2 = new javax.swing.JScrollPane();
        specificQuestion.setLineWrap(true);
        specificQuestion.setBorder(new LineBorder(Color.white));
        specificQuestion.setFocusable(true);
        specificQuestion.setEditable(true);
        specificQuestion.addKeyListener(new KeyAdapter() {

            public void keyReleased(KeyEvent e) {
                testFields();
            }
        });
        jScrollPane2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setViewportView(specificQuestion);
        namePane.add(0, 21, "Specific Question:", jScrollPane2);
        teamLead.addActionListener(buttonComboListener);
        namePane.add(0, 23, "Assign To:", teamLead);

        answer = new JTextArea(5, 30);
        jScrollPane3 = new javax.swing.JScrollPane();
        answer.setLineWrap(true);
        answer.setBorder(new LineBorder(Color.white));
        answer.setFocusable(true);
        answer.setEditable(false);
        jScrollPane3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane3.setViewportView(answer);
        namePane.add(0, 25, "Answer:", jScrollPane3);

        imagePath = new JCheckBox("(Check to Upload Image)");
        namePane.add(0, 27, "Upload Image:", imagePath);

        selectPanel.add(namePane, BorderLayout.CENTER);
        pack();
    }

    private void getUserData() {

        fieldModel = new QueryComboModel(GET_ALL_TL);
        teamLead = new LComboBox(38);
        teamLead.setModel(fieldModel);

        generalQuestionModel = new QueryComboModel(GET_ALL_GENERAL_QUESTION);
        generalQuestion = new LComboBox(38);
        generalQuestion.setModel(generalQuestionModel);

        String[] parameters = new String[4];
        parameters[0] = field_name;
        parameters[1] = Integer.toString(field.projectId);
        parameters[2] = Integer.toString(childId);        
        parameters[3] = whichStatus;
        final ClientTask task;
        task = new TaskExecuteQuery("queryTracker", parameters);
        task.setCallback(new Runnable() {

            @Override
            public void run() {
                getUserByIdDataEntry((ResultSet) task.getResult());

            }
        });
        boolean ok = task.enqueue(this);

        addControls();
    }

    /**
     * Save the query data 
     * @return true if data saved successfully else return false.
     */
    private boolean save() {       
        final ClientTask task;
        int userId = fieldModel.getSelectedId();
        if (userId < 0) {
            userId = 0;
        }

        int generalQuestionId = generalQuestionModel.getSelectedId();
        if (generalQuestionId < 0) {
            generalQuestionId = 0;
        }
        queryData = new QueryData();       
        queryData.project_id = field.projectId;
        queryData.project_name = projectName.getText();
        queryData.volume_name = volumeName.getText();
        queryData.field_name = fieldName.getText();
        queryData.level = (String) processlevel.getSelectedItem();
        queryData.collection = (String) collection.getSelectedItem();
        queryData.description = description.getText();
        queryData.dtyg = (String) dtyg.getSelectedItem();
        queryData.dtys = (String) dtys.getSelectedItem();
        queryData.generalQuestion = generalQuestionId;
        queryData.specificQuestion = specificQuestion.getText();     
        queryData.uploadImage = imagePath.isSelected() ? "Yes" : "No";      
        queryData.field_type = value.fieldType;
        queryData.childId = childId;
        Log.print("queryData.childId----->" + queryData.childId);
        queryData.raised_to = userId;
        queryData.batch_id = batchId;
        queryData.posted_date = System.currentTimeMillis();
        task = new TaskSendQueryData(queryData);
        task.enqueue(this);
        return true;
    }

    /*
     * Enable the Ok button when all required field is filled up.
     */
    private void testFields() {
        testFieldsLength();
        if ((processlevel.getItemCount() > 1 && processlevel.getSelectedIndex() < 1) || (dtyg.getItemCount() > 1 
                && dtyg.getSelectedIndex() < 1) || (dtys.getItemCount() > 1 && dtys.getSelectedIndex() < 1) 
                || (collection.getItemCount() > 1 && collection.getSelectedIndex() < 1) 
                || (description.getText().isEmpty()) || (generalQuestion.getSelectedIndex() < 0 
                && specificQuestion.getText().isEmpty()) || (teamLead.getItemCount() > 1 
                && teamLead.getSelectedIndex() < 1)) {
            //saveButton.setEnabled(false);            
        } else {
            saveButton.setEnabled(true);
        }
    }

    /**
     * Check the length of the description.
     */
    private void testFieldsLength() {
        String descString;
        if ((descString = description.getText()).length() > FIELD_LENGTH) {
            description.setText(descString.substring(0, FIELD_LENGTH));
        }
        if ((descString = specificQuestion.getText()).length() > FIELD_LENGTH) {
            description.setText(descString.substring(0, FIELD_LENGTH));
        }
    }
    private ActionListener comboActionListner = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            testFields();
        }
    };

    private void getUserByIdDataEntry(ResultSet queryResult) {
        if (queryResult != null) {
            try {
                if ("Admin".equals(whichStatus)) {
                    saveButton.setEnabled(false);
                }

                ResultSetMetaData rsmd = queryResult.getMetaData();
                int columnCount = rsmd.getColumnCount();
                if (columnCount > 2) {
                    SplitPaneViewer viewer = SplitPaneViewer.getInstance();
                    viewer.setMarks("giveMarks");                   
                    if ("Coding".equals(whichStatus)) {
                        if (queryResult.next()) {
                            // updating a user
                            projectName.setText(queryResult.getString(1));
                            projectName.setEnabled(false);
                            volumeName.setText(queryResult.getString(2));
                            volumeName.setEnabled(false);
                            processlevel.setSelectedItem(queryResult.getString(4));
                            processlevel.setEnabled(true);
                            processlevel.addActionListener(comboActionListner);
                            collection.setSelectedItem(queryResult.getString(5));
                            collection.setEnabled(true);
                            collection.addActionListener(comboActionListner);
                            dtyg.setSelectedItem(queryResult.getString(6));
                            dtyg.setEnabled(true);
                            dtys.addActionListener(comboActionListner);
                            dtys.setSelectedItem(queryResult.getString(7));
                            dtys.setEnabled(true);
                            dtys.addActionListener(comboActionListner);
                            description.setText(queryResult.getString(8));
                            generalQuestion.setSelectedIndex(generalQuestionModel.indexOf(queryResult.getInt(9)));
                            generalQuestion.addActionListener(comboActionListner);
                            specificQuestion.setText(queryResult.getString(10));                            
                            teamLead.setSelectedIndex(fieldModel.indexOf(queryResult.getInt(11)));
                            teamLead.addActionListener(comboActionListner);
                            answer.setText(queryResult.getString(13));
                            if (answer.getText().trim().equals("")) {
                                answer.setEnabled(false);
                            }
                        }
                    } else if ("CodingQC".equals(whichStatus) || "Admin".equals(whichStatus)) {
                        if (queryResult.next()) {
                            // updating a user
                            saveButton.setEnabled(false);
                            projectName.setText(queryResult.getString(1));
                            projectName.setEnabled(false);
                            volumeName.setText(queryResult.getString(2));
                            volumeName.setEnabled(false);
                            processlevel.setSelectedItem(queryResult.getString(4));
                            processlevel.setEnabled(false);
                            collection.setSelectedItem(queryResult.getString(5));
                            collection.setEnabled(false);
                            dtyg.setSelectedItem(queryResult.getString(6));
                            dtyg.setEnabled(false);
                            dtys.setSelectedItem(queryResult.getString(7));
                            dtys.setEnabled(false);
                            description.setText(queryResult.getString(8));
                            description.setEnabled(false);
                            generalQuestion.setSelectedIndex(generalQuestionModel.indexOf(queryResult.getInt(9)));
                            generalQuestion.setEnabled(false);
                            specificQuestion.setText(queryResult.getString(10));
                            specificQuestion.setEnabled(false);                            
                            teamLead.setSelectedIndex(fieldModel.indexOf(queryResult.getInt(11)));
                            teamLead.setEnabled(false);
                            answer.setText(queryResult.getString(13));
                            answer.setEnabled(false);                            
                            imagePath.setEnabled(false);
                        }
                    }
                } else {
                    if (queryResult.next()) {
                        projectName.setText(queryResult.getString(1));
                        projectName.setEnabled(false);
                        volumeName.setText(queryResult.getString(2));
                        volumeName.setEnabled(false);
                    }
                }

            } catch (SQLException ex) {
                Logger.getLogger(AddEditQuery.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
