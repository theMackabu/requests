package dev.themackabu.requests.models.api

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class World (
    val name: String,
    val players: Int,
    val chunksLoaded: Int,
)