/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.resources.users;

import java.util.List;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.cmg.aeminium.datamodel.users.dao.identity.UserDAO;
import pt.cmg.aeminium.datamodel.users.dao.identity.UserDAO.UserFilter;
import pt.cmg.aeminium.datamodel.users.entities.identity.Role;
import pt.cmg.aeminium.datamodel.users.entities.identity.User;
import pt.cmg.aeminium.identity.api.rest.v1.resources.users.converters.UserConverter;
import pt.cmg.aeminium.identity.api.rest.v1.resources.users.dto.request.CreateUserDTO;
import pt.cmg.aeminium.identity.api.rest.v1.resources.users.dto.request.EditUserDTO;
import pt.cmg.aeminium.identity.api.rest.v1.resources.users.dto.request.SearchUsersFilterDTO;
import pt.cmg.aeminium.identity.api.rest.v1.resources.users.dto.response.UserDTO;
import pt.cmg.aeminium.identity.api.rest.v1.resources.users.validators.UserValidator;
import pt.cmg.aeminium.identity.tasks.users.UserCreator;
import pt.cmg.jakartautils.errors.ErrorDTO;
import pt.cmg.jakartautils.text.TextFormatter;

/**
 * @author Carlos Gonçalves
 */
@RequestScoped
@Transactional(value = TxType.REQUIRED)
@Path("users")
@Tag(name = "Users", description = "Endpoints related operations with users")
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
    @RolesAllowed({"GOD", "SCHOLAR"})
    @Transactional(value = TxType.SUPPORTS)
    @Operation(
        summary = "Retrieves a user by the id",
        description = "Obtains user info for a given identification number",
        operationId = "GET_user_by_id")
    @APIResponse(
        responseCode = "200",
        description = "User found. Return user data.",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDTO.class)))
    @APIResponse(
        responseCode = "400",
        description = "User with id does not exist.  Returns a list of the Errors",
        content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorDTO.class)))
    public Response getUser(@PathParam("id") Long id) {

        User user = userDAO.findById(id);

        if (user == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorDTO(1, TextFormatter.formatMessage("User with id {0} not found", id))).build();
        }

        return Response.ok(UserConverter.toUserDTO(user)).build();
    }

    @GET
    @Transactional(value = TxType.SUPPORTS)
    @RolesAllowed({"GOD", "SCHOLAR"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Searches for users",
        description = "Obtains a paginated list of users given a set of user input filters",
        operationId = "GET_users_filterd")
    @APIResponse(
        responseCode = "200",
        description = "Returns a user list that matches the search criteria",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.ARRAY, ref = "#/components/schemas/UserDTO")))
    @APIResponse(
        responseCode = "400",
        description = "User with id does not exist.  Returns a list of the Errors",
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(type = SchemaType.ARRAY, implementation = ErrorDTO.class),
            example = """
                        [
                            {
                            "code": 1001,
                            "description": "Email cannot be null or empty"
                            },
                            {
                            "code": 1002,
                            "description": "Name cannot be null or empty"
                            }
                        ]
                """))
    public Response getUsers(@Valid @BeanParam SearchUsersFilterDTO filter) {

        List<User> users = userDAO.findByFiltered(new UserFilter(filter.status, filter.roles, filter.email, filter.size, filter.offset));

        return Response.ok(UserConverter.toUsersDTO(users)).build();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Creates a new User",
        description = "Creates a new User, if authorized to do so.",
        operationId = "POST_users")
    @APIResponse(
        responseCode = "200",
        description = "Returns the newly created user",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(ref = "#/components/schemas/UserDTO")))
    @APIResponse(
        responseCode = "400",
        description = "A number of input parameters were not fit to create user.  Returns a list of the Errors",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.ARRAY, implementation = ErrorDTO.class)))
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
    @Operation(
        summary = "Edits a User",
        description = "Edits the User Details, if authorized to do so",
        operationId = "PUT_users")
    @APIResponse(
        responseCode = "200",
        description = "Returns the edited user",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(ref = "#/components/schemas/UserDTO")))
    @APIResponse(
        responseCode = "400",
        description = "A number of input parameters were not fit to edit user. Returns a list of the Errors",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.ARRAY, implementation = ErrorDTO.class)))
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
    @Operation(
        summary = "Edits the Roles of a User",
        description = "Edits Roles of a User, if authorized to do so",
        operationId = "PUT_users_details")
    @APIResponse(
        responseCode = "200",
        description = "Returns the edited user with new Roles",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(ref = "#/components/schemas/UserDTO")))
    @APIResponse(
        responseCode = "400",
        description = "A number of input parameters were not fit to edit user Roles. Returns a list of the Errors",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = SchemaType.ARRAY, implementation = ErrorDTO.class)))
    public Response editUseRoles(@PathParam("id") Long userId, @NotEmpty(message = "1001-Roles cannot be empty") List<Role.Name> roles) {

        if (roles.contains(Role.Name.GOD)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorDTO(1, "There can only be one GOD")).build();
        }

        User newUser = userCreator.editUserRoles(userId, roles);
        return Response.ok(UserConverter.toUserDTO(newUser)).build();
    }

}
