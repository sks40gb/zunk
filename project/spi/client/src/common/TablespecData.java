/* $Header: /home/common/cvsarea/ibase/dia/src/common/TablespecData.java,v 1.2.6.1 2005/12/01 18:27:29 nancy Exp $ */
package common;

/**
 * A container for data required for update of a user.
 */
public class TablespecData {

    /** id of the tablespec row, or 0 for insert */
    public int tablespec_id;

    /** name of the table */
    public String table_name;

    /** type of tablespec: Name or Text */
    public String table_type;

    /** id of the project the tablespec belongs to, or 0 for Global */
    public int project_id;

    /** is use of the table mandatory: Optional or Required */
    public String requirement;

    /** who may update the table: CoderAdd or SuperMod */
    public String updateable;

    /** the list of values shown for this table is based on values from this table */
    public int model_tablespec_id;
}
