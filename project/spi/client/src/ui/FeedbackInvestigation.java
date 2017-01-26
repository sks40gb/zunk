/*
 * FeedbackInvestigation.java
 *
 * Created on May 7, 2008, 7:29 PM
 */
package ui;

import com.lexpar.util.Log;
import model.CheckBoxTableModel;
import valueobjects.Feedback;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import java.util.ArrayList;
import java.util.List;
import common.msg.MessageConstants;
import com.acordex.vtj.ImageBean;
import export.IToolXlsReaderWriter;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

/**
 * Investigation's main window which display the i-tool records which is stored in the XLS files.
 * @author  sunil
 */
public class FeedbackInvestigation extends javax.swing.JFrame {

    private JFrame parent;
    public List<Feedback> feedbackList;
    Feedback feedback;
    private int index = 0;
    private beans.ViewTIFFPanel ourViewer;
    private int gridXPos = 0;
    private int gridYPos = 0;
    private int resolution = MessageConstants.RES_HIGH;
    int length = 0;
    public List<String> selectedImageList;
    private ImageBean ourBean = new ImageBean();
    public boolean isSaved = true;
    private final String VALID_ERROR = "Valid Error";
    private final String INVALID_ERROR = "Invalid Error";

    /** Creates new form FeedbackInvestigation */
    public FeedbackInvestigation(JFrame parent) {
        this.parent = parent;
        initComponents();
    }

    public FeedbackInvestigation(JFrame parent, List<Feedback> feedbackList, int projectId) {
        this.parent = parent;
        this.feedbackList = feedbackList;
        //this.projectId = projectId;
        Feedback.projecId = projectId;
        selectedImageList = new ArrayList<String>();
        initComponents();
        setIcons();
        ourViewer = new beans.ViewTIFFPanel();
        Constrain(theViewer, ourViewer, 0, -2, 3, 1, java.awt.GridBagConstraints.BOTH,
                java.awt.GridBagConstraints.WEST, 100, 100, 0, 0, 0, 0);

        //setting pagination values
        if (feedbackList == null || feedbackList.size() == 0) {
            firstButton.setEnabled(false);
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
            lastButton.setEnabled(false);
            totalPagesTextField.setText("0");
            currentPageTextField.setText("0");
            this.setTitle("Page 0 of " + feedbackList.size() + "   Investigation Tool    ");
        } else {
            totalPagesTextField.setText(feedbackList.size() + "");
            index = 0;
            setValuesForInvestigationForm(index);
            currentPageTextField.setText("1");
            this.setTitle("Page 1 of " + feedbackList.size() + "   Investigation Tool    ");
            displayImage(0);
        }
    }

