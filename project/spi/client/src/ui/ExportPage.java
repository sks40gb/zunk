/* $Header: /home/common/cvsarea/ibase/dia/src/ui/ExportPage.java,v 1.38.2.12 2006/08/23 19:04:52 nancy Exp $ */
/*
 * ExportPage.java
 *
 * Created on August 25, 2004, 10:15 AM
 */
package ui;

import beans.ExampleFileFilter;
import beans.LTextField;
import client.ClientTask;
import client.Global;
import client.ServerConnection;
import client.TaskExecuteQuery;
import client.TaskExecuteUpdate;
import client.TaskExportData;
import client.TaskQueueId;
import common.DelimiterData;
import common.ExportData;
import common.Log;
import common.msg.MessageConstants;
import dbload.XrefConstants;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.ManagedTableFilter;
import model.ManagedComboModel;
import model.ManagedTableSorter;
import model.QueryComboModel;
import model.SQLManagedTableModel;
import model.TableRow;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.w3c.dom.Element;

import ui.OutputFormat;


/**
 * The export screen on the AdminFrame allows users to export the cross
 * reference file and coded data.  The default is to export only those batches
 * with a status of CodingQC or greater, but the user may select the checkbox
 * to allow export of non-QCed batches.  The user selects a project and volume
 * and may select specific batches for export.  The default cross reference
 * format is LFP, although the user may select one of DOCULEX, OPTICON,
 * SUMMATION and BRS.  The user may select to export only the cross reference.
 * If data is being exported, the fields will be in the order of the fields
 * listed on the ProjectPage.  The delimiters selected at the bottom of the
 * screen are used in the output.  The Output File, Cross Reference File and
 * Log File are all written to the client computer.
 * @author  Nancy McCall
 * @see common.DelimiterData
 * @see common.ExportData
 * @see common.ProjectFieldsData
 * @see DelimiterPanel
 * @see export.DiaExportFromGui
 */
public class ExportPage extends AbstractPage implements XrefConstants,MessageConstants {

    private final String GET_ALL_VOLUMES = "Import Export.get all volumes";
    private final String GET_QCED_VOLUMES = "ExportPage.get qced volumes";
    private final String GET_ALL_BATCHES = "ExportPage.get all batches";
    private final String GET_QCED_BATCHES = "ExportPage.get qced batches";
    private DelimiterPanel delimiters = null;
    private QueryComboModel volumeModel = null;
    private QueryComboModel batchModel = null;
    private DefaultComboBoxModel batchEndModel = null;
    private ManagedTableSorter pModel = null;
    private ManagedComboModel projectModel = null;
    private ManagedComboModel projectModelFilter = null;
    ManagedTableFilter projectFilter = null;
    private int projectId = 0;
    private int volumeId = 0;
    private int batchId = 0;
    private int batchEndId = 0;   
    private JFileChooser chooser;
    private FileFilter txtFilter;
    private FileFilter logFilter;
    private FileFilter lfpFilter;
    private FileFilter diiFilter;
    private FileFilter optFilter;
    private FileFilter dbfFilter;
    private FileFilter brsFilter;  
    private static ExportData data;        
    private boolean isTSearchExport = false;        
    private String pathFile[] = new String[3];
    private String fileName[] = new String[3];    
    private int task_type = 0;
    private int serverQueueId = 0;
    private int userId =0;
    private final int OUTPUT_VALIDATION = 2;
    private final int EXPORT = 1;
    /**
     * Creates new form ExportPage.
     * @param frame the frame in which to place this screen
     */
     public ExportPage() {
     }
    public ExportPage(AdminFrame frame) {
        super(frame);
        initComponents();

        txtFilter = makeFilter("txt", "Exported Data File");
        logFilter = makeFilter("log", "Export Log File");
        lfpFilter = makeFilter("lfp", "LFP Cross Reference File");
        diiFilter = makeFilter("dii", "SUMMATION Cross Reference File");
        optFilter = makeFilter("opt", "OPTICON Cross Reference File");
        dbfFilter = makeFilter("dbf", "DOCULEX Cross Reference File");
        brsFilter = makeFilter("br2", "BRS Exported Data File");

        csvTextField.setText("");
        lfpTextField.setText("");
    }

