package com.mesaeva.viktorines.form.annotation;

import com.mesaeva.viktorines.form.converters.RequestParameterValueConverter;
import com.mesaeva.viktorines.form.converters.ValueConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

/**
 * Annotates a field of a form class.
 * <code>entityFieldName</code> attribute is name of another class field from which value for this field
 * is given when creating form class instance with factory
 * <code>formFieldLabel</code> is a label bound with field
 * <code>formFieldType</code> defines a type of input for this field in HTML page.
 * <code>formFieldOptions</code> points to another field of form class that stores a {@link List}
 * of {@link FormFieldOption} annotated class instances representing possible values for this field
 * By default no options are expected
 * <code>extractiveType</code> is name of the type to which field value is converted before rendering
 * a template; by default it's not converted
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormField {
    String formFieldName();

    String entityFieldName() default "";

    String formFieldLabel() default "";

    String formFieldType() default "text";

    String formFieldOptions() default "";

    String extractiveType() default "";

    Class<? extends ValueConverter> valueConverter() default RequestParameterValueConverter.class;
}