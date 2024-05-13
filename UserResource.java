/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.api.rest.resources.users;

import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import pt.cmg.aeminium.knowledge.api.rest.resources.users.converters.UserConverter;
import pt.cmg.aeminium.knowledge.api.rest.resources.users.dto.request.CreateUserDTO;
import pt.cmg.aeminium.knowledge.api.rest.resources.users.validators.UserValidator;
import pt.cmg.aeminium.knowledge.dao.identity.UserDAO;
import pt.cmg.aeminium.knowledge.persistence.entities.identity.User;

/**
 * @author Carlos Gonçalves
 */
@RequestScoped
@Path("users")
public class UserResource {

    private static final Logger LOGGER = Logger.getLogger(UserResource.class.getName());

    @Inject
    private UserDAO userDAO;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@NotNull CreateUserDTO userDTO) {

        var validationErrors = UserValidator.isValidUserForCreation(userDTO);
        if (validationErrors.isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors.get()).build();
        }

        User newUser = UserConverter.toUser(userDTO);
        userDAO.create(newUser, true); // true because I need the ID

        return Response.ok(UserConverter.toUserDTO(newUser)).build();

    }

}
