/* $Header: /home/common/cvsarea/ibase/dia/src/common/TablevalueData.java,v 1.2.6.1 2005/12/01 18:27:29 nancy Exp $ */
package common;

/**
 * A container for data required for update of a tablevalue row.
 */
public class TablevalueData {

    /** id of the tablespec row, or 0 for insert */
    public int tablevalue_id;

    /** tablespec_id of the table */
    public int tablespec_id;

    /** value to be written to the table or "" for delete */
    public String value;

    /** document level indicator */
    public int level;

    /** the value in tablespec.model_table that controls this tablevalue.value */
    public String model_value;

    /** original value of this tablevalue.value */
    public String old_value;
}
