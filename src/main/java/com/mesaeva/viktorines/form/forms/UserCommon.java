package com.mesaeva.viktorines.form.forms;

import com.mesaeva.viktorines.form.annotation.FormField;
import com.mesaeva.viktorines.form.annotation.constraint.EqualFieldValues;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@EqualFieldValues(fields = {"password", "confirmPassword"},
        message = "Ошибка подтверждения пароля",
        errorField = "confirmPassword")
public abstract class UserCommon {
    @NotBlank(message = "Введите имя")
    @NotNull
    @FormField(formFieldName = "username",
            entityFieldName = "username",
            formFieldLabel = "Имя")
    private String username;
    @DecimalMin(value = "0", message = "Укажите верное значение 0: Заблокирован, 1: Активен")
    @DecimalMax(value = "1", message = "Укажите верное значение 0: Заблокирован, 1: Активен")
    @NotNull
    @FormField(formFieldName = "enabled",
            entityFieldName = "enabled",
            formFieldLabel = "Активность")
    private Integer enabled;
    @Pattern(regexp = "^|([a-zA-Z0-9]{6,50})$",
            message = "Пароль должен содержать от 6 до 50 латинских букв или цифр")
    @FormField(formFieldName = "password",
            formFieldLabel = "Пароль",
            formFieldType = "password")
    protected String password;
    @FormField(formFieldName = "confirmPassword",
            formFieldLabel = "Повторите пароль",
            formFieldType = "password")
    private String confirmPassword;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getEnabled() {
        return enabled;
    }

    public void setEnabled(Integer enabled) {
        this.enabled = enabled;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

}
