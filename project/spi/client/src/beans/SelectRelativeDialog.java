/* $Header: /home/common/cvsarea/ibase/dia/src/beans/SelectRelativeDialog.java,v 1.7.8.1 2006/02/22 20:05:51 nancy Exp $ */
package beans;

//import com.lexpar.util.Log;
import java.awt.BorderLayout;
import java.awt.Component;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;

/**
 * Dialog to allow the user to enter an absolute document number for
 * viewing in <code>ui.SplitPaneViewer</code>.
 */
final public class SelectRelativeDialog extends SelectDialog {

    private LIntegerField relativeImageNumberField = new LIntegerField(6);
    private javax.swing.JLabel selectLabel = new javax.swing.JLabel("Document Number: ");
    private String key;
    private int max;

    /**
     * Create an instance of SelectRelativeDialog.
     * @param parent the component to use in positioning this dialog
     * @param key the current document number
     * @param max the greatest document number that can be viewed in this batch
     */
    public SelectRelativeDialog(Component parent, String key, int max) {
        super(parent, true); // true to add buttons to dialog

        this.key = key;
        this.max = max;
        setTitle("Select Document (1 to " + max + ")");
        addControls();
        okSetEnabled(false);
        pack();
    }

    private void addControls() {
        javax.swing.JPanel otherGroup = new javax.swing.JPanel();
        otherGroup.setLayout(new BorderLayout());
        otherGroup.add(BorderLayout.WEST, selectLabel);
        otherGroup.add(relativeImageNumberField);
        relativeImageNumberField.addPropertyChangeListener("text", new PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                if (relativeImageNumberField.getValue() > 0 && relativeImageNumberField.getValue() <= max) {
                    okSetEnabled(true);
                } else {
                    okSetEnabled(false);
                }
            }
        });

        add(otherGroup);
        relativeImageNumberField.setText(key);
        add(new JLabel(" "));
    }

    public Object[] getSelection() {
        if (cancelButtonWasSelected || ((String) relativeImageNumberField.getText()).equals("")) {
            return null;
        }
        Object[] result = new Object[1];
        result[0] = relativeImageNumberField.getText();
        return result;
    }

    public Object[][] getSelections() {
        return null;
    }

    public ListSelectionModel getSelectionModel() {
        return null;
    }
}
