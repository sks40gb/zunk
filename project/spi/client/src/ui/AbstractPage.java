/* $Header: /home/common/cvsarea/ibase/dia/src/ui/AbstractPage.java,v 1.3.8.1 2006/02/17 13:40:06 nancy Exp $ */
package ui;

import javax.swing.Action;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
    
    /**
     * This is an abstract class for the pages of the administrative DIA.  It is subclassed
     * by all of the tabs defined for the JTabbedPane defined in AdminFrame.
     * 
     * @see ui.AdminFrame
     */

public abstract class AbstractPage extends JPanel {

    /** The menu bar painted on the AdminFrame.  We save it for possible later use. */
    private JMenuBar defaultJMenuBar;


    /** The AdminFrame that wraps the JTabbedPane and the pages. */
    protected AdminFrame frame;
    
    /**
     * Create an AbstractPage with the given AdminFrame as parent
     * @param frame The AdminFrame that wraps the JTabbedPane and the pages.
     */
    public AbstractPage(){
    }
    public AbstractPage(AdminFrame frame) {
        this.frame = frame;
        this.defaultJMenuBar = frame.getJMenuBar();
    }
    
    /**
     * Close the GUI and exit.  Check first that it's OK to leave.
     */
    protected void exitForm() {
        if (exitPageCheck()) {
            frame.exitForm(null);
        }
    }

    /**
     * Check that it's OK to exit the current page.  Subclasses must override this to provide a
     * page-dependent check.
     * @return true if it's OK to exit.  If user cancels save/no-save/cancel dialog,
     *         false is returned.
     */
    protected abstract boolean exitPageCheck();
    
    /**
     * Get the menu bar for the current page.  Subclasses must override this to provide a
     * page-dependent menu bar.
     */
    protected abstract JMenuBar getPageJMenuBar();
    
    /**
     * Perform page initialization.  Subclasses must override this to provide any
     * required page-dependent initialization.
     */
    protected abstract void tabSelected();
    
    /**
     * Return the default menu bar.  May be used by subclasses if they don't make their own.
     * (Not currently used.)
     */
    protected JMenuBar getDefaultJMenuBar() {
        return defaultJMenuBar;
    }

    /**
     * Convenience call to set Action enabled or disabled.
     * Request ignored if Action a is null.
     * @param a Action to be enabled or disabled
     * @param flag true or false
     */
    protected static void setEnabled(Action a, boolean flag) {
        if (a != null) {
            a.setEnabled(flag);
        }
    }

}

