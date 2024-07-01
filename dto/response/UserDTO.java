/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.api.rest.resources.users.dto.response;

import java.text.Collator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import pt.cmg.aeminium.knowledge.persistence.entities.identity.User;
import pt.cmg.aeminium.knowledge.persistence.entities.localisation.Language;

/**
 * @author Carlos Gonçalves
 */
@JsonbPropertyOrder({"id", "name", "email", "language", "status", "createdAt", "roles"})
public class UserDTO implements Comparable<UserDTO> {

    public Long id;
    public String name;
    public String email;
    public Language language;
    public User.Status status;
    public LocalDateTime createdAt;

    public List<String> roles;

    @Override
    public int compareTo(UserDTO o) {
        // Sort ignoring case and accents
        Collator comparator = Collator.getInstance(Locale.UK);
        comparator.setStrength(Collator.PRIMARY);

        return comparator.compare(name, o.name);
    }
}
