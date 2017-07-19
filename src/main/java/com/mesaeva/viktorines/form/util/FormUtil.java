package com.mesaeva.viktorines.form.util;

import com.mesaeva.viktorines.form.annotation.FormField;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FormUtil {
    private FormUtil() {
        //Default constructor
    }

    public static Field findField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        Field result = null;
        for (Class<?> cls = clazz; cls != null; cls = cls.getSuperclass()) {
            for (Field field : cls.getDeclaredFields()) {
                if (field.getName().equals(fieldName)) {
                    result = field;
                    break;
                }
            }
            if (result != null) {
                break;
            }
        }
        if (result == null) {
            throw new NoSuchFieldException();
        }
        return result;
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        for (Class cls = clazz; cls != null; cls = cls.getSuperclass()) {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
        }
        return fields;
    }

    public static void updateEntity(Object entity, Object form)
            throws IllegalAccessException, NoSuchFieldException {
        List<Field> fields = getAllFields(form.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(FormField.class)) {
                FormField annotation = field.getAnnotation(FormField.class);
                String fieldName = annotation.entityFieldName();
                if (!fieldName.equals("")) {
                    Object value = field.get(form);

                    // IMPORTANT: convert value here

                    Field entityField = entity.getClass().getDeclaredField(fieldName);
                    entityField.setAccessible(true);
                    entityField.set(entity, value);
                }
            }
        }
    }
}
