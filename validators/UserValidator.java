/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.api.rest.resources.users.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import pt.cmg.aeminium.knowledge.api.rest.KnowledgeApplication;
import pt.cmg.aeminium.knowledge.api.rest.resources.users.dto.request.CreateUserDTO;
import pt.cmg.aeminium.knowledge.persistence.entities.identity.Role;
import pt.cmg.jakartautils.errors.ErrorDTO;

/**
 * @author Carlos Gonçalves
 */
public class UserValidator {

    public static Optional<List<ErrorDTO>> isValidUserForCreation(CreateUserDTO userDTO) {
        List<ErrorDTO> errors = new ArrayList<>();

        if (StringUtils.isBlank(userDTO.email)) {
            errors.add(new ErrorDTO(1, "Email cannot be null or empty"));
        }

        if (!KnowledgeApplication.isAcceptablePassword(userDTO.password, false)) {
            errors.add(new ErrorDTO(2, "Password does not comply to acceptable standards"));
        }

        if (userDTO.roles == null || userDTO.roles.isEmpty()) {
            errors.add(new ErrorDTO(3, "Roles cannot be null or empty"));
        } else {
            if (userDTO.roles.contains(Role.Name.GOD)) {
                errors.add(new ErrorDTO(4, "There is only one GOD"));
            }
        }

        return errors.isEmpty() ? Optional.empty() : Optional.of(errors);
    }

}
