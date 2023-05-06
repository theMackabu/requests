package dev.themackabu.requests.helpers.player

import java.util.UUID
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import dev.themackabu.requests.plugin
import org.bukkit.attribute.Attribute
import kotlinx.serialization.json.Json
import org.bukkit.scheduler.BukkitTask
import dev.themackabu.requests.playerDB
import kotlinx.serialization.encodeToString
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerExpChangeEvent
import dev.themackabu.requests.models.api.PlayerInfo
import org.bukkit.event.player.PlayerLevelChangeEvent
import dev.themackabu.requests.models.api.PlayerStatus
import dev.themackabu.requests.models.api.PlayerHealth

private val debouncePlayer = mutableMapOf<UUID, BukkitTask?>()

fun updatePlayer(player: Player) {
    val uuid = player.uniqueId.toString()
    val maxHealth = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value

    val json = Json.encodeToString(PlayerInfo(
        uuid = uuid,
        username = player.name,
        level = getExp(player),
        allowFlight = player.allowFlight,
        health = PlayerHealth(
            max = maxHealth,
            current = player.health,
            percent = player.health / maxHealth as Double,
            absorption = player.absorptionAmount
        ),
        status = PlayerStatus(
            online = player.isOnline
        )
    ))

    playerDB.apply { set("players.$uuid", json) }
}

fun debouncePlayerEvent(player: Player) {
    val delayTicks = 20L
    val uuid = player.uniqueId
    val runnable = Runnable { updatePlayer(player) }

    debouncePlayer[uuid]?.cancel()
    debouncePlayer[uuid] = Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks)
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
