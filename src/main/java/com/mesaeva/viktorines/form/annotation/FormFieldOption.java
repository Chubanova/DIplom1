package com.mesaeva.viktorines.form.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a class representing possible value for some form field.
 * <code>valueField</code> points to field that stores possible value
 * <code>valueField</code> points to field that stores a label for possible value
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormFieldOption {
    String valueField() default "value";

    String labelField() default "label";
}
