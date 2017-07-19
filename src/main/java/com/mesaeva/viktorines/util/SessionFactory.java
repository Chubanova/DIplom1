package com.mesaeva.viktorines.util;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

public class SessionFactory implements ObjectFactory {

    private final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
            .configure()
            .build();

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
            throws Exception {
        return new MetadataSources(registry)
                .buildMetadata()
                .buildSessionFactory();
    }

    public void close() {
        StandardServiceRegistryBuilder.destroy(registry);
    }
}
