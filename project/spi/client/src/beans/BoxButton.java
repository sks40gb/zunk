/* $Header: /home/common/cvsarea/ibase/dia/src/beans/BoxButton.java,v 1.3.8.1 2006/02/22 20:05:50 nancy Exp $ */
package beans;

import com.lexpar.util.Log;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import javax.swing.UIManager;

/**
 * Override of BasicArrowButton to add useful icons for <code>beans.AddEditIssue</code>
 * and <code>ui.ProjectAdminPage</code>.
 */
public class BoxButton extends javax.swing.plaf.basic.BasicArrowButton {

    /** <code>direction</code> 101 */
    final static public int PLUS = 101;
    /** <code>direction</code> 102 */
    final static public int MINUS = 102;
    /** <code>direction</code> 103 */
    final static public int UP = 103;
    /** <code>direction</code> 104 */
    final static public int DOWN = 104;
    /** <code>direction</code> 105 */
    final static public int SELECT = 105;
    /** <code>direction</code> 106 */
    final static public int REMOVE = 106;
    /** <code>direction</code> 107 */
    final static public int ADD = 107;
    /** <code>direction</code> 108 */
    final static public int BROWSE = 108;
    /** <code>direction</code> 109 */
    final static public int DELETE = 109;
    /** <code>direction</code> 110 */
    final static public int UNDELETE = 110;
    /** <code>direction</code> 111 */
    final static public int CHANGE = 111;
    /** <code>FocusListener</code> for this button */
    FocusText focusText = new FocusText();

    /**
     * Create a <code>BoxButton</code> for the given direction.
     * @param direction one of the defined <code>direction</code> fields
     */
    public BoxButton(int direction) {
        super(direction);
        addFocusListener(focusText);
    }

    /**
     * The listener interface for receiving keyboard focus events
     * on a component.
     */
    public class FocusText implements FocusListener {

        /**
         * Invoked when a component gains the keyboard focus.
         */
        public void focusGained(FocusEvent e) {
            try {
                //AbstractPage.getStatusString().setText("");
                //Log.print("is focused " + getSelectedIcon());
                setFocusPainted(true);
                setSelected(true);
                setBorderPainted(true);
            } catch (Throwable t) {
                Log.quit(t);
            }
        }

        /**
         * Invoked when a component loses the keyboard focus.
         */
        public void focusLost(FocusEvent e) {
            try {
                //Log.print("not focused " + getSelectedIcon());
                setFocusPainted(false);
                setSelected(false);
                setBorderPainted(false);
            } catch (Throwable t) {
                Log.quit(t);
            }
        }
    }

    /**
     * Returns whether this Component can become the focus owner.
     * @return true if this Component is focusable; false otherwise
     */
    public boolean isFocusTraversable() {
        return true;
    }

    final public void paintTriangle(Graphics g, int x, int y, int size,
            int direction, boolean isEnabled) {
        Rectangle rect = new Rectangle();
        final Color oldColor = g.getColor();

        g.translate(8, 8);
        if (isEnabled) {
            g.setColor(UIManager.getColor("controlDkShadow"));
        } else {
            g.setColor(UIManager.getColor("controlShadow"));
        }

        switch (direction) {
            case PLUS:
                setSize(new Dimension(16, 16));
                g.fillRect(-1, -4, 3, 9);
                g.fillRect(-4, -1, 9, 3);
                break;
            case MINUS:
                setSize(new Dimension(16, 16));
                g.fillRect(-4, -1, 9, 3);
                break;
            case UP:
                setSize(new Dimension(16, 16));
                for (int i = 0; i < 5; i++) {
                    g.drawLine(-i, i - 4, -i, i - 1);
                    g.drawLine(i, i - 4, i, i - 1);
                }
                break;
            case DOWN:
                setSize(new Dimension(16, 16));
                for (int i = 0; i < 5; i++) {
                    g.drawLine(-i, 1 - i, -i, 4 - i);
                    g.drawLine(i, 1 - i, i, 4 - i);
                }
                break;
            case SELECT:
                setSize(new Dimension(48, 16)); // larger button

                g.setFont(new Font("Dialog", Font.BOLD, 9));
                g.drawString(" Select", 0, 3);
                if (isSelected()) {
                    rect = g.getClipBounds();
                    if (rect != null) {
                        //Log.print("draw rectangle " + rect);
                        g.drawRect(-2, -6, 35, rect.height - 6);
                    }
                }
                break;
            case REMOVE:
                setSize(new Dimension(48, 16)); // larger button

                g.setFont(new Font("Dialog", Font.BOLD, 9));
                g.drawString("Remove", -1, 3);
                if (isSelected()) {
                    rect = g.getClipBounds();
                    if (rect != null) {
                        //Log.print("draw rectangle " + rect);
                        g.drawRect(-2, -6, 38, rect.height - 6);
                    }
                }
                break;
            case ADD:
                setSize(new Dimension(48, 16)); // larger button

                g.setFont(new Font("Dialog", Font.BOLD, 9));
                g.drawString("  Add", 1, 3);
                if (isSelected()) {
                    rect = g.getClipBounds();
                    if (rect != null) {
                        //Log.print("draw rectangle " + rect);
                        g.drawRect(-2, -6, 36, rect.height - 6);
                    }
                }
                break;
            case BROWSE:
                setSize(new Dimension(48, 16)); // larger button

                g.setFont(new Font("Dialog", Font.BOLD, 9));
                g.drawString("Browse", 0, 3);
                if (isSelected()) {
                    rect = g.getClipBounds();
                    if (rect != null) {
                        //Log.print("draw rectangle " + rect);
                        g.drawRect(-2, -6, 36, rect.height - 6);
                    }
                }
                break;
            case DELETE:
                setSize(new Dimension(48, 16)); // larger button

                g.setFont(new Font("Dialog", Font.BOLD, 9));
                g.drawString("Delete", 0, 3);
                if (isSelected()) {
                    rect = g.getClipBounds();
                    if (rect != null) {
                        //Log.print("draw rectangle " + rect);
                        g.drawRect(-2, -6, 36, rect.height - 6);
                    }
                }
                break;
            case UNDELETE:
                setSize(new Dimension(48, 16)); // larger button

                g.setFont(new Font("Dialog", Font.BOLD, 9));
                g.drawString("Undelete", 0, 3);
                if (isSelected()) {
                    rect = g.getClipBounds();
                    if (rect != null) {
                        //Log.print("draw rectangle " + rect);
                        g.drawRect(-2, -6, 36, rect.height - 6);
                    }
                }
                break;
            case CHANGE:
                setSize(new Dimension(48, 16)); // larger button

                g.setFont(new Font("Dialog", Font.BOLD, 9));
                g.drawString("Change", 0, 3);
                if (isSelected()) {
                    rect = g.getClipBounds();
                    if (rect != null) {
                        //Log.print("draw rectangle " + rect);
                        g.drawRect(-2, -6, 36, rect.height - 6);
                    }
                }
                break;
            default:
                Log.quit("BoxButton: invalid direction: " + direction);
        }
        g.translate(-8, -8);
        g.setColor(oldColor);
    }
}

