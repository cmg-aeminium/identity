/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.resources.users.dto.request;

import java.util.List;
import javax.management.relation.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import pt.cmg.aeminium.knowledge.persistence.entities.localisation.Language;

/**
 * @author Carlos Gonçalves
 */
public class CreateUserDTO {

    public String name;

    @NotBlank(message = "1001-Email cannot be null or empty")
    public String email;

    @NotBlank(message = "1002-Password cannot be null or empty")
    public String password;

    public Language language;

    @NotEmpty(message = "1003-Roles cannot be null or empty")
    public List<Role.Name> roles;
}
