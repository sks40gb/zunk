/* $Header: /home/common/cvsarea/ibase/dia/src/export/ValueData.java,v 1.2 2004/12/31 15:32:22 weaston Exp $ */
package export;

/**
 * Collect the value, namevalue or longvalue data to be written
 * to the csv file.
 * Note:  If an instance holds a namevalue, the values have already
 *        been combined into lastName, firstName MI / organization.
 */
public class ValueData {
    public int childLft = 0;
    public int projectfieldsSequence = 0;
    public int sequence = 0;
    public String fieldName = "";
    public String value = "";
}
