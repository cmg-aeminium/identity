/**
 * Copyright (c) 2019 Carlos Gonçalves (https://www.linkedin.com/in/carlosmogoncalves/)
 * Likely open-source, so copy at will, bugs will be yours as well.
 */
package pt.cmg.aeminium.knowledge.cache;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.persistence.Cache;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.persistence.jpa.JpaCache;
import pt.cmg.aeminium.knowledge.persistence.entities.knowledgebodies.KnowledgeArea;
import pt.cmg.aeminium.knowledge.persistence.entities.knowledgebodies.KnowledgeBody;
import pt.cmg.aeminium.knowledge.persistence.entities.knowledgebodies.KnowledgeTopic;
import pt.cmg.aeminium.knowledge.persistence.entities.localisation.Country;
import pt.cmg.aeminium.knowledge.persistence.entities.localisation.Language;
import pt.cmg.aeminium.knowledge.persistence.entities.localisation.TextContent;
import pt.cmg.aeminium.knowledge.persistence.entities.localisation.TranslatedText;
import pt.cmg.aeminium.knowledge.persistence.entities.schools.Course;
import pt.cmg.aeminium.knowledge.persistence.entities.schools.CourseClass;
import pt.cmg.aeminium.knowledge.persistence.entities.schools.CourseClassTopic;
import pt.cmg.aeminium.knowledge.persistence.entities.schools.School;
import pt.cmg.jakartautils.jpa.QueryUtils;

/**
 * @author Carlos Gonçalves
 */
@Singleton
@Startup
public class CacheLoader {

    private static final Logger LOGGER = Logger.getLogger(CacheLoader.class.getName());

    @PersistenceUnit(unitName = "knowledge-data")
    private EntityManagerFactory entityManagerFactory;

    // This variable can be injected into the environment to control whether it should run or not
    @Inject
    @ConfigProperty(name = "jpa.cache.loadAtStartup", defaultValue = "true")
    private boolean loadCacheAtStartup;

    // @Inject
    // @ConfigProperty(name = "jpa.cache.refresh", defaultValue = "true")
    // private boolean refreshCacheEnabled;

    @Inject
    @ConfigProperty(name = "app.language", defaultValue = Language.DEFAULT_LANGUAGE_NAME)
    private Language language;

    @Inject
    private HazelcastCache hazelcastCache;

    @PostConstruct
    public void loadCacheAtStartup() {

        if (loadCacheAtStartup) {
            LOGGER.info("Loading Cache at startup is active. Loading objects and query results to memory.");
            loadObjectCache();
        } else {
            LOGGER.warning("No objects/queries will be loaded at startup, but cache will still be available whenever objects are loaded from database.");
        }

    }

    /**
     * Triggered somewhere to refresh cache
     */
    // public void refreshCacheTimer() {
    // if (refreshCacheEnabled) {
    // refreshCache();
    // }
    // }

    @Asynchronous
    public void refreshCache() {
        invalidateAllCaches();
        loadObjectCache();
    }

    /**
     * This will clear the shared cache of ALL data.
     * Either OBJECT cache and QUERY RESULTS cache.
     * If used, please reload all of it again.
     * I can't think of a good reason to use this alone, but maybe for sanity check purposes.
     */
    public void invalidateAllCaches() {

        Cache appCache = entityManagerFactory.getCache();
        appCache.evictAll();

        if (appCache instanceof JpaCache) {
            ((JpaCache) appCache).clearQueryCache();
        }

    }

    /**
     * Loads all the object cache.
     * This affects only objects that are accessed by their ID and nothing else.
     */
    private void loadObjectCache() {
        LOGGER.log(Level.INFO, "Started loading Object cache");
        loadSchools();
        loadCountries();
        loadCourses();
        loadCourseClasses();
        loadCourseClassTopics();
        loadKnowledgeBodies();
        loadKnowledgeAreas();
        loadKnowledgeTopics();
        LOGGER.log(Level.INFO, "Finished loading Object cache");
    }

    public void loadSchools() {

        EntityManager database = entityManagerFactory.createEntityManager();

        TypedQuery<School> query = database.createNamedQuery(School.QUERY_FIND_ALL, School.class);
        var schools = query.getResultList();

        Set<Long> ids = schools.stream().map(School::getNameTextContentId).collect(Collectors.toSet());

        database.close();

        loadTextsToHazelcastCache(ids);
    }

    public void loadCountries() {

        EntityManager database = entityManagerFactory.createEntityManager();

        TypedQuery<Country> query = database.createNamedQuery(Country.QUERY_FIND_ALL, Country.class);
        var countries = query.getResultList();

        Set<Long> ids = countries.stream().map(Country::getNameTextContentId).collect(Collectors.toSet());

        database.close();

        loadTextsToHazelcastCache(ids);
    }

