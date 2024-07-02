/**
 * Copyright (c) 2020 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.filters.request;

import java.io.IOException;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import pt.cmg.aeminium.datamodel.common.entities.localisation.Language;
import pt.cmg.aeminium.identity.api.rest.v1.resources.IdentityApplication;

/**
 * This is a global filter that checks every HTTP request for a language and if it is not sent
 * then it sets it to the default.
 *
 * @author Carlos Gonçalves
 */
public class LanguageSetterRequestFilter implements ContainerRequestFilter {

    @Inject
    @RequestData
    private Event<Language> languageEventHandler;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String requestLanguage = requestContext.getHeaders().getFirst(IdentityApplication.REQUEST_HEADER_LANGUAGE);

        Language selectedLanguage = Language.fromString(requestLanguage);

        requestContext.getHeaders().add(IdentityApplication.REQUEST_HEADER_LANGUAGE, selectedLanguage.toString());

        languageEventHandler.fire(selectedLanguage);
    }

}
