package com.mesaeva.viktorines.form.util;

import com.mesaeva.viktorines.form.annotation.Form;
import com.mesaeva.viktorines.form.annotation.FormField;
import com.mesaeva.viktorines.form.annotation.FormFieldOption;
import com.mesaeva.viktorines.form.converters.ValueConverter;

import javax.validation.ConstraintViolation;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

import static com.mesaeva.viktorines.template.TemplateTags.FormTags.*;

/**
 * Extracts data from a form in format compatible with forms.simpleForm soy template.
 *
 * @param <T> a form type
 */
public class DefaultFormDataExtractor<T> implements FormDataExtractor<T> {
    protected static final String NO_EXTRACTIVE_TYPE = "";
    protected static final String NO_OPTIONS = "";

    protected Class<T> formClass;

    public DefaultFormDataExtractor(Class<T> formClass) {
        if (!formClass.isAnnotationPresent(Form.class)) {
            throw new IllegalArgumentException("Not a form class");
        }
        this.formClass = formClass;
    }



    protected Map<String, String> collectErrors(Set<ConstraintViolation<T>> violations) {
        Map<String, String> errors = new HashMap<>();
        for (ConstraintViolation<T> violation : violations) {
            String key = violation.getPropertyPath().toString();
            if (!errors.containsKey(key)) {
                errors.put(key, violation.getMessage());
            } else {
                String value = errors.get(key);
                errors.put(key, value + ", " + violation.getMessage());
            }
        }
        return errors;
    }

    private boolean isList(Class<?> clazz) {
        return List.class.isAssignableFrom(clazz);
    }

    protected List defaultOptions() {
        Map<String, Object> option = new HashMap<>();
        option.put(OPTION_LABEL, "");
        option.put(OPTION_VALUE, "");

        List<Object> optionsList = new ArrayList<>();
        optionsList.add(option);

        return optionsList;
    }

    protected List defaultValues() {
        return new ArrayList<>();
    }

    protected void putMessage(Form formAnnotation, Map<String, String> fieldsErrors, Map<String, Object> formData) {
        if (fieldsErrors == null) {
            formData.put(MESSAGE, null);
            return;
        }
        formData.put(MESSAGE, (fieldsErrors.size() > 0) ?
                formAnnotation.failMessage() : formAnnotation.successMessage());
    }

    protected void putValue(Field formField, FormField fieldMeta, T form, Map<String, Object> fieldData)
            throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        Object value = formField.get(form);
        if (value == null) {
            fieldData.put(VALUES, defaultValues());
            fieldData.put(VALUE, null);
            return;
        }

        ValueConverter converter = fieldMeta.valueConverter().newInstance();
        Class<?> extractiveType = fieldMeta.extractiveType().equals(NO_EXTRACTIVE_TYPE) ?
                value.getClass() : Class.forName(fieldMeta.extractiveType());

