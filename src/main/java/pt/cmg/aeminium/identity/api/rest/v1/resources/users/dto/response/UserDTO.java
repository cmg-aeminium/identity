/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.resources.users.dto.response;

import java.text.Collator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import jakarta.json.bind.annotation.JsonbPropertyOrder;
import pt.cmg.aeminium.datamodel.common.entities.localisation.Language;
import pt.cmg.aeminium.datamodel.users.entities.identity.User;

/**
 * @author Carlos Gonçalves
 */
@JsonbPropertyOrder({"id", "name", "email", "status", "createdAt", "language", "roles"})
@Schema(description = "Represents a User", example = """
    {
        "id": 1,
        "name": "Jason D. Frank",
        "email": "jdf@mail.com",
        "language": "en-UK",
        "status": "ACTIVE",
        "roles" : ["GOD", "SCHOLAR"]}
    """)
public record UserDTO(
    Long id,
    String name,
    String email,
    Language language,
    User.Status status,
    LocalDateTime createdAt,
    List<String> roles) implements Comparable<UserDTO> {

    @Override
    public int compareTo(UserDTO o) {
        // Sort ignoring case and accents
        Collator comparator = Collator.getInstance(Locale.UK);
        comparator.setStrength(Collator.PRIMARY);

        return comparator.compare(name, o.name);
    }

}
