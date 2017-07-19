package com.mesaeva.viktorines.domain.dao.impl;

import com.mesaeva.viktorines.domain.dao.DisciplineDAO;
import com.mesaeva.viktorines.domain.entities.Discipline;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.List;

public class HibernateDisciplineDAO implements DisciplineDAO {
    private SessionFactory sessionFactory;
    Logger logger = Logger.getLogger(HibernateDisciplineDAO.class);

    public HibernateDisciplineDAO() throws NamingException {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            sessionFactory = (SessionFactory) envContext.lookup("viktorines/SessionFactory");
        } catch (NamingException e) {
            logger.error(e);
            throw e;
        }
    }

    @Override
    public List<String> list() {
        try (Session session = sessionFactory.openSession()) {
            List<Discipline> list = session.createQuery("select d from Discipline d").list();
            List<String> names = new ArrayList<>();
            for(Discipline d : list){
                names.add(d.getNameDiscipline());
            }
            return names;
        }
    }

    @Override
    public void create(Discipline name) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.save(name);
            session.getTransaction().commit();
        }
    }

    @Override
    public void remove(int id) {
        try (Session session = sessionFactory.openSession()) {
            Discipline d = (Discipline) session.createQuery("select d from Discipline d where d.idDiscipline = :id")
                    .setParameter("id", id)
                    .uniqueResult();
            session.beginTransaction();
            session.delete(d);
            session.getTransaction().commit();
        }
    }

    @Override
    public String getNameById(int id) {
        try (Session session = sessionFactory.openSession()) {
            Discipline d = (Discipline) session
                    .createQuery("select d from Discipline d where d.idDiscipline = :id ")
                    .setParameter("id", id)
                    .uniqueResult();
            return d.getNameDiscipline();
        }
    }

    @Override
    public Integer getIdByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            return (Integer) session
                    .createQuery("select d.idDiscipline from Discipline d where d.nameDiscipline = :name")
                    .setParameter("name", name)
                    .uniqueResult();
        }
    }
}
