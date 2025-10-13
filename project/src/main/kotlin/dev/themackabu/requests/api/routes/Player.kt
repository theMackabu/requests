package dev.themackabu.requests.api.routes
import org.bukkit.Bukkit
import io.ktor.server.auth.*
import cafe.adriel.satchel.ktx.get
import org.bukkit.attribute.Attribute
import dev.themackabu.requests.playerDB
import dev.themackabu.requests.helpers.fromJson
import io.ktor.server.application.ApplicationCall
import dev.themackabu.requests.models.api.PlayerInfo
import dev.themackabu.requests.models.api.PlayerHealth
import dev.themackabu.requests.models.api.PlayerStatus
import dev.themackabu.requests.models.api.ApiException
import dev.themackabu.requests.models.cmd.ResponseContext
import dev.themackabu.requests.helpers.player.getExp
import dev.themackabu.requests.models.api.AuthenticatedResponse

fun getPlayer(uuid: String): PlayerInfo? = playerDB.get<String>("players.$uuid")?.fromJson<PlayerInfo>()

fun playersList(call: ApplicationCall): AuthenticatedResponse<List<Map<String, String>>> {
    val info = call.principal<UserIdPrincipal>()!!.name
    
    val players = Bukkit.getOnlinePlayers().map { player ->
        mapOf(
            "name" to player.name,
            "uuid" to player.uniqueId.toString()
        )
    }
    
    return AuthenticatedResponse(
        code = 200,
        response = info.fromJson<ResponseContext>(),
        data = players
    )
}

fun playerInfo(call: ApplicationCall): AuthenticatedResponse<PlayerInfo> {
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