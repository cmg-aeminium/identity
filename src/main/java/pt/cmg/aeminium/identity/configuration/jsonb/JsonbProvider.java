/**
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.configuration.jsonb;

import java.util.Locale;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import pt.cmg.aeminium.identity.configuration.jsonb.deserialisers.StringToLanguageDeserialiser;
import pt.cmg.aeminium.identity.configuration.jsonb.serialisers.LanguageToStringSerialiser;
import pt.cmg.jakartautils.jsonb.deserialisers.MillisToDateDeserialiser;
import pt.cmg.jakartautils.jsonb.deserialisers.MillisToLocalDateTimeDeserialiser;
import pt.cmg.jakartautils.jsonb.serialisers.DateToMillisSerialiser;
import pt.cmg.jakartautils.jsonb.serialisers.LocalDateTimeToMillisSerialiser;
import pt.cmg.jakartautils.jsonb.serialisers.PairSerialiser;

/**
 * This is a Context Resolver that configures the JSONB feature globally.
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JsonbProvider implements ContextResolver<Jsonb> {

    private static Jsonb INSTANCE;
    private static Jsonb IGNORE_CASE_INSTANCE;

    @Override
    public Jsonb getContext(Class<?> type) {

        return JsonbBuilder.newBuilder()
            .withConfig(createConfiguration())
            .build();

    }

    private static JsonbConfig createConfiguration() {
        return new JsonbConfig()
            .withDateFormat(JsonbDateFormat.TIME_IN_MILLIS, Locale.getDefault())
            .withDeserializers(new MillisToLocalDateTimeDeserialiser(), new MillisToDateDeserialiser(), new StringToLanguageDeserialiser())
            .withSerializers(new DateToMillisSerialiser(), new LocalDateTimeToMillisSerialiser(), new PairSerialiser(), new LanguageToStringSerialiser())
            .withNullValues(true);
    }

    public static Jsonb getJsonB() {
        return getJsonB(false);

    }

    public static Jsonb getJsonB(boolean ignoreCase) {
        if (ignoreCase) {
            if (IGNORE_CASE_INSTANCE == null) {
                init(true);
            }
            return IGNORE_CASE_INSTANCE;
        } else {
            if (INSTANCE == null) {
                init(false);
            }
            return INSTANCE;
        }
    }

    private static void init(boolean ignoreCase) {
        JsonbConfig configuration = createConfiguration();

        if (ignoreCase) {
            configuration.withPropertyNamingStrategy(PropertyNamingStrategy.CASE_INSENSITIVE);
            IGNORE_CASE_INSTANCE = JsonbBuilder.newBuilder().withConfig(configuration).build();
        } else {
            INSTANCE = JsonbBuilder.newBuilder().withConfig(configuration).build();
        }
    }

}
