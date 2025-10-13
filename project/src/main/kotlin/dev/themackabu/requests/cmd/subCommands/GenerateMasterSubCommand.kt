package dev.themackabu.requests.cmd.subCommands

import dev.themackabu.requests.mainDB
import dev.themackabu.requests.messages
import org.bukkit.command.CommandSender
import dev.themackabu.requests.helpers.*
import dev.themackabu.requests.models.cmd.SubCommand
import org.bukkit.command.ConsoleCommandSender

class GenerateMasterSubCommand: SubCommand(
    name = "generate-master",
    description = "Generates a new master token (console only)",
    usage = "/api generate-master [force]",
    minArguments = 0,
    executableByConsole = true,
    neededPermission = null
) {
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (sender !is ConsoleCommandSender) {
            messages.sendMessage(sender, messages.getMessage("commands", "console-command-only", null, addPrefix = true))
            return
        }

        val forceFlag = args.size > 1 && args[1] == "force"
        val masterToken = nanoid(50, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
        val placeholders: HashMap<String, String> = hashMapOf("%token%" to masterToken)
        val message = messages.getMessage("commands", "console-generate-token", placeholders, addPrefix = false)

        mainDB.apply {
            when {
                contains("token.master") && !forceFlag -> {
                    messages.send(sender, "<red>A master token already exists. Use force to overwrite it.")
                }
                else -> {
                    messages.sendMessage(sender, message)
                    set("token.master", masterToken)
                }
            }
        }
    }

    override fun getTabCompletions(sender: CommandSender, args: Array<out String>): MutableList<String> = mutableListOf("force")
}
