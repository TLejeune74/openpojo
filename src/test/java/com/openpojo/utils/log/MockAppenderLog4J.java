/*
 * Copyright (c) 2010-2018 Osman Shoukry
 * Licensed under the Apache License, Version 2.0
 */
package com.openpojo.utils.log;

import java.util.List;

import com.openpojo.utils.log.LogEvent.Priority;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.appender.AbstractAppender;      // Base appender Log4j2
import org.apache.logging.log4j.core.config.Property;

/**
 * Version Log4j 2 de l’appender de test.
 * - Convertit le niveau Log4j 2 en com.openpojo.utils.log.LogEvent.Priority
 * - Enregistre les événements via EventLogger
 */
public class MockAppenderLog4J extends AbstractAppender implements MockAppender {

    public MockAppenderLog4J() {
        // name = "MockAppenderLog4J", pas de Filter, pas de Layout, ignoreExceptions = true
        super("MockAppenderLog4J", null, null, true, Property.EMPTY_ARRAY);
    }

    private Priority extractPriority(final Level level) {
        if (level == Level.TRACE) return Priority.TRACE;
        if (level == Level.DEBUG) return Priority.DEBUG;
        if (level == Level.INFO)  return Priority.INFO;
        if (level == Level.WARN)  return Priority.WARN;
        if (level == Level.ERROR) return Priority.ERROR;
        if (level == Level.FATAL) return Priority.FATAL;
        throw new IllegalArgumentException("Unknown Logged Level " + level);
    }

    public void append(final LogEvent event) {
        final String loggerName = event.source();
        final String message = event.message();

        // Attention au FQCN : évite le conflit avec org.apache.logging.log4j.core.LogEvent
        com.openpojo.utils.log.LogEvent le =
                new com.openpojo.utils.log.LogEvent(loggerName, event.priority(), message);

        EventLogger.registerEvent(this.getClass(), le);
    }

    /**
     * Assert: nombre d’événements par source ET priorité.
     */
    public synchronized Integer getCountBySourceByPriority(final String source, final Priority priority) {
        return EventLogger.getCountByAppenderBySourceByPriority(this.getClass(), source, priority);
    }

    /**
     * Assert: nombre d’événements par source (toutes priorités confondues).
     */
    public synchronized Integer getCountBySource(final String source) {
        return EventLogger.getCountBySource(this.getClass(), source);
    }

    /**
     * Récupérer les événements par source et priorité.
     */
    public synchronized List<LogEvent> getLoggedEventsBySourceByPriority(final String source, final Priority priority) {
        return EventLogger.getLoggedEventsByAppenderBySourceByPriority(this.getClass(), source, priority);
    }

    public void resetAppender() {
        EventLogger.resetEvents(this.getClass());
    }

    @Override
    public void append(org.apache.logging.log4j.core.LogEvent event) {

    }
}