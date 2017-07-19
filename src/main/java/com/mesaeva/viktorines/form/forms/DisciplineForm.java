package com.mesaeva.viktorines.form.forms;

import com.mesaeva.viktorines.form.annotation.Form;
import com.mesaeva.viktorines.form.annotation.FormField;
import com.mesaeva.viktorines.template.Pages;

@Form(action = Pages.DISCIPLINES, submitLabel = "Добавить")
public class DisciplineForm {
    @FormField(formFieldName = "name",
            entityFieldName = "name_discipline",
            formFieldLabel = "Наименование")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
