package com.hibernate.base;

/**
 *
 * @author Administrator
 */
public enum QueryOperator {

    LIKE("LIKE"),NOT_LIKE("NOT LIKE"), EQUAL("="), NOT_EQUAL_TO("!="), LESS_THAN("<"), GREATER_THAN(">"), LESS_THAN_EQAUL_TO("<="), GREATER_THAN_EQUAL_TO(">=");
    private String operator;

    QueryOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        return operator;
    }
};
