package com.mesaeva.viktorines.domain.dao;

import com.mesaeva.viktorines.domain.entities.Role;

import java.util.List;

public interface RoleDAO {
    Role get(Integer id);
    Role get(String name);
    List<String> list();
}
