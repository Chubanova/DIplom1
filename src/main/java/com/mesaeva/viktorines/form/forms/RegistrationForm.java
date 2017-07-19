package com.mesaeva.viktorines.form.forms;

import com.mesaeva.viktorines.form.annotation.Form;
import com.mesaeva.viktorines.form.annotation.FormField;
import com.mesaeva.viktorines.form.annotation.constraint.RegistrationFormPasswordsMatch;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Form(action = "/newuser")
@RegistrationFormPasswordsMatch
public class RegistrationForm {

    @NotNull
    @Size(min = 4, max = 16)
    @FormField(formFieldName = "login",
            entityFieldName = "login",
            formFieldLabel = "Логин")
    private String login;

    @NotNull
    @Size(min = 2, max = 32)
    @FormField(formFieldName = "username",
            entityFieldName = "username",
            formFieldLabel = "Имя пользователя")
    private String username;

    @NotNull
    @Size(min = 6, max = 50)
    @FormField(formFieldName = "password",
            formFieldLabel = "Пароль",
            formFieldType = "password")
    private String password;

    @NotNull
    @Size(min = 6, max = 50)
    @FormField(formFieldName = "passwordRepeat",
            formFieldLabel = "Подтвердите пароль",
            formFieldType = "password")
    private String passwordRepeat;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }
}
