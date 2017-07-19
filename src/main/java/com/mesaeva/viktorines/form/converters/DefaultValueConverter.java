package com.mesaeva.viktorines.form.converters;

import java.lang.reflect.Type;

/**
 * General "all-purpose" value converter. Use at your own risk.
 * <p>
 * The current implementation is very poor and supports the following transformations:
 * 1. T -> T
 * 2. String -> int
 */
public class DefaultValueConverter implements ValueConverter {

    /**
     * Converts the object from one type to another and throws tons of exceptions for sure.
     *
     * @param value      the object to convert
     * @param targetType the target type
     * @return an object of the target type
     */
    @Override
    public Object convert(Object value, Type targetType) {
        Object result = null;
        if (value == null) {
            result = getDefaultValue(targetType);
        } else {
            Class sourceType = value.getClass();
            if (sourceType == targetType) {
                result = value;
            } else {
                try {
                    if (sourceType == String.class && (targetType == int.class || targetType == Integer.class)) {
                        result= Integer.parseInt((String) value);
                    }
                    if (sourceType == String.class && (targetType == double.class || targetType == Double.class)) {
                        result = Double.parseDouble((String) value);
                    }
                } catch (NumberFormatException ignored){
                    result = null;
                }
            }
        }
        return result;
    }

    private Object getDefaultValue(Type targetType) {
        if (targetType == null) {
            throw new IllegalArgumentException();
        }
        if (targetType == int.class || targetType == double.class) {
            return 0;
        }
        if (targetType == String.class) {
            return null;
        }
        return null;
    }
}
