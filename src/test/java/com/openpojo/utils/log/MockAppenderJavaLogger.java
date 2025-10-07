package com.openpojo.utils.log;

import java.util.List;

import com.openpojo.utils.log.LogEvent.Priority;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;                       // Log4j 2
import org.apache.logging.log4j.core.appender.AbstractAppender;      // Base appender
import org.apache.logging.log4j.core.config.Property;

/**
 * MockAppenderJavaLogger (Log4j 2)
 * - Appender de test : convertit les niveaux Log4j 2 en Priority et enregistre via EventLogger.
 * - Option julSevereAsFatal : pour émuler le mapping historique JUL SEVERE -> FATAL.
 */
public class MockAppenderJavaLogger extends AbstractAppender implements MockAppender {

    private final boolean julSevereAsFatal;

    public MockAppenderJavaLogger() {
        this(false);
    }

    /**
     * @param julSevereAsFatal si true, Level.ERROR est compté comme Priority.FATAL (utile si JUL SEVERE doit rester FATAL)
     */
    public MockAppenderJavaLogger(boolean julSevereAsFatal) {
        super("MockAppenderJavaLogger", /*filter*/ null, /*layout*/ null, /*ignoreExceptions*/ true, Property.EMPTY_ARRAY);
        this.julSevereAsFatal = julSevereAsFatal;
    }

    private Priority toPriority(final Level level) {
        if (level == Level.TRACE) return Priority.TRACE;
        if (level == Level.DEBUG) return Priority.DEBUG;
        if (level == Level.INFO)  return Priority.INFO;
        if (level == Level.WARN)  return Priority.WARN;
        if (level == Level.ERROR) return julSevereAsFatal ? Priority.FATAL : Priority.ERROR;
        if (level == Level.FATAL) return Priority.FATAL;
        // Niveaux spéciaux : on choisit un fallback neutre
        if (level == Level.ALL || level == Level.OFF) return Priority.INFO;
        throw new IllegalArgumentException("Unknown Logged Level " + level);
    }

    @Override
    public void append(final LogEvent event) {
        final String loggerName = event.getLoggerName();
        final String message = (event.getMessage() != null) ? event.getMessage().getFormattedMessage() : null;

        // FQCN explicite pour éviter l’ambiguïté avec org.apache.logging.log4j.core.LogEvent
        com.openpojo.utils.log.LogEvent le =
                new com.openpojo.utils.log.LogEvent(loggerName, toPriority(event.getLevel()), message);

        EventLogger.registerEvent(this.getClass(), le);
    }

    // --- API d’assertion conservée ---

    public synchronized Integer getCountBySourceByPriority(final String source, final Priority priority) {
        return EventLogger.getCountByAppenderBySourceByPriority(this.getClass(), source, priority);
    }

    public synchronized Integer getCountBySource(final String source) {
        return EventLogger.getCountBySource(this.getClass(), source);
    }

    public synchronized List<LogEvent> getLoggedEventsBySourceByPriority(final String source, final Priority priority) {
        return EventLogger.getLoggedEventsByAppenderBySourceByPriority(this.getClass(), source, priority);
    }

    public void resetAppender() {
        EventLogger.resetEvents(this.getClass());
    }
}