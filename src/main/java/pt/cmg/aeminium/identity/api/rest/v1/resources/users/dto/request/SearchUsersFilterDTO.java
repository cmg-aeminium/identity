/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.resources.users.dto.request;

import java.util.Set;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import pt.cmg.aeminium.datamodel.users.entities.identity.Role;
import pt.cmg.aeminium.datamodel.users.entities.identity.User.Status;

/**
 * @author Carlos Gonçalves
 */
public class SearchUsersFilterDTO {

    @QueryParam("email")
    public String email;

    @QueryParam("status")
    public Set<@NotNull Status> status;

    @QueryParam("role")
    public Set<Role.@NotNull Name> roles;

    @QueryParam("size")
    @DefaultValue("30")
    @Min(value = 0)
    public Long size;

    @QueryParam("offset")
    @DefaultValue("0")
    @Min(value = 0)
    public Long offset;

}
