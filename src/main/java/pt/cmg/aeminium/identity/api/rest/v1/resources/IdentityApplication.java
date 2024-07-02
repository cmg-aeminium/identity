/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.resources;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.auth.LoginConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import pt.cmg.aeminium.datamodel.common.entities.localisation.Language;

/**
 * @author Carlos Gonçalves
 */
@ApplicationScoped
@LoginConfig(authMethod = "MP-JWT")
@ApplicationPath("/v1")
public class IdentityApplication extends Application {

    public static final String REQUEST_HEADER_LANGUAGE = "aem-language";
    public static final String REQUEST_HEADER_APP_NAME = "aem-app";
    public static final String REQUEST_HEADER_APP_VERSION = "aem-app-version";

    public static final Language APP_DEFAULT_LANGUAGE = Language.DEFAULT_LANGUAGE;

    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 32;

    // The maximum email length is 254, by the specification RFC 5321 (see http://www.dominicsayers.com/isemail/).
    // This is, however a source of a heated discussion, so bear that in mind should this blow in the future
    public static final int EMAIL_MAX_LENGTH = 254;

    /**
     * Simple password rule enforcing.
     * Returns true if this password respects the basic rules enforced by FC, which are:<br>
     * 1) it must have 6 or more characters<br>
     * 2) it must have 32 or less characters<br>
     * 3) it must not be composed entirely by whitespaces<br>
     * 4) it must be composed only by letters or numbers (a-z, A-Z, 0-9)
     *
     * TODO: should this be here? So far it seems an acceptable temporary place
     */
    public static boolean isAcceptablePassword(String password, boolean onlyAcceptAlphanumeric) {
        if (StringUtils.isBlank(password) ||
            password.length() < PASSWORD_MIN_LENGTH ||
            password.length() > PASSWORD_MAX_LENGTH) {
            return false;
        }
        if (StringUtils.containsOnly(password, ' ')) {
            return false;
        }
        if (onlyAcceptAlphanumeric && !StringUtils.isAlphanumericSpace(password)) {
            return false;
        }
        return true;
    }

}
