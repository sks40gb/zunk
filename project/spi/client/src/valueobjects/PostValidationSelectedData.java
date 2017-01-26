package valueobjects;

/**
 * This Value Object holds the project Field and the Validation Function assosiated with it.
 *
 * @author sunil
 */
public class PostValidationSelectedData {

    // Project Field ID
    private int fieldId;
    
    // Validation Function ID
    private int functionId;
    
    // Project Field Name
    private String fieldName;
    
    // Validation Function Name
    private String functionName;

    /**
     * Get Project Field Id
     *
     * @return Project Field Id
     */
    public int getFieldId() {
        return fieldId;
    }

    /**
     * Set Project Field Id
     *
     * @param fieldId - Project Field Id
     */
    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    /**
     * Get Project Field Name
     *
     * @return - Project Field Name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Set Project Field Name
     *
     * @param fieldName - Project Field Name
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Get Validation Function Id
     *
     * @return - Validation Function Id
     */
    public int getFunctionId() {
        return functionId;
    }

    /**
     * Set Validation Function Id
     *
     * @param functionId - Validation Function Id
     */
    public void setFunctionId(int functionId) {
        this.functionId = functionId;
    }

    /**
     * Get Validation Function Name
     *
     * @return functionName Validation Function Name
     */
    public String getFunctionName() {
        return functionName;
    }

    /**
     * Set Validation Function Name
     *
     * @param functionName - Validation Function Name
     */
    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }
}
