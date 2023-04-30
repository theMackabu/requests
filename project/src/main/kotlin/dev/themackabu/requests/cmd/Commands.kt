package dev.themackabu.requests.cmd

import dev.themackabu.requests.Main
import dev.themackabu.requests.config.MessagesManager
import dev.themackabu.requests.cmd.subCommands.HelpSubCommand
import dev.themackabu.requests.cmd.subCommands.ReloadSubCommand
import dev.themackabu.requests.cmd.subCommands.SubCommandsInterface
import org.bukkit.command.*

class Commands: CommandExecutor, TabCompleter {
    companion object {
        val subCommands: HashMap<String, SubCommandsInterface> = hashMapOf(
            "help" to HelpSubCommand(),
            "reload" to ReloadSubCommand()
        )
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val messagesManager: MessagesManager = Main.messagesManager

        if (args.isEmpty()) {
            sender.sendMessage(messagesManager.getMessage("commands", "not-enough-arguments", null, true))
            sender.sendMessage(messagesManager.getMessage("commands", "use-help-command", null, true))
            return true
        }

        val placeholders = hashMapOf("%subcommand%" to args[0])

        if (!subCommands.containsKey(args[0])) {
            sender.sendMessage(messagesManager.getMessage("commands", "command-not-found", placeholders, true))
            sender.sendMessage(messagesManager.getMessage("commands", "use-help-command", placeholders, true))
            return true
        }

        val subCommand: SubCommandsInterface = subCommands[args[0]] as SubCommandsInterface
        placeholders["%description%"] = subCommand.description
        placeholders["%usage%"] = subCommand.usage
        placeholders["%minArguments%"] = subCommand.minArguments.toString()

        if (!sender.hasPermission("plugin.admin") && (subCommand.neededPermission != null && !sender.hasPermission(subCommand.neededPermission!!))) {
            sender.sendMessage(messagesManager.getMessage("commands", "no-permission", placeholders, true))
            return true
        }

        if (sender is ConsoleCommandSender && !subCommand.executableByConsole) {
            sender.sendMessage(messagesManager.getMessage("commands", "player-command-only", placeholders, true))
            return true
        }

        if (args.size - 1 < subCommand.minArguments) {
            sender.sendMessage(messagesManager.getMessage("commands", "not-enough-arguments", placeholders, true))
            sender.sendMessage(messagesManager.getMessage("commands", "command-usage", placeholders, true))
            return true
        }

        subCommand.run(sender, args)
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, label: String, args: Array<out String>): MutableList<String> {
        var completions: MutableList<String> = ArrayList()

        if (args.size == 1) {
            for ((key, value) in subCommands) {
                if (value.neededPermission == null) {
                    completions.add(key)
                } else if (sender.hasPermission(value.neededPermission!!) || sender.hasPermission("plugin.admin")) {
                    completions.add(key)
                }
            }
        }

        if (args.size >= 2 && subCommands.containsKey(args[0])) {
            completions = subCommands[args[0]]!!.getTabCompletions(sender, args)
        }

        return completions.filter { completion -> completion.startsWith(args[args.size - 1]) }.toMutableList()
    }
}
