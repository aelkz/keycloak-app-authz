package org.keycloak.example.photoz;

import org.keycloak.example.photoz.cors.CORSFilter;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Basic auth app.
 */
@ApplicationPath("/")
public class PhotozApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        classes.add(CORSFilter.class);
        return classes;
    }

}
