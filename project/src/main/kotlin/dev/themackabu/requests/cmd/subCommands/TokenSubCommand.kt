package dev.themackabu.requests.cmd.subCommands

import java.util.UUID
import org.bukkit.entity.Player
import dev.themackabu.requests.mainDB
import org.bukkit.conversations.Prompt
import dev.themackabu.requests.messages
import org.bukkit.command.CommandSender
import dev.themackabu.requests.conversation
import dev.themackabu.requests.helpers.*
import dev.themackabu.requests.models.cmd.UserToken
import org.bukkit.conversations.ConversationContext
import dev.themackabu.requests.models.cmd.SubCommand
import dev.themackabu.requests.models.cmd.TokenStorage

fun generateToken(player: Player): UserToken {
    return UserToken(
        name = player.name,
        uuid = player.uniqueId.toString(),
        token = nanoid()
    )
}

class TokenSubCommand: SubCommand(
    name = "token",
    description = "Creates a new token to use the api",
    usage = "/api token new",
    minArguments = 1,
    executableByConsole = false,
    neededPermission = "requests.token.new"
) {
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (args[1] == "new") {
            val player: Player = sender as Player
            val tokenStorage = TokenStorage(uses = 0).toJson()
            val token = generateToken(player).toJson().toBase64()

            val message = messages.getMessage(
                "commands", "generate-token",
                hashMapOf("%token%" to token),
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
                        mainDB["token.${token}"] = tokenStorage
                    } else { messages.send(sender, "<grey>Aborting...") }
                    return null
                }
            }

            mainDB.keys.forEach { key ->
                val split = key.split(".")[1].fromBase64()
                val uniqueId = if (split != "") UUID.fromString(split.fromJson<UserToken>().uuid) else { null }

                if (uniqueId == player.uniqueId) {
                    mainDB.remove(key)
                    conversation.withFirstPrompt(prompt)
                        .withLocalEcho(false)
                        .buildConversation(player)
                        .begin()

                    return
                } else {
                    mainDB.remove(key)
                    messages.sendMessage(sender, message)
                    mainDB["token.${token}"] = tokenStorage

                    return
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
