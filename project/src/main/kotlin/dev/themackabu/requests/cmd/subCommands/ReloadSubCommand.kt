package dev.themackabu.requests.cmd.subCommands

import dev.themackabu.requests.Main
import org.bukkit.command.CommandSender

class ReloadSubCommand: SubCommandsInterface {
    override val name: String = "reload"
    override val description: String = "Reloads the plugin's configuration files."
    override val usage: String = "/api reload"
    override val minArguments: Int = 0
    override val executableByConsole: Boolean = true
    override val neededPermission: String = "requests.reload"

    override fun run(sender: CommandSender, args: Array<out String>) {
        Main.reloadConfigs()
        Main.messagesManager.sendMessage(sender, Main.messagesManager.getMessage("commands", "reload-successful", null, true))
    }
}
