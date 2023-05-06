package dev.themackabu.requests.api.routes

import java.io.File
import javax.imageio.ImageIO
import dev.themackabu.requests.server
import dev.themackabu.requests.plugin
import dev.themackabu.requests.messages
import dev.themackabu.requests.models.api.Tps
import java.lang.management.ManagementFactory
import dev.themackabu.requests.models.api.Server
import dev.themackabu.requests.models.api.Health
import dev.themackabu.requests.models.api.Memory
import dev.themackabu.requests.models.api.Players
import dev.themackabu.requests.models.api.Dimension
import dev.themackabu.requests.models.api.PlayerCount

fun serverInfo(): Server {
    return Server(
      name = server.name,
      motd = messages.toLegacyString(server.motd()),
      onlineMode = server.onlineMode,
      version = server.version,
      bukkitVersion = server.bukkitVersion,
      viewDistance = server.viewDistance,
      players = Players(
          allowFlight = server.allowFlight,
          isWhitelist = server.isWhitelistEnforced,
          defautGamemode = server.defaultGameMode.toString(),
          resourcePack = server.isResourcePackRequired,
          isSecureProfile = server.isEnforcingSecureProfiles,
          playerCount = PlayerCount(
              online = server.onlinePlayers.size,
              max = server.maxPlayers
          )
      ),
      health = Health(
          cpuCount = Runtime.getRuntime().availableProcessors(),
          uptime = ManagementFactory.getRuntimeMXBean().uptime / 1000L,
          warningState = server.warningState.toString(),
          tps = Tps(
              oneMinute = server.tps[0],
              fiveMinutes = server.tps[1],
              fifteenMinutes = server.tps[2]
          ),
          memory = Memory(
              totalMemory = Runtime.getRuntime().maxMemory(),
              maxMemory = Runtime.getRuntime().totalMemory(),
              freeMemory = Runtime.getRuntime().freeMemory()
          )
      ),
      dimension = Dimension(
          defaultType = server.worldType,
          allowNether = server.allowNether,
          allowEnd = server.allowEnd
      ),
    )
}

fun serverIcon(): File {
    val iconFile = File("server-icon.png")
    
    if (!iconFile.exists()) {
        val defaultIcon = File.createTempFile("default-icon", ".png")
        val inputStream = plugin.getResource("default-icon.png")
        val image = ImageIO.read(inputStream)
        
        ImageIO.write(image, "png", defaultIcon)
        return defaultIcon
    }
    
    return iconFile
}