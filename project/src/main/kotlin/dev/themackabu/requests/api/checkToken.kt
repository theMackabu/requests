package dev.themackabu.requests.api

import io.ktor.server.auth.*
import cafe.adriel.satchel.ktx.get
import dev.themackabu.requests.mainDB

fun checkToken(tokenCredential: BearerTokenCredential): UserIdPrincipal? {
    return if (tokenCredential.token == "abc123") {
        UserIdPrincipal("jetbrains")
    } else { null }
}
