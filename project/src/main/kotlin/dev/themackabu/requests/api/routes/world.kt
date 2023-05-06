package dev.themackabu.requests.api.routes

import dev.themackabu.requests.server
import dev.themackabu.requests.models.api.World

fun serverWorlds(): MutableList<World> {
    val worlds = mutableListOf<World>()

    server.worlds.forEach {
        worlds.add(World(
            name = it.name,
            players = it.playerCount,
            chunksLoaded = it.chunkCount,
        ))
    }
    
    return worlds
}
