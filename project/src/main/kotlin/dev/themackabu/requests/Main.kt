package dev.themackabu.requests

import dev.themackabu.requests.cmd.Commands
import dev.themackabu.requests.models.SubCommandsInterface
import dev.themackabu.requests.config.ConfigManager
import dev.themackabu.requests.config.MessagesManager
import dev.themackabu.requests.models.ConfigInterface
import dev.themackabu.requests.utils.PlayerDataListener
import dev.themackabu.requests.utils.Logger
import dev.themackabu.requests.db.Database
import dev.themackabu.requests.api.Api

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.conversations.ConversationFactory
import org.bukkit.conversations.ConversationPrefix
import de.leonhard.storage.Toml
import io.ktor.server.netty.*
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.logging.Level
import cafe.adriel.satchel.SatchelStorage

class Main: JavaPlugin() {
    companion object {
        private lateinit var plugin: Plugin
        private lateinit var internal: Toml
        private var port: Int = 5000

        lateinit var db: SatchelStorage
        lateinit var players: SatchelStorage
        lateinit var messages: Toml
        lateinit var api: NettyApplicationEngine;
        lateinit var conversation: ConversationFactory;
        lateinit var audiences: BukkitAudiences;
        lateinit var config: ConfigInterface
        lateinit var messagesManager: MessagesManager

        val subCommands: HashMap<String, SubCommandsInterface> = Commands.subCommands

        @Suppress("UNCHECKED_CAST")
        fun reloadConfigs() {
            this.createConfigs()
            internal = ConfigManager(this.plugin, "config.toml").getConfig()

            this.config = object: ConfigInterface {
                override var api = internal.get("api") as HashMap<String, String>
                override var plugin = internal.get("plugin") as HashMap<String, String>
                override var database = internal.get("database") as HashMap<String, String>
            }

            this.port = if (this.config.database["port"] != null) (this.config.api["port"] as String).toInt() else 5000
            this.db = Database(if (this.config.database["tokens"] != null) this.config.database["tokens"] as String else "tokens.db").init()
            this.players = Database(if (this.config.database["players"] != null) this.config.database["players"] as String else "players.db").init()
            this.messages = ConfigManager(this.plugin, "messages.toml").getConfig()
            this.messagesManager = MessagesManager(this.messages)
        }

        fun getPlugin(): Plugin {
            return this.plugin
        }

        private fun createConfigs() {
            var dataFolder: String = plugin.dataFolder.absolutePath + File.separator
            val logs = File("${dataFolder}log${File.separator}api.log")
            logs.getParentFile().mkdirs()

            if (!File("${dataFolder}log${File.separator}api.log").exists()) logs.createNewFile()
            if (!File("${dataFolder}config.toml").exists()) plugin.saveResource("config.toml", false)
            if (!File("${dataFolder}messages.toml").exists()) plugin.saveResource("messages.toml", false)
        }
    }

    override fun onEnable() {
        plugin = this
        api = Api(port).start()
        audiences = BukkitAudiences.create(this)
        conversation = ConversationFactory(this)

        reloadConfigs()
        this.getCommand("api")?.setExecutor(Commands())
        this.getCommand("api")?.tabCompleter = Commands()
        getServer().getPluginManager().registerEvents(PlayerDataListener(), this)

        Logger.log("INFO", "plugin enabled.")
    }

    override fun onDisable() {
        api.stop(0, 0)
        Logger.log("INFO", "plugin disabled.")
    }
}
