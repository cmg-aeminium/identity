/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.tasks.users;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Carlos Gonçalves
 */
public class PasswordConstrainer {

    public static final int PASSWORD_MIN_LENGTH = 6;
    public static final int PASSWORD_MAX_LENGTH = 32;

    /**
     * Simple password rule enforcing.
     * Returns true if this password respects the basic rules enforced by FC, which are:<br>
     * 1) it must have 6 or more characters<br>
     * 2) it must have 32 or less characters<br>
     * 3) it must not be composed entirely by whitespaces<br>
     * 4) it must be composed only by letters or numbers (a-z, A-Z, 0-9)
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
