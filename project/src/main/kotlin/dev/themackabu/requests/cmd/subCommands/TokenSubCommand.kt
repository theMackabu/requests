package dev.themackabu.requests.cmd.subCommands

import java.util.Base64
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.security.SecureRandom
import dev.themackabu.requests.mainDB
import kotlinx.serialization.json.Json
import org.bukkit.conversations.Prompt
import dev.themackabu.requests.messages
import org.bukkit.command.CommandSender
import dev.themackabu.requests.conversation
import kotlinx.serialization.encodeToString
import com.aventrix.jnanoid.jnanoid.NanoIdUtils
import dev.themackabu.requests.models.cmd.UserToken
import org.bukkit.conversations.ConversationContext
import dev.themackabu.requests.models.cmd.SubCommand
import dev.themackabu.requests.models.cmd.TokenStorage

fun generateToken(player: Player): UserToken {
    return UserToken(
        name = player.name,
        uuid = player.uniqueId.toString(),
        token = NanoIdUtils.randomNanoId()
    )
}

class TokenSubCommand: SubCommand(
    name = "token",
    description = "Creates a new token to use the api",
    usage = "/api token new",
    minArguments = 1,
    executableByConsole = true,
    neededPermission = "requests.token.new"
) {
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (args[1] == "new") {
            when (sender) {
                is Player -> {
                    val player: Player = sender
                    val token = generateToken(player)
                    val tokenStorage = TokenStorage(
                        token = Base64.getEncoder().encodeToString(Json.encodeToString(token).toByteArray()),
                        uses = 0
                    )

                    val message = messages.getMessage(
                        "commands", "generate-token",
                        hashMapOf("%token%" to tokenStorage.token),
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
                                mainDB.apply { set("token.${player.uniqueId}", Json.encodeToString(tokenStorage)) }
                            } else { messages.send(sender, "<grey>Aborting...") }
                            return null
                        }
                    }

                    mainDB.apply {
                        when {
                            contains("token.${player.uniqueId}") -> {
                                conversation.withFirstPrompt(prompt)
                                    .withLocalEcho(false)
                                    .withTimeout(30)
                                    .withEscapeSequence("cancel")
                                    .buildConversation(player)
                                    .begin()
                            }
                        else -> {
                            messages.sendMessage(sender, message)
                            mainDB.apply { set("token.${player.uniqueId}", Json.encodeToString(tokenStorage)) }
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
