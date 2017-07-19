package com.mesaeva.viktorines.servlet;

import com.mesaeva.viktorines.domain.dao.UserDAO;
import com.mesaeva.viktorines.domain.dao.ViktorineDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateDisciplineDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateUserDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateViktorineDAO;
import com.mesaeva.viktorines.domain.entities.Viktorine;
import com.mesaeva.viktorines.template.HtmlResponseWriter;
import com.mesaeva.viktorines.template.Pages;
import com.mesaeva.viktorines.template.TemplateTags;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
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

@WebServlet(Pages.VIKTORINES)
public class ViktorinesServlet extends AppHttpServlet {
    private static final String VIKT_AUTH_TITLE_FOR_ADMIN_AND_TEACHER = "myViktorines";
    private static final String VIKT_AUTH_TITLE_FOR_STUDENT = "viktorines";
    private static final String VIKTORINES = "Викторины";
    private final ViktorineDAO hibernateViktorineDAO = new HibernateViktorineDAO();
    private final UserDAO userDAO = new HibernateUserDAO();
    static Logger logger = Logger.getLogger(ViktorinesServlet.class);

    public ViktorinesServlet() throws NamingException {
        //Default constructor
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (getAuthUsername() == null) {
            resp.sendError(403);
            return;
        }
        HtmlResponseWriter writer = null;
        try {
            writer = HtmlResponseWriter.create(resp);
        } catch (NamingException e) {
            logger.error(e);
        }
        List<Map<String, Object>> viktorinesToTemplate = new ArrayList<>();

        int role = userDAO.get(getAuthUsername()).getRoleId();
        switch (role) {
            case TemplateTags.ROLE_STUDENT:
                viktorinesToTemplate = viktorinesToTemplate(
                        hibernateViktorineDAO.getListByUsername(
                                userDAO.get(
                                        userDAO.get(getAuthUsername()).getOwnerId())
                                        .getLogin()));

                writer.setContentDelegate(VIKT_AUTH_TITLE_FOR_STUDENT);
                break;
            case TemplateTags.ROLE_TEACHER: {
                viktorinesToTemplate = viktorinesToTemplate(
                        hibernateViktorineDAO.getListByUsername(getAuthUsername()));

                writer.setContentDelegate(VIKT_AUTH_TITLE_FOR_ADMIN_AND_TEACHER)
                        .putData(TemplateTags.USERNAME, getAuthUsername());
                break;
            }
            case TemplateTags.ROLE_ADMIN: {
                viktorinesToTemplate = viktorinesToTemplate(
                        hibernateViktorineDAO.list());

                writer.setContentDelegate(VIKT_AUTH_TITLE_FOR_ADMIN_AND_TEACHER);
                break;
            }
            default:
                break;
        }
        try {
            writer.setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                    .setTitle(VIKTORINES)
                    .putData(TemplateTags.VIKTS, viktorinesToTemplate)
                    .write();
        } catch (NamingException e) {
            logger.error(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String actualUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        Viktorine v = hibernateViktorineDAO.get(Integer.valueOf(req.getParameter(TemplateTags.VIKT_ID)));
        if (v.getIdUser().intValue() == userDAO.get(actualUserName).getIdUser().intValue() ||
                userDAO.get(actualUserName).getRoleId() == 3) {
            hibernateViktorineDAO.delete(v);
        }
        resp.sendRedirect(Pages.VIKTORINES);
    }

    private List<Map<String, Object>> viktorinesToTemplate(List<Viktorine> viktorines) {

        viktorines = viktorines == null ? new ArrayList<>() : viktorines;

        List<Map<String, Object>> templateViktorines = new ArrayList<>();
        for (Viktorine vikt : viktorines) {
            Map<String, Object> templateVikt = new HashMap<>();
            templateVikt.put(TemplateTags.VIKT_ID, vikt.getIdVikt());
            templateVikt.put(TemplateTags.VIKT_NAME, vikt.getName());
            try {
                templateVikt.put(TemplateTags.VIKT_DISCIPLINE, (new HibernateDisciplineDAO()).getNameById(
                        vikt.getIdDicipline()));
            } catch (NamingException e) {
                logger.error(e);
            }
            templateVikt.put(TemplateTags.VIKT_ID_USER, vikt.getIdUser());
            templateViktorines.add(templateVikt);
        }
        return templateViktorines;
    }
}