        if (isList(value.getClass())) {
            fieldData.put(VALUES, converter.convert(value, extractiveType));
            fieldData.put(VALUE, null);
        } else {
            fieldData.put(VALUE, converter.convert(value, extractiveType));
            fieldData.put(VALUES, defaultValues());
        }
    }

    protected void putOptions(Field field, FormField formFieldMeta, T form, Map<String, Object> fieldData)
            throws NoSuchFieldException, IllegalAccessException, IllegalClassFormatException {
        if (!formFieldMeta.formFieldOptions().equals(NO_OPTIONS)) {
            Field optionsField = FormUtil.findField(formClass, formFieldMeta.formFieldOptions());
            optionsField.setAccessible(true);

            if (isList(optionsField.getType())) {
                ParameterizedType optionsFieldType = (ParameterizedType) optionsField.getGenericType();
                Class<?> optionClass = (Class<?>) optionsFieldType.getActualTypeArguments()[0];

                if (optionClass.isAnnotationPresent(FormFieldOption.class)) {
                    FormFieldOption optionMeta = optionClass.getAnnotation(FormFieldOption.class);

                    Field optionValueField = optionClass.getDeclaredField(optionMeta.valueField());
                    optionValueField.setAccessible(true);

                    Field optionLabelField = optionClass.getDeclaredField(optionMeta.labelField());
                    optionLabelField.setAccessible(true);

                    List<Map<String, Object>> optionsData = new ArrayList<>();
                    List optionsList = (List) optionsField.get(form);
                    for (Object option : optionsList) {
                        Map<String, Object> optionData = new HashMap<>();
                        optionData.put(OPTION_VALUE, optionValueField.get(option));
                        optionData.put(OPTION_LABEL, optionLabelField.get(option));
                        optionsData.add(optionData);
                    }

                    fieldData.put(OPTIONS, optionsData);
                } else {
                    throw new IllegalClassFormatException(optionClass.getCanonicalName() +
                            " is not a form field option class");
                }
            } else {
                throw new IllegalClassFormatException("options field " + optionsField.getName() +
                        " of type " + optionsField.getType().getCanonicalName() +
                        " for form field " + field.getName() + " is not a list");
            }
        } else {
            fieldData.put(OPTIONS, defaultOptions());
        }
    }

    protected void putError(Field formField, Map<String, String> fieldsErrors, Map<String, Object> fieldData) {
        if (fieldsErrors == null) {
            fieldData.put(ERROR, null);
            return;
        }
        fieldData.put(ERROR, fieldsErrors.get(formField.getName()));
    }

    /**
     * Extracts data from a form instance in appropriate format for forms.simpleForm template.
     * Form metadata is obtained from {@link Form} annotation of form class, {@link FormField} annotations
     * of form class fields and {@link FormFieldOption} annotations of classes representing possible values for
     * some form class fields.
     *
     * @param form       form instance
     * @param violations constraint violations after validating a form
     * @return map of the following format:
     * ["action" : URI to send form data;
     * "submitLabel" : label on submit button;
     * "failed" : true if form is invalid;
     * "message" : message about the result of validation or something else;
     * "fields" : list of maps, each one containing single form field data]
     * "fields" key keeps a list of maps of the following format:
     * ["name" : name of the request parameter bound to the field,
     * "value" : value of the field if it's not a list else null;
     * "values" : list of values or null if value is not multiple;
     * "options" : list of maps each representing possible value for the field;
     * "label" : label near the field;
     * "error" : error message if value is invalid else null]
     * "options" key keeps a list of maps of following format:
     * ["label" : label near the possible value of the field;
     * "value" : possible value of the field]
     * @throws IllegalStateException if reflection, data converting, field options or values extraction fails.
     *                               For example, if a field representing an option for another field is not of {@link List} type.
     */
    public Map<String, Object> extract(T form, Set<ConstraintViolation<T>> violations) {
        Map<String, String> fieldsErrors = violations != null ? collectErrors(violations) : null;

        Form formAnnotation = formClass.getAnnotation(Form.class);

        Map<String, Object> formData = new HashMap<>();

        formData.put(ACTION, formAnnotation.action());
        putMessage(formAnnotation, fieldsErrors, formData);
        formData.put(SUBMIT_LABEL, formAnnotation.submitLabel());
        formData.put(FAILED, fieldsErrors != null && fieldsErrors.size() > 0);

        List<Map<String, Object>> formFieldsData = new ArrayList<>();

        try {
            List<Field> fields = FormUtil.getAllFields(formClass);
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(FormField.class)) {
                    FormField meta = field.getAnnotation(FormField.class);
                    Map<String, Object> fieldData = new HashMap<>();

                    fieldData.put(NAME, meta.formFieldName());
                    putValue(field, meta, form, fieldData);
                    putOptions(field, meta, form, fieldData);
                    fieldData.put(LABEL, meta.formFieldLabel());
                    fieldData.put(TYPE, meta.formFieldType());
                    putError(field, fieldsErrors, fieldData);

                    formFieldsData.add(fieldData);
                }
            }

            formData.put(FIELDS, formFieldsData);

            return formData;
        } catch (ClassNotFoundException | InstantiationException | IllegalClassFormatException |
                IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalStateException(e);
        }
    }
}
