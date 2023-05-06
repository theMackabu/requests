package dev.themackabu.requests.api.routes

import cafe.adriel.satchel.ktx.get
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import dev.themackabu.requests.playerDB
import kotlinx.serialization.decodeFromString
import io.ktor.server.application.ApplicationCall
import dev.themackabu.requests.models.api.Response
import dev.themackabu.requests.models.api.PlayerInfo

fun getPlayer(uuid: String): PlayerInfo {
    val data = playerDB.get<String>("players.$uuid")
    return Json.decodeFromString<PlayerInfo>(data as String)
}

fun playerInfo(call: ApplicationCall): Any {
    val uuid = call.parameters["uuid"]
    return try { getPlayer(uuid as String) } catch (e: NullPointerException) {
        call.response.status(HttpStatusCode.NotFound)
        Response(
            code = 404,
            error = "player.not_found",
            message = "The player $uuid could not be found."
        )
    }
}