    public void loadCourses() {

        EntityManager database = entityManagerFactory.createEntityManager();

        TypedQuery<Course> query = database.createNamedQuery(Course.QUERY_FIND_ALL, Course.class);
        var courses = query.getResultList();

        Set<Long> ids = courses
            .stream()
            .flatMap(course -> Stream.of(course.getNameTextContentId(), course.getDescriptionContentId()))
            .collect(Collectors.toSet());

        database.close();

        loadTextsToHazelcastCache(ids);
    }

    public void loadCourseClassTopics() {

        EntityManager database = entityManagerFactory.createEntityManager();

        TypedQuery<CourseClass> query = database.createNamedQuery(CourseClass.QUERY_FIND_ALL, CourseClass.class);
        var courseClasses = query.getResultList();

        Set<Long> ids = courseClasses
            .stream()
            .flatMap(crsClass -> Stream.of(crsClass.getNameTextContentId(), crsClass.getDescriptionContentId()))
            .collect(Collectors.toSet());

        database.close();

        loadTextsToHazelcastCache(ids);
    }

    public void loadCourseClasses() {

        EntityManager database = entityManagerFactory.createEntityManager();

        TypedQuery<CourseClassTopic> query = database.createNamedQuery(CourseClassTopic.QUERY_FIND_ALL, CourseClassTopic.class);
        var topics = query.getResultList();

        Set<Long> ids = topics
            .stream()
            .map(CourseClassTopic::getDescriptionContentId)
            .collect(Collectors.toSet());

        database.close();

        loadTextsToHazelcastCache(ids);
    }

    public void loadKnowledgeBodies() {

        EntityManager database = entityManagerFactory.createEntityManager();

        TypedQuery<KnowledgeBody> query = database.createNamedQuery(KnowledgeBody.QUERY_FIND_ALL, KnowledgeBody.class);
        var kBodies = query.getResultList();

        Set<Long> ids = kBodies
            .stream()
            .flatMap(body -> Stream.of(body.getNameTextContentId(), body.getDescriptionContentId()))
            .collect(Collectors.toSet());

        database.close();

        loadTextsToHazelcastCache(ids);
    }

    public void loadKnowledgeAreas() {

        EntityManager database = entityManagerFactory.createEntityManager();

        TypedQuery<KnowledgeArea> query = database.createNamedQuery(KnowledgeArea.QUERY_FIND_ALL, KnowledgeArea.class);
        var kAreas = query.getResultList();

        Set<Long> ids = kAreas
            .stream()
            .flatMap(kArea -> Stream.of(kArea.getNameTextContentId(), kArea.getDescriptionContentId()))
            .collect(Collectors.toSet());

        database.close();

        loadTextsToHazelcastCache(ids);
    }

    public void loadKnowledgeTopics() {

        EntityManager database = entityManagerFactory.createEntityManager();

        TypedQuery<KnowledgeTopic> query = database.createNamedQuery(KnowledgeTopic.QUERY_FIND_ALL, KnowledgeTopic.class);
        var topics = query.getResultList();

        Set<Long> ids = topics
            .stream()
            .flatMap(topic -> Stream.of(topic.getNameTextContentId(), topic.getDescriptionContentId()))
            .collect(Collectors.toSet());

        database.close();

        loadTextsToHazelcastCache(ids);
    }

    /**
     * Loads Texts to Hazelcast.
     * This will store the translations on a cluster-shared cached data structure for fast access in all
     * application instances.
     */
    private void loadTextsToHazelcastCache(Collection<Long> textIds) {

        if (textIds == null || textIds.isEmpty()) {
            return;
        }

        loadDefaultTextsToHazelcastMap(textIds);
        loadTranslatedTextsToHazelcast(textIds);
    }

    private void loadDefaultTextsToHazelcastMap(Collection<Long> textIds) {

        EntityManager database = entityManagerFactory.createEntityManager();

        TypedQuery<TextContent> query = database.createNamedQuery(TextContent.QUERY_FIND_IN_IDS, TextContent.class);
        query.setParameter("ids", textIds);

        List<TextContent> result = QueryUtils.getResultListFromQuery(query);

        result.forEach(textContent -> hazelcastCache.putTranslation(textContent));

        database.close();

    }

    private void loadTranslatedTextsToHazelcast(Collection<Long> textIds) {

        EntityManager database = entityManagerFactory.createEntityManager();

        TypedQuery<TranslatedText> query = database.createNamedQuery(TranslatedText.QUERY_FIND_IN_IDS, TranslatedText.class);
        query.setParameter("ids", textIds);

        List<TranslatedText> result = QueryUtils.getResultListFromQuery(query);

        result.forEach(translation -> hazelcastCache.putTranslation(translation));

        database.close();

    }

}
