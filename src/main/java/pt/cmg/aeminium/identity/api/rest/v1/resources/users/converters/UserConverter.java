/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.resources.users.converters;

import java.util.ArrayList;
import java.util.List;
import pt.cmg.aeminium.datamodel.users.entities.identity.Role;
import pt.cmg.aeminium.datamodel.users.entities.identity.User;
import pt.cmg.aeminium.identity.api.rest.v1.resources.users.dto.response.UserDTO;

/**
 * @author Carlos Gonçalves
 */
public class UserConverter {

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getId(),
            user.getName(),
            user.getEmail(),
            user.getLanguage(),
            user.getStatus(),
            user.getCreatedAt(),
            user.getRoles().stream().map(rl -> rl.getName().toString()).toList());

    }

    public static List<UserDTO> toUsersDTO(List<User> users) {

        List<UserDTO> usersDTO = new ArrayList<>();

        for (User user : users) {

            List<Role> roles = user.getRoles();

            UserDTO dto = new UserDTO(user.getId(),
                user.getName(),
                user.getEmail(),
                user.getLanguage(),
                user.getStatus(),
                user.getCreatedAt(),
                roles.stream().map(rl -> rl.getName().toString()).toList());

            usersDTO.add(dto);

        }

        return usersDTO;

    }
}
