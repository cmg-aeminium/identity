/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.filters.request;

import java.io.IOException;
import org.eclipse.microprofile.jwt.JsonWebToken;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import pt.cmg.aeminium.datamodel.users.dao.identity.UserDAO;

/**
 * @author Carlos Gonçalves
 */
public class UserLoaderRequestFilter implements ContainerRequestFilter {

    @Inject
    @RequestData
    private Event<Long> userEventHandler;

    @Inject
    private JsonWebToken jwtToken;

    @Inject
    private UserDAO userDAO;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if (jwtToken.getRawToken() == null) {
            return;
        }

        Long sub = Long.valueOf(jwtToken.getSubject());

        // I will check the cache anyway. There really isn't much point as the JWT was generated by me and if it reaches this point
        // I know it is valid... but I don't trust computers.
        if (userDAO.findById(sub) == null) {
            requestContext.abortWith(Response.status(Status.FORBIDDEN).build());
        }

        userEventHandler.fire(sub);
    }

}
