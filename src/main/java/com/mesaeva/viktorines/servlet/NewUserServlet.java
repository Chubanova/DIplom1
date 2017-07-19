package com.mesaeva.viktorines.servlet;

import com.mesaeva.viktorines.domain.dao.UserDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateRoleDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateUserDAO;
import com.mesaeva.viktorines.domain.entities.User;
import com.mesaeva.viktorines.form.forms.RegistrationForm;
import com.mesaeva.viktorines.form.util.DarkMagicFormFactory;
import com.mesaeva.viktorines.form.util.DefaultFormDataExtractor;
import com.mesaeva.viktorines.form.util.FormFactory;
import com.mesaeva.viktorines.template.HtmlResponseWriter;
import com.mesaeva.viktorines.template.Pages;
import com.mesaeva.viktorines.template.TemplateTags;
import org.apache.log4j.Logger;
import org.springframework.security.web.csrf.CsrfToken;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

@WebServlet(Pages.USER_REGISTRATION)
public class NewUserServlet extends AppHttpServlet {

    private static final String CONTENT_DELEGATE = "newuser";
    private static final String PAGE_TITLE = "Создать нового пльзователя";
    @Resource(name = "java:comp/Validator")
    private final UserDAO userDAO = new HibernateUserDAO();
    private static FormFactory<RegistrationForm> registrationFormFactory;
    static Logger logger = Logger.getLogger(NewUserServlet.class);

    public NewUserServlet() throws NamingException {
        //Default constructor
    }

    @Override
    public void init() throws ServletException {
        registrationFormFactory = new DarkMagicFormFactory<>(RegistrationForm.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Integer role = TemplateTags.ROLE_STUDENT;
        try {
            role = userDAO.get(getAuthUsername()).getRoleId();
            if (role != TemplateTags.ROLE_TEACHER && role != TemplateTags.ROLE_ADMIN) {
                resp.sendRedirect(Pages.LOGIN);
            }
        } catch (Exception e) {
            resp.sendError(403);
        }
        Map<String, Object> formData;
        try {
            RegistrationForm form = new DarkMagicFormFactory<>(RegistrationForm.class).createFromRequest(req);
            formData = new DefaultFormDataExtractor<>(RegistrationForm.class).extract(form, null);
        } catch (Exception e) {
            logger.error(e);
            return;
        }
        if (role == TemplateTags.ROLE_ADMIN) {
            try {
                HtmlResponseWriter writer = HtmlResponseWriter.create(resp);
                writer.putData(TemplateTags.TEACHERS, userDAO.listTeachers())
                        .putData(TemplateTags.ROLES, (new HibernateRoleDAO()).list())
                        .putData(TemplateTags.IS_ADMIN, "true");
                writer.setContentDelegate(CONTENT_DELEGATE)
                        .setTitle(PAGE_TITLE)
                        .setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                        .putData(TemplateTags.FORM_DATA, formData)
                        .write();
            } catch (NamingException e) {
                logger.error(e);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RegistrationForm form;
        try {
            form = registrationFormFactory.createFromRequest(req);
            Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form);
            if (violations.isEmpty()) {
                User user = new User();
                user.setLogin(form.getLogin());
                user.setHashpass(form.getPassword());
                user.setRoleId(req.getParameter(TemplateTags.USER_ROLE) == null ?
                        TemplateTags.ROLE_STUDENT :
                        (new HibernateRoleDAO()).get(req.getParameter(TemplateTags.USER_ROLE)).getId());
                user.setOwnerId(user.getRoleId() == TemplateTags.ROLE_STUDENT ?
                        req.getParameter(TemplateTags.USER_OWNER_ID) == null ||
                                req.getParameter(TemplateTags.USER_OWNER_ID).equals("") ?
                                userDAO.get(getAuthUsername()).getIdUser() :
                                userDAO.get(req.getParameter(TemplateTags.USER_OWNER_ID), 1).getIdUser() : null);
                user.setEnabled(1);
                user.setUsername(form.getUsername());
                try {
                    userDAO.create(user);
                    resp.sendRedirect(Pages.USERS_MANAGEMENT);
                } catch (Exception e) {
                    resp.getWriter().print("Error. User with same login is already registered.");
                }
            } else {
                Map<String, Object> formData = new DefaultFormDataExtractor<>(RegistrationForm.class).
                        extract(form, violations);
                int role = userDAO.get(getAuthUsername()).getRoleId();
                HtmlResponseWriter writer = HtmlResponseWriter.create(resp);
                if (role == TemplateTags.ROLE_ADMIN) {
                    writer.putData(TemplateTags.TEACHERS, userDAO.listTeachers())
                            .putData(TemplateTags.ROLES, (new HibernateRoleDAO()).list())
                            .putData(TemplateTags.IS_ADMIN, "true");
                }
                writer.setContentDelegate(CONTENT_DELEGATE)
                        .setTitle(PAGE_TITLE)
                        .setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                        .putData(TemplateTags.FORM_DATA, formData)
                        .write();
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
