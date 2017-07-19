package com.mesaeva.viktorines.servlet;

import com.mesaeva.viktorines.template.HtmlResponseWriter;
import com.mesaeva.viktorines.template.Pages;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(Pages.LOGIN)
public class LoginServlet extends AppHttpServlet {

    private static final String CONTENT_DELEGATE = "login";
    private static final String PAGE_TITLE = "Вход";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        boolean isAnon = SecurityContextHolder.getContext()
                .getAuthentication() instanceof AnonymousAuthenticationToken;

        if (isAnon) {
            HtmlResponseWriter.create(resp)
                    .setContentDelegate(CONTENT_DELEGATE)
                    .setTitle(PAGE_TITLE)
                    .setCsrfToken((CsrfToken) req.getAttribute("_csrf"))
                    .putData("error", req.getParameter("error") != null)
                    .write();
        } else {
            resp.sendRedirect(Pages.HOME);
        }
    }
}
