package dev.themackabu.requests.api.routes

import dev.themackabu.requests.playerDB
import dev.themackabu.requests.models.api.PlayerInfo
import dev.themackabu.requests.models.api.Response

import cafe.adriel.satchel.ktx.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import kotlinx.serialization.*
import kotlinx.serialization.json.*

fun getPlayer(uuid: String): PlayerInfo {
    val data = playerDB.get<String>("players.$uuid")
    val json = Json.decodeFromString<PlayerInfo>(data as String)

    return json
}

fun playerInfo(call: ApplicationCall): Any {
    val uuid = call.parameters["uuid"]
    try {
        return getPlayer(uuid as String)
    } catch (e: NullPointerException) {
        call.response.status(HttpStatusCode.NotFound)
        return Response(
            code = 404,
            error = "player.not_found",
            message = "The player $uuid could not be found."
        )
    }
}