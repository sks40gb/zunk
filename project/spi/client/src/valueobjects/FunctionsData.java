/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package valueobjects;

/**
 * This Value Object has the name, id of the Validation Function along with the selected status which indicates whether 
 * the function is activated or not.
 *
 * @author Sunil
 */
public class FunctionsData {
    
    // Name of the Validation function
    protected String name;
    
    //ID of the Validation Function
    protected int id;
    
    //Indicator which tells us whether the validation function has been activated
    protected boolean selected;

    /**
     * Create new Function instance 
     *
     * @param name - Function Name
     * @param id - Function Id
     */
    public FunctionsData(String name, int id) {
        this.name = name;
        this.id = id;
        selected = false;
    }

    /**
     * Create new Function instance
     *
     * @param name - Function Name
     * @param id - Function Id
     * @param selected - Function is selected for the Field or not.
     */
    public FunctionsData(String name, int id, boolean selected) {
        this.name = name;
        this.id = id;
        this.selected = selected;

    }

    /**
     * Get the Function Name
     * @return - function Name
     */
    public String getName() {
        return name;
    }

    /**
     * Get Function Id
     * @return -Functoin Id
     */
    public int getId() {
        return id;
    }

    /**
     * Set the Function is as selected Function.
     * @param selected - true if selected else false.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Invert the selection.
     */
    public void invertSelected() {
        selected = !selected;
    }

    /**
     * Checking function is selected or not. 
     *
     * @return - returns true if function is selected else returns false.
     */
    public boolean isSelected() {
        return selected;
    }

    public String toString() {
        return name;
    }
}