package dev.themackabu.requests.models.config

data class ApiConfig(
    val port: Int,
    val vault: Boolean,
    val luckperms: Boolean,
    val danger: DangerConfig
)

data class DangerConfig(
    val remote_commands: Boolean
)

data class PluginConfig(
    val salt: String,
    val prefix: String
)

data class DatabaseConfig(
    val tokens: String,
    val players: String
)

data class Config(
    val api: ApiConfig,
    val plugin: PluginConfig,
    val database: DatabaseConfig
)
