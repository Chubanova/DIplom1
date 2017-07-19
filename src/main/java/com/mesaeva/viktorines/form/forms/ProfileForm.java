package com.mesaeva.viktorines.form.forms;

import com.mesaeva.viktorines.form.annotation.Form;
import com.mesaeva.viktorines.template.Pages;

@Form(action = Pages.EDIT_USER,
        failMessage = "Некорректный ввод",
        successMessage = "Профиль успешно изменён",
        submitLabel = "Сохранить")
public class ProfileForm extends UserCommon {
}
