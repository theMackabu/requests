package dev.themackabu.requests.helpers.player

import dev.themackabu.requests.playerDB
import dev.themackabu.requests.plugin
import dev.themackabu.requests.helpers.player.getExp
import dev.themackabu.requests.models.api.PlayerInfo
import dev.themackabu.requests.models.api.PlayerStatus
import dev.themackabu.requests.models.api.PlayerHealth

import java.util.UUID
import kotlinx.serialization.*
import kotlinx.serialization.json.*

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.attribute.Attribute
import org.bukkit.scheduler.BukkitTask
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerLevelChangeEvent

private val moveDebounce = mutableMapOf<UUID, BukkitTask?>()
private val interactDebounce = mutableMapOf<UUID, BukkitTask?>()

fun updatePlayer(player: Player) {
    val uuid = player.getUniqueId().toString()
    val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.getValue()

    val json = Json.encodeToString(PlayerInfo(
        uuid = uuid,
        username = player.getName(),
        level = getExp(player),
        allowFlight = player.getAllowFlight(),
        health = PlayerHealth(
            max = maxHealth,
            current = player.getHealth(),
            percent = player.getHealth() / maxHealth as Double,
            absorption = player.getAbsorptionAmount()
        ),
        status = PlayerStatus(
            online = player.isOnline()
        )
    ))

    println("[DEBUG] event.update.player")
    playerDB.apply { set("players.$uuid", json) }
}

fun debouncePlayerEvent(player: Player) {
    val delayTicks = 20L
    val uuid = player.getUniqueId()
    val runnable = Runnable { updatePlayer(player) }

    moveDebounce[uuid]?.cancel()
    moveDebounce[uuid] = Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks)
}

class dataListener: Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {debouncePlayerEvent(event.player)}
    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) { debouncePlayerEvent(event.player) }
    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) { debouncePlayerEvent(event.player) }
    @EventHandler
    fun onPlayerExpChange(event: PlayerExpChangeEvent) { debouncePlayerEvent(event.player) }
    @EventHandler
    fun onPlayerLevelChange(event: PlayerLevelChangeEvent) { debouncePlayerEvent(event.player) }
    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) { debouncePlayerEvent(event.player) }
}
