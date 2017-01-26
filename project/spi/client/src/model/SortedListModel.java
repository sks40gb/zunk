/* $Header: /home/common/cvsarea/ibase/dia/src/model/SortedListModel.java,v 1.7.6.1 2005/12/01 18:27:29 nancy Exp $ */

/* 
 * SortedListModel.java
 *
 * Created on March 21, 2003, 10:46 PM
 */

package model;
//import com.lexpar.util.Log;

/**
 * A DefaulListModel which keeps items in sorted order.
 * @author  bill
 */
public class SortedListModel extends javax.swing.DefaultListModel {
    
    /** Creates a new instance of SortedListModel */
    public SortedListModel() {}
    
    /**
     * Inserts the specified element at the proper position in this list.
     * <p>
     *  If the new element .equals() an
     *  existing element, the existing element is replaced.  We assume the objects
     *  are Strings, for now, and use .equalsIgnoreCase().  In the future, we
     *  may want to wrap the String's in another object, to maintain information
     *  about insertions and deletions.
     * <p>
     *  Note.  This method can efficiently populate a model if the elements are given
     *  in approximate alphabetic order.
     * @param element element to be inserted
     */
    public int add(Object element) {
        //Log.print("(SLM).add element " + element);
        String elementString = (String) element;
        // find position for insertion
        // start from end of list, so insertion efficient for loading in sorted order
        int newPosition = this.size();
        while (newPosition > 0 && elementString.compareToIgnoreCase((String) this.get(newPosition-1)) <= 0) {
            newPosition--;
        }
        //System.out.println("newPosition="+newPosition);
        if ( newPosition < this.size() && elementString.equalsIgnoreCase((String) this.get(newPosition))) {
            // element matches existing item; replace existing item (for change in capitalization)
            //System.err.println("match");
            this.set(newPosition, element);
        } else {
            // add element to list
            this.add(newPosition, element);
        }
        return newPosition;
    }

    public void removeElementAt(int row) {
        super.removeElementAt(row);
    }

    public Object elementAt(int row) {
        return super.elementAt(row);
    }

    /** 
     * Searches for the specified element in this list, ignoring case.
     * <p>
     *  If the element .equals() an existing element, return the position.  We assume the objects
     *  are Strings, for now, and use .equalsIgnoreCase().
     * <p>
     * @param element element to be sought in the model.
     */
    public int containsIgnoreCase(Object element) {
        String elementString = (String) element;
        // find position for comparison
        int newPosition = this.size();
        while (newPosition > 0 && (elementString.trim()).compareToIgnoreCase(((String) this.get(newPosition-1)).trim()) <= 0) {
            newPosition--;
        }
        //System.out.println("newPosition="+newPosition);
        if ( newPosition < this.size() && (elementString.trim()).equalsIgnoreCase(((String) this.get(newPosition)).trim())) {
            // element matches existing item; do not replace existing item
            return newPosition;
        }
        return -1;
    }
}
