package dev.themackabu.requests.models.cmd
import kotlinx.serialization.Serializable

@Serializable
data class UserToken(
    val name: String,
    val uuid: String,
    val token: String,
)

@Serializable
data class ResponseContext(
    val name: String,
    val uuid: String?
)

@Serializable
data class TokenStorage(val uses: Int?)
