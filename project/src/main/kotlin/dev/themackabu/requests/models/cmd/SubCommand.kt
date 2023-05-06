package dev.themackabu.requests.models.cmd
import org.bukkit.command.CommandSender

abstract class SubCommand(
    var name: String,
    var description: String,
    var usage: String,
    var minArguments: Int,
    var executableByConsole: Boolean,
    var neededPermission: String?
) {
    open fun getTabCompletions(sender: CommandSender, args: Array<out String>): MutableList<String> = ArrayList()
    abstract fun run(sender: CommandSender, args: Array<out String>)
}
