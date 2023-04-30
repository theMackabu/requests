package dev.themackabu.requests

import dev.themackabu.requests.cmd.Commands
import dev.themackabu.requests.cmd.subCommands.SubCommandsInterface
import dev.themackabu.requests.config.ConfigManager
import dev.themackabu.requests.config.MessagesManager
import dev.themackabu.requests.config.ConfigInterface
import dev.themackabu.requests.utils.Logger

import de.leonhard.storage.Toml
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.logging.Level

class Main: JavaPlugin() {
    companion object {
        private lateinit var plugin: Plugin
        private lateinit var internal: Toml

        val subCommands: HashMap<String, SubCommandsInterface> = Commands.subCommands

        lateinit var config: ConfigInterface
        lateinit var messages: Toml
        lateinit var messagesManager: MessagesManager

        @Suppress("UNCHECKED_CAST")
        fun reloadConfigs() {
            this.createConfigs()
            internal = ConfigManager(this.plugin, "config.toml").getConfig()

            this.config = object: ConfigInterface {
                override var api = internal.get("api") as HashMap<String, String>
                override var plugin = internal.get("plugin") as HashMap<String, String>
                override var database = internal.get("database") as HashMap<String, String>
            }

            this.messages = ConfigManager(this.plugin, "messages.toml").getConfig()
            this.messagesManager = MessagesManager(this.messages)
        }

        private fun createConfigs() {
            var dataFolder: String = plugin.dataFolder.absolutePath + File.separator
            if (!File("${dataFolder}config.toml").exists()) plugin.saveResource("config.toml", false)
            if (!File("${dataFolder}messages.toml").exists()) plugin.saveResource("messages.toml", false)
        }
    }

    override fun onEnable() {
        plugin = this

        reloadConfigs()
        this.getCommand("api")?.setExecutor(Commands())
        this.getCommand("api")?.tabCompleter = Commands()

        Logger.log(Level.INFO, "plugin enabled.")
    }

    override fun onDisable() {
        Logger.log(Level.INFO, "plugin disabled.")
    }
}
