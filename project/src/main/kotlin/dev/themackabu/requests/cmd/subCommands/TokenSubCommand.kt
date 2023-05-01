package dev.themackabu.requests.cmd.subCommands

import dev.themackabu.requests.Main
import dev.themackabu.requests.db.UserInterface
import dev.themackabu.requests.db.Database

import java.util.Scanner
import java.util.Base64
import java.security.SecureRandom;
import com.google.gson.Gson
import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.Bukkit
import java.io.File
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt

fun generateToken(player: Player): UserInterface {
    return object: UserInterface {
        override var name = player.getName()
        override var uuid = player.getUniqueId().toString()
        override var token = NanoIdUtils.randomNanoId()
    }
}

class TokenSubCommand: SubCommandsInterface {
    override val name: String = "token"
    override val description: String = "Creates a new token to use the api"
    override val usage: String = "/api token new"
    override val minArguments: Int = 1
    override val executableByConsole: Boolean = true
    override val neededPermission: String = "requests.token.new"

    override fun run(sender: CommandSender, args: Array<out String>) {
        if (args[1] == "new") {
            val db = Main.db
            when (sender) {
                is Player -> {
                    var gson = Gson()
                    val player: Player = sender
                    val token = generateToken(player)
                    val encodedToken = Base64.getEncoder().encodeToString(gson.toJson(token).toByteArray())

                    val message = Main.messagesManager.getMessage(
                        "commands", "generate-token",
                        hashMapOf("%token%" to encodedToken),
                        addPrefix = false
                    )

                    val prompt = object: Prompt {
                        override fun blocksForInput(context: ConversationContext): Boolean { return true }
                        override fun getPromptText(context: ConversationContext): String {
                            return Main.messagesManager.toLegacyString(Main.messagesManager.getMessage("commands", "action-destructive", hashMapOf("%action%" to "generate a new token"), addPrefix = false))
                        }
                        override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
                            if (input.equals("y", ignoreCase = true) || input.equals("yes", ignoreCase = true)) {
                                Main.messagesManager.sendMessage(sender, message)
                                db.apply { set(player.getUniqueId().toString(), encodedToken) }
                            } else { Main.messagesManager.send(sender, "<grey>Aborting...") }
                            return null
                        }
                    }

                    db.apply {
                        when {
                            contains(token.uuid) -> {
                                Main.conversation.withFirstPrompt(prompt)
                                    .withLocalEcho(false)
                                    .withTimeout(30)
                                    .withEscapeSequence("cancel")
                                    .buildConversation(player)
                                    .begin()
                            }
                        else -> {
                            Main.messagesManager.sendMessage(sender, message)
                            db.apply { set(player.getUniqueId().toString(), encodedToken) }
                        }}
                    }
                }
                else -> {
                    val consoleSender = Bukkit.getConsoleSender()
                    val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
                    val masterToken = NanoIdUtils.randomNanoId(SecureRandom(), chars, 35)
                    val placeholders: HashMap<String, String> = hashMapOf("%token%" to masterToken)
                    val message = Main.messagesManager.getMessage("commands", "console-generate-token", placeholders, addPrefix = false)

                    val prompt = object: Prompt {
                        override fun getPromptText(context: ConversationContext): String { return "\n" + Main.messagesManager.toLegacyString(Main.messagesManager.getMessage("commands", "action-destructive", hashMapOf("%action%" to "generate a new master token"), addPrefix = false)) }
                        override fun blocksForInput(context: ConversationContext): Boolean { return true }
                        override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
                            if (input.equals("y", ignoreCase = true) || input.equals("yes", ignoreCase = true)) {
                                Main.messagesManager.sendMessage(sender, message)
                                db.apply { set("token.master", masterToken) }
                            } else { Main.messagesManager.send(sender, "<grey>Aborting...") }
                            return null
                        }
                    }

                    db.apply {
                        when {
                            contains("token.master") -> {
                                Main.conversation.withFirstPrompt(prompt)
                                    .withLocalEcho(false)
                                    .withTimeout(30)
                                    .withEscapeSequence("cancel")
                                    .buildConversation(consoleSender)
                                    .begin()
                            }
                            else -> {
                                Main.messagesManager.sendMessage(sender, message)
                                db.apply { set("token.master", masterToken) }
                            }}
                    }
                }
            }
        } else {
            var placeholders: HashMap<String, String> = hashMapOf("%argument%" to args[1], "%usage%" to usage)

            Main.messagesManager.sendMessage(sender, Main.messagesManager.getMessage("commands", "invalid-arguments", placeholders, addPrefix = true))
            Main.messagesManager.sendMessage(sender, Main.messagesManager.getMessage("commands", "command-usage", placeholders, addPrefix = true))
        }
    }

    override fun getTabCompletions(sender: CommandSender, args: Array<out String>): MutableList<String> = mutableListOf("new")
}
