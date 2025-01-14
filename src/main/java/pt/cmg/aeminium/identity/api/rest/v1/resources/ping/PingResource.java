/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.resources.ping;

import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import pt.cmg.aeminium.identity.communication.cdievents.EventPublisher;

/**
 * @author Carlos Gonçalves
 */
@RequestScoped
@Path("ping")
@Tag(name = "Test", description = "Test operations")
public class PingResource {

    @Inject
    private EventPublisher publisher;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendPing() {
        publisher.sendPingEvent();
        return Response.ok().build();
    }

}
