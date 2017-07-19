package com.mesaeva.viktorines.util;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import javax.validation.Validation;
import javax.validation.Validator;

import java.util.Hashtable;

public class ValidatorFactory implements ObjectFactory {

    @Override
    public Validator getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
            throws Exception {
        javax.validation.ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        return factory.getValidator();
    }
}
