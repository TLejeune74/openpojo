package com.openpojo.utils.log;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.AppenderRef;

/**
 * SpyAppender (Log4j 2) : capture les LogEvent d’un logger spécifique,
 * en forçant temporairement son niveau et en restaurant l’état initial.
 */
public class SpyAppender extends AbstractAppender {

    private static final String APPENDER_NAME = "SpyAppender";

    @Override
    public void append(org.apache.logging.log4j.core.LogEvent event) {
        final String loggerName = event.getLoggerName();
        eventMap.computeIfAbsent(loggerName, k -> new CopyOnWriteArrayList<>()).add(new LogEvent(event.getSource().getClassName(), LogEvent.Priority.valueOf(event.getLevel().name()), event.getMessage().getFormattedMessage()));

    }

    private static final class PriorState {
        final Level priorLevel;
        boolean createdLoggerConfig;
        PriorState(Level priorLevel) { this.priorLevel = priorLevel; }
    }

    private final Map<String, PriorState> priorStates = new ConcurrentHashMap<>();
    private final Map<String, List<LogEvent>> eventMap = new ConcurrentHashMap<>();

    public SpyAppender() {
        // layout = null (par défaut), ignoreExceptions = true
        super(APPENDER_NAME, /*filter*/null, /*layout*/null, /*ignoreExceptions*/ true, Property.EMPTY_ARRAY);
    }

    // -------- Lecture des événements capturés --------
    public List<LogEvent> getEventsForLogger(Class<?> clazz) { return getEventsForLogger(clazz.getName()); }

    public List<LogEvent> getEventsForLogger(String loggerName) {
        final List<LogEvent> list = eventMap.get(loggerName);
        return (list == null) ? List.of() : new ArrayList<>(list);
    }

    public void clearEventsForLogger(String loggerName) {
        final List<LogEvent> list = eventMap.get(loggerName);
        if (list != null) list.clear();
    }

    public void clearAll() { eventMap.clear(); }

    // -------- Démarrer / arrêter la capture --------
    public void startCaptureForLogger(Class<?> clazz) { startCaptureForLogger(clazz.getName()); }

    public void startCaptureForLogger(String loggerName) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        if (!isStarted()) {
            start(); // démarre l’appender lui-même
        }
        if (config.getAppender(getName()) == null) {
            config.addAppender(this); // enregistre l’appender dans la config
        }

        // LoggerConfig effectif (peut être root si aucun LoggerConfig explicite)
        LoggerConfig effective = config.getLoggerConfig(loggerName);
        final boolean hasExplicit = effective.getName().equals(loggerName);

        final PriorState ps = new PriorState(effective.getLevel());
        priorStates.put(loggerName, ps);

        LoggerConfig target = effective;
        if (!hasExplicit) {
            // Crée un LoggerConfig dédié pour n’impacter que ce logger
            target = LoggerConfig.createLogger(
                    /*additivity*/ false,
                    /*level*/     Level.ALL,
                    /*loggerName*/loggerName,
                    /*includeLocation*/ "true",
                    /*refs*/      new AppenderRef[0],
                    /*properties*/null,
                    /*config*/    config,
                    /*filter*/    null);
            config.addLogger(loggerName, target);
            ps.createdLoggerConfig = true;
        } else {
            // LoggerConfig existant -> on force son niveau pendant la capture
            target.setLevel(Level.ALL);
        }

        // Attacher l’appender à ce LoggerConfig
        target.addAppender(this, Level.ALL, null);
        ctx.updateLoggers(); // applique la configuration
    }

    public void stopCaptureForLogger(Class<?> clazz) { stopCaptureForLogger(clazz.getName()); }

    public void stopCaptureForLogger(String loggerName) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        final PriorState ps = priorStates.get(loggerName);
        if (ps == null) return;

        final LoggerConfig lc = config.getLoggerConfig(loggerName);
        lc.removeAppender(getName());

        if (ps.createdLoggerConfig && lc.getName().equals(loggerName)) {
            // On supprime le LoggerConfig dédié pour restaurer l’héritage
            config.removeLogger(loggerName);
        } else {
            lc.setLevel(ps.priorLevel);
        }

        priorStates.remove(loggerName);
        ctx.updateLoggers();
    }

    // -------- Log4j2 callback --------
    public void append(LogEvent event) {
        final String loggerName = event.source();
        eventMap.computeIfAbsent(loggerName, k -> new CopyOnWriteArrayList<>()).add(event);
    }
}