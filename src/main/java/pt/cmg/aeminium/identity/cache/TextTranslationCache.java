/*
 * Copyright (c) 2024 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.identity.cache;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import java.util.logging.Logger;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import pt.cmg.aeminium.datamodel.common.entities.localisation.Language;
import pt.cmg.aeminium.datamodel.common.entities.localisation.TextContent;
import pt.cmg.aeminium.datamodel.common.entities.localisation.TranslatedText;
import pt.cmg.aeminium.knowledge.api.rest.filters.request.RequestContextData;
import pt.cmg.aeminium.knowledge.api.rest.filters.request.RequestData;

/**
 * @author Carlos Gonçalves
 */
@ApplicationScoped
public class TextTranslationCache {

    private static final Logger LOGGER = Logger.getLogger(TextTranslationCache.class.getName());

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
        defaultTexts.putIfAbsent("%s_%s".formatted(id, language), textContent);
    }

    public void replaceTranslation(TextContent defaultLangText) {
        defaultTexts.replace("%s_%s".formatted(defaultLangText.getId(), defaultLangText.getLanguage()), defaultLangText.getTextValue());
    }

    public void replaceTranslation(TranslatedText translatedText) {
        defaultTexts.replace("%s_%s".formatted(translatedText.getId(), translatedText.getLanguage()), translatedText.getTextValue());
    }

    public String getTranslatedText(Long id) {
        return defaultTexts.get("%s_%s".formatted(id, requestData.getSelectedLanguage()));
    }

    public boolean containsText(Long id) {
        return defaultTexts.containsKey("%s_%s".formatted(id, requestData.getSelectedLanguage()));
    }

    public boolean containsText(Long id, Language languague) {
        return defaultTexts.containsKey("%s_%s".formatted(id, languague));
    }

}
