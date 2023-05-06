package dev.themackabu.requests.cmd.subCommands

import org.bukkit.command.CommandSender
import dev.themackabu.requests.subCommands
import dev.themackabu.requests.models.cmd.SubCommand

class HelpSubCommand: SubCommand(
    name = "help",
    description = "Information about the commands the plugin has.",
    usage = "/api help",
    minArguments = 0,
    executableByConsole = true,
    neededPermission = null
) {
    override fun run(sender: CommandSender, args: Array<out String>) {
        for (value in subCommands.values) {
            if (value.neededPermission == null || sender.hasPermission(value.neededPermission!!) || sender.hasPermission("requests.admin")) {
                sender.sendMessage(value.usage + " - " + value.description)
            }
        }
    }
}
