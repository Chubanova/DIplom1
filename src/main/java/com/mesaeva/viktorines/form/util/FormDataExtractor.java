package com.mesaeva.viktorines.form.util;

import javax.validation.ConstraintViolation;
import java.util.Map;
import java.util.Set;

public interface FormDataExtractor<T> {
    /**
     * Extracts form data as a map.
     *
     * @param form       a form instance
     * @param violations constraint violations after validating a form
     * @return form data as a map
     * @throws Exception when something fails
     */
    Map<String, Object> extract(T form, Set<ConstraintViolation<T>> violations) throws Exception;
}