    void displayImage(int index) {
        String filename = ((Feedback) feedbackList.get(index)).getImagePath() + "/" + ((Feedback) feedbackList.get(index)).getDocNumber() + ".TIF";
        ImageThread.renderImage(ourViewer, filename, 0, resolution,"");
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

    /**
     * Get the values from xls file and put the values to investigaton form 
     * fields.
     * @param index
     */
    private void setValuesForInvestigationForm(int index) {
        feedback = feedbackList.get(index);
        boxIdTextField.setText(feedback.getBoxId());
        docNumberTextField.setText(feedback.getDocNumber());
        tagNameTextField.setText(feedback.getTagName());
        tagValueTextField.setText(feedback.getTagValue());
        tagValueCorrectedTextField.setText(feedback.getCorrectTagValue());
        spiRemarksTextField.setText(feedback.getAuditProblem());
        spiRemarksTextArea.setText(feedback.getProcText());
        coderTextField.setText(feedback.getCoder());
        coderValueTextField.setText(feedback.getCoderValue());
        checkerTextField.setText(feedback.getChecker());
        checkerValueTextField.setText(feedback.getCheckerValue());
        listerTextField.setText(feedback.getListing());
        listerValueTextField.setText(feedback.getListingValue());
        //tallyTextField.setText(feedback.getTally());
       //tallyValueTextField.setText(feedback.getTallyValue());
        qaTextField.setText(feedback.getQa());
        qaValueTextField.setText(feedback.getQaValue());
        locationTextField.setText(feedback.getLocation());
        commentRemarksTextArea.setText(feedback.getComments());
        coderCheckBox.setSelected(feedback.isCoderSelected());
        checkerCheckBox.setSelected(feedback.isCheckerSelected());
        listingCheckBox.setSelected(feedback.isListingSelected());
       // tallyCheckBox.setSelected(feedback.isTallySelected());
        qaCheckBox.setSelected(feedback.isQaSelected());
        
       
        Object [][] obj = new Object[feedback.getMultipleTallyList().size()][3];  
        for(int i =0; i < feedback.getMultipleTallyList().size(); i++){
            obj[i][0] = null;
            obj[i][1] = feedback.getMultipleTallyList().get(i);
            obj[i][2] = feedback.getMultipleTallyValueList().get(i);            
        }
        multipleTallyTable.setModel(new javax.swing.table.DefaultTableModel(obj,
                new String [] {
                "Select", "Emp.No", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
    }

    /**
     * saving the current updated record for a particular feedback.
     */
    private void saveValuesForCurrentFeedback() {        
        feedback.setLocation(locationTextField.getText());
        feedback.setComments(commentRemarksTextArea.getText());
        if (validErrorCheckbox.isSelected()) {
            feedback.setValidity(VALID_ERROR);
        } else if (invalidErrorCheckbox.isSelected()) {
            feedback.setValidity(INVALID_ERROR);
        }
        feedback.setCoderSelected(coderCheckBox.isSelected());
        feedback.setCheckerSelected(checkerCheckBox.isSelected());
        feedback.setListingSelected(listingCheckBox.isSelected());
       // feedback.setTallySelected(tallyCheckBox.isSelected());
        feedback.setQaSelected(qaCheckBox.isSelected());
        
        
    }

    /**
     * setting the icons for the menus button dynamically.
     */
    public void setIcons() {
        vHorizButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fit_Horiz.gif")));
        vVertButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fit_Vert.gif")));
        vScreenButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/fit_Screen.gif")));
        vTurn90ClockwiseButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/turn90clockwise.gif")));
        vTurn90CounterwiseButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/turn90counterclockwise.gif")));
        vZoomWindowButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/zoomReset.gif")));
        vZoomInButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/zoomIn16.gif")));
        vZoomOutButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/zoomOut16.gif")));
        selectImageButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/filview3.gif")));
    }

    /**
     * Check the current feedback going to be displayed and enable or disable the
     * checkbox accordingly.
     * @param feedback - current record that going to be displayed.
     */
    public void enableCheckBox(Feedback feedback) {
        if (feedback.getValidity() != null && feedback.getValidity().equals(VALID_ERROR)) {
            validErrorCheckbox.setSelected(true);
            invalidErrorCheckbox.setSelected(false);
        } else {
            validErrorCheckbox.setSelected(false);
            invalidErrorCheckbox.setSelected(true);
        }
    }

    /**
     * warn if some changes are made by user and click exit without exporting 
     * xls file.
     */
    public void warnBeforeExit() {
        if (!isSaved) {
            int input = JOptionPane.showConfirmDialog(this, "The changes has not been saved. \n Are you sure want to exit ?");
            if (input == 0) {
                exportButtonActionPerformed(new ActionEvent(this, 0, "DIALOG_BOX"));
            } else if (input == 1) {
                this.dispose();
                parent.setVisible(true);
            }
        } else {
            parent.setVisible(true);
            this.dispose();
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLayeredPane11 = new javax.swing.JLayeredPane();
        boxIdTextField = new javax.swing.JTextField();
        docNumberLabel1 = new javax.swing.JLabel();
        jLayeredPane12 = new javax.swing.JLayeredPane();
        docNumberTextField = new javax.swing.JTextField();
        docNumberLabel = new javax.swing.JLabel();
        jLayeredPane13 = new javax.swing.JLayeredPane();
        tagNameTextField = new javax.swing.JTextField();
        tagNameLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        tagValueLabel = new javax.swing.JLabel();
        tagValueTextField = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        correctTagValueLabel = new javax.swing.JLabel();
        tagValueCorrectedTextField = new javax.swing.JTextField();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        validityLabel = new javax.swing.JLabel();
        invalidErrorCheckbox = new javax.swing.JCheckBox();
        validErrorCheckbox = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        spiRemarksTextArea = new javax.swing.JTextArea();
        spiRemarksLabel = new javax.swing.JLabel();
        spiRemarksTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        commentRemarksTextArea = new javax.swing.JTextArea();
        commentLabel = new javax.swing.JLabel();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        jLayeredPane21 = new javax.swing.JLayeredPane();
        legibilityLabel = new javax.swing.JLabel();
        LegibilityCombo = new javax.swing.JComboBox();
        jLayeredPane22 = new javax.swing.JLayeredPane();
        errorClassLabel = new javax.swing.JLabel();
        errorClassCombo = new javax.swing.JComboBox();
        jLayeredPane23 = new javax.swing.JLayeredPane();
        typeOfDocLabel = new javax.swing.JLabel();
        typeOfDocCombo = new javax.swing.JComboBox();
        jLayeredPane24 = new javax.swing.JLayeredPane();
        locationLabel = new javax.swing.JLabel();
        locationTextField = new javax.swing.JTextField();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jLayeredPane6 = new javax.swing.JLayeredPane();
        jPanel10 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        qaValueTextField = new javax.swing.JTextField();
        jLayeredPane28 = new javax.swing.JLayeredPane();
        qaTextField = new javax.swing.JTextField();
        qaCheckBox = new javax.swing.JCheckBox();
        jLayeredPane29 = new javax.swing.JLayeredPane();
        qaTextField1 = new javax.swing.JTextField();
        qaCheckBox1 = new javax.swing.JCheckBox();
        qaLabel = new javax.swing.JLabel();
        jLayeredPane7 = new javax.swing.JLayeredPane();
        jPanel11 = new javax.swing.JPanel();
        valueLabel = new javax.swing.JLabel();
        coderValueTextField = new javax.swing.JTextField();
        jLayeredPane30 = new javax.swing.JLayeredPane();
        coderTextField = new javax.swing.JTextField();
        coderCheckBox = new javax.swing.JCheckBox();
        coderLabel = new javax.swing.JLabel();
        jLayeredPane8 = new javax.swing.JLayeredPane();
        jPanel12 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        checkerValueTextField = new javax.swing.JTextField();
        jLayeredPane31 = new javax.swing.JLayeredPane();
        checkerTextField = new javax.swing.JTextField();
        checkerCheckBox = new javax.swing.JCheckBox();
        checkerLabel = new javax.swing.JLabel();
        jLayeredPane9 = new javax.swing.JLayeredPane();
        jPanel13 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        listerValueTextField = new javax.swing.JTextField();
        jLayeredPane32 = new javax.swing.JLayeredPane();
        listerTextField = new javax.swing.JTextField();
        listingCheckBox = new javax.swing.JCheckBox();
        listerLabel = new javax.swing.JLabel();
        jLayeredPane10 = new javax.swing.JLayeredPane();
        tallyLabel = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        multipleTallyTable = new javax.swing.JTable();
        dateOfTableLabel = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        firstButton = new javax.swing.JButton();
        previousButton = new javax.swing.JButton();
        currentPageTextField = new javax.swing.JTextField();
        ofLabel = new javax.swing.JLabel();
        totalPagesTextField = new javax.swing.JTextField();
        exitButton = new javax.swing.JButton();
        nextButton = new javax.swing.JButton();
        lastButton = new javax.swing.JButton();
        exportButton = new javax.swing.JButton();
        exitButton1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        theViewer = new javax.swing.JPanel();
        viewerToolBar = new javax.swing.JToolBar();
        vHorizButton = new javax.swing.JButton();
        vVertButton = new javax.swing.JButton();
        vScreenButton = new javax.swing.JButton();
        vTurn90ClockwiseButton = new javax.swing.JButton();
        vTurn90CounterwiseButton1 = new javax.swing.JButton();
        vZoomWindowButton = new javax.swing.JButton();
        vZoomInButton = new javax.swing.JButton();
        vZoomOutButton = new javax.swing.JButton();
        selectImageButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jSplitPane1.setDividerLocation(410);
        jSplitPane1.setAutoscrolls(true);
        jSplitPane1.setContinuousLayout(true);

        jPanel1.setBackground(new java.awt.Color(70, 130, 180));
        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(70, 130, 180), 5, true));
        jPanel1.setAlignmentX(0.4F);
        jPanel1.setAlignmentY(0.4F);

        jLayeredPane4.setBackground(new java.awt.Color(70, 130, 180));
        jLayeredPane4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(176, 196, 222), 3));
        jLayeredPane4.setForeground(new java.awt.Color(176, 196, 222));
        jLayeredPane4.setOpaque(true);

        jPanel8.setBackground(new java.awt.Color(70, 130, 180));
        jPanel8.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(176, 196, 222)));

        jPanel9.setBackground(new java.awt.Color(70, 130, 180));

        jLayeredPane11.setBackground(new java.awt.Color(70, 130, 180));
        jLayeredPane11.setForeground(java.awt.Color.white);
        jLayeredPane11.setOpaque(true);

