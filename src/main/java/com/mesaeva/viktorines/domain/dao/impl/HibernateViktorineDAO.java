package com.mesaeva.viktorines.domain.dao.impl;

import com.mesaeva.viktorines.domain.dao.ViktorineDAO;
import com.mesaeva.viktorines.domain.entities.Viktorine;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;

public class HibernateViktorineDAO implements ViktorineDAO {
    private SessionFactory sessionFactory;
    Logger logger = Logger.getLogger(HibernateUserDAO.class);


    public HibernateViktorineDAO() throws NamingException {
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
    public Viktorine get(int id) {
        try (Session session = sessionFactory.openSession()) {
            return (Viktorine) session.createQuery("select v from Viktorine v where v.id = :id")
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    @Override
    public Viktorine get(String name, int teacherId) {
        try (Session session = sessionFactory.openSession()) {
            return (Viktorine) session
                    .createQuery("select v from Viktorine v where v.name = :name and v.idUser = :teacherId")
                    .setParameter("name", name)
                    .setParameter("teacherId", teacherId)
                    .uniqueResult();
        }
    }

    @Override
    public void create(Viktorine v) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(v);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Viktorine viktorine) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(viktorine);
            session.getTransaction().commit();
        }
    }

    @Override
    public void delete(Viktorine viktorine) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(viktorine);
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

    @Override
    public List<Viktorine> getListByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select v from Viktorine v where v.idUser = " +
                    "(select u.idUser from User u where u.login = :username )")
                    .setParameter("username", username)
                    .list();
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }

    @Override
    public List<Viktorine> list() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select v from Viktorine v").list();
        } catch (Exception e) {
            logger.error(e);
            throw e;
        }
    }
}
