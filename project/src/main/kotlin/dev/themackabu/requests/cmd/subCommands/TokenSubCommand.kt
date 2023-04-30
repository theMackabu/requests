package dev.themackabu.requests.cmd.subCommands

import java.util.Base64
import com.google.gson.Gson
import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import dev.themackabu.requests.Main
import dev.themackabu.requests.db.UserInterface
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.ChatColor

//import net.md_5.bungee.api.chat.hover.content.Text
//import net.md_5.bungee.api.chat.TextComponent
//import net.md_5.bungee.api.chat.HoverEvent
//import net.md_5.bungee.api.chat.ClickEvent
//import net.md_5.bungee.api.chat.ComponentBuilder

class TokenSubCommand: SubCommandsInterface {
    override val name: String = "token"
    override val description: String = "Creates a new token to use the api"
    override val usage: String = "/api token new"
    override val minArguments: Int = 1
    override val executableByConsole: Boolean = true
    override val neededPermission: String = "requests.token.new"

    override fun run(sender: CommandSender, args: Array<out String>) {
        if (args[1] == "new") {
            if (sender is Player) {
                var gson = Gson()
                val player: Player = sender

                val token = object: UserInterface {
                    override var name = player.getName()
                    override var uuid = player.getUniqueId().toString()
                    override var token = NanoIdUtils.randomNanoId()
                }

                val encodedToken = Base64.getEncoder().encodeToString(gson.toJson(token).toByteArray())

                val message = Main.messagesManager.getMessage(
                    "commands", "generate-token",
                    hashMapOf("%token%" to encodedToken),
                    true
                )

                println(player.getName().toByteArray())

                Main.messagesManager.sendMessage(sender, message)
            } else {
                val placeholders: HashMap<String, String> = hashMapOf("%token%" to "console_token")

                Main.messagesManager.sendMessage(sender, Main.messagesManager.getMessage("commands", "console-generate-token", placeholders, true))
            }
        } else {
            var placeholders: HashMap<String, String> = hashMapOf("%argument%" to args[1], "%usage%" to usage)

            Main.messagesManager.sendMessage(sender, Main.messagesManager.getMessage("commands", "invalid-arguments", placeholders, true))
            Main.messagesManager.sendMessage(sender, Main.messagesManager.getMessage("commands", "command-usage", placeholders, true))
        }
    }

    override fun getTabCompletions(sender: CommandSender, args: Array<out String>): MutableList<String> = mutableListOf("new")
}