        boxIdTextField.setEditable(false);
        boxIdTextField.setAlignmentX(0.0F);
        boxIdTextField.setAlignmentY(0.0F);
        boxIdTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                boxIdTextFieldActionPerformed(evt);
            }
        });
        boxIdTextField.setBounds(0, 20, 120, 20);
        jLayeredPane11.add(boxIdTextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        docNumberLabel1.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        docNumberLabel1.setForeground(java.awt.Color.white);
        docNumberLabel1.setText(" Box ID");
        docNumberLabel1.setAlignmentY(0.0F);
        docNumberLabel1.setBounds(0, 0, 50, 10);
        jLayeredPane11.add(docNumberLabel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        docNumberTextField.setEditable(false);
        docNumberTextField.setAlignmentX(0.0F);
        docNumberTextField.setAlignmentY(0.0F);
        docNumberTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                docNumberTextFieldActionPerformed(evt);
            }
        });
        docNumberTextField.setBounds(0, 20, 110, 20);
        jLayeredPane12.add(docNumberTextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        docNumberLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        docNumberLabel.setForeground(java.awt.Color.white);
        docNumberLabel.setText(" DocN");
        docNumberLabel.setAlignmentY(0.0F);
        docNumberLabel.setBounds(0, 0, 50, 10);
        jLayeredPane12.add(docNumberLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tagNameTextField.setEditable(false);
        tagNameTextField.setAlignmentX(0.0F);
        tagNameTextField.setAlignmentY(0.0F);
        tagNameTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tagNameTextFieldActionPerformed(evt);
            }
        });
        tagNameTextField.setBounds(0, 20, 130, 20);
        jLayeredPane13.add(tagNameTextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tagNameLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        tagNameLabel.setForeground(java.awt.Color.white);
        tagNameLabel.setText(" Tag Name");
        tagNameLabel.setAlignmentY(0.0F);
        tagNameLabel.setBounds(0, 0, 70, 10);
        jLayeredPane13.add(tagNameLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(jLayeredPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane13, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLayeredPane13, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane11, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.add(jPanel9);

        jPanel8.setBounds(10, 10, 380, 50);
        jLayeredPane4.add(jPanel8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(176, 196, 222)));
        jPanel2.setForeground(new java.awt.Color(70, 130, 180));
        jPanel2.setOpaque(false);

        tagValueLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        tagValueLabel.setForeground(java.awt.Color.white);
        tagValueLabel.setText(" Tag Value");
        tagValueLabel.setAlignmentY(0.0F);

        tagValueTextField.setEditable(false);
        tagValueTextField.setAlignmentX(0.0F);
        tagValueTextField.setAlignmentY(0.0F);
        tagValueTextField.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                tagValueTextFieldMouseMoved(evt);
            }
        });
        tagValueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tagValueTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(tagValueTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 378, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(tagValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(272, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(tagValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tagValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBounds(10, 60, 380, 50);
        jLayeredPane4.add(jPanel2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jPanel3.setBackground(new java.awt.Color(70, 130, 180));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(176, 196, 222)));
        jPanel3.setForeground(new java.awt.Color(176, 196, 222));

        correctTagValueLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        correctTagValueLabel.setForeground(java.awt.Color.white);
        correctTagValueLabel.setText(" Correct Tag Value");
        correctTagValueLabel.setAlignmentY(0.0F);

        tagValueCorrectedTextField.setEditable(false);
        tagValueCorrectedTextField.setAlignmentX(0.0F);
        tagValueCorrectedTextField.setAlignmentY(0.0F);
        tagValueCorrectedTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tagValueCorrectedTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(correctTagValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tagValueCorrectedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(correctTagValueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tagValueCorrectedTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBounds(10, 110, 380, 50);
        jLayeredPane4.add(jPanel3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane1.setBackground(new java.awt.Color(184, 202, 218));
        jLayeredPane1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(176, 196, 222)));
        jLayeredPane1.setForeground(new java.awt.Color(70, 130, 180));

        validityLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        validityLabel.setForeground(java.awt.Color.white);
        validityLabel.setText("Validity");
        validityLabel.setAlignmentY(0.0F);
        validityLabel.setBounds(10, 0, 50, 20);
        jLayeredPane1.add(validityLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        invalidErrorCheckbox.setBackground(new java.awt.Color(70, 130, 180));
        invalidErrorCheckbox.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        invalidErrorCheckbox.setForeground(new java.awt.Color(40, 238, 40));
        invalidErrorCheckbox.setText("Invalid Error");
        invalidErrorCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invalidErrorCheckboxActionPerformed(evt);
            }
        });
        invalidErrorCheckbox.setBounds(190, 0, 120, 30);
        jLayeredPane1.add(invalidErrorCheckbox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        validErrorCheckbox.setBackground(new java.awt.Color(70, 130, 180));
        validErrorCheckbox.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        validErrorCheckbox.setForeground(new java.awt.Color(40, 238, 40));
        validErrorCheckbox.setSelected(true);
        validErrorCheckbox.setText("Valid Error");
        validErrorCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validErrorCheckboxActionPerformed(evt);
            }
        });
        validErrorCheckbox.setBounds(80, 2, 100, 30);
        jLayeredPane1.add(validErrorCheckbox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane1.setBounds(10, 160, 380, 30);
        jLayeredPane4.add(jLayeredPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jPanel4.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(176, 196, 222)));
        jPanel4.setForeground(new java.awt.Color(176, 196, 222));
        jPanel4.setOpaque(false);

        spiRemarksTextArea.setLineWrap(true);
        spiRemarksTextArea.setRows(5);
        spiRemarksTextArea.setEnabled(false);
        spiRemarksTextArea.setMinimumSize(new java.awt.Dimension(14, 0));
        jScrollPane1.setViewportView(spiRemarksTextArea);

        spiRemarksLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        spiRemarksLabel.setForeground(java.awt.Color.white);
        spiRemarksLabel.setText("SPI Remarks");
        spiRemarksLabel.setAlignmentY(0.0F);

        spiRemarksTextField.setEditable(false);
        spiRemarksTextField.setAlignmentX(0.0F);
        spiRemarksTextField.setAlignmentY(0.0F);
        spiRemarksTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spiRemarksTextFieldActionPerformed(evt);
            }
        });

        commentRemarksTextArea.setLineWrap(true);
        commentRemarksTextArea.setRows(5);
        commentRemarksTextArea.setMinimumSize(new java.awt.Dimension(14, 0));
        jScrollPane2.setViewportView(commentRemarksTextArea);

        commentLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        commentLabel.setForeground(java.awt.Color.white);
        commentLabel.setText("Comment");
        commentLabel.setAlignmentY(0.0F);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                    .addComponent(spiRemarksTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(commentLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(spiRemarksLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(270, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(spiRemarksLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 88, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(spiRemarksTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(commentLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        jPanel4.setBounds(10, 190, 380, 110);
        jLayeredPane4.add(jPanel4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane3.setBackground(new java.awt.Color(220, 223, 225));
        jLayeredPane3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(176, 196, 222)));

        legibilityLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        legibilityLabel.setForeground(java.awt.Color.white);
        legibilityLabel.setText("Legibility");
        legibilityLabel.setAlignmentY(0.0F);
        legibilityLabel.setBounds(0, 0, 70, 20);
        jLayeredPane21.add(legibilityLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        LegibilityCombo.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        LegibilityCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Legible", "Partial Legible", "Illegible" }));
        LegibilityCombo.setBounds(0, 20, 100, 20);
        jLayeredPane21.add(LegibilityCombo, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane21.setBounds(280, 0, 100, 50);
        jLayeredPane3.add(jLayeredPane21, javax.swing.JLayeredPane.DEFAULT_LAYER);

        errorClassLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        errorClassLabel.setForeground(java.awt.Color.white);
        errorClassLabel.setText("Error Class");
        errorClassLabel.setAlignmentY(0.0F);
        errorClassLabel.setBounds(0, 0, 80, 20);
        jLayeredPane22.add(errorClassLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        errorClassCombo.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        errorClassCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Miscoded", "Deleted", "Added/Uncoded" }));
        errorClassCombo.setBounds(0, 20, 90, 20);
        jLayeredPane22.add(errorClassCombo, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane22.setBounds(0, 0, 100, 50);
        jLayeredPane3.add(jLayeredPane22, javax.swing.JLayeredPane.DEFAULT_LAYER);

        typeOfDocLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        typeOfDocLabel.setForeground(java.awt.Color.white);
        typeOfDocLabel.setText("Type of Doc");
        typeOfDocLabel.setAlignmentY(0.0F);
        typeOfDocLabel.setBounds(0, 0, 80, 20);
        jLayeredPane23.add(typeOfDocLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        typeOfDocCombo.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        typeOfDocCombo.setBounds(0, 20, 90, 20);
        jLayeredPane23.add(typeOfDocCombo, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane23.setBounds(100, 0, 100, 50);
        jLayeredPane3.add(jLayeredPane23, javax.swing.JLayeredPane.DEFAULT_LAYER);

        locationLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        locationLabel.setForeground(java.awt.Color.white);
        locationLabel.setText("Location");
        locationLabel.setAlignmentY(0.0F);
        locationLabel.setBounds(0, 0, 80, 20);
        jLayeredPane24.add(locationLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        locationTextField.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        locationTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                locationTextFieldActionPerformed(evt);
            }
        });
        locationTextField.setBounds(0, 20, 70, 20);
        jLayeredPane24.add(locationTextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane24.setBounds(200, 0, 80, 50);
        jLayeredPane3.add(jLayeredPane24, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane3.setBounds(10, 300, 380, 50);
        jLayeredPane4.add(jLayeredPane3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane2.setFocusCycleRoot(true);
        jLayeredPane2.setOpaque(true);

        jLayeredPane6.setBackground(new java.awt.Color(176, 196, 222));
        jLayeredPane6.setOpaque(true);

        jPanel10.setBackground(new java.awt.Color(70, 130, 180));
        jPanel10.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel24.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        jLabel24.setForeground(java.awt.Color.white);
        jLabel24.setText("Value");
        jLabel24.setAlignmentY(0.0F);

        qaValueTextField.setEditable(false);
        qaValueTextField.setAlignmentX(0.0F);
        qaValueTextField.setAlignmentY(0.0F);
        qaValueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qaValueTextFieldActionPerformed(evt);
            }
        });

        jLayeredPane28.setBackground(new java.awt.Color(70, 130, 180));
        jLayeredPane28.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLayeredPane28.setForeground(new java.awt.Color(70, 130, 180));
        jLayeredPane28.setOpaque(true);
        jLayeredPane28.setRequestFocusEnabled(false);

        qaTextField.setEditable(false);
        qaTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qaTextFieldActionPerformed(evt);
            }
        });
        qaTextField.setBounds(10, 20, 80, 20);
        jLayeredPane28.add(qaTextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        qaCheckBox.setBackground(new java.awt.Color(70, 130, 180));
        qaCheckBox.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        qaCheckBox.setForeground(java.awt.Color.white);
        qaCheckBox.setText("Emp no.");
        qaCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qaCheckBoxActionPerformed(evt);
            }
        });
        qaCheckBox.setBounds(10, 0, 80, 20);
        jLayeredPane28.add(qaCheckBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(124, Short.MAX_VALUE))
            .addComponent(qaValueTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(qaValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel10.setBounds(120, 20, 250, 50);
        jLayeredPane6.add(jPanel10, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane29.setBackground(new java.awt.Color(70, 130, 180));
        jLayeredPane29.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLayeredPane29.setForeground(new java.awt.Color(70, 130, 180));
        jLayeredPane29.setOpaque(true);
        jLayeredPane29.setRequestFocusEnabled(false);

        qaTextField1.setEditable(false);
        qaTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qaTextFieldActionPerformed(evt);
            }
        });
        qaTextField1.setBounds(10, 20, 80, 20);
        jLayeredPane29.add(qaTextField1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        qaCheckBox1.setBackground(new java.awt.Color(70, 130, 180));
        qaCheckBox1.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        qaCheckBox1.setForeground(java.awt.Color.white);
        qaCheckBox1.setText("Emp no.");
        qaCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                qaCheckBoxActionPerformed(evt);
            }
        });
        qaCheckBox1.setBounds(10, 0, 80, 20);
        jLayeredPane29.add(qaCheckBox1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane29.setBounds(10, 20, 100, 50);
        jLayeredPane6.add(jLayeredPane29, javax.swing.JLayeredPane.DEFAULT_LAYER);

        qaLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        qaLabel.setForeground(java.awt.Color.white);
        qaLabel.setText(" QA");
        qaLabel.setAlignmentY(0.0F);
        qaLabel.setBounds(10, 0, 70, 20);
        jLayeredPane6.add(qaLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane6.setBounds(0, 300, 380, 80);
        jLayeredPane2.add(jLayeredPane6, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane7.setBackground(new java.awt.Color(176, 196, 222));
        jLayeredPane7.setOpaque(true);

        jPanel11.setBackground(new java.awt.Color(70, 130, 180));
        jPanel11.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        valueLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        valueLabel.setForeground(java.awt.Color.white);
        valueLabel.setText("Value");
        valueLabel.setAlignmentY(0.0F);

        coderValueTextField.setEditable(false);
        coderValueTextField.setAlignmentX(0.0F);
        coderValueTextField.setAlignmentY(0.0F);
        coderValueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coderValueTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(valueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(124, Short.MAX_VALUE))
            .addComponent(coderValueTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(valueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(coderValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBounds(120, 20, 250, 50);
        jLayeredPane7.add(jPanel11, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane30.setBackground(new java.awt.Color(70, 130, 180));
        jLayeredPane30.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLayeredPane30.setForeground(new java.awt.Color(70, 130, 180));
        jLayeredPane30.setOpaque(true);
        jLayeredPane30.setRequestFocusEnabled(false);

        coderTextField.setEditable(false);
        coderTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coderTextFieldActionPerformed(evt);
            }
        });
        coderTextField.setBounds(10, 20, 80, 20);
        jLayeredPane30.add(coderTextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        coderCheckBox.setBackground(new java.awt.Color(70, 130, 180));
        coderCheckBox.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        coderCheckBox.setForeground(java.awt.Color.white);
        coderCheckBox.setText("Emp no.");
        coderCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                coderCheckBoxActionPerformed(evt);
            }
        });
        coderCheckBox.setBounds(10, 0, 80, 20);
        jLayeredPane30.add(coderCheckBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane30.setBounds(10, 20, 100, 50);
        jLayeredPane7.add(jLayeredPane30, javax.swing.JLayeredPane.DEFAULT_LAYER);

        coderLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        coderLabel.setForeground(java.awt.Color.white);
        coderLabel.setText(" Coder");
        coderLabel.setAlignmentY(0.0F);
        coderLabel.setBounds(10, 0, 50, 20);
        jLayeredPane7.add(coderLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane7.setBounds(0, 0, 380, 70);
        jLayeredPane2.add(jLayeredPane7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane8.setBackground(new java.awt.Color(176, 196, 222));
        jLayeredPane8.setOpaque(true);

        jPanel12.setBackground(new java.awt.Color(70, 130, 180));
        jPanel12.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel30.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        jLabel30.setForeground(java.awt.Color.white);
        jLabel30.setText("Value");
        jLabel30.setAlignmentY(0.0F);

        checkerValueTextField.setEditable(false);
        checkerValueTextField.setAlignmentX(0.0F);
        checkerValueTextField.setAlignmentY(0.0F);
        checkerValueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkerValueTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(124, Short.MAX_VALUE))
            .addComponent(checkerValueTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(checkerValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.setBounds(120, 20, 250, 50);
        jLayeredPane8.add(jPanel12, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane31.setBackground(new java.awt.Color(70, 130, 180));
        jLayeredPane31.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLayeredPane31.setForeground(new java.awt.Color(70, 130, 180));
        jLayeredPane31.setOpaque(true);
        jLayeredPane31.setRequestFocusEnabled(false);

        checkerTextField.setEditable(false);
        checkerTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkerTextFieldActionPerformed(evt);
            }
        });
        checkerTextField.setBounds(10, 20, 80, 20);
        jLayeredPane31.add(checkerTextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        checkerCheckBox.setBackground(new java.awt.Color(70, 130, 180));
        checkerCheckBox.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        checkerCheckBox.setForeground(java.awt.Color.white);
        checkerCheckBox.setText("Emp no.");
        checkerCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkerCheckBoxActionPerformed(evt);
            }
        });
        checkerCheckBox.setBounds(10, 0, 80, 20);
        jLayeredPane31.add(checkerCheckBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane31.setBounds(10, 20, 100, 50);
        jLayeredPane8.add(jLayeredPane31, javax.swing.JLayeredPane.DEFAULT_LAYER);

        checkerLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        checkerLabel.setForeground(java.awt.Color.white);
        checkerLabel.setText(" Checker");
        checkerLabel.setAlignmentY(0.0F);
        checkerLabel.setBounds(10, 0, 70, 20);
        jLayeredPane8.add(checkerLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane8.setBounds(0, 70, 380, 70);
        jLayeredPane2.add(jLayeredPane8, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane9.setBackground(new java.awt.Color(176, 196, 222));
        jLayeredPane9.setOpaque(true);

        jPanel13.setBackground(new java.awt.Color(70, 130, 180));
        jPanel13.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel31.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        jLabel31.setForeground(java.awt.Color.white);
        jLabel31.setText("Value");
        jLabel31.setAlignmentY(0.0F);

        listerValueTextField.setEditable(false);
        listerValueTextField.setAlignmentX(0.0F);
        listerValueTextField.setAlignmentY(0.0F);
        listerValueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listerValueTextFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(124, Short.MAX_VALUE))
            .addComponent(listerValueTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(listerValueTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBounds(120, 20, 250, 50);
        jLayeredPane9.add(jPanel13, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane32.setBackground(new java.awt.Color(70, 130, 180));
        jLayeredPane32.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jLayeredPane32.setForeground(new java.awt.Color(70, 130, 180));
        jLayeredPane32.setOpaque(true);
        jLayeredPane32.setRequestFocusEnabled(false);

        listerTextField.setEditable(false);
        listerTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listerTextFieldActionPerformed(evt);
            }
        });
        listerTextField.setBounds(10, 20, 80, 20);
        jLayeredPane32.add(listerTextField, javax.swing.JLayeredPane.DEFAULT_LAYER);

        listingCheckBox.setBackground(new java.awt.Color(70, 130, 180));
        listingCheckBox.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        listingCheckBox.setForeground(java.awt.Color.white);
        listingCheckBox.setText("Emp no.");
        listingCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listingCheckBoxActionPerformed(evt);
            }
        });
        listingCheckBox.setBounds(10, 0, 80, 20);
        jLayeredPane32.add(listingCheckBox, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane32.setBounds(10, 20, 100, 50);
        jLayeredPane9.add(jLayeredPane32, javax.swing.JLayeredPane.DEFAULT_LAYER);

        listerLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        listerLabel.setForeground(java.awt.Color.white);
        listerLabel.setText(" Listing");
        listerLabel.setAlignmentY(0.0F);
        listerLabel.setBounds(10, 0, 70, 20);
        jLayeredPane9.add(listerLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane9.setBounds(0, 140, 380, 70);
        jLayeredPane2.add(jLayeredPane9, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane10.setBackground(new java.awt.Color(176, 196, 222));
        jLayeredPane10.setOpaque(true);
        jLayeredPane10.setPreferredSize(new java.awt.Dimension(380, 100));

        tallyLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        tallyLabel.setForeground(java.awt.Color.white);
        tallyLabel.setText(" Tally");
        tallyLabel.setAlignmentY(0.0F);
        tallyLabel.setBounds(10, 0, 70, 20);
        jLayeredPane10.add(tallyLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        multipleTallyTable.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        multipleTallyTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        multipleTallyTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Select", "Emp.No", "Value"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                true, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(multipleTallyTable);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 360, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
        );

        jPanel7.setBounds(10, 20, 360, 70);
        jLayeredPane10.add(jPanel7, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane10.setBounds(0, 210, 380, 90);
        jLayeredPane2.add(jLayeredPane10, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jLayeredPane2.setBounds(10, 370, 380, 380);
        jLayeredPane4.add(jLayeredPane2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        dateOfTableLabel.setFont(new java.awt.Font("DejaVu Sans", 1, 11));
        dateOfTableLabel.setForeground(java.awt.Color.white);
        dateOfTableLabel.setText("Data of Table");
        dateOfTableLabel.setBounds(20, 350, 90, 20);
        jLayeredPane4.add(dateOfTableLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jPanel5.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(176, 196, 222), 2));
        jPanel5.setOpaque(false);

        firstButton.setText("<--");
        firstButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstButtonActionPerformed(evt);
            }
        });
        jPanel5.add(firstButton);

        previousButton.setText("<");
        previousButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousButtonActionPerformed(evt);
            }
        });
        jPanel5.add(previousButton);

        currentPageTextField.setEditable(false);
        jPanel5.add(currentPageTextField);

        ofLabel.setForeground(java.awt.Color.white);
        ofLabel.setText("OF");
        jPanel5.add(ofLabel);

        totalPagesTextField.setEditable(false);
        jPanel5.add(totalPagesTextField);

        exitButton.setText("EXIT");
        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        jPanel5.add(exitButton);

        nextButton.setText(">");
        nextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextButtonActionPerformed(evt);
            }
        });
        jPanel5.add(nextButton);

        lastButton.setText("-->");
        lastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lastButtonActionPerformed(evt);
            }
        });
        jPanel5.add(lastButton);

        exportButton.setText("Export");
        exportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportButtonActionPerformed(evt);
            }
        });
        jPanel5.add(exportButton);

        exitButton1.setText("EXIT");
        exitButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitButtonActionPerformed(evt);
            }
        });
        jPanel5.add(exitButton1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLayeredPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLayeredPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 758, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane1.setLeftComponent(jPanel1);

        theViewer.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        theViewer.setName("theViewer"); // NOI18N
        theViewer.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                theViewerMouseClicked(evt);
            }
        });
        theViewer.setLayout(new java.awt.GridBagLayout());

        viewerToolBar.setRollover(true);

        viewerToolBar.addSeparator();
        vHorizButton.setToolTipText("Fit Horizontally");
        vHorizButton.setFocusable(false);
        vHorizButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        vHorizButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        vHorizButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vHorizButtonActionPerformed(evt);
            }
        });
        viewerToolBar.add(vHorizButton);

        vVertButton.setToolTipText("Fit Vertically");
        vVertButton.setFocusable(false);
        vVertButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        vVertButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        vVertButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vVertButtonActionPerformed(evt);
            }
        });
        viewerToolBar.add(vVertButton);

        vScreenButton.setToolTipText("Fit to Screen");
        vScreenButton.setFocusable(false);
        vScreenButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        vScreenButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        vScreenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vScreenButtonActionPerformed(evt);
            }
        });
        viewerToolBar.add(vScreenButton);

        viewerToolBar.addSeparator();
        vTurn90ClockwiseButton.setToolTipText("Rotate Clockwise");
        vTurn90ClockwiseButton.setFocusable(false);
        vTurn90ClockwiseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        vTurn90ClockwiseButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        vTurn90ClockwiseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vTurn90ClockwiseButtonActionPerformed(evt);
            }
        });
        viewerToolBar.add(vTurn90ClockwiseButton);

        vTurn90CounterwiseButton1.setToolTipText("Rotate Counter Clockwise");
        vTurn90CounterwiseButton1.setFocusable(false);
        vTurn90CounterwiseButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        vTurn90CounterwiseButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        vTurn90CounterwiseButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vTurn90CounterwiseButton1ActionPerformed(evt);
            }
        });
        viewerToolBar.add(vTurn90CounterwiseButton1);

        viewerToolBar.addSeparator();
        vZoomWindowButton.setToolTipText("Zoom Window");
        vZoomWindowButton.setFocusable(false);
        vZoomWindowButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        vZoomWindowButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        vZoomWindowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vZoomWindowButtonActionPerformed(evt);
            }
        });
        viewerToolBar.add(vZoomWindowButton);

        vZoomInButton.setToolTipText("Zoom In");
        vZoomInButton.setFocusable(false);
        vZoomInButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        vZoomInButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        vZoomInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vZoomInButtonActionPerformed(evt);
            }
        });
        viewerToolBar.add(vZoomInButton);

        vZoomOutButton.setToolTipText("Zoom Out");
        vZoomOutButton.setFocusable(false);
        vZoomOutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        vZoomOutButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        vZoomOutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vZoomOutButtonActionPerformed(evt);
            }
        });
        viewerToolBar.add(vZoomOutButton);

        selectImageButton.setToolTipText("Export feedback reports");
        selectImageButton.setBorderPainted(false);
        selectImageButton.setFocusable(false);
        selectImageButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectImageButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectImageButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectImageButtonActionPerformed(evt);
            }
        });
        viewerToolBar.add(selectImageButton);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(viewerToolBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
            .addComponent(theViewer, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(viewerToolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(theViewer, javax.swing.GroupLayout.PREFERRED_SIZE, 745, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1018, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void boxIdTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_boxIdTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_boxIdTextFieldActionPerformed

private void docNumberTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_docNumberTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_docNumberTextFieldActionPerformed

private void tagNameTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tagNameTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_tagNameTextFieldActionPerformed

private void tagValueTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tagValueTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_tagValueTextFieldActionPerformed

private void tagValueCorrectedTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tagValueCorrectedTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_tagValueCorrectedTextFieldActionPerformed

private void invalidErrorCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invalidErrorCheckboxActionPerformed
    if (invalidErrorCheckbox.isSelected()) {
        validErrorCheckbox.setSelected(false);
    } else {
        validErrorCheckbox.setSelected(true);
    }
}//GEN-LAST:event_invalidErrorCheckboxActionPerformed

private void spiRemarksTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spiRemarksTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_spiRemarksTextFieldActionPerformed

private void locationTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_locationTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_locationTextFieldActionPerformed

private void qaValueTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qaValueTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_qaValueTextFieldActionPerformed

private void qaTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qaTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_qaTextFieldActionPerformed

private void qaCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_qaCheckBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_qaCheckBoxActionPerformed

private void coderValueTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coderValueTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_coderValueTextFieldActionPerformed

private void coderTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coderTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_coderTextFieldActionPerformed

private void coderCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_coderCheckBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_coderCheckBoxActionPerformed

private void checkerValueTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkerValueTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_checkerValueTextFieldActionPerformed

private void checkerTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkerTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_checkerTextFieldActionPerformed

private void checkerCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkerCheckBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_checkerCheckBoxActionPerformed

private void listerValueTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listerValueTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_listerValueTextFieldActionPerformed

private void listerTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listerTextFieldActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_listerTextFieldActionPerformed

private void listingCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listingCheckBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_listingCheckBoxActionPerformed

private void exitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitButtonActionPerformed

    warnBeforeExit();
}//GEN-LAST:event_exitButtonActionPerformed

private void firstButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstButtonActionPerformed
    saveValuesForCurrentFeedback();
    index = 0;
    onClickingPaginatioButtons(); 
}//GEN-LAST:event_firstButtonActionPerformed

private void lastButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lastButtonActionPerformed
    try {
        saveValuesForCurrentFeedback();
        index = (feedbackList.size() - 1);
        onClickingPaginatioButtons();
    } catch (Exception ex) {
        Logger.getLogger(FeedbackInvestigation.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_lastButtonActionPerformed

private void previousButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousButtonActionPerformed

    if (index > 0) {
        saveValuesForCurrentFeedback();
        index--;
        onClickingPaginatioButtons();
    }
}//GEN-LAST:event_previousButtonActionPerformed

private void nextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextButtonActionPerformed
    if (index < (feedbackList.size() - 1)) {
        saveValuesForCurrentFeedback();
        index++;
        onClickingPaginatioButtons();
    }
}//GEN-LAST:event_nextButtonActionPerformed

    private void onClickingPaginatioButtons() {
        setValuesForInvestigationForm(index);
        currentPageTextField.setText((index + 1) + "");
        this.setTitle("Page " + (index + 1) + " of " + feedbackList.size() + "   Investigation Tool    ");
        displayImage(index);
        enablePagingButtons(index);
        enableCheckBox(feedbackList.get(index));
    }

    /**
     * performing the pagination for the investiation form.
     * @param index - current index of total count.
     */
    private void enablePagingButtons(int index) {
        if (index == 0) {
            firstButton.setEnabled(false);
            previousButton.setEnabled(false);
            if (feedbackList.size() > 0) {
                lastButton.setEnabled(true);
                nextButton.setEnabled(true);
            }
        } else if (index == (feedbackList.size() - 1)) {
            lastButton.setEnabled(false);
            nextButton.setEnabled(false);
            if (feedbackList.size() > 0) {
                firstButton.setEnabled(true);
                previousButton.setEnabled(true);
            }
        } else {
            if (feedbackList.size() > 1) {
                firstButton.setEnabled(true);
                previousButton.setEnabled(true);
                lastButton.setEnabled(true);
                nextButton.setEnabled(true);
            }
        }
    }

private void theViewerMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_theViewerMouseClicked
// TODO add your handling code here:
}//GEN-LAST:event_theViewerMouseClicked

