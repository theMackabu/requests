package dev.themackabu.requests.helpers

import io.ktor.events.EventDefinition
import io.ktor.server.application.hooks.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*

import java.io.File
import com.google.gson.Gson
import java.time.LocalDateTime
import dev.themackabu.requests.plugin
import java.time.format.DateTimeFormatter

interface LogMessage {
    val timestamp: String
    val status: Int
    val path: String
    val method: String
    val userAgent: String
}

class PluginConfiguration {
    var path: String = plugin.dataFolder.absolutePath + File.separator + "log" + File.separator + "api.log"
}

val FileLogger = createApplicationPlugin(name = "FileLogger", createConfiguration = ::PluginConfiguration) {
    val path = pluginConfig.path
    val logFile = File(path)
    val gson = Gson()

    on(ResponseSent) { call ->
        val uri = call.request.uri
        val status = call.response.status()?.value
        val httpMethod = call.request.httpMethod.value
        val userAgent = call.request.headers["User-Agent"]
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        val logEntry = gson.toJson(object: LogMessage {
            override var timestamp = "$timestamp"
            override var status = "$status".toInt()
            override var method = "$httpMethod"
            override var path = "$uri"
            override var userAgent = "$userAgent"
        })

        logFile.appendText("$logEntry\n")
    }
}
