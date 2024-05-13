/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.api.rest.resources.users.converters;

import pt.cmg.aeminium.knowledge.api.rest.resources.users.dto.request.CreateUserDTO;
import pt.cmg.aeminium.knowledge.api.rest.resources.users.dto.response.UserDTO;
import pt.cmg.aeminium.knowledge.persistence.entities.identity.User;

/**
 * @author Carlos Gonçalves
 */
public class UserConverter {

    public static User toUser(CreateUserDTO userDTO) {

        User newUser = new User();
        newUser.setName(userDTO.name);
        newUser.setEmail(userDTO.email);
        newUser.setLanguage(userDTO.language);

        // Also get the groups

        return newUser;
    }

    public static UserDTO toUserDTO(User user) {

        UserDTO dto = new UserDTO();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.email = user.getEmail();
        dto.status = user.getStatus();
        dto.language = user.getLanguage();

        return dto;

    }
}
