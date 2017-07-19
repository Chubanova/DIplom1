package com.mesaeva.viktorines.form.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Creates forms (just another boring POJO) from different sources.
 *
 * @param <T> the type of form
 */
public interface FormFactory<T> {

    /**
     * Creates a from from servlet request.
     *
     * @param request the http servlet request
     * @return the ready to use form
     */
    T createFromRequest(HttpServletRequest request) throws Exception;

    /**
     * Creates a form from map.
     *
     * @param data the data for form
     * @return the ready to use form
     * @throws Exception if something goes wrong
     */
    T createFromMap(Map<String, Object> data) throws Exception;

    /**
     * Creates a form from entities.
     *
     * @param entity the data for form
     * @return the ready to use form
     * @throws Exception if something goes wrong
     */
    T createFromEntity(Object entity) throws Exception;

}
