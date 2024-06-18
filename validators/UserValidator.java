/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.api.rest.resources.users.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import pt.cmg.aeminium.knowledge.api.rest.KnowledgeApplication;
import pt.cmg.aeminium.knowledge.api.rest.filters.request.RequestContextData;
import pt.cmg.aeminium.knowledge.api.rest.filters.request.RequestData;
import pt.cmg.aeminium.knowledge.api.rest.resources.users.dto.request.CreateUserDTO;
import pt.cmg.aeminium.knowledge.api.rest.resources.users.dto.request.EditUserDTO;
import pt.cmg.aeminium.knowledge.dao.identity.UserDAO;
import pt.cmg.aeminium.knowledge.persistence.entities.identity.Role;
import pt.cmg.aeminium.knowledge.persistence.entities.identity.User;
import pt.cmg.jakartautils.errors.ErrorDTO;

/**
 * @author Carlos Gonçalves
 */
@RequestScoped
public class UserValidator {

    @Inject
    @RequestData
    private RequestContextData requestData;

    @Inject
    @Claim(standard = Claims.groups)
    private Set<String> roles;

    @Inject
    private UserDAO userDAO;

    public Optional<List<ErrorDTO>> isValidUserForCreation(CreateUserDTO userDTO) {
        List<ErrorDTO> errors = new ArrayList<>();

        if (!KnowledgeApplication.isAcceptablePassword(userDTO.password, false)) {
            errors.add(new ErrorDTO(1, "Password does not comply to acceptable standards"));
        }

        if (userDTO.roles.contains(Role.Name.GOD)) {
            errors.add(new ErrorDTO(2, "There is only one GOD"));
        }

        return errors.isEmpty() ? Optional.empty() : Optional.of(errors);
    }

    public Optional<List<ErrorDTO>> isValidUserForEdition(Long userId, EditUserDTO userDTO) {
        List<ErrorDTO> errors = new ArrayList<>();

        if (userDTO.name != null && userDTO.name.isBlank()) {
            errors.add(new ErrorDTO(1, "Empty user names are not acceptable"));
        }

        if (userDTO.email != null && userDTO.email.isBlank()) {
            errors.add(new ErrorDTO(2, "Empty emails are not acceptable"));
        }

        User callingUser = userDAO.findById(requestData.getUserId());
        if (!userId.equals(callingUser.getId()) && !roles.contains(Role.GOD)) {
            errors.add(new ErrorDTO(3, "User can only change its own data"));
        }

        if (userDTO.email != null && !userDTO.email.isBlank()) {
            if (userDAO.findByEmail(userDTO.email) != null) {
                errors.add(new ErrorDTO(4, "Email already in use"));
            }
        }

        return errors.isEmpty() ? Optional.empty() : Optional.of(errors);
    }

}
