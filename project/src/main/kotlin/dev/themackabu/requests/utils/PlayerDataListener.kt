package dev.themackabu.requests.utils

import dev.themackabu.requests.api.PlayerStatus
import dev.themackabu.requests.api.PlayerInterface
import dev.themackabu.requests.utils.getExpTotal
import dev.themackabu.requests.Main

import java.util.UUID
import com.google.gson.Gson
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.scheduler.BukkitTask
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerExpChangeEvent
import org.bukkit.event.player.PlayerLevelChangeEvent

val db = Main.players
var gson = Gson()
val plugin = Main.getPlugin()

private val moveDebounce = mutableMapOf<UUID, BukkitTask?>()
private val interactDebounce = mutableMapOf<UUID, BukkitTask?>()

fun updatePlayer(player: Player) {
    val uuid = player.getUniqueId().toString()
    val json = gson.toJson(object: PlayerInterface {
        override var uuid = uuid
        override var username = player.getName()
        override var level = getExpTotal(player)
        override var allowFlight = player.getAllowFlight()
        override var status = object: PlayerStatus {
            override var online = player.isOnline()
        }
    })

    println("[DEBUG] event.update.player")
    db.apply { set("players.$uuid", json) }
}

fun debouncePlayerEvent(player: Player) {
    val delayTicks = 20L
    val uuid = player.getUniqueId()
    val runnable = Runnable { updatePlayer(player) }

    moveDebounce[uuid]?.cancel()
    moveDebounce[uuid] = Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks)
}

class PlayerDataListener: Listener {
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
