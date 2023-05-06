package dev.themackabu.requests.models.logger
import kotlinx.serialization.Serializable

@Serializable
data class Message (
    val timestamp: String,
    val status: Int,
    val path: String,
    val method: String,
    val userAgent: String
)

data class Config (var path: String)
