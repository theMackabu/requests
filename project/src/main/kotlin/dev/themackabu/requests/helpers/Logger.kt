package dev.themackabu.requests.helpers

import java.util.logging.Level
import dev.themackabu.requests.plugin

class Logger {
    companion object {
        fun info(message: String) { plugin.logger.log(Level.INFO, message) }
        fun debug(message: String) { plugin.logger.log(Level.FINE, message) }
        fun warning(message: String) { plugin.logger.log(Level.WARNING, message) }
        fun error(message: String) { plugin.logger.log(Level.SEVERE, message) }
    }
}
