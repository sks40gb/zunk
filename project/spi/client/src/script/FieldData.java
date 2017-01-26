/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package script;

/**
 * Class FieldData used to get project fields data.
 * @see Validator  
 * @author sunil
 */
public class FieldData {

    /** Field Id */
    private int fieldId;
    /** Field size */
    private int fieldSize;
    /** Field Name */
    private String fieldName;
    /** Field Type */
    private String fieldType;
    /** Field Value */
    private String fieldValue;

    public FieldData() {
    }

    /**
     * Set the field name.
     * @param name Field name.
     * @return instance of this class.
     */
    public FieldData addName(String name) {
        fieldName = name;
        return this;
    }

    /**
     * Set the Field Type
     * @param type Field Type
     * @return instance of this class.
     */
    public FieldData addType(String type) {
        fieldType = type;
        return this;
    }

    /**
     * Set the Field Value
     * @param value Value of Field.
     * @return instance of this class.
     */
    public FieldData addValue(String value) {
        fieldValue = value;
        return this;
    }

    /**
     * Set the Field Size
     * @param size Field Size
     * @return instance of this class.
     */
    public FieldData addSize(int size) {
        fieldSize = size;
        return this;
    }

    /**
     * Get Field Id
     * @return - Field Id
     */
    public int getFieldId() {
        return fieldId;
    }

    /**
     * Set Field Id
     * @param fieldId - Field Id
     */
    public void setFieldId(int fieldId) {
        this.fieldId = fieldId;
    }

    /**
     * Set Field Name
     * @return - Field Name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Set Field Name
     * @param fieldName - Field Name
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Get Field Size
     * @return - Field Size
     */
    public int getFieldSize() {
        return fieldSize;
    }

    /**
     * Set Field size
     * @param fieldSize - Field size
     */
    public void setFieldSize(int fieldSize) {
        this.fieldSize = fieldSize;
    }

    /**
     * Get Field Type
     * @return - Field Type
     */
    public String getFieldType() {
        return fieldType;
    }

    /**
     * Set Field Type
     * @param fieldType - Field type
     */
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * Get Field Value
     * @return - Field value
     */
    public String getFieldValue() {
        return (fieldValue == null ? "" : fieldValue);
    }

    /**
     * Set Field Value
     * @param fieldValue - Field Value
     */
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }
}