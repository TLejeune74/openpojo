package com.openpojo.utils.log;

import java.net.URI;
import java.util.List;
import java.util.Objects;

import com.openpojo.utils.log.LogEvent.Priority;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

import org.apache.logging.log4j.core.appender.AbstractAppender;

/**
 * Helper de tests modernisé pour Log4j 2.25.2 (Java 21).
 * - Initialise la config Log4j 2 programmétiquement.
 * - Route les logs JUL -> Log4j 2 via le bridge log4j-jul.
 * Notes:
 *  - Le bridge JUL doit être actif très tôt :
 *    System.setProperty("java.util.logging.manager","org.apache.logging.log4j.jul.LogManager")
 *    avant toute utilisation de java.util.logging. [1](https://stackoverflow.com/questions/62595853/how-do-i-add-an-appender-to-an-existing-logger-in-log4j2)
 *  - La configuration programmatique s’appuie sur LoggerContext/Configuration/LoggerConfig. [3](https://logging.apache.org/log4j/2.x/javadoc/log4j-core/org/apache/logging/log4j/core/Appender.html)
 */
public final class LogHelper {

    private LogHelper() { }

    // ---------------------------
    // Initialisation Log4j 2 only
    // ---------------------------

    /** Initialise Log4j 2 et attache le MockAppenderLog4J au root (niveau ALL). */
    public static void initializeLog4J() {
        EventLogger.resetEvents(MockAppenderLog4J.class);
        attachAppenderToRoot(new MockAppenderLog4J(), Level.ALL);
    }

    // ---------------------------
    // Initialisation JUL -> Log4j 2
    // ---------------------------

    /**
     * Initialise le bridge JUL->Log4j 2 et attache MockAppenderJavaLogger au root.
     * <p>
     * IMPORTANT : le system property "java.util.logging.manager" doit être défini
     * avant tout usage de JUL. Si ce n’est pas possible dans ton contexte, passe
     * par la configuration de ta JVM (vmArgs, container, test runner). [1](https://stackoverflow.com/questions/62595853/how-do-i-add-an-appender-to-an-existing-logger-in-log4j2)
     */
    public static void initializeJavaLogger() {
        EventLogger.resetEvents(MockAppenderJavaLogger.class);

        // Active le bridge JUL->Log4j 2 si non encore positionné (idéalement à passer en -D au démarrage).
        final String key = "java.util.logging.manager";
        final String julBridge = "org.apache.logging.log4j.jul.LogManager";
        if (!julBridge.equals(System.getProperty(key))) {
            System.setProperty(key, julBridge);
            // NB : si JUL a déjà été utilisé plus tôt, ce setProperty peut être trop tardif (contrainte du JDK). [2](https://markaicode.com/fixing-log4j-vulnerabilities-2025-guide/)
        }

        // Côté Log4j 2, on attache un appender qui collectera aussi les logs JUL (via le bridge).
        // Option: new MockAppenderJavaLogger(true) si tu veux compter JUL SEVERE comme FATAL.
        attachAppenderToRoot(new MockAppenderJavaLogger(/*julSevereAsFatal*/ false), Level.ALL);
    }

    /** Initialise les deux flux (Log4j 2 natif + JUL → Log4j 2). */
    public static void initializeLoggers() {
        initializeLog4J();
        initializeJavaLogger();
    }

    // --------------------------------------
    // Méthodes de comptage/lecture (inchangées)
    // --------------------------------------

    public static Integer getTraceCountBySource(final Class<? extends MockAppender> appender, final String source) {
        return EventLogger.getCountByAppenderBySourceByPriority(appender, source, Priority.TRACE);
    }

    public static Integer getDebugCountBySource(final Class<? extends MockAppender> appender, final String source) {
        return EventLogger.getCountByAppenderBySourceByPriority(appender, source, Priority.DEBUG);
    }

    public static Integer getInfoCountBySource(final Class<? extends MockAppender> appender, final String source) {
        return EventLogger.getCountByAppenderBySourceByPriority(appender, source, Priority.INFO);
    }

    public static Integer getWarnCountBySource(final Class<? extends MockAppender> appender, final String source) {
        return EventLogger.getCountByAppenderBySourceByPriority(appender, source, Priority.WARN);
    }

    public static Integer getErrorCountBySource(final Class<? extends MockAppender> appender, final String source) {
        return EventLogger.getCountByAppenderBySourceByPriority(appender, source, Priority.ERROR);
    }

