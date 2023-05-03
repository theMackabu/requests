package dev.themackabu.requests.api.routes

import dev.themackabu.requests.Main
import dev.themackabu.requests.models.api.World

import org.bukkit.Bukkit;
import kotlinx.serialization.*
import kotlinx.serialization.json.*

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