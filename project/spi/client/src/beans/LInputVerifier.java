/* $Header: /home/common/cvsarea/ibase/dia/src/beans/LInputVerifier.java,v 1.2.10.1 2006/02/22 20:05:51 nancy Exp $ */
package beans;

import javax.swing.InputVerifier;
import javax.swing.JComponent;
import com.lexpar.util.Log;

/**
 * Replacement for javax.swing.InputVerifier, workaround for Java bug # 4546591.
 * Handles problem that in Java 1.4, shouldYieldFocus() does not properly allow 
 * side effects.
 */
public abstract class LInputVerifier extends InputVerifier {

    ///**
    //* Checks whether the JComponent's input is valid. This method should
    //* have no side effects. It returns a boolean indicating the status
    //* of the argument's input.
    //*
    //* @param input the JComponent to verify
    //* @return <code>true</code> when valid, <code>false</code> when invalid
    //* @see JComponent#setLInputVerifier
    //* @see JComponent#getLInputVerifier
    //*
    //*/
    //inherited from InputVerifier
    //public abstract boolean verify(JComponent input);
    /**
     * Calls <code>verify(input)</code> to ensure that the input is valid.
     * This method can have side effects. In particular, this method
     * is called when the user attempts to advance focus out of the
     * argument component into another Swing component in this window.
     * If this method returns <code>true</code>, then the focus is transfered
     * normally; if it returns <code>false</code>, then the focus remains in
     * the argument component.
     *
     * @param input the JComponent to verify
     * @return <code>true</code> when valid, <code>false</code> when invalid
     * @see JComponent#setInputVerifier
     * @see JComponent#getInputVerifier
     *
     */
    final public boolean shouldYieldFocus(JComponent input) {

        // According to the documentation should yield focus is allowed to cause
        // side effects.  So temporarily remove the input verifier on the text
        // field.
        input.setInputVerifier(null);
        Log.write("Removed input verifier " + this.getClass());

        // Call the "real" shouldYieldFocus
        boolean result = this.lShouldYieldFocus(input);

        // Reinstall the input verifier.
        input.setInputVerifier(this);
        Log.write("Reinstalled input verifier");

        // return the result form the "real" shouldYieldFocus.
        return result;
    }

    /**
     * Must be overridden in the subclasses, this calls <code>verify(input)</code>
     * to ensure that the input is valid.
     * @param input the <code>JComponent</code> upon which this verifier is set
     * @return true if the input is valid; otherwise false
     */
    public boolean lShouldYieldFocus(JComponent input) {
        return this.verify(input);
    }
}
