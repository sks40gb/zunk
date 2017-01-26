/* $Header: /home/common/cvsarea/ibase/dia/src/beans/LGridBag.java,v 1.4.8.1 2006/02/22 20:05:51 nancy Exp $ */
package beans;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.StringTokenizer;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A pane with a <code>GridBagLayout</code>.
 */
public class LGridBag extends JPanel {

    protected String labelDefaultConstraints = null;
    protected GridBagConstraints labelConstraints = new GridBagConstraints();
    static protected HashMap gbConstants = new HashMap(24);
    String lableColorForQueryRaised = "";
    String lableColorForQueryAnswered = "";
    boolean setinvisible = true;

    /**
     * Create a pane with a <code>GridBagLayout</code>.
     */
    public LGridBag() {
        super(new GridBagLayout());

        labelConstraints.ipady = 0;
        labelConstraints.ipadx = 8;
        labelConstraints.fill = GridBagConstraints.BOTH;
        labelConstraints.anchor = GridBagConstraints.WEST;
    }

    /**
     * Add a <code>Component</code> to the pane with <code>GridBagConstraints</code>.
     * @param gridx Specifies the cell containing the leading edge of the
     * component's display area, where the first cell in a row has gridx=0.
     * The leading edge of a component's display area is its left edge for a
     * horizontal, left-to-right container and its right edge for a horizontal,
     * right-to-left container. The value RELATIVE specifies that the component
     * be placed immediately following the component that was added to the container
     * just before this component was added.
     * @param gridy Specifies the cell at the top of the component's display area,
     * where the topmost cell has gridy=0. The value RELATIVE specifies that the
     * component be placed just below the component that was added to the container
     * just before this component was added.
     * @param comp the <code>Component</code> to add to the pane
     * @param clist specification of other values for the new constraints
     * @see LGridBag#gbc
     */
    public void add(int gridx, int gridy, Component comp, String clist) {
        add(comp, gbc(gridx, gridy, clist));
    }

    /**
     * Add a <code>Component</code> to the pane with <code>GridBagConstraints</code>.
     * @param gridx Specifies the cell containing the leading edge of the
     * component's display area, where the first cell in a row has gridx=0.
     * The leading edge of a component's display area is its left edge for a
     * horizontal, left-to-right container and its right edge for a horizontal,
     * right-to-left container. The value RELATIVE specifies that the component
     * be placed immediately following the component that was added to the container
     * just before this component was added.
     * @param gridy Specifies the cell at the top of the component's display area,
     * where the topmost cell has gridy=0. The value RELATIVE specifies that the
     * component be placed just below the component that was added to the container
     * just before this component was added.
     * @param comp the <code>Component</code> to add to the pane
     * @see LGridBag#gbc
     */
    public void add(int gridx, int gridy, Component comp) {
        add(gridx, gridy, comp, null);
    }

    /**
     * Add a <code>Component</code> with a label to the pane with
     * <code>GridBagConstraints</code>.
     * @param gridx Specifies the cell containing the leading edge of the
     * component's display area, where the first cell in a row has gridx=0.
     * The leading edge of a component's display area is its left edge for a
     * horizontal, left-to-right container and its right edge for a horizontal,
     * right-to-left container. The value RELATIVE specifies that the component
     * be placed immediately following the component that was added to the container
     * just before this component was added.
     * @param gridy Specifies the cell at the top of the component's display area,
     * where the topmost cell has gridy=0. The value RELATIVE specifies that the
     * component be placed just below the component that was added to the container
     * just before this component was added.
     * @param label <code>String</code> to use in creating a label for <code>comp</code>
     * @param comp the <code>Component</code> to add to the pane
     * @param clist specification of other values for the new constraints
     * @see LGridBag#gbc
     */
    public void add(int gridx, int gridy, String label, Component comp, String clist) {
        labelConstraints.gridx = gridx;
        labelConstraints.gridy = gridy;
        add(new JLabel(label), labelConstraints);
        add(comp, gbc(gridx + 1, gridy, clist));
    }

    /**
     * Add a <code>Component</code> with a label to the pane with
     * <code>GridBagConstraints</code>.
     * @param gridx Specifies the cell containing the leading edge of the
     * component's display area, where the first cell in a row has gridx=0.
     * The leading edge of a component's display area is its left edge for a
     * horizontal, left-to-right container and its right edge for a horizontal,
     * right-to-left container. The value RELATIVE specifies that the component
     * be placed immediately following the component that was added to the container
     * just before this component was added.
     * @param gridy Specifies the cell at the top of the component's display area,
     * where the topmost cell has gridy=0. The value RELATIVE specifies that the
     * component be placed just below the component that was added to the container
     * just before this component was added.
     * @param label a label for <code>comp</code>
     * @param comp the <code>Component</code> to add to the pane
     * @param clist specification of other values for the new constraints
     * @see LGridBag#gbc
     */
    public void add(int gridx, int gridy, javax.swing.JLabel label, Component comp, String clist) {
        labelConstraints.gridx = gridx;
        labelConstraints.gridy = gridy;
        add(label, labelConstraints);
        add(comp, gbc(gridx + 1, gridy, clist));
    }

