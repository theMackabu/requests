package dev.themackabu.requests.api.routes

import io.ktor.server.auth.*
import cafe.adriel.satchel.ktx.get
import dev.themackabu.requests.helpers.fromJson
import dev.themackabu.requests.playerDB
import io.ktor.server.application.ApplicationCall
import dev.themackabu.requests.models.api.PlayerInfo
import dev.themackabu.requests.models.api.ApiException
import dev.themackabu.requests.models.cmd.ResponseContext
import dev.themackabu.requests.models.api.AuthenticatedResponse

fun getPlayer(uuid: String): PlayerInfo? = playerDB.get<String>("players.$uuid")?.fromJson<PlayerInfo>()

fun playerInfo(call: ApplicationCall): AuthenticatedResponse {
    val uuid = call.parameters["uuid"]
    val info = call.principal<UserIdPrincipal>()!!.name
    
    val player = getPlayer(uuid as String) ?: throw ApiException(
        message = "The player $uuid could not be found.",
        error = "player.not_found"
    )
    
    return AuthenticatedResponse(
        code = 200,
        response = info.fromJson<ResponseContext>(),
        data = player
    )
}