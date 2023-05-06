package dev.themackabu.requests.helpers

import java.io.File
import java.time.LocalDateTime
import io.ktor.server.request.*
import io.ktor.server.application.*
import dev.themackabu.requests.plugin
import kotlinx.serialization.json.Json
import io.ktor.server.application.hooks.*
import java.time.format.DateTimeFormatter
import kotlinx.serialization.encodeToString
import dev.themackabu.requests.models.logger.Config
import dev.themackabu.requests.models.logger.Message

private val config = Config(path = plugin.dataFolder.absolutePath + File.separator + "log" + File.separator + "api.log")

val FileLogger = createApplicationPlugin(name = "FileLogger", createConfiguration = ::config) {
    val path = pluginConfig.path
    val logFile = File(path)

    on(ResponseSent) { call ->
        val uri = call.request.uri
        val status = call.response.status()?.value
        val httpMethod = call.request.httpMethod.value
        val userAgent = call.request.headers["User-Agent"]
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

        val logEntry = Json.encodeToString(Message(
            timestamp = timestamp,
            status = "$status".toInt(),
            method = httpMethod,
            path = uri,
            userAgent = userAgent.toString()
        ))

        logFile.appendText("$logEntry\n")
    }
}
