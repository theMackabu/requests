package dev.themackabu.requests

import dev.themackabu.requests.cmd.Commands
import dev.themackabu.requests.helpers.Logger
import dev.themackabu.requests.api.startServer
import dev.themackabu.requests.helpers.Messages
import dev.themackabu.requests.models.config.Config
import dev.themackabu.requests.models.cmd.SubCommand
import dev.themackabu.requests.helpers.player.dataListener

import org.bukkit.Bukkit
import org.apache.logging.log4j.Level
import org.bukkit.plugin.java.JavaPlugin
import cafe.adriel.satchel.SatchelStorage
import io.ktor.server.netty.NettyApplicationEngine
import org.bukkit.conversations.ConversationFactory
import org.apache.logging.log4j.core.config.Configurator
import net.kyori.adventure.platform.bukkit.BukkitAudiences

internal lateinit var plugin: JavaPlugin; private set
internal lateinit var messages: Messages; private set
internal lateinit var mainDB: SatchelStorage; private set
internal lateinit var config: Config; private set
internal lateinit var playerDB: SatchelStorage; private set
internal lateinit var audiences: BukkitAudiences; private set
internal lateinit var conversation: ConversationFactory; private set
internal lateinit var apiServer: NettyApplicationEngine; private set

internal val log = Logger
internal var apiPort = 5000; private set
internal val server = Bukkit.getServer()
internal val subCommands: HashMap<String, SubCommand> = Commands.subCommands

fun loadPlugin(internal: JavaPlugin) {
    /* JavaPlugin */
    plugin = internal

    /* messaging */
    audiences = BukkitAudiences.create(internal)
    conversation = ConversationFactory(internal)

    /* config */
    config = config("config.toml")
    messages = Messages("messages.toml")

    /* database */
    mainDB = database(if (config.database["tokens"] != null) config.database["tokens"] as String else "tokens.db")
    playerDB = database(if (config.database["players"] != null) config.database["players"] as String else "players.db")

    /* api server */
    apiPort = if (config.database["port"] != null) (config.api["port"] as String).toInt() else 5000
    apiServer = startServer(apiPort)
}

class Main: JavaPlugin() {
    override fun onEnable() {
        /* disable ktor.logger */
        Configurator.setLevel("ktor.application", Level.OFF)

        /* main services */
        loadPlugin(this)

        /* init token DB */
        mainDB["token."] = ""

        /* register commands */
        this.getCommand("api")?.setExecutor(Commands())
        this.getCommand("api")?.tabCompleter = Commands()
        server.pluginManager.registerEvents(dataListener(), this)

        log.info("plugin enabled.")
    }

    override fun onDisable() {
        /* stop api server */
        apiServer.stop(0, 0)
        log.info("plugin disabled.")
    }
}
