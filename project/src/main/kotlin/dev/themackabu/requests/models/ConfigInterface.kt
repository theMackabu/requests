package dev.themackabu.requests.models

data class ConfigInterface(
    val api: HashMap<String, String>,
    val plugin: HashMap<String, String>,
    val database: HashMap<String, String>
)
