package com.mesaeva.viktorines.servlet;

import com.mesaeva.viktorines.domain.dao.RoleDAO;
import com.mesaeva.viktorines.domain.dao.UserDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateRoleDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateUserDAO;
import com.mesaeva.viktorines.domain.entities.User;
import com.mesaeva.viktorines.template.HtmlResponseWriter;
import com.mesaeva.viktorines.template.Pages;
import com.mesaeva.viktorines.template.TemplateTags;
import org.apache.log4j.Logger;
import org.springframework.security.web.csrf.CsrfToken;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(Pages.USERS_MANAGEMENT)
public class UsersServlet extends AppHttpServlet {
    private static final String USERS_AUTH_CONTENT_DELEGATE = "users";
    private static final String USERS_AUTH_TITLE = "Пользователи";
    private final UserDAO userDAO = new HibernateUserDAO();
    private final RoleDAO roleDAO = new HibernateRoleDAO();
    static Logger logger = Logger.getLogger(UsersServlet.class);

    public UsersServlet() throws NamingException {
        //Default constructor
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User myself = userDAO.get(getAuthUsername());
        int role = myself.getRoleId();
        List<User> users = new ArrayList<>();
        HtmlResponseWriter writer = null;
        try {
            writer = HtmlResponseWriter.create(resp);
        } catch (NamingException e) {
            logger.error(e);
        }
        switch (role) {
            case TemplateTags.ROLE_TEACHER:
                users = userDAO.listForTeacher(myself.getIdUser());
                writer.putData(TemplateTags.USER, userToTemplate(myself));
                break;
            case TemplateTags.ROLE_ADMIN:
                users = userDAO.list();
                break;
            default:
                break;
        }
        try {
            writer.putData(TemplateTags.USERS, usersToTemplate(users))
                    .setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                    .setTitle(USERS_AUTH_TITLE)
                    .setContentDelegate(USERS_AUTH_CONTENT_DELEGATE)
                    .write();
        } catch (Exception e) {
            resp.sendError(500);
            logger.error(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("useridForRemove") != null) {
            User user = userDAO.get(Integer.parseInt(req.getParameter("useridForRemove")));
            userDAO.delete(user);
            resp.sendRedirect(Pages.USERS_MANAGEMENT);
        } else if (req.getParameter("useridForEdit") != null) {
            User user = userDAO.get(Integer.parseInt(req.getParameter("useridForEdit")));
            resp.sendRedirect(Pages.EDIT_USER + "?login=" + user.getLogin());
        }
    }

    private List<Map<String, Object>> usersToTemplate(List<User> users) {

        users = users == null ? new ArrayList<>() : users;

        List<Map<String, Object>> templateUsers = new ArrayList<>();
        for (User user : users) {
            Map<String, Object> templateUser = new HashMap<>();
            templateUser.put(TemplateTags.USER_ID, user.getIdUser());
            templateUser.put(TemplateTags.USER_LOGIN, user.getLogin());
            templateUser.put(TemplateTags.USERNAME, user.getUsername());
            templateUser.put(TemplateTags.USER_ENABLED, user.getEnabled());
            templateUser.put(TemplateTags.USER_ROLE, roleDAO.get(user.getRoleId()).getAuthority());
            templateUsers.add(templateUser);
        }
        return templateUsers;
    }

    private Map<String, Object> userToTemplate(User user) {
        Map<String, Object> templateUser = new HashMap<>();
        templateUser.put(TemplateTags.USER_ID, user.getIdUser());
        templateUser.put(TemplateTags.USER_LOGIN, user.getLogin());
        templateUser.put(TemplateTags.USERNAME, user.getUsername());
        templateUser.put(TemplateTags.USER_ENABLED, user.getEnabled());
        templateUser.put(TemplateTags.USER_ROLE, roleDAO.get(user.getRoleId()).getAuthority());
        return templateUser;
    }
}
