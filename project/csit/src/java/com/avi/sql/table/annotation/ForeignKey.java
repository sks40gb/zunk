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
public @interface ForeignKey {
    public final String RESTRICT = "RESTRICT";
    public final String CASCADE = "CASCADE";
    public final String SET_NULL = "SET NULL";
    public final String NO_ACTION = "NO ACTION";

    Class table();
    String column();
    String onDelete() default NO_ACTION;
    String onUpdate() default NO_ACTION;
}