private void vHorizButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vHorizButtonActionPerformed
    try {
        ourViewer.zoomFitWidth();
    } catch (Throwable th) {
        Log.quit(th);
    }
}//GEN-LAST:event_vHorizButtonActionPerformed

private void vVertButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vVertButtonActionPerformed
    try {
        ourViewer.zoomFitHeight();
    } catch (Throwable th) {
        Log.quit(th);
    }
}//GEN-LAST:event_vVertButtonActionPerformed

private void vScreenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vScreenButtonActionPerformed
    try {
        ourViewer.zoomFitWindow();
    } catch (Throwable th) {
        Log.quit(th);
    }
}//GEN-LAST:event_vScreenButtonActionPerformed

private void vTurn90ClockwiseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vTurn90ClockwiseButtonActionPerformed

    try {
        ourViewer.rotateClockwise();
    } catch (Throwable th) {
        Log.quit(th);
    }
}//GEN-LAST:event_vTurn90ClockwiseButtonActionPerformed

private void vTurn90CounterwiseButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vTurn90CounterwiseButton1ActionPerformed
    try {
        ourViewer.rotateCounterClockwise();
    } catch (Throwable th) {
        Log.quit(th);
    }
}//GEN-LAST:event_vTurn90CounterwiseButton1ActionPerformed

