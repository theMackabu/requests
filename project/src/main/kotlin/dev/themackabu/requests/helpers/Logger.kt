package dev.themackabu.requests.helpers

import java.util.logging.Level
import dev.themackabu.requests.plugin

class Logger {
    companion object {
        fun info(message: String) { plugin.logger.log(Level.INFO, "[requests] $message") }
        fun debug(message: String) { plugin.logger.log(Level.FINE, "[requests] $message") }
        fun warning(message: String) { plugin.logger.log(Level.WARNING, "[requests] $message") }
        fun error(message: String) { plugin.logger.log(Level.SEVERE, "[requests] $message") }
    }
}
