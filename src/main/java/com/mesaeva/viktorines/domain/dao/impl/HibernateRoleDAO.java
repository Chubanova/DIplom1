package com.mesaeva.viktorines.domain.dao.impl;

import com.mesaeva.viktorines.domain.dao.RoleDAO;
import com.mesaeva.viktorines.domain.entities.Role;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;

import java.util.stream.Collectors;

public class HibernateRoleDAO implements RoleDAO {
    private SessionFactory sessionFactory;
    Logger logger = Logger.getLogger(HibernateRoleDAO.class);

    public HibernateRoleDAO() throws NamingException {
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
    public Role get(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return (Role) session
                    .createQuery("select r from Role r where r.id = :id")
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    @Override
    public Role get(String name) {
        try (Session session = sessionFactory.openSession()) {
            return (Role) session
                    .createQuery("select r from Role r where r.authority = :name")
                    .setParameter("name", name)
                    .uniqueResult();
        }
    }

    @Override
    public List<String> list() {
        try (Session session = sessionFactory.openSession()) {
            List<Role> list = session.createQuery("select r from Role r").list();
            return list.stream().map(Role::getAuthority).collect(Collectors.toList());
        }
    }
}
