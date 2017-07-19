package com.mesaeva.viktorines.form.annotation.constraint;

import com.mesaeva.viktorines.form.validators.EqualFieldValuesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EqualFieldValuesValidator.class)
public @interface EqualFieldValues {
    String[] fields();

    String errorField();

    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
