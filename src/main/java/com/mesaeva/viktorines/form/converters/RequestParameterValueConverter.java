package com.mesaeva.viktorines.form.converters;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A wrapper around default converter for converting from values extracted from a request data
 * obtained by calling getParameterMap() method on a ServletRequest instance.
 */
public class RequestParameterValueConverter implements ValueConverter {

    protected ValueConverter converter = new DefaultValueConverter();

    /**
     * Converts given value to given type considering the value may be a request parameter value;
     *
     * @param value      the object to convert
     * @param targetType the target type
     * @return 1) a list with source array's elements converted by the wrapped converter
     * if value is of String[] type and target type is a generic list type;
     * 2) a list with unconverted source array's elements
     * if value is of String[] type and target type is not generic list type;
     * 3) only the first element of source array converted to target type
     * if value is of String[] type and target type is not a list type;
     * 4) a value converted by the wrapped converter in other cases
     * @throws IllegalArgumentException when target type is null
     */
    @Override
    public Object convert(Object value, Type targetType){
        if (targetType == null)
            throw new IllegalArgumentException("Target type is null");

        if (value != null && value.getClass().equals(String[].class)) {
            String[] reqParamValue = (String[]) value;

            if (targetType instanceof ParameterizedType) {
                Class<?> targetClass = (Class) (((ParameterizedType) targetType).getRawType());
                if (List.class.isAssignableFrom(targetClass)) {
                    Class<?> itemType = (Class) ((ParameterizedType) targetType).getActualTypeArguments()[0];

                    List<Object> convertedValue = new ArrayList<>(reqParamValue.length);
                    for (String elem : reqParamValue) {
                        convertedValue.add(converter.convert(elem, itemType));
                    }
                    // 1)
                    return convertedValue;
                }
            } else if (List.class.isAssignableFrom((Class) targetType)) {
                // 2)
                return Arrays.asList(reqParamValue);
            }
            //3)
            return converter.convert(reqParamValue[0], targetType);
        }
        //4)
        return converter.convert(value, targetType);
    }

}
