package dev.themackabu.requests.api.routes

import org.bukkit.Bukkit;
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import dev.themackabu.requests.server
import dev.themackabu.requests.models.api.World

fun serverWorlds(): MutableList<World> {
    val worlds = mutableListOf<World>()

    server.getWorlds().forEach {
        worlds.add(World(
            name = it.getName(),
            players = it.getPlayerCount(),
            chunksLoaded = it.getChunkCount(),
        ))
    }
    
    return worlds
}