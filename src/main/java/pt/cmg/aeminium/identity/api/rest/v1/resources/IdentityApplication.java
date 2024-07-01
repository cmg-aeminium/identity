/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.resources;

import org.eclipse.microprofile.auth.LoginConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * @author Carlos Gonçalves
 */
@ApplicationScoped
@LoginConfig(authMethod = "MP-JWT")
@ApplicationPath("/v1")
public class IdentityApplication extends Application {

}
