package dev.themackabu.requests

import java.io.File
import de.leonhard.storage.Toml
import dev.themackabu.requests.helpers.createFiles
import dev.themackabu.requests.models.config.Config

fun config(path: String): Config {
    createFiles()
    
    val file = File(plugin.dataFolder.absolutePath + File.separator + path)
    val config = Toml(file)
    
    log.info("Loaded config $path")

    @Suppress("UNCHECKED_CAST")
    return Config(
        api = config.get("api") as? HashMap<String, String> ?: HashMap(),
        plugin = config.get("plugin") as? HashMap<String, String> ?: HashMap(),
        database = config.get("database") as? HashMap<String, String> ?: HashMap()
    )
}