package dev.themackabu.requests.config

import de.leonhard.storage.Toml
import org.bukkit.plugin.Plugin
import java.io.File

class ConfigManager(plugin: Plugin, path: String) {
    private val file: File
    private val config: Toml
    private val plugin: Plugin

    init {
        this.file = File(plugin.dataFolder.absolutePath + File.separator + path)
        this.config = Toml(this.file)
        this.plugin = plugin
    }

    fun getFile(): File {
        return this.getFile()
    }

    fun getConfig(): Toml {
        return this.config
    }
}
