/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import client.ClientTask;
import client.TaskExecuteQuery;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Dialog for the verifying the coded values. It is called in different custome
 * components like  
 *               <code>lbaseTextField</code>
 *               <code>LFormattedTextField</code>
 *               <code>LTextField</code>
 *               <code>LTextButton</code>
 * @see beans.lbaseTextField
 * @see beans.LFormattedTextField
 * @see beans.LTextField
 * @see beans.LTextButton
 * @author bmurali
 */
public class ShowVerifyDialog extends JDialog {

    private final String SPACE_1 = " ";
    private final String SPACE_2 = "  ";
    private Map valueMap;
    private JPanel mainPanel = new javax.swing.JPanel();
    private LGridBag namePane = new LGridBag();
    private JTextField codedValue;
    private JTextField suggestedValue;
    private JTextField checkedValue;
    private JButton codedButton = new JButton("Coded");
    private JButton SuggestedButton = new JButton("Suggested");
    private JButton checkedButton = new JButton("Checked");
    private JButton closeButton = new JButton("Close");
    private String Suggested;
    private String fieldName;
    private String value;
    private int childId;
    private String whichStatus;
    private String typeOfComponent;
    private Component parent;

    /**
     * Create an instance of ShowVerifyDialog
     * @param parent           Parent window 
     * @param suggestedValue   Suggested value
     * @param valueMap         Map of coded value
     * @param fieldName        Field name
     * @param childId          Child Id, can be achieved from child.child_id from child.
     * @param whichStatus      Process like Listing, Tally, Qa, TAllQc etc..
     * @param typeOfComponent
     */
    public ShowVerifyDialog(Component parent, String suggestedValue, Map valueMap, String fieldName, int childId, String whichStatus, String typeOfComponent) {
        super(JOptionPane.getFrameForComponent(parent));
        this.parent = parent;
        this.Suggested = suggestedValue;
        this.valueMap = valueMap;
        this.fieldName = fieldName;
        this.typeOfComponent = typeOfComponent;
        this.childId = childId;
        this.whichStatus = whichStatus;

        getContentPane().add(mainPanel, BorderLayout.CENTER);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));
        codedButton.addActionListener(buttonComboListener);
        checkedButton.addActionListener(buttonComboListener);
        SuggestedButton.addActionListener(buttonComboListener);
        closeButton.addActionListener(buttonComboListener);
        JPanel outerButtonPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 30, 20));
        buttonPanel.add(codedButton);
        if (whichStatus.equals("Masking")) {
            buttonPanel.add(checkedButton);
        }
        buttonPanel.add(SuggestedButton);
        buttonPanel.add(closeButton);
        outerButtonPanel.add(buttonPanel);
        mainPanel.add(outerButtonPanel, BorderLayout.SOUTH);
        setLocationRelativeTo(parent);
        setTitle("iBase Message");
        getCoderData();
    }

    /**
     * Get the coder data for a field.
     */
    private void getCoderData() {

        String[] parameters = new String[2];
        parameters[0] = Integer.toString(childId);
        parameters[1] = fieldName;
        final ClientTask task;
        task = new TaskExecuteQuery("get_coded_data", parameters);
        task.setCallback(new Runnable() {

            public void run() {
                geCheckedValue((ResultSet) task.getResult());
            }
        });
        boolean ok = task.enqueue(this);

        addControls();
    }

    /**
     * Get the checked value and display to <code>checkedValue</code>.
     * @param queryResult ResultSet contains coded value.
     */
    private void geCheckedValue(ResultSet queryResult) {
        value = "";
        if (valueMap != null) {
            value = (String) valueMap.get(fieldName);
        }

        if (queryResult != null) {
            try {
                if (queryResult.next()) {
                    // updating a user   
                    final String coded_value = queryResult.getString(1);
                    codedValue.setText(coded_value);
                    codedValue.setEnabled(false);
                    suggestedValue.setEnabled(false);
                    suggestedValue.setText(Suggested);
                    // if the status or process is Masking
                    // get the child id and fieldname
                    // Pass this value to checked_data query to get the checked value.                    
                    if (whichStatus.equals("Masking")) {
                        String[] parameters = new String[2];
                        parameters[0] = Integer.toString(childId);
                        parameters[1] = fieldName;
                        final ClientTask checkerData;
                        checkerData = new TaskExecuteQuery("get_checked_data", parameters);
                        checkerData.setCallback(new Runnable() {

                            public void run() {
                                try {
                                    ResultSet rs = (ResultSet) checkerData.getResult();
                                    if(rs.next()){
                                    checkedValue.setEnabled(false);
                                    checkedValue.setText(rs.getString(1));
                                    }else{
                                       checkedValue.setEnabled(false);
                                       checkedValue.setText(coded_value);
                                    }
                                } catch (SQLException ex) {
                                    Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        });
                        boolean ok = checkerData.enqueue(this);

                    }
                } else {
                    codedValue.setText(value);
                    codedValue.setEnabled(false);
                    suggestedValue.setText(Suggested);
                    suggestedValue.setEnabled(false);
                }
            } catch (SQLException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void addControls() {

        codedValue = new JTextField(30);
        codedValue.setEnabled(false);
        namePane.add(0, 0, SPACE_2, new JLabel(SPACE_1));
        namePane.add(0, 1, "Coded Value:", codedValue);
        namePane.add(0, 2, SPACE_2, new JLabel(SPACE_1));
        namePane.add(0, 4, SPACE_2, new JLabel(SPACE_1));
        namePane.add(0, 6, SPACE_2, new JLabel(SPACE_1));
        suggestedValue = new JTextField(30);
        suggestedValue.setEnabled(false);
        namePane.add(0, 5, "Suggested Value:", suggestedValue);

        if (whichStatus.equals("Masking")) {
            checkedValue = new JTextField(30);
            checkedValue.setEnabled(false);
            namePane.add(0, 3, "Checked Value:", checkedValue);
       }
        mainPanel.add(namePane, BorderLayout.CENTER);
        pack();
    }
    
    private ActionListener buttonComboListener = new ActionListener() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {

                Object source = A.getSource();
                if (source == codedButton) {
                    if (typeOfComponent.equals("LTextField")) {
                        LTextField field = (LTextField) parent;
                        field.setText(codedValue.getText());
                    } else if (typeOfComponent.equals("IbaseTextField")) {
                        IbaseTextField field = (IbaseTextField) parent;
                        field.setText(codedValue.getText());
                    } else if (typeOfComponent.equals("LFormattedTextField")) {
                        LFormattedTextField field = (LFormattedTextField) parent;
                        field.setText(codedValue.getText());
                    } else if (typeOfComponent.equals("LTextButton")) {
                        LTextButton field = (LTextButton) parent;
                        field.setText(codedValue.getText());
                    }

                    dispose();
                } else if (source == SuggestedButton) {
                    if (typeOfComponent.equals("LTextField")) {
                        LTextField field = (LTextField) parent;
                        field.setText(suggestedValue.getText());
                    } else if (typeOfComponent.equals("LFormattedTextField")) {
                        LFormattedTextField field = (LFormattedTextField) parent;
                        field.setText(suggestedValue.getText());
                    } else if (typeOfComponent.equals("LTextButton")) {
                        LTextButton field = (LTextButton) parent;
                        field.setText(suggestedValue.getText());
                    } else if (typeOfComponent.equals("IbaseTextField")) {
                        IbaseTextField field = (IbaseTextField) parent;
                        field.setText(suggestedValue.getText());
                    }
                    dispose();
                } else if (source == checkedButton) {
                    if (typeOfComponent.equals("LTextField")) {
                        LTextField field = (LTextField) parent;
                        field.setText(checkedValue.getText());
                    } else if (typeOfComponent.equals("LFormattedTextField")) {
                        LFormattedTextField field = (LFormattedTextField) parent;
                        field.setText(checkedValue.getText());
                    } else if (typeOfComponent.equals("LTextButton")) {
                        LTextButton field = (LTextButton) parent;
                        field.setText(checkedValue.getText());
                    } else if (typeOfComponent.equals("IbaseTextField")) {
                        IbaseTextField field = (IbaseTextField) parent;
                        field.setText(checkedValue.getText());
                    }
                    dispose();
                } else if (source == closeButton) {
                    dispose();
                }
            } catch (Exception e) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, e);
            }
        }
    };
}
