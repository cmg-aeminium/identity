/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.microprofile.auth.LoginConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import pt.cmg.aeminium.datamodel.common.entities.localisation.Language;
import pt.cmg.aeminium.identity.api.rest.v1.filters.request.ApplicationDataRequestFilter;
import pt.cmg.aeminium.identity.api.rest.v1.filters.request.LanguageSetterRequestFilter;
import pt.cmg.aeminium.identity.api.rest.v1.filters.request.UserLoaderRequestFilter;
import pt.cmg.aeminium.identity.api.rest.v1.resources.login.LoginResource;
import pt.cmg.aeminium.identity.api.rest.v1.resources.users.UserResource;
import pt.cmg.aeminium.identity.configuration.jsonb.JsonbProvider;
import pt.cmg.jakartautils.errors.ConstraintViolationExceptionMapper;

/**
 * @author Carlos Gonçalves
 */
@ApplicationScoped
@LoginConfig(authMethod = "MP-JWT")
@ApplicationPath("/v1")
public class IdentityApplication extends Application {

    public static final String REQUEST_HEADER_LANGUAGE = "aem-language";
    public static final String REQUEST_HEADER_APP_NAME = "aem-app";
    public static final String REQUEST_HEADER_APP_VERSION = "aem-app-version";

    public static final Language APP_DEFAULT_LANGUAGE = Language.DEFAULT_LANGUAGE;

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();

        resources.add(UserResource.class);
        resources.add(LoginResource.class);

        resources.add(LanguageSetterRequestFilter.class);
        resources.add(ApplicationDataRequestFilter.class);
        resources.add(UserLoaderRequestFilter.class);

        resources.add(JsonbProvider.class);
        resources.add(ConstraintViolationExceptionMapper.class);
        return resources;
    }

}
