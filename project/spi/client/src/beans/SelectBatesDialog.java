/* $Header: /home/common/cvsarea/ibase/dia/src/beans/SelectBatesDialog.java,v 1.4.8.1 2006/02/22 20:05:51 nancy Exp $ */
package beans;

//import com.lexpar.util.Log;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Called when the absoluteButton is clicked in <code>ui.SplitPaneViewer</code>
 * to allow the user to enter a bates number.
 */
final public class SelectBatesDialog extends SelectDialog {

    private LTextField image = new LTextField(40);
    private javax.swing.JLabel selectLabel = new javax.swing.JLabel("Document Number: ");
    private String bates;

    /**
     * Create an instance of <code>SelectBatesDialog</code>.
     * @param parent the component to use in positioning this dialog
     * @param bates show the bates of the current viewer for editing
     */
    public SelectBatesDialog(Component parent, String bates) {
        super(parent, true); // true to add buttons to dialog

        this.bates = bates;
        setTitle("Select Document");
        addControls();
        pack();
    }

    private void addControls() {
        javax.swing.JPanel otherGroup = new javax.swing.JPanel();
        otherGroup.setLayout(new BorderLayout());
        otherGroup.add(BorderLayout.WEST, selectLabel);
        otherGroup.add(image);
        image.setDocument(new PlainDocument() {

            public void insertString(int offs, String str, AttributeSet a)
                    throws BadLocationException {
                if (str == null) {
                    return;
                }
                okSetEnabled(true);
                super.insertString(offs, str, a);
            }
        });

        add(otherGroup);
        image.setText(bates);
        add(new JLabel(" "));
    }

    /**
     * Return the bates to the user.
     * @return the bates entered by the user or null if Cancel was clicked
     */
    public Object[] getSelection() {
        if (cancelButtonWasSelected || ((String) image.getText()).equals("")) {
            return null;
        }
        Object[] result = new Object[1];
        result[0] = image.getText();
        return result;
    }

    /**
     * Not in use at this time.
     */
    public Object[][] getSelections() {
        return null;
    }

    /**
     * Not in use at this time.
     */
    public ListSelectionModel getSelectionModel() {
        return null;
    }
}
