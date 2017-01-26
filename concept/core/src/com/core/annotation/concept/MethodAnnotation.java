package com.core.annotation.concept;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Here are some rules-of-thumb when defining an annotation type:

   1. Annotation declaration should start with an 'at' sign like @, following with an interface keyword, following with the annotation name.
   2. Method declarations should not have any parameters.
   3. Method declarations should not have any throws clauses.
   4. Return types of the method should be one of the following:
          * primitives
          * String
          * Class
          * enum
          * array of the above types 
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodAnnotation {

    public enum Shape{
        ROUND,SQAURE,CUBE;
    }

    String source() default "yellow";
    Shape shape() default Shape.CUBE;

}

