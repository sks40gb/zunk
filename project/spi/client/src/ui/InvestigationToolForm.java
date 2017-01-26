/*
 * LoadToolVersionForm.java
 *
 * Created on April 29, 2008, 3:03 PM
 */
package ui;

import model.CommonTableModel;
import valueobjects.FileError;
import export.IToolXlsReaderWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import model.SQLManagedComboModel;
import client.ClientTask;
import client.TaskRequestInvestigationData;
import valueobjects.Feedback;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/** 
 * Component for Loading the i-itool information from xls based on the project and volume.
 *
 * @author  sunil
 */
public class InvestigationToolForm extends javax.swing.JFrame {

    private final String GET_ALL_PROJECTS = "AdvanceValidation.projectCombo";
    private int projectId = 0;
    private String xlsFilePath;
    //xls file reader for the investigaiton tool
    private IToolXlsReaderWriter xlsfilereader = null;
    private SQLManagedComboModel projectModel = null;
    private CommonTableModel errorTableModel = null;
    private JFrame parent = null;
    private ArrayList<Feedback> recordList;
    private ArrayList<Feedback> feedbackList;
    public Feedback feedback;
    private int loadingDataProgressBarIndex = 0;
    private final String LOCK = "lock";
    private boolean isValidationSuccessful = false;
    private int multipleTallySize = 0;

    public InvestigationToolForm() {
        initComponents();
        isValidationSuccessful = false;
        spiLogoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spica.jpg")));
    }

    /** Creates new form LoadToolVersionForm */
    public InvestigationToolForm(JFrame parent) {
        this.parent = parent;
        initComponents();
        spiLogoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/spica.jpg")));
        //get all projects and set to project combobox.
        projectModel = new SQLManagedComboModel(GET_ALL_PROJECTS);
        projectModel.register();
        projectComboBox.setModel(projectModel);
    }

