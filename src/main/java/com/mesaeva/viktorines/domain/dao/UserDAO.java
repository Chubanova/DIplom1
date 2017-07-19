package com.mesaeva.viktorines.domain.dao;

import com.mesaeva.viktorines.domain.entities.User;
import com.mesaeva.viktorines.form.forms.ProfileForm;
import com.mesaeva.viktorines.form.forms.RegistrationForm;

import java.util.List;

public interface UserDAO {
    User get(String username);
    User get(String username, int i);
    User get(Integer id);
    void delete(User user);
    void create(RegistrationForm form) throws IllegalAccessException, NoSuchFieldException, InstantiationException;
    void create(User u);
    void update(String username, ProfileForm form) throws IllegalAccessException, NoSuchFieldException, InstantiationException;
    void update(User u);
    List<User> list();
    List<User> listForTeacher(int id);
    List<String> listTeachers();
}
