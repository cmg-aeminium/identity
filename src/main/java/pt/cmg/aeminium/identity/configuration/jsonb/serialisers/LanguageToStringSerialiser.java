/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.configuration.jsonb.serialisers;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import pt.cmg.aeminium.datamodel.common.entities.localisation.Language;

/**
 * Turns a Language enum object into its matching String form that will be printed into a JSON object of such type.
 * This was needed because Language has a presentation name that differs from its enum constant name.
 * And that happens because of the Java language naming conventions.
 * 
 * @author Carlos Gonçalves
 */
public class LanguageToStringSerialiser implements JsonbSerializer<Language> {

    @Override
    public void serialize(Language obj, JsonGenerator generator, SerializationContext ctx) {
        generator.write(obj.getName());
    }

}
