package dev.themackabu.requests.cmd.subCommands

import dev.themackabu.requests.Main
import dev.themackabu.requests.db.UserInterface

import java.util.Scanner
import java.util.Base64
import java.security.SecureRandom;
import com.google.gson.Gson
import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.Bukkit
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt

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

                val prompt = object: Prompt {
                    override fun getPromptText(context: ConversationContext): String { return Main.messagesManager.toLegacyString(Main.messagesManager.getMessage("commands", "action-destructive", hashMapOf("%action%" to "generate a new token"), addPrefix = true)) }
                    override fun blocksForInput(context: ConversationContext): Boolean { return true }
                    override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
                        if (input.equals("y", ignoreCase = true)) {
                            val encodedToken = Base64.getEncoder().encodeToString(gson.toJson(token).toByteArray())
                            val message = Main.messagesManager.getMessage(
                                "commands", "generate-token",
                                hashMapOf("%token%" to encodedToken),
                                addPrefix = false
                            )

                            Main.messagesManager.sendMessage(sender, message)

                        } else { Main.messagesManager.send(sender, "\n<grey>Aborting...") }

                        return null
                    }
                }

                Main.conversation.withFirstPrompt(prompt)
                    .withLocalEcho(false)
                    .withTimeout(30)
                    .withEscapeSequence("cancel")
                    .buildConversation(player)
                    .begin()
            } else {
                val consoleSender = Bukkit.getConsoleSender()
                val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
                val placeholders: HashMap<String, String> = hashMapOf("%token%" to NanoIdUtils.randomNanoId(SecureRandom(), chars, 35))

                val prompt = object: Prompt {
                    override fun getPromptText(context: ConversationContext): String { return Main.messagesManager.toLegacyString(Main.messagesManager.getMessage("commands", "action-destructive", hashMapOf("%action%" to "generate a new master token"), addPrefix = false)) }
                    override fun blocksForInput(context: ConversationContext): Boolean { return true }
                    override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
                        if (input.equals("y", ignoreCase = true)) {
                            Main.messagesManager.sendMessage(sender, Main.messagesManager.getMessage("commands", "console-generate-token", placeholders, addPrefix = false))
                        } else { Main.messagesManager.send(sender, "<grey>Aborting...") }

                        return null
                    }
                }

                Main.conversation.withFirstPrompt(prompt)
                    .withLocalEcho(false)
                    .withTimeout(30)
                    .withEscapeSequence("cancel")
                    .buildConversation(consoleSender)
                    .begin()
            }
        } else {
            var placeholders: HashMap<String, String> = hashMapOf("%argument%" to args[1], "%usage%" to usage)

            Main.messagesManager.sendMessage(sender, Main.messagesManager.getMessage("commands", "invalid-arguments", placeholders, addPrefix = true))
            Main.messagesManager.sendMessage(sender, Main.messagesManager.getMessage("commands", "command-usage", placeholders, addPrefix = true))
        }
    }

    override fun getTabCompletions(sender: CommandSender, args: Array<out String>): MutableList<String> = mutableListOf("new")
}
