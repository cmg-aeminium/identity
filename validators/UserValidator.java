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
import pt.cmg.aeminium.knowledge.tasks.users.CreateUserDTO;
import pt.cmg.jakartautils.errors.ErrorDTO;

/**
 * @author Carlos Gonçalves
 */
public class UserValidator {

    public static Optional<List<ErrorDTO>> isValidUserForCreation(CreateUserDTO userDTO) {
        List<ErrorDTO> errors = new ArrayList<>();

        if (StringUtils.isBlank(userDTO.email)) {
            errors.add(new ErrorDTO(1, "Email is null or empty"));
        }

        if (!KnowledgeApplication.isAcceptablePassword(userDTO.password, false)) {
            errors.add(new ErrorDTO(2, "Password does not comply to acceptable standards"));
        }

        // now validate groups

        return errors.isEmpty() ? Optional.empty() : Optional.of(errors);
    }

}