    /**
     * Add a <code>Component</code> with a label to the pane with
     * <code>GridBagConstraints</code>.
     * @param gridx Specifies the cell containing the leading edge of the
     * component's display area, where the first cell in a row has gridx=0.
     * The leading edge of a component's display area is its left edge for a
     * horizontal, left-to-right container and its right edge for a horizontal,
     * right-to-left container. The value RELATIVE specifies that the component
     * be placed immediately following the component that was added to the container
     * just before this component was added.
     * @param gridy Specifies the cell at the top of the component's display area,
     * where the topmost cell has gridy=0. The value RELATIVE specifies that the
     * component be placed just below the component that was added to the container
     * just before this component was added.
     * @param label <code>String</code> to use in creating a label for <code>comp</code>
     * @param comp the <code>Component</code> to add to the pane
     * @see LGridBag#gbc
     */
    public void add(int gridx, int gridy, String label, Component comp) {
        add(gridx, gridy, label, comp, null);
    }

    /**
     * Add a <code>Component</code> with a label to the pane with
     * <code>GridBagConstraints</code>.
     * @param gridx Specifies the cell containing the leading edge of the
     * component's display area, where the first cell in a row has gridx=0.
     * The leading edge of a component's display area is its left edge for a
     * horizontal, left-to-right container and its right edge for a horizontal,
     * right-to-left container. The value RELATIVE specifies that the component
     * be placed immediately following the component that was added to the container
     * just before this component was added.
     * @param gridy Specifies the cell at the top of the component's display area,
     * where the topmost cell has gridy=0. The value RELATIVE specifies that the
     * component be placed just below the component that was added to the container
     * just before this component was added.
     * @param label a label for <code>comp</code>
     * @param comp the <code>Component</code> to add to the pane
     * @see LGridBag#gbc
     */
    public void add(int gridx, int gridy, javax.swing.JLabel label, Component comp) {
        add(gridx, gridy, label, comp, null);
    }