    public static Integer getFatalCountBySource(final Class<? extends MockAppender> appender, final String source) {
        return EventLogger.getCountByAppenderBySourceByPriority(appender, source, Priority.FATAL);
    }

    public static Integer getCountBySource(final Class<? extends MockAppender> appender, final String source) {
        return EventLogger.getCountBySource(appender, source);
    }

    public static List<LogEvent> getTraceEvents(final Class<? extends MockAppender> appender, final String source) {
        return EventLogger.getLoggedEventsByAppenderBySourceByPriority(appender, source, Priority.TRACE);
    }

    public static List<LogEvent> getDebugEvents(final Class<? extends MockAppender> appender, final String source) {
        return EventLogger.getLoggedEventsByAppenderBySourceByPriority(appender, source, Priority.DEBUG);
    }

    public static List<LogEvent> getInfoEvents(final Class<? extends MockAppender> appender, final String source) {
        return EventLogger.getLoggedEventsByAppenderBySourceByPriority(appender, source, Priority.INFO);
    }

    public static List<LogEvent> getWarnEvents(final Class<? extends MockAppender> appender, final String source) {
        return EventLogger.getLoggedEventsByAppenderBySourceByPriority(appender, source, Priority.WARN);
    }

    public static List<LogEvent> getErrorEvents(final Class<? extends MockAppender> appender, final String source) {
        return EventLogger.getLoggedEventsByAppenderBySourceByPriority(appender, source, Priority.ERROR);
    }

    public static List<LogEvent> getFatalEvents(final Class<? extends MockAppender> appender, final String source) {
        return EventLogger.getLoggedEventsByAppenderBySourceByPriority(appender, source, Priority.FATAL);
    }

    // --------------------------------------
    // Initialisation ciblée par type d’appender
    // --------------------------------------

    public static void initialize(final Class<?> mockAppender) {
        if (Objects.equals(mockAppender.getName(), MockAppenderLog4J.class.getCanonicalName())) {
            initializeLog4J();
        }
        if (Objects.equals(mockAppender.getName(), MockAppenderJavaLogger.class.getName())) {
            initializeJavaLogger();
        }
    }

    // --------------------------------------
    // Reset global (reconfig simple Console si pas de conf)
    // --------------------------------------

    /**
     * Réinitialise la configuration Log4j 2.
     * - Si un fichier log4j2.* est présent sur le classpath et pointé par la propriété système
     *   "log4j.configurationFile", Log4j le rechargera.
     * - Sinon on applique une petite configuration Console par défaut.
     * (En Log4j 2, la reconfiguration se fait via LoggerContext/Configuration/Configurator). [4](https://stackoverflow.com/questions/78500693/log4j2-routingappender-with-listappenders-across-multiple-threads)[3](https://logging.apache.org/log4j/2.x/javadoc/log4j-core/org/apache/logging/log4j/core/Appender.html)
     */
    public static void resetLoggers() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        ctx.stop();

        final String cfgProp = System.getProperty("log4j.configurationFile");
        if (cfgProp != null && !cfgProp.isBlank()) {
            // Laisse Log4j 2 recharger la configuration pointée par la propriété.
            ctx.setConfigLocation(URI.create(cfgProp));
        } else {
            // Fallback : simple root logger en CONSOLE niveau INFO.
            // On repart d’un contexte vide et on (ré)attache une config minimale programmatique.
            ctx.start();
            final Configuration config = ctx.getConfiguration();

            // NB: pas d’appender custom ici ; cette méthode vise un reset simple.
            final LoggerConfig root = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
            root.setLevel(Level.INFO);
        }
        ctx.start();          // (re)démarre le contexte si nécessaire
        ctx.updateLoggers();  // applique la config
    }

    // --------------------------------------
    // Utilitaires internes
    // --------------------------------------

    private static void attachAppenderToRoot(final AbstractAppender appender, final Level level) {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration cfg = ctx.getConfiguration();

        if (!appender.isStarted()) appender.start();
        // Enregistre l’appender dans la config s’il n’y est pas déjà.
        if (cfg.getAppender(appender.getName()) == null) {
            cfg.addAppender(appender);
        }

        final LoggerConfig root = cfg.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);
        root.addAppender(appender, level, /*filter*/ null);
        root.setLevel(level);
        ctx.updateLoggers();
    }
}
