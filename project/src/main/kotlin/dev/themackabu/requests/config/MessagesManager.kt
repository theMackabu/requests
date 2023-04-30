package dev.themackabu.requests.config

import dev.themackabu.requests.Main
import dev.themackabu.requests.utils.Logger
import org.bukkit.ChatColor
import de.leonhard.storage.Toml
import java.util.logging.Level

class MessagesManager(config: Toml) {
    private var messages: Toml

    init { this.messages = config }

    fun getMessage(scope: String, key: String, placeholders: HashMap<String, String>?, addPrefix: Boolean = false): String {
        val messages = this.messages.get(scope) as HashMap<String, String>
        var message = messages[key] as String

        if (message == "") {
            Logger.log(Level.SEVERE, "The key $key was not found in your language file. Try to delete the file and generate it again to solve this issue.")
            return "Message not found."
        }

        if (placeholders != null) message = this.replacePlaceholders(message, placeholders)
        if (addPrefix) message = Main.config.plugin["prefix"] + message

        return ChatColor.translateAlternateColorCodes('&', message)
    }

    private fun replacePlaceholders(message: String, placeholders: HashMap<String, String>): String {
        var newMessage = message
        for ((key, value) in placeholders) {
            newMessage = newMessage.replace(key, value)
        }

        return newMessage
    }
}
