/* $Header: /home/common/cvsarea/ibase/dia/src/beans/Attic/ToolTipText.java,v 1.1.2.1 2006/03/28 17:02:05 nancy Exp $ */
package beans;

import java.awt.Point;
import java.awt.event.MouseEvent;
import javax.swing.JTable;

/** 
 * Provide the text from a table location for tooltip display.
 */
public class ToolTipText {

    /**
     * This class cannot be instantiated.
     */
    private void ToolTipText() {
    }

    /**
     * Return the value from the location of the user's mouse.
     * @param event an event which indicates that a mouse action
     * occurred in a component
     * @return a String containing the value
     */
    public static String getToolTipText(MouseEvent event, JTable table) {
        int row = table.rowAtPoint(event.getPoint());
        int col = table.columnAtPoint(event.getPoint());
        Object o = table.getValueAt(row, col);
        if (o == null) {
            return null;
        }
        if (o.toString().equals("")) {
            return null;
        }
        return o.toString();
    }

    /**
     * Return the location of the user's mouse.
     * @param event an event which indicates that a mouse action
     * occurred in a component
     * @return a Point containing the location of the mouse
     */
    public static Point getToolTipLocation(MouseEvent event, JTable table) {
        int row = table.rowAtPoint(event.getPoint());
        int col = table.columnAtPoint(event.getPoint());
        Object o = table.getValueAt(row, col);
        if (o == null) {
            return null;
        }
        if (o.toString().equals("")) {
            return null;
        }
        Point pt = table.getCellRect(row, col, true).getLocation();
        pt.translate(-1, -2);
        return pt;
    }
}