private void vZoomWindowButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vZoomWindowButtonActionPerformed
//Log.print("(SplitPaneViewer).vZoomWindowButtonActionPerformed ---");
    //ourViewer.zoomPercent(120);
    try {
        ourViewer.openMagWindow();
    } catch (Throwable th) {
        Log.quit(th);
    }
}//GEN-LAST:event_vZoomWindowButtonActionPerformed

private void vZoomInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vZoomInButtonActionPerformed
    try {
        ourViewer.zoomEnlarge();
    } catch (Throwable th) {
        Log.quit(th);
    }
}//GEN-LAST:event_vZoomInButtonActionPerformed

private void vZoomOutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vZoomOutButtonActionPerformed
    try {
        ourViewer.zoomReduce();
    } catch (Throwable th) {
        Log.quit(th);
    }
}//GEN-LAST:event_vZoomOutButtonActionPerformed

private void selectImageButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectImageButtonActionPerformed
    try {
        String[] headings = {"", ""};
        CheckBoxTableModel checkboxtablemodel = new CheckBoxTableModel(feedbackList.get(index), headings);
        SelectImageLinkFrame selectframe = new SelectImageLinkFrame(this, checkboxtablemodel, index);
        selectframe.setLocationRelativeTo(null);
        selectframe.setVisible(true);
        this.setEnabled(false);

    } catch (Exception e) {
        e.printStackTrace();
    }

}//GEN-LAST:event_selectImageButtonActionPerformed

