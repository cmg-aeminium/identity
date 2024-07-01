/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.tasks.users;

import java.util.List;
import javax.management.relation.Role;
import jakarta.ejb.Lock;
import jakarta.ejb.LockType;
import jakarta.ejb.Singleton;
import jakarta.inject.Inject;
import pt.cmg.aeminium.identity.api.rest.v1.resources.users.dto.request.CreateUserDTO;
import pt.cmg.aeminium.identity.api.rest.v1.resources.users.dto.request.EditUserDTO;
import pt.cmg.aeminium.knowledge.dao.identity.RoleDAO;
import pt.cmg.aeminium.knowledge.dao.identity.UserDAO;
import pt.cmg.aeminium.knowledge.persistence.entities.identity.User;
import pt.cmg.aeminium.knowledge.persistence.entities.localisation.Language;
import pt.cmg.jakartautils.identity.PasswordUtils;

/**
 * @author Carlos Gonçalves
 */
@Singleton
@Lock(LockType.READ)
public class UserCreator {

    @Inject
    private UserDAO userDAO;

    @Inject
    private RoleDAO roleDAO;

    public User creatUser(CreateUserDTO userDTO) {

        User newUser = new User();

        newUser.setName(userDTO.name);
        newUser.setEmail(userDTO.email);
        newUser.setLanguage(userDTO.language == null ? Language.DEFAULT_LANGUAGE : userDTO.language);

        List<Role> roles = roleDAO.findByNames(userDTO.roles);
        newUser.setRoles(roles);

        String newSalt = PasswordUtils.generateSalt();
        String newSaltedPassword = PasswordUtils.generateSaltedPassword(newSalt, userDTO.password);

        newUser.setSalt(newSalt);
        newUser.setPassword(newSaltedPassword);

        newUser.setStatus(Status.PENDING);

        userDAO.create(newUser, true);

        return newUser;
    }

    public User editUser(Long userId, EditUserDTO userDTO) {

        User editingUser = userDAO.findById(userId);

        if (userDTO.name != null) {
            editingUser.setName(userDTO.name);
        }

        if (userDTO.email != null) {
            editingUser.setEmail(userDTO.email);
        }

        if (userDTO.language != null) {
            editingUser.setLanguage(userDTO.language);
        }
        return editingUser;
    }

    public User editUserRoles(Long userId, List<Role.Name> roleNames) {

        User editingUser = userDAO.findById(userId);

        List<Role> roles = roleDAO.findByNames(roleNames);
        editingUser.setRoles(roles);

        return editingUser;
    }

}
