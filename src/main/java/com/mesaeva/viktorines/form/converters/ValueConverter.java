package com.mesaeva.viktorines.form.converters;

import java.lang.reflect.Type;

/**
 * Converts objects from one type to another.
 */
public interface ValueConverter {

    /**
     * Converts the object from one type to another and throws tons of exceptions for sure.
     *
     * @param value      the object to convert
     * @param targetType the target type
     * @return an object of the target type
     */
    Object convert(Object value, Type targetType);
}
