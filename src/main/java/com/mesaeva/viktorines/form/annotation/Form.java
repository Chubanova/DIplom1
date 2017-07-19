package com.mesaeva.viktorines.form.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a form class.
 * <code>action</code> URI to send form data
 * <code>successMessage</code> a message for user if received data is valid
 * <code>failMessage</code> a message for opposite case
 * <code>submitLabel</code> a label on form data submitting button on HTML page
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Form {
    String action();

    String successMessage() default "";

    String failMessage() default "";

    String submitLabel() default "Подтвердить";
}
