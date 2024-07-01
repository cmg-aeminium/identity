/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.api.rest.resources.users;

import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.cmg.aeminium.knowledge.api.rest.resources.users.converters.UserConverter;
import pt.cmg.aeminium.knowledge.api.rest.resources.users.dto.request.CreateUserDTO;
import pt.cmg.aeminium.knowledge.api.rest.resources.users.dto.request.EditUserDTO;
import pt.cmg.aeminium.knowledge.api.rest.resources.users.validators.UserValidator;
import pt.cmg.aeminium.knowledge.dao.identity.UserDAO;
import pt.cmg.aeminium.knowledge.persistence.entities.identity.Role;
import pt.cmg.aeminium.knowledge.persistence.entities.identity.User;
import pt.cmg.aeminium.knowledge.tasks.users.UserCreator;
import pt.cmg.jakartautils.errors.ErrorDTO;

/**
 * @author Carlos Gonçalves
 */
@RequestScoped
@Path("users")
@RolesAllowed("GOD")
public class UserResource {

    @Inject
    private UserCreator userCreator;

    @Inject
    private UserDAO userDAO;

    @Inject
    private UserValidator userValidator;

    @GET
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response getUser(@PathParam("id") Long id) {

        User user = userDAO.findById(id);

        return Response.ok(UserConverter.toUserDTO(user)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createUser(@NotNull CreateUserDTO userDTO) {

        var validationErrors = userValidator.isValidUserForCreation(userDTO);
        if (validationErrors.isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors.get()).build();
        }

        User newUser = userCreator.creatUser(userDTO);
        return Response.ok(UserConverter.toUserDTO(newUser)).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PermitAll
    public Response editUser(@PathParam("id") Long userId, @NotNull EditUserDTO userDTO) {

        var validationErrors = userValidator.isValidUserForEdition(userId, userDTO);
        if (validationErrors.isPresent()) {
            return Response.status(Response.Status.BAD_REQUEST).entity(validationErrors.get()).build();
        }

        User newUser = userCreator.editUser(userId, userDTO);
        return Response.ok(UserConverter.toUserDTO(newUser)).build();
    }

    @PUT
    @Path("{id}/roles")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response editUser(@PathParam("id") Long userId, @NotEmpty(message = "1001-Roles cannot be empty") List<Role.Name> roles) {

        if (roles.contains(Role.Name.GOD)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorDTO(1, "There can only be one GOD")).build();
        }

        User newUser = userCreator.editUserRoles(userId, roles);
        return Response.ok(UserConverter.toUserDTO(newUser)).build();
    }

}
