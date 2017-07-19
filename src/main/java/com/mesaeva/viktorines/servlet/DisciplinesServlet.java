package com.mesaeva.viktorines.servlet;

import com.mesaeva.viktorines.domain.dao.DisciplineDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateDisciplineDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateUserDAO;
import com.mesaeva.viktorines.domain.entities.Discipline;
import com.mesaeva.viktorines.form.forms.DisciplineForm;
import com.mesaeva.viktorines.form.util.DarkMagicFormFactory;
import com.mesaeva.viktorines.form.util.DefaultFormDataExtractor;
import com.mesaeva.viktorines.form.util.FormFactory;
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
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@WebServlet(Pages.DISCIPLINES)
public class DisciplinesServlet extends AppHttpServlet {
    private static final String CONTENT_DELEGATE = "dis";
    private static final String TITLE = "Дисциплины";
    private final DisciplineDAO disciplineDAO = new HibernateDisciplineDAO();
    private static FormFactory<DisciplineForm> disciplineFormFactory;

    final Logger logger = Logger.getLogger(DisciplinesServlet.class);

    public DisciplinesServlet() throws NamingException {
        //Default constructor
    }

    @Override
    public void init() throws ServletException {
        disciplineFormFactory = new DarkMagicFormFactory<>(DisciplineForm.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HtmlResponseWriter writer = HtmlResponseWriter.create(resp);
            if ((new HibernateUserDAO()).get(getAuthUsername()).getRoleId() == TemplateTags.ROLE_ADMIN) {
                sendPageData(writer, req, resp);
            }
        } catch (Exception e) {
            resp.sendRedirect(Pages.LOGIN);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter(TemplateTags.DISCIPLINE_NAME_FOR_REMOVE);
        try {
            if ((new HibernateUserDAO()).get(getAuthUsername()).getRoleId() == TemplateTags.ROLE_ADMIN) {
                if (name != null && !name.equals("")) {
                    removeDiscipline(resp, name);
                } else {
                    DisciplineForm form;
                    try {
                        form = disciplineFormFactory.createFromRequest(req);
                        Set<ConstraintViolation<DisciplineForm>> violations = validator.validate(form);
                        if (violations.isEmpty()) {
                            createDiscipline(form);
                            resp.sendRedirect(Pages.DISCIPLINES);
                        } else {
                            Map<String, Object> formData = new DefaultFormDataExtractor<>(DisciplineForm.class).
                                    extract(form, violations);
                            HtmlResponseWriter.create(resp)
                                    .setContentDelegate(CONTENT_DELEGATE)
                                    .setTitle(TITLE)
                                    .setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                                    .putData(TemplateTags.FORM_DATA, formData)
                                    .putData(TemplateTags.DISCIPLINES, disciplineDAO.list())
                                    .write();
                        }
                    } catch (Exception e) {
                        throw new ServletException(e);
                    }
                }
            } else {
                resp.sendRedirect(Pages.LOGIN);
            }
        } catch (NamingException e) {
            logger.error(e);
        }
    }

    private void removeDiscipline(HttpServletResponse resp, String name) {
        disciplineDAO.remove(disciplineDAO.getIdByName(name));
        try {
            resp.sendRedirect(Pages.DISCIPLINES);
        } catch (IOException e) {
            logger.error(e);
        }
    }
    private void createDiscipline(DisciplineForm form){
        Discipline d = new Discipline();
        d.setNameDiscipline(form.getName());
        try {
            disciplineDAO.create(d);
        } catch (Exception e) {
            logger.error(e);
        }
    }
    private void sendPageData(HtmlResponseWriter writer, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DisciplineForm form = new DisciplineForm();
        Map<String, Object> formData = new DefaultFormDataExtractor<>(DisciplineForm.class).extract(form, null);
        try {
            writer.setTitle(TITLE)
                    .setContentDelegate(CONTENT_DELEGATE)
                    .setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                    .putData(TemplateTags.DISCIPLINES, disciplineDAO.list())
                    .putData(TemplateTags.FORM_DATA, formData)
                    .write();
        } catch (Exception e) {
            resp.sendError(500);
            logger.error(e);
        }
    }
}
