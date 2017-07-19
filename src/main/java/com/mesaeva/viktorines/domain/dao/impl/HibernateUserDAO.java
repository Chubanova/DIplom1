package com.mesaeva.viktorines.domain.dao.impl;

import com.mesaeva.viktorines.domain.dao.UserDAO;
import com.mesaeva.viktorines.domain.entities.User;
import com.mesaeva.viktorines.form.forms.ProfileForm;
import com.mesaeva.viktorines.form.forms.RegistrationForm;
import com.mesaeva.viktorines.form.util.FormUtil;
import com.mesaeva.viktorines.template.TemplateTags;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;

public class HibernateUserDAO implements UserDAO {
    private SessionFactory sessionFactory;
    Logger logger = Logger.getLogger(HibernateUserDAO.class);


    public HibernateUserDAO() throws NamingException {
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
    public User get(String login) {
        try (Session session = sessionFactory.openSession()) {
            return (User) session
                    .createQuery("select u from User u where u.login = :login")
                    .setParameter("login", login)
                    .uniqueResult();
        }
    }

    @Override
    public User get(String username, int i) {
        try (Session session = sessionFactory.openSession()) {
            return (User) session
                    .createQuery("select u from User u where u.username = :username")
                    .setParameter("username", username)
                    .uniqueResult();
        }
    }

    @Override
    public User get(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return (User) session
                    .createQuery("select u from User u where u.idUser = :id")
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    @Override
    public void delete(User user) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.delete(user);
            session.getTransaction().commit();
        }
    }

    @Override
    public void create(RegistrationForm form) throws IllegalAccessException, NoSuchFieldException, InstantiationException {
        try (Session session = sessionFactory.openSession()) {
            User user = new User();
            FormUtil.updateEntity(user, form);
            user.setHashpass(form.getPassword());
            user.setRoleId(TemplateTags.ROLE_STUDENT);
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            logger.error(e);
            throw e;
        }
    }

    @Override
    public void create(User u) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(u);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(String username, ProfileForm form) throws IllegalAccessException, NoSuchFieldException, InstantiationException {
        try (Session session = sessionFactory.openSession()) {
            User user = get(username);
            FormUtil.updateEntity(user, form);
            if (form.getPassword() != null && !form.getPassword().equals("")) {
                user.setHashpass(form.getPassword());
            }
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
        } catch (IllegalAccessException | NoSuchFieldException e) {
            logger.error(e);
            throw e;        }
    }

    @Override
    public void update(User u) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.update(u);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<User> list() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select u from User u").list();
        }
    }

    @Override
    public List<User> listForTeacher(int id) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select u from User u where u.ownerId = :id")
                    .setParameter("id", id)
                    .list();
        }
    }

    public List<String> listTeachers() {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery("select u.username from User u where u.roleId = "
                    + TemplateTags.ROLE_TEACHER).list();
        }
    }
}