    /**
     * Add two <code>Component</code>s with labels to the pane with
     * <code>GridBagConstraints</code>.
     * @param gridx Specifies the cell containing the leading edge of the
     * component's display area, where the first cell in a row has gridx=0.
     * The leading edge of a component's display area is its left edge for a
     * horizontal, left-to-right container and its right edge for a horizontal,
     * right-to-left container. The value RELATIVE specifies that the component
     * be placed immediately following the component that was added to the container
     * just before this component was added.
     * @param gridy Specifies the cell at the top of the component's display area,
     * where the topmost cell has gridy=0. The value RELATIVE specifies that the
     * component be placed just below the component that was added to the container
     * just before this component was added.
     * @param label1 <code>String</code> to use in creating a label for <code>comp1</code>
     * @param label2 <code>String</code> to use in creating a label for <code>comp2</code>
     * @param comp1 a <code>Component</code> to add to the pane
     * @param comp2 a <code>Component</code> to add to the pane
     * @param clist specification of other values for the new constraints
     * @see LGridBag#gbc
     */
    public void add(int gridx, int gridy, String label1, Component comp1, String clist,
            String label2, Component comp2) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel label = new JLabel(label2);
        label.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        panel.add(comp1);
        panel.add(label);
        panel.add(comp2);
        add(gridx, gridy, label1, panel, clist);
    }

    /**
     * Add two <code>Component</code>s with labels to the pane with
     * <code>GridBagConstraints</code>.
     * @param gridx Specifies the cell containing the leading edge of the
     * component's display area, where the first cell in a row has gridx=0.
     * The leading edge of a component's display area is its left edge for a
     * horizontal, left-to-right container and its right edge for a horizontal,
     * right-to-left container. The value RELATIVE specifies that the component
     * be placed immediately following the component that was added to the container
     * just before this component was added.
     * @param gridy Specifies the cell at the top of the component's display area,
     * where the topmost cell has gridy=0. The value RELATIVE specifies that the
     * component be placed just below the component that was added to the container
     * just before this component was added.
     * @param label1 a label for <code>comp1</code>
     * @param label2 <code>String</code> to use in creating a label for <code>comp2</code>
     * @param comp1 a <code>Component</code> to add to the pane
     * @param comp2 a <code>Component</code> to add to the pane
     * @param clist specification of other values for the new constraints
     * @see LGridBag#gbc
     */
    public void add(int gridx, int gridy, javax.swing.JLabel label1, Component comp1, String clist,
            String label2, Component comp2) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        JLabel label = new JLabel(label2);
        label.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        panel.add(comp1);
        panel.add(label);
        panel.add(comp2);
        add(gridx, gridy, label1, panel, clist);
    }

    /**
     * Add two <code>Component</code>s with labels to the pane with
     * <code>GridBagConstraints</code>.
     * @param gridx Specifies the cell containing the leading edge of the
     * component's display area, where the first cell in a row has gridx=0.
     * The leading edge of a component's display area is its left edge for a
     * horizontal, left-to-right container and its right edge for a horizontal,
     * right-to-left container. The value RELATIVE specifies that the component
     * be placed immediately following the component that was added to the container
     * just before this component was added.
     * @param gridy Specifies the cell at the top of the component's display area,
     * where the topmost cell has gridy=0. The value RELATIVE specifies that the
     * component be placed just below the component that was added to the container
     * just before this component was added.
     * @param label1 a label for <code>comp1</code>
     * @param label2 a label for <code>comp2</code>
     * @param comp1 a <code>Component</code> to add to the pane
     * @param comp2 a <code>Component</code> to add to the pane
     * @param clist specification of other values for the new constraints
     * @see LGridBag#gbc
     */
    public void add(int gridx, int gridy, javax.swing.JLabel label1, Component comp1, String clist,
            javax.swing.JLabel label2, Component comp2) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(comp1);
        panel.add(label2);
        panel.add(comp2);
        add(gridx, gridy, label1, panel, clist);
    }

    /**
     * Add three <code>Component</code>s to the pane with <code>GridBagConstraints</code>.
     * @param gridx Specifies the cell containing the leading edge of the
     * component's display area, where the first cell in a row has gridx=0.
     * The leading edge of a component's display area is its left edge for a
     * horizontal, left-to-right container and its right edge for a horizontal,
     * right-to-left container. The value RELATIVE specifies that the component
     * be placed immediately following the component that was added to the container
     * just before this component was added.
     * @param gridy Specifies the cell at the top of the component's display area,
     * where the topmost cell has gridy=0. The value RELATIVE specifies that the
     * component be placed just below the component that was added to the container
     * just before this component was added.
     * @param comp1 a <code>Component</code> to add to the pane
     * @param comp2 a <code>Component</code> to add to the pane
     * @param comp3 a <code>Component</code> to add to the pane
     * @param clist specification of other values for the new constraints
     * @see LGridBag#gbc
     */
    public void add(int gridx, int gridy, Component comp1, Component comp2,
            Component comp3, String clist) {
        JLabel label1 = new JLabel("");
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(comp1);
        panel.add(comp2);
        panel.add(comp3);
        add(gridx, gridy, label1, panel, clist);
    }

    /**
     * Add two <code>Component</code>s with labels to the pane with
     * <code>GridBagConstraints</code>.
     * @param gridx Specifies the cell containing the leading edge of the
     * component's display area, where the first cell in a row has gridx=0.
     * The leading edge of a component's display area is its left edge for a
     * horizontal, left-to-right container and its right edge for a horizontal,
     * right-to-left container. The value RELATIVE specifies that the component
     * be placed immediately following the component that was added to the container
     * just before this component was added.
     * @param gridy Specifies the cell at the top of the component's display area,
     * where the topmost cell has gridy=0. The value RELATIVE specifies that the
     * component be placed just below the component that was added to the container
     * just before this component was added.
     * @param label1 a label for <code>comp1</code>
     * @param label2 a label for <code>comp2</code>
     * @param comp1 a <code>Component</code> to add to the pane
     * @param comp2 a <code>Component</code> to add to the pane
     * @see LGridBag#gbc
     */
    public void add(int gridx, int gridy, String label1, Component comp1,
            String label2, Component comp2) {
        add(gridx, gridy, label1, comp1, null, label2, comp2);
    }

    /**
     * Create a new GridBagConstraints with given values replacing defaults.
     * @param gridx The gridx value for the new constraints
     * @param gridy The gridy value for the new constraints
     * @param clist Specification of other values for the new constraints.
     *        Specifications are given as a comma-separated list
     *        of key=value pairs.  Keys are as follows:
     * <table border="1">
     * <th>    key        <th>GridBagConstraints field  <th>values            <tb>
     * <tr><td>w          <td>gridwidth                 <td>integer, REL, REM </tr>
     * <tr><td>h          <td>gridheight                <td>integer, REL, REM </tr>
     * <tr><td>wx         <td>weightx                   <td>percent           </tr>
     * <tr><td>wy         <td>weighty                   <td>percent           </tr>
     * <tr><td>a          <td>anchor                    <td>C,N,NE,E,SE,...   </tr>
     * <tr><td>f          <td>fill                      <td>NO,H,V,B          </tr>
     * <tr><td>it         <td>ginsets.top               <td>integer           </tr>
     * <tr><td>il         <td>ginsets.left              <td>integer           </tr>
     * <tr><td>ib         <td>ginsets.bottom            <td>integer           </tr>
     * <tr><td>ir         <td>ginsets.right             <td>integer           </tr>
     * <tr><td>px         <td>ipadx                     <td>integer           </tr>
     * <tr><td>py         <td>ipady                     <td>integer           </tr>
     * </table>
     */
    protected GridBagConstraints gbc(int gridx, int gridy, String clist) {
        GridBagConstraints result = new GridBagConstraints();
        result.gridx = gridx;
        result.gridy = gridy;
        result.anchor = GridBagConstraints.WEST;
        boolean errorSeen = false;
        if (clist != null) {
            StringTokenizer st = new StringTokenizer(clist, ",", false);
            while (st.hasMoreTokens()) {
                String assignment = st.nextToken();

                // assignment should be 'fieldname=value'
                int equals = assignment.indexOf('=');
                if (equals < 0) {
                    if (assignment.trim().length() > 0) {
                        errorSeen = true;
                        break;
                    }
                } else {

                    // extract the value
                    int intValue;
                    String item = assignment.substring(0, equals).trim();
                    String value = assignment.substring(equals + 1).trim();
                    if (value.length() > 0 && value.charAt(0) >= '0' && value.charAt(0) <= '9') {
                        try {
                            intValue = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            errorSeen = true;
                            break;
                        }
                    } else {
                        Integer mapValue = (Integer) gbConstants.get(value);
                        if (mapValue == null) {
                            errorSeen = true;
                            break;
                        }
                        intValue = mapValue.intValue();
                    }

                    // store in correct field
                    if (item.equals("w")) {
                        result.gridwidth = intValue;
                    } else if (item.equals("h")) {
                        result.gridheight = intValue;
                    } else if (item.equals("wx")) {
                        result.weightx = intValue * .01;
                    } else if (item.equals("wy")) {
                        result.weighty = intValue * .01;
                    } else if (item.equals("a")) {
                        result.anchor = intValue;
                    } else if (item.equals("f")) {
                        result.fill = intValue;
                    } else if (item.equals("it")) {
                        result.insets.top = intValue;
                    } else if (item.equals("il")) {
                        result.insets.left = intValue;
                    } else if (item.equals("ib")) {
                        result.insets.bottom = intValue;
                    } else if (item.equals("ir")) {
                        result.insets.right = intValue;
                    } else if (item.equals("px")) {
                        result.ipadx = intValue;
                    } else if (item.equals("py")) {
                        result.ipady = intValue;
                    } else {
                        errorSeen = true;
                        break;
                    }
                }
            }
            if (errorSeen) {
                throw new Error("LGridBag.gbc: invalid argument: " + clist);
            }
        }
        return result;
    }
    

    static {
        gbConstants.put("REL", new Integer(GridBagConstraints.RELATIVE));
        gbConstants.put("REM", new Integer(GridBagConstraints.REMAINDER));
        gbConstants.put("NO", new Integer(GridBagConstraints.NONE));
        gbConstants.put("B", new Integer(GridBagConstraints.BOTH));
        gbConstants.put("H", new Integer(GridBagConstraints.HORIZONTAL));
        gbConstants.put("V", new Integer(GridBagConstraints.VERTICAL));
        gbConstants.put("C", new Integer(GridBagConstraints.CENTER));
        gbConstants.put("N", new Integer(GridBagConstraints.NORTH));
        gbConstants.put("NE", new Integer(GridBagConstraints.NORTHEAST));
        gbConstants.put("E", new Integer(GridBagConstraints.EAST));
        gbConstants.put("SE", new Integer(GridBagConstraints.SOUTHEAST));
        gbConstants.put("S", new Integer(GridBagConstraints.SOUTH));
        gbConstants.put("SW", new Integer(GridBagConstraints.SOUTHWEST));
        gbConstants.put("W", new Integer(GridBagConstraints.WEST));
        gbConstants.put("NW", new Integer(GridBagConstraints.NORTHWEST));
    }

    public void setLableColorForQueryRaised(String color) {
        this.lableColorForQueryRaised = color;
    }

    public String getLableColorForQueryRaised() {
        return lableColorForQueryRaised;
    }

    public void setLableColorForQueryAnswered(String color) {
        this.lableColorForQueryAnswered = color;
    }

    public String getLableColorForQueryAnswered() {
        return lableColorForQueryAnswered;
    }
    //Added for F10

    public void setVisibleQueryTracker(boolean color) {
        this.setinvisible = color;
    }

    public boolean getVisibleQueryTracker() {
        return setinvisible;
    }
}
