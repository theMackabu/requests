package dev.themackabu.requests.models.api
import kotlinx.serialization.Serializable

@Serializable
data class Response (
    val code: Int,
    val error: String?,
    val message: String?,
)