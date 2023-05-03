package dev.themackabu.requests

import java.io.File
import de.leonhard.storage.Toml
import dev.themackabu.requests.plugin
import dev.themackabu.requests.helpers.createFiles
import dev.themackabu.requests.models.ConfigInterface

@SuppressWarnings("unchecked")
fun config(path: String): ConfigInterface {
    createFiles()
    
    val file = File(plugin.dataFolder.absolutePath + File.separator + path)
    val config = Toml(file)
    
    return ConfigInterface(
        api = config.get("api") as? HashMap<String, String> ?: HashMap<String, String>(),
        plugin = config.get("plugin") as? HashMap<String, String> ?: HashMap<String, String>(),
        database = config.get("database") as? HashMap<String, String> ?: HashMap<String, String>()
    )
}