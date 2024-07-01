/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.resources.login;

import java.util.Map;
import java.util.logging.Logger;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.cmg.aeminium.identity.api.rest.v1.resources.login.converters.LoginConverter;
import pt.cmg.aeminium.identity.api.rest.v1.resources.login.validators.LoginValidator;
import pt.cmg.aeminium.identity.tasks.jwt.JWTokenCreator;
import pt.cmg.aeminium.knowledge.dao.identity.UserDAO;
import pt.cmg.aeminium.knowledge.persistence.entities.identity.User;
import pt.cmg.jakartautils.text.TextFormatter;

/**
 * @author Carlos Gonçalves
 */
@RequestScoped
@Path("login")
public class LoginResource {

    private static final Logger LOGGER = Logger.getLogger(LoginResource.class.getName());

    @Inject
    private UserDAO userDAO;

    @Inject
    private LoginValidator loginValidator;

    @Inject
    private JWTokenCreator jwtokenCreator;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(@Context HttpHeaders headers) {

        var validationErrors = loginValidator.isValidLogin(headers);
        if (validationErrors.isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors.get()).build();
        }

        User user = userDAO.findByEmail(LoginConverter.extractUserEmail(headers));

        LOGGER.info(TextFormatter.formatMessageToLazyLog("User {0} logged in", user.getId()));

        return Response.ok(Map.of("token", jwtokenCreator.generateNewToken(user))).build();
    }

}
