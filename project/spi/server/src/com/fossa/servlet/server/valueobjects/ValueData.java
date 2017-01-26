/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fossa.servlet.server.valueobjects;

/**
 *
 * @author sunil
 */
public class ValueData {

    private int valueId;
    private String value;

    /**
     * Set Value of project field for a particular batch.
     * @return - value
     */
    public String getValue() {
        return value;
    }

    /**
     * Set the value of project field for a particular batch.
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Get Value Id
     * @return - value Id
     */
    public int getValueId() {
        return valueId;
    }

    /**
     * Set the Value Id
     * @param valueId - value id
     */
    public void setValueId(int valueId) {
        this.valueId = valueId;
    }
}
