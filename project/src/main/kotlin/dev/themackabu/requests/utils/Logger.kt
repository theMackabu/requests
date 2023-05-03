package dev.themackabu.requests.utils

import org.bukkit.Bukkit
import java.util.logging.Level

class Logger {
    companion object {
        fun log(level: String, message: String) {
            Bukkit.getLogger().log(Level.parse(level), "[requests] $message")
        }
    }
}
