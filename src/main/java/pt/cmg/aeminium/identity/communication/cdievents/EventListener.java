/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.communication.cdievents;

import java.util.logging.Logger;
import fish.payara.micro.cdi.Inbound;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * @author Carlos Gonçalves
 */
@ApplicationScoped
public class EventListener {

    private static final Logger LOGGER = Logger.getLogger(EventListener.class.getName());

    public void listenToPing(@Observes @Inbound(eventName = "PING") String message) {
        LOGGER.info(message);
    }

}
