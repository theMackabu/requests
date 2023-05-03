package dev.themackabu.requests.models.api

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Response (
    val code: Int,
    val error: String?,
    val message: String?,
)