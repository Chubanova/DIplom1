package com.mesaeva.viktorines.domain.dao;

import com.mesaeva.viktorines.domain.entities.Answer;
import java.util.List;

public interface AnswerDAO {
    void create(Answer a);
    void update(Answer a);
    void clean(int idQ);
    List<Answer> getAnswersForQuestion(int idQuestion);
    Answer get(Integer id);
}
