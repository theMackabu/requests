package dev.themackabu.requests.models.api
import kotlinx.serialization.*

@Serializable
data class Server (
    val name: String,
    val motd: String,
    val onlineMode: Boolean,
    val version: String,
    val bukkitVersion: String,
    val viewDistance: Int,
    val players: Players,
    val health: Health,
    val dimension: Dimension
)

@Serializable
data class Players (
    val allowFlight: Boolean,
    val isWhitelist: Boolean,
    val defautGamemode: String,
    val resourcePack: Boolean,
    val isSecureProfile: Boolean,
    val playerCount: PlayerCount
)

@Serializable
data class PlayerCount (
    val online: Int,
    val max: Int
)

@Serializable
data class Health (
    val cpuCount: Int,
    val uptime: Long,
    val warningState: String,
    val tps: Tps,
    val memory: Memory
)

@Serializable
data class Tps (
    val oneMinute: Double,
    val fiveMinutes: Double,
    val fifteenMinutes: Double
)

@Serializable
data class Memory (
    val totalMemory: Long,
    val maxMemory: Long,
    val freeMemory: Long
)

@Serializable
data class Dimension (
    val defaultType: String,
    val allowNether: Boolean,
    val allowEnd: Boolean
)