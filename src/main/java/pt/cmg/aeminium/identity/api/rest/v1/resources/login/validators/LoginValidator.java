/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.resources.login.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import pt.cmg.aeminium.datamodel.users.dao.identity.UserDAO;
import pt.cmg.aeminium.datamodel.users.entities.identity.User;
import pt.cmg.aeminium.identity.api.rest.v1.resources.login.converters.LoginConverter;
import pt.cmg.aeminium.identity.tasks.users.PasswordConstrainer;
import pt.cmg.jakartautils.errors.ErrorDTO;
import pt.cmg.jakartautils.identity.PasswordUtils;
import pt.cmg.jakartautils.text.TextFormatter;

/**
 * @author Carlos Gonçalves
 */
@RequestScoped
public class LoginValidator {

    // The maximum email length is 254, by the specification RFC 5321 (see http://www.dominicsayers.com/isemail/).
    // This is, however a source of a heated discussion, so bear that in mind should this blow in the future
    public static final int EMAIL_MAX_LENGTH = 254;

    private static final Logger LOGGER = Logger.getLogger(LoginValidator.class.getName());

    @Inject
    private UserDAO userDAO;

    private User currentUser;

    public Optional<List<ErrorDTO>> isValidLogin(HttpHeaders headers) {

        var errors = isValidLoginHeader(headers);
        if (errors.isPresent()) {
            return errors;
        }

        String[] credentials = LoginConverter.extractBasicAuthenticationCredentials(headers);

        errors = areValidCredentials(credentials);
        if (errors.isPresent()) {
            return errors;
        }

        errors = isUserStatusValid(credentials[0]);
        if (errors.isPresent()) {
            return errors;
        }

        errors = isCorrectPassword(credentials[1]);
        if (errors.isPresent()) {
            return errors;
        }

        return Optional.empty();
    }

    private Optional<List<ErrorDTO>> isValidLoginHeader(HttpHeaders headers) {

        List<ErrorDTO> errors = new ArrayList<>();

        if (headers.getRequestHeader(HttpHeaders.AUTHORIZATION) == null) {
            errors.add(new ErrorDTO(1, "Invalid parameters, authentication header is null"));
        }

        return errors.isEmpty() ? Optional.empty() : Optional.of(errors);
    }

    private Optional<List<ErrorDTO>> areValidCredentials(String[] crendentials) {

        // Missing parameter, split returned one-sized array -> password is missing
        if (crendentials.length < 2) {
            return Optional.of(List.of(new ErrorDTO(2, "Missing parameter: password")));
        }

        if (!isEmailValid(crendentials[0]) || !PasswordConstrainer.isAcceptablePassword(crendentials[1], false)) {
            return Optional.of(List.of(new ErrorDTO(3, "Invalid login credentials")));
        }

        return Optional.empty();
    }

    private boolean isEmailValid(String email) {

        if (StringUtils.isBlank(email) || email.length() > EMAIL_MAX_LENGTH) {
            return false;
        }

        // Here this validator should do a better job than the previous Internet Address (javamail InternetAddress) which was RFC822 compliant
        return EmailValidator.getInstance().isValid(email);
    }

    private Optional<List<ErrorDTO>> isUserStatusValid(String email) {

        currentUser = userDAO.findByEmail(email);
        if (currentUser == null) {
            LOGGER.warning(TextFormatter.formatMessageToLazyLog("This user does not exist {0}", email));
            return Optional.of(List.of(new ErrorDTO(4, "This user does not exist")));
        }

        if (currentUser.getSalt() == null) {
            LOGGER.warning(TextFormatter.formatMessageToLazyLog("User {0} does not have a salt assigned. Password has not been correctly set.", email));
            return Optional.of(List.of(new ErrorDTO(5, "User login failed. Contact administrator.")));
        }

        return Optional.empty();
    }

    private Optional<List<ErrorDTO>> isCorrectPassword(String password) {

        String saltedPassword = PasswordUtils.generateSaltedPassword(currentUser.getSalt(), password);

        if (!saltedPassword.equals(currentUser.getPassword())) {
            LOGGER.warning(TextFormatter.formatMessageToLazyLog("User {0} - Wrong password.", currentUser.getEmail()));
            Optional.of(List.of(new ErrorDTO(3, "Invalid login credentials")));
        }

        return Optional.empty();
    }

}
