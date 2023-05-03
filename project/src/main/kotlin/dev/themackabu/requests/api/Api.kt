package dev.themackabu.requests.api

import dev.themackabu.requests.utils.Logger
import dev.themackabu.requests.api.FileLogger

import dev.themackabu.requests.api.routes.playerInfo
import dev.themackabu.requests.api.routes.serverInfo
import dev.themackabu.requests.api.routes.serverIcon
import dev.themackabu.requests.api.routes.serverWorlds

import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.http.*

class Api(port: Int) {
    private var server: NettyApplicationEngine

    init {
        server = embeddedServer(Netty, port = port) {
            install(DefaultHeaders)
            install(DoubleReceive)
            install(FileLogger)

            install(ContentNegotiation) { json() }
            install(CORS) { anyHost() }

            routing {
                get("/players/{uuid}") { call.respond(playerInfo(call)) }
                get("/server") { call.respond(serverInfo()) }
                get("/server/icon") { call.respondFile(serverIcon()) }
                get("/server/worlds") { call.respond(serverWorlds()) }
            }
        }

        server.start()
        Logger.log("INFO", "API server started on port $port.")
    }

    fun start(): NettyApplicationEngine {
        return server
    }
}