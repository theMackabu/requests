package dev.themackabu.requests.helpers

import java.io.File
import dev.themackabu.requests.plugin
    
internal val dataFolder = plugin.dataFolder.absolutePath + File.separator
internal val cfgPath = dataFolder + "config.toml"
internal val msgPath = dataFolder + "messages.toml"
internal val logPath = dataFolder + "log" + File.separator + "api.log"

fun createFiles() {
    val logs = File(logPath)
    
    logs.getParentFile().mkdirs()
    if (!File(cfgPath).exists()) plugin.saveResource("config.toml", false)
    if (!File(msgPath).exists()) plugin.saveResource("messages.toml", false)
    if (!File(logPath).exists()) logs.createNewFile()
}