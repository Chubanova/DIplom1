package com.mesaeva.viktorines.domain.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "questions")
public class Question implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idQuestion;
    private Integer idVikt;
    private String textQuestion;
    private Integer numberOfQuestion;

    public Question() { }

    public Question(Integer idVikt, String textQuestion, Integer numberOfQuestion) {
        this.idVikt = idVikt;
        this.textQuestion = textQuestion;
        this.numberOfQuestion = numberOfQuestion;
    }

    public Integer getNumberOfQuestion() {
        return numberOfQuestion;
    }

    public void setNumberOfQuestion(Integer numberOfQuestion) {
        this.numberOfQuestion = numberOfQuestion;
    }

    public Integer getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(Integer idQuestion) {
        this.idQuestion = idQuestion;
    }

    public Integer getIdVikt() {
        return idVikt;
    }

    public void setIdVikt(Integer idVikt) {
        this.idVikt = idVikt;
    }

    public String getTextQuestion() {
        return textQuestion;
    }

    public void setTextQuestion(String textQuestion) {
        this.textQuestion = textQuestion;
    }
}
