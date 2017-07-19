package com.mesaeva.viktorines.form.validators;

import com.mesaeva.viktorines.form.annotation.constraint.RegistrationFormPasswordsMatch;
import com.mesaeva.viktorines.form.forms.RegistrationForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Tests password and password repeat fields of the Registration form for equality.
 */
public class RegistrationFormPasswordsMatchValidator
        implements ConstraintValidator<RegistrationFormPasswordsMatch, RegistrationForm> {

    @Override
    public void initialize(RegistrationFormPasswordsMatch registrationFormPasswordsMatch) {
        //Default initializer
    }

    @Override
    public boolean isValid(RegistrationForm registrationForm,
                           ConstraintValidatorContext constraintValidatorContext) {
        String password = registrationForm.getPassword();
        String passwordRepeat = registrationForm.getPasswordRepeat();
        if (password != null && passwordRepeat != null) {
            return password.equals(passwordRepeat);
        }
        return false;
    }
}
