package dev.themackabu.requests.api.routes

import dev.themackabu.requests.server
import dev.themackabu.requests.messages
import dev.themackabu.requests.plugin
import dev.themackabu.requests.models.api.Server
import dev.themackabu.requests.models.api.Players
import dev.themackabu.requests.models.api.PlayerCount
import dev.themackabu.requests.models.api.Health
import dev.themackabu.requests.models.api.Tps
import dev.themackabu.requests.models.api.Memory
import dev.themackabu.requests.models.api.Dimension

import java.io.File
import java.util.Base64
import javax.imageio.ImageIO
import java.lang.management.ManagementFactory;

fun serverInfo(): Server {
    return Server(
      name = server.getName(),
      motd = messages.toLegacyString(server.motd()),
      onlineMode = server.getOnlineMode(),
      version = server.getVersion(),
      bukkitVersion = server.getBukkitVersion(),
      viewDistance = server.getViewDistance(),
      players = Players(
          allowFlight = server.getAllowFlight(),
          isWhitelist = server.isWhitelistEnforced(),
          defautGamemode = server.getDefaultGameMode().toString(),
          resourcePack = server.isResourcePackRequired(),
          isSecureProfile = server.isEnforcingSecureProfiles(),
          playerCount = PlayerCount(
              online = server.getOnlinePlayers().size,
              max = server.getMaxPlayers()
          )
      ),
      health = Health(
          cpuCount = Runtime.getRuntime().availableProcessors(),
          uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000L,
          warningState = server.getWarningState().toString(),
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
          defaultType = server.getWorldType(),
          allowNether = server.getAllowNether(),
          allowEnd = server.getAllowEnd()
      ),
    )
}

fun serverIcon(): File {
    val iconFile = File("server-icon.png")
    
    if (iconFile.exists()) { return iconFile } else {
        val defaultIcon = File.createTempFile("default-icon", ".png")
        val inputStream = plugin.getResource("default-icon.png")
        val image = ImageIO.read(inputStream)
        
        ImageIO.write(image, "png", defaultIcon)
        return defaultIcon
    }
}