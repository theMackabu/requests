package dev.themackabu.requests.config

interface ConfigInterface {
    val api: HashMap<String, String>
    val plugin: HashMap<String, String>
    val database: HashMap<String, String>
}
