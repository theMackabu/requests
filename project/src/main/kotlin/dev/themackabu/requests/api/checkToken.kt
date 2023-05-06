package dev.themackabu.requests.api

import io.ktor.server.auth.*
import cafe.adriel.satchel.ktx.get
import dev.themackabu.requests.mainDB
import dev.themackabu.requests.helpers.toJson
import dev.themackabu.requests.helpers.fromJson
import dev.themackabu.requests.helpers.fromBase64
import dev.themackabu.requests.models.cmd.UserToken
import dev.themackabu.requests.models.cmd.TokenStorage

fun getMasterToken(): String? = mainDB.get<String>("token.master")
fun getUserToken(token: String): String? = mainDB.get<String>("token.$token")

fun getUserInfo(token: String): UserIdPrincipal? {
     mainDB.apply {
        return when {
            contains("token.$token") -> {
                val tokenHolder = token.fromBase64().fromJson<UserToken>()
                val uses = getUserToken(token)?.fromJson<TokenStorage>()?.uses

                set("token.$token", TokenStorage(uses?.plus(1)).toJson())
                UserIdPrincipal("${tokenHolder.name}.${tokenHolder.uuid}")
            } else -> {
                null
            }
        }
    }
}

fun checkToken(token: String): UserIdPrincipal? {
    return if (token == getMasterToken()) {
        UserIdPrincipal("console.user")
    } else {
        getUserInfo(token)
    }
}
