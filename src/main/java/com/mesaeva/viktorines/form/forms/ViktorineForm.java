package com.mesaeva.viktorines.form.forms;

import com.mesaeva.viktorines.form.annotation.Form;
import com.mesaeva.viktorines.form.annotation.FormField;
import com.mesaeva.viktorines.template.Pages;

import javax.validation.constraints.NotNull;

@Form(action = Pages.CREATE_VIKTORINE)
public class ViktorineForm {

    @NotNull
    @FormField(formFieldName = "name",
            entityFieldName = "name",
            formFieldLabel = "Название викторины")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
