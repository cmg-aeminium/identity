/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.cache;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import pt.cmg.aeminium.knowledge.api.rest.filters.request.RequestContextData;
import pt.cmg.aeminium.knowledge.api.rest.filters.request.RequestData;
import pt.cmg.aeminium.knowledge.persistence.entities.localisation.Language;
import pt.cmg.aeminium.knowledge.persistence.entities.localisation.TextContent;
import pt.cmg.aeminium.knowledge.persistence.entities.localisation.TranslatedText;

/**
 * @author Carlos Gonçalves
 */
@ApplicationScoped
public class HazelcastCache {

    private static final Logger LOGGER = Logger.getLogger(HazelcastCache.class.getName());

    private static final String DEFAULT_LANG_MAP_NAME = "translations";

    @Inject
    private HazelcastInstance hazelcast;

    @Inject
    @RequestData
    private RequestContextData requestData;

    private IMap<String, String> defaultTexts;

    @PostConstruct
    public void initTranslationMap() {
        defaultTexts = hazelcast.getMap(DEFAULT_LANG_MAP_NAME);
    }

    public void putTranslation(TextContent defaultLangText) {
        putTranslation(defaultLangText.getId(), defaultLangText.getLanguage(), defaultLangText.getTextValue());
    }

    public void putTranslation(TranslatedText translatedText) {
        putTranslation(translatedText.getId(), translatedText.getLanguage(), translatedText.getTextValue());
    }

    public void putTranslation(Long id, Language language, String textContent) {
        defaultTexts.putIfAbsent(String.format("%s_%s", id, language), textContent);
    }

    public String getTranslatedText(Long id) {
        return defaultTexts.get(String.format("%s_%s", id, requestData.getSelectedLanguage()));
    }

}
