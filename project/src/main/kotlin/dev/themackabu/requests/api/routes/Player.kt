package dev.themackabu.requests.api.routes

import io.ktor.server.auth.*
import cafe.adriel.satchel.ktx.get
import io.ktor.http.HttpStatusCode
import dev.themackabu.requests.helpers.fromJson
import dev.themackabu.requests.playerDB
import io.ktor.server.application.ApplicationCall
import dev.themackabu.requests.models.api.Response
import dev.themackabu.requests.models.api.PlayerInfo
import dev.themackabu.requests.models.cmd.ResponseContext
import dev.themackabu.requests.models.api.AuthenticatedResponse

fun getPlayer(uuid: String): PlayerInfo? = playerDB.get<String>("players.$uuid")?.fromJson<PlayerInfo>()

fun playerInfo(call: ApplicationCall): Any {
    val uuid = call.parameters["uuid"]
    val info = call.principal<UserIdPrincipal>()!!.name
    
    return try {
        AuthenticatedResponse<PlayerInfo?>(
            code = 200,
            response = info.fromJson<ResponseContext>(),
            data = getPlayer(uuid as String)
        )
    } catch (e: NullPointerException) {
        call.response.status(HttpStatusCode.NotFound)
        Response(
            code = 404,
            error = "player.not_found",
            message = "The player $uuid could not be found."
        )
    }
}