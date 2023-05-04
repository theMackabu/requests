package dev.themackabu.requests.api

import dev.themackabu.requests.log
import dev.themackabu.requests.helpers.FileLogger
import dev.themackabu.requests.models.api.Response
import dev.themackabu.requests.api.routes.playerInfo
import dev.themackabu.requests.api.routes.serverInfo
import dev.themackabu.requests.api.routes.serverIcon
import dev.themackabu.requests.api.routes.serverWorlds

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.netty.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.plugins.contentnegotiation.*

fun startServer(port: Int): NettyApplicationEngine {
    val server = embeddedServer(Netty, port = port) {
        install(DefaultHeaders)
        install(DoubleReceive)
        install(FileLogger)

        install(ContentNegotiation) { json() }
        install(CORS) { anyHost() }

        install(Authentication) {
            bearer {
                authenticate { tokenCredential ->
                    if (tokenCredential.token == "abc123") {
                        UserIdPrincipal("jetbrains")
                    } else { null }
                }
            }
        }

        install(StatusPages) {
            status(HttpStatusCode.Unauthorized) { call, status ->
                call.response.status(HttpStatusCode.Unauthorized)
                call.respond(Response(
                    code = 401,
                    error = "server.unauthorized",
                    message = "$status"
                ))
            }

            status(HttpStatusCode.Forbidden) { call, status ->
                call.response.status(HttpStatusCode.Forbidden)
                call.respond(Response(
                    code = 403,
                    error = "server.forbidden",
                    message = "$status"
                ))
            }

            status(HttpStatusCode.InternalServerError) { call, status ->
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(Response(
                    code = 500,
                    error = "server.unauthorized",
                    message = "$status"
                ))
            }

            exception<Throwable> { call, cause ->
                call.response.status(HttpStatusCode.InternalServerError)
                call.respond(Response(
                    code = 500,
                    error = "server.exception",
                    message = "$cause"
                ))
            }
        }

        routing {
            authenticate {
                get("/players/{uuid}") { call.respond(playerInfo(call)) }
                get("/server") { call.respond(serverInfo()) }
                get("/server/worlds") { call.respond(serverWorlds()) }
                get("/server/icon") { call.respondFile(serverIcon()) }
            }
        }
    }

    server.start()
    log.info("API server started on port $port.")

    return server
}