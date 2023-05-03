package dev.themackabu.requests.helpers

import dev.themackabu.requests.plugin
import dev.themackabu.requests.config
import dev.themackabu.requests.audiences
import dev.themackabu.requests.helpers.Logger
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.command.CommandSender
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player
import org.bukkit.ChatColor
import java.io.File
import de.leonhard.storage.Toml

class Messages(path: String) {
    private val file = File(plugin.dataFolder.absolutePath + File.separator + path)
    private var messages = Toml(file)
    
    fun getMessage(scope: String, key: String, placeholders: HashMap<String, String>?, addPrefix: Boolean = false): Component {
        val messages = this.messages.get(scope) as HashMap<String, String>
        var message: String? = messages[key]
        var mm = MiniMessage.miniMessage();

        if (message.isNullOrEmpty()) {
            Logger.error("The key $key was not found in your language file. Try to delete the file and generate it again to solve this issue.")
            return mm.deserialize("<dark_red>[FATAL]<red>Language keys are missing. Contact the server owner to resolve.");
        }

        if (placeholders != null) message = this.replacePlaceholders(message, placeholders)
        if (addPrefix) message = config.plugin["prefix"] + message

        return mm.deserialize(message);
    }

    fun send(sender: CommandSender, message: String) {
        var mm = MiniMessage.miniMessage();
        audiences.sender(sender).sendMessage(mm.deserialize(message));
    }

    fun sendMessage(sender: CommandSender, textComponent: Component) {
        audiences.sender(sender).sendMessage(textComponent);
    }


    fun toString(message: String): String {
        var mm = MiniMessage.miniMessage();
        return GsonComponentSerializer.gson().serialize(mm.deserialize(message))
    }

    fun toLegacyString(textComponent: Component): String {
        val legacy = LegacyComponentSerializer.legacyAmpersand().serialize(textComponent)
        return ChatColor.translateAlternateColorCodes('&', legacy);
    }

    private fun replacePlaceholders(message: String, placeholders: HashMap<String, String>): String {
        var newMessage = message
        for ((key, value) in placeholders) {
            newMessage = newMessage.replace(key, value)
        }

        return newMessage
    }
}