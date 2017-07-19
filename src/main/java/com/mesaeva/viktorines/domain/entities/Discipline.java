package com.mesaeva.viktorines.domain.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "disciplines")
public class Discipline implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idDiscipline;
    @Column(unique = true, nullable = false)
    private String nameDiscipline;

    public Discipline() {
        //Default constructor
    }

    public Integer getIdDiscipline() {
        return idDiscipline;
    }

    public void setIdDiscipline(Integer idDiscipline) {
        this.idDiscipline = idDiscipline;
    }

    public String getNameDiscipline() {
        return nameDiscipline;
    }

    public void setNameDiscipline(String nameDiscipline) {
        this.nameDiscipline = nameDiscipline;
    }
}
