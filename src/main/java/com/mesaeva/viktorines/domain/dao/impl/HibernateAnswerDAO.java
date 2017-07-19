package com.mesaeva.viktorines.domain.dao.impl;

import com.mesaeva.viktorines.domain.dao.AnswerDAO;
import com.mesaeva.viktorines.domain.entities.Answer;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;


public class HibernateAnswerDAO implements AnswerDAO {
    private SessionFactory sessionFactory;
    Logger logger = Logger.getLogger(HibernateAnswerDAO.class);

    public HibernateAnswerDAO() throws NamingException {
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
    public void create(Answer a) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.save(a);
            session.getTransaction().commit();
        }
    }

    @Override
    public void update(Answer a) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Answer answer = get(a.getTextAnswer(), a.getIdQuestion());
            a.setIdAnswer(answer.getIdAnswer());
            session.update(a);
            session.getTransaction().commit();
        }
    }

    @Override
    public void clean(int idQ) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("delete from Answer a where a.idQuestion = :idQ ")
                    .setParameter("idQ", idQ).executeUpdate();
            session.getTransaction().commit();
        }
    }

    private Answer get(String text, int idQ){
        try (Session session = sessionFactory.openSession()) {
            return (Answer) session.
                    createQuery("select a from Answer a where a.idQuestion = :idQ and a.textAnswer = :text ")
                    .setParameter("text", text).setParameter("idQ", idQ).uniqueResult();
        }
    }

    @Override
    public List<Answer> getAnswersForQuestion(int idQuestion) {
        try (Session session = sessionFactory.openSession()) {
            return (List<Answer>) session.
                    createQuery("select a from Answer a where a.idQuestion = :idQuestion ")
                    .setParameter("idQuestion", idQuestion).list();
        }
    }

    @Override
    public Answer get(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return (Answer) session.
                    createQuery("select a from Answer a where a.idAnswer = :id ")
                    .setParameter("id", id).uniqueResult();
        }
    }
}
