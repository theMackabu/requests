package dev.themackabu.requests.cmd.subCommands

import dev.themackabu.requests.mainDB
import dev.themackabu.requests.messages
import dev.themackabu.requests.conversation
import dev.themackabu.requests.models.UserInterface
import dev.themackabu.requests.models.SubCommandsInterface

import java.util.Base64
import org.bukkit.Bukkit
import kotlinx.serialization.*
import org.bukkit.entity.Player
import java.security.SecureRandom
import kotlinx.serialization.json.*
import org.bukkit.conversations.Prompt
import org.bukkit.command.CommandSender
import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import org.bukkit.conversations.ConversationContext

fun generateToken(player: Player): UserInterface {
    return UserInterface(
        name = player.name,
        uuid = player.uniqueId.toString(),
        token = NanoIdUtils.randomNanoId()
    )
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
            when (sender) {
                is Player -> {
                    val player: Player = sender
                    val token = generateToken(player)
                    val encodedToken = Base64.getEncoder().encodeToString(Json.encodeToString(token).toByteArray())

                    val message = messages.getMessage(
                        "commands", "generate-token",
                        hashMapOf("%token%" to encodedToken),
                        addPrefix = false
                    )

                    val prompt = object: Prompt {
                        override fun blocksForInput(context: ConversationContext): Boolean { return true }
                        override fun getPromptText(context: ConversationContext): String {
                            return messages.toLegacyString(messages.getMessage("commands", "action-destructive", hashMapOf("%action%" to "generate a new token"), addPrefix = false))
                        }
                        override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
                            if (input.equals("y", ignoreCase = true) || input.equals("yes", ignoreCase = true)) {
                                messages.sendMessage(sender, message)
                                mainDB.apply { set(player.getUniqueId().toString(), encodedToken) }
                            } else { messages.send(sender, "<grey>Aborting...") }
                            return null
                        }
                    }

                    mainDB.apply {
                        when {
                            contains(token.uuid) -> {
                                conversation.withFirstPrompt(prompt)
                                    .withLocalEcho(false)
                                    .withTimeout(30)
                                    .withEscapeSequence("cancel")
                                    .buildConversation(player)
                                    .begin()
                            }
                        else -> {
                            messages.sendMessage(sender, message)
                            mainDB.apply { set(player.getUniqueId().toString(), encodedToken) }
                        }}
                    }
                }
                else -> {
                    val consoleSender = Bukkit.getConsoleSender()
                    val chars = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray()
                    val masterToken = NanoIdUtils.randomNanoId(SecureRandom(), chars, 35)
                    val placeholders: HashMap<String, String> = hashMapOf("%token%" to masterToken)
                    val message = messages.getMessage("commands", "console-generate-token", placeholders, addPrefix = false)

                    val prompt = object: Prompt {
                        override fun getPromptText(context: ConversationContext): String { return "\n" + messages.toLegacyString(messages.getMessage("commands", "action-destructive", hashMapOf("%action%" to "generate a new master token"), addPrefix = false)) }
                        override fun blocksForInput(context: ConversationContext): Boolean { return true }
                        override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
                            if (input.equals("y", ignoreCase = true) || input.equals("yes", ignoreCase = true)) {
                                messages.sendMessage(sender, message)
                                mainDB.apply { set("token.master", masterToken) }
                            } else { messages.send(sender, "<grey>Aborting...") }
                            return null
                        }
                    }

                    mainDB.apply {
                        when {
                            contains("token.master") -> {
                                conversation.withFirstPrompt(prompt)
                                    .withLocalEcho(false)
                                    .withTimeout(30)
                                    .withEscapeSequence("cancel")
                                    .buildConversation(consoleSender)
                                    .begin()
                            }
                            else -> {
                                messages.sendMessage(sender, message)
                                mainDB.apply { set("token.master", masterToken) }
                            }}
                    }
                }
            }
        } else {
            val placeholders: HashMap<String, String> = hashMapOf("%argument%" to args[1], "%usage%" to usage)

            messages.sendMessage(sender, messages.getMessage("commands", "invalid-arguments", placeholders, addPrefix = true))
            messages.sendMessage(sender, messages.getMessage("commands", "command-usage", placeholders, addPrefix = true))
        }
    }

    override fun getTabCompletions(sender: CommandSender, args: Array<out String>): MutableList<String> = mutableListOf("new")
}
