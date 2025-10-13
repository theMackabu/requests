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
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.netty.NettyApplicationEngine
import org.bukkit.conversations.ConversationFactory
import org.apache.logging.log4j.core.config.Configurator
import net.kyori.adventure.platform.bukkit.BukkitAudiences
import org.bukkit.event.HandlerList

internal lateinit var plugin: JavaPlugin; private set
internal lateinit var messages: Messages; private set
internal lateinit var mainDB: SatchelStorage; private set
internal lateinit var config: Config; private set
internal lateinit var playerDB: SatchelStorage; private set
internal lateinit var audiences: BukkitAudiences; private set
internal lateinit var conversation: ConversationFactory; private set
internal lateinit var apiServer: EmbeddedServer<NettyApplicationEngine, NettyApplicationEngine.Configuration>; private set

internal val log = Logger
internal var apiPort = 5000; private set
internal val server = Bukkit.getServer()
internal val subCommands: HashMap<String, SubCommand> = Commands.subCommands

fun loadPlugin(internal: JavaPlugin) {
    plugin = internal
    
    if (::audiences.isInitialized) audiences.close()
    audiences = BukkitAudiences.create(internal)
    conversation = ConversationFactory(internal)

    config = config("config.toml")
    messages = Messages("messages.toml")
    
    mainDB = database(config.database.tokens)
    playerDB = database(config.database.players)
    
    if (::apiServer.isInitialized) apiServer.stop(0, 0)
    
    apiPort = config.api.port
    apiServer = startServer(apiPort)
}

class Entry: JavaPlugin() {
    override fun onEnable() {
        Configurator.setLevel("dev.themackabu.requests.shaded.ktor.application", Level.OFF)
        Configurator.setLevel("dev.themackabu.requests.shaded.ktor.server", Level.OFF)
        
        loadPlugin(this)
        mainDB["token."] = ""

        this.getCommand("api")?.setExecutor(Commands())
        this.getCommand("api")?.tabCompleter = Commands()
        server.pluginManager.registerEvents(dataListener(), this)

        log.info("plugin enabled.")
    }

    override fun onDisable() {
        HandlerList.unregisterAll(this)
        
        if (::apiServer.isInitialized) apiServer.stop(0, 0)
        if (::audiences.isInitialized) audiences.close()
        
        log.info("plugin disabled.")
    }
}
