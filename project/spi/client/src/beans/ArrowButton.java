/* $Header: /home/common/cvsarea/ibase/dia/src/beans/ArrowButton.java,v 1.2 2003/05/16 00:48:18 weaston Exp $ */

/*
 * ArrowButton.java
 *
 * Created on March 20, 2003, 9:31 PM
 */
package beans;

import javax.swing.plaf.basic.BasicArrowButton;

/**
 * Arrow button bean for use in compound components.
 * This is a subclass of Java's BasicArrowButton, with
 * a no-argument constructor so that it can be used
 * as a visual component.
 * @author  bill
 */
public class ArrowButton extends BasicArrowButton {

    /** Creates a new instance of ArrowButton */
    public ArrowButton() {
        super(BasicArrowButton.SOUTH);
    }
}