private void exportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportButtonActionPerformed
    try {
        
        saveValuesForCurrentFeedback();
        JFileChooser chooseFile = new JFileChooser();
        chooseFile.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JPanel p = new JPanel(true);
        int returnVal = chooseFile.showSaveDialog(this);
         List selectedList = new ArrayList();
//        multipleTallyTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//
//            public void valueChanged(ListSelectionEvent e) {
//                //Ignore extra messages.
//                if (e.getValueIsAdjusting()) {
//                    return;
//                }
//                ListSelectionModel lsm =
//                        (ListSelectionModel) e.getSource();
//                if (lsm.isSelectionEmpty() || multipleTallyTable.getSelectedRow() < 0) {
//                    //no rows are selected
//                    int rows = multipleTallyTable.getRowCount();                   
//                        for (int i = 0; i < rows; i++) {
//                            String selectedValue = multipleTallyTable.getValueAt(i, 1).toString();
//                            if (selectedValue.equals("true")) {                              
//                              
//                            }
//                        }                    
//                } else {                  
//                    int rows = multipleTallyTable.getRowCount();  
//                   
//                        for (int i = 0; i < rows; i++) {
//                           // selectedList.add(multipleTallyTable.getValueAt(i, 1).toString());                            
//                        }                   
//                }
//            }
//
//               
//        });
        
                      int rows = multipleTallyTable.getRowCount();                     
                        for (int i = 0; i < rows; i++) {
                           // String check = multipleTallyTable.getValueAt(i, 0).toString();
                            if(multipleTallyTable.getValueAt(i, 0) !=null){
                                 System.out.println("2222222222222=============>" );
                              if(multipleTallyTable.getValueAt(i, 0).toString().equals("true")){
                                  System.out.println("1111111111111=============>" );
                                selectedList.add(multipleTallyTable.getValueAt(i, 1).toString()); 
                              }
                            }
                                             
                        }  
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            isSaved = true;
            String filePath = chooseFile.getSelectedFile().getPath() + ".xls";          
            IToolXlsReaderWriter xlsWriter = IToolXlsReaderWriter.writer();
             List list = new ArrayList();
             list =  feedback.getMultipleTallyList();
            xlsWriter.write(filePath, "MainSheet", feedbackList,list.size(),selectedList);
            JOptionPane.showMessageDialog(parent , "Export done successfully" );
        }
         
      
        } catch (Exception ex) {ex.printStackTrace();}//GEN-LAST:event_exportButtonActionPerformed
    }

