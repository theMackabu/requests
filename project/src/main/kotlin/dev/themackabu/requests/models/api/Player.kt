package dev.themackabu.requests.models.api
import kotlinx.serialization.*

@Serializable
data class PlayerInfo (
    val uuid: String,
    val username: String,
    val level: Int,
    val health: PlayerHealth,
    val allowFlight: Boolean,
    val status: PlayerStatus
)

@Serializable
data class PlayerHealth (
    val max: Double?,
    val current: Double,
    val percent: Double,
    val absorption: Double 
)

@Serializable
data class PlayerStatus (
    val online: Boolean
)