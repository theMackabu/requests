package dev.themackabu.requests.models.config

data class Config (
    val api: HashMap<String, String>,
    val plugin: HashMap<String, String>,
    val database: HashMap<String, String>
)
