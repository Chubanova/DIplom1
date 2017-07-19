package com.mesaeva.viktorines.util;

import com.google.template.soy.SoyFileSet;
import com.google.template.soy.tofu.SoyTofu;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import java.net.URL;
import java.util.Hashtable;

public class SoyTofuFactory implements ObjectFactory {

    @Override
    public SoyTofu getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment)
            throws Exception {
        return SoyFileSet.builder()
                .add(getResource("login.soy"))
                .add(getResource("profile.soy"))
                .add(getResource("layout.soy"))
                .add(getResource("forms.soy"))
                .add(getResource("mainMenu.soy"))
                .add(getResource("users.soy"))
                .add(getResource("edituser.soy"))
                .add(getResource("newuser.soy"))
                .add(getResource("myVikts.soy"))
                .add(getResource("vikt.soy"))
                .add(getResource("newviktorine.soy"))
                .add(getResource("dis.soy"))
                .add(getResource("newquestion.soy"))
                .build()
                .compileToTofu();
    }

    private URL getResource(String name) {
        return getClass().getResource(name);
    }
}
