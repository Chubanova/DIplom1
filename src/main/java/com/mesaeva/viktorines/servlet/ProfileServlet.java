package com.mesaeva.viktorines.servlet;

import com.mesaeva.viktorines.domain.dao.UserDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateUserDAO;
import com.mesaeva.viktorines.template.HtmlResponseWriter;
import com.mesaeva.viktorines.template.Pages;
import com.mesaeva.viktorines.template.TemplateTags;
import org.springframework.security.web.csrf.CsrfToken;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(Pages.HOME)
public class ProfileServlet extends AppHttpServlet {

    private static final String PROFILE_AUTH_TITLE = "Мой профиль";
    private static final String PROFILE_AUTH_CONTENT_DELEGATE = "profileAuth";
    private final UserDAO userDAO = new HibernateUserDAO();

    public ProfileServlet() throws NamingException{
        //Default constructor
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int role = userDAO.get(getAuthUsername()).getRoleId();
        if (role == 1 || role == 2)
            resp.sendRedirect(Pages.VIKTORINES);
        else {
            HtmlResponseWriter writer = HtmlResponseWriter.create(resp);
            writer.putData(TemplateTags.USERNAME, getAuthUsername())
                    .setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                    .setTitle(PROFILE_AUTH_TITLE)
                    .setContentDelegate(PROFILE_AUTH_CONTENT_DELEGATE)
                    .write();
        }
    }
}