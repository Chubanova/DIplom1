package com.mesaeva.viktorines.domain.dao.impl;

import com.mesaeva.viktorines.domain.dao.QuestionDAO;
import com.mesaeva.viktorines.domain.entities.Question;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;

public class HibernateQuestionDAO implements QuestionDAO {
    private SessionFactory sessionFactory;
    Logger logger = Logger.getLogger(HibernateQuestionDAO.class);

    public HibernateQuestionDAO() throws NamingException {
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
    public void create(Question q) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(q);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Question q) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            q.setIdQuestion(question(q.getIdVikt(), q.getNumberOfQuestion()).getIdQuestion());
            session.update(q);
            session.getTransaction().commit();
        }
    }

    @Override
    public List<Question> getQuestionsForViktorine(int idViktorine) {
        try (Session session = sessionFactory.openSession()) {
            return (List<Question>) session.createQuery("select q from Question q where q.idVikt = :id")
                    .setParameter("id", idViktorine).list();
        }
    }

    @Override
    public Question question(int idViktorine, int numberOfQuestion) {
        try (Session session = sessionFactory.openSession()) {
            return (Question) session
                    .createQuery("select q from Question q where q.idVikt = :id AND q.numberOfQuestion = :numberQ")
                    .setParameter("id", idViktorine)
                    .setParameter("numberQ", numberOfQuestion)
                    .uniqueResult();
        }
    }

    @Override
    public Question question(int id) {
        try (Session session = sessionFactory.openSession()) {
            return (Question) session
                    .createQuery("select q from Question q where q.idQuestion = :id")
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }
}
