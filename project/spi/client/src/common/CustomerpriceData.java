/* $Header: /home/common/cvsarea/ibase/dia/src/common/Attic/CustomerpriceData.java,v 1.1.2.2 2006/03/22 20:27:15 nancy Exp $ */
package common;

/**
 * A container for data required to store customerprice.
 */
public class CustomerpriceData {

    /** id of the customerprice row, or 0 for insert */
    public int customerprice_id = 0;

    /** id of the project the customerprice belongs to */
    public int project_id = 0;

    /** id of the volume the customerprice belongs to, or 0 for project default */
    public int volume_id = 0;

    /** price applies to this level */
    public int field_level = 0;

    /** price the client pays per unitize page - make it a string for xml message */
    public String unitize_page_price = "0";
    /** price the client pays per unitize document - make it a string for xml message */
    public String unitize_doc_price = "0";
    /** price the client pays per coded page - make it a string for xml message */
    public String coding_page_price = "0";
    /** price the client pays per coded document - make it a string for xml message */
    public String coding_doc_price = "0";
}
