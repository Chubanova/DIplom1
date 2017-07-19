package com.mesaeva.viktorines.form.util;

import com.mesaeva.viktorines.form.annotation.FormField;
import com.mesaeva.viktorines.form.converters.ValueConverter;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * The implementation of {@link FormFactory} which heavily depends on dark magic (aka java reflection api) and
 * use of {@link FormField} annotation.
 * WARNING: Current implementation doesn't support neither arrays nor collections.
 *
 * @param <T> the type of form
 */
// MAKE FINAL
public class DarkMagicFormFactory<T> implements FormFactory<T> {

    private static final String NO_ENTITY_FIELD = "";
    private Class<T> klass;

    public DarkMagicFormFactory(Class<T> klass) {
        this.klass = klass;
    }

    /**
     * Creates a from from servlet request.
     *
     * @param request the http servlet request
     * @return the ready to use form
     * @throws IllegalStateException when the form class is invalid
     * @throws NullPointerException  when request is null
     */
    @Override
    public T createFromRequest(HttpServletRequest request) {
        Map<String, String[]> parameters = request.getParameterMap();
        try {
            return createFrom(a -> parameters.get(a.formFieldName()));
        } catch (IllegalAccessException | InstantiationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Creates a form from map.
     *
     * @param data the data for form
     * @return the ready to use form
     * @throws IllegalAccessException probably will never happen
     * @throws InstantiationException hope will never happen
     * @throws NoSuchFieldException   probably will never happen
     */
    @Override
    public T createFromMap(Map<String, Object> data)
            throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        return createFrom(a -> data.get(a.formFieldName()));
    }

    /**
     * Creates a form from entities.
     *
     * @param entity the data for form
     * @return the ready to use form
     * @throws IllegalAccessException probably will never happen
     * @throws InstantiationException hope will never happen
     * @throws NoSuchFieldException   probably will never happen
     */
    @Override
    public T createFromEntity(Object entity)
            throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        return createFrom(a -> {
            String entityFieldName = a.entityFieldName();
            try {
                if (!entityFieldName.equals(NO_ENTITY_FIELD)) {
                    Field entityField = entity.getClass().getDeclaredField(entityFieldName);
                    entityField.setAccessible(true);
                    return entityField.get(entity);
                }
                return null;
            } catch (NoSuchFieldException | IllegalAccessException e) {
                return null;
            }
        });
    }

    /**
     * Creates a form
     *
     * @param dataMap a universal mechanism for retrieving data from user
     * @return the ready to use form
     * @throws IllegalAccessException probably will never happen
     * @throws InstantiationException hope will never happen
     * @throws NoSuchFieldException   probably will never happen
     */
    // CHANGE ACCESS LEVEL BACK TO PRIVATE
    protected T createFrom(Function<FormField, Object> dataMap)
            throws IllegalAccessException, InstantiationException{
        T form = klass.newInstance();

        List<Field> fields = FormUtil.getAllFields(klass);
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(FormField.class)) {
                // get annotation
                FormField annotation = field.getAnnotation(FormField.class);

                // extractSoyMap data
                Object value = dataMap.apply(annotation);

                // convert data
                // Certainly there must be a better method to get an instance of the converter, but this works too
                ValueConverter converter = annotation.valueConverter().newInstance();
                Type targetType = field.getGenericType();
                value = converter.convert(value, targetType);

                // set data
                field.set(form, value);
            }
        }

        return form;
    }
}
