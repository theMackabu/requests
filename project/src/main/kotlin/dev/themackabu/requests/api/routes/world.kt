package dev.themackabu.requests.api.routes

import io.ktor.server.auth.*
import dev.themackabu.requests.server
import dev.themackabu.requests.helpers.fromJson
import dev.themackabu.requests.models.api.World
import io.ktor.server.application.ApplicationCall
import dev.themackabu.requests.models.cmd.ResponseContext
import dev.themackabu.requests.models.api.AuthenticatedResponse

fun serverWorlds(call: ApplicationCall): AuthenticatedResponse<MutableList<World>> {
    val worlds = mutableListOf<World>()
    val info = call.principal<UserIdPrincipal>()!!.name

    server.worlds.forEach {
        worlds.add(World(
            name = it.name,
            players = it.playerCount,
            chunksLoaded = it.chunkCount,
        ))
    }
    
    return AuthenticatedResponse(
        code = 200,
        response = info.fromJson<ResponseContext>(),
        data = worlds
    )
}
