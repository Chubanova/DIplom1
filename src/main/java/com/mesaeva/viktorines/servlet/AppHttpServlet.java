package com.mesaeva.viktorines.servlet;

import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.validation.Validator;

public abstract class AppHttpServlet extends HttpServlet {

    @Resource(name = "java:comp/Validator")
    protected static Validator validator;

    String getAuthUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
