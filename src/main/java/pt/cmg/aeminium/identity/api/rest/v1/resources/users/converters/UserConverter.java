/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.resources.users.converters;

import java.util.List;
import java.util.stream.Collectors;
import pt.cmg.aeminium.datamodel.users.entities.identity.Role;
import pt.cmg.aeminium.datamodel.users.entities.identity.User;
import pt.cmg.aeminium.identity.api.rest.v1.resources.users.dto.response.UserDTO;

/**
 * @author Carlos Gonçalves
 */
public class UserConverter {

    public static UserDTO toUserDTO(User user) {

        UserDTO dto = new UserDTO();
        dto.id = user.getId();
        dto.name = user.getName();
        dto.email = user.getEmail();
        dto.status = user.getStatus();
        dto.language = user.getLanguage();

        List<Role> roles = user.getRoles();
        dto.roles = roles.stream().map(rl -> rl.getName().toString()).collect(Collectors.toList());

        dto.createdAt = user.getCreatedAt();

        return dto;

    }
}