    /**
     * 1. reading the contents from the xls file and increasing the progressbar 
     *    accordingly.
     * 2. disable the load data while reading the file.
     * 3. performing validation.
     * 
     * @throws java.io.FileNotFoundException 
     */
    public void readFile() throws FileNotFoundException {
        try {
            loadingDataProgressBar.setValue(0);
            xlsFilePath = excelReportTextField.getText();
            new Thread() {

                public void run() {
                    try {
                        loadDataButton.setEnabled(false);
                        investigateButton.setEnabled(false);
                        synchronized (LOCK) {
                            for (int i = 1; i <= 100; i++) {
                                Thread.sleep(10);
                                validationProgressBar.setValue(i);
                            }
                        }

                        if (!isValidationSuccessful) {
                            return;
                        }
                        recordList = xlsfilereader.getRecordsForSheet(0);
                        loadData();
                        loadDataButton.setEnabled(true);
                        investigateButton.setEnabled(true);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        loadDataButton.setEnabled(true);
                    }
                }
            }.start();

            isValidationSuccessful = false;

            synchronized (LOCK) {
                if (!validateXlsFile(xlsFilePath)) {
                    investigateButton.setEnabled(false);
                    loadDataButton.setEnabled(true);
                    //ValidatingExcelProgressBar.setValue(0);
                    //loadingDataProgressBar.setValue(0);
                    recordList = null;
                    isValidationSuccessful = false;
                } else {
                    isValidationSuccessful = true;
                }
                try {
                    LOCK.notifyAll();
                } catch (IllegalMonitorStateException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception ex) {
            loadDataButton.setEnabled(true);
            Logger.getLogger(InvestigationToolForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Validating the xls file going to be read. Perform the investigation operation
     * if succeed else show error message.
     * @param xlsFilePath - xls file path.
     * @return return null if no error found else return list of errors.
     * @throws java.io.FileNotFoundException - if xls file is not found.
     * @throws java.io.IOException - excepton occured while reading xls file.
     */
    private boolean validateXlsFile(String xlsFilePath) throws FileNotFoundException, IOException {
        String error = null;
        ArrayList<FileError> errorList = null;
        try {
            //test file exits or not 
            File file = new File(xlsFilePath);
            if (!file.isFile()) {
                Object object[][] = {{"Missing File", "incorrect file path"}};
                errorTableModel = new CommonTableModel(object);
                errorMsgsTable.setModel(errorTableModel);
                return false;

            } else {
                //ValidatingExcelProgressBar.setValue(0);

                //xlsfilereader = new IToolXlsReaderWriter(xlsFilePath);
                xlsfilereader = IToolXlsReaderWriter.reader(xlsFilePath);
                if ((errorList = xlsfilereader.validateXlsSheetFile(0)) != null) {
                    Object object[][] = new Object[errorList.size()][2];
                    for (int i = 0; i < errorList.size(); i++) {
                        object[i][0] = errorList.get(i).getErrorType();
                        object[i][1] = errorList.get(i).getError();
                    }
                    errorTableModel = new CommonTableModel(object);
                    errorMsgsTable.setModel(errorTableModel);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            error = "Cannot read file. File is corrupted. Please load a valid report file.";
            Object object[][] = {{"Invalid Report", error}};
            errorTableModel = new CommonTableModel(object);
            errorMsgsTable.setModel(errorTableModel);
            return false;
        }
        recordList = xlsfilereader.getRecordsForSheet(0);
        return errorList == null;

    }

    /**
     * If validation is succeed then load the date from xls file and DB.
     * @throws java.lang.InterruptedException
     */
    private void loadData() throws InterruptedException {
        loadingDataProgressBarIndex = 1;
        if (projectComboBox.getSelectedIndex() > -1) {
            int sel = projectComboBox.getSelectedIndex();
            projectId = ((SQLManagedComboModel) projectComboBox.getModel()).getIdAt(sel);
            Feedback.setProjecId(projectId);
        }
        feedbackList = new ArrayList<Feedback>();
        for (Feedback feedback1 : recordList) {
            this.feedback = feedback1;
            getRecordFor(feedback1);
            synchronized (LOCK) {
                LOCK.wait();
            }
        }
    }

    /**
     * Reading each record of xls file and increasing the progressbar accordingly.
     * @param feedback1
     */
    private synchronized void getRecordFor(Feedback feedback1) {
        final ClientTask task;
        task = new TaskRequestInvestigationData(feedback1);
        task.setCallback(new Runnable() {

            String eventType = null;
            String user_id = null;
            String image_path = null;
            StringTokenizer tokens;
            private ResultSet results;

            public void run() {
                List imageList = new ArrayList();
                List tallyList = new ArrayList();
                List tallyValueList = new ArrayList();
                results = (java.sql.ResultSet) task.getResult();
                try {
                    if (results.next()) {
                        feedback.setCoder(results.getString(1));
                        feedback.setCoderValue(results.getString(2));

                        feedback.setChecker(results.getString(3));
                        feedback.setCheckerValue(results.getString(4));

                        feedback.setListing(results.getString(5));
                        feedback.setListingValue(results.getString(6));

                        feedback.setTally(results.getString(7));
                        feedback.setTallyValue(results.getString(8));

                        feedback.setQa(results.getString(9));
                        feedback.setQaValue(results.getString(10));

                       // feedback.setImagePath(results.getString(11));

                        StringTokenizer tokens = new StringTokenizer(results.getString(11), "|");
                        while (tokens.hasMoreTokens()) {
                            tallyValueList.add(tokens.nextToken());
                        }
                        feedback.setMultipleTallyValueList(tallyValueList);
                        
                        StringTokenizer tokens1 = new StringTokenizer(results.getString(12), "|");
                         
                        while (tokens1.hasMoreTokens()) {
                            
                           tallyList.add(tokens1.nextToken());                             
                        }
                        feedback.setMultipleTallyList(tallyList);
                        System.out.println("tallyList.size()==============>"+tallyList.size());
                        setMultipleTallySize(tallyList.size());
                        
                              
                        StringTokenizer tokens2 = new StringTokenizer(results.getString(14), "|");
                        while (tokens2.hasMoreTokens()) {
                            imageList.add(tokens2.nextToken());
                        }
                        feedback.setImageList(imageList);
                        
                    }

                    loadingDataProgressBar.setValue(loadingDataProgressBarIndex * 100 / recordList.size());
                    loadingDataProgressBarIndex++;
                    feedbackList.add(feedback);
                } catch (SQLException ex) {
                    Logger.getLogger(FeedbackInvestigation.class.getName()).log(Level.SEVERE, null, ex);
                }

                synchronized (LOCK) {
                    LOCK.notifyAll();
                }

            }
        });
        boolean ok = task.enqueue(this);
    }

    /**
     * clear the error table if no error is found.
     */
    public void clearTable() {
        Object object[][] = {{"", ""}};
        errorTableModel = new CommonTableModel(object);
        errorMsgsTable.setModel(errorTableModel);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jScrollBar1 = new javax.swing.JScrollBar();
        jProgressBar1 = new javax.swing.JProgressBar();
        validateExcelPanel = new javax.swing.JPanel();
        validateExcelProgressPanel = new javax.swing.JPanel();
        loadingDataPanel = new javax.swing.JPanel();
        loadingDataLabel = new javax.swing.JLabel();
        loadingDataProgressBar = new javax.swing.JProgressBar();
        validatingExcelPanel = new javax.swing.JPanel();
        ValidatingExcelLabel = new javax.swing.JLabel();
        validationProgressBar = new javax.swing.JProgressBar();
        investigateButton = new javax.swing.JButton();
        loadDataButton = new javax.swing.JButton();
        exitButton = new javax.swing.JButton();
        errorTablePanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        errorMsgsTable = new javax.swing.JTable();
        importFilePanel = new javax.swing.JPanel();
        projectComboBox = new javax.swing.JComboBox();
        excelReportPanel1 = new javax.swing.JPanel();
        browseReportButton = new javax.swing.JButton();
        excelReportTextField = new javax.swing.JTextField();
        excelReportLabel = new javax.swing.JLabel();
        projectLabel = new javax.swing.JLabel();
        spiLogoLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Investigation Tool");
        setBackground(new java.awt.Color(25, 25, 112));
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        validateExcelPanel.setBackground(new java.awt.Color(25, 25, 112));
        validateExcelPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        validateExcelProgressPanel.setBackground(new java.awt.Color(25, 25, 112));
        validateExcelProgressPanel.setBorder(null);
        validateExcelProgressPanel.setFocusable(false);

        loadingDataPanel.setBackground(new java.awt.Color(70, 130, 180));
        loadingDataPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        loadingDataPanel.setAlignmentX(0.0F);
        loadingDataPanel.setAlignmentY(0.0F);
        loadingDataPanel.setInheritsPopupMenu(true);

        loadingDataLabel.setForeground(new java.awt.Color(255, 255, 255));
        loadingDataLabel.setText("Loading Data");
        loadingDataLabel.setAlignmentY(0.0F);
        loadingDataLabel.setMaximumSize(new java.awt.Dimension(70, 55));
        loadingDataLabel.setMinimumSize(new java.awt.Dimension(70, 44));
        loadingDataLabel.setPreferredSize(new java.awt.Dimension(70, 20));
        loadingDataLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        loadingDataProgressBar.setForeground(new java.awt.Color(102, 204, 0));
        loadingDataProgressBar.setAlignmentX(100.0F);
        loadingDataProgressBar.setAlignmentY(100.0F);
        loadingDataProgressBar.setPreferredSize(new java.awt.Dimension(12, 12));

        javax.swing.GroupLayout loadingDataPanelLayout = new javax.swing.GroupLayout(loadingDataPanel);
        loadingDataPanel.setLayout(loadingDataPanelLayout);
        loadingDataPanelLayout.setHorizontalGroup(
            loadingDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loadingDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loadingDataLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadingDataProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        loadingDataPanelLayout.setVerticalGroup(
            loadingDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, loadingDataPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(loadingDataProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(loadingDataLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, Short.MAX_VALUE)
        );

        validatingExcelPanel.setBackground(new java.awt.Color(70, 130, 180));
        validatingExcelPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        validatingExcelPanel.setAlignmentX(0.0F);
        validatingExcelPanel.setAlignmentY(0.0F);
        validatingExcelPanel.setInheritsPopupMenu(true);

        ValidatingExcelLabel.setForeground(new java.awt.Color(255, 255, 255));
        ValidatingExcelLabel.setText("Validating Excel");
        ValidatingExcelLabel.setAlignmentY(0.0F);
        ValidatingExcelLabel.setMaximumSize(new java.awt.Dimension(70, 55));
        ValidatingExcelLabel.setMinimumSize(new java.awt.Dimension(70, 44));
        ValidatingExcelLabel.setPreferredSize(new java.awt.Dimension(70, 20));
        ValidatingExcelLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);

        validationProgressBar.setForeground(new java.awt.Color(102, 204, 0));
        validationProgressBar.setAlignmentX(100.0F);
        validationProgressBar.setAlignmentY(100.0F);
        validationProgressBar.setPreferredSize(new java.awt.Dimension(12, 12));

        javax.swing.GroupLayout validatingExcelPanelLayout = new javax.swing.GroupLayout(validatingExcelPanel);
        validatingExcelPanel.setLayout(validatingExcelPanelLayout);
        validatingExcelPanelLayout.setHorizontalGroup(
            validatingExcelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, validatingExcelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ValidatingExcelLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 105, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(validationProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 548, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        validatingExcelPanelLayout.setVerticalGroup(
            validatingExcelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(ValidatingExcelLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 36, Short.MAX_VALUE)
            .addGroup(validatingExcelPanelLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(validationProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        investigateButton.setText("INVESTIGATE");
        investigateButton.setEnabled(false);
        investigateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                investigateButtonActionPerformed(evt);
            }
        });

        loadDataButton.setText("LOAD DATA");
        loadDataButton.setEnabled(false);
        loadDataButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadDataButtonActionPerformed(evt);
            }
        });

        exitButton.setText("EXIT");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout validateExcelProgressPanelLayout = new javax.swing.GroupLayout(validateExcelProgressPanel);
        validateExcelProgressPanel.setLayout(validateExcelProgressPanelLayout);
        validateExcelProgressPanelLayout.setHorizontalGroup(
            validateExcelProgressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validateExcelProgressPanelLayout.createSequentialGroup()
                .addGroup(validateExcelProgressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(validateExcelProgressPanelLayout.createSequentialGroup()
                        .addGap(404, 404, 404)
                        .addComponent(loadDataButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 20, Short.MAX_VALUE)
                        .addComponent(investigateButton)
                        .addGap(18, 18, 18)
                        .addComponent(exitButton, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(validatingExcelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(loadingDataPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        validateExcelProgressPanelLayout.setVerticalGroup(
            validateExcelProgressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, validateExcelProgressPanelLayout.createSequentialGroup()
                .addComponent(validatingExcelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loadingDataPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addGroup(validateExcelProgressPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loadDataButton)
                    .addComponent(exitButton)
                    .addComponent(investigateButton))
                .addContainerGap())
        );

        errorTablePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        errorMsgsTable.setForeground(new java.awt.Color(255, 0, 0));
        errorMsgsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "ERROR TYPE", "ERROR MESSAGE"
            }
        ));
        jScrollPane1.setViewportView(errorMsgsTable);
        errorMsgsTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        errorMsgsTable.getColumnModel().getColumn(0).setMaxWidth(200);

        javax.swing.GroupLayout errorTablePanelLayout = new javax.swing.GroupLayout(errorTablePanel);
        errorTablePanel.setLayout(errorTablePanelLayout);
        errorTablePanelLayout.setHorizontalGroup(
            errorTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(errorTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 662, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
        errorTablePanelLayout.setVerticalGroup(
            errorTablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(errorTablePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                .addContainerGap())
        );

        importFilePanel.setBackground(new java.awt.Color(25, 25, 112));
        importFilePanel.setBorder(null);
        importFilePanel.setForeground(new java.awt.Color(25, 25, 112));

        projectComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectComboBoxActionPerformed(evt);
            }
        });

        excelReportPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 153, 204), 2));

        browseReportButton.setText("...");
        browseReportButton.setMargin(new java.awt.Insets(1, 1, 2, 2));
        browseReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseReportButtonActionPerformed(evt);
            }
        });

        excelReportTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excelReportTextFieldActionPerformed(evt);
            }
        });
        excelReportTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                excelReportTextFieldKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                excelReportTextFieldKeyReleased(evt);
            }
        });

        excelReportLabel.setText("Excel Report");

        javax.swing.GroupLayout excelReportPanel1Layout = new javax.swing.GroupLayout(excelReportPanel1);
        excelReportPanel1.setLayout(excelReportPanel1Layout);
        excelReportPanel1Layout.setHorizontalGroup(
            excelReportPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(excelReportPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(excelReportPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(excelReportPanel1Layout.createSequentialGroup()
                        .addComponent(excelReportTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(browseReportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(excelReportLabel))
                .addContainerGap())
        );
        excelReportPanel1Layout.setVerticalGroup(
            excelReportPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, excelReportPanel1Layout.createSequentialGroup()
                .addComponent(excelReportLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 13, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(excelReportPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(excelReportTextField)
                    .addComponent(browseReportButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, Short.MAX_VALUE))
                .addContainerGap())
        );

        projectLabel.setBackground(new java.awt.Color(255, 255, 255));
        projectLabel.setFont(new java.awt.Font("Dialog", 1, 12));
        projectLabel.setForeground(new java.awt.Color(255, 255, 255));
        projectLabel.setText("Project");

        spiLogoLabel.setIcon(new javax.swing.ImageIcon("/home/sunil/Desktop/workspace/spi/client/images/spica.jpg")); // NOI18N
        spiLogoLabel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.white,1));

        javax.swing.GroupLayout importFilePanelLayout = new javax.swing.GroupLayout(importFilePanel);
        importFilePanel.setLayout(importFilePanelLayout);
        importFilePanelLayout.setHorizontalGroup(
            importFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(importFilePanelLayout.createSequentialGroup()
                .addComponent(spiLogoLabel)
                .addGroup(importFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(importFilePanelLayout.createSequentialGroup()
                        .addGap(357, 357, 357)
                        .addComponent(projectLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(projectComboBox, 0, 160, Short.MAX_VALUE))
                    .addGroup(importFilePanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(excelReportPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        importFilePanelLayout.setVerticalGroup(
            importFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, importFilePanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(importFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(spiLogoLabel)
                    .addGroup(importFilePanelLayout.createSequentialGroup()
                        .addComponent(excelReportPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(importFilePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(projectLabel)
                            .addComponent(projectComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(31, 31, 31))
        );

        javax.swing.GroupLayout validateExcelPanelLayout = new javax.swing.GroupLayout(validateExcelPanel);
        validateExcelPanel.setLayout(validateExcelPanelLayout);
        validateExcelPanelLayout.setHorizontalGroup(
            validateExcelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, validateExcelPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(validateExcelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(validateExcelPanelLayout.createSequentialGroup()
                        .addComponent(errorTablePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, validateExcelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(importFilePanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(validateExcelProgressPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        validateExcelPanelLayout.setVerticalGroup(
            validateExcelPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(validateExcelPanelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(importFilePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(validateExcelProgressPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(errorTablePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(validateExcelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(validateExcelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void excelReportTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excelReportTextFieldActionPerformed
}//GEN-LAST:event_excelReportTextFieldActionPerformed

    private void loadDataButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadDataButtonActionPerformed
        try {
            clearTable();
            String filePath = excelReportTextField.getText();
            setXlsFilePath(filePath);
            readFile();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(InvestigationToolForm.class.getName()).log(Level.SEVERE, null, ex);
        }

}//GEN-LAST:event_loadDataButtonActionPerformed

    private void investigateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_investigateButtonActionPerformed

        if (projectComboBox.getSelectedIndex() > -1) {
            int sel = projectComboBox.getSelectedIndex();
            projectId = ((SQLManagedComboModel) projectComboBox.getModel()).getIdAt(sel);
        }
        //keep this code inside the above if condition
        this.setVisible(false);
        new FeedbackInvestigation(this, feedbackList, projectId).setVisible(true);

}//GEN-LAST:event_investigateButtonActionPerformed

    private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed
        parent.setVisible(true);
        this.setVisible(false);
}//GEN-LAST:event_exitButtonActionPerformed

    private void browseReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseReportButtonActionPerformed

        FileFilter filter = new FileFilter() {
            public String getDescription() {
                return "XLS Files (*.xls)";
            }

            public boolean accept(File file) {             
                if (file.isDirectory()) {
                    return true;

                } else if (file.isFile()) {
                    if (file.getName().endsWith("xls")) {
                        return true;
                    }
                }
                return false;
            }
        };
        chooseFile = new JFileChooser();
        chooseFile.setFileFilter((javax.swing.filechooser.FileFilter) filter);
        int returnVal = chooseFile.showOpenDialog(validateExcelProgressPanel);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooseFile.getSelectedFile();
            excelReportTextField.setText(file.getPath());
        }
        enableLoadButton();
}//GEN-LAST:event_browseReportButtonActionPerformed

    private void enableLoadButton() {
        if (projectComboBox.getSelectedIndex() > -1 && (excelReportTextField.getText() != null) && (!excelReportTextField.getText().isEmpty())) {
            loadDataButton.setEnabled(true);
        } else {
            loadDataButton.setEnabled(false);
        }
    }
    private void projectComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectComboBoxActionPerformed
        enableLoadButton();
    }//GEN-LAST:event_projectComboBoxActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.setVisible(false);
        parent.setVisible(true);
    }//GEN-LAST:event_formWindowClosing

private void excelReportTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_excelReportTextFieldKeyPressed
    enableLoadButton();
}//GEN-LAST:event_excelReportTextFieldKeyPressed

private void excelReportTextFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_excelReportTextFieldKeyReleased
    enableLoadButton();
}//GEN-LAST:event_excelReportTextFieldKeyReleased
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {                
                new InvestigationToolForm().setVisible(true);
            }
        });
    }

    /**
     * get the path for xls file
     * @return file path for xls file
     */
    public String getXlsFilePath() {
        return xlsFilePath;
    }

    /**
     * set the file path for the xls file
     * @param xlsFilePath - path for xls file
     */
    public void setXlsFilePath(String xlsFilePath) {       
        this.xlsFilePath = xlsFilePath;
    }
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel ValidatingExcelLabel;
    private javax.swing.JButton browseReportButton;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JTable errorMsgsTable;
    private javax.swing.JPanel errorTablePanel;
    private javax.swing.JLabel excelReportLabel;
    private javax.swing.JPanel excelReportPanel1;
    private javax.swing.JTextField excelReportTextField;
    private javax.swing.JButton exitButton;
    private javax.swing.JPanel importFilePanel;
    private javax.swing.JButton investigateButton;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollBar jScrollBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton loadDataButton;
    private javax.swing.JLabel loadingDataLabel;
    private javax.swing.JPanel loadingDataPanel;
    private javax.swing.JProgressBar loadingDataProgressBar;
    private javax.swing.JComboBox projectComboBox;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JLabel spiLogoLabel;
    private javax.swing.JPanel validateExcelPanel;
    private javax.swing.JPanel validateExcelProgressPanel;
    private javax.swing.JPanel validatingExcelPanel;
    private javax.swing.JProgressBar validationProgressBar;
    // End of variables declaration//GEN-END:variables
    private JFileChooser chooseFile;

    public int getMultipleTallySize() {
        return multipleTallySize;
    }

    public void setMultipleTallySize(int multipleTallySize) {
        this.multipleTallySize = multipleTallySize;
    }
}
