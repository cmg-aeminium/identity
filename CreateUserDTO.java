/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.tasks.users;

import pt.cmg.aeminium.knowledge.persistence.entities.localisation.Language;

/**
 * @author Carlos Gonçalves
 */
public class CreateUserDTO {

    public String name;
    public String email;
    public String password;
    public Language language;
}
