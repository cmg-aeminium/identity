/**
 * Copyright (c) 2020 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.api.rest.v1.filters.request;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Produces;
import pt.cmg.aeminium.datamodel.common.entities.localisation.Language;

/**
 * This is both an Observer and a Producer.
 * It observes events with different origins that will fill a Request Data object
 * that has a set of variables usable during a request.
 *
 * @author Carlos Gonçalves
 */
@RequestScoped
public class RequestDataProducer {

    @Produces
    @RequestData
    @RequestScoped
    private RequestContextData requestContextData;

    @PostConstruct
    public void initContextDate() {
        this.requestContextData = new RequestContextData();
    }

    public void observeLanguage(@Observes @RequestData Language language) {
        requestContextData.setSelectedLanguage(language);
    }

    public void observeAppData(@Observes @RequestData ClientApplicationData appData) {
        requestContextData.setAppData(appData);
    }

    public void observeUser(@Observes @RequestData Long userId) {
        requestContextData.setUserId(userId);
    }

}