private void validErrorCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validErrorCheckboxActionPerformed
    if (validErrorCheckbox.isSelected()) {
        invalidErrorCheckbox.setSelected(false);
    } else {
        invalidErrorCheckbox.setSelected(true);
    }

}//GEN-LAST:event_validErrorCheckboxActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
    warnBeforeExit();
}//GEN-LAST:event_formWindowClosing

private void tagValueTextFieldMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tagValueTextFieldMouseMoved
    tagValueTextField.setToolTipText(tagValueTextField.getText());        
}//GEN-LAST:event_tagValueTextFieldMouseMoved

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new FeedbackInvestigation(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox LegibilityCombo;
    private javax.swing.JTextField boxIdTextField;
    private javax.swing.JCheckBox checkerCheckBox;
    private javax.swing.JLabel checkerLabel;
    private javax.swing.JTextField checkerTextField;
    private javax.swing.JTextField checkerValueTextField;
    private javax.swing.JCheckBox coderCheckBox;
    private javax.swing.JLabel coderLabel;
    private javax.swing.JTextField coderTextField;
    private javax.swing.JTextField coderValueTextField;
    private javax.swing.JLabel commentLabel;
    private javax.swing.JTextArea commentRemarksTextArea;
    private javax.swing.JLabel correctTagValueLabel;
    private javax.swing.JTextField currentPageTextField;
    private javax.swing.JLabel dateOfTableLabel;
    private javax.swing.JLabel docNumberLabel;
    private javax.swing.JLabel docNumberLabel1;
    private javax.swing.JTextField docNumberTextField;
    private javax.swing.JComboBox errorClassCombo;
    private javax.swing.JLabel errorClassLabel;
    private javax.swing.JButton exitButton;
    private javax.swing.JButton exitButton1;
    private javax.swing.JButton exportButton;
    private javax.swing.JButton firstButton;
    private javax.swing.JCheckBox invalidErrorCheckbox;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane10;
    private javax.swing.JLayeredPane jLayeredPane11;
    private javax.swing.JLayeredPane jLayeredPane12;
    private javax.swing.JLayeredPane jLayeredPane13;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane21;
    private javax.swing.JLayeredPane jLayeredPane22;
    private javax.swing.JLayeredPane jLayeredPane23;
    private javax.swing.JLayeredPane jLayeredPane24;
    private javax.swing.JLayeredPane jLayeredPane28;
    private javax.swing.JLayeredPane jLayeredPane29;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JLayeredPane jLayeredPane30;
    private javax.swing.JLayeredPane jLayeredPane31;
    private javax.swing.JLayeredPane jLayeredPane32;
    private javax.swing.JLayeredPane jLayeredPane4;
    private javax.swing.JLayeredPane jLayeredPane6;
    private javax.swing.JLayeredPane jLayeredPane7;
    private javax.swing.JLayeredPane jLayeredPane8;
    private javax.swing.JLayeredPane jLayeredPane9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JButton lastButton;
    private javax.swing.JLabel legibilityLabel;
    private javax.swing.JLabel listerLabel;
    private javax.swing.JTextField listerTextField;
    private javax.swing.JTextField listerValueTextField;
    private javax.swing.JCheckBox listingCheckBox;
    private javax.swing.JLabel locationLabel;
    private javax.swing.JTextField locationTextField;
    private javax.swing.JTable multipleTallyTable;
    private javax.swing.JButton nextButton;
    private javax.swing.JLabel ofLabel;
    private javax.swing.JButton previousButton;
    private javax.swing.JCheckBox qaCheckBox;
    private javax.swing.JCheckBox qaCheckBox1;
    private javax.swing.JLabel qaLabel;
    private javax.swing.JTextField qaTextField;
    private javax.swing.JTextField qaTextField1;
    private javax.swing.JTextField qaValueTextField;
    private javax.swing.JButton selectImageButton;
    private javax.swing.JLabel spiRemarksLabel;
    private javax.swing.JTextArea spiRemarksTextArea;
    private javax.swing.JTextField spiRemarksTextField;
    private javax.swing.JLabel tagNameLabel;
    private javax.swing.JTextField tagNameTextField;
    private javax.swing.JTextField tagValueCorrectedTextField;
    private javax.swing.JLabel tagValueLabel;
    private javax.swing.JTextField tagValueTextField;
    private javax.swing.JLabel tallyLabel;
    private javax.swing.JPanel theViewer;
    private javax.swing.JTextField totalPagesTextField;
    private javax.swing.JComboBox typeOfDocCombo;
    private javax.swing.JLabel typeOfDocLabel;
    private javax.swing.JButton vHorizButton;
    private javax.swing.JButton vScreenButton;
    private javax.swing.JButton vTurn90ClockwiseButton;
    private javax.swing.JButton vTurn90CounterwiseButton1;
    private javax.swing.JButton vVertButton;
    private javax.swing.JButton vZoomInButton;
    private javax.swing.JButton vZoomOutButton;
    private javax.swing.JButton vZoomWindowButton;
    private javax.swing.JCheckBox validErrorCheckbox;
    private javax.swing.JLabel validityLabel;
    private javax.swing.JLabel valueLabel;
    private javax.swing.JToolBar viewerToolBar;
    // End of variables declaration//GEN-END:variables
}
