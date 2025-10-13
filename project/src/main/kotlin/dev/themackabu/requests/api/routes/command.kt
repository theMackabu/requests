package dev.themackabu.requests.api.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import dev.themackabu.requests.config
import dev.themackabu.requests.server
import dev.themackabu.requests.helpers.fromJson
import io.ktor.server.application.ApplicationCall
import dev.themackabu.requests.models.api.ApiException
import dev.themackabu.requests.models.cmd.ResponseContext
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.craftbukkit.CraftServer
import org.bukkit.craftbukkit.command.ProxiedNativeCommandSender
import net.minecraft.commands.CommandSource
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component as MinecraftComponent

class InterceptingCommandSource(
    private val original: CommandSource,
    private val messages: MutableList<String>
): CommandSource {
    override fun sendSystemMessage(component: MinecraftComponent) {
        messages.add(component.string)
        original.sendSystemMessage(component)
    }
    
    override fun acceptsSuccess(): Boolean = original.acceptsSuccess()
    
    override fun acceptsFailure(): Boolean = original.acceptsFailure()
    
    override fun shouldInformAdmins(): Boolean = original.shouldInformAdmins()
    
    override fun getBukkitSender(commandSourceStack: CommandSourceStack): CommandSender {
        return original.getBukkitSender(commandSourceStack)
    }
}

class NMSCommandResult private constructor(
    sourceStack: CommandSourceStack,
    caller: CommandSender,
    callee: CommandSender,
    private val capturedMessages: MutableList<String>
): ProxiedNativeCommandSender(sourceStack, caller, callee) {
    fun getOutput(): String = capturedMessages.joinToString("\n")
    
    companion object {
        fun create(): NMSCommandResult {
            val craftServer = Bukkit.getServer() as CraftServer
            val nmsServer = craftServer.server
            val originalStack = nmsServer.createCommandSourceStack()
            val console = Bukkit.getConsoleSender()
            
            val messages = mutableListOf<String>()
            val interceptingSource = InterceptingCommandSource(originalStack.source, messages)
            val newStack = originalStack.withSource(interceptingSource)
            
            return NMSCommandResult(newStack, console, console, messages)
        }
    }
}

suspend fun executeCommand(call: ApplicationCall) {
    val info = call.principal<UserIdPrincipal>()!!.name
    val context = info.fromJson<ResponseContext>()
    
    if (context.uuid != null) {
        throw ApiException(
            message = "Only master token can execute commands",
            error = "command.forbidden"
        )
    }
    
    if (!config.api.danger.remote_commands) {
        throw ApiException(
            message = "Remote commands are disabled. Enable [api.danger] remote_commands = true in config.toml",
            error = "command.disabled"
        )
    }
    
    val command = call.receiveText().trim()
    
    if (command.isBlank()) {
        throw ApiException(
            message = "Command cannot be empty",
            error = "command.empty"
        )
    }
    
    try {
        val commandResult = NMSCommandResult.create()
    
        val future = server.scheduler.callSyncMethod(dev.themackabu.requests.plugin) {
            server.dispatchCommand(commandResult, command)
        }
        
        val executed = future.get()
        val output = commandResult.getOutput().trim()
        
        call.respondText(
            text = output,
            contentType = ContentType.Text.Plain,
            status = if (executed) HttpStatusCode.OK else HttpStatusCode.BadRequest
        )
    } catch (e: Exception) {
        throw ApiException(
            message = "Failed to execute command: ${e.message}",
            error = "command.execution_failed"
        )
    }
}