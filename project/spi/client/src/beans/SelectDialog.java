/* $Header: /home/common/cvsarea/ibase/dia/src/beans/SelectDialog.java,v 1.3.10.1 2006/02/22 20:05:51 nancy Exp $ */
package beans;

import com.lexpar.util.Log;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

/**
 * Basic JDialog with ok and cancel buttons that can be extended
 * with components.
 */
public abstract class SelectDialog extends javax.swing.JDialog {

    private JPanel selectPanel = new javax.swing.JPanel();
    public JButton okButton = new JButton("OK");
    private JButton cancelButton = new JButton("Cancel");
    protected boolean cancelButtonWasSelected = false;

    /**
     * Create an instance of a JDialog.
     * @param parent the component to use in positioning this dialog
     * @param addButtons true to show the ok and cancel button to the dialog
     */
    public SelectDialog(Component parent, boolean addButtons) {
        super(JOptionPane.getFrameForComponent(parent));

        getContentPane().add(selectPanel, BorderLayout.CENTER);

        selectPanel.setLayout(new BoxLayout(selectPanel, BoxLayout.Y_AXIS));
        selectPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        okButton.addActionListener(buttonListener);
        okButton.setEnabled(false);
        cancelButton.addActionListener(buttonListener);
        JPanel outerButtonPanel = new JPanel(new FlowLayout());
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 30, 20));
        if (addButtons) {
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            outerButtonPanel.add(buttonPanel);
            selectPanel.add(outerButtonPanel, BorderLayout.SOUTH);
        }

        setLocationRelativeTo(parent);
    }

    /**
     * To be called from within the subclass to add the actual controls.
     * Last component is the button panel; add new component before it.
     */
    public void add(JComponent widget) {
        //Log.print("(SelectDialog) add " + widget);
        int componentCount = selectPanel.getComponentCount();
        selectPanel.add(Box.createVerticalStrut(20), componentCount - 1);
        selectPanel.add(widget, componentCount);
    }
    /**
     * ActionListener to close the dialog on ok or cancel.
     */
    protected ActionListener buttonListener = new ActionListener() {

        public void actionPerformed(java.awt.event.ActionEvent A) {
            try {
                Object source = A.getSource();
                if (source == okButton) {
                    setVisible(false);
                } else if (source == cancelButton) {
                    cancelButtonWasSelected = true;
                    setVisible(false);
                }
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    };

    /**
     * Enable or disable the okButton.
     * @param flag true to enable; false to disable
     */
    protected void okSetEnabled(boolean flag) {
        okButton.setEnabled(flag);
        okButton.setSelected(flag);
        //okButton.setDefaultCapable(flag);
        getRootPane().setDefaultButton(okButton);
    }

    /**
     * If the user has entered selectable data, enable the okButton.
     */
    protected void checkOkEnabled() {
        boolean isOk = isSelectionAvailable();
        okButton.setEnabled(isOk);
        okButton.setSelected(isOk);
        //okButton.setDefaultCapable(isOk);
        getRootPane().setDefaultButton(okButton);
    }

    /**
     * May be overridden with more "efficient" version if desired.
     */
    protected boolean isSelectionAvailable() {
        Object[] theSelection = getSelection();
        return (theSelection != null);
    }

    /**
     * Creates an empty border on all sides of selectPanel.
     */
    public void setBorder(int top, int left, int bottom, int right) {
        selectPanel.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
    }

    /**
     * Must be overridden to return data to caller.
     */
    public abstract Object[] getSelection();

    /**
     * Must be overridden to return data to caller.
     */
    public abstract Object[][] getSelections();

    /**
     * Must be overridden to return model to caller.
     */
    public abstract ListSelectionModel getSelectionModel();
}
