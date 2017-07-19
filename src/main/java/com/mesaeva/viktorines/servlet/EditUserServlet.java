package com.mesaeva.viktorines.servlet;

import com.mesaeva.viktorines.domain.dao.UserDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateRoleDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateUserDAO;
import com.mesaeva.viktorines.domain.entities.User;
import com.mesaeva.viktorines.form.forms.ProfileForm;
import com.mesaeva.viktorines.form.util.DarkMagicFormFactory;
import com.mesaeva.viktorines.form.util.DefaultFormDataExtractor;
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

@WebServlet(Pages.EDIT_USER)
public class EditUserServlet extends AppHttpServlet {

    private static final String EDIT_PROFILE_TITLE = "Редактирование профиля";
    private static final String EDIT_PROFILE_CONTENT_DELEGATE = "editProfile";
    private final UserDAO userDAO = new HibernateUserDAO();

   static Logger logger = Logger.getLogger(EditUserServlet.class);

    public EditUserServlet() throws NamingException {
        //Default constructor
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = userDAO.get(req.getParameter(TemplateTags.USER_LOGIN));
        Map<String, Object> formData;
        try {
            HtmlResponseWriter writer = HtmlResponseWriter.create(resp);
            ProfileForm form = new DarkMagicFormFactory<>(ProfileForm.class).createFromEntity(user);
            formData = new DefaultFormDataExtractor<>(ProfileForm.class).extract(form, null);
            formData.put(TemplateTags.USER_LOGIN, user.getLogin());
            if (userDAO.get(getAuthUsername()).getRoleId() == TemplateTags.ROLE_ADMIN) {
                writer.putData(TemplateTags.IS_ADMIN, "true")
                        .putData(TemplateTags.ROLE, (new HibernateRoleDAO()).get(user.getRoleId()).getAuthority());
                if (user.getOwnerId() != null) {
                    writer.putData(TemplateTags.TEACHER_NAME, userDAO.get(user.getOwnerId()).getUsername());
                }
            }
            writer.putData(TemplateTags.TEACHERS, userDAO.listTeachers())
                    .putData(TemplateTags.ROLES, (new HibernateRoleDAO()).list());
            writer.setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                    .setTitle(EDIT_PROFILE_TITLE)
                    .setContentDelegate(EDIT_PROFILE_CONTENT_DELEGATE)
                    .putData(TemplateTags.FORM_DATA, formData)
                    .write();
        } catch (Exception e) {
            resp.sendError(500);
            logger.error(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String username = req.getParameter(TemplateTags.USER_LOGIN);
            ProfileForm form = new DarkMagicFormFactory<>(ProfileForm.class).
                    createFromRequest(req);
            Set<ConstraintViolation<ProfileForm>> violations = validator.validate(form);
            if (violations.isEmpty()) {
                User u = userDAO.get(username);
                u.setUsername(form.getUsername());
                if (form.getPassword() != null && !form.getPassword().equals("")) {
                    u.setHashpass(form.getPassword());
                }
                u.setEnabled(form.getEnabled());
                String role = req.getParameter(TemplateTags.USER_ROLE);
                String owner = req.getParameter(TemplateTags.USER_OWNER_ID);
                if (role != null && !role.equals("")) {
                    u.setRoleId((new HibernateRoleDAO()).get(role).getId());
                }
                if (owner != null && !owner.equals("") && u.getRoleId() == TemplateTags.ROLE_STUDENT) {
                    u.setOwnerId(userDAO.get(owner, 1).getIdUser());
                }
                userDAO.update(u);
                resp.sendRedirect(Pages.USERS_MANAGEMENT);
            }
            Map<String, Object> formData = new DefaultFormDataExtractor<>(ProfileForm.class).extract(form, violations);
            formData.put(TemplateTags.USER_LOGIN, username);
            HtmlResponseWriter.create(resp)
                    .setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                    .setTitle(EDIT_PROFILE_TITLE)
                    .setContentDelegate(EDIT_PROFILE_CONTENT_DELEGATE)
                    .putData(TemplateTags.FORM_DATA, formData)
                    .write();
        } catch (Exception e) {
            resp.sendError(500);
            logger.error(e);
        }
    }
}