    private FileFilter makeFilter(String extension, String description) {
        // Note: source for ExampleFileFilter can be found in FileChooserDemo,
        // under the demo/jfc directory in the Java 2 SDK, Standard Edition.
        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension(extension);
        filter.setDescription(description);
        return filter;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        exitMenuItem = new javax.swing.JMenuItem();
        xrefFormatGroup = new javax.swing.ButtonGroup();
        titleLabel = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        projectCombo = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        qcCheckBox = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        volumeCombo = new javax.swing.JComboBox();
        jLabel16 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        batchCombo = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        batchEndCombo = new javax.swing.JComboBox();
        xrefTypePanel = new javax.swing.JPanel();
        doculexButton = new javax.swing.JRadioButton();
        lfpButton = new javax.swing.JRadioButton();
        opticonButton = new javax.swing.JRadioButton();
        summationButton = new javax.swing.JRadioButton();
        brsButton = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        unitizationCheckBox = new javax.swing.JCheckBox();
        nullAttachmentsCheckBox = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        dataPanel = new javax.swing.JPanel();
	exportButtonsPanel = new JPanel();
        csvTextField = new LTextField() {
            /**
            * Returns the preferred size <code>Dimensions</code> needed for this
            * <code>TextField</code>.  If a non-zero number of columns has been
            * set, the width is set to the columns multiplied by
            * the column width.
            *
            * @return the dimension of this textfield
            */
            public Dimension getPreferredSize() {
                return getFilenameSize();
            }
        };

        csvButton = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        lfpPanel = new javax.swing.JPanel();
        lfpTextField = new LTextField() {
            /**
            * Returns the preferred size <code>Dimensions</code> needed for this
            * <code>TextField</code>.  If a non-zero number of columns has been
            * set, the width is set to the columns multiplied by
            * the column width.
            *
            * @return the dimension of this textfield
            */
            public Dimension getPreferredSize() {
                return getFilenameSize();
            }
        };

        xrefButton = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        logPanel = new javax.swing.JPanel();
        logTextField = new LTextField() {
            /**
            * Returns the preferred size <code>Dimensions</code> needed for this
            * <code>TextField</code>.  If a non-zero number of columns has been
            * set, the width is set to the columns multiplied by
            * the column width.
            *
            * @return the dimension of this textfield
            */
            public Dimension getPreferredSize() {
                return getFilenameSize();
            }
        };

        logButton = new javax.swing.JButton();
        delimiterPanel = new javax.swing.JPanel();
        exportButton = new javax.swing.JButton();
        outpurFormatButton = new javax.swing.JButton();
	tSearchExportButton = new javax.swing.JButton();
	validationHistoryButton = new javax.swing.JButton();
	exportHistoryButton = new javax.swing.JButton();

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");
        exitMenuItem.setMnemonic('E');
        exitMenuItem.setText("Exit");
        exitMenuItem.setToolTipText("Exit program.");
		exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        setLayout(new java.awt.GridBagLayout());

        titleLabel.setFont(new java.awt.Font("Dialog", 1, 24));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Export");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        add(titleLabel, gridBagConstraints);

        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel10.setLayout(new java.awt.GridBagLayout());

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 11));
        jLabel13.setText("Project: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel10.add(jLabel13, gridBagConstraints);

        projectCombo.setPreferredSize(new java.awt.Dimension(175, 25));
        projectCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectComboActionPerformed(evt);
            }
        });

        jPanel13.add(projectCombo);

        jSeparator1.setPreferredSize(new java.awt.Dimension(30, 0));
        jPanel13.add(jSeparator1);

        qcCheckBox.setFont(new java.awt.Font("Dialog", 0, 11));
        qcCheckBox.setText("Allow non-QCed Batches");
        qcCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qcCheckBoxActionPerformed(evt);
            }
        });

        jPanel13.add(qcCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel10.add(jPanel13, gridBagConstraints);

        jLabel15.setFont(new java.awt.Font("Dialog", 0, 11));
        jLabel15.setText("Volume: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel10.add(jLabel15, gridBagConstraints);

        volumeCombo.setPreferredSize(new java.awt.Dimension(175, 25));
        volumeCombo.setEnabled(false);
        volumeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                volumeComboActionPerformed(evt);
            }
        });
        volumeCombo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                volumeComboPopupMenuWillBecomeVisible(evt);
            }
        });

        jPanel12.add(volumeCombo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel10.add(jPanel12, gridBagConstraints);

        jLabel16.setFont(new java.awt.Font("Dialog", 0, 11));
        jLabel16.setText("Batch: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel10.add(jLabel16, gridBagConstraints);

        batchCombo.setPreferredSize(new java.awt.Dimension(95, 25));
        batchCombo.setEnabled(false);
        batchCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batchComboActionPerformed(evt);
            }
        });
        batchCombo.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
                batchComboPopupMenuWillBecomeVisible(evt);
            }
        });

        jPanel11.add(batchCombo);

        jLabel17.setFont(new java.awt.Font("Dialog", 0, 11));
        jLabel17.setText(" End Batch: ");
        jPanel11.add(jLabel17);

        batchEndCombo.setPreferredSize(new java.awt.Dimension(95, 25));
        batchEndCombo.setEnabled(false);
        batchEndCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batchEndComboActionPerformed(evt);
            }
        });

        jPanel11.add(batchEndCombo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel10.add(jPanel11, gridBagConstraints);

        xrefTypePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        doculexButton.setText("DOCULEX");
        doculexButton.setToolTipText("DOCULEX .dbf file");
        xrefFormatGroup.add(doculexButton);
        xrefTypePanel.add(doculexButton);

        lfpButton.setSelected(true);
        lfpButton.setText("LFP");
        lfpButton.setToolTipText("LFP (.lfp) file");
        xrefFormatGroup.add(lfpButton);
        xrefTypePanel.add(lfpButton);

        opticonButton.setText("OPTICON");
        opticonButton.setToolTipText("OPTICON .opt file");
        xrefFormatGroup.add(opticonButton);
        xrefTypePanel.add(opticonButton);

        summationButton.setText("SUMMATION");
        summationButton.setToolTipText("SUMMATION .dii file");
        xrefFormatGroup.add(summationButton);
        xrefTypePanel.add(summationButton);

        brsButton.setText("BRS");
        brsButton.setToolTipText("BRS file");
        xrefFormatGroup.add(brsButton);
        xrefTypePanel.add(brsButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel10.add(xrefTypePanel, gridBagConstraints);

        jSeparator2.setPreferredSize(new java.awt.Dimension(30, 0));
        jPanel1.add(jSeparator2);

        unitizationCheckBox.setText("Unitization Export");
        unitizationCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                unitizationCheckBoxActionPerformed(evt);
            }
        });

        jPanel1.add(unitizationCheckBox);

        nullAttachmentsCheckBox.setText("Null Attachments");
        nullAttachmentsCheckBox.setEnabled(false);
        jPanel1.add(nullAttachmentsCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel10.add(jPanel1, gridBagConstraints);

        jLabel12.setFont(new java.awt.Font("Dialog", 0, 11));
        jLabel12.setText("Output Data: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
       // jPanel10.add(jLabel12, gridBagConstraints);

        csvTextField.setColumns(180);
        csvTextField.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        csvTextField.setMaximumSize(new java.awt.Dimension(556, 20));
        dataPanel.add(csvTextField);

        csvButton.setFont(new java.awt.Font("Dialog", 1, 10));
        csvButton.setIcon(new javax.swing.ImageIcon(""));
        csvButton.setText("Browse ...");
        csvButton.setToolTipText("");
        csvButton.setFocusable(false);
        csvButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        csvButton.setIconTextGap(0);
        csvButton.setPreferredSize(new java.awt.Dimension(84, 27));
        csvButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        csvButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                csvButtonActionPerformed(evt);
            }
        });

        dataPanel.add(csvButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
       // jPanel10.add(dataPanel, gridBagConstraints);

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 11));
        jLabel14.setText("Cross Ref. File: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        //jPanel10.add(jLabel14, gridBagConstraints);

        lfpTextField.setColumns(180);
        lfpTextField.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        lfpTextField.setMaximumSize(new java.awt.Dimension(556, 20));
        lfpPanel.add(lfpTextField);

        xrefButton.setFont(new java.awt.Font("Dialog", 1, 10));
        xrefButton.setIcon(new javax.swing.ImageIcon(""));
        xrefButton.setText("Browse ...");
        xrefButton.setToolTipText("");
        xrefButton.setFocusable(false);
        xrefButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        xrefButton.setMinimumSize(new java.awt.Dimension(70, 27));
        xrefButton.setPreferredSize(new java.awt.Dimension(84, 27));
        xrefButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        xrefButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                xrefButtonActionPerformed(evt);
            }
        });

        lfpPanel.add(xrefButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        //jPanel10.add(lfpPanel, gridBagConstraints);

        jLabel18.setFont(new java.awt.Font("Dialog", 0, 11));
        jLabel18.setText("Log File: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        //jPanel10.add(jLabel18, gridBagConstraints);

        logTextField.setColumns(180);
        logTextField.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        logTextField.setMaximumSize(new java.awt.Dimension(556, 20));
        logPanel.add(logTextField);

        logButton.setFont(new java.awt.Font("Dialog", 1, 10));
        logButton.setIcon(new javax.swing.ImageIcon(""));
        logButton.setText("Browse ...");
        logButton.setToolTipText("");
        logButton.setFocusable(false);
        logButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        logButton.setMinimumSize(new java.awt.Dimension(70, 27));
        logButton.setPreferredSize(new java.awt.Dimension(84, 27));
        logButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        logButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logButtonActionPerformed(evt);
            }
        });

        logPanel.add(logButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        //jPanel10.add(logPanel, gridBagConstraints);

        jPanel6.add(jPanel10, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        add(jPanel6, gridBagConstraints);

        delimiterPanel.setLayout(new java.awt.BorderLayout());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        add(delimiterPanel, gridBagConstraints);		

        exportButton.setFont(new java.awt.Font("Dialog", 1, 14));
        exportButton.setText("Export");
        exportButton.setToolTipText("");
        exportButton.setFocusable(false);
        exportButton.setEnabled(false);	
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        
        outpurFormatButton.setFont(new java.awt.Font("Dialog", 1, 14));
        outpurFormatButton.setText("Output Format");
        outpurFormatButton.setToolTipText("");
        outpurFormatButton.setFocusable(false);
        outpurFormatButton.setEnabled(false);	
        outpurFormatButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputFormatButtonActionPerformed(evt);
            }
        });

	exportButtonsPanel.add(exportButton);
	exportButtonsPanel.add(outpurFormatButton);		
	
	tSearchExportButton.setFont(new java.awt.Font("Dialog", 1, 14));
        tSearchExportButton.setText("TSearchExport");
        tSearchExportButton.setToolTipText("");
        tSearchExportButton.setFocusable(false);
        tSearchExportButton.setEnabled(false);
        tSearchExportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tSearchExportButtonActionPerformed(evt);
            }
        });

		exportButtonsPanel.add(tSearchExportButton);

	
	    validationHistoryButton.setFont(new java.awt.Font("Dialog", 1, 14));
        validationHistoryButton.setText("Output Validations History");
        validationHistoryButton.setToolTipText("");
        validationHistoryButton.setFocusable(false);
        validationHistoryButton.setEnabled(true);
        validationHistoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validationsHistoryButtonActionPerformed(evt);
            }
        });

		exportButtonsPanel.add(validationHistoryButton);

		exportHistoryButton.setFont(new java.awt.Font("Dialog", 1, 14));
        exportHistoryButton.setText("Export History");
        exportHistoryButton.setToolTipText("");
        exportHistoryButton.setFocusable(false);
        exportHistoryButton.setEnabled(true);
        exportHistoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportHistoryButtonActionPerformed(evt);
            }
        });

		exportButtonsPanel.add(exportHistoryButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 20, 0);
        add(exportButtonsPanel, gridBagConstraints);

    }//GEN-END:initComponents

    /**
     * Check whether unitization is selected or not.
     * @param evt
     */
    private void unitizationCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_unitizationCheckBoxActionPerformed
        try {
            if (unitizationCheckBox.isSelected()) {
                nullAttachmentsCheckBox.setEnabled(true);
            } else {
                nullAttachmentsCheckBox.setEnabled(false);
            }
        } catch (Throwable th) {
            Log.quit(th);
        }
    }//GEN-LAST:event_unitizationCheckBoxActionPerformed

    private void logButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logButtonActionPerformed
        chooser = new JFileChooser();
        chooser.setDialogTitle("Log Filename");
        chooser.addChoosableFileFilter(logFilter);
        chooser.setFileFilter(logFilter);
        getFilename(logTextField, "log");
        checkEnableExportButton();
    }//GEN-LAST:event_logButtonActionPerformed

    private void csvButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_csvButtonActionPerformed
        chooser = new JFileChooser();
        chooser.setDialogTitle("Output Data Filename");
        chooser.addChoosableFileFilter(txtFilter);
        chooser.setFileFilter(txtFilter);

        DelimiterData del = delimiters.getDelimiterData(true);
        //Log.print("(ExportPage.csvButton) " + del.brs_format);
        if (brsButton.isSelected() || del.brs_format.equals("Yes")) {
            chooser.addChoosableFileFilter(brsFilter);
            chooser.setFileFilter(brsFilter);
            getFilename(csvTextField, "br2");
        } else {
            chooser.addChoosableFileFilter(txtFilter);
            chooser.setFileFilter(txtFilter);
            getFilename(csvTextField, "txt");
        }

        checkEnableExportButton();
    }//GEN-LAST:event_csvButtonActionPerformed

    private void xrefButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_xrefButtonActionPerformed
        String ext = "";
        chooser = new JFileChooser();
        chooser.setDialogTitle("Cross Reference Filename");
        chooser.addChoosableFileFilter(dbfFilter);
        chooser.addChoosableFileFilter(lfpFilter);
        chooser.addChoosableFileFilter(optFilter);
        chooser.addChoosableFileFilter(diiFilter);
        if (doculexButton.isSelected()) {
            chooser.setFileFilter(dbfFilter);
            ext = "dbf";
        } else if (opticonButton.isSelected()) {
            chooser.setFileFilter(optFilter);
            ext = "opt";
        } else if (summationButton.isSelected()) {
            chooser.setFileFilter(diiFilter);
            ext = "dii";
        } else if (brsButton.isSelected()) {
            chooser.setFileFilter(txtFilter);
            ext = "txt";
        } else {
            chooser.setFileFilter(lfpFilter);
            ext = "lfp";
        }
        getFilename(lfpTextField, ext);
        checkEnableExportButton();
    }//GEN-LAST:event_xrefButtonActionPerformed

    /**
     * Get a filename from a JFileChooser for the given field and fill in
     * the default filenames of the other filename fields if they are blank.
     * @param field - the LTextField associated with the browse button that the user clicked
     * @see lfpButtonActionPerformed, csvButtonActionPerformed, logButtonActionPerformed
     */
    private void getFilename(LTextField field, String ext) {
        chooser.setSelectedFile(new File(field.getText()));
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnVal = chooser.showSaveDialog(csvButton);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            // see if the filename needs the extension appended
            String fNoExt = file.getPath();
            String f = "";
            if (fNoExt.indexOf(".") < 0) {
                f = fNoExt + "." + ext;
                file = new File(f);
            } else {
                fNoExt = fNoExt.substring(0, fNoExt.indexOf("."));
            }
            if (file.exists()) {
                // see if the user wants to overwrite the file
                if (existsDialog(file.getPath()) == JOptionPane.NO_OPTION) {
                    return;
                }
            }
            Log.print("(ExportPage.getFilename) Saving: " + file.getPath());
            // fill in the default filenames
            DelimiterData del = delimiters.getDelimiterData(true);
            if (brsButton.isSelected() || del.brs_format.equals("Yes")) {
                csvTextField.setText(fNoExt + ".br2");
            } else {
                csvTextField.setText(fNoExt + ".txt");
            }
            logTextField.setText(fNoExt + ".log");
            if (doculexButton.isSelected()) {
                lfpTextField.setText(fNoExt + ".dbf");
            } else if (opticonButton.isSelected()) {
                lfpTextField.setText(fNoExt + ".opt");
            } else if (summationButton.isSelected()) {
                lfpTextField.setText(fNoExt + ".dii");
            } else if (brsButton.isSelected()) {
                lfpTextField.setText(fNoExt + ".txt");
            } else {
                lfpTextField.setText(fNoExt + ".lfp");
            }
        } else {
            Log.print("(ExportPage.csvButton) Cancelled");
        }
    // TBD: Clear chooser here????
    }

    /**
     * See if one of the filename fields contains data and, if it does,
     * use the filename to create default filename for the given extension.
     * @param ext - the default extension for the filename
     * @return a string containing the filename with ext appended
     */
    private String getLikeFilename(String ext) {
        String name = "";
        int i;
        if (csvTextField.getText().length() > 0) {
            i = csvTextField.getText().indexOf(".");
            if (i > 0) {
                name = csvTextField.getText().substring(0, i);
            } else {
                name = csvTextField.getText();
            }
        } else if (lfpTextField.getText().length() > 0) {
            i = lfpTextField.getText().indexOf(".");
            if (i > 0) {
                name = lfpTextField.getText().substring(0, i);
            } else {
                name = lfpTextField.getText();
            }
        } else if (logTextField.getText().length() > 0) {
            i = logTextField.getText().indexOf(".");
            if (i > 0) {
                name = logTextField.getText().substring(0, i);
            } else {
                name = logTextField.getText();
            }
        }
        if (name.length() > 0) {
            name = name + "." + ext;
        }
        return name;
    }

    private int existsDialog(String msg) {
        Object[] options = {"Yes",
            "No"
        };
        return JOptionPane.showOptionDialog(this,
                "File " + msg + " exists." + "\n\nOverwrite?",
                "Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                options,
                options[0]);
    }

    private void volumeComboPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_volumeComboPopupMenuWillBecomeVisible
    }//GEN-LAST:event_volumeComboPopupMenuWillBecomeVisible

    private void batchComboPopupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_batchComboPopupMenuWillBecomeVisible
        if (qcCheckBox.isSelected()) {
            batchModel = new QueryComboModel(GET_ALL_BATCHES, /* required? */ false, new String[]{Integer.toString(volumeId)}, /* default value */ "") {

                public void getDataEntry(ResultSet queryResult) {
                    super.getDataEntry(queryResult);
                    loadBatchEndModel();
                }
            };
        } else {
            batchModel = new QueryComboModel(GET_QCED_BATCHES, /* required? */ false, new String[]{Integer.toString(volumeId)}, /* default value */ "") {

                /**
                 * Override to load only the qc'ed and higher volumes
                 * Load the combo from the resultSet.
                 * @param queryResult - ResultSet returned by ClientTask
                 */
                public void getDataEntry(ResultSet queryResult) {
                    //try {
                    //    this.resultSet = queryResult;
                    //    // add blank so user is not forced to select a value
                    //    ids.add("-1");
                    //    addElement("");
                    //    // load data
                    //    while (resultSet.next()) {
                    //        ids.add(resultSet.getString(1));
                    //        if (resultSet.getInt(3) >= 7) {
                    //            // status > Coded
                    //            addElement(resultSet.getString(2));
                    //        }
                    //    }
                    super.getDataEntry(queryResult);
                    loadBatchEndModel();
                //} catch (SQLException e) {
                //    Log.quit(e);
                //}
                }
            };
        }
        batchCombo.setModel(batchModel);
    }//GEN-LAST:event_batchComboPopupMenuWillBecomeVisible

    private void loadBatchEndModel() {
        Object[] objects = new Object[batchModel.ids.size()];
        for (int i = 0; i < objects.length; i++) {
            objects[i] = batchModel.getElementAt(i);
        }
        batchEndModel = new DefaultComboBoxModel(objects);
        batchEndCombo.setModel(batchEndModel);
    }

    private void projectComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectComboActionPerformed
        int sel = projectCombo.getSelectedIndex();
        if (sel > -1) {
            projectId = ((ManagedComboModel) projectCombo.getModel()).getIdAt(sel);
            setVolumes();
            volumeCombo.setEnabled(true);
        } else {
            projectId = 0;
            volumeCombo.setEnabled(false);
            batchCombo.setEnabled(false);
            batchEndCombo.setEnabled(false);
        }
        checkEnableExportButton();
    }//GEN-LAST:event_projectComboActionPerformed
    
    private void outputFormatButtonActionPerformed(java.awt.event.ActionEvent evt) {                                             
       OutputFormat outputFormat = new OutputFormat(this, volumeId);
       outputFormat.setVisible(true);
    }                                            

    private void setVolumes() {
        if (qcCheckBox.isSelected()) {
            volumeModel = new QueryComboModel(GET_ALL_VOLUMES, false, new String[]{Integer.toString(projectId)}, "");
        } else {
            volumeModel = new QueryComboModel(GET_QCED_VOLUMES, false, new String[]{Integer.toString(projectId)}, ""); // {

        }
        volumeCombo.setModel(volumeModel);
    }

    private void batchEndComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batchEndComboActionPerformed
        int sel = batchCombo.getSelectedIndex();
        if (sel > -1) {
            // NOTE:  the batchModel and batchEndModel share id's, but the
            // batchModel is a QueryComboModel and has the ids's.
            batchEndId = batchModel.getIdAt(sel);
        }
    }//GEN-LAST:event_batchEndComboActionPerformed

    private void batchComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batchComboActionPerformed
        int sel = batchCombo.getSelectedIndex();
        if (sel > -1) {
            batchId = batchModel.getSelectedId();
            if (batchId == 0) {
                // all batches
                batchEndCombo.setEnabled(false);
            } else {
                batchEndCombo.setEnabled(true);
            }
        }
    }//GEN-LAST:event_batchComboActionPerformed

    private void volumeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_volumeComboActionPerformed
        volumeId = volumeModel.getSelectedId();
        if (volumeId > -1) {
            batchCombo.setEnabled(true);
            batchEndCombo.setEnabled(true);
        } else {
            batchCombo.setEnabled(false);
            batchEndCombo.setEnabled(false);
        }
        checkEnableExportButton();
    }//GEN-LAST:event_volumeComboActionPerformed

    private void qcCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qcCheckBoxActionPerformed
        setModels();
        if (volumeModel != null) {
            volumeCombo.setSelectedIndex(-1);
        }
        if (batchModel != null) {
            batchCombo.setSelectedIndex(-1);
            batchEndCombo.setSelectedIndex(-1);
        }
    }//GEN-LAST:event_qcCheckBoxActionPerformed

    /**
     * Start export operation.
     * @param evt
     */
    private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
        try {
            //exportButton.setEnabled(false); // until export finishes

            data = (ExportData) delimiters.getDelimiterData(true);
             getUserId();
            //Prompt user for validation
            if(isTSearchExport){
                JOptionPane.showMessageDialog(this, "Please refer to the ExportHistory button for getting the TSearchExport status.");
                data.doValidation = "false";
                data.isTSearchExport = "true";
                isTSearchExport = false;
            }else{
                    data.isTSearchExport = "false";
                    int input = JOptionPane.showConfirmDialog(this, "Do you want to run all the validations now ?", "DO_VALIDATION",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

                    if (input == 0) { //YES_OPTION

                        data.doValidation = "true";
                        JOptionPane.showMessageDialog(this, "Please refer to the OutputValidationsHistory  button for getting the validation status.");
                    } else if (input == 1) {   //NO_OPTION

                        data.doValidation = "false";
                        JOptionPane.showMessageDialog(this, "Please refer to the ExportHistory button for getting the export status.");
                    } else {   //CANCEL_OPTION
                        return;
                    }
            }

            // project_name
            int row = projectCombo.getSelectedIndex();
            if (row > -1) {
                data.project_name = (String) ((ManagedComboModel) projectCombo.getModel()).getElementAt(row);
            }
            // volume_name
            data.volume_id = volumeModel == null || volumeModel.getSelectedItem().equals("") ? 0 : volumeModel.getSelectedId();
            data.volume_name = volumeModel == null ? "" : (String) volumeModel.getSelectedItem();

            // batch_number    
            data.batch_number = batchModel == null || batchModel.getSelectedItem().equals("")
                    ? 0 : Integer.parseInt(
                    (String) batchModel.getSelectedItem());
            if (batchEndModel == null || batchEndModel.getSelectedItem().equals("")) {
                // no end batch selected
                if (data.batch_number == 0) {
                    // if no batch is selected, set the end batch to the max
                    data.end_batch_number = Integer.MAX_VALUE;
                } else {
                    // if one batch is selected, set the end batch to that batch number
                    data.end_batch_number = Integer.parseInt((String) batchModel.getSelectedItem());
                }
            } else {
                data.end_batch_number = Integer.parseInt((String) batchEndModel.getSelectedItem());
            }

            data.unitizeOnly = Boolean.toString(unitizationCheckBox.isSelected());
            data.isQCBatchAllowed = Boolean.toString(qcCheckBox.isSelected());
            data.isDoculex = Boolean.toString(doculexButton.isSelected());
            data.isOpticon = Boolean.toString(opticonButton.isSelected());
            data.isSummation = Boolean.toString(summationButton.isSelected());
            data.isBRS = Boolean.toString(brsButton.isSelected());
            //getTaskType("export");
                    Date date = new Date();
                    long time = date.getTime();
                    Timestamp timestamp = new Timestamp(time);
                    if(data.doValidation.equals("true")){
                     task_type = OUTPUT_VALIDATION;       //task type---> OVP             
                    }else if(data.doValidation.equals("false")){
                      task_type = EXPORT;       //task type---> export         
                    }
                     
                        //Create server task queue               
                    if(task_type > 0){                     
                      String parameter1[] = {Integer.toString(task_type),"InQueue",timestamp.toString(),Integer.toString(userId)};
                      final ClientTask task = new TaskExecuteUpdate("InsertIntoTaskQueue",parameter1); 
                      task.enqueue();
                      
                        final ClientTask getServerTaskQueueId = new TaskQueueId();    
                       // System.out.println("getServerQueueId=========="+ getServerQueueId());
                         getServerTaskQueueId.setCallback(new Runnable() {
                          public void run() {
                        Element element = (Element) getServerTaskQueueId.getResult();
                        String action = element.getNodeName();
                            if (T_SERVER_QUEUE_ID.equals(action)) {
                                
                                int server_queue_id = Integer.parseInt(element.getAttribute(A_SERVER_QUEUE_ID));
                                System.out.println("server_queue_id   " + server_queue_id);
                                saveExportData(server_queue_id);
                            }
                         }
                         });
                        getServerTaskQueueId.enqueue();
                       
                     // final ClientTask task_export_data = new TaskExecuteUpdate("InsertIntoExportData",parameter2); 
                     // task_export_data.enqueue();  
                    }
                  
               // final ClientTask taskExportData = new TaskExportData(data);
               // taskExportData.enqueue(this);

        } catch (Throwable th) {
            Log.quit(th);
        }
    }//GEN-LAST:event_exportButtonActionPerformed
    
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
    //Method which is used to save the export parameters
    private void saveExportData(int serverQueueId){
      
        String parameter2[] = {Integer.toString(serverQueueId),data.delimiter_set_name,
                       data.project_name,
                       Integer.toString(data.volume_id),
                       data.volume_name,
                       Integer.toString(data.batch_number),
                       Integer.toString(data.end_batch_number),
                       "",
                       "",
                       "",
                       data.isQCBatchAllowed,
                       data.isDoculex,
                       data.isOpticon,
                       data.isSummation,
                       data.isBRS,
                       data.doValidation,
                       data.isTSearchExport};
        
          final ClientTask task_export_data = new TaskExecuteUpdate("InsertIntoExportData",parameter2); 
                task_export_data.enqueue();  
        
        
    }
    
    
    private void getTaskType(String type){      
       
       String param = null;
       if(type.equals("export")){
       param = type;
       }else{
       param = type;
       }
         final ClientTask task_Type = new TaskExecuteQuery("getTaskType",param);
            task_Type.setCallback(new Runnable() {
                   
            public void run() {
                try {
                     
                    int taskType =0;                   
                    ResultSet rs = (ResultSet) task_Type.getResult();
                    taskType(rs);                    
                    
                } catch (Throwable th) {
                    Log.quit(th);
                }
            }
        });
         
        task_Type.enqueue();
        
                
    }
    
    
    private void taskType(ResultSet rs){
        try {
            rs.next();
            int taskType = rs.getInt(1);
            task_type = taskType;
            System.out.println("777777777777777777" + task_type);
        } catch (SQLException ex) {
            Logger.getLogger(ExportPage.class.getName()).log(Level.SEVERE, null, ex);
        }
         
    }
    /**
     * Start T Search.
     * @param evt
     */
    private void tSearchExportButtonActionPerformed(java.awt.event.ActionEvent evt) {
        isTSearchExport = true;
        exportButtonActionPerformed(evt);        
    }

    /**
     * See the validation report generated while exporting the volume.
     * @param evt
     */
    private void validationsHistoryButtonActionPerformed(java.awt.event.ActionEvent evt) {
        OutputValidationReportDialog dialog = new OutputValidationReportDialog("OutputValidations");
        dialog.setVisible(true);
    }

    /**
     * All succeed export operation is mantained by the export history.
     * @param evt
     */
    private void exportHistoryButtonActionPerformed(java.awt.event.ActionEvent evt) {
        OutputValidationReportDialog dialog = new OutputValidationReportDialog("ExportDetails");
        dialog.setVisible(true);
    }

    private void checkEnableExportButton() {
        if (projectId > 0 && volumeId > 0) {
            exportButton.setEnabled(true);
            tSearchExportButton.setEnabled(true);
            outpurFormatButton.setEnabled(true);
        } else {
            exportButton.setEnabled(false);
            tSearchExportButton.setEnabled(false);
            outpurFormatButton.setEnabled(false);
        }
    }  

    /**
     * Check that it's OK to exit the current page.  Subclasses must override this to provide a
     * page-dependent check.
     * @return true if it's OK to exit.  If field cancels save/no-save/cancel dialog,
     *         false is returned.
     */
    protected boolean exitPageCheck() {
        // TBD
        return true;
    }

    /** Get the menu bar for the current page.  Subclasses must override this to provide a
     * page-dependent menu bar.
     */
    protected javax.swing.JMenuBar getPageJMenuBar() {
        return menuBar;
    }

    /**
     * Perform page initialization.  Subclasses must override this to provide any
     * required page-dependent initialization.
     * 
     * Create a filtered and unfiltered model for project, volume, batch and batchEnd combos.
     */
    protected void tabSelected() {
        Log.print("ExportPage tabSelected");

        // project models
        if (pModel == null) {
            pModel = new ManagedTableSorter(0, SQLManagedTableModel.makeInstance("ExportPage.projectCombo"));
            projectModel = new ManagedComboModel(pModel);
            projectFilter = new ManagedTableFilter(pModel) {

                public boolean accept(TableRow theRow) {
                    if (((String) theRow.getValue(1)).equals("1")) {
                        // this project contains qc'd data
                        return true;
                    } else {
                        return false;
                    }
                }
            };
        }
        if (projectModelFilter == null) {
            projectModelFilter = new ManagedComboModel(projectFilter);
            pModel.register();
            setModels();
        }

        if (delimiters == null) {
            delimiters = new DelimiterPanel(null);
            delimiterPanel.add(delimiters, java.awt.BorderLayout.CENTER);

            delimiters.loadDelimiterNames(DelimiterPanel.DEFAULT);
        //delimiters.getSaveButton().setText("Edit Parameters");
        //delimiters.getDeleteButton().setEnabled(false);
        }
    }

    private void setModels() {        
        if (qcCheckBox != null || qcCheckBox.isSelected()) {
            // show all projects
            projectCombo.setModel(projectModel);
        } else {
            // show projects containing qc or higher batches
            projectCombo.setModel(projectModelFilter);
        }
    }

    private Dimension getFilenameSize() {
        return new java.awt.Dimension(500, 20);

    }

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            exitForm();
        } catch (Throwable th) {
            Log.quit(th);
        }
    }
    
    public void setEnableFormatButton(boolean enable){
        outpurFormatButton.setEnabled(enable);
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox batchCombo;
    private javax.swing.JComboBox batchEndCombo;
    private javax.swing.JRadioButton brsButton;
    private javax.swing.JButton csvButton;
    private beans.LTextField csvTextField;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JPanel delimiterPanel;
    private JPanel exportButtonsPanel;
    private javax.swing.JRadioButton doculexButton;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JButton exportButton;
    private javax.swing.JButton outpurFormatButton;    
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JRadioButton lfpButton;
    private javax.swing.JPanel lfpPanel;
    private beans.LTextField lfpTextField;
    private javax.swing.JButton logButton;
    private javax.swing.JPanel logPanel;
    private beans.LTextField logTextField;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JCheckBox nullAttachmentsCheckBox;
    private javax.swing.JRadioButton opticonButton;
    private javax.swing.JComboBox projectCombo;
    private javax.swing.JCheckBox qcCheckBox;
    private javax.swing.JRadioButton summationButton;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JCheckBox unitizationCheckBox;
    private javax.swing.JComboBox volumeCombo;
    private javax.swing.JButton xrefButton;
    private javax.swing.ButtonGroup xrefFormatGroup;
    private javax.swing.JPanel xrefTypePanel;
	private javax.swing.JButton tSearchExportButton;
	private javax.swing.JButton validationHistoryButton;
	private javax.swing.JButton exportHistoryButton;
    // End of variables declaration//GEN-END:variables

    public int getServerQueueId() {
        return serverQueueId;
    }

    public void setServerQueueId(int serverQueueId) {
        this.serverQueueId = serverQueueId;
    }
}
