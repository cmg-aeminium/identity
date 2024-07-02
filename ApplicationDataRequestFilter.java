/**
 * Copyright (c) 2020 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.api.rest.filters.request;

import java.io.IOException;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import pt.cmg.aeminium.knowledge.api.rest.KnowledgeApplication;

/**
 * @author Carlos Gonçalves
 */
public class ApplicationDataRequestFilter implements ContainerRequestFilter {

    @Inject
    @RequestData
    private Event<ClientApplicationData> appDataEventHandler;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String appName = requestContext.getHeaders().getFirst(KnowledgeApplication.REQUEST_HEADER_APP_NAME);

        String appVersion = requestContext.getHeaders().getFirst(KnowledgeApplication.REQUEST_HEADER_APP_VERSION);

        // Do some logic stuff here
        ClientApplicationData appData = new ClientApplicationData(appName, appVersion);

        appDataEventHandler.fire(appData);
    }

}
