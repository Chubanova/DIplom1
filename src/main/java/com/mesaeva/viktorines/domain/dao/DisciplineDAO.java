package com.mesaeva.viktorines.domain.dao;

import com.mesaeva.viktorines.domain.entities.Discipline;

import java.util.List;

public interface DisciplineDAO {
    void remove(int id);
    String getNameById(int idDiscipline);
    Integer getIdByName(String nameDiscipline);
    List<String> list();
    void create(Discipline name);
}
