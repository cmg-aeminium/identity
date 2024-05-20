/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.tasks.users;

import java.util.logging.Logger;
import javax.ejb.Singleton;
import javax.inject.Inject;
import pt.cmg.aeminium.knowledge.dao.identity.UserDAO;
import pt.cmg.aeminium.knowledge.persistence.entities.identity.User;
import pt.cmg.aeminium.knowledge.persistence.entities.localisation.Language;

/**
 * @author Carlos Gonçalves
 */
@Singleton
public class UserCreator {

    private static final Logger LOGGER = Logger.getLogger(UserCreator.class.getName());

    @Inject
    private UserDAO userDAO;

    public User creatUser(CreateUserDTO userDTO) {

        User newUser = new User();

        newUser.setName(userDTO.name);
        newUser.setEmail(userDTO.email);
        newUser.setLanguage(userDTO.language == null ? Language.DEFAULT_LANGUAGE : userDTO.language);

        return newUser;
    }

}
