/* $Header: /home/common/cvsarea/ibase/dia/src/common/ProjectFieldsData.java,v 1.7.6.4 2005/11/11 15:14:29 nancy Exp $ */
package common;

/**
 * A container for data required for update and retrieval of projectfields.
 */
public class ProjectFieldsData {

    /** Id of the projectfields row */
    public int projectfieldsId;

    /** Id of the project */
    public int projectId;

    /** Sequence of this component on the screen */
    public int sequence;

    /** The name of the field */
    public String fieldName;
    
    /** L1 information field */
    public String l1_information;

    /** The short name of the field (used for BRS) */
    public String tagName;

    /** Type of field: text, date, signed, unsigned, name */
    public String fieldType;

    /** Size of the field */
    public int fieldSize;

    /** Minimum size of the field */
    public int minimumSize;

    /** One value or can the value be repeated */
    public String repeated;

    /** Should this field show on the unitize screen */
    public String unitize;

    /** Should this field have a spell check */
    public String spellCheck;

    /** Is the field required */
    public String required;

    /** If no value entered by user, use this value */
    public String defaultValue;

    /** document level indicator */
    public String fieldLevel;

    /** group this field belongs to */
    public int fieldGroup;

    /** Minimum value of the data entered by the user */
    public String minValue;

    /** Maximum value of the data entered by the user */
    public String maxValue;

    /** If the data can be selected from a list, the id of the table in tablespec */
    public int tablespecId;

    /** Is the data required to be selected from a list */
    public String tableMandatory;

    /** Field mask */
    public String mask;

    /** Characters allowed in the field */
    public String validChars;

    /** Characters not allowed in the field */
    public String invalidChars;

    /** Character set */
    public String charset;

    public String typeField;
    public String typeValue;

    /** +1 indicates move up, -1 indicates move down */
    public int moveIndicator;
    
    public String description;
    
    public String standardFieldValidations;
    
    public String mode = "";
    
    public int std_group_id = -1;
}
