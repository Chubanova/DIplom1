package com.mesaeva.viktorines.form.validators;

import com.mesaeva.viktorines.form.annotation.constraint.EqualFieldValues;
import org.apache.log4j.Logger;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class EqualFieldValuesValidator implements ConstraintValidator<EqualFieldValues, Object> {
    private String[] fieldNames;
    private String errorFieldName;
    Logger logger = Logger.getLogger(EqualFieldValuesValidator.class);

    @Override
    public void initialize(EqualFieldValues equalFieldValues) {
        fieldNames = equalFieldValues.fields();
        errorFieldName = equalFieldValues.errorField();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext constraintContext) {
        Object[] fieldValues = new Object[fieldNames.length];
        try {
            int i = 0;
            for (String fieldName : fieldNames) {
                Field field = null;
                for (Class<?> cls = object.getClass(); cls != null; cls = cls.getSuperclass()) {
                    try {
                        field = cls.getDeclaredField(fieldName);
                        break;
                    } catch (NoSuchFieldException e) {
                        field = null;
                    }
                }
                if (field == null)
                    continue;
                field.setAccessible(true);
                fieldValues[i] = field.get(object);
                for (int j = 0; j < i; j++) {
                    if (((fieldValues[j] != null) && !fieldValues[j].equals(fieldValues[i])) ||
                            ((fieldValues[j] == null) && (fieldValues[i] != null))) {
                        constraintContext.disableDefaultConstraintViolation();
                        constraintContext.buildConstraintViolationWithTemplate(
                                constraintContext.getDefaultConstraintMessageTemplate()
                        ).addPropertyNode(errorFieldName).addConstraintViolation();
                        return false;
                    }
                }
                ++i;
            }
            return true;
        } catch (Exception e) {
            logger.error(e);
            constraintContext.disableDefaultConstraintViolation();
            constraintContext.buildConstraintViolationWithTemplate("При обработке данных произошла ошибка")
                    .addPropertyNode(errorFieldName).addConstraintViolation();
            return false;
        }
    }
}
