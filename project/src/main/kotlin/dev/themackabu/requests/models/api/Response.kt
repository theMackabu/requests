package dev.themackabu.requests.models.api

import kotlinx.serialization.Serializable
import dev.themackabu.requests.models.cmd.ResponseContext

@Serializable
data class Response(
    val code: Int,
    val error: String?,
    val message: String?
)

@Serializable
data class AuthenticatedResponse<T: Any?>(
    val code: Int,
    val response: ResponseContext,
    val data: T
)