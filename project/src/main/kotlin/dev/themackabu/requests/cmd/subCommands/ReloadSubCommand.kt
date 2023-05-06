package dev.themackabu.requests.cmd.subCommands

import dev.themackabu.requests.plugin
import dev.themackabu.requests.messages
import org.bukkit.command.CommandSender
import dev.themackabu.requests.apiServer
import dev.themackabu.requests.loadPlugin
import dev.themackabu.requests.models.cmd.SubCommand

class ReloadSubCommand: SubCommand(
    name = "reload",
    description = "Reloads the plugin's configuration files.",
    usage = "/api reload",
    minArguments = 0,
    executableByConsole  = true,
    neededPermission ="requests.reload"
) {
    override fun run(sender: CommandSender, args: Array<out String>) {
        apiServer.stop(0, 0)
        loadPlugin(plugin)

        messages.sendMessage(sender, messages.getMessage("commands", "reload-successful", null, true))
    }
}