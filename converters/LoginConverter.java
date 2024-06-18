/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.api.rest.resources.login.converters;

import java.security.InvalidParameterException;
import java.util.Base64;
import jakarta.ws.rs.core.HttpHeaders;

/**
 * @author Carlos Gonçalves
 */
public class LoginConverter {

    /**
     * Extracts the Basic Authentication credentials from the headers.
     */
    public static String[] extractBasicAuthenticationCredentials(HttpHeaders headers) throws InvalidParameterException {

        // Basic authentication credentials come encoded as a Base64 String AFTER the String "Basic " with space, that's why substring was used
        String authHeader = headers.getRequestHeader(HttpHeaders.AUTHORIZATION).get(0).substring("Basic ".length());

        // ...and even then it comes separated by a ":"
        String[] decoded = new String(Base64.getDecoder().decode(authHeader)).split(":");

        return decoded;
    }

    public static String extractUserEmail(HttpHeaders headers) throws InvalidParameterException {
        return extractBasicAuthenticationCredentials(headers)[0];
    }

}
