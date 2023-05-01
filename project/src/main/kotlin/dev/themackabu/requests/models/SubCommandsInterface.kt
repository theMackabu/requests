package dev.themackabu.requests.models

import org.bukkit.command.CommandSender

interface SubCommandsInterface {
    val name: String
    val description: String
    val usage: String
    val minArguments: Int
    val executableByConsole: Boolean
    val neededPermission: String?
    fun getTabCompletions(sender: CommandSender, args: Array<out String>): MutableList<String> = ArrayList()
    fun run(sender: CommandSender, args: Array<out String>)
}
