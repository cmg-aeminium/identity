/**
 * Copyright (c) 2020 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.api.rest.filters.request;

import java.io.IOException;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import pt.cmg.aeminium.knowledge.api.rest.SwerankerApplication;

/**
 * @author Carlos Gonçalves
 */
public class ApplicationDataRequestFilter implements ContainerRequestFilter {

    @Inject
    @RequestData
    private Event<ClientApplicationData> appDataEventHandler;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String appName = requestContext.getHeaders().getFirst(SwerankerApplication.REQUEST_HEADER_APP_NAME);

        String appVersion = requestContext.getHeaders().getFirst(SwerankerApplication.REQUEST_HEADER_APP_VERSION);

        // Do some logic stuff here
        ClientApplicationData appData = new ClientApplicationData(appName, appVersion);

        appDataEventHandler.fire(appData);
    }

}
