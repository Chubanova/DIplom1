package com.mesaeva.viktorines.domain.dao;

import com.mesaeva.viktorines.domain.entities.Viktorine;

import java.util.List;

public interface ViktorineDAO {
    Viktorine get(int id);
    Viktorine get(String name, int teacherId);
    void create(Viktorine viktorine);
    void update(Viktorine viktorine);
    void delete(Viktorine viktorine);
    List<Viktorine> getListByUsername(String username);
    List<Viktorine> list();
}
