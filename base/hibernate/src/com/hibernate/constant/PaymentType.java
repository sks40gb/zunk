/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hibernate.constant;

public enum PaymentType {

    APPROVAL("Approval"), IMMEDIATE("Immediate");
    String text;

    PaymentType(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }
}
