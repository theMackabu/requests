package dev.themackabu.requests.config

import dev.themackabu.requests.Main
import dev.themackabu.requests.utils.Logger
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import org.bukkit.command.CommandSender
import net.kyori.adventure.text.Component
import de.leonhard.storage.Toml
import java.util.logging.Level

class MessagesManager(config: Toml) {
    private var messages: Toml

    init { this.messages = config }

    fun getMessage(scope: String, key: String, placeholders: HashMap<String, String>?, addPrefix: Boolean = false): Component {
        val err = "The key $key was not found in your language file. Try to delete the file and generate it again to solve this issue."
        val messages = this.messages.get(scope) as HashMap<String, String>
        var message: String? = messages[key]
        var mm = MiniMessage.miniMessage();

        if (message.isNullOrEmpty()) {
            Logger.log(Level.SEVERE, err)
            return mm.deserialize("<dark_red>$err");
        }

        if (placeholders != null) message = this.replacePlaceholders(message, placeholders)
        if (addPrefix) message = Main.config.plugin["prefix"] + message

        return mm.deserialize(message);
    }

    fun sendMessage(sender: CommandSender, textComponent: Component) {
        Main.audiences.sender(sender).sendMessage(textComponent);
    }

    private fun replacePlaceholders(message: String, placeholders: HashMap<String, String>): String {
        var newMessage = message
        for ((key, value) in placeholders) {
            newMessage = newMessage.replace(key, value)
        }

        return newMessage
    }
}
