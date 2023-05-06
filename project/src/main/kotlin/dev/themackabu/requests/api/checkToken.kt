package dev.themackabu.requests.api

import io.ktor.server.auth.*
import cafe.adriel.satchel.ktx.get
import dev.themackabu.requests.mainDB
import dev.themackabu.requests.helpers.toJson
import dev.themackabu.requests.helpers.fromJson
import dev.themackabu.requests.helpers.fromBase64
import dev.themackabu.requests.models.cmd.UserToken
import dev.themackabu.requests.models.cmd.TokenStorage
import dev.themackabu.requests.models.cmd.ResponseContext

fun getMasterToken(): String? = mainDB.get<String>("token.master")
fun getUserToken(token: String): String? = mainDB.get<String>("token.$token")
fun responseData(name: String, uuid: String?): String = ResponseContext(name, uuid).toJson()

fun getUserInfo(token: String): UserIdPrincipal? {
     mainDB.apply {
        return when {
            contains("token.$token") -> {
                val tokenHolder = token.fromBase64().fromJson<UserToken>()
                val uses = getUserToken(token)?.fromJson<TokenStorage>()?.uses

                set("token.$token", TokenStorage(uses?.plus(1)).toJson())
                UserIdPrincipal(responseData(tokenHolder.name, tokenHolder.uuid))
            } else -> {
                null
            }
        }
    }
}

fun checkToken(token: String): UserIdPrincipal? {
    return if (token == getMasterToken()) {
        UserIdPrincipal(responseData("master.console.user", null))
    } else {
        getUserInfo(token)
    }
}
