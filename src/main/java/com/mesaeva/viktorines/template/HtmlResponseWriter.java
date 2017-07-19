package com.mesaeva.viktorines.template;

import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.tofu.SoyTofu;
import com.mesaeva.viktorines.domain.dao.UserDAO;
import com.mesaeva.viktorines.domain.dao.impl.HibernateUserDAO;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.csrf.CsrfToken;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The main purpose of the class is to simplify writing HTML responses using
 * closure templates and spring security framework.
 * <p>
 * The <code>write</code> method of the class do the following:
 * 1. Sets up proper content type of http response
 * 2. Creates new closure template renderer
 * 3. Passes necessary data to the renderer
 * 4. Renders a template to the {@link HttpServletResponse} object
 * <p>
 * To use the class you need to perform the following steps:
 * 1. Obtain an instance of the class using <code>create</code> method
 * 2. Set title and content delegate with <code>setTitle</code> and
 * <code>setContentDelegate</code> methods
 * 3. Write the response with <code>write</code> method
 * <p>
 * Optionally you can change the template layout with <code>setLayout</code>
 * method, set the csrf token with <code>setCsrfToken</code> method and add
 * template data using <code>putData</code> method.
 * <p>
 * Note: if you set custom layout methods <code>setTitle</code> and
 * <code>setContentDelegate</code> will do nothing.
 * <p>
 * Example:
 * <code>
 * HtmlResponseWriter.create(response)
 * .setContentDelegate("helloWorld")
 * .setTitle("Hello world")
 * .setCsrfToken((CsrfToken) request.getAttribute("_csrf"))
 * .putData("greeting": "Hello world!")
 * .write();
 * </code>
 */
public final class HtmlResponseWriter {

    private static final String ANON_MENU_DELEGATE = "anon";
    private static final String STUDENT_MENU_DELEGATE = "student";
    private static final String TEACHER_MENU_DELEGATE = "teacher";
    private static final String ADMIN_MENU_DELEGATE = "admin";
    private static final String MAIN_MENU_DELEGATE = "mainMenuDelegate";
    private static final String DEFAULT_LAYOUT = "layout.main";

    private SoyTofu tofu;

    private SoyMapData data = new SoyMapData();
    private SoyMapData ijData = new SoyMapData();

    private HttpServletResponse response;

    private String layout = DEFAULT_LAYOUT;

    private String title;
    private String contentDelegate;

    Logger logger = Logger.getLogger(HtmlResponseWriter.class);

    private HtmlResponseWriter(HttpServletResponse response) throws NamingException {
        this.response = response;

        // "inject" the SoyTofu object without CDI or something else :(
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            tofu = (SoyTofu) envContext.lookup("viktorines/SoyTofu");
        } catch (NamingException e) {
            logger.error(e);
            throw e;
        }
    }

    /**
     * Creates an instance of {@link HtmlResponseWriter}
     *
     * @param response the response to write
     * @return an instance of {@link HtmlResponseWriter}
     */
    public static HtmlResponseWriter create(HttpServletResponse response) throws NamingException {
        return new HtmlResponseWriter(response);
    }

    /**
     * Sets a non-default layout
     * <p>
     * Note: if you set custom layout the menu delegate will not be
     * automatically attached
     *
     * @param layout the layout to set
     * @return this
     */
    public HtmlResponseWriter setLayout(String layout) {
        this.layout = layout;
        return this;
    }

    /**
     * Sets a page title
     *
     * @param title a page title of your dream
     * @return this
     */
    public HtmlResponseWriter setTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * Sets a content delegate
     *
     * @param contentDelegate a content delegate you're going to use
     * @return this
     */
    public HtmlResponseWriter setContentDelegate(String contentDelegate) {
        this.contentDelegate = contentDelegate;
        return this;
    }

    /**
     * Sets a csrf token as templates' injected data
     *
     * @param token the csrf token to put in HTML forms
     * @return this
     */
    public HtmlResponseWriter setCsrfToken(CsrfToken token) {
        ijData.put("csrfName", token.getParameterName());
        ijData.put("csrfValue", token.getToken());
        return this;
    }

    /**
     * Sets the data to call the template with
     * <p>
     * Note: The value type must be supported by closure templates. You can
     * read more about supported types
     * <a href="https://developers.google.com/closure/templates/docs/concepts#basic-types">here</a>
     *
     * @param key   the variable name
     * @param value the variable value
     * @return this
     */
    public HtmlResponseWriter putData(String key, Object value) {
        data.put(key, value);
        return this;
    }

    /**
     * Sets up the content type of response and renders everything to it
     *
     * @throws IOException           if an input or output exception occurred
     * @throws IllegalStateException if missing some mandatory data
     */
    public void write() throws IOException, NamingException {
        buildSoyData();
        response.setContentType("text/html; charset=UTF-8");
        tofu.newRenderer(layout)
                .setIjData(ijData)
                .setData(data)
                .render(response.getWriter());
    }

    private void buildSoyData() throws NamingException {
        if (layout.equals(DEFAULT_LAYOUT)) {
            if (contentDelegate == null || title == null) {
                throw new IllegalStateException("Content delegate name or page title is missing");
            }
            UserDAO userDAO = new HibernateUserDAO();

            int key = 0;

            if (!(SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken)) {
                key = userDAO.get(SecurityContextHolder.getContext().getAuthentication().getName()).getRoleId();
            }

            String val;
            switch (key) {
                case 1:
                    val = STUDENT_MENU_DELEGATE;
                    break;
                case 2:
                    val = TEACHER_MENU_DELEGATE;
                    break;
                case 3:
                    val = ADMIN_MENU_DELEGATE;
                    break;
                default:
                    val = ANON_MENU_DELEGATE;
                    break;
            }

            data.put(MAIN_MENU_DELEGATE, val);
            data.put("contentDelegate", contentDelegate);
            data.put("title", title);
        }
    }
}
