package com.core.annotation.concept;

import java.lang.annotation.Documented;

/**
 *
 * @author Sunil
 */
@Documented
public @interface ClassPreamble {

    String author();

    String date();

    String lastModifiedBy() default "N/A";

    String lastModifed() default "N/A";

    String[] reviewedBy() default "N/A";
}
