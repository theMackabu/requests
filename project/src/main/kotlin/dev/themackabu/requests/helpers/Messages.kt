package dev.themackabu.requests.helpers

import java.io.File
import org.bukkit.ChatColor
import de.leonhard.storage.Toml
import dev.themackabu.requests.log
import dev.themackabu.requests.plugin
import dev.themackabu.requests.config
import org.bukkit.command.CommandSender
import dev.themackabu.requests.audiences
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

class Messages(path: String) {
    private val file = File(plugin.dataFolder.absolutePath + File.separator + path)
    private var messages = Toml(file)
    private val mm = MiniMessage.miniMessage()

    
    fun getMessage(scope: String, key: String, placeholders: HashMap<String, String>?, addPrefix: Boolean = false): Component {
        @Suppress("UNCHECKED_CAST")
        val messages = this.messages.get(scope) as HashMap<String, String>
        var message: String? = messages[key]

        if (message.isNullOrEmpty()) {
            log.error("The key $key was not found in your language file. Try to delete the file and generate it again to solve this issue.")
            return mm.deserialize("<dark_red>[FATAL]<red>Language keys are missing. Contact the server owner to resolve.")
        }

        if (placeholders != null) message = this.replacePlaceholders(message, placeholders)
        if (addPrefix) message = config.plugin["prefix"] + message

        return mm.deserialize(message)
    }

    fun send(sender: CommandSender, message: String) {
        audiences.sender(sender).sendMessage(mm.deserialize(message))
    }

    fun sendMessage(sender: CommandSender, textComponent: Component) {
        audiences.sender(sender).sendMessage(textComponent)
    }

    fun toString(message: String): String {
        return GsonComponentSerializer.gson().serialize(mm.deserialize(message))
    }

    fun toLegacyString(textComponent: Component): String {
        val legacy = LegacyComponentSerializer.legacyAmpersand().serialize(textComponent)
        return ChatColor.translateAlternateColorCodes('&', legacy)
    }

    private fun replacePlaceholders(message: String, placeholders: HashMap<String, String>): String {
        var newMessage = message
        for ((key, value) in placeholders) {
            newMessage = newMessage.replace(key, value)
        }

        return newMessage
    }
}