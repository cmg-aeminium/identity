/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.api.rest.resources.users;

import java.util.List;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
