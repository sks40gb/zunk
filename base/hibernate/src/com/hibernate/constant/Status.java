/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hibernate.constant;

public enum Status {

    APPROVED("Approved"), PENDING("pending"), ALL("All"),DECLINE("decline"),A("A"),A_PLUS_2("A++"),PURCHASED("purchased");
    String text;

    Status(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }
}
