package dev.themackabu.requests.cmd.subCommands

import dev.themackabu.requests.plugin
import dev.themackabu.requests.messages
import dev.themackabu.requests.loadPlugin
import dev.themackabu.requests.apiServer
import org.bukkit.command.CommandSender
import dev.themackabu.requests.models.SubCommandsInterface

class ReloadSubCommand: SubCommandsInterface {
    override val name: String = "reload"
    override val description: String = "Reloads the plugin's configuration files."
    override val usage: String = "/api reload"
    override val minArguments: Int = 0
    override val executableByConsole: Boolean = true
    override val neededPermission: String = "requests.reload"

    override fun run(sender: CommandSender, args: Array<out String>) {
        apiServer.stop(0, 0)
        loadPlugin(plugin)

        messages.sendMessage(sender, messages.getMessage("commands", "reload-successful", null, true))
    }
}
