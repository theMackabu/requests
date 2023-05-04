package dev.themackabu.requests.models
import kotlinx.serialization.*

@Serializable
data class UserInterface(
    val name: String,
    val uuid: String,
    val token: String,
)
