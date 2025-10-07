package com.openpojo.utils.log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.openpojo.utils.log.LogEvent.Priority;

/**
 * Holds logged events for testing, keyed by appender class.
 * Modernized for Java 21 with proper thread-safety.
 */
public final class EventLogger {

    // Map: Appender class -> thread-safe list of events
    private static final ConcurrentMap<Class<? extends MockAppender>, List<LogEvent>> EVENTS =
            new ConcurrentHashMap<>();

    private EventLogger() { /* utility */ }

    /** Clears events for the given appender class. */
    public static void resetEvents(final Class<? extends MockAppender> appender) {
        final List<LogEvent> logEvents = EVENTS.get(appender);
        if (logEvents != null) {
            logEvents.clear();
        }
    }

    /** Registers a new event for the given appender class. */
    public static synchronized void registerEvent(final Class<? extends MockAppender> appender,
                                                  final LogEvent logEvent) {
        // synchronized conservé pour compat avec l’implémentation d’origine ;
        // computeIfAbsent + CopyOnWriteArrayList garantissent déjà la sûreté en concurrence.
        EVENTS.computeIfAbsent(appender, k -> new CopyOnWriteArrayList<>()).add(logEvent);
    }

    /**
     * Count events for a given appender, filtered by source and priority.
     */
    public static synchronized Integer getCountByAppenderBySourceByPriority(
            final Class<? extends MockAppender> appender,
            final String source,
            final Priority priority) {

        final List<LogEvent> logEvents = EVENTS.get(appender);
        if (logEvents == null || logEvents.isEmpty()) return 0;

        int count = 0;
        for (LogEvent entry : logEvents) {
            if (source.equals(entry.source()) && priority.equals(entry.priority())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Count events for a given appender and source (all priorities).
     */
    public static synchronized Integer getCountBySource(
            final Class<? extends MockAppender> appender,
            final String source) {

        final List<LogEvent> logEvents = EVENTS.get(appender);
        if (logEvents == null || logEvents.isEmpty()) return 0;

        int count = 0;
        for (LogEvent entry : logEvents) {
            if (source.equals(entry.source())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Get all logged events for a given appender, filtered by source and priority.
     */
    public static synchronized List<LogEvent> getLoggedEventsByAppenderBySourceByPriority(
            final Class<? extends MockAppender> appender,
            final String source,
            final Priority priority) {

        final List<LogEvent> logEvents = EVENTS.get(appender);
        if (logEvents == null || logEvents.isEmpty()) return List.of();

        final List<LogEvent> result = new ArrayList<>();
        for (LogEvent entry : logEvents) {
            if (source.equals(entry.source()) && priority.equals(entry.priority())) {
                result.add(entry);
            }
        }
        return result;
    }
}