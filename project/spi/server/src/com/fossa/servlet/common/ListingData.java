/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.common;

/**
 *
 * @author Bala
 */
/**
 * A container for data required for display of Listing.
 * 
 */
public class ListingData {

    //Field value Occurrence
    private int occurrence = 0;
    
    //Field value marked for Listing
    private String marking = "";

    public int getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(int occurrence) {
        this.occurrence = occurrence;
    }

    public String getMarking() {
        return marking;
    }

    public void setMarking(String marking) {
        this.marking = marking;
    }
}
