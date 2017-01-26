
package valueobjects;

import java.util.HashMap;
import java.util.Map;

/**
 * This Value Object holds the Project Field ID and Project Field name. It also has the Validation functions appropriated 
 * for the project field.
 * 
 * @author Sunil
 */
public class FieldsData {
    
    // ID of the project field
    private int id;
    
    //Name of the project field
    private String name;
    
    //Map which contains the Validation Functions against the name of the Validation Function.
    private Map<String,FunctionsData> functionsMap;    

    /**
     * constructs new functionsMap for different fields data.
     */
    public FieldsData(){
        functionsMap = new HashMap<String, FunctionsData>();
    }
    
    /**
     * Get the Functions Map (contains the list of Function Data) for the Field. 
     * @return - Functions Map
     */
    public Map<String, FunctionsData> getFunctionsMap() {
        return functionsMap;
    }

    /**
     * Set the Functions Map 
     * @param functionsMap - Function Map
     */
    public void setFunctionsMap(Map<String, FunctionsData> functionsMap) {
        this.functionsMap = functionsMap;
    }

    /**
     * Get Field Id
     * @return - Field Id
     */
    public int getId() {
        return id;
    }

    /**
     * Set Field Id
     * @param id  - Field Id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get Field Name
     * @return - Field Name
     */
    public String getName() {
        return name;
    }

    /**
     * Set Field Name
     * @param name - Field Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Place the Function data to functionsMap.
     *
     * @param key - Function Name
     * @param value - Function record or data.
     */
    public void putInFuntionsMap(String key, FunctionsData value) {
        functionsMap.put(key, value);
    }

    @Override
    public String toString() {
        return name;
    }
 
}
