package dev.themackabu.requests.api.routes

import io.ktor.server.auth.*
import io.ktor.server.request.*
import dev.themackabu.requests.config
import dev.themackabu.requests.server
import dev.themackabu.requests.helpers.fromJson
import io.ktor.server.application.ApplicationCall
import dev.themackabu.requests.models.api.ApiException
import dev.themackabu.requests.models.cmd.ResponseContext
import dev.themackabu.requests.models.api.AuthenticatedResponse
import kotlinx.serialization.Serializable

@Serializable
data class CommandResult(
    val executed: Boolean,
    val command: String
)

suspend fun executeCommand(call: ApplicationCall): AuthenticatedResponse<CommandResult> {
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
    
    val future = server.scheduler.callSyncMethod(dev.themackabu.requests.plugin) {
        server.dispatchCommand(server.consoleSender, command)
    }
    
    val executed = future.get()
    
    return AuthenticatedResponse(
        code = 200,
        response = info.fromJson<ResponseContext>(),
        data = CommandResult(
            executed = executed,
            command = command
        )
    )
}
