package dev.themackabu.requests.models.api

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Response (
    val error: String?,
    val code: Int,
    val message: String,
)