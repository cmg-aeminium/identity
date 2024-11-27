/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.communication.cdievents;

import fish.payara.micro.cdi.Outbound;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;

/**
 * @author Carlos Gonçalves
 */
@ApplicationScoped
public class EventPublisher {

    @Inject
    @Outbound(eventName = "PING")
    private Event<String> event;

    public void sendPingEvent() {
        event.fire("pong");
    }

}
