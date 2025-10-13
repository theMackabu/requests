package dev.themackabu.requests

import java.io.File
import de.leonhard.storage.Toml
import dev.themackabu.requests.models.config.*
import dev.themackabu.requests.helpers.createFiles

fun config(path: String): Config {
    createFiles()
    
    val file = File(plugin.dataFolder.absolutePath + File.separator + path)
    val config = Toml(file)
    
    log.info("Loaded config $path")

    return Config(
        api = ApiConfig(
            port = config.getInt("api.port"),
            vault = config.getBoolean("api.vault"),
            luckperms = config.getBoolean("api.luckperms"),
            danger = DangerConfig(
                remote_commands = config.getBoolean("api.danger.remote_commands")
            )
        ),
        plugin = PluginConfig(
            salt = config.getString("plugin.salt"),
            prefix = config.getString("plugin.prefix")
        ),
        database = DatabaseConfig(
            tokens = config.getString("database.tokens"),
            players = config.getString("database.players")
        )
    )
}