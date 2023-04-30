package dev.themackabu.requests.utils

import org.bukkit.Bukkit
import java.util.logging.Level

class Logger {
    companion object {
        fun log(level: Level, message: String) {
            Bukkit.getLogger().log(level, "[requests] $message")
        }
    }
}
