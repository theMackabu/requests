package dev.themackabu.requests.api

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.netty.*
import io.ktor.server.engine.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import dev.themackabu.requests.log
import io.ktor.server.application.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.plugins.defaultheaders.*
import dev.themackabu.requests.helpers.FileLogger
import io.ktor.server.plugins.contentnegotiation.*
import dev.themackabu.requests.api.routes.playerInfo
import dev.themackabu.requests.api.routes.serverInfo
import dev.themackabu.requests.api.routes.serverIcon
import dev.themackabu.requests.api.routes.serverWorlds
import dev.themackabu.requests.models.api.ApiException


fun startServer(port: Int): NettyApplicationEngine {
    val server = embeddedServer(Netty, port = port) {
        install(DefaultHeaders)
        install(DoubleReceive)
        install(FileLogger)

        install(ContentNegotiation) { json() }
        install(CORS) { anyHost() }

        install(Authentication) {
            bearer { authenticate { tokenCredential -> checkToken(tokenCredential.token) } }
        }

        install(StatusPages) {
            exception<Throwable> { call, cause -> serverError(call, cause) }
            exception<ApiException> { call, cause -> apiError(call, cause) }
            status(HttpStatusCode.Unauthorized) { call, status -> unauthorizedPage(call, status) }
            status(HttpStatusCode.Forbidden) { call, status -> forbiddenPage(call, status) }
            status(HttpStatusCode.InternalServerError) { call, status -> serverErrorPage(call, status) }
        }

        routing {
            authenticate {
                get("/players/{uuid}") { call.respond(playerInfo(call)) }
                get("/server") { call.respond(serverInfo(call)) }
                get("/server/worlds") { call.respond(serverWorlds(call)) }
                get("/server/icon") { call.respondFile(serverIcon()) }
            }
        }
    }

    server.start()
    log.info("API server started on port $port.")

    return server
}