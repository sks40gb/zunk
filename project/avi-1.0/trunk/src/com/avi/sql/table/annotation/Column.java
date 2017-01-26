/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.avi.sql.table.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author sunil
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)

public @interface Column {
    String DEFAULT_NAME = "DeFaUlt";
    String name() default DEFAULT_NAME;
    boolean isPK() default false;
    boolean autoIncrement() default false;
    boolean notNull() default false;
}
