package com.openpojo.utils.log;

/**
 * For testing, this structure holds the logged events.
 * Modernized for Java 21 using record.
 */
public record LogEvent(String source, Priority priority, String message) {

    public enum Priority {
        TRACE, DEBUG, INFO, WARN, ERROR, FATAL
    }
}