/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package common;

/**
 * @author balab
 */

/*
 * This class is used to get the listing data
 **/ 
 
public class MarkingData {
 
    
    /** start bates number in the range */
    private String firstBatesOfRange;
    
    /** Last bates number in the range */
    private String lastBatesOfRange;
    
    /** Field value */
    private String fieldvalue;
    
    /** Sequence */
    private String sequence;
    
    /** view_marking */
    private String view_marking;
    
    /** correctionData */
    private String correctionData;
    
    /** correctionType */
    private String correctionType;
    

    public String getFirstBatesOfRange() {
        return firstBatesOfRange;
    }

    public void setFirstBatesOfRange(String firstBatesOfRange) {
        this.firstBatesOfRange = firstBatesOfRange;
    }

    public String getLastBatesOfRange() {
        return lastBatesOfRange;
    }

    public void setLastBatesOfRange(String lastBatesOfRange) {
        this.lastBatesOfRange = lastBatesOfRange;
    }

    public String getFieldvalue() {
        return fieldvalue;
    }

    public void setFieldvalue(String fieldvalue) {
        this.fieldvalue = fieldvalue;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getView_marking() {
        return view_marking;
    }

    public void setView_marking(String view_marking) {
        this.view_marking = view_marking;
    }

    public String getCorrectionData() {
        return correctionData == null ? "" : correctionData;
    }

    public void setCorrectionData(String correctionData) {
        this.correctionData = correctionData;
    }

    public String getCorrectionType() {
        return correctionType == null ? "" : correctionType;
    }

    public void setCorrectionType(String correctionType) {
        this.correctionType = correctionType;
    }
    
    

}
