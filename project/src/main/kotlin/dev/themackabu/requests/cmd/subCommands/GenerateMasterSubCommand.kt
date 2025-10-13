package dev.themackabu.requests.cmd.subCommands

import org.bukkit.Bukkit
import dev.themackabu.requests.mainDB
import org.bukkit.conversations.Prompt
import dev.themackabu.requests.messages
import org.bukkit.command.CommandSender
import dev.themackabu.requests.conversation
import dev.themackabu.requests.helpers.*
import org.bukkit.conversations.ConversationContext
import dev.themackabu.requests.models.cmd.SubCommand
import org.bukkit.command.ConsoleCommandSender

class GenerateMasterSubCommand: SubCommand(
    name = "generate-master",
    description = "Generates a new master token (console only)",
    usage = "/api generate-master",
    minArguments = 0,
    executableByConsole = true,
    neededPermission = null
) {
    override fun run(sender: CommandSender, args: Array<out String>) {
        if (sender !is ConsoleCommandSender) {
            messages.sendMessage(sender, messages.getMessage("commands", "console-command-only", null, addPrefix = true))
            return
        }

        val consoleSender = Bukkit.getConsoleSender()
        val masterToken = nanoid(50, "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ")
        val placeholders: HashMap<String, String> = hashMapOf("%token%" to masterToken)
        val message = messages.getMessage("commands", "console-generate-token", placeholders, addPrefix = false)

        val prompt = object: Prompt {
            override fun getPromptText(context: ConversationContext): String { return "\n" + messages.toLegacyString(messages.getMessage("commands", "action-destructive", hashMapOf("%action%" to "generate a new master token"), addPrefix = false)) }
            override fun blocksForInput(context: ConversationContext): Boolean { return true }
            override fun acceptInput(context: ConversationContext, input: String?): Prompt? {
                if (input.equals("y", ignoreCase = true) || input.equals("yes", ignoreCase = true)) {
                    messages.sendMessage(sender, message)
                    mainDB["token.master"] = masterToken
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
                    set("token.master", masterToken)
                }}
        }
    }

    override fun getTabCompletions(sender: CommandSender, args: Array<out String>): MutableList<String> = mutableListOf()
}
