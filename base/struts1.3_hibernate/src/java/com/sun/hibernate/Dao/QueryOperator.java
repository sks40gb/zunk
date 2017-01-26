/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sun.hibernate.Dao;

/**
 *
 * @author Administrator
 */
public enum QueryOperator {

    LIKE("LIKE"), EQUAL("="), LESS_THAN("<"), GREATER_THAN(">"), LESS_THAN_EQAUL_TO("<="), GREATER_THAN_EQUAL_TO(">=");
    private String operator;

    QueryOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return operator;
    }
};
