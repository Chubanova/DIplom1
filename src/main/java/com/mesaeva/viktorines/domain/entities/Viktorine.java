package com.mesaeva.viktorines.domain.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "viktorines")
public class Viktorine implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idVikt;

    private String name;
    private Integer idDicipline;
    private Integer idUser;
    private Integer questioncol;

    public Viktorine() { }

    public Viktorine(String name, Integer idDicipline, Integer idUser) {
        this.name = name;
        this.idDicipline = idDicipline;
        this.idUser = idUser;
    }

    public Integer getQuestioncol() {
        return questioncol;
    }

    public void setQuestioncol(Integer questioncol) {
        this.questioncol = questioncol;
    }

    public Integer getIdVikt() {
        return idVikt;
    }

    public void setIdVikt(Integer idVict) {
        this.idVikt = idVict;
    }

    public Integer getIdDicipline() {
        return idDicipline;
    }

    public void setIdDicipline(Integer idDicipline) {
        this.idDicipline = idDicipline;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }
}
