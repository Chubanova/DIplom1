package com.mesaeva.viktorines.domain.dao;

import com.mesaeva.viktorines.domain.entities.Question;

import java.util.List;

public interface QuestionDAO {
    void create(Question q);
    void update(Question q);
    List getQuestionsForViktorine(int idViktorine);
    Question question(int idViktorine, int numberOfQuestion);
    Question question(int id);
}
