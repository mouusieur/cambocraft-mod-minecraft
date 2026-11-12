package com.cambocraft.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Journalisation centralisée : le préfixe rappelle toujours le sous-système concerné. */
public final class Log {
    private static final Logger LOGGER = LoggerFactory.getLogger("Cambocraft");

    private Log() {}

    public static void info(String message, Object... args) {
        LOGGER.info(message, args);
    }

    public static void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }

    public static void error(String message, Object... args) {
        LOGGER.error(message, args);
    }

    /** Signale une donnée invalide sans interrompre le chargement du jeu. */
    public static void dataError(String message, Object... args) {
        LOGGER.error("[données] " + message, args);
    }
}
