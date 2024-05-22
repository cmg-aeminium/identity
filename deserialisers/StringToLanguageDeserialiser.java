/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.configuration.jsonb.deserialisers;

import java.lang.reflect.Type;
import javax.json.bind.serializer.DeserializationContext;
import javax.json.bind.serializer.JsonbDeserializer;
import javax.json.stream.JsonParser;
import pt.cmg.aeminium.knowledge.persistence.entities.localisation.Language;

/**
 * Turns a JSON object that has a Language into its matching Language Enum object.
 * This was needed because Language has a presentation name that differs from its enum constant name.
 * And that happens because of the Java language naming conventions.
 * 
 * @author Carlos Gonçalves
 */
public class StringToLanguageDeserialiser implements JsonbDeserializer<Language> {

    @Override
    public Language deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {

        final String value = parser.getString();

        return Language.fromString(value);
    }

}
