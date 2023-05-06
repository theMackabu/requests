package dev.themackabu.requests.models.api

data class ApiException(
    override val message: String,
    val error: String,
    val code: Int = 404
): Exception(message)