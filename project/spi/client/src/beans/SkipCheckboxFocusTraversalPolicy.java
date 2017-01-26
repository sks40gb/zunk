/* $Header: /home/common/cvsarea/ibase/dia/src/beans/SkipCheckboxFocusTraversalPolicy.java,v 1.2.8.1 2006/02/22 20:05:51 nancy Exp $ */
package beans;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JCheckBox;
import javax.swing.LayoutFocusTraversalPolicy;

/**
 * A FocusTraversalPolicy that traverses in layout order, skipping JCheckBox components
 * in the forward direction.
 * JCheckBox components are assumed not to be adjacent, and there is assumed
 * to be at least one component other than a JCheckBox
 */
public class SkipCheckboxFocusTraversalPolicy extends LayoutFocusTraversalPolicy {

    /**
     * Returns the Component that should receive the focus after
     * aComponent; focusCycleRoot must be a focus cycle root of aComponent.
     * @param focusCycleRoot a focus cycle root of aComponent
     * @param aComponent a (possibly indirect) child of focusCycleRoot,
     * or focusCycleRoot itself
     * @return the Component that should receive the focus after aComponent,
     * skipping JCheckBoxes, or null if no suitable Component can be found
     */
    public Component getComponentAfter(Container focusCycleRoot,
            Component aComponent) {
        Component result = super.getComponentAfter(focusCycleRoot, aComponent);
        if (result instanceof JCheckBox) {
            result = super.getComponentAfter(focusCycleRoot, result);
        }
        return result;
    }

    /**
     * Returns the first Component in the traversal cycle. This method
     * is used to determine the next Component to focus when traversal
     * wraps in the forward direction.
     * @param focusCycleRoot the focus cycle root whose first Component
     * is to be returned
     * @return the first Component in the traversal cycle when focusCycleRoot
     * is the focus cycle root, skipping JCheckBoxes, or null if no suitable
     * Component can be found
     */
    public Component getFirstComponent(Container focusCycleRoot) {
        Component result = super.getFirstComponent(focusCycleRoot);
        if (result instanceof JCheckBox) {
            result = super.getComponentAfter(focusCycleRoot, result);
        }
        return result;
    }
